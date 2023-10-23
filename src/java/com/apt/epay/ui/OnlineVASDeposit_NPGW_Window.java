/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui;

import com.apt.epay.beans.BasicInfoReqBean;
import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.nokia.util.NokiaHLChangeLifeCycleUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.epay.zte.util.ZTEOCS4GBasicInfoUtil;
import com.apt.util.HttpClientUtil;
import com.apt.util.OCS4GBasicInfoUtil;
import com.apt.util.SecuredMsg;
import com.apt.util.SoaProfile;
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_INVOICE;
import com.epay.ejb.bean.EPAY_PROMOTIONCODE;
import com.epay.ejb.bean.EPAY_SERVICE_INFO;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Captcha;
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
public class OnlineVASDeposit_NPGW_Window extends Window {

//    private static final long serialVersionUID = -84547354457720L;
    private static final Logger log = Logger.getLogger("EPAY");

    private String cpid = new ShareParm().PARM_EPAY_CPID;
    private String libm = "";
    private String contractid;
    private String mdn;
    private String contractstatuscode;
    private String promotioncode;
    private String producttype;
    private String email;
    private String name;
    private String address;

    private Key key = null;
    private String returnUrl = "http://localhost:8080/cpCreditTest/cpCreditCardResp.jsp";
//    private String[] ch_serviceid = new String[100];
    private String[] radio_serviceid = new String[100];
    private int platformtype;
//    private OnlineDepositBean odBean;

