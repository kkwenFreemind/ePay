/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.deposit.api;

import com.apt.epay.beans.BasicInfoReqBean;
import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.deposit.bean.SubscriberLookupReqBean;
import com.apt.epay.deposit.util.SubscriberProfile;
import com.apt.epay.deposit.util.toolUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.util.OCS4GBasicInfoUtil;
import com.apt.util.SoaProfile;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_SERVICE_INFO;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
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
public class ServiceLookup extends HttpServlet {

    private static final Logger log = Logger.getLogger("EPAY");
    //沒有LOG
    private String channelID;
    private String mdn;
    private String contractstatuscode;
    private String promotioncode;
    private String lifecyclestatus;

    private String responseMsg = "";
    private String status = "";
//    private String reasonCode = "";
    private String message = "";
    private String md5Param = "&identifyCode=";
    private String desParam = "&callerInMac="; //to PaymentGateway use callerInMac, Receive from PaymentGateway use returnOutMac    
    private String encode_status;
//    private String encode_reasonCode;
    private String encode_message;
    private String response_timestamp;
    private String encode_response_timestamp;
    private boolean responflag;
    private String pgKey = "";
    private String identKey = "";
    private String print_responseMsg = "";

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

        log.info("ServiceLookup.processRequest");
        
        String headIP = request.getHeader("X-Forwarded-For");
        String target_ip =null;
        
        if(headIP == null){
            target_ip = request.getRemoteAddr();
        }else{
            StringTokenizer stk_ip = new StringTokenizer(headIP, ",");
            while (stk_ip.hasMoreTokens()) {
                target_ip = stk_ip.nextToken();
                log.info("targetIP===>"+target_ip);
            }
        }

        String logMsg = "Client IP :" + request.getRemoteAddr();
        log.info(logMsg);

        ServletInputStream input = request.getInputStream();
        toolUtil tool = new toolUtil();
        String str_input = tool.getStringFromInputStream(input);
        log.info("INPUT==>" + str_input);

        responflag = false;

