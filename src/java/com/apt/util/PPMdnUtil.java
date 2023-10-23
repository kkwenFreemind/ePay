/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.util;

import com.apt.epay.beans.BasicInfoReqBean;
import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.deposit.bean.IbonReqBean;
import com.apt.epay.deposit.bean.PincodeOrderReqBean;
import com.apt.epay.deposit.bean.ServiceOrderReqBean;
import com.apt.epay.deposit.bean.ServiceOrderStatusReqBean;
import com.apt.epay.deposit.bean.UserAccountLookupReqBean;
import com.apt.epay.deposit.bean.VoucherChangeStatusReqBean;
import com.apt.epay.deposit.bean.VoucherInfoReqBean;
import com.apt.epay.nokia.main.NokiaMainLoginAndLifeCycleUtil;
import com.apt.epay.nokia.util.NokiaHLMdnLCUtil;
import com.apt.epay.share.ShareParm;
import com.apt.epay.zte.util.ZTEOCS4GBasicInfoUtil;
import com.epay.ejb.bean.EPAY_PROMOTIONCODE;
import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
public class PPMdnUtil {

    private String mdn;
    private String contractstatus;
    private String contractstatuscode;
    private String ocsstatus;
    private String promotioncode;
    private String contractid;
    private int platformtype;
    private boolean zteflag;
    private boolean aluflag;
    private final Logger log;

    public PPMdnUtil() {
        this.log = Logger.getLogger("EPAY");
    }

    public PPMdnUtil(String mdn, boolean aluflag, boolean zteflag) {
        this.log = Logger.getLogger("EPAY");
        this.mdn = mdn;
        this.zteflag = zteflag;
        this.aluflag = aluflag;
    }

    public String getContractid() {
        return contractid;
    }

    public void setContractid(String contractid) {
        this.contractid = contractid;
    }

    public int getPlatformtype() {
        return platformtype;
    }

    public void setPlatformtype(int platformtype) {
        this.platformtype = platformtype;
    }

    public String getPromotioncode() {
        return promotioncode;
    }

    public String getContractstatus() {
        return contractstatus;
    }

    public void setContractstatus(String contractstatus) {
        this.contractstatus = contractstatus;
    }

    public void setPromotioncode(String promotioncode) {
        this.promotioncode = promotioncode;
    }

    public String getContractstatuscode() {
        return contractstatuscode;
    }

    public void setContractstatuscode(String contractstatuscode) {
        this.contractstatuscode = contractstatuscode;
    }

    public String getOcsstatus() {
        return ocsstatus;
    }

    public void setOcsstatus(String ocsstatus) {
        this.ocsstatus = ocsstatus;
    }

    public String getAPIResponseTime() {
        Calendar nowDateTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
        return sdf.format(nowDateTime.getTime());
    }

