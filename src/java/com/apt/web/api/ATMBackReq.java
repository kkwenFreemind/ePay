/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.web.api;

import com.apt.epay.beans.APIATMBackReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
//import com.apt.util.ApolSecuredUrlMsg;
import com.cryptographic.util.SecuredMsg;
import com.epay.ejb.bean.EPAY_CALLER;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import java.io.IOException;
import java.util.StringTokenizer;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author kevinchang
 */
public class ATMBackReq extends HttpServlet {

    private EPayBusinessConreoller epaybusinesscontroller = null;
//    private final String cpid = new ShareParm().PARM_EPAY_CPID;
    private static final Logger log = Logger.getLogger("EPAY");
    private static final String CALLID = ShareParm.EPAY_CALLID;

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

        log.info("ATMBackReq.processRequest");
        String logMsg = "Client IP :" + request.getRemoteAddr();
        log.info(logMsg);

        ServletOutputStream output = response.getOutputStream();
        response.setCharacterEncoding("UTF-8");

        epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", this.getServletContext());

        String encryptNotifyMsg;// = null;

        String md5Param = "&identifyCode=";
        String desParam = "&returnOutMac=";
        String decodeMsg;// = "";

        encryptNotifyMsg = request.getParameter("encryptedMsg");
        log.info("ATM encryptedMsg==>" + encryptNotifyMsg);

        if ("".equals(encryptNotifyMsg) || encryptNotifyMsg == null) {
//            throw new ServletException("無接收資料");
            log.info("No Data Received, encryptResponseMsg is null");
            throw new ServletException("No Data Received, encryptResponseMsg is null");
        }

        try {

            APIATMBackReqBean apiAtmBackReqBean = new APIATMBackReqBean();
            EPAY_CALLER caller = epaybusinesscontroller.getCallerById(CALLID);
            log.info("epaybusinesscontroller.getCallerById(callId)==>" + CALLID);
            log.info("ApolSecuredUrlMsg(caller.getPgEnkey(), caller.getPgIdentify()==>(" + caller.getPgEnkey() + "," + caller.getPgIdentify() + ")");

//            ECFUtil ecfutil = new ECFUtil(caller.getPgEnkey(), caller.getPgIdentify());
//            decodeMsg = ecfutil.decode(encryptNotifyMsg, md5Param, desParam);            
//            ApolSecuredUrlMsg asum = new ApolSecuredUrlMsg(caller.getPgEnkey(), caller.getPgIdentify());
            SecuredMsg asum = new SecuredMsg(caller.getPgEnkey(), caller.getPgIdentify());
            decodeMsg = asum.decode(encryptNotifyMsg, md5Param, desParam);
            log.info("ATM Back decodeMsg===>" + decodeMsg);

            StringTokenizer st = new StringTokenizer(decodeMsg, "&");
            while (st.hasMoreTokens()) {
                String parms = st.nextElement().toString();
                String returnItem[] = parms.split("=");
                apiAtmBackReqBean = encapBean(apiAtmBackReqBean, returnItem);
            }

            String libm = apiAtmBackReqBean.getLibm();
            EPAY_TRANSACTION trans = epaybusinesscontroller.getTransaction(libm);

            if (trans != null) {
                trans.setBankid(apiAtmBackReqBean.getBankid());
                trans.setAtmaccount(apiAtmBackReqBean.getAccount());
                epaybusinesscontroller.updateTransaction(trans);
            }
        } catch (Exception ex) {
            log.info(ex);
        }
    }

    private APIATMBackReqBean encapBean(APIATMBackReqBean apiAtmBackReqBean, String... items) {
        if ("libm".equalsIgnoreCase(items[0])) {
            apiAtmBackReqBean.setLibm(items.length > 1 ? items[1] : "");
            log.info("APIATMBackReqBean.getLibm==>" + apiAtmBackReqBean.getLibm());
        }
        if ("account".equalsIgnoreCase(items[0])) {
            apiAtmBackReqBean.setAccount(items.length > 1 ? items[1] : "");
            log.info("APIATMBackReqBean.getAccount==>" + apiAtmBackReqBean.getAccount());
        }
        if ("bankid".equalsIgnoreCase(items[0])) {
            apiAtmBackReqBean.setBankid(items.length > 1 ? items[1] : "");
            log.info("APIATMBackReqBean.getBankid=>" + apiAtmBackReqBean.getBankid());
        }

        return apiAtmBackReqBean;
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
