/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui;

import com.apt.epay.beans.BasicInfoReqBean;
import com.apt.epay.beans.BucketReqBean;
import com.apt.epay.beans.CMSBean;
import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.nokia.bean.NokiaPricePlanResultBean;
import com.apt.epay.nokia.bean.NokiaSubscribeAgentInfoBean;
import com.apt.epay.nokia.bean.NokiaSubscribeBalanceBean;
import com.apt.epay.nokia.main.NokiaMainBasicInfoUtil;
import com.apt.epay.nokia.main.NokiaMainLoginAndLifeCycleUtil;
import com.apt.epay.nokia.main.NokiaMainPricePlanCodeUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import static com.apt.epay.ui.CMS_OnlinePinCodeDeposit_Window.MD5_003;
import com.apt.epay.zte.util.ZTEAdjustAccUtil;
import com.apt.epay.zte.util.ZTEOCS4GBasicInfoUtil;
import com.apt.util.AdjustAccUtil;
import com.apt.util.AdjustBucket;
import com.apt.util.Base64Util;
import com.apt.util.BundleDateUtil;
import com.apt.util.MailUtil;
import com.apt.util.OCS4GBasicInfoUtil;
import com.apt.util.SendSMS;
import com.apt.util.SoaProfile;
import com.epay.ejb.bean.EPAY_BUCKET;
import com.epay.ejb.bean.EPAY_BUCKETHISTORY;
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_PROMOTIONCODE;
import com.epay.ejb.bean.EPAY_SERVICE_INFO;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Captcha;
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
//KK 餘額抵扣
public class CMS_OnlineAdjustAccountDeposit_Window extends Window {

//    private static final long serialVersionUID = -84547354457720L;
    private static final Logger log = Logger.getLogger("EPAY");

    private final String cpid = new ShareParm().PARM_EPAY_CPID;
    private String libm = "";
    private String contractid;
    private String mdn;
    private String contractstatuscode;
    private String promotioncode;
    private String producttype;
    private String name;

    private String[] radio_serviceid = new String[100];
    private final Integer XValue = 10000;
    private final String xcode = "餘額抵扣";

    private CMSBean cmsbean;
    private String storeid;
    private String salesid;
    private String invno;
    private String srcid;
    private String trnsdatetime;
    private String oc_checkpoint;
    private boolean rflag = false;
    private String NokiaAgentId = "";

