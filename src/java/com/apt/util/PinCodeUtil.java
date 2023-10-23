/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.util;

import com.apt.epay.beans.PinCodeReqBean;
import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.nokia.bean.NokiaPincodeResultBean;
import com.apt.epay.nokia.bean.NokiaReqBean;
import com.apt.epay.nokia.main.NokiaMainPincodeUtil;
import com.apt.epay.nokia.util.NokiaECGPinCodeUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.epay.zte.bean.PinCodeResultBean;
import com.apt.epay.zte.util.ZTEPinCodeUtil;
import com.epay.ejb.bean.EPAY_PROMOTIONCODE;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MimeHeaders;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.zkoss.zk.ui.Executions;

/**
 *
 * @author kevinchang
 */
public class PinCodeUtil {

    private static final Logger log = Logger.getLogger("EPAY");

    public String putPincodeOCSlet(String libm, String mdn, String pincode, String tradedate) throws Exception {

        String result;// = "";
        String ocs_systemid = new ShareParm().OCS_SYSTEM_ID;
        String ocs_system_pwd = new ShareParm().OCS_SYSTEM_PWD;
        String sendURL = new ShareParm().PARM_4GOCS_URL;

        MimeHeaders mh = new MimeHeaders();
        StringBuffer pincodeXml = retnPinCodeXML(ocs_systemid, ocs_system_pwd, libm, mdn, pincode, tradedate);

        log.info("PinCodeProce(MDN:" + mdn + " OCS XML Request)==>" + pincodeXml);
        log.info("PARM_4GOCS_URL==>" + sendURL);

        RequestEntity body;
        body = new StringRequestEntity(pincodeXml.toString(), "text/xml", "utf-8");

        result = sendHttpPostMsg(body, sendURL).replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://10.108.17.36:8081/ecgs/dtd/gateway.dtd\">", "");
        log.info("putPincodeOCSlet Result==>" + result);

        return result;
    }

    public static StringBuffer retnPinCodeXML(String system_id, String system_pwd, String libm, String mdn, String pincode, String tradedate) {
        String mdn886 = "886" + mdn.substring(1);
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
        sb.append("<!DOCTYPE GatewayRequest SYSTEM \"http://10.108.20.44:8081/ecgs/dtd/gateway.dtd\">");
        sb.append("<GatewayRequest>");
        sb.append("<RequestHeader");
        sb.append(" version=\"2915\"");
        sb.append(" requesting_system_id=\"" + system_id + "\"");
        sb.append(" requesting_system_pwd=\"" + system_pwd + "\"");
        sb.append(" request_tid=\"" + libm + "\" request_timestamp=\"" + tradedate + "\"");
        sb.append(" additional_info=\"Recharge\">");
        sb.append("</RequestHeader>");
        sb.append("<SubscriberAccountInfo account_code=\"2000\" balance_type=\"primary\">");
        sb.append("<SubscriberID");
        sb.append(" SubscriberIDType=\"00\">" + mdn886 + "</SubscriberID>");
        sb.append("</SubscriberAccountInfo>");
        sb.append("<QueryDataRequest");
        sb.append(" OP=\"A\"");
        sb.append(" Action=\"IMOM\"");
        sb.append(" IMOMCommand=\"ADJ:SCRRECHARGE,MSISDN=" + mdn886 + ",ID=" + pincode + ",TRANS_ID=" + libm + ",R=" + system_id);
        sb.append("\">");
        sb.append("</QueryDataRequest>");
        sb.append("</GatewayRequest>");
        return sb;
    }

    public static StringBuffer retnTempPinCodeXML() {

        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
        sb.append("<!DOCTYPE GatewayResponse SYSTEM \"http://10.0.0.1:8081/ecgs/dtd/gateway.dtd\">");
        sb.append("<GatewayResponse>");
        sb.append("<ResponseHeader status=\"pass\" result_code=\"00\" response_timestamp=\"05/20/2005.12:12:13\" additional_info=\"SUCCESS\">");
        sb.append("</ResponseHeader>");
        sb.append("<RequestHeader version=\"2915\" request_tid=\"4321\" requesting_system_id=\"100\" requesting_system_pwd=\"pwd123\" request_timestamp=\"05/20/2005.12:12:12\" additional_info=\"1234\">");
        sb.append("</RequestHeader>");
        sb.append("<SubscriberAccountInfo account_code=\"2000\" balance_type=\"primary\">");
        sb.append("<SubscriberID SubscriberIDType=\"00\">886906123456</SubscriberID>");
        sb.append("</SubscriberAccountInfo>");
        sb.append("<CreditResponse>");
        sb.append("<Balance type=\"primary\">");
        sb.append("<Amount>100</Amount>");
        sb.append("<Currency>NTD</Currency>");
        sb.append("</Balance>");
        sb.append("</CreditResponse>");
        sb.append("</GatewayResponse>");

        return sb;
    }

