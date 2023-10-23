/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.deposit.util;

import com.apt.epay.deposit.bean.UnReserveReqBean;
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
public class UnReserveProfile {

    private static final Logger log = Logger.getLogger("EPAY");
    UnReserveReqBean aPIUnReserveReqBean = new UnReserveReqBean();
    
    public UnReserveReqBean parseXMLString(String xmlRecords, String TagName) throws Exception {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));

            Document doc = db.parse(is);
            NodeList SOARes = doc.getElementsByTagName(TagName);

            for (int i = 0; i < SOARes.getLength(); i++) {

                Element element = (Element) SOARes.item(i);

                NodeList nodes = element.getElementsByTagName("CPID");
                Element line = (Element) nodes.item(0);
                aPIUnReserveReqBean.setChannelID(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("MDN");
                line = (Element) nodes.item(0);
                aPIUnReserveReqBean.setMdn(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("TOKENID");
                line = (Element) nodes.item(0);
                aPIUnReserveReqBean.setTokenId(getCharacterDataFromElement(line));

            }

        } catch (Exception e) {
            log.info(e);
        }
        return aPIUnReserveReqBean;
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
