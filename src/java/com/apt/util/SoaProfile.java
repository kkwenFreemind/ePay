/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.util;

import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.share.ShareParm;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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

/**
 *
 * @author kevinchang
 */
public class SoaProfile {

    private static final Logger log = Logger.getLogger("EPAY");

    public String putSoaProxylet(String uuid) throws Exception {

        String result = "";
        String serviceID = new ShareParm().SOA_SYSTEM_ID;
        String servicePWD = new ShareParm().SOA_SYSTEM_PWD;
        String sendURL = new ShareParm().PARM_SOA_URL;

        MimeHeaders mh = new MimeHeaders();

        // Input Parameter.
        //String userID = "788ed2ca-1dbb-11e0-8520-001517eb22c4";
        String userID = uuid;
        String contractID = "";
//        String mdn = "0982347610";
        String mdn = "";
        String min = "";
        StringBuffer soaXml = retnSOAXML(serviceID, servicePWD, userID, contractID, mdn, min);
        log.debug("soaXml:" + soaXml);

        RequestEntity body;
        body = new StringRequestEntity(soaXml.toString(), "text/xml", "utf-8");

        result = sendHttpPostMsg(body, sendURL).replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://10.108.17.36:8081/ecgs/dtd/gateway.dtd\">", "");
        log.info("Result==>" + result);

//        try {
//            InputStream is = new ByteArrayInputStream(soaXml.toString().getBytes("UTF-8"));
//            SOAPMessage request = MessageFactory.newInstance().createMessage(mh, is);
//            SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();
//            SOAPConnection con = scf.createConnection();
//            SOAPMessage reply = con.call(request, new URL(sendURL));
//            ByteArrayOutputStream baos = null;
//            try {
//                baos = new ByteArrayOutputStream();
//                reply.writeTo(baos);
//                result = baos.toString("UTF-8");
//                log.debug("putSoaProxylet Result --->" + result);
//            } catch (Exception e) {
//                log.info(e);
//            }
//
//        } catch (UnsupportedEncodingException e) {
//            log.info(e);
//        }
        return result;
    }

    public String putSoaProxyletByMDN(String xmdn) throws Exception {

        String result = "";

        String serviceID = "";
        String servicePWD = "";
        String sendURL = "";

        try {
            serviceID = new ShareParm().SOA_SYSTEM_ID;
//            log.info("serviceID===>" + serviceID);
            servicePWD = new ShareParm().SOA_SYSTEM_PWD;
//            log.info("servicePWD===>" + servicePWD);
            sendURL = new ShareParm().PARM_SOA_URL;
//            log.info("sendURL===>" + sendURL);

            MimeHeaders mh = new MimeHeaders();

            // Input Parameter.
            //String userID = "788ed2ca-1dbb-11e0-8520-001517eb22c4";
            String userID = "";
            String contractID = "";
//        String mdn = "0982347610";
            String mdn = xmdn;
            String min = "";
            StringBuffer soaXml = retnSOAXML(serviceID, servicePWD, userID, contractID, mdn, min);
            log.debug("soaXml:" + soaXml);

            RequestEntity body;
            body = new StringRequestEntity(soaXml.toString(), "text/xml", "utf-8");

            result = sendHttpPostMsg(body, sendURL).replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://10.108.17.36:8081/ecgs/dtd/gateway.dtd\">", "");
            log.info("Result==>" + result);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }

    public static StringBuffer retnSOAXML(String serviceID, String servicePWD, String userID, String contractID, String mdn, String min) {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
        sb.append("<soap:Body>");
        sb.append("<ns0:getUserProfile xmlns:ns0=\"http://www.aptg.com.tw/ws/api/core/ProfileService\">");
        sb.append("<serviceID>" + serviceID + "</serviceID>");
        sb.append("<servicePWD>" + servicePWD + "</servicePWD>");
        sb.append("<userID>" + userID + "</userID>");
        sb.append("<contractID>" + contractID + "</contractID>");
        sb.append("<mdn>" + mdn + "</mdn>");
        sb.append("<min>" + min + "</min>");
        sb.append("</ns0:getUserProfile>");
        sb.append("</soap:Body>");
        sb.append("</soap:Envelope>");
        return sb;
    }

    public SOAReqBean parseXMLString(String xmlRecords) throws Exception {

        log.info("kkstr=>" + xmlRecords);
        SOAReqBean aPIInvoiceRequestBean = new SOAReqBean();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));

            Document doc = db.parse(is);
            NodeList SOARes = doc.getElementsByTagName("response");

            for (int i = 0; i < SOARes.getLength(); i++) {

                Element element = (Element) SOARes.item(i);

//                NodeList nodes = element.getElementsByTagName("contractID");
                NodeList nodes = element.getElementsByTagName("resultCode");
                Element line = (Element) nodes.item(0);
//                log.info("contractID------>" + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setResultcode(getCharacterDataFromElement(line));
//                aPIInvoiceRequestBean.setContractid(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("contractStatus");
                line = (Element) nodes.item(0);
//                log.info("contractStatus------>" + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setContract_status(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("contractStatusCode");
                line = (Element) nodes.item(0);
//                log.info("contractStatusCode------>" + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setContract_status_code(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("promotionCode");
                line = (Element) nodes.item(0);
//                log.info("promotionCode------>" + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setPromotioncode(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("email");
                line = (Element) nodes.item(0);
//                log.info("email------>" + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setEmail(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("mdn");
                line = (Element) nodes.item(0);
//                log.info("mdn------>" + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setMdn(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("min");
                line = (Element) nodes.item(0);
//                log.info("min------>" + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setMin(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("name");
                line = (Element) nodes.item(0);
//                log.info("name------>" + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setName(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("personalID");
                line = (Element) nodes.item(0);
//                log.info("personalID------>" + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setPersonalid(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("productType");
                line = (Element) nodes.item(0);
//                log.info("productType------>" + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setProducttype(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("contractID");
                line = (Element) nodes.item(0);
//                log.info("productType------>" + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setContractid(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("address");
                line = (Element) nodes.item(0);
//                log.info("address------>" + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setAddress(getCharacterDataFromElement(line));
            }

        } catch (Exception e) {
            log.info(e);
        }
        return aPIInvoiceRequestBean;
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
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            post.releaseConnection();
        }
        return rtresult;
    }
    
    public String putSoaProxyletByMIN(String xmin) throws Exception {

        String result = "";

        String serviceID = "";
        String servicePWD = "";
        String sendURL = "";

        try {
            serviceID = new ShareParm().SOA_SYSTEM_ID;
//            log.info("serviceID===>" + serviceID);
            servicePWD = new ShareParm().SOA_SYSTEM_PWD;
//            log.info("servicePWD===>" + servicePWD);
            sendURL = new ShareParm().PARM_SOA_URL;
//            log.info("sendURL===>" + sendURL);

            MimeHeaders mh = new MimeHeaders();

            // Input Parameter.
            //String userID = "788ed2ca-1dbb-11e0-8520-001517eb22c4";
            String userID = "";
            String contractID = "";
//        String mdn = "0982347610";
            String mdn = "";
            String min = xmin;
            StringBuffer soaXml = retnSOAXML(serviceID, servicePWD, userID, contractID, mdn, min);
            log.debug("soaXml:" + soaXml);

            RequestEntity body;
            body = new StringRequestEntity(soaXml.toString(), "text/xml", "utf-8");

            result = sendHttpPostMsg(body, sendURL).replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://10.108.17.36:8081/ecgs/dtd/gateway.dtd\">", "");
            log.info("Result==>" + result);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }    
}
