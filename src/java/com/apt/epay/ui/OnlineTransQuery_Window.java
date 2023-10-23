/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui;

import com.apt.epay.beans.CWSReqBean;
import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.util.CwsProfile;
import com.apt.util.SoaProfile;
import com.epay.ejb.bean.EPAY_INVOICE;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class OnlineTransQuery_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    public void onCreate() {

        log.info("Creating Online Deposit...from IP " + Executions.getCurrent().getRemoteAddr());
        String uuid = "";
        uuid = ((HttpServletRequest) Executions.getCurrent().getNativeRequest()).getRemoteUser();
        log.info("PinCodeProces(init)==>IP:" + Executions.getCurrent().getRemoteAddr() + ",UUID:" + uuid);

        String PROXY_FLAG = new ShareParm().PARM_SCT_PROXY_FLAG;

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        String mdn = "";

        Listbox listbox_tx_log = (Listbox) this.getFellow("listbox_tx_log");
        SimpleDateFormat kksdf = new SimpleDateFormat("yyyy-MM-dd");

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

            SoaProfile soa = new SoaProfile();
            String result = soa.putSoaProxylet(uuid);
            SOAReqBean apirequestbean = new SOAReqBean();
            apirequestbean = soa.parseXMLString(result);

            log.info("kkflag==>TransQuery:" + result);

            mdn = apirequestbean.getMdn();
            log.info("Transaction Information Query By using MDN:" + mdn);
            SimpleDateFormat datesdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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

            log.info(mdn + " QueryList==>"+list.size());
            
            if (list != null && !list.isEmpty()) {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    EPAY_TRANSACTION transaction = new EPAY_TRANSACTION();
                    transaction = (EPAY_TRANSACTION) it.next();
                    Listitem listitemtx_log = new Listitem();
//                    tradedatetime = datesdf.format(transaction.getTradedate());

                    String status = "", paystatus = "", amount = "";
                    new Listcell(transaction.getLibm()).setParent(listitemtx_log);
                    new Listcell(convertStringToDate(transaction.getTradedate())).setParent(listitemtx_log);
                    new Listcell(transaction.getItemproductname()).setParent(listitemtx_log);

//                    String type = "";
//                    if (transaction.getPaymethod() == 1) {
//                        type = "信用卡";
//                    }
//                    if (transaction.getPaymethod() == 2) {
//                        type = "ATM";
//                    }
//                    if (transaction.getPaymethod() == 3) {
//                        type = "IBone";
//                    }
//                    if (transaction.getPaymethod() == 4) {
//                        type = "儲值卡";
//                    }
//                    if (transaction.getPaymethod() == 5) {
//                        type = "門市儲值";
//                    }
//                    if (transaction.getPaymethod() == 6) {
//                        type = "餘額扣抵";
//                    }
                    String paytool = "";
                    if (transaction.getPaytool() != null) {
                        if (!"".equals(transaction.getPaytool())) {
                            int ipaytool = Integer.valueOf(transaction.getPaytool());
                            String payname = transaction.getPayname();
                            paytool = getPayToolDesc(ipaytool, payname);
                        }
                    }
                    new Listcell(paytool).setParent(listitemtx_log);

                    if (transaction.getStatus().equals("00")) {
                        status = "成功";
                    } else {
                        status = "失敗";
                    }
                    new Listcell(status).setParent(listitemtx_log);
//                    new Listcell(String.valueOf(transaction.getStatus())).setParent(listitemtx_log);
//                    new Listcell(String.valueOf(transaction.getCpName())).setParent(listitemtx_log);
//                    listitemtx_log.setParent(listbox_tx_log);                    
                    if (transaction.getPaystatus() != null) {
                        if (transaction.getPaystatus() == 1) {
                            paystatus = "已繳費";
                        } else {
                            paystatus = "未繳費";
                        }
                    } else {
                        paystatus = "";
                    }
                    new Listcell(paystatus).setParent(listitemtx_log);
//                    new Listcell(String.valueOf(transaction.getPaystatus())).setParent(listitemtx_log);

                    String kk_amount = "";
                    if (transaction.getPayamount() == null) {
                        kk_amount = amount;
                        new Listcell(amount).setParent(listitemtx_log);
                    } else {
                        kk_amount = String.valueOf(transaction.getPayamount());
                        new Listcell(String.valueOf(transaction.getPayamount())).setParent(listitemtx_log);
                    }

                    String arcid = "";
                    if (transaction.getApisrcid() != null) {
                        if (!"".equals(transaction.getApisrcid())) {
                            int iarcid = Integer.valueOf(transaction.getApisrcid());
                            arcid = getPayArcsID(iarcid);
                            if (iarcid == 5) {
                                if (!"".equals(transaction.getStorename())) {
                                    arcid = arcid + "(" + transaction.getStorename() + ")";
                                }
                            }
                        }
                    }
                    new Listcell(arcid).setParent(listitemtx_log);
//                    new Listcell(String.valueOf(transaction.getCpName())).setParent(listitemtx_log);

                    String libm = transaction.getLibm();
                    EPAY_INVOICE inv = new EPAY_INVOICE();
                    inv = epaybusinesscontroller.listInvoiceByInvoicenoLibm(libm);
                    String invnumber = "";
                    String invdate = "";
                    String unform = "";
                    String radomno = "";
                    String donate = "";

                    if (inv != null) {

                        invnumber = inv.getInvoice_no();
                        if (invnumber != null) {
                            invdate = kksdf.format(inv.getInvoice_created_date());
                            unform = inv.getUniform();
                            radomno = String.valueOf(inv.getRandom_no());
                            donate = inv.getDonate();
                            if (donate != null && donate.equals("Y")) {
                                invnumber = invnumber.substring(0, 7) + "***";
                            } else {
                                //for test
                            }
                        }
                    } else {
                        //do nothing
                    }


                    /*
                     <z:listheader label="發票號碼" sort="auto" style="font-size: 16px"/> 
                     <z:listheader label="發票日期" sort="auto" style="font-size: 16px"/> 
                     <z:listheader label="統一編號" sort="auto" style="font-size: 16px"/> 
                     <z:listheader label="隨機碼" sort="auto" style="font-size: 16px"/> 
                     <z:listheader label="發票金額(應稅)" sort="auto" style="font-size: 16px"/> 
                     */
                    new Listcell(invnumber).setParent(listitemtx_log);
                    new Listcell(invdate).setParent(listitemtx_log);
                    new Listcell(unform).setParent(listitemtx_log);
                    new Listcell(radomno).setParent(listitemtx_log);
//                    new Listcell(kk_amount).setParent(listitemtx_log);

                    listitemtx_log.setParent(listbox_tx_log);

                }
            } else {
//                try {
//                    Messagebox.show("查詢無任何結果!!", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
//                } catch (Exception ex) {
//                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.info(ex);
        }

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
        if (arcid == 8) {
            return "促案平台";
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
        if (paytool == 10) {
            if (!"".equals(payname)) {
                return "贈送(" + payname + ")";
            } else {
                return "贈送";
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

    public void backURL() {
        String tmpurl = new ShareParm().PARM_ECARE_URL;
        if (tmpurl != null) {

        } else {
            log.info("ECARE URL IS NULL");
            tmpurl = "https://pv.www.aptg.com.tw/ecare/ecHome.seam";
        }
        log.info("ECARE URL==>" + tmpurl);
        Executions.getCurrent().sendRedirect(tmpurl);

//        Executions.sendRedirect("/");
    }
}
