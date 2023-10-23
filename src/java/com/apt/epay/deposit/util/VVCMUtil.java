/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.deposit.util;

import com.apt.epay.deposit.bean.ServiceOrderReqBean;
import com.apt.epay.share.ShareParm;
import com.apt.epay.zte.bean.VVCMResultBean;
import static com.apt.util.AdjustBucket.retnChangeServiceExpireDateXML;
import static com.apt.util.AdjustBucket.sendHttpPostMsg;
import com.apt.util.BundleDateUtil;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
public class VVCMUtil {

    private final Logger log;

    public VVCMUtil() {
        this.log = Logger.getLogger("EPAY");
    }

    public String putVVCMlet(String mdn, String storeid, String cardid) throws Exception {

        String result = "";
//        String sendURL = new ShareParm().PARM_4GOCS_URL;
        String sendURL = "http://10.31.80.210:8080/EPAY/APITest";

        MimeHeaders mh = new MimeHeaders();
        StringBuffer bucketXml = QueryVVMCXML(mdn, storeid, cardid);

        log.info(bucketXml);
        log.info("sendURL==>" + sendURL);

        RequestEntity body;
        body = new StringRequestEntity(bucketXml.toString(), "text/xml", "utf-8");

        result = sendHttpPostMsg(body, sendURL);
        log.info("putVVCMlet Result==>" + result);

        return result;
    }

    public static StringBuffer QueryVVMCXML(String mdn, String storeid, String cardid) {

        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
        sb.append("<!DOCTYPE GatewayRequest SYSTEM \"http://10.108.20.44:8081/ecgs/dtd/gateway.dtd\">");
        sb.append("<VVCMInfoRequest>");
        sb.append("<VOUCHERCARD>" + cardid + "</VOUCHERCARD>");
        sb.append("<STOREID>" + storeid + "</STOREID>");
        sb.append("<MDN>" + mdn + "</MDN>");
        sb.append("</VOUCHERCARD>");
        sb.append("</VVCMInfoRequest>");
        return sb;
    }

    public VVCMResultBean parseXMLString(String xmlRecords, String TagName) throws Exception {

        VVCMResultBean aPIVVCMresultbean = new VVCMResultBean();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));

            Document doc = db.parse(is);
            NodeList SOARes = doc.getElementsByTagName(TagName);

            for (int i = 0; i < SOARes.getLength(); i++) {

                Element element = (Element) SOARes.item(i);

                NodeList nodes = element.getElementsByTagName("FLAG");
                Element line = (Element) nodes.item(0);
                aPIVVCMresultbean.setVvcm_flag(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("STATUS_DESC");
                line = (Element) nodes.item(0);
                aPIVVCMresultbean.setVvcm_status_desc(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("STATUS");
                line = (Element) nodes.item(0);
                aPIVVCMresultbean.setVvcm_status(getCharacterDataFromElement(line));

            }

        } catch (Exception e) {
            log.info(e);
        }
        return aPIVVCMresultbean;
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
}
