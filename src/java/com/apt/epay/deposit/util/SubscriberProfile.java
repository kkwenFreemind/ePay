/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.deposit.util;

import com.apt.epay.deposit.bean.SubscriberLookupReqBean;
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
public class SubscriberProfile {

    private static final Logger log = Logger.getLogger("APPLEPAY");

    public SubscriberLookupReqBean parseXMLString(String xmlRecords, String TagName) throws Exception {

        SubscriberLookupReqBean aPISubscriberLookupReqBean = new SubscriberLookupReqBean();
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
                aPISubscriberLookupReqBean.setMdn(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("CPID");
                line = (Element) nodes.item(0);
                aPISubscriberLookupReqBean.setChannelID(getCharacterDataFromElement(line));

            }

        } catch (Exception e) {
            log.info(e);
        }
        return aPISubscriberLookupReqBean;
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
