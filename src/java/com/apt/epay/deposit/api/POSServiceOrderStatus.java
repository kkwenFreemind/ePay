/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.deposit.api;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.deposit.bean.ServiceOrderStatusReqBean;
import com.apt.epay.deposit.util.PosUtil;
import com.apt.epay.deposit.util.toolUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.util.PPMdnUtil;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_PROMOTIONCODE;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author kevinchang
 */
public class POSServiceOrderStatus extends HttpServlet {

    private final Logger log;

    public POSServiceOrderStatus() {
        this.log = Logger.getLogger("EPAY");
    }

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
        response.setContentType("text/html;charset=UTF-8");
        log.info("PosUserStateLookup.processRequest");
        log.info("Client IP :" + request.getRemoteAddr());

        String cpid = request.getParameter("CPID");
        log.info("CPID===>" + cpid);
        String Status = "0x00000000";
        String Status_Desc = "成功";
        String cplibm = "";
        String libm = "";
        String basicinfo = "";
//        String tradeDate = "";
        String accountbalance = "";
        String deskey = "";
        String responseStr = "";
        boolean cpkeyflag = false;
        toolUtil tool = new toolUtil();
        Utilities util = new Utilities();
        
        String mdn="";

        PPMdnUtil mdntool = new PPMdnUtil();
        String responsetime = mdntool.getAPIResponseTime();

        try {
            EPayBusinessConreoller epaybusinesscontroller;
            epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", this.getServletContext());

            int icpid = Integer.valueOf(cpid);
            EPAY_CP_INFO cpinfo = epaybusinesscontroller.getCpInfo(icpid);

            if (cpinfo != null) {
                deskey = cpinfo.getPoskey();
                log.info("deskey===>" + deskey);
                if (!"".equals(deskey)) {
                    cpkeyflag = true;
                }
            } else {
                //cpinfo is null
                log.info("cannot get cpinfo & pos key");
            }

            if (cpkeyflag) {
                ServletInputStream input = request.getInputStream();

                String encode_str_input = tool.getStringFromInputStream(input);
                String str_input = util.decrypt(deskey, encode_str_input);
                log.info("POSServiceOrder INPUT==>" + str_input);

                try {
                    ServiceOrderStatusReqBean aPIServiceOrdrReqBean;
                    aPIServiceOrdrReqBean = mdntool.ServiceOrderStatusParseXMLString(str_input, "ServiceOrderStatusRequest");
                    mdn = aPIServiceOrdrReqBean.getMdn();
                    cplibm = aPIServiceOrdrReqBean.getLibm();

                    log.info("MDN===>" + mdn);
                    log.info("CPLIBM==>" + cplibm);

                    if (!"".equals(mdn)) {

                        Calendar nowDateTime = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
                        String tradeDate = sdf.format(nowDateTime.getTime());

                        EPAY_TRANSACTION trans;//= new EPAY_TRANSACTION();
                        trans = epaybusinesscontroller.getTransactionByCPLibm(cplibm);

                        if (trans != null) {
                            log.info("trans.getStatus==>" + trans.getStatus());
                            libm = trans.getLibm();

                            if ("00".endsWith(trans.getStatus())) {
                                Status = "0x00000000";
                                Status_Desc = "儲值成功";
                            } else {
                                Status = "0x03000007";
                                Status_Desc = "儲值失敗";
                            }
                        } else {
                            log.info("trans is null");
                            Status = "0x03000007";
                            Status_Desc = "儲值失敗";
                        }

                        PPMdnUtil ppmdn = new PPMdnUtil(mdn, false, true);
                        String checkresult = ppmdn.checkPPMDN(epaybusinesscontroller);
                        log.info("ppmdn.checkPPMDN(" + mdn + ", epaybusinesscontroller)==>" + checkresult);
//                        basicinfo = ppmdn.getBasicOCSInfo(epaybusinesscontroller);

                        String promotioncode = ppmdn.getPromotioncode();
                        EPAY_PROMOTIONCODE epay_promotioncode;// = new EPAY_PROMOTIONCODE();
                        epay_promotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);

                        if (epay_promotioncode.getPlatformtype() == 2) {
                            
                            basicinfo = ppmdn.getBasicOCSInfo(epaybusinesscontroller);
                            
                        } else if (epay_promotioncode.getPlatformtype() == 3 ) {
                            
                            //KK NOKIA
                            PosUtil nokia_posutil = new PosUtil();
                            basicinfo = nokia_posutil.nokiaMdnBuckInfo(mdn);
                        }

                        accountbalance = ppmdn.getAccountBalance(epaybusinesscontroller);
                    }
                    responseStr = "<ServiceOrderStatusResponse><Result><STATUS>" + Status + "</STATUS><STATUS_DESC>" + Status_Desc + "</STATUS_DESC>"
                                    + "<CPLIBM>" + cplibm + "</CPLIBM>\n"
                                    + "<LIBM>" + libm + "</LIBM>"
                                    + "<RESPONSE_TIMESTAMP>" + responsetime + "</RESPONSE_TIMESTAMP>"
                                    + accountbalance
                                    + basicinfo
                                    + "</Result></ServiceOrderStatusResponse>";
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                Status = "0x02000010";
                Status_Desc = "未授權此功能";
                responseStr = "<ServiceOrderStatusResponse>\n"
                                + "<Result>\n"
                                + "<STATUS>" + Status + "</STATUS>\n"
                                + "<STATUS_DESC>" + Status_Desc + "</STATUS_DESC>\n"
                                + "<RESPONSE_TIMESTAMP>" + responsetime + "</RESPONSE_TIMESTAMP>\n"
                                + "</Result>\n"
                                + "</ServiceOrderStatusResponse>\n";
            }
        } catch (Exception ex) {
            log.info(ex);
        }
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            if (cpkeyflag) {
                String encode_responseStr = util.encrypt(deskey, responseStr);
                log.info(mdn + " POSServiceOrderStatus ResponseStr ===>" + responseStr.replaceAll("[ \t\r\n]+", " ").trim());
                log.info(mdn + " POSServiceOrderStatus encode_responseStr==>" + encode_responseStr);
                /* TODO output your page here. You may use following sample code. */
                out.println(encode_responseStr);
            } else {
                response.sendError(500);
                out.println(responseStr);
            }
        } catch (Exception ex) {
            log.info(ex);
        } finally {
            out.close();
        }

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
