/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.zte.util;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.deposit.bean.ZTEFailBean;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.epay.ejb.bean.EPAY_SERVICE_INFO;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MimeHeaders;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
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
public class ZTEAdjustAccUtil {

    private static final Logger log = Logger.getLogger("EPAY");
//    private static String cpid = new ShareParm().PARM_EPAY_CPID;
    SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);
    ServletContext ctx;

    public ZTEAdjustAccUtil() {

    }

    public ZTEAdjustAccUtil(ServletContext ctx) {
        this.ctx = ctx;
    }

    public boolean ZTEAdjustBucketInit(String cpid, String sid, String libm, String mdn, String tradedate, int channeltype) {
        log.info("ZTEAdjustBucket(String sid, String libm, String mdn, String tradedate)" + sid + "," + libm + "," + mdn + "," + tradedate);
        boolean successflag = true;

        try {
            EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

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

    public boolean ZTEAdjustBucket(String cpid, String sid, String libm, String mdn, String tradedate, int channeltype) {

        log.info("ZTEAdjustBucket(String sid, String libm, String mdn, String tradedate)" + sid + "," + libm + "," + mdn + "," + tradedate);
        boolean successflag = true;

        try {
            EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", ctx);

            Long serviceid = Long.valueOf(sid);
            Integer epay_cpid = Integer.valueOf(cpid);

            EPAY_SERVICE_INFO serviceinfo = epaybusinesscontroller.getServiceInfoById(serviceid, epay_cpid);
            String priceplancode = serviceinfo.getPriceplancode();

            int result = putZTEOCS4GPricePlanCode(mdn, priceplancode, channeltype);

            log.info("(" + libm + "," + mdn + ")==> putZTEOCS4GPricePlanCode result :" + result);

            EPAY_TRANSACTION trans = epaybusinesscontroller.getTransaction(libm);
            if (result == 200) {
                String Status_Desc = "儲值成功";
                trans.setStatus("00");
                trans.setErrdesc(Status_Desc);
                log.info("(" + libm + "," + mdn + ")==> Status_Desc :" + Status_Desc);
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

    public String PosZTEAdjustBucket(String cpid, String sid, String libm, String mdn, String tradedate, int channeltype) {

        log.info("ZTEAdjustBucket(String sid, String libm, String mdn, String tradedate)" + sid + "," + libm + "," + mdn + "," + tradedate);
        String resultStr = "";

        try {
            EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", ctx);

            Long serviceid = Long.valueOf(sid);
            Integer epay_cpid = Integer.valueOf(cpid);

            EPAY_SERVICE_INFO serviceinfo = epaybusinesscontroller.getServiceInfoById(serviceid, epay_cpid);
            String priceplancode = serviceinfo.getPriceplancode();

            String result = putPosZTEOCS4GPricePlanCode(mdn, priceplancode, channeltype);
            log.info("putZTEOCS4GPricePlanCode result ===>" + result);

            EPAY_TRANSACTION trans = epaybusinesscontroller.getTransaction(libm);
            if ("00".equals(result)) {
                trans.setStatus("00");
                String Status_Desc = "儲值成功";
                trans.setErrdesc(Status_Desc);
                epaybusinesscontroller.updateTransaction(trans);
                resultStr = "00";
            } else {
                resultStr = result;

                //2018/5/7
                ZTEFailBean ztefailbean;// = new ZTEFailBean();
                ztefailbean = ZTEStatusParseXMLString(result);
                log.info("ztefailbean.getFaultstring()==>" + ztefailbean.getFaultstring());
                String descStr = getErrorDes(ztefailbean.getFaultstring());
                log.info("desc str==>" + descStr);
                String Status_Desc = "儲值失敗(" + descStr + ")";
                String status = "-1";
                trans.setStatus(status);
                trans.setErrdesc(Status_Desc);
                epaybusinesscontroller.updateTransaction(trans);
                //resultStr = "-1";
            }

        } catch (Exception ex) {
            log.info(ex);
        }
        return resultStr;
    }

    public String putPosZTEOCS4GPricePlanCode(String mdn, String priceplancode, int channeltype) throws Exception {
        String resultStr = "";
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

        String kk_result = PossendHttpPostMsg(body, sendURL);
        if ("200".equals(kk_result)) {
            resultStr = "00";
        } else {
            resultStr = kk_result;
        }
//        result = retnTempBasicInfoXML().toString();
        log.info("putZTEOCS4GPricePlanCode Result==>" + resultStr);
//        String tmpStr = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
//                + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
//                + "  <soap:Body>\n"
//                + "    <soap:Fault>\n"
//                + "      <faultcode>soap:Server</faultcode>\n"
//                + "      <faultstring>Server was unable to process request. --&gt; ERROR_CODE=[S-PPS-80928]ERROR_MESSAGE=[the total Bal is more than the maxFee of the config BalCha rgeMaxFee.]</faultstring>\n"
//                + "      <detail />\n"
//                + "    </soap:Fault>\n"
//                + "  </soap:Body>\n"
//                + "</soap:Envelope>";
//        log.info(tmpStr);
        return resultStr;
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

    public static StringBuffer retnZTE4GOCSXML(String system_id, String system_pwd, String priceplancode, String mdn, int channeltype) {

        StringBuffer sb = new StringBuffer();

        sb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:zsm=\"http://www.ZTEsoft.com/ZSmart\">");
        sb.append("<soapenv:Header>");
        sb.append("<zsm:AuthHeader>");
        sb.append("<zsm:Username>" + system_id + "</zsm:Username>");
        sb.append("<zsm:Password>" + system_pwd + "</zsm:Password>");
        sb.append("</zsm:AuthHeader>");
        sb.append("</soapenv:Header>");
        sb.append("<soapenv:Body>");
        sb.append("<zsm:AddUserIndiPricePlan>");
        sb.append("<zsm:MSISDN>" + mdn + "</zsm:MSISDN>");
        sb.append("<zsm:PricePlanCode>" + priceplancode + "</zsm:PricePlanCode>");
        sb.append("<zsm:Channel>" + channeltype + "</zsm:Channel>");
        sb.append("</zsm:AddUserIndiPricePlan>");
        sb.append("</soapenv:Body>");
        sb.append("</soapenv:Envelope>");

        return sb;
    }

    public static int sendHttpPostMsg(RequestEntity requestBody, String url) throws Exception {

        int rtresult = 0;

        PostMethod post = new PostMethod(url);
        post.setRequestEntity(requestBody);
        HttpClient hc = new HttpClient();

        post.setRequestHeader("SOAPAction", "http://www.ZTEsoft.com/ZSmart/AddUserIndiPricePlan");

        try {
            hc.getParams().setConnectionManagerTimeout(10);

            rtresult = hc.executeMethod(post);
//            rtresult = hc.executeMethod(post);
            log.info("===>" + post.getResponseBodyAsString());
//            if (result == HttpStatus.SC_OK) {
//                rtresult = post.getResponseBodyAsString();
//            } else {
//                rtresult = post.getResponseBodyAsString();
//            }
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            post.releaseConnection();
        }
        return rtresult;
    }

    public String PossendHttpPostMsg(RequestEntity requestBody, String url) throws Exception {

        String resultStr = "";
        int rtresult = 0;

        PostMethod post = new PostMethod(url);
        post.setRequestEntity(requestBody);
        HttpClient hc = new HttpClient();

        post.setRequestHeader("SOAPAction", "http://www.ZTEsoft.com/ZSmart/AddUserIndiPricePlan");

        try {
            hc.getParams().setConnectionManagerTimeout(10);

            rtresult = hc.executeMethod(post);

            if (rtresult == 200) {
                resultStr = "200";
            } else {
                resultStr = post.getResponseBodyAsString();
            }
//            rtresult = hc.executeMethod(post);
            log.info("===>" + post.getResponseBodyAsString());
//            if (result == HttpStatus.SC_OK) {
//                rtresult = post.getResponseBodyAsString();
//            } else {
//                rtresult = post.getResponseBodyAsString();
//            }
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            post.releaseConnection();
        }
        return resultStr;
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

    public static StringBuffer retnZTE4GOCS_DeductFeeXML(String system_id, String system_pwd, int amount, String mdn, String glcode, String balid) {

        StringBuffer sb = new StringBuffer();

        sb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:zsm=\"http://www.ZTEsoft.com/ZSmart\">");
        sb.append("<soapenv:Header>");
        sb.append("<zsm:AuthHeader>");
        sb.append("<zsm:Username>" + system_id + "</zsm:Username>");
        sb.append("<zsm:Password>" + system_pwd + "</zsm:Password>");
//        sb.append("<zsm:Username></zsm:Username>");
//        sb.append("<zsm:Password></zsm:Password>");
        sb.append("</zsm:AuthHeader>");
        sb.append("</soapenv:Header>");
        sb.append("<soapenv:Body>");
        sb.append("<zsm:DeductFee>");
        sb.append("<zsm:MSISDN>" + mdn + "</zsm:MSISDN>");
        sb.append("<zsm:Amount>" + "-10" + "</zsm:Amount>");
        sb.append("<zsm:Days></zsm:Days>");
        sb.append("<zsm:BalID>" + balid + "</zsm:BalID>");
        sb.append("<zsm:AcctResID></zsm:AcctResID>");
        sb.append("<zsm:AcctItemTypeCode>" + glcode + "</zsm:AcctItemTypeCode>");
        sb.append("</zsm:DeductFee>");
        sb.append("</soapenv:Body>");
        sb.append("</soapenv:Envelope>");

        return sb;
    }

    //ModifyBal
    public static StringBuffer retnZTE4GOCS_ModifyBalXML(String system_id, String system_pwd, int amount, String mdn, String AcctResID) {

        StringBuffer sb = new StringBuffer();
        sb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:zsm=\"http://www.ZTEsoft.com/ZSmart\">");
        sb.append("<soapenv:Header>");
        sb.append("<zsm:AuthHeader>");
        sb.append("<zsm:Username>" + system_id + "</zsm:Username>");
        sb.append("<zsm:Password>" + system_pwd + "</zsm:Password>");
        sb.append("</zsm:AuthHeader>");
        sb.append("</soapenv:Header>");
        sb.append("<soapenv:Body>");
        sb.append("<zsm:ModifyBal>");
        sb.append("<zsm:MSISDN>" + mdn + "</zsm:MSISDN>");
        sb.append("<zsm:Operator>Promotion</zsm:Operator>");
        sb.append("<zsm:Reason>4</zsm:Reason>");
        sb.append("<zsm:Chanel>1</zsm:Chanel>");
        sb.append("<zsm:BalID></zsm:BalID>");
        sb.append("<zsm:AcctResID>" + AcctResID + "</zsm:AcctResID>");
        sb.append("<zsm:state></zsm:state>");
        sb.append("<zsm:AddBalance>" + amount + "</zsm:AddBalance>");
        sb.append("<zsm:CurExpdate></zsm:CurExpdate>");
        sb.append("<zsm:BasicExpdate></zsm:BasicExpdate>");
        sb.append("</zsm:ModifyBal>");
        sb.append("</soapenv:Body>");
        sb.append("</soapenv:Envelope>");
        return sb;
    }

    public int putZTEOCS4GModifyBal(String mdn, int amount, String AcctResID) throws Exception {

        String mdn886 = "886" + mdn.substring(1);
        int result = 0;
        String ocs_systemid = new ShareParm().PARM_4GZTEOCS_SYSTEM_ID;
        String ocs_system_pwd = new ShareParm().PARM_4GZTEOCS_SYSTEM_PWD;
        String sendURL = new ShareParm().PARM_DeductFee;

        MimeHeaders mh = new MimeHeaders();
        StringBuffer BasicInfoXml = retnZTE4GOCS_ModifyBalXML(ocs_systemid, ocs_system_pwd, amount, mdn886, AcctResID);
        log.info("sendURL==>" + sendURL);
        log.info("4G OCSXml:" + BasicInfoXml);

        RequestEntity body;
        body = new StringRequestEntity(BasicInfoXml.toString(), "text/xml", "utf-8");

        result = sendModifyHttpPostMsg(body, sendURL);

        log.info("putZTEOCS4GModifyBal Result==>" + result);

        return result;
    }

    public static int sendModifyHttpPostMsg(RequestEntity requestBody, String url) throws Exception {

        int rtresult = 0;

        PostMethod post = new PostMethod(url);
        post.setRequestEntity(requestBody);
        HttpClient hc = new HttpClient();

        post.setRequestHeader("SOAPAction", "http://www.ZTEsoft.com/ZSmart/ModifyBal");

        try {
            hc.getParams().setConnectionManagerTimeout(10);

            rtresult = hc.executeMethod(post);
//            rtresult = hc.executeMethod(post);
            log.info("===>" + post.getResponseBodyAsString());
//            if (result == HttpStatus.SC_OK) {
//                rtresult = post.getResponseBodyAsString();
//            } else {
//                rtresult = post.getResponseBodyAsString();
//            }
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            post.releaseConnection();
        }
        return rtresult;
    }
}