    public BasicInfoReqBean getBaiscInfo(EPayBusinessConreoller epaybusinesscontroller) {
        BasicInfoReqBean basicinforeqbean = null;

        try {
            SoaProfile soa = new SoaProfile();
            String soaresult = soa.putSoaProxyletByMDN(this.mdn);
            SOAReqBean apirequestbean;// = new SOAReqBean();
            apirequestbean = soa.parseXMLString(soaresult);

            this.contractstatuscode = apirequestbean.getContract_status_code();
            this.promotioncode = apirequestbean.getPromotioncode();
            this.contractstatus = apirequestbean.getContract_status();
            this.contractid = apirequestbean.getContractid();

            EPAY_PROMOTIONCODE epaypromotioncode;// = new EPAY_PROMOTIONCODE();
            epaypromotioncode = epaybusinesscontroller.getPomtionCode(apirequestbean.getPromotioncode());

            if (epaypromotioncode != null) {

                this.platformtype = epaypromotioncode.getPlatformtype();

                SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                Calendar nowDateTime = Calendar.getInstance();
                String libm = sdf15.format(nowDateTime.getTime());

                switch (platformtype) {
                    case 1:
                        //ALU
                        if (aluflag) {
                            OCS4GBasicInfoUtil basicinfoutil = new OCS4GBasicInfoUtil();
                            SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);
                            String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());
                            String basicinfo = basicinfoutil.putOCS4GBasicInfoSlet(libm, mdn, tradeDate_Pincode);
                            log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfo);
                            basicinforeqbean = basicinfoutil.parseBasicInfoXMLString(basicinfo);
                            log.info("basicinforeqbean.getResultcode()========>" + basicinforeqbean.getResultcode());
                            log.info("basicinforeqbean.getLifeCycleState()====>" + basicinforeqbean.getLifeCycleState());
                            this.ocsstatus = getOC_Status(basicinforeqbean.getLifeCycleState());
                        } else {
                            log.info("aluflag ====>" + aluflag);
                            this.ocsstatus = "-1";
                        }
                        break;
                    case 2:
                        //ZTE
                        if (zteflag) {
                            ZTEOCS4GBasicInfoUtil ztebasicinfoutil = new ZTEOCS4GBasicInfoUtil();
                            String basicinfo = ztebasicinfoutil.putZTEOCS4GBasicInfoSlet(libm, mdn);
                            log.info("ZTE QueryBasicInfo===>MDN:" + mdn + " ZTE OCSXmlResult=>" + basicinfo);
                            basicinforeqbean = ztebasicinfoutil.parseZTEBasicInfoXMLString(basicinfo);
                            log.info("ZTE basicinforeqbean.getLifeCycleState()====>" + basicinforeqbean.getLifeCycleState());
                            this.ocsstatus = getOC_Status(basicinforeqbean.getLifeCycleState());
                        } else {
                            log.info("zteflag===>" + zteflag);
                            this.ocsstatus = "-1";
                        }
                        break;
                    case 3:
                        //ALU
                        log.info("Nokia QueryBasicInfo===>MDN:" + mdn + " Nokia OCSXmlResult=>Empty (ok)");
                        break;
                    default:
                        log.info("do nothing");
                        this.ocsstatus = "-1";
                        break;
                }
            } else {
                this.ocsstatus = "-1";
            }
        } catch (Exception ex) {
            log.info(ex);
        }
        return basicinforeqbean;
    }

    public String checkIBonePPMDN(EPayBusinessConreoller epaybusinesscontroller) {

        String result = "";

        try {
            SoaProfile soa = new SoaProfile();
            String soaresult = soa.putSoaProxyletByMDN(this.mdn);
            SOAReqBean apirequestbean;// = new SOAReqBean();
            apirequestbean = soa.parseXMLString(soaresult);

            this.contractstatuscode = apirequestbean.getContract_status_code();
            this.promotioncode = apirequestbean.getPromotioncode();
            this.contractstatus = apirequestbean.getContract_status();
            this.contractid = apirequestbean.getContractid();

            EPAY_PROMOTIONCODE epaypromotioncode;// = new EPAY_PROMOTIONCODE();
            epaypromotioncode = epaybusinesscontroller.getPomtionCode(apirequestbean.getPromotioncode());
            BasicInfoReqBean basicinforeqbean;// = new BasicInfoReqBean();

            if (epaypromotioncode != null) {

                this.platformtype = epaypromotioncode.getPlatformtype();

                SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                Calendar nowDateTime = Calendar.getInstance();
                String libm = sdf15.format(nowDateTime.getTime());

                switch (platformtype) {
                    case 1:
                        //ALU
                        if (aluflag) {
                            OCS4GBasicInfoUtil basicinfoutil = new OCS4GBasicInfoUtil();
                            SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);
                            String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());
                            String basicinfo = basicinfoutil.putOCS4GBasicInfoSlet(libm, mdn, tradeDate_Pincode);
                            log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfo);
                            basicinforeqbean = basicinfoutil.parseBasicInfoXMLString(basicinfo);
                            log.info("basicinforeqbean.getResultcode()========>" + basicinforeqbean.getResultcode());
                            log.info("basicinforeqbean.getLifeCycleState()====>" + basicinforeqbean.getLifeCycleState());
                            // soa pass, alu active --> 1
                            String tmpstatus = getOC_Status(basicinforeqbean.getLifeCycleState());
                            if ("1".equals(tmpstatus) || "2".equals(tmpstatus)) {
                                this.ocsstatus = "0"; //不可使用服務
                            } else {
                                this.ocsstatus = "-1";
                            }
//                            this.ocsstatus = getOC_Status(basicinforeqbean.getLifeCycleState());
                        } else {
                            log.info("aluflag ====>" + aluflag);
                            this.ocsstatus = "-1";
                        }
                        break;
                    case 2:
                        //ZTE
                        if (zteflag) {
                            ZTEOCS4GBasicInfoUtil ztebasicinfoutil = new ZTEOCS4GBasicInfoUtil();
                            String basicinfo = ztebasicinfoutil.putZTEOCS4GBasicInfoSlet(libm, mdn);
                            log.info("ZTE QueryBasicInfo===>MDN:" + mdn + " ZTE OCSXmlResult=>" + basicinfo);
                            basicinforeqbean = ztebasicinfoutil.parseZTEBasicInfoXMLString(basicinfo);
                            log.info("ZTE basicinforeqbean.getLifeCycleState()====>" + basicinforeqbean.getLifeCycleState());
                            log.info("ZTE contractstatuscode====>" + contractstatuscode);
//                            this.ocsstatus = getOC_Status(basicinforeqbean.getLifeCycleState());
                            String tmpstatus = getZTE_OCS_Status(basicinforeqbean.getLifeCycleState());
                            if ("1".equals(tmpstatus) || "2".equals(tmpstatus)) {
                                if ("9".equals(contractstatuscode) || "43".equals(contractstatuscode)) {
                                    this.ocsstatus = "1"; //可使用服務
                                } else {
                                    this.ocsstatus = "0"; //不可使用服務
                                }
                            } else {
                                this.ocsstatus = "-1";
                            }
                        } else {
                            log.info("zteflag===>" + zteflag);
                            this.ocsstatus = "-1";
                        }
                        break;
                    case 3:

                        //KK NOKIA
//                        NokiaMainLoginAndLifeCycleUtil nokia_util = new NokiaMainLoginAndLifeCycleUtil();
//                        String LC = "";
//                        try {
//                            String pid = nokia_util.login();
//                            LC = nokia_util.getMdnLifeCycle(libm, mdn, pid);
//                            boolean logoutflag = nokia_util.logout(pid);
//                            String tmpstatus = getNokai_OCS_Status(LC);
//                            if ("1".equals(tmpstatus) || "2".equals(tmpstatus)) {
//                                if ("9".equals(contractstatuscode) || "43".equals(contractstatuscode)) {
//                                    this.ocsstatus = "1"; //可使用服務
//                                } else {
//                                    this.ocsstatus = "0"; //不可使用服務
//                                }
//                            } else {
//                                this.ocsstatus = "-1";
//                            }
//                        } catch (Exception ex) {
//                            log.info(ex);
//                        }
                        break;
                    default:
                        log.info("do nothing");
                        this.ocsstatus = "-1";
                        break;
                }
            } else {
                this.ocsstatus = "-1";
            }