    public void onCreate() throws Exception {

        Sessions.getCurrent().removeAttribute("cmsbean");
        cmsbean = new CMSBean();
        log.info("Creating Online Deposit...from IP " + Executions.getCurrent().getRemoteAddr());
        HttpServletRequest req = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
        log.debug("doAfterCompose.getQueryString():" + req.getQueryString());

        String PROXY_FLAG = new ShareParm().PARM_SCT_PROXY_FLAG;

        boolean proflag = true;

        if (req.getParameter("SS") == null) {
            log.error("Parameter encryptResponseMsg is null!");
            proflag = false;
        } else {
            String encryptMsg = (String) req.getParameter("SS");
            String PKEY = (String) req.getParameter("PKEY");
            log.info("PKey==>" + PKEY);
            log.info("encryptMsg==>" + encryptMsg);

//            String pkey1 = "1122334455";
            String pkey2 = new ShareParm().PARM_CMS_KEY;
            String newstr = MD5_003(pkey2);
            log.info("newstr===>" + newstr);

            if (PKEY.equals(newstr)) {
                //解碼
                Base64Util base64 = new Base64Util();
                String decodestr = new String(base64.base64Decode(encryptMsg));
                log.info("decodestr====>" + decodestr);
                StringTokenizer st = new StringTokenizer(decodestr, "&");
                try {
                    while (st.hasMoreTokens()) {
                        String parms[] = st.nextElement().toString().split("=");

                        if ("MDN".equalsIgnoreCase(parms[0])) {
                            mdn = parms.length == 1 ? "" : parms[1];
                            log.info("MDN===>" + mdn);
                            if ("".equals(mdn)) {
                                log.error("MDN為空值!");
                                Sessions.getCurrent().setAttribute("errorMsg", "MDN為空值!");
                                Executions.getCurrent().forward("BusinessRoleError.zul");
                                return;
                            } else {
                                cmsbean.setMdn(mdn);
                            }
                        }

                        if ("INVOICE_NO".equalsIgnoreCase(parms[0])) {
                            invno = parms.length == 1 ? "" : parms[1];
                            log.info("INVOICE_NO===>" + invno);
                            if ("".equals(invno)) {
                                log.error("INVOICE_NO為空值!");
                                Sessions.getCurrent().setAttribute("errorMsg", "INVOICE_NO為空值!");
                                Executions.getCurrent().forward("BusinessRoleError.zul");
                                return;
                            } else {
                                cmsbean.setInv_no(invno);
                            }
                        }
                        if ("SRC_ID".equalsIgnoreCase(parms[0])) {
                            srcid = parms.length == 1 ? "" : parms[1];
                            log.info("SRC_ID===>" + srcid);
                            if ("".equals(invno)) {
                                log.error("SRC_ID為空值!");
                                Sessions.getCurrent().setAttribute("errorMsg", "SRC_ID為空值!");
                                Executions.getCurrent().forward("BusinessRoleError.zul");
                                return;
                            } else {
                                cmsbean.setScr_id(srcid);
                            }
                        }

                        if ("STORE_ID".equalsIgnoreCase(parms[0])) {
                            storeid = parms.length == 1 ? "" : parms[1];
                            log.info("STORE_ID===>" + storeid);
                            if ("".equals(storeid)) {
                                log.error("StoreID為空值!");
                                Sessions.getCurrent().setAttribute("errorMsg", "StoreID為空值!");
                                Executions.getCurrent().forward("BusinessRoleError.zul");
                                return;
                            } else {
                                cmsbean.setStore_id(storeid);
                            }
                        }

                        if ("SALES_ID".equalsIgnoreCase(parms[0])) {

                            salesid = parms.length == 1 ? "" : parms[1];
                            log.info("SALE_ID==>" + salesid);
                            if ("".equals(salesid)) {
                                log.error("SALES_ID為空值!");
                                Sessions.getCurrent().setAttribute("errorMsg", "SalesID為空值!");
                                Executions.getCurrent().forward("BusinessRoleError.zul");
                                return;
                            } else {
                                cmsbean.setSaleid(salesid);
                            }
                        }

                        if ("TIMESTAMP".equalsIgnoreCase(parms[0])) {
                            trnsdatetime = parms.length == 1 ? "" : parms[1];
                            log.info("TIMESTAMP==>" + trnsdatetime);

                            if ("".equals(trnsdatetime)) {
                                log.error("TIMESTAMP!");
                                Sessions.getCurrent().setAttribute("errorMsg", "TIMESTAMP為空值!");
                                Executions.getCurrent().forward("BusinessRoleError.zul");
                                return;
                            } else {
                                cmsbean.setTransdatetime(trnsdatetime);
                            }
                        }
                        if ("OCCHECKPOING".equalsIgnoreCase(parms[0])) {
                            oc_checkpoint = parms.length == 1 ? "" : parms[1];
                            log.info("OCCHECKPOING==>" + oc_checkpoint);
                            if ("".equals(oc_checkpoint)) {
                                log.error("OCCHECKPOING!");
                                Sessions.getCurrent().setAttribute("errorMsg", "OCCHECKPOING為空值!");
                                Executions.getCurrent().forward("BusinessRoleError.zul");
                                return;
                            } else {
                                cmsbean.setTransdatetime(oc_checkpoint);
                            }
                        }

                    }
                } catch (Exception ex) {
                    proflag = false;
                    log.info(ex);
                }
            } else {
                proflag = false;
                this.detach();
                Executions.sendRedirect("/cms/OnlineSSOErrorMsg.zhtml");

            }
        }

        //debug
        if ("TDE".equalsIgnoreCase(PROXY_FLAG)) {
            String tde_mdn = new ShareParm().PARM_TDE_MDN;
            mdn = tde_mdn;
            log.info("This is ===================>" + PROXY_FLAG);
            log.info("tde_mdn  is ===================>" + mdn);
        }

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        Textbox textbox_mdn = (Textbox) this.getFellow("textbox_mdn");
        Textbox textbox_amount = (Textbox) this.getFellow("textbox_amount");
        Radiogroup radio_service_type = (Radiogroup) this.getFellow("radio_service_type");
        Vbox radio_servicevbox = (Vbox) this.getFellow("radio_servicevbox");

        try {
            if (mdn != null) {

                SoaProfile soa = new SoaProfile();
                String result = soa.putSoaProxyletByMDN(mdn);
                SOAReqBean apirequestbean = new SOAReqBean();
                apirequestbean = soa.parseXMLString(result);

                log.info("kkflag==>CMSAdjustAccountDeposit:" + result);

                contractid = apirequestbean.getContractid();
                contractstatuscode = apirequestbean.getContract_status_code();
                producttype = apirequestbean.getProducttype();
                promotioncode = apirequestbean.getPromotioncode();
                mdn = apirequestbean.getMdn();

                EPAY_PROMOTIONCODE epaypromotioncode = new EPAY_PROMOTIONCODE();
                epaypromotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);
                int platformtype = epaypromotioncode.getPlatformtype();

                BasicInfoReqBean basicinforeqbean = new BasicInfoReqBean();
                BasicInfoReqBean basicinforeqbeanx = new BasicInfoReqBean();
                NokiaSubscribeAgentInfoBean AgentBean = new NokiaSubscribeAgentInfoBean();

                int ddday = 0;
                int sumx = 0;

                if (platformtype == 1) { //ALU
                    //for 合約到期日比對天數
                    SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                    SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);
                    Calendar nowDateTime = Calendar.getInstance();
                    String libm = sdf15.format(nowDateTime.getTime());
                    String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());

                    OCS4GBasicInfoUtil basicinfoutil = new OCS4GBasicInfoUtil();
                    String basicinfo = basicinfoutil.putOCS4GBasicInfoSlet(libm, mdn, tradeDate_Pincode);
                    log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfo);

                    basicinforeqbean = new BasicInfoReqBean();
                    basicinforeqbean = basicinfoutil.parseBasicInfoXMLString(basicinfo);

                    String d1 = TransDateForamt(basicinforeqbean.getEndDate1());
                    log.info("d1===>" + d1);

                    BundleDateUtil bdutil = new BundleDateUtil();
                    ddday = bdutil.GetDiffDayOfContractDay(d1);
                    if (ddday > 0) {
                        log.info("DiffDate====>" + ddday);
                    } else {
                        //合約到期日小於今天
                        log.info("DiffDate====>" + ddday);
                    }

