/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.dtone;

import com.apt.epay.beans.BasicInfoReqBean;
import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.nokia.bean.NokiaSubscribeBalanceBean;
import com.apt.epay.nokia.main.NokiaMainBasicInfoUtil;
import com.apt.epay.nokia.main.NokiaMainLoginAndLifeCycleUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.epay.zte.util.ZTEOCS4GBasicInfoUtil;
import com.apt.util.SoaProfile;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_CP_INFO;
//import com.epay.ejb.bean.EPAY_DTONESERVICE_INFO;
import com.epay.ejb.bean.EPAY_PROMOTIONCODE;
//import static java.lang.Math.min;
import java.text.SimpleDateFormat;
import java.util.Calendar;
//import java.util.Iterator;
//import java.util.List;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Window;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Label;
//import org.zkoss.zul.Radio;
//import org.zkoss.zul.Radiogroup;
//import org.zkoss.zul.Textbox;
//import org.zkoss.zul.Vbox;

/**
 *
 * @author kevinchang
 */
public class OnlineCountryCode_Back extends Window {

    private final Logger log = Logger.getLogger("EPAY");
    private String cpid;//= "10001";
    private String dtone_mdn;// = "639954726025";
    private String libm = "";
    private String promotioncode;
    private String contractid;
    private int platformtype;
    private String apisrcid;
    private String apt_mdn, dt_operator_name;
    private String key;

