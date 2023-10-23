/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.nokia.util;

import com.apt.epay.nokia.bean.NokiaResultBean;
import com.apt.epay.nokia.bean.NokiaSubscribeAgentInfoBean;
import com.apt.epay.nokia.bean.NokiaSubscriberStatusBean;
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
public class NokiaHLAgentInfoUtil {

    private static final Logger log = Logger.getLogger("EPAY");

    public StringBuffer retnAgentXML(String libm, String sessionId, String mdn) {
        String mdn886 = "886" + mdn.substring(1);
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
                        + "   <soapenv:Body>\n"
                        + "      <RetrieveRequest xmlns=\"http://alcatel-lucent.com/esm/ws/svcmgr/V2_0\">\n"
                        + "         <SessionInfo>\n"
                        + "            <sessionId>" + sessionId + "</sessionId>\n"
                        + "         </SessionInfo>\n"
                        + "         <RequestInfo>\n"
                        + "            <ReqID>" + libm + "</ReqID>\n"
                        + "         </RequestInfo>\n"
                        + "         <TaskList>\n"
                        + "            <Task>\n"
                        + "               <Name>Query Subscriber Account</Name>\n"
                        + "               <QueryCriteria>\n"
                        + "                   <Param>\n"
                        + "                       <Name>Account ID</Name>\n"
                        + "                       <Value>" + mdn886 + "</Value>\n"
                        + "                   </Param>\n"
                        + "	            </QueryCriteria>\n"
                        + "                 <QueryData>\n"
                        + "                     <Collection>\n"
                        + "                         <CollectionName>Subscriber Account</CollectionName>\n"
                        + "                         <Attributes>\n"
                        + "                             <item>Class of Service ID</item>\n"
                        + "                             <item>External ID</item>\n"
                        + "                             <item>IMSI 1</item>\n"
                        + "                             <item>Contract ID</item>\n"
                        + "                             <item>Language Label</item>\n"
                        + "                             <item>SIM State</item>\n"
                        + "                             <item>Lifecycle Expiry Date 1</item>\n"
                        + "                             <item>Agent ID</item>\n"
                        + "                         </Attributes>\n"
                        + "                     </Collection>\n"
                        + "                 </QueryData>"
                        + "             </Task>\n"
                        + "         </TaskList>\n"
                        + "      </RetrieveRequest>\n"
                        + "   </soapenv:Body>\n"
                        + "</soapenv:Envelope>");

        log.info("NOKIA Agent Request ==>" + sb.toString().replaceAll("[ \t\r\n]+", " ").trim());
        return sb;
    }

    public NokiaResultBean putNokiaBasicInfoOCSlet(String libm, String sessionId, String mdn) throws Exception {

        String PROXY_FLAG = new ShareParm().PARM_SCT_PROXY_FLAG;
        String sendURL = new ShareParm().PARM_NOKIA_HLAPI_URL;

        NokiaResultBean result;
        NokiaUtil nokiautil = new NokiaUtil();

        MimeHeaders mh = new MimeHeaders();
        StringBuffer AgentInfoXml = retnAgentXML(libm, sessionId, mdn);

        RequestEntity body;
        body = new StringRequestEntity(AgentInfoXml.toString(), "text/xml", "utf-8");

        result = nokiautil.sendNokiaHttpPostMsg(body, sendURL);
        log.info("AgentInfoXml Response==>" + result);

        if (result.getHttpstatus() == HttpStatus.SC_OK) {
            String tmp_xml = result.getXmdrecord();
            if ("PROD".equalsIgnoreCase(PROXY_FLAG)) {
                tmp_xml = result.getXmdrecord().replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://10.108.17.36:8081/ecgs/dtd/gateway.dtd\">", "");
            } else {
                tmp_xml = result.getXmdrecord().replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://192.168.20.100:8081/ecgs/dtd/gateway.dtd\">", "");
            }
            result.setXmdrecord(tmp_xml);
//            log.info("Response====>"+tmp_xml);            
        }
        return result;
    }

    public NokiaSubscribeAgentInfoBean parseNokiaAgentXMLString(String xmlRecords) throws Exception {

        log.info("NOKIA Agent Response ==>" + xmlRecords.replaceAll("[ \t\r\n]+", " ").trim());

        NokiaSubscribeAgentInfoBean subbean = new NokiaSubscribeAgentInfoBean();

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
//                System.out.println("\nCurrent Element :" + rNode.getNodeName());
                if (rNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) rNode;
                    subbean.setResult(eElement.getElementsByTagName("Result").item(0).getTextContent());
                }
            }

            //ErrorInfo
            NodeList nList = doc.getElementsByTagName("ErrorInfo");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
//                System.out.println("\nCurrent Element :" + nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    subbean.setErrorCode(eElement.getElementsByTagName("ErrorCode").item(0).getTextContent());
                    subbean.setErrorMsg(eElement.getElementsByTagName("ErrorMsg").item(0).getTextContent());
                    subbean.setErrorCategory(eElement.getElementsByTagName("ErrorCategory").item(0).getTextContent());
                }
            }

            //ParamList
            NodeList pList = doc.getElementsByTagName("Param");
            for (int temp = 0; temp < pList.getLength(); temp++) {
                Node pNode = pList.item(temp);
//                System.out.println("\nCurrent Element :" + pNode.getNodeName());
                if (pNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) pNode;

                    if ("Class of Service ID".equalsIgnoreCase(eElement.getElementsByTagName("Name").item(0).getTextContent())) {
                        subbean.setServiceID(eElement.getElementsByTagName("Value").item(0).getTextContent());
                    }

                    if ("Language Label".equalsIgnoreCase(eElement.getElementsByTagName("Name").item(0).getTextContent())) {
                        subbean.setLanguage(eElement.getElementsByTagName("Value").item(0).getTextContent());
                    }

                    if ("SIM State".equalsIgnoreCase(eElement.getElementsByTagName("Name").item(0).getTextContent())) {
                        subbean.setSIMState(eElement.getElementsByTagName("Value").item(0).getTextContent());
                    }

                    if ("Contract ID".equalsIgnoreCase(eElement.getElementsByTagName("Name").item(0).getTextContent())) {
                        subbean.setContractID(eElement.getElementsByTagName("Value").item(0).getTextContent());
                    }

                    if ("External ID".equalsIgnoreCase(eElement.getElementsByTagName("Name").item(0).getTextContent())) {
                        subbean.setExternalID(eElement.getElementsByTagName("Value").item(0).getTextContent());
                    }
                    if ("Agent ID".equalsIgnoreCase(eElement.getElementsByTagName("Name").item(0).getTextContent())) {
                        subbean.setAgentID(eElement.getElementsByTagName("Value").item(0).getTextContent());
                    }
                }
            }
        } catch (Exception ex) {
            log.info(ex);
        }
        return subbean;
    }
}
