/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.nokia.util;

import com.apt.epay.nokia.bean.NokiaPricePlanResultBean;
import com.apt.epay.nokia.bean.NokiaResultBean;
import com.apt.epay.share.ShareParm;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
public class NokiaECGPricePlanCodeUtil {

    private static final Logger log = Logger.getLogger("EPAY");

    public static StringBuffer retnNokiaPricePlanCodeXML(String libm, String mdn, String priceplancode, String tradedate, int Rchg_Type) {

        //kk configure
        String ocs_systemid = new ShareParm().PARM_NOKIA_OCS_SYSTEM_ID;
        String ocs_system_pwd = new ShareParm().PARM_NOKIA_OCS_SYSTEM_PWD;

        String mdn886 = "886" + mdn.substring(1);
        StringBuffer sb = new StringBuffer();

        //change traddate format
        SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("MM/dd/yyyy.HH:mm:ss");
        Calendar nowDateTime = Calendar.getInstance();
        String nokia_tradedate = sdf15.format(nowDateTime.getTime());

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n"
                        + "<!DOCTYPE GatewayRequest SYSTEM \"http://10.108.20.44:8081/ecgs/dtd/gateway.dtd\">\n"
                        + "<GatewayRequest>\n"
                        + "    <RequestHeader \n"
                        + "    	version=\"203\" \n"
                        + "       requesting_system_id=\"" + ocs_systemid + "\"\n"
                        + "    requesting_system_pwd=\"" + ocs_system_pwd + "\"\n"
                        + "       request_tid=\"" + libm + "\"\n"
                        + "    	request_timestamp=\"" + nokia_tradedate + "\"\n"
                        + "		additional_info=\"Recharge\">\n"
                        + "	</RequestHeader>    \n"
                        + "	  <SubscriberAccountInfo account_code=\"4000\" balance_type=\"primary\">\n"
                        + "        <SubscriberID SubscriberIDType=\"00\">" + mdn886 + "</SubscriberID>\n"
                        + "    </SubscriberAccountInfo>\n"
                        + "    <QueryDataRequest  OP=\"A\" Action=\"IMOM\" IMOMCommand=\"ADJ:BALANCE,MSISDN=" + mdn886
                        + ",TRANS_ID=" + priceplancode + ",AMOUNT=0,ADJ=INCR,BAL=P,NO_LC=N,RECHARGE=Y,Rchg_Type=" + Rchg_Type
                        + ",REF1=" + libm.substring(3, 15) + "\"/>\n"
                        + "</GatewayRequest>");
        log.info(mdn + " NOKIA PricePlanCode Request==>" + sb.toString().replaceAll("[ \t\r\n]+", " ").trim());
        return sb;
    }

    public NokiaResultBean putNokiaPricePlanOCSlet(String libm, String mdn, String serviceId, String tradedate, int Rchg_Type) throws Exception {

        String PROXY_FLAG = new ShareParm().PARM_SCT_PROXY_FLAG;
        String sendURL = new ShareParm().PARM_NOKIA_OCS_URL;

        NokiaResultBean result = new NokiaResultBean();
        NokiaUtil nokiautil = new NokiaUtil();

        MimeHeaders mh = new MimeHeaders();
        StringBuffer PricePlanCodeXml = retnNokiaPricePlanCodeXML(libm, mdn, serviceId, tradedate, Rchg_Type);

        log.info(mdn + " Nokia putNokiaPricePlanOCSlet (MDN:" + mdn + " Nokia OCS XML Request)==>" + PricePlanCodeXml.toString().replaceAll("[ \t\r\n]+", " ").trim());
        log.info(mdn + " Nokia PARM_NOKIA_OCS_URL==>" + sendURL);

        RequestEntity body;
        body = new StringRequestEntity(PricePlanCodeXml.toString(), "text/xml", "utf-8");

        result = nokiautil.sendNokiaHttpPostMsg(body, sendURL);

        if (result.getHttpstatus() == HttpStatus.SC_OK) {
            String tmp_xml;// = result.getXmdrecord();
            if ("PROD".equalsIgnoreCase(PROXY_FLAG)) {
                tmp_xml = result.getXmdrecord().replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://10.108.17.36:8081/ecgs/dtd/gateway.dtd\">", "");
            } else {
                tmp_xml = result.getXmdrecord().replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://192.168.20.100:8081/ecgs/dtd/gateway.dtd\">", "");
            }
            result.setXmdrecord(tmp_xml);
            log.info("NOKIA PricePlanCode Response ==>" + tmp_xml);
        }

        return result;
    }

    public NokiaPricePlanResultBean parseNokiaXMLString(String xmlRecords) {

        NokiaPricePlanResultBean bean = new NokiaPricePlanResultBean();

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
                    System.out.println("lineA.getAttribute(status)=====>" + lineA.getAttribute("status"));
                    System.out.println("lineA.getAttribute(result_code)=====>" + lineA.getAttribute("result_code"));
                }
            }

            //RecordList
            Element rootElement = doc.getDocumentElement();
            NodeList RecordList = rootElement.getElementsByTagName("Data");
            System.out.println("RecordList.getLength()===>" + RecordList.getLength());

            for (int i = 0; i < RecordList.getLength(); i++) {

                Node ParamList = RecordList.item(i);

                if (ParamList.getTextContent().contains("REASON")) {
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