    public void onCreate() throws Exception {

        Sessions.getCurrent().removeAttribute("odBean");
        log.info("Creating Online Deposit...from IP " + Executions.getCurrent().getRemoteAddr());

        String uuid = "";
        uuid = ((HttpServletRequest) Executions.getCurrent().getNativeRequest()).getRemoteUser();
        log.info("VASProces(init)==>IP:" + Executions.getCurrent().getRemoteAddr() + ",UUID:" + uuid);

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        Captcha chkcaptcha = (Captcha) getSpaceOwner().getFellow("cpa");
        Textbox chkcode = (Textbox) this.getFellow("chkcode");
        Textbox textbox_mdn = (Textbox) this.getFellow("textbox_mdn");
//        Combobox combo_serviceid = (Combobox) this.getFellow("combo_serviceid");
        Radiogroup radio_service_type = (Radiogroup) this.getFellow("radio_service_type");
        Vbox radio_servicevbox = (Vbox) this.getFellow("radio_servicevbox");

        try {
            if (uuid != null) {

                SoaProfile soa = new SoaProfile();
                String result = soa.putSoaProxylet(uuid);
                SOAReqBean apirequestbean = new SOAReqBean();
                apirequestbean = soa.parseXMLString(result);

                log.info("kkflag==>VASDeposit:" + result);

                contractid = apirequestbean.getContractid();
                contractstatuscode = apirequestbean.getContract_status_code();
                producttype = apirequestbean.getProducttype();
                promotioncode = apirequestbean.getPromotioncode();
                mdn = apirequestbean.getMdn();
                email = apirequestbean.getEmail();
                name = apirequestbean.getName();
                address = apirequestbean.getAddress();
                log.info("address=========>" + address);

                textbox_mdn.setValue(mdn);

                if (name.length() > 10) {
                    name = name.substring(0, 9);
                }

                log.info("promotioncode==>" + promotioncode);

//                AdjustAccUtil adaccutil = new AdjustAccUtil();
//                int cardtype = adaccutil.GetMdnCardType(mdn);
//                boolean verifyUserStatus = adaccutil.VerifyUserStatus(contractstatuscode, promotioncode);
                // kk 20160406 LifeCycle Value
                SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);

                EPAY_PROMOTIONCODE epaypromotioncode = new EPAY_PROMOTIONCODE();
                epaypromotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);

                if (epaypromotioncode != null) {

                    platformtype = epaypromotioncode.getPlatformtype();
                    log.info(mdn + " platformtype IS " + platformtype);

                    BasicInfoReqBean basicinforeqbean = new BasicInfoReqBean();

                    //取得儲值卡資訊
                    if (platformtype == 1) {
                        //ALU
                        Calendar nowDateTime = Calendar.getInstance();
                        String libm = sdf15.format(nowDateTime.getTime());
                        OCS4GBasicInfoUtil basicinfoutil = new OCS4GBasicInfoUtil();
                        String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());
                        String basicinfo = basicinfoutil.putOCS4GBasicInfoSlet(libm, mdn, tradeDate_Pincode);
                        log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfo);
                        basicinforeqbean = basicinfoutil.parseBasicInfoXMLString(basicinfo);
                        log.info("basicinforeqbean.getLifeCycleState()====>" + basicinforeqbean.getLifeCycleState());
                    } else if (platformtype == 2) {
                        //ZTE
                        ZTEOCS4GBasicInfoUtil ztebasicinfoutil = new ZTEOCS4GBasicInfoUtil();
                        String basicinfo = ztebasicinfoutil.putZTEOCS4GBasicInfoSlet(libm, mdn);
                        log.info("ZTE QueryBasicInfo===>MDN:" + mdn + " ZTE OCSXmlResult=>" + basicinfo);

                        basicinforeqbean = ztebasicinfoutil.parseZTEBasicInfoXMLString(basicinfo);
                        log.info("ZTE basicinforeqbean.getLifeCycleState()====>" + basicinforeqbean.getLifeCycleState());
                    } else if (platformtype == 3) {
                        //KK NOKIA
//                        NokiaHLChangeLifeCycleUtil util = new NokiaHLChangeLifeCycleUtil();
                        
                    } else {

                        log.info(mdn + " platformtype IS " + platformtype);
                        this.detach();
                        Executions.sendRedirect("/deposit/OnlineSSOErrorMsg.zhtml");
                    }

//                if (((producttype.equals("P4G")) || (producttype.equals("D3G"))) && basicinforeqbean.getLifeCycleState().equals("ACTIVE")) {
                    // if (basicinforeqbean.getLifeCycleState().equalsIgnoreCase("ACTIVE")) {
                    log.info(mdn + " platformtype===>" + platformtype);
                    log.info(mdn + " basicinforeqbean.getLifeCycleState()==>" + basicinforeqbean.getLifeCycleState());
                    log.info(mdn + " contractstatuscode==>" + contractstatuscode);

