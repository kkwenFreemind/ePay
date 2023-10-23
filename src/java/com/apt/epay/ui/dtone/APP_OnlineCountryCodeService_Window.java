/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.dtone;

import com.apt.epay.beans.BasicInfoReqBean;
import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.nokia.bean.NokiaResultBean;
import com.apt.epay.nokia.bean.NokiaSubscribeBalanceBean;
import com.apt.epay.nokia.main.NokiaMainBasicInfoUtil;
import com.apt.epay.nokia.main.NokiaMainLoginAndLifeCycleUtil;
import com.apt.epay.nokia.util.NokiaECGDeFeeUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.epay.ui.dtone.util.DTONEUtil;
import com.apt.epay.ui.dtone.util.DTOneResultBean;
import com.apt.epay.zte.util.ZTEAdjustAccUtil;
import com.apt.epay.zte.util.ZTEOCS4GBasicInfoUtil;
import com.apt.util.MailUtil;
import com.apt.util.SoaProfile;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_DTONESERVICE_INFO;
import com.epay.ejb.bean.EPAY_PROMOTIONCODE;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import static java.lang.Math.min;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
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
public class APP_OnlineCountryCodeService_Window extends Window {

    private final Logger log = Logger.getLogger("EPAY");
    private static final String ACCRESID = "61";
    private String[] radio_serviceid = new String[200];
    private String cpid;//= "10001";
    private String dtone_mdn;// = "639954726025";
    private String libm = "";
    private String promotioncode;
    private String contractid;
    private int platformtype;
    private String apisrcid;
    private String apt_mdn, dt_operator_name;
    private String key;
    private boolean rflag = false;
    private boolean check_resent_btn = true;
    private int send_count = 0;

