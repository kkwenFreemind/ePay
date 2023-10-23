/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.util;

import com.apt.epay.beans.CWSReqBean;
//import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.share.ShareParm;
//import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MimeHeaders;
//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpException;
//import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
//import org.apache.commons.httpclient.methods.PostMethod;
//import org.apache.commons.httpclient.methods.RequestEntity;
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
public class CwsProfile {

    private static final Logger log = Logger.getLogger("EPAY");

    public String putCwsProxyletByMDN(String xmdn) throws Exception {

        String result = "";
//        String serviceID = ShareParm.SOA_SYSTEM_ID;
//        String servicePWD = ShareParm.SOA_SYSTEM_PWD;
        String sendURL = new ShareParm().PARM_CWS_URL;
        MimeHeaders mh = new MimeHeaders();
        String mdn = xmdn;
        
        String dd = "queryIndicator=MDN&queryValue="+mdn+"&PIDPosition=1&PIDLen=4";
        log.info("ShareParm.PARM_CWS_URL="+new ShareParm().PARM_CWS_URL+"?"+dd);
        StringRequestEntity entity=new StringRequestEntity(dd,"application/xml","UTF-8");
        NameValuePair[] nvp = new NameValuePair[]{ new NameValuePair("queryIndicator", "MDN"), new NameValuePair("queryValue", mdn), new NameValuePair("PIDPosition", "1"),new NameValuePair("PIDLen", "4")};
//        result = sendHttpPostMsg(entity,sendURL);
//        result = sendHttpPostMsg(nvp,sendURL);
        result = HttpClientUtil.sendHttpPostMsg(nvp, sendURL);
        return result;
    }

    public CWSReqBean parseXMLString(String xmlRecords) throws Exception {

        log.info("xmlRecords===>"+xmlRecords);
        CWSReqBean aPIInvoiceRequestBean = new CWSReqBean();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));

            Document doc = db.parse(is);
            NodeList SOARes = doc.getElementsByTagName("cws-api");

            for (int i = 0; i < SOARes.getLength(); i++) {
                Element element = (Element) SOARes.item(i);

                NodeList nodes = element.getElementsByTagName("contractActDate");
                Element line = (Element) nodes.item(0);
                log.info("contractActDate------>" + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setContractActDate(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("contractStatus");
                line = (Element) nodes.item(0);
                log.info("contractStatus------>" + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setContractStatus(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("productType");
                line = (Element) nodes.item(0);
                log.info("productType------>" + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setProductType(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("contractStatusDesc");
                line = (Element) nodes.item(0);
                log.info("contractStatusDesc------>" + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setContractStatusDesc(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("MDN");
                line = (Element) nodes.item(0);
                log.info("MDN------>" + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setMdn(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("MIN");
                line = (Element) nodes.item(0);
                log.info("MIN------>" + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setMin(getCharacterDataFromElement(line));

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

//    public static String sendHttpPostMsg(RequestEntity requestBody, String url) throws Exception {
////        System.out.println(" ** sendHttpPostMsg step 01");
//        String rtresult = null;
//        PostMethod post = new PostMethod(url);
//        post.setRequestEntity(requestBody);
//        HttpClient hc = new HttpClient();
//
////        System.out.println(" ** setConnectionManagerTimeout(10) step 02");
//        try {
//            hc.getParams().setConnectionManagerTimeout(1);
////        System.out.println(" ** .setSoTimeout(10) step 02");
//            hc.getParams().setSoTimeout(1);
//
//            int result = hc.executeMethod(post);
////            System.out.println(" ** sendHttpPostMsg step 03");
//            if (result == HttpStatus.SC_OK) {
//                rtresult = post.getResponseBodyAsString();
//            } else {
//                rtresult = null;
//            }
//        } catch (HttpException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            post.releaseConnection();
//        }
//        return rtresult;
//    }
//    public static String sendHttpPostMsg(NameValuePair[] nvp, String url) throws Exception {
////        System.out.println(" ** sendHttpPostMsg step 01");
//        String rtresult = null;
//        PostMethod post = new PostMethod(url);
//        post.setRequestBody(nvp);
//        HttpClient hc = new HttpClient();
//
////        System.out.println(" ** setConnectionManagerTimeout(10) step 02");
//        try {
//            hc.getParams().setConnectionManagerTimeout(ShareParm.HTTP_REQ_TIMEOUT);
////        System.out.println(" ** .setSoTimeout(10) step 02");
//            hc.getParams().setSoTimeout(2);
//
//            int result = hc.executeMethod(post);
////            System.out.println(" ** sendHttpPostMsg step 03");
//            if (result == HttpStatus.SC_OK) {
//                log.info("SC_OK");
//                rtresult = post.getResponseBodyAsString();
//            } else {
//                log.info("HTTP RESULT="+result);
//                rtresult = null;
//            }
//        } catch (HttpException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            post.releaseConnection();
//        }
//        return rtresult;
//    }
}
