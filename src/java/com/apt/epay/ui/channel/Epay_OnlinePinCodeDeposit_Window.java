/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.channel;

import com.apt.epay.beans.CMSBean;
import com.apt.epay.beans.PinCodeReqBean;
import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.nokia.bean.NokiaPincodeResultBean;
import com.apt.epay.nokia.main.NokiaMainPincodeUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.epay.zte.bean.PinCodeResultBean;
import com.apt.epay.zte.util.ZTEPinCodeUtil;
import com.apt.util.PinCodeUtil;
import com.apt.util.SoaProfile;
import com.epay.ejb.bean.EPAY_PROMOTIONCODE;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Captcha;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
// KK PINCODE
public class Epay_OnlinePinCodeDeposit_Window extends Window {

//    private static final long serialVersionUID = -84547344457721L;
    private static final Logger log = Logger.getLogger("EPAY");
    private String libm = "";
    private String contractid;
    private String mdn;
    private String contractstatuscode;
    private String promotioncode;
    private String producttype;

    private CMSBean cmsbean;
    private String storeid;
    private String salesid;
    private String invno;
    private String srcid;
    private String trnsdatetime;
    private String oc_checkpoint;

    public void onCreate() throws Exception {

        // http://10.31.80.210:8080/EPAY/cms/CMS_OnlinePinCodeDeposit.zhtml?PKEY=11223344&APTCMS&MDN=0906044025&STORE_ID=70771235&SALES_ID=09600333&SRC_ID=1&INVOICE_NO=A1234567890&TIMESTAMP=201508171700&OCCHECKPOING=F
        // http://10.31.80.210:8080/EPAY/cms/CMS_OnlinePinCodeDeposit.zhtml?PKEY=11223344&encryptResponseMsg=JkFQVENNUyZNRE49MDkwNjA0NDAyNSZTVE9SRV9JRD03MDc3MTIzNSZTQUxFU19JRD0wOTYwMDMzMyZTUkNfSUQ9MSZJTlZPSUNFX05PPUExMjM0NTY3ODkwJlRJTUVTVEFNUD0yMDE1MDgxNzE3MDA=
        Sessions.getCurrent().removeAttribute("cmsbean");
        cmsbean = new CMSBean();

        log.info("Creating Online Deposit...from IP " + Executions.getCurrent().getRemoteAddr());
        HttpServletRequest req = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
        log.debug("doAfterCompose.getQueryString():" + req.getQueryString());

//        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox textbox_mdn = (Textbox) this.getFellow("textbox_mdn");
        textbox_mdn.setValue(mdn);

    }

    public void sendOrder() throws Exception {

//        Button sendBtn = (Button) this.getFellow("sendPinCodeBtn");
//        sendBtn.setDisabled(true);        
//        log.info("sendPinCodeBtn.setDisabled(true)");
        String message = "";

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
        SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
        SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);

        Captcha chkcaptcha = (Captcha) getSpaceOwner().getFellow("cpa");
        Textbox chkcode = (Textbox) this.getFellow("chkcode");
        Textbox textbox_mdn = (Textbox) this.getFellow("textbox_mdn"); //聯絡人手機號碼
        Textbox textbox_pincode = (Textbox) this.getFellow("textbox_pincode");

        try {

            mdn = textbox_mdn.getValue();
            SoaProfile soa = new SoaProfile();
            String result = soa.putSoaProxyletByMDN(textbox_mdn.getValue());

            SOAReqBean apirequestbean = new SOAReqBean();
            apirequestbean = soa.parseXMLString(result);

            contractid = apirequestbean.getContractid();
            contractstatuscode = apirequestbean.getContract_status_code();
            producttype = apirequestbean.getProducttype();

            if ((apirequestbean.getPromotioncode() != null)) {

                promotioncode = apirequestbean.getPromotioncode();

            } else {
                log.info("Exception==> The REAL promotioncode is" + promotioncode);
            }

            log.info("PinCodeProce(SOA Result)==>IP:" + Executions.getCurrent().getRemoteAddr()
                            + ",MDN:" + mdn
                            + ",ContractID:" + contractid
                            + ",Contract_status:" + contractstatuscode
                            + ",producttype:" + producttype
                            + ",promotioncode:" + promotioncode);

            EPAY_PROMOTIONCODE epay_promotioncode = new EPAY_PROMOTIONCODE();
            epay_promotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);

