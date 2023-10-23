/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.util;

import com.apt.epay.beans.BasicInfoReqBean;
import com.apt.epay.share.ShareParm;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MimeHeaders;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
//import org.apache.commons.httpclient.NameValuePair;
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
public class OCS4GBasicInfoUtil {

    private static final Logger log = Logger.getLogger("EPAY");

    public String putOCS4GBasicInfoSlet(String libm, String mdn, String tradedate) throws Exception {

        String result = "";
        String ocs_systemid = new ShareParm().PARM_4GOCS_SYSTEM_ID;
        String ocs_system_pwd = new ShareParm().PARM_4GOCS_SYSTEM_PWD;
        String sendURL = new ShareParm().PARM_4GOCS_URL;

        MimeHeaders mh = new MimeHeaders();
        StringBuffer BasicInfoXml = retn4GOCSXML(ocs_systemid, ocs_system_pwd, libm, mdn, tradedate);
        log.info("4G OCSXml:" + BasicInfoXml);

        RequestEntity body;
        body = new StringRequestEntity(BasicInfoXml.toString(), "text/xml", "utf-8");

        result = sendHttpPostMsg(body, sendURL).replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://10.108.17.36:8081/ecgs/dtd/gateway.dtd\">", "");
        log.info("putOCS4GBasicInfoSlet Result==>" + result);

//        try {
//            InputStream is = new ByteArrayInputStream(BasicInfoXml.toString().getBytes("UTF-8"));
//            SOAPMessage request = MessageFactory.newInstance().createMessage(mh, is);
//            SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();
//            SOAPConnection con = scf.createConnection();
//
//            //TDE 10.107.170.201:8081
//            //PROD 10.106.133.136:8081
//            SOAPMessage reply = con.call(request, new URL(sendURL));
//            ByteArrayOutputStream baos = null;
//
//            try {
//                baos = new ByteArrayOutputStream();
//                reply.writeTo(baos);
//                result = baos.toString();
//                log.info("putOCS4GBasicInfoSlet Result==>" + result);
//            } catch (Exception e) {
//
//            }
////            StringBuffer sb = retnTempBasicInfoXML();
////            result = sb.toString();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return result;
    }

    public static StringBuffer retn4GOCSXML(String system_id, String system_pwd, String libm, String mdn, String tradedate) {
        String mdn886 = "886" + mdn.substring(1);
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
//        sb.append("<soap-env:Envelope xmlns:soap-env='http://schemas.xmlsoap.org/soap/envelope/'>");
//        sb.append("<soap-env:Header/>");
//        sb.append("<soap-env:Body>");
        sb.append("<!DOCTYPE GatewayRequest SYSTEM \"http://10.108.20.44:8081/ecgs/dtd/gateway.dtd\">");
        sb.append("<GatewayRequest>");
        sb.append("<RequestHeader");
        sb.append(" version=\"2915\"");
        sb.append(" requesting_system_id=\"" + system_id + "\"");
        sb.append(" requesting_system_pwd=\"" + system_pwd + "\"");
        sb.append(" request_tid=\"" + libm + "\" request_timestamp=\"" + tradedate + "\"");
        sb.append(" additional_info=\"Query Account Info\">");
        sb.append("</RequestHeader>");
        sb.append("<SubscriberAccountInfo account_code=\"2000\" balance_type=\"primary\">");
        sb.append("<SubscriberID");
        sb.append(" SubscriberIDType=\"00\">" + mdn886 + "</SubscriberID>");
        sb.append("</SubscriberAccountInfo>");
        sb.append("<BundleSubscriptionRequest>");
        sb.append("</BundleSubscriptionRequest>");
        sb.append("</GatewayRequest>");
//        sb.append("</soap-env:Body>");
//        sb.append("</soap-env:Envelope>");
        return sb;
    }

