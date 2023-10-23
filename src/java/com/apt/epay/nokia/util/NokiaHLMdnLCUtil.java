/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.nokia.util;

import com.apt.epay.nokia.bean.NokiaResultBean;
import com.apt.epay.nokia.bean.NokiaSubscriberStatusBean;
import com.apt.epay.share.ShareParm;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MimeHeaders;
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
public class NokiaHLMdnLCUtil {

    private static final Logger log = Logger.getLogger("EPAY");

    public NokiaResultBean putNokiaOCSlet(String libm, String mdn, String sessionId) throws Exception {

        String sendURL = new ShareParm().PARM_NOKIA_HLAPI_URL;

        NokiaResultBean result;
        NokiaUtil nokiautil = new NokiaUtil();
        MimeHeaders mh = new MimeHeaders();

        StringBuffer bucketXml = retnBucketXML(libm, mdn, sessionId);

        RequestEntity body;
        body = new StringRequestEntity(bucketXml.toString(), "text/xml", "utf-8");

        result = nokiautil.sendNokiaHttpPostMsg(body, sendURL);
        return result;
    }

    public StringBuffer retnBucketXML(String libm, String mdn, String sessionId) {
        String mdn886 = "886" + mdn.substring(1);
        StringBuffer sb = new StringBuffer();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
                        + "    <soapenv:Body>\n"
                        + "      <RetrieveRequest xmlns=\"http://alcatel-lucent.com/esm/ws/svcmgr/V2_0\">\n"
                        + "            <SessionInfo>\n"
                        + "                <sessionId>" + sessionId + "</sessionId>\n"
                        + "            </SessionInfo>\n"
                        + "            <RequestInfo>\n"
                        + "                <ReqID>" + libm + "</ReqID>\n"
                        + "            </RequestInfo>\n"
                        + "         <TaskList>\n"
                        + "            <Task>\n"
                        + "                <Name>Query Subscriber Account</Name>\n"
                        + "               <QueryCriteria>\n"
                        + "                   <Param>\n"
                        + "                       <Name>Account ID</Name>\n"
                        + "                       <Value>" + mdn886 + "</Value>\n"
                        + "                   </Param>\n"
                        + "	           </QueryCriteria>\n"
                        + "	             <QueryData>\n"
                        + "	                 <Collection>\n"
                        + "	                     <CollectionName>Subscriber Account</CollectionName>\n"
                        + "	                     <Attributes>\n"
                        + "	                                        <item>Class of Service ID</item>\n"
                        + "	                                        <item>External ID</item>\n"
                        + "	                                        <item>Contract ID</item>\n"
                        + "	                                        <item>Language Label</item>\n"
                        + "	                                        <item>SIM State</item>\n"
                        + "	                     </Attributes>\n"
                        + "	                 </Collection>\n"
                        + "	             </QueryData>\n"
                        + "             </Task>\n"
                        + "         </TaskList>\n"
                        + "      </RetrieveRequest>\n"
                        + "    </soapenv:Body>\n"
                        + "</soapenv:Envelope>");

        log.info(mdn + " NOKIA LifeCycle Request ==>" + sb.toString().replaceAll("[ \t\r\n]+", " ").trim());
        return sb;
    }

    public NokiaSubscriberStatusBean parseNokiaXMLString(String xmlRecords) throws Exception {

        log.info("NOKIA LifeCycle Response ==>" + xmlRecords.replaceAll("[ \t\r\n]+", " ").trim());

        NokiaSubscriberStatusBean subbean = new NokiaSubscriberStatusBean();

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
//                    System.out.println("ErrorCode : " + eElement.getElementsByTagName("ErrorCode").item(0).getTextContent());
//                    System.out.println("ErrorMsg : " + eElement.getElementsByTagName("ErrorMsg").item(0).getTextContent());
//                    System.out.println("ErrorCategory : " + eElement.getElementsByTagName("ErrorCategory").item(0).getTextContent());
                }
            }

            //ParamList
            NodeList pList = doc.getElementsByTagName("Param");
            for (int temp = 0; temp < pList.getLength(); temp++) {
                Node pNode = pList.item(temp);
//                System.out.println("\nCurrent Element :" + pNode.getNodeName());
                if (pNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) pNode;
//                    System.out.println("Name : " + eElement.getElementsByTagName("Name").item(0).getTextContent());                    
//                    System.out.println("Value : " + eElement.getElementsByTagName("Value").item(0).getTextContent());
                    if ("Class of Service ID".equalsIgnoreCase(eElement.getElementsByTagName("Name").item(0).getTextContent())) {
                        subbean.setServiceId(eElement.getElementsByTagName("Value").item(0).getTextContent());
                    }

                    if ("Language Label".equalsIgnoreCase(eElement.getElementsByTagName("Name").item(0).getTextContent())) {
                        subbean.setLanguage(eElement.getElementsByTagName("Value").item(0).getTextContent());
                    }

                    if ("SIM State".equalsIgnoreCase(eElement.getElementsByTagName("Name").item(0).getTextContent())) {
                        subbean.setSimstatus(eElement.getElementsByTagName("Value").item(0).getTextContent());
                    }

                    if ("Contract ID".equalsIgnoreCase(eElement.getElementsByTagName("Name").item(0).getTextContent())) {
                        subbean.setContract(eElement.getElementsByTagName("Value").item(0).getTextContent());
                    }

                    if ("External ID".equalsIgnoreCase(eElement.getElementsByTagName("Name").item(0).getTextContent())) {
                        subbean.setExternalId(eElement.getElementsByTagName("Value").item(0).getTextContent());
                    }
                }
            }
        } catch (Exception ex) {
            log.info(ex);
        }
        return subbean;
    }

}
