/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.dealer.util;

import com.apt.epay.deposit.bean.ServiceOrderReqBean;
import com.apt.epay.zte.bean.PinCodeResultBean;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.CharacterData;
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
public class DealerUtil {

    private static final Logger log = Logger.getLogger("EPAY");
    public ServiceOrderReqBean OrderServParseXMLString(String xmlRecords, String TagName) {

        ServiceOrderReqBean aPIServiceOrdrReqBean = new ServiceOrderReqBean();

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

                nodes = element.getElementsByTagName("SERVICEID");
                line = (Element) nodes.item(0);
                aPIServiceOrdrReqBean.setServiceID(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("PRICE");
                line = (Element) nodes.item(0);
                aPIServiceOrdrReqBean.setPrice(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("STOREID");
                line = (Element) nodes.item(0);
                aPIServiceOrdrReqBean.setStoreid(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("STORENAME");
                line = (Element) nodes.item(0);
                aPIServiceOrdrReqBean.setStroename(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("APISRCID");
                line = (Element) nodes.item(0);
                aPIServiceOrdrReqBean.setApisrcid(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("PAYTOOL");
                line = (Element) nodes.item(0);
                aPIServiceOrdrReqBean.setPaytool(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("PAYNAME");
                line = (Element) nodes.item(0);
                aPIServiceOrdrReqBean.setPayname(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("CPLIBM");
                line = (Element) nodes.item(0);
                aPIServiceOrdrReqBean.setCplibm(getCharacterDataFromElement(line));
//
//                nodes = element.getElementsByTagName("LIBM");
//                line = (Element) nodes.item(0);
//                aPIServiceOrdrReqBean.setLibm(getCharacterDataFromElement(line));
            }

        } catch (IOException e) {
            log.info(e);
        } catch (ParserConfigurationException e) {
            log.info(e);
        } catch (SAXException e) {
            log.info(e);
        }
        return aPIServiceOrdrReqBean;
    }
    
    public DearlerMDNReqBean getMDNByparseXMLString(String xmlRecords, String TagName) throws Exception {
        DearlerMDNReqBean reqbean = new DearlerMDNReqBean();

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
                reqbean.setMdn(getCharacterDataFromElement(line));
                
                nodes = element.getElementsByTagName("STOREID");
                line = (Element) nodes.item(0);
                reqbean.setStoreId(getCharacterDataFromElement(line));    

                nodes = element.getElementsByTagName("STORENAME");
                line = (Element) nodes.item(0);
                reqbean.setStoreName(getCharacterDataFromElement(line));                  
            }

        } catch (IOException e) {
            log.info(e);
        } catch (ParserConfigurationException e) {
            log.info(e);
        } catch (SAXException e) {
            log.info(e);
        }
        return reqbean;
    }
    
    //解析ZTE回傳的結果
    public VoucherChangeResultBean parseZTEVoucherChangResultXMLString(String xmlRecords) throws Exception {
        /*
         <?xml version="1.0" encoding="UTF-8"?>
         <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:ns2="http://www.xmlsoap.com" xmlns:ns1="GPAService">
         <SOAP-ENV:Header>
         </SOAP-ENV:Header>
         <SOAP-ENV:Body SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"><ns1:RechargeResponse>
         <rsp>
         <SessionID>161128145518527</SessionID>
         <ReturnCode>1012</ReturnCode>
         <Desc>SUB_ERR_CARDPIN_ERROR</Desc>
         </rsp>
         </ns1:RechargeResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>
         */
        VoucherChangeResultBean resultbean = new VoucherChangeResultBean();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));
            Document doc = db.parse(is);

            NodeList PinCodeRes = doc.getElementsByTagName("rsp");
            for (int i = 0; i < PinCodeRes.getLength(); i++) {
                Element element = (Element) PinCodeRes.item(i);

                NodeList nodes = element.getElementsByTagName("SessionID");
                Element line = (Element) nodes.item(0);
                log.debug("SessionID---->" + getCharacterDataFromElement(line));
                resultbean.setSessionId(line.getAttribute("SessionID"));

                nodes = element.getElementsByTagName("ReturnCode");
                line = (Element) nodes.item(0);
                log.info("ReturnCode------>" + getCharacterDataFromElement(line));
                resultbean.setReturnCode(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("Desc");
                line = (Element) nodes.item(0);
                log.info("Desc------>" + getCharacterDataFromElement(line));
                resultbean.setReturnDesc(getCharacterDataFromElement(line));


            }

        } catch (Exception e) {
            log.info(e);
        }
        return resultbean;
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