    public static StringBuffer retnTempBasicInfoXML() {

        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
//        sb.append("<!DOCTYPE GatewayResponse SYSTEM \"http://10.0.0.1:8081/ecgs/dtd/gateway.dtd\">");
        sb.append("<GatewayResponse>");
        sb.append("<ResponseHeader status=\"pass\" result_code=\"00\" response_timestamp=\"05/20/2005.12:12:13\" additional_info=\"Query Account Info\">");
        sb.append("</ResponseHeader>");
        sb.append("<RequestHeader version=\"2915\" requesting_system_id=\"100\" requesting_system_pwd=\"pwd123\" request_tid=\"4321\" request_timestamp=\"05/20/2005.12:12:12\" additional_info=\"1234\">");
        sb.append("</RequestHeader>");
        sb.append("<SubscriberAccountInfo account_code=\"2000\" balance_type=\"primary\">");
        sb.append("<SubscriberID SubscriberIDType=\"00\">886906123456</SubscriberID>");
        sb.append("</SubscriberAccountInfo>");
        sb.append("<BundleSubscriptionResponse");
        sb.append(" Balance1=\"5000\"");
        sb.append(" Balance1Type=\"0\"");
        sb.append(" CurrencyLabel=\"NTD\"");
//        sb.append(" SIMExpDate=\"01122007\"");
        sb.append(" DefaultTariffPlanCOSPID=\"COSP1\"");
        sb.append(" LanguageLabel=\"Chinese\"");
        sb.append(" LifeCycleState=\"active\"");
//        sb.append(" LifeCycleExpDate1=\"10122007\"");
//        sb.append(" LifeCycleExpDate2=\"10062008\"");
        sb.append(" CreditExpDate=\"01122007\"");
        sb.append(" ErrorIndicator=\"0\"");
        sb.append(" SIMFrozen=\"0\">");
        sb.append(" <PromotionalTariffPlanCOSP Sequence_No=\"1\"");
        sb.append(" ID=\"BAL\"");
        sb.append(" StartDate=\"01012007\"");
        sb.append(" EndDate=\"12122007\"");
        sb.append(" Status=\"0\">");
        sb.append(" </PromotionalTariffPlanCOSP>");
        sb.append("<PromotionalTariffPlanCOSP Sequence_No=\"2\"");
        sb.append(" ID=\"VOLVB1\"");
        sb.append(" StartDate=\"01012007\"");
        sb.append(" EndDate=\"12122007\"");
        sb.append(" Status=\"1\">");
        sb.append(" </PromotionalTariffPlanCOSP>");
        sb.append(" <PromotionalTariffPlanCOSP Sequence_No=\"4\"");
        sb.append(" ID=\"MINVB1\"");
        sb.append(" StartDate=\"01012007\"");
        sb.append(" EndDate=\"12122007\"");
        sb.append(" Status=\"3\">");
        sb.append(" </PromotionalTariffPlanCOSP>");
        sb.append(" <Discount Sequence_No=\"1\"");
        sb.append(" ID=\"610\"");
        sb.append(" StartDate=\"01012007\"");
        sb.append(" EndDate=\"11122007\"");
        sb.append(" CounterValue=\"10\"");
        sb.append(" RolloverValue=\"100\">");
        sb.append(" </Discount>");
        sb.append(" <Discount Sequence_No=\"2\"");
        sb.append(" ID=\"620\"");
        sb.append(" StartDate=\"01012007\"");
        sb.append(" EndDate=\"12122007\">");
        sb.append(" CounterValue=\"10\"");
        sb.append(" RolloverValue=\"200\">");
        sb.append(" </Discount>");
        sb.append(" <Discount Sequence_No=\"3\"");
        sb.append(" ID=\"720\"");
        sb.append(" StartDate=\"01012007\"");
        sb.append(" EndDate=\"13122007\"");
        sb.append(" CounterValue=\"10\"");
        sb.append(" RolloverValue=\"300\">");
        sb.append(" </Discount>");
        sb.append(" <Discount Sequence_No=\"4\"");
        sb.append(" ID=\"730\"");
        sb.append(" StartDate=\"01012007\"");
        sb.append(" EndDate=\"14122007\"");
        sb.append(" CounterValue=\"10\"");
        sb.append(" RolloverValue=\"400\">");
        sb.append(" </Discount>");
        sb.append(" <Discount Sequence_No=\"5\"");
        sb.append(" ID=\"810\"");
        sb.append(" StartDate=\"01012007\"");
        sb.append(" EndDate=\"15122007\"");
        sb.append(" CounterValue=\"10\"");
        sb.append(" RolloverValue=\"500\">");
        sb.append(" </Discount>");
        sb.append(" </BundleSubscriptionResponse>");
        sb.append(" </GatewayResponse>");
        return sb;
    }

