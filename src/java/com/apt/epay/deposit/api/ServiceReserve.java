/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.deposit.api;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.deposit.bean.ReserveReqBean;
import com.apt.epay.deposit.util.ReserveProfile;
import com.apt.epay.deposit.util.toolUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.util.BundleDateUtil;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_SERVICE_INFO;
import com.epay.ejb.bean.EPAY_VCARD;
import com.epay.ejb.bean.EPAY_VCARDTYPE;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
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
public class ServiceReserve extends HttpServlet {
    //沒有LOG
    private static final Logger log = Logger.getLogger("EPAY");

    private String channelID;
    private String mdn;
    private String serviceID;
    private String serviceName;
    private String price;
    private String uuid;
    private String token;

    private String response_timestamp;
    private String resultstatus = "";
    private String resultcode = "";

    private String pgKey;
    private String identKey;

//    private String contractid = "";
//    private String contractstatuscode = "";
//    private String producttype = "";
    private String responseMsg = "";
    private String print_responseMsg;
    private String tradeDate;
    private String orderexpiredate;
//    private String message = "";

    private String md5Param = "&identifyCode=";
    private String desParam = "&callerInMac="; //to PaymentGateway use callerInMac, Receive from PaymentGateway use returnOutMac    

    private boolean reserveFlag = false;

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

        log.info("ServiceReserve.processRequest");
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
        reserveFlag = false;

