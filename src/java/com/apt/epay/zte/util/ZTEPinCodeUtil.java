/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.zte.util;

import com.apt.epay.share.ShareParm;
import com.apt.epay.zte.bean.PinCodeResultBean;
import com.apt.epay.zte.bean.VoucherInfoResultBean;
import java.io.IOException;
import java.io.StringReader;
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
public class ZTEPinCodeUtil {

    private static final Logger log = Logger.getLogger("EPAY");

    //執行傳送XML到ZTE
    public String putPincodeOCSlet(String libm, String mdn, String pincode) throws Exception {
        String result = "";
        String ocs_systemid = new ShareParm().OCS_SYSTEM_ID;
        String ocs_system_pwd = new ShareParm().OCS_SYSTEM_PWD;
        String sendURL = new ShareParm().PARM_ReCharge;

        MimeHeaders mh = new MimeHeaders();
        StringBuffer pincodeXml = retnPinCodeXML(ocs_systemid, ocs_system_pwd, libm, mdn, pincode);
        log.info("ZTE PinCodeProce(MDN:" + mdn + " OCS XML Request)==>" + pincodeXml);
        log.info("ZTE PARM_ReCharge==>" + sendURL);
        RequestEntity body;
        body = new StringRequestEntity(pincodeXml.toString(), "text/xml", "utf-8");
        result = sendHttpPostMsg(body, sendURL);
//        result = retnTempPinCodeXML().toString();
        log.info("ZTE putPincodeOCSlet Result==>" + result);
        return result;
    }

