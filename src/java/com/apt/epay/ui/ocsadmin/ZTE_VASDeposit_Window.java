/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.ocsadmin;

import com.apt.epay.beans.BasicInfoReqBean;
import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.epay.zte.util.ZTEAdjustAccUtil;
import com.apt.epay.zte.util.ZTEOCS4GBasicInfoUtil;
import com.apt.util.SendSMS;
import com.apt.util.SoaProfile;
import com.epay.ejb.bean.EPAY_COMMON_USER;
import com.epay.ejb.bean.EPAY_PROMOTIONCODE;
import com.epay.ejb.bean.EPAY_SERVICE_INFO;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.zkoss.zhtml.Messagebox;
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
public class ZTE_VASDeposit_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");
    private String cpid = "10001";
    SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);
    int source_otp = 0;
    String sms_mdn = "";
    String user_id = "";
    String[] ch_serviceid;// = new String[100];
    String remoteAddr = "";

    public void onCreate() {

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        remoteAddr = Executions.getCurrent().getRemoteAddr();
        log.info("Creating Online Deposit...from RemoteAddr " + remoteAddr);

        Textbox text_user_code = (Textbox) getSpaceOwner().getFellow("text_user_code");
        user_id = (String) Sessions.getCurrent().getAttribute("user_code");

        EPAY_COMMON_USER user;// = new EPAY_COMMON_USER();
        user = epaybusinesscontroller.getCommonUser_ByCode(user_id);
        if (user != null) {
            String ocs_flag = user.getOcs_flag();
            String user_ipaddr = user.getCuser_ipaddr();

            if ("1".equals(ocs_flag)) {

                if (remoteAddr.equals(user_ipaddr)) {
                    log.info(user_id + " is trying to do ocs function");
                    text_user_code.setValue(user_id);

                    //for test 0968422005
                    Textbox text_mdn = (Textbox) getSpaceOwner().getFellow("text_mdn");
                    text_mdn.setValue("0968422005");
                } else {
                    Messagebox.show(user_id + " IP有誤", "亞太電信", Messagebox.OK, Messagebox.ERROR);
                    Executions.sendRedirect("/admin/main.zul");
                }
            } else {
                Messagebox.show(user_id + "請產品PM通知開啟儲值權限", "亞太電信", Messagebox.OK, Messagebox.ERROR);
                Executions.sendRedirect("/admin/main.zul");
            }
        }
    }

