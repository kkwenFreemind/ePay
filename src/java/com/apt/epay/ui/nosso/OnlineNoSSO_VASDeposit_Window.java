/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.nosso;

import com.apt.epay.beans.BasicInfoReqBean;
import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.deposit.bean.NOSSOReqBean;
import com.apt.epay.deposit.bean.ServiceOrderReqBean;
import com.apt.epay.deposit.util.toolUtil;
import com.apt.epay.nokia.bean.NokiaSubscribeAgentInfoBean;
import com.apt.epay.nokia.bean.NokiaSubscribeBalanceBean;
import com.apt.epay.nokia.main.NokiaMainBasicInfoUtil;
import com.apt.epay.nokia.main.NokiaMainLoginAndLifeCycleUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.epay.zte.util.ZTEOCS4GBasicInfoUtil;
import com.apt.util.OCS4GBasicInfoUtil;
import com.apt.util.OCSUtil;
import com.apt.util.SecuredMsg;
import com.apt.util.SoaProfile;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_INVOICE;
import com.epay.ejb.bean.EPAY_PROMOTIONCODE;
import com.epay.ejb.bean.EPAY_SERVICE_INFO;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
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
public class OnlineNoSSO_VASDeposit_Window extends Window {

    private final Logger log = Logger.getLogger("EPAY");

    SOAReqBean apirequestbean;

    private final String returnUrl = "http://localhost:8080/cpCreditTest/cpCreditCardResp.jsp";
    private final String[] radio_serviceid = new String[100];
    private String mdn1, mdn2, cplibm, salesid, storeid, storename, apisrcid;
    private String cpid;
    private String NokiaAgentId = "";