        if (!"".equals(str_input)) {
            SubscriberProfile subscriber = new SubscriberProfile();
            String TagName = "ServiceLookupRequest";
            try {

                EPayBusinessConreoller epaybusinesscontroller;
                epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", this.getServletContext());

                SubscriberLookupReqBean SubscriberLookupreqbean = new SubscriberLookupReqBean();
                SubscriberLookupreqbean = subscriber.parseXMLString(str_input, TagName);

                channelID = SubscriberLookupreqbean.getChannelID();

                boolean chkip = epaybusinesscontroller.chkIpValidation(target_ip, Integer.valueOf(channelID));

                if (chkip) {

                    String source_mdn = SubscriberLookupreqbean.getMdn();

                    EPAY_CP_INFO cpinfo = null;
                    cpinfo = epaybusinesscontroller.getCpInfo(Integer.valueOf(channelID));
                    pgKey = cpinfo.getEnkey();
                    identKey = cpinfo.getIdentify();
                    log.info("pgKey,identKey==>" + pgKey + "," + identKey);

                    if (!"".equals(pgKey)) {
                        
                        Utilities util = new Utilities();
                        mdn = util.decrypt(pgKey, source_mdn);
//                        ApolSecuredUrlMsg asum;
//                        asum = new ApolSecuredUrlMsg(pgKey, identKey);
//                        mdn = asum.kkdecode(source_mdn, md5Param, desParam);

                        log.info("SubscriberLookupreqbean cpid==>" + SubscriberLookupreqbean.getChannelID());
                        log.info("SubscriberLookupreqbean mdn==>" + SubscriberLookupreqbean.getMdn());

                        SoaProfile soa = new SoaProfile();
                        String result = soa.putSoaProxyletByMDN(mdn);
                        SOAReqBean apirequestbean = new SOAReqBean();
                        apirequestbean = soa.parseXMLString(result);

                        String soa_result_code = apirequestbean.getResultcode();
                        promotioncode = apirequestbean.getPromotioncode();
                        contractstatuscode = apirequestbean.getContract_status_code();

                        SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                        SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);
                        Calendar nowDateTime = Calendar.getInstance();
                        String libm = sdf15.format(nowDateTime.getTime());
                        OCS4GBasicInfoUtil basicinfoutil = new OCS4GBasicInfoUtil();
                        String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());
                        String basicinfo = basicinfoutil.putOCS4GBasicInfoSlet(libm, mdn, tradeDate_Pincode);

                        BasicInfoReqBean basicinforeqbean = new BasicInfoReqBean();
                        basicinforeqbean = basicinfoutil.parseBasicInfoXMLString(basicinfo);
                        lifecyclestatus = basicinforeqbean.getLifeCycleState();
                        log.info("MDN==>" + mdn + "," + promotioncode + "," + contractstatuscode + "," + lifecyclestatus);

                        if (soa_result_code.equals("00000000")) {

                            List serviceinfo1 = epaybusinesscontroller.listAllServiceInfoByPromotion(Integer.valueOf(channelID), "/"+promotioncode, "/"+contractstatuscode, "/"+lifecyclestatus);
                            Iterator itserviceinfo1 = serviceinfo1.iterator();
                            int j = 0;

                            status = "0x00000000";
                            message = "Success";

                            SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
                            String tradeDate = sdf.format(nowDateTime.getTime());
                            response_timestamp = tradeDate;

                            print_responseMsg = "<ServiceLookupResponse>\n"
                                    + "<Result>"
                                    + "<STATUS>" + status + "</STATUS>"
                                    + "<STATUS_DESC>" + message + "</STATUS_DESC>"
                                    + "<RESPONSE_TIMESTAMP>" + response_timestamp + "</RESPONSE_TIMESTAMP>"
                                    + "</Result>\n";

                            //encode_status = (new SecuredMsg(pgKey, identKey).encode(status)).toString();
                            //                        encode_reasonCode = (new SecuredMsg(pgKey, identKey).encode(reasonCode)).toString();
                            //encode_message = (new SecuredMsg(pgKey, identKey).encode(message)).toString();
                            //encode_response_timestamp = (new SecuredMsg(pgKey, identKey).encode(response_timestamp)).toString();
                            encode_status = util.encrypt(pgKey,status);
                            encode_message = util.encrypt(pgKey,message);
                            encode_response_timestamp =util.encrypt(pgKey,response_timestamp);

                            responseMsg = "<ServiceLookupResponse>\n"
                                    + "<Result>\n"
                                    + "<STATUS>" + encode_status + "</STATUS>\n"
                                    + "<STATUS_DESC>" + encode_message + "</STATUS_DESC>\n"
                                    + "<RESPONSE_TIMESTAMP>" + encode_response_timestamp + "</RESPONSE_TIMESTAMP>\n"
                                    + "</Result>\n";

                            if (serviceinfo1.size() > 0) {

                                while (itserviceinfo1.hasNext()) {
                                    j++;
                                    EPAY_SERVICE_INFO serid = (EPAY_SERVICE_INFO) itserviceinfo1.next();

                                    String serviceId = String.valueOf(serid.getServiceId());
                                    String serviceName = serid.getServiceName();
                                    String price = String.valueOf(serid.getPrice());
                                    String note = serid.getNote();

                                    print_responseMsg = print_responseMsg + "<Promotion>\n"
                                            + "<SERVICEID>" + serviceId + "</SERVICEID>\n"
                                            + "<SERVICENAME>" + serviceName + "</SERVICENAME>\n"
                                            + "<PRICE>" + price + "</PRICE>\n"
                                            + "<NOTE>" + note + "</NOTE>\n"
                                            + "</Promotion>\n";
//                                print_responseMsg = print_responseMsg + "<Promotion Sequence_No=\"" + j + "\"\n"
//                                        + "ServiceID=\"" + serviceId + "\"\n"
//                                        + "ServiceName=\"" + serviceName + "\"\n"
//                                        + "Price=\"" + price + "\"\n"
//                                        + "Note=\"" + note + "\"\n"
//                                        + "></Promotion>\n";

//                                    String encode_serviceId = (new SecuredMsg(pgKey, identKey).encode(serviceId)).toString();
//                                    String encode_serviceName = (new SecuredMsg(pgKey, identKey).encode(serviceName)).toString();
//                                    String encode_Price = (new SecuredMsg(pgKey, identKey).encode(price)).toString();
//                                    String encode_Note = (new SecuredMsg(pgKey, identKey).encode(note)).toString();

                                    String encode_serviceId = util.encrypt(pgKey,serviceId);
                                    String encode_serviceName = util.encrypt(pgKey,serviceName);
                                    String encode_Price = util.encrypt(pgKey,price);
                                    String encode_Note =  util.encrypt(pgKey,note);             
                                    
                                    responseMsg = responseMsg + "<Promotion>\n"
                                            + "<SERVICEID>" + encode_serviceId + "</SERVICEID>\n"
                                            + "<SERVICENAME>" + encode_serviceName + "</SERVICENAME>\n"
                                            + "<PRICE>" + encode_Price + "</PRICE>\n"
                                            + "<NOTE>" + encode_Note + "</NOTE>\n"
                                            + "</Promotion>\n";
                                }

                                print_responseMsg = print_responseMsg + "</ServiceLookupResponse>";
                                responseMsg = responseMsg + "</ServiceLookupResponse>";
                                responflag = true;
                            } else {
                                log.info("No Promotion");
                                status = "0x02000010";
                                message = "No Promotion";
                            }
                        } else {
                            log.info("No Promotion");
                            status = "0x02000010";
                            message = "No Promotion";
                        }

                    } else {
                        log.info("Not PP User"); //SOA查無此用戶
                        status = "0x01000001";
                        message = "Unknown PP User";

                    }
                } else {
                        log.info("Access Deny"); 
                        status = "0x01000002";
                        message = "IP Tables";                    
                }
            } catch (Exception ex) {
                log.info(ex);
            }

        } else {
            //輸入格式有錯誤
            status = "0x03000005";
            message = "Data format or validation error";
        }

        if (!responflag) {

            try {
//                encode_status = (new SecuredMsg(pgKey, identKey).encode(status)).toString();
//                encode_message = (new SecuredMsg(pgKey, identKey).encode(message)).toString();
                Utilities utils = new Utilities();
                encode_status = utils.encrypt(pgKey,status);     
                encode_message = utils.encrypt(pgKey,message);     
                
                print_responseMsg = "<ServiceLookupResponse>\n"
                        + "<Result>\n"
                        + "<STATUS>" + status + "</STATUS>\n"
                        + "<STATUS_DESC>" + message + "</STATUS_DESC>\n"
                        + "<RESPONSE_TIMESTAMP>" + response_timestamp + "</RESPONSE_TIMESTAMP>\n"
                        + "</Result>\n"
                        + "</ServiceLookupResponse>";

                responseMsg = "<ServiceLookupResponse>\n"
                        + "<Result>\n"
                        + "<STATUS>" + encode_status + "</STATUS>\n"
                        + "<STATUS_DESC>" + encode_message + "</STATUS_DESC>\n"
                        + "<RESPONSE_TIMESTAMP>" + encode_response_timestamp + "</RESPONSE_TIMESTAMP>\n"
                        + "</Result>\n"
                        + "</ServiceLookupResponse>";
            } catch (Exception ex) {
                log.info(ex);
            }
        }

        log.info("responseMsg==>" + print_responseMsg);
        log.info("responseMsg==>" + responseMsg);

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.print(responseMsg);

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