    public void onCreate() throws Exception {

        Textbox textbox_dtone_mdn = (Textbox) getSpaceOwner().getFellow("textbox_dtone_mdn");
        Label textbox_balance61 = (Label) getSpaceOwner().getFellow("balance");
        final Label account = (Label) getSpaceOwner().getFellow("account");

        //交電費
        final Textbox electricitybill = (Textbox) getSpaceOwner().getFellow("electricitybill");
        final Label label_electricitybill_1 = (Label) getSpaceOwner().getFellow("label_electricitybill_1");
        final Label label_electricitybill_2 = (Label) getSpaceOwner().getFellow("label_electricitybill_2");

//        electricitybill.setValue("");
//        electricitybill.setReadonly(true);
        label_electricitybill_1.setVisible(false);
        label_electricitybill_2.setVisible(false);
        electricitybill.setVisible(false);
        //交電費結束

        log.info("Executions.getCurrent().getRemoteAddr()==>" + Executions.getCurrent().getRemoteAddr());

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

                    if (decodeMsg.length() != 0) {

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
                                if ("".equals(apisrcid) || "null".equalsIgnoreCase(apisrcid)) {
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
                    } else {
                        log.info("INPUT is NULL");
                        log.error("Parameter Data is Error!");
                        this.detach();
                        Executions.sendRedirect("/dtone/OnlineNoSSOSSOErrorMsg.zhtml");
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

        textbox_dtone_mdn.setValue("+" + dtone_mdn);
        textbox_dtone_mdn.setReadonly(true);

        String PARM_DTONE_QUOTA = new ShareParm().PARM_DTONE_QUOTA;
        log.info("PARM_DTONE_QUOTA:" + PARM_DTONE_QUOTA);

        try {
            int icpid = Integer.valueOf(cpid);

            if (apt_mdn.length() == 10 && dtone_mdn.length() > 5 && !"".equalsIgnoreCase(dt_operator_name)) {

                //Radio code
                Radiogroup radio_service_type = (Radiogroup) this.getFellow("radio_service_type");
                Vbox radio_servicevbox = (Vbox) this.getFellow("radio_servicevbox");

                SoaProfile soa = new SoaProfile();
                String result = soa.putSoaProxyletByMDN(apt_mdn);
                SOAReqBean apirequestbean;
                apirequestbean = soa.parseXMLString(result);

                log.info("kkflag==>" + result);

                String resultcode = apirequestbean.getResultcode();
                contractid = apirequestbean.getContractid();
//                contractstatuscode = apirequestbean.getContract_status_code();
//                producttype = apirequestbean.getProducttype();
                promotioncode = apirequestbean.getPromotioncode();

                if ("00000000".equals(resultcode)) {
                    EPAY_PROMOTIONCODE epaypromotioncode;// = new EPAY_PROMOTIONCODE();
                    epaypromotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);
                    platformtype = epaypromotioncode.getPlatformtype();

                    int dtone_trans_sum = getDTOneMdnQuota(apt_mdn);
                    log.info("(PARM_DTONE_QUOTA,MDN Trans Amount):" + "(" + PARM_DTONE_QUOTA + "," + dtone_trans_sum + ")");
                    Double dtone_mdn_quota = Double.valueOf(PARM_DTONE_QUOTA) - dtone_trans_sum;
                    log.info("DTONE_MDN_QUOTA:" + dtone_mdn_quota);

                    BasicInfoReqBean basicinforeqbean;// = new BasicInfoReqBean();
                    BasicInfoReqBean basicinforeqbeanx;// = new BasicInfoReqBean();
//                    int ddday = 0;
//                    double sumx = 0;

                    SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                    Calendar nowDateTime = Calendar.getInstance();
                    String libm = sdf15.format(nowDateTime.getTime());
                    boolean count61x_flag = false;
                    double limite_quota = 0.0;
                    double count61x = 0.0;

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

                            count61x = Double.valueOf(basicinforeqbeanx.getCounterValue1());
//                            double count62x = Double.valueOf(basicinforeqbeanx.getCounterValue2());
//                            sumx = (double) count61x + (double) count62x;

                            textbox_balance61.setValue(String.valueOf(count61x));
//                            textbox_balance61.setReadonly(true);

                            limite_quota = min(dtone_mdn_quota, count61x);
                            count61x_flag = true;//額度不足
                            if (count61x > dtone_mdn_quota) {
                                count61x_flag = false; //61帳本不足
                            }

                            log.info(apt_mdn + " 61:count1x()==>" + count61x);
                            log.info(apt_mdn + " (PARM_DTONE_QUOTA,MDN Trans Amount):" + "(" + PARM_DTONE_QUOTA + "," + dtone_trans_sum + ")" + "===>" + dtone_mdn_quota);
                            log.info(apt_mdn + " limite_quota)==>" + limite_quota);

                        }
                    } else if (platformtype == 3) {

                        log.info(apt_mdn + "Nokai MDN");
                        NokiaMainLoginAndLifeCycleUtil nutil = new NokiaMainLoginAndLifeCycleUtil();
                        String pid = nutil.login();
                        String LC = nutil.getMdnLifeCycle(libm, apt_mdn, pid);
                        boolean logoutflag = nutil.logout(pid);

                        log.info(apt_mdn + " Nokia Pid:" + pid + " login");
                        log.info(apt_mdn + " Nokia LifeCycle ===>" + LC);
                        log.info(apt_mdn + " Nokia " + pid + " logout=>" + logoutflag);

                        if (("ACTIVE".equalsIgnoreCase(LC))) {

                            //門號帳本資訊
                            NokiaSubscribeBalanceBean bean = new NokiaSubscribeBalanceBean();
                            NokiaMainBasicInfoUtil mutil = new NokiaMainBasicInfoUtil();

                            bean = mutil.GetMDNBalanceInfo(libm, apt_mdn);

                            double tmp_d_value_650 = Double.valueOf(bean.getAPT5GVoice_650_Counter()) / 10000;
                            double d_value_650 = Math.round(tmp_d_value_650 * 100.0) / 100.0;
                            String s_value_650 = String.valueOf(d_value_650);
                            textbox_balance61.setValue(s_value_650);

                            limite_quota = min(dtone_mdn_quota, d_value_650);
                            count61x_flag = true;//額度不足
                            if (d_value_650 > dtone_mdn_quota) {
                                count61x_flag = false; //61帳本不足
                            }
                            log.info(apt_mdn + " 650:count1x()==>" + d_value_650);
                            log.info(apt_mdn + " (PARM_DTONE_QUOTA,MDN Trans Amount):" + "(" + PARM_DTONE_QUOTA + "," + dtone_trans_sum + ")" + "===>" + dtone_mdn_quota);
                            log.info(apt_mdn + " limite_quota)==>" + limite_quota);
                        }
                    }

                    if (limite_quota > 0) {

                        List dtone_serviceinfo = null;
                        dtone_serviceinfo = epaybusinesscontroller.listDTOneServiceInfo(icpid, promotioncode, 2, dt_operator_name);
                        Iterator itserviceinfo1 = dtone_serviceinfo.iterator();
//                                int listcount = 0;
                        log.info("dtone_serviceinfo.size()==>" + dtone_serviceinfo.size());
                        int j = 0, x = 0, y = 0;
                        String pricevalue = "0";
                        int initvalue = 0;

                        while (itserviceinfo1.hasNext()) {
                            final EPAY_DTONESERVICE_INFO serid = (EPAY_DTONESERVICE_INFO) itserviceinfo1.next();
                            int price = serid.getPrice();
                            log.info(serid.getServiceName() + " price ==>" + price);

                            if (limite_quota > price) {

                                y = j;

                                Radio serviceid_radio = new Radio();
                                serviceid_radio.setId(String.valueOf(serid.getServiceId()));
                                serviceid_radio.setStyle("font-family: '微軟正黑體';font-size: 42px; color:darkgreen;");
                                serviceid_radio.setLabel(serid.getNote() + " = " + serid.getPrice());
                                serviceid_radio.setParent(radio_servicevbox);
                                log.info("Radio--->" + serid.getServiceName());

                                serviceid_radio.addEventListener("onClick", new EventListener() {
                                    @Override
                                    public void onEvent(Event arg0) throws Exception {
                                        log.info("serid.getPrice()===>" + serid.getPrice());
                                        account.setValue(String.valueOf(serid.getPrice()));
                                        String kk = serid.getNote().substring(0, 3);
//                                                if ("P50000".equalsIgnoreCase(serid.getNote())) {
                                        boolean chkP5000flag = chkP50000(serid.getNote());

                                        if (chkP5000flag) {
                                            log.info("DTONE Service IS " + serid.getNote());
                                            label_electricitybill_1.setVisible(true);
                                            label_electricitybill_2.setVisible(true);
                                            electricitybill.setVisible(true);
                                            electricitybill.setReadonly(false);
//                                                    electricitybill.setConstraint("no empty");
                                        } else {
                                            electricitybill.setValue("");
                                            label_electricitybill_1.setVisible(false);
                                            label_electricitybill_2.setVisible(false);
                                            electricitybill.setVisible(false);
                                            //electricitybill.setReadonly(true);
//                                                    electricitybill.setConstraint("empty");
                                        }
                                    }
                                });

                            } else {

                                x++;
                                Hbox hbox = new Hbox();
                                hbox.setParent(radio_servicevbox);

                                Radio serviceid_radio = new Radio();
                                serviceid_radio.setId(String.valueOf(serid.getServiceId()));
                                serviceid_radio.setLabel(serid.getNote() + " = " + serid.getPrice());
                                serviceid_radio.setStyle("font-family: '微軟正黑體';font-size: 42px; color: gray;");
                                serviceid_radio.setParent(hbox);
                                serviceid_radio.setDisabled(true);

                                log.info("Remove Radio--->" + serid.getServiceName());
                            }
                            radio_serviceid[j] = String.valueOf(serid.getServiceId());
                            j++;
                        }

                        log.info("The RadioCount is " + j);

                        // j : 所有的比數
                        // x : 不能選取的比數
                        log.info("j==>" + j);
                        log.info("x==>" + x);

                        if (j - x == 0) {
                            rflag = false; //沒有可以選擇的方案
                        } else {
                            rflag = true; //有可以選擇的方案
                        }

                        if (rflag) {
                            log.info("radio_service_type.setSelectedIndex(x)==> " + y);

                            radio_service_type.setSelectedIndex(y);
                            String xserviceid = radio_serviceid[y];
                            log.info("The init Radio serviceid ====>" + xserviceid);
                            EPAY_DTONESERVICE_INFO serviceinfo = epaybusinesscontroller.getDtoneServiceInfoById(Long.valueOf(xserviceid), Integer.valueOf(cpid));
                            int amount = serviceinfo.getPrice();
                            log.info("amount==>" + amount);
                            account.setValue(String.valueOf(amount));
                            //if ("P50000".equalsIgnoreCase(serid.getNote())) {
                            boolean chkP5000flag = chkP50000(serviceinfo.getNote());
                            if (chkP5000flag) {
                                log.info("DTONE Service IS " + serviceinfo.getNote());
                                label_electricitybill_1.setVisible(true);
                                label_electricitybill_2.setVisible(true);
                                electricitybill.setVisible(true);
                                electricitybill.setReadonly(false);

//                                        electricitybill.setConstraint("no empty");
                            } else {
                                electricitybill.setValue("");
                                label_electricitybill_1.setVisible(false);
                                label_electricitybill_2.setVisible(false);
                                electricitybill.setVisible(false);
//                                        electricitybill.setContext("empty");
                            }
                        } else {
                            log.info(apt_mdn + ":limite_quota==>" + limite_quota + " NO DTOne Service Can Choices");
                            if (count61x_flag) {
                                this.detach();
                                Executions.sendRedirect("/dtone/OnlineAddValue61CountMsg.zhtml");//61帳本不足
                            } else {
                                this.detach();
                                Executions.sendRedirect("/dtone/OnlineAddValueQuotaMsg.zhtml");//額度不足
                            }
                        }

                    } else {
                        log.info(apt_mdn + ":61 count1x==>" + count61x);
                        this.detach();
                        Executions.sendRedirect("/dtone/OnlineAddValueQuotaMsg.zhtml");
                    }

                } else {
//                    log.info(apt_mdn + ":platformtype==>" + platformtype);
//                    this.detach();
//                    Executions.sendRedirect("/dtone/OnlineAddValueQuotaMsg.zhtml");
                    log.info(apt_mdn + ":resultcode==>" + resultcode);
                    this.detach();
                    Executions.sendRedirect("/dtone/OnlineAddValueQuotaMsg.zhtml");
                }
            } else {
                this.detach();
                Executions.sendRedirect("/dtone/OnlineAddValueQuotaMsg.zhtml");
            }

//        }else {
//                this.detach();
//                Executions.sendRedirect("/dtone/OnlineAddValueQuotaMsg.zhtml");
//            }
        } catch (Exception ex) {
            log.info(ex);
        }
    }

    public void sendBackBtn() throws Exception {
//        String CountryCode_URL = new ShareParm().PARM_COUNTRYCODE_URL;
//        String sendURL = CountryCode_URL + "/OnlineCountryCodeLogin.zhtml?CPID=10001&DATA=Y2m3%2BopOfvLzfLeISoR5UumNQhs582xJhALf1EjjluYHCW9KPuE5AnAkPaPOZFQb3k20%2BXOSCHBkh8anTqfqGrD3xfiJrUOGL7u3SaFqQHUU8LdeRzlAHvakZQ8jMx67ojhdAvsULs3Krns%2BlSr0yKs1XIpXeVK0IZ8yzPnJriJDA0ENz8KbVQAONiE0VDyWQprcNpy0QTeATiNmjfYiKSdOaQo51yhhp3Fm%2BJBFFZNu0H%2F%2B808JfHHIHfC21mSxPB5gCn2FaGdOyK5VxdLCZIn9eweugOOITiWW%2BesxWF%2FivHIKHFEzHw%2BNUXmzZBtzLqRiBCd%2BSIzR8oqEQNdbeqG%2FX3QprE2c";
//        this.detach();
//        Executions.sendRedirect(sendURL);

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
        postToLoginPage(cpid, data);
    }

    private void postToLoginPage(String systemid, String data) throws Exception {

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

    public void sendBtn() {

        log.info("Run Country Code sendBtn 1==>" + check_resent_btn + "," + send_count + "," + libm);

        Button sendBtn = (Button) this.getFellow("sendBtn");
        sendBtn.setDisabled(true);

        if (check_resent_btn && send_count == 0 && libm == "") {

            Textbox electricitybill = (Textbox) getSpaceOwner().getFellow("electricitybill");
            String elect_bill = electricitybill.getValue();

            try {
                EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

                Radiogroup radio_service_type = (Radiogroup) this.getFellow("radio_service_type");
                int r_index = radio_service_type.getSelectedIndex();
                log.info("radio_service_type.getSelectedIndex()===>" + radio_service_type.getSelectedIndex());

                String xserviceid = radio_serviceid[r_index];
                log.info("The Radio serviceid ====>" + xserviceid);

                EPAY_DTONESERVICE_INFO serviceinfo = epaybusinesscontroller.getDtoneServiceInfoById(Long.valueOf(xserviceid), Integer.valueOf(cpid));

                String itemName = serviceinfo.getServiceName();
                String itemUnitPrice = serviceinfo.getPrice().toString();
                String itemCode = serviceinfo.getGlcode();
                String note = serviceinfo.getNote();
                int amount = serviceinfo.getPrice();
                int price_id = serviceinfo.getPrice_id();
                log.info("ServiceInfo===>" + itemName + "," + itemUnitPrice);
                log.info("price_id==>" + price_id);

                String contactCellPhone = apt_mdn;//聯絡手機號碼
                int tradeAmount = Integer.valueOf(itemUnitPrice);
                int tradeQuantity = 1;

                SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
                SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);
                SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");

                String orderTotal = String.valueOf(tradeAmount * tradeQuantity);
                Calendar nowDateTime = Calendar.getInstance();
                String tradeDate = sdf.format(nowDateTime.getTime());
//            String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());

                // 產生訂單編號 yymmddHHmissSSS
                libm = sdf15.format(nowDateTime.getTime());
                log.info("Run Country Code sendBtn 2==>" + check_resent_btn + "," + send_count + "," + libm);

                //記錄和比對是否已有訂單            
                EPAY_TRANSACTION trans = epaybusinesscontroller.getTransaction(libm);

                log.info(apt_mdn + " promotioncode:" + promotioncode);
                log.info(apt_mdn + " platformtype:" + platformtype);

                if (trans == null && !"".equalsIgnoreCase(libm) && !"null".equalsIgnoreCase(libm)) {
                    trans = new EPAY_TRANSACTION();
                    trans.setLibm(libm);
                    trans.setItemcode(itemCode);
                    trans.setItemproductname(itemName);
                    trans.setItemunitprice(Integer.parseInt(itemUnitPrice));//0
                    trans.setItemquantity(tradeQuantity);//1
                    trans.setOrdertotal(Integer.parseInt(orderTotal));//0
                    trans.setFee(0);
                    trans.setDiscount(0);
                    trans.setTradedate(sdf.parse(tradeDate));
                    trans.setPaymethod(ShareParm.PAYMETHOD_ADJUSTACC); //付款方式 信用卡:value = 1 
                    trans.setStatus("N"); //OCS尚未儲值完成
//                trans.setPaystatus(0); //無須繳費
                    trans.setPayamount(Integer.parseInt(orderTotal));
                    trans.setPrivatedata(libm); //PinCode number

                    trans.setServiceId(xserviceid);//
                    trans.setCpLibm(libm);
                    trans.setCpId(Integer.parseInt(new ShareParm().PARM_EPAY_CPID));//

//                String cpid = new ShareParm().PARM_EPAY_CPID;
                    EPAY_CP_INFO cpinfo = epaybusinesscontroller.getCpInfo(Integer.valueOf(cpid));
                    String cpname = cpinfo.getCpName();
                    trans.setCpName(cpname);

                    trans.setFeeType("0"); //無拆帳需求
                    trans.setInvoiceContactMobilePhone(contactCellPhone);
                    trans.setContractID(contractid);
                    trans.setPlatformtype(platformtype);

                    //20170713
                    trans.setApisrcid(apisrcid); //2 gtpay
                    trans.setPaytool("14"); // 家鄉儲

                    //countrycode
                    trans.setOperatiorname(dt_operator_name);
                    trans.setPriceid(String.valueOf(price_id));
                    trans.setCountrycodenote(note);
                    trans.setCountrycodemdn("+" + dtone_mdn);
                    trans.setDtone_electricitybill(elect_bill);

                    log.info("AdjustAccProce(insert EPAY_TRANSACTION Table)==>MDN:" + apt_mdn + ",Libm:" + libm);
                    epaybusinesscontroller.insertTransaction(trans);

                    int result = 0;
                    int kkamount = 0;
                    int new_kkamount = 0;

                    //Step 1 扣除61帳本
                    if (platformtype == 1) {
                        //DO NOTHING
                    } else if (platformtype == 2) {

                        //ocs_systemid, ocs_system_pwd, amount, mdn886, glcode, balid
                        ZTEAdjustAccUtil zteutil = new ZTEAdjustAccUtil();
                        kkamount = amount * (-100);
                        new_kkamount = amount * (100);
                        result = zteutil.putZTEOCS4GModifyBal(apt_mdn, kkamount, ACCRESID);

                    } else if (platformtype == 3) {
                        //NOKIA
                        NokiaECGDeFeeUtil nokiaUtil = new NokiaECGDeFeeUtil();
                        String Adjustment = "DECR"; //INCR DECR
                        log.info(apt_mdn + " Nokia Mdn");
                        log.info(apt_mdn + " start DECR 650 , amount ===>" + amount);
                        NokiaResultBean bean = nokiaUtil.putNokiaDeFeeOCSlet(libm, apt_mdn, tradeDate, "650", Adjustment, amount);

                        log.info(apt_mdn + " Nokia tXmdrecord Resp====>" + bean.getXmdrecord());
                        log.info(apt_mdn + " Nokia Httpstatus Resp====>" + bean.getHttpstatus());
                        result = bean.getHttpstatus();

                    } else {
                        log.info("The PlatformType is NOT define");
                        this.detach();
                        Executions.sendRedirect("/dtone/OnlineAddValueOCSErrorMsg.zhtml");
                    }

                    //Step 2 呼叫DTONE儲值，如果成功，則結束，如果失敗，則回來儲值61帳本
                    if (result == 200) {

                        trans.setStatus("10"); //表示扣款成功
                        epaybusinesscontroller.updateTransaction(trans);

                        //Step 2 呼叫DTONE儲值，如果成功，則結束，如果失敗，則回來儲值61帳本
                        DTONEUtil dtone = new DTONEUtil();
                        String dt_tid = "-1";

                        log.info("dt_operator_name==>" + dt_operator_name);
                        log.info("itemName==>" + itemName);
                        log.info("elect_bill==>" + elect_bill);

                        DTOneResultBean dtonebean = new DTOneResultBean();
//                        if ("PLN".equalsIgnoreCase(dt_operator_name.substring(0, 3)) && ("P50000".equalsIgnoreCase(itemName))) {
                        //if ("P50000".equalsIgnoreCase(itemName))) {
                        boolean chkP5000_flag = chkP50000(itemName);
                        if (chkP5000_flag) {
                            if ("".equalsIgnoreCase(elect_bill) || elect_bill == null) {
                                log.info("Error : elect_bill is NULL");
                            } else {
                                log.info(" dtone.setCountryCodeAddBillValue(" + apt_mdn + "," + dtone_mdn + "," + price_id + "," + elect_bill + ")");
                                dtonebean = dtone.setCountryCodeAddBillValue(apt_mdn, dtone_mdn, price_id, elect_bill);
                            }
                        } else {
                            log.info(" dtone.setCountryCodeAddValue(" + apt_mdn + "," + dtone_mdn + "," + price_id + ")");
                            dtonebean = dtone.setCountryCodeAddValue(apt_mdn, dtone_mdn, price_id);
                        }

                        if (dtonebean.getTid() != null || !"".equals(dtonebean.getTid())) {
                            dt_tid = dtonebean.getTid();
                            log.info("================================>" + dt_tid);
                        }

                        trans.setDtoneid(dt_tid);
                        epaybusinesscontroller.updateTransaction(trans);

                        if (!"-1".equals(dt_tid)) { //dtone tid 
                            String message = "";

                            try {

                                message = getCountryCodeTransResult(dt_tid);
                                
                                
//                                message="模擬失敗，回補650帳本";
                                
                                
                                log.info(apt_mdn + ":" + "家鄉儲結果==>" + message);
                                log.info(message);
                                if ("COMPLETED".equalsIgnoreCase(message)) {

                                    log.info(apt_mdn + ":" + "家鄉儲成功==>" + message);
                                    trans.setStatus("00");
                                    trans.setErrdesc("家鄉儲成功");

                                    log.info("dtonebean.getWholesale()==>" + dtonebean.getWholesale());
                                    trans.setDtone_code(dtonebean.getCode());
                                    trans.setDtone_date(dtonebean.getConfirmation_date());
                                    trans.setDtone_prices(dtonebean.getWholesale());
                                    epaybusinesscontroller.updateTransaction(trans);

                                    StringBuffer originalParm = new StringBuffer();
                                    originalParm.append("apisrcid=").append(apisrcid).append("&")
                                                    .append("apt_mdn=").append(apt_mdn).append("&")
                                                    .append("dtone_mdn=").append(dtone_mdn).append("&")
                                                    .append("dt_operator_name=").append(dt_operator_name).append("&")
                                                    .append("countrycode=").append("na");

                                    log.info(apt_mdn + " originalParm==>" + originalParm);
                                    Utilities util = new Utilities();
                                    String data = util.encrypt(key, originalParm.toString());
                                    log.info(apt_mdn + " data==>" + data);
                                    log.info(apt_mdn + " run postToNextPage");
                                    postToNextPage(cpid, data);

                                } else {
                                    log.info("dtone.getTransactionStatus==>" + message);
                                    //結束家鄉儲，update transaction status<扣款失敗>
                                    log.info(apt_mdn + ":" + "家鄉儲DTONE儲值失敗==>" + message);
//                                    String status = String.valueOf(result);
                                    trans.setStatus("-20");
                                    trans.setErrdesc(message + ":家鄉儲DTONE儲值失敗");

                                    epaybusinesscontroller.updateTransaction(trans);
                                    String error_desc = trans.getErrdesc();

                                    //61回補
                                    int kkresult = 0;
                                    if (platformtype == 2) {
                                        ZTEAdjustAccUtil zteutilx = new ZTEAdjustAccUtil();
                                        kkresult = zteutilx.putZTEOCS4GModifyBal(apt_mdn, new_kkamount, ACCRESID);

                                    } else if (platformtype == 3) {
                                        NokiaECGDeFeeUtil nokiaUtilxx = new NokiaECGDeFeeUtil();
                                        String Adjustment_inc = "INCR"; //INCR DECR
                                        log.info(apt_mdn + " Nokia Mdn");
                                        log.info(apt_mdn + " start INC 650 , amount ===>" + amount);
                                        NokiaResultBean inc_bean = nokiaUtilxx.putNokiaDeFeeOCSlet(libm, apt_mdn, tradeDate, "650", Adjustment_inc, amount);
                                        kkresult = inc_bean.getHttpstatus();
                                    }
                                    log.info(apt_mdn + " 61回補結果:" + kkresult);

                                    if (kkresult == 200) {

                                        error_desc = error_desc + ";已成功回補61帳本";
                                        log.info(apt_mdn + ":" + error_desc);
                                        trans.setStatus("-1");
                                        trans.setErrdesc(error_desc);
                                        epaybusinesscontroller.updateTransaction(trans);

                                    } else {
                                        error_desc = error_desc + ";回補61帳本失敗";
                                        log.info(apt_mdn + ":" + error_desc);
                                        trans.setStatus("-10");
                                        trans.setErrdesc(error_desc);
                                        epaybusinesscontroller.updateTransaction(trans);
                                    }

                                    if ("REJECTED".equalsIgnoreCase(message)) {
                                        this.detach();
                                        Executions.sendRedirect("/dtone/OnlineAddValueRejectedMsg.zhtml");
                                    } else if ("CANCELLED".equalsIgnoreCase(message)) {
                                        this.detach();
                                        Executions.sendRedirect("/dtone/OnlineAddValueCancelledMsg.zhtml");
                                    } else if ("DECLINED".equalsIgnoreCase(message)) {
                                        this.detach();
                                        Executions.sendRedirect("/dtone/OnlineAddValueDeclinedMsg.zhtml");
                                    } else {
                                        this.detach();
                                        Executions.sendRedirect("/dtone/OnlineAddValueDeclinedMsg.zhtml");
                                    }
                                }

                            } catch (InterruptedException e) {
                                log.info(e);
                            }

                        } else {

                            log.info(apt_mdn + ":家鄉儲DTONE儲值失敗;沒有取得交易代碼");
                            //結束家鄉儲，update transaction status<扣款失敗>
                            String status = String.valueOf(result);
                            trans.setStatus("-1");
                            trans.setErrdesc("家鄉儲DTONE儲值失敗;沒有取得交易代碼");
                            epaybusinesscontroller.updateTransaction(trans);

                            int kkresult = 0;
                            String error_desc = "";

                            if (platformtype == 1) {
                                //DO NOTHING
                            } else if (platformtype == 2) {
                                //61回補
                                error_desc = trans.getErrdesc();
                                ZTEAdjustAccUtil zteutilx = new ZTEAdjustAccUtil();
                                kkresult = zteutilx.putZTEOCS4GModifyBal(apt_mdn, new_kkamount, ACCRESID);
                            } else if (platformtype == 3) {
                                NokiaECGDeFeeUtil nokiaUtil_ll = new NokiaECGDeFeeUtil();
                                String Adjustment_inc = "INCR"; //INCR DECR
                                log.info(apt_mdn + " Nokia Mdn");
                                log.info(apt_mdn + " start INCR 650 , amount ===>" + amount);
                                NokiaResultBean inc_bean = nokiaUtil_ll.putNokiaDeFeeOCSlet(libm, apt_mdn, tradeDate, "650", Adjustment_inc, amount);
                                kkresult = inc_bean.getHttpstatus();
                            }
                            if (kkresult == 200) {

                                error_desc = error_desc + ";已成功回補61帳本";
                                log.info(apt_mdn + ":" + error_desc);
                                trans.setStatus("-1");
                                trans.setErrdesc(error_desc);
                                epaybusinesscontroller.updateTransaction(trans);

                            } else {

                                error_desc = error_desc + ";回補61帳本失敗";
                                log.info(apt_mdn + ":" + error_desc);
                                trans.setStatus("-10");
                                trans.setErrdesc(error_desc);
                                epaybusinesscontroller.updateTransaction(trans);

                                sendAlertEmail(libm + " EPAY-家鄉儲==>" + apt_mdn + ":" + error_desc);
                            }
                            this.detach();
                            Executions.sendRedirect("/dtone/OnlineAddValueOCSErrorMsg.zhtml");
                        }

                    } else {

                        log.info(apt_mdn + ":家鄉儲61帳本扣款失敗");
                        //log.info("putZTEOCS4GDeductFee result==>" + result);
                        //結束家鄉儲，update transaction status<扣款失敗>
                        String status = String.valueOf(result);
                        trans.setStatus("-1");
                        trans.setErrdesc("61帳本扣款失敗");
                        epaybusinesscontroller.updateTransaction(trans);

                        sendAlertEmail(libm + " EPAY-家鄉儲==>" + apt_mdn + ":" + "家鄉儲61帳本扣款失敗");

                        this.detach();
                        Executions.sendRedirect("/dtone/OnlineAddValueOCSErrorMsg.zhtml");

                    }
                }

            } catch (Exception ex) {
                log.info(ex);
            }
        } else {

            check_resent_btn = false;
            log.info("check_resent_btn==>" + check_resent_btn + "," + send_count + "," + libm);

        }
    }

    private void sendAlertEmail(String xmsg) {
        String email_form = new ShareParm().PARM_MAIL_FROM;
        String dst_user = new ShareParm().PARM_MAIL_TO_OC + ";" + new ShareParm().PARM_MAIL_TO_4GOCS;
        try {
            MailUtil.sendMail(new ShareParm().PARM_MAIL_RELAY_HOST, email_form, dst_user, xmsg, xmsg);
        } catch (Exception ex) {
            log.info(ex);
        }
    }

    private String getCountryCodeTransResult(String dt_tid) throws InterruptedException {
        String result = "";
        DTONEUtil dtone = new DTONEUtil();
        for (int i = 1; i <= 36; i++) {
            result = dtone.getTransactionStatus(dt_tid);
            log.info(dt_tid + " result(" + i + ")==>" + result);
            if (("COMPLETED".equalsIgnoreCase(result)) || ("REJECTED".equalsIgnoreCase(result)) || ("CANCELLED".equalsIgnoreCase(result)) || ("DECLINED").equalsIgnoreCase(result)) {
                return result;
            } else {
                Thread.sleep(5000);
            }
        }
        return result;
    }

    private int getDTOneMdnQuota(String mdn) {

        int sum = 0;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String begindate, enddate;
        Calendar cale = null;
//        cale = Calendar.getInstance();
        // 獲取前月的第一天
        cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, 0);
        cale.set(Calendar.DAY_OF_MONTH, 1);
        begindate = format.format(cale.getTime()) + " 00:00:00";
        // 獲取前月的最後一天
        cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, 1);
        cale.set(Calendar.DAY_OF_MONTH, 0);
        enddate = format.format(cale.getTime()) + " 23:59:59";

        try {
            EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

            List kklist = epaybusinesscontroller.getDTOneMdnTransAmount(mdn, begindate, enddate);
            Iterator ittrans = kklist.iterator();

            while (ittrans.hasNext()) {
                EPAY_TRANSACTION kktrans = (EPAY_TRANSACTION) ittrans.next();
                sum = kktrans.getPayamount() + sum;
            }

        } catch (Exception ex) {

        }
        return sum;
    }

    private void postToNextPage(String systemid, String data) throws Exception {

        log.info("postToNextPage systemid==>" + systemid);
        log.info("postToNextPage data =====>" + data);

        Sessions.getCurrent().setAttribute("cpid", systemid);
        Sessions.getCurrent().setAttribute("data", data);
        String CountryCode_URL = new ShareParm().PARM_COUNTRYCODE_URL;

        String sendURL = "";
        sendURL = CountryCode_URL + "/OnlineAddValueSuccessMsg.zhtml?CPID=" + systemid; //台灣
        log.info("systemid,data,sendURL===>" + systemid + "," + data + "," + sendURL);

        Sessions.getCurrent().setAttribute("sendURL", sendURL);
        Executions.getCurrent().setAttribute(org.zkoss.zk.ui.sys.Attributes.NO_CACHE, Boolean.TRUE);

        Window window = (Window) Executions.createComponents("/dtone/OnlineDepositSend.zul", null, null);
    }

    private boolean chkP50000(String serviceNote) {
        boolean result = false;
        String dtone_servicelist = new ShareParm().PARM_DTONE_SERVICENAME;

        String[] tokens = dtone_servicelist.split(",");

        for (String token : tokens) {
            if (serviceNote.equalsIgnoreCase(token)) {
                return true;
            }
        }
        return result;
    }
}
