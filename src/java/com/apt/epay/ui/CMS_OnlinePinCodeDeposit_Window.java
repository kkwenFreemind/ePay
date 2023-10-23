/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui;

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
import com.apt.util.Base64Util;
import com.apt.util.PinCodeUtil;
import com.apt.util.SoaProfile;
import com.apt.util.TransDateUtil;
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_PROMOTIONCODE;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Captcha;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
// KK PINCODE
public class CMS_OnlinePinCodeDeposit_Window extends Window {

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

        Sessions.getCurrent().removeAttribute("cmsbean");
        cmsbean = new CMSBean();

        log.info("Creating Online Deposit...from IP " + Executions.getCurrent().getRemoteAddr());
        HttpServletRequest req = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
        log.info("doAfterCompose.getQueryString():" + req.getQueryString());

        String PROXY_FLAG = new ShareParm().PARM_SCT_PROXY_FLAG;
        String SS = req.getParameter("SS");
        log.info("SS==>" + SS);

        boolean proflag = true;

        if (SS.length() == 0) {
            log.error("Parameter encryptResponseMsg is null!");
            proflag = false;
        } else {

            String encryptMsg = "";
            String PKEY = "";

            encryptMsg = (String) req.getParameter("SS");
            PKEY = (String) req.getParameter("PKEY");

            log.info("PKey==>" + PKEY);
            log.info("encryptMsg==>" + encryptMsg);

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
                Executions.sendRedirect("/cms/CMS_OnlineSSOErrorMsg.zhtml");

            }
        }

        //debug
        if ("TDE".equalsIgnoreCase(PROXY_FLAG)) {
            String tde_mdn = new ShareParm().PARM_TDE_MDN;
            mdn = tde_mdn;
            log.info("This is ===================>" + PROXY_FLAG);
            log.info("mdn  is ===================>" + mdn);            
        }
        
        Textbox textbox_mdn = (Textbox) this.getFellow("textbox_mdn");
        textbox_mdn.setValue(mdn);

        if (proflag) {

            try {

                SoaProfile soa = new SoaProfile();
                String result = soa.putSoaProxyletByMDN(mdn);

                SOAReqBean apirequestbean = new SOAReqBean();
                apirequestbean = soa.parseXMLString(result);

                log.info("kkflag==>CMSPincodeDeposit:" + result);

                contractid = apirequestbean.getContractid();
                contractstatuscode = apirequestbean.getContract_status_code();
                producttype = apirequestbean.getProducttype();

                if ((apirequestbean.getPromotioncode() != null)) {
                    promotioncode = apirequestbean.getPromotioncode();
                } else {
                    log.info("Exception==> The REAL promotioncode is" + promotioncode);
                }

//                boolean verifyUserStatus = checkPincodeUserStatus(contractstatuscode, sub_promotioncode);
                textbox_mdn.setValue(mdn);

                log.info("PinCodeProce(SOA Result)==>IP:" + Executions.getCurrent().getRemoteAddr()
                                + ",MDN:" + mdn
                                + ",ContractID:" + contractid
                                + ",Contract_status:" + contractstatuscode
                                + ",producttype:" + producttype
                                + ",promotioncode:" + promotioncode
                                + ",transDatetime:" + trnsdatetime
                                + ",oc_checkpoint:" + oc_checkpoint
                );

                //那些條件才能使用實體儲值卡儲值
                //promotion code = "P4G"
                //contractstatus =9 or 43 *合約正常，停話
                //如果不符合條件則回到官網首頁
                EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
                EPAY_PROMOTIONCODE epay_promotioncode = new EPAY_PROMOTIONCODE();
                epay_promotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);

                if ((contractstatuscode.equals("9") || contractstatuscode.equals("43"))
                                && (epay_promotioncode.getPlatformtype() == 1
                                || epay_promotioncode.getPlatformtype() == 2
                                || epay_promotioncode.getPlatformtype() == 3)) {

                    log.info("contractstatuscode==>" + contractstatuscode);
                    log.info("epay_promotioncode.getPlatformtype()===>" + epay_promotioncode.getPlatformtype());

                    //檢查連線到期時間是否小於10分鐘
                    String pattern1 = "yyyy-MM-dd HH:mm:ss";
                    String pattern2 = "yyyyMMddHHmmss";
                    SimpleDateFormat sdf1 = new SimpleDateFormat(pattern1);
                    SimpleDateFormat sdf2 = new SimpleDateFormat(pattern2);
                    Date parseDate = new SimpleDateFormat(pattern2).parse(trnsdatetime);
                    String newdate = sdf1.format(parseDate);
                    log.info("TransDateTime reformat ==>" + newdate);

                    TransDateUtil transdateutil = new TransDateUtil();
                    boolean flag = transdateutil.getDiffTime(newdate);
                    if (flag) {
                        //DO Nothing
                    } else {
                        //超過交易時間10分鐘，重新登入交易
                        if (oc_checkpoint != null) {

                        } else {
                            log.info("超過交易時間10分鐘，需重新登入交易");
                            this.detach();
                            Executions.sendRedirect("/cms/CMS_OnlineTimeoutErrorMsg.zhtml");
                        }
                    }
                } else {
                    log.info(mdn + " Contract Status Check : " + contractstatuscode);
                    this.detach();
                    Sessions.getCurrent().setAttribute("status", contractstatuscode);
                    Executions.sendRedirect("/cms/CMS_OnlineUserErrorMsg.zhtml");
                }

            } catch (Exception ex) {
                log.info(ex);
                this.detach();
                Executions.sendRedirect("/cms/CMS_OnlineSSOErrorMsg.zhtml");
            }
        } else {
            this.detach();
            Executions.sendRedirect("/cms/CMS_OnlineSSOErrorMsg.zhtml");
        }
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

        String itemCode = ShareParm.PINCODE_ITEMCODE; //PinCode
        String itemName = ShareParm.PINCODE_ITEMNAME; //實體卡片儲值

        String contactCellPhone = textbox_mdn.getValue();//聯絡手機號碼
        String pincode = textbox_pincode.getValue(); //PinCode
        int tradeAmount = 0;// PinCode實體卡片儲值金額為null, 我們不會知道該實體卡片實際金額(Price)
        int tradeQuantity = 1;

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
//                trans.setPaystatus(1); //0:失敗(default value), 但PinCode繳費狀態為null
                trans.setPrivatedata(pincode); //PinCode number

                trans.setServiceId(ShareParm.PINCODE_SERVICEID);//
                trans.setCpLibm(libm);
                trans.setCpId(Integer.parseInt(new ShareParm().PARM_EPAY_CPID));//

                String cpid = new ShareParm().PARM_EPAY_CPID;
                EPAY_CP_INFO cpinfo = epaybusinesscontroller.getCpInfo(Integer.valueOf(cpid));
                String cpname = cpinfo.getCpName();
                trans.setCpName(cpname);

                trans.setFeeType("0"); //無拆帳需求
                trans.setInvoiceContactMobilePhone(contactCellPhone);
                trans.setContractID(contractid);
                trans.setPossaleid(salesid);
                trans.setPoscode(storeid);
                trans.setPlatformtype(platformtype); //ALU or ZTE

                //20170713
                trans.setApisrcid("3");

                log.info("CMS PinCodeProce(insert Table)==>MDN:" + mdn + ",Libm:" + libm);

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

                log.info("MDN:" + mdn + " ===>" + epay_promotioncode.getPlatformtype() + "," + epay_promotioncode.getPromotioncode());

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

                } else if (epay_promotioncode.getPlatformtype() == 2) {

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

                    //KK NOKIA
                    log.info("NOKIA ===>" + mdn + "," + promotioncode);
                    String pomotion_type = promotioncode.substring(0, 3);
                    log.info("NOKIA MDN:" + mdn + " Promotion Type(3)==>" + promotioncode + " ==>" + pomotion_type);

                    NokiaMainPincodeUtil mainutil = new NokiaMainPincodeUtil();
                    NokiaPincodeResultBean result = mainutil.AddMainPincode(pomotion_type, libm, mdn, pincode, tradeDate);
                    resultcode = result.getResult_code();
                    log.info("NOKIA MDN:" + mdn + " Pincode Result ==>" + resultcode + " ==>" + pincode);

                    String nokia_result_desc = "", nokia_errorcode = "", nokia_status = "";
                    if ("00".equalsIgnoreCase(resultcode)) {
                        nokia_result_desc = "儲值成功";
                        nokia_errorcode = resultcode;
                        nokia_status = "00";
                    } else {
                        nokia_result_desc = "儲值失敗:" + result.getReason();
                        nokia_errorcode = "-1";
                        nokia_status = "N";
                    }
                    trans.setErrdesc(nokia_result_desc);
                    trans.setErrcode(nokia_errorcode);
                    trans.setStatus(nokia_status);
                    log.info("NOKIA PinCodeProce(MDN:" + mdn + " NOKIA OCS ResultCode)==>" + resultcode);
                    message = message + "\n" + "PinCodeProce(MDN:" + mdn + " NOKIA OCS ResultCode)==>" + resultcode;

                    boolean brst = epaybusinesscontroller.updateTransaction(trans);
                    log.info("NOKIA PinCodeProce(MDN:" + mdn + " update Table status is " + brst + ")==>resultcode:" + resultcode);
                    message = message + "\n" + " Nokia PinCodeProce(MDN:" + mdn + " update Table status is " + brst + ")==>resultcode:" + resultcode;
                }
            } catch (Exception ex) {
                log.info(ex);
            }

            if (resultcode.equals("00")) {
                //Success
                this.detach();
                Executions.sendRedirect("/cms/CMS_OnlinePinCodeMsg.zhtml");

            } else {
                this.detach();
                Executions.sendRedirect("/cms/CMS_OnlinePinCodeErrorMsg.zhtml");
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

//    public boolean checkPincodeUserStatus(String contractstatuscode, String promotioncode) {
//        boolean result = false;
////        log.info("contractstatuscode==>" + contractstatuscode);
////        log.info("promotioncode=======>" + promotioncode);
//        if (promotioncode != null) {
//            if (contractstatuscode.equals("9") || contractstatuscode.equals("43")) {
//                if (promotioncode.equals("PPP") || promotioncode.equals("P4G") || promotioncode.equals("D3G")
//                        || promotioncode.equals("APT") || promotioncode.equals("GTA") || promotioncode.equals("C2U") || promotioncode.equals("AIR") || promotioncode.equals("TRV")) {
//                    result = true;
//                }
//            } else {
//                log.info("The Pincode User Check is False");
//            }
//        }
//        return result;
//    }
}
