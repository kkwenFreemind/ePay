/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.deposit.api;

import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.deposit.bean.ServiceOrderReqBean;
import com.apt.epay.deposit.bean.ServiceOrderStatusReqBean;
import com.apt.epay.deposit.bean.ZTEFailBean;
import com.apt.epay.deposit.util.PosUtil;
import com.apt.epay.deposit.util.toolUtil;
import com.apt.epay.nokia.bean.NokiaPricePlanResultBean;
import com.apt.epay.nokia.main.NokiaMainPricePlanCodeUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.epay.soa.util.SOAUtil;
import com.apt.epay.zte.util.ZTEAdjustAccUtil;
import com.apt.util.PPMdnUtil;
import com.apt.util.SendSMS;
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
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author kevinchang
 */
public class POSServiceOrder extends HttpServlet {

    private final Logger log;

    public POSServiceOrder() {
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

        log.info("PosUserStateLookup.processRequest");
        log.info("Client IP :" + request.getRemoteAddr());

        String cpid = request.getParameter("CPID");
        log.info("CPID===>" + cpid);
        toolUtil tool = new toolUtil();
        Utilities util = new Utilities();
        String deskey = "";
        String responseStr = "";
        boolean cpkeyflag = false;

        String mdn = "";

        PPMdnUtil mdntool = new PPMdnUtil();
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
                log.info("POSServiceOrder INPUT==>" + str_input);

                String libm = "";
                String basicinfo = "";
                String transtatus = "";

                try {

                    ServiceOrderReqBean aPIServiceOrdrReqBean;
                    aPIServiceOrdrReqBean = mdntool.OrderServParseXMLString(str_input, "ServiceOrderRequest");
                    mdn = aPIServiceOrdrReqBean.getMdn();
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

                    if (paytoolflag && apisrcflag) {
                        log.info("MDN===>" + mdn);
                        if (!"".equals(mdn)) {

                            PPMdnUtil ppmdn = new PPMdnUtil(mdn, false, true);
                            String checkresult = ppmdn.checkPPMDN(epaybusinesscontroller);
                            log.info("ppmdn.checkPPMDN(" + mdn + ", epaybusinesscontroller)==>" + checkresult);

                            if ("1".equals(checkresult)) {
                                int channeltype = 1; //1.网络储值  2.余额抵扣
                                Calendar nowDateTime = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
                                SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                                libm = sdf15.format(nowDateTime.getTime());
                                String tradeDate = sdf.format(nowDateTime.getTime());

                                PosUtil posutil = new PosUtil();
                                boolean insertresult = posutil.insertPosTransaction(ppmdn, aPIServiceOrdrReqBean, libm, cpid, epaybusinesscontroller);

                                int adjust_value;
                                if (insertresult) {

                                    SOAUtil soautil = new SOAUtil();
                                    SOAReqBean soabean = soautil.getSOAInfo(mdn);
                                    String promotioncode = soabean.getPromotioncode();

                                    EPAY_PROMOTIONCODE epaypromotioncode;// = new EPAY_PROMOTIONCODE();
                                    epaypromotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);

                                    NokiaPricePlanResultBean nbean = new NokiaPricePlanResultBean();

                                    if (epaypromotioncode != null) {

                                        int platformtype = epaypromotioncode.getPlatformtype();
                                        log.info("MDN:" + mdn + " , PlatformType====>" + platformtype);

                                        String resultflag = "";
                                        if (platformtype == 2) {

                                            log.info(mdn + " ZTE platformtype==>" + platformtype);
                                            ZTEAdjustAccUtil zteadjust = new ZTEAdjustAccUtil(this.getServletContext());
                                            resultflag = zteadjust.PosZTEAdjustBucket(cpid, serviceid, libm, mdn, tradeDate, channeltype);
                                            log.info(mdn + " ZTE adjust.ZTEAdjustBucket(" + serviceid + "," + libm + "," + mdn + "," + tradeDate + "," + channeltype + ") Result ==>" + resultflag);

                                        } else if (platformtype == 3) {

                                            //KK NOKIA
                                            log.info(mdn + " Nokai platformtype==>" + platformtype);
                                            int Rchg_Type = 1; //1.网络储值  2.余额抵扣

                                            NokiaMainPricePlanCodeUtil mutil = new NokiaMainPricePlanCodeUtil();
                                            String promotion_type3 = epaypromotioncode.getPromotioncode().substring(0, 3);
                                            log.info(mdn + " promotioncode ==>" + epaypromotioncode.getPromotioncode() + "," + promotion_type3);

                                            //get priceplancode
                                            Integer epay_cpid = Integer.valueOf(cpid);
                                            EPAY_TRANSACTION kk_trans = epaybusinesscontroller.getTransaction(libm);
                                            String nokia_sid = kk_trans.getServiceId();
                                            Long nokia_serviceid = Long.valueOf(nokia_sid);

                                            EPAY_SERVICE_INFO serviceinfo = epaybusinesscontroller.getServiceInfoById(nokia_serviceid, epay_cpid);
                                            String priceplancode = serviceinfo.getPriceplancode();
                                            log.info(mdn + " Nokia serviceid,priceplancode  ==>" + nokia_serviceid + "," + priceplancode);

                                            nbean = mutil.AddMainPricePlanCode(promotion_type3, libm, mdn, priceplancode, tradeDate, Rchg_Type);

                                            resultflag = nbean.getResult_code();
                                            log.info(mdn + " Nokia Price Plan Code ==>Result " + resultflag);

                                        }

                                        if ("00".equals(resultflag)) {
                                            Status = "0x00000000";
                                            adjust_value = 1; //已繳款
                                            transtatus = "00";
                                            Status_Desc = "儲值成功";

                                            if (platformtype == 2) {
                                                basicinfo = ppmdn.getBasicOCSInfo(epaybusinesscontroller);

                                            } else if (platformtype == 3 || platformtype == 4) {

                                                //KK NOKIA
                                                PosUtil nokia_posutil = new PosUtil();
                                                basicinfo = nokia_posutil.nokiaMdnBuckInfo(mdn);
                                            }

                                        } else {
                                            log.info("kk========>" + resultflag);
                                            String descStr = "";
                                            if (platformtype == 2) {

                                                ZTEFailBean ztefailbean;// = new ZTEFailBean();
                                                ztefailbean = ZTEStatusParseXMLString(resultflag);
                                                log.info("ztefailbean.getFaultstring()==>" + ztefailbean.getFaultstring());
                                                descStr = getErrorDes(ztefailbean.getFaultstring());

                                            } else if (platformtype == 3 || platformtype == 4) {

                                                //KK NOKIA
                                                descStr = nbean.getReason();

                                            }
                                            log.info("desc str==>" + descStr);
                                            Status = "0x03000007";
                                            adjust_value = 0;
                                            transtatus = "-1";
                                            Status_Desc = "儲值失敗(" + descStr + ")";
                                        }

                                        log.info(mdn + " ==>" + adjust_value + "," + transtatus + "," + Status_Desc);
                                        log.info(mdn + " libm=>" + libm + " update Result ==>" + transtatus);
                                        EPAY_TRANSACTION trans;//= new EPAY_TRANSACTION();
                                        trans = epaybusinesscontroller.getTransaction(libm);
                                        trans.setPaystatus(adjust_value);
                                        trans.setStatus(transtatus);
                                        trans.setErrdesc(Status_Desc);
                                        trans.setErrcode(transtatus);
                                        epaybusinesscontroller.updateTransaction(trans);

                                        if ("00".equalsIgnoreCase(transtatus)) {
                                            Long lserviecid = Long.valueOf(serviceid);
                                            EPAY_SERVICE_INFO serviceinfo = epaybusinesscontroller.getServiceInfoById(lserviecid, icpid);
                                            if (serviceinfo != null) {
                                                SendSMS xsms = new SendSMS();
                                                String xmsg = "親愛的用戶您好，您申購的【" + serviceinfo.getServiceName() + "】服務已生效";
                                                try {
                                                    log.info(mdn + " xsms.sendsms(mdn, xmsg)");
                                                    String PROXY_FLAG = new ShareParm().PARM_SCT_PROXY_FLAG;
                                                    if ("PROD".equalsIgnoreCase(PROXY_FLAG)) {
                                                        xsms.sendsms(mdn, xmsg);
                                                    }
                                                } catch (Exception ex) {
                                                    log.info(ex);
                                                }
                                            } else {
                                                log.info("ServiceInfo is Null");
                                            }
                                        }
                                    } else {
                                        log.info("posutil.insertPosTransaction(ppmdn, aPIServiceOrdrReqBean," + libm + "," + cpid + ", epaybusinesscontroller)==>" + insertresult);
                                        Status = "0x01000001";
                                        Status_Desc = "促案代碼不存在";
                                    }
                                } else {
                                    log.info("posutil.insertPosTransaction(ppmdn, aPIServiceOrdrReqBean," + libm + "," + cpid + ", epaybusinesscontroller)==>" + insertresult);
                                    Status = "0x01000001";
                                    Status_Desc = "新增交易紀錄失敗";
                                }

                            } else if ("-1".equals(checkresult)) {
                                Status = "0x01000001";
                                Status_Desc = "認證失敗(用戶門號不存在)";
                            } else if ("0".equals(checkresult)) {
                                Status = "0x02000004";
                                Status_Desc = "用戶門號不允許存取)";
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
                log.info(mdn + " POSServiceOrder ResponseStr ===>" + responseStr.replaceAll("[ \t\r\n]+", " ").trim());
                log.info(mdn + " POSServiceOrder encode_responseStr==>" + encode_responseStr);
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

    private ZTEFailBean ZTEStatusParseXMLString(String xmlRecords) {
        ZTEFailBean aPIReqBean = new ZTEFailBean();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));

            Document doc = db.parse(is);
            NodeList SOARes = doc.getElementsByTagName("soap:Fault");

            for (int i = 0; i < SOARes.getLength(); i++) {

                Element element = (Element) SOARes.item(i);

                NodeList nodes = element.getElementsByTagName("faultcode");
                Element line = (Element) nodes.item(0);
                aPIReqBean.setFaultcode(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("faultstring");
                line = (Element) nodes.item(0);
                aPIReqBean.setFaultstring(getCharacterDataFromElement(line));

            }

        } catch (IOException e) {
            log.info(e);
        } catch (ParserConfigurationException e) {
            log.info(e);
        } catch (SAXException e) {
            log.info(e);
        }
        return aPIReqBean;
    }

    private String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }

    private String getErrorDes(String str) {
        String result = "";
        String[] tokens = str.split("=");
        for (String token : tokens) {
            //System.out.println(token);
            int kkindex = token.indexOf("]");
//            System.out.println(kkindex);
            if (kkindex > 0) {
                System.out.println(token.substring(1, kkindex));
                if (!"".equals(result)) {
                    result = result + "," + token.substring(1, kkindex);
                } else {
                    result = token.substring(1, kkindex);
                }
            }
//            System.out.print(token.substring(1, kkindex-1));
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
