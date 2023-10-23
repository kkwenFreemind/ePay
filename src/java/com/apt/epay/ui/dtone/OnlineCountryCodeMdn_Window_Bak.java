/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.dtone;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.deposit.util.toolUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.epay.ui.dtone.util.DTONEUtil;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_CP_INFO;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class OnlineCountryCodeMdn_Window_Bak extends Window {

    private final Logger log = Logger.getLogger("EPAY");

    private String cpid, apt_mdn, apisrcid;
    private String key;

    public void onCreate() throws Exception {

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        Sessions.getCurrent().removeAttribute("odBean");
        log.info("Creating Online Deposit...from IP " + Executions.getCurrent().getRemoteAddr());

        HttpServletRequest req = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
        log.info("doAfterCompose.getQueryString():" + req.getQueryString());

        toolUtil tool = new toolUtil();
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

                //改由DB取出對應資料
                EPAY_CP_INFO cpinfo = epaybusinesscontroller.getCpInfo(Integer.valueOf(cpid));
                // =======================
                if (cpinfo != null) {
                    key = cpinfo.getPoskey();

                    String decodeMsg = util.decrypt(key, data);
                    log.info("INPUT==>" + decodeMsg);

                    StringTokenizer st = new StringTokenizer(decodeMsg, "&");
                    while (st.hasMoreTokens()) {
                        String parms[] = st.nextElement().toString().split("=");
                        if ("mdn".equalsIgnoreCase(parms[0])) {
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
                    }

                } else {
                    //cpinfo is null
                    log.info("cannot get cpinfo & pos key");
                    this.detach();
                    Executions.sendRedirect("/dtone/OnlineNoSSOSSOErrorMsg.zhtml");
                }

            } catch (Exception e) {
                log.error("Cannot Find key or identifyCode!", e);
                Executions.sendRedirect("/dtone/OnlineNoSSOSSOErrorMsg.zhtml");
            }
        }

        if ("".equalsIgnoreCase(apt_mdn)) {
            this.detach();
            Executions.sendRedirect("/dtone/OnlineNoSSOSSOErrorMsg.zhtml");
        } else {
            Textbox textbox_apt_mdn = (Textbox) getSpaceOwner().getFellow("textbox_apt_mdn");
            textbox_apt_mdn.setValue(apt_mdn);
            textbox_apt_mdn.setReadonly(true);

            //testing
            //Textbox textbox_dtone_mdn = (Textbox) getSpaceOwner().getFellow("textbox_dtone_mdn");
            //textbox_dtone_mdn.setValue("639954726025");
            //textbox_dtone_mdn.setReadonly(true);
        }
    }

    public void sendBtn() throws Exception {

        String countrycode = "";
        Textbox textbox_dtone_mdn = (Textbox) getSpaceOwner().getFellow("textbox_dtone_mdn");
        Textbox textbox_apt_mdn = (Textbox) getSpaceOwner().getFellow("textbox_apt_mdn");
        Radiogroup radio_country_id = (Radiogroup) getSpaceOwner().getFellow("radio_country_id");

        String dtone_mdn = textbox_dtone_mdn.getValue();
        apt_mdn = textbox_apt_mdn.getValue();

        if (radio_country_id.getSelectedItem() != null) {
            countrycode = radio_country_id.getSelectedItem().getId();
        }
        log.info("Select Country====>" + countrycode);

        if (dtone_mdn.length() > 5) {

            DTONEUtil dtone = new DTONEUtil();
            String dt_operator_name = dtone.getMobileNumberInfo(dtone_mdn);

            log.info(dtone_mdn + " dt_operator_name====>" + dt_operator_name);
            if (!"-1".equalsIgnoreCase(dt_operator_name)) {

                StringBuffer originalParm = new StringBuffer();
                originalParm.append("apisrcid=").append(apisrcid).append("&")
                                .append("apt_mdn=").append(apt_mdn).append("&")
                                .append("dtone_mdn=").append(dtone_mdn).append("&")
                                .append("dt_operator_name=").append(dt_operator_name).append("&")
                                .append("countrycode=").append(countrycode);

                log.info("originalParm==>" + originalParm);
                Utilities util = new Utilities();
                String data = util.encrypt(key, originalParm.toString());
                log.info("data==>" + data);

                postToNextPage(cpid, data, countrycode);

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

    }

    private void postToNextPage(String systemid, String data, String countrycode) throws Exception {

        Sessions.getCurrent().setAttribute("cpid", systemid);
        Sessions.getCurrent().setAttribute("data", data);
        String CountryCode_URL = new ShareParm().PARM_COUNTRYCODE_URL;

        String sendURL = "";
        if ("ra1".equalsIgnoreCase(countrycode)) {
            sendURL = CountryCode_URL + "/OnlineCountryCodeService.zhtml?CPID=" + systemid; //台灣
        } else if ("ra2".equalsIgnoreCase(countrycode)) {
            sendURL = CountryCode_URL + "/OnlineCountryCodeService_Vietnam.zhtml?CPID=" + systemid;
        } else if ("ra3".equalsIgnoreCase(countrycode)) {
            sendURL = CountryCode_URL + "/OnlineCountryCodeService_Indonesia.zhtml?CPID=" + systemid;
        } else if ("ra4".equalsIgnoreCase(countrycode)) {
            sendURL = CountryCode_URL + "/OnlineCountryCodeService_Philippines.zhtml?CPID=" + systemid;
        } else if ("ra5".equalsIgnoreCase(countrycode)) {
            sendURL = CountryCode_URL + "/OnlineCountryCodeService_Thai.zhtml?CPID=" + systemid;
        } else {
            sendURL = CountryCode_URL + "/OnlineCountryCodeService.zhtml?CPID=" + systemid; //台灣
        }
        log.info("systemid,data,sendURL===>" + systemid + "," + data + "," + sendURL);
        Sessions.getCurrent().setAttribute("sendURL", sendURL);
        Executions.getCurrent().setAttribute(org.zkoss.zk.ui.sys.Attributes.NO_CACHE, Boolean.TRUE);

        Window window = (Window) Executions.createComponents("/dtone/OnlineDepositSend.zul", null, null);
    }
}