            if (epay_promotioncode != null) {

                if ((contractstatuscode.equals("9") || contractstatuscode.equals("43"))
                                && (epay_promotioncode.getPlatformtype() == 1
                                || epay_promotioncode.getPlatformtype() == 2
                                || epay_promotioncode.getPlatformtype() == 3)) {

                    log.info("contractstatuscode==>" + contractstatuscode);
                    log.info("epay_promotioncode.getPlatformtype()===>" + epay_promotioncode.getPlatformtype());

                } else {
                    log.info(mdn + " Contract Status Check : " + contractstatuscode);
//                    this.detach();
//                    Executions.sendRedirect("/channel/Epay_OnlineUserErrorMsg.zhtml");
                    org.zkoss.zul.Messagebox.show("門號驗證失敗，條件不符合，ContractStatus:" + contractstatuscode + ",Promotion Code:" + promotioncode, "亞太電信", org.zkoss.zul.Messagebox.OK, org.zkoss.zul.Messagebox.INFORMATION);

                }
            } else {
                // no promotioncode data in table
                log.info(mdn + " promotion code Check : " + promotioncode);
                this.detach();
                Sessions.getCurrent().setAttribute("promotioncode", promotioncode);
                Executions.sendRedirect("/deposit/OnlinePromotionCodeErrorMsg.zhtml");
            }
        } catch (Exception ex) {
            log.info(ex);
//            this.detach();
//            Executions.sendRedirect("/channel/OnlineSSOErrorMsg.zul");
//            org.zkoss.zul.Messagebox.show("例外錯誤發生(exception):" + ex, "亞太電信", org.zkoss.zul.Messagebox.OK, org.zkoss.zul.Messagebox.INFORMATION);
            org.zkoss.zul.Messagebox.show("門號驗證失敗，條件不符合，ContractStatus:" + contractstatuscode + ",Promotion Code:" + promotioncode, "亞太電信", org.zkoss.zul.Messagebox.OK, org.zkoss.zul.Messagebox.INFORMATION);
        }

        String itemCode = ShareParm.PINCODE_ITEMCODE; //PinCode
        String itemName = ShareParm.PINCODE_ITEMNAME; //實體卡片儲值

        String contactCellPhone = textbox_mdn.getValue();//聯絡手機號碼
        String pincode = textbox_pincode.getValue(); //PinCode

        String receiptType = "";//無發票資訊(二聯三聯)
        int tradeAmount = 0;// PinCode實體卡片儲值金額為null, 我們不會知道該實體卡片實際金額(Price)
        int tradeQuantity = 1;
        String receiptTitle = "na";//無發票資訊(發票抬頭)
        String vatNo = "na";//無發票資訊(統編)
        String receiptAddress = "na";//無發票資訊(地址)
        String contactName = "na";//無發票資訊(聯絡人)
        String contactPhone = "na";//無發票資訊(聯絡電話)
        String contactEmail = "na";//無發票資訊(電郵)

        String orderTotal = String.valueOf(tradeAmount * tradeQuantity);
        Calendar nowDateTime = Calendar.getInstance();
        String tradeDate = sdf.format(nowDateTime.getTime());
        String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());
        String resultcode = "";

        EPAY_PROMOTIONCODE epaypromotioncode = new EPAY_PROMOTIONCODE();
        epaypromotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);
        int platformtype = epaypromotioncode.getPlatformtype();

        if (chkcode.getValue() != null && chkcode.getValue().equals(chkcaptcha.getValue())) {

            // 產生訂單編號 yymmddHHmissSSS
            libm = sdf15.format(nowDateTime.getTime());
            //記錄和比對是否已有訂單            
            EPAY_TRANSACTION trans = epaybusinesscontroller.getTransaction(libm);

            if (trans == null && !"".equalsIgnoreCase(libm) && !"null".equalsIgnoreCase(libm)) {
                //如果沒有編號,直接insert新的
                trans = new EPAY_TRANSACTION();
                trans.setLibm(libm);
                trans.setItemcode(itemCode);//PinCode
                trans.setItemproductname(itemName);//實體卡片儲值
                trans.setItemquantity(tradeQuantity);//1
                trans.setOrdertotal(Integer.parseInt(orderTotal));//0
                trans.setFee(0);
                trans.setDiscount(0);
                trans.setTradedate(sdf.parse(tradeDate));
                trans.setPaytime(sdf.parse(tradeDate));
                trans.setPaymethod(ShareParm.PAYMETHOD_PINCODE); //付款方式 PinCode=4
                trans.setStatus("N"); //OCS尚未儲值完成
                trans.setPrivatedata(pincode); //PinCode number

                trans.setServiceId(ShareParm.PINCODE_SERVICEID);//
                trans.setCpLibm(libm);
                trans.setCpId(Integer.parseInt(new ShareParm().PARM_EPAY_CPID));//
                trans.setFeeType("0"); //無拆帳需求
                trans.setInvoiceContactMobilePhone(contactCellPhone);

                trans.setContractID(contractid);
                trans.setPossaleid(salesid);
                trans.setPoscode(storeid);
                trans.setPlatformtype(platformtype);

                //20170713
                trans.setApisrcid("7");

                log.info("Epay PinCodeProce(insert Table)==>MDN:" + mdn + ",Libm:" + libm);

                epaybusinesscontroller.insertTransaction(trans);

            } else {
                //如果有編號,檢查是否已經交易, 如果還未交易可在更新資料。   
            }

            //Sending Pincode/Password/MDN to OCS
            log.info("PinCodeProce(MDN:" + mdn + " SendData to OCS)==>libm:" + libm);
            message = "PinCodeProce(MDN:" + mdn + " SendData to OCS)==>libm:" + libm;

            try {

                //判斷是要到ALU還是ZTE做儲值
                EPAY_PROMOTIONCODE epay_promotioncode = new EPAY_PROMOTIONCODE();
                epay_promotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);
                log.info("===>" + epay_promotioncode.getPlatformtype() + "," + epay_promotioncode.getPromotioncode());

                if (epay_promotioncode.getPlatformtype() == 1) { //ALU

                    PinCodeUtil pincodeutil = new PinCodeUtil();
                    String result = pincodeutil.putPincodeOCSlet(libm, contactCellPhone, pincode, tradeDate_Pincode);

                    log.info("PinCodeProce(MDN:" + mdn + " OCS XML Resonse)==>" + result);
                    message = message + "\n" + "PinCodeProce(MDN:" + mdn + " OCS XML Resonse)==>" + result;

                    if (result != null) {
                        PinCodeReqBean apirequestbean = new PinCodeReqBean();
                        apirequestbean = pincodeutil.parsePinCodeXMLString(result);

                        resultcode = apirequestbean.getResultcode();

                        trans.setStatus(resultcode);
                        log.info("CMS PinCodeProce(MDN:" + mdn + " OCS ResultCode)==>" + resultcode);
                        message = message + "\n" + "PinCodeProce(MDN:" + mdn + " OCS ResultCode)==>" + resultcode;

                        boolean brst = epaybusinesscontroller.updateTransaction(trans);
                        log.info("CMS PinCodeProce(MDN:" + mdn + " update Table status is " + brst + ")==>resultcode:" + resultcode);
                        message = message + "\n" + "PinCodeProce(MDN:" + mdn + " update Table status is " + brst + ")==>resultcode:" + resultcode;

                    } else {
                        log.info("CMS Admin Exception===>THE PINCODE Result From 4G OCS IS NULL!!!!!");
                        message = message + "\n" + "Admin Exception===>THE PINCODE Result From 4G OCS IS NULL!!!!!";
                    }
                } else if (epay_promotioncode.getPlatformtype() == 2) {// ZTE

                    ZTEPinCodeUtil ztePincode = new ZTEPinCodeUtil();
                    String result = ztePincode.putPincodeOCSlet(libm, contactCellPhone, pincode);
                    log.info("ZTE PinCodeProce(MDN:" + mdn + " ZTE OCS XML Resonse)==>" + result);

                    if (result != null) {

                        if (!result.contains("Fault")) {
                            PinCodeResultBean apirequestbean = new PinCodeResultBean();
                            apirequestbean = ztePincode.parseZTEPinCodeXMLString(result);

                            String zteresultcode = apirequestbean.getReturncode();
                            log.info("zteresultcode====>" + zteresultcode);

                            int amt = 0;
                            if (zteresultcode.equals("0000")) {
                                resultcode = "00";
                                amt = Integer.valueOf(apirequestbean.getChargemoney());
                                trans.setItemunitprice(amt);
                                trans.setPayamount(amt);
                                trans.setOrdertotal(amt);
                            } else {
                                resultcode = zteresultcode;
                            }

                            trans.setStatus(resultcode);
                            boolean brst = epaybusinesscontroller.updateTransaction(trans);

                            log.info("ZTE PinCodeProce(MDN:" + mdn + " ZTE OCS ResultCode)==>" + resultcode);
                            message = message + "\n" + "ZTE PinCodeProce(MDN:" + mdn + " ZTE OCS ResultCode)==>" + resultcode;

                            log.info("ZTE PinCodeProce(MDN:" + mdn + " update Table status is " + brst + ")==>resultcode:" + resultcode);
                            message = message + "\n" + "ZTE PinCodeProce(MDN:" + mdn + " update Table status is " + brst + ")==>resultcode:" + resultcode;

                        } else {
                            log.info("Admin Exception===>THE ZTEPINCODE Result From 4G ZTE OCS IS NULL!!!!!");
                            message = message + "\n" + "Admin Exception===>THE PINCODE Result From 4G ZTE OCS IS NULL!!!!!";

                        }
                    } else {
                        log.info("Admin Exception===>THE ZTEPINCODE Result From 4G ZTE OCS IS NULL!!!!!");
                        message = message + "\n" + "Admin Exception===>THE PINCODE Result From 4G ZTE OCS IS NULL!!!!!";

                    }

                } else if (epay_promotioncode.getPlatformtype() == 3) { //NOKIA
                    // KK NOKIA

                    log.info("NOKIA ===>" + mdn + "," + promotioncode);
                    String pomotion_type = promotioncode.substring(0, 3);
                    log.info("NOKIA MDN:" + mdn + " Promotion Type(3)==>" + promotioncode + " ==>" + pomotion_type);

                    NokiaMainPincodeUtil mainutil = new NokiaMainPincodeUtil();
                    NokiaPincodeResultBean nokia_result = mainutil.AddMainPincode(pomotion_type, libm, mdn, pincode, tradeDate);
                    resultcode = nokia_result.getResult_code();
                    log.info("NOKIA MDN:" + mdn + " Pincode Result ==>" + resultcode + " ==>" + pincode);

                    String nokia_result_desc = "", nokia_errorcode = "", nokia_status = "";
                    if ("00".equalsIgnoreCase(resultcode)) {
                        nokia_result_desc = "儲值成功";
                        nokia_errorcode = resultcode;
                        nokia_status = "00";
                    } else {
                        nokia_result_desc = "儲值失敗:" + nokia_result.getReason();
                        nokia_errorcode = "-1";
                        nokia_status = "N";
                    }
                    trans.setErrdesc(nokia_result_desc);
                    trans.setErrcode(nokia_errorcode);
                    trans.setStatus(nokia_status);
                    log.info("NOKIA PinCodeProce(MDN:" + mdn + " NOKIA OCS ResultCode)==>" + resultcode);
                    //message = message + "\n" + "PinCodeProce(MDN:" + mdn + " NOKIA OCS ResultCode)==>" + resultcode;

                    boolean brst = epaybusinesscontroller.updateTransaction(trans);
                    log.info("NOKIA PinCodeProce(MDN:" + mdn + " update Table status is " + brst + ")==>resultcode:" + resultcode);
                    //message = message + "\n" + " Nokia PinCodeProce(MDN:" + mdn + " update Table status is " + brst + ")==>resultcode:" + resultcode;
                }

            } catch (Exception ex) {
                log.info(ex);
            }

            if (resultcode.equals("00")) {
                //Success
//                this.detach();
//                Executions.sendRedirect("/channel/OnlinePinCodeMsg.zul");
                org.zkoss.zul.Messagebox.show("成功", "亞太電信", org.zkoss.zul.Messagebox.OK, org.zkoss.zul.Messagebox.INFORMATION);
                textbox_mdn.setValue("");
                textbox_pincode.setValue("");
                chkcode.setValue("");
                chkcode.setFocus(true);
            } else {
//                this.detach();
//                Executions.sendRedirect("/channel/OnlinePinCodeErrorMsg.zul");
                org.zkoss.zul.Messagebox.show("OCS儲值失敗", "亞太電信", org.zkoss.zul.Messagebox.OK, org.zkoss.zul.Messagebox.INFORMATION);
                textbox_mdn.setValue("");
                textbox_pincode.setValue("");
                chkcode.setValue("");
                chkcode.setFocus(true);
            }

        } else {
            Messagebox.show("請輸入正確的檢核碼", "亞太電信", Messagebox.OK, Messagebox.ERROR);
            chkcaptcha.randomValue();
            chkcode.setValue("");
            chkcode.setFocus(true);
        }

    }

    public static String MD5_003(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] source = plainText.getBytes();
            md.update(source);
            byte[] tmp = md.digest();

            StringBuffer buf = new StringBuffer();
            for (byte byte0 : tmp) {
                buf.append(String.format("%02x", byte0 & 0xff));
            }
            return buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
