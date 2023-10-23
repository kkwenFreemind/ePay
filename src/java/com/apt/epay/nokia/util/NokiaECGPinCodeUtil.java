/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.nokia.util;

import com.apt.epay.nokia.bean.NokiaPincodeResultBean;
import com.apt.epay.nokia.bean.NokiaResultBean;
import com.apt.epay.share.ShareParm;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MimeHeaders;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author kevinchang
 */
public class NokiaECGPinCodeUtil {

    private static final Logger log = Logger.getLogger("EPAY");

    public NokiaResultBean putNokiaPincodeOCSlet(String libm, String mdn, String pincode, String tradedate) throws Exception {

        //kk configure
        String PROXY_FLAG = new ShareParm().PARM_SCT_PROXY_FLAG;
        String sendURL = new ShareParm().PARM_NOKIA_OCS_URL;

        NokiaResultBean result;
        NokiaUtil nokiautil = new NokiaUtil();

        MimeHeaders mh = new MimeHeaders();
        StringBuffer pincodeXml = retnNokiaPinCodeXML(libm, mdn, pincode, tradedate);

        RequestEntity body;
        body = new StringRequestEntity(pincodeXml.toString(), "text/xml", "utf-8");

        result = nokiautil.sendNokiaHttpPostMsg(body, sendURL);

        if (result.getHttpstatus() == HttpStatus.SC_OK) {
            String tmp_xml;// = result.getXmdrecord();
            if ("PROD".equalsIgnoreCase(PROXY_FLAG)) {
                tmp_xml = result.getXmdrecord().replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://10.108.17.36:8081/ecgs/dtd/gateway.dtd\">", "");
            } else {
                tmp_xml = result.getXmdrecord().replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://192.168.20.100:8081/ecgs/dtd/gateway.dtd\">", "");
            }
            result.setXmdrecord(tmp_xml);
            log.info("NOKIA PinCode Response ==>" + tmp_xml);
        }
        return result;
    }

    public StringBuffer retnNokiaPinCodeXML(String libm, String mdn, String pincode, String tradedate) {

        //kk configure
        String ocs_systemid = new ShareParm().PARM_NOKIA_OCS_SYSTEM_ID;
        String ocs_system_pwd = new ShareParm().PARM_NOKIA_OCS_SYSTEM_PWD;

        //change traddate format
        SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("MM/dd/yyyy.HH:mm:ss");
        Calendar nowDateTime = Calendar.getInstance();
        String nokia_tradedate = sdf15.format(nowDateTime.getTime());

        String mdn886 = "886" + mdn.substring(1);
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n"
                        + "<!DOCTYPE GatewayRequest SYSTEM \"http://10.108.20.44:8081/ecgs/dtd/gateway.dtd\">\n"
                        + "<GatewayRequest>\n"
                        + "<RequestHeader\n"
                        + "version=\"203\"\n"
                        + "requesting_system_id=\"" + ocs_systemid + "\"\n"
                        + "requesting_system_pwd=\"" + ocs_system_pwd + "\"\n"
                        + "request_tid=\"" + libm + "\"\n"
                        + "request_timestamp=\"" + nokia_tradedate + "\"\n"
                        + "additional_info=\"999999\">\n"
                        + "</RequestHeader>\n"
                        + "<SubscriberAccountInfo account_code=\"4000\" balance_type=\"primary\">\n"
                        + "<SubscriberID SubscriberIDType=\"00\">" + mdn886 + "</SubscriberID>\n"
                        + "</SubscriberAccountInfo>\n"
                        + "<QueryDataRequest\n"
                        + "OP=\"A\"\n"
                        + "Action=\"IMOM\"\n"
                        + "IMOMCommand=\"ADJ:SCRRECHARGE,MSISDN=" + mdn886 + ",ID=" + pincode
                        + ",TRANS_ID=" + libm.substring(3, 15) + "\"\n"
                        + ">\n"
                        + "</QueryDataRequest>\n"
                        + "</GatewayRequest>");

        log.info(mdn + " NOKIA Pincode Request ==>" + sb.toString().replaceAll("[ \t\r\n]+", " ").trim());

        return sb;
    }

    public NokiaPincodeResultBean parseNokiaXMLString(String xmlRecords) {

        NokiaPincodeResultBean bean = new NokiaPincodeResultBean();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));

            Document doc = db.parse(is);

            NodeList Res = doc.getElementsByTagName("ResponseHeader");
            for (int index = 0; index < Res.getLength(); index++) {
                Node NodeA = Res.item(index);
                if (NodeA.getNodeType() == Node.ELEMENT_NODE) {
                    Element lineA = (Element) NodeA;
                    bean.setStatus(lineA.getAttribute("status"));
                    bean.setResult_code(lineA.getAttribute("result_code"));
//                    System.out.println("NOKIA PinCode Respnse(status)=====>" + lineA.getAttribute("status"));
//                    System.out.println("NOKIA PinCode Respnse(result_code)=====>" + lineA.getAttribute("result_code"));
                }
            }

            //RecordList
            Element rootElement = doc.getDocumentElement();
            NodeList RecordList = rootElement.getElementsByTagName("Data");
            System.out.println("RecordList.getLength()===>" + RecordList.getLength());

            for (int i = 0; i < RecordList.getLength(); i++) {

                 Node ParamList = RecordList.item(i);
                 
                 if(ParamList.getTextContent().contains("REASON")){
                     bean.setReason(ParamList.getTextContent());
//                     System.out.println(ParamList.getTextContent());
                 }
            }

        } catch (Exception ex) {
            log.info(ex);
        }

        return bean;
    }
}
