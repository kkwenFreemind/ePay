/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.deposit.api;

import com.apt.epay.beans.BasicInfoReqBean;
import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.deposit.bean.IbonReqBean;
import com.apt.epay.deposit.util.toolUtil;
import com.apt.epay.nokia.bean.NokiaSubscribeBalanceBean;
import com.apt.epay.nokia.main.NokiaMainBasicInfoUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.soa.util.SOAUtil;
import com.apt.epay.zte.util.ZTEOCS4GBasicInfoUtil;
import com.apt.util.EPaySecuredUrlMsg;
import com.apt.util.PPMdnUtil;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_PROMOTIONCODE;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author kevinchang
 */
public class IBonUserStateLookup extends HttpServlet {

    private final Logger log = Logger.getLogger("EPAY");
    private String identifyCode = "";
    private String key = "";
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

        log.info("IBonUserStateLookup.processRequest");
        log.info("Client IP :" + request.getRemoteAddr());

        String cpid = request.getParameter("CPID");
        log.info("CPID===>" + cpid);

        toolUtil tool = new toolUtil();
        Utilities util = new Utilities();
        String poskey = "";
        String responseStr = "";
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
                poskey = cpinfo.getPoskey();
                key = cpinfo.getEnkey();
                identifyCode = cpinfo.getIdentify();
                log.info("deskey===>" + key + "," + identifyCode + "," + poskey);

