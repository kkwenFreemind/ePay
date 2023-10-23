/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.dealer.api;

import com.apt.epay.beans.BasicInfoReqBean;
import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.deposit.bean.UserAccountLookupReqBean;
import com.apt.epay.deposit.util.toolUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.zte.util.ZTEOCS4GBasicInfoUtil;
import com.apt.util.BundleDateUtil;
import com.apt.util.PPMdnUtil;
import com.apt.util.SoaProfile;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_DEALERMDN;
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
public class DealerServiceLookup extends HttpServlet {

    private final Logger log;
    private final String xcode = "餘額抵扣";

    public DealerServiceLookup() {
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

        log.info("DealerServiceLookup.processRequest");
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

        try {

            EPayBusinessConreoller epaybusinesscontroller;
            epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", this.getServletContext());

            int icpid = Integer.valueOf(cpid);
            EPAY_CP_INFO cpinfo = epaybusinesscontroller.getCpInfo(icpid);
            String Status;// = "0x00000000";
            String Status_Desc;// = "成功";

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
                log.info("DealerServiceLookup INPUT==>" + str_input);

                try {
                    UserAccountLookupReqBean useraccountbean;
                    useraccountbean = mdntool.DealerUserAccountParseXMLString(str_input, "ServiceLookupRequest");
                    String mdn = useraccountbean.getMdn();
                    String storeid = useraccountbean.getStoreid();
                    String storename = useraccountbean.getStorename();

//                    String mdn = mdntool.getMDNByparseXMLString(str_input, "ServiceLookupRequest");
                    log.info("MDN===>" + mdn);

                    if (!"".equals(mdn)) {

                        EPAY_DEALERMDN epaydealermdn = epaybusinesscontroller.getDealerMDNByMDN(storeid, mdn);

                        if (epaydealermdn != null) {

                            SoaProfile soa = new SoaProfile();
                            String result = soa.putSoaProxyletByMDN(mdn);
                            SOAReqBean apirequestbean = new SOAReqBean();
                            apirequestbean = soa.parseXMLString(result);

                            log.info("kkflag==>CMSAdjustAccountDeposit:" + result);

                            String contractid = apirequestbean.getContractid();
                            String contractstatuscode = apirequestbean.getContract_status_code();
                            String producttype = apirequestbean.getProducttype();
                            String promotioncode = apirequestbean.getPromotioncode();
                            mdn = apirequestbean.getMdn();

                            EPAY_PROMOTIONCODE epaypromotioncode = new EPAY_PROMOTIONCODE();
                            epaypromotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);
                            int platformtype = epaypromotioncode.getPlatformtype();

                            BasicInfoReqBean basicinforeqbean = new BasicInfoReqBean();
                            BasicInfoReqBean basicinforeqbeanx = new BasicInfoReqBean();
                            int ddday = 0;
                            int sumx = 0;

                            //for 合約到期日比對天數
                            SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                            Calendar nowDateTime = Calendar.getInstance();
                            String libm = sdf15.format(nowDateTime.getTime());

                            ZTEOCS4GBasicInfoUtil ztebasicinfoutil = new ZTEOCS4GBasicInfoUtil();
                            String basicinfo = ztebasicinfoutil.putZTEOCS4GBasicInfoSlet(libm, mdn);
                            log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfo);

                            basicinforeqbean = ztebasicinfoutil.parseZTEBasicInfoXMLString(basicinfo);

                            String d1 = basicinforeqbean.getEndDate1();
                            log.info("d1===>" + d1);

                            BundleDateUtil bdutil = new BundleDateUtil();
                            ddday = bdutil.GetDiffDayOfContractDay(d1);
                            if (ddday > 0) {
                                log.info("DiffDate====>" + ddday);
                            } else {
                                //合約到期日小於今天
                                log.info("DiffDate====>" + ddday);
                            }

                            //Test For 金額比對
                            ZTEOCS4GBasicInfoUtil ztebasicinfoutilx = new ZTEOCS4GBasicInfoUtil();
                            String basicinfox = ztebasicinfoutilx.putZTEOCS4GBasicInfoSlet(libm, mdn);
                            log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfox);

                            basicinforeqbeanx = ztebasicinfoutilx.parseZTEBasicInfoXMLString(basicinfo);

                            double count1x = Double.valueOf(basicinforeqbeanx.getCounterValue1());
                            double count2x = Double.valueOf(basicinforeqbeanx.getCounterValue2());
                            sumx = (int) count1x + (int) count2x;
//                            textbox_amount.setValue(String.valueOf(sumx));

                            PPMdnUtil ppmdn = new PPMdnUtil(mdn, true, true); //PPMdnUtil(String mdn, boolean aluflag, boolean zteflag)
                            String checkresult = ppmdn.checkPPMDN(epaybusinesscontroller);

                            int pptype = 0;
                            pptype = ppmdn.getPlatformtype();
                            log.info("ppmdn.getPlatformtype()===>" + pptype);

                            Status = "0x00000000";
                            log.info("ppmdn.checkPPMDN(" + mdn + ", epaybusinesscontroller)==>" + checkresult);

                            if (!"-1".equals(checkresult)) {
                                Status_Desc = "成功";
                                responseStr = "<ServiceLookupResponse>\n"
                                        + "<Result>\n"
                                        + "<STATUS>" + Status + "</STATUS>\n"
                                        + "<STATUS_DESC>" + Status_Desc + "</STATUS_DESC>\n"
                                        + "<RESPONSE_TIMESTAMP>" + responsetime + "</RESPONSE_TIMESTAMP>\n"
                                        + "<USERSTATE>" + checkresult + "</USERSTATE>\n"
                                        + "<PPTYPE>" + pptype + "</PPTYPE>\n"
                                        + "</Result>\n";

//                                List PosServiceinfo;
//                                log.info("list serviceinfo==>" + cpid + "," + ppmdn.getPromotioncode() + "," + ppmdn.getPlatformtype());
                                List dealer_serviceinfo1 = null;
                                
                                log.info("cpid====>"+Integer.valueOf(cpid));
                                log.info("promotioncode====>"+promotioncode);

                                dealer_serviceinfo1 = epaybusinesscontroller.listAdjustAccServiceInfo(Integer.valueOf(cpid), promotioncode,xcode, platformtype);
//
//                                dealer_serviceinfo1 = epaybusinesscontroller.listAllServiceInfo(Integer.valueOf(cpid), ppmdn.getPromotioncode(), ppmdn.getPlatformtype());
                                Iterator itserviceinfo = dealer_serviceinfo1.iterator();
                                int DealerServiceinfosize = dealer_serviceinfo1.size();

                                log.info("Serviceinfo.size()===>" + DealerServiceinfosize);

                                int j = 0, x = 0, y = 0;
                                if (dealer_serviceinfo1.size() > 0) {

                                    while (itserviceinfo.hasNext()) {
                                        EPAY_SERVICE_INFO serid = (EPAY_SERVICE_INFO) itserviceinfo.next();
                                        int sid_dday = serid.getDday(); // 延展天數
                                        int s_price = serid.getPrice(); // 服務折抵金額
                                        // sumx : 610+620 金額
                                        // ddday : 合約到期日到今日的天數
                                        String wording = getWording(sumx, ddday, s_price, sid_dday);

                                        if ((sid_dday <= ddday) && (s_price <= sumx)) {
                                            y = j;
                                            EPAY_SERVICE_INFO serviceinfo = (EPAY_SERVICE_INFO) itserviceinfo.next();
                                            String serviceId = String.valueOf(serviceinfo.getServiceId());
                                            String serviceName = serviceinfo.getServiceName();
                                            String price = String.valueOf(serviceinfo.getPrice());
                                            String note = serviceinfo.getNote();
                                            String servicematcode = serviceinfo.getServicematcode();
                                            log.info(j  + "==>" + serviceId + "," + serviceName + "," + price + "," + note);
                                            responseStr = responseStr + "<ServiceList>\n<SERVICEID>" + serviceId + "</SERVICEID><SERVICENAME>" + serviceName + "</SERVICENAME><SERVICEMATCODE>" + servicematcode + "</SERVICEMATCODE><PRICE>" + price + "</PRICE><SERVICEDESC>" + note + "</SERVICEDESC>\n</ServiceList>\n";
                                        } else {
                                            x++;

                                        }

                                        j++;
                                    }
                                }

                                responseStr = responseStr + "</ServiceLookupResponse>";
                            } else {
                                Status_Desc = "失敗";
                                responseStr = "<ServiceLookupResponse>\n"
                                        + "<Result>\n"
                                        + "<STATUS>" + Status + "</STATUS>\n"
                                        + "<STATUS_DESC>" + Status_Desc + "</STATUS_DESC>\n"
                                        + "<RESPONSE_TIMESTAMP>" + responsetime + "</RESPONSE_TIMESTAMP>\n"
                                        + "<USERSTATE>-1</USERSTATE>\n"
                                        + "<PPTYPE>" + pptype + "</PPTYPE>\n"
                                        + "</Result>\n"
                                        + "</ServiceLookupResponse>\n";
                            }
                        } else {
                            Status = "0x02000010";
                            Status_Desc = "經銷商與門號不符";
                            responseStr = "<ServiceLookupResponse>\n"
                                    + "<Result>\n"
                                    + "<STATUS>" + Status + "</STATUS>\n"
                                    + "<STATUS_DESC>" + Status_Desc + "</STATUS_DESC>\n"
                                    + "<RESPONSE_TIMESTAMP>" + responsetime + "</RESPONSE_TIMESTAMP>\n"
                                    + "</Result>\n"
                                    + "</ServiceLookupResponse>\n";
                        }

                    } else { // mdn is null
                        log.info("MDN is NULL");
                    }

                } catch (Exception ex) {
                    log.info(ex);
                }
            } else {
                Status = "0x02000010";
                Status_Desc = "未授權此功能";
                responseStr = "<ServiceLookupResponse>\n"
                        + "<Result>\n"
                        + "<STATUS>" + Status + "</STATUS>\n"
                        + "<STATUS_DESC>" + Status_Desc + "</STATUS_DESC>\n"
                        + "<RESPONSE_TIMESTAMP>" + responsetime + "</RESPONSE_TIMESTAMP>\n"
                        + "</Result>\n"
                        + "</ServiceLookupResponse>\n";
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
                /* TODO output your page here. You may use following sample code. */
                String encode_responseStr = util.encrypt(deskey, responseStr);
                log.info("POSServiceLookup ResponseStr ===>" + responseStr);
                log.info("POSServiceLookup encode_responseStr==>" + encode_responseStr);
                /* TODO output your page here. You may use following sample code. */
                out.println(encode_responseStr);
//                out.println(responseStr);

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

    private String getWording(int sum, int dday, int sr_price, int sr_dday) {

//        int sid_dday = serid.getDday(); // 延展天數
//        int s_price = serid.getPrice(); // 服務折抵金額
//        // sumx : 610+620 金額
//        // ddday : 合約到期日到今日的天數
//        String wording = getWording(sumx, ddday, s_price, sid_dday);
        String result = "";

        if (dday < sr_dday) {
            result = result + "，合約有效日期不足";
        }
        if (sum < sr_price) {
            result = result + "，餘額抵扣金額不足";
        }
        return result;
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