                    //End of Test
                    //Test For 金額比對
                    OCS4GBasicInfoUtil basicinfoutilx = new OCS4GBasicInfoUtil();
                    String basicinfox = basicinfoutilx.putOCS4GBasicInfoSlet(libm, mdn, tradeDate_Pincode);
                    log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfox);

                    basicinforeqbeanx = new BasicInfoReqBean();
                    basicinforeqbeanx = basicinfoutilx.parseBasicInfoXMLString(basicinfo);

                    double count1x = Double.valueOf(basicinforeqbeanx.getCounterValue1()) / 10000;
                    double count2x = Double.valueOf(basicinforeqbeanx.getCounterValue2()) / 10000;
                    double tmp_sumx = count1x + count2x;
                    sumx = (int) tmp_sumx;
                    textbox_amount.setValue(String.valueOf(sumx));

                    // End of Test for 金額比對        
                } else if (platformtype == 2) { // ZTE
                    //for 合約到期日比對天數
                    SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                    Calendar nowDateTime = Calendar.getInstance();
                    String libm = sdf15.format(nowDateTime.getTime());

                    ZTEOCS4GBasicInfoUtil ztebasicinfoutil = new ZTEOCS4GBasicInfoUtil();
                    String basicinfo = ztebasicinfoutil.putZTEOCS4GBasicInfoSlet(libm, mdn);
                    log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfo);

                    basicinforeqbean = ztebasicinfoutil.parseZTEBasicInfoXMLString(basicinfo);

                    String d1 = basicinforeqbean.getEndDate1();
                    log.info("d1===>" + d1);

                    BundleDateUtil bdutil = new BundleDateUtil();
                    ddday = bdutil.GetDiffDayOfContractDay(d1);
                    if (ddday > 0) {
                        log.info("DiffDate====>" + ddday);
                    } else {
                        //合約到期日小於今天
                        log.info("DiffDate====>" + ddday);
                    }

                    //Test For 金額比對
                    ZTEOCS4GBasicInfoUtil ztebasicinfoutilx = new ZTEOCS4GBasicInfoUtil();
                    String basicinfox = ztebasicinfoutilx.putZTEOCS4GBasicInfoSlet(libm, mdn);
                    log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfox);

                    basicinforeqbeanx = ztebasicinfoutilx.parseZTEBasicInfoXMLString(basicinfo);

                    double count1x = Double.valueOf(basicinforeqbeanx.getCounterValue1());
                    double count2x = Double.valueOf(basicinforeqbeanx.getCounterValue2());
                    sumx = (int) count1x + (int) count2x;
                    textbox_amount.setValue(String.valueOf(sumx));

                } else if (platformtype == 3) {
                    //KK NOKIA
                    //get LC
                    NokiaMainLoginAndLifeCycleUtil nutil = new NokiaMainLoginAndLifeCycleUtil();
                    String pid = nutil.login();
                    String LC = nutil.getMdnLifeCycle(libm, mdn, pid);
                    boolean logoutflag = nutil.logout(pid);
                    basicinforeqbean.setLifeCycleState(LC);

                    NokiaMainBasicInfoUtil mutil = new NokiaMainBasicInfoUtil();
                    AgentBean = mutil.GetMDNAgentInfo(libm, mdn);
                    NokiaAgentId = AgentBean.getAgentID();

                    log.info(mdn + " Nokia Pid:" + pid + " login");
                    log.info(mdn + " Nokia LifeCycle ===>" + LC);
                    log.info(mdn + " Nokia " + pid + " logout=>" + logoutflag);
                    log.info(mdn + " Agent ID ====>" + NokiaAgentId);

                    //for 合約到期日比對天數
                    SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                    Calendar nowDateTime = Calendar.getInstance();
                    String libm = sdf15.format(nowDateTime.getTime());

                    NokiaMainBasicInfoUtil nokiaUtil = new NokiaMainBasicInfoUtil();
                    NokiaSubscribeBalanceBean bean = nokiaUtil.GetMDNBalanceInfo(libm, mdn);

                    String d1 = bean.getAPT_LC_EndDate(); //門號到期日
                    String d2 = NokiaDateFormat(d1);
                    log.info("d1===>" + d2);

                    BundleDateUtil bdutil = new BundleDateUtil();
                    ddday = bdutil.GetDiffDayOfContractDay(d2);
                    if (ddday > 0) {
                        log.info("DiffDate====>" + ddday);
                    } else {
                        //合約到期日小於今天
                        log.info("DiffDate====>" + ddday);
                    }

                    //Test For 金額比對
                    double tmp_d_value_650 = 0.0;
                    tmp_d_value_650 = Double.valueOf(bean.getAPT5GVoice_650_Counter()) / 10000;
                    double d_value_650 = Math.round(tmp_d_value_650 * 100.0) / 100.0;

                    double tmp_d_value_660 = 0.0;
                    tmp_d_value_660 = Double.valueOf(bean.getAPT5GVoice1_660_Counter()) / 10000;
                    double d_value_660 = Math.round(tmp_d_value_660 * 100.0) / 100.0;

                    double count1x = d_value_650;
                    double count2x = d_value_660;
                    sumx = (int) count1x + (int) count2x;
                    textbox_amount.setValue(String.valueOf(sumx));
                }

                name = apirequestbean.getName();
                textbox_mdn.setValue(mdn);

                if (name.length() > 10) {
                    name = name.substring(0, 9);
                }

                if (("ACTIVE".equalsIgnoreCase(basicinforeqbean.getLifeCycleState()))
                                || ("CED1".equalsIgnoreCase(basicinforeqbean.getLifeCycleState()))
                                || ("CED10".equalsIgnoreCase(basicinforeqbean.getLifeCycleState()))
                                || ("CED3".equalsIgnoreCase(basicinforeqbean.getLifeCycleState()))) {

                    List serviceinfo1 = null;

                    if (platformtype == 3) {
                        //Nokia ServiceID
//                        NokiaAgentId = "100005";
                        log.info(mdn + "==>Nokia AgentID==>" + NokiaAgentId);
                        serviceinfo1 = epaybusinesscontroller.listAdjustAccNokiaServiceInfo(Integer.valueOf(cpid), promotioncode, xcode, platformtype);
                    } else {
                        serviceinfo1 = epaybusinesscontroller.listAdjustAccServiceInfo(Integer.valueOf(cpid), promotioncode, xcode, platformtype);
                    }

                    Iterator itserviceinfo1 = serviceinfo1.iterator();
                    int j = 0, x = 0, y = 0;
                    while (itserviceinfo1.hasNext()) {

                        EPAY_SERVICE_INFO serid = (EPAY_SERVICE_INFO) itserviceinfo1.next();
                        int sid_dday = serid.getDday(); // 延展天數
                        int s_price = serid.getPrice(); // 服務折抵金額
                        // sumx : 610+620 金額
                        // ddday : 合約到期日到今日的天數
                        String wording = getWording(sumx, ddday, s_price, sid_dday);

                        if (!"100005".equalsIgnoreCase(NokiaAgentId)) {
                            if ((serid.getPlatformtype() == 2) || (serid.getPlatformtype() == 3 && serid.getGtype() == 4)) {
                                if ((sid_dday <= ddday) && (s_price <= sumx)) {
                                    y = j;
                                    Radio serviceid_radio = new Radio();
                                    serviceid_radio.setId(String.valueOf(serid.getServiceId()));
                                    serviceid_radio.setLabel(serid.getServiceName());
                                    serviceid_radio.setStyle("\"font-family: '微軟正黑體';font-size: 20px;\"");
                                    serviceid_radio.setParent(radio_servicevbox);
                                    Label label = new Label();
                                    label.setValue("　" + serid.getNote());
                                    label.setParent(radio_servicevbox);

                                    log.info("Radio--->" + serid.getServiceName());

                                } else {
                                    x++;
                                    Hbox hbox = new Hbox();
                                    hbox.setParent(radio_servicevbox);

                                    Radio serviceid_radio = new Radio();
                                    serviceid_radio.setId(String.valueOf(serid.getServiceId()));
                                    serviceid_radio.setLabel(serid.getServiceName());
                                    serviceid_radio.setStyle("\"font-family: '微軟正黑體';font-size: 20px;\"");
                                    serviceid_radio.setParent(hbox);
                                    serviceid_radio.setDisabled(true);

                                    Label errLable = new Label();
                                    errLable.setValue(wording);

                                    errLable.setStyle("color:#FF0000;");
                                    errLable.setParent(hbox);

                                    Label label = new Label();
                                    label.setValue("　" + serid.getNote());
                                    label.setParent(radio_servicevbox);
                                    log.info("Remove Radio--->" + serid.getServiceName());

                                }

                                radio_serviceid[j] = String.valueOf(serid.getServiceId());

                                j++;
                            } else {
                                log.info(mdn + " PlatformType=" + serid.getPlatformtype() + ",GType=" + serid.getGtype() + " Filter 5G Nokia Item--->>" + serid.getServiceName());
                                //log.info(mdn + " Filter 5G Nokia Item--->" + serid.getServiceName() + ",serid.getPlatformtype()==>" + serid.getPlatformtype());
                            }
                        } else {
                            if ((sid_dday <= ddday) && (s_price <= sumx)) {
                                y = j;
                                Radio serviceid_radio = new Radio();
                                serviceid_radio.setId(String.valueOf(serid.getServiceId()));
                                serviceid_radio.setLabel(serid.getServiceName());
                                serviceid_radio.setStyle("\"font-family: '微軟正黑體';font-size: 20px;\"");
                                serviceid_radio.setParent(radio_servicevbox);
                                Label label = new Label();
                                label.setValue("　" + serid.getNote());
                                label.setParent(radio_servicevbox);

                                log.info("Radio--->" + serid.getServiceName());

                            } else {
                                x++;
                                Hbox hbox = new Hbox();
                                hbox.setParent(radio_servicevbox);

                                Radio serviceid_radio = new Radio();
                                serviceid_radio.setId(String.valueOf(serid.getServiceId()));
                                serviceid_radio.setLabel(serid.getServiceName());
                                serviceid_radio.setStyle("\"font-family: '微軟正黑體';font-size: 20px;\"");
                                serviceid_radio.setParent(hbox);
                                serviceid_radio.setDisabled(true);

                                Label errLable = new Label();
                                errLable.setValue(wording);

                                errLable.setStyle("color:#FF0000;");
                                errLable.setParent(hbox);

                                Label label = new Label();
                                label.setValue("　" + serid.getNote());
                                label.setParent(radio_servicevbox);
                                log.info("Remove Radio--->" + serid.getServiceName());

                            }

                            radio_serviceid[j] = String.valueOf(serid.getServiceId());

                            j++;
                        }
                    }

                    log.info("AdjustAccProce(SOA Result)==>IP:" + Executions.getCurrent().getRemoteAddr()
                                    + ",MDN:" + mdn
                                    + ",ContractID:" + contractid
                                    + ",Contract_status:" + contractstatuscode
                                    + ",producttype:" + producttype
                                    + ",promotioncode:" + promotioncode);

                    log.info("The RadioCount is " + j);

                    if (j - x == 0) {
                        rflag = false; //沒有可以選擇的方案
                    } else {
                        rflag = true; //有可以選擇的方案
                    }

                    if (rflag) {
                        log.info("radio_service_type.setSelectedIndex(x)==> " + y);
                        radio_service_type.setSelectedIndex(y);
                    } else {

                    }

                    String subPromotionCode = promotioncode.substring(0, 3);
                    if (checkUserStatus(contractstatuscode, subPromotionCode)) {
                        //DO NOTHING                        
                    } else {
                        log.info("AdjustAccProce==>" + mdn + " Contract Status Check : " + contractstatuscode);
                        this.detach();
                        Sessions.getCurrent().setAttribute("status", contractstatuscode);
                        Executions.sendRedirect("/cms/OnlineUserErrorMsg.zhtml");
                    }
                } else {
//                    log.info("AdjustAccProce===>" + mdn + " DefaultTariffPlanCOSPID() 9300");
//                    this.detach();
//                    Executions.sendRedirect("/cms/OnlineSSOErrorMsg.zhtml");
                    //您的有效期限已逾期,請盡速儲值或洽客服協助
                    log.info(mdn + " 您的有效期限已逾期,請盡速儲值或洽客服協助==>" + basicinforeqbean.getLifeCycleState());
                    this.detach();
                    Executions.sendRedirect("/cms/OnlineLifeCycleStateErrorMsg.zhtml");
                }

            } else {
                log.info("UUID IS NULL");
                this.detach();
                Window window = (Window) Executions.createComponents("/cms/OnlineSSOErrorMsg.zhtml", null, null);
            }
        } catch (Exception ex) {
            log.info(ex);

        }

    }

    public void sendAdjustAcc() throws Exception {
        if (rflag) {
            EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
            SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
            SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
            SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);

            Captcha chkcaptcha = (Captcha) getSpaceOwner().getFellow("cpa");
            Textbox chkcode = (Textbox) this.getFellow("chkcode");

            Radiogroup radio_service_type = (Radiogroup) this.getFellow("radio_service_type");
            int r_index = radio_service_type.getSelectedIndex();
            log.info("radio_service_type.getSelectedIndex()===>" + radio_service_type.getSelectedIndex());
            log.info("radio_serviceid[radio_service_type.getSelectedIndex()]==>" + radio_serviceid[r_index]);
            String xserviceid = radio_serviceid[r_index];

//        log.info("The Radio serviceid ====>" + radio_service_type.getId());
//        log.info("The Radio serviceid ====>" + radio_service_type.getSelectedIndex());
            log.info("The Radio serviceid ====>" + radio_serviceid[r_index]);

            EPAY_SERVICE_INFO serviceinfo = epaybusinesscontroller.getServiceInfoById(Long.valueOf(xserviceid), Integer.valueOf(cpid));
            String itemName = serviceinfo.getServiceName();
            String itemUnitPrice = serviceinfo.getPrice().toString();
            String itemCode = serviceinfo.getGlcode();
            String note = serviceinfo.getNote();
            String priceplancode = serviceinfo.getPriceplancode();
            int hr = serviceinfo.getDday() * 24;

            log.info("ServiceInfo===>" + itemName + "," + itemUnitPrice + "," + itemCode + "," + note);

            String contactCellPhone = mdn;//聯絡手機號碼
            int tradeAmount = Integer.valueOf(itemUnitPrice);
            int tradeQuantity = 1;

            String orderTotal = String.valueOf(tradeAmount * tradeQuantity);
            Calendar nowDateTime = Calendar.getInstance();
            String tradeDate = sdf.format(nowDateTime.getTime());
            String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());

            if (chkcode.getValue() != null && chkcode.getValue().equals(chkcaptcha.getValue())) {

                // 產生訂單編號 yymmddHHmissSSS
                libm = sdf15.format(nowDateTime.getTime());

                //記錄和比對是否已有訂單            
                EPAY_TRANSACTION trans = epaybusinesscontroller.getTransaction(libm);
                EPAY_PROMOTIONCODE epaypromotioncode = new EPAY_PROMOTIONCODE();
                epaypromotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);
                int platformtype = epaypromotioncode.getPlatformtype();

                if (trans == null && !"".equalsIgnoreCase(libm) && !"null".equalsIgnoreCase(libm)) {
                    //如果沒有編號,直接insert新的
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

                    String cpid = new ShareParm().PARM_EPAY_CPID;
                    EPAY_CP_INFO cpinfo = epaybusinesscontroller.getCpInfo(Integer.valueOf(cpid));
                    String cpname = cpinfo.getCpName();
                    trans.setCpName(cpname);

                    trans.setFeeType("0"); //無拆帳需求
                    trans.setInvoiceContactMobilePhone(contactCellPhone);
                    trans.setContractID(contractid);
                    trans.setPlatformtype(platformtype);

                    //20170713
                    trans.setApisrcid("3");
                    trans.setPaytool("3");

                    log.info("AdjustAccProce(insert EPAY_TRANSACTION Table)==>MDN:" + mdn + ",Libm:" + libm);

                    epaybusinesscontroller.insertTransaction(trans);

                } else {
                    //如果有編號,檢查是否已經交易, 如果還未交易可在更新資料。   
                }

                boolean flag = true;
                if (platformtype == 1) {//ALU


                    /*
                     查詢4G OCS的帳本餘額並加總                
                     */
                    OCS4GBasicInfoUtil basicinfoutil = new OCS4GBasicInfoUtil();
                    String basicinfo = basicinfoutil.putOCS4GBasicInfoSlet(libm, mdn, tradeDate_Pincode);
                    log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfo);

                    BasicInfoReqBean basicinforeqbean = new BasicInfoReqBean();
                    basicinforeqbean = basicinfoutil.parseBasicInfoXMLString(basicinfo);

                    double count1 = Double.valueOf(basicinforeqbean.getCounterValue1()) / 10000;
                    double count2 = Double.valueOf(basicinforeqbean.getCounterValue2()) / 10000;
                    double sum = count1 + count2;

                    log.info("AdjustAccPoc==>Libm:" + libm + ",AdjustAmt:" + serviceinfo.getPrice() + ",ACC610:" + count1 + ",ACC620:" + count2 + ",Sum:" + sum);
                    List bucketlist = epaybusinesscontroller.getBucketListBySid(xserviceid);
                    if (bucketlist != null && !bucketlist.isEmpty()) {
                        Iterator it = bucketlist.iterator();
                        while (it.hasNext()) {
                            EPAY_BUCKET bucket = new EPAY_BUCKET();
                            bucket = (EPAY_BUCKET) it.next();
                            String bucketid = bucket.getBucketId();
                            String amount = String.valueOf(bucket.getAmount());
                            log.info("AdjustAccPoc==>(tid,serviceid,bucketid,amount,note)=(" + libm + "," + bucket.getServiceId() + "," + bucketid + "," + amount + "," + bucket.getNote() + ")");
                        }
                    }

                    //比較抵扣金額與帳本餘額
                    if (serviceinfo.getPrice() > sum) {
                        //餘額不足，無法抵扣
                        log.info("AdjustAccPOC===>餘額不足，無法抵扣(" + serviceinfo.getPrice() + "," + sum + "," + count1 + "," + count2 + ")");

//                    SendSMS xsms = new SendSMS();
//                    String testmdn = "0982158008,0906402025";
//                    String xmsg = "Epay：用戶" + mdn + "，餘額抵扣訂單LIB" + libm + "餘額不足，無法抵扣";
//                    xsms.sendsms(testmdn, xmsg);
                        this.detach();
                        Executions.sendRedirect("/cms/OnlineAdjustAccounErrorMsg.zhtml");
//                    org.zkoss.zul.Messagebox.show("餘額不足，無法抵扣", "亞太電信", org.zkoss.zul.Messagebox.OK, org.zkoss.zul.Messagebox.INFORMATION);
                    } else {
                        //餘額足夠扣抵
                        log.info("AdjustAccPOC===>DO Ajust Amt(" + serviceinfo.getPrice() + "," + sum + "," + count1 + "," + count2 + ")");

                        String sid = trans.getServiceId();
                        String mdn = trans.getInvoiceContactMobilePhone();
                        String tradedate = sdf_pincode.format(trans.getTradedate());
                        String libmx = trans.getLibm();
                        String ref = trans.getLibm();
                        log.info("record==>" + trans.getServiceId());

                        AdjustBucket adjustbucket = new AdjustBucket();
                        String serviceinfo_getPrice = String.valueOf((int) Math.floor(serviceinfo.getPrice() * XValue));
                        log.info("BucketOCSlet==>serviceinfo_getPrice:" + serviceinfo_getPrice);
                        String resultcode = "";
                        String xmlresult = "";

                        String count1x = String.valueOf((int) Math.floor(count1 * XValue));
                        log.info("BucketOCSlet==>count1x:" + count1x);

                        if (serviceinfo.getPrice() > count1) {
                            //610帳本先扣完，再扣620
//                            log.info("BucketOCSlet==>" + libm + "," + mdn + "," + tradedate + ",610," + count1x + "," + ref + ",decr");
//
//                            xmlresult = adjustbucket.putBucketOCSlet(libm, mdn, tradedate, "610", count1x, "decr");
                            log.info("BucketOCSlet==>" + libm + "," + mdn + "," + tradedate + ",610," + count1x + "," + ref + ",decr");

                            int ttype = ShareParm.TTYPE_ADJUSTACCOUNT;

                            //get tid
                            String cardtype610 = serviceinfo.getCardtype();
                            BundleDateUtil bdt610 = new BundleDateUtil();
                            String kk610 = bdt610.getNowDate();
                            String tid610 = ShareParm.TRANS_ID + kk610.substring(2, 4) + cardtype610;
                            log.info("tid610 ===>" + tid610);
                            //get ref
                            EPAY_BUCKET bucket_ref610 = new EPAY_BUCKET();
                            bucket_ref610 = epaybusinesscontroller.getBucketListBySidAndBid(sid, "610");
                            String ref610 = bucket_ref610.getRef();
                            log.info("ref610 ===>" + ref610);

                            xmlresult = adjustbucket.putBucketOCSlet(libm, mdn, tradedate, "610", count1x, "decr", sid, ttype, tid610, ref610);

                            if (!count1x.equals("0")) {
                                BucketReqBean apirequestbean610 = new BucketReqBean();
                                apirequestbean610 = adjustbucket.parseBucketCodeXMLString(xmlresult);
                                resultcode = apirequestbean610.getResultcode();
                                flag = ProcessFlag(resultcode);

                                //
                                EPAY_BUCKETHISTORY bucket_history = new EPAY_BUCKETHISTORY();
                                bucket_history.setLibm(libm);
                                bucket_history.setMdn(mdn);
                                bucket_history.setBucketid("610");
                                bucket_history.setAmount(Integer.valueOf(count1x) * (-1));
                                epaybusinesscontroller.insertbuckethistory(bucket_history);
                            }
                            if (flag) {
                                //610扣款成功
                                //扣除620帳本
                                int b2 = (int) Math.floor(serviceinfo.getPrice()) - (int) Math.floor(count1);
                                log.info("BucketOCSlet==>count1x:" + b2);

                                String count2x = String.valueOf((int) Math.floor(b2 * XValue));
                                log.info("BucketOCSlet==>" + libm + "," + mdn + "," + tradedate + ",620," + count2x + "," + ref + ",decr");
                                //  xmlresult = adjustbucket.putBucketOCSlet(libm, mdn, tradedate, "620", count2x, "decr");

                                //get tid
                                String cardtype620 = serviceinfo.getCardtype();
                                BundleDateUtil bdt620 = new BundleDateUtil();
                                String kk620 = bdt620.getNowDate();
                                String tid620 = ShareParm.TRANS_ID + kk620.substring(2, 4) + cardtype620;
                                log.info("tid ===>" + tid620);
                                //get ref
                                EPAY_BUCKET bucket_ref620 = new EPAY_BUCKET();
                                bucket_ref620 = epaybusinesscontroller.getBucketListBySidAndBid(sid, "620");
                                String ref620 = bucket_ref620.getRef();
                                log.info("ref620 ===>" + ref620);

                                xmlresult = adjustbucket.putBucketOCSlet(libm, mdn, tradedate, "620", count2x, "decr", sid, ttype, tid620, ref620);

                                BucketReqBean apirequestbean620 = new BucketReqBean();
                                apirequestbean620 = adjustbucket.parseBucketCodeXMLString(xmlresult);
                                resultcode = apirequestbean620.getResultcode();
                                flag = ProcessFlag(resultcode);
                                //
                                EPAY_BUCKETHISTORY bucket_history = new EPAY_BUCKETHISTORY();
                                bucket_history.setLibm(libm);
                                bucket_history.setMdn(mdn);
                                bucket_history.setBucketid("620");
                                bucket_history.setAmount(Integer.valueOf(count2x) * (-1));
                                epaybusinesscontroller.insertbuckethistory(bucket_history);
                                if (!flag) {
                                    //如果620調帳失敗，則回補610金額
                                    log.error("Adjust Acc 620 fail, and rollback 610 account");
                                    log.info("BucketOCSlet==>" + libm + "," + mdn + "," + tradedate + "610," + count1x + "," + ref + ",incr");
                                    if (!count1x.equals("0")) {
//                                        xmlresult = adjustbucket.putBucketOCSlet(libm, mdn, tradedate, "610", count1x, "incr");
                                        //get tid
                                        String incr_cardtype610 = serviceinfo.getCardtype();
                                        BundleDateUtil incr_bdt610 = new BundleDateUtil();
                                        String incr_kk610 = incr_bdt610.getNowDate();
                                        String incr_tid610 = ShareParm.TRANS_ID + incr_kk610.substring(2, 4) + incr_cardtype610;
                                        log.info("incr_tid610 ===>" + incr_tid610);
                                        //get ref
                                        EPAY_BUCKET incr_bucket_ref610 = new EPAY_BUCKET();
                                        incr_bucket_ref610 = epaybusinesscontroller.getBucketListBySidAndBid(sid, "610");
                                        String incr_ref610 = incr_bucket_ref610.getRef();
                                        log.info("incr_ref610 ===>" + incr_ref610);

                                        xmlresult = adjustbucket.putBucketOCSlet(libm, mdn, tradedate, "610", count1x, "incr", sid, ttype, incr_tid610, incr_ref610);

                                        EPAY_BUCKETHISTORY bucket_historyx = new EPAY_BUCKETHISTORY();
                                        bucket_historyx.setLibm(libm);
                                        bucket_historyx.setMdn(mdn);
                                        bucket_historyx.setBucketid("610");
                                        bucket_historyx.setAmount(Integer.valueOf(count1x));
                                        epaybusinesscontroller.insertbuckethistory(bucket_history);
                                    }
                                }
                            }

                        } else {
                            //直接扣610 Only
//                            xmlresult = adjustbucket.putBucketOCSlet(libm, mdn, tradedate, "610", serviceinfo_getPrice, "decr");
                            int ttype = ShareParm.TTYPE_ADJUSTACCOUNT;

                            //get tid
                            String cardtype610 = serviceinfo.getCardtype();
                            BundleDateUtil bdt610 = new BundleDateUtil();
                            String kk610 = bdt610.getNowDate();
                            String tid610 = ShareParm.TRANS_ID + kk610.substring(2, 4) + cardtype610;
                            log.info("tid610 ===>" + tid610);
                            //get ref
                            EPAY_BUCKET bucket_ref610 = new EPAY_BUCKET();
                            bucket_ref610 = epaybusinesscontroller.getBucketListBySidAndBid(sid, "610");
                            String ref610 = bucket_ref610.getRef();
                            log.info("ref610 ===>" + ref610);

                            xmlresult = adjustbucket.putBucketOCSlet(libm, mdn, tradedate, "610", serviceinfo_getPrice, "decr", sid, ttype, tid610, ref610);

                            BucketReqBean apirequestbean610 = new BucketReqBean();
                            apirequestbean610 = adjustbucket.parseBucketCodeXMLString(xmlresult);
                            resultcode = apirequestbean610.getResultcode();
                            flag = ProcessFlag(resultcode);

                            EPAY_BUCKETHISTORY bucket_history = new EPAY_BUCKETHISTORY();
                            bucket_history.setLibm(libm);
                            bucket_history.setMdn(mdn);
                            bucket_history.setBucketid("610");
                            bucket_history.setAmount(Integer.valueOf(serviceinfo_getPrice) * (-1));
                            epaybusinesscontroller.insertbuckethistory(bucket_history);

                        }

                        //餘額抵扣開始進行
                        if (flag) {
//                            AdjustAccUtil ajacc = new AdjustAccUtil();
//                            flag = ajacc.AdjustAccBucketInit(sid, libm, mdn, tradedate);
                            AdjustAccUtil ajacc = new AdjustAccUtil();
                            int ttype = ShareParm.TTYPE_ADJUSTACCOUNT;
                            flag = ajacc.AdjustAccBucketInit(sid, libm, mdn, tradedate, ttype);

                        }
                    }
                } else if (platformtype == 2) { //ZTE
                    String tradedate = sdf_pincode.format(trans.getTradedate());
                    String sid = trans.getServiceId();
                    int channeltype = 2; //1.网络储值  2.余额抵扣
                    ZTEAdjustAccUtil zteadjust = new ZTEAdjustAccUtil();
                    flag = zteadjust.ZTEAdjustBucketInit(cpid, sid, libm, mdn, tradedate, channeltype);

                } else if (platformtype == 3) {

                    //KK NOKIA
                    int Rchg_Type = 2; //餘額抵扣:2
                    NokiaMainPricePlanCodeUtil mutil = new NokiaMainPricePlanCodeUtil();
                    String promotion_type3 = promotioncode.substring(0, 3);
                    NokiaPricePlanResultBean nbean = mutil.AddMainPricePlanCode(promotion_type3, libm, mdn, priceplancode, tradeDate, Rchg_Type);

                    EPAY_TRANSACTION nokia_trans = epaybusinesscontroller.getTransaction(libm);
                    nokia_trans.setStatus(nbean.getResult_code());
                    epaybusinesscontroller.updateTransaction(nokia_trans);

                    if ("00".equalsIgnoreCase(nbean.getResult_code())) {
                        flag = true;
                    }
                } else {
                    log.info("The PlatformType is NOT define");
                    this.detach();
                    Executions.sendRedirect("/deposit/OnlineAdjustAccounServiceErrMsg.zhtml");
                }

                if (flag) {

                    //dd 0510
//                    SimpleDateFormat sdf15x = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
//                    SimpleDateFormat sdf_pincodex = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);
//                    Calendar nowDateTimex = Calendar.getInstance();
//                    String libmq = sdf15x.format(nowDateTimex.getTime());
//                    String tradeDate_Pincodex = sdf_pincodex.format(nowDateTime.getTime());
//
//                    OCS4GBasicInfoUtil basicinfoutilx = new OCS4GBasicInfoUtil();
//                    String basicinfox = basicinfoutilx.putOCS4GBasicInfoSlet(libmq, mdn, tradeDate_Pincodex);
//                    log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfox);
//
//                    BasicInfoReqBean basicinforeqbeanx = new BasicInfoReqBean();
//                    basicinforeqbeanx = basicinfoutilx.parseBasicInfoXMLString(basicinfox);
//
//                    String d1x = TransDateForamt(basicinforeqbeanx.getEndDate3());
                    SendSMS xsms = new SendSMS();
                    //親愛的用戶您好，您申購的【4GLTE數據儲值699元5.0 GB】服務已生效，服務有效期限2015-10-10 02:00:00。
                    String newmsg = "親愛的用戶您好，您申購的【" + serviceinfo.getServiceName() + "】服務已生效";
//                    String newmsg = "親愛的用戶您好，您申購的【" + serviceinfo.getServiceName() + "】服務已生效，服務有效期限" + d1x;
//                            String xmsg = "用戶" + mdn + "，餘額抵扣交易編號:" + libm + "，" + serviceinfo.getServiceName() + "餘額抵扣成功";
//                            log.info("用戶" + mdn + "，餘額抵扣交易編號:" + libm + "，" + serviceinfo.getServiceName() + "餘額抵扣成功");
                    xsms.sendsms(mdn, newmsg);

                    this.detach();
                    Executions.sendRedirect("/cms/OnlineAdjustAccounMsg.zhtml");
                } else {
                    SendSMS xsms = new SendSMS();
                    String ocmdn = new ShareParm().PARM_OC_MDN;
                    String xmsg = "Epay急件：用戶" + mdn + "，餘額抵扣訂單LIB:" + libm + "儲值失敗，請查修";
                    xsms.sendsms(ocmdn, xmsg);

                    String email_form = new ShareParm().PARM_MAIL_FROM;
                    String dst_user = new ShareParm().PARM_MAIL_TO;
                    try {
                        MailUtil.sendMail(new ShareParm().PARM_MAIL_RELAY_HOST, email_form, dst_user, xmsg, xmsg);
                    } catch (Exception ex) {
                        log.info(ex);
                    }

                    this.detach();
                    Executions.sendRedirect("/cms/OnlineAdjustAccounErrMsg.zhtml");
                }

            } else {
                Messagebox.show("請輸入正確的檢核碼", "亞太電信", Messagebox.OK, Messagebox.ERROR);
                chkcaptcha.randomValue();
                chkcode.setValue("");
                chkcode.setFocus(true);
            }
        } else {
            log.info("The RadioCount is 0");
            this.detach();
            Executions.sendRedirect("/cms/OnlineAdjustAccounServiceErrMsg.zhtml");
        }
    }

    public boolean checkUserStatus(String contractstatuscode, String promotioncode) { //餘額抵扣的條件與其他不同
        boolean result = false;
//        log.info("contractstatuscode==>" + contractstatuscode);
//        log.info("promotioncode=======>" + promotioncode);
        if (promotioncode != null) {
            if (contractstatuscode.equals("9")) {
//                if (promotioncode.equals("P4G") || promotioncode.equals("D3G")) {
                result = true;
//                }
            } else {
                log.info("The Pincode User Check is False");
            }
        }
        return result;
    }

    public boolean ProcessFlag(String resultcode) {
        boolean flag = true;
        if (!resultcode.equals("00")) {
            flag = false;
        }
        return flag;
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

    private String getWording(int sum, int dday, int sr_price, int sr_dday) {

//        int sid_dday = serid.getDday(); // 延展天數
//        int s_price = serid.getPrice(); // 服務折抵金額
//        // sumx : 610+620 金額
//        // ddday : 合約到期日到今日的天數
//        String wording = getWording(sumx, ddday, s_price, sid_dday);
        String result = "";

        if (dday < sr_dday) {
            result = result + "，合約有效日期不足";
        }
        if (sum < sr_price) {
            result = result + "，餘額抵扣金額不足";
        }
        return result;
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
}
