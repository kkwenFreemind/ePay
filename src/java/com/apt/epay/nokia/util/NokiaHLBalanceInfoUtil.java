/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.nokia.util;

import com.apt.epay.nokia.bean.NokiaResultBean;
import com.apt.epay.nokia.bean.NokiaSubscribeBalanceBean;
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
public class NokiaHLBalanceInfoUtil {

    private static final Logger log = Logger.getLogger("EPAY");

    public StringBuffer retnBasicInfoXML(String libm, String sessionId, String mdn) {
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
                        + "               <Name>Query Bundle</Name>\n"
                        + "               <QueryCriteria>\n"
                        + "                   <Param>\n"
                        + "                       <Name>Account ID</Name>\n"
                        + "                       <Value>" + mdn886 + "</Value>\n"
                        + "                   </Param>\n"
                        + "                   <Param>\n"
                        + "                       <Name>Account Type</Name>\n"
                        + "                       <Value>Subscriber</Value>\n"
                        + "                   </Param>\n"
                        + "                   <Param>\n"
                        + "                       <Name>Operation Type</Name>\n"
                        + "                       <Value>Query Bundle only</Value>\n"
                        + "                   </Param>\n"
                        + "	            </QueryCriteria>\n"
                        + "             </Task>\n"
                        + "         </TaskList>\n"
                        + "      </RetrieveRequest>\n"
                        + "   </soapenv:Body>\n"
                        + "</soapenv:Envelope>");

