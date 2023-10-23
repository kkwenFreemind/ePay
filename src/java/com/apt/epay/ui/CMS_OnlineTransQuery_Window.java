/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui;

import com.apt.epay.beans.CMSBean;
import com.apt.epay.beans.CWSReqBean;
import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.util.Base64Util;
import com.apt.util.CwsProfile;
import com.apt.util.SoaProfile;
import com.apt.util.TransDateUtil;
import com.epay.ejb.bean.EPAY_INVOICE;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class CMS_OnlineTransQuery_Window extends Window {

    private final Logger log = Logger.getLogger("EPAY");
//    private String pkey = "1122334455";
    private CMSBean cmsbean;
    private String storeid;
    private String salesid;
    private String invno;
    private String srcid;
    private String mdn;
    private String trnsdatetime;
    private String oc_checkpoint;

    public void onCreate() {

        Sessions.getCurrent().removeAttribute("cmsbean");
        cmsbean = new CMSBean();

        log.info("Creating Online Deposit...from IP " + Executions.getCurrent().getRemoteAddr());
        HttpServletRequest req = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
        log.debug("doAfterCompose.getQueryString():" + req.getQueryString());

        String PROXY_FLAG = new ShareParm().PARM_SCT_PROXY_FLAG;

        boolean proflag = true;
        boolean chkTimeStamp = true;

        if (req.getParameter("SS") == null) {
            log.error("Parameter SS is null!");
            proflag = false;
        } else {
            String encryptMsg = (String) req.getParameter("SS");
            String PKEY = (String) req.getParameter("PKEY");
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
                                log.error("StoreID為空值!");
                                Sessions.getCurrent().setAttribute("errorMsg", "SalesID為空值!");
                                Executions.getCurrent().forward("BusinessRoleError.zul");
                                return;
                            } else {
                                cmsbean.setSaleid(salesid);
                            }
                        }

                        if ("OCCHECKPOING".equalsIgnoreCase(parms[0])) {
                            oc_checkpoint = parms.length == 1 ? "" : parms[1];
                            log.info("OCCHECKPOING==>" + oc_checkpoint);
                            if ("PROD".equalsIgnoreCase(PROXY_FLAG)) {
                                if ("".equals(oc_checkpoint)) {
                                    log.error("OCCHECKPOING!");
                                    Sessions.getCurrent().setAttribute("errorMsg", "OCCHECKPOING為空值!");
                                    Executions.getCurrent().forward("BusinessRoleError.zul");
                                    return;
                                } else {
                                    chkTimeStamp = false;
                                    SimpleDateFormat sdf_HH_mm = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT8);
                                    trnsdatetime = sdf_HH_mm.format(new Date());
                                    cmsbean.setTransdatetime(trnsdatetime);
//                                cmsbean.setTransdatetime(oc_checkpoint);
                                }
                            }
                        }

                        if ("TIMESTAMP".equalsIgnoreCase(parms[0]) && chkTimeStamp) {
                            trnsdatetime = parms.length == 1 ? "" : parms[1];
                            log.info("TIMESTAMP==>" + trnsdatetime);

                            if ("".equals(trnsdatetime)) {

                                //debug
                                if ("PROD".equalsIgnoreCase(PROXY_FLAG)) {
                                    log.error("TIMESTAMP!");
                                    Sessions.getCurrent().setAttribute("errorMsg", "TIMESTAMP為空值!");
                                    Executions.getCurrent().forward("BusinessRoleError.zul");
                                    //傳入參數值如果TIMESTAMP在前面，就無法吃到OCCHECKPOING，所以不要跳出去
                                    //return;
                                }
                            } else {
                                cmsbean.setTransdatetime(trnsdatetime);
                            }
                        }
                    }
                } catch (Exception ex) {
                    proflag = false;
                    log.info(ex);
                }
            } else {
                this.detach();
                Executions.sendRedirect("/cms/OnlineSSOErrorMsg.zhtml");
            }

            //debug
            if ("TDE".equalsIgnoreCase(PROXY_FLAG)) {
                String tde_mdn = new ShareParm().PARM_TDE_MDN;

                mdn = tde_mdn;

                Long datetime = System.currentTimeMillis();
                Timestamp timestamp = new Timestamp(datetime);

                trnsdatetime = timestamp.toString();

                log.info("This is ===================>" + PROXY_FLAG);
                log.info("mdn  is ===================>" + mdn);
                log.info("trnsdatetime is ===========>" + trnsdatetime);
            }

            EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

            Listbox listbox_tx_log = (Listbox) this.getFellow("listbox_tx_log");
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

            String result = "";

            String cwsresult = "";
            try {
                String cdate = "";

                CwsProfile cws = new CwsProfile();

                //debug
                if ("PROD".equalsIgnoreCase(PROXY_FLAG)) {
                    cwsresult = cws.putCwsProxyletByMDN(mdn);
                    if (cwsresult != null) {
                        CWSReqBean cwsbean = new CWSReqBean();
                        cwsbean = cws.parseXMLString(cwsresult);
                        cdate = cwsbean.getContractActDate();
                    }
                    log.info(String.format("cwsresult=" + cwsresult));
                }

                // 顯示被儲值門號 //
                Label mdnLabel = (Label) this.getFellow("mdnLabel");
                mdnLabel.setValue(mdn);

                SoaProfile soa = new SoaProfile();
                result = soa.putSoaProxyletByMDN(mdn);
                SOAReqBean apirequestbean = new SOAReqBean();
                apirequestbean = soa.parseXMLString(result);

                log.info("kkflag==>CMSTransQuery:" + result);

                mdn = apirequestbean.getMdn();
                log.info("Transaction Information Query By using MDN:" + mdn);

                String pattern1 = "yyyy-MM-dd HH:mm:ss";
                String pattern2 = "yyyyMMddHHmmss";
                SimpleDateFormat sdf1 = new SimpleDateFormat(pattern1);
                SimpleDateFormat sdf2 = new SimpleDateFormat(pattern2);
                Date parseDate = new SimpleDateFormat(pattern2).parse(trnsdatetime);
                String newdate = sdf1.format(parseDate);
                log.info("TransDateTime reformat ==>" + newdate);

                TransDateUtil transdateutil = new TransDateUtil();
                boolean flag = transdateutil.getDiffTime(newdate);

                //debug
                if ("TDE".equalsIgnoreCase(PROXY_FLAG)) {
                    flag = true;
                    log.info("This is ===================>" + PROXY_FLAG);
                    log.info("flag is ===================>" + flag);
                }

                if (flag != true) {
                    if (oc_checkpoint != null) {
                        //Do nothing
                    } else {
                        log.info("超過連線時間10分鐘，需重新登入交易");
                        this.detach();
                        Executions.sendRedirect("/cms/OnlineTimeoutErrorMsg.zhtml");
                    }
                } else {
                    List list;
                    if (cdate == null || cdate.equals("")) {
                        log.info("cwsbean.getContractActDate() IS NULL!!");
                        list = epaybusinesscontroller.getTxLogListByMDN(mdn);
                    } else {
                        list = epaybusinesscontroller.getTxLogListByMDNAndCDate(mdn, cdate);
                    }

                    log.info(mdn + " QueryList==>" + list.size());

                    if (list != null && !list.isEmpty()) {
                        Iterator it = list.iterator();
                        while (it.hasNext()) {
                            EPAY_TRANSACTION transaction = new EPAY_TRANSACTION();
                            transaction = (EPAY_TRANSACTION) it.next();
                            Listitem listitemtx_log = new Listitem();
                            String status = "", paystatus = "", amount = "";
                            new Listcell(transaction.getLibm()).setParent(listitemtx_log);
                            new Listcell(convertStringToDate(transaction.getTradedate())).setParent(listitemtx_log);
                            new Listcell(transaction.getItemproductname()).setParent(listitemtx_log);

                            String paytool = "";
                            if (transaction.getPaytool() != null) {
                                if (!"".equals(transaction.getPaytool())) {
                                    int ipaytool = Integer.valueOf(transaction.getPaytool());
                                    String payname = transaction.getPayname();
                                    paytool = getPayToolDesc(ipaytool, payname);
                                }
                            }
                            new Listcell(paytool).setParent(listitemtx_log);

                            String errorDesc = "";
                            if (transaction.getStatus().equals("00")) {
                                status = "成功";
                            } else {
                                status = "失敗";
                                errorDesc = transaction.getErrdesc();
                            }
                            new Listcell(status).setParent(listitemtx_log);
                            new Listcell(errorDesc).setParent(listitemtx_log);

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

                            if (transaction.getPayamount() == null) {
                                new Listcell(amount).setParent(listitemtx_log);
                            } else {
                                new Listcell(String.valueOf(transaction.getPayamount())).setParent(listitemtx_log);
                            }
                            new Listcell(transaction.getPoscode()).setParent(listitemtx_log);
                            new Listcell(transaction.getPossaleid()).setParent(listitemtx_log);

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

                            if (transaction.getPlatformtype() == 1) {
                                new Listcell("ALU(4G)").setParent(listitemtx_log);
                            } else if (transaction.getPlatformtype() == 2) {
                                new Listcell("ZTE(4G)").setParent(listitemtx_log);
                            } else if (transaction.getPlatformtype() == 3) {
                                new Listcell("NOKIA").setParent(listitemtx_log);
                            }

                            EPAY_INVOICE invoice;
                            invoice = epaybusinesscontroller.listInvoiceByInvoicenoLibm(transaction.getLibm());
                            String invoice_no = "";
                            if (invoice != null) {
                                invoice_no = invoice.getInvoice_no();
                            }
                            new Listcell(invoice_no).setParent(listitemtx_log);

                            listitemtx_log.setParent(listbox_tx_log);

                        }
                    }
                }
            } catch (Exception ex) {
//                log.info(String.format("CwsProfile() Exception cwsresult=%s", cwsresult));
                log.info(ex);

            }
        }
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

    public void backURL() {
        String tmpurl = new ShareParm().PARM_ECARE_URL;
        if (tmpurl != null) {

        } else {
            log.info("ECARE URL IS NULL");
            tmpurl = "https://pv.www.aptg.com.tw/ecare/ecHome.seam";
        }
        log.info("ECARE URL==>" + tmpurl);
        Executions.getCurrent().sendRedirect(tmpurl);

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
}
