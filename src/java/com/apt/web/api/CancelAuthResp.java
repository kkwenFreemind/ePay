package com.apt.web.api;

import com.apt.epay.beans.CancelAuthRespBean;
//import com.apt.epay.beans.PaymentReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
//import com.apt.epay.share.ShareParm;
import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.List;
import java.util.StringTokenizer;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.apt.util.ApolSecuredUrlMsg;

//import com.apt.util.ReturnURL;
import com.epay.ejb.bean.EPAY_CALLER;
//import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import java.util.Enumeration;
//import org.zkoss.zk.ui.Executions;
//import org.zkoss.zk.ui.Sessions;
//import org.zkoss.zul.Window;

/**
 *
 * @author Booker Hsu
 */
public class CancelAuthResp extends HttpServlet {

//    private static final long serialVersionUID = -27366546290977690L;
    private static final String md5Param = "&identifyCode=";
    private static final String desParam = "&returnOutMac=";
//    private static final SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
//    private static final SimpleDateFormat sdf7 = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT7);
    private static final Logger log = Logger.getLogger("ECF");

    private EPayBusinessConreoller epaybusinesscontroller = null;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //取消授權結果回傳
        log.info("Cancel Auth Response Notice");
        String remoteAddress = "Client IP :" + request.getRemoteAddr();
        log.info("Cancel Auth Resp remoteAddress" + remoteAddress);

        ServletOutputStream output = response.getOutputStream();
        response.setContentType("text/html");

        epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", this.getServletContext());

        Enumeration params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = (String) params.nextElement();
            log.info("Parameter Name - " + paramName + ", Value - " + request.getParameter(paramName));
        }

        String encryptResponseMsg = request.getParameter("encryptedMsg");
        String callidInfo = ShareParm.EPAY_CALLID;
        log.info("callerMerchantId====>" + callidInfo);
        log.info("encryptedMsg===>" + encryptResponseMsg);

        if ("".equals(encryptResponseMsg) || encryptResponseMsg == null) {
            throw new ServletException("No Data Received, encryptResponseMsg is null");
        }

        String decodeMsg = "";
        CancelAuthRespBean cancelauthrespBean = new CancelAuthRespBean();
        try {
            epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", this.getServletContext());
            EPAY_CALLER caller = epaybusinesscontroller.getCallerById(callidInfo);
            log.info("CallerID==>" + caller.getCallerid());
            log.info("caller.getPgEnkey()==>" + caller.getPgEnkey());
            log.info("caller.getPgIdentify()==>" + caller.getPgIdentify());

            ApolSecuredUrlMsg asum = new ApolSecuredUrlMsg(caller.getPgEnkey(), caller.getPgIdentify());
            decodeMsg = asum.decode(encryptResponseMsg, md5Param, desParam);
            log.info("decodeMsg===>" + decodeMsg);

            StringTokenizer st = new StringTokenizer(decodeMsg, "&");
            while (st.hasMoreTokens()) {
                String parms = st.nextElement().toString();
                String returnItem[] = parms.split("=");
                cancelauthrespBean = encapBean(cancelauthrespBean, returnItem);
            }
            output.print("1");

            log.info("Cancel Auth Libm===>" + cancelauthrespBean.getLibm());
            log.info("Cancel Auth Status===>" + cancelauthrespBean.getStatus());
            log.info("Cancel Auth private data (CP ID) == >" + cancelauthrespBean.getPrivatedata());

            EPAY_TRANSACTION trans = epaybusinesscontroller.getTransaction(cancelauthrespBean.getLibm());
//            EPAY_CP_INFO cpInfo = epaybusinesscontroller.getCpInfoTxType(trans.getCpId());

            if (cancelauthrespBean.getStatus().equals("0")) {
                //取消授權成功，更新資料庫
                log.info("Libm:==>" + cancelauthrespBean.getLibm() + " CancelAuth Success. ==>" + cancelauthrespBean.getStatus());
                trans.setStatus("C");
                boolean brst = epaybusinesscontroller.updateTransaction(trans);
                if (brst) {
                    log.info("Update Success");
                } else {
                    log.info("Update Fail");
                }
            } else {
                //do nothing
                log.info("Libm:==>" + cancelauthrespBean.getLibm() + " CancelAuth Fail. ==>" + cancelauthrespBean.getStatus());
            }

        } catch (Exception ex) {
            log.info(ex);
        }
    }

    private CancelAuthRespBean encapBean(CancelAuthRespBean cancelauthrespBean, String... items) {

        if ("status".equalsIgnoreCase(items[0])) {
            cancelauthrespBean.setStatus(items.length > 1 ? items[1] : "");
        }
        if ("errCode".equalsIgnoreCase(items[0])) {
            cancelauthrespBean.setErrcode(items.length > 1 ? items[1] : "");
        }
        if ("errDesc".equalsIgnoreCase(items[0])) {
            cancelauthrespBean.setErrdesc(items.length > 1 ? items[1] : "");
        }

        if ("libm".equalsIgnoreCase(items[0])) {
            cancelauthrespBean.setLibm(items.length > 1 ? items[1] : "");
        }
        if ("action".equalsIgnoreCase(items[0])) {
            cancelauthrespBean.setAction(items.length > 1 ? items[1] : "");
        }
        if ("payType".equalsIgnoreCase(items[0])) {
            cancelauthrespBean.setPaytype(items.length > 1 ? items[1] : "");
        }
        if ("paymentStatus".equalsIgnoreCase(items[0])) {
            cancelauthrespBean.setPaymentstatus(items.length > 1 ? items[1] : "");
        }
        if ("captureStatus".equalsIgnoreCase(items[0])) {
            cancelauthrespBean.setCapturestatus(items.length > 1 ? items[1] : "");
        }
        if ("refundStatus".equalsIgnoreCase(items[0])) {
            cancelauthrespBean.setRefundstatus(items.length > 1 ? items[1] : "");
        }
        if ("invoiceStatus".equalsIgnoreCase(items[0])) {
            cancelauthrespBean.setInvoicestatus(items.length > 1 ? items[1] : "");
        }
        if ("orderTotal".equalsIgnoreCase(items[0])) {
            cancelauthrespBean.setOrdertotal(items.length > 1 ? items[1] : "");
        }
        if ("actualAmt".equalsIgnoreCase(items[0])) {
            cancelauthrespBean.setActualamt(items.length > 1 ? items[1] : "");
        }
        if ("orderCreateDate".equalsIgnoreCase(items[0])) {
            cancelauthrespBean.setOrdercreatedate(items.length > 1 ? items[1] : "");
        }
        if ("payAccount".equalsIgnoreCase(items[0])) {
            cancelauthrespBean.setPayaccount(items.length > 1 ? items[1] : "");
        }
        if ("invoiceNo".equalsIgnoreCase(items[0])) {
            cancelauthrespBean.setInvoiceno(items.length > 1 ? items[1] : "");
        }
        if ("privateData".equalsIgnoreCase(items[0])) {
            cancelauthrespBean.setPrivatedata(items.length > 1 ? items[1] : "");
        }

        if ("authDate".equalsIgnoreCase(items[0])) {
            cancelauthrespBean.setAuthdate(items.length > 1 ? items[1] : "");
        }
        return cancelauthrespBean;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