                    if (("ACTIVE".equalsIgnoreCase(basicinforeqbean.getLifeCycleState())) || ("CED1".equalsIgnoreCase(basicinforeqbean.getLifeCycleState())) || ("CED10".equalsIgnoreCase(basicinforeqbean.getLifeCycleState())) || ("CED3".equalsIgnoreCase(basicinforeqbean.getLifeCycleState()))) {

                        List serviceinfo1 = null;
                        serviceinfo1 = epaybusinesscontroller.listAllServiceInfo(Integer.valueOf(cpid), promotioncode, platformtype);
                        log.info("PromotionCode ===>" + promotioncode);

                        Iterator itserviceinfo1 = serviceinfo1.iterator();
                        int j = 0;
                        while (itserviceinfo1.hasNext()) {

                            EPAY_SERVICE_INFO serid = (EPAY_SERVICE_INFO) itserviceinfo1.next();
                            Radio serviceid_radio = new Radio();
                            serviceid_radio.setId(String.valueOf(serid.getServiceId()));
                            serviceid_radio.setLabel(serid.getServiceName());
                            serviceid_radio.setStyle("\"font-family: '微軟正黑體';font-size: 20px;\"");
                            serviceid_radio.setParent(radio_servicevbox);

                            Label label = new Label();
                            label.setValue("　" + serid.getNote());
                            label.setParent(radio_servicevbox);

                            log.info("Radio--->" + serid.getServiceName());
                            radio_serviceid[j] = String.valueOf(serid.getServiceId());
                            j++;
                        }
                        radio_service_type.setSelectedIndex(0);

                        if ((apirequestbean.getPromotioncode() != null) || (apirequestbean.getPromotioncode() != "")) {
                            promotioncode = apirequestbean.getPromotioncode().substring(0, 3);
                        } else {
                            log.info("Exception==> The REAL promotioncode is" + apirequestbean.getPromotioncode());
                        }

                        log.info("VASProce(SOA Result)==>IP:" + Executions.getCurrent().getRemoteAddr()
                                        + ",MDN:" + mdn
                                        + ",ContractID:" + contractid
                                        + ",Contract_status:" + contractstatuscode
                                        + ",producttype:" + producttype
                                        + ",promotioncode:" + promotioncode);

                        if ((contractstatuscode.equals("9") || contractstatuscode.equals("43")) && (platformtype == 1 || platformtype == 2)) {
                            //DO NOTHING                        
                            log.info("contractstatuscode==>" + contractstatuscode);
                            log.info("epay_promotioncode.getPlatformtype()===>" + platformtype);

                        } else {
                            log.info(mdn + " Contract Status Check : " + contractstatuscode);
                            this.detach();
                            Sessions.getCurrent().setAttribute("status", contractstatuscode);
                            Executions.sendRedirect("/deposit/OnlineUserErrorMsg.zhtml");
                        }
//                    } else {
//                        log.info(mdn + " check9400COSPID IS false");
//                        this.detach();
//                        Executions.sendRedirect("/deposit/OnlineSSOErrorMsg.zhtml");
//                    }
                    } else {
//                        log.info(mdn + " Product Type IS " + producttype);
//                        this.detach();
//                        Executions.sendRedirect("/deposit/OnlineSSOErrorMsg.zhtml");

                        //您的有效期限已逾期,請盡速儲值或洽客服協助
                        log.info(mdn + " 您的有效期限已逾期,請盡速儲值或洽客服協助==>" + basicinforeqbean.getLifeCycleState());
                        this.detach();
                        Executions.sendRedirect("/deposit/OnlineLifeCycleStateErrorMsg.zhtml");
                    }
                } else {
                    // no promotioncode data in table
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

    public void sendCreditCardOrder() throws Exception {

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
        SimpleDateFormat sdf8 = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT8);
        SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
        SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);

        Captcha chkcaptcha = (Captcha) getSpaceOwner().getFellow("cpa");
        Textbox chkcode = (Textbox) this.getFellow("chkcode");

//        Combobox combo_serviceid = (Combobox) this.getFellow("combo_serviceid");
//        String serviceid = combo_serviceid.getSelectedItem().getId().replaceAll("combo", "");
//        String serviceid = ch_serviceid[combo_serviceid.getSelectedIndex()];
        Radiogroup radio_service_type = (Radiogroup) this.getFellow("radio_service_type");
        String xserviceid = radio_serviceid[radio_service_type.getSelectedIndex()];
        //
        log.info("The Radio serviceid ====>" + radio_service_type.getId());
        log.info("The Radio serviceid ====>" + radio_service_type.getSelectedIndex());
        log.info("The Radio serviceid ====>" + radio_serviceid[radio_service_type.getSelectedIndex()]);
        //

        EPAY_SERVICE_INFO serviceinfo = epaybusinesscontroller.getServiceInfoById(Long.valueOf(xserviceid), Integer.valueOf(cpid));
        EPAY_CP_INFO cpinfo = epaybusinesscontroller.getCpInfo(Integer.valueOf(cpid));
        String cpname = cpinfo.getCpName();

        String itemName = serviceinfo.getServiceName();
        String itemUnitPrice = serviceinfo.getPrice().toString(); //虛擬卡實際金額(Price)
        String itemCode = serviceinfo.getGlcode();

        String contactCellPhone = mdn;//聯絡手機號碼
        String receiptType = "2";//無發票資訊(二聯三聯)
        int tradeAmount = Integer.valueOf(itemUnitPrice);
        int tradeQuantity = 1;
        String receiptTitle = ""; //無發票資訊(發票抬頭)
        String vatNo = "";//無發票資訊(統編)
        String receiptAddress = address;//無發票資訊(地址)
        String contactPhone = mdn;//無發票資訊(聯絡電話)
