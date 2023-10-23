/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.nokia.util;

import com.apt.epay.nokia.bean.NokiaReqBean;
import com.apt.epay.nokia.bean.NokiaResultBean;
import com.apt.epay.share.ShareParm;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
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
public class NokiaUtil {

    private static final Logger log = Logger.getLogger("EPAY");

    public NokiaResultBean sendNokiaHttpPostMsg(RequestEntity requestBody, String url) throws Exception {

        String PROXY_FLAG = new ShareParm().PARM_SCT_PROXY_FLAG;

        log.info("PARM_NOKIA_OCS_URL===>" + url);
        NokiaResultBean nokiaresult = new NokiaResultBean();
        PostMethod post = new PostMethod(url);
        post.setRequestEntity(requestBody);
        HttpClient hc = new HttpClient();
        int ii = 0;
        String xml = "";
        try {
            hc.getParams().setConnectionManagerTimeout(10);
            ii = hc.executeMethod(post);            

            if ("PROD".equals(PROXY_FLAG)) {
                xml = post.getResponseBodyAsString().replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://10.108.17.36:8081/ecgs/dtd/gateway.dtd\">", "").replaceAll("[ \t\r\n]+", " ").trim();
            } else {
                xml = post.getResponseBodyAsString().replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://192.168.20.100:8081/ecgs/dtd/gateway.dtd\">", "").replaceAll("[ \t\r\n]+", " ").trim();
            }
            nokiaresult.setHttpstatus(ii);
            nokiaresult.setXmdrecord(xml);

        } catch (HttpException e) {
            log.info(e);
        } catch (IOException e) {
            log.info(e);
        } finally {
            post.releaseConnection();
            return nokiaresult;
        }

    }

    public String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }

    public static NokiaReqBean parseNokiaResultXMLString(String xmlRecords) {

        NokiaUtil nokiaUtil = new NokiaUtil();
        NokiaReqBean aPIPinCodeRequestBean = new NokiaReqBean();
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

                log.info("status--->" + line.getAttribute("status"));
                aPIPinCodeRequestBean.setStatus(line.getAttribute("status"));

                log.info("result_code----->" + line.getAttribute("result_code"));
                aPIPinCodeRequestBean.setResultcode(line.getAttribute("result_code"));

                log.info("response_timestamp----->" + line.getAttribute("response_timestamp"));
                aPIPinCodeRequestBean.setResponse_timestamp(line.getAttribute("response_timestamp"));

                nodes = element.getElementsByTagName("RequestHeader");
                log.info("request_tid------>" + line.getAttribute("request_tid"));
                aPIPinCodeRequestBean.setRequestid(line.getAttribute("request_tid"));

                nodes = element.getElementsByTagName("SubscriberID");
                line = (Element) nodes.item(0);
                log.info("SubscriberID---->" + nokiaUtil.getCharacterDataFromElement(line));
                aPIPinCodeRequestBean.setMdn(nokiaUtil.getCharacterDataFromElement(line));
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

}
