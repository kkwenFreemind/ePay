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
import com.apt.epay.nokia.main.NokiaMainLoginAndLifeCycleUtil;
import com.apt.epay.nokia.util.NokiaHLMdnLCUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.epay.ui.dtone.util.DTONEUtil;
import com.apt.epay.zte.util.ZTEOCS4GBasicInfoUtil;
import com.apt.util.SendSMS;
import com.apt.util.SoaProfile;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_PROMOTIONCODE;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class APP_OnlineCountryCodeLogin_Window extends Window {

    private final Logger log = Logger.getLogger("EPAY");

    SOAReqBean apirequestbean;
//    private final String xcode = "家鄉儲";
//    private boolean rflag = false;
    String source_otp = "";
    private String data = "";
    private String cpid;
    private String apisrcid = "";
    private String deskey = "";
    private String sms_mdn;
    private String key;

    public void onCreate() throws Exception {

        Radiogroup country_group = (Radiogroup) getSpaceOwner().getFellow("country_group");
        Textbox textbox_mdn = (Textbox) getSpaceOwner().getFellow("textbox_mdn");
        Textbox textbox_otp = (Textbox) getSpaceOwner().getFellow("textbox_otp");
        Textbox textbox_dtone_mdn = (Textbox) getSpaceOwner().getFellow("textbox_dtone_mdn");

        textbox_mdn.setValue("");
        textbox_otp.setValue("");
        textbox_dtone_mdn.setValue("");
        country_group.setSelectedIndex(0);

        Button sendLoginBtn = (Button) getSpaceOwner().getFellow("sendLoginBtn");
        sendLoginBtn.setDisabled(false);

        String remoteAddr = Executions.getCurrent().getRemoteAddr();
        log.info("Creating OnlineCountryCodeLogin_Window...from RemoteAddr ==> " + remoteAddr);

        HttpServletRequest req = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
        log.info("doAfterCompose.getQueryString():" + req.getQueryString());

//        String x_up_calling_line_id = "";
//        String x_up_min = "";
//        String x_up_imsi = "";
//        String min = "";
//        Enumeration enuma = req.getHeaderNames();
//        while (enuma.hasMoreElements()) {
//            String header_name = (String) enuma.nextElement();
//            String header_value = req.getHeader(header_name);
//            String head_info = String.format("header_name<=>[%s], header_value<=>[%s]", header_name, header_value);
//            log.info(head_info + "\n");
//            // X-Up-Min
//            if (header_name != null) {
//                log.info("[" + header_name + "] is " + "[" + header_value + "]");
//                if (header_name.equalsIgnoreCase("x-up-calling-line-id")) {
//                    x_up_calling_line_id = header_value;
//                }
//                if (header_name.equalsIgnoreCase("x-up-min")) {
//                    x_up_min = header_value;
//                }
//                if (header_name.equalsIgnoreCase("x-up-imsi")) {
//                    x_up_imsi = header_value;
//                }
//            }
//        }
        try {
            toolUtil tool = new toolUtil();
            Utilities util = new Utilities();

            cpid = req.getParameter("CPID");
            data = req.getParameter("DATA");

            log.info("cpid==>" + cpid);
            log.info("data==>" + data);

            if (data != null) {

                EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

                int icpid = Integer.valueOf(cpid);
                EPAY_CP_INFO cpinfo = epaybusinesscontroller.getCpInfo(icpid);

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

                    if (str_input.length() != 0) {
                        NOSSOReqBean aPIServiceOrdrReqBean;
                        aPIServiceOrdrReqBean = getMDNByparseXMLString(str_input, "ServiceAdjustAccountDeposit");
                        String mdn1 = aPIServiceOrdrReqBean.getMdn1();
                        String mdn2 = aPIServiceOrdrReqBean.getMdn2();
                        String cplibm = aPIServiceOrdrReqBean.getCplibm();
                        apisrcid = aPIServiceOrdrReqBean.getApisrcid();
                        String salesid = aPIServiceOrdrReqBean.getSalesid();
                        String storeid = aPIServiceOrdrReqBean.getStoreid();
                        String storename = aPIServiceOrdrReqBean.getStorename();

                        log.info("apisrcid===>" + apisrcid);

                        if ("".equalsIgnoreCase(apisrcid) || "null".equalsIgnoreCase(apisrcid)) {
                            this.detach();
                            Executions.sendRedirect("/dtone/OnlineNoSSOSSOErrorMsg.zhtml");
                        }
                    } else {
                        log.info("INPUT is NULL");
                        log.error("Parameter Data is Error!");
                        this.detach();
                        Executions.sendRedirect("/dtone/OnlineNoSSOSSOErrorMsg.zhtml");
                    }
                }
            } else {
                this.detach();
                Executions.sendRedirect("/dtone/OnlineNoSSOSSOErrorMsg.zhtml");
            }

        } catch (Exception ex) {
            log.info(ex);
        }
    }

    public void chkMDNBtn() throws Exception {

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox textbox_mdn = (Textbox) getSpaceOwner().getFellow("textbox_mdn");
        String check_mdn = textbox_mdn.getValue();

        Label messageLabel = (Label) getSpaceOwner().getFellow("messageLabel");

        if (check_mdn.length() == 10) {
            //step1 check mdn --> aptg pp number
            SoaProfile soa = new SoaProfile();
            String result = soa.putSoaProxyletByMDN(check_mdn);
            apirequestbean = soa.parseXMLString(result);
            log.info("kkflag==>" + result);

            String resultcode = apirequestbean.getResultcode();
            String mdn = apirequestbean.getMdn();
            String promotioncode = apirequestbean.getPromotioncode();

            if ("00000000".equals(resultcode)) {
                EPAY_PROMOTIONCODE epaypromotioncode;// = new EPAY_PROMOTIONCODE();
                epaypromotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);
                int platformtype = epaypromotioncode.getPlatformtype();
                BasicInfoReqBean basicinforeqbean;// = new BasicInfoReqBean();

                SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                Calendar nowDateTime = Calendar.getInstance();
                String libm = sdf15.format(nowDateTime.getTime());

                if (platformtype == 2) { //ZTE

                    ZTEOCS4GBasicInfoUtil ztebasicinfoutil = new ZTEOCS4GBasicInfoUtil();
                    String basicinfo = ztebasicinfoutil.putZTEOCS4GBasicInfoSlet(libm, mdn);
                    log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfo);
                    basicinforeqbean = ztebasicinfoutil.parseZTEBasicInfoXMLString(basicinfo);
                    log.info("basicinforeqbean.getLifeCycleState()====>" + basicinforeqbean.getLifeCycleState());
                    if (("ACTIVE".equalsIgnoreCase(basicinforeqbean.getLifeCycleState()))) {
                        log.info(mdn + "check MDN Success");
                        messageLabel.setValue("CHECK PASS");
//                        Messagebox.show("OK", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
                    } else {
                        //error
                        log.info(mdn + "check MDN Error");
                        messageLabel.setValue("CHECK FALSE");
                        textbox_mdn.setValue("");
//                        Messagebox.show("Error", "亞太電信", Messagebox.OK, Messagebox.ERROR);
                    }
                } else if (platformtype == 3) {

                    log.info(mdn + "Nokai MDN");
                    NokiaMainLoginAndLifeCycleUtil nutil = new NokiaMainLoginAndLifeCycleUtil();
                    String pid = nutil.login();
                    String LC = nutil.getMdnLifeCycle(libm, mdn, pid);
                    boolean logoutflag = nutil.logout(pid);

                    log.info(mdn + " Nokia Pid:" + pid + " login");
                    log.info(mdn + " Nokia LifeCycle ===>" + LC);
                    log.info(mdn + " Nokia " + pid + " logout=>" + logoutflag);

                    if ("ACTIVE".equalsIgnoreCase(LC)) {
                        log.info(mdn + "check MDN Success");
                        messageLabel.setValue("CHECK PASS");
//                        Messagebox.show("OK", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
                    } else {
                        //error
                        log.info(mdn + "check MDN Error");
                        messageLabel.setValue("CHECK FALSE");
                        textbox_mdn.setValue("");
//                        Messagebox.show("Error", "亞太電信", Messagebox.OK, Messagebox.ERROR);
                    }

                } else {
                    //error
                    log.info(mdn + "check MDN Error");
                    messageLabel.setValue("CHECK FALSE");
                    textbox_mdn.setValue("");
//                    Messagebox.show("Error", "亞太電信", Messagebox.OK, Messagebox.ERROR);
                }
            } else {
                //error
                log.info(mdn + "check MDN Error");
                messageLabel.setValue("CHECK FALSE");
                textbox_mdn.setValue("");
//                Messagebox.show("Error", "亞太電信", Messagebox.OK, Messagebox.ERROR);
            }
        } else {
            //error
//            log.info(mdn + "check MDN Error");
            messageLabel.setValue("CHECK FALSE");
            textbox_mdn.setValue("");
//            Messagebox.show("Error", "亞太電信", Messagebox.OK, Messagebox.ERROR);
        }
    }

    public void ReSendOTPBtn() throws Exception {

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox textbox_mdn = (Textbox) getSpaceOwner().getFellow("textbox_mdn");
        sms_mdn = textbox_mdn.getValue();

        if (sms_mdn.length() == 10) {

            //step1 check mdn --> aptg pp number
            SoaProfile soa = new SoaProfile();
            String result = soa.putSoaProxyletByMDN(sms_mdn);
            apirequestbean = soa.parseXMLString(result);
            log.info("kkflag==>" + result);

            String resultcode = apirequestbean.getResultcode();
            String mdn = apirequestbean.getMdn();
            String promotioncode = apirequestbean.getPromotioncode();

            if ("00000000".equals(resultcode)) {
                EPAY_PROMOTIONCODE epaypromotioncode;// = new EPAY_PROMOTIONCODE();
                epaypromotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);
                int platformtype = epaypromotioncode.getPlatformtype();
                BasicInfoReqBean basicinforeqbean;// = new BasicInfoReqBean();

                SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                Calendar nowDateTime = Calendar.getInstance();
                String libm = sdf15.format(nowDateTime.getTime());

                switch (platformtype) {
                    case 2:
                        //ZTE
                        ZTEOCS4GBasicInfoUtil ztebasicinfoutil = new ZTEOCS4GBasicInfoUtil();
                        String basicinfo = ztebasicinfoutil.putZTEOCS4GBasicInfoSlet(libm, mdn);
                        log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfo);
                        //
                        basicinforeqbean = ztebasicinfoutil.parseZTEBasicInfoXMLString(basicinfo);
                        log.info("basicinforeqbean.getReal_LifeCycleState()====>" + basicinforeqbean.getReal_LifeCycleState());
                        if (("ACTIVE".equalsIgnoreCase(basicinforeqbean.getLifeCycleState()))) {

                            source_otp = getOTP();
                            //send sms : OPT
                            SendSMS xsms = new SendSMS();
                            String xmsg = source_otp;
                            log.info(sms_mdn + "==>" + xmsg);
                            try {
                                Button rtbutton = (Button) getSpaceOwner().getFellow("sendOTPBtn");
                                rtbutton.setDisabled(true);
                                xsms.sendOTPsms(sms_mdn, xmsg);
                            } catch (Exception ex) {
                                log.info(ex);
                            }
                        } else {
                            log.info(mdn + ":LifeCycleState()==>" + basicinforeqbean.getLifeCycleState());
                            log.info(mdn + ":RealLifeCycleState()==>" + basicinforeqbean.getReal_LifeCycleState());
                            this.detach();
                            Executions.sendRedirect("/dtone/OnlineAddValueMdnMsg.zhtml");
                        }
                        break;
                    case 3:
                        log.info(mdn + "Nokai MDN");
                        NokiaMainLoginAndLifeCycleUtil nutil = new NokiaMainLoginAndLifeCycleUtil();
                        String pid = nutil.login();
                        String LC = nutil.getMdnLifeCycle(libm, mdn, pid);
                        boolean logoutflag = nutil.logout(pid);

                        log.info(mdn + " Nokia Pid:" + pid + " login");
                        log.info(mdn + " Nokia LifeCycle ===>" + LC);
                        log.info(mdn + " Nokia " + pid + " logout=>" + logoutflag);

                        if ("ACTIVE".equalsIgnoreCase(LC)) {
                            source_otp = getOTP();
                            //send sms : OPT
                            SendSMS xsms = new SendSMS();
                            String xmsg = source_otp;
                            log.info(sms_mdn + "==>" + xmsg);
                            try {
                                Button rtbutton = (Button) getSpaceOwner().getFellow("sendOTPBtn");
                                rtbutton.setDisabled(true);
                                xsms.sendOTPsms(sms_mdn, xmsg);
                            } catch (Exception ex) {
                                log.info(ex);
                            }
                        } else {
                            log.info(mdn + ":LifeCycleState()==>" + LC);
                            this.detach();
                            Executions.sendRedirect("/dtone/OnlineAddValueMdnMsg.zhtml");
                        }
                        break;

                    default:
                        log.info(mdn + ":platformtype==>" + platformtype);
                        this.detach();
                        Executions.sendRedirect("/dtone/OnlineAddValueMdnMsg.zhtml");
                        break;
                }

            } else {
                log.info(mdn + ":resultcode==>" + resultcode);
                this.detach();
                Executions.sendRedirect("/dtone/OnlineAddValueMdnMsg.zhtml");
            }

        } else {
            log.info(sms_mdn + " is Wrong");
            Messagebox.show("請輸入正確的門號", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
        }
    }

    public void sendLoginBtn() throws Exception {

        Button sendLoginBtn = (Button) getSpaceOwner().getFellow("sendLoginBtn");
        sendLoginBtn.setDisabled(true);

        //Step 1 : check ID4
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox textbox_mdn = (Textbox) getSpaceOwner().getFellow("textbox_mdn");
        String check_mdn = textbox_mdn.getValue();
        Textbox textbox_otp = (Textbox) getSpaceOwner().getFellow("textbox_otp");
        Label countrycode = (Label) getSpaceOwner().getFellow("country");

        Textbox textbox_dtone_mdn = (Textbox) getSpaceOwner().getFellow("textbox_dtone_mdn");
        String tmp_dtone_mdn = countrycode.getValue() + textbox_dtone_mdn.getValue();
        String dtone_mdn = tmp_dtone_mdn.substring(1);
        log.info("dtone_mdn ==>" + dtone_mdn);

        Label label_otp = (Label) getSpaceOwner().getFellow("label_otp");
        String optStr = label_otp.getValue();

        String text_otp = optStr + textbox_otp.getValue();
        log.info("text_otp ==>" + text_otp);

        if (check_mdn.equalsIgnoreCase(sms_mdn)) {
            if (sms_mdn.length() == 10) {

                //step1 check mdn --> aptg pp number
                SoaProfile soa = new SoaProfile();
                String result = soa.putSoaProxyletByMDN(sms_mdn);
                apirequestbean = soa.parseXMLString(result);
                log.info("kkflag==>" + result);

                String resultcode = apirequestbean.getResultcode();
                String mdn = apirequestbean.getMdn();
                String promotioncode = apirequestbean.getPromotioncode();

                if ("00000000".equals(resultcode)) {
                    EPAY_PROMOTIONCODE epaypromotioncode;// = new EPAY_PROMOTIONCODE();
                    epaypromotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);
                    int platformtype = epaypromotioncode.getPlatformtype();

                    SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                    Calendar nowDateTime = Calendar.getInstance();
                    String libm = sdf15.format(nowDateTime.getTime());

                    BasicInfoReqBean basicinforeqbean;// = new BasicInfoReqBean();
                    String LC = "";

                    if (platformtype == 2) { //ZTE

                        ZTEOCS4GBasicInfoUtil ztebasicinfoutil = new ZTEOCS4GBasicInfoUtil();
                        String basicinfo = ztebasicinfoutil.putZTEOCS4GBasicInfoSlet(libm, mdn);
                        log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfo);
                        basicinforeqbean = ztebasicinfoutil.parseZTEBasicInfoXMLString(basicinfo);
                        log.info("basicinforeqbean.getLifeCycleState()====>" + basicinforeqbean.getLifeCycleState());
                        LC = basicinforeqbean.getLifeCycleState();
                        log.info(mdn + " ZTE LC ==>"+ LC);

                    } else if (platformtype == 3) {

                        log.info(mdn + "Nokai MDN");
                        NokiaMainLoginAndLifeCycleUtil nutil = new NokiaMainLoginAndLifeCycleUtil();
                        String pid = nutil.login();
                        LC = nutil.getMdnLifeCycle(libm, mdn, pid);
                        boolean logoutflag = nutil.logout(pid);

                        log.info(mdn + " Nokia Pid:" + pid + " login");
                        log.info(mdn + " Nokia LifeCycle ===>" + LC);
                        log.info(mdn + " Nokia " + pid + " logout=>" + logoutflag);
                        
                        log.info(mdn + " Nokia LC ==>"+ LC);

                    } else {
                        log.info(mdn + ":platformtype==>" + platformtype);
                        this.detach();
                        Executions.sendRedirect("/dtone/OnlineAddValueMdnMsg.zhtml");
                    }

                    if (("ACTIVE".equalsIgnoreCase(LC))) {

                        log.info(mdn + " check MDN Success");

                        //Step 2 : check ID
                        //Step3 : Check OTP
                        if (text_otp.equalsIgnoreCase(String.valueOf(source_otp))) {
                            log.info(mdn + " check OTP Success");
                            //Step4 check Dtone_mdn
                            if (dtone_mdn.length() > 5) {

                                DTONEUtil dtone = new DTONEUtil();
                                String dt_operator_name = dtone.getMobileNumberInfo(dtone_mdn);

                                log.info(dtone_mdn + " dt_operator_name====>" + dt_operator_name);

                                if (!"-1".equalsIgnoreCase(dt_operator_name)) {
                                    StringBuffer originalParm = new StringBuffer();
                                    originalParm.append("apisrcid=").append(apisrcid).append("&")
                                                    .append("apt_mdn=").append(sms_mdn).append("&")
                                                    .append("dtone_mdn=").append(dtone_mdn).append("&")
                                                    .append("dt_operator_name=").append(dt_operator_name).append("&")
                                                    .append("countrycode=").append("na");

                                    log.info("originalParm==>" + originalParm);
                                    Utilities util = new Utilities();
                                    String data = util.encrypt(deskey, originalParm.toString());
                                    log.info("data==>" + data);

                                    postToNextPage(cpid, data);
                                } else {
                                    log.info(dtone_mdn + " dt_operator_name is Wrong");
                                    this.detach();
                                    Executions.sendRedirect("/dtone/OnlineAddValueRejectedMsg.zhtml");
                                }

                            } else {
                                log.info(dtone_mdn + " is Wrong");
                                this.detach();
                                Executions.sendRedirect("/dtone/OnlineAddValueRejectedMsg.zhtml");
                            }

                            log.info(mdn + "start to postToNextPage(" + cpid + "," + apisrcid + "," + sms_mdn + ")");

                        } else {
                            log.info(mdn + ":OPT==>" + source_otp + " is wrong");
                            this.detach();
                            Executions.sendRedirect("/dtone/OnlineAddValueMdnMsg.zhtml");
                        }

                    } else {
                        log.info(dtone_mdn + " LC is Wrong: " + LC);
                        this.detach();
                        Executions.sendRedirect("/dtone/OnlineAddValueRejectedMsg.zhtml");
                    }

                } else {
                    log.info(mdn + ":resultcode==>" + resultcode);
                    this.detach();
                    Executions.sendRedirect("/dtone/OnlineAddValueMdnMsg.zhtml");
                }

            } else {
                log.info(sms_mdn + " is Wrong");
                this.detach();
                Executions.sendRedirect("/dtone/OnlineAddValueMdnMsg.zhtml");
            }
        } else {
            log.info("(SMS_MDN,TBOX_MDN)==>" + sms_mdn + "," + check_mdn);
            this.detach();
            Executions.sendRedirect("/dtone/OnlineAddValueMdnMsg.zhtml");
        }

    }

    private void postToNextPage(String systemid, String data) throws Exception {

        Sessions.getCurrent().setAttribute("cpid", systemid);
        Sessions.getCurrent().setAttribute("data", data);
        String CountryCode_URL = new ShareParm().PARM_COUNTRYCODE_URL;

        String sendURL = "";
        sendURL = CountryCode_URL + "/OnlineCountryCodeService.zhtml?CPID=" + systemid; //台灣
        log.info("systemid,data,sendURL===>" + systemid + "," + data + "," + sendURL);

        Sessions.getCurrent().setAttribute("sendURL", sendURL);
        Executions.getCurrent().setAttribute(org.zkoss.zk.ui.sys.Attributes.NO_CACHE, Boolean.TRUE);

        Window window = (Window) Executions.createComponents("/dtone/OnlineDepositSend.zul", null, null);
    }

    public void sendOTPBtn() throws Exception {

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox textbox_mdn = (Textbox) getSpaceOwner().getFellow("textbox_mdn");
        sms_mdn = textbox_mdn.getValue();

        if (sms_mdn.length() == 10) {

            //step1 check mdn --> aptg pp number
            SoaProfile soa = new SoaProfile();
            String result = soa.putSoaProxyletByMDN(sms_mdn);
            apirequestbean = soa.parseXMLString(result);
            log.info("kkflag==>" + result);

            String resultcode = apirequestbean.getResultcode();
            String mdn = apirequestbean.getMdn();
            String promotioncode = apirequestbean.getPromotioncode();

            if ("00000000".equals(resultcode)) {
                EPAY_PROMOTIONCODE epaypromotioncode;// = new EPAY_PROMOTIONCODE();
                epaypromotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);
                int platformtype = epaypromotioncode.getPlatformtype();
                BasicInfoReqBean basicinforeqbean;// = new BasicInfoReqBean();

                SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                Calendar nowDateTime = Calendar.getInstance();
                String libm = sdf15.format(nowDateTime.getTime());

                switch (platformtype) {
                    case 2:
                        //ZTE

                        ZTEOCS4GBasicInfoUtil ztebasicinfoutil = new ZTEOCS4GBasicInfoUtil();
                        String basicinfo = ztebasicinfoutil.putZTEOCS4GBasicInfoSlet(libm, mdn);
                        log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfo);
                        //
                        basicinforeqbean = ztebasicinfoutil.parseZTEBasicInfoXMLString(basicinfo);
                        log.info("basicinforeqbean.getReal_LifeCycleState()====>" + basicinforeqbean.getReal_LifeCycleState());
                        if (("ACTIVE".equalsIgnoreCase(basicinforeqbean.getLifeCycleState()))) {

                            source_otp = getOTP();
                            //send sms : OPT
                            SendSMS xsms = new SendSMS();
                            String xmsg = source_otp;
                            log.info(sms_mdn + "==>" + xmsg);
                            try {
                                Button rtbutton = (Button) getSpaceOwner().getFellow("sendOTPBtn");
                                rtbutton.setDisabled(true);
                                xsms.sendOTPsms(sms_mdn, xmsg);
                            } catch (Exception ex) {
                                log.info(ex);
                            }
                        } else {
                            log.info(mdn + ":LifeCycleState()==>" + basicinforeqbean.getLifeCycleState());
                            log.info(mdn + ":RealLifeCycleState()==>" + basicinforeqbean.getReal_LifeCycleState());
                            this.detach();
                            Executions.sendRedirect("/dtone/OnlineAddValueMdnMsg.zhtml");
                        }
                        break;
                    case 3:

                        log.info(mdn + "Nokai MDN");
                        NokiaMainLoginAndLifeCycleUtil nutil = new NokiaMainLoginAndLifeCycleUtil();
                        String pid = nutil.login();
                        String LC = nutil.getMdnLifeCycle(libm, mdn, pid);
                        boolean logoutflag = nutil.logout(pid);

                        log.info(mdn + " Nokia Pid:" + pid + " login");
                        log.info(mdn + " Nokia LifeCycle ===>" + LC);
                        log.info(mdn + " Nokia " + pid + " logout=>" + logoutflag);

                        if ("ACTIVE".equalsIgnoreCase(LC)) {
                            source_otp = getOTP();
                            //send sms : OPT
                            SendSMS xsms = new SendSMS();
                            String xmsg = source_otp;
                            log.info(sms_mdn + "==>" + xmsg);
                            try {
                                Button rtbutton = (Button) getSpaceOwner().getFellow("sendOTPBtn");
                                rtbutton.setDisabled(true);
                                xsms.sendOTPsms(sms_mdn, xmsg);
                            } catch (Exception ex) {
                                log.info(ex);
                            }
                        } else {
                            log.info(mdn + ":LifeCycleState()==>" + LC);
                            this.detach();
                            Executions.sendRedirect("/dtone/OnlineAddValueMdnMsg.zhtml");
                        }

                        break;
                    default:
                        log.info(mdn + ":platformtype==>" + platformtype);
                        this.detach();
                        Executions.sendRedirect("/dtone/OnlineAddValueMdnMsg.zhtml");
                        break;
                }

            } else {
                log.info(mdn + ":resultcode==>" + resultcode);
                this.detach();
                Executions.sendRedirect("/dtone/OnlineAddValueMdnMsg.zhtml");
            }

        } else {
            log.info(sms_mdn + " is Wrong");
            Messagebox.show("請輸入正確的門號", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
        }
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

    private int produceNumber(int maxLength) {
        Random random = new Random();
        return random.nextInt(maxLength);
    }

    private String doProduce(int maxLength, String source) {
        StringBuilder sb = new StringBuilder(100);
        for (int i = 0; i < maxLength; i++) {
            int number = produceNumber(source.length());
            sb.append(source.charAt(number));
        }
        return sb.toString();
    }

    private String produceString(int maxLength) {
        String source = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return doProduce(maxLength, source);
    }

    private String getOTP() {
        int kkresult = 0;
        String result = "";
        for (int j = 0; j < 100; j++) {
            kkresult = ((int) ((Math.random() * 9 + 1) * 100000));
        }

        String optstr = produceString(3);
        result = optstr + "-" + kkresult;

//        Textbox textbox_otp = (Textbox) getSpaceOwner().getFellow("textbox_otp");
//        textbox_otp.setValue(optstr + "-");
        Label label_otp = (Label) getSpaceOwner().getFellow("label_otp");
        label_otp.setValue(optstr + "-");

        return result;
    }
}