    public PinCodeReqBean parsePinCodeXMLString(String xmlRecords) throws Exception {

//        log.debug("PinCode xmlRecords==>" + xmlRecords);
        PinCodeReqBean aPIPinCodeRequestBean = new PinCodeReqBean();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));
            Document doc = db.parse(is);

            NodeList PinCodeRes = doc.getElementsByTagName("GatewayResponse");
            for (int i = 0; i < PinCodeRes.getLength(); i++) {
                Element element = (Element) PinCodeRes.item(i);

                NodeList nodes = element.getElementsByTagName("ResponseHeader");
                Element line = (Element) nodes.item(0);

                log.debug("status--->" + line.getAttribute("status"));
                aPIPinCodeRequestBean.setStatus(line.getAttribute("status"));

                log.debug("result_code----->" + line.getAttribute("result_code"));
                aPIPinCodeRequestBean.setResultcode(line.getAttribute("result_code"));

                log.debug("response_timestamp----->" + line.getAttribute("response_timestamp"));
                aPIPinCodeRequestBean.setResponse_timestamp(line.getAttribute("response_timestamp"));

                nodes = element.getElementsByTagName("RequestHeader");
                log.debug("request_tid------>" + line.getAttribute("request_tid"));
                aPIPinCodeRequestBean.setRequestid(line.getAttribute("request_tid"));

                nodes = element.getElementsByTagName("SubscriberID");
                line = (Element) nodes.item(0);
                log.debug("SubscriberID---->" + getCharacterDataFromElement(line));
                aPIPinCodeRequestBean.setMdn(getCharacterDataFromElement(line));

            }

        } catch (IOException e) {
            log.info(e);
        } catch (ParserConfigurationException e) {
            log.info(e);
        } catch (SAXException e) {
            log.info(e);
        }
        return aPIPinCodeRequestBean;
    }

    private String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }

    public static String sendHttpPostMsg(RequestEntity requestBody, String url) throws Exception {
//        System.out.println(" ** sendHttpPostMsg step 01");
        String rtresult = null;
        PostMethod post = new PostMethod(url);
        post.setRequestEntity(requestBody);
        HttpClient hc = new HttpClient();

//        System.out.println(" ** setConnectionManagerTimeout(10) step 02");
        try {
            hc.getParams().setConnectionManagerTimeout(10);
//        System.out.println(" ** .setSoTimeout(10) step 02");
//            hc.getParams().setSoTimeout(10);

            int result = hc.executeMethod(post);
//            System.out.println(" ** sendHttpPostMsg step 03");
            if (result == HttpStatus.SC_OK) {
                rtresult = post.getResponseBodyAsString();
            } else {
                rtresult = null;
            }
        } catch (HttpException e) {
            log.info(e);
        } catch (IOException e) {
            log.info(e);
        } finally {
            post.releaseConnection();
        }
        return rtresult;
    }

    public boolean sendOrder(String mdn, String pincode, String batchfile) throws Exception {

        EPayBusinessConreoller epaybusinesscontroller;// = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        boolean send_order_result = false;
//        String message = "";
        String promotioncode = "";
        String contractid = "";// = apirequestbean.getContractid();
        String contractstatuscode;// = "";// = apirequestbean.getContract_status_code();
        String producttype;// = "";// = apirequestbean.getProducttype();
        String salesid = "";
        String storeid = "";

        epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
        SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
        SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);

        try {

            SoaProfile soa = new SoaProfile();
            String result = soa.putSoaProxyletByMDN(mdn);

            SOAReqBean apirequestbean;// = new SOAReqBean();
            apirequestbean = soa.parseXMLString(result);

            contractid = apirequestbean.getContractid();
            contractstatuscode = apirequestbean.getContract_status_code();
            producttype = apirequestbean.getProducttype();

            if ((apirequestbean.getPromotioncode() != null)) {
                promotioncode = apirequestbean.getPromotioncode();
            } else {
                log.info("Exception==> The REAL promotioncode is" + promotioncode);
            }

            log.info("PinCodeProce(SOA Result)==>IP:" + Executions.getCurrent().getRemoteAddr()
                            + ",MDN:" + mdn
                            + ",ContractID:" + contractid
                            + ",Contract_status:" + contractstatuscode
                            + ",producttype:" + producttype
                            + ",promotioncode:" + promotioncode);

            EPAY_PROMOTIONCODE epay_promotioncode;// = new EPAY_PROMOTIONCODE();
            epay_promotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);

            if (epay_promotioncode != null) {

                if ((contractstatuscode.equals("9") || contractstatuscode.equals("43"))
                                && (epay_promotioncode.getPlatformtype() == 1
                                || epay_promotioncode.getPlatformtype() == 2
                                || epay_promotioncode.getPlatformtype() == 3)) {

                    log.info("contractstatuscode==>" + contractstatuscode);
                    log.info("epay_promotioncode.getPlatformtype()===>" + epay_promotioncode.getPlatformtype());
                } else {
                }
            } else {
            }
        } catch (Exception ex) {
            log.info(ex);
        }

        String itemCode = ShareParm.PINCODE_ITEMCODE; //PinCode
        String itemName = ShareParm.PINCODE_ITEMNAME; //實體卡片儲值

        String contactCellPhone = mdn;//聯絡手機號碼
