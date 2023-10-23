/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.deposit.api;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.deposit.bean.UnReserveReqBean;
import com.apt.epay.deposit.util.UnReserveProfile;
import com.apt.epay.deposit.util.toolUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_VCARD;
import com.epay.ejb.bean.EPAY_VCARDTYPE;
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
public class ServiceUnReserve extends HttpServlet {
    //沒有LOG
    private static final Logger log = Logger.getLogger("EPAY");

    private String channelID;
    private String mdn;
    private String token;

    private String response_timestamp;
    private String resultstatus = "";
    private String resultcode = "";

    private String responseMsg = "";
    private String print_responseMsg;
    private String tradeDate;
    private String pgKey;
    private String identKey;

    private String md5Param = "&identifyCode=";
    private String desParam = "&callerInMac="; //to PaymentGateway use callerInMac, Receive from PaymentGateway use returnOutMac    

    private boolean UnReserveFlag = false;

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

        log.info("ServiceUnReserve.processRequest");
        String logMsg = "Client IP :" + request.getRemoteAddr();
        String target_ip = request.getRemoteAddr();
        log.info(logMsg);

        ServletInputStream input = request.getInputStream();
        toolUtil tool = new toolUtil();
        String str_input = tool.getStringFromInputStream(input);
        log.info("INPUT==>" + str_input);

        Calendar nowDateTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
        tradeDate = sdf.format(nowDateTime.getTime());

        UnReserveFlag = false;

        if (!"".equals(str_input)) {
            UnReserveProfile un_reserveprofile = new UnReserveProfile();
            String TagName = "ServiceUnReserveRequest";
            try {
                response_timestamp = tradeDate;
                UnReserveReqBean un_reservebean = new UnReserveReqBean();
                un_reservebean = un_reserveprofile.parseXMLString(str_input, TagName);
                channelID = un_reservebean.getChannelID();
                String source_mdn = un_reservebean.getMdn();
                String source_tokenid = un_reservebean.getTokenId();

                EPayBusinessConreoller epaybusinesscontroller;
                epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", this.getServletContext());
                EPAY_CP_INFO cpinfo = epaybusinesscontroller.getCpInfo(Integer.valueOf(channelID));
                pgKey = cpinfo.getEnkey();
                identKey = cpinfo.getIdentify();

                boolean chkip = epaybusinesscontroller.chkIpValidation(target_ip, Integer.valueOf(channelID));
                if (chkip) {
                    Utilities util = new Utilities();
                    mdn = util.decrypt(pgKey, source_mdn);
                    token = util.decrypt(pgKey, source_tokenid);
//                    ApolSecuredUrlMsg asum = new ApolSecuredUrlMsg(pgKey, identKey);
//                    mdn = asum.kkdecode(source_mdn, md5Param, desParam);
//                    token = asum.kkdecode(source_tokenid, md5Param, desParam);

                    log.info("Un_Reserve encode Bean ==>" + source_mdn + "," + source_tokenid);
                    log.info("Un_Reserve Bean ==>" + mdn + "," + channelID + "," + token);

                    //依據CPID&TOKEN查詢VCARD Table
                    EPAY_VCARD vcard = epaybusinesscontroller.getVCPass(token);
                    if (vcard != null) {
                        vcard.setTokenid(null);
                        vcard.setOrderdate(null);
                        vcard.setOrderexpiredate(null);
                        vcard.setChannel(null);
                        vcard.setMdn(null);
                        vcard.setUuid(null);
                        vcard.setStatus("0");
                        if (epaybusinesscontroller.updateVCard(vcard)) {
                            response_timestamp = tradeDate;
                            resultstatus = "Success";
                            resultcode = "0x00000000";
                            UnReserveFlag = true;

                            //update vcardtype quanity
                            String cardtype = vcard.getCardtype();
                            EPAY_VCARDTYPE vcardtypeinfo = new EPAY_VCARDTYPE();
                            vcardtypeinfo = epaybusinesscontroller.queryCardTypeByCardType(cardtype);
                            int old_qty = vcardtypeinfo.getQuantity();
                            int new_qty = old_qty + 1;
                            vcardtypeinfo.setQuantity(new_qty);
                            epaybusinesscontroller.updateVCardType(vcardtypeinfo);
                        } else {
                            response_timestamp = tradeDate;
                            resultstatus = "DB Error";
                            resultcode = "0x03000006";
                        }

                    } else {
                        response_timestamp = tradeDate;
                        resultstatus = "No VCard UnReserve";
                        resultcode = "0x03000002";
                    }
                } else {
                    log.info("Access Deny");
                    resultcode = "0x01000002";
                    resultstatus = "IP Tables";
                }
            } catch (Exception ex) {
                log.info(ex);
            }
        } else {
            response_timestamp = tradeDate;
            resultstatus = "Data format or validation error";
            resultcode = "0x03000005";
        }