//            boolean kkresult = !"-1".equals(this.ocsstatus) && ("9".equals(this.contractstatuscode) || "43".endsWith(this.contractstatuscode));

            result = this.ocsstatus;

        } catch (Exception ex) {
            log.info(ex);
        }
        return result;
    }

    public String checkPPMDN(EPayBusinessConreoller epaybusinesscontroller) {

        String result = "";

        try {
            SoaProfile soa = new SoaProfile();
            String soaresult = soa.putSoaProxyletByMDN(this.mdn);
            SOAReqBean apirequestbean;// = new SOAReqBean();
            apirequestbean = soa.parseXMLString(soaresult);

            this.contractstatuscode = apirequestbean.getContract_status_code();
            this.promotioncode = apirequestbean.getPromotioncode();
            this.contractstatus = apirequestbean.getContract_status();
            this.contractid = apirequestbean.getContractid();

            EPAY_PROMOTIONCODE epaypromotioncode;// = new EPAY_PROMOTIONCODE();
            epaypromotioncode = epaybusinesscontroller.getPomtionCode(apirequestbean.getPromotioncode());
            BasicInfoReqBean basicinforeqbean;// = new BasicInfoReqBean();

            log.info(mdn + " Platformtype====>" + platformtype);
            log.info(mdn + " contractstatuscode===>" +  apirequestbean.getContract_status_code());
            log.info(mdn + " Promtion Code========>" + apirequestbean.getPromotioncode());
            log.info(mdn + " Contract_status======>" + apirequestbean.getContract_status());

            if (epaypromotioncode != null) {

                this.platformtype = epaypromotioncode.getPlatformtype();
                log.info(mdn + " platformtype==>" + platformtype);

                SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                Calendar nowDateTime = Calendar.getInstance();
                String libm = sdf15.format(nowDateTime.getTime());

                if (platformtype == 1) {
                    //ALU
                    if (aluflag) {
                        OCS4GBasicInfoUtil basicinfoutil = new OCS4GBasicInfoUtil();
                        SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);
                        String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());
                        String basicinfo = basicinfoutil.putOCS4GBasicInfoSlet(libm, mdn, tradeDate_Pincode);
                        log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfo);
                        basicinforeqbean = basicinfoutil.parseBasicInfoXMLString(basicinfo);
                        log.info("basicinforeqbean.getResultcode()========>" + basicinforeqbean.getResultcode());
                        log.info("basicinforeqbean.getLifeCycleState()====>" + basicinforeqbean.getLifeCycleState());
                        // soa pass, alu active --> 1
                        String tmpstatus = getOC_Status(basicinforeqbean.getLifeCycleState());
                        if ("1".equals(tmpstatus) || "2".equals(tmpstatus)) {
                            this.ocsstatus = "0"; //不可使用服務
                        } else {
                            this.ocsstatus = "-1";
                        }
//                            this.ocsstatus = getOC_Status(basicinforeqbean.getLifeCycleState());
                    } else {
                        log.info("aluflag ====>" + aluflag);
                        this.ocsstatus = "-1";
                    }

                } else if (platformtype == 2) {
                    //ZTE
                    if (zteflag) {
                        ZTEOCS4GBasicInfoUtil ztebasicinfoutil = new ZTEOCS4GBasicInfoUtil();
                        String basicinfo = ztebasicinfoutil.putZTEOCS4GBasicInfoSlet(libm, mdn);
                        log.info("ZTE QueryBasicInfo===>MDN:" + mdn + " ZTE OCSXmlResult=>" + basicinfo);
                        basicinforeqbean = ztebasicinfoutil.parseZTEBasicInfoXMLString(basicinfo);
                        log.info("ZTE basicinforeqbean.getLifeCycleState()====>" + basicinforeqbean.getLifeCycleState());
                        log.info("ZTE contractstatuscode====>" + contractstatuscode);
//                            this.ocsstatus = getOC_Status(basicinforeqbean.getLifeCycleState());
                        String tmpstatus = getZTE_OCS_Status(basicinforeqbean.getLifeCycleState());
                        if ("1".equals(tmpstatus) || "2".equals(tmpstatus)) {
                            if ("9".equals(contractstatuscode) || "43".equals(contractstatuscode)) {
                                this.ocsstatus = "1"; //可使用服務
                            } else {
                                this.ocsstatus = "0"; //不可使用服務
                            }
                        } else {
                            this.ocsstatus = "-1";
                        }
                    } else {
                        log.info("zteflag===>" + zteflag);
                        this.ocsstatus = "-1";
                    }

                } else if (platformtype == 3) { //NOKIA

                    //KK NOKIA
                    log.info(mdn + " Nokia Platformtype====>" + platformtype);
                    NokiaMainLoginAndLifeCycleUtil nokia_util = new NokiaMainLoginAndLifeCycleUtil();
                    String LC = "";
                    try {
                        String pid = nokia_util.login();
                        LC = nokia_util.getMdnLifeCycle(libm, mdn, pid);
                        log.info(mdn + " GetNokai_OCS_Status:" + LC);
                        boolean logoutflag = nokia_util.logout(pid);
                        log.info(mdn + " logoutflag==>" + logoutflag);

                        String tmpstatus = getNokai_OCS_Status(LC);
                        log.info(mdn + " GetNokai_OCS_Status:" + LC + "===>getNokai_OCS_Status(1,2):" + tmpstatus);
                        log.info(mdn + " contractstatuscode:" + contractstatuscode);

                        if ("1".equals(tmpstatus) || "2".equals(tmpstatus)) {
                            if ("9".equals(contractstatuscode) || "43".equals(contractstatuscode)) {
                                this.ocsstatus = "1"; //可使用服務
                            } else {
                                this.ocsstatus = "0"; //不可使用服務
                            }
                        } else {
                            this.ocsstatus = "-1";
                        }
                        log.info(mdn + " Nokai ocsstatus(1:ok,0:bad)===>" + ocsstatus);
                    } catch (Exception ex) {
                        log.info(ex);
                    }
                } else {

                    log.info("do nothing");
                    this.ocsstatus = "-1";

                }
            } else {
                this.ocsstatus = "-1";
            }