        if (!"".equals(str_input)) {

            String TagName = "ServiceReserveRequest";

            try {
                response_timestamp = tradeDate;
                String vcardexpirehour = new ShareParm().PARM_VCARD_EXPIRE;
                log.info("vcardexpirehour===>" + vcardexpirehour);
                BundleDateUtil bdateutil = new BundleDateUtil();
                String tmp_orderexpiredate = bdateutil.AddMin(Integer.valueOf(vcardexpirehour));
                //2016-07-15 17:57:06.0
                orderexpiredate = tmp_orderexpiredate.substring(0, 19);

                ReserveReqBean reservebean = new ReserveReqBean();
                ReserveProfile reserveprofile = new ReserveProfile();
                reservebean = reserveprofile.parseXMLString(str_input, TagName);
                channelID = reservebean.getChannelID();

                EPayBusinessConreoller epaybusinesscontroller;
                epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", this.getServletContext());

                String source_mdn = reservebean.getMdn();
                String source_serviceID = reservebean.getServiceID();
//                String source_serviceName = reservebean.getServiceName();
                String source_price = reservebean.getPrice();
                String source_uuid = reservebean.getUuid();
                EPAY_CP_INFO cpinfo = epaybusinesscontroller.getCpInfo(Integer.valueOf(channelID));
                pgKey = cpinfo.getEnkey();
                identKey = cpinfo.getIdentify();

                boolean chkip = epaybusinesscontroller.chkIpValidation(target_ip, Integer.valueOf(channelID));

                if (chkip) {
                    Utilities util = new Utilities();
                    mdn = util.decrypt(pgKey, source_mdn);
                    serviceID = util.decrypt(pgKey, source_serviceID);
                    price = util.decrypt(pgKey, source_price);
                    uuid = util.decrypt(pgKey, source_uuid);

//                    ApolSecuredUrlMsg asum = new ApolSecuredUrlMsg(pgKey, identKey);
//                    mdn = asum.kkdecode(source_mdn, md5Param, desParam);
//                    serviceID = asum.kkdecode(source_serviceID, md5Param, desParam);
////                    serviceName = asum.kkdecode(source_serviceName, md5Param, desParam);
//                    price = asum.kkdecode(source_price, md5Param, desParam);
//                uuid = asum.kkdecode(source_uuid, md5Param, desParam);
                    log.info("Reserve encode Bean ==>" + source_mdn + "," + source_serviceID + "," + source_price);
                    log.info("Reserve Bean ==>" + mdn + "," + channelID + "," + serviceID + "," + serviceName + "," + price);

                    //查詢取得cardtype 
                    EPAY_SERVICE_INFO serviceInfo = new EPAY_SERVICE_INFO();
                    serviceInfo = epaybusinesscontroller.getServiceInfoByCpidAndServiceId(Long.valueOf(serviceID), Integer.valueOf(channelID));
                    String cardtype = serviceInfo.getCardtype();
                    log.info("CardType===>" + cardtype);

                    //查詢取得tokenId
                    List vcardinfo = epaybusinesscontroller.queryCardByCardtype(cardtype);
                    int recordsize = vcardinfo.size();
                    EPAY_VCARD vcardxx = null;
                    if (recordsize >= 1) {
                        Iterator itvcardinfo = vcardinfo.iterator();
                        for (int i = 1; i < 2; i++) {
                            vcardxx = (EPAY_VCARD) itvcardinfo.next();
                            String vcno = vcardxx.getVcno();
                            log.info("vcno===>" + vcno);
                        }
                        if (!vcardxx.equals(null)) {

                            toolUtil toolutil = new toolUtil();
                            SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                            token = sdf15.format(nowDateTime.getTime()) + toolutil.GetRandomString(10);

                            vcardxx.setChannel(channelID);
                            vcardxx.setMdn(mdn);
                            vcardxx.setStatus("1");
                            vcardxx.setOrderdate(sdf.parse(tradeDate));
                            vcardxx.setOrderexpiredate(sdf.parse(orderexpiredate));
                            vcardxx.setTokenid(token);
                            vcardxx.setUuid(uuid);
                            vcardxx.setServiceid(serviceID);
                            if (epaybusinesscontroller.updateVCard(vcardxx)) {
                                response_timestamp = tradeDate;
                                resultstatus = "Success";
                                resultcode = "0x00000000";
                                reserveFlag = true;

                                //update vcardtype quanity
                                EPAY_VCARDTYPE vcardtypeinfo = new EPAY_VCARDTYPE();
                                vcardtypeinfo = epaybusinesscontroller.queryCardTypeByCardType(cardtype);
                                int old_qty = vcardtypeinfo.getQuantity();
                                int new_qty = old_qty - 1;
                                vcardtypeinfo.setQuantity(new_qty);
                                epaybusinesscontroller.updateVCardType(vcardtypeinfo);
                            } else {
                                response_timestamp = tradeDate;
                                resultstatus = "DB Error";
                                resultcode = "0x03000006";
                            }
                        } else {
                            response_timestamp = tradeDate;
                            resultstatus = "No VCard";
                            resultcode = "0x03000002";
                        }
                    } else {
                        response_timestamp = tradeDate;
                        resultstatus = "No VCard";
                        resultcode = "0x03000002";
                    }
//                    if (vcardxx.getCardtype().equals("")) {

                } else {
                    log.info("Access Deny");
                    resultcode = "0x01000002";
                    resultstatus = "IP Tables";
                }
//                responseMsg = "<DepositResponse>\n"
//                        + "<ResponseHeader status=\"" + encode_resultstatus + "\" result_code=\"" + encode_resultcode + "\" response_timestamp=\"" + encode_response_timestamp + "\">\n"
//                        + "</ResponseHeader>\n"
//                        + "<SubscriberID>" + encode_mdn + "</SubscriberID>\n"
//                        + "<TokenId>" + encode_token + "</TokenId>\n"
//                        + "</DepositResponse>";
            } catch (Exception ex) {
                log.info(ex);
            }
        } else {
            response_timestamp = tradeDate;
            resultstatus = "Data format or validation error";
            resultcode = "0x03000005";
        }

