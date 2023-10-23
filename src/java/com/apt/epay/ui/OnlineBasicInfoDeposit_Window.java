/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui;

import com.apt.epay.beans.BasicInfoReqBean;
import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.nokia.bean.NokiaSubscribeAgentInfoBean;
import com.apt.epay.nokia.bean.NokiaSubscribeBalanceBean;
import com.apt.epay.nokia.main.NokiaMainBasicInfoUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.epay.zte.util.ZTEOCS4GBasicInfoUtil;
import com.apt.util.HttpClientUtil;
import com.apt.util.OCS4GBasicInfoUtil;
import com.apt.util.SoaProfile;
import com.epay.ejb.bean.EPAY_PROMOTIONCODE;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class OnlineBasicInfoDeposit_Window extends Window {

//    private static final long serialVersionUID = -84547344457721L;
    private static final Logger log = Logger.getLogger("EPAY");

    private String name;
    private String email;
    private String personalid;

    private String contractid;
    private String contractstatuscode;
    private String promotioncode;
    private String producttype;
    private String mdn;
    private String min;
    private String uuid;

//    private String mask_name;
//    private String mask_email;
//    private String mask_personalid;
//    private String mask_mdn;
    public void onCreate() throws Exception {
//
        Sessions.getCurrent().removeAttribute("odBean");

//        SendSMS xsms = new SendSMS();
//        String bno = "0982158008";
//        String message = "test/中文";
//        xsms.sendsms(bno,message);
        //SSO Part
        String uuid = "";
        uuid = ((HttpServletRequest) Executions.getCurrent().getNativeRequest()).getRemoteUser();
        log.info("UUID:" + uuid);

        String ipaddress = Executions.getCurrent().getRemoteAddr();
        log.info("IP:" + ipaddress + " QueryBasicInfo==>UUID:" + uuid);

        // Init for ZK
        Div id710 = (Div) this.getFellow("id710");

        //門號
        Label textbox_mdn = (Label) this.getFellow("textbox_mdn");

        //服務類型
        Label textbox_servicename = (Label) this.getFellow("textbox_servicename");

        //門號有效截止日
        Label textbox_contract_expire = (Label) this.getFellow("textbox_contract_expire");

        //基本通信費(元)
        Label textbox_tel_balance = (Label) this.getFellow("textbox_tel_balance");

        //贈送通信費(元)
        Label textbox_tel_add_balance = (Label) this.getFellow("textbox_tel_add_balance");

        //數據上網基本量(MB)
        Label textbox_data_balance = (Label) this.getFellow("textbox_data_balance");

        //數據上網基本量(MB) 服務使用到期日
        Label textbox_data_expire = (Label) this.getFellow("textbox_data_expire");

        //數據上網贈送量(MB)
        Label textbox_data_add_balance = (Label) this.getFellow("textbox_data_add_balance");

        //數據上網贈送量(MB) 服務使用到期日
        Label textbox_data_add_expire = (Label) this.getFellow("textbox_data_add_expire");

        //網內免費通話金額(元)
        Label textbox_onnet_balance = (Label) this.getFellow("textbox_onnet_balance");

        //網內免費通話金額(元) 服務使用到期日
        Label textbox_onnet_expire = (Label) this.getFellow("textbox_onnet_expire");

        //30天免費網內語音 服務使用到期日
        Label textbox_onnetx_expire = (Label) this.getFellow("textbox_onnetx_expire");

        //數據上網基本量(MB)
        Label textbox_data_balance_id = (Label) this.getFellow("textbox_data_balance_id");
        Label textbox_data_expire_id = (Label) this.getFellow("textbox_data_expire_id");
        Label textbox_data_add_balance_id = (Label) this.getFellow("textbox_data_add_balance_id");
        Label textbox_data_add_expire_id = (Label) this.getFellow("textbox_data_add_expire_id");
        Label textbox_onnet_balance_id = (Label) this.getFellow("textbox_onnet_balance_id");
        Label textbox_onnet_expire_id = (Label) this.getFellow("textbox_onnet_expire_id");

        Label textbox_onnetx_balance_id = (Label) this.getFellow("textbox_onnetx_balance_id");
        Label textbox_onnetx_expire_id = (Label) this.getFellow("textbox_onnet_expire_id");

        Label textbox_710_balance_id = (Label) this.getFellow("textbox_710_balance_id");
        //kk
        //Label textbox_710_balance_id_1 = (Label) this.getFellow("textbox_710_balance_id_1");
        Label textbox_710_expire_id = (Label) this.getFellow("textbox_710_expire_id");

        //kk
        //Label textbox_onnetx_balance_expired_id = (Label) this.getFellow("textbox_onnetx_balance_expired_id");
        Label textbox_internet_voice_balance = (Label) this.getFellow("textbox_internet_voice_balance");
        Label textbox_internal_voice_free_balance = (Label) this.getFellow("textbox_internal_voice_free_balance");

        Label textbox_intranet_voice_balance = (Label) this.getFellow("textbox_intranet_voice_balance");
        Label textbox_intranet_voice_expire = (Label) this.getFellow("textbox_intranet_voice_expire");

        //5G 帳本
        Div id530 = (Div) this.getFellow("id530");
        Label textbox_5GDataFUm_balance_id = (Label) this.getFellow("textbox_5GDataFUm_balance_id");
        Label textbox_5GDataFUm_balance = (Label) this.getFellow("textbox_5GDataFUm_balance");
        Label textbox_5GDataFUm_expire_id = (Label) this.getFellow("textbox_5GDataFUm_expire_id");
        Label textbox_5GDataFUm_expire = (Label) this.getFellow("textbox_5GDataFUm_expire");

        Div id531 = (Div) this.getFellow("id531");
        Label textbox_5GDataF21MbLm_balance_id = (Label) this.getFellow("textbox_5GDataF21MbLm_balance_id");
        Label textbox_5GDataF21MbLm_balance = (Label) this.getFellow("textbox_5GDataF21MbLm_balance");
        Label textbox_5GDataF21MbLm_expire_id = (Label) this.getFellow("textbox_5GDataF21MbLm_expire_id");
        Label textbox_5GDataF21MbLm_expire = (Label) this.getFellow("textbox_5GDataF21MbLm_expire");

        Div id532 = (Div) this.getFellow("id532");
        Label textbox_5GDataF12MbLm_balance_id = (Label) this.getFellow("textbox_5GDataF12MbLm_balance_id");
        Label textbox_5GDataF12MbLm_balance = (Label) this.getFellow("textbox_5GDataF12MbLm_balance");
        Label textbox_5GDataF12MbLm_expire_id = (Label) this.getFellow("textbox_5GDataF12MbLm_expire_id");
        Label textbox_5GDataF12MbLm_expire = (Label) this.getFellow("textbox_5GDataF12MbLm_expire");

        Div id555 = (Div) this.getFellow("id555");
        Label textbox_5GDataWiFi_balance_id = (Label) this.getFellow("textbox_5GDataWiFi_balance_id");
        Label textbox_5GDataWiFi_balance = (Label) this.getFellow("textbox_5GDataWiFi_balance");
        Label textbox_5GDataWiFi_expire_id = (Label) this.getFellow("textbox_5GDataWiFi_expire_id");
        Label textbox_5GDataWiFi_expire = (Label) this.getFellow("textbox_5GDataWiFi_expire");

        //5G計日型全速無限上網(530)
        id530.setVisible(false);
        textbox_5GDataFUm_balance_id.setVisible(false);
        textbox_5GDataFUm_balance.setVisible(false);
        textbox_5GDataFUm_expire_id.setVisible(false);
        textbox_5GDataFUm_expire.setVisible(false);

        //5G計日型全速上網到量降速21M(531/541)
        id531.setVisible(false);
        textbox_5GDataF21MbLm_balance_id.setVisible(false);
        textbox_5GDataF21MbLm_balance.setVisible(false);
        textbox_5GDataF21MbLm_expire_id.setVisible(false);
        textbox_5GDataF21MbLm_expire.setVisible(false);

        //5G計日型全速上網到量降速12M(532/542)
        id532.setVisible(false);
        textbox_5GDataF12MbLm_balance_id.setVisible(false);
        textbox_5GDataF12MbLm_balance.setVisible(false);
        textbox_5GDataF12MbLm_expire_id.setVisible(false);
        textbox_5GDataF12MbLm_expire.setVisible(false);

        //熱點分享數據量(555)
        id555.setVisible(false);
        textbox_5GDataWiFi_balance_id.setVisible(false);
        textbox_5GDataWiFi_balance.setVisible(false);
        textbox_5GDataWiFi_expire_id.setVisible(false);
        textbox_5GDataWiFi_expire.setVisible(false);

        try {
            if (uuid == null) {
                log.info("UUID IS NULL");
                this.detach();
                Window window = (Window) Executions.createComponents("/deposit/OnlineSSOErrorMsg.zhtml", null, null);
            } else {
                //get data from SOA server
                SoaProfile soa = new SoaProfile();
                String result = soa.putSoaProxylet(uuid);
                SOAReqBean apirequestbean = new SOAReqBean();
                apirequestbean = soa.parseXMLString(result);

                log.info("kkflag==>BasicInfoDeposit:" + result);

                log.info("==>" + result);

                name = apirequestbean.getName();
                email = apirequestbean.getEmail();
                personalid = apirequestbean.getPersonalid();
                mdn = apirequestbean.getMdn();
                min = apirequestbean.getMin();
                contractid = apirequestbean.getContractid();
                contractstatuscode = apirequestbean.getContract_status_code();
                producttype = apirequestbean.getProducttype();
                promotioncode = apirequestbean.getPromotioncode();

                log.info("promotioncode==>" + promotioncode);

                log.info("IP:" + Executions.getCurrent().getRemoteAddr()
                                + " QueryBasicInfo==>SOA"
                                + "(" + promotioncode + "," + name + "," + email + "," + personalid + "," + mdn + "," + min + "," + contractid + "," + contractstatuscode + "," + producttype + ")");

                textbox_mdn.setValue(mdn);

                log.info("hello1");
                EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
                log.info("hello1111");
                EPAY_PROMOTIONCODE epay_promotioncode = new EPAY_PROMOTIONCODE();
                log.info("hello12222");
                epay_promotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);
                log.info("hello13333");
                log.info("MDN:" + mdn + " ===>" + epay_promotioncode.getPlatformtype() + "," + epay_promotioncode.getPromotioncode());

                //5G 帳本
                //Case1 : PP , ContractStatus = 9/43
                if (producttype.equals("PP") && ((contractstatuscode.equals("9") || contractstatuscode.equals("43")))) {

//                    textbox_servicename.setValue("3GCDMA");
                    textbox_servicename.setValue("3G預付卡");

//                    red1.setVisible(false);
//                    red2.setVisible(false);
//                    red3.setVisible(false);
                    textbox_data_balance_id.setVisible(false);
                    textbox_data_expire_id.setVisible(false);
                    textbox_data_add_balance_id.setVisible(false);
                    textbox_data_add_expire_id.setVisible(false);
                    textbox_onnet_balance_id.setVisible(false);
                    textbox_onnet_expire_id.setVisible(false);
                    textbox_onnetx_balance_id.setVisible(false);
                    textbox_onnetx_expire_id.setVisible(false);
                    textbox_710_balance_id.setVisible(false);
                    //textbox_710_balance_id_1.setVisible(false);
                    textbox_710_expire_id.setVisible(false);

                    textbox_data_balance.setVisible(false);
                    textbox_data_expire.setVisible(false);
                    textbox_data_add_balance.setVisible(false);
                    textbox_data_add_expire.setVisible(false);
                    textbox_onnet_balance.setVisible(false);
                    textbox_onnet_expire.setVisible(false);
//                    textbox_onnetx_balance.setVisible(false);
                    textbox_onnetx_expire.setVisible(false);
                    //textbox_onnetx_balance_expired_id.setVisible(false);

//                String sendurl_3gocs = new ShareParm().PARM_3GOCS_URL;
                    String sendurl_3gocs = new ShareParm().PARM_3GOCS_URL;// "http://10.31.80.38/ZSmartService/user.action";
                    String URL = sendurl_3gocs + "?scode=QueryUserProfile&MDN=" + mdn + "&MSISDN=" + mdn + "&IMSI=&UserPwd=";
//                String xxx  = "http://10.31.80.38/ZSmartService/user.action?scode=QueryUserProfile&MDN=0982091282&MSISDN=0982091282&IMSI=&UserPwd=";

                    log.info("URL--->" + URL);

                    HttpClientUtil httpx = new HttpClientUtil();
                    NameValuePair[] requestBody = null;
                    String ocs3g = httpx.sendKKHttpPostMsg(URL);
                    String ocs3gresult = httpx.KKStringSplit(ocs3g);
                    log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + ocs3gresult);

                    String[] strArray0 = ocs3gresult.split(";");
                    for (int index = 0; index < strArray0.length; index++) {

                        log.info("QueryBasicInfo===>MDN:" + mdn + " 3GOCS Banlance Result: " + strArray0[index]);

                        String[] strArray1 = strArray0[index].split(",");
                        for (int j = 0; j < strArray1.length; j++) {
//                        log.info("strArray1[" + j + "]===>" + strArray1[j]);
                            if (strArray1[0].equals("1")) {
                                int number = Integer.parseInt(strArray1[1]) / 100;
//                            log.info("Number==>" + number);
                                textbox_tel_balance.setValue(Integer.toString(number));
//                                textbox_tel_expire.setValue(strArray1[2]);
                                textbox_contract_expire.setValue(strArray1[2]);

                            }
                            if (strArray1[0].equals("4")) {
                                int number = Integer.parseInt(strArray1[1]) / 100;
//                            log.info("Number==>" + number);
                                textbox_tel_add_balance.setValue(Integer.toString(number));
//                                textbox_tel_add_expire.setValue(strArray1[2]);
                            }
                        }
                    }
                } else if (((contractstatuscode.equals("9") || contractstatuscode.equals("43"))) && ((epay_promotioncode.getPlatformtype() == 1))) {

                    log.info("IP:" + ipaddress + " QueryBasicInfo==>4G OSC Server,MDN:" + mdn);

                    textbox_servicename.setValue("4G LTE行動預付卡");
                    try {

                        SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                        SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);

                        Calendar nowDateTime = Calendar.getInstance();
                        String libm = sdf15.format(nowDateTime.getTime());
                        OCS4GBasicInfoUtil basicinfoutil = new OCS4GBasicInfoUtil();
                        String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());

                        String basicinfo = basicinfoutil.putOCS4GBasicInfoSlet(libm, mdn, tradeDate_Pincode);
                        log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfo);

                        BasicInfoReqBean basicinforeqbean = new BasicInfoReqBean();
                        basicinforeqbean = basicinfoutil.parseBasicInfoXMLString(basicinfo);

                        float count1 = Float.valueOf(basicinforeqbean.getCounterValue1()) / 10000;
                        BigDecimal b1 = new BigDecimal(count1);
                        float f1 = b1.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();

                        String countx = String.valueOf(f1);
                        textbox_tel_balance.setValue(countx);

                        if (!basicinforeqbean.getEndDate1().equals("")) {
                            textbox_contract_expire.setValue(TransDateForamt(basicinforeqbean.getEndDate1()));
                        }

                        float count2 = Float.valueOf(basicinforeqbean.getCounterValue2()) / 10000;
                        BigDecimal b2 = new BigDecimal(count2);
                        float f2 = b2.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();

                        String countx2 = String.valueOf(f2);
                        textbox_tel_add_balance.setValue(countx2);

                        String DefaultTariffPlanCOSPID = basicinforeqbean.getDefaultTariffPlanCOSPID();
                        log.info("DefaultTariffPlanCOSPID========>" + DefaultTariffPlanCOSPID);

                        if (!basicinforeqbean.getID7().equals("")) {

                            log.info("basicinforeqbean.getEndDate7()===>" + basicinforeqbean.getEndDate7());
                            log.info("TransDateForamt(basicinforeqbean.getEndDate7())===>" + TransDateForamt(basicinforeqbean.getEndDate7()));
                            textbox_710_expire_id.setVisible(true);
                            textbox_710_expire_id.setValue(TransDateForamt(basicinforeqbean.getEndDate7()));
                        } else {
                            log.info("710帳本不存在");
                            id710.setVisible(false);
                            textbox_710_expire_id.setVisible(false);
                            textbox_710_expire_id.setVisible(false);
                            //textbox_710_balance_id_1.setVisible(false);
                        }

                        float count3 = Float.valueOf(basicinforeqbean.getCounterValue3()) / 1048576;
                        BigDecimal b3 = new BigDecimal(count3);
                        float f3 = b3.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
                        String countx3 = String.valueOf(f3);
                        textbox_data_balance.setValue(countx3);
                        if (!basicinforeqbean.getEndDate3().equals("")) {
                            textbox_data_expire.setValue(TransDateForamt(basicinforeqbean.getEndDate3()));
                        }

                        float count4 = Float.valueOf(basicinforeqbean.getCounterValue4()) / 1048576;
                        BigDecimal b4 = new BigDecimal(count4);
                        float f4 = b4.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
                        String countx4 = String.valueOf(f4);
                        textbox_data_add_balance.setValue(countx4);
                        if (!basicinforeqbean.getEndDate4().equals("")) {
                            textbox_data_add_expire.setValue(TransDateForamt(basicinforeqbean.getEndDate4()));
                        }

                        if (!basicinforeqbean.getCounterValue5().equals("")) {
                            double count5 = Double.valueOf(basicinforeqbean.getCounterValue5()) * 0.033;
                            BigDecimal a = new BigDecimal(count5);
                            String countx5 = String.valueOf(a.setScale(2, 2));
                            textbox_onnet_balance.setValue(countx5);
                        }

                        if (!basicinforeqbean.getEndDate5().equals("")) {
                            textbox_onnet_expire.setValue(TransDateForamt(basicinforeqbean.getEndDate5()));
                        }

                        if (!basicinforeqbean.getEndDate6().equals("")) {
                            textbox_onnetx_expire.setValue(TransDateForamt(basicinforeqbean.getEndDate6()));
                        }
                    } catch (Exception ex) {
                        log.info(ex);
                    }
                } else if (((contractstatuscode.equals("9") || contractstatuscode.equals("43"))) && ((epay_promotioncode.getPlatformtype() == 2))) { //ZTE

                    /*
                     Alu: 4G LTE行動預付卡 
                     ZTE(3G): 3G預付卡 
                     ZTE(4G): 4G Love行動預付卡
                     */
                    log.info("producttype===>" + producttype);
                    if ("GTB1831000".equals(epay_promotioncode.getPromotioncode())) {
                        textbox_servicename.setValue("4G Go行動預付卡");
                    } else {
                        textbox_servicename.setValue("4G Love行動預付卡");
                    }

                    ZTEOCS4GBasicInfoUtil zte_basicinfoutil = new ZTEOCS4GBasicInfoUtil();
                    SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                    Calendar nowDateTime = Calendar.getInstance();
                    String libm = sdf15.format(nowDateTime.getTime());
                    String basicinfo = zte_basicinfoutil.putZTEOCS4GBasicInfoSlet(libm, mdn);

                    BasicInfoReqBean basicinforeqbean = new BasicInfoReqBean();
                    basicinforeqbean = zte_basicinfoutil.parseZTEBasicInfoXMLString(basicinfo);

                    textbox_tel_balance.setValue(basicinforeqbean.getCounterValue1());
//                        textbox_tel_expire.setValue(TransDateForamt(basicinforeqbean.getEndDate1()));
                    if (!basicinforeqbean.getEndDate1().equals("")) {
                        textbox_contract_expire.setValue(basicinforeqbean.getEndDate1());
                    }

                    textbox_tel_add_balance.setValue(basicinforeqbean.getCounterValue2());

                    if (basicinforeqbean.getID7().equals("710")) {

                        log.info("basicinforeqbean.getEndDate7()===>" + basicinforeqbean.getEndDate7());
                        log.info("TransDateForamt(basicinforeqbean.getEndDate7())===>" + basicinforeqbean.getEndDate7());
                        textbox_710_expire_id.setVisible(true);
                        textbox_710_expire_id.setValue(basicinforeqbean.getEndDate7());
                    } else {
                        log.info("710帳本不存在");
                        id710.setVisible(false);
                        textbox_710_expire_id.setVisible(false);
                        textbox_710_balance_id.setVisible(false);
                        //textbox_710_balance_id_1.setVisible(false);
                    }

                    textbox_data_balance.setValue(basicinforeqbean.getCounterValue3());
                    if (!basicinforeqbean.getEndDate3().equals("")) {
                        textbox_data_expire.setValue(basicinforeqbean.getEndDate3());
                    }

                    textbox_data_add_balance.setValue(basicinforeqbean.getCounterValue4());
                    if (!basicinforeqbean.getEndDate4().equals("")) {
                        textbox_data_add_expire.setValue(basicinforeqbean.getEndDate4());
                    }

                    textbox_onnet_balance.setValue(basicinforeqbean.getCounterValue5());
                    log.info("basicinforeqbean.getEndDate5()===>" + basicinforeqbean.getEndDate5());
                    if (!basicinforeqbean.getEndDate5().equals("")) {
                        textbox_onnet_expire.setValue(basicinforeqbean.getEndDate5());
                    }

                    if (!basicinforeqbean.getEndDate6().equals("")) {
                        textbox_onnetx_expire.setValue(basicinforeqbean.getEndDate6());
                    }
                } else if (((contractstatuscode.equals("9") || contractstatuscode.equals("43")))
                                && ((epay_promotioncode.getPlatformtype() == 3))) { //Nokia

                    //KK NOKIA
                    NokiaSubscribeBalanceBean bean = new NokiaSubscribeBalanceBean();
                    NokiaMainBasicInfoUtil mutil = new NokiaMainBasicInfoUtil();
                    
                    SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                    Calendar nowDateTime = Calendar.getInstance();
                    String libm = sdf15.format(nowDateTime.getTime());
                    bean = mutil.GetMDNBalanceInfo(libm, mdn);
                    
//                    NokiaSubscribeAgentInfoBean AgentBean = new NokiaSubscribeAgentInfoBean();
//                    AgentBean = mutil.GetMDNAgentInfo(libm, mdn);
//                    
//                    log.info(mdn + " Agent ID ====>"+AgentBean.getAgentID());
                    

                    log.info("producttype===>" + producttype);
                    log.info("epay_promotioncode.getPromotioncode()==>" + epay_promotioncode.getPromotioncode());
                    if ("GTB1831000".equals(epay_promotioncode.getPromotioncode())) {
                        textbox_servicename.setValue("4G Go行動預付卡");
                    } else {
                        textbox_servicename.setValue("4G Love行動預付卡");
                    }

                    //門號有效截止日 
                    if (bean.getAPT_LC_EndDate() != null) {
                        textbox_contract_expire.setValue(NokiaDateFormat(bean.getAPT_LC_EndDate()));
                    }
                    log.info("textbox_contract_expire(門號有效截止日)==>" + textbox_contract_expire.getValue());

                    //基本通信費(元) 650 
                    double tmp_d_value_650 = 0.0;
                    tmp_d_value_650 = Double.valueOf(bean.getAPT5GVoice_650_Counter()) / 10000;
                    double d_value_650 = Math.round(tmp_d_value_650 * 100.0) / 100.0;
                    String s_value_650 = String.valueOf(d_value_650);
                    textbox_tel_balance.setValue(s_value_650);
                    log.info("textbox_tel_balance(基本通信費(元) 650==>" + textbox_tel_balance.getValue());

                    //贈送通信費(元) 660
                    double tmp_d_value_660 = 0.0;
                    double tmp_d_value_66P = 0.0;

                    tmp_d_value_660 = Double.valueOf(bean.getAPT5GVoice1_660_Counter()) / 10000;
                    double d_value_660 = Math.round(tmp_d_value_660 * 100.0) / 100.0;

                    tmp_d_value_66P = Double.valueOf(bean.getAPT5GVoice1Pro_66P_Counter()) / 10000;
                    double d_value_66P = Math.round(tmp_d_value_66P * 100.0) / 100.0;

                    String s_value_660_66P = String.valueOf(d_value_660 + d_value_66P);

                    log.info("贈送通信費(元)=>" + d_value_660 + "+" + d_value_66P + "=" + s_value_660_66P);
                    textbox_tel_add_balance.setValue(s_value_660_66P);
                    log.info("textbox_tel_add_balance(贈送通信費(元) 660)==>" + textbox_tel_add_balance.getValue());

                    //國內語音贈送
                    double d_value_840 = 0.0, d_value_84P = 0.0;
                    d_value_840 = Double.valueOf(bean.getAPT5GVoice2_840_Counter()) / 10000;
                    d_value_84P = Double.valueOf(bean.getAPT5GVoice2Pro_84P_Counter()) / 10000;
                    double tmp_d_value_84S = d_value_840 + d_value_84P;
                    double d_value_84S = Math.round(tmp_d_value_84S * 100.0) / 100.0;
                    String s_value_84S = String.valueOf(d_value_84S);
                    textbox_intranet_voice_balance.setValue(s_value_84S);
                    log.info("textbox_intranet_voice_balance(國內語音贈送)==>" + textbox_intranet_voice_balance.getValue());

                    //國內語音贈送(日期)
                    String d_expire_840 = "", d_expire_84P = "";
                    d_expire_840 = bean.getAPT5GVoice2_840_EndDate();
                    d_expire_84P = bean.getAPT5GVoice2Pro_84P_EndDate();
                    String l_expire_84x = d_expire_840;
                    if (l_expire_84x.compareTo(d_expire_84P) < 0) {
                        l_expire_84x = d_expire_84P;
                    }
                    textbox_intranet_voice_expire.setValue(NokiaDateFormat(String.valueOf(l_expire_84x)));
                    log.info("textbox_intranet_voice_expire(國內語音贈送(日期))==>" + textbox_intranet_voice_expire.getValue());

                    //國際通信費基本(元)
                    double tmp_d_value_670 = 0.0;
                    tmp_d_value_670 = Double.valueOf(bean.getAPT5GIDD_670_Counter()) / 10000;
                    double d_value_670 = Math.round(tmp_d_value_670 * 100.0) / 100.0;
                    String s_value_670 = String.valueOf(d_value_670);
                    log.info("國際通信費基本(元)=>" + tmp_d_value_670 + "," + s_value_670);
                    textbox_internet_voice_balance.setValue(s_value_670);
                    log.info("textbox_internet_voice_balance(國際通信費基本(元))==>" + textbox_internet_voice_balance.getValue());

                    //國際通信費贈送(元)
                    double tmp_d_value_680 = 0.0;
                    tmp_d_value_680 = Double.valueOf(bean.getAPT5GIDD1_680_Counter()) / 10000;
                    double d_value_680 = Math.round(tmp_d_value_680 * 100.0) / 100.0;
                    String s_value_680 = String.valueOf(d_value_680);
                    log.info("國際通信費贈送(元)=>" + tmp_d_value_680 + "," + s_value_680);
                    textbox_internal_voice_free_balance.setValue(s_value_680);
                    log.info("textbox_internal_voice_free_balance(國際通信費贈送(元))==>" + textbox_internal_voice_free_balance.getValue());

                    //數據上網基本量(MB) 750
                    double tmp_d_value_750 = 0.0;
                    tmp_d_value_750 = Double.valueOf(bean.getAPT5GData_750_Counter()) / 1048576;
                    double d_value_750 = Math.round(tmp_d_value_750 * 100.0) / 100.0;
                    String s_value_750 = String.valueOf(d_value_750);
                    textbox_data_balance.setValue(s_value_750);
                    log.info("textbox_data_balance(數據上網基本量(MB) 750)==>" + textbox_data_balance.getValue());

                    //數據上網基本量(MB) 服務使用到期日 750   
                    textbox_data_expire.setValue(NokiaDateFormat(bean.getAPT5GData_750_EndDate()));
                    log.info("textbox_data_expire(數據上網基本量(MB) 服務使用到期日 750   )==>" + textbox_data_expire.getValue());

                    // 數據上網贈送量(MB) 760 + 76P
                    double d_value_760 = 0.0, d_value_76P = 0.0;
                    d_value_760 = Double.valueOf(bean.getAPT5GData1_760_Counter()) / 1048576;
                    d_value_76P = Double.valueOf(bean.getAPT5GData1Pro_76P_Counter()) / 1048576;
                    double tmp_d_value_76S = d_value_760 + d_value_76P;
                    double d_value_76S = Math.round(tmp_d_value_76S * 100.0) / 100.0;
                    String s_value_76S = String.valueOf(d_value_76S);
                    textbox_data_add_balance.setValue(s_value_76S);
                    log.info("textbox_data_add_balance(數據上網贈送量(MB) 760 + 76P)==>" + textbox_data_add_balance.getValue());

                    // 數據上網贈送量(MB)  服務使用到期日 760 + 76P  
                    String d_expire_760 = "", d_expire_76P = "";
                    d_expire_760 = bean.getAPT5GData1_760_EndDate();
                    d_expire_76P = bean.getAPT5GData1Pro_76P_EndDate();
                    String l_expire_76x = d_expire_760;
                    if (l_expire_76x.compareTo(d_expire_76P) < 0) {
                        l_expire_76x = d_expire_76P;
                    }
                    textbox_data_add_expire.setValue(NokiaDateFormat(String.valueOf(l_expire_76x)));
                    log.info("textbox_data_add_expire(數據上網贈送量(MB)  服務使用到期日 760 + 76P  )==>" + textbox_data_add_expire.getValue());

                    //網內免費通話金額(元) 830
                    double tmp_d_value_830 = 0.0;
                    tmp_d_value_830 = Double.valueOf(bean.getAPT5GVoiceOnnet_830_Counter()) / 100000;

                    double d_value_830 = Math.round(tmp_d_value_830 * 100.0) / 100.0;
                    String s_value_830 = String.valueOf(d_value_830);
                    textbox_onnet_balance.setValue(s_value_830);
                    log.info("textbox_onnet_balance(網內免費通話金額(元) 830)==>" + textbox_onnet_balance.getValue());

                    //網內免費通話金額(元) 830 服務使用到期日	
                    textbox_onnet_expire.setValue(NokiaDateFormat(bean.getAPT5GVoiceOnnet_830_EndDate()));
                    log.info("textbox_onnet_expire(網內免費通話金額(元) 830)==>" + textbox_onnet_expire.getValue());

                    //30天免費網內語音 服務使用到期日    
                    textbox_onnetx_expire.setValue(NokiaDateFormat(bean.getAPT5GVoiceOnPro_83P_EndDate()));
                    log.info("textbox_onnetx_expire(網內免費通話金額(元) 830)==>" + textbox_onnetx_expire.getValue());

                    //計日型數據 服務使用到期日 430 431 441 433 4P3 435 445 436 446 437
                    String Date_expiredate = getAPT5GDataExpireDate(bean);
                    textbox_710_expire_id.setValue(NokiaDateFormat(Date_expiredate));
                    log.info("textbox_710_expire_id(計日型數據 服務使用到期日 430 431 441 433 4P3 435 445 436 446 437)==>" + textbox_710_expire_id.getValue());

                    //5G計日型全速無限上網(530) 5GDataFUm
                    double d_value_530_x = 0.0;
                    d_value_530_x = Double.valueOf(bean.getDataFUm5G_530_Counter()) / 1048576;
                    double d_value_530 = Math.round(d_value_530_x * 100.0) / 100.0;

                    String d_expire_530 = bean.getDataFUm5G_530_EndDate();
                    log.info(mdn + " 5G計日型全速無限上網(530) 5GDataFUm==>" + d_value_530);
                    if (d_value_530 > 0) {
                        id530.setVisible(true);
                        textbox_5GDataFUm_balance_id.setVisible(true);
                        textbox_5GDataFUm_balance.setVisible(true);
                        textbox_5GDataFUm_expire_id.setVisible(true);
                        textbox_5GDataFUm_expire.setVisible(true);

                        textbox_5GDataFUm_balance.setValue(String.valueOf(d_value_530));
                        textbox_5GDataFUm_expire.setValue(NokiaDateFormat(d_expire_530));
                    }

                    //5G計日型全速上網到量降速21M(531/541)
                    double d_value_531_x = 0.0;
                    d_value_531_x = Double.valueOf(bean.getDataF21MbLm5G_531_Counter()) / 1048576;
                    double d_value_531 = Math.round(d_value_531_x * 100.0) / 100.0;

                    String d_expire_531 = bean.getDataF21MbLm5G_531_EndDate();
                    String d_expire_541 = bean.getDataF21MbLm5G_541_EndDate();
                    String l_expire_531_541 = d_expire_531;
                    if (l_expire_531_541.compareTo(d_expire_541) < 0) {
                        l_expire_531_541 = d_expire_541;
                    }
                    log.info(mdn + " 5G計日型全速上網到量降速21M(531/541)==>" + d_value_531);
                    if (d_value_531 > 0) {
                        id531.setVisible(true);
                        textbox_5GDataF21MbLm_balance_id.setVisible(true);
                        textbox_5GDataF21MbLm_balance.setVisible(true);
                        textbox_5GDataF21MbLm_expire_id.setVisible(true);
                        textbox_5GDataF21MbLm_expire.setVisible(true);

                        textbox_5GDataF21MbLm_balance.setValue(String.valueOf(d_value_531));
                        textbox_5GDataF21MbLm_expire.setValue(NokiaDateFormat(l_expire_531_541));
                    }

                    //5G計日型全速上網到量降速12M(532/542)
                    double d_value_532_x = 0.0;
                    d_value_532_x = Double.valueOf(bean.getDataF12MbLm5G_532_Counter()) / 1048576;
                    double d_value_532 = Math.round(d_value_532_x * 100.0) / 100.0;

                    String d_expire_532 = bean.getDataF12MbLm5G_532_EndDate();
                    String d_expire_542 = bean.getDataF12MbLm5G_542_EndDate();
                    String l_expire_532_542 = d_expire_532;
                    if (l_expire_532_542.compareTo(d_expire_542) < 0) {
                        l_expire_532_542 = d_expire_542;
                    }
                    log.info(mdn + " 5G計日型全速上網到量降速12M(532/542)==>" + d_value_532);
                    if (d_value_532 > 0) {
                        id532.setVisible(true);
                        textbox_5GDataF12MbLm_balance_id.setVisible(true);
                        textbox_5GDataF12MbLm_balance.setVisible(true);
                        textbox_5GDataF12MbLm_expire_id.setVisible(true);
                        textbox_5GDataF12MbLm_expire.setVisible(true);

                        textbox_5GDataF12MbLm_balance.setValue(String.valueOf(d_value_532));
                        textbox_5GDataF12MbLm_expire.setValue(NokiaDateFormat(l_expire_532_542));
                    }

                    //5GDataWiFi 熱點分享數據量(555)
                    double d_value_555_x = 0.0;
                    d_value_555_x = Double.valueOf(bean.getAPT5GDataWiFi_555_Counter()) / 1048576;
                    double d_value_555 = Math.round(d_value_555_x * 100.0) / 100.0;

                    String d_expire_555 = bean.getAPT5GDataWiFi_555_EndDate();
                    log.info(mdn + " 5GDataWiFi 熱點分享數據量(555)" + d_value_555);
                    if (d_value_555 > 0) {
                        id555.setVisible(true);
                        textbox_5GDataWiFi_balance_id.setVisible(true);
                        textbox_5GDataWiFi_balance.setVisible(true);
                        textbox_5GDataWiFi_expire_id.setVisible(true);
                        textbox_5GDataWiFi_expire.setVisible(true);

                        textbox_5GDataWiFi_balance.setValue(String.valueOf(d_value_555));
                        textbox_5GDataWiFi_expire.setValue(NokiaDateFormat(d_expire_555));
                    }

                } else {
                    log.info("Exception===>MDN:" + mdn + ",contractstatuscode:" + contractstatuscode + ",producttype" + producttype);
                    this.detach();
                    Sessions.getCurrent().setAttribute("status", contractstatuscode);
                    Executions.sendRedirect("/deposit/OnlineUserErrorMsg.zhtml");
//                    Executions.getCurrent().sendRedirect(ShareParm.PARM_APT_URL);
//                    Executions.sendRedirect("http://www.aptg.com.tw/my/index.htm");
                }
            }
        } catch (Exception ex) {
            log.info(ex);
        }

    }

    private String getAPT5GDataExpireDate(NokiaSubscribeBalanceBean bean) {
        String resultDate = "";
        String e430 = "", e431 = "", e433 = "", e4P3 = "", e435 = "", e445 = "", e436 = "", e437 = "";

        e430 = bean.getAPT5GDataFULUm_430_EndDate();
        e431 = bean.getAPT5GDataFULLm_431_EndDate();
        e433 = bean.getAPT5GData12Mb_433_EndDate();
        e4P3 = bean.getAPT5GData12MbPr_4P3_EndDate();
        e435 = bean.getAPT5GData12MbLm_435_EndDate();
        e445 = bean.getAPT5GData12MbLm_445_EndDate();
        e436 = bean.getAPT5GData12MbMt_436_EndDate();
        e437 = bean.getAPT5GData8Mb_437_EndDate();

        resultDate = e430;
        if (resultDate.compareTo(e431) < 0) {
            resultDate = e431;
        }

        if (resultDate.compareTo(e433) < 0) {
            resultDate = e433;
        }

        if (resultDate.compareTo(e4P3) < 0) {
            resultDate = e4P3;
        }
        if (resultDate.compareTo(e435) < 0) {
            resultDate = e435;
        }
        if (resultDate.compareTo(e445) < 0) {
            resultDate = e445;
        }
        if (resultDate.compareTo(e436) < 0) {
            resultDate = e436;
        }

        if (resultDate.compareTo(e437) < 0) {
            resultDate = e437;
        }
        return resultDate;
    }

    private String NokiaDateFormat(String datestr) {
        String result = "";
        try {
            SimpleDateFormat nokia_sdf = new SimpleDateFormat("yyyyMMddHHmm");
            SimpleDateFormat epay_sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (datestr.equals("")) {
                result = "";
            } else {
                Date date = nokia_sdf.parse(datestr);
                result = epay_sdf.format(date);
            }

        } catch (Exception ex) {
            log.info(ex);
        }
        return result;
    }

    private String TransDateForamt(String datestr) {

        String result = "";

        try {
            SimpleDateFormat sdf17 = new java.text.SimpleDateFormat(ShareParm.PARM_DATEFORMAT7);
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
            if (datestr.equals("")) {
                result = "";
            } else {
                Date date = sdf.parse(datestr);
                result = sdf17.format(date);
            }

        } catch (Exception ex) {
            log.info(ex);
        }
        return result;

    }

    public void backURL() throws Exception {
        String tmpurl = new ShareParm().PARM_ECARE_URL;
        if (tmpurl != null) {

        } else {
            log.info("ECARE URL IS NULL");
            tmpurl = "https://pv.www.aptg.com.tw/ecare/ecHome.seam";
        }
        log.info("ECARE URL==>" + tmpurl);
        Executions.getCurrent().sendRedirect(tmpurl);

    }
}
