/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.nosso;

import com.apt.epay.beans.PinCodeReqBean;
import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.deposit.bean.NOSSOReqBean;
import com.apt.epay.deposit.util.toolUtil;
import com.apt.epay.nokia.bean.NokiaPincodeResultBean;
import com.apt.epay.nokia.main.NokiaMainPincodeUtil;
//import com.apt.epay.nokia.bean.NokiaReqBean;
//import com.apt.epay.nokia.util.NokiaECGPinCodeUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.epay.zte.bean.PinCodeResultBean;
import com.apt.epay.zte.util.ZTEPinCodeUtil;
import com.apt.util.PinCodeUtil;
import com.apt.util.SoaProfile;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_PROMOTIONCODE;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import java.io.IOException;
import java.io.StringReader;
import static java.lang.Thread.sleep;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Captcha;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
// KK PINCODE
public class OnlineNoSSO_PinCodeDeposit_Window extends Window {

    private final Logger log = Logger.getLogger("EPAY");

    SOAReqBean apirequestbean;
    private String mdn1, mdn2, cplibm, salesid, storeid, storename, apisrcid;

    private String contractstatuscode;
    private String promotioncode;
    private String producttype;
    private String libm;
    private String contractid;

    public void onCreate() throws Exception {

        Sessions.getCurrent().removeAttribute("odBean");
        log.info("Creating Online Deposit...from IP " + Executions.getCurrent().getRemoteAddr());

        HttpServletRequest req = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
        log.info("doAfterCompose.getQueryString():" + req.getQueryString());

        String PROXY_FLAG = new ShareParm().PARM_SCT_PROXY_FLAG;
        

        toolUtil tool = new toolUtil();
        Utilities util = new Utilities();

        String cpid = req.getParameter("CPID");
        String data = req.getParameter("DATA");

        //debug
//        if ("TDE".equalsIgnoreCase(PROXY_FLAG)) {
//            log.info("This is ==========>" + PROXY_FLAG);
//            cpid = "10001";//kk config
//            data = "L2IhQ/10pDhoAsoshr17H1yUqbTQbHSRi3KwXAr88tba9HDBSqp83a4G9RRZO3/WsbY78dt3yPUE5RGwMhphdw1WywRNQGrl+L5GDP3um/rFbsOXJEZ2ANWMMeHw0AMCNND+G1oj9l95+zOJSbdc6JP0qpA33nf8EDMxBZIs5Qk01H3Vlqlsq3N8D3AyJrlgKXHhNKVCAPv1sY6rUaLBwm0X5dwabf/kXilBAwDz/Zu0WP0cAKda2HuqTXa5HkpN8q20kn8ZfmiDBv/h6ahGxg0NvqcOn2P0hbDnm9+nLMNtUxmf5jTTPcy7ER5eRPMfQ16fQEkKKe0=";
//        }

        log.info("cpid==>" + cpid);
        log.info("data==>" + data);

        if (cpid != null && data != null) {

            try {

                EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

                int icpid = Integer.valueOf(cpid);
                EPAY_CP_INFO cpinfo = epaybusinesscontroller.getCpInfo(icpid);
                String deskey = "";
                boolean cpkeyflag = false;

                if (cpinfo != null) {
                    deskey = cpinfo.getPoskey();
                    log.info("deskey===>" + deskey);
                    if (!"".equals(deskey)) {
                        cpkeyflag = true;
                    }
                } else {
                    //cpinfo is null
                    log.info("cannot get cpinfo & pos key");
                }

                if (cpkeyflag) {

                    String str_input = util.decrypt(deskey, data);
                    log.info("INPUT==>" + str_input);

                    //debug
//                    if ("TDE".equalsIgnoreCase(PROXY_FLAG)) {
//                        String tde_mdn = new ShareParm().PARM_TDE_MDN;
//                        log.info("This is ================>" + PROXY_FLAG);
//                        log.info("TDE mdn  is ================>" + tde_mdn);
//                        str_input = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><ServicePinCodeDeposit><MDN1>"+tde_mdn+"</MDN1><MDN2>"+tde_mdn+"</MDN2><SALESID></SALESID><STOREID></STOREID><STORENAME></STORENAME><CPLIBM>210725230853EWx</CPLIBM><APISRCID>2</APISRCID></ServicePinCodeDeposit>";
//                        log.info( PROXY_FLAG + "INPUT==>" + str_input);
//                    }

                    NOSSOReqBean aPIServiceOrdrReqBean;
                    aPIServiceOrdrReqBean = getMDNByparseXMLString(str_input, "ServicePinCodeDeposit");
                    mdn1 = aPIServiceOrdrReqBean.getMdn1();
                    mdn2 = aPIServiceOrdrReqBean.getMdn2();
                    cplibm = aPIServiceOrdrReqBean.getCplibm();
                    apisrcid = aPIServiceOrdrReqBean.getApisrcid();
                    salesid = aPIServiceOrdrReqBean.getSalesid();
                    storeid = aPIServiceOrdrReqBean.getStoreid();
                    storename = aPIServiceOrdrReqBean.getStorename();

                    Textbox textbox_mdn = (Textbox) this.getFellow("textbox_mdn");
//                Textbox textbox_contract_mdn = (Textbox) this.getFellow("textbox_contract_mdn");
                    Textbox textbox_pincode = (Textbox) this.getFellow("textbox_pincode");
//                Label label_contract = (Label) this.getFellow("label_contract");
                    Label label_mdn = (Label) this.getFellow("label_mdn");
                    Label label_mdn_type = (Label) this.getFellow("label_mdn_type");

//                label_contract.setValue("合約門號");
                    label_mdn.setValue("儲值門號");
//                textbox_contract_mdn.setValue(mdn1);

                    String mdn_promotiontype = getPromtionStr(mdn2);
                    label_mdn_type.setValue(mdn_promotiontype);

                    if (mdn2 != null && !"".equals(mdn2)) {

                        SoaProfile soa = new SoaProfile();
                        String result = soa.putSoaProxyletByMDN(mdn2);
                        apirequestbean = soa.parseXMLString(result);

                        log.info("kkflag==>NoSSO_PincodeDeposit:" + result);

                        String resultcode = apirequestbean.getResultcode();
                        contractid = apirequestbean.getContractid();
                        contractstatuscode = apirequestbean.getContract_status_code();
                        producttype = apirequestbean.getProducttype();
//                    mdn = apirequestbean.getMdn();
                        promotioncode = apirequestbean.getPromotioncode();
                        textbox_mdn.setValue(mdn2);

                        if ("00000000".equals(resultcode)) {
                            EPAY_PROMOTIONCODE epaypromotioncode;// = new EPAY_PROMOTIONCODE();
                            epaypromotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);

                            SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                            Calendar nowDateTime = Calendar.getInstance();
                            libm = sdf15.format(nowDateTime.getTime());

                            log.info("=================================>" + libm);

                            if (epaypromotioncode != null) {

                                if ((contractstatuscode.equals("9") || contractstatuscode.equals("43"))
                                                && (epaypromotioncode.getPlatformtype() == 1
                                                || epaypromotioncode.getPlatformtype() == 2
                                                || epaypromotioncode.getPlatformtype() == 3)) {

                                    log.info("contractstatuscode==>" + contractstatuscode);
                                    log.info("epay_promotioncode.getPlatformtype()===>" + epaypromotioncode.getPlatformtype());

                                } else {
                                    log.info(mdn2 + " Contract Status Check : " + contractstatuscode);
                                    this.detach();
                                    Sessions.getCurrent().setAttribute("status", contractstatuscode);
                                    Executions.sendRedirect("/nosso/OnlineUserErrorMsg.zhtml");
                                }
                            } else {
                                log.info(mdn2 + " promotion code Check : " + promotioncode);
                                this.detach();
                                Sessions.getCurrent().setAttribute("promotioncode", promotioncode);
                                Executions.sendRedirect("/nosso/OnlineNoSSOPromotionCodeErrorMsg.zhtml");
                            }
                        } else {
                            this.detach();
                            Executions.sendRedirect("/nosso/OnlineNoSSOSSOErrorMsg.zhtml");
                        }
                    } else {
                        log.info("MDN IS NULL");
                        this.detach();
                        Window window = (Window) Executions.createComponents("/nosso/OnlineNoSSOSSOErrorMsg.zhtml", null, null);
                    }
                } else {
                    // no cpid 
                }
            } catch (Exception ex) {
                log.info(ex);
            }
        } else {
            log.info("cpid ==>" + cpid + ", data ==>" + data);
            this.detach();
            Executions.sendRedirect("/nosso/OnlineNoSSOSSOErrorMsg.zhtml");
        }
    }

    public void doOrder() throws Exception {

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        String message = "";
        Textbox textbox_mdn = (Textbox) this.getFellow("textbox_mdn"); //聯絡人手機號碼
        String chk_mdn = textbox_mdn.getValue();

        SoaProfile kk_soa = new SoaProfile();
        SOAReqBean kk_apirequestbean;
        String kk_result = kk_soa.putSoaProxyletByMDN(chk_mdn);
        kk_apirequestbean = kk_soa.parseXMLString(kk_result);

        log.info("kkflag==>NoSSO_PincodeDeposit:" + kk_result);

        String kk_resultcode = kk_apirequestbean.getResultcode();
        String kk_promotioncode = kk_apirequestbean.getPromotioncode();
        String kk_contractstatuscode = kk_apirequestbean.getContract_status_code();
        String kk_contractid = kk_apirequestbean.getContractid();

        log.info("kk_resultcode==>" + kk_resultcode);
        log.info("MDN:" + chk_mdn + ",kk_promotioncode==>" + kk_promotioncode);
        log.info("MDN:" + chk_mdn + ",kk_contractstatuscode==>" + kk_contractstatuscode);

        if ("00000000".equals(kk_resultcode)) {
            EPAY_PROMOTIONCODE kk_epaypromotioncode;// = new EPAY_PROMOTIONCODE();
            kk_epaypromotioncode = epaybusinesscontroller.getPomtionCode(kk_promotioncode);

            if (kk_epaypromotioncode != null) {

                if ((kk_contractstatuscode.equals("9") || kk_contractstatuscode.equals("43"))
                                && (kk_epaypromotioncode.getPlatformtype() == 1
                                || kk_epaypromotioncode.getPlatformtype() == 2
                                || kk_epaypromotioncode.getPlatformtype() == 3)) {

                    log.info("MDN:" + chk_mdn + ",contractstatuscode==>" + kk_contractstatuscode);
                    log.info("MDN:" + chk_mdn + ",epay_promotioncode.getPlatformtype()===>" + kk_epaypromotioncode.getPlatformtype());

                    SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
//                    SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                    SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);

                    Captcha chkcaptcha = (Captcha) getSpaceOwner().getFellow("cpa");
                    Textbox chkcode = (Textbox) this.getFellow("chkcode");

                    Textbox textbox_pincode = (Textbox) this.getFellow("textbox_pincode");

                    String itemCode = ShareParm.PINCODE_ITEMCODE; //PinCode
                    String itemName = ShareParm.PINCODE_ITEMNAME; //實體卡片儲值

//                    String contactCellPhone = textbox_mdn.getValue();//聯絡手機號碼
                    String pincode = textbox_pincode.getValue(); //PinCode

                    int tradeAmount = 0;// PinCode實體卡片儲值金額為null, 我們不會知道該實體卡片實際金額(Price)
                    int tradeQuantity = 1;

                    String orderTotal = String.valueOf(tradeAmount * tradeQuantity);
                    Calendar nowDateTime = Calendar.getInstance();
                    String tradeDate = sdf.format(nowDateTime.getTime());
                    String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());
                    String resultcode = "";

                    EPAY_PROMOTIONCODE epaypromotioncode;//= new EPAY_PROMOTIONCODE();
                    epaypromotioncode = epaybusinesscontroller.getPomtionCode(kk_promotioncode);
                    int platformtype = epaypromotioncode.getPlatformtype();

                    if (chkcode.getValue() != null && chkcode.getValue().equals(chkcaptcha.getValue())) {

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

//                trans.setPaystatus(""); //0:失敗(default value), 但PinCode實體卡已繳費完成，所以狀態為1
                            trans.setPrivatedata(pincode); //PinCode number

                            trans.setServiceId(ShareParm.PINCODE_SERVICEID);//
                            trans.setCpLibm(cplibm);
                            trans.setCpId(Integer.parseInt(new ShareParm().PARM_EPAY_CPID));//

                            String cpid = new ShareParm().PARM_EPAY_CPID;
                            EPAY_CP_INFO cpinfo = epaybusinesscontroller.getCpInfo(Integer.valueOf(cpid));
                            String cpname = cpinfo.getCpName();
                            trans.setCpName(cpname);

                            trans.setFeeType("0"); //無拆帳需求
                            trans.setInvoiceContactMobilePhone(chk_mdn);
                            trans.setContractID(kk_contractid);
                            trans.setPlatformtype(platformtype);

                            //20170713
//                trans.setApisrcid(apisrcid);
//                trans.setSales_id(salesid);
//                trans.setLoginmdn(mdn1);
//                trans.setStoreid(storeid);
//                trans.setStorename(storename);
                            //20170713
                            trans.setApisrcid(apisrcid);
//                trans.setPaytool(String.valueOf(type));
                            if (storename != null) {
                                trans.setStorename(storename);
                            }

                            if (storeid != null) {
                                trans.setStoreid(storeid);
                            }

                            if (!"".equals(salesid)) {
                                trans.setSales_id(salesid);
                            }

                            if (mdn1 != null) {
                                trans.setLoginmdn(mdn1);
                            }
                            log.info("PinCodeProce(insert Table)==>MDN:" + chk_mdn + ",Libm:" + libm);

                            epaybusinesscontroller.insertTransaction(trans);

                        } else {
                            //如果有編號,檢查是否已經交易, 如果還未交易可在更新資料。   
                        }

                        //Sending Pincode/Password/MDN to OCS
                        log.info("PinCodeProce(MDN:" + chk_mdn + " SendData to OCS)==>libm:" + libm);
                        message = "PinCodeProce(MDN:" + chk_mdn + " SendData to OCS)==>libm:" + libm;

                        try {
                            //判斷是要到ALU還是ZTE做儲值
                            EPAY_PROMOTIONCODE epay_promotioncode;// = new EPAY_PROMOTIONCODE();
                            epay_promotioncode = epaybusinesscontroller.getPomtionCode(kk_promotioncode);

                            log.info("MDN:" + chk_mdn + " ===>" + epay_promotioncode.getPlatformtype() + "," + epay_promotioncode.getPromotioncode());

                            int promotioncode_number = epay_promotioncode.getPlatformtype();

                            if (promotioncode_number == 1) { //ALU

                                PinCodeUtil pincodeutil = new PinCodeUtil();
                                String result = pincodeutil.putPincodeOCSlet(libm, chk_mdn, pincode, tradeDate_Pincode);

                                log.info("PinCodeProce(MDN:" + chk_mdn + " OCS XML Resonse)==>" + result);
                                message = message + "\n" + "PinCodeProce(MDN:" + chk_mdn + " OCS XML Resonse)==>" + result;

                                if (result != null) {
                                    PinCodeReqBean apirequestbean = new PinCodeReqBean();
                                    apirequestbean = pincodeutil.parsePinCodeXMLString(result);

                                    resultcode = apirequestbean.getResultcode();

                                    trans.setStatus(resultcode);
                                    boolean brst = epaybusinesscontroller.updateTransaction(trans);

                                    log.info("PinCodeProce(MDN:" + chk_mdn + " OCS ResultCode)==>" + resultcode);
                                    message = message + "\n" + "PinCodeProce(MDN:" + chk_mdn + " OCS ResultCode)==>" + resultcode;

                                    log.info("PinCodeProce(MDN:" + chk_mdn + " update Table status is " + brst + ")==>resultcode:" + resultcode);
                                    message = message + "\n" + "PinCodeProce(MDN:" + chk_mdn + " update Table status is " + brst + ")==>resultcode:" + resultcode;

                                } else {
                                    log.info("Admin Exception===>THE PINCODE Result From 4G OCS IS NULL!!!!!");
                                    message = message + "\n" + "Admin Exception===>THE PINCODE Result From 4G OCS IS NULL!!!!!";

                                }
                            } else if (promotioncode_number == 2) { // ZTE

                                ZTEPinCodeUtil ztePincode = new ZTEPinCodeUtil();
                                String result = ztePincode.putPincodeOCSlet(libm, chk_mdn, pincode);
                                log.info("ZTE PinCodeProce(MDN:" + chk_mdn + " ZTE OCS XML Resonse)==>" + result);

                                if (result != null) {

                                    if (!result.contains("Fault")) {
                                        PinCodeResultBean apirequestbean = new PinCodeResultBean();
                                        apirequestbean = ztePincode.parseZTEPinCodeXMLString(result);

                                        String zteresultcode = apirequestbean.getReturncode();
                                        log.info("zteresultcode====>" + zteresultcode);
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

                                        log.info("ZTE PinCodeProce(MDN:" + chk_mdn + " ZTE OCS ResultCode)==>" + resultcode);
                                        message = message + "\n" + "ZTE PinCodeProce(MDN:" + chk_mdn + " ZTE OCS ResultCode)==>" + resultcode;

                                        log.info("ZTE PinCodeProce(MDN:" + chk_mdn + " update Table status is " + brst + ")==>resultcode:" + resultcode);
                                        message = message + "\n" + "ZTE PinCodeProce(MDN:" + chk_mdn + " update Table status is " + brst + ")==>resultcode:" + resultcode;

                                    } else {
                                        log.info("Admin Exception===>THE ZTEPINCODE Result From 4G ZTE OCS IS NULL!!!!!");
                                        message = message + "\n" + "Admin Exception===>THE PINCODE Result From 4G ZTE OCS IS NULL!!!!!";
                                    }
                                } else {
                                    log.info("Admin Exception===>THE ZTEPINCODE Result From 4G ZTE OCS IS NULL!!!!!");
                                    message = message + "\n" + "Admin Exception===>THE PINCODE Result From 4G ZTE OCS IS NULL!!!!!";

                                }

                            } else if (promotioncode_number == 3) { //NOKIA

                                //KK NOKIA
                                log.info("NOKIA ===>" + chk_mdn + "," + promotioncode);
                                String pomotion_type = kk_promotioncode.substring(0, 3);
                                log.info("NOKIA MDN:" + chk_mdn + " Promotion Type(3)==>" + promotioncode + " ==>" + pomotion_type);

                                NokiaMainPincodeUtil mainutil = new NokiaMainPincodeUtil();
                                NokiaPincodeResultBean nokia_result = mainutil.AddMainPincode(pomotion_type, libm, chk_mdn, pincode, tradeDate);
                                resultcode = nokia_result.getResult_code();
                                log.info(chk_mdn + " NOKIA MDN:" + chk_mdn + " Pincode Result ==>" + resultcode + " ==>" + pincode);

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
                                EPAY_TRANSACTION nokai_trans = epaybusinesscontroller.getTransaction(libm);
                                nokai_trans.setErrdesc(nokia_result_desc);
                                nokai_trans.setErrcode(nokia_errorcode);
                                nokai_trans.setStatus(nokia_status);
                                log.info(chk_mdn + " NOKIA PinCodeProce(MDN:" + chk_mdn + " NOKIA OCS ResultCode)==>" + resultcode);
                                message = message + "\n" + "PinCodeProce(MDN:" + chk_mdn + " NOKIA OCS ResultCode)==>" + resultcode;

                                boolean brst = epaybusinesscontroller.updateTransaction(nokai_trans);
                                log.info(chk_mdn + " NOKIA PinCodeProce(MDN:" + chk_mdn + " update Table status is " + brst + ")==>resultcode:" + resultcode);
                                message = message + "\n" + " Nokia PinCodeProce(MDN:" + chk_mdn + " update Table status is " + brst + ")==>resultcode:" + resultcode;
                            } else {
                                log.info(chk_mdn + " Get no platformType");
                            }

                        } catch (Exception ex) {
                            log.info(ex);
                        }

                        if (resultcode.equals("00")) {
                            //Success
                            this.detach();
                            Executions.sendRedirect("/nosso/OnlineNoSSO_PinCodeMsg.zhtml");
//                            Window window = (Window) Executions.createComponents("/deposit/OnlinePinCodeMsg.zhtml", null, null);
                        } else {
                            //send alertmail & alerSMS

                            this.detach();
//                            Window window = (Window) Executions.createComponents("/deposit/OnlinePinCodeErrorMsg.zhtml", null, null);
                            Executions.sendRedirect("/nosso/OnlineNoSSO_PinCodeErrorMsg.zhtml");
                        }
                    } else {
                        Messagebox.show("請輸入正確的檢核碼", "亞太電信", Messagebox.OK, Messagebox.ERROR);
                        chkcaptcha.randomValue();
                        chkcode.setValue("");
                        chkcode.setFocus(true);
                    }

                } else {
                    log.info(chk_mdn + " Contract Status Check : " + kk_contractstatuscode);
                    this.detach();
                    Sessions.getCurrent().setAttribute("status", kk_contractstatuscode);
                    Executions.sendRedirect("/nosso/OnlineUserErrorMsg.zhtml");
                }
            } else {
                log.info(chk_mdn + " promotion code Check : " + kk_promotioncode);
                this.detach();
                Sessions.getCurrent().setAttribute("promotioncode", kk_promotioncode);
                Executions.sendRedirect("/nosso/OnlineNoSSOPromotionCodeErrorMsg.zhtml");
            }
        } else {
            this.detach();
            Executions.sendRedirect("/nosso/OnlineNoSSOSSOErrorMsg.zhtml");
        }

    }

    public void sendOrder() throws Exception {

        Textbox textbox_mdn = (Textbox) this.getFellow("textbox_mdn"); //聯絡人手機號碼
        String addvalue_mdn = textbox_mdn.getValue();
        String contract_mdn = mdn1;
//        Button button =(Button) this.getFellow("sendPinCodeBtn");
//        button.setDisabled(true);

        log.info("addvalue_mdn==>" + addvalue_mdn);
        log.info("contract_mdn==>" + contract_mdn);

        if (addvalue_mdn.equals(contract_mdn)) {
//            sleep(10000);
            doOrder();
//            button.setDisabled(false);
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

    public NOSSOReqBean getMDNByparseXMLString(String xmlRecords, String TagName) throws Exception {
        NOSSOReqBean nossobean = new NOSSOReqBean();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));

            Document doc = db.parse(is);
            NodeList SOARes = doc.getElementsByTagName(TagName);

            for (int i = 0; i < SOARes.getLength(); i++) {

                Element element = (Element) SOARes.item(i);

                NodeList nodes = element.getElementsByTagName("MDN1");
                Element line = (Element) nodes.item(0);
                nossobean.setMdn1(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("MDN2");
                line = (Element) nodes.item(0);
                nossobean.setMdn2(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("CPLIBM");
                line = (Element) nodes.item(0);
                nossobean.setCplibm(getCharacterDataFromElement(line));

                //    private String mdn1, mdn2, cplibm, salesid, storeid, storename, apisrcid;
                nodes = element.getElementsByTagName("SALESID");
                line = (Element) nodes.item(0);
                nossobean.setSalesid(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("STOREID");
                line = (Element) nodes.item(0);
                nossobean.setStoreid(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("STORENAME");
                line = (Element) nodes.item(0);
                nossobean.setStorename(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("APISRCID");
                line = (Element) nodes.item(0);
                nossobean.setApisrcid(getCharacterDataFromElement(line));
            }

        } catch (IOException e) {
            log.info(e);
        } catch (ParserConfigurationException e) {
            log.info(e);
        } catch (SAXException e) {
            log.info(e);
        }
        return nossobean;
    }

    private String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }

    private String getPromtionStr(String mdn) throws Exception {

        SOAReqBean kk_apirequestbean;
        SoaProfile soa = new SoaProfile();
        String result = soa.putSoaProxyletByMDN(mdn);
        kk_apirequestbean = soa.parseXMLString(result);

        String tmp_promotioncode = kk_apirequestbean.getPromotioncode();

        String label_result = "";

        if (tmp_promotioncode != null && !"".equals(tmp_promotioncode)) {
            log.info("chkPromotionCode=====>" + tmp_promotioncode);
            String promotioncode_3 = tmp_promotioncode.substring(0, 3);
            log.info("chkPromotionCode=====>" + promotioncode_3);

            if (promotioncode_3.equalsIgnoreCase("P4G")) {
                label_result = "4G LTE";
            } else if (promotioncode_3.equalsIgnoreCase("GTB")) {
                label_result = "4G Go";
            } else if (promotioncode_3.equalsIgnoreCase("GTA") || promotioncode_3.equalsIgnoreCase("APT") || promotioncode_3.equalsIgnoreCase("AIR") || promotioncode_3.equalsIgnoreCase("TRV")) {
                label_result = "4G Love";
            } else {
                label_result = "4G PP";
            }
        }
        return label_result;
    }

    public void chkPromotionCode() throws Exception {
        Textbox textbox_mdn = (Textbox) this.getFellow("textbox_mdn"); //聯絡人手機號碼
        Label label_mdn_type = (Label) this.getFellow("label_mdn_type");

        String chk_mdn = textbox_mdn.getValue();
        log.info("chkPromotionCode=====>" + chk_mdn);

        String label_result = getPromtionStr(chk_mdn);
        label_mdn_type.setValue(label_result);

    }
}
