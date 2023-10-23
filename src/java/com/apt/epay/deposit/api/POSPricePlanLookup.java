/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.deposit.api;

import com.apt.epay.beans.BasicInfoReqBean;
import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.deposit.util.toolUtil;
import com.apt.epay.nokia.bean.NokiaSubscribeBalanceBean;
import com.apt.epay.nokia.main.NokiaMainBasicInfoUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.epay.soa.util.SOAUtil;
import com.apt.epay.zte.util.ZTEOCS4GBasicInfoUtil;
import com.apt.util.PPMdnUtil;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_PROMOTIONCODE;
import com.epay.ejb.bean.EPAY_SERVICE_INFO;
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
public class POSPricePlanLookup extends HttpServlet {

    private final Logger log = Logger.getLogger("EPAY");

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {

        log.info("POSPricePlanLookup.processRequest");
        log.info("Client IP :" + request.getRemoteAddr());
        
        String mdn = "";

        String cpid = request.getParameter("CPID");
        log.info("CPID===>" + cpid);

        toolUtil tool = new toolUtil();
        Utilities util = new Utilities();
        String deskey = "";
        String responseStr = "";
        String responseStatus = "";
        String responseDesc = "";

        boolean cpkeyflag = false;

        PPMdnUtil mdntool = new PPMdnUtil();
        String responsetime = mdntool.getAPIResponseTime();

        String contractstatuscode = "";
        String ocs_status = "-1";

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
                log.info("===>" + encode_str_input);

                String str_input = util.decrypt(deskey, encode_str_input);
                log.info("POSPricePlanLookup INPUT==>" + str_input);

                
                String serviceId = "";

                String priceplancode = "";
                long db_check_value = 0;
//                int platformtype = 0;

                if (!"".equals(str_input)) {
                    try {
                        mdn = mdntool.getMDNByparseXMLString(str_input, "PricePlanLookupRequest");
                        serviceId = mdntool.getServiceIDByparseXMLString(str_input, "PricePlanLookupRequest");
                        long lserviceId = Long.valueOf(serviceId);

                        log.info( mdn + " PricePlanLookup ===>" + serviceId);

                        if (!"".equalsIgnoreCase(mdn) && !"".equalsIgnoreCase(serviceId)) {
                            //check PricePlan Code
                            EPAY_SERVICE_INFO serviceinfo = null;
                            serviceinfo = epaybusinesscontroller.getServiceInfoByCpidAndServiceId(lserviceId, icpid);

                            if (serviceinfo != null) {
                                priceplancode = serviceinfo.getPriceplancode();
                                db_check_value = serviceinfo.getCheck_value();

                                log.info(mdn + " serviceinfo.getCheck_value()==>" + db_check_value);
                                log.info(mdn + " priceplancode==>" + priceplancode);

                                if (!"".equalsIgnoreCase(priceplancode)) {
                                    String priceplancodeList = new ShareParm().PARM_PRICEPLCODE;
                                    log.info(mdn + " priceplancodeList==>" + priceplancodeList);

                                    if (priceplancodeList.contains(priceplancode)) {

                                        SOAUtil soautil = new SOAUtil();
                                        SOAReqBean soabean = soautil.getSOAInfo(mdn);
                                        String promotioncode = soabean.getPromotioncode();

                                        EPAY_PROMOTIONCODE epaypromotioncode;// = new EPAY_PROMOTIONCODE();
                                        epaypromotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);
                                        int platformtype = epaypromotioncode.getPlatformtype();

                                        log.info(mdn + " platformtype==>" + platformtype);

                                        double count1x = 0.0, count2x = 0.0;
                                        double sumx = 0;

                                        //check bill count
                                        SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                                        Calendar nowDateTime = Calendar.getInstance();
                                        String libm = sdf15.format(nowDateTime.getTime());

                                        if (platformtype == 2) {

                                            ZTEOCS4GBasicInfoUtil ztebasicinfoutil = new ZTEOCS4GBasicInfoUtil();
                                            String basicinfo = ztebasicinfoutil.putZTEOCS4GBasicInfoSlet(libm, mdn);
                                            log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfo);
                                            BasicInfoReqBean basicinforeqbean;
                                            BasicInfoReqBean basicinforeqbeanx;

                                            basicinforeqbean = ztebasicinfoutil.parseZTEBasicInfoXMLString(basicinfo);

                                            log.info("basicinforeqbean.getReal_LifeCycleState()====>" + basicinforeqbean.getReal_LifeCycleState());
                                            //if (("ACTIVE".equalsIgnoreCase(basicinforeqbean.getLifeCycleState()))) {
                                            //Test For 金額比對
                                            ZTEOCS4GBasicInfoUtil ztebasicinfoutilx = new ZTEOCS4GBasicInfoUtil();
                                            String basicinfox = ztebasicinfoutilx.putZTEOCS4GBasicInfoSlet(libm, mdn);
                                            log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfox);

                                            basicinforeqbeanx = ztebasicinfoutilx.parseZTEBasicInfoXMLString(basicinfo);

                                            count1x = Double.valueOf(basicinforeqbeanx.getCounterValue1());
                                            count2x = Double.valueOf(basicinforeqbeanx.getCounterValue2());

                                        } else if (platformtype == 3 ) {

                                            //KK NOKIA
                                            log.info("NOKIA ===>" + mdn + "," + promotioncode);
                                            String pomotion_type = promotioncode.substring(0, 3);
                                            log.info("NOKIA MDN:" + mdn + " Promotion Type(3)==>" + promotioncode + " ==>" + pomotion_type);

                                            NokiaMainBasicInfoUtil nokiaUtil = new NokiaMainBasicInfoUtil();
                                            NokiaSubscribeBalanceBean bean = nokiaUtil.GetMDNBalanceInfo(libm, mdn);

                                            double tmp_d_value_650 = 0.0;
                                            tmp_d_value_650 = Double.valueOf(bean.getAPT5GVoice_650_Counter()) / 10000;
                                            double d_value_650 = Math.round(tmp_d_value_650 * 100.0) / 100.0;

                                            double tmp_d_value_670 = 0.0;
                                            tmp_d_value_670 = Double.valueOf(bean.getAPT5GIDD_670_Counter()) / 10000;
                                            double d_value_670 = Math.round(tmp_d_value_670 * 100.0) / 100.0;

                                            double tmp_d_value_750 = 0.0;
                                            tmp_d_value_750 = Double.valueOf(bean.getAPT5GData_750_Counter()) / 1048576;
                                            double d_value_750 = Math.round(tmp_d_value_750 * 100.0) / 100.0;

                                            log.info(mdn + " Nokia b650==>" + d_value_650 + ", Nokia b670==>" + d_value_670);
                                            log.info(mdn + " Nokia b750==>" + d_value_750);
                                            count1x = d_value_650 + d_value_670;
                                            count2x = d_value_750;

                                        }
                                        sumx = (double) count1x + (double) count2x;
                                        log.info(mdn + ":count1x()==>" + count1x);
                                        log.info(mdn + ":count2x()==>" + count2x);
                                        log.info(mdn + ":sumx===>" + sumx);
                                        log.info(mdn + ":db_check_value==>" + db_check_value);

                                        double checkvalue = sumx + db_check_value;

                                        log.info(mdn + ":sumx + db_check_value(5000)==>" + checkvalue);

                                        if (checkvalue < 5000) {
                                            responseStatus = "0x00000000";
                                            responseDesc = "成功";
                                        } else {
                                            responseStatus = "0x03000002";
                                            responseDesc = "該門號餘額$" + sumx + "上限金額$5000，已超過限額，不可儲值";
                                        }
                                        //} else {
                                        //    responseStatus = "0x03000002";
                                        //    responseDesc = "門號狀態不符";
                                        //}
                                    } else {
                                        responseStatus = "0x00000000";
                                        responseDesc = "成功";
                                    }

                                } else {
                                    responseStatus = "0x03000002";
                                    responseDesc = "Parameter Error, PricePlanCode is Null";
                                    log.info(lserviceId + " Parameter Error, PricePlanCode is Null");
                                }
                            } else {
                                responseStatus = "0x03000002";
                                responseDesc = "ServiceId Error, ServiceId cannot get priceplancode";
                                log.info(lserviceId + " Parameter Error, PricePlanCode is Null");
                            }

                        } else {
                            responseStatus = "0x03000002";
                            responseDesc = "Parameter is Null";
                            log.info("Error MDN:SerivrId===>" + mdn + "," + serviceId);
                        }

                    } catch (Exception ex) {
                        log.info(ex);
                    }
                } else {
                    log.info("POSPricePlanLookup INPUT IS Null");
                }

            }
        } catch (Exception ex) {
            log.info(ex);
        }

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        responseStr = "<PricePlanLookupResponse>\n"
                        + "<Result>\n"
                        + "<STATUS>" + responseStatus + "</STATUS>\n"
                        + "<STATUS_DESC>" + responseDesc + "</STATUS_DESC>\n"
                        + "<RESPONSE_TIMESTAMP>" + responsetime + "</RESPONSE_TIMESTAMP>\n"
                        + "</Result>\n"
                        + "</PricePlanLookupResponse>\n"
                        + "";
        try {

            if (cpkeyflag) {
                String encode_responseStr = util.encrypt(deskey, responseStr);
                log.info(mdn +  " POSpricePlanLookup ResponseStr ===>" + responseStr.replaceAll("[ \t\r\n]+", " ").trim());
                log.info(mdn +  " POSpricePlanLookup encode_responseStr==>" + encode_responseStr);
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
