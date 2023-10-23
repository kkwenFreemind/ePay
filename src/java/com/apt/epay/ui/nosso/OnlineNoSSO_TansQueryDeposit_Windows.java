/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.nosso;

import com.apt.epay.beans.CWSReqBean;
import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.deposit.bean.NoSSOTxQueryReqBean;
import com.apt.epay.deposit.util.toolUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.util.CwsProfile;
import com.apt.util.SoaProfile;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class OnlineNoSSO_TansQueryDeposit_Windows extends Window {

    private final Logger log = Logger.getLogger("EPAY");

    SOAReqBean apirequestbean;
    private String mdn1, mdn2, cplibm, salesid, storeid, storename, apisrcid, stardatetime, enddatetime;

    private String contractstatuscode;
    private String promotioncode;
    private String producttype;
    private String libm;
    private String contractid;
    private String mdn;

    public void onCreate() {

        Sessions.getCurrent().removeAttribute("odBean");
        log.info("Creating Online Deposit...from IP " + Executions.getCurrent().getRemoteAddr());

        HttpServletRequest req = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
        log.info("doAfterCompose.getQueryString():" + req.getQueryString());

        toolUtil tool = new toolUtil();
        Utilities util = new Utilities();

        String cpid = req.getParameter("CPID");
        String data = req.getParameter("DATA");

        log.info("cpid==>" + cpid);
        log.info("data==>" + data);

        Listbox listbox_tx_log = (Listbox) this.getFellow("listbox_tx_log");
        SimpleDateFormat kksdf = new SimpleDateFormat("yyyy-MM-dd");

        String PROXY_FLAG = new ShareParm().PARM_SCT_PROXY_FLAG;

        try {

            AbstractComponent[] acs = (AbstractComponent[]) listbox_tx_log.getChildren().toArray(new AbstractComponent[0]);

            for (int i = 0; i < acs.length; i++) {
                if (acs[i] instanceof Listhead) {
                    continue;
                }
                listbox_tx_log.removeChild(acs[i]);
            }
        } catch (Exception ex) {
        }

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

                NoSSOTxQueryReqBean aPIServiceOrdrReqBean;
                aPIServiceOrdrReqBean = getMDNByparseXMLString(str_input, "ServiceTransQuery");
                mdn = aPIServiceOrdrReqBean.getMdn();
                apisrcid = aPIServiceOrdrReqBean.getApisrcid();
                salesid = aPIServiceOrdrReqBean.getSalesid();
                storeid = aPIServiceOrdrReqBean.getStoreid();
                storename = aPIServiceOrdrReqBean.getStorename();
                stardatetime = aPIServiceOrdrReqBean.getStardatetime();
                enddatetime = aPIServiceOrdrReqBean.getEnddatatime();

                if (mdn != null && !"".equals(mdn)) {

                    SoaProfile soa = new SoaProfile();
                    String result = soa.putSoaProxyletByMDN(mdn);
                    apirequestbean = soa.parseXMLString(result);

                    log.info("kkflag==>NoSSO_TransQuery:" + result);

                    String resultcode = apirequestbean.getResultcode();
                    contractid = apirequestbean.getContractid();
                    contractstatuscode = apirequestbean.getContract_status_code();
                    producttype = apirequestbean.getProducttype();
                    mdn = apirequestbean.getMdn();
                    promotioncode = apirequestbean.getPromotioncode();

                    if ("00000000".equals(resultcode)) {

                        String cdate = "";

                        CwsProfile cws = new CwsProfile();

                        if ("PROD".equalsIgnoreCase(PROXY_FLAG)) {

                            String tmp_cwsresult = cws.putCwsProxyletByMDN(mdn);
                            log.info("cwsresult====>" + tmp_cwsresult);
                            String cwsresult = tmp_cwsresult.trim();

                            if (cwsresult != null) {
                                CWSReqBean cwsbean = new CWSReqBean();
                                cwsbean = cws.parseXMLString(cwsresult);
                                log.info("cwsbean.getContractActDate()====>" + cwsbean.getContractActDate());
                                cdate = cwsbean.getContractActDate();
                            }
                        }

                        List list;
                        if (cdate == null || cdate.equals("")) {
                            log.info("cwsbean.getContractActDate() IS NULL!!");
                            list = epaybusinesscontroller.getTxLogListByMDN(mdn);
                        } else {
                            list = epaybusinesscontroller.getTxLogListByMDNAndCDate(mdn, cdate);
                        }

                        log.info("list size ====>" + list.size());

                        if (list != null && !list.isEmpty()) {
                            Iterator it = list.iterator();
                            while (it.hasNext()) {
                                EPAY_TRANSACTION transaction = new EPAY_TRANSACTION();
                                transaction = (EPAY_TRANSACTION) it.next();
                                Listitem listitemtx_log = new Listitem();
                                String status = "", paystatus = "", amount = "";

                                if (transaction.getStatus().equals("00")) {
                                    status = "成功";
                                } else {
                                    status = "失敗";
                                }

                                String arcid = "";
                                if (!"".equals(transaction.getPaytool())) {
                                    int iarcid = Integer.valueOf(transaction.getApisrcid());
                                    arcid = getPayArcsID(iarcid);
                                    if (iarcid == 5) {
                                        if (!"".equals(transaction.getStorename())) {
                                            arcid = arcid + "(" + transaction.getStorename() + ")";
                                        }
                                    }
                                }

                                Vbox myvbox = new Vbox();
//                                String ks
//                                        = transaction.getItemproductname() + "    $" + transaction.getOrdertotal() + "\r\n"
//                                        + "時間:" + transaction.getTradedate() + "\r\n"
//                                        + "訂單:" + transaction.getLibm() + "\r\n"
//                                        + "結果:" + status + "\r\n"
//                                        + "方式:" + arcid + "\r\n";

//                                Label aa = new Label(ks);
                                Label a1 = new Label(transaction.getItemproductname() + "    $" + transaction.getOrdertotal() + "\r\n");
                                Label a2 = new Label("💖時間:" + transaction.getTradedate() + "\r\n");
                                Label a3 = new Label("💖訂單:" + transaction.getLibm() + "\r\n");
                                Label a4 = new Label("💖結果:" + status + "\r\n");
                                Label a5 = new Label("💖方式:" + arcid + "\r\n");
//                                Label a0 = new Label("\n");
//                                new Listcell().appendChild(a1);

                                myvbox.appendChild(a1);
                                myvbox.appendChild(a2);
                                myvbox.appendChild(a3);
                                myvbox.appendChild(a4);
                                myvbox.appendChild(a5);

                                Listcell k1 = new Listcell();
                                k1.appendChild(myvbox);
//                                k1.appendChild(a0);
//                                k1.appendChild(a2);
//                                k1.appendChild(a3);
//                                k1.appendChild(a4);
//                                k1.appendChild(a5);
                                listitemtx_log.appendChild(k1);
                                listitemtx_log.setParent(listbox_tx_log);

//                                new Listcell(transaction.getLibm()).setParent(listitemtx_log);
//                                new Listcell(convertStringToDate(transaction.getTradedate())).setParent(listitemtx_log);
//                                new Listcell(transaction.getItemproductname()).setParent(listitemtx_log);
//                                String paytool = "";
//                                if (!"".equals(transaction.getPaytool())) {
//                                    int ipaytool = Integer.valueOf(transaction.getPaytool());
//                                    String payname = transaction.getPayname();
//                                    paytool = getPayToolDesc(ipaytool, payname);
//                                }
//                                new Listcell(paytool).setParent(listitemtx_log);
//
//                                if (transaction.getStatus().equals("00")) {
//                                    status = "成功";
//                                } else {
//                                    status = "失敗";
//                                }
//                                new Listcell(status).setParent(listitemtx_log);
////                    new Listcell(String.valueOf(transaction.getStatus())).setParent(listitemtx_log);
////                    new Listcell(String.valueOf(transaction.getCpName())).setParent(listitemtx_log);
////                    listitemtx_log.setParent(listbox_tx_log);                    
//                                if (transaction.getPaystatus() != null) {
//                                    if (transaction.getPaystatus() == 1) {
//                                        paystatus = "已繳費";
//                                    } else {
//                                        paystatus = "未繳費";
//                                    }
//                                } else {
//                                    paystatus = "";
//                                }
//                                new Listcell(paystatus).setParent(listitemtx_log);
////                    new Listcell(String.valueOf(transaction.getPaystatus())).setParent(listitemtx_log);
//
//                                String kk_amount = "";
//                                if (transaction.getPayamount() == null) {
//                                    kk_amount = amount;
//                                    new Listcell(amount).setParent(listitemtx_log);
//                                } else {
//                                    kk_amount = String.valueOf(transaction.getPayamount());
//                                    new Listcell(String.valueOf(transaction.getPayamount())).setParent(listitemtx_log);
//                                }
//
//
//                                new Listcell(arcid).setParent(listitemtx_log);
////                    new Listcell(String.valueOf(transaction.getCpName())).setParent(listitemtx_log);
//
//                                String libm = transaction.getLibm();
//                                EPAY_INVOICE inv = new EPAY_INVOICE();
//                                inv = epaybusinesscontroller.listInvoiceByInvoicenoLibm(libm);
//                                String invnumber = "";
//                                String invdate = "";
//                                String unform = "";
//                                String radomno = "";
//                                String donate = "";
//
//                                if (inv != null) {
//
//                                    invnumber = inv.getInvoice_no();
//                                    if (invnumber != null) {
//                                        invdate = kksdf.format(inv.getInvoice_created_date());
//                                        unform = inv.getUniform();
//                                        radomno = String.valueOf(inv.getRandom_no());
//                                        donate = inv.getDonate();
//                                        if (donate != null && donate.equals("Y")) {
//                                            invnumber = invnumber.substring(0, 7) + "***";
//                                        } else {
//                                            //for test
//                                        }
//                                    }
//                                } else {
//                                    //do nothing
//                                }
//
//
//                                /*
//                     <z:listheader label="發票號碼" sort="auto" style="font-size: 16px"/> 
//                     <z:listheader label="發票日期" sort="auto" style="font-size: 16px"/> 
//                     <z:listheader label="統一編號" sort="auto" style="font-size: 16px"/> 
//                     <z:listheader label="隨機碼" sort="auto" style="font-size: 16px"/> 
//                     <z:listheader label="發票金額(應稅)" sort="auto" style="font-size: 16px"/> 
//                                 */
//                                new Listcell(invnumber).setParent(listitemtx_log);
//                                new Listcell(invdate).setParent(listitemtx_log);
//                                new Listcell(unform).setParent(listitemtx_log);
//                                new Listcell(radomno).setParent(listitemtx_log);
//                    new Listcell(kk_amount).setParent(listitemtx_log);
                            }
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

    public NoSSOTxQueryReqBean getMDNByparseXMLString(String xmlRecords, String TagName) throws Exception {
        NoSSOTxQueryReqBean nossobean = new NoSSOTxQueryReqBean();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));

            Document doc = db.parse(is);
            NodeList SOARes = doc.getElementsByTagName(TagName);

            for (int i = 0; i < SOARes.getLength(); i++) {

                Element element = (Element) SOARes.item(i);

                NodeList nodes = element.getElementsByTagName("MDN");
                Element line = (Element) nodes.item(0);
                nossobean.setMdn(getCharacterDataFromElement(line));

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

//                nodes = element.getElementsByTagName("STARTDATETIME");
//                line = (Element) nodes.item(0);
//                nossobean.setStardatetime(getCharacterDataFromElement(line));
//
//                nodes = element.getElementsByTagName("ENDDATETIME");
//                line = (Element) nodes.item(0);
//                nossobean.setEnddatatime(getCharacterDataFromElement(line));
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

    public String getPayArcsID(int arcid) {
        String result = "";
        //儲值方式(TRANSACTION.PAYAPISRCID)：(1)Gt網站(2)GtPay(3)Gt客服(4)Gt行動客服(5)Gt門市(XXX)(6)超商立即儲(7)代理商

        if (arcid == 1) {
            return "Gt網站";
        }
        if (arcid == 2) {
            return "GtPay";
        }
        if (arcid == 3) {
            return "Gt客服";
        }
        if (arcid == 4) {
            return "Gt行動客服";
        }
        if (arcid == 5) {
            return "Gt門市";
        }
        if (arcid == 6) {
            return "超商立即儲";
        }
        if (arcid == 7) {
            return "代理商";
        }

        return result;
    }

    public String getPayToolDesc(int paytool, String payname) {
        String result = "";

        //付款方式(TRANSACTION.PAYTOOL)：(1)信用卡(2)ATM(3)餘額抵扣(4)電信帳單(5)小額支付(6)現金(7)悠遊卡(8)一卡通(9)iCash(10)GtPay(11)玉山支付寶(12)u2bill
        if (paytool == 1) {
            return "信用卡";
        }
        if (paytool == 2) {
            return "ATM";
        }
        if (paytool == 3) {
            return "餘額抵扣";
        }
        if (paytool == 4) {
            return "電信帳單";
        }
        if (paytool == 5) {
            return "小額支付";
        }
        if (paytool == 6) {
            return "現金";
        }

        if (paytool == 7) {
            if (!"".equals(payname)) {
                return "電子票證(" + payname + ")";
            } else {
                return "電子票證";
            }
        }
        if (paytool == 8) {
            if (!"".equals(payname)) {
                return "第三方支付(" + payname + ")";
            } else {
                return "第三方支付";
            }
        }
        if (paytool == 9) {
            if (!"".equals(payname)) {
                return "GtPay(" + payname + ")";
            } else {
                return "GtPay";
            }
        }

        return result;
    }

    public String convertStringToDate(Date indate) {
        String dateString = null;
        SimpleDateFormat sdfr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            dateString = sdfr.format(indate);
        } catch (Exception ex) {
            log.info(ex);
        }
        return dateString;
    }
}