//    private void clear_old_data() throws Exception {
//
//        Textbox text_mdn = (Textbox) getSpaceOwner().getFellow("text_mdn");
//        text_mdn.setConstraint("");
//        text_mdn.setValue("");
//        text_mdn.setConstraint("no empty");
//
//        Textbox text_priceplancode = (Textbox) getSpaceOwner().getFellow("text_priceplancode");
//        text_priceplancode.setConstraint("");
//        text_priceplancode.setValue("");
//        text_priceplancode.setConstraint("no empty");
//
//        Textbox text_otp = (Textbox) getSpaceOwner().getFellow("text_otp");
//        text_otp.setConstraint("");
//        text_otp.setValue("");
//        source_otp = 0;
//
//        ch_serviceid = new String[100];
//        Combobox combo_serviceid = (Combobox) getSpaceOwner().getFellow("combo_priceplancode");
//        combo_serviceid.removeChild(this);
//    }
    public void onGetPricePlanCode() throws Exception {

        ch_serviceid = new String[100];
        Textbox text_mdn = (Textbox) getSpaceOwner().getFellow("text_mdn");
        String mdn = text_mdn.getValue();
        int length_mdn = mdn.length();

        if (length_mdn == 10) {
            EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

            SOAReqBean apirequestbean;
            //check mdn
            SoaProfile soa = new SoaProfile();
            String kkresult = soa.putSoaProxyletByMDN(mdn);
            apirequestbean = soa.parseXMLString(kkresult);

            log.info("kkflag==>ZTE_ADMIN_VASDeposit:" + kkresult);
//            String resultcode = apirequestbean.getResultcode();
            String promotioncode = apirequestbean.getPromotioncode();
            int platformtype = 2;
            log.info("PromotionCode ===>" + promotioncode);

            Combobox combo_serviceid = (Combobox) getSpaceOwner().getFellow("combo_priceplancode");

            List serviceinfo1;
            serviceinfo1 = epaybusinesscontroller.listAllServiceInfo(Integer.valueOf(cpid), promotioncode, platformtype);

            if (serviceinfo1.size() > 0) {

                Iterator itserviceinfo1 = serviceinfo1.iterator();
                int j = 0;

                while (itserviceinfo1.hasNext()) {

                    EPAY_SERVICE_INFO serid = (EPAY_SERVICE_INFO) itserviceinfo1.next();

                    Comboitem coitem_serviceid = new Comboitem();
                    coitem_serviceid.setId("combo" + serid.getPriceplancode());
                    coitem_serviceid.setLabel(serid.getServiceName());
                    combo_serviceid.appendChild(coitem_serviceid);
                    ch_serviceid[j] = String.valueOf(serid.getPriceplancode());
                    j++;
                }
                combo_serviceid.setSelectedIndex(1);
            } else {
                Messagebox.show("門號有誤，無促案可選，請查明後繼續", "亞太電信", Messagebox.OK, Messagebox.ERROR);
            }
        } else {
            Messagebox.show("門號有誤，請查明後繼續", "亞太電信", Messagebox.OK, Messagebox.ERROR);
        }

    }

    public void onGetOTP() throws Exception {

        source_otp = getOTP();

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        EPAY_COMMON_USER user;// = new EPAY_COMMON_USER();
        user = epaybusinesscontroller.getCommonUser_ByCode(user_id);
        if (user != null) {
            sms_mdn = user.getCuser_mobile_number();
            SendSMS xsms = new SendSMS();
            String xmsg = "OTP:" + source_otp;
            log.info(user_id + "==>" + xmsg);
            try {
                xsms.sendsms(sms_mdn, xmsg);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void onCommit() throws Exception {

        if (source_otp != 0) {

            Textbox text_otp = (Textbox) getSpaceOwner().getFellow("text_otp");
            int user_otp = Integer.valueOf(text_otp.getValue());

            //OTP One Time Password
            if (source_otp == user_otp) {

                EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

                Textbox text_user_code = (Textbox) getSpaceOwner().getFellow("text_user_code");
                Textbox text_mdn = (Textbox) getSpaceOwner().getFellow("text_mdn");
                Combobox combo_serviceid = (Combobox) getSpaceOwner().getFellow("combo_priceplancode");

                String price_plan_code = ch_serviceid[combo_serviceid.getSelectedIndex()];
                String user_code = text_user_code.getValue();
                String mdn = text_mdn.getValue();

                log.info("input mdn ===>" + mdn);
                log.info("input price_plan_code ==>" + price_plan_code);

                SOAReqBean apirequestbean;

                //check mdn
                SoaProfile soa = new SoaProfile();
                String result = soa.putSoaProxyletByMDN(mdn);
                apirequestbean = soa.parseXMLString(result);

                log.info("kkflag==>ZTE_ADMIN_VASDeposit:" + result);
                String resultcode = apirequestbean.getResultcode();
                String promotioncode = apirequestbean.getPromotioncode();

                if (("00000000".equals(resultcode) && !"".equals(promotioncode))) {

//                    SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);

                    EPAY_PROMOTIONCODE epaypromotioncode;// = new EPAY_PROMOTIONCODE();
                    epaypromotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);

                    SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                    Calendar nowDateTime = Calendar.getInstance();
                    String libm = sdf15.format(nowDateTime.getTime());

                    if (epaypromotioncode != null) {
                        int platformtype = epaypromotioncode.getPlatformtype();
                        BasicInfoReqBean basicinforeqbean;//= new BasicInfoReqBean();

                        if (platformtype == 2) {
                            ZTEOCS4GBasicInfoUtil ztebasicinfoutil = new ZTEOCS4GBasicInfoUtil();
                            String basicinfo = ztebasicinfoutil.putZTEOCS4GBasicInfoSlet(libm, mdn);
                            log.info("ZTE QueryBasicInfo===>MDN:" + mdn + " ZTE OCSXmlResult=>" + basicinfo);
                            basicinforeqbean = ztebasicinfoutil.parseZTEBasicInfoXMLString(basicinfo);
                            log.info("ZTE basicinforeqbean.getLifeCycleState()====>" + basicinforeqbean.getLifeCycleState());
                            String ZTE_ERRORDESC1 = ShareParm.ZTE_ERRORDESC1;
                            String ZTE_ERRORDESC2 = ShareParm.ZTE_ERRORDESC2;
                            if (basicinfo.contains(ZTE_ERRORDESC1) || basicinfo.contains(ZTE_ERRORDESC2)) {
                                Messagebox.show("ZTE_ERRORDESC", "亞太電信", Messagebox.OK, Messagebox.ERROR);
                            } else {
                                log.info("platformtype===>" + platformtype);
                                log.info("basicinforeqbean.getLifeCycleState()==>" + basicinforeqbean.getLifeCycleState());

                                if (("ACTIVE".equalsIgnoreCase(basicinforeqbean.getLifeCycleState())) || ("CED1".equalsIgnoreCase(basicinforeqbean.getLifeCycleState())) || ("CED10".equalsIgnoreCase(basicinforeqbean.getLifeCycleState())) || ("CED3".equalsIgnoreCase(basicinforeqbean.getLifeCycleState()))) {

                                    //檢查price plan code
                                    EPAY_SERVICE_INFO serid;
                                    serid = epaybusinesscontroller.getServiceInfoByPricePlanCode(price_plan_code);
                                    if (serid != null) {
                                        String tmp_promotioncode_strlist = serid.getPromotioncode();
                                        if (tmp_promotioncode_strlist.contains(promotioncode)) {
                                            log.info(mdn + " Promotioncode check(" + promotioncode + ") success");
//                                            int channeltype = 1;

                                            String sid = String.valueOf(serid.getServiceId());
                                            String tradedate = sdf_pincode.format(nowDateTime.getTime());

                                            log.info("PosZTEAdjustBucket===>" + cpid + "," + sid + "," + serid.getServiceName() + "," + mdn + ',' + tradedate);

                                            try {
                                                ZTEAdjustAccUtil zteadjust = new ZTEAdjustAccUtil();
//                                                String resultflag = zteadjust.putPosZTEOCS4GPricePlanCode(mdn, price_plan_code, channeltype);
                                                String resultflag = "00";
                                                log.info("zteadjust.PosZTEAdjustBucket Result==>" + resultflag);

                                                if ("00".equals(resultflag)) {
                                                    Messagebox.show(mdn + "儲值成功", "亞太電信", Messagebox.OK, Messagebox.ERROR);
                                                    Executions.sendRedirect("/admin/main.zul");
                                                } else {
                                                    Messagebox.show(mdn + "儲值失敗", "亞太電信", Messagebox.OK, Messagebox.ERROR);
                                                    Executions.sendRedirect("/admin/main.zul");
                                                }

                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }
                                        } else {
                                            Messagebox.show("PromotionCode有誤(" + promotioncode + ")，請查明後繼續", "亞太電信", Messagebox.OK, Messagebox.ERROR);
                                        }
                                    } else {
                                        Messagebox.show("Price_Plan_Code有誤(" + price_plan_code + ")，請查明後繼續", "亞太電信", Messagebox.OK, Messagebox.ERROR);
                                    }

                                } else {
                                    Messagebox.show("LifeCycleState有誤(" + basicinforeqbean.getLifeCycleState() + ")，請查明後繼續", "亞太電信", Messagebox.OK, Messagebox.ERROR);
                                }
                            }
                        } else {
                            Messagebox.show("PlatformType不屬於中興，請查明後繼續", "亞太電信", Messagebox.OK, Messagebox.ERROR);
                        }

                    } else {
                        Messagebox.show("Promotion Code Error(" + promotioncode + "，請查明後繼續", "亞太電信", Messagebox.OK, Messagebox.ERROR);
                    }
                } else {

                    Messagebox.show("門號有誤，請查明後繼續", "亞太電信", Messagebox.OK, Messagebox.ERROR);
                }
            } else {
                Messagebox.show("OPT密碼不符", "亞太電信", Messagebox.OK, Messagebox.ERROR);
            }
        } else {
            Messagebox.show("請取得OTP後繼續", "亞太電信", Messagebox.OK, Messagebox.ERROR);
        }
    }

    private int getOTP() {
        int result = 0;
        for (int j = 0; j < 100; j++) {
            result = ((int) ((Math.random() * 9 + 1) * 100000));
        }
        return result;
    }

}
