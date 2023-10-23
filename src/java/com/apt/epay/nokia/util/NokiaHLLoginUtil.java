/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.nokia.util;

import com.apt.epay.nokia.bean.NokiaResultBean;
import com.apt.epay.share.ShareParm;
import java.io.StringReader;
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

/**
 *
 * @author kevinchang
 */
public class NokiaHLLoginUtil {

    private static final Logger log = Logger.getLogger("EPAY");

    public StringBuffer retnLoginXML(String system_id, String system_pwd) {
        
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
                        + "  <soapenv:Body>\n"
                        + "    <LoginRequest xmlns=\"http://alcatel-lucent.com/esm/ws/svcmgr/V2_0\">\n"
                        + "      <loginId>" + system_id + "</loginId>\n"
                        + "      <passwd>" + system_pwd + "</passwd>\n"
                        + "      <newPasswd></newPasswd>\n"
                        + "      <srcIP></srcIP>\n"
                        + "      <wsdlVersion>V_2</wsdlVersion>\n"
                        + "    </LoginRequest>\n"
                        + "  </soapenv:Body>\n"
                        + "</soapenv:Envelope>");

        log.info("NOKIA Login Request ==>" + sb.toString().replaceAll("[ \t\r\n]+", " ").trim());
        return sb;
    }

    public NokiaResultBean putLoginOCSlet() {
        //kk configure

        String ocs_systemid = new ShareParm().PARM_NOKIA_HLAPI_SYSTEM_ID;
        String ocs_system_pwd = new ShareParm().PARM_NOKIA_HLAPI_SYSTEM_PWD;
        String sendURL = new ShareParm().PARM_NOKIA_HLAPI_URL;

        NokiaResultBean result = null;
        try {
            NokiaUtil nokiautil = new NokiaUtil();
            MimeHeaders mh = new MimeHeaders();
            StringBuffer bucketXml = retnLoginXML(ocs_systemid, ocs_system_pwd);

            RequestEntity body;
            body = new StringRequestEntity(bucketXml.toString(), "text/xml", "utf-8");

            result = nokiautil.sendNokiaHttpPostMsg(body, sendURL);
            log.info("NOKIA Login Response ==>" + result.getHttpstatus());
            log.info("NOKIA Login Response ==>" + result.getXmdrecord());

        } catch (Exception ex) {
            log.info(ex);
        }
        return result;
    }

    public String getSeesionId(String xmlRecords) throws ParserConfigurationException {
        String sessionId = "";

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));
            Document doc = db.parse(is);

            NodeList nodes = doc.getElementsByTagName("SessionInfo");
            // iterate the employees
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);

                NodeList name = element.getElementsByTagName("sessionId");
                Element line = (Element) name.item(0);
                sessionId = getCharacterDataFromElement(line);
                log.info("NOKIA Login SessionId ==>" + getCharacterDataFromElement(line));
            }

        } catch (Exception ex) {
            log.info(ex);
        }

        return sessionId;
    }


    private static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }
}
