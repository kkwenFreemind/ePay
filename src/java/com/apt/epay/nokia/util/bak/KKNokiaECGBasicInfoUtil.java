/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.nokia.util.bak;

import com.apt.epay.nokia.bean.NokiaBasicInfoReqBean;
import com.apt.epay.nokia.bean.NokiaResultBean;
import com.apt.epay.nokia.util.NokiaUtil;
import com.apt.epay.share.ShareParm;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
import org.xml.sax.SAXException;

/**
 *
 * @author kevinchang
 */
public class KKNokiaECGBasicInfoUtil {

    private static final Logger log = Logger.getLogger("EPAY");

    public static NokiaResultBean putNokiaOCSBasicInfoSlet(String ocs_systemid, String ocs_system_pwd, String sendURL, String libm, String mdn, String tradedate) throws Exception {
        NokiaResultBean result = new NokiaResultBean();
        NokiaUtil nokiautil = new NokiaUtil();

//        String ocs_systemid = "123456";
//        String ocs_system_pwd = "123456";
//        String sendURL = "http://localhost:1234/gateway_servlet/gateway";
        MimeHeaders mh = new MimeHeaders();
        StringBuffer BasicInfoXml = retnNokiaOCSXML(ocs_systemid, ocs_system_pwd, libm, mdn, tradedate);

        RequestEntity body;
        body = new StringRequestEntity(BasicInfoXml.toString(), "text/xml", "utf-8");
        result = nokiautil.sendNokiaHttpPostMsg(body, sendURL);
//        String PROXY_FLAG = new ShareParm().PARM_SCT_PROXY_FLAG;
//        String PROXY_FLAG = "PROD";
//        if ("PROD".equals(PROXY_FLAG)) {
//            result = nokiautil.sendNokiaHttpPostMsg(body, sendURL);//.replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://10.108.17.36:8081/ecgs/dtd/gateway.dtd\">","");
//        } else {
//            result = nokiautil.sendNokiaHttpPostMsg(body, sendURL);//.replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://192.168.20.100:8081/ecgs/dtd/gateway.dtd\">", "");
//        }

//        System.out.println("Nokia Query Account Info Result==>" + result);
        return result;

    }