//            boolean kkresult = !"-1".equals(this.ocsstatus) && ("9".equals(this.contractstatuscode) || "43".endsWith(this.contractstatuscode));

            result = this.ocsstatus;

        } catch (Exception ex) {
            log.info(ex);
        }
        return result;
    }

    private String getOC_Status(String str) {
        String result;// = "0";
        if ("pre-active".equalsIgnoreCase(str)) {
            result = "0";
        } else if ("Active".equalsIgnoreCase(str)) {
            result = "1";
        } else if ("Two-Way Block".equalsIgnoreCase(str)) {
            result = "2";
        } else {
            result = "0";
        }
        return result;
    }

    private String getNokai_OCS_Status(String str) {
        String result;// = "0";
        if ("Preactive".equalsIgnoreCase(str)) {
            result = "1";
        } else if ("Active".equalsIgnoreCase(str)) {
            result = "1";
        } else if ("Expired".equalsIgnoreCase(str)) {
            result = "2";
        } else {
            result = "0";
        }
        
        log.info("OCS LC==>"+str+", return "+ result);
        
        return result;
    }

    private String getZTE_OCS_Status(String str) {
        String result;// = "0";
        if ("pre-active".equalsIgnoreCase(str)) {
            result = "1";
        } else if ("Active".equalsIgnoreCase(str)) {
            result = "1";
        } else if ("Two-Way Block".equalsIgnoreCase(str)) {
            result = "2";
        } else {
            result = "0";
        }
        return result;
    }

    public UserAccountLookupReqBean DealerUserAccountParseXMLString(String xmlRecords, String TagName) throws Exception {
        UserAccountLookupReqBean useraccountlookupbean = new UserAccountLookupReqBean();

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
                useraccountlookupbean.setMdn(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("STOREID");
                line = (Element) nodes.item(0);
                useraccountlookupbean.setStoreid(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("STORENAME");
                line = (Element) nodes.item(0);
                useraccountlookupbean.setStorename(getCharacterDataFromElement(line));

            }

        } catch (IOException e) {
            log.info(e);
        } catch (ParserConfigurationException e) {
            log.info(e);
        } catch (SAXException e) {
            log.info(e);
        }
        return useraccountlookupbean;
    }

    public String getMDNByparseXMLString(String xmlRecords, String TagName) throws Exception {
        String result;
        result = "";
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
                result = getCharacterDataFromElement(line);
            }

        } catch (IOException e) {
            log.info(e);
        } catch (ParserConfigurationException e) {
            log.info(e);
        } catch (SAXException e) {
            log.info(e);
        }
        return result;
    }

    public IbonReqBean getIBoneMDNByparseXMLString(String xmlRecords, String TagName) throws Exception {

        IbonReqBean aIbonBean = new IbonReqBean();
//        String result;
//        result = "";
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
                aIbonBean.setMdn(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("VALUE");
                line = (Element) nodes.item(0);
                aIbonBean.setValue(getCharacterDataFromElement(line));
            }

        } catch (IOException e) {
            log.info(e);
        } catch (ParserConfigurationException e) {
            log.info(e);
        } catch (SAXException e) {
            log.info(e);
        }
        return aIbonBean;
    }

    private String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }

    public ServiceOrderStatusReqBean ServiceOrderStatusParseXMLString(String xmlRecords, String TagName) {
        ServiceOrderStatusReqBean aPIServiceOrdrReqBean = new ServiceOrderStatusReqBean();

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

                nodes = element.getElementsByTagName("CPLIBM");
                line = (Element) nodes.item(0);
                aPIServiceOrdrReqBean.setLibm(getCharacterDataFromElement(line));

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

    public VoucherChangeStatusReqBean VoucherChangeStatusParseXMLString(String xmlRecords, String TagName) {
        VoucherChangeStatusReqBean aPIVoucherChangeStatusoReqBean = new VoucherChangeStatusReqBean();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));

            Document doc = db.parse(is);
            NodeList SOARes = doc.getElementsByTagName(TagName);

            for (int i = 0; i < SOARes.getLength(); i++) {

                Element element = (Element) SOARes.item(i);

                NodeList nodes = element.getElementsByTagName("STARTSERIALNO");
                Element line = (Element) nodes.item(0);
                aPIVoucherChangeStatusoReqBean.setStartserialno(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("ENDSERIALNO");
                line = (Element) nodes.item(0);
                aPIVoucherChangeStatusoReqBean.setEndserialno(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("STOREID");
                line = (Element) nodes.item(0);
                aPIVoucherChangeStatusoReqBean.setStoreid(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("STORENAME");
                line = (Element) nodes.item(0);
                aPIVoucherChangeStatusoReqBean.setStorename(getCharacterDataFromElement(line));

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return aPIVoucherChangeStatusoReqBean;
    }

    public VoucherInfoReqBean VoucherInfoParseXMLString(String xmlRecords, String TagName) {
        VoucherInfoReqBean aPIVoucherInfoReqBean = new VoucherInfoReqBean();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));

            Document doc = db.parse(is);
            NodeList SOARes = doc.getElementsByTagName(TagName);

            for (int i = 0; i < SOARes.getLength(); i++) {

                Element element = (Element) SOARes.item(i);

                NodeList nodes = element.getElementsByTagName("VOUCHERCARD");
                Element line = (Element) nodes.item(0);
                aPIVoucherInfoReqBean.setVoucherCard(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("STOREID");
                line = (Element) nodes.item(0);
                aPIVoucherInfoReqBean.setStoreId(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("STORENAME");
                line = (Element) nodes.item(0);
                aPIVoucherInfoReqBean.setStoreName(getCharacterDataFromElement(line));

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return aPIVoucherInfoReqBean;
    }

    public PincodeOrderReqBean PincodeOrderServParseXMLString(String xmlRecords, String TagName) {

        PincodeOrderReqBean aPIPincodeOrderReqBean = new PincodeOrderReqBean();

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
                aPIPincodeOrderReqBean.setMdn(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("PINCODE");
                line = (Element) nodes.item(0);
                aPIPincodeOrderReqBean.setPincode(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("SALESID");
                line = (Element) nodes.item(0);
                aPIPincodeOrderReqBean.setSaleid(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("STOREID");
                line = (Element) nodes.item(0);
                aPIPincodeOrderReqBean.setStoreid(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("STORENAME");
                line = (Element) nodes.item(0);
                aPIPincodeOrderReqBean.setStroename(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("APISRCID");
                line = (Element) nodes.item(0);
                aPIPincodeOrderReqBean.setApisrcid(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("PAYTOOL");
                line = (Element) nodes.item(0);
                aPIPincodeOrderReqBean.setPaytool(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("PAYNAME");
                line = (Element) nodes.item(0);
                aPIPincodeOrderReqBean.setPayname(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("CPLIBM");
                line = (Element) nodes.item(0);
                aPIPincodeOrderReqBean.setCplibm(getCharacterDataFromElement(line));
            }

        } catch (IOException e) {
            log.info(e);
        } catch (ParserConfigurationException e) {
            log.info(e);
        } catch (SAXException e) {
            log.info(e);
        }
        return aPIPincodeOrderReqBean;
    }

    public PincodeOrderReqBean DearlerPincodeOrderServParseXMLString(String xmlRecords, String TagName) {

        PincodeOrderReqBean aPIPincodeOrderReqBean = new PincodeOrderReqBean();

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
                aPIPincodeOrderReqBean.setMdn(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("CARDPASS");
                line = (Element) nodes.item(0);
                aPIPincodeOrderReqBean.setPincode(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("CARDNO");
                line = (Element) nodes.item(0);
                aPIPincodeOrderReqBean.setCardno(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("SALESID");
                line = (Element) nodes.item(0);
                aPIPincodeOrderReqBean.setSaleid(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("STOREID");
                line = (Element) nodes.item(0);
                aPIPincodeOrderReqBean.setStoreid(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("STORENAME");
                line = (Element) nodes.item(0);
                aPIPincodeOrderReqBean.setStroename(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("APISRCID");
                line = (Element) nodes.item(0);
                aPIPincodeOrderReqBean.setApisrcid(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("PAYTOOL");
                line = (Element) nodes.item(0);
                aPIPincodeOrderReqBean.setPaytool(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("PAYNAME");
                line = (Element) nodes.item(0);
                aPIPincodeOrderReqBean.setPayname(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("CPLIBM");
                line = (Element) nodes.item(0);
                aPIPincodeOrderReqBean.setCplibm(getCharacterDataFromElement(line));
            }

        } catch (IOException e) {
            log.info(e);
        } catch (ParserConfigurationException e) {
            log.info(e);
        } catch (SAXException e) {
            log.info(e);
        }
        return aPIPincodeOrderReqBean;
    }

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

                nodes = element.getElementsByTagName("SALESID");
                line = (Element) nodes.item(0);
                aPIServiceOrdrReqBean.setSaleid(getCharacterDataFromElement(line));

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

    public String getAccountBalance(EPayBusinessConreoller epaybusinesscontroller) {

        String returnStr = "";
        BasicInfoReqBean basicinforeqbean = getBaiscInfo(epaybusinesscontroller);
        if (basicinforeqbean != null) {
            String BOCKET_VOICE = basicinforeqbean.getCounterValue1();//1+61
            String BOCKET_VOICE_EXTRA = basicinforeqbean.getCounterValue2();//3+4+7+62
            String BOCKET_DATA = basicinforeqbean.getCounterValue3(); //71
            String BOCKET_DATA_EXPDATE = basicinforeqbean.getEndDate3();
            String BOCKET_DATA_EXTRA = basicinforeqbean.getCounterValue4();
            String BOCKET_DATA_EXTRA_EXPDATE = basicinforeqbean.getEndDate4();
            String BOCKET_VOICE_ONNET = basicinforeqbean.getCounterValue5();
            String BOCKET_VOICE_ONNET_EXPDATE = basicinforeqbean.getEndDate5();
            String BOCKET_VOICE_ONNETFREE_EXPDATE = basicinforeqbean.getEndDate6();
            String BOCKET_DATA_DAYPASS_EXPDATE = basicinforeqbean.getEndDate7();

            /*
                        通信費(元)：通信基本費(Bocket ID 1+61)+通信贈送費(Bocket ID 3+4+7+62)
                        數據上網：數據基本量(Bocket ID 71)+數據贈送量(Bocket ID 72)
                        網內免費通話：(Bocket ID 81)
                        30天免費網內語音服務使用到期日：(Bocket ID 2,82帳本日期選擇最晚的一組到期日)
                        計日型數據服務使用到期日：(Bocket ID 75,76,77,78帳本日期選擇最晚的一組到期日)
             */
            float intVoice = Float.valueOf(BOCKET_VOICE) + Float.valueOf(BOCKET_VOICE_EXTRA);
            float intData = Float.valueOf(BOCKET_DATA) + Float.valueOf(BOCKET_DATA_EXTRA);
            float intOnnet = Float.valueOf(BOCKET_VOICE_ONNET);

            DecimalFormat decimalFormat = new DecimalFormat("####0.00");//小數點第二位下四捨五入
            String strVoice = decimalFormat.format(intVoice);
            String strData = decimalFormat.format(intData);
            String strOnnet = decimalFormat.format(intOnnet);

            returnStr = "<ACCOUNT_BALANCE>" + strVoice + "</ACCOUNT_BALANCE>";
        }
        return returnStr;
    }

    public String getBasicOCSInfo(EPayBusinessConreoller epaybusinesscontroller) {

        String returnStr = "";

        BasicInfoReqBean basicinforeqbean = getBaiscInfo(epaybusinesscontroller);

        if (basicinforeqbean != null) {
            String BOCKET_VOICE = basicinforeqbean.getCounterValue1();//1+61
            String BOCKET_VOICE_EXTRA = basicinforeqbean.getCounterValue2();//3+4+7+62
            String BOCKET_DATA = basicinforeqbean.getCounterValue3(); //71
            String BOCKET_DATA_EXPDATE = basicinforeqbean.getEndDate3();
            String BOCKET_DATA_EXTRA = basicinforeqbean.getCounterValue4();
            String BOCKET_DATA_EXTRA_EXPDATE = basicinforeqbean.getEndDate4();
            String BOCKET_VOICE_ONNET = basicinforeqbean.getCounterValue5();
            String BOCKET_VOICE_ONNET_EXPDATE = basicinforeqbean.getEndDate5();
            String BOCKET_VOICE_ONNETFREE_EXPDATE = basicinforeqbean.getEndDate6();
            String BOCKET_DATA_DAYPASS_EXPDATE = basicinforeqbean.getEndDate7();

            /*
                        通信費(元)：通信基本費(Bocket ID 1+61)+通信贈送費(Bocket ID 3+4+7+62)
                        數據上網：數據基本量(Bocket ID 71)+數據贈送量(Bocket ID 72)
                        網內免費通話：(Bocket ID 81)
                        30天免費網內語音服務使用到期日：(Bocket ID 2,82帳本日期選擇最晚的一組到期日)
                        計日型數據服務使用到期日：(Bocket ID 75,76,77,78帳本日期選擇最晚的一組到期日)
             */
            float intVoice = Float.valueOf(BOCKET_VOICE) + Float.valueOf(BOCKET_VOICE_EXTRA);
            float intData = Float.valueOf(BOCKET_DATA) + Float.valueOf(BOCKET_DATA_EXTRA);
            float intOnnet = Float.valueOf(BOCKET_VOICE_ONNET);

            DecimalFormat decimalFormat = new DecimalFormat("####0.00");//小數點第二位下四捨五入
            String strVoice = decimalFormat.format(intVoice);
            String strData = decimalFormat.format(intData);
            String strOnnet = decimalFormat.format(intOnnet);

            returnStr = "<ACCOUNT_BALANCE>" + strVoice + "</ACCOUNT_BALANCE>\n"
                            + "<SUMUPINFO>\n"
                            + "1.通信費(含贈送帳本)(元)：" + strVoice + "\n"
                            + "2.數據上網(含贈送帳本)(MB)：" + strData + "\n"
                            + "3.網內免費通話(贈送帳本)(元)：" + strOnnet + "\n";
            int itemcount = 4;
            if (!"".equals(BOCKET_VOICE_ONNETFREE_EXPDATE)) {
                returnStr = returnStr + itemcount + ".30天免費網內語音服務使用到期日：" + BOCKET_VOICE_ONNETFREE_EXPDATE + "\n";
                itemcount = itemcount + 1;
            }
            if (!"".equals(BOCKET_DATA_DAYPASS_EXPDATE)) {
                returnStr = returnStr + itemcount + ".計日型數據服務使用到期日：" + BOCKET_DATA_DAYPASS_EXPDATE + "\n";
            }
            returnStr = returnStr + "★僅為系統即時資訊，將因您使用行為變動，歡迎您使用Gt網站/987 IVR進行查詢。";
            returnStr = returnStr + "</SUMUPINFO>";

        }
        return returnStr;
    }

    public boolean checkAPISrcID(String srcid) {
        boolean result = false;
        String apiscrid = new ShareParm().PARM_APISRCID;
        String[] AfterSplit = apiscrid.split(",");
        for (int i = 0; i < AfterSplit.length; i++) {
            if (AfterSplit[i].endsWith(srcid)) {
                result = true;
                return result;
            }
        }
        return result;
    }

    public boolean checkPaytoolID(String paytool) {
        boolean result = false;
        String paytoolid = new ShareParm().PARM_PAYTOOL;
        String[] AfterSplit = paytoolid.split(",");
        for (int i = 0; i < AfterSplit.length; i++) {
            if (AfterSplit[i].endsWith(paytool)) {
                result = true;
                return result;
            }
        }
        return result;
    }

    public String getServiceIDByparseXMLString(String xmlRecords, String TagName) throws Exception {
        String result;
        result = "";
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));

            Document doc = db.parse(is);
            NodeList SOARes = doc.getElementsByTagName(TagName);

            for (int i = 0; i < SOARes.getLength(); i++) {

                Element element = (Element) SOARes.item(i);

                NodeList nodes = element.getElementsByTagName("SERVICEID");
                Element line = (Element) nodes.item(0);
                result = getCharacterDataFromElement(line);
            }

        } catch (IOException e) {
            log.info(e);
        } catch (ParserConfigurationException e) {
            log.info(e);
        } catch (SAXException e) {
            log.info(e);
        }
        return result;
    }
}
