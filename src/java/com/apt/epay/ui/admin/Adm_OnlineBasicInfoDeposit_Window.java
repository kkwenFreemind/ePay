/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.beans.BasicInfoReqBean;
import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class Adm_OnlineBasicInfoDeposit_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    private String name;
    private String email;
    private String personalid;

    private String contractid;
    private String contractstatuscode;
    private String producttype;
    private String mdn;
    private String min;
    private String promotioncode;

    public void onCreate() throws Exception {

        String user_code = (String) Sessions.getCurrent().getAttribute("user_code");
        log.info("user_code(onCreate) ===>" + user_code);

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
        } catch (ParseException ex) {
            log.info(ex);
        }
        return result;

    }

    public boolean CheckBasicInfoQueryUser(String contractstatuscode, String promotioncode) {
        boolean result = false;
        if (contractstatuscode.equals("9") || contractstatuscode.equals("43")) {
            if (promotioncode.equals("PPP") || promotioncode.equals("P4G") || promotioncode.equals("D3G")) {
                result = true;
            }
        }
        return result;
    }

    public void Mdn_Query() throws Exception {

        Textbox textbox_mdn = (Textbox) this.getFellow("textbox_mdn");
        Label textbox_servicename = (Label) this.getFellow("textbox_servicename");

        Label textbox_contractId = (Label) this.getFellow("textbox_contractId");
        Label textbox_updatedate = (Label) this.getFellow("textbox_updatedate");

        Label textbox_contract_expire = (Label) this.getFellow("textbox_contract_expire");
        Label textbox_contract_status = (Label) this.getFellow("textbox_contract_status");

        Label textbox_tel_balance = (Label) this.getFellow("textbox_tel_balance");
        Label textbox_tel_add_balance = (Label) this.getFellow("textbox_tel_add_balance");

        Label textbox_data_balance = (Label) this.getFellow("textbox_data_balance");
        Label textbox_data_expire = (Label) this.getFellow("textbox_data_expire");

        Label textbox_data_add_balance = (Label) this.getFellow("textbox_data_add_balance");
        Label textbox_data_add_expire = (Label) this.getFellow("textbox_data_add_expire");

        Label textbox_onnet_balance = (Label) this.getFellow("textbox_onnet_balance");
        Label textbox_onnet_expire = (Label) this.getFellow("textbox_onnet_expire");
        Label textbox_onnetx_expire = (Label) this.getFellow("textbox_onnetx_expire");

        Label textbox_data_balance_id = (Label) this.getFellow("textbox_data_balance_id");
        Label textbox_data_expire_id = (Label) this.getFellow("textbox_data_expire_id");
        Label textbox_data_add_balance_id = (Label) this.getFellow("textbox_data_add_balance_id");
        Label textbox_data_add_expire_id = (Label) this.getFellow("textbox_data_add_expire_id");
        Label textbox_onnet_balance_id = (Label) this.getFellow("textbox_onnet_balance_id");
        Label textbox_onnet_expire_id = (Label) this.getFellow("textbox_onnet_expire_id");

        Label textbox_onnetx_balance_id = (Label) this.getFellow("textbox_onnetx_balance_id");
        Label textbox_onnetx_expire_id = (Label) this.getFellow("textbox_onnet_expire_id");

        Label textbox_710_balance_id = (Label) this.getFellow("textbox_710_balance_id");
        Label textbox_710_expire_id = (Label) this.getFellow("textbox_710_expire_id");
        Label textbox_770_expire_id = (Label) this.getFellow("textbox_770_expire_id");

        Label textbox_onnetx_balance_expired_id = (Label) this.getFellow("textbox_onnetx_balance_expired_id");
        Label textbox_ocs_cintractstatus = (Label) this.getFellow("textbox_ocs_cintractstatus");

        //5G
        Label textbox_APT5GVoice2Pro_balance = (Label) this.getFellow("textbox_APT5GVoice2Pro_balance");
        Label textbox_APT5GDataFULLm_balance = (Label) this.getFellow("textbox_APT5GDataFULLm_balance");
        Label textbox_5GDataFUm_balance = (Label) this.getFellow("textbox_5GDataFUm_balance");
        Label textbox_5GDataF21MbLm_balance = (Label) this.getFellow("textbox_5GDataF21MbLm_balance");
        Label textbox_5GDataF12MbLm_balance = (Label) this.getFellow("textbox_5GDataF12MbLm_balance");

        Label textbox_5GDataWiFi_balance = (Label) this.getFellow("textbox_5GDataWiFi_balance");
        Label textbox_APT5GIDD_balance = (Label) this.getFellow("textbox_APT5GIDD_balance");
        Label textbox_APT5GIDD1_balance = (Label) this.getFellow("textbox_APT5GIDD1_balance");


        textbox_servicename.setValue("");
        textbox_tel_balance.setValue("");
        textbox_contract_expire.setValue("");
        textbox_tel_add_balance.setValue("");
        textbox_710_expire_id.setValue("");
        textbox_770_expire_id.setValue("");
        textbox_data_balance.setValue("");
        textbox_data_expire.setValue("");
        textbox_data_add_balance.setValue("");
        textbox_data_add_expire.setValue("");
        textbox_onnet_balance.setValue("");
        textbox_onnet_expire.setValue("");
        textbox_onnetx_expire.setValue("");
        textbox_ocs_cintractstatus.setValue("");
        textbox_contract_status.setValue("");
        textbox_contractId.setValue("");

        try {

            String user_code = (String) Sessions.getCurrent().getAttribute("user_code");

            mdn = textbox_mdn.getValue();
            log.info("IP:" + Executions.getCurrent().getRemoteAddr() + " Account:" + user_code + " query mdn :" + mdn);

            //get data from SOA server
            SoaProfile soa = new SoaProfile();
            String result = soa.putSoaProxyletByMDN(mdn);
            log.info("soa result==>" + result);

            if (result.contains("00000000")) {
                SOAReqBean apirequestbean;// = new SOAReqBean();
                apirequestbean = soa.parseXMLString(result);

                String resultcode = apirequestbean.getResultcode();
                log.info("resultcode====>" + resultcode);

                if (resultcode.equals("00000000")) {
                    name = apirequestbean.getName();
                    email = apirequestbean.getEmail();
                    personalid = apirequestbean.getPersonalid();
                    mdn = apirequestbean.getMdn();
                    min = apirequestbean.getMin();
                    contractid = apirequestbean.getContractid();
                    contractstatuscode = apirequestbean.getContract_status_code();
                    producttype = apirequestbean.getProducttype();
                    promotioncode = apirequestbean.getPromotioncode();
                    textbox_contract_status.setValue(contractstatuscode);

                    int kki = contractid.indexOf("-") + 1;
                    String kkcontractId = contractid.substring(kki, kki + 6);
                    textbox_contractId.setValue(contractid + "(" + kkcontractId + ")");

                    log.info("IP:" + Executions.getCurrent().getRemoteAddr()
                                    + " QueryBasicInfo==>SOA"
                                    + "(" + name + "," + email + "," + personalid + "," + mdn + "," + min + "," + contractid + "," + contractstatuscode + "," + producttype + ")");

                    textbox_mdn.setValue(mdn);

                    EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
                    EPAY_PROMOTIONCODE epay_promotioncode;// = new EPAY_PROMOTIONCODE();
                    epay_promotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);

                    log.info("MDN:" + mdn + " ===>" + epay_promotioncode.getPlatformtype() + "," + epay_promotioncode.getPromotioncode());

                    if (producttype.equals("PP") && ((contractstatuscode.equals("9") || contractstatuscode.equals("43")))) {

                        log.info("PP ====>" + epay_promotioncode.getPlatformtype());

                        textbox_servicename.setValue("3GCDMA");
                        textbox_data_balance_id.setVisible(false);
                        textbox_data_expire_id.setVisible(false);
                        textbox_data_add_balance_id.setVisible(false);
                        textbox_data_add_expire_id.setVisible(false);
                        textbox_onnet_balance_id.setVisible(false);
                        textbox_onnet_expire_id.setVisible(false);
                        textbox_onnetx_balance_id.setVisible(false);
                        textbox_onnetx_expire_id.setVisible(false);
                        textbox_710_balance_id.setVisible(false);
                        textbox_710_expire_id.setVisible(false);

                        textbox_data_balance.setVisible(false);
                        textbox_data_expire.setVisible(false);
                        textbox_data_add_balance.setVisible(false);
                        textbox_data_add_expire.setVisible(false);
                        textbox_onnet_balance.setVisible(false);
                        textbox_onnet_expire.setVisible(false);
                        textbox_onnetx_expire.setVisible(false);
                        textbox_onnetx_balance_expired_id.setVisible(false);

                        String sendurl_3gocs = new ShareParm().PARM_3GOCS_URL;// "http://10.31.80.38/ZSmartService/user.action";
                        String URL = sendurl_3gocs + "?scode=QueryUserProfile&MDN=" + mdn + "&MSISDN=" + mdn + "&IMSI=&UserPwd=";

                        log.info("URL--->" + URL);

                        HttpClientUtil httpx = new HttpClientUtil();
                        String ocs3g = httpx.sendKKHttpPostMsg(URL);
                        String ocs3gresult = httpx.KKStringSplit(ocs3g);
                        log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + ocs3gresult);

                        String[] strArray0 = ocs3gresult.split(";");
                        for (String strArray01 : strArray0) {
                            log.info("QueryBasicInfo===>MDN:" + mdn + " 3GOCS Banlance Result: " + strArray01);
                            String[] strArray1 = strArray01.split(",");
                            for (String strArray11 : strArray1) {
                                if (strArray1[0].equals("1")) {
                                    int number = Integer.parseInt(strArray1[1]) / 100;
                                    textbox_tel_balance.setValue(Integer.toString(number));
                                    textbox_contract_expire.setValue(strArray1[2]);
                                }
                                if (strArray1[0].equals("4")) {
                                    int number = Integer.parseInt(strArray1[1]) / 100;
                                    textbox_tel_add_balance.setValue(Integer.toString(number));
                                }
                            }
                        }

                    } else if (((contractstatuscode.equals("9") || contractstatuscode.equals("43"))) && ((epay_promotioncode.getPlatformtype() == 1))) {

                        log.info("ALU ====>" + epay_promotioncode.getPlatformtype());
                        log.info("IP:" + "none" + " QueryBasicInfo==>4G OSC Server,MDN:" + mdn);

                        if (producttype.equals("P4G")) {
                            textbox_servicename.setValue("4G");
                        }

                        if (producttype.equals("D3G")) {
                            textbox_servicename.setValue("4GGT");
                        }

                        try {

                            SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                            SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);

                            Calendar nowDateTime = Calendar.getInstance();
                            String libm = sdf15.format(nowDateTime.getTime());
                            OCS4GBasicInfoUtil basicinfoutil = new OCS4GBasicInfoUtil();
                            String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());

                            String basicinfo = basicinfoutil.putOCS4GBasicInfoSlet(libm, mdn, tradeDate_Pincode);
                            log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfo);

                            BasicInfoReqBean basicinforeqbean;// = new BasicInfoReqBean();
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
                            log.info("basicinforeqbean.getEndDate7()===>" + basicinforeqbean.getEndDate7());
                            log.info("TransDateForamt(basicinforeqbean.getEndDate7())===>" + TransDateForamt(basicinforeqbean.getEndDate7()));
                            textbox_710_expire_id.setValue(TransDateForamt(basicinforeqbean.getEndDate7()));

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
                            textbox_ocs_cintractstatus.setValue(basicinforeqbean.getLifeCycleState());

                        } catch (Exception ex) {
                            log.info(ex);
                        }

                    } else if (epay_promotioncode.getPlatformtype() == 2) { //ZTE
                        /*
                             Alu: 4G LTE行動預付卡 
                             ZTE(3G): 3G預付卡 
                             ZTE(4G): 4G Love行動預付卡
                         */
                        log.info("producttype===>" + producttype);
                        if ("GTB1831000".equals(epay_promotioncode.getPromotioncode())) {
                            textbox_servicename.setValue("4G Go行動預付卡");
                        }
                        ZTEOCS4GBasicInfoUtil zte_basicinfoutil = new ZTEOCS4GBasicInfoUtil();
                        SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                        Calendar nowDateTime = Calendar.getInstance();
                        String libm = sdf15.format(nowDateTime.getTime());
                        String basicinfo = zte_basicinfoutil.putZTEOCS4GBasicInfoSlet(libm, mdn);

                        if (!basicinfo.contains("ERROR_CODE")) {

                            BasicInfoReqBean basicinforeqbean;// = new BasicInfoReqBean();
                            basicinforeqbean = zte_basicinfoutil.parseZTEBasicInfoXMLString(basicinfo);

                            textbox_servicename.setValue(basicinforeqbean.getBrandNamee());
                            textbox_tel_balance.setValue(basicinforeqbean.getCounterValue1());
                            textbox_updatedate.setValue(basicinforeqbean.getUpdateDate());

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
                            }

                            //2018/10/17
                            textbox_770_expire_id.setVisible(true);
                            ZTEOCS4GBasicInfoUtil kk_zte_basicinfoutil = new ZTEOCS4GBasicInfoUtil();
                            String kk77date = kk_zte_basicinfoutil.GetBuck77(basicinfo);
                            log.info("kk77date===>" + kk77date);
                            textbox_770_expire_id.setValue(kk77date);

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
                            textbox_ocs_cintractstatus.setValue(basicinforeqbean.getReal_LifeCycleState());
                        } else {
                            Messagebox.show("OCS無此資料", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);

                        }
                    }
                    if (epay_promotioncode.getPlatformtype() == 3) {

                        //KK NOKIA
                        NokiaSubscribeBalanceBean bean = new NokiaSubscribeBalanceBean();
                        NokiaMainBasicInfoUtil mutil = new NokiaMainBasicInfoUtil();

                        SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                        Calendar nowDateTime = Calendar.getInstance();
                        String libm = sdf15.format(nowDateTime.getTime());
                        bean = mutil.GetMDNBalanceInfo(libm, mdn);

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
                        log.info("textbox_contract_expire==>" + textbox_contract_expire.getValue());

                        //基本通信費(元) 650 
                        double tmp_d_value_650 = 0.0;
                        tmp_d_value_650 = Double.valueOf(bean.getAPT5GVoice_650_Counter()) / 10000;
                        double d_value_650 = Math.round(tmp_d_value_650 * 100.0) / 100.0;
                        String s_value_650 = String.valueOf(d_value_650);
                        textbox_tel_balance.setValue(s_value_650);
                        log.info("textbox_tel_balance==>" + textbox_tel_balance.getValue());

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
                        log.info("textbox_tel_add_balance==>" + textbox_tel_add_balance.getValue());

                        //國內語音贈送
                        double d_value_840 = 0.0, d_value_84P = 0.0;
                        d_value_840 = Double.valueOf(bean.getAPT5GVoice2_840_Counter()) / 10000;
                        d_value_84P = Double.valueOf(bean.getAPT5GVoice2Pro_84P_Counter()) / 10000;
                        double tmp_d_value_84S = d_value_840 + d_value_84P;
                        double d_value_84S = Math.round(tmp_d_value_84S * 100.0) / 100.0;
                        String s_value_84S = String.valueOf(d_value_84S);
                        //textbox_APT5GVoice2Pro_balance.setValue(s_value_84S);
                        //g.info("textbox_intranet_voice_balance==>" + textbox_APT5GVoice2Pro_balance.getValue());

                        //國內語音贈送(日期)
                        String d_expire_840 = "", d_expire_84P = "";
                        d_expire_840 = bean.getAPT5GVoice2_840_EndDate();
                        d_expire_84P = bean.getAPT5GVoice2Pro_84P_EndDate();
                        String l_expire_84x = d_expire_840;
                        if (l_expire_84x.compareTo(d_expire_84P) < 0) {
                            l_expire_84x = d_expire_84P;
                        }
                        //textbox_intranet_voice_expire.setValue(NokiaDateFormat(String.valueOf(l_expire_84x)));
                        //log.info("textbox_intranet_voice_expire==>" + textbox_intranet_voice_expire.getValue());
                        textbox_APT5GVoice2Pro_balance.setValue(s_value_84S + " 到期日:" + l_expire_84x);

                        //國際通信費基本(元)
                        double tmp_d_value_670 = 0.0;
                        tmp_d_value_670 = Double.valueOf(bean.getAPT5GIDD_670_Counter()) / 10000;
                        double d_value_670 = Math.round(tmp_d_value_670 * 100.0) / 100.0;
                        String s_value_670 = String.valueOf(d_value_670);
                        log.info("國際通信費基本(元)=>" + tmp_d_value_670 + "," + s_value_670);
                        textbox_APT5GIDD_balance.setValue(s_value_670);
                        log.info("textbox_APT5GIDD_balance==>" + textbox_APT5GIDD_balance.getValue());

                        //國際通信費贈送(元)
                        double tmp_d_value_680 = 0.0;
                        tmp_d_value_680 = Double.valueOf(bean.getAPT5GIDD1_680_Counter()) / 10000;
                        double d_value_680 = Math.round(tmp_d_value_680 * 100.0) / 100.0;
                        String s_value_680 = String.valueOf(d_value_680);
                        log.info("國際通信費贈送(元)=>" + tmp_d_value_680 + "," + s_value_680);
                        textbox_APT5GIDD1_balance.setValue(s_value_680);
                        log.info("textbox_APT5GIDD1_balance==>" + textbox_APT5GIDD1_balance.getValue());

                        //數據上網基本量(MB) 750
                        double tmp_d_value_750 = 0.0;
                        tmp_d_value_750 = Double.valueOf(bean.getAPT5GData_750_Counter()) / 1048576;
                        double d_value_750 = Math.round(tmp_d_value_750 * 100.0) / 100.0;
                        String s_value_750 = String.valueOf(d_value_750);
                        textbox_data_balance.setValue(s_value_750);
                        log.info("textbox_data_balance==>" + textbox_data_balance.getValue());

                        //數據上網基本量(MB) 服務使用到期日 750   
                        textbox_data_expire.setValue(NokiaDateFormat(bean.getAPT5GData_750_EndDate()));
                        log.info("textbox_data_expire==>" + textbox_data_expire.getValue());

                        // 數據上網贈送量(MB) 760 + 76P
                        double d_value_760 = 0.0, d_value_76P = 0.0;
                        d_value_760 = Double.valueOf(bean.getAPT5GData1_760_Counter()) / 1048576;
                        d_value_76P = Double.valueOf(bean.getAPT5GData1Pro_76P_Counter()) / 1048576;
                        double tmp_d_value_76S = d_value_760 + d_value_76P;
                        double d_value_76S = Math.round(tmp_d_value_76S * 100.0) / 100.0;
                        String s_value_76S = String.valueOf(d_value_76S);
                        textbox_data_add_balance.setValue(s_value_76S);
                        log.info("textbox_data_add_balance==>" + textbox_data_add_balance.getValue());

                        // 數據上網贈送量(MB)  服務使用到期日 760 + 76P  
                        String d_expire_760 = "", d_expire_76P = "";
                        d_expire_760 = bean.getAPT5GData1_760_EndDate();
                        d_expire_76P = bean.getAPT5GData1Pro_76P_EndDate();
                        String l_expire_76x = d_expire_760;
                        if (l_expire_76x.compareTo(d_expire_76P) < 0) {
                            l_expire_76x = d_expire_76P;
                        }
                        textbox_data_add_expire.setValue(NokiaDateFormat(String.valueOf(l_expire_76x)));
                        log.info("textbox_data_add_expire==>" + textbox_data_add_expire.getValue());

                        //網內免費通話金額(元) 830
                        double tmp_d_value_830 = 0.0;
                        tmp_d_value_830 = Double.valueOf(bean.getAPT5GVoiceOnnet_830_Counter()) / 100000;

                        double d_value_830 = Math.round(tmp_d_value_830 * 100.0) / 100.0;
                        String s_value_830 = String.valueOf(d_value_830);
                        textbox_onnet_balance.setValue(s_value_830);
                        log.info("textbox_onnet_balance==>" + textbox_onnet_balance.getValue());

                        //網內免費通話金額(元) 830 服務使用到期日	
                        textbox_onnet_expire.setValue(NokiaDateFormat(bean.getAPT5GVoiceOnnet_830_EndDate()));
                        log.info("textbox_onnet_expire==>" + textbox_onnet_expire.getValue());

                        //30天免費網內語音 服務使用到期日    
                        textbox_onnetx_expire.setValue(NokiaDateFormat(bean.getAPT5GVoiceOnPro_83P_EndDate()));
                        log.info("textbox_onnetx_expire==>" + textbox_onnetx_expire.getValue());

                        //5G計日型全速無限上網(530) 5GDataFUm
                        double d_value_530_x = 0.0;
                        d_value_530_x = Double.valueOf(bean.getDataFUm5G_530_Counter()) / 1048576;
                        double d_value_530 = Math.round(d_value_530_x * 100.0) / 100.0;

                        String d_expire_530 = bean.getDataFUm5G_530_EndDate();
                        log.info(mdn + " 5G計日型全速無限上網(530)==>" + d_value_530);
                        textbox_5GDataFUm_balance.setValue(String.valueOf(d_value_530) + " 日期:" + d_expire_530);

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
                        textbox_5GDataF21MbLm_balance.setValue(String.valueOf(d_value_531) + " 日期:" + l_expire_531_541);

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
                        textbox_5GDataF12MbLm_balance.setValue(String.valueOf(d_value_532) + " 日期:" + l_expire_532_542);

                        //5GDataWiFi 熱點分享數據量(555)
                        double d_value_555_x = 0.0;
                        d_value_555_x = Double.valueOf(bean.getAPT5GDataWiFi_555_Counter()) / 1048576;
                        double d_value_555 = Math.round(d_value_555_x * 100.0) / 100.0;

                        String d_expire_555 = bean.getAPT5GDataWiFi_555_EndDate();
                        log.info(mdn + " 熱點分享數據量(555)==>" + d_value_555);
                        textbox_5GDataWiFi_balance.setValue(String.valueOf(d_value_555) + " 日期" + d_expire_555);

                        //計日型數據 服務使用到期日 430 431 441 433 4P3 435 445 436 446 437
                        String Date_expiredate = getAPT5GDataExpireDate(bean);
                        textbox_APT5GDataFULLm_balance.setValue(NokiaDateFormat(Date_expiredate));
                        log.info("textbox_APT5GDataFULLm_balance==>" + textbox_APT5GDataFULLm_balance.getValue());

                    } else {
                        log.info("Exception===>MDN:" + mdn + ",contractstatuscode:" + contractstatuscode + ",producttype" + producttype);
                        Messagebox.show(mdn + "無此資料:contractstatuscode " + contractstatuscode, "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
                    }
                } else {
                    Messagebox.show("無此資料:Code " + resultcode, "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
                }
            } else {
                Messagebox.show("無此資料(No Such Profile)", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
            }
        } catch (Exception ex) {
            log.info(ex);
        }
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
}
