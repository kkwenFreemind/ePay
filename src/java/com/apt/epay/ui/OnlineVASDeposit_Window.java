/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui;

import com.apt.epay.beans.BasicInfoReqBean;
import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.nokia.bean.NokiaSubscribeAgentInfoBean;
import com.apt.epay.nokia.bean.NokiaSubscribeBalanceBean;
import com.apt.epay.nokia.main.NokiaMainBasicInfoUtil;
import com.apt.epay.nokia.main.NokiaMainLoginAndLifeCycleUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.epay.zte.util.ZTEOCS4GBasicInfoUtil;
import com.apt.util.HttpClientUtil;
import com.apt.util.OCS4GBasicInfoUtil;
import com.apt.util.OCSUtil;
import com.apt.util.SecuredMsg;
import com.apt.util.SoaProfile;
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_INVOICE;
import com.epay.ejb.bean.EPAY_PROMOTIONCODE;
import com.epay.ejb.bean.EPAY_SERVICE_INFO;
import com.epay.ejb.bean.EPAY_TRANSACTION;
//import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
//import org.apache.commons.httpclient.NameValuePair;
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
public class OnlineVASDeposit_Window extends Window {

//    private static final long serialVersionUID = -84547354457720L;
    private static final Logger log = Logger.getLogger("EPAY");

    private final static String ZTE_ERRORDESC1 = "S-PPS-00001";
    private final static String ZTE_ERRORDESC2 = "S-PPS-00009";
    private final static String ALU_EROR_RESULTCODE = "20160";

    private final String cpid = new ShareParm().PARM_EPAY_CPID;
    private String libm = "";
    private String contractid;
    private String mdn;
    private String contractstatuscode;
    private String promotioncode;
    private String producttype;
    private String email;
    private String name;
    private String address;
    private String NokiaAgentId = "";

    private final String returnUrl = "http://localhost:8080/cpCreditTest/cpCreditCardResp.jsp";
    private final String[] radio_serviceid = new String[100];