    public static StringBuffer retnNokiaOCSXML(String system_id, String system_pwd, String libm, String mdn, String tradedate) {
        String mdn886 = "886" + mdn.substring(1);
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n"
                        + "<!DOCTYPE GatewayRequest SYSTEM \"http://10.107.192.15:8081/ecgs/dtd/gateway.dtd\">\n"
                        + "<GatewayRequest>\n"
                        + "    <RequestHeader\n"
                        + "    version=\"203\"\n"
                        + "    requesting_system_id=\"" + system_id + "\"\n"
                        + "    requesting_system_pwd=\"" + system_pwd + "\"\n"
                        + "    request_tid=\"" + libm + "\"\n"
                        + "    request_timestamp=\"" + tradedate + "\"\n"
                        + "    additional_info=\"Query Account Info\">\n"
                        + "</RequestHeader>\n"
                        + "    <SubscriberAccountInfo account_code=\"4000\" balance_type=\"primary\">\n"
                        + "        <SubscriberID SubscriberIDType=\"00\">" + mdn886 + "</SubscriberID>\n"
                        + "    </SubscriberAccountInfo>\n"
                        + "    <BundleSubscriptionRequest QueryID=\"ALL\"  RequestIndex=\"1\">\n"
                        + "</BundleSubscriptionRequest>\n"
                        + "</GatewayRequest>");
        return sb;
    }

    public static NokiaBasicInfoReqBean parseNokiaBasicInfoXMLString(String xmlRecords) throws Exception {
//        System.out.println("Nokia BasicInfo xmlRecords==>" + xmlRecords);

        NokiaBasicInfoReqBean aPIBasicInfoRequestBean = new NokiaBasicInfoReqBean();

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
                    System.out.println("lineA.getAttribute(result_code)=====>" + kklineA.getAttribute("result_code"));
                }
            }

            NodeList BasicInfoResA = doc.getElementsByTagName("BundleSubscriptionResponse");
            for (int index = 0; index < BasicInfoResA.getLength(); index++) {
                Node nNodeA = BasicInfoResA.item(index);
                if (nNodeA.getNodeType() == Node.ELEMENT_NODE) {
                    Element lineA = (Element) nNodeA;
                    //aPIBasicInfoRequestBean.setDefaultTariffPlanCOSPID(lineA.getAttribute("DefaultTariffPlanCOSPID"));
                    aPIBasicInfoRequestBean.setDefaultTariffPlanCOSPID(lineA.getAttribute("COSPID"));
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
                        System.out.println("line.getAttribute(ID)==>" + line.getAttribute("ID"));
                        if ((tmp_PromotionalTariffPlanCOSP_ID.substring(0, 5)).equals("VOLVB")) {
                            aPIBasicInfoRequestBean.setPromotionalTariffPlanCOSP_ID(tmp_PromotionalTariffPlanCOSP_ID);
                            System.out.println("PromotionalTariffPlanCOSP_ID====>" + aPIBasicInfoRequestBean.getPromotionalTariffPlanCOSP_ID());
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
                    System.out.println("k1===>" + k1);

                    switch (kk) {
                        case 650: //610
//                        log.info("Sequence_No--->" + line.getAttribute("Sequence_No"));
//                        aPIBasicInfoRequestBean.setSequence_No1(line.getAttribute("StartDate"));
                            System.out.println("ID----->" + line.getAttribute("ID"));
                            aPIBasicInfoRequestBean.setID1(line.getAttribute("ID"));
//                        log.info("EndDate----->" + line.getAttribute("EndDate"));
//                        aPIBasicInfoRequestBean.setEndDate1(line.getAttribute("EndDate"));
                            System.out.println("CounterValue----->" + line.getAttribute("CounterValue"));
                            aPIBasicInfoRequestBean.setCounterValue1(line.getAttribute("CounterValue"));
                            System.out.println("RolloverValue----->" + line.getAttribute("RolloverValue"));
                            aPIBasicInfoRequestBean.setRolloverValue1(line.getAttribute("RolloverValue"));
                            break;

                        case 660: //620
                            System.out.println("Sequence_No--->" + line.getAttribute("Sequence_No"));
//                        aPIBasicInfoRequestBean.setSequence_No2(line.getAttribute("StartDate"));
                            System.out.println("ID----->" + line.getAttribute("ID"));
                            aPIBasicInfoRequestBean.setID2(line.getAttribute("ID"));
//                        log.info("EndDate----->" + line.getAttribute("EndDate"));
//                        aPIBasicInfoRequestBean.setEndDate2(line.getAttribute("EndDate"));
                            System.out.println("CounterValue----->" + line.getAttribute("CounterValue"));
                            aPIBasicInfoRequestBean.setCounterValue2(line.getAttribute("CounterValue"));
                            System.out.println("RolloverValue----->" + line.getAttribute("RolloverValue"));
                            aPIBasicInfoRequestBean.setRolloverValue2(line.getAttribute("RolloverValue"));
                            break;

                        case 750: //720
                            System.out.println("Sequence_No--->" + line.getAttribute("Sequence_No"));
                            aPIBasicInfoRequestBean.setSequence_No3(line.getAttribute("Sequence_No"));
                            System.out.println("StartDate--->" + line.getAttribute("StartDate"));
                            aPIBasicInfoRequestBean.setStartDate3(line.getAttribute("StartDate"));
                            System.out.println("ID----->" + line.getAttribute("ID"));
                            aPIBasicInfoRequestBean.setID3(line.getAttribute("ID"));
                            System.out.println("EndDate----->" + line.getAttribute("EndDate"));
                            aPIBasicInfoRequestBean.setEndDate3(line.getAttribute("EndDate"));
                            System.out.println("CounterValue----->" + line.getAttribute("CounterValue"));
                            aPIBasicInfoRequestBean.setCounterValue3(line.getAttribute("CounterValue"));
                            System.out.println("RolloverValue----->" + line.getAttribute("RolloverValue"));
                            aPIBasicInfoRequestBean.setRolloverValue3(line.getAttribute("RolloverValue"));
                            break;

                        case 760: //730
                            System.out.println("Sequence_No--->" + line.getAttribute("Sequence_No"));
                            aPIBasicInfoRequestBean.setStartDate4(line.getAttribute("StartDate"));
                            System.out.println("StartDate--->" + line.getAttribute("StartDate"));
                            System.out.println("ID----->" + line.getAttribute("ID"));
                            aPIBasicInfoRequestBean.setID4(line.getAttribute("ID"));
                            System.out.println("EndDate----->" + line.getAttribute("EndDate"));
                            aPIBasicInfoRequestBean.setEndDate4(line.getAttribute("EndDate"));
                            System.out.println("CounterValue----->" + line.getAttribute("CounterValue"));
                            aPIBasicInfoRequestBean.setCounterValue4(line.getAttribute("CounterValue"));
                            System.out.println("RolloverValue----->" + line.getAttribute("RolloverValue"));
                            aPIBasicInfoRequestBean.setRolloverValue4(line.getAttribute("RolloverValue"));
                            break;

                        case 830: //810
                            System.out.println("Sequence_No--->" + line.getAttribute("Sequence_No"));
                            aPIBasicInfoRequestBean.setSequence_No5(line.getAttribute("Sequence_No"));
//                            log.info("StartDate--->" + line.getAttribute("StartDate"));
//                            aPIBasicInfoRequestBean.setStartDate5(line.getAttribute("StartDate"));
                            System.out.println("ID----->" + line.getAttribute("ID"));
                            aPIBasicInfoRequestBean.setID5(line.getAttribute("ID"));
                            System.out.println("EndDate----->" + line.getAttribute("EndDate"));
                            aPIBasicInfoRequestBean.setEndDate5(line.getAttribute("EndDate"));
                            System.out.println("CounterValue----->" + line.getAttribute("CounterValue"));
                            aPIBasicInfoRequestBean.setCounterValue5(line.getAttribute("CounterValue"));
                            System.out.println("RolloverValue----->" + line.getAttribute("RolloverValue"));
                            aPIBasicInfoRequestBean.setRolloverValue5(line.getAttribute("RolloverValue"));
                            break;

                        case 820:
                            System.out.println("Sequence_No--->" + line.getAttribute("Sequence_No"));
                            aPIBasicInfoRequestBean.setSequence_No6(line.getAttribute("Sequence_No"));
                            System.out.println("ID----->" + line.getAttribute("ID"));
                            aPIBasicInfoRequestBean.setID6(line.getAttribute("ID"));
                            System.out.println("EndDate----->" + line.getAttribute("EndDate"));
                            aPIBasicInfoRequestBean.setEndDate6(line.getAttribute("EndDate"));
                            System.out.println("CounterValue----->" + line.getAttribute("CounterValue"));
                            aPIBasicInfoRequestBean.setCounterValue6(line.getAttribute("CounterValue"));
                            System.out.println("RolloverValue----->" + line.getAttribute("RolloverValue"));
                            aPIBasicInfoRequestBean.setRolloverValue6(line.getAttribute("RolloverValue"));
                            
//                            log.info("StartDate--->" + line.getAttribute("StartDate"));
//                            aPIBasicInfoRequestBean.setStartDate6(line.getAttribute("StartDate"));
                            break;

                        case 710:
                            System.out.println("Sequence_No--->" + line.getAttribute("Sequence_No"));
                            aPIBasicInfoRequestBean.setSequence_No7(line.getAttribute("Sequence_No"));
//                            log.info("StartDate--->" + line.getAttribute("StartDate"));
//                            aPIBasicInfoRequestBean.setStartDate6(line.getAttribute("StartDate"));
                            System.out.println("ID----->" + line.getAttribute("ID"));
                            aPIBasicInfoRequestBean.setID7(line.getAttribute("ID"));
                            System.out.println("EndDate----->" + line.getAttribute("EndDate"));
                            aPIBasicInfoRequestBean.setEndDate7(line.getAttribute("EndDate"));
                            System.out.println("CounterValue----->" + line.getAttribute("CounterValue"));
                            aPIBasicInfoRequestBean.setCounterValue7(line.getAttribute("CounterValue"));
                            System.out.println("RolloverValue----->" + line.getAttribute("RolloverValue"));
                            aPIBasicInfoRequestBean.setRolloverValue7(line.getAttribute("RolloverValue"));
                            break;
                        default:
                    }
                }
            }

        } catch (IOException | NumberFormatException | ParserConfigurationException | SAXException e) {
            log.info(e);
        }
        return aPIBasicInfoRequestBean;
    }

    public static void main(String[] args) {

        //        String ocs_systemid = new ShareParm().NOKIA_OCS_SYSTEM_ID;
        //        String ocs_system_pwd = new ShareParm().NOKIA_OCS_SYSTEM_PWD;
        //        String sendURL = new ShareParm().PARM_NOKIA_OCS_URL;
        //PROD
//        String ocs_systemid = "46605002";
//        String ocs_system_pwd = "Apt20050664";
//        String sendURL = "http://localhost:4312/gateway_servlet/gateway";
//        String contactCellPhone = "0906400314";
        //906400309  Terminated

        //TDE
//        String ocs_systemid = "123456";
//        String ocs_system_pwd = "123456";
//        String sendURL = "http://localhost:1234/gateway_servlet/gateway";
//        String contactCellPhone = "0906001002";
//        String libm = "223456";
//
//        NokiaUtil nokiautil = new NokiaUtil();
//
//        Calendar nowDateTime = Calendar.getInstance();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String tradeDate = sdf.format(nowDateTime.getTime());
//        SimpleDateFormat sdf_pincode = new SimpleDateFormat("MM/dd/yyyy.HH:mm:ss");
//        String kktradeDate = sdf_pincode.format(nowDateTime.getTime());
//        try {
//            NokiaResultBean result = putNokiaOCSBasicInfoSlet(ocs_systemid, ocs_system_pwd, sendURL, libm, contactCellPhone, kktradeDate);
//            if (result.getHttpstatus() == HttpStatus.SC_OK) {
//
//                String result_format = result.getXmdrecord().replaceAll("[ \t\r\n]+", " ").trim();
//                System.out.println(result_format);
//
//                NokiaBasicInfoReqBean basicinforeqbean = new NokiaBasicInfoReqBean();
//                basicinforeqbean = parseNokiaBasicInfoXMLString(result.getXmdrecord());
//
//                if ("00".equalsIgnoreCase(basicinforeqbean.getResultcode())) {
//                    System.out.println("result==>" + result.getHttpstatus());
//                    System.out.println("result==>" + result.getXmdrecord());                    
//                    System.out.println(basicinforeqbean.getLifeCycleState());
//                } else {
//                    System.out.println("result==>" + result.getHttpstatus());
//                    System.out.println("result==>" + result.getXmdrecord());
//                }
//            } else {
//                System.out.println("result==>" + result.getHttpstatus());
//                System.out.println("result==>" + result.getXmdrecord());
//            }
//        } catch (Exception ex) {
//            System.out.println(ex);
//        }
    }
}
