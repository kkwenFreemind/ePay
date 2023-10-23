/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.deposit.api;

import com.apt.epay.beans.PinCodeReqBean;
import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.deposit.bean.DepositReqBean;
import com.apt.epay.deposit.util.DepostProfile;
import com.apt.epay.deposit.util.toolUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.util.ApolSecuredUrlMsg;
import com.apt.util.PinCodeUtil;
import com.apt.util.SecuredMsg;
import com.apt.util.SoaProfile;
import com.epay.ejb.bean.EPAY_CP_INFO;
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
//import org.zkoss.zk.ui.Executions;

/**
 *
 * @author kevinchang
 */
public class Deposit extends HttpServlet {

    private static final Logger log = Logger.getLogger("EPAY");

    private final String cpid = new ShareParm().PARM_EPAY_CPID;
    private String libm = "";
    private final String itemCode = ShareParm.PINCODE_ITEMCODE; //PinCode
    private String channelID;
    private String mdn;
    private String serviceID;
    private String serviceName;
    private String price;
    private String uuid;
    private String tokenId;

    private String contractid = "";
    private String contractstatuscode = "";
    private String producttype = "";
    private String responseMsg = "";
    private String print_responseMsg;
    private String message = "";

    private String md5Param = "&identifyCode=";
    private String desParam = "&callerInMac="; //to PaymentGateway use callerInMac, Receive from PaymentGateway use returnOutMac    

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

        log.info("Deposit.processRequest");
        String logMsg = "Client IP :" + request.getRemoteAddr();
        log.info(logMsg);

        ServletInputStream input = request.getInputStream();
        toolUtil tool = new toolUtil();
        String str_input = tool.getStringFromInputStream(input);
        log.info("INPUT==>" + str_input);

        if (!"".equals(str_input)) {

            DepostProfile depositprofile = new DepostProfile();
            String TagName = "DepositRequest";

            try {

                DepositReqBean depositbean = new DepositReqBean();
                depositbean = depositprofile.parseXMLString(str_input, TagName);
                channelID = depositbean.getChannelID();

                EPayBusinessConreoller epaybusinesscontroller;
                epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", this.getServletContext());

                String source_mdn = depositbean.getMdn();
                String source_serviceID = depositbean.getServiceID();
                String source_serviceName = depositbean.getServiceName();
                String source_price = depositbean.getPrice();
                String source_uuid =  depositbean.getUuid();
                String source_tokenId =  depositbean.getTokenId();

                EPAY_CP_INFO cpinfo = epaybusinesscontroller.getCpInfo(Integer.valueOf(channelID));
                String pgKey = cpinfo.getEnkey();
                String identKey = cpinfo.getIdentify();

                ApolSecuredUrlMsg asum = new ApolSecuredUrlMsg(pgKey, identKey);
                mdn = asum.kkdecode(source_mdn, md5Param, desParam);
                serviceID = asum.kkdecode(source_serviceID, md5Param, desParam);
                serviceName = asum.kkdecode(source_serviceName, md5Param, desParam);
                price = asum.kkdecode(source_price, md5Param, desParam);
                uuid =  asum.kkdecode(source_uuid, md5Param, desParam);
                tokenId = asum.kkdecode(source_tokenId, md5Param, desParam);

//                String encode_uuid = (new SecuredMsg(pgKey, identKey).encode("A123456780")).toString();
//                String encode_orderid = (new SecuredMsg(pgKey, identKey).encode("3")).toString();
//                log.info("encode_uuid==>"+encode_uuid);
//                log.info("encode_orderid==>"+encode_orderid);
                        
//                String encode_mdn = (new SecuredMsg(pgKey, identKey).encode(mdn)).toString();
//                String encode_serviceID = (new SecuredMsg(pgKey, identKey).encode(serviceID)).toString();
//                String encode_serviceName = (new SecuredMsg(pgKey, identKey).encode(serviceName)).toString();
//                String encode_price = (new SecuredMsg(pgKey, identKey).encode(price)).toString();
                //Deposit Bean ==>0906077724,20001,1000100001,4G LTE 通信費 300 元儲值卡,100,A123456789,3
                /*
                 s%F1%24%5D%A12a%A4%A2%80%FC%D9%BD%3F%24aB%D7%89%05%BC%DDUb%FF%3D%A4%EE%80%FF%07%F00S%DB%1A%D2%AC*%BA%3E%B66V%AD%236%60P%ED%B4%80M%84%94-,
                 %1A%BFB%EEM1%CBn%7B%F7P%19%1A%05%FB%5E%8B%BE%DF%1E%B7%3D%FE%18%1A%F6%FE%FD.%40%ACf%BF%5Db%84*C1%1C%28%25%956%CA%0Co%5BJ%1Fp%91%DE%85o%15,
                 %28%3Ae%C47%93%8B%26%98%8B%08Q%E2%B0%09%89%C3%CD%18%B9%B0%DC%EDZ%00aB%A7%C9-w%18%8E%3B%A2%83%ABH%B9%1B%CC%04%02t%05%15%8F%9D%93%C9x%01%13%F6Ds1%BF%E9%FB%81+%E0%AC%A7%A8%A9%C7%FF%0C%0Ed%FB%EDn%AD%E8%AF%EA%98,
                 .%7BF%25%17%AD%E46%3Eq%04%FA%D8%91%22%99%ED.%A3S%29%E4%D2F%7EX%CC%2B%B0%FE%22%FC%EFw%F8%D7%D8fG%D2%27%C3%3F%07%60%B2%C85C%AB%E06%D9j%93%9C
                 */
//                log.info("Deposit EncodeBean ==>" + encode_mdn + "," + encode_serviceID + "," + encode_serviceName + "," + encode_price);
                log.info("Deposit encode Bean ==>" + source_mdn + "," + source_serviceID + "," + source_serviceName + "," + source_price);
                log.info("Deposit Bean ==>" + mdn + "," + channelID + "," + serviceID + "," + serviceName + "," + price);

                if (mdn != null) {
                    SoaProfile soa = new SoaProfile();
                    String result = soa.putSoaProxyletByMDN(mdn);
                    SOAReqBean apirequestbean = new SOAReqBean();
                    apirequestbean = soa.parseXMLString(result);
                    contractid = apirequestbean.getContractid();
                    contractstatuscode = apirequestbean.getContract_status_code();
                    producttype = apirequestbean.getProducttype();
                }

                // 產生訂單編號 yymmddHHmissSSS
                SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
                SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);
                Calendar nowDateTime = Calendar.getInstance();
                libm = sdf15.format(nowDateTime.getTime());
                String tradeDate = sdf.format(nowDateTime.getTime());
                String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());