    public void onCreate() throws Exception {

        Sessions.getCurrent().removeAttribute("odBean");
        log.info("Creating Online Deposit...from IP " + Executions.getCurrent().getRemoteAddr());

        String uuid;// = "";
        uuid = ((HttpServletRequest) Executions.getCurrent().getNativeRequest()).getRemoteUser();
        log.info("VASProces(init)==>IP:" + Executions.getCurrent().getRemoteAddr() + ",UUID:" + uuid);

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        Textbox textbox_mdn = (Textbox) this.getFellow("textbox_mdn");
        Radiogroup radio_service_type = (Radiogroup) this.getFellow("radio_service_type");
        Vbox radio_servicevbox = (Vbox) this.getFellow("radio_servicevbox");

        try {
            if (uuid != null) {

                SoaProfile soa = new SoaProfile();
                String result = soa.putSoaProxylet(uuid);
                SOAReqBean apirequestbean;// = new SOAReqBean();
                apirequestbean = soa.parseXMLString(result);

                log.info("kkflag==>VASDeposit:" + result);

                contractid = apirequestbean.getContractid();
                contractstatuscode = apirequestbean.getContract_status_code();
                producttype = apirequestbean.getProducttype();
                mdn = apirequestbean.getMdn();
                promotioncode = apirequestbean.getPromotioncode();
                email = apirequestbean.getEmail();
                name = apirequestbean.getName();
                address = apirequestbean.getAddress();

                textbox_mdn.setValue(mdn);

                if (name.length() > 10) {
                    name = name.substring(0, 9);
                }

//                promotioncode = "GTA1711000";
//                log.info("promotioncode==>" + promotioncode);
                // kk 20160406 LifeCycle Value
                SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);

//
                EPAY_PROMOTIONCODE epaypromotioncode;// = new EPAY_PROMOTIONCODE();
                epaypromotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);
                NokiaSubscribeAgentInfoBean AgentBean = new NokiaSubscribeAgentInfoBean();

                if (epaypromotioncode != null) {
                    int platformtype = epaypromotioncode.getPlatformtype();

                    BasicInfoReqBean basicinforeqbean = new BasicInfoReqBean();

                    boolean activeFlag = false;
                    OCSUtil ocsutil = new OCSUtil();

                    //取得儲值卡資訊
                    if (platformtype == 1) {
                        //ALU
                        OCS4GBasicInfoUtil basicinfoutil = new OCS4GBasicInfoUtil();
                        Calendar nowDateTime = Calendar.getInstance();
                        String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());
                        String basicinfo = basicinfoutil.putOCS4GBasicInfoSlet(libm, mdn, tradeDate_Pincode);
                        log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfo);
                        basicinforeqbean = basicinfoutil.parseBasicInfoXMLString(basicinfo);
                        log.info(mdn + "==>basicinforeqbean.getResultcode()========>" + basicinforeqbean.getResultcode());
                        log.info(mdn + "==>basicinforeqbean.getLifeCycleState()====>" + basicinforeqbean.getLifeCycleState());
                        if (ALU_EROR_RESULTCODE.equals(basicinforeqbean.getResultcode())) {
                            //用戶門號[09XXXXXXXX]不存在，請洽亞太電信客服
                            log.info(mdn + " basicinforeqbean.getResultcode() Check : " + basicinforeqbean.getResultcode());
                            this.detach();
                            Sessions.getCurrent().setAttribute("mdn", mdn);
                            Executions.sendRedirect("/deposit/OnlineUserErrorMsg.zhtml");
                        }

                    } else if (platformtype == 2) {

                        //ZTE
                        ZTEOCS4GBasicInfoUtil ztebasicinfoutil = new ZTEOCS4GBasicInfoUtil();
                        String basicinfo = ztebasicinfoutil.putZTEOCS4GBasicInfoSlet(libm, mdn);
                        log.info(mdn + " ZTE QueryBasicInfo===>MDN:" + mdn + " ZTE OCSXmlResult=>" + basicinfo);
                        basicinforeqbean = ztebasicinfoutil.parseZTEBasicInfoXMLString(basicinfo);
                        log.info(mdn + " ZTE basicinforeqbean.getLifeCycleState()====>" + basicinforeqbean.getLifeCycleState());
                        if (basicinfo.contains(ZTE_ERRORDESC1) || basicinfo.contains(ZTE_ERRORDESC2)) {
                            //用戶門號[09XXXXXXXX]不存在，請洽亞太電信客服
                            this.detach();
                            Sessions.getCurrent().setAttribute("mdn", mdn);
                            Executions.sendRedirect("/deposit/OnlineUserErrorMsg.zhtml");
                        }

                    } else if (platformtype == 3) {
                        //KK NOKIA

                        log.info(mdn + " Nokia PlatformType:" + platformtype);
                        NokiaMainLoginAndLifeCycleUtil nokiautil = new NokiaMainLoginAndLifeCycleUtil();
                        NokiaMainBasicInfoUtil mutil = new NokiaMainBasicInfoUtil();

                        AgentBean = mutil.GetMDNAgentInfo(libm, mdn);
                        NokiaAgentId = AgentBean.getAgentID();

                        log.info(mdn + " Agent ID ====>" + NokiaAgentId);
                        
//                        if("0905001002".equals(mdn)){
//                            NokiaAgentId = "100005";
//                        }

                        String LC = "";
                        String pid = nokiautil.login();
                        log.info(mdn + " Get Login ID ==>" + pid);

                        LC = nokiautil.getMdnLifeCycle(libm, mdn, pid);
                        log.info(mdn + " Get LifeCycle Value ==>" + LC);

                        boolean logoutflag = nokiautil.logout(pid);
                        log.info(mdn + " Logout result ==>" + logoutflag);

                        boolean nokiaFlag = ocsutil.CheckOCSFlag(platformtype, LC);

                        if (nokiaFlag) {
                            basicinforeqbean.setLifeCycleState(LC);
                        } else {
                            this.detach();
                            Sessions.getCurrent().setAttribute("mdn", mdn);
                            Executions.sendRedirect("/deposit/OnlineUserErrorMsg.zhtml");
                        }

                    } else {

                        log.info(mdn + " platformtype IS " + platformtype);
                        this.detach();
                        Executions.sendRedirect("/deposit/OnlineSSOErrorMsg.zhtml");

                    }