    public void onCreate() throws Exception {

        Label textbox_balance61 = (Label) getSpaceOwner().getFellow("balance");

        HttpServletRequest req = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
        log.info("req.getQueryString()==>" + req.getQueryString());

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        Utilities util = new Utilities();

        cpid = req.getParameter("cpid");
        String data = req.getParameter("data");

        log.info("cpid==>" + cpid);
        log.info("data==>" + data);

        if (cpid == null || "".equals(cpid.trim()) || data == null || "".equals(data.trim())) {
            this.detach();
            log.error("Parameter Data is null!");
            Executions.sendRedirect("/dtone/OnlineNoSSOSSOErrorMsg.zhtml");
        } else {
            try {
                EPAY_CP_INFO cpinfo = epaybusinesscontroller.getCpInfo(Integer.valueOf(cpid));
                if (cpinfo != null) {
                    key = cpinfo.getPoskey();
                    String decodeMsg = util.decrypt(key, data);
                    log.info("INPUT==>" + decodeMsg);
                    StringTokenizer st = new StringTokenizer(decodeMsg, "&");
                    while (st.hasMoreTokens()) {
                        String parms[] = st.nextElement().toString().split("=");
                        if ("apt_mdn".equalsIgnoreCase(parms[0])) {
                            apt_mdn = parms.length == 1 ? "" : parms[1];
                            if ("".equals(apt_mdn)) {
                                log.error("門號為空值!");
                                this.detach();
                                Executions.sendRedirect("/dtone/OnlineNoSSOSSOErrorMsg.zhtml");
                            }
                        }
                        if ("apisrcid".equalsIgnoreCase(parms[0])) {
                            apisrcid = parms.length == 1 ? "" : parms[1];
                            if ("".equals(apisrcid)) {
                                log.error("APISRC為空值!");
                                this.detach();
                                Executions.sendRedirect("/dtone/OnlineNoSSOSSOErrorMsg.zhtml");
                            }
                        }
                        if ("dt_operator_name".equalsIgnoreCase(parms[0])) {
                            dt_operator_name = parms.length == 1 ? "" : parms[1];
                            if ("".equals(dt_operator_name)) {
                                log.error("dt_operator_name為空值!");
                                this.detach();
                                Executions.sendRedirect("/dtone/OnlineNoSSOSSOErrorMsg.zhtml");
                            }
                        }
                        if ("dtone_mdn".equalsIgnoreCase(parms[0])) {
                            dtone_mdn = parms.length == 1 ? "" : parms[1];
                            if ("".equals(dtone_mdn)) {
                                log.error("dtone_mdn為空值!");
                                this.detach();
                                Executions.sendRedirect("/dtone/OnlineNoSSOSSOErrorMsg.zhtml");
                            }
                        }
                    }
                }

            } catch (Exception ex) {
                log.info(ex);
            }
        }

        log.info("cpid==>" + cpid);
        log.info("apisrcid==>" + apisrcid);
        log.info("apt_mdn==>" + apt_mdn);
        log.info("dtone_mdn==>" + dtone_mdn);
        log.info("dt_operator_name==>" + dt_operator_name);

        try {
            int icpid = Integer.valueOf(cpid);

            if (apt_mdn.length() == 10 && dtone_mdn.length() > 5 && !"".equalsIgnoreCase(dt_operator_name)) {

                SoaProfile soa = new SoaProfile();
                String result = soa.putSoaProxyletByMDN(apt_mdn);
                SOAReqBean apirequestbean;
                apirequestbean = soa.parseXMLString(result);

                log.info("kkflag==>" + result);

                String resultcode = apirequestbean.getResultcode();
                contractid = apirequestbean.getContractid();
                promotioncode = apirequestbean.getPromotioncode();

                if ("00000000".equals(resultcode)) {
                    EPAY_PROMOTIONCODE epaypromotioncode;// = new EPAY_PROMOTIONCODE();
                    epaypromotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);
                    platformtype = epaypromotioncode.getPlatformtype();

                    BasicInfoReqBean basicinforeqbean;// = new BasicInfoReqBean();
                    BasicInfoReqBean basicinforeqbeanx;// = new BasicInfoReqBean();
                    int ddday = 0;
                    double sumx = 0;

                    SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                    Calendar nowDateTime = Calendar.getInstance();
                    String libm = sdf15.format(nowDateTime.getTime());

                    if (platformtype == 1) {//ALU

                    } else if (platformtype == 2) { //ZTE

                        ZTEOCS4GBasicInfoUtil ztebasicinfoutil = new ZTEOCS4GBasicInfoUtil();
                        String basicinfo = ztebasicinfoutil.putZTEOCS4GBasicInfoSlet(libm, apt_mdn);
                        log.info("QueryBasicInfo===>MDN:" + apt_mdn + " OCSXmlResult=>" + basicinfo);

                        basicinforeqbean = ztebasicinfoutil.parseZTEBasicInfoXMLString(basicinfo);
                        log.info("basicinforeqbean.getReal_LifeCycleState()====>" + basicinforeqbean.getReal_LifeCycleState());
                        if (("ACTIVE".equalsIgnoreCase(basicinforeqbean.getReal_LifeCycleState()))) {
                            ZTEOCS4GBasicInfoUtil ztebasicinfoutilx = new ZTEOCS4GBasicInfoUtil();
                            String basicinfox = ztebasicinfoutilx.putZTEOCS4GBasicInfoSlet(libm, apt_mdn);
                            log.info("QueryBasicInfo===>MDN:" + apt_mdn + " OCSXmlResult=>" + basicinfox);

                            basicinforeqbeanx = ztebasicinfoutilx.parseZTEBasicInfoXMLString(basicinfo);

                            double count61x = Double.valueOf(basicinforeqbeanx.getCounterValue1());
                            textbox_balance61.setValue(String.valueOf(count61x));

                            log.info(apt_mdn + " 61:count1x()==>" + count61x);
                        }

                    } else if (platformtype == 3) {

                        log.info(apt_mdn + "Nokai MDN");
                        NokiaMainLoginAndLifeCycleUtil nutil = new NokiaMainLoginAndLifeCycleUtil();
                        String pid = nutil.login();
                        String LC = nutil.getMdnLifeCycle(libm, apt_mdn, pid);
                        boolean logoutflag = nutil.logout(pid);
                        if (("ACTIVE".equalsIgnoreCase(LC))) {
                            //門號帳本資訊
                            NokiaSubscribeBalanceBean bean = new NokiaSubscribeBalanceBean();
                            NokiaMainBasicInfoUtil mutil = new NokiaMainBasicInfoUtil();

                            bean = mutil.GetMDNBalanceInfo(libm, apt_mdn);

                            double tmp_d_value_650 = Double.valueOf(bean.getAPT5GVoice_650_Counter()) / 10000;
                            double d_value_650 = Math.round(tmp_d_value_650 * 100.0) / 100.0;
                            String s_value_650 = String.valueOf(d_value_650);
                            textbox_balance61.setValue(s_value_650);

                            log.info(apt_mdn + " 61:count1x()==>" + s_value_650);
                        }

                    } else {
                        log.info(apt_mdn + ":platformtype==>" + platformtype);
                        this.detach();
                        Executions.sendRedirect("/dtone/OnlineAddValueQuotaMsg.zhtml");
                    }
                } else {
                    log.info(apt_mdn + ":resultcode==>" + resultcode);
                    this.detach();
                    Executions.sendRedirect("/dtone/OnlineAddValueQuotaMsg.zhtml");
                }

            } else {
                this.detach();
                Executions.sendRedirect("/dtone/OnlineAddValueQuotaMsg.zhtml");
            }
        } catch (Exception ex) {
            log.info(ex);
        }

    }

    public void sendOKBtn() throws Exception {

        String MDN1 = "", MDN2 = "", CPLIBM = "", SALESID = "", STOREID = "", STORENAME = "";
        int APISRCID = 2;
        String tmprequestBody = "<ServiceAdjustAccountDeposit><MDN1>" + MDN1 + "</MDN1>"
                        + "<MDN2>" + MDN2 + "</MDN2>"
                        + "<CPLIBM>" + CPLIBM + "</CPLIBM>"
                        + "<SALESID>" + SALESID + "</SALESID>"
                        + "<STOREID>" + STOREID + "</STOREID>"
                        + "<APISRCID>" + APISRCID + "</APISRCID>"
                        + "<STORENAME>" + STORENAME + "</STORENAME></ServiceAdjustAccountDeposit>";

        Utilities util = new Utilities();
        String data = util.encrypt(key, tmprequestBody);

        postToNextPage(cpid, data);

//        String CountryCode_URL = new ShareParm().PARM_COUNTRYCODE_URL;
//        String sendURL = CountryCode_URL + "/OnlineCountryCodeLogin.zhtml?CPID=10001&DATA=Y2m3%2BopOfvLzfLeISoR5UumNQhs582xJhALf1EjjluYHCW9KPuE5AnAkPaPOZFQb3k20%2BXOSCHBkh8anTqfqGrD3xfiJrUOGL7u3SaFqQHUU8LdeRzlAHvakZQ8jMx67ojhdAvsULs3Krns%2BlSr0yKs1XIpXeVK0IZ8yzPnJriJDA0ENz8KbVQAONiE0VDyWQprcNpy0QTeATiNmjfYiKSdOaQo51yhhp3Fm%2BJBFFZNu0H%2F%2B808JfHHIHfC21mSxPB5gCn2FaGdOyK5VxdLCZIn9eweugOOITiWW%2BesxWF%2FivHIKHFEzHw%2BNUXmzZBtzLqRiBCd%2BSIzR8oqEQNdbeqG%2FX3QprE2c";
//        this.detach();
//        Executions.sendRedirect(sendURL);
    }

    private void postToNextPage(String systemid, String data) throws Exception {

        Sessions.getCurrent().setAttribute("cpid", systemid);
        Sessions.getCurrent().setAttribute("data", data);
        String CountryCode_URL = new ShareParm().PARM_COUNTRYCODE_URL;

        String sendURL = "";
        sendURL = CountryCode_URL + "/OnlineCountryCodeLogin.zhtml?CPID=" + systemid; //台灣
        log.info("systemid,data,sendURL===>" + systemid + "," + data + "," + sendURL);

        Sessions.getCurrent().setAttribute("sendURL", sendURL);
        Executions.getCurrent().setAttribute(org.zkoss.zk.ui.sys.Attributes.NO_CACHE, Boolean.TRUE);

        Window window = (Window) Executions.createComponents("/dtone/OnlineBackSend.zul", null, null);
    }
}
