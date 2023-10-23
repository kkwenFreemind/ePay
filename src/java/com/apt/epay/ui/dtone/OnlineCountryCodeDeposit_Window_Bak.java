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
import com.apt.epay.ui.dtone.util.DTONEUtil;
import com.apt.epay.zte.util.ZTEAdjustAccUtil;
import com.apt.epay.zte.util.ZTEOCS4GBasicInfoUtil;
import com.apt.util.SoaProfile;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_PROMOTIONCODE;
import com.epay.ejb.bean.EPAY_SERVICE_INFO;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
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
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class OnlineCountryCodeDeposit_Window_Bak extends Window {

    private final Logger log = Logger.getLogger("EPAY");

    SOAReqBean apirequestbean;
    private final String xcode = "家鄉儲";
    private boolean rflag = false;

    private final String returnUrl = "http://localhost:8080/cpCreditTest/cpCreditCardResp.jsp";
    private final String[] radio_serviceid = new String[100];
    private String cpid, mdn, mdn1, mdn2, cplibm, salesid, storeid, storename, apisrcid;
    private String libm = "";
    private String promotioncode;
    private String contractid;
    private final Integer XValue = 10000;
    private String[] ch_serviceid = new String[100];
    private String receiver_mdn = "";

    public void onCreate() throws Exception {

        Sessions.getCurrent().removeAttribute("odBean");
        log.info("Creating Online Deposit...from IP " + Executions.getCurrent().getRemoteAddr());

        HttpServletRequest req = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
        log.info("doAfterCompose.getQueryString():" + req.getQueryString());

        toolUtil tool = new toolUtil();
        Utilities util = new Utilities();

        cpid = req.getParameter("CPID");
        String data = req.getParameter("DATA");
        String dtone_mdn = req.getParameter("dtone_mdn");

        log.info("cpid==>" + cpid);
        log.info("data==>" + data);
        log.info("dtone_mdn==>" + dtone_mdn);

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
                
                

                if (mdn1 != null && !"".equals(mdn1)) {

                    DTONEUtil dtone = new DTONEUtil();
                    String dt_operator_name = dtone.getMobileNumberInfo(dtone_mdn);
                    log.info(dt_operator_name);

                    Radiogroup radio_service_type = (Radiogroup) this.getFellow("radio_service_type");
                    Vbox radio_servicevbox = (Vbox) this.getFellow("radio_servicevbox");

                    //String dt_tid = dtone.setCountryCodeAddValue(dtone_mdn);
                    //log.info(dt_tid);

//                    List dtone_serviceinfo = null;
//                    dtone_serviceinfo = epaybusinesscontroller.listDTOneServiceInfo(icpid, promotioncode, 0, dt_operator_name);
                    //int dt_query_tid = dtone.getTransactionStatus(dt_tid);
                    //log.info(dt_query_tid);

                    SoaProfile soa = new SoaProfile();
                    String result = soa.putSoaProxyletByMDN(mdn1);
                    apirequestbean = soa.parseXMLString(result);

                    log.info("kkflag==>NoSSO_AdjustAccountDeposit:" + result);

                    String resultcode = apirequestbean.getResultcode();
                    contractid = apirequestbean.getContractid();
                    String contractstatuscode = apirequestbean.getContract_status_code();
                    String producttype = apirequestbean.getProducttype();
                    mdn = apirequestbean.getMdn();
                    promotioncode = apirequestbean.getPromotioncode();
                    String name;                    
                    
                    textbox_mdn.setValue(mdn);
                    textbox_dtone_mdn.setValue(dtone_mdn);

                    if ("00000000".equals(resultcode)) {

                        EPAY_PROMOTIONCODE epaypromotioncode;// = new EPAY_PROMOTIONCODE();
                        epaypromotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);
                        int platformtype = epaypromotioncode.getPlatformtype();

                        BasicInfoReqBean basicinforeqbean;// = new BasicInfoReqBean();
                        BasicInfoReqBean basicinforeqbeanx;// = new BasicInfoReqBean();
                        int ddday = 0;
                        double sumx = 0;

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

                                basicinforeqbeanx = ztebasicinfoutilx.parseZTEBasicInfoXMLString(basicinfo);

                                double count1x = Double.valueOf(basicinforeqbeanx.getCounterValue1());
                                double count2x = Double.valueOf(basicinforeqbeanx.getCounterValue2());
                                sumx = (double) count1x + (double) count2x;
                                log.info(mdn + ":count1x()==>" + count1x);
                                log.info(mdn + ":count2x()==>" + count2x);

                                if (!"".equals(dt_operator_name)) {

                                    if (count1x > 0) {

                                        List dtone_serviceinfo = null;
                                        dtone_serviceinfo = epaybusinesscontroller.listDTOneServiceInfo(icpid, promotioncode, 2, dt_operator_name);

                                        Iterator itserviceinfo1 = dtone_serviceinfo.iterator();
                                        while (itserviceinfo1.hasNext()) {
                                            EPAY_SERVICE_INFO serid = (EPAY_SERVICE_INFO) itserviceinfo1.next();
                                            Radio serviceid_radio = new Radio();
                                            serviceid_radio.setId(String.valueOf(serid.getServiceId()));
                                            serviceid_radio.setLabel(serid.getServiceName());
//                                            serviceid_radio.setStyle("\"font-family: '微軟正黑體';font-size: 20px;\"");
                                            serviceid_radio.setParent(radio_servicevbox);
//                                            Label label = new Label();
//                                            label.setValue("　" + serid.getNote());
//                                            label.setParent(radio_servicevbox);

                                            log.info("Radio--->" + serid.getServiceName());
                                        }

                                    } else {
                                        log.info(mdn + ":61 count1x==>" + count1x);
                                        this.detach();
                                        Executions.sendRedirect("/dtone/OnlineNoSSOSSOErrorMsg.zhtml");
                                    }
                                } else {
                                    log.info(mdn1 + " is wrOng");
                                    //this.detach();
                                    //Executions.sendRedirect("/dtone/OnlineNoSSOSSOErrorMsg.zhtml");
                                }

                            } else {
                                log.info(mdn + ":LifeCycleState()==>" + basicinforeqbean.getLifeCycleState());
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

                }

            } else {
                // // no cpid 
            }
        } catch (Exception ex) {
            log.info(ex);
        }

    }

    public void sendBtn() throws Exception {

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        Textbox textbox_mdn = (Textbox) this.getFellow("textbox_mdn");

//        ZTEAdjustAccUtil zteadjust = new ZTEAdjustAccUtil();
////        flag = zteadjust.ZTEAdjustBucketInit(cpid, sid, libm, mdn, tradedate, channeltype);
//        boolean flag = zteadjust.ZTEDeductFeeInit(cpid, mdn);

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
}
