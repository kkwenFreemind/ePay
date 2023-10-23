/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.deposit.util;

import com.apt.epay.deposit.bean.ServiceOrderReqBean;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
public class OrderProfile {

    private static final Logger log = Logger.getLogger("EPAY");
    ServiceOrderReqBean aPIServiceOrdrReqBean = new ServiceOrderReqBean();

    public ServiceOrderReqBean parseXMLString(String xmlRecords, String TagName) throws Exception {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));

            Document doc = db.parse(is);
            NodeList SOARes = doc.getElementsByTagName(TagName);

            for (int i = 0; i < SOARes.getLength(); i++) {

                Element element = (Element) SOARes.item(i);

                NodeList nodes = element.getElementsByTagName("MDN");
                Element line = (Element) nodes.item(0);
                aPIServiceOrdrReqBean.setMdn(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("CPID");
                line = (Element) nodes.item(0);
                aPIServiceOrdrReqBean.setChannelID(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("SERVICEID");
                line = (Element) nodes.item(0);
                aPIServiceOrdrReqBean.setServiceID(getCharacterDataFromElement(line));

//                nodes = element.getElementsByTagName("SERVICENAME");
//                line = (Element) nodes.item(0);
//                aPIServiceOrdrReqBean.setServiceName(getCharacterDataFromElement(line));
                nodes = element.getElementsByTagName("TOKENID");
                line = (Element) nodes.item(0);
                aPIServiceOrdrReqBean.setTokenId(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("PRICE");
                line = (Element) nodes.item(0);
                aPIServiceOrdrReqBean.setPrice(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("CPORDERID");
                line = (Element) nodes.item(0);
                aPIServiceOrdrReqBean.setCporderid(getCharacterDataFromElement(line));
            }

        } catch (Exception e) {
            log.info(e);
        }
        return aPIServiceOrdrReqBean;
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