/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.epay.ejb.bean.EPAY_INVOICE;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Textbox;

/**
 *
 * @author kevinchang
 */
public class Tx_ibon_query_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    public void onCreate() {

        SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Datebox start_db = (Datebox) this.getFellow("start_db");
        Timebox start_tb = (Timebox) this.getFellow("start_tb");
        Datebox end_db = (Datebox) this.getFellow("end_db");
        Timebox end_tb = (Timebox) this.getFellow("end_tb");
        Calendar cal = Calendar.getInstance();
//        cal.add((GregorianCalendar.MONTH), -2);
        cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
        Date beginTime = cal.getTime();
        String beginTime1 = datef.format(beginTime) + " 00:00:00";
        Date ccdate = new Date();
        try {
            ccdate = sdf.parse(beginTime1);
        } catch (Exception ex) {
            log.info(ex);
        }
        start_db.setValue(ccdate);
        start_tb.setValue(ccdate);
        end_db.setValue(new Date());
        end_tb.setValue(new Date());

        Combobox cmbStatus = (Combobox) this.getFellow("cmbStatus");
        cmbStatus.setValue("全部");

    }

    public void onClear() {

        Datebox start_db = (Datebox) this.getFellow("start_db");
        Timebox start_tb = (Timebox) this.getFellow("start_tb");
        Datebox end_db = (Datebox) this.getFellow("end_db");
        Timebox end_tb = (Timebox) this.getFellow("end_tb");
        Listbox listbox_tx_log = (Listbox) this.getFellow("listbox_tx_log");
        Textbox text_mdn = (Textbox) this.getFellow("text_mdn");
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

        start_db.setValue(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24));
        start_tb.setValue(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24));
        end_db.setValue(new Date());
        end_tb.setValue(new Date());
        text_mdn.setValue("");

        Combobox cmbStatus = (Combobox) this.getFellow("cmbStatus");
        cmbStatus.setValue("全部");

    }

    public void doExport() {
        if (Messagebox.show("確認匯出嗎?", "亞太電信CP金流", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) == Messagebox.YES) {

            log.info("tx_ibon_query.doExport()===>from IP " + Executions.getCurrent().getRemoteAddr());
            EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
            Datebox start_db = (Datebox) this.getFellow("start_db");
            Timebox start_tb = (Timebox) this.getFellow("start_tb");
            Datebox end_db = (Datebox) this.getFellow("end_db");
            Timebox end_tb = (Timebox) this.getFellow("end_tb");
            Textbox text_mdn = (Textbox) this.getFellow("text_mdn");

            Combobox cmbStatus = (Combobox) this.getFellow("cmbStatus");
            String ResultStatus = cmbStatus.getText();
            log.info("cmbStatus.getText() ===>" + cmbStatus.getText());

            Listbox listbox_tx_log = (Listbox) this.getFellow("listbox_tx_log");
            Date start_date;
            Date start_time;
            Date end_date;
            Date end_time;
            SimpleDateFormat datesdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timesdf = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startdatetime = "", enddatetime = "";
//
//            if (start_db.getValue() != null && start_tb.getValue() != null) {
//                start_date = start_db.getValue();
//                start_time = start_tb.getValue();
//                startdatetime = datesdf.format(start_date) + " " + timesdf.format(start_time);
//            }
//
//            if (end_db.getValue() != null && end_tb.getValue() != null) {
//                end_date = end_db.getValue();
//                end_time = end_tb.getValue();
//                enddatetime = datesdf.format(end_date) + " " + timesdf.format(end_time);
//            }

            if (start_db.getValue() != null && start_tb.getValue() != null) {
                start_date = start_db.getValue();
                start_time = start_tb.getValue();
                startdatetime = datesdf.format(start_date) + " " + timesdf.format(start_time);
            }

            if (end_db.getValue() != null && end_tb.getValue() != null) {
                end_date = end_db.getValue();
                end_time = end_tb.getValue();
                enddatetime = datesdf.format(end_date) + " " + timesdf.format(end_time);
            }

            SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            cal.add((GregorianCalendar.MONTH), -2);
            cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
            Date beginTime = cal.getTime();
            String beginTime1 = datef.format(beginTime) + " 00:00:00";

            if (beginTime1.compareTo(startdatetime) <= 0) {
                log.info("beginTime1.compareTo(startdatetime) <= 0 TRUE");
                log.info("startdatetime == >" + startdatetime);
                log.info("beginTime1====== >" + beginTime1);
            } else {
                log.info("beginTime1.compareTo(startdatetime) >=0");
                log.info("startdatetime == >" + startdatetime);
                log.info("beginTime1====== >" + beginTime1);

                Messagebox.show("日期區間大於三個月，系統已調整起日!!", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
                startdatetime = beginTime1;
                start_db.setValue(beginTime);
            }

            try {
                String mdn = text_mdn.getValue();

                List list = null;
                if ("全部".equals(ResultStatus)) {
                    if ("".equalsIgnoreCase(mdn)) {
                        list = epaybusinesscontroller.getTxIbonListByDate(startdatetime, enddatetime);
                    } else {
                        list = epaybusinesscontroller.getTxIbonListByDateByMdn(startdatetime, enddatetime, mdn);
                    }
                } else if ("成功".equals(ResultStatus)) {
                    String kkstatus1 = "='00'";
                    if ("".equalsIgnoreCase(mdn)) {
                        list = epaybusinesscontroller.getTxIbonListByDateByStatus(startdatetime, enddatetime, kkstatus1);
                    } else {
                        list = epaybusinesscontroller.getTxIbonListByDateByMdnByStatus(startdatetime, enddatetime, mdn, kkstatus1);
                    }
                } else {
                    String kkstatus2 = "!='00'";
                    if ("".equalsIgnoreCase(mdn)) {
                        list = epaybusinesscontroller.getTxIbonListByDateByStatus(startdatetime, enddatetime, kkstatus2);
                    } else {
                        list = epaybusinesscontroller.getTxIbonListByDateByMdnByStatus(startdatetime, enddatetime, mdn, kkstatus2);
                    }
                }

                Calendar nowDateTime = Calendar.getInstance();
                SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyyyMMdd");
                String ref = "APT_IBON_" + sdf15.format(nowDateTime.getTime()) + ".csv";
                String filePath = "/tmp/";
                String fileName = ref;
                String fullfilename = filePath + fileName;

                File filex;// = null;
                filex = new File(fullfilename);
                if (filex.exists()) {
                    log.info(fullfilename + " is exist, then delete()");
                    filex.delete();
                }

                // “繳費狀態”名稱異動為”服務費金額” 儲值結果 = 成功，才顯示5元
                //“金額(元)” 名稱異動為 “儲值卡金額(元)””
                String title = "交易序號,交易日期,儲值方案,交易結果,ErrorDesc,儲值方式,服務費金額,儲值卡售價,交易業者,進件門號,OCS" + "\r\n";
                WriteFile(title, fullfilename, true);

                if (list != null && !list.isEmpty()) {
                    Iterator it = list.iterator();
                    while (it.hasNext()) {
                        EPAY_TRANSACTION transaction = new EPAY_TRANSACTION();
                        transaction = (EPAY_TRANSACTION) it.next();

                        String status = "", paystatus = "", amount = "";

//                        if (transaction.getStatus().equals("00")) {
                        String paytool = "";
                        if (transaction.getPaytool() != null) {
                            if (!"".equals(transaction.getPaytool())) {
                                int ipaytool = Integer.valueOf(transaction.getPaytool());
                                String payname = transaction.getPayname();
                                paytool = getPayToolDesc(ipaytool, payname);
                            }
                        } else {

                        }

                        String errorDesc = "";
                        String serviceAmount = "";
                        if (transaction.getStatus().equals("00")) {
                            status = "成功";
                            serviceAmount = "5";
                        } else {
                            status = "失敗";
                            errorDesc = transaction.getErrdesc();
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

                        if (transaction.getPayamount() == null) {

                        }
                        String platformtype = "";
                        if (transaction.getPlatformtype() == 1) {
                            platformtype = "ALU";
                        } else {
                            platformtype = "ZTE";
                        }
                        //交易序號,交易日期,儲值方案,付款方式,儲值結果,ErrorDesc,儲值方式,繳費狀態,金 額(元),銷售點代碼,進件門號,儲值系統
                        // “繳費狀態”名稱異動為”服務費金額” 儲值結果 = 成功，才顯示5元
                        //“金額(元)” 名稱異動為 “儲值卡金額(元)”
                        String outputstr = transaction.getLibm() + ","//交易序號
                                        + convertStringToDate(transaction.getTradedate()) + ","//交易日期
                                        + "超商立即儲" + ","//儲值方案,
                                        //                                            +paytool+","//付款方式,
                                        + status + "," //儲值結果
                                        + errorDesc + "," //ErrorDesc,
                                        + "API儲值" + "," //儲值方式,
                                        + serviceAmount + ","//“繳費狀態”名稱異動為”服務費金額”
                                        + transaction.getPayamount() + "," //金 額(元)
                                        + "中礦" + "," //交易業者
                                        + transaction.getInvoiceContactMobilePhone() + "," //進件門號
                                        + platformtype + "\r\n"; //儲值系統
                        log.info("outputstr==>" + outputstr);
                        WriteFile(outputstr, fullfilename, true);

                    }
//                    }
                    File file_dst_file = new File(fullfilename);
                    if (file_dst_file.exists()) {
                        Filedownload.save(file_dst_file, null);
                    }
                } else {
                    try {
                        Messagebox.show("查詢無任何結果!!", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
                    } catch (Exception ex) {
                        log.info(ex);
                    }
                }

            } catch (Exception ex) {
                log.info(ex);
            }
        }
    }

    public void doQuery() {

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        Datebox start_db = (Datebox) this.getFellow("start_db");
        Timebox start_tb = (Timebox) this.getFellow("start_tb");
        Datebox end_db = (Datebox) this.getFellow("end_db");
        Timebox end_tb = (Timebox) this.getFellow("end_tb");
        Textbox text_mdn = (Textbox) this.getFellow("text_mdn");

        Combobox cmbStatus = (Combobox) this.getFellow("cmbStatus");
        String ResultStatus = cmbStatus.getText();
        log.info("cmbStatus.getText() ===>" + cmbStatus.getText());

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

        Date start_date;
        Date start_time;
        Date end_date;
        Date end_time;
        SimpleDateFormat datesdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timesdf = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startdatetime = "", enddatetime = "";

//        if (start_db.getValue() != null && start_tb.getValue() != null) {
//            start_date = start_db.getValue();
//            start_time = start_tb.getValue();
//            startdatetime = datesdf.format(start_date) + " " + timesdf.format(start_time);
//        }
//
//        if (end_db.getValue() != null && end_tb.getValue() != null) {
//            end_date = end_db.getValue();
//            end_time = end_tb.getValue();
//            enddatetime = datesdf.format(end_date) + " " + timesdf.format(end_time);
//        }
        if (start_db.getValue() != null && start_tb.getValue() != null) {
            start_date = start_db.getValue();
            start_time = start_tb.getValue();
            startdatetime = datesdf.format(start_date) + " " + timesdf.format(start_time);
        }

        if (end_db.getValue() != null && end_tb.getValue() != null) {
            end_date = end_db.getValue();
            end_time = end_tb.getValue();
            enddatetime = datesdf.format(end_date) + " " + timesdf.format(end_time);
        }

        SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add((GregorianCalendar.MONTH), -2);
        cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
        Date beginTime = cal.getTime();
        String beginTime1 = datef.format(beginTime) + " 00:00:00";

        if (beginTime1.compareTo(startdatetime) <= 0) {
            log.info("beginTime1.compareTo(startdatetime) <= 0 TRUE");
            log.info("startdatetime == >" + startdatetime);
            log.info("beginTime1====== >" + beginTime1);
        } else {
            log.info("beginTime1.compareTo(startdatetime) >=0");
            log.info("startdatetime == >" + startdatetime);
            log.info("beginTime1====== >" + beginTime1);

            Messagebox.show("日期區間大於三個月，系統已調整起日!!", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
            startdatetime = beginTime1;
            start_db.setValue(beginTime);
        }

        try {
            String mdn = text_mdn.getValue();
            List list = null;

            if ("全部".equals(ResultStatus)) {
                if ("".equalsIgnoreCase(mdn)) {
                    list = epaybusinesscontroller.getTxIbonListByDate(startdatetime, enddatetime);
                } else {
                    list = epaybusinesscontroller.getTxIbonListByDateByMdn(startdatetime, enddatetime, mdn);
                }
            } else if ("成功".equals(ResultStatus)) {
                String kkstatus1 = "='00'";
                if ("".equalsIgnoreCase(mdn)) {
                    list = epaybusinesscontroller.getTxIbonListByDateByStatus(startdatetime, enddatetime, kkstatus1);
                } else {
                    list = epaybusinesscontroller.getTxIbonListByDateByMdnByStatus(startdatetime, enddatetime, mdn, kkstatus1);
                }
            } else {
                String kkstatus2 = "!='00'";
                if ("".equalsIgnoreCase(mdn)) {
                    list = epaybusinesscontroller.getTxIbonListByDateByStatus(startdatetime, enddatetime, kkstatus2);
                } else {
                    list = epaybusinesscontroller.getTxIbonListByDateByMdnByStatus(startdatetime, enddatetime, mdn, kkstatus2);
                }
            }

            if (list != null && !list.isEmpty()) {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    EPAY_TRANSACTION transaction = new EPAY_TRANSACTION();
                    transaction = (EPAY_TRANSACTION) it.next();
                    Listitem listitemtx_log = new Listitem();
                    String status = "", paystatus = "", amount = "";
                    String ik = transaction.getApisrcid();
                    String kkstr = getPayArcsID(Integer.valueOf(ik));
                    log.info("transaction.getApisrcid() ==>" + transaction.getApisrcid());
                    log.info("getPayArcsID ==>" + kkstr);
                    if ("".equals(kkstr)) {
                        log.info("transaction.getApisrcid() ==>" + transaction.getApisrcid());
                        kkstr = "超商立即儲";
                    }

//                    if (transaction.getStatus().equals("00")) {
                    new Listcell(transaction.getLibm()).setParent(listitemtx_log);
                    new Listcell(convertStringToDate(transaction.getTradedate())).setParent(listitemtx_log);
                    new Listcell(kkstr).setParent(listitemtx_log);

                    String paytool = "";

//                        new Listcell(paytool).setParent(listitemtx_log);
                    String errorDesc = "";
                    String serviceAmount = "";
                    if (transaction.getStatus().equals("00")) {
                        status = "成功";
                        serviceAmount = "5";
                    } else {
                        status = "失敗";
                        errorDesc = transaction.getErrdesc();
                    }
                    new Listcell(status).setParent(listitemtx_log);
                    new Listcell(errorDesc).setParent(listitemtx_log);

                    String arcid = "API 儲值";

                    new Listcell(arcid).setParent(listitemtx_log);

                    //“繳費狀態”名稱異動為”服務費金額” 儲值結果 = 成功，才顯示5元
                    new Listcell(serviceAmount).setParent(listitemtx_log);

                    if (transaction.getPayamount() == null) {
                        new Listcell(amount).setParent(listitemtx_log);
                    } else {
                        new Listcell(String.valueOf(transaction.getPayamount())).setParent(listitemtx_log);
                    }
                    new Listcell("中礦").setParent(listitemtx_log);
                    new Listcell(transaction.getInvoiceContactMobilePhone()).setParent(listitemtx_log);
                    if (transaction.getPlatformtype() == 1) {
                        new Listcell("ALU").setParent(listitemtx_log);
                    } else {
                        new Listcell("ZTE").setParent(listitemtx_log);
                    }
                    listitemtx_log.setParent(listbox_tx_log);
                }
//                }
            } else {
                try {
                    Messagebox.show("查詢無任何結果!!", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
                } catch (Exception ex) {
                    log.info(ex);
                }
            }
        } catch (Exception ex) {
            log.info(ex);
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
        if (paytool == 13) {
            return "IBon";
        }
        return result;
    }

    public static void WriteFile(String str, String path, boolean append) { // 寫檔

        try {
            File file = new File(path);// 建立檔案，準備寫檔
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), "big5"));// 設定為BIG5格式
            writer.write(str); // 寫入該字串
            writer.close();
        } catch (IOException e) {
            log.info(e);
            System.out.println(path + "寫檔錯誤!!");
        }

    }
}