//        String receiptTitle = "APT"; //無發票資訊(發票抬頭)
//        String vatNo = "28020674";//無發票資訊(統編)
//        String receiptAddress = "台北市南港區經貿二路66號12樓";//無發票資訊(地址)
//        String contactPhone = "02-55558888";//無發票資訊(聯絡電話) 
        String contactEmail = email;//無發票資訊(電郵)
        String contactName = name;//無發票資訊(聯絡人)        

        String orderTotal = String.valueOf(tradeAmount * tradeQuantity);
        Calendar nowDateTime = Calendar.getInstance();
        String tradeDate = sdf.format(nowDateTime.getTime());
        String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());

//        if(itemName.equals("") || itemName == null){
        if (chkcode.getValue() != null && chkcode.getValue().equals(chkcaptcha.getValue())) {

            // 產生訂單編號 yymmddHHmissSSS
            libm = sdf15.format(nowDateTime.getTime());
//            odBean.setLibm(libm);

            //記錄和比對是否已有訂單            
            EPAY_TRANSACTION trans = epaybusinesscontroller.getTransaction(libm);

            if (trans == null && !"".equalsIgnoreCase(libm) && !"null".equalsIgnoreCase(libm)) {
                //如果沒有編號,直接insert新的
                trans = new EPAY_TRANSACTION();
                trans.setLibm(libm);
                trans.setItemcode(itemCode);//PinCode
                trans.setItemproductname(itemName);//虛擬卡片儲值
                trans.setItemunitprice(Integer.parseInt(itemUnitPrice));//0
                trans.setItemquantity(tradeQuantity);//1
                trans.setOrdertotal(Integer.parseInt(orderTotal));//0
                trans.setFee(0);
                trans.setDiscount(0);
                trans.setTradedate(sdf.parse(tradeDate));
                trans.setPaymethod(ShareParm.PAYMETHOD_CREDITCARD); //付款方式 信用卡:value = 1 
                trans.setStatus("N"); //OCS尚未儲值完成
                trans.setPaystatus(0); //0:(default value) 尚未繳費成功
                trans.setPayamount(Integer.parseInt(orderTotal));
                trans.setPrivatedata(libm); //PinCode number

                trans.setServiceId(xserviceid);//
                trans.setCpLibm(libm);
                trans.setCpId(Integer.parseInt(new ShareParm().PARM_EPAY_CPID));//
                trans.setCpName(cpname);
                trans.setFeeType("0"); //無拆帳需求
                trans.setInvoiceContactMobilePhone(contactCellPhone);
                trans.setContractID(contractid);
                trans.setPlatformtype(platformtype);

                log.info("VASProce(insert EPAY_TRANSACTION Table)==>MDN:" + mdn + ",Libm:" + libm);

                epaybusinesscontroller.insertTransaction(trans);

                EPAY_INVOICE inv = new EPAY_INVOICE();
                inv.setLibm(libm);
                inv.setStatus("N"); //交易狀態 先設立失敗 因為還未開立
                inv.setInvoicetype(0); //繳款狀態 先設立失敗 因為還未開立
                inv.setInvoicetitle(receiptTitle);
                inv.setUniform(vatNo);
                inv.setInvoiceaddress(receiptAddress);
                inv.setInvoicecontact(contactName);
                inv.setInvoicecontacttel(contactPhone);
                inv.setInvoicecontactmobilephone(contactCellPhone);
                inv.setInvoicecontactemail(contactEmail);
                inv.setCpLibm(libm);
                log.info("VASProce(insert EPAY_INVOICE Table)==>MDN:" + mdn + ",Libm:" + libm);

                epaybusinesscontroller.insertInvoice(inv);
                /*
                 發送至NPGW平台                
                 */
////                String sendURL = "http://cpaytde.aptg.com.tw/CPECF/npgw/OnlineDeposit_NPGW.zul";
//                log.info("CPECF_URL===>" + sendURL);
                StringBuffer originalParm = new StringBuffer();
                originalParm.append("libm=").append(libm).append("&")
                                .append("serviceid=").append(xserviceid).append("&")
                                .append("serviceName=").append(itemName).append("&")
                                .append("txType=").append("1").append("&")
                                .append("orderTotal=").append(orderTotal).append("&")
                                .append("tradeDate=").append(tradeDate).append("&")
                                .append("callerResUrl=").append(returnUrl).append("&")
                                .append("invoiceType=").append(receiptType).append("&")
                                .append("invoiceTitle=").append(receiptTitle).append("&")
                                .append("uniform=").append(vatNo).append("&")
                                .append("invoiceAddress=").append(receiptAddress).append("&")
                                .append("invoiceContact=").append(contactName).append("&")
                                .append("invoiceContactTel=").append(contactPhone).append("&")
                                .append("invoiceContactMobilePhone=").append(contactCellPhone).append("&")
                                .append("invoiceContactEmail=").append(contactEmail).append("&")                                
                                .append("privateData=").append(libm).append("&")
//                                .append("bankID=").append("812").append("&")
                                .append("callerInMac=").append("38dc7020a508b59ba409615273495ab5").append("&")
                                .append("isMd5Match=").append("true");

                log.info("originalParm==>" + originalParm);
                HttpClientUtil httpx = new HttpClientUtil();
                NameValuePair[] requestBody = null;
                String data = (new SecuredMsg(new ShareParm().PARM_PG_KEY, new ShareParm().PARM_PG_IDENT).encode(originalParm.toString())).toString();
                postToNextPage(cpid, data);
//                String vas = httpx.sendVASPostMsg(cpid, sendURL, originalParm);
//
//                log.info("CPECF Result===>" + vas);
//                Executions.getCurrent().sendRedirect("../../XSMSAP/ExcelReport?is_report=4");                

            } else {
                //如果有編號,檢查是否已經交易, 如果還未交易可在更新資料。   
            }

//            this.detach();
//            Window window = (Window) Executions.createComponents("/deposit/OnlinePinCodeMsg.zul", null, null);
        } else {
            Messagebox.show("請輸入正確的檢核碼", "亞太電信", Messagebox.OK, Messagebox.ERROR);
            chkcaptcha.randomValue();
            chkcode.setValue("");
            chkcode.setFocus(true);
        }