                //記錄和比對是否已有訂單  
                EPAY_TRANSACTION trans = epaybusinesscontroller.getTransaction(libm);

                int tradeQuantity = 1;
                String orderTotal = String.valueOf(Integer.parseInt(price) * tradeQuantity);

                //get Pincode vcPass
                EPAY_VCARD vcard = new EPAY_VCARD();
                vcard = epaybusinesscontroller.getVCPass(tokenId);
                String pincode = vcard.getVcpass();
                log.info("Pincode===>" + pincode);

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
                    trans.setCpId(Integer.parseInt(new ShareParm().PARM_EPAY_CPID));//
                    trans.setFeeType("0"); //無拆帳需求
                    trans.setInvoiceContactMobilePhone(mdn);
                    trans.setContractID(contractid);
                    trans.setServiceId(serviceID);

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

                        String resultcode = apirequestbean.getResultcode();
//                        String amount = apirequestbean.getAmount();
                        String resultstatus = apirequestbean.getStatus();
                        String response_timestamp = apirequestbean.getResponse_timestamp();

                        trans.setStatus(resultcode);
                        log.info("PinCodeProce(MDN:" + mdn + " OCS ResultCode)==>" + resultcode);
                        message = message + "\n" + "PinCodeProce(MDN:" + mdn + " OCS ResultCode)==>" + resultcode;

                        boolean brst = epaybusinesscontroller.updateTransaction(trans);

                        if (!resultcode.equals("00")) {
                            EPAY_VCARD xcard = new EPAY_VCARD();
                            xcard = epaybusinesscontroller.getVCPass(tokenId);
                            log.info("xcard.getVCPass====>"+xcard.getVcpass());
                            
                            xcard.setStatus("2");
                            xcard.setSubmitdate(sdf.parse(tradeDate));
                            xcard.setChannel(channelID);
                            xcard.setUuid(uuid);
                            epaybusinesscontroller.updateVCard(xcard);
                        }

                        log.info("PinCodeProce(MDN:" + mdn + " update Table status is " + brst + ")==>resultcode:" + resultcode);
                        message = message + "\n" + "PinCodeProce(MDN:" + mdn + " update Table status is " + brst + ")==>resultcode:" + resultcode;

                        print_responseMsg = "<DepositResponse>\n"
                                + "<ResponseHeader status=\"" + resultstatus + "\" result_code=\"" + resultcode + "\" response_timestamp=\"" + response_timestamp + "\">\n"
                                + "</ResponseHeader>\n"
                                + "<SubscriberID>" + mdn + "</SubscriberID>\n"
                                + "<LibmID>" + libm + "</LibmID>\n"
                                + "</DepositResponse>";

                        String encode_resultstatus = (new SecuredMsg(pgKey, identKey).encode(resultstatus)).toString();
                        String encode_resultcode = (new SecuredMsg(pgKey, identKey).encode(resultcode)).toString();
                        String encode_response_timestamp = (new SecuredMsg(pgKey, identKey).encode(response_timestamp)).toString();
                        String encode_mdn = (new SecuredMsg(pgKey, identKey).encode(mdn)).toString();
                        String encode_libm = (new SecuredMsg(pgKey, identKey).encode(libm)).toString();

                        responseMsg = "<DepositResponse>\n"
                                + "<ResponseHeader status=\"" + encode_resultstatus + "\" result_code=\"" + encode_resultcode + "\" response_timestamp=\"" + encode_response_timestamp + "\">\n"
                                + "</ResponseHeader>\n"
                                + "<SubscriberID>" + encode_mdn + "</SubscriberID>\n"
                                + "<LibmID>" + encode_libm + "</LibmID>\n"
                                + "</DepositResponse>";

                    } else {
                        log.info("Admin Exception===>THE PINCODE Result From 4G OCS IS NULL!!!!!");
                        message = message + "\n" + "Admin Exception===>THE PINCODE Result From 4G OCS IS NULL!!!!!";
                    }
                }

            } catch (Exception ex) {
                log.info(ex);
            }
        }

        log.info("responseMsg==>" + print_responseMsg);
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println(responseMsg);
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
