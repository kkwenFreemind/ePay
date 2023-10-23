/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.deposit.util;

import com.apt.epay.deposit.bean.ReserveReqBean;
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
public class ReserveProfile {

    private static final Logger log = Logger.getLogger("EPAY");
    ReserveReqBean aPIReserveReqBean = new ReserveReqBean();
    
    public ReserveReqBean parseXMLString(String xmlRecords, String TagName) throws Exception {
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
                aPIReserveReqBean.setMdn(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("CPID");
                line = (Element) nodes.item(0);
                aPIReserveReqBean.setChannelID(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("SERVICEID");
                line = (Element) nodes.item(0);
                aPIReserveReqBean.setServiceID(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("UUID");
                line = (Element) nodes.item(0);
                aPIReserveReqBean.setUuid(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("PRICE");
                line = (Element) nodes.item(0);
                aPIReserveReqBean.setPrice(getCharacterDataFromElement(line));
                
            

            }
            

        } catch (Exception e) {
            log.info(e);
        }
        return aPIReserveReqBean;
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
