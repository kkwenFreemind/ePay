/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.dtone;

import com.apt.epay.beans.BasicInfoReqBean;
import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.epay.ui.dtone.util.DTONEUtil;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class OnlineCountryCodeService_Window_BAK extends Window {

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

    public void onCreate() throws Exception {

        Textbox textbox_dtone_mdn = (Textbox) getSpaceOwner().getFellow("textbox_dtone_mdn");
        Textbox textbox_balance61 = (Textbox) getSpaceOwner().getFellow("textbox_balance61");
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

        textbox_dtone_mdn.setValue(dtone_mdn);
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
                    int ddday = 0;
                    double sumx = 0;

                    if (platformtype == 1) {//ALU

                    } else if (platformtype == 2) { //ZTE
                        SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                        Calendar nowDateTime = Calendar.getInstance();
                        String libm = sdf15.format(nowDateTime.getTime());

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
//                            double count62x = Double.valueOf(basicinforeqbeanx.getCounterValue2());
//                            sumx = (double) count61x + (double) count62x;

                            textbox_balance61.setValue(String.valueOf(count61x));
                            textbox_balance61.setReadonly(true);

                            double limite_quota = min(dtone_mdn_quota, count61x);
                            boolean count61x_flag = true;//額度不足
                            if (count61x > dtone_mdn_quota) {
                                count61x_flag = false; //61帳本不足
                            }

                            log.info(apt_mdn + " 61:count1x()==>" + count61x);
                            log.info(apt_mdn + " (PARM_DTONE_QUOTA,MDN Trans Amount):" + "(" + PARM_DTONE_QUOTA + "," + dtone_trans_sum + ")" + "===>" + dtone_mdn_quota);
                            log.info(apt_mdn + " limite_quota)==>" + limite_quota);

                            if (limite_quota > 0) {
                                List dtone_serviceinfo = null;
                                dtone_serviceinfo = epaybusinesscontroller.listDTOneServiceInfo(icpid, promotioncode, 2, dt_operator_name);
                                Iterator itserviceinfo1 = dtone_serviceinfo.iterator();
                                int listcount = 0;
                                log.info("dtone_serviceinfo.size()==>" + dtone_serviceinfo.size());

                                while (itserviceinfo1.hasNext()) {
                                    EPAY_DTONESERVICE_INFO serid = (EPAY_DTONESERVICE_INFO) itserviceinfo1.next();
                                    int price = serid.getPrice();
                                    log.info(serid.getServiceName() + " price ==>" + price);

                                    if (limite_quota > price) {

                                        Radio serviceid_radio = new Radio();
                                        serviceid_radio.setId(String.valueOf(serid.getServiceId()));
                                        serviceid_radio.setLabel(serid.getNote() + "=" + serid.getPrice() + "NTD");
                                        serviceid_radio.setParent(radio_servicevbox);
                                        log.info("Radio--->" + serid.getServiceName());

                                        radio_serviceid[listcount] = String.valueOf(serid.getServiceId());
                                        listcount++;
                                    } else {
                                        //DO NOTHING
                                    }
                                }

                                if (listcount == 0) {
                                    log.info(apt_mdn + ":limite_quota==>" + limite_quota + " NO DTOne Service Can Choices");
                                    if (count61x_flag) {
                                        this.detach();
                                        Executions.sendRedirect("/dtone/OnlineAddValue61CountMsg.zhtml");//61帳本不足
                                    } else {
                                        this.detach();
                                        Executions.sendRedirect("/dtone/OnlineAddValueQuotaMsg.zhtml");//額度不足
                                    }
                                } else {
                                    radio_service_type.setSelectedIndex(0);
                                }
                            } else {
                                log.info(apt_mdn + ":61 count1x==>" + count61x);
                                this.detach();
                                Executions.sendRedirect("/dtone/OnlineAddValueQuotaMsg.zhtml");
                            }
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

    public void sendBtn() {

        Button sendBtn = (Button) this.getFellow("sendBtn");
        sendBtn.setDisabled(true);

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
                trans.setCountrycodemdn(dtone_mdn);

                log.info("AdjustAccProce(insert EPAY_TRANSACTION Table)==>MDN:" + apt_mdn + ",Libm:" + libm);
                epaybusinesscontroller.insertTransaction(trans);

                if (platformtype == 1) {
                    //DO NOTHING
                } else if (platformtype == 2) {
                    //Step 1 扣除61帳本
                    //ocs_systemid, ocs_system_pwd, amount, mdn886, glcode, balid
                    ZTEAdjustAccUtil zteutil = new ZTEAdjustAccUtil();
                    int kkamount = amount * (-100);
                    int new_kkamount = amount * (100);
                    int result = zteutil.putZTEOCS4GModifyBal(apt_mdn, kkamount, ACCRESID);

                    if (result == 200) {

                        trans.setStatus("10"); //表示扣款成功
                        epaybusinesscontroller.updateTransaction(trans);

                        //Step 2 呼叫DTONE儲值，如果成功，則結束，如果失敗，則回來儲值61帳本
                        DTONEUtil dtone = new DTONEUtil();
                        String dt_tid ="";
                        log.info(dtone_mdn + " dtone result===>" + dt_tid);

                        trans.setDtoneid(dt_tid);
                        epaybusinesscontroller.updateTransaction(trans);

                        if (!"-1".equals(dt_tid)) {
                            String message = "";
                            try {

                                message = getCountryCodeTransResult(dt_tid);
                                log.info(apt_mdn + ":" + "家鄉儲結果==>" + message);
                                log.info(message);
                                if ("COMPLETED".equalsIgnoreCase(message)) {

                                    log.info(apt_mdn + ":" + "家鄉儲成功==>" + message);
                                    trans.setStatus("00");
                                    trans.setErrdesc("家鄉儲成功");
                                    epaybusinesscontroller.updateTransaction(trans);

                                    this.detach();
                                    Executions.sendRedirect("/dtone/OnlineAddValueSuccessMsg.zhtml");

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
                                    ZTEAdjustAccUtil zteutilx = new ZTEAdjustAccUtil();

                                    int kkresult = zteutilx.putZTEOCS4GModifyBal(apt_mdn, new_kkamount, ACCRESID);
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

                            //61回補
                            String error_desc = trans.getErrdesc();
                            ZTEAdjustAccUtil zteutilx = new ZTEAdjustAccUtil();

                            int kkresult = zteutilx.putZTEOCS4GModifyBal(apt_mdn, new_kkamount, ACCRESID);
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

                } else if (platformtype == 3) {

                } else {
                    log.info("The PlatformType is NOT define");
                    this.detach();
                    Executions.sendRedirect("/dtone/OnlineAddValueOCSErrorMsg.zhtml");
                }
            } else {
                //DO NOTHING
                log.info("交易代碼重複取用==>" + libm);
            }

        } catch (Exception ex) {
            log.info(ex);
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
        cale = Calendar.getInstance();
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
}
