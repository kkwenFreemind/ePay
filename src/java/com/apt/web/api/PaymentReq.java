package com.apt.web.api;

import com.apt.epay.beans.PaymentReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
//import com.epay.ejb.bean.EPAY_SERVICE_INFO;
import com.epay.ejb.bean.EPAY_TRANSACTION;

//import com.apt.cpecf.beans.PaymentReqBean;
//import com.apt.cpecf.controller.CPBusinessConreoller;
//import com.apt.cpecf.share.ShareBean;
//import com.apt.cpecf.share.ShareParm;
//
//import com.cpecf.ejb.bean.CP_INFO;
//import com.cpecf.ejb.bean.CALLER;
//import com.cpecf.ejb.bean.SERVICE_INFO;
//import com.cpecf.ejb.bean.TRANSACTION;

/**
 *
 * @author Booker Hsu
 */
public class PaymentReq extends HttpServlet {

//    private static final long serialVersionUID = -27366546290977690L;
    private static final String md5Param = "&identifyCode=";
    private static final String desParam = "&returnOutMac=";
    private static final SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
    private static final SimpleDateFormat sdf7 = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT7);
    private static final Logger log = Logger.getLogger("EPAY");

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

        //請款結果通知
        log.info("ECF Receiving PaymentGateway Notice");
        String logMsg = "Client IP :" + request.getRemoteAddr() ;
        log.info(logMsg);
        String callId = request.getQueryString();
        ServletOutputStream output = response.getOutputStream();
        response.setCharacterEncoding("UTF-8");

        String encryptResponseMsg = request.getParameter("encryptedMsg");
        if ("".equals(encryptResponseMsg) || encryptResponseMsg == null) {
            throw new ServletException("No Data Received, encryptedMsg is null");
        }

        try {
            
            epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", this.getServletContext());
            PaymentReqBean paymentBean = new PaymentReqBean();
            String decodeMsg = "";

            try {

                EPAY_CALLER caller = epaybusinesscontroller.getCallerById(callId);
                ApolSecuredUrlMsg asum = new ApolSecuredUrlMsg(caller.getPgEnkey(), caller.getPgIdentify());
                decodeMsg = asum.decode(encryptResponseMsg, md5Param, desParam);

            } catch (Exception e) {
                log.error("Decoding Message Error", e);
                throw e;
            }

            StringTokenizer st = new StringTokenizer(decodeMsg, "&");
            while (st.hasMoreTokens()) {
                String parms = st.nextElement().toString();
                String returnItem[] = parms.split("=");
                paymentBean = encapBean(paymentBean, returnItem);
            }

            EPAY_TRANSACTION trans = epaybusinesscontroller.getTransaction(paymentBean.getLibm());
            if (updateTransaction(trans, paymentBean)) {
                String logmsg = "PaymentRequest,Update Transaction Success ,libm = " + paymentBean.getLibm();
                log.info(logmsg);
                //回應PaymentGateway收到通知
//                String respon = "1";
//                output.write(respon.getBytes(ShareParm.PARM_CHARSETNAME_UTF8));
//
//                EPAY_CP_INFO cpInfo = epaybusinesscontroller.getCpInfoTxType(trans.getCpId());
//                EPAY_SERVICE_INFO serviceInfo = epaybusinesscontroller.getServiceInfoById(Long.valueOf(trans.getServiceId()), trans.getCpId());
                //傳送到對應的系統
//                log.info("PaymentRequest passing paramenter to 3rd party system " + trans.getCpId());
//                log.info("3rd party system url : " + cpInfo.getPayment_url());
//                //join other information to end.
//                decodeMsg = "cpLibm=" + trans.getCpLibm() + "&serviceid=" + trans.getServiceId() + "&" + decodeMsg;
//                
//                
//                
//                ReturnURL returnUrl = new ReturnURL(cpInfo.getEnkey(), cpInfo.getIdentify());
//                returnUrl.callReturnUrl(cpInfo.getPayment_url(), decodeMsg);
            } else {
                 String logmsg = "Payment Request Update DB fail ,libm =" + paymentBean.getLibm();
                 log.error(logmsg);
            }

        } catch (Exception ex) {
            log.error("Payment Request Process error", ex);
        } finally {
            output.flush();
            output.close();
        }
    }

    private boolean updateTransaction(EPAY_TRANSACTION trans, PaymentReqBean paymentBean) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
        boolean result = false;

        log.info("KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK");
        if (trans != null) {
            if ("Y".equals(paymentBean.getStatus())) {
                trans.setPaystatus(1);
                trans.setErrcode("00"); //繳款成功後設定為00
            } 
            trans.setOrdertotal(Integer.parseInt(paymentBean.getOrderTotal()));
            trans.setPayamount(Integer.parseInt(paymentBean.getActualAmt()));
            trans.setPaytime(sdf.parse(paymentBean.getResponseTime()));
            trans.setErrdesc(paymentBean.getResponseMsg());
            result = epaybusinesscontroller.updateTransaction(trans);
        } else {
            log.error("PaymentRequest[No Transaction Data,libm :" + paymentBean.getLibm() + "]");
            throw new Exception("PaymentRequest[No Transaction Data,libm :" + paymentBean.getLibm() + "]");
        }
        return result;
    }

    private PaymentReqBean encapBean(PaymentReqBean paymentBean, String... items) {
        if ("libm".equalsIgnoreCase(items[0])) {
            paymentBean.setLibm(items.length > 1 ? items[1] : "");
        }
        if ("type".equalsIgnoreCase(items[0])) {
            paymentBean.setType(items.length > 1 ? items[1] : "");
        }
        if ("batchNo".equalsIgnoreCase(items[0])) {
            paymentBean.setBatchNo(items.length > 1 ? items[1] : "");
        }
        if ("orderTotal".equalsIgnoreCase(items[0])) {
            paymentBean.setOrderTotal(items.length > 1 ? items[1] : "");
        }
        if ("actualAmt".equalsIgnoreCase(items[0])) {
            paymentBean.setActualAmt(items.length > 1 ? items[1] : "");
        }
        if ("status".equalsIgnoreCase(items[0])) {
            paymentBean.setStatus(items.length > 1 ? items[1] : "");
        }
        if ("responseMsg".equalsIgnoreCase(items[0])) {
            paymentBean.setResponseMsg(items.length > 1 ? items[1] : "");
        }
        if ("responseTime".equalsIgnoreCase(items[0])) {
            paymentBean.setResponseTime(items.length > 1 ? items[1] : "");
        }
        return paymentBean;
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
