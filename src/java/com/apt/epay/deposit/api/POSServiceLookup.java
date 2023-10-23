/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.deposit.api;

//import com.apt.epay.beans.BasicInfoReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.deposit.util.PosUtil;
import com.apt.epay.deposit.util.toolUtil;
import com.apt.epay.nokia.bean.NokiaSubscribeAgentInfoBean;
import com.apt.epay.nokia.main.NokiaMainBasicInfoUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.util.PPMdnUtil;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_CP_INFO;
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
public class POSServiceLookup extends HttpServlet {

    private final Logger log;

    public POSServiceLookup() {
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

        log.info("PosServiceLookup.processRequest");
        log.info("Client IP :" + request.getRemoteAddr());

        String PROXY_FLAG = new ShareParm().PARM_SCT_PROXY_FLAG;

        String cpid = request.getParameter("CPID");
        log.info("CPID===>" + cpid);
        toolUtil tool = new toolUtil();
        Utilities util = new Utilities();
        String deskey = "";
        String responseStr = "";
        boolean cpkeyflag = false;
        String NokiaAgentId = "";
        NokiaSubscribeAgentInfoBean AgentBean = new NokiaSubscribeAgentInfoBean();

        String mdn = "";

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
                log.info("POSServiceLookup INPUT==>" + str_input);

                try {
                    mdn = mdntool.getMDNByparseXMLString(str_input, "ServiceLookupRequest");
                    log.info("MDN===>" + mdn);

                    if (!"".equals(mdn)) {

                        PPMdnUtil ppmdn = new PPMdnUtil(mdn, true, true); //PPMdnUtil(String mdn, boolean aluflag, boolean zteflag)
                        String checkresult = ppmdn.checkPPMDN(epaybusinesscontroller);

                        int pptype = 0;
                        pptype = ppmdn.getPlatformtype();
                        log.info(mdn + " ppmdn.getPlatformtype()===>" + pptype);

                        Status = "0x00000000";
                        log.info(mdn + " ppmdn.checkPPMDN(" + mdn + ", epaybusinesscontroller)==>" + checkresult);

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

                            List PosServiceinfo = null;
                            log.info(mdn + " list serviceinfo==>" + cpid + "," + ppmdn.getPromotioncode() + "," + ppmdn.getPlatformtype());

                            if (ppmdn.getPlatformtype() == 3) {

                                //Nokia ServiceID
                                //NokiaAgentId = "100005";
                                //不分PlatformType，只要promotion code符合就撈取
                                //後面再來依照NokiaAgentId判斷，Nokia門號是4G/5G
                                //如果是4G，則把5G的資費剃除
                                //如果是5G，則都保留，不用剔除    
                                NokiaMainBasicInfoUtil mutil = new NokiaMainBasicInfoUtil();
                                SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                                Calendar nowDateTime = Calendar.getInstance();
                                String libm = sdf15.format(nowDateTime.getTime());

                                AgentBean = mutil.GetMDNAgentInfo(libm, mdn);
                                NokiaAgentId = AgentBean.getAgentID();
                                log.info(mdn + " Nokia AgentId ==>" + NokiaAgentId);

                                //debug
//                                if ("TDE".equalsIgnoreCase(PROXY_FLAG)) {
//                                    log.info("This is ===============>" + PROXY_FLAG);
//                                    if ("0905001002".equalsIgnoreCase(mdn)) {
//                                        NokiaAgentId = "100005";
//                                        log.info(mdn + " Nokia AgentId change to 100005");
//                                    }
//                                }

                                PosServiceinfo = epaybusinesscontroller.listNokialServiceInfo(Integer.valueOf(cpid), ppmdn.getPromotioncode(), ppmdn.getPlatformtype());
                            } else {

                                //case 1 : platform type == 1 ,ALU 4G
                                //case 2 : platform type == 2 ,ZTE 4G
                                //依照platform Type以及Promotion code來判斷其門號可以選擇的資費
                                PosServiceinfo = epaybusinesscontroller.listAllServiceInfo(Integer.valueOf(cpid), ppmdn.getPromotioncode(), ppmdn.getPlatformtype());
                            }

                            log.info(mdn + "==>Platform Type =>" + ppmdn.getPlatformtype());
                            log.info(mdn + "==>Nokia AgentID==>" + NokiaAgentId);
                            log.info(mdn + "==>get serviceinfo1 (" + Integer.valueOf(cpid) + "," + ppmdn.getPromotioncode() + "," + ppmdn.getPlatformtype());

                            Iterator itserviceinfo = PosServiceinfo.iterator();
                            int PosServiceinfosize = PosServiceinfo.size();
                            log.info(mdn + " Serviceinfo.size()===>" + PosServiceinfosize);
                            if (PosServiceinfo.size() > 0) {

                                while (itserviceinfo.hasNext()) {

                                    EPAY_SERVICE_INFO serviceinfo = (EPAY_SERVICE_INFO) itserviceinfo.next();

                                    if (!"100005".equalsIgnoreCase(NokiaAgentId)) {

                                        //Nokia 門號
                                        //不分PlatformType，只要promotion code符合就撈取
                                        //後面再來依照NokiaAgentId判斷，Nokia門號是4G/5G
                                        //如果是4G，則把5G的資費剃除
                                        //如果是5G，則都保留，不用剔除  
                                        //ZTE & ALU
                                        //依照platform Type以及Promotion code來判斷其門號可以選擇的資費        
                                        if ((serviceinfo.getPlatformtype() == 2) || (serviceinfo.getPlatformtype() == 3 && serviceinfo.getGtype() == 4)) {
                                            String serviceId = String.valueOf(serviceinfo.getServiceId());
                                            String serviceName = serviceinfo.getServiceName();
                                            String price = String.valueOf(serviceinfo.getPrice());
                                            String note = serviceinfo.getNote();
                                            String servicematcode = serviceinfo.getServicematcode();
                                            log.info(mdn + " (" + PosServiceinfosize + ")==>" + serviceId + "," + serviceName + "," + price + "," + note);
                                            responseStr = responseStr + "<ServiceList>\n<SERVICEID>" + serviceId + "</SERVICEID><SERVICENAME>" + serviceName + "</SERVICENAME><SERVICEMATCODE>" + servicematcode + "</SERVICEMATCODE><PRICE>" + price + "</PRICE><SERVICEDESC>" + note + "</SERVICEDESC>\n</ServiceList>\n";
                                            PosServiceinfosize--;
                                        } else {
                                            log.info(mdn + " PlatformType=" + serviceinfo.getPlatformtype() + ",GType=" + serviceinfo.getGtype() + " Filter 5G Nokia Item--->>" + serviceinfo.getServiceName());
                                            //log.info(mdn + " Filter 5G Nokia Item--->" + serid.getServiceName() + ",serid.getPlatformtype()==>" + serid.getPlatformtype());
                                        }
                                    } else {
                                        //Nokia 4G+5G , ZTE 4G 促案
                                        String serviceId = String.valueOf(serviceinfo.getServiceId());
                                        String serviceName = serviceinfo.getServiceName();
                                        String price = String.valueOf(serviceinfo.getPrice());
                                        String note = serviceinfo.getNote();
                                        String servicematcode = serviceinfo.getServicematcode();
                                        log.info(mdn + " (" + PosServiceinfosize + ")==>" + serviceId + "," + serviceName + "," + price + "," + note);
                                        responseStr = responseStr + "<ServiceList>\n<SERVICEID>" + serviceId + "</SERVICEID><SERVICENAME>" + serviceName + "</SERVICENAME><SERVICEMATCODE>" + servicematcode + "</SERVICEMATCODE><PRICE>" + price + "</PRICE><SERVICEDESC>" + note + "</SERVICEDESC>\n</ServiceList>\n";
                                        PosServiceinfosize--;

                                    }

//                                    String serviceId = String.valueOf(serviceinfo.getServiceId());
//                                    String serviceName = serviceinfo.getServiceName();
//                                    String price = String.valueOf(serviceinfo.getPrice());
//                                    String note = serviceinfo.getNote();
//                                    String servicematcode = serviceinfo.getServicematcode();
//                                    log.info(mdn + " (" + PosServiceinfosize + ")==>" + serviceId + "," + serviceName + "," + price + "," + note);
//                                    responseStr = responseStr + "<ServiceList>\n<SERVICEID>" + serviceId + "</SERVICEID><SERVICENAME>" + serviceName + "</SERVICENAME><SERVICEMATCODE>" + servicematcode + "</SERVICEMATCODE><PRICE>" + price + "</PRICE><SERVICEDESC>" + note + "</SERVICEDESC>\n</ServiceList>\n";
//                                    PosServiceinfosize--;
                                }
                            }

                            String basicinfo = "";
                            if (pptype == 2) {
                                basicinfo = ppmdn.getBasicOCSInfo(epaybusinesscontroller);
                            } else if (pptype == 3) {

                                //KK NOKIA
                                log.info(mdn + " Run ==> nokia_posutil.nokiaMdnBuckInfo(mdn) ");
                                PosUtil nokia_posutil = new PosUtil();
                                basicinfo = nokia_posutil.nokiaMdnBuckInfo(mdn);
                            }
                            responseStr = responseStr + basicinfo + "</ServiceLookupResponse>";
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
                log.info(mdn + " POSServiceLookup ResponseStr ===>" + responseStr.replaceAll("[ \t\r\n]+", " ").trim());
                log.info(mdn + " POSServiceLookup encode_responseStr==>" + encode_responseStr);
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