                if (!"".equals(poskey)) {
                    cpkeyflag = true;
                }
            } else {
                //cpinfo is null
                log.info("cannot get cpinfo & pos key");
            }

            if (cpkeyflag) {

                EPaySecuredUrlMsg asum = new EPaySecuredUrlMsg(key, identifyCode);
                String decodeMsg = null;

                String requestStr = IOUtils.toString(request.getInputStream());
                decodeMsg = asum.decode(requestStr, md5Param, desParam);

//                
//                ServletInputStream input = request.getInputStream();
//                String encode_str_input = tool.getStringFromInputStream(input);
//                log.info("===>" + encode_str_input);
//                String str_input = util.decrypt(deskey, encode_str_input);
                log.info("IBonUserStateLookup INPUT==>" + decodeMsg);

                try {
//                    String mdn = mdntool.getMDNByparseXMLString(decodeMsg, "UserStateLookupRequest");
                    IbonReqBean ibonbean = new IbonReqBean();
                    ibonbean = mdntool.getIBoneMDNByparseXMLString(decodeMsg, "UserStateLookupRequest");

                    String mdn = ibonbean.getMdn();
                    String value = "0";
                    double check_value = 0;
                    try {
                        value = ibonbean.getValue();
                        check_value = Double.valueOf(value);
                        if (check_value < 0) {
                            check_value = 0;
                        }
                    } catch (Exception ex) {
                        log.info(ex);
                    }
                    log.info("MDN===>" + mdn);
                    log.info("value===>" + value);

                    if (!"".equals(mdn)) {

                        PPMdnUtil ppmdn = new PPMdnUtil(mdn, false, true);
                        String checkresult = ppmdn.checkPPMDN(epaybusinesscontroller);

                        if ("1".equals(checkresult)) { //zte 9,43,active,two-way-block
                            contractstatuscode = ppmdn.getContractstatuscode();
                            ocs_status = ppmdn.getOcsstatus();
                            Status = "0x00000000";
                            log.info("ppmdn.checkPPMDN(" + mdn + ", epaybusinesscontroller)==>" + checkresult);

                            SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                            Calendar nowDateTime = Calendar.getInstance();
                            String libm = sdf15.format(nowDateTime.getTime());

                            SOAUtil soautil = new SOAUtil();
                            SOAReqBean soabean = soautil.getSOAInfo(mdn);
                            String promotioncode = soabean.getPromotioncode();

                            EPAY_PROMOTIONCODE epaypromotioncode;// = new EPAY_PROMOTIONCODE();
                            epaypromotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);
                            int platformtype = epaypromotioncode.getPlatformtype();
                            double count1x = 0.0, count2x = 0.0;

                            if (platformtype == 2) {

                                ZTEOCS4GBasicInfoUtil ztebasicinfoutil = new ZTEOCS4GBasicInfoUtil();
                                String basicinfo = ztebasicinfoutil.putZTEOCS4GBasicInfoSlet(libm, mdn);
                                log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfo);
                                BasicInfoReqBean basicinforeqbean;
                                BasicInfoReqBean basicinforeqbeanx;
                                basicinforeqbean = ztebasicinfoutil.parseZTEBasicInfoXMLString(basicinfo);
                                log.info("basicinforeqbean.getReal_LifeCycleState()====>" + basicinforeqbean.getReal_LifeCycleState());

                                ZTEOCS4GBasicInfoUtil ztebasicinfoutilx = new ZTEOCS4GBasicInfoUtil();
                                String basicinfox = ztebasicinfoutilx.putZTEOCS4GBasicInfoSlet(libm, mdn);
                                log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfox);
                                basicinforeqbeanx = ztebasicinfoutilx.parseZTEBasicInfoXMLString(basicinfo);

                                count1x = Double.valueOf(basicinforeqbeanx.getCounterValue1());
                                count2x = Double.valueOf(basicinforeqbeanx.getCounterValue2());

                            } else if (platformtype == 3) {
                                
                                //KK NOKIA
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

                                log.info("b650==>" + d_value_650 + ",b670==>" + d_value_670);
                                log.info("b750==>" + d_value_750);
                                count1x = d_value_650 + d_value_670;
                                count2x = d_value_750;
                            }

                            double sumx = 0;

                            sumx = (double) count1x + (double) count2x;
                            log.info(mdn + ":count1x()==>" + count1x);
                            log.info(mdn + ":count2x()==>" + count2x);
                            log.info(mdn + ":sumx===>" + sumx);
                            log.info(mdn + ":check_value==>" + check_value);

                            double checkvalue = sumx + check_value;

                            log.info(mdn + ":sumx + db_check_value==>" + checkvalue);

                            if (checkvalue < 5000) {
                                Status = "0x00000000";
                            } else {
                                Status = "0x01000001";
                                Status_Desc = "該門號儲值後餘額，將超過上限金額$5,000元，無法進行儲值";
                            }

                        } else {
                            Status = "0x01000001";
                            Status_Desc = "SOA檢驗失敗";
                        }
                    } else { // mdn is null
                        Status = "0x01000001";
                        Status_Desc = "認證失敗(用戶門號不存在)";
                    }

                    responseStr = "<UserStateLookupResponse>\n"
                                    + "<Result>\n"
                                    + "<STATUS>" + Status + "</STATUS>\n"
                                    + "<STATUS_DESC>" + Status_Desc + "</STATUS_DESC>\n"
                                    + "<RESPONSE_TIMESTAMP>" + responsetime + "</RESPONSE_TIMESTAMP>\n"
                                    + "<OCS_STATE>" + ocs_status + "</OCS_STATE>\n"
                                    + "<CMS_STATE>" + contractstatuscode + "</CMS_STATE>\n"
                                    + "</Result>\n"
                                    + "</UserStateLookupResponse>";
                } catch (Exception ex) {
                    log.info(ex);
                }
            } else {
                Status = "0x02000010";
                Status_Desc = "未授權此功能";
                responseStr = "<UserStateLookupResponse>\n"
                                + "<Result>\n"
                                + "<STATUS>" + Status + "</STATUS>\n"
                                + "<STATUS_DESC>" + Status_Desc + "</STATUS_DESC>\n"
                                + "<RESPONSE_TIMESTAMP>" + responsetime + "</RESPONSE_TIMESTAMP>\n"
                                + "</Result>\n"
                                + "</UserStateLookupResponse>\n";
            }
        } catch (Exception ex) {
            log.info(ex);
        }

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            log.info("cpkeyflag====>" + cpkeyflag);
            if (cpkeyflag) {
                String encode_responseStr = util.encrypt(poskey, responseStr);
                log.info("IBonUserStateLookup ResponseStr ===>\n" + responseStr);
                log.info("IBonUserStateLookup encode_responseStr==>" + encode_responseStr);
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
