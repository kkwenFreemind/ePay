/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.dealer.api;

import com.apt.epay.beans.BasicInfoReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.deposit.bean.UserAccountLookupReqBean;
import com.apt.epay.deposit.util.toolUtil;
import com.apt.epay.share.ShareBean;
import com.apt.util.PPMdnUtil;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_DEALERMDN;
import java.io.IOException;
import java.io.PrintWriter;
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
public class DealerUserAccountLookup extends HttpServlet {

    private final Logger log;

    public DealerUserAccountLookup() {
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

        log.info("DealerUserAccountLookup.processRequest");
        log.info("Client IP :" + request.getRemoteAddr());

        String cpid = request.getParameter("CPID");
        log.info("CPID===>" + cpid);

        toolUtil tool = new toolUtil();
        Utilities util = new Utilities();
        String deskey = "";
        String responseStr = "";
        boolean cpkeyflag = false;

        PPMdnUtil mdntool = new PPMdnUtil();
        String responsetime = mdntool.getAPIResponseTime();

        String Status = "0x00000000";
        String Status_Desc = "成功";

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
                log.info("DealerUserAccountLookup INPUT==>" + str_input);

                try {

//                    String mdn = mdntool.getMDNByparseXMLString(str_input, "UserAccountLookupRequest");
                    UserAccountLookupReqBean useraccountbean;// = new UserAccountLookupReqBean();
                    useraccountbean = mdntool.DealerUserAccountParseXMLString(str_input, "UserAccountLookupRequest");
                    String mdn = useraccountbean.getMdn();
                    String storeid = useraccountbean.getStoreid();
                    String storename = useraccountbean.getStorename();

                    log.info("MDN===>" + mdn);
                    if (!"".equals(mdn)) {

                        EPAY_DEALERMDN epaydealermdn = epaybusinesscontroller.getDealerMDNByMDN(storeid, mdn);

                        if (epaydealermdn != null) {
                            PPMdnUtil ppmdn = new PPMdnUtil(mdn, false, true);
                            BasicInfoReqBean basicinforeqbean = ppmdn.getBaiscInfo(epaybusinesscontroller);

                            if (basicinforeqbean != null) {
                                String BOCKET_VOICE = basicinforeqbean.getCounterValue1();//1+61
                                String BOCKET_VOICE_EXTRA = basicinforeqbean.getCounterValue2();//3+4+7+62
                                String BOCKET_DATA = basicinforeqbean.getCounterValue3(); //71
                                String BOCKET_DATA_EXPDATE = basicinforeqbean.getEndDate3();
                                String BOCKET_DATA_EXTRA = basicinforeqbean.getCounterValue4();
                                String BOCKET_DATA_EXTRA_EXPDATE = basicinforeqbean.getEndDate4();
                                String BOCKET_VOICE_ONNET = basicinforeqbean.getCounterValue5();
                                String BOCKET_VOICE_ONNET_EXPDATE = basicinforeqbean.getEndDate5();
                                String BOCKET_VOICE_ONNETFREE_EXPDATE = basicinforeqbean.getEndDate6();
                                String BOCKET_DATA_DAYPASS_EXPDATE = basicinforeqbean.getEndDate7();
//                    log.info("BOCKET_VOICE==>" + BOCKET_VOICE);
//                    log.info("BOCKET_VOICE_EXTRA==>" + BOCKET_VOICE_EXTRA);
//                    log.info("BOCKET_DATA==>" + BOCKET_DATA);
//                    log.info("BOCKET_DATA_EXPDATE==>" + BOCKET_DATA_EXPDATE);
//                    log.info("BOCKET_DATA_EXTRA==>" + BOCKET_DATA_EXTRA);
//                    log.info("BOCKET_DATA_EXTRA_EXPDATE==>" + BOCKET_DATA_EXTRA_EXPDATE);
//                    log.info("BOCKET_VOICE_ONNET==>" + BOCKET_VOICE_ONNET);
//                    log.info("BOCKET_VOICE_ONNET_EXPDATE==>" + BOCKET_VOICE_ONNET_EXPDATE);
//                    log.info("BOCKET_VOICE_ONNETFREE_EXPDATE==>" + BOCKET_VOICE_ONNETFREE_EXPDATE);
//                    log.info("BOCKET_DATA_DAYPASS_EXPDATE==>" + BOCKET_DATA_DAYPASS_EXPDATE);
                                responseStr = "<UserAccountLookupResponse>\n"
                                        + "<Result>\n"
                                        + "<STATUS>" + Status + "</STATUS>\n"
                                        + "<STATUS_DESC>" + Status_Desc + "</STATUS_DESC>\n"
                                        + "<RESPONSE_TIMESTAMP>" + responsetime + "</RESPONSE_TIMESTAMP>\n"
                                        + "<USERSTATE>1</USERSTATE>\n"
                                        + "</Result>\n"
                                        + "<AccountList>\n"
                                        + "<BOCKET_VOICE>" + BOCKET_VOICE + "</BOCKET_VOICE>\n"
                                        + "<BOCKET_VOICE_EXTRA>" + BOCKET_VOICE_EXTRA + "</BOCKET_VOICE_EXTRA>\n"
                                        + "<BOCKET_DATA>" + BOCKET_DATA + "</BOCKET_DATA>\n"
                                        + "<BOCKET_DATA_EXPDATE>" + BOCKET_DATA_EXPDATE + "</BOCKET_DATA_EXPDATE>\n"
                                        + "<BOCKET_DATA_EXTRA>" + BOCKET_DATA_EXTRA + "</BOCKET_DATA_EXTRA>\n"
                                        + "<BOCKET_DATA_EXTRA_EXPDATE>" + BOCKET_DATA_EXTRA_EXPDATE + "</BOCKET_DATA_EXTRA_EXPDATE>\n"
                                        + "<BOCKET_VOICE_ONNET>" + BOCKET_VOICE_ONNET + "</BOCKET_VOICE_ONNET>\n"
                                        + "<BOCKET_VOICE_ONNET_EXPDATE>" + BOCKET_VOICE_ONNET_EXPDATE + "</BOCKET_VOICE_ONNET_EXPDATE>\n"
                                        + "<BOCKET_VOICE_ONNETFREE_EXPDATE>" + BOCKET_VOICE_ONNETFREE_EXPDATE + "</BOCKET_VOICE_ONNETFREE_EXPDATE>\n"
                                        + "<BOCKET_DATA_DAYPASS_EXPDATE>" + BOCKET_DATA_DAYPASS_EXPDATE + "</BOCKET_DATA_DAYPASS_EXPDATE>\n"
                                        + "</AccountList>\n"
                                        + "</UserAccountLookupResponse>";
                            } else {
                                Status = "0x01000001";
                                Status_Desc = "查詢失敗";
                                responseStr = "<UserAccountLookupResponse>\n"
                                        + "<Result>\n"
                                        + "<STATUS>" + Status + "</STATUS>\n"
                                        + "<STATUS_DESC>" + Status_Desc + "</STATUS_DESC>\n"
                                        + "<RESPONSE_TIMESTAMP>" + responsetime + "</RESPONSE_TIMESTAMP>\n"
                                        + "<USERSTATE>1</USERSTATE>\n"
                                        + "</Result>\n"
                                        + "</UserAccountLookupResponse>";
                            }
                        } else {
                            Status = "0x01000001";
                            Status_Desc = "認證失敗(門號與經銷商代碼不一致)";
                            responseStr = "<UserAccountLookupResponse>\n"
                                    + "<Result>\n"
                                    + "<STATUS>" + Status + "</STATUS>\n"
                                    + "<STATUS_DESC>" + Status_Desc + "</STATUS_DESC>\n"
                                    + "<RESPONSE_TIMESTAMP>" + responsetime + "</RESPONSE_TIMESTAMP>\n"
                                    + "<USERSTATE>1</USERSTATE>\n"
                                    + "</Result>\n"
                                    + "</UserAccountLookupResponse>";
                        }
                        //goto zte and get basic info
                    } else { // mdn is null
                        Status = "0x01000001";
                        Status_Desc = "認證失敗(用戶門號不存在)";
                        responseStr = "<UserAccountLookupResponse>\n"
                                + "<Result>\n"
                                + "<STATUS>" + Status + "</STATUS>\n"
                                + "<STATUS_DESC>" + Status_Desc + "</STATUS_DESC>\n"
                                + "<RESPONSE_TIMESTAMP>" + responsetime + "</RESPONSE_TIMESTAMP>\n"
                                + "<USERSTATE>1</USERSTATE>\n"
                                + "</Result>\n"
                                + "</UserAccountLookupResponse>";
                    }
                } catch (Exception ex) {
                    log.info(ex);
                }
            } else {
                Status = "0x02000010";
                Status_Desc = "未授權此功能";
                responseStr = "<UserAccountLookupResponse>\n"
                        + "<Result>\n"
                        + "<STATUS>" + Status + "</STATUS>\n"
                        + "<STATUS_DESC>" + Status_Desc + "</STATUS_DESC>\n"
                        + "<RESPONSE_TIMESTAMP>" + responsetime + "</RESPONSE_TIMESTAMP>\n"
                        + "</Result>\n"
                        + "</UserAccountLookupResponse>\n";
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
                log.info("POSUserAccountLookup ResponseStr ===>" + responseStr);
                log.info("POSUserAccountLookup encode_responseStr==>" + encode_responseStr);
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