        if (UnReserveFlag) {
            print_responseMsg = "<ServiceUnReserveResponse>\n"
                    + "<Result>\n"
                    + "<STATUS>" + resultcode + "</STATUS>\n"
                    + "<STATUS_DESC>" + resultstatus + "</STATUS_DESC>\n"
                    + "<RESPONSE_TIMESTAMP>" + response_timestamp + "</RESPONSE_TIMESTAMP>\n"
                    + "</Result>\n"
                    + "<Order>\n"
                    //                    + "<MDN>" + mdn + "</MDN>\n"
                    + "<TOKENID>" + token + "</TOKENID>\n"
                    + "</Order>\n"
                    + "</ServiceUnReserveResponse>";

            try {
                String encode_token = "";
                Utilities utilx = new Utilities();
                String encode_resultstatus = utilx.encrypt(pgKey, resultstatus);
                String encode_resultcode = utilx.encrypt(pgKey, resultcode);
                String encode_response_timestamp = utilx.encrypt(pgKey, response_timestamp);
                String encode_mdn = utilx.encrypt(pgKey, mdn);
                if (!token.equals("")) {
                    encode_token = utilx.encrypt(pgKey, token);
                }
//                String encode_resultstatus = (new SecuredMsg(pgKey, identKey).encode(resultstatus)).toString();
//                String encode_resultcode = (new SecuredMsg(pgKey, identKey).encode(resultcode)).toString();
//                String encode_response_timestamp = (new SecuredMsg(pgKey, identKey).encode(response_timestamp)).toString();
//                String encode_mdn = (new SecuredMsg(pgKey, identKey).encode(mdn)).toString();
//                if (!token.equals("")) {
//                    encode_token = (new SecuredMsg(pgKey, identKey).encode(token)).toString();
//                }

                responseMsg = "<ServiceUnReserveResponse>\n"
                        + "<Result>\n"
                        + "<STATUS>" + encode_resultcode + "</STATUS>\n"
                        + "<STATUS_DESC>" + encode_resultstatus + "</STATUS_DESC>\n"
                        + "<RESPONSE_TIMESTAMP>" + encode_response_timestamp + "</RESPONSE_TIMESTAMP>\n"
                        + "</Result>\n"
                        + "<Order>\n"
                        //                        + "<MDN>" + encode_mdn + "</MDN>\n"
                        + "<TOKENID>" + encode_token + "</TOKENID>\n"
                        + "</Order>\n"
                        + "</ServiceUnReserveResponse>";
            } catch (Exception ex) {
                log.info(ex);
            }
        } else {//UnReserveFlag = false
            print_responseMsg = "<ServiceUnReserveResponse>\n"
                    + "<Result>\n"
                    + "<STATUS>" + resultcode + "</STATUS>\n"
                    + "<STATUS_DESC>" + resultstatus + "</STATUS_DESC>\n"
                    + "<RESPONSE_TIMESTAMP>" + response_timestamp + "</RESPONSE_TIMESTAMP>\n"
                    + "</Result>\n"
                    + "<Order>\n"
                    //                        + "<MDN>" + encode_mdn + "</MDN>\n"
                    + "<TOKENID>" + token + "</TOKENID>\n"
                    + "</Order>\n"
                    + "</ServiceUnReserveResponse>";

            try {
                String encode_token = "";
                Utilities utils = new Utilities();
                String encode_resultstatus = utils.encrypt(pgKey, resultstatus);
                String encode_resultcode = utils.encrypt(pgKey, resultcode);
                String encode_response_timestamp = utils.encrypt(pgKey, response_timestamp);
                if (!token.equals("")) {
                    encode_token = utils.encrypt(pgKey, token);
                }
//                String encode_resultstatus = (new SecuredMsg(pgKey, identKey).encode(resultstatus)).toString();
//                String encode_resultcode = (new SecuredMsg(pgKey, identKey).encode(resultcode)).toString();
//                String encode_response_timestamp = (new SecuredMsg(pgKey, identKey).encode(response_timestamp)).toString();
//                if (!token.equals("")) {
//                    encode_token = (new SecuredMsg(pgKey, identKey).encode(token)).toString();
//                }

                responseMsg = "<ServiceUnReserveResponse>\n"
                        + "<Result>\n"
                        + "<STATUS>" + encode_resultcode + "</STATUS>\n"
                        + "<STATUS_DESC>" + encode_resultstatus + "</STATUS_DESC>\n"
                        + "<RESPONSE_TIMESTAMP>" + encode_response_timestamp + "</RESPONSE_TIMESTAMP>\n"
                        + "</Result>\n"
                        + "<Order>\n"
                        //                        + "<MDN>" + encode_mdn + "</MDN>\n"
                        + "<TOKENID>" + encode_token + "</TOKENID>\n"
                        + "</Order>\n"
                        + "</ServiceUnReserveResponse>";
            } catch (Exception ex) {
                log.info(ex);
            }
        }

        log.info("VCardUnReserve responseMsg==>\n" + print_responseMsg);
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.print(responseMsg);
            log.info("===>\n" + responseMsg);
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