//        }else{
//             Messagebox.show("請選擇儲值方案", "亞太電信", Messagebox.OK, Messagebox.ERROR);           
//        }

    }

//    public boolean check9400COSPID(String mdn) {
//        boolean result = false;
//        SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);
//        SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
//        Calendar nowDateTime = Calendar.getInstance();
//
//        String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());
//        OCS4GBasicInfoUtil basicinfoutil = new OCS4GBasicInfoUtil();
//        String libm = sdf15.format(nowDateTime.getTime());
//        try {
//            String basicinfo = basicinfoutil.putOCS4GBasicInfoSlet(libm, mdn, tradeDate_Pincode);
//            BasicInfoReqBean basicinforeqbean = new BasicInfoReqBean();
//            basicinforeqbean = basicinfoutil.parseBasicInfoXMLString(basicinfo);
//            log.info("basicinforeqbean.getDefaultTariffPlanCOSPID()====>" + basicinforeqbean.getDefaultTariffPlanCOSPID());
//            if (basicinforeqbean.getDefaultTariffPlanCOSPID().equals("9400")) {
//                result = true;
//            } else {
//                result = false;
//            }
//        } catch (Exception ex) {
//            log.info(ex);
//        }
//        return result;
//    }
    public void sendATMOrder() throws Exception {
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
        SimpleDateFormat sdf8 = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT8);
        SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
        SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);

        Captcha chkcaptcha = (Captcha) getSpaceOwner().getFellow("cpa");
        Textbox chkcode = (Textbox) this.getFellow("chkcode");

