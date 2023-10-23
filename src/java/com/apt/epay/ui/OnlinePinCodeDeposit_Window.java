/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui;

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
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_PROMOTIONCODE;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Captcha;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
// KK PINCODE
public class OnlinePinCodeDeposit_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");
    private String libm = "";
    private String contractid;
    private String mdn;
    private String contractstatuscode;
    private String promotioncode;
    private String producttype;

    public void onCreate() throws Exception {

        Sessions.getCurrent().removeAttribute("odBean");
        String uuid;
        uuid = ((HttpServletRequest) Executions.getCurrent().getNativeRequest()).getRemoteUser();
        log.info("PinCodeProces(init)==>IP:" + Executions.getCurrent().getRemoteAddr() + ",UUID:" + uuid);

        Textbox textbox_mdn = (Textbox) this.getFellow("textbox_mdn");
        Textbox textbox_contract_mdn = (Textbox) this.getFellow("textbox_contract_mdn");

        Label label_mdn = (Label) this.getFellow("label_mdn");
        Label label_contract_mdn = (Label) this.getFellow("label_contract_mdn");

        try {
            if (uuid != null) {
                SoaProfile soa = new SoaProfile();
                String result = soa.putSoaProxylet(uuid);
                SOAReqBean apirequestbean = new SOAReqBean();
                apirequestbean = soa.parseXMLString(result);

                log.info("kkflag==>PincodeDeposit:" + result);

                contractid = apirequestbean.getContractid();
                contractstatuscode = apirequestbean.getContract_status_code();
                producttype = apirequestbean.getProducttype();
                mdn = apirequestbean.getMdn();

                if ((apirequestbean.getPromotioncode() != null)) {
                    promotioncode = apirequestbean.getPromotioncode();
                } else {
                    log.info("Exception==> The REAL promotioncode is" + promotioncode);
                }

                textbox_mdn.setValue(mdn);
                textbox_contract_mdn.setValue(mdn);

                String label_result = getPromtionStr(mdn);
                String cc_result = " 合約門號　" + label_result;
                String kk_result = " 儲值門號　" + label_result;

                label_mdn.setStyle("font-family: '微軟正黑體';font-size: 20px;");
                label_mdn.setValue(kk_result);

                label_contract_mdn.setStyle("font-family: '微軟正黑體';font-size: 20px;");
                label_contract_mdn.setValue(cc_result);

                /*
                 PinCodeProce(SOA Result)==>IP:10.31.81.5,MDN:0906077724,ContractID:P4G-15072900046,Contract_status:9,producttype:P4G,promotioncode:P4G1521000
                 */
                log.info("PinCodeProce(SOA Result)==>IP:" + Executions.getCurrent().getRemoteAddr()
                                + ",MDN:" + mdn
                                + ",ContractID:" + contractid
                                + ",Contract_status:" + contractstatuscode
                                + ",producttype:" + producttype
                                + ",promotioncode:" + promotioncode);

//                //那些條件才能使用實體儲值卡儲值
//                //promotion code = "P4G" "PPP""D3G""APT""GTA""C2U""AIR""TRV"
//                //contractstatus =9 or 43 *合約正常，停話
                //如果不符合條件則回到官網首頁
                //判斷是要到ALU還是ZTE做儲值
                EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
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
                        this.detach();
                        Sessions.getCurrent().setAttribute("status", contractstatuscode);
                        Executions.sendRedirect("/deposit/OnlineUserErrorMsg.zhtml");
                    }
                } else {
                    log.info(mdn + " promotion code Check : " + promotioncode);
                    this.detach();
                    Sessions.getCurrent().setAttribute("promotioncode", promotioncode);
                    Executions.sendRedirect("/deposit/OnlinePromotionCodeErrorMsg.zhtml");
                }
            } else {
                log.info("UUID IS NULL");
                this.detach();
                Window window = (Window) Executions.createComponents("/deposit/OnlineSSOErrorMsg.zhtml", null, null);
            }
        } catch (Exception ex) {
            log.info(ex);
        }

    }

    public String getPromtionStr(String mdn) throws Exception {

        String str_result = "";
        SOAReqBean apirequestbean;
        SoaProfile soa = new SoaProfile();
        String result = soa.putSoaProxyletByMDN(mdn);
        apirequestbean = soa.parseXMLString(result);

        String tmp_promotioncode = apirequestbean.getPromotioncode();

        String label_result = "";

        if (tmp_promotioncode != null && !"".equals(tmp_promotioncode)) {
            log.info("chkPromotionCode=====>" + tmp_promotioncode);
            String promotioncode_3 = tmp_promotioncode.substring(0, 3);
            log.info("chkPromotionCode=====>" + promotioncode_3);

            if (promotioncode_3.equalsIgnoreCase("P4G")) {
                label_result = "4G LTE";
            } else if (promotioncode_3.equalsIgnoreCase("GTB")) {
                label_result = "4G Go";
            } else if (promotioncode_3.equalsIgnoreCase("GTA") || promotioncode_3.equalsIgnoreCase("APT")
                            || promotioncode_3.equalsIgnoreCase("AIR") || promotioncode_3.equalsIgnoreCase("TRV")) {
                label_result = "4G Love";
            } else {
                label_result = "4G PP";
            }
        }
        str_result = label_result;
        return str_result;
    }

    public void chkPromotionCode() throws Exception {
        Textbox textbox_mdn = (Textbox) this.getFellow("textbox_mdn"); //聯絡人手機號碼
        Label label_mdn = (Label) this.getFellow("label_mdn");

//        label_mdn.setValue("");
        String chk_mdn = textbox_mdn.getValue();

        log.info("chkPromotionCode=====>" + chk_mdn);

        String label_result = getPromtionStr(chk_mdn);
        String kk_result = " 儲值門號　" + label_result;

        label_mdn.setStyle("font-family: '微軟正黑體';font-size: 20px;");
        label_mdn.setValue(kk_result);

    }

    public void doOrder() throws Exception {

        Textbox textbox_mdn = (Textbox) this.getFellow("textbox_mdn"); //聯絡人手機號碼
        Textbox textbox_contract_mdn = (Textbox) this.getFellow("textbox_contract_mdn"); //聯絡人手機號碼
        String addvalue_mdn = textbox_mdn.getValue();
        String contract_mdn = textbox_contract_mdn.getValue();

        String message = "";

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
        SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
        SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);

        Captcha chkcaptcha = (Captcha) getSpaceOwner().getFellow("cpa");
        Textbox chkcode = (Textbox) this.getFellow("chkcode");

