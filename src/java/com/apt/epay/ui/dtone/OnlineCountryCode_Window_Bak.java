/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.dtone;

import com.apt.epay.beans.BasicInfoReqBean;
import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.deposit.bean.NOSSOReqBean;
import com.apt.epay.deposit.util.toolUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.epay.ui.dtone.util.DTONEUtil;
import com.apt.epay.zte.util.ZTEAdjustAccUtil;
import com.apt.epay.zte.util.ZTEOCS4GBasicInfoUtil;
import com.apt.util.HttpClientUtil;
import com.apt.util.SecuredMsg;
import com.apt.util.SoaProfile;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_PROMOTIONCODE;
import com.epay.ejb.bean.EPAY_SERVICE_INFO;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class OnlineCountryCode_Window_Bak extends Window {

    private final Logger log = Logger.getLogger("EPAY");

    SOAReqBean apirequestbean;
    private final String xcode = "家鄉儲";
    private boolean rflag = false;

    private final String returnUrl = "http://localhost:8080/cpCreditTest/cpCreditCardResp.jsp";
    private final String APTMIN = "46605";
    private final String[] radio_serviceid = new String[100];
    private String cpid, mdn, mdn1, mdn2, cplibm, salesid, storeid, storename, apisrcid;
    private String libm = "";
    private String promotioncode;
    private String contractid;
    private final Integer XValue = 10000;
    private String[] ch_serviceid = new String[100];
    private String receiver_mdn = "";
    private String data = "";

    public void onCreate() throws Exception {

        Sessions.getCurrent().removeAttribute("odBean");
        log.info("Creating Online Deposit...from IP " + Executions.getCurrent().getRemoteAddr());

        HttpServletRequest req = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
        log.info("doAfterCompose.getQueryString():" + req.getQueryString());

        String x_up_calling_line_id = "";
        String x_up_min = "";
        String x_up_imsi = "";
        String min = "";

        Enumeration enuma = req.getHeaderNames();
        while (enuma.hasMoreElements()) {
            String header_name = (String) enuma.nextElement();
            String header_value = req.getHeader(header_name);
            String head_info = String.format("header_name<=>[%s], header_value<=>[%s]", header_name, header_value);
            log.info(head_info + "\n");
            // X-Up-Min
            if (header_name != null) {
//                log.info("-------------------------------------------------------------------");
//                log.info("[" + header_name + "] is " + "[" + header_value + "]");
//                log.info("-------------------------------------------------------------------");
                if (header_name.equalsIgnoreCase("x-up-calling-line-id")) {
                    x_up_calling_line_id = header_value;
                }
                if (header_name.equalsIgnoreCase("x-up-min")) {
                    x_up_min = header_value;
                }
                //
                if (header_name.equalsIgnoreCase("x-up-imsi")) {
                    x_up_imsi = header_value;
                }
            }
        }

        String headerMdn = x_up_calling_line_id;
        String headerMin = x_up_min;
        String headeImsi = x_up_imsi;

        log.info("headerMdn==========>" + headerMdn);
        log.info("headerMin==========>" + headerMin);
        log.info("headeImsi==========>" + headeImsi);

        if (!"".equals(headeImsi)) {

            if (APTMIN.equals(headeImsi.substring(0, 5))) {
                min = headeImsi.substring(5);

                toolUtil tool = new toolUtil();
                Utilities util = new Utilities();

                cpid = req.getParameter("CPID");
                data = req.getParameter("DATA");

                log.info("cpid==>" + cpid);
                log.info("data==>" + data);

                try {
                    EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

                    int icpid = Integer.valueOf(cpid);
                    EPAY_CP_INFO cpinfo = epaybusinesscontroller.getCpInfo(icpid);

                    String deskey = "";
                    boolean cpkeyflag = false;

                    if (cpinfo != null) {
                        deskey = cpinfo.getPoskey();
                        log.info("deskey===>" + deskey);
                        if (!"".equals(deskey)) {
                            cpkeyflag = true;
                        }
                    } else {
                        //cpinfo is null
                        log.info("cannot get cpinfo & pos key");
                    }

                    if (cpkeyflag) {
                        String str_input = util.decrypt(deskey, data);
                        log.info("INPUT==>" + str_input);
                        NOSSOReqBean aPIServiceOrdrReqBean;
                        aPIServiceOrdrReqBean = getMDNByparseXMLString(str_input, "ServiceAdjustAccountDeposit");
                        mdn1 = aPIServiceOrdrReqBean.getMdn1();
                        mdn2 = aPIServiceOrdrReqBean.getMdn2();
                        cplibm = aPIServiceOrdrReqBean.getCplibm();
                        apisrcid = aPIServiceOrdrReqBean.getApisrcid();
                        salesid = aPIServiceOrdrReqBean.getSalesid();
                        storeid = aPIServiceOrdrReqBean.getStoreid();
                        storename = aPIServiceOrdrReqBean.getStorename();

                        Textbox textbox_mdn = (Textbox) this.getFellow("textbox_mdn");
                        Textbox textbox_dtone_mdn = (Textbox) this.getFellow("textbox_dtone_mdn");

                        Combobox combo_country_type = (Combobox) getSpaceOwner().getFellow("combo_country_type");

                        if (!"".equals(min)) {

                            SoaProfile soa = new SoaProfile();
                            String result = soa.putSoaProxyletByMIN(min);
                            apirequestbean = soa.parseXMLString(result);

                            log.info("kkflag==>NoSSO_AdjustAccountDeposit:" + result);

                            String resultcode = apirequestbean.getResultcode();
                            contractid = apirequestbean.getContractid();
                            String contractstatuscode = apirequestbean.getContract_status_code();
                            String producttype = apirequestbean.getProducttype();
                            mdn = apirequestbean.getMdn();
                            promotioncode = apirequestbean.getPromotioncode();
//                        String name;
                            textbox_mdn.setValue(mdn);

                            if ("00000000".equals(resultcode)) {

                                EPAY_PROMOTIONCODE epaypromotioncode;// = new EPAY_PROMOTIONCODE();
                                epaypromotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);
                                int platformtype = epaypromotioncode.getPlatformtype();

                                BasicInfoReqBean basicinforeqbean;// = new BasicInfoReqBean();

                                if (platformtype == 2) { //ZTE
                                    SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                                    Calendar nowDateTime = Calendar.getInstance();
                                    libm = sdf15.format(nowDateTime.getTime());

                                    ZTEOCS4GBasicInfoUtil ztebasicinfoutil = new ZTEOCS4GBasicInfoUtil();
                                    String basicinfo = ztebasicinfoutil.putZTEOCS4GBasicInfoSlet(libm, mdn);
                                    log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfo);
//
                                    basicinforeqbean = ztebasicinfoutil.parseZTEBasicInfoXMLString(basicinfo);

                                    log.info("basicinforeqbean.getReal_LifeCycleState()====>" + basicinforeqbean.getReal_LifeCycleState());

                                    if (("ACTIVE".equalsIgnoreCase(basicinforeqbean.getReal_LifeCycleState()))) {
                                        //Test For 金額比對
                                        ZTEOCS4GBasicInfoUtil ztebasicinfoutilx = new ZTEOCS4GBasicInfoUtil();
                                        String basicinfox = ztebasicinfoutilx.putZTEOCS4GBasicInfoSlet(libm, mdn);
                                        log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfox);

                                        for (int countryid = 1; countryid <= 4; countryid++) {
                                            Comboitem coitem_serviceid = new Comboitem();
                                            String countryLabel = GetCountryLabel(countryid);
                                            coitem_serviceid.setId("combo" + countryid);

                                            coitem_serviceid.setLabel(countryLabel);
                                            combo_country_type.appendChild(coitem_serviceid);
                                            ch_serviceid[countryid] = String.valueOf(countryid);
                                        }

                                    } else {
                                        log.info(mdn + ":LifeCycleState()==>" + basicinforeqbean.getLifeCycleState());
                                        log.info(mdn + ":RealLifeCycleState()==>" + basicinforeqbean.getReal_LifeCycleState());
                                        this.detach();
                                        Executions.sendRedirect("/dtone/OnlineNoSSOSSOErrorMsg.zhtml");
                                    }
                                } else {
                                    log.info(mdn + ":platformtype==>" + platformtype);
                                    this.detach();
                                    Executions.sendRedirect("/dtone/OnlineNoSSOSSOErrorMsg.zhtml");
                                }

                            } else {
                                log.info(mdn + ":resultcode==>" + resultcode);
                                this.detach();
                                Executions.sendRedirect("/dtone/OnlineNoSSOSSOErrorMsg.zhtml");
                            }

                        } else {
                            log.info("Min is null");
                            this.detach();
                            Executions.sendRedirect("/dtone/OnlineNoSSOSSOErrorMsg.zhtml");
                        }

                    } else {
                        // // no cpid 
                    }
                } catch (Exception ex) {
                    log.info(ex);
                }
            } else {
                log.info(headeImsi + " headeImsi NOT APTG");
                this.detach();
                Executions.sendRedirect("/dtone/OnlineNoSSOSSOErrorMsg.zhtml");
            }
        } else {
            log.info(headeImsi + " headeImsi is Null");
            this.detach();
            Executions.sendRedirect("/dtone/OnlineNoSSOSSOErrorMsg.zhtml");
        }

    }

    public void sendBtn() throws Exception {

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Combobox combo_country_type = (Combobox) getSpaceOwner().getFellow("combo_country_type");
        Textbox textbox_mdn = (Textbox) this.getFellow("textbox_mdn");
        Textbox textbox_dtone_mdn = (Textbox) this.getFellow("textbox_dtone_mdn");

        String mdn = textbox_mdn.getValue();
        String dtone_mdn = textbox_dtone_mdn.getValue();

        if (!"".equalsIgnoreCase(dtone_mdn)) {
            DTONEUtil dtone = new DTONEUtil();
            String dt_operator_name = dtone.getMobileNumberInfo(dtone_mdn);

            log.info("mdn====>" + mdn);
            log.info("dt_operator_name====>" + dt_operator_name);

            if (!"".equalsIgnoreCase(dt_operator_name)) {

                String countryId = ch_serviceid[combo_country_type.getSelectedIndex()];
                log.info(dtone_mdn + ":get country code ==>" + countryId);

                HttpClientUtil httpx = new HttpClientUtil();
                NameValuePair[] requestBody = null;
                postToNextPage(cpid, data, dtone_mdn);
            } else {
                log.info("dt_operator_name is null");
            }
        } else {
            log.info("dtone_mdn is null");
        }
    }

    private String GetCountryLabel(int code) {
        String countryLabel = "";

        switch (code) {
            case 1:
                countryLabel = "越南 Tiếng việt";
                break;
            case 2:
                countryLabel = "印尼 Indonesia";
                break;
            case 3:
                countryLabel = "菲律賓  Pilipinas";
                break;
            case 4:
                countryLabel = "泰國  ประเทศไทย";
                break;
            default:
                break;
        }
        return countryLabel;
    }

    public NOSSOReqBean getMDNByparseXMLString(String xmlRecords, String TagName) throws Exception {
        NOSSOReqBean nossobean = new NOSSOReqBean();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));

            Document doc = db.parse(is);
            NodeList SOARes = doc.getElementsByTagName(TagName);

            for (int i = 0; i < SOARes.getLength(); i++) {

                Element element = (Element) SOARes.item(i);

                NodeList nodes = element.getElementsByTagName("MDN1");
                Element line = (Element) nodes.item(0);
                nossobean.setMdn1(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("MDN2");
                line = (Element) nodes.item(0);
                nossobean.setMdn2(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("CPLIBM");
                line = (Element) nodes.item(0);
                nossobean.setCplibm(getCharacterDataFromElement(line));

                //    private String mdn1, mdn2, cplibm, salesid, storeid, storename, apisrcid;
                nodes = element.getElementsByTagName("SALESID");
                line = (Element) nodes.item(0);
                nossobean.setSalesid(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("STOREID");
                line = (Element) nodes.item(0);
                nossobean.setStoreid(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("STORENAME");
                line = (Element) nodes.item(0);
                nossobean.setStorename(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("APISRCID");
                line = (Element) nodes.item(0);
                nossobean.setApisrcid(getCharacterDataFromElement(line));
            }

        } catch (IOException e) {
            log.info(e);
        } catch (ParserConfigurationException e) {
            log.info(e);
        } catch (SAXException e) {
            log.info(e);
        }
        return nossobean;
    }

    private String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }

    private void postToNextPage(String systemid, String data, String dtone_mdn) throws Exception {

        Sessions.getCurrent().setAttribute("cpid", systemid);
        Sessions.getCurrent().setAttribute("data", data);
        Sessions.getCurrent().setAttribute("dtone_mdn", dtone_mdn);

        String sendURL = "http://epaytde.aptg.com.tw/EPAY/dtone/OnlineCountryCodeDeposit.zhtml?CPID=" + systemid;

        log.info("systemid,data,sendURL===>" + systemid + "," + data + "," + sendURL);
        Sessions.getCurrent().setAttribute("sendURL", sendURL);
        Executions.getCurrent().setAttribute(org.zkoss.zk.ui.sys.Attributes.NO_CACHE, Boolean.TRUE);

        Window window = (Window) Executions.createComponents("/dtone/OnlineDepositSend.zul", null, null);
    }
}