//        Combobox combo_serviceid = (Combobox) this.getFellow("combo_serviceid");
//        String serviceid = combo_serviceid.getSelectedItem().getId().replaceAll("combo", "");
        Radiogroup radio_service_type = (Radiogroup) this.getFellow("radio_service_type");
        String serviceid = radio_serviceid[radio_service_type.getSelectedIndex()];
        //
        log.info("The Radio serviceid ====>" + radio_service_type.getId());
        log.info("The Radio serviceid ====>" + radio_service_type.getSelectedIndex());
        log.info("The Radio serviceid ====>" + radio_serviceid[radio_service_type.getSelectedIndex()]);

        EPAY_SERVICE_INFO serviceinfo = epaybusinesscontroller.getServiceInfoById(Long.valueOf(serviceid), Integer.valueOf(cpid));
        log.info("epaybusinesscontroller.getServiceInfoById(Long.valueOf(serviceid), Integer.valueOf(cpid))==>" + serviceid + "," + cpid);
        String itemName = serviceinfo.getServiceName();
        String itemUnitPrice = serviceinfo.getPrice().toString();
        String itemCode = serviceinfo.getGlcode();

//        String itemCode = ShareParm.VAS_CREDITCARD_ITEMCODE; //ATM
//        String itemName = ShareParm.VAS_ITEMNAME; //虛擬卡片儲值
//        String itemUnitPrice = textbox_cost.getValue(); //虛擬卡實際金額(Price)
        String contactCellPhone = mdn;//聯絡手機號碼

        String receiptType = "2";//無發票資訊(二聯三聯)
        int tradeAmount = Integer.valueOf(itemUnitPrice);
        int tradeQuantity = 1;
        String receiptTitle = ""; //無發票資訊(發票抬頭)
        String vatNo = "";//無發票資訊(統編)
        String receiptAddress = address;//無發票資訊(地址)
        String contactPhone = mdn;//無發票資訊(聯絡電話)
