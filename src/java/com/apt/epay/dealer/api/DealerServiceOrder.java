/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.dealer.api;

import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.dealer.util.DealerUtil;
import com.apt.epay.deposit.bean.ServiceOrderReqBean;
import com.apt.epay.deposit.bean.ZTEFailBean;
import com.apt.epay.deposit.util.PosUtil;
import com.apt.epay.deposit.util.toolUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.epay.zte.util.ZTEAdjustAccUtil;
import static com.apt.epay.zte.util.ZTEAdjustAccUtil.retnZTE4GOCSXML;
import static com.apt.epay.zte.util.ZTEAdjustAccUtil.sendHttpPostMsg;
import com.apt.util.PPMdnUtil;
import com.apt.util.SendSMS;
import com.apt.util.SoaProfile;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_PROMOTIONCODE;
import com.epay.ejb.bean.EPAY_SERVICE_INFO;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MimeHeaders;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.zkoss.zk.ui.Executions;

/**
 *
 * @author kevinchang
 */
public class DealerServiceOrder extends HttpServlet {

    private final Logger log;

    public DealerServiceOrder() {
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
        log.info("DealerServiceOrder.processRequest");
        log.info("Client IP :" + request.getRemoteAddr());

        String cpid = request.getParameter("CPID");
        log.info("CPID===>" + cpid);
        toolUtil tool = new toolUtil();
        Utilities util = new Utilities();
        String deskey = "";
        String responseStr = "";
        boolean cpkeyflag = false;

        PPMdnUtil mdntool = new PPMdnUtil();
        DealerUtil dealerutil = new DealerUtil();
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
                log.info("DealerServiceOrder INPUT==>" + str_input);

                String libm = "";
                String basicinfo = "";
                String transtatus = "";

                try {

                    ServiceOrderReqBean aPIServiceOrdrReqBean;
                    aPIServiceOrdrReqBean = dealerutil.OrderServParseXMLString(str_input, "ServiceOrderRequest");
                    String mdn = aPIServiceOrdrReqBean.getMdn();
                    String serviceid = aPIServiceOrdrReqBean.getServiceID();
                    String apisrcid = aPIServiceOrdrReqBean.getApisrcid();
                    String paytool = aPIServiceOrdrReqBean.getPaytool();
                    String payname = aPIServiceOrdrReqBean.getPayname();
                    String cplibm = aPIServiceOrdrReqBean.getCplibm();

                    PPMdnUtil kk_ppmdn = new PPMdnUtil(mdn, false, true);

                    boolean apisrcflag = false;
                    boolean paytoolflag = false;

                    if (!"".equals(apisrcid)) {
                        apisrcflag = kk_ppmdn.checkAPISrcID(apisrcid);
                        if (!apisrcflag) {
                            Status = "0x03000009";
                            Status_Desc = "APISRCID參數不正確";
                        }
                    } else {
                        Status = "0x03000009";
                        Status_Desc = "APISRCID參數不正確(不得為空白)";
                    }

                    if (!"".equals(paytool)) {
                        paytoolflag = kk_ppmdn.checkPaytoolID(paytool);
                        if (!paytoolflag) {
                            Status = "0x03000010";
                            Status_Desc = "PAYTOOL參數不正確";
                        }
                    } else {
                        Status = "0x03000010";
                        Status_Desc = "PAYTOOL參數不正確(不得為空白)";
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
                    SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);
                    SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");

                    if (paytoolflag && apisrcflag) {
                        log.info("MDN===>" + mdn);
                        if (!"".equals(mdn)) {
                            SoaProfile soa = new SoaProfile();
                            String result = soa.putSoaProxyletByMDN(mdn);
                            SOAReqBean apirequestbean = new SOAReqBean();
                            apirequestbean = soa.parseXMLString(result);

                            log.info("kkflag==>CMSAdjustAccountDeposit:" + result);

                            String contractid = apirequestbean.getContractid();
                            String contractstatuscode = apirequestbean.getContract_status_code();
                            String producttype = apirequestbean.getProducttype();
                            String promotioncode = apirequestbean.getPromotioncode();

                            EPAY_SERVICE_INFO serviceinfo = epaybusinesscontroller.getServiceInfoById(Long.valueOf(serviceid), Integer.valueOf(cpid));
                            String itemName = serviceinfo.getServiceName();
                            String itemUnitPrice = serviceinfo.getPrice().toString();
                            String itemCode = serviceinfo.getGlcode();
                            String note = serviceinfo.getNote();
                            int hr = serviceinfo.getDday() * 24;
                            log.info("ServiceInfo===>" + itemName + "," + itemUnitPrice + "," + itemCode + "," + note);

                            String contactCellPhone = mdn;//聯絡手機號碼
                            int tradeAmount = Integer.valueOf(itemUnitPrice);
                            int tradeQuantity = 1;

                            String orderTotal = String.valueOf(tradeAmount * tradeQuantity);
                            Calendar nowDateTime = Calendar.getInstance();
                            String tradeDate = sdf.format(nowDateTime.getTime());
                            String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());

                            libm = sdf15.format(nowDateTime.getTime());
                            EPAY_TRANSACTION trans = epaybusinesscontroller.getTransaction(libm);

                            EPAY_PROMOTIONCODE epaypromotioncode = new EPAY_PROMOTIONCODE();
                            log.info("promotioncode===>" + promotioncode);
                            epaypromotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);
                            int platformtype = epaypromotioncode.getPlatformtype();

                            if (trans == null && !"".equalsIgnoreCase(libm) && !"null".equalsIgnoreCase(libm)) {
                                //如果沒有編號,直接insert新的
                                trans = new EPAY_TRANSACTION();
                                trans.setLibm(libm);
                                trans.setItemcode(itemCode);
                                trans.setItemproductname(itemName);
                                trans.setItemunitprice(Integer.parseInt(itemUnitPrice));//0
                                trans.setItemquantity(tradeQuantity);//1
                                trans.setOrdertotal(Integer.parseInt(orderTotal));//0
                                trans.setFee(0);
                                trans.setDiscount(0);
                                trans.setTradedate(sdf.parse(tradeDate));
                                trans.setPaymethod(ShareParm.PAYMETHOD_ADJUSTACC); //付款方式 信用卡:value = 1 
                                trans.setStatus("N"); //OCS尚未儲值完成
//                trans.setPaystatus(0); //無須繳費
                                trans.setPayamount(Integer.parseInt(orderTotal));
                                trans.setPrivatedata(libm); //PinCode number

                                trans.setServiceId(serviceid);//
                                trans.setCpLibm(libm);
                                trans.setCpId(icpid);//

//                                cpid = new ShareParm().PARM_EPAY_CPID;
//                                EPAY_CP_INFO cpinfo = epaybusinesscontroller.getCpInfo(Integer.valueOf(cpid));
                                String cpname = cpinfo.getCpName();
                                log.info("cpname==>" + cpname);
                                trans.setCpName(cpname);

                                trans.setFeeType("0"); //無拆帳需求
                                trans.setInvoiceContactMobilePhone(contactCellPhone);
                                trans.setContractID(contractid);
                                trans.setPlatformtype(platformtype);

                                //20170713
                                trans.setApisrcid("1");
                                trans.setPaytool("3");

                                log.info("AdjustAccProce(insert EPAY_TRANSACTION Table)==>MDN:" + mdn + ",Libm:" + libm);

                                epaybusinesscontroller.insertTransaction(trans);

                                boolean flag = false;

                                if (platformtype == 2) {
                                    String tradedate = sdf_pincode.format(trans.getTradedate());
                                    String sid = trans.getServiceId();
                                    int channeltype = 2; //1.网络储值  2.余额抵扣
                                    ZTEAdjustAccUtil zteadjust = new ZTEAdjustAccUtil();
                                    flag = ZTEAdjustBucketInit(cpid, sid, libm, mdn, tradedate, channeltype);

                                    if (flag) {
                                        Status = "0x00000000";
                                        Status_Desc = "餘額抵扣完成";
                                    }
                                }

                            } else {
                                //如果有編號,檢查是否已經交易, 如果還未交易可在更新資料。   
                            }

                        } else { // mdn is null
                            Status = "0x01000001";
                            Status_Desc = "認證失敗(用戶門號不存在)";
                        }
                    }
                    responseStr = "<ServiceOrderResponse><Result><STATUS>" + Status + "</STATUS><STATUS_DESC>" + Status_Desc + "</STATUS_DESC>"
                            + "<CPLIBM>" + cplibm + "</CPLIBM>\n"
                            + "<LIBM>" + libm + "</LIBM>"
                            + "<RESPONSE_TIMESTAMP>" + responsetime + "</RESPONSE_TIMESTAMP>"
                            + basicinfo
                            + "</Result></ServiceOrderResponse>";
                } catch (Exception ex) {
                    log.info(ex);
                }
            } else {
                Status = "0x02000010";
                Status_Desc = "未授權此功能";
                responseStr = "<ServiceOrderResponse>\n"
                        + "<Result>\n"
                        + "<STATUS>" + Status + "</STATUS>\n"
                        + "<STATUS_DESC>" + Status_Desc + "</STATUS_DESC>\n"
                        + "<RESPONSE_TIMESTAMP>" + responsetime + "</RESPONSE_TIMESTAMP>\n"
                        + "</Result>\n"
                        + "</ServiceOrderResponse>\n";
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
                String encode_responseStr = util.encrypt(deskey, responseStr);
                log.info("DealerServiceOrder ResponseStr ===>" + responseStr);
                log.info("DealerServiceOrder encode_responseStr==>" + encode_responseStr);
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

    public boolean ZTEAdjustBucketInit(String cpid, String sid, String libm, String mdn, String tradedate, int channeltype) {
        log.info("ZTEAdjustBucket(String sid, String libm, String mdn, String tradedate)" + sid + "," + libm + "," + mdn + "," + tradedate);
        boolean successflag = true;

        try {
            EPayBusinessConreoller epaybusinesscontroller;
            epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", this.getServletContext());
            
            Long serviceid = Long.valueOf(sid);
            Integer epay_cpid = Integer.valueOf(cpid);

//            EPAY_SERVICE_INFO serviceinfo = epaybusinesscontroller.getServiceInfoById(serviceid, epay_cpid);
            int icpid = Integer.valueOf(cpid);
            EPAY_SERVICE_INFO serviceinfo = epaybusinesscontroller.getServiceInfoById(serviceid, icpid);
            String priceplancode = serviceinfo.getPriceplancode();
            log.info("priceplancode===>" + priceplancode);

            int result = putZTEOCS4GPricePlanCode(mdn, priceplancode, channeltype);
            log.info("putZTEOCS4GPricePlanCode result ===>" + result);

            EPAY_TRANSACTION trans = epaybusinesscontroller.getTransaction(libm);
            if (result == 200) {
                trans.setStatus("00");
                epaybusinesscontroller.updateTransaction(trans);
            } else {
                successflag = false;
                String status = String.valueOf(result);
                trans.setStatus(status);
                epaybusinesscontroller.updateTransaction(trans);
            }

        } catch (Exception ex) {
            log.info(ex);
        }
        return successflag;
    }

    public int putZTEOCS4GPricePlanCode(String mdn, String priceplancode, int channeltype) throws Exception {

        String mdn886 = "886" + mdn.substring(1);
        int result = 0;
        String ocs_systemid = new ShareParm().PARM_4GZTEOCS_SYSTEM_ID;
        String ocs_system_pwd = new ShareParm().PARM_4GZTEOCS_SYSTEM_PWD;
        String sendURL = new ShareParm().PARM_AddUserIndiPricePlan;

        MimeHeaders mh = new MimeHeaders();
        StringBuffer BasicInfoXml = retnZTE4GOCSXML(ocs_systemid, ocs_system_pwd, priceplancode, mdn886, channeltype);
        log.info("sendURL==>" + sendURL);
        log.info("4G OCSXml:" + BasicInfoXml);

        RequestEntity body;
        body = new StringRequestEntity(BasicInfoXml.toString(), "text/xml", "utf-8");

        result = sendHttpPostMsg(body, sendURL);
//        result = retnTempBasicInfoXML().toString();
        log.info("putZTEOCS4GPricePlanCode Result==>" + result);

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