    //組XML送到ZTE
    public static StringBuffer retnPinCodeXML(String system_id, String system_pwd, String libm, String mdn, String pincode) {
        /*
         <soapenv:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:gpas="GPAService"> 
         <soapenv:Header/> 
         <soapenv:Body> 
         <gpas:Recharge soapenv:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"> 
         <req xsi:type="xs:RechargeReq" xmlns:xs="http://www.xmlsoap.com"> 
         <SessionID xsi:type="xsd:string">161123100419106</SessionID> 
         <SubscriberID xsi:type="xsd:string">0906001031</SubscriberID> 
         <vouchercardPIN xsi:type="xsd:string">1324657980132465</vouchercardPIN> 
         </req> 
         </gpas:Recharge> 
         </soapenv:Body> 
         </soapenv:Envelope>

         */
        String mdn886 = "886" + mdn.substring(1);
        StringBuffer sb = new StringBuffer();
        sb.append("<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:gpas=\"GPAService\">");
        sb.append("<soapenv:Header/>");
        sb.append("<soapenv:Body>");
        sb.append("<gpas:Recharge soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">");
        sb.append("<req xsi:type=\"xs:RechargeReq\" xmlns:xs=\"http://www.xmlsoap.com\">");
        sb.append("<SessionID xsi:type=\"xsd:string\">" + libm + "</SessionID>");
        sb.append("<SubscriberID xsi:type=\"xsd:string\">" + mdn886 + "</SubscriberID>");
        sb.append("<vouchercardPIN xsi:type=\"xsd:string\">" + pincode + "</vouchercardPIN>");
        sb.append("</req> ");
        sb.append("</gpas:Recharge> ");
        sb.append("</soapenv:Body>");
        sb.append("</soapenv:Envelope>");

        return sb;
    }

    public String putVoucherChangeStatusOCSlet(String libm, String StartSerialNo, String EndSerialNo) throws Exception {

        String result = "";
        String ocs_systemid = new ShareParm().OCS_SYSTEM_ID;
        String ocs_system_pwd = new ShareParm().OCS_SYSTEM_PWD;
        String sendURL = new ShareParm().PARM_ReCharge;

        MimeHeaders mh = new MimeHeaders();
        StringBuffer voucherCardXml = retnVoucherChangeStatusXML(ocs_systemid, ocs_system_pwd, libm, StartSerialNo, EndSerialNo);
        log.info("ZTE OCS XML Request)==>" + voucherCardXml);
        log.info("ZTE PARM_ReCharge==>" + sendURL);
        RequestEntity body;
        body = new StringRequestEntity(voucherCardXml.toString(), "text/xml", "utf-8");
        result = sendHttpPostMsg(body, sendURL);
//        result = retnTempPinCodeXML().toString();
        log.info("ZTE putPincodeOCSlet Result==>" + result);
        return result;
    }

    public static StringBuffer retnVoucherChangeStatusXML(String system_id, String system_pwd, String libm, String StartSerialNo, String EndSerialNo) {
        StringBuffer sb = new StringBuffer();
        sb.append("<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:gpas=\"GPAService\">");
        sb.append("<soapenv:Header/>");
        sb.append("<soapenv:Body>");
        sb.append("<gpas:VoucherBatchLock soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">");
        sb.append("<req xsi:type=\"xs:VoucherBatchLockReq\" xmlns:xs=\"http://www.xmlsoap.com\">");
        sb.append("<SessionID xsi:type=\"xsd:string\">" + libm + "</SessionID>");
        sb.append("<StartSerialNo xsi:type=\"xsd:string\">" + StartSerialNo + "</StartSerialNo>");
        sb.append("<EndSerialNo  xsi:type=\"xsd:string\">" + EndSerialNo + "</EndSerialNo >");
        sb.append("<LockState xsi:type=\"xsd:int\">" + 0 + "</LockState>");
        sb.append("</req> ");
        sb.append("</gpas:VoucherBatchLock>");
        sb.append("</soapenv:Body>");
        sb.append("</soapenv:Envelope>");

        return sb;
    }

    public String putVoucherInfoQueryOCSlet(String libm, String voucherCard) throws Exception {

        String result = "";
        String ocs_systemid = new ShareParm().OCS_SYSTEM_ID;
        String ocs_system_pwd = new ShareParm().OCS_SYSTEM_PWD;
        String sendURL = new ShareParm().PARM_ReCharge;

        MimeHeaders mh = new MimeHeaders();
        StringBuffer voucherCardXml = retnVoucherInfoQueryXML(ocs_systemid, ocs_system_pwd, libm, voucherCard);
        log.info("ZTE OCS XML Request)==>" + voucherCardXml);
        log.info("ZTE PARM_ReCharge==>" + sendURL);
        RequestEntity body;
        body = new StringRequestEntity(voucherCardXml.toString(), "text/xml", "utf-8");
        result = sendHttpPostMsg(body, sendURL);
//        result = retnTempPinCodeXML().toString();
        log.info("ZTE putPincodeOCSlet Result==>" + result);
        return result;
    }

    public static StringBuffer retnVoucherInfoQueryXML(String system_id, String system_pwd, String libm, String voucherCard) {
        StringBuffer sb = new StringBuffer();
        sb.append("<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:gpas=\"GPAService\">");
        sb.append("<soapenv:Header/>");
        sb.append("<soapenv:Body>");
        sb.append("<gpas:VoucherInfoQuery soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">");
        sb.append("<req xsi:type=\"xs:VoucherInfoQueryReq\" xmlns:xs=\"http://www.xmlsoap.com\">");
        sb.append("<SessionID xsi:type=\"xsd:string\">" + libm + "</SessionID>");
//        sb.append("<SubscriberID xsi:type=\"xsd:string\">" + mdn886 + "</SubscriberID>");
        sb.append("<VoucherCard xsi:type=\"xsd:string\">" + voucherCard + "</VoucherCard>");
        sb.append("<VoucherCardFlag xsi:type=\"xsd:int\">" + 0 + "</VoucherCardFlag>");
        sb.append("</req> ");
        sb.append("</gpas:VoucherInfoQuery> ");
        sb.append("</soapenv:Body>");
        sb.append("</soapenv:Envelope>");

        return sb;
    }

    public VoucherInfoResultBean parseZTEVoucherInfoXMLString(String xmlRecords) throws Exception {

        VoucherInfoResultBean voucherinforesultbean = new VoucherInfoResultBean();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));
            Document doc = db.parse(is);

            NodeList PinCodeRes = doc.getElementsByTagName("rsp");
            for (int i = 0; i < PinCodeRes.getLength(); i++) {
                Element element = (Element) PinCodeRes.item(i);

                NodeList nodes = element.getElementsByTagName("SessionID");
                Element line = (Element) nodes.item(0);
                log.debug("SessionID---->" + getCharacterDataFromElement(line));
                voucherinforesultbean.setLibm(line.getAttribute("SessionID"));

                nodes = element.getElementsByTagName("ReturnCode");
                line = (Element) nodes.item(0);
                log.info("ReturnCode------>" + getCharacterDataFromElement(line));
                voucherinforesultbean.setReturncode(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("Desc");
                line = (Element) nodes.item(0);
                log.info("Desc------>" + getCharacterDataFromElement(line));
                voucherinforesultbean.setDesc(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("ExpireDate");
                line = (Element) nodes.item(0);
                log.info("ExpireDate------>" + getCharacterDataFromElement(line));
                voucherinforesultbean.setExpiredate(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("CardType");
                line = (Element) nodes.item(0);
                log.info("CardType------>" + getCharacterDataFromElement(line));
                voucherinforesultbean.setCardtype(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("CardValue");
                line = (Element) nodes.item(0);
                log.info("CardValue------>" + getCharacterDataFromElement(line));
                voucherinforesultbean.setCardvalue(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("AddDays");
                line = (Element) nodes.item(0);
                log.info("AddDays------>" + getCharacterDataFromElement(line));
                voucherinforesultbean.setAdddays(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("ExpireDate");
                line = (Element) nodes.item(0);
                log.info("ExpireDate------>" + getCharacterDataFromElement(line));
                voucherinforesultbean.setCardvalue(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("ExpireDate");
                line = (Element) nodes.item(0);
                log.info("ExpireDate------>" + getCharacterDataFromElement(line));
                voucherinforesultbean.setExpiredate(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("VoucherCardNo");
                line = (Element) nodes.item(0);
                log.info("VoucherCardNo------>" + getCharacterDataFromElement(line));
                voucherinforesultbean.setVouchercardno(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("Status");
                line = (Element) nodes.item(0);
                log.info("Status------>" + getCharacterDataFromElement(line));
                voucherinforesultbean.setStatus(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("LockState");
                line = (Element) nodes.item(0);
                log.info("LockState------>" + getCharacterDataFromElement(line));
                voucherinforesultbean.setLockstate(getCharacterDataFromElement(line));

                //PIN
                nodes = element.getElementsByTagName("PIN");
                line = (Element) nodes.item(0);
                log.info("PIN------>" + getCharacterDataFromElement(line));
                voucherinforesultbean.setPin(getCharacterDataFromElement(line));

            }

        } catch (IOException e) {
            log.info(e);
        } catch (ParserConfigurationException e) {
            log.info(e);
        } catch (SAXException e) {
            log.info(e);
        }
        return voucherinforesultbean;
    }

    //temp result
//    public static StringBuffer retnTempPinCodeXML() {
//
//        StringBuffer sb = new StringBuffer();
//        sb.append("<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/xmlns:gpas=GPAService\"> ");
//        sb.append("<soapenv:Header/>");
//        sb.append("<soap:Body>");
//        sb.append("<gpas:Recharge soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">");
//        sb.append("<Resp xsi:type=\"xs:RechargeResp\" xmlns:xs=\"http://www.xmlsoap.com\">");
//        sb.append("<SessionID xsi:type=\"xsd:string\">T20161001100000001</SessionID>");
//        sb.append("<ReturnCode xsi:type=\"xsd:string\">0000</ReturnCode>");
//        sb.append("<Desc xsi:type=\"xsd:string\">Success</Desc>");
//        sb.append("<ExpireDate xsi:type=\"xsd:string\">01/10/2016</ExpireDate>");
//        sb.append("<ChargeMoney xsi:type=\"xsd:int\">300</ChargeMoney>");
//        sb.append("</Resp>");
//        sb.append("</gpas:Recharge>");
//        sb.append("</soap:Body>");
//        sb.append("</soapenv:Envelope>");
//        return sb;
//    }
    //解析ZTE回傳的結果
    public PinCodeResultBean parseZTEPinCodeXMLString(String xmlRecords) throws Exception {
        /*
         <?xml version="1.0" encoding="UTF-8"?>
         <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:ns2="http://www.xmlsoap.com" xmlns:ns1="GPAService">
         <SOAP-ENV:Header>
         </SOAP-ENV:Header>
         <SOAP-ENV:Body SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"><ns1:RechargeResponse>
         <rsp>
         <SessionID>161128145518527</SessionID>
         <ReturnCode>1012</ReturnCode>
         <Desc>SUB_ERR_CARDPIN_ERROR</Desc>
         </rsp>
         </ns1:RechargeResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>
         */
        PinCodeResultBean pincoderesultbean = new PinCodeResultBean();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));
            Document doc = db.parse(is);

            NodeList PinCodeRes = doc.getElementsByTagName("rsp");
            for (int i = 0; i < PinCodeRes.getLength(); i++) {
                Element element = (Element) PinCodeRes.item(i);

                NodeList nodes = element.getElementsByTagName("SessionID");
                Element line = (Element) nodes.item(0);
                log.debug("SessionID---->" + getCharacterDataFromElement(line));
                pincoderesultbean.setLibm(line.getAttribute("SessionID"));

                nodes = element.getElementsByTagName("ReturnCode");
                line = (Element) nodes.item(0);
                log.info("ReturnCode------>" + getCharacterDataFromElement(line));
                pincoderesultbean.setReturncode(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("Desc");
                line = (Element) nodes.item(0);
                log.info("Desc------>" + getCharacterDataFromElement(line));
                pincoderesultbean.setDesc(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("ChargeMoney");
                line = (Element) nodes.item(0);
                if (line != null) {
                    log.info("ChargeMoney------>" + getCharacterDataFromElement(line));
                    pincoderesultbean.setChargemoney(getCharacterDataFromElement(line));
                }

                nodes = element.getElementsByTagName("ExpireDate");
                line = (Element) nodes.item(0);
                if (line != null) {
                    log.info("ExpireDate------>" + getCharacterDataFromElement(line));
                    pincoderesultbean.setExpiredate(getCharacterDataFromElement(line));
                }
            }

        } catch (IOException e) {
            log.info(e);
        } catch (ParserConfigurationException e) {
            log.info(e);
        } catch (SAXException e) {
            log.info(e);
        }
        return pincoderesultbean;
    }

    private String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
//            log.info("cd.getData()==>" + cd.getData());
            return cd.getData();
        }
        return "";
    }

    public static String sendHttpPostMsg(RequestEntity requestBody, String url) throws Exception {

        String zte_KK = new ShareParm().PARM_4GZTEPINCODE_ACC;
        String rtresult = null;
        PostMethod post = new PostMethod(url);
        post.setRequestEntity(requestBody);
        HttpClient hc = new HttpClient();
        
        log.info("ZTE Auth==>"+zte_KK);
        
        post.setRequestHeader("SOAPAction", "http://www.ZTEsoft.com/ZSmart/QuerySubsProfile");
        post.setRequestHeader("Authorization", zte_KK);

        try {
            hc.getParams().setConnectionManagerTimeout(10);

            int result = hc.executeMethod(post);
            log.info("HttpStatus====>" + result);

            if (result == HttpStatus.SC_OK) {

                rtresult = post.getResponseBodyAsString();
            } else {
                rtresult = post.getResponseBodyAsString();
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

}
