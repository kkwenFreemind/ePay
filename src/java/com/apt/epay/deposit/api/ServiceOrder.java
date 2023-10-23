/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.deposit.api;

import com.apt.epay.beans.PinCodeReqBean;
import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.deposit.bean.ServiceOrderReqBean;
import com.apt.epay.deposit.util.OrderProfile;
import com.apt.epay.deposit.util.toolUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.util.KeyUtil;
import com.apt.util.MailUtil;
import com.apt.util.PinCodeUtil;
import com.apt.util.SendSMS;
import com.apt.util.SoaProfile;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_SERVICE_INFO;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import com.epay.ejb.bean.EPAY_VCARD;
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
public class ServiceOrder extends HttpServlet {
    //沒有LOG
    private static final Logger log = Logger.getLogger("EPAY");

    private String response_timestamp;
    private String resultstatus = "";
    private String resultcode = "";
    private String tradeDate;
    private boolean orderFlag;

    private String channelID;
    private String mdn;
    private String serviceID;
    private String serviceName;
    private String token;
    private String price;

    private String pgKey;
    private String identKey;

    private String md5Param = "&identifyCode=";
    private String desParam = "&callerInMac="; //to PaymentGateway use callerInMac, Receive from PaymentGateway use returnOutMac    

    private String libm = "";
    private String contractid = "";
    private String contractstatuscode = "";
    private String producttype = "";
    private final String itemCode = ShareParm.PINCODE_ITEMCODE; //PinCode
    private String message = "";
    private String print_responseMsg;
    private String responseMsg = "";
    private String cpname;
    private String cporderid;

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

        log.info("ServiceOrder.processRequest");
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
        orderFlag = false;