//        String receiptTitle = "APT"; //無發票資訊(發票抬頭)
//        String vatNo = "28020674";//無發票資訊(統編)
//        String receiptAddress = "台北市南港區經貿二路66號12樓";//無發票資訊(地址)
//        String contactPhone = "02-55558888";//無發票資訊(聯絡電話)        

        String contactName = name;//無發票資訊(聯絡人)
        String contactEmail = email;//無發票資訊(電郵)

        String orderTotal = String.valueOf(tradeAmount * tradeQuantity);
        Calendar nowDateTime = Calendar.getInstance();
        String tradeDate = sdf.format(nowDateTime.getTime());
        String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());

        if (chkcode.getValue() != null && chkcode.getValue().equals(chkcaptcha.getValue())) {

            // 產生訂單編號 yymmddHHmissSSS
            libm = sdf15.format(nowDateTime.getTime());
//            odBean.setLibm(libm);

            //記錄和比對是否已有訂單            
            EPAY_TRANSACTION trans = epaybusinesscontroller.getTransaction(libm);
            log.info("epaybusinesscontroller.getTransaction(libm)==>" + libm);

            if (trans == null && !"".equalsIgnoreCase(libm) && !"null".equalsIgnoreCase(libm)) {
                //如果沒有編號,直接insert新的
                trans = new EPAY_TRANSACTION();
                trans.setLibm(libm);
                trans.setItemcode(itemCode);//PinCode
                trans.setItemproductname(itemName);//虛擬卡片儲值
                trans.setItemunitprice(Integer.parseInt(itemUnitPrice));//0
                trans.setItemquantity(tradeQuantity);//1
                trans.setOrdertotal(Integer.parseInt(orderTotal));//0
                trans.setFee(0);
                trans.setDiscount(0);
                trans.setTradedate(sdf.parse(tradeDate));
                trans.setPaymethod(ShareParm.PAYMETHOD_ATM); //付款方式 信用卡:value = 1 
                trans.setStatus("N"); //OCS尚未儲值完成
                trans.setPaystatus(0); //0:(default value) 尚未繳費成功
                trans.setPayamount(Integer.parseInt(orderTotal));//0

                trans.setPrivatedata(libm); //PinCode number

                trans.setServiceId(serviceid);//
                trans.setCpLibm(libm);
                trans.setCpId(Integer.parseInt(new ShareParm().PARM_EPAY_CPID));//
                trans.setFeeType("0"); //無拆帳需求
                trans.setInvoiceContactMobilePhone(contactCellPhone);
                trans.setContractID(contractid);

                log.info("VASProce(insert EPAY_TRANSACTION Table)==>MDN:" + mdn + ",Libm:" + libm);

                epaybusinesscontroller.insertTransaction(trans);

                EPAY_INVOICE inv = new EPAY_INVOICE();
                inv.setLibm(libm);
                inv.setStatus("N"); //交易狀態 先設立失敗 因為還未開立
                inv.setInvoicetype(0); //繳款狀態 先設立失敗 因為還未開立
                inv.setInvoicetitle(receiptTitle);
                inv.setUniform(vatNo);
                inv.setInvoiceaddress(receiptAddress);
                inv.setInvoicecontact(contactName);
                inv.setInvoicecontacttel(contactPhone);
                inv.setInvoicecontactmobilephone(contactCellPhone);
                inv.setInvoicecontactemail(contactEmail);
                inv.setCpLibm(libm);
                log.info("VASProce(insert EPAY_INVOICE Table)==>MDN:" + mdn + ",Libm:" + libm);

                epaybusinesscontroller.insertInvoice(inv);

                /*
                 發送至CP金流平台                
                 */
//                String sendURL = ShareParm.PARM_CPECF_URL;
//                String sendURL = new ShareParm().PARM_ECF_URL;
                StringBuffer originalParm = new StringBuffer();
                originalParm.append("libm=").append(libm).append("&")
                                .append("serviceid=").append(serviceid).append("&")
                                .append("serviceName=").append(itemName).append("&")
                                .append("txType=").append("2").append("&")
                                .append("orderTotal=").append(orderTotal).append("&")
                                .append("tradeDate=").append(tradeDate).append("&")
                                .append("callerResUrl=").append(returnUrl).append("&")
                                .append("invoiceType=").append(receiptType).append("&")
                                .append("invoiceTitle=").append(receiptTitle).append("&")
                                .append("uniform=").append(vatNo).append("&")
                                .append("invoiceAddress=").append(receiptAddress).append("&")
                                .append("invoiceContact=").append(contactName).append("&")
                                .append("invoiceContactTel=").append(contactPhone).append("&")
                                .append("invoiceContactMobilePhone=").append(contactCellPhone).append("&")
                                .append("invoiceContactEmail=").append(contactEmail).append("&")
                                .append("privateData=").append(libm).append("&")
                                .append("callerInMac=").append("38dc7020a508b59ba409615273495ab5").append("&")
                                .append("isMd5Match=").append("true");

                log.info("originalParm==>" + originalParm);
                HttpClientUtil httpx = new HttpClientUtil();
                NameValuePair[] requestBody = null;
                String data = (new SecuredMsg(new ShareParm().PARM_PG_KEY, new ShareParm().PARM_PG_IDENT).encode(originalParm.toString())).toString();
                postToNextPage(cpid, data);
//                String vas = httpx.sendVASPostMsg(cpid, sendURL, originalParm);
//
//                log.info("CPECF Result===>" + vas);
//                Executions.getCurrent().sendRedirect("../../XSMSAP/ExcelReport?is_report=4");                

            } else {
                //如果有編號,檢查是否已經交易, 如果還未交易可在更新資料。   
            }

//            this.detach();
//            Window window = (Window) Executions.createComponents("/deposit/OnlinePinCodeMsg.zul", null, null);
        } else {
            Messagebox.show("請輸入正確的檢核碼", "亞太電信", Messagebox.OK, Messagebox.ERROR);
            chkcaptcha.randomValue();
            chkcode.setValue("");
            chkcode.setFocus(true);
        }
    }

    private void postToNextPage(String systemid, String data) throws Exception {

        Sessions.getCurrent().setAttribute("system", systemid);
        Sessions.getCurrent().setAttribute("data", data);
//        Sessions.getCurrent().setAttribute("DepositTradeType", tradeType);
//        Sessions.getCurrent().setAttribute("CallerSystem", odBean.getSystem());
//        Sessions.getCurrent().setAttribute("odBean", odBean);

//        String sendURL = ShareParm.PARM_CPECF_URL;
//        String sendURL = "http://localhost:8080/CPECF/npgw/OnlineDeposit_NPGW.zul";
        String sendURL = new ShareParm().PARM_ECF_URL;

        log.info("systemid,data,sendURL===>" + systemid + "," + data + "," + sendURL);
//        if (tradeType.equals("1")) {
        //            sendURL = new ShareParm().PARM_ATM_URL;
        //        }
        //test connection
        //        try {
        //            System.setProperty("jsse.enableSNIExtension", "false");
        //            URL obj = new URL(sendURL);
        //            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        //            con.setRequestMethod("POST");
        //            con.setRequestProperty("User-Agent", "Mozilla/5.0");
        //            con.setRequestProperty("Accept-Language", "UTF-8");
        //            con.setHostnameVerifier(new HostnameVerifier() {
        //                public boolean verify(String hostname, SSLSession session) {
        //                    return true;
        //                }
        //            });
        //            con.setDoOutput(true);
        //            con.setConnectTimeout(5000); //5 secs
        //            con.getOutputStream();
        //        } catch (SocketTimeoutException e) {
        //            Messagebox.show("無法連線PaymentGateway!", new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.ERROR);
        //            log.error("Cannot connect to PaymentGateway, Time Out!", e);
        //            return;
        //        }

        Sessions.getCurrent().setAttribute("sendURL", sendURL);
        Executions.getCurrent().setAttribute(org.zkoss.zk.ui.sys.Attributes.NO_CACHE, Boolean.TRUE);

        Window window = (Window) Executions.createComponents(
                        "/deposit/OnlineVASDepositSendECF.zul", null, null);
//        log.info("Sending data to PaymentgateWay, libm :" + odBean.getLibm());
        //pop window
//        try {
//            window.setClosable(true);
//            window.setSizable(true);
//            window.setHeight("500px");
//            window.setWidth("600px");
//            window.doModal();
//
//        } catch (SuspendNotAllowedException ex) {
//            java.util.logging.Logger.getLogger(OnlineDeposit_Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
    }

    public boolean checkUIData() {
        boolean result = false;

        return result;
    }

    public boolean checkPincodeUserStatus(String contractstatuscode, String promotioncode) {
        boolean result = false;
//        log.info("contractstatuscode==>" + contractstatuscode);
//        log.info("promotioncode=======>" + promotioncode);
        if (promotioncode != null) {
            if (contractstatuscode.equals("9") || contractstatuscode.equals("43")) {
                if (promotioncode.equals("PPP") || promotioncode.equals("P4G") || promotioncode.equals("D3G")) {
                    result = true;
                }
            } else {
                log.info("The Pincode User Check is False");
            }
        }
        return result;
    }
}
