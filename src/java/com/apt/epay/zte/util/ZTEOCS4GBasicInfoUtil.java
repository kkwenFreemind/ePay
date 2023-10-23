/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.zte.util;

import com.apt.epay.beans.BasicInfoReqBean;
import com.apt.epay.share.ShareParm;
import com.apt.epay.zte.bean.ZTEBasicInfoReqBean;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MimeHeaders;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author kevinchang
 */
public class ZTEOCS4GBasicInfoUtil {

    private static final Logger log = Logger.getLogger("EPAY");

    public String putZTEOCS4GBasicInfoSlet(String libm, String mdn) throws Exception {

        String mdn886 = "886" + mdn.substring(1);
        String result = "";
        String ocs_systemid = new ShareParm().PARM_4GZTEOCS_SYSTEM_ID;
        String ocs_system_pwd = new ShareParm().PARM_4GZTEOCS_SYSTEM_PWD;
        String sendURL = new ShareParm().PARM_QuerySubsProfile;

        MimeHeaders mh = new MimeHeaders();
        StringBuffer BasicInfoXml = retnZTE4GOCSXML(ocs_systemid, ocs_system_pwd, libm, mdn886);
        log.info("sendURL==>" + sendURL);
        log.info("4G OCSXml:" + BasicInfoXml);

        RequestEntity body;
        body = new StringRequestEntity(BasicInfoXml.toString(), "text/xml", "utf-8");

        result = sendHttpPostMsg(body, sendURL);
//        result = retnTempBasicInfoXML().toString();
        log.info("putZTEOCS4GBasicInfoSlet Result==>" + result);

        return result;
    }

    public static StringBuffer retnZTE4GOCSXML(String system_id, String system_pwd, String libm, String mdn) {

        StringBuffer sb = new StringBuffer();

        sb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:zsm=\"http://www.ZTEsoft.com/ZSmart\">");
        sb.append("<soapenv:Header>");
        sb.append("<zsm:AuthHeader>");
        sb.append("<zsm:Username>" + system_id + "</zsm:Username>");
        sb.append("<zsm:Password>" + system_pwd + "</zsm:Password>");
        sb.append("</zsm:AuthHeader>");
        sb.append("</soapenv:Header>");
        sb.append("<soapenv:Body>");
        sb.append("<zsm:QuerySubsProfile>");
        sb.append("<zsm:MSISDN>" + mdn + "</zsm:MSISDN>");
//        sb.append("<zsm:MSISDN>886980443073</zsm:MSISDN>");
        sb.append("<zsm:ICCID></zsm:ICCID>");
        sb.append("</zsm:QuerySubsProfile>");
        sb.append("</soapenv:Body>");
        sb.append("</soapenv:Envelope>");

        return sb;
    }

