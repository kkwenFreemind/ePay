/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.nokia.util;

import com.apt.epay.nokia.bean.NokiaChangeLifeCycleResultBean;
import com.apt.epay.nokia.bean.NokiaResultBean;
import com.apt.epay.share.ShareParm;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MimeHeaders;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author kevinchang
 */
public class NokiaHLChangeLifeCycleUtil {

    private static final Logger log = Logger.getLogger("EPAY");

    public StringBuffer retnBucketActiveXML(String libm, String sessionId, String mdn, String LC) {
        String mdn886 = "886" + mdn.substring(1);
        StringBuffer sb = new StringBuffer();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
                        + "  <soapenv:Body>\n"
                        + "    <SubmitRequest xmlns=\"http://alcatel-lucent.com/esm/ws/svcmgr/V2_0\">\n"
                        + "      <SessionInfo>\n"
                        + "        <sessionId>" + sessionId + "</sessionId>\n"
                        + "      </SessionInfo>\n"
                        + "      <RequestInfo>\n"
                        + "        <ReqID>" + libm + "</ReqID>\n"
                        + "      </RequestInfo>\n"
                        + "      <NERoutingInfo>\n"
                        + "        <NeName></NeName>\n"
                        + "        <NeGroupName>inasgrp</NeGroupName>\n"
                        + "        <DistributionKey></DistributionKey>\n"
                        + "      </NERoutingInfo>\n"
                        + "      <ParamList></ParamList>\n"
                        + "      <TaskList>\n"
                        + "        <Task>\n"
                        + "          <Name>Update Lifecycle</Name>\n"
                        + "          <ParamList>\n"
                        + "            <Param>\n"
                        + "              <Name>Account ID</Name>\n"
                        + "              <Value>" + mdn886 + "</Value>\n"
                        + "            </Param>\n"
                        + "            <Param>\n"
                        + "               <Name>New State</Name>\n"
                        + "               <Value>" + LC + "</Value>\n"
                        + "            </Param>\n"
                        + "          </ParamList>\n"
                        + "        </Task>\n"
                        + "      </TaskList>\n"
                        + "    </SubmitRequest>\n"
                        + "  </soapenv:Body>\n"
                        + "</soapenv:Envelope>");

        log.info("NOKIA Change LifeCycle Request ==>" + sb.toString().replaceAll("[ \t\r\n]+", " ").trim());
        return sb;
    }

    public NokiaResultBean putNokiaChangeLifeCycleOCSlet(String libm, String sessionId, String mdn, String lifecycle) throws Exception {

        NokiaResultBean result;
        NokiaUtil nokiautil = new NokiaUtil();

        String PROXY_FLAG = new ShareParm().PARM_SCT_PROXY_FLAG;
        String sendURL = new ShareParm().PARM_NOKIA_HLAPI_URL;

        MimeHeaders mh = new MimeHeaders();
        StringBuffer ChangeLifeCycelXml = retnBucketActiveXML(libm, sessionId, mdn, lifecycle);

        RequestEntity body;
        body = new StringRequestEntity(ChangeLifeCycelXml.toString(), "text/xml", "utf-8");

        result = nokiautil.sendNokiaHttpPostMsg(body, sendURL);

        if (result.getHttpstatus() == HttpStatus.SC_OK) {
            String tmp_xml;// = result.getXmdrecord();
            if ("PROD".equalsIgnoreCase(PROXY_FLAG)) {
                tmp_xml = result.getXmdrecord().replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://10.108.17.36:8081/ecgs/dtd/gateway.dtd\">", "");
            } else {
                tmp_xml = result.getXmdrecord().replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://192.168.20.100:8081/ecgs/dtd/gateway.dtd\">", "");
            }
            result.setXmdrecord(tmp_xml.replaceAll("[ \t\r\n]+", " ").trim());
            log.info("NOKIA Change LifeCycle Response ==>" + tmp_xml.replaceAll("[ \t\r\n]+", " ").trim());
        }
        return result;
    }

    public NokiaChangeLifeCycleResultBean parseNokiaXMLString(String xmlRecords) throws Exception {

        //log.info("NOKIA ChangeLifeCycle Response ==>" + xmlRecords.replaceAll("[ \t\r\n]+", " ").trim());

        NokiaChangeLifeCycleResultBean subbean = new NokiaChangeLifeCycleResultBean();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));

            Document doc = db.parse(is);

            //TaskResponse
            NodeList rList = doc.getElementsByTagName("TaskResponse");
            for (int temp = 0; temp < rList.getLength(); temp++) {
                Node rNode = rList.item(temp);
                if (rNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) rNode;
                    subbean.setResult(eElement.getElementsByTagName("Result").item(0).getTextContent());
                }
            }

            //ErrorInfo
            NodeList nList = doc.getElementsByTagName("ErrorInfo");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    subbean.setErrorCode(eElement.getElementsByTagName("ErrorCode").item(0).getTextContent());
                    subbean.setErrorMsg(eElement.getElementsByTagName("ErrorMsg").item(0).getTextContent());
                    subbean.setErrorCategory(eElement.getElementsByTagName("ErrorCategory").item(0).getTextContent());
                }
            }
        } catch (Exception ex) {
            log.info(ex);
        }

        return subbean;
    }
}