        if (reserveFlag) {
            print_responseMsg = "<ServiceReserveResponse>\n"
                    + "<Result>\n"
                    + "<STATUS>" + resultcode + "</STATUS>\n"
                    + "<STATUS_DESC>" + resultstatus + "</STATUS_DESC>\n"
                    + "<RESPONSE_TIMESTAMP>" + response_timestamp + "</RESPONSE_TIMESTAMP>\n"
                    + "</Result>\n"
                    + "<Order>\n"
                    + "<MDN>" + mdn + "</MDN>\n"
                    + "<TOKENID>" + token + "</TOKENID>\n"
                    + "<VCEXPIRED>" + orderexpiredate + "</VCEXPIRED>\n"
                    + "</Order>\n"
                    + "</ServiceReserveResponse>";

            try {
                String encode_token = "";
                Utilities utilx = new Utilities();
                String encode_resultstatus = utilx.encrypt(pgKey, resultstatus);
                String encode_resultcode = utilx.encrypt(pgKey, resultcode);
                String encode_response_timestamp = utilx.encrypt(pgKey, response_timestamp);
                String encode_mdn = utilx.encrypt(pgKey, mdn);
                String encode_orderexpiredate = utilx.encrypt(pgKey, orderexpiredate);
                if (!token.equals("")) {
                    encode_token = utilx.encrypt(pgKey, token);
                }
//                String encode_resultstatus = (new SecuredMsg(pgKey, identKey).encode(resultstatus)).toString();
//                String encode_resultcode = (new SecuredMsg(pgKey, identKey).encode(resultcode)).toString();
//                String encode_response_timestamp = (new SecuredMsg(pgKey, identKey).encode(response_timestamp)).toString();
//                String encode_mdn = (new SecuredMsg(pgKey, identKey).encode(mdn)).toString();
//                String encode_orderexpiredate = (new SecuredMsg(pgKey, identKey).encode(orderexpiredate)).toString();
//                if (!token.equals("")) {
//                    encode_token = (new SecuredMsg(pgKey, identKey).encode(token)).toString();
//                }

                responseMsg = "<ServiceReserveResponse>\n"
                        + "<Result>\n"
                        + "<STATUS>" + encode_resultcode + "</STATUS>\n"
                        + "<STATUS_DESC>" + encode_resultstatus + "</STATUS_DESC>\n"
                        + "<RESPONSE_TIMESTAMP>" + encode_response_timestamp + "</RESPONSE_TIMESTAMP>\n"
                        + "</Result>\n"
                        + "<Order>\n"
                        + "<MDN>" + encode_mdn + "</MDN>\n"
                        + "<TOKENID>" + encode_token + "</TOKENID>\n"
                        + "<VCEXPIRED>" + encode_orderexpiredate + "</VCEXPIRED>\n"
                        + "</Order>\n"
                        + "</ServiceReserveResponse>";
            } catch (Exception ex) {
                log.info(ex);
            }
        } else { //reserveFlag = false
            print_responseMsg = "<ServiceReserveResponse>\n"
                    + "<Result>\n"
                    + "<STATUS>" + resultcode + "</STATUS>\n"
                    + "<STATUS_DESC>" + resultstatus + "</STATUS_DESC>\n"
                    + "<RESPONSE_TIMESTAMP>" + response_timestamp + "</RESPONSE_TIMESTAMP>\n"
                    + "</Result>\n"
                    + "</ServiceReserveResponse>";

            try {
                Utilities util = new Utilities();
                String encode_resultstatus = util.encrypt(pgKey, resultstatus);
                String encode_resultcode = util.encrypt(pgKey, resultcode);
                String encode_response_timestamp = util.encrypt(pgKey, response_timestamp);
//                String encode_resultstatus = (new SecuredMsg(pgKey, identKey).encode(resultstatus)).toString();
//                String encode_resultcode = (new SecuredMsg(pgKey, identKey).encode(resultcode)).toString();
//                String encode_response_timestamp = (new SecuredMsg(pgKey, identKey).encode(response_timestamp)).toString();

                responseMsg = "<ServiceReserveResponse>\n"
                        + "<Result>\n"
                        + "<STATUS>" + encode_resultcode + "</STATUS>\n"
                        + "<STATUS_DESC>" + encode_resultstatus + "</STATUS_DESC>\n"
                        + "<RESPONSE_TIMESTAMP>" + encode_response_timestamp + "</RESPONSE_TIMESTAMP>\n"
                        + "</Result>\n"
                        + "</ServiceReserveResponse>";
            } catch (Exception ex) {
                log.info(ex);
            }
        }
        log.info("responseMsg==>\n" + print_responseMsg);
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