//        Textbox textbox_ser_num = (Textbox) this.getFellow("textbox_ser_num");
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

        //2018/6/5 begin
        boolean pincodeflag = false;

        if (!"".equals(addvalue_mdn) && addvalue_mdn.length() == 10) {

            if (addvalue_mdn.equals(contract_mdn)) {
                //do nothing
                //// 如果門號都相同，沒有改變，則golbal variable都依樣
                log.info("MDN are same ==>" + addvalue_mdn + "," + contract_mdn);
                pincodeflag = true;
            } else {
                // 如果不相同，要做儲值條件判斷,且要改變global variable變數
                log.info("MDN are Diff ==>" + addvalue_mdn + "," + contract_mdn);

                SOAReqBean apirequestbean;
                SoaProfile soa = new SoaProfile();
                String result = soa.putSoaProxyletByMDN(addvalue_mdn);
                apirequestbean = soa.parseXMLString(result);

                String tmp_resultcode = apirequestbean.getResultcode();
                String tmp_contractid = apirequestbean.getContractid();
                String tmp_contractstatuscode = apirequestbean.getContract_status_code();
                String tmp_producttype = apirequestbean.getProducttype();
                String tmp_promotioncode = apirequestbean.getPromotioncode();

                log.info("SOA Result Code ===>" + tmp_resultcode);

                if ("00000000".equals(tmp_resultcode)) {
                    EPAY_PROMOTIONCODE tmp_epaypromotioncode;// = new EPAY_PROMOTIONCODE();
                    tmp_epaypromotioncode = epaybusinesscontroller.getPomtionCode(tmp_promotioncode);
                    if (tmp_epaypromotioncode != null) {
                        log.info("Platform Type =====>" + tmp_epaypromotioncode.getPlatformtype());
                        if ((tmp_contractstatuscode.equals("9") || tmp_contractstatuscode.equals("43"))
                                        && (tmp_epaypromotioncode.getPlatformtype() == 1
                                        || tmp_epaypromotioncode.getPlatformtype() == 2
                                        || tmp_epaypromotioncode.getPlatformtype() == 3)) {
//                        if (tmp_contractstatuscode.equals("9") || tmp_contractstatuscode.equals("43"))  {

                            log.info(addvalue_mdn + " contractstatuscode==>" + tmp_contractstatuscode);
                            log.info(addvalue_mdn + " epay_promotioncode.getPlatformtype()===>" + tmp_epaypromotioncode.getPlatformtype());

                            log.info("adj promotioncode===>" + promotioncode + "," + tmp_promotioncode);
                            promotioncode = tmp_promotioncode;

                            log.info("adj contractid======>" + contractid + "," + tmp_contractid);
                            contractid = tmp_contractid;

                            log.info("adj mdn=============>" + mdn + "," + addvalue_mdn);
                            mdn = addvalue_mdn;

                            pincodeflag = true;

                        } else {
                            log.info(addvalue_mdn + " Contract Status Check : " + tmp_contractstatuscode);
                            this.detach();
                            Sessions.getCurrent().setAttribute("status", tmp_contractstatuscode);
                            Executions.sendRedirect("/deposit/OnlineUserErrorMsg.zhtml");
                        }
                    } else {
                        log.info(addvalue_mdn + " promotion code Check : " + tmp_promotioncode);
                        this.detach();
                        Sessions.getCurrent().setAttribute("promotioncode", tmp_promotioncode);
                        Executions.sendRedirect("/deposit/OnlinePromotionCodeErrorMsg.zhtml");
                    }
                } else {
                    log.info(addvalue_mdn + " Result code Check : " + tmp_resultcode);
                    this.detach();
                    Sessions.getCurrent().setAttribute("promotioncode", tmp_promotioncode);
                    Executions.sendRedirect("/deposit/OnlinePromotionCodeErrorMsg.zhtml");
                }
            }

            if (pincodeflag) {
                // 2018/6/5 end
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
//                trans.setItemunitprice(Integer.parseInt(itemUnitPrice));//0
                        trans.setItemquantity(tradeQuantity);//1
                        trans.setOrdertotal(Integer.parseInt(orderTotal));//0
                        trans.setFee(0);
                        trans.setDiscount(0);
                        trans.setTradedate(sdf.parse(tradeDate));
                        trans.setPaytime(sdf.parse(tradeDate));
                        trans.setPaymethod(ShareParm.PAYMETHOD_PINCODE); //付款方式 PinCode=4
                        trans.setStatus("N"); //OCS尚未儲值完成

//                trans.setPaystatus(""); //0:失敗(default value), 但PinCode實體卡已繳費完成，所以狀態為1
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
                        trans.setPlatformtype(platformtype);

                        //20170713
                        trans.setApisrcid("1");

                        //20180614
                        trans.setLoginmdn(contract_mdn);

                        log.info("PinCodeProce(insert Table)==>MDN:" + mdn + ",Libm:" + libm);

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
                                log.info("PinCodeProce(MDN:" + mdn + " OCS ResultCode)==>" + resultcode);
                                message = message + "\n" + "PinCodeProce(MDN:" + mdn + " OCS ResultCode)==>" + resultcode;

                                boolean brst = epaybusinesscontroller.updateTransaction(trans);
                                log.info("PinCodeProce(MDN:" + mdn + " update Table status is " + brst + ")==>resultcode:" + resultcode);
                                message = message + "\n" + "PinCodeProce(MDN:" + mdn + " update Table status is " + brst + ")==>resultcode:" + resultcode;

                            } else {
                                log.info("Admin Exception===>THE PINCODE Result From 4G OCS IS NULL!!!!!");
                                message = message + "\n" + "Admin Exception===>THE PINCODE Result From 4G OCS IS NULL!!!!!";

                            }
                        } else if (epay_promotioncode.getPlatformtype() == 2) { // ZTE

                            ZTEPinCodeUtil ztePincode = new ZTEPinCodeUtil();
                            String result = ztePincode.putPincodeOCSlet(libm, contactCellPhone, pincode);
                            log.info("ZTE PinCodeProce(MDN:" + mdn + " ZTE OCS XML Resonse)==>" + result);

                            if (result != null) {

                                if (!result.contains("Fault")) {
                                    PinCodeResultBean apirequestbean = new PinCodeResultBean();
                                    apirequestbean = ztePincode.parseZTEPinCodeXMLString(result);

                                    String zteresultcode = apirequestbean.getReturncode();
                                    String zteresult_desc = "";

                                    int amt = 0;
                                    if (zteresultcode.equals("0000")) {
                                        resultcode = "00";
                                        amt = Integer.valueOf(apirequestbean.getChargemoney());
                                        trans.setItemunitprice(amt);
                                        trans.setPayamount(amt);
                                        trans.setOrdertotal(amt);
                                        zteresult_desc = "儲值成功";
                                        trans.setErrdesc(zteresult_desc);
                                    } else {
                                        resultcode = zteresultcode;
                                        zteresult_desc = "儲值失敗:" + apirequestbean.getDesc();
                                        trans.setErrdesc(zteresult_desc);
                                        trans.setErrcode("-1");
                                    }

                                    log.info("zteresultcode====>" + zteresultcode);
                                    log.info("zteresult_desc====>" + zteresult_desc);

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
                            //message = message + "\n" + "PinCodeProce(MDN:" + mdn + " NOKIA OCS ResultCode)==>" + resultcode;

                            boolean brst = epaybusinesscontroller.updateTransaction(trans);
                            log.info("NOKIA PinCodeProce(MDN:" + mdn + " update Table status is " + brst + ")==>resultcode:" + resultcode);
                            //message = message + "\n" + "PinCodeProce(MDN:" + mdn + " update Table status is " + brst + ")==>resultcode:" + resultcode;

                        } else {
                            log.info("Platformtype Error==>" + epay_promotioncode.getPlatformtype());
                            this.detach();
                            Executions.sendRedirect("/deposit/OnlinePinCodeErrorMsg.zhtml");
                        }
                    } catch (Exception ex) {
                        log.info(ex);
                    }

                    if (resultcode.equals("00")) {
                        //Success
                        this.detach();
                        Executions.sendRedirect("/deposit/OnlinePinCodeMsg.zhtml");
//                Window window = (Window) Executions.createComponents("/deposit/OnlinePinCodeMsg.zhtml", null, null);
                    } else {
                        //send alertmail & alerSMS
                        this.detach();
//                Window window = (Window) Executions.createComponents("/deposit/OnlinePinCodeErrorMsg.zhtml", null, null);
                        Executions.sendRedirect("/deposit/OnlinePinCodeErrorMsg.zhtml");
                    }
                } else {
                    Messagebox.show("請輸入正確的檢核碼", "亞太電信", Messagebox.OK, Messagebox.ERROR);
                    chkcaptcha.randomValue();
                    chkcode.setValue("");
                    chkcode.setFocus(true);
                }
            } else {

            }
        } else {
            Messagebox.show("請輸入正確的儲值號碼", "亞太電信", Messagebox.OK, Messagebox.ERROR);
        }
    }

    public void sendOrder() throws Exception {

//        Button sendBtn = (Button) this.getFellow("sendPinCodeBtn");
//        sendBtn.setDisabled(true);
//        log.info("sendPinCodeBtn.setDisabled(true)");
        Textbox textbox_mdn = (Textbox) this.getFellow("textbox_mdn"); //聯絡人手機號碼
        Textbox textbox_contract_mdn = (Textbox) this.getFellow("textbox_contract_mdn"); //聯絡人手機號碼
        String addvalue_mdn = textbox_mdn.getValue();
        String contract_mdn = textbox_contract_mdn.getValue();

        if (addvalue_mdn.equals(contract_mdn)) {
            doOrder();
        } else {
            Messagebox.show("請再次確認您將儲值之4G預付卡門號，以免因輸入錯誤，無法使用。", "亞太電信",
                            Messagebox.OK | Messagebox.CANCEL, Messagebox.EXCLAMATION,
                            new org.zkoss.zk.ui.event.EventListener() {
                public void onEvent(Event e) throws Exception {
                    if (Messagebox.ON_OK.equals(e.getName())) {
                        log.info("sendOrder ON_OK");
                        doOrder();
                        //OK is clicked
                    } else if (Messagebox.ON_CANCEL.equals(e.getName())) {
                        //Cancel is clicked
                        log.info("sendOrder ON_CANCEL");
                    }
                }
            }
            );
        }

    }

    public void sendURL() throws Exception {
        String tmpurl = new ShareParm().PARM_VAS_URL;
        if (tmpurl != null) {

        } else {
            log.info("PARM_VAS_URL URL IS NULL");
            tmpurl = "http://epaytde.aptg.com.tw/EPAY/deposit/OnlineVASDeposit.zhtml";
        }
        log.info("PARM_VAS_URL URL==>" + tmpurl);
        Executions.getCurrent().sendRedirect(tmpurl);
    }
}