    public BasicInfoReqBean parseBasicInfoXMLString(String xmlRecords) throws Exception {

        log.info("BasicInfo xmlRecords==>" + xmlRecords);

        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        BasicInfoReqBean aPIBasicInfoRequestBean = new BasicInfoReqBean();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));

            Document doc = db.parse(is);

            NodeList kkBasicInfoRes = doc.getElementsByTagName("ResponseHeader");
            for (int index = 0; index < kkBasicInfoRes.getLength(); index++) {
                Node kkNodeA = kkBasicInfoRes.item(index);
                if (kkNodeA.getNodeType() == Node.ELEMENT_NODE) {
                    Element kklineA = (Element) kkNodeA;
                    aPIBasicInfoRequestBean.setResultcode(kklineA.getAttribute("result_code"));
                    log.info("lineA.getAttribute(result_code)=====>" + kklineA.getAttribute("result_code"));
                }
            }

            NodeList BasicInfoResA = doc.getElementsByTagName("BundleSubscriptionResponse");
            for (int index = 0; index < BasicInfoResA.getLength(); index++) {
                Node nNodeA = BasicInfoResA.item(index);
                if (nNodeA.getNodeType() == Node.ELEMENT_NODE) {
                    Element lineA = (Element) nNodeA;
                    aPIBasicInfoRequestBean.setDefaultTariffPlanCOSPID(lineA.getAttribute("DefaultTariffPlanCOSPID"));
                    aPIBasicInfoRequestBean.setLifeCycleState(lineA.getAttribute("LifeCycleState"));
                    aPIBasicInfoRequestBean.setEndDate1(lineA.getAttribute("CreditExpDate"));
                    aPIBasicInfoRequestBean.setEndDate2(lineA.getAttribute("CreditExpDate"));
                }
            }

            NodeList PromotionalTariffPlanCOSPRes = doc.getElementsByTagName("PromotionalTariffPlanCOSP");
            for (int j = 0; j < PromotionalTariffPlanCOSPRes.getLength(); j++) {
                Node nNodex = PromotionalTariffPlanCOSPRes.item(j);
                if (nNodex.getNodeType() == Node.ELEMENT_NODE) {
                    Element line = (Element) nNodex;
                    String tmp_PromotionalTariffPlanCOSP_ID = "";
                    if (!line.getAttribute("ID").equals("") || line.getAttribute("ID") != null) {
                        tmp_PromotionalTariffPlanCOSP_ID = line.getAttribute("ID");
                        log.info("line.getAttribute(ID)==>" + line.getAttribute("ID"));
                        if ((tmp_PromotionalTariffPlanCOSP_ID.substring(0, 5)).equals("VOLVB")) {
                            aPIBasicInfoRequestBean.setPromotionalTariffPlanCOSP_ID(tmp_PromotionalTariffPlanCOSP_ID);
                            log.info("PromotionalTariffPlanCOSP_ID====>" + aPIBasicInfoRequestBean.getPromotionalTariffPlanCOSP_ID());
                        }
                    }
                }
            }

            NodeList BasicInfoRes = doc.getElementsByTagName("Discount");

            for (int i = 0; i < BasicInfoRes.getLength(); i++) {

                Node nNode = BasicInfoRes.item(i);
//                Element element = (Element) BasicInfoRes.item(i);
//                NodeList nodes = element.getElementsByTagName("Discount");
//                Element line = (Element) nodes.item(0);
//                Node nNode = BasicInfoRes.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element line = (Element) nNode;

                    String k1 = "";
                    int kk = 0;
                    if (!line.getAttribute("ID").equals("") || line.getAttribute("ID") != null) {
                        k1 = line.getAttribute("ID");
                        kk = Integer.valueOf(line.getAttribute("ID"));
                    }
                    log.info("k1===>" + k1);

                    switch (kk) {
                        case 610:
//                        log.info("Sequence_No--->" + line.getAttribute("Sequence_No"));
//                        aPIBasicInfoRequestBean.setSequence_No1(line.getAttribute("StartDate"));
                            log.info("ID----->" + line.getAttribute("ID"));
                            aPIBasicInfoRequestBean.setID1(line.getAttribute("ID"));
//                        log.info("EndDate----->" + line.getAttribute("EndDate"));
//                        aPIBasicInfoRequestBean.setEndDate1(line.getAttribute("EndDate"));
                            log.info("CounterValue----->" + line.getAttribute("CounterValue"));
                            aPIBasicInfoRequestBean.setCounterValue1(line.getAttribute("CounterValue"));
                            log.info("RolloverValue----->" + line.getAttribute("RolloverValue"));
                            aPIBasicInfoRequestBean.setRolloverValue1(line.getAttribute("RolloverValue"));
                            break;

                        case 620:
                            log.info("Sequence_No--->" + line.getAttribute("Sequence_No"));
//                        aPIBasicInfoRequestBean.setSequence_No2(line.getAttribute("StartDate"));
                            log.info("ID----->" + line.getAttribute("ID"));
                            aPIBasicInfoRequestBean.setID2(line.getAttribute("ID"));
//                        log.info("EndDate----->" + line.getAttribute("EndDate"));
//                        aPIBasicInfoRequestBean.setEndDate2(line.getAttribute("EndDate"));
                            log.info("CounterValue----->" + line.getAttribute("CounterValue"));
                            aPIBasicInfoRequestBean.setCounterValue2(line.getAttribute("CounterValue"));
                            log.info("RolloverValue----->" + line.getAttribute("RolloverValue"));
                            aPIBasicInfoRequestBean.setRolloverValue2(line.getAttribute("RolloverValue"));
                            break;

                        case 720:
                            log.info("Sequence_No--->" + line.getAttribute("Sequence_No"));
                            aPIBasicInfoRequestBean.setSequence_No3(line.getAttribute("Sequence_No"));
                            log.info("StartDate--->" + line.getAttribute("StartDate"));
                            aPIBasicInfoRequestBean.setStartDate3(line.getAttribute("StartDate"));
                            log.info("ID----->" + line.getAttribute("ID"));
                            aPIBasicInfoRequestBean.setID3(line.getAttribute("ID"));
                            log.info("EndDate----->" + line.getAttribute("EndDate"));
                            aPIBasicInfoRequestBean.setEndDate3(line.getAttribute("EndDate"));
                            log.info("CounterValue----->" + line.getAttribute("CounterValue"));
                            aPIBasicInfoRequestBean.setCounterValue3(line.getAttribute("CounterValue"));
                            log.info("RolloverValue----->" + line.getAttribute("RolloverValue"));
                            aPIBasicInfoRequestBean.setRolloverValue3(line.getAttribute("RolloverValue"));
                            break;

                        case 730:
                            log.info("Sequence_No--->" + line.getAttribute("Sequence_No"));
                            aPIBasicInfoRequestBean.setStartDate4(line.getAttribute("StartDate"));
                            log.info("StartDate--->" + line.getAttribute("StartDate"));
                            log.info("ID----->" + line.getAttribute("ID"));
                            aPIBasicInfoRequestBean.setID4(line.getAttribute("ID"));
                            log.info("EndDate----->" + line.getAttribute("EndDate"));
                            aPIBasicInfoRequestBean.setEndDate4(line.getAttribute("EndDate"));
                            log.info("CounterValue----->" + line.getAttribute("CounterValue"));
                            aPIBasicInfoRequestBean.setCounterValue4(line.getAttribute("CounterValue"));
                            log.info("RolloverValue----->" + line.getAttribute("RolloverValue"));
                            aPIBasicInfoRequestBean.setRolloverValue4(line.getAttribute("RolloverValue"));
                            break;

                        case 810:
                            log.info("Sequence_No--->" + line.getAttribute("Sequence_No"));
                            aPIBasicInfoRequestBean.setSequence_No5(line.getAttribute("Sequence_No"));
//                            log.info("StartDate--->" + line.getAttribute("StartDate"));
//                            aPIBasicInfoRequestBean.setStartDate5(line.getAttribute("StartDate"));
                            log.info("ID----->" + line.getAttribute("ID"));
                            aPIBasicInfoRequestBean.setID5(line.getAttribute("ID"));
                            log.info("EndDate----->" + line.getAttribute("EndDate"));
                            aPIBasicInfoRequestBean.setEndDate5(line.getAttribute("EndDate"));
                            log.info("CounterValue----->" + line.getAttribute("CounterValue"));
                            aPIBasicInfoRequestBean.setCounterValue5(line.getAttribute("CounterValue"));
                            log.info("RolloverValue----->" + line.getAttribute("RolloverValue"));
                            aPIBasicInfoRequestBean.setRolloverValue5(line.getAttribute("RolloverValue"));
                            break;

                        case 820:
                            log.info("Sequence_No--->" + line.getAttribute("Sequence_No"));
                            aPIBasicInfoRequestBean.setSequence_No6(line.getAttribute("Sequence_No"));
//                            log.info("StartDate--->" + line.getAttribute("StartDate"));
//                            aPIBasicInfoRequestBean.setStartDate6(line.getAttribute("StartDate"));
                            log.info("ID----->" + line.getAttribute("ID"));
                            aPIBasicInfoRequestBean.setID6(line.getAttribute("ID"));
                            log.info("EndDate----->" + line.getAttribute("EndDate"));
                            aPIBasicInfoRequestBean.setEndDate6(line.getAttribute("EndDate"));
                            log.info("CounterValue----->" + line.getAttribute("CounterValue"));
                            aPIBasicInfoRequestBean.setCounterValue6(line.getAttribute("CounterValue"));
                            log.info("RolloverValue----->" + line.getAttribute("RolloverValue"));
                            aPIBasicInfoRequestBean.setRolloverValue6(line.getAttribute("RolloverValue"));
                            break;

                        case 710:
                            log.info("Sequence_No--->" + line.getAttribute("Sequence_No"));
                            aPIBasicInfoRequestBean.setSequence_No7(line.getAttribute("Sequence_No"));
//                            log.info("StartDate--->" + line.getAttribute("StartDate"));
//                            aPIBasicInfoRequestBean.setStartDate6(line.getAttribute("StartDate"));
                            log.info("ID----->" + line.getAttribute("ID"));
                            aPIBasicInfoRequestBean.setID7(line.getAttribute("ID"));
                            log.info("EndDate----->" + line.getAttribute("EndDate"));
                            aPIBasicInfoRequestBean.setEndDate7(line.getAttribute("EndDate"));
                            log.info("CounterValue----->" + line.getAttribute("CounterValue"));
                            aPIBasicInfoRequestBean.setCounterValue7(line.getAttribute("CounterValue"));
                            log.info("RolloverValue----->" + line.getAttribute("RolloverValue"));
                            aPIBasicInfoRequestBean.setRolloverValue7(line.getAttribute("RolloverValue"));
                            break;
                        default:

                    }

//                    if (line.getAttribute("ID").equals("610")) {
////                        log.info("Sequence_No--->" + line.getAttribute("Sequence_No"));
////                        aPIBasicInfoRequestBean.setSequence_No1(line.getAttribute("StartDate"));
//                        log.info("ID----->" + line.getAttribute("ID"));
//                        aPIBasicInfoRequestBean.setID1(line.getAttribute("ID"));
////                        log.info("EndDate----->" + line.getAttribute("EndDate"));
////                        aPIBasicInfoRequestBean.setEndDate1(line.getAttribute("EndDate"));
//                        log.info("CounterValue----->" + line.getAttribute("CounterValue"));
//                        aPIBasicInfoRequestBean.setCounterValue1(line.getAttribute("CounterValue"));
//                        log.info("RolloverValue----->" + line.getAttribute("RolloverValue"));
//                        aPIBasicInfoRequestBean.setRolloverValue1(line.getAttribute("RolloverValue"));
//                    }
//                    if (line.getAttribute("ID").equals("620")) {
//                        log.info("Sequence_No--->" + line.getAttribute("Sequence_No"));
////                        aPIBasicInfoRequestBean.setSequence_No2(line.getAttribute("StartDate"));
//                        log.info("ID----->" + line.getAttribute("ID"));
//                        aPIBasicInfoRequestBean.setID2(line.getAttribute("ID"));
////                        log.info("EndDate----->" + line.getAttribute("EndDate"));
////                        aPIBasicInfoRequestBean.setEndDate2(line.getAttribute("EndDate"));
//                        log.info("CounterValue----->" + line.getAttribute("CounterValue"));
//                        aPIBasicInfoRequestBean.setCounterValue2(line.getAttribute("CounterValue"));
//                        log.info("RolloverValue----->" + line.getAttribute("RolloverValue"));
//                        aPIBasicInfoRequestBean.setRolloverValue2(line.getAttribute("RolloverValue"));
//                    }
//
//                    if (line.getAttribute("ID").equals("720")) {
//                        log.info("Sequence_No--->" + line.getAttribute("Sequence_No"));
//                        aPIBasicInfoRequestBean.setSequence_No3(line.getAttribute("Sequence_No"));
//                        log.info("StartDate--->" + line.getAttribute("StartDate"));
//                        aPIBasicInfoRequestBean.setStartDate3(line.getAttribute("StartDate"));
//                        log.info("ID----->" + line.getAttribute("ID"));
//                        aPIBasicInfoRequestBean.setID3(line.getAttribute("ID"));
//                        log.info("EndDate----->" + line.getAttribute("EndDate"));
//                        aPIBasicInfoRequestBean.setEndDate3(line.getAttribute("EndDate"));
//                        log.info("CounterValue----->" + line.getAttribute("CounterValue"));
//                        aPIBasicInfoRequestBean.setCounterValue3(line.getAttribute("CounterValue"));
//                        log.info("RolloverValue----->" + line.getAttribute("RolloverValue"));
//                        aPIBasicInfoRequestBean.setRolloverValue3(line.getAttribute("RolloverValue"));
//                    }
//                    if (line.getAttribute("ID").equals("730")) {
//                        log.info("Sequence_No--->" + line.getAttribute("Sequence_No"));
//                        aPIBasicInfoRequestBean.setStartDate4(line.getAttribute("StartDate"));
//                        log.info("StartDate--->" + line.getAttribute("StartDate"));
//                        log.info("ID----->" + line.getAttribute("ID"));
//                        aPIBasicInfoRequestBean.setID4(line.getAttribute("ID"));
//                        log.info("EndDate----->" + line.getAttribute("EndDate"));
//                        aPIBasicInfoRequestBean.setEndDate4(line.getAttribute("EndDate"));
//                        log.info("CounterValue----->" + line.getAttribute("CounterValue"));
//                        aPIBasicInfoRequestBean.setCounterValue4(line.getAttribute("CounterValue"));
//                        log.info("RolloverValue----->" + line.getAttribute("RolloverValue"));
//                        aPIBasicInfoRequestBean.setRolloverValue4(line.getAttribute("RolloverValue"));
//                    }
//                    if (line.getAttribute("ID").equals("810")) {
//                        log.info("Sequence_No--->" + line.getAttribute("Sequence_No"));
//                        aPIBasicInfoRequestBean.setSequence_No5(line.getAttribute("Sequence_No"));
//                        log.info("StartDate--->" + line.getAttribute("StartDate"));
//                        aPIBasicInfoRequestBean.setStartDate5(line.getAttribute("StartDate"));
//                        log.info("ID----->" + line.getAttribute("ID"));
//                        aPIBasicInfoRequestBean.setID5(line.getAttribute("ID"));
//                        log.info("EndDate----->" + line.getAttribute("EndDate"));
//                        aPIBasicInfoRequestBean.setEndDate5(line.getAttribute("EndDate"));
//                        log.info("CounterValue----->" + line.getAttribute("CounterValue"));
//                        aPIBasicInfoRequestBean.setCounterValue5(line.getAttribute("CounterValue"));
//                        log.info("RolloverValue----->" + line.getAttribute("RolloverValue"));
//                        aPIBasicInfoRequestBean.setRolloverValue5(line.getAttribute("RolloverValue"));
//                    }
//                    if (line.getAttribute("ID").equals("820")) {
//                        log.info("Sequence_No--->" + line.getAttribute("Sequence_No"));
//                        aPIBasicInfoRequestBean.setSequence_No6(line.getAttribute("Sequence_No"));
//                        log.info("StartDate--->" + line.getAttribute("StartDate"));
//                        aPIBasicInfoRequestBean.setStartDate6(line.getAttribute("StartDate"));
//                        log.info("ID----->" + line.getAttribute("ID"));
//                        aPIBasicInfoRequestBean.setID6(line.getAttribute("ID"));
//                        log.info("EndDate----->" + line.getAttribute("EndDate"));
//                        aPIBasicInfoRequestBean.setEndDate6(line.getAttribute("EndDate"));
//                        log.info("CounterValue----->" + line.getAttribute("CounterValue"));
//                        aPIBasicInfoRequestBean.setCounterValue6(line.getAttribute("CounterValue"));
//                        log.info("RolloverValue----->" + line.getAttribute("RolloverValue"));
//                        aPIBasicInfoRequestBean.setRolloverValue6(line.getAttribute("RolloverValue"));
//                    }
                }
            }

        } catch (Exception e) {
//            log.info(e);
        }
        return aPIBasicInfoRequestBean;
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
}