                    log.info(mdn + " platformtype===>" + platformtype);
                    log.info(mdn + " basicinforeqbean.getLifeCycleState()==>" + basicinforeqbean.getLifeCycleState());
                    log.info(mdn + " contractstatuscode==>" + contractstatuscode);

                    log.info(mdn + " platformtype=>" + platformtype + ",basicinforeqbean.getLifeCycleState()=>" + basicinforeqbean.getLifeCycleState() + ",Check Flag=>" + activeFlag);

                    activeFlag = ocsutil.CheckOCSFlag(platformtype, basicinforeqbean.getLifeCycleState());
                    NokiaSubscribeBalanceBean bean;
                    NokiaMainBasicInfoUtil nokiaUtil = new NokiaMainBasicInfoUtil();

                    if ((activeFlag)) {

                        List serviceinfo1;//= null;
                        if (platformtype == 3) {
                            //Nokia ServiceID
//                            NokiaAgentId = "100005";

                            //不分PlatformType，只要promotion code符合就撈取
                            //後面再來依照NokiaAgentId判斷，Nokia門號是4G/5G
                            //如果是4G，則把5G的資費剃除
                            //如果是5G，則都保留，不用剔除                         
                            log.info(mdn + "==>Platform Type =>" + platformtype);
                            log.info(mdn + "==>Nokia AgentID==>" + NokiaAgentId);
                            log.info(mdn + "==>get serviceinfo1 (" + Integer.valueOf(cpid) + "," + promotioncode + "," + platformtype);
                            serviceinfo1 = epaybusinesscontroller.listNokialServiceInfo(Integer.valueOf(cpid), promotioncode, platformtype);

                        } else {
                            //case 1 : platform type == 1 ,ALU 4G
                            //case 2 : platform type == 2 ,ZTE 4G
                            //依照platform Type以及Promotion code來判斷其門號可以選擇的資費

                            log.info(mdn + "==>Platform Type =>" + platformtype);
                            log.info(mdn + "==>get serviceinfo1 (" + Integer.valueOf(cpid) + "," + promotioncode + "," + platformtype);
                            serviceinfo1 = epaybusinesscontroller.listAllServiceInfo(Integer.valueOf(cpid), promotioncode, platformtype);
                        }

                        log.info(mdn + " PromotionCode ===>" + promotioncode);

                        Iterator itserviceinfo1 = serviceinfo1.iterator();
                        int j = 0;
                        while (itserviceinfo1.hasNext()) {

                            EPAY_SERVICE_INFO serid = (EPAY_SERVICE_INFO) itserviceinfo1.next();

                            //Nokia 門號
                            //不分PlatformType，只要promotion code符合就撈取
                            //後面再來依照NokiaAgentId判斷，Nokia門號是4G/5G
                            //如果是4G，則把5G的資費剃除
                            //如果是5G，則都保留，不用剔除  
                            //ZTE & ALU
                            //依照platform Type以及Promotion code來判斷其門號可以選擇的資費          
                            if (!"100005".equalsIgnoreCase(NokiaAgentId)) {

                                //非5G Nokia的情況
                                //case 1 : platform type == 1 ,ALU 4G
                                //case 2 : platform type == 2 ,ZTE 4G
                                //case 3 : platfrom type == 3 ,Nokia 4G
                                if ((serid.getPlatformtype() == 1) || (serid.getPlatformtype() == 2) || (serid.getPlatformtype() == 3 && serid.getGtype() == 4)) {

                                    Radio serviceid_radio = new Radio();
                                    serviceid_radio.setId(String.valueOf(serid.getServiceId()));
                                    serviceid_radio.setLabel(serid.getServiceName());
                                    serviceid_radio.setStyle("\"font-family: '微軟正黑體';font-size: 18px;\"");
                                    serviceid_radio.setParent(radio_servicevbox);

                                    Label label = new Label();
                                    label.setValue("　" + serid.getNote());
                                    label.setParent(radio_servicevbox);

                                    log.info(mdn + " PlatformType=" + serid.getPlatformtype() + ",GType=" + serid.getGtype() + "==>Radio--->" + serid.getServiceName());
                                    radio_serviceid[j] = String.valueOf(serid.getServiceId());
                                    j++;

                                } else {
                                    log.info(mdn + " PlatformType=" + serid.getPlatformtype() + ",GType=" + serid.getGtype() + " Filter 5G Nokia Item--->>" + serid.getServiceName());
                                    //log.info(mdn + " Filter 5G Nokia Item--->" + serid.getServiceName() + ",serid.getPlatformtype()==>" + serid.getPlatformtype());
                                }
                            } else {

                                //5G Nokia的情況 
                                Radio serviceid_radio = new Radio();
                                serviceid_radio.setId(String.valueOf(serid.getServiceId()));
                                serviceid_radio.setLabel(serid.getServiceName());
                                serviceid_radio.setStyle("\"font-family: '微軟正黑體';font-size: 18px;\"");
                                serviceid_radio.setParent(radio_servicevbox);

                                Label label = new Label();
                                label.setValue("　" + serid.getNote());
                                label.setParent(radio_servicevbox);

                                log.info(mdn + "==>Radio--->" + serid.getServiceName());
                                radio_serviceid[j] = String.valueOf(serid.getServiceId());
                                j++;

                            }
                        }
                        radio_service_type.setSelectedIndex(0);

                        log.info(mdn + " VASProce(SOA Result)==>IP:" + Executions.getCurrent().getRemoteAddr()
                                        + ",MDN:" + mdn
                                        + ",ContractID:" + contractid
                                        + ",Contract_status:" + contractstatuscode
                                        + ",producttype:" + producttype
                                        + ",promotioncode:" + promotioncode);

                        if ((contractstatuscode.equals("9") || contractstatuscode.equals("43"))
                                        && (platformtype == 1 || platformtype == 2 || platformtype == 3)) {
                            //DO NOTHING                        
                            log.info(mdn + " contractstatuscode==>" + contractstatuscode);
                            log.info(mdn + " epay_promotioncode.getPlatformtype()===>" + platformtype);

                        } else {
                            log.info(mdn + " Contract Status Check : " + contractstatuscode);
                            this.detach();
                            Sessions.getCurrent().setAttribute("status", contractstatuscode);
                            Executions.sendRedirect("/deposit/OnlineUserErrorMsg.zhtml");
                        }
                    } else {
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
        SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");

        Captcha chkcaptcha = (Captcha) getSpaceOwner().getFellow("cpa");
        Textbox chkcode = (Textbox) this.getFellow("chkcode");

        Radiogroup radio_service_type = (Radiogroup) this.getFellow("radio_service_type");
        String xserviceid = radio_serviceid[radio_service_type.getSelectedIndex()];

        log.info(mdn + " The Radio serviceid ====>" + radio_service_type.getId());
        log.info(mdn + " The Radio serviceid ====>" + radio_service_type.getSelectedIndex());
        log.info(mdn + " The Radio serviceid ====>" + radio_serviceid[radio_service_type.getSelectedIndex()]);

        boolean flag = checkPricePlanByServiceId(xserviceid, Integer.valueOf(cpid));

        if (flag) {

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
            String contactEmail = email;//無發票資訊(電郵)
            String contactName = name;//無發票資訊(聯絡人)        

            String orderTotal = String.valueOf(tradeAmount * tradeQuantity);
            Calendar nowDateTime = Calendar.getInstance();
            String tradeDate = sdf.format(nowDateTime.getTime());

            EPAY_PROMOTIONCODE epaypromotioncode;// = new EPAY_PROMOTIONCODE();
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

                    //20170713
                    trans.setApisrcid("1");
                    trans.setPaytool("1");

                    log.info(mdn + " VASProce(insert EPAY_TRANSACTION Table)==>MDN:" + mdn + ",Libm:" + libm);

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
                    log.info(mdn + " VASProce(insert EPAY_INVOICE Table)==>MDN:" + mdn + ",Libm:" + libm);

                    epaybusinesscontroller.insertInvoice(inv);
                    /*
                 發送至CP金流平台                
                     */
                    String sendURL = new ShareParm().PARM_CPECF_URL;
                    log.info(mdn + " CPECF_URL===>" + sendURL);
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
                                    .append("bankID=").append("812").append("&")
                                    .append("callerInMac=").append("38dc7020a508b59ba409615273495ab5").append("&")
                                    .append("isMd5Match=").append("true");

                    log.info("originalParm==>" + originalParm);
//
//                NameValuePair[] requestBody = null;
                    String data = (new SecuredMsg(new ShareParm().PARM_PG_KEY, new ShareParm().PARM_PG_IDENT).encode(originalParm.toString()));
                    postToNextPage(cpid, data);

                } else {
                    //如果有編號,檢查是否已經交易, 如果還未交易可在更新資料。
                    log.info(mdn + " The Libm is exist ===>" + libm);
//                Messagebox.show("已送出，交易代碼為"+libm, "亞太電信", Messagebox.OK, Messagebox.ERROR);
                }
            } else {
                Messagebox.show("請輸入正確的檢核碼", "亞太電信", Messagebox.OK, Messagebox.ERROR);
                chkcaptcha.randomValue();
                chkcode.setValue("");
                chkcode.setFocus(true);
            }
        } else {
            log.info(libm + "該門號儲值後餘額，將超過上限金額$5,000元，無法進行儲值");
            this.detach();
            Executions.sendRedirect("/deposit/OnlineSSOErrorPricePlanMsg.zhtml");
            //Window window = (Window) Executions.createComponents("/deposit/OnlineSSOErrorPricePlanMsg.zhtml", null, null);

        }

    }

    public void sendATMOrder() throws Exception {
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
        SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");

        Captcha chkcaptcha = (Captcha) getSpaceOwner().getFellow("cpa");
        Textbox chkcode = (Textbox) this.getFellow("chkcode");

        EPAY_CP_INFO cpinfo = epaybusinesscontroller.getCpInfo(Integer.valueOf(cpid));
        String cpname = cpinfo.getCpName();

        Radiogroup radio_service_type = (Radiogroup) this.getFellow("radio_service_type");
        String serviceid = radio_serviceid[radio_service_type.getSelectedIndex()];
        //
        log.info(mdn + " The Radio serviceid ====>" + radio_service_type.getId());
        log.info(mdn + " The Radio serviceid ====>" + radio_service_type.getSelectedIndex());
        log.info(mdn + " The Radio serviceid ====>" + radio_serviceid[radio_service_type.getSelectedIndex()]);

        boolean flag = checkPricePlanByServiceId(serviceid, Integer.valueOf(cpid));

        if (flag) {

            EPAY_SERVICE_INFO serviceinfo = epaybusinesscontroller.getServiceInfoById(Long.valueOf(serviceid), Integer.valueOf(cpid));
            log.info(mdn + " epaybusinesscontroller.getServiceInfoById(Long.valueOf(serviceid), Integer.valueOf(cpid))==>" + serviceid + "," + cpid);
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

            String contactName = name;//無發票資訊(聯絡人)
            String contactEmail = email;//無發票資訊(電郵)

            String orderTotal = String.valueOf(tradeAmount * tradeQuantity);
            Calendar nowDateTime = Calendar.getInstance();
            String tradeDate = sdf.format(nowDateTime.getTime());

            if (chkcode.getValue() != null && chkcode.getValue().equals(chkcaptcha.getValue())) {

                // 產生訂單編號 yymmddHHmissSSS
                libm = sdf15.format(nowDateTime.getTime());

                //記錄和比對是否已有訂單            
                EPAY_TRANSACTION trans = epaybusinesscontroller.getTransaction(libm);
                log.info("epaybusinesscontroller.getTransaction(libm)==>" + libm);

                EPAY_PROMOTIONCODE epaypromotioncode;// = new EPAY_PROMOTIONCODE();
                epaypromotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);
                int platformtype = epaypromotioncode.getPlatformtype();

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
                    trans.setCpName(cpname);
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
                    trans.setPlatformtype(platformtype);

                    //20170713
                    trans.setApisrcid("1");
                    trans.setPaytool("2");

                    log.info(mdn + " VASProce(insert EPAY_TRANSACTION Table)==>MDN:" + mdn + ",Libm:" + libm);

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
                    log.info(mdn + " VASProce(insert EPAY_INVOICE Table)==>MDN:" + mdn + ",Libm:" + libm);

                    epaybusinesscontroller.insertInvoice(inv);

                    /*
                    發送至CP金流平台                
                     */
                    String sendURL = new ShareParm().PARM_CPECF_URL;
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
                    String data = (new SecuredMsg(new ShareParm().PARM_PG_KEY, new ShareParm().PARM_PG_IDENT).encode(originalParm.toString()));
                    postToNextPage(cpid, data);
                } else {
                    //如果有編號,檢查是否已經交易, 如果還未交易可在更新資料。   
                }

            } else {
                Messagebox.show("請輸入正確的檢核碼", "亞太電信", Messagebox.OK, Messagebox.ERROR);
                chkcaptcha.randomValue();
                chkcode.setValue("");
                chkcode.setFocus(true);
            }
        } else {
            log.info(libm + "該門號餘額已超過限額上限金額$5000, 不可儲值");
            this.detach();
            Executions.sendRedirect("/deposit/OnlineSSOErrorPricePlanMsg.zhtml");
            //Window window = (Window) Executions.createComponents("/deposit/OnlineSSOErrorPricePlanMsg.zhtml", null, null);
        }
    }

    private void postToNextPage(String systemid, String data) throws Exception {

        Sessions.getCurrent().setAttribute("system", systemid);
        Sessions.getCurrent().setAttribute("data", data);

        String sendURL = new ShareParm().PARM_CPECF_URL;
        log.info("systemid,data,sendURL===>" + systemid + "," + data + "," + sendURL);
        Sessions.getCurrent().setAttribute("sendURL", sendURL);
        Executions.getCurrent().setAttribute(org.zkoss.zk.ui.sys.Attributes.NO_CACHE, Boolean.TRUE);
        Window window = (Window) Executions.createComponents("/deposit/OnlineVASDepositSendECF.zul", null, null);

    }

    public boolean checkPricePlanByServiceId(String serviceid, int icpid) throws Exception {

        boolean result = false;

        EPAY_SERVICE_INFO serviceinfo = null;
        long lserviceId = Long.valueOf(serviceid);

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        serviceinfo = epaybusinesscontroller.getServiceInfoByCpidAndServiceId(lserviceId, icpid);

        //20210729
        //KP 要求Nokia也要能使用ZTE的priceplan code       
        EPAY_PROMOTIONCODE epaypromotioncode;// = new EPAY_PROMOTIONCODE();
        epaypromotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);
        int platformtype = epaypromotioncode.getPlatformtype();

        log.info(mdn + " Start to Check PricePlanCode By ServiceId ==>" + serviceid + "," + platformtype);

        if (serviceinfo != null) {
            String priceplancode = serviceinfo.getPriceplancode();

            int db_check_value = serviceinfo.getCheck_value();
            double count1x = 0.0;
            double count2x = 0.0;
            double sumx = 0;
            log.info(mdn + " serviceinfo.getCheck_DB_Config_value()==>" + db_check_value);

            if (!"".equalsIgnoreCase(priceplancode)) {

                String priceplancodeList = new ShareParm().PARM_PRICEPLCODE;

                if (priceplancodeList.contains(priceplancode)) {
                    SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                    Calendar nowDateTime = Calendar.getInstance();
                    String libm = sdf15.format(nowDateTime.getTime());

                    if (platformtype == 2) {

                        ZTEOCS4GBasicInfoUtil ztebasicinfoutil = new ZTEOCS4GBasicInfoUtil();
                        String basicinfo = ztebasicinfoutil.putZTEOCS4GBasicInfoSlet(libm, mdn);
                        log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfo);
                        BasicInfoReqBean basicinforeqbean;
                        BasicInfoReqBean basicinforeqbeanx;

                        basicinforeqbean = ztebasicinfoutil.parseZTEBasicInfoXMLString(basicinfo);

                        log.info("basicinforeqbean.getReal_LifeCycleState()====>" + basicinforeqbean.getReal_LifeCycleState());

                        ZTEOCS4GBasicInfoUtil ztebasicinfoutilx = new ZTEOCS4GBasicInfoUtil();
                        String basicinfox = ztebasicinfoutilx.putZTEOCS4GBasicInfoSlet(libm, mdn);
                        log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfox);

                        basicinforeqbeanx = ztebasicinfoutilx.parseZTEBasicInfoXMLString(basicinfo);

                        count1x = Double.valueOf(basicinforeqbeanx.getCounterValue1());
                        count2x = Double.valueOf(basicinforeqbeanx.getCounterValue2());

                    } else if (platformtype == 3) {

                        //KK NOKIA
                        NokiaMainBasicInfoUtil nokiaUtil = new NokiaMainBasicInfoUtil();
                        NokiaSubscribeBalanceBean bean = nokiaUtil.GetMDNBalanceInfo(libm, mdn);
                        double tmp_d_value_650 = 0.0;
                        tmp_d_value_650 = Double.valueOf(bean.getAPT5GVoice_650_Counter()) / 10000;
                        double d_value_650 = Math.round(tmp_d_value_650 * 100.0) / 100.0;

                        double tmp_d_value_670 = 0.0;
                        tmp_d_value_670 = Double.valueOf(bean.getAPT5GIDD_670_Counter()) / 10000;
                        double d_value_670 = Math.round(tmp_d_value_670 * 100.0) / 100.0;

                        double tmp_d_value_750 = 0.0;
                        tmp_d_value_750 = Double.valueOf(bean.getAPT5GData_750_Counter()) / 1048576;
                        double d_value_750 = Math.round(tmp_d_value_750 * 100.0) / 100.0;

                        log.info(mdn + " b650==>" + d_value_650 + ",b670==>" + d_value_670);
                        log.info(mdn + " b750==>" + d_value_750);
                        count1x = d_value_650 + d_value_670;
                        count2x = d_value_750;

                    }

                    sumx = (double) count1x + (double) count2x;
                    log.info(mdn + ":count1x()==>" + count1x);
                    log.info(mdn + ":count2x()==>" + count2x);
                    log.info(mdn + ":sumx===>" + sumx);
                    log.info(mdn + ":db_config_check_value==>" + db_check_value);

                    double checkvalue = sumx + db_check_value;

                    log.info(mdn + ":sumx + db_config_check_value==>" + checkvalue);
                    if (checkvalue < 5000) {
                        result = true;
                    }

                } else {
                    result = true;
                }
            }
        } else {

        }
        log.info(mdn + "End to  check PricePlanCode By ServiceId ==>" + serviceid + "," + platformtype + "," + result);
        return result;
    }
}