//    public static StringBuffer retnTempBasicInfoXML() {
//
//        /*
//         <?xml version="1.0" encoding="utf-8"?>
//         <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
//         <soap:Body>
//         <QuerySubsProfileResponse xmlns="http://www.ZTEsoft.com/ZSmart">
//         <QuerySubsProfileResult>
//         <MSISDN>886980443073</MSISDN>
//         <DefLang>0</DefLang>
//         <BrandIndex>1027</BrandIndex>
//         <BrandName>4G_APT1711000</BrandName>
//         <State>A</State>
//         <IMSI>466051181036390</IMSI>
//         <ICCID>89886051007028163904</ICCID>
//         <balDtoList>
//         <BalDto>
//         <AcctResID>1</AcctResID>
//         <AcctResName>Local Currency</AcctResName>
//         <Balance>99.00</Balance>
//         <EffDate>2016-11-03 00:00:00</EffDate>
//         <ExpDate>2017-05-03 00:00:00</ExpDate>
//         <UpdateDate>2016-11-03 14:00:46</UpdateDate>
//         <Unit>0</Unit>
//         <BalID>5638786</BalID>
//         </BalDto>
//         <BalDto>
//         <AcctResID>71</AcctResID>
//         <AcctResName>Basic Data</AcctResName>
//         <Balance>-1048576</Balance>
//         <EffDate>2016-11-03 00:00:00</EffDate>
//         <ExpDate>2017-05-03 00:00:00</ExpDate>
//         <UpdateDate>2016-11-03 14:00:46</UpdateDate>
//         <BalID>5638790</BalID>
//         </BalDto>
//         <BalDto>
//         <AcctResID>81</AcctResID>
//         <AcctResName>Free voice currency Onnet</AcctResName>
//         <Balance>-100</Balance>
//         <EffDate>2016-11-03 17:35:12</EffDate>
//         <ExpDate>2016-12-03 19:35:12</ExpDate>
//         <UpdateDate>2016-11-03 17:35:13</UpdateDate>
//         <BalID>5638862</BalID>
//         </BalDto></balDtoList>
//         </QuerySubsProfileResult>
//         </QuerySubsProfileResponse>
//         </soap:Body>
//         </soap:Envelope>
//         */
//        StringBuffer sb = new StringBuffer();
//        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
//        sb.append(" <soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
//        sb.append("<soap:Header>");
//        sb.append("<AuthHeader xmlns=\"http://www.ZTEsoft.com/ZSmart\">");
//        sb.append("<Username>APT</Username>");
//        sb.append("<Password>APT1234</Password>");
//        sb.append("<RequestId>T20161001100000001</RequestId>");
//        sb.append("</AuthHeader>");
//        sb.append("</soap:Header>");
//
//        sb.append("<soap:Body>");
//        sb.append("<QueryUserProfileResponse xmlns=\"http://www.ZTEsoft.com/ZSmart\">");
//        sb.append("<QueryUserProfileResult>");
//        sb.append("<BrandIndex>APT11800</BrandIndex>");
//        sb.append("<BrandName>4G GT數據儲值卡180型</BrandName>");
//        sb.append("<MSISDN>0982174022</MSISDN>");
//        sb.append("<ICCID>89886051114011000125</ICCID>");
//        sb.append("<State>A</State>");
//        sb.append("<DefLang>1</DefLang>");
//        sb.append("<BalDtoList>");
//
//        sb.append("<Balto>");
//        sb.append("<BalID>0102</BalID>");
//        sb.append("<AcctResID>1</AcctResID>");
//        sb.append("<AcctResName>4G GT數據儲值卡180型</AcctResName>");
//        sb.append("<Balance>610.00</Balance>");
//        sb.append("<Unit>0</Unit>");
//        sb.append("<EffDate>2016-10-01</EffDate>");
//        sb.append("<ExpDate>2016-12-31</ExpDate>");
//        sb.append("<UpdateDate>2016-10-01</UpdateDate>");
//        sb.append("</Balto>");
//
//        sb.append("<Balto>");
//        sb.append("<BalID>0102</BalID>");
//        sb.append("<AcctResID>61</AcctResID>");
//        sb.append("<AcctResName>4G GT數據儲值卡180型</AcctResName>");
//        sb.append("<Balance>610.00</Balance>");
//        sb.append("<Unit>0</Unit>");
//        sb.append("<EffDate>2016-10-01</EffDate>");
//        sb.append("<ExpDate>2016-12-31</ExpDate>");
//        sb.append("<UpdateDate>2016-10-01</UpdateDate>");
//        sb.append("</Balto>");
//
//        sb.append("<Balto>");
//        sb.append("<BalID>0102</BalID>");
//        sb.append("<AcctResID>62</AcctResID>");
//        sb.append("<AcctResName>4G GT數據儲值卡180型</AcctResName>");
//        sb.append("<Balance>620.00</Balance>");
//        sb.append("<Unit>0</Unit>");
//        sb.append("<EffDate>2016-10-01</EffDate>");
//        sb.append("<ExpDate>2016-12-31</ExpDate>");
//        sb.append("<UpdateDate>2016-10-01</UpdateDate>");
//        sb.append("</Balto>");
//
//        sb.append("<Balto>");
//        sb.append("<BalID>0102</BalID>");
//        sb.append("<AcctResID>71</AcctResID>");
//        sb.append("<AcctResName>4G GT數據儲值卡180型</AcctResName>");
//        sb.append("<Balance>720.00</Balance>");
//        sb.append("<Unit>0</Unit>");
//        sb.append("<EffDate>2016-10-01</EffDate>");
//        sb.append("<ExpDate>2016-12-31</ExpDate>");
//        sb.append("<UpdateDate>2016-10-01</UpdateDate>");
//        sb.append("</Balto>");
//
//        sb.append("<Balto>");
//        sb.append("<BalID>0102</BalID>");
//        sb.append("<AcctResID>72</AcctResID>");
//        sb.append("<AcctResName>4G GT數據儲值卡180型</AcctResName>");
//        sb.append("<Balance>730.00</Balance>");
//        sb.append("<Unit>0</Unit>");
//        sb.append("<EffDate>2016-10-01</EffDate>");
//        sb.append("<ExpDate>2016-12-31</ExpDate>");
//        sb.append("<UpdateDate>2016-10-01</UpdateDate>");
//        sb.append("</Balto>");
//
//        sb.append("<Balto>");
//        sb.append("<BalID>0102</BalID>");
//        sb.append("<AcctResID>81</AcctResID>");
//        sb.append("<AcctResName>4G GT數據儲值卡180型</AcctResName>");
//        sb.append("<Balance>8100.00</Balance>");
//        sb.append("<Unit>0</Unit>");
//        sb.append("<EffDate>2016-10-01</EffDate>");
//        sb.append("<ExpDate>2016-12-31</ExpDate>");
//        sb.append("<UpdateDate>2016-10-01</UpdateDate>");
//        sb.append("</Balto>");
//
//        sb.append("</BalDtoList>");
//        sb.append("</QueryUserProfileResult>");
//        sb.append("</QueryUserProfileResponse>");
//        sb.append("</soap:Body>");
//        sb.append("</soap:Envelope>");
//
//        return sb;
//    }
    public BasicInfoReqBean parseZTEBasicInfoXMLString(String xmlRecords) throws Exception {

        ZTEBasicInfoReqBean aPIBasicInfoRequestBean = new ZTEBasicInfoReqBean();
        BasicInfoReqBean basicbean = new BasicInfoReqBean();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));

            Document doc = db.parse(is);

            NodeList MainInfoRes = doc.getElementsByTagName("QuerySubsProfileResult");
            for (int j = 0; j < MainInfoRes.getLength(); j++) {
                String State = doc.getElementsByTagName("State").item(j).getFirstChild().getNodeValue();
                String BrandName = doc.getElementsByTagName("BrandName").item(j).getFirstChild().getNodeValue();
                aPIBasicInfoRequestBean.setReal_LifeCycleState(State);
                aPIBasicInfoRequestBean.setLifeCycleState(State);
                aPIBasicInfoRequestBean.setBrandName(BrandName);
                
                log.info("BrandNamee==>" + BrandName);
                log.info("Real State==>" + State);
                if (State.equalsIgnoreCase("Active") || State.equalsIgnoreCase("Two-Way Block") || State.equalsIgnoreCase("pre-active")) {
                    aPIBasicInfoRequestBean.setLifeCycleState("ACTIVE");
                } else {
                    aPIBasicInfoRequestBean.setLifeCycleState(State);
                }
                log.info("Adj State==>" + aPIBasicInfoRequestBean.getLifeCycleState());
            }

            NodeList BasicInfoRes = doc.getElementsByTagName("BalDto");

            for (int i = 0; i < BasicInfoRes.getLength(); i++) {

                String AcctResID = doc.getElementsByTagName("AcctResID").item(i).getFirstChild().getNodeValue();
                String Balance = doc.getElementsByTagName("Balance").item(i).getFirstChild().getNodeValue();
                String ExpDate = doc.getElementsByTagName("ExpDate").item(i).getFirstChild().getNodeValue();
                String UpdateDate = doc.getElementsByTagName("UpdateDate").item(i).getFirstChild().getNodeValue();

                log.info("AcctResID==>" + doc.getElementsByTagName("AcctResID").item(i).getFirstChild().getNodeValue());
                log.info("Balance==>" + doc.getElementsByTagName("Balance").item(i).getFirstChild().getNodeValue());
                log.info("UpdateDate==>" + doc.getElementsByTagName("ExpDate").item(i).getFirstChild().getNodeValue());

                if (AcctResID.equals("1")) {
                    aPIBasicInfoRequestBean.setID1(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue1(Balance);
                    aPIBasicInfoRequestBean.setEndDate1(ExpDate);
                    aPIBasicInfoRequestBean.setUpdateDateKK(UpdateDate);
                    log.info("bucketid 1 ==> " + aPIBasicInfoRequestBean.getCounterValue1());
                    log.info("bucketid 1 ==> " + aPIBasicInfoRequestBean.getEndDate1());
                    log.info("bucketid 1 ==> " + aPIBasicInfoRequestBean.getUpdateDateKK());
                }

                if (AcctResID.equals("2")) {
                    aPIBasicInfoRequestBean.setID2(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue2(Balance);
//                    aPIBasicInfoRequestBean.setEndDate2(ExpDate);
//                    log.info("bucketid 2 ==> " + aPIBasicInfoRequestBean.getCounterValue2());
//                    log.info("bucketid 2 ==> " + aPIBasicInfoRequestBean.getEndDate2());

                    if (aPIBasicInfoRequestBean.getEndDate2().equals("")) {
                        aPIBasicInfoRequestBean.setEndDate2(ExpDate);
                        log.info("First bucketid 2 ==> " + aPIBasicInfoRequestBean.getEndDate2());
                    } else {
                        if (ExpDate.compareTo(aPIBasicInfoRequestBean.getEndDate2()) > 0) {
                            aPIBasicInfoRequestBean.setEndDate2(ExpDate);
                            log.info("Update First bucketid 2 ==> " + ExpDate + "," + aPIBasicInfoRequestBean.getEndDate2());
                        } else {
                            log.info("No Upadate bucketid 2 ==> " + ExpDate + "," + aPIBasicInfoRequestBean.getEndDate2());
                        }
                    }
                }

                if (AcctResID.equals("3")) {
                    aPIBasicInfoRequestBean.setID3(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue3(Balance);
                    aPIBasicInfoRequestBean.setEndDate3(ExpDate);
                    log.info("bucketid 3 ==> " + aPIBasicInfoRequestBean.getCounterValue3());
                    log.info("bucketid 3 ==> " + aPIBasicInfoRequestBean.getEndDate3());
                }

                if (AcctResID.equals("4")) {
                    aPIBasicInfoRequestBean.setID4(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue4(Balance);
                    aPIBasicInfoRequestBean.setEndDate4(ExpDate);
                    log.info("bucketid 4 ==> " + aPIBasicInfoRequestBean.getCounterValue4());
                    log.info("bucketid 4 ==> " + aPIBasicInfoRequestBean.getEndDate4());
                }

                if (AcctResID.equals("7")) {
                    aPIBasicInfoRequestBean.setID7(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue7(Balance);
                    aPIBasicInfoRequestBean.setEndDate7(ExpDate);
                    log.info("bucketid 7 ==> " + aPIBasicInfoRequestBean.getCounterValue7());
                    log.info("bucketid 7 ==> " + aPIBasicInfoRequestBean.getEndDate7());

                }

                if (AcctResID.equals("61")) {
                    aPIBasicInfoRequestBean.setID6(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue61(Balance);
                    aPIBasicInfoRequestBean.setEndDate61(ExpDate);
                    log.info("bucketid 61 ==> " + aPIBasicInfoRequestBean.getCounterValue61());
                    log.info("bucketid 61 ==> " + aPIBasicInfoRequestBean.getEndDate61());

                }

                if (AcctResID.equals("62")) {
                    aPIBasicInfoRequestBean.setID2(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue62(Balance);
                    aPIBasicInfoRequestBean.setEndDate62(ExpDate);
                    log.info("bucketid 62 ==> " + aPIBasicInfoRequestBean.getCounterValue62());
                    log.info("bucketid 62 ==> " + aPIBasicInfoRequestBean.getEndDate62());

                }

                if (AcctResID.equals("65")) {
                    aPIBasicInfoRequestBean.setID65(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue65(Balance);
                    aPIBasicInfoRequestBean.setEndDate65(ExpDate);
                    log.info("bucketid 65 ==> " + aPIBasicInfoRequestBean.getCounterValue65());
                    log.info("bucketid 65 ==> " + aPIBasicInfoRequestBean.getEndDate65());

                }

                if (AcctResID.equals("66")) {
                    aPIBasicInfoRequestBean.setID66(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue66(Balance);
                    aPIBasicInfoRequestBean.setEndDate66(ExpDate);
                    log.info("bucketid 66 ==> " + aPIBasicInfoRequestBean.getCounterValue66());
                    log.info("bucketid 66 ==> " + aPIBasicInfoRequestBean.getEndDate66());

                }
                if (AcctResID.equals("71")) {
                    aPIBasicInfoRequestBean.setID71(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue71(Balance);
                    aPIBasicInfoRequestBean.setEndDate71(ExpDate);
                    log.info("bucketid 71 ==> " + aPIBasicInfoRequestBean.getCounterValue71());
                    log.info("bucketid 71 ==> " + aPIBasicInfoRequestBean.getEndDate71());

                }
                if (AcctResID.equals("72")) {
                    aPIBasicInfoRequestBean.setID72(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue72(Balance);
                    aPIBasicInfoRequestBean.setEndDate72(ExpDate);
                    log.info("bucketid 72 ==> " + aPIBasicInfoRequestBean.getCounterValue72());
                    log.info("bucketid 72 ==> " + aPIBasicInfoRequestBean.getEndDate72());

                }
                if (AcctResID.equals("75")) {
                    aPIBasicInfoRequestBean.setID75(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue75(Balance);
                    log.info("bucketid 75 ==> " + aPIBasicInfoRequestBean.getCounterValue75());

                    if (aPIBasicInfoRequestBean.getEndDate75().equals("")) {
                        aPIBasicInfoRequestBean.setEndDate75(ExpDate);
                        log.info("First bucketid 75 ==> " + aPIBasicInfoRequestBean.getEndDate75());
                    } else {
                        if (ExpDate.compareTo(aPIBasicInfoRequestBean.getEndDate75()) > 0) {
                            aPIBasicInfoRequestBean.setEndDate75(ExpDate);
                            log.info("Update First bucketid 75 ==> " + ExpDate + "," + aPIBasicInfoRequestBean.getEndDate75());
                        } else {
                            log.info("No Upadate bucketid 75 ==> " + ExpDate + "," + aPIBasicInfoRequestBean.getEndDate75());
                        }
                    }

                }
                if (AcctResID.equals("76")) {
                    aPIBasicInfoRequestBean.setID76(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue76(Balance);
//                    aPIBasicInfoRequestBean.setEndDate76(ExpDate);
                    if (aPIBasicInfoRequestBean.getEndDate76().equals("")) {
                        aPIBasicInfoRequestBean.setEndDate76(ExpDate);
                        log.info("First bucketid 76 ==> " + aPIBasicInfoRequestBean.getEndDate76());
                    } else {
                        if (ExpDate.compareTo(aPIBasicInfoRequestBean.getEndDate76()) > 0) {
                            aPIBasicInfoRequestBean.setEndDate76(ExpDate);
                            log.info("Update First bucketid 76 ==> " + ExpDate + "," + aPIBasicInfoRequestBean.getEndDate76());
                        } else {
                            log.info("No Upadate bucketid 76 ==> " + ExpDate + "," + aPIBasicInfoRequestBean.getEndDate76());
                        }
                    }

                }
                if (AcctResID.equals("77")) {
                    aPIBasicInfoRequestBean.setID77(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue77(Balance);
//                    aPIBasicInfoRequestBean.setEndDate77(ExpDate);
                    log.info("bucketid 77 ==> " + aPIBasicInfoRequestBean.getCounterValue77());
//                    log.info("bucketid 77 ==> " + aPIBasicInfoRequestBean.getEndDate77());
                    if (aPIBasicInfoRequestBean.getEndDate77().equals("")) {
                        aPIBasicInfoRequestBean.setEndDate77(ExpDate);
                        log.info("First bucketid 77 ==> " + aPIBasicInfoRequestBean.getEndDate77());
                    } else {
                        if (ExpDate.compareTo(aPIBasicInfoRequestBean.getEndDate77()) > 0) {
                            aPIBasicInfoRequestBean.setEndDate77(ExpDate);
                            log.info("Update First bucketid 77 ==> " + ExpDate + "," + aPIBasicInfoRequestBean.getEndDate77());
                        } else {
                            log.info("No Upadate bucketid 77 ==> " + ExpDate + "," + aPIBasicInfoRequestBean.getEndDate77());
                        }
                    }

                }

                if (AcctResID.equals("78")) {
                    aPIBasicInfoRequestBean.setID78(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue78(Balance);
//                    aPIBasicInfoRequestBean.setEndDate78(ExpDate);
//                    log.info("bucketid 78 ==> " + aPIBasicInfoRequestBean.getCounterValue78());
//                    log.info("bucketid 78 ==> " + aPIBasicInfoRequestBean.getEndDate78());
                    if (aPIBasicInfoRequestBean.getEndDate78().equals("")) {
                        aPIBasicInfoRequestBean.setEndDate78(ExpDate);
                        log.info("First bucketid 78 ==> " + aPIBasicInfoRequestBean.getEndDate78());
                    } else {
                        if (ExpDate.compareTo(aPIBasicInfoRequestBean.getEndDate78()) > 0) {
                            aPIBasicInfoRequestBean.setEndDate78(ExpDate);
                            log.info("Update First bucketid 78 ==> " + ExpDate + "," + aPIBasicInfoRequestBean.getEndDate78());
                        } else {
                            log.info("No Upadate bucketid 78 ==> " + ExpDate + "," + aPIBasicInfoRequestBean.getEndDate78());
                        }
                    }
                }

                if (AcctResID.equals("81")) {
                    aPIBasicInfoRequestBean.setID81(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue81(Balance);
                    aPIBasicInfoRequestBean.setEndDate81(ExpDate);
                    log.info("bucketid 81 ==> " + aPIBasicInfoRequestBean.getCounterValue81());
                    log.info("bucketid 81 ==> " + aPIBasicInfoRequestBean.getEndDate81());

                }

                if (AcctResID.equals("82")) {
                    aPIBasicInfoRequestBean.setID82(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue82(Balance);
                    log.info("bucketid 82 ==> " + aPIBasicInfoRequestBean.getCounterValue82());
//                    log.info("bucketid 82 ==> " + aPIBasicInfoRequestBean.getEndDate82());
//                    if (ExpDate.compareTo(aPIBasicInfoRequestBean.getEndDate82()) > 0) {
//                        aPIBasicInfoRequestBean.setEndDate82(ExpDate);
//                        log.info("Update bucketid 82 ==> " + aPIBasicInfoRequestBean.getEndDate82());
//                    }
                    if (aPIBasicInfoRequestBean.getEndDate82().equals("")) {
                        aPIBasicInfoRequestBean.setEndDate82(ExpDate);
                        log.info("First bucketid 82 ==> " + aPIBasicInfoRequestBean.getEndDate82());
                    } else {
                        if (ExpDate.compareTo(aPIBasicInfoRequestBean.getEndDate82()) > 0) {
                            aPIBasicInfoRequestBean.setEndDate82(ExpDate);
                            log.info("Update First bucketid 82 ==> " + ExpDate + "," + aPIBasicInfoRequestBean.getEndDate82());
                        } else {
                            log.info("No Upadate bucketid 82 ==> " + ExpDate + "," + aPIBasicInfoRequestBean.getEndDate82());
                        }
                    }
                }

            }

            basicbean.setLifeCycleState(aPIBasicInfoRequestBean.getLifeCycleState());
            basicbean.setReal_LifeCycleState(aPIBasicInfoRequestBean.getReal_LifeCycleState());
            basicbean.setBrandNamee(aPIBasicInfoRequestBean.getBrandName());
            basicbean.setUpdateDate(aPIBasicInfoRequestBean.getUpdateDateKK());
            
            log.info("aPIBasicInfoRequestBean.getRealLifeCycleState()==>" + basicbean.getReal_LifeCycleState());
            log.info("aPIBasicInfoRequestBean.getLifeCycleState()==>" + basicbean.getLifeCycleState());
            log.info("aPIBasicInfoRequestBean.getBrandName()==>" + basicbean.getBrandNamee());
            log.info("aPIBasicInfoRequestBean.getUpdateDateKK()==>" + basicbean.getUpdateDate());
            
            basicbean.setID1("610");
            String bucketid610 = String.valueOf(Double.valueOf(aPIBasicInfoRequestBean.getCounterValue1()) + Double.valueOf(aPIBasicInfoRequestBean.getCounterValue61()));
            basicbean.setCounterValue1(bucketid610);
            if (!aPIBasicInfoRequestBean.getEndDate1().equals("")) {
                basicbean.setEndDate1(aPIBasicInfoRequestBean.getEndDate1().substring(0, 10));
            }

            basicbean.setID2(("620"));
            String bucketid620 = String.valueOf(Double.valueOf(aPIBasicInfoRequestBean.getCounterValue3())
                            + Double.valueOf(aPIBasicInfoRequestBean.getCounterValue4())
                            + Double.valueOf(aPIBasicInfoRequestBean.getCounterValue7())
                            + Double.valueOf(aPIBasicInfoRequestBean.getCounterValue62()));
            /*
             String bucketid620 = String.valueOf(Double.valueOf(aPIBasicInfoRequestBean.getCounterValue2())
             + Double.valueOf(aPIBasicInfoRequestBean.getCounterValue3())
             + Double.valueOf(aPIBasicInfoRequestBean.getCounterValue4())
             + Double.valueOf(aPIBasicInfoRequestBean.getCounterValue7())
             + Double.valueOf(aPIBasicInfoRequestBean.getCounterValue62()));
             */
            basicbean.setCounterValue2(bucketid620);

            basicbean.setID3("720");
            String bucketid720 = String.valueOf(Double.valueOf(aPIBasicInfoRequestBean.getCounterValue71()));
            basicbean.setCounterValue3(bucketid720);
            if (!aPIBasicInfoRequestBean.getEndDate71().equals("")) {
                basicbean.setEndDate3(aPIBasicInfoRequestBean.getEndDate71().substring(0, 10));
            }

            basicbean.setID4("730");
            String bucketid730 = String.valueOf(Double.valueOf(aPIBasicInfoRequestBean.getCounterValue72()));
            basicbean.setCounterValue4(bucketid730);
            if (!aPIBasicInfoRequestBean.getEndDate72().equals("")) {
                basicbean.setEndDate4(aPIBasicInfoRequestBean.getEndDate72().substring(0, 10));
            }
            basicbean.setID5("810");
            String bucketid810 = String.valueOf(Double.valueOf(aPIBasicInfoRequestBean.getCounterValue81()));
            basicbean.setCounterValue5(bucketid810);

            log.info("aPIBasicInfoRequestBean.getEndDate81()===>" + aPIBasicInfoRequestBean.getEndDate81());

            if (!aPIBasicInfoRequestBean.getEndDate81().equals("")) {
                basicbean.setEndDate5(aPIBasicInfoRequestBean.getEndDate81().substring(0, 10));
            }

            basicbean.setID6("820");
            String bucketid820 = String.valueOf(Double.valueOf(aPIBasicInfoRequestBean.getCounterValue82()));
            basicbean.setCounterValue6(bucketid820);

            String buckK82date = aPIBasicInfoRequestBean.getEndDate82();
            String buckK2date = aPIBasicInfoRequestBean.getEndDate2();
            String buck82 = buckK82date;
            if (buck82.compareTo(buckK2date) < 0) {
                buck82 = buckK2date;
            }

//            if (!aPIBasicInfoRequestBean.getEndDate82().equals("")) {
            if (!buck82.equals("")) {
//                basicbean.setEndDate6(aPIBasicInfoRequestBean.getEndDate82().substring(0, 10));
                basicbean.setEndDate6(buck82.substring(0, 10));
            }

            String buck75date = aPIBasicInfoRequestBean.getEndDate75();
            String buck76date = aPIBasicInfoRequestBean.getEndDate76();
            String buck77date = aPIBasicInfoRequestBean.getEndDate77();
            String buck78date = aPIBasicInfoRequestBean.getEndDate78();

            String buckKK = buck75date;

            if (buckKK.compareTo(buck76date) < 0) {
                buckKK = buck76date;
            }

            if (buckKK.compareTo(buck77date) < 0) {
                buckKK = buck77date;
            }

            if (buckKK.compareTo(buck78date) < 0) {
                buckKK = buck78date;
            }
            log.info("====>" + buckKK);

            if (!buckKK.equals("")) {
                basicbean.setID7("710");
                basicbean.setEndDate7(buckKK.substring(0, 10));
            }
        } catch (IOException e) {
            log.info(e);
        } catch (NumberFormatException e) {
            log.info(e);
        } catch (ParserConfigurationException e) {
            log.info(e);
        } catch (DOMException e) {
            log.info(e);
        } catch (SAXException e) {
            log.info(e);
        }
        return basicbean;
    }

    public static String sendHttpPostMsg(RequestEntity requestBody, String url) throws Exception {
//        System.out.println(" ** sendHttpPostMsg step 01");
        String rtresult = null;

        PostMethod post = new PostMethod(url);
        post.setRequestEntity(requestBody);
        HttpClient hc = new HttpClient();

//        post.addParameter("SOAPAction", "http://www.ZTEsoft.com/ZSmart/QuerySubsProfile");
        post.setRequestHeader("SOAPAction", "http://www.ZTEsoft.com/ZSmart/QuerySubsProfile");

//        System.out.println(" ** setConnectionManagerTimeout(10) step 02");
        try {
            hc.getParams().setConnectionManagerTimeout(10);
//        System.out.println(" ** .setSoTimeout(10) step 02");
//            hc.getParams().setSoTimeout(10);

            int result = hc.executeMethod(post);
//            System.out.println(" ** sendHttpPostMsg step 03");
            if (result == HttpStatus.SC_OK) {
                rtresult = post.getResponseBodyAsString();
            } else {
                rtresult = post.getResponseBodyAsString();
            }
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            post.releaseConnection();
        }
        return rtresult;
    }

    public String GetBuck77(String xmlRecords) throws Exception {

        String buck77date = "";
        ZTEBasicInfoReqBean aPIBasicInfoRequestBean = new ZTEBasicInfoReqBean();
        BasicInfoReqBean basicbean = new BasicInfoReqBean();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));

            Document doc = db.parse(is);

            NodeList MainInfoRes = doc.getElementsByTagName("QuerySubsProfileResult");
            for (int j = 0; j < MainInfoRes.getLength(); j++) {
                String State = doc.getElementsByTagName("State").item(j).getFirstChild().getNodeValue();

                aPIBasicInfoRequestBean.setLifeCycleState(State);
                log.info("Real State==>" + State);
                if (State.equalsIgnoreCase("Active") || State.equalsIgnoreCase("Two-Way Block") || State.equalsIgnoreCase("pre-active")) {
                    aPIBasicInfoRequestBean.setLifeCycleState("ACTIVE");
                } else {
                    aPIBasicInfoRequestBean.setLifeCycleState(State);
                }
                log.info("Adj State==>" + aPIBasicInfoRequestBean.getLifeCycleState());
            }

            NodeList BasicInfoRes = doc.getElementsByTagName("BalDto");

            for (int i = 0; i < BasicInfoRes.getLength(); i++) {

                String AcctResID = doc.getElementsByTagName("AcctResID").item(i).getFirstChild().getNodeValue();
                String Balance = doc.getElementsByTagName("Balance").item(i).getFirstChild().getNodeValue();
                String ExpDate = doc.getElementsByTagName("ExpDate").item(i).getFirstChild().getNodeValue();

//                log.info("AcctResID==>" + doc.getElementsByTagName("AcctResID").item(i).getFirstChild().getNodeValue());
//                log.info("Balance==>" + doc.getElementsByTagName("Balance").item(i).getFirstChild().getNodeValue());
//                log.info("UpdateDate==>" + doc.getElementsByTagName("ExpDate").item(i).getFirstChild().getNodeValue());
                if (AcctResID.equals("1")) {
                    aPIBasicInfoRequestBean.setID1(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue1(Balance);
                    aPIBasicInfoRequestBean.setEndDate1(ExpDate);
//                    log.info("bucketid 1 ==> " + aPIBasicInfoRequestBean.getCounterValue1());
//                    log.info("bucketid 1 ==> " + aPIBasicInfoRequestBean.getEndDate1());
                }

                if (AcctResID.equals("2")) {
                    aPIBasicInfoRequestBean.setID2(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue2(Balance);
//                    aPIBasicInfoRequestBean.setEndDate2(ExpDate);
//                    log.info("bucketid 2 ==> " + aPIBasicInfoRequestBean.getCounterValue2());
//                    log.info("bucketid 2 ==> " + aPIBasicInfoRequestBean.getEndDate2());

                    if (aPIBasicInfoRequestBean.getEndDate2().equals("")) {
                        aPIBasicInfoRequestBean.setEndDate2(ExpDate);
                        log.info("First bucketid 2 ==> " + aPIBasicInfoRequestBean.getEndDate2());
                    } else {
                        if (ExpDate.compareTo(aPIBasicInfoRequestBean.getEndDate2()) > 0) {
                            aPIBasicInfoRequestBean.setEndDate2(ExpDate);
//                            log.info("Update First bucketid 2 ==> " + ExpDate + "," + aPIBasicInfoRequestBean.getEndDate2());
                        } else {
//                            log.info("No Upadate bucketid 2 ==> " + ExpDate + "," + aPIBasicInfoRequestBean.getEndDate2());
                        }
                    }
                }

                if (AcctResID.equals("3")) {
                    aPIBasicInfoRequestBean.setID3(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue3(Balance);
                    aPIBasicInfoRequestBean.setEndDate3(ExpDate);
//                    log.info("bucketid 3 ==> " + aPIBasicInfoRequestBean.getCounterValue3());
//                    log.info("bucketid 3 ==> " + aPIBasicInfoRequestBean.getEndDate3());
                }

                if (AcctResID.equals("4")) {
                    aPIBasicInfoRequestBean.setID4(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue4(Balance);
                    aPIBasicInfoRequestBean.setEndDate4(ExpDate);
//                    log.info("bucketid 4 ==> " + aPIBasicInfoRequestBean.getCounterValue4());
//                    log.info("bucketid 4 ==> " + aPIBasicInfoRequestBean.getEndDate4());
                }

                if (AcctResID.equals("7")) {
                    aPIBasicInfoRequestBean.setID7(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue7(Balance);
                    aPIBasicInfoRequestBean.setEndDate7(ExpDate);
//                    log.info("bucketid 7 ==> " + aPIBasicInfoRequestBean.getCounterValue7());
//                    log.info("bucketid 7 ==> " + aPIBasicInfoRequestBean.getEndDate7());

                }

                if (AcctResID.equals("61")) {
                    aPIBasicInfoRequestBean.setID6(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue61(Balance);
                    aPIBasicInfoRequestBean.setEndDate61(ExpDate);
//                    log.info("bucketid 61 ==> " + aPIBasicInfoRequestBean.getCounterValue61());
//                    log.info("bucketid 61 ==> " + aPIBasicInfoRequestBean.getEndDate61());

                }

                if (AcctResID.equals("62")) {
                    aPIBasicInfoRequestBean.setID2(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue62(Balance);
                    aPIBasicInfoRequestBean.setEndDate62(ExpDate);
//                    log.info("bucketid 62 ==> " + aPIBasicInfoRequestBean.getCounterValue62());
//                    log.info("bucketid 62 ==> " + aPIBasicInfoRequestBean.getEndDate62());

                }

                if (AcctResID.equals("65")) {
                    aPIBasicInfoRequestBean.setID65(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue65(Balance);
                    aPIBasicInfoRequestBean.setEndDate65(ExpDate);
//                    log.info("bucketid 65 ==> " + aPIBasicInfoRequestBean.getCounterValue65());
//                    log.info("bucketid 65 ==> " + aPIBasicInfoRequestBean.getEndDate65());

                }

                if (AcctResID.equals("66")) {
                    aPIBasicInfoRequestBean.setID66(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue66(Balance);
                    aPIBasicInfoRequestBean.setEndDate66(ExpDate);
//                    log.info("bucketid 66 ==> " + aPIBasicInfoRequestBean.getCounterValue66());
//                    log.info("bucketid 66 ==> " + aPIBasicInfoRequestBean.getEndDate66());

                }
                if (AcctResID.equals("71")) {
                    aPIBasicInfoRequestBean.setID71(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue71(Balance);
                    aPIBasicInfoRequestBean.setEndDate71(ExpDate);
//                    log.info("bucketid 71 ==> " + aPIBasicInfoRequestBean.getCounterValue71());
//                    log.info("bucketid 71 ==> " + aPIBasicInfoRequestBean.getEndDate71());

                }
                if (AcctResID.equals("72")) {
                    aPIBasicInfoRequestBean.setID72(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue72(Balance);
                    aPIBasicInfoRequestBean.setEndDate72(ExpDate);
//                    log.info("bucketid 72 ==> " + aPIBasicInfoRequestBean.getCounterValue72());
//                    log.info("bucketid 72 ==> " + aPIBasicInfoRequestBean.getEndDate72());

                }
                if (AcctResID.equals("75")) {
                    aPIBasicInfoRequestBean.setID75(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue75(Balance);
//                    log.info("bucketid 75 ==> " + aPIBasicInfoRequestBean.getCounterValue75());

                    if (aPIBasicInfoRequestBean.getEndDate75().equals("")) {
                        aPIBasicInfoRequestBean.setEndDate75(ExpDate);
//                        log.info("First bucketid 75 ==> " + aPIBasicInfoRequestBean.getEndDate75());
                    } else {
                        if (ExpDate.compareTo(aPIBasicInfoRequestBean.getEndDate75()) > 0) {
                            aPIBasicInfoRequestBean.setEndDate75(ExpDate);
//                            log.info("Update First bucketid 75 ==> " + ExpDate + "," + aPIBasicInfoRequestBean.getEndDate75());
                        } else {
//                            log.info("No Upadate bucketid 75 ==> " + ExpDate + "," + aPIBasicInfoRequestBean.getEndDate75());
                        }
                    }

                }
                if (AcctResID.equals("76")) {
                    aPIBasicInfoRequestBean.setID76(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue76(Balance);
//                    aPIBasicInfoRequestBean.setEndDate76(ExpDate);
                    if (aPIBasicInfoRequestBean.getEndDate76().equals("")) {
                        aPIBasicInfoRequestBean.setEndDate76(ExpDate);
//                        log.info("First bucketid 76 ==> " + aPIBasicInfoRequestBean.getEndDate76());
                    } else {
                        if (ExpDate.compareTo(aPIBasicInfoRequestBean.getEndDate76()) > 0) {
                            aPIBasicInfoRequestBean.setEndDate76(ExpDate);
//                            log.info("Update First bucketid 76 ==> " + ExpDate + "," + aPIBasicInfoRequestBean.getEndDate76());
                        } else {
//                            log.info("No Upadate bucketid 76 ==> " + ExpDate + "," + aPIBasicInfoRequestBean.getEndDate76());
                        }
                    }

                }
                if (AcctResID.equals("77")) {
                    aPIBasicInfoRequestBean.setID77(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue77(Balance);
//                    aPIBasicInfoRequestBean.setEndDate77(ExpDate);
                    log.info("bucketid 77 ==> " + aPIBasicInfoRequestBean.getCounterValue77());
//                    log.info("bucketid 77 ==> " + aPIBasicInfoRequestBean.getEndDate77());
                    if (aPIBasicInfoRequestBean.getEndDate77().equals("")) {
                        aPIBasicInfoRequestBean.setEndDate77(ExpDate);
                        log.info("First bucketid 77 ==> " + aPIBasicInfoRequestBean.getEndDate77());
                    } else {
                        if (ExpDate.compareTo(aPIBasicInfoRequestBean.getEndDate77()) > 0) {
                            aPIBasicInfoRequestBean.setEndDate77(ExpDate);
                            log.info("Update First bucketid 77 ==> " + ExpDate + "," + aPIBasicInfoRequestBean.getEndDate77());
                        } else {
                            log.info("No Upadate bucketid 77 ==> " + ExpDate + "," + aPIBasicInfoRequestBean.getEndDate77());
                        }
                    }

                }

                if (AcctResID.equals("78")) {
                    aPIBasicInfoRequestBean.setID78(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue78(Balance);
//                    aPIBasicInfoRequestBean.setEndDate78(ExpDate);
//                    log.info("bucketid 78 ==> " + aPIBasicInfoRequestBean.getCounterValue78());
//                    log.info("bucketid 78 ==> " + aPIBasicInfoRequestBean.getEndDate78());
                    if (aPIBasicInfoRequestBean.getEndDate78().equals("")) {
                        aPIBasicInfoRequestBean.setEndDate78(ExpDate);
//                        log.info("First bucketid 78 ==> " + aPIBasicInfoRequestBean.getEndDate78());
                    } else {
                        if (ExpDate.compareTo(aPIBasicInfoRequestBean.getEndDate78()) > 0) {
                            aPIBasicInfoRequestBean.setEndDate78(ExpDate);
//                            log.info("Update First bucketid 78 ==> " + ExpDate + "," + aPIBasicInfoRequestBean.getEndDate78());
                        } else {
//                            log.info("No Upadate bucketid 78 ==> " + ExpDate + "," + aPIBasicInfoRequestBean.getEndDate78());
                        }
                    }
                }

                if (AcctResID.equals("81")) {
                    aPIBasicInfoRequestBean.setID81(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue81(Balance);
                    aPIBasicInfoRequestBean.setEndDate81(ExpDate);
//                    log.info("bucketid 81 ==> " + aPIBasicInfoRequestBean.getCounterValue81());
//                    log.info("bucketid 81 ==> " + aPIBasicInfoRequestBean.getEndDate81());

                }

                if (AcctResID.equals("82")) {
                    aPIBasicInfoRequestBean.setID82(AcctResID);
                    aPIBasicInfoRequestBean.setCounterValue82(Balance);
                    log.info("bucketid 82 ==> " + aPIBasicInfoRequestBean.getCounterValue82());
//                    log.info("bucketid 82 ==> " + aPIBasicInfoRequestBean.getEndDate82());
//                    if (ExpDate.compareTo(aPIBasicInfoRequestBean.getEndDate82()) > 0) {
//                        aPIBasicInfoRequestBean.setEndDate82(ExpDate);
//                        log.info("Update bucketid 82 ==> " + aPIBasicInfoRequestBean.getEndDate82());
//                    }
                    if (aPIBasicInfoRequestBean.getEndDate82().equals("")) {
                        aPIBasicInfoRequestBean.setEndDate82(ExpDate);
//                        log.info("First bucketid 82 ==> " + aPIBasicInfoRequestBean.getEndDate82());
                    } else {
                        if (ExpDate.compareTo(aPIBasicInfoRequestBean.getEndDate82()) > 0) {
                            aPIBasicInfoRequestBean.setEndDate82(ExpDate);
//                            log.info("Update First bucketid 82 ==> " + ExpDate + "," + aPIBasicInfoRequestBean.getEndDate82());
                        } else {
//                            log.info("No Upadate bucketid 82 ==> " + ExpDate + "," + aPIBasicInfoRequestBean.getEndDate82());
                        }
                    }
                }

            }

//            String buck75date = aPIBasicInfoRequestBean.getEndDate75();
//            String buck76date = aPIBasicInfoRequestBean.getEndDate76();
            buck77date = aPIBasicInfoRequestBean.getEndDate77();
//            String buck78date = aPIBasicInfoRequestBean.getEndDate78();

        } catch (Exception e) {
            log.info(e);
        }
        return buck77date;
    }
}
