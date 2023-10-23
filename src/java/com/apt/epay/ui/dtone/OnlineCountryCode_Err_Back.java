/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.dtone;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_CP_INFO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class OnlineCountryCode_Err_Back extends Window {

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
//        HttpServletRequest req = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
//        log.info("req.getQueryString()==>" + req.getQueryString());
//
//        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
//
//        Utilities util = new Utilities();
//
//        cpid = req.getParameter("cpid");
//        String data = req.getParameter("data");
//
//        log.info("cpid==>" + cpid);
//        log.info("data==>" + data);
//
//        if (cpid == null || "".equals(cpid.trim()) || data == null || "".equals(data.trim())) {
//            this.detach();
//            log.error("Parameter Data is null!");
//            Executions.sendRedirect("/dtone/OnlineNoSSOSSOErrorMsg.zhtml");
//        } else {
//            try {
//                EPAY_CP_INFO cpinfo = epaybusinesscontroller.getCpInfo(Integer.valueOf(cpid));
//                if (cpinfo != null) {
//                    key = cpinfo.getPoskey();
//                    String decodeMsg = util.decrypt(key, data);
//                    log.info("INPUT==>" + decodeMsg);
//                }
//            } catch (Exception ex) {
//                log.info(ex);
//            }
//        }

    }

    public void sendOKBtn() throws Exception {

//        String CountryCode_URL = new ShareParm().PARM_COUNTRYCODE_URL;
//        String sendURL = CountryCode_URL + "/OnlineCountryCodeLogin.zhtml?CPID=10001&DATA=Y2m3%2BopOfvLzfLeISoR5UumNQhs582xJhALf1EjjluYHCW9KPuE5AnAkPaPOZFQb3k20%2BXOSCHBkh8anTqfqGrD3xfiJrUOGL7u3SaFqQHUU8LdeRzlAHvakZQ8jMx67ojhdAvsULs3Krns%2BlSr0yKs1XIpXeVK0IZ8yzPnJriJDA0ENz8KbVQAONiE0VDyWQprcNpy0QTeATiNmjfYiKSdOaQo51yhhp3Fm%2BJBFFZNu0H%2F%2B808JfHHIHfC21mSxPB5gCn2FaGdOyK5VxdLCZIn9eweugOOITiWW%2BesxWF%2FivHIKHFEzHw%2BNUXmzZBtzLqRiBCd%2BSIzR8oqEQNdbeqG%2FX3QprE2c";
//        this.detach();
//        Executions.sendRedirect(sendURL);

        if ((cpid == null) || ("".equals(cpid))) {
            cpid = "10001";
        }

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        try {
            EPAY_CP_INFO cpinfo = epaybusinesscontroller.getCpInfo(Integer.valueOf(cpid));
            if (cpinfo != null) {
                key = cpinfo.getPoskey();
            }

            if ("".equals(key) || null == key) {
                String CountryCode_URL = new ShareParm().PARM_COUNTRYCODE_URL;
                String sendURL = CountryCode_URL + "/OnlineCountryCodeLogin.zhtml?CPID=10001&DATA=Y2m3%2BopOfvLzfLeISoR5UumNQhs582xJhALf1EjjluYHCW9KPuE5AnAkPaPOZFQb3k20%2BXOSCHBkh8anTqfqGrD3xfiJrUOGL7u3SaFqQHUU8LdeRzlAHvakZQ8jMx67ojhdAvsULs3Krns%2BlSr0yKs1XIpXeVK0IZ8yzPnJriJDA0ENz8KbVQAONiE0VDyWQprcNpy0QTeATiNmjfYiKSdOaQo51yhhp3Fm%2BJBFFZNu0H%2F%2B808JfHHIHfC21mSxPB5gCn2FaGdOyK5VxdLCZIn9eweugOOITiWW%2BesxWF%2FivHIKHFEzHw%2BNUXmzZBtzLqRiBCd%2BSIzR8oqEQNdbeqG%2FX3QprE2c";
                this.detach();
                Executions.sendRedirect(sendURL);
            } else {
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
            }
        } catch (Exception ex) {
            log.info(ex);
            String CountryCode_URL = new ShareParm().PARM_COUNTRYCODE_URL;
            String sendURL = CountryCode_URL + "/OnlineCountryCodeLogin.zhtml?CPID=10001&DATA=Y2m3%2BopOfvLzfLeISoR5UumNQhs582xJhALf1EjjluYHCW9KPuE5AnAkPaPOZFQb3k20%2BXOSCHBkh8anTqfqGrD3xfiJrUOGL7u3SaFqQHUU8LdeRzlAHvakZQ8jMx67ojhdAvsULs3Krns%2BlSr0yKs1XIpXeVK0IZ8yzPnJriJDA0ENz8KbVQAONiE0VDyWQprcNpy0QTeATiNmjfYiKSdOaQo51yhhp3Fm%2BJBFFZNu0H%2F%2B808JfHHIHfC21mSxPB5gCn2FaGdOyK5VxdLCZIn9eweugOOITiWW%2BesxWF%2FivHIKHFEzHw%2BNUXmzZBtzLqRiBCd%2BSIzR8oqEQNdbeqG%2FX3QprE2c";
            this.detach();
            Executions.sendRedirect(sendURL);
        }
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