    public void onCreate() throws Exception {

        Sessions.getCurrent().removeAttribute("odBean");
        log.info("Creating Online Deposit...from IP " + Executions.getCurrent().getRemoteAddr());

        HttpServletRequest req = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
        log.info("doAfterCompose.getQueryString():" + req.getQueryString());

        toolUtil tool = new toolUtil();
        Utilities util = new Utilities();

        cpid = req.getParameter("CPID");
        String data = req.getParameter("DATA");

        log.info("cpid==>" + cpid);
        log.info("data==>" + data);

        try {

            EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

            int icpid = Integer.valueOf(cpid);
            EPAY_CP_INFO cpinfo = epaybusinesscontroller.getCpInfo(icpid);
//            String Status;// = "0x00000000";
//            String Status_Desc;// = "成功";
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

                NOSSOReqBean aPIServiceOrdrReqBean;
                aPIServiceOrdrReqBean = getMDNByparseXMLString(str_input, "ServiceVASDeposit");
                mdn1 = aPIServiceOrdrReqBean.getMdn1();
                mdn2 = aPIServiceOrdrReqBean.getMdn2();
                cplibm = aPIServiceOrdrReqBean.getCplibm();
                apisrcid = aPIServiceOrdrReqBean.getApisrcid();
                salesid = aPIServiceOrdrReqBean.getSalesid();
                storeid = aPIServiceOrdrReqBean.getStoreid();
                storename = aPIServiceOrdrReqBean.getStorename();

                Textbox textbox_mdn = (Textbox) this.getFellow("textbox_mdn");
                Radiogroup radio_service_type = (Radiogroup) this.getFellow("radio_service_type");
                Vbox radio_servicevbox = (Vbox) this.getFellow("radio_servicevbox");

                if (mdn2 != null && !"".equals(mdn2)) {

                    SoaProfile soa = new SoaProfile();
                    String result = soa.putSoaProxyletByMDN(mdn2);
                    apirequestbean = soa.parseXMLString(result);

                    log.info("kkflag==>NoSSO_VASDeposit:" + result);

                    String resultcode = apirequestbean.getResultcode();
                    String contractid = apirequestbean.getContractid();
                    String contractstatuscode = apirequestbean.getContract_status_code();
                    String producttype = apirequestbean.getProducttype();
                    String mdn = apirequestbean.getMdn();
                    String promotioncode = apirequestbean.getPromotioncode();
                    String name = apirequestbean.getName();
                    textbox_mdn.setValue(mdn);
                    if (("00000000".equals(resultcode) && !"".equals(promotioncode))) {
                        if (name.length() > 10) {
                            apirequestbean.setName(name.substring(0, 9));
                        }

                        SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);

                        EPAY_PROMOTIONCODE epaypromotioncode;// = new EPAY_PROMOTIONCODE();
                        epaypromotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);

                        SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                        Calendar nowDateTime = Calendar.getInstance();
                        String libm = sdf15.format(nowDateTime.getTime());

                        NokiaSubscribeAgentInfoBean AgentBean = new NokiaSubscribeAgentInfoBean();

                        if (epaypromotioncode != null) {
                            int platformtype = epaypromotioncode.getPlatformtype();
                            BasicInfoReqBean basicinforeqbean = new BasicInfoReqBean();

                            boolean activeFlag = false;
                            OCSUtil ocsutil = new OCSUtil();

                            //取得儲值卡資訊
                            switch (platformtype) {
                                case 1: {
                                    //ALU
                                    OCS4GBasicInfoUtil basicinfoutil = new OCS4GBasicInfoUtil();
                                    String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());
                                    String basicinfo = basicinfoutil.putOCS4GBasicInfoSlet(libm, mdn, tradeDate_Pincode);
                                    log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfo);
                                    basicinforeqbean = basicinfoutil.parseBasicInfoXMLString(basicinfo);
                                    log.info("basicinforeqbean.getResultcode()========>" + basicinforeqbean.getResultcode());
                                    log.info("basicinforeqbean.getLifeCycleState()====>" + basicinforeqbean.getLifeCycleState());
                                    String ALU_EROR_RESULTCODE = ShareParm.ALU_EROR_RESULTCODE;
                                    if (ALU_EROR_RESULTCODE.equals(basicinforeqbean.getResultcode())) {
                                        //用戶門號[09XXXXXXXX]不存在，請洽亞太電信客服
                                        log.info(mdn + " basicinforeqbean.getResultcode() Check : " + basicinforeqbean.getResultcode());
                                        this.detach();
                                        Sessions.getCurrent().setAttribute("mdn", mdn);
                                        Executions.sendRedirect("/nosso/OnlineNoSSOUserErrorMsg.zhtml");
                                    }
                                    break;
                                }
                                case 2: {
                                    //ZTE
                                    ZTEOCS4GBasicInfoUtil ztebasicinfoutil = new ZTEOCS4GBasicInfoUtil();
                                    String basicinfo = ztebasicinfoutil.putZTEOCS4GBasicInfoSlet(libm, mdn);
                                    log.info("ZTE QueryBasicInfo===>MDN:" + mdn + " ZTE OCSXmlResult=>" + basicinfo);
                                    basicinforeqbean = ztebasicinfoutil.parseZTEBasicInfoXMLString(basicinfo);
                                    log.info("ZTE basicinforeqbean.getLifeCycleState()====>" + basicinforeqbean.getLifeCycleState());
                                    String ZTE_ERRORDESC1 = ShareParm.ZTE_ERRORDESC1;
                                    String ZTE_ERRORDESC2 = ShareParm.ZTE_ERRORDESC2;
                                    if (basicinfo.contains(ZTE_ERRORDESC1) || basicinfo.contains(ZTE_ERRORDESC2)) {
                                        //用戶門號[09XXXXXXXX]不存在，請洽亞太電信客服
                                        this.detach();
                                        Sessions.getCurrent().setAttribute("mdn", mdn);
                                        Executions.sendRedirect("/nosso/OnlineNoSSOUserErrorMsg.zhtml");
                                    }
                                    break;
                                }
                                case 3: {

                                    //KK NOKIA
                                    NokiaMainLoginAndLifeCycleUtil nokiautil = new NokiaMainLoginAndLifeCycleUtil();
                                    NokiaMainBasicInfoUtil mutil = new NokiaMainBasicInfoUtil();

                                    AgentBean = mutil.GetMDNAgentInfo(libm, mdn);
                                    NokiaAgentId = AgentBean.getAgentID();

                                    log.info(mdn + " Agent ID ====>" + NokiaAgentId);

                                    String LC = "";
                                    String pid = nokiautil.login();
                                    log.info(mdn + " Get Login ID ==>" + pid);

                                    LC = nokiautil.getMdnLifeCycle(libm, mdn, pid);
                                    log.info(mdn + " Get LifeCycle Value ==>" + LC);

                                    boolean logoutflag = nokiautil.logout(pid);
                                    log.info(mdn + " Logout result ==>" + logoutflag);

                                    //TEST   LC= "Expire";
                                    boolean nokiaFlag = ocsutil.CheckOCSFlag(platformtype, LC);

                                    if (nokiaFlag) {
                                        basicinforeqbean.setLifeCycleState(LC);
                                    } else {
                                        this.detach();
                                        Sessions.getCurrent().setAttribute("mdn", mdn);
                                        Executions.sendRedirect("/nosso/OnlineNoSSOUserErrorMsg.zhtml");
                                    }
                                    break;
                                }
                                default:
                                    log.info(mdn + " platformtype IS " + platformtype);
                                    this.detach();
                                    Executions.sendRedirect("/nosso/OnlineNoSSOSSOErrorMsg.zhtml");
                                    break;
                            }

                            log.info(mdn + " platformtype===>" + platformtype);
                            log.info(mdn + " basicinforeqbean.getLifeCycleState()==>" + basicinforeqbean.getLifeCycleState());
                            log.info(mdn + " contractstatuscode==>" + contractstatuscode);

                            log.info(mdn + " platformtype=>" + platformtype + ",basicinforeqbean.getLifeCycleState()=>" + basicinforeqbean.getLifeCycleState() + ",Check Flag=>" + activeFlag);

                            activeFlag = ocsutil.CheckOCSFlag(platformtype, basicinforeqbean.getLifeCycleState());
                            NokiaSubscribeBalanceBean bean;
                            NokiaMainBasicInfoUtil nokiaUtil = new NokiaMainBasicInfoUtil();

                            if (activeFlag) {

                                List serviceinfo1;
                                if (platformtype == 3) {
                                    //Nokia ServiceID
                                    //NokiaAgentId = "100005";

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

                                log.info("PromotionCode ===>" + promotioncode);

                                Iterator itserviceinfo1 = serviceinfo1.iterator();
                                int j = 0;
                                while (itserviceinfo1.hasNext()) {

                                    EPAY_SERVICE_INFO serid = (EPAY_SERVICE_INFO) itserviceinfo1.next();

                                    if (!"100005".equalsIgnoreCase(NokiaAgentId)) {
                                        //非5G Nokia的情況
                                        //case 1 : platform type == 1 ,ALU 4G
                                        //case 2 : platform type == 2 ,ZTE 4G
                                        //case 3 : platfrom type == 3 ,Nokia 4G
                                        if ((serid.getPlatformtype() == 2) || (serid.getPlatformtype() == 3 && serid.getGtype() == 4)) {

                                            Radio serviceid_radio = new Radio();
                                            serviceid_radio.setId(String.valueOf(serid.getServiceId()));
                                            serviceid_radio.setLabel(serid.getServiceName());
                                            serviceid_radio.setParent(radio_servicevbox);

                                            log.info(mdn + " PlatformType=" + serid.getPlatformtype() + ",GType=" + serid.getGtype() + "==>Radio--->" + serid.getServiceName());
                                            radio_serviceid[j] = String.valueOf(serid.getServiceId());
                                            j++;
                                        } else {
                                            log.info(mdn + " PlatformType=" + serid.getPlatformtype() + ",GType=" + serid.getGtype() + " Filter 5G Nokia Item--->>" + serid.getServiceName());
                                        }

                                    } else {
                                        //5G Nokia的情況 
                                        Radio serviceid_radio = new Radio();
                                        serviceid_radio.setId(String.valueOf(serid.getServiceId()));
                                        serviceid_radio.setLabel(serid.getServiceName());
                                        serviceid_radio.setParent(radio_servicevbox);

                                        log.info(mdn + "==>Radio--->" + serid.getServiceName());
                                        radio_serviceid[j] = String.valueOf(serid.getServiceId());
                                        j++;

                                    }

                                }
                                radio_service_type.setSelectedIndex(0);

                                log.info("VASProce(SOA Result)==>IP:" + Executions.getCurrent().getRemoteAddr()
                                                + ",MDN:" + mdn
                                                + ",ContractID:" + contractid
                                                + ",Contract_status:" + contractstatuscode
                                                + ",producttype:" + producttype
                                                + ",promotioncode:" + promotioncode);

                                if ((contractstatuscode.equals("9") || contractstatuscode.equals("43")) && (platformtype == 1 || platformtype == 2 || platformtype == 3)) {
                                    //DO NOTHING                        
                                    log.info("contractstatuscode==>" + contractstatuscode);
                                    log.info("epay_promotioncode.getPlatformtype()===>" + platformtype);

                                } else {
                                    log.info(mdn + " Contract Status Check : " + contractstatuscode);
                                    this.detach();
                                    Sessions.getCurrent().setAttribute("status", contractstatuscode);
                                    Executions.sendRedirect("/nosso/OnlineNoSSOUserErrorMsg.zhtml");
                                }
                            } else {
                                log.info(mdn + " 您的有效期限已逾期,請盡速儲值或洽客服協助==>" + basicinforeqbean.getLifeCycleState());
                                this.detach();
                                Executions.sendRedirect("/nosso/OnlineLifeCycleStateErrorMsg.zhtml");
                            }
                        } else {
                            // no promotioncode data in table
                            log.info(mdn + " promotion code Check : " + promotioncode);
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
    }

    private void SendtoNPGW(int type) {

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
        SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");

        Captcha chkcaptcha = (Captcha) getSpaceOwner().getFellow("cpa");
        Textbox chkcode = (Textbox) this.getFellow("chkcode");

        Radiogroup radio_service_type = (Radiogroup) this.getFellow("radio_service_type");
        String xserviceid = radio_serviceid[radio_service_type.getSelectedIndex()];

        String mdn = apirequestbean.getMdn();
        String promotioncode = apirequestbean.getPromotioncode();

        log.info("The Radio serviceid ====>" + radio_service_type.getId());
        log.info("The Radio serviceid ====>" + radio_service_type.getSelectedIndex());
        log.info("The Radio serviceid ====>" + radio_serviceid[radio_service_type.getSelectedIndex()]);

        EPAY_SERVICE_INFO serviceinfo;
        try {

            boolean flag = checkPricePlanByServiceId(xserviceid, Integer.valueOf(cpid), mdn, promotioncode);

            if (flag) {
                serviceinfo = epaybusinesscontroller.getServiceInfoById(Long.valueOf(xserviceid), Integer.valueOf(cpid));

                EPAY_CP_INFO cpinfo = epaybusinesscontroller.getCpInfo(Integer.valueOf(cpid));
                String cpname = cpinfo.getCpName();

                String itemName = serviceinfo.getServiceName();
                String itemUnitPrice = serviceinfo.getPrice().toString(); //虛擬卡實際金額(Price)
                String itemCode = serviceinfo.getGlcode();

                String email = apirequestbean.getEmail();
                String name = apirequestbean.getName();

                String contractid = apirequestbean.getContractid();

                String contactCellPhone = mdn;//聯絡手機號碼
                String receiptType = "2";//無發票資訊(二聯三聯)
                int tradeAmount = Integer.valueOf(itemUnitPrice);
                int tradeQuantity = 1;
                String receiptTitle = name; //無發票資訊(發票抬頭)
                String vatNo = "";//無發票資訊(統編)
                String receiptAddress = "";//無發票資訊(地址)
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
                    String libm = sdf15.format(nowDateTime.getTime());

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
                        trans.setPaymethod(type); //付款方式 信用卡:value = 1 
                        trans.setStatus("N"); //OCS尚未儲值完成
                        trans.setPaystatus(0); //0:(default value) 尚未繳費成功
                        trans.setPayamount(Integer.parseInt(orderTotal));
                        trans.setPrivatedata(libm); //PinCode number

                        trans.setServiceId(xserviceid);//
                        trans.setCpLibm(cplibm);
                        trans.setCpId(Integer.parseInt(new ShareParm().PARM_EPAY_CPID));//
                        trans.setCpName(cpname);
                        trans.setFeeType("0"); //無拆帳需求
                        trans.setInvoiceContactMobilePhone(contactCellPhone);
                        trans.setContractID(contractid);
                        trans.setPlatformtype(platformtype);

                        //20170713
                        trans.setApisrcid(apisrcid);
                        trans.setPaytool(String.valueOf(type));
                        if (storename != null) {
                            trans.setStorename(storename);
                        }

                        if (storeid != null) {
                            trans.setStoreid(storeid);
                        }

                        log.info("===>" + salesid);
                        if (!"".equals(salesid)) {
                            trans.setSales_id(salesid);
                        }

                        if (mdn1 != null) {
                            trans.setLoginmdn(mdn1);
                        }

                        log.info("VASProce(insert EPAY_TRANSACTION Table)==>MDN:" + mdn + ",Libm:" + libm);
                        boolean insertranflag = epaybusinesscontroller.insertTransaction(trans);

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
                        boolean insertinvflag = epaybusinesscontroller.insertInvoice(inv);

                        /*
                 發送至CP金流平台                
                         */
                        if (insertranflag && insertinvflag) {
                            String sendURL = new ShareParm().PARM_CPECF_URL;
                            log.info("CPECF_URL===>" + sendURL);
                            StringBuffer originalParm = new StringBuffer();
                            originalParm.append("libm=").append(libm).append("&")
                                            .append("serviceid=").append(xserviceid).append("&")
                                            .append("serviceName=").append(itemName).append("&")
                                            .append("txType=").append(type).append("&")
                                            .append("orderTotal=").append(orderTotal).append("&")
                                            .append("tradeDate=").append(tradeDate).append("&")
                                            .append("callerResUrl=").append(returnUrl).append("&")
                                            .append("invoiceType=").append(receiptType).append("&")
                                            .append("invoiceTitle=").append(receiptTitle).append("&")
                                            .append("uniform=").append(vatNo).append("&")
                                            .append("invoiceAddress=").append(receiptAddress).append("&")
                                            .append("invoiceContact=").append(contactName).append("&")
                                            .append("invoiceContactTel=").append(contactPhone).append("&")
                                            .append("bankID=").append("812").append("&")
                                            .append("invoiceContactMobilePhone=").append(contactCellPhone).append("&")
                                            .append("invoiceContactEmail=").append(contactEmail).append("&")
                                            .append("privateData=").append(libm).append("&")
                                            .append("callerInMac=").append("38dc7020a508b59ba409615273495ab5").append("&")
                                            .append("isMd5Match=").append("true");

                            log.info("originalParm==>" + originalParm);

//                        String epay_cpid = new ShareParm().PARM_EPAY_CPID;
                            String data = (new SecuredMsg(new ShareParm().PARM_PG_KEY, new ShareParm().PARM_PG_IDENT).encode(originalParm.toString()));
                            postToNextPage(cpid, data);
                        } else {
                            log.info("insertranflag===>" + insertranflag);
                            log.info("insertinvflag===>" + insertinvflag);
                        }

                    } else {
                        //如果有編號,檢查是否已經交易, 如果還未交易可在更新資料。
                        log.info("The Libm is exist ===>" + libm);
                    }

                } else {
                    Messagebox.show("請輸入正確的檢核碼", "亞太電信", Messagebox.OK, Messagebox.ERROR);
                    chkcaptcha.randomValue();
                    chkcode.setValue("");
                    chkcode.setFocus(true);
                }
            } else {
                log.info("該門號餘額已超過限額上限金額$5000, 不可儲值");
                this.detach();
                Executions.sendRedirect("/nosso/OnlineNoSSO_PricePlanCodeErrorMsg.zhtml");
            }
        } catch (Exception ex) {
            log.info(ex);
        }

    }

    public void sendCreditCardOrder() {

        SendtoNPGW(ShareParm.PAYMETHOD_CREDITCARD);

    }

    public void sendATMOrder() {

        SendtoNPGW(ShareParm.PAYMETHOD_ATM);

    }

    private void postToNextPage(String systemid, String data) {

        try {
            Sessions.getCurrent().setAttribute("system", systemid);
            Sessions.getCurrent().setAttribute("data", data);

            String sendURL = new ShareParm().PARM_CPECF_URL;
            log.info("systemid,data,sendURL===>" + systemid + "," + data + "," + sendURL);

            Sessions.getCurrent().setAttribute("sendURL", sendURL);
            Executions.getCurrent().setAttribute(org.zkoss.zk.ui.sys.Attributes.NO_CACHE, Boolean.TRUE);

            Window window = (Window) Executions.createComponents("/deposit/OnlineVASDepositSendECF.zul", null, null);
        } catch (Exception ex) {
            log.info(ex);
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

    public boolean checkPricePlanByServiceId(String serviceid, int icpid, String mdn, String promotioncode) throws Exception {

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

        log.info(mdn + " checkPricePlanByServiceId ==>" + serviceid + "," + platformtype);

        if (serviceinfo != null) {
            String priceplancode = serviceinfo.getPriceplancode();

            int db_check_value = serviceinfo.getCheck_value();
            double count1x = 0.0;
            double count2x = 0.0;
            double sumx = 0;
            log.info("serviceinfo.getCheck_value()==>" + db_check_value);

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

                        log.info("b650==>" + d_value_650 + ",b670==>" + d_value_670);
                        log.info("b750==>" + d_value_750);
                        count1x = d_value_650 + d_value_670;
                        count2x = d_value_750;

                    }

                    sumx = (double) count1x + (double) count2x;
                    log.info(mdn + ":count1x()==>" + count1x);
                    log.info(mdn + ":count2x()==>" + count2x);
                    log.info(mdn + ":sumx===>" + sumx);
                    log.info(mdn + ":db_check_value==>" + db_check_value);

                    double checkvalue = sumx + db_check_value;

                    log.info(mdn + ":sumx + db_check_value==>" + checkvalue);
                    if (checkvalue < 5000) {
                        result = true;
                    }

                } else {
                    result = true;
                }
            }
        } else {

        }
        return result;
    }
}