        if (!"".equals(str_input)) {
            OrderProfile orderprofile = new OrderProfile();
            String TagName = "ServiceOrderRequest";
            try {
                ServiceOrderReqBean orderbean = new ServiceOrderReqBean();
                orderbean = orderprofile.parseXMLString(str_input, TagName);
                channelID = orderbean.getChannelID();

                String source_mdn = orderbean.getMdn();
                String source_serviceID = orderbean.getServiceID();
//                String source_serviceName = orderbean.getServiceName();
                String source_tokenid = orderbean.getTokenId();
                String source_price = orderbean.getPrice();
                String source_cporderid = orderbean.getCporderid();

                EPayBusinessConreoller epaybusinesscontroller;
                epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", this.getServletContext());
                EPAY_CP_INFO cpinfo = epaybusinesscontroller.getCpInfo(Integer.valueOf(channelID));
                pgKey = cpinfo.getEnkey();
                identKey = cpinfo.getIdentify();
                cpname = cpinfo.getCpName();

                boolean chkip = epaybusinesscontroller.chkIpValidation(target_ip, Integer.valueOf(channelID));
                if (chkip) {

                    Utilities util = new Utilities();
                    mdn = util.decrypt(pgKey, source_mdn);
                    serviceID = util.decrypt(pgKey, source_serviceID);
                    token = util.decrypt(pgKey, source_tokenid);
                    price = util.decrypt(pgKey, source_price);
                    cporderid = util.decrypt(pgKey, source_cporderid);

//                    ApolSecuredUrlMsg asum = new ApolSecuredUrlMsg(pgKey, identKey);                    
//                    mdn = asum.kkdecode(source_mdn, md5Param, desParam);
//                    serviceID = asum.kkdecode(source_serviceID, md5Param, desParam);
//                    serviceName = asum.kkdecode(source_serviceName, md5Param, desParam);
//                    token = asum.kkdecode(source_tokenid, md5Param, desParam);
//                    price = asum.kkdecode(source_price, md5Param, desParam);
                    EPAY_SERVICE_INFO serviceinfo = new EPAY_SERVICE_INFO();
                    serviceinfo = epaybusinesscontroller.getServiceInfoByCpidAndServiceId(Long.valueOf(serviceID), Integer.valueOf(channelID));
                    serviceName = serviceinfo.getServiceName();

                    log.info("Order encode Bean ==>" + source_mdn + "," + source_serviceID + "," + source_tokenid);
                    log.info("Order Bean ==>" + mdn + "," + channelID + "," + serviceID + "," + serviceName + "," + token);

                    // 產生訂單編號 yymmddHHmissSSS
                    SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                    SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);
                    libm = sdf15.format(nowDateTime.getTime());
                    String tradeDate = sdf.format(nowDateTime.getTime());
                    String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());

                    //記錄和比對是否已有訂單  
                    EPAY_TRANSACTION trans = epaybusinesscontroller.getTransaction(libm);

                    int tradeQuantity = 1;
                    String orderTotal = String.valueOf(Integer.parseInt(price) * tradeQuantity);

                    //get Pincode vcPass
                    EPAY_VCARD vcard = new EPAY_VCARD();
                    log.info("==>" + token + "," + mdn);
                    vcard = epaybusinesscontroller.queryVCardByToken(token, mdn, serviceID);

                    if (vcard != null) {

                        String temppincode = vcard.getVcpass();

                        KeyUtil rsaU = new KeyUtil();
                        rsaU.setKeyPair(rsaU.LoadKeyPair("/opt/jboss/", "private.key", "public.key"));
//                        String base64Str = rsaU.encodeBase64(rsaU.getPublicKey(), temppincode.getBytes());
//                        log.info("base64Str==>" + base64Str);
                        byte[] decBytes = rsaU.decodeBase64(rsaU.getPrivateKey(), temppincode);
                        String pincode = new String(decBytes);

                        //mdn
                        if (mdn != null) {
                            SoaProfile soa = new SoaProfile();
                            String result = soa.putSoaProxyletByMDN(mdn);
                            SOAReqBean apirequestbean = new SOAReqBean();
                            apirequestbean = soa.parseXMLString(result);
                            contractid = apirequestbean.getContractid();
                            contractstatuscode = apirequestbean.getContract_status_code();
                            producttype = apirequestbean.getProducttype();
                        }

                        if (trans == null && !"".equalsIgnoreCase(libm) && !"null".equalsIgnoreCase(libm)) {
                            //如果沒有編號,直接insert新的
                            trans = new EPAY_TRANSACTION();
                            trans.setLibm(libm);
                            trans.setItemcode(itemCode);//PinCode
                            trans.setItemproductname(serviceName);//實體卡片儲值
                            trans.setItemunitprice(Integer.parseInt(price));//0
                            trans.setItemquantity(tradeQuantity);//1
                            trans.setOrdertotal(Integer.parseInt(orderTotal));//0
                            trans.setFee(0);
                            trans.setDiscount(0);
                            trans.setTradedate(sdf.parse(tradeDate));
                            trans.setPaytime(sdf.parse(tradeDate));
                            trans.setPaymethod(ShareParm.PAYMETHOD_PINCODE); //付款方式 PinCode=4
                            trans.setStatus("N"); //OCS尚未儲值完成

//                    trans.setPaystatus(1); //0:失敗(default value), 但PinCode實體卡已繳費完成，所以狀態為1
                            trans.setPrivatedata(pincode); //PinCode number

                            trans.setServiceId(ShareParm.PINCODE_SERVICEID);//
                            trans.setCpLibm(libm);
                            trans.setCpId(Integer.valueOf(channelID));//
                            trans.setFeeType("0"); //無拆帳需求
                            trans.setInvoiceContactMobilePhone(mdn);
                            trans.setContractID(contractid);
                            trans.setServiceId(serviceID);
                            trans.setCpName(cpname);
                            trans.setCpLibm(cporderid);

                            log.info("PinCodeProce(insert Table)==>MDN:" + mdn + ",Libm:" + libm);
                            epaybusinesscontroller.insertTransaction(trans);

                            //Sending Pincode/Password/MDN to OCS
                            log.info("PinCodeProce(MDN:" + mdn + " SendData to OCS)==>libm:" + libm);
                            message = "PinCodeProce(MDN:" + mdn + " SendData to OCS)==>libm:" + libm;

                            PinCodeUtil pincodeutil = new PinCodeUtil();
                            String result = pincodeutil.putPincodeOCSlet(libm, mdn, pincode, tradeDate_Pincode);

                            log.info("PinCodeProce(MDN:" + mdn + " OCS XML Resonse)==>" + result);
                            message = message + "\n" + "PinCodeProce(MDN:" + mdn + " OCS XML Resonse)==>" + result;

                            if (result != null) {
                                PinCodeReqBean apirequestbean = new PinCodeReqBean();
                                apirequestbean = pincodeutil.parsePinCodeXMLString(result);

                                String ocsresultcode = apirequestbean.getResultcode();
//                        String amount = apirequestbean.getAmount();
                                String ocsresultstatus = apirequestbean.getStatus();
                                String ocsresponse_timestamp = apirequestbean.getResponse_timestamp();

                                trans.setStatus(resultcode);
                                log.info("PinCodeProce(MDN:" + mdn + " OCS ResultCode)==>" + ocsresultcode);
                                message = message + "\n" + "PinCodeProce(MDN:" + mdn + " OCS ResultCode)==>" + ocsresultcode;
                                boolean brst = epaybusinesscontroller.updateTransaction(trans);

                                if (ocsresultcode.equals("00")) {
                                    vcard.setStatus("2");
                                    vcard.setSubmitdate(sdf.parse(tradeDate));
                                    epaybusinesscontroller.updateVCard(vcard);

                                    log.info("PinCodeProce(MDN:" + mdn + " update Table status is " + brst + ")==>resultcode:" + ocsresultcode);
                                    message = message + "\n" + "PinCodeProce(MDN:" + mdn + " update Table status is " + brst + ")==>resultcode:" + ocsresultcode;

                                    orderFlag = true;
                                    //儲值成功
                                    //for SMS測試
                                    SendSMS xsms = new SendSMS();
                                    String xmsg = "親愛的用戶您好，您申購的【" + serviceName + "】服務已生效";
                                    xsms.sendsms(mdn, xmsg);

                                    response_timestamp = tradeDate;
                                    resultstatus = "Success";
                                    resultcode = "0x00000000";
                                } else {
                                    //儲值失敗
                                    vcard.setStatus("11");
                                    vcard.setSubmitdate(sdf.parse(tradeDate));
                                    epaybusinesscontroller.updateVCard(vcard);
                                    
                                    SendSMS xsms = new SendSMS();
                                    String ocmdn = new ShareParm().PARM_OC_MDN;
                                    String xmsg = "Epay急件：用戶" + mdn + "，訂單LIB:" + libm + "儲值失敗，請查修";
                                    String email_form = new ShareParm().PARM_MAIL_FROM;
                                    String dst_user = new ShareParm().PARM_MAIL_TO_OC + ";" + new ShareParm().PARM_MAIL_TO_4GOCS;
                                    try {
                                        MailUtil.sendMail(new ShareParm().PARM_MAIL_RELAY_HOST, email_form, dst_user, xmsg, xmsg);
                                    } catch (Exception ex) {
                                        log.info(ex);
                                    }

                                    xsms.sendsms(ocmdn, xmsg);

                                    response_timestamp = tradeDate;
                                    resultstatus = "Order Fail";
                                    resultcode = "0x03000007";
                                }

                            } else {
                                log.info("Admin Exception===>THE PINCODE Result From 4G OCS IS NULL!!!!!");
                                message = message + "\n" + "Admin Exception===>THE PINCODE Result From 4G OCS IS NULL!!!!!";
                            }
                        }
                    } else {
                        response_timestamp = tradeDate;
                        resultstatus = "No VCard";
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

        if (orderFlag) {
            print_responseMsg = "<ServiceOrderResponse>\n"
                    + "<Result>\n"
                    + "<STATUS>" + resultcode + "</STATUS>\n"
                    + "<STATUS_DESC>" + resultstatus + "</STATUS_DESC>\n"
                    + "<RESPONSE_TIMESTAMP>" + response_timestamp + "</RESPONSE_TIMESTAMP>\n"
                    + "</Result>\n"
                    + "<Order>\n"
                    //                    + "<MDN>" + mdn + "</MDN>\n"
                    + "<CPORDERID>" + cporderid + "</CPORDERID>\n"
                    + "<ORDERID>" + libm + "</ORDERID>\n"
                    + "<TOKENID>" + token + "</TOKENID>\n"
                    + "</Order>\n"
                    + "</ServiceOrderResponse>";

            try {
                String encode_token = "";
                Utilities utils = new Utilities();
                String encode_resultstatus = utils.encrypt(pgKey, resultstatus);
                String encode_resultcode = utils.encrypt(pgKey, resultcode);
                String encode_response_timestamp = utils.encrypt(pgKey, response_timestamp);
                String encode_libm = utils.encrypt(pgKey, libm);
                String encode_cporderid = utils.encrypt(pgKey, cporderid);

                if (!token.equals("")) {
                    encode_token = utils.encrypt(pgKey, token);
                }
//                String encode_resultstatus = (new SecuredMsg(pgKey, identKey).encode(resultstatus)).toString();
//                String encode_resultcode = (new SecuredMsg(pgKey, identKey).encode(resultcode)).toString();
//                String encode_response_timestamp = (new SecuredMsg(pgKey, identKey).encode(response_timestamp)).toString();
//                String encode_libm = (new SecuredMsg(pgKey, identKey).encode(libm)).toString();
////                String encode_mdn = (new SecuredMsg(pgKey, identKey).encode(mdn)).toString();
//                if (!token.equals("")) {
//                    encode_token = (new SecuredMsg(pgKey, identKey).encode(token)).toString();
//                }

                responseMsg = "<ServiceOrderResponse>\n"
                        + "<Result>\n"
                        + "<STATUS>" + encode_resultcode + "</STATUS>\n"
                        + "<STATUS_DESC>" + encode_resultstatus + "</STATUS_DESC>\n"
                        + "<RESPONSE_TIMESTAMP>" + encode_response_timestamp + "</RESPONSE_TIMESTAMP>\n"
                        + "</Result>\n"
                        + "<Order>\n"
                        //                        + "<MDN>" + encode_mdn + "</MDN>\n"
                        + "<CPORDERID>" + encode_cporderid + "</CPORDERID>\n"
                        + "<ORDERID>" + encode_libm + "</ORDERID>\n"
                        + "<TOKENID>" + encode_token + "</TOKENID>\n"
                        + "</Order>\n"
                        + "</ServiceOrderResponse>";
            } catch (Exception ex) {
                log.info(ex);
            }
        } else { // orderflag = false
            print_responseMsg = "<ServiceOrderResponse>\n"
                    + "<Result>\n"
                    + "<STATUS>" + resultcode + "</STATUS>\n"
                    + "<STATUS_DESC>" + resultstatus + "</STATUS_DESC>\n"
                    + "<RESPONSE_TIMESTAMP>" + response_timestamp + "</RESPONSE_TIMESTAMP>\n"
                    + "</Result>\n"
                    + "<Order>\n"
                    //                    + "<MDN>" + mdn + "</MDN>\n"
                    + "<CPORDERID>" + cporderid + "</CPORDERID>\n"
                    + "<TOKENID>" + token + "</TOKENID>\n"
                    + "</Order>\n"
                    + "</ServiceOrderResponse>";

            try {
                String encode_token = "";
                Utilities utilsx = new Utilities();
                String encode_resultstatus = utilsx.encrypt(pgKey, resultstatus);
                String encode_resultcode = utilsx.encrypt(pgKey, resultcode);
                String encode_response_timestamp = utilsx.encrypt(pgKey, response_timestamp);
                String encode_cporderid = utilsx.encrypt(pgKey, cporderid);
                if (!token.equals("")) {
                    encode_token = utilsx.encrypt(pgKey, token);
                }
//                String encode_resultstatus = (new SecuredMsg(pgKey, identKey).encode(resultstatus)).toString();
//                String encode_resultcode = (new SecuredMsg(pgKey, identKey).encode(resultcode)).toString();
//                String encode_response_timestamp = (new SecuredMsg(pgKey, identKey).encode(response_timestamp)).toString();
//                if (!token.equals("")) {
//                    encode_token = (new SecuredMsg(pgKey, identKey).encode(token)).toString();
//                }

                responseMsg = "<ServiceOrderResponse>\n"
                        + "<Result>\n"
                        + "<STATUS>" + encode_resultcode + "</STATUS>\n"
                        + "<STATUS_DESC>" + encode_resultstatus + "</STATUS_DESC>\n"
                        + "<RESPONSE_TIMESTAMP>" + encode_response_timestamp + "</RESPONSE_TIMESTAMP>\n"
                        + "</Result>\n"
                        + "<Order>\n"
                        //                        + "<MDN>" + encode_mdn + "</MDN>\n"
                         + "<CPORDERID>" + encode_cporderid + "</CPORDERID>\n"
                        + "<TOKENID>" + encode_token + "</TOKENID>\n"
                        + "</Order>\n"
                        + "</ServiceOrderResponse>";
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