//        String pincode = textbox_pincode.getValue(); //PinCode

        String receiptType = "";//無發票資訊(二聯三聯)
        int tradeAmount = 0;// PinCode實體卡片儲值金額為null, 我們不會知道該實體卡片實際金額(Price)
        int tradeQuantity = 1;
        String receiptTitle = "na";//無發票資訊(發票抬頭)
        String vatNo = "na";//無發票資訊(統編)
        String receiptAddress = "na";//無發票資訊(地址)
        String contactName = "na";//無發票資訊(聯絡人)
        String contactPhone = "na";//無發票資訊(聯絡電話)
        String contactEmail = "na";//無發票資訊(電郵)

        String orderTotal = String.valueOf(tradeAmount * tradeQuantity);
        Calendar nowDateTime = Calendar.getInstance();
        String tradeDate = sdf.format(nowDateTime.getTime());
        String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());
        String resultcode = "";

        EPAY_PROMOTIONCODE epaypromotioncode;// = new EPAY_PROMOTIONCODE();
        epaypromotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);
        int platformtype = epaypromotioncode.getPlatformtype();

        // 產生訂單編號 yymmddHHmissSSS
        String libm = sdf15.format(nowDateTime.getTime());
        //記錄和比對是否已有訂單            
        EPAY_TRANSACTION trans = epaybusinesscontroller.getTransaction(libm);

        if (trans == null && !"".equalsIgnoreCase(libm) && !"null".equalsIgnoreCase(libm)) {
            //如果沒有編號,直接insert新的
            trans = new EPAY_TRANSACTION();
            trans.setLibm(libm);
            trans.setItemcode(itemCode);//PinCode
            trans.setItemproductname(itemName);//實體卡片儲值
            trans.setItemquantity(tradeQuantity);//1
            trans.setOrdertotal(Integer.parseInt(orderTotal));//0
            trans.setFee(0);
            trans.setDiscount(0);
            trans.setTradedate(sdf.parse(tradeDate));
            trans.setPaytime(sdf.parse(tradeDate));
            trans.setPaymethod(ShareParm.PAYMETHOD_PINCODE); //付款方式 PinCode=4
            trans.setStatus("N"); //OCS尚未儲值完成
            trans.setPrivatedata(pincode); //PinCode number

            trans.setServiceId(ShareParm.PINCODE_SERVICEID);//
            trans.setCpLibm(libm);
            trans.setCpId(Integer.parseInt(new ShareParm().PARM_EPAY_CPID));//
            trans.setFeeType("0"); //無拆帳需求
            trans.setInvoiceContactMobilePhone(contactCellPhone);

            trans.setContractID(contractid);
            trans.setPossaleid(salesid);
            trans.setPoscode(storeid);
            trans.setPlatformtype(platformtype);

            //20170713
            trans.setApisrcid("7");
            trans.setBATCHFILE(batchfile);

            log.info("Epay PinCodeProce(insert Table)==>MDN:" + mdn + ",Libm:" + libm);

            epaybusinesscontroller.insertTransaction(trans);

            log.info("PinCodeProce(MDN:" + mdn + " SendData to OCS)==>libm:" + libm);
            try {

                //判斷是要到ALU還是ZTE做儲值
                EPAY_PROMOTIONCODE epay_promotioncode;// = new EPAY_PROMOTIONCODE();
                epay_promotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);
                log.info("===>" + epay_promotioncode.getPlatformtype() + "," + epay_promotioncode.getPromotioncode());

                if (epay_promotioncode.getPlatformtype() == 1) { //ALU

                    PinCodeUtil pincodeutil = new PinCodeUtil();
                    String result = pincodeutil.putPincodeOCSlet(libm, contactCellPhone, pincode, tradeDate_Pincode);

                    log.info("PinCodeProce(MDN:" + mdn + " OCS XML Resonse)==>" + result);

                    if (result != null) {
                        PinCodeReqBean apirequestbean;// = new PinCodeReqBean();
                        apirequestbean = pincodeutil.parsePinCodeXMLString(result);

                        try {
                            resultcode = apirequestbean.getResultcode();
                            trans.setStatus(resultcode);
                        } catch (Exception ex) {
                            log.info(ex);
                        }
                        log.info("CMS PinCodeProce(MDN:" + mdn + " OCS ResultCode)==>" + resultcode);
                        boolean brst = epaybusinesscontroller.updateTransaction(trans);
                        log.info("CMS PinCodeProce(MDN:" + mdn + " update Table status is " + brst + ")==>resultcode:" + resultcode);
                    } else {
                        log.info("CMS Admin Exception===>THE PINCODE Result From 4G OCS IS NULL!!!!!");
                    }

                    //Nokia
                } else if (epay_promotioncode.getPlatformtype() == 2) {// ZTE

                    ZTEPinCodeUtil ztePincode = new ZTEPinCodeUtil();
                    String result = ztePincode.putPincodeOCSlet(libm, contactCellPhone, pincode);
                    log.info("ZTE PinCodeProce(MDN:" + mdn + " ZTE OCS XML Resonse)==>" + result);

                    if (result != null) {

                        if (!result.contains("Fault")) {
                            PinCodeResultBean apirequestbean;// = new PinCodeResultBean();
                            apirequestbean = ztePincode.parseZTEPinCodeXMLString(result);

                            String zteresultcode = apirequestbean.getReturncode();
                            log.info("zteresultcode====>" + zteresultcode);

                            if (zteresultcode.equals("0000")) {
                                if (apirequestbean.getChargemoney() != null) {
                                    int amt = Integer.valueOf(apirequestbean.getChargemoney());
                                    resultcode = "00";
                                    trans.setPayamount(amt);
                                    trans.setOrdertotal(amt);
                                    trans.setItemunitprice(amt);
                                } else {

                                }
                            } else {
                                resultcode = zteresultcode;
                            }

                            String error_desc = apirequestbean.getDesc();
                            trans.setErrdesc(error_desc);
                            trans.setStatus(resultcode);

                            boolean brst = epaybusinesscontroller.updateTransaction(trans);
                            log.info("ZTE PinCodeProce(MDN:" + mdn + " ZTE OCS ResultCode)==>" + resultcode);
                            log.info("ZTE PinCodeProce(MDN:" + mdn + " update Table status is " + brst + ")==>resultcode:" + resultcode);
                        } else {
                            log.info("Admin Exception===>THE ZTEPINCODE Result From 4G ZTE OCS IS NULL!!!!!");
                        }
                    } else {
                        log.info("Admin Exception===>THE ZTEPINCODE Result From 4G ZTE OCS IS NULL!!!!!");
                    }

                } else if (epay_promotioncode.getPlatformtype() == 3 ) { //Nokia

                    // KK NOKIA
                    String pomotion_type = promotioncode.substring(0, 3);
                    NokiaMainPincodeUtil mainutil = new NokiaMainPincodeUtil();
                    NokiaPincodeResultBean result = mainutil.AddMainPincode(pomotion_type, libm, mdn, pincode, tradeDate);
                    resultcode = result.getResult_code();
                    String nokia_result_desc = "", nokia_errorcode = "", nokia_status = "";
                    if ("00".equalsIgnoreCase(resultcode)) {
                        nokia_result_desc = "儲值成功";
                        nokia_errorcode = resultcode;
                        nokia_status = "00";
                    } else {
                        nokia_result_desc = "儲值失敗:" + result.getReason();
                        nokia_errorcode = "-1";
                        nokia_status = "N";
                    }
                    trans.setErrdesc(nokia_result_desc);
                    trans.setErrcode(nokia_errorcode);
                    trans.setStatus(nokia_status);
                    log.info("NOKIA PinCodeProce(MDN:" + mdn + " NOKIA OCS ResultCode)==>" + resultcode);
                    boolean brst = epaybusinesscontroller.updateTransaction(trans);
                } else {
                    log.info("Big Error : epay_promotioncode.getPlatformtype()==>" + epay_promotioncode.getPlatformtype());
                }
            } catch (Exception ex) {
                log.info(ex);
            }
        } else {
            //如果有編號,檢查是否已經交易, 如果還未交易可在更新資料。   
        }
        if (resultcode.equals("00")) {
            send_order_result = true;
        }
        return send_order_result;
    }
}