        log.info("NOKIA Query Bundle Request ==>" + sb.toString().replaceAll("[ \t\r\n]+", " ").trim());
        return sb;
    }

    public NokiaResultBean putNokiaBasicInfoOCSlet(String libm, String sessionId, String mdn) throws Exception {

        String PROXY_FLAG = new ShareParm().PARM_SCT_PROXY_FLAG;
        String sendURL = new ShareParm().PARM_NOKIA_HLAPI_URL;

        NokiaResultBean result;
        NokiaUtil nokiautil = new NokiaUtil();

        MimeHeaders mh = new MimeHeaders();
        StringBuffer BasicInfoXml = retnBasicInfoXML(libm, sessionId, mdn);

        RequestEntity body;
        body = new StringRequestEntity(BasicInfoXml.toString(), "text/xml", "utf-8");

        result = nokiautil.sendNokiaHttpPostMsg(body, sendURL);

        if (result.getHttpstatus() == HttpStatus.SC_OK) {
            String tmp_xml = result.getXmdrecord();
            if ("PROD".equalsIgnoreCase(PROXY_FLAG)) {
                tmp_xml = result.getXmdrecord().replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://10.108.17.36:8081/ecgs/dtd/gateway.dtd\">", "");
            } else {
                tmp_xml = result.getXmdrecord().replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://192.168.20.100:8081/ecgs/dtd/gateway.dtd\">", "");
            }
            result.setXmdrecord(tmp_xml);
        }
        return result;
    }

    public NokiaSubscribeBalanceBean parseNokiaXMLString(String xmlRecords) throws Exception {

        log.info("NOKIA BasicInfo Response ==>" + xmlRecords);

        NokiaSubscribeBalanceBean subbean = new NokiaSubscribeBalanceBean();

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

            //RecordList
            Element rootElement = doc.getDocumentElement();
            NodeList RecordList = rootElement.getElementsByTagName("ParamList");
            log.info("RecordList.getLength()===>" + RecordList.getLength());

            for (int i = 0; i < RecordList.getLength(); i++) {

                log.info("ParamList==>" + i);
                String bundle_name = "", state = "", startdate = "", enddate = "", cosp = "", discount1 = "", counter1 = "0.0", discount2 = "", counter2 = "0.0";

                Node ParamList = RecordList.item(i);
                NodeList properties = ParamList.getChildNodes();

                for (int j = 0; j < properties.getLength(); j++) {

                    Node property = properties.item(j);
                    NodeList parm = property.getChildNodes();

                    //log.info(property.getTextContent());
//                    log.info(parm.item(0).getTextContent() + "," + parm.item(1).getTextContent());
                    if ("Bundle ID".equalsIgnoreCase(parm.item(0).getTextContent())) {
                        bundle_name = parm.item(1).getTextContent();
                    }
                    if ("Bundle State".equalsIgnoreCase(parm.item(0).getTextContent())) {
                        state = parm.item(1).getTextContent();
                    }
                    if ("Start Date Time".equalsIgnoreCase(parm.item(0).getTextContent())) {
                        startdate = parm.item(1).getTextContent();
                    }
                    if ("End Date Time".equalsIgnoreCase(parm.item(0).getTextContent())) {
                        enddate = parm.item(1).getTextContent();
                    }
                    if ("Tariff Plan COSP ID".equalsIgnoreCase(parm.item(0).getTextContent())) {
                        cosp = parm.item(1).getTextContent();
                    }
                    if ("Bucket/Discount ID 1".equalsIgnoreCase(parm.item(0).getTextContent())) {
                        discount1 = parm.item(1).getTextContent();
                    }
                    if ("Bucket/UBD Counter 1".equalsIgnoreCase(parm.item(0).getTextContent())) {
                        counter1 = parm.item(1).getTextContent();
                    }
                    if ("Bucket/Discount ID 2".equalsIgnoreCase(parm.item(0).getTextContent())) {
                        discount2 = parm.item(1).getTextContent();
                    }
                    if ("Bucket/UBD Counter 2".equalsIgnoreCase(parm.item(0).getTextContent())) {
                        counter2 = parm.item(1).getTextContent();
                    }
                }
//                log.info("Bundle ID=>" + bundle_name + "," + state + "," + startdate + "," + cosp + "," + discount1 + "," + counter1);

                switch (bundle_name) {
                    case "APT_LC": //基本帳本虛擬
                        subbean.setAPT_LC_State(state);
                        subbean.setAPT_LC_StartDate(startdate);
                        subbean.setAPT_LC_EndDate(enddate);
                        break;
                    case "APT5GVoice": //通信費基本
                        subbean.setAPT5GVoice_650_State(state);
                        subbean.setAPT5GVoice_650_StartDate(startdate);
                        subbean.setAPT5GVoice_650_EndDate(enddate);
                        subbean.setAPT5GVoice_650_DiscountID(discount1);
                        subbean.setAPT5GVoice_650_Counter(counter1);
                        break;
                    case "APT5GVoice1": //通信費贈送(包括ZTE通信費贈送)
                        subbean.setAPT5GVoice1_660_State(state);
                        subbean.setAPT5GVoice1_660_StartDate(startdate);
                        subbean.setAPT5GVoice1_660_EndDate(enddate);
                        subbean.setAPT5GVoice1_660_DiscountID(discount1);
                        subbean.setAPT5GVoice1_660_Counter(counter1);
                        break;
                    case "APT5GVoice1Pro": //通信費贈送(元)-促案
                        subbean.setAPT5GVoice1Pro_66P_State(state);
                        subbean.setAPT5GVoice1Pro_66P_StartDate(startdate);
                        subbean.setAPT5GVoice1Pro_66P_EndDate(enddate);
                        subbean.setAPT5GVoice1Pro_66P_DiscountID(discount1);
                        subbean.setAPT5GVoice1Pro_66P_Counter(counter1);
                        break;
                    case "APT5GVoice2": //國内通信費贈送
                        subbean.setAPT5GVoice2_840_State(state);
                        subbean.setAPT5GVoice2_840_StartDate(startdate);
                        subbean.setAPT5GVoice2_840_EndDate(enddate);
                        subbean.setAPT5GVoice2_840_DiscountID(discount1);
                        subbean.setAPT5GVoice2_840_Counter(counter1);
                        break;
                    case "APT5GVoice2Pro": //通信贈送國內使用(元)-促案
                        subbean.setAPT5GVoice2Pro_84P_State(state);
                        subbean.setAPT5GVoice2Pro_84P_StartDate(startdate);
                        subbean.setAPT5GVoice2Pro_84P_EndDate(enddate);
                        subbean.setAPT5GVoice2Pro_84P_DiscountID(discount1);
                        subbean.setAPT5GVoice2Pro_84P_Counter(counter1);
                        break;
                    case "APT3GVoice": //3G通信費基本
                        subbean.setAPT3GVoice_652_State(state);
                        subbean.setAPT3GVoice_652_StartDate(startdate);
                        subbean.setAPT3GVoice_652_EndDate(enddate);
                        subbean.setAPT3GVoice_652_DiscountID(discount1);
                        subbean.setAPT3GVoice_652_Counter(counter1);
                        break;
                    case "APT3GVoice1": //3G通信費贈送
                        subbean.setAPT3GVoice1_662_State(state);
                        subbean.setAPT3GVoice1_662_StartDate(startdate);
                        subbean.setAPT3GVoice1_662_EndDate(enddate);
                        subbean.setAPT3GVoice1_662_DiscountID(discount1);
                        subbean.setAPT3GVoice1_662_Counter(counter1);
                        break;
                    case "APT5GIDD": //國際通信費基本
                        subbean.setAPT5GIDD_670_State(state);
                        subbean.setAPT5GIDD_670_StartDate(startdate);
                        subbean.setAPT5GIDD_670_EndDate(enddate);
                        subbean.setAPT5GIDD_670_DiscountID(discount1);
                        subbean.setAPT5GIDD_670_Counter(counter1);
                        break;
                    case "APT5GIDD1": //國際通信費贈送
                        subbean.setAPT5GIDD1_680_State(state);
                        subbean.setAPT5GIDD1_680_StartDate(startdate);
                        subbean.setAPT5GIDD1_680_EndDate(enddate);
                        subbean.setAPT5GIDD1_680_DiscountID(discount1);
                        subbean.setAPT5GIDD1_680_Counter(counter1);
                        break;
                    case "APT5GData": //計量數據基本
                        subbean.setAPT5GData_750_State(state);
                        subbean.setAPT5GData_750_StartDate(startdate);
                        subbean.setAPT5GData_750_EndDate(enddate);
                        subbean.setAPT5GData_750_DiscountID(discount1);
                        subbean.setAPT5GData_750_Counter(counter1);
                        break;
                    case "APT5GData1": //計量數據贈送
                        subbean.setAPT5GData1_760_State(state);
                        subbean.setAPT5GData1_760_StartDate(startdate);
                        subbean.setAPT5GData1_760_EndDate(enddate);
                        subbean.setAPT5GData1_760_DiscountID(discount1);
                        subbean.setAPT5GData1_760_Counter(counter1);
                        break;
                    case "APT5GData1Pro": //數據贈送(MB)促案
                        subbean.setAPT5GData1Pro_76P_State(state);
                        subbean.setAPT5GData1Pro_76P_StartDate(startdate);
                        subbean.setAPT5GData1Pro_76P_EndDate(enddate);
                        subbean.setAPT5GData1Pro_76P_DiscountID(discount1);
                        subbean.setAPT5GData1Pro_76P_Counter(counter1);
                        break;
                    case "APT5GDataFULUm": //計日型數據高速上網(FUL)不限量
                        subbean.setAPT5GDataFULUm_430_State(state);
                        subbean.setAPT5GDataFULUm_430_StartDate(startdate);
                        subbean.setAPT5GDataFULUm_430_EndDate(enddate);
                        subbean.setAPT5GDataFULUm_430_DiscountID(discount1);
                        subbean.setAPT5GDataFULUm_430_Counter(counter1);
                        break;
                    case "APT5GDataFULLm": // #### 計日型數據高速上網(FUL)限量G
                        subbean.setAPT5GDataFULLm_431_State(state);
                        subbean.setAPT5GDataFULLm_431_StartDate(startdate);
                        subbean.setAPT5GDataFULLm_431_EndDate(enddate);
                        subbean.setAPT5GDataFULLm_431_DiscountID(discount1);
                        subbean.setAPT5GDataFULLm_431_Counter(counter1);
                        subbean.setAPT5GDataFULLm_441_Counter(discount2);
                        subbean.setAPT5GDataFULLm_441_Counter(counter2);
                        break;
                    case "APT5GData12Mb": //計日型數據輕量型上網(12Mb)不限量
                        subbean.setAPT5GData12Mb_433_State(state);
                        subbean.setAPT5GData12Mb_433_StartDate(startdate);
                        subbean.setAPT5GData12Mb_433_EndDate(enddate);
                        subbean.setAPT5GData12Mb_433_DiscountID(discount1);
                        subbean.setAPT5GData12Mb_433_Counter(counter1);
                        break;
                    case "APT5GData12MbPr": //計日型輕量型上網(12Mb)-促案
                        subbean.setAPT5GData12MbPr_4P3_State(state);
                        subbean.setAPT5GData12MbPr_4P3_StartDate(startdate);
                        subbean.setAPT5GData12MbPr_4P3_EndDate(enddate);
                        subbean.setAPT5GData12MbPr_4P3_DiscountID(discount1);
                        subbean.setAPT5GData12MbPr_4P3_Counter(counter1);
                        break;
                    case "APT5GData12MbLm": // ##### 計日型數據輕量型上網(12Mb)限量G
                        subbean.setAPT5GData12MbLm_435_State(state);
                        subbean.setAPT5GData12MbLm_435_StartDate(startdate);
                        subbean.setAPT5GData12MbLm_435_EndDate(enddate);
                        subbean.setAPT5GData12MbLm_435_DiscountID(discount1);
                        subbean.setAPT5GData12MbLm_435_Counter(counter1);
                        subbean.setAPT5GData12MbLm_445_DiscountID(discount2);
                        subbean.setAPT5GData12MbLm_445_Counter(counter2);

                        break;
                    case "APT5GData12MbMt": // ##### 計日型數據輕量型上網(12Mb)限量G
                        subbean.setAPT5GData12MbMt_436_State(state);
                        subbean.setAPT5GData12MbMt_436_StartDate(startdate);
                        subbean.setAPT5GData12MbMt_436_EndDate(enddate);
                        subbean.setAPT5GData12MbMt_436_DiscountID(discount1);
                        subbean.setAPT5GData12MbMt_436_Counter(counter1);
                        subbean.setAPT5GData12MbMt_446_DiscountID(discount2);
                        subbean.setAPT5GData12MbMt_446_Counter(counter2);

                        break;
                    case "APT5GData8Mb": // #### 計日型數據低量型上網(8Mb)不限量
                        subbean.setAPT5GData8Mb_437_State(state);
                        subbean.setAPT5GData8Mb_437_StartDate(startdate);
                        subbean.setAPT5GData8Mb_437_EndDate(enddate);
                        subbean.setAPT5GData8Mb_437_DiscountID(discount1);
                        subbean.setAPT5GData8Mb_437_Counter(counter1);
                        break;
                    case "APT5GVoiceOnnet": //網內語音通話費
                        subbean.setAPT5GVoiceOnnet_830_State(state);
                        subbean.setAPT5GVoiceOnnet_830_StartDate(startdate);
                        subbean.setAPT5GVoiceOnnet_830_EndDate(enddate);
                        subbean.setAPT5GVoiceOnnet_830_DiscountID(discount1);
                        subbean.setAPT5GVoiceOnnet_830_Counter(counter1);
                        break;
                    case "APT5GVoiceOnPro": //網內語音通話費(元)-促案
                        subbean.setAPT5GVoiceOnPro_83P_State(state);
                        subbean.setAPT5GVoiceOnPro_83P_StartDate(startdate);
                        subbean.setAPT5GVoiceOnPro_83P_EndDate(enddate);
                        subbean.setAPT5GVoiceOnPro_83P_DiscountID(discount1);
                        subbean.setAPT5GVoiceOnPro_83P_Counter(counter1);
                        break;
                    case "APT5GVoiceOnUlm": //網內語音(分鐘)
                        subbean.setAPT5GVoiceOnUlm_831_State(state);
                        subbean.setAPT5GVoiceOnUlm_831_StartDate(startdate);
                        subbean.setAPT5GVoiceOnUlm_831_EndDate(enddate);
                        subbean.setAPT5GVoiceOnUlm_831_DiscountID(discount1);
                        subbean.setAPT5GVoiceOnUlm_831_Counter(counter1);
                        break;
                    case "RechargeCounter": //促案 031701
                        subbean.setRechargeCounter_RC1_State(state);
                        subbean.setRechargeCounter_RC1_StartDate(startdate);
                        subbean.setRechargeCounter_RC1_EndDate(enddate);
                        subbean.setRechargeCounter_RC1_DiscountID(discount1);
                        subbean.setRechargeCounter_RC1_Counter(counter1);
                        break;
                    case "5GShareLm": //共享數據限量 770
                        subbean.setShareLm5G_770_State(startdate);
                        subbean.setShareLm5G_770_StartDate(startdate);
                        subbean.setShareLm5G_770_EndDate(enddate);
                        subbean.setShareLm5G_770_DiscountID(discount1);
                        subbean.setShareLm5G_770_Counter(counter1);
                        break;
                    case "5GShareDt": // 共享數據每日限量 771
                        subbean.setShareDt5G_771_State(startdate);
                        subbean.setShareDt5G_771_StartDate(startdate);
                        subbean.setShareDt5G_771_EndDate(enddate);
                        subbean.setShareDt5G_771_DiscountID(discount1);
                        subbean.setShareDt5G_771_Counter(counter1);
                        break;
                    case "5GDataFUm": // 計日飆速無限上網(FUL) 530
                        subbean.setDataFUm5G_530_State(startdate);
                        subbean.setDataFUm5G_530_StartDate(startdate);
                        subbean.setDataFUm5G_530_EndDate(enddate);
                        subbean.setDataFUm5G_530_DiscountID(discount1);
                        subbean.setDataFUm5G_530_Counter(counter1);
                        break;
                    case "5GDataF21MbLm": // 計日型高速上網(FUL)限量GB/計日型中量無限上網(21Mb) 531 541
                        subbean.setDataF21MbLm5G_531_State(startdate);
                        subbean.setDataF21MbLm5G_531_StartDate(startdate);
                        subbean.setDataF21MbLm5G_531_EndDate(enddate);
                        subbean.setDataF21MbLm5G_531_DiscountID(discount1);
                        subbean.setDataF21MbLm5G_531_Counter(counter1);
                        subbean.setDataF21MbLm5G_541_DiscountID(discount2);
                        subbean.setDataF21MbLm5G_541_Counter(counter2);
                        break;

                    case "5GDataF12MbLm": // 計日型高速上網(FUL)限量GB/計日型中量無限上網(12Mb) 532 542
                        subbean.setDataF12MbLm5G_532_State(startdate);
                        subbean.setDataF12MbLm5G_532_StartDate(startdate);
                        subbean.setDataF12MbLm5G_532_EndDate(enddate);
                        subbean.setDataF12MbLm5G_532_DiscountID(discount1);
                        subbean.setDataF12MbLm5G_532_Counter(counter1);
                        subbean.setDataF12MbLm5G_542_DiscountID(discount2);
                        subbean.setDataF12MbLm5G_542_Counter(counter2);
                        break;
                    case "5G100Mb12MbLm": // 計日型高速上網(100Mb)限量GB/計日型中量無限上網(12Mb) 536 546
                        subbean.setOneHundredMb12MbLm5G_536_State(startdate);
                        subbean.setOneHundredMb12MbLm5G_536_StartDate(startdate);
                        subbean.setOneHundredMb12MbLm5G_536_EndDate(enddate);
                        subbean.setOneHundredMb12MbLm5G_536_DiscountID(discount1);
                        subbean.setOneHundredMb12MbLm5G_536_Counter(counter1);
                        subbean.setOneHundredMb12MbLm5G_546_DiscountID(discount2);
                        subbean.setOneHundredMb12MbLm5G_546_Counter(counter2);
                        break;
                    case "5G500Mb21MbLm": // 計日型高速上網(500Mb)限量GB/計日型中量無限上網(21Mb) 537 547
                        subbean.setFiveHundredMb21MbLm5G_537_State(startdate);
                        subbean.setFiveHundredMb21MbLm5G_537_StartDate(startdate);
                        subbean.setFiveHundredMb21MbLm5G_537_EndDate(enddate);
                        subbean.setFiveHundredMb21MbLm5G_537_DiscountID(discount1);
                        subbean.setFiveHundredMb21MbLm5G_537_Counter(counter1);
                        subbean.setFiveHundredMb21MbLm5G_547_DiscountID(discount2);
                        subbean.setFiveHundredMb21MbLm5G_547_Counter(counter2);
                        break;
                    case "5G1Gb21MbLm": // 計日型高速上網(1Gb)限量GB/計日型中量無限上網(21Mb) 538 548
                        subbean.setOneGb21MbLm5G_538_State(startdate);
                        subbean.setOneGb21MbLm5G_538_StartDate(startdate);
                        subbean.setOneGb21MbLm5G_538_EndDate(enddate);
                        subbean.setOneGb21MbLm5G_538_DiscountID(discount1);
                        subbean.setOneGb21MbLm5G_538_Counter(counter1);
                        subbean.setOneGb21MbLm5G_548_DiscountID(discount2);
                        subbean.setOneGb21MbLm5G_548_Counter(counter2);
                        break;
                    case "5GDataWiFi": // 熱點分享數據量(555)
                        subbean.setAPT5GDataWiFi_555_StartDate(startdate);
                        subbean.setAPT5GDataWiFi_555_EndDate(enddate);
                        subbean.setAPT5GDataWiFi_555_DiscountID(discount1);
                        subbean.setAPT5GDataWiFi_555_Counter(counter1);
                        subbean.setAPT5GDataWiFi_555_State(state);
                        break;
                    default:
                        continue;
                }

            }

            log.info("APT_LC=>" + subbean.getAPT_LC_State() + "," + subbean.getAPT_LC_StartDate() + "," + subbean.getAPT_LC_EndDate());
            log.info("APT5GVoice(650)=>" + subbean.getAPT5GVoice_650_State() + "," + subbean.getAPT5GVoice_650_StartDate() + "," + subbean.getAPT5GVoice_650_EndDate() + "," + subbean.getAPT5GVoice_650_DiscountID() + "," + subbean.getAPT5GVoice_650_Counter());
            log.info("APT5GVoice1(660)=>" + subbean.getAPT5GVoice1_660_State() + "," + subbean.getAPT5GVoice1_660_StartDate() + "," + subbean.getAPT5GVoice1_660_EndDate() + "," + subbean.getAPT5GVoice1_660_DiscountID() + "," + subbean.getAPT5GVoice1_660_Counter());
            log.info("APT5GVoice1Pro(66P)=>" + subbean.getAPT5GVoice1Pro_66P_State() + "," + subbean.getAPT5GVoice1Pro_66P_StartDate() + "," + subbean.getAPT5GVoice1Pro_66P_EndDate() + "," + subbean.getAPT5GVoice1Pro_66P_DiscountID() + "," + subbean.getAPT5GVoice1Pro_66P_Counter());

            log.info("APT5GVoice2(840)=>" + subbean.getAPT5GVoice2_840_State() + "," + subbean.getAPT5GVoice2_840_StartDate() + "," + subbean.getAPT5GVoice2_840_EndDate() + "," + subbean.getAPT5GVoice2_840_DiscountID() + "," + subbean.getAPT5GVoice2_840_Counter());
            log.info("APT5GVoice2Pro(84P)=>" + subbean.getAPT5GVoice2Pro_84P_State() + "," + subbean.getAPT5GVoice2Pro_84P_StartDate() + "," + subbean.getAPT5GVoice2Pro_84P_EndDate() + "," + subbean.getAPT5GVoice2Pro_84P_DiscountID() + "," + subbean.getAPT5GVoice2Pro_84P_Counter());

            log.info("APT3GVoice(652)=>" + subbean.getAPT3GVoice_652_State() + "," + subbean.getAPT3GVoice_652_StartDate() + "," + subbean.getAPT3GVoice_652_EndDate() + "," + subbean.getAPT3GVoice_652_DiscountID() + "," + subbean.getAPT3GVoice_652_Counter());
            log.info("APT3GVoice1(662)=>" + subbean.getAPT3GVoice1_662_State() + "," + subbean.getAPT3GVoice1_662_StartDate() + "," + subbean.getAPT3GVoice1_662_EndDate() + "," + subbean.getAPT3GVoice1_662_DiscountID() + "," + subbean.getAPT3GVoice1_662_Counter());
            log.info("APT5GIDD(670)=>" + subbean.getAPT5GIDD_670_State() + "," + subbean.getAPT5GIDD_670_StartDate() + "," + subbean.getAPT5GIDD_670_EndDate() + "," + subbean.getAPT5GIDD_670_DiscountID() + "," + subbean.getAPT5GIDD_670_Counter());
            log.info("APT5GIDD1(680)=>" + subbean.getAPT5GIDD1_680_State() + "," + subbean.getAPT5GIDD1_680_StartDate() + "," + subbean.getAPT5GIDD1_680_EndDate() + "," + subbean.getAPT5GIDD1_680_DiscountID() + "," + subbean.getAPT5GIDD1_680_Counter());

            log.info("APT5GData(750)=>" + subbean.getAPT5GData_750_State() + "," + subbean.getAPT5GData_750_StartDate() + "," + subbean.getAPT5GData_750_EndDate() + "," + subbean.getAPT5GData_750_DiscountID() + "," + subbean.getAPT5GData_750_Counter());
            log.info("APT5GData1(760)=>" + subbean.getAPT5GData1_760_State() + "," + subbean.getAPT5GData1_760_StartDate() + "," + subbean.getAPT5GData1_760_EndDate() + "," + subbean.getAPT5GData1_760_DiscountID() + "," + subbean.getAPT5GData1_760_Counter());
            log.info("APT5GData1Pro(76P)=>" + subbean.getAPT5GData1Pro_76P_State() + "," + subbean.getAPT5GData1Pro_76P_StartDate() + "," + subbean.getAPT5GData1Pro_76P_EndDate() + "," + subbean.getAPT5GData1Pro_76P_DiscountID() + "," + subbean.getAPT5GData1Pro_76P_Counter());

            log.info("APT5GDataFULUm(430)=>" + subbean.getAPT5GDataFULUm_430_State() + "," + subbean.getAPT5GDataFULUm_430_StartDate() + "," + subbean.getAPT5GDataFULUm_430_EndDate() + "," + subbean.getAPT5GDataFULUm_430_DiscountID() + "," + subbean.getAPT5GDataFULUm_430_Counter());
            log.info("APT5GDataFULLm(431)=>" + subbean.getAPT5GDataFULLm_431_State() + "," + subbean.getAPT5GDataFULLm_431_StartDate() + "," + subbean.getAPT5GDataFULLm_431_EndDate() + "," + subbean.getAPT5GDataFULLm_431_DiscountID() + "," + subbean.getAPT5GDataFULLm_431_Counter() + "," + subbean.getAPT5GDataFULLm_441_DiscountID() + "," + subbean.getAPT5GDataFULLm_441_Counter());
            log.info("APT5GData12Mb(433)=>" + subbean.getAPT5GData12Mb_433_State() + "," + subbean.getAPT5GData12Mb_433_StartDate() + "," + subbean.getAPT5GData12Mb_433_EndDate() + "," + subbean.getAPT5GData12Mb_433_DiscountID() + "," + subbean.getAPT5GData12Mb_433_Counter());
            log.info("APT5GData12MbPr(4P3)=>" + subbean.getAPT5GData12MbPr_4P3_State() + "," + subbean.getAPT5GData12MbPr_4P3_StartDate() + "," + subbean.getAPT5GData12MbPr_4P3_EndDate() + "," + subbean.getAPT5GData12MbPr_4P3_DiscountID() + "," + subbean.getAPT5GData12MbPr_4P3_Counter());
            log.info("APT5GData12MbLm(435)=>" + subbean.getAPT5GData12MbLm_435_State() + "," + subbean.getAPT5GData12MbLm_435_StartDate() + "," + subbean.getAPT5GData12MbLm_435_EndDate() + "," + subbean.getAPT5GData12MbLm_435_DiscountID() + "," + subbean.getAPT5GData12MbLm_435_Counter() + "," + subbean.getAPT5GData12MbLm_445_DiscountID() + "," + subbean.getAPT5GData12MbLm_445_Counter());
            log.info("APT5GData12MbMt(436)=>" + subbean.getAPT5GData12MbMt_436_State() + "," + subbean.getAPT5GData12MbMt_436_StartDate() + "," + subbean.getAPT5GData12MbMt_436_EndDate() + "," + subbean.getAPT5GData12MbMt_436_DiscountID() + "," + subbean.getAPT5GData12MbMt_436_Counter() + "," + subbean.getAPT5GData12MbMt_446_DiscountID() + "," + subbean.getAPT5GData12MbMt_446_Counter());
            log.info("APT5GData8Mb(437)=>" + subbean.getAPT5GData8Mb_437_State() + "," + subbean.getAPT5GData8Mb_437_StartDate() + "," + subbean.getAPT5GData8Mb_437_EndDate() + "," + subbean.getAPT5GData8Mb_437_DiscountID() + "," + subbean.getAPT5GData8Mb_437_Counter());

            log.info("APT5GVoiceOnnet(830)=>" + subbean.getAPT5GVoiceOnnet_830_State() + "," + subbean.getAPT5GVoiceOnnet_830_StartDate() + "," + subbean.getAPT5GVoiceOnnet_830_EndDate() + "," + subbean.getAPT5GVoiceOnnet_830_DiscountID() + "," + subbean.getAPT5GVoiceOnnet_830_Counter());
            log.info("APT5GVoiceOnPro(83P)=>" + subbean.getAPT5GVoiceOnPro_83P_State() + "," + subbean.getAPT5GVoiceOnPro_83P_StartDate() + "," + subbean.getAPT5GVoiceOnPro_83P_EndDate() + "," + subbean.getAPT5GVoiceOnPro_83P_DiscountID() + "," + subbean.getAPT5GVoiceOnPro_83P_Counter());
            log.info("APT5GVoiceOnUlm(831)=>" + subbean.getAPT5GVoiceOnUlm_831_State() + "," + subbean.getAPT5GVoiceOnUlm_831_StartDate() + "," + subbean.getAPT5GVoiceOnUlm_831_EndDate() + "," + subbean.getAPT5GVoiceOnUlm_831_DiscountID() + "," + subbean.getAPT5GVoiceOnUlm_831_Counter());

            log.info("RechargeCounter(RC1)=>" + subbean.getRechargeCounter_RC1_State() + "," + subbean.getRechargeCounter_RC1_StartDate() + "," + subbean.getRechargeCounter_RC1_EndDate() + "," + subbean.getRechargeCounter_RC1_DiscountID() + "," + subbean.getRechargeCounter_RC1_Counter());

            log.info("5GShareLm(770)=>" + subbean.getShareLm5G_770_State() + "," + subbean.getShareLm5G_770_StartDate() + "," + subbean.getShareLm5G_770_EndDate() + "," + subbean.getShareLm5G_770_DiscountID() + "," + subbean.getShareLm5G_770_Counter());
            log.info("5GShareDt(771)=>" + subbean.getShareDt5G_771_State() + "," + subbean.getShareDt5G_771_StartDate() + "," + subbean.getShareDt5G_771_EndDate() + "," + subbean.getShareDt5G_771_DiscountID() + "," + subbean.getShareDt5G_771_Counter());
            log.info("5GDataFUm(530)=>" + subbean.getDataFUm5G_530_State() + "," + subbean.getDataFUm5G_530_StartDate() + "," + subbean.getDataFUm5G_530_EndDate() + "," + subbean.getDataFUm5G_530_DiscountID() + "," + subbean.getDataFUm5G_530_Counter());
            log.info("5GDataF21MbLm(531,541)=>" + subbean.getDataF21MbLm5G_531_State() + "," + subbean.getDataF21MbLm5G_531_StartDate() + "," + subbean.getDataF21MbLm5G_531_EndDate() + "," + subbean.getDataF21MbLm5G_531_DiscountID() + "," + subbean.getDataF21MbLm5G_531_Counter() + "," + subbean.getDataF21MbLm5G_541_DiscountID() + "," + subbean.getDataF21MbLm5G_541_Counter());
            log.info("5GDataF12MbLm(532,542)=>" + subbean.getDataF12MbLm5G_532_State() + "," + subbean.getDataF12MbLm5G_532_StartDate() + "," + subbean.getDataF12MbLm5G_532_EndDate() + "," + subbean.getDataF12MbLm5G_532_DiscountID() + "," + subbean.getDataF12MbLm5G_532_Counter() + "," + subbean.getDataF12MbLm5G_542_DiscountID() + "," + subbean.getDataF12MbLm5G_542_Counter());

            log.info("5G100Mb12MbLm(536,546)=>" + subbean.getOneHundredMb12MbLm5G_536_State() + "," + subbean.getOneHundredMb12MbLm5G_536_StartDate() + "," + subbean.getOneHundredMb12MbLm5G_536_EndDate() + "," + subbean.getOneHundredMb12MbLm5G_536_DiscountID() + "," + subbean.getOneHundredMb12MbLm5G_536_Counter() + "," + subbean.getOneHundredMb12MbLm5G_546_DiscountID() + "," + subbean.getOneHundredMb12MbLm5G_546_Counter());
            log.info("5G500Mb21MbLm(537,547)=>" + subbean.getFiveHundredMb21MbLm5G_537_State() + "," + subbean.getFiveHundredMb21MbLm5G_537_StartDate() + "," + subbean.getFiveHundredMb21MbLm5G_537_EndDate() + "," + subbean.getFiveHundredMb21MbLm5G_537_DiscountID() + "," + subbean.getFiveHundredMb21MbLm5G_537_Counter() + "," + subbean.getFiveHundredMb21MbLm5G_547_DiscountID() + "," + subbean.getFiveHundredMb21MbLm5G_547_Counter());
            log.info("5G1Gb21MbLm(538,548)=>" + subbean.getOneGb21MbLm5G_538_State() + "," + subbean.getOneGb21MbLm5G_538_StartDate() + "," + subbean.getOneGb21MbLm5G_538_EndDate() + "," + subbean.getOneGb21MbLm5G_538_DiscountID() + "," + subbean.getOneGb21MbLm5G_538_Counter() + "," + subbean.getOneGb21MbLm5G_548_DiscountID() + "," + subbean.getOneGb21MbLm5G_548_Counter());

            log.info("5GDataWiFi(555)=>" + subbean.getAPT5GDataWiFi_555_State() + "," + subbean.getAPT5GDataWiFi_555_StartDate() + "," + subbean.getAPT5GDataWiFi_555_EndDate() + "," + subbean.getAPT5GDataWiFi_555_DiscountID() + "," + subbean.getAPT5GDataWiFi_555_Counter());

        } catch (Exception ex) {
            log.info(ex);
        }

        return subbean;
    }
}
