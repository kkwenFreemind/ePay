/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.apt.util.TransUtil;
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
//import javax.net.ssl.HttpsURLConnection;
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
public class Tx_countrycode_query_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    public void onCreate() {

        SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
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
    }

    public void onSelect() {
        this.detach();
        Map model = new HashMap();
    }

    public void doExport() {
        if (Messagebox.show("確認匯出嗎?", "亞太電信CP金流", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) == Messagebox.YES) {

            log.info("tx_countrycode_query.doExport()===>from IP " + Executions.getCurrent().getRemoteAddr());
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

            try {
                String mdn = text_mdn.getValue();

//                List list = epaybusinesscontroller.getTxCountryCodeListByDate(startdatetime, enddatetime);
                List list = null;

                if ("全部".equals(ResultStatus)) {
                    if ("".equalsIgnoreCase(mdn)) {
                        list = epaybusinesscontroller.getTxCountryCodeListByDate(startdatetime, enddatetime);
                    } else {
                        list = epaybusinesscontroller.getTxCountryCodeListByDateByMdn(startdatetime, enddatetime, mdn);
                    }
                } else if ("成功".equals(ResultStatus)) {
                    String kkstatus1 = "='00'";
                    if ("".equalsIgnoreCase(mdn)) {
                        list = epaybusinesscontroller.getTxCountryCodeListByDateBystatus(startdatetime, enddatetime, kkstatus1);
                    } else {
                        list = epaybusinesscontroller.getTxCountryCodeListByDateByMdnByStatus(startdatetime, enddatetime, mdn, kkstatus1);
                    }
                } else {
                    String kkstatus2 = "!='00'";
                    if ("".equalsIgnoreCase(mdn)) {
                        list = epaybusinesscontroller.getTxCountryCodeListByDateBystatus(startdatetime, enddatetime, kkstatus2);
                    } else {
                        list = epaybusinesscontroller.getTxCountryCodeListByDateByMdnByStatus(startdatetime, enddatetime, mdn, kkstatus2);
                    }
                }

                Calendar nowDateTime = Calendar.getInstance();
                SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyyyMMdd");
                String ref = "APT_COUNTRYCODE_" + sdf15.format(nowDateTime.getTime()) + ".csv";
                String filePath = "/tmp/";
                String fileName = ref;
                String fullfilename = filePath + fileName;

                File filex;// = null;
                filex = new File(fullfilename);
                if (filex.exists()) {
                    log.info(fullfilename + " is exist, then delete()");
                    filex.delete();
                }

                String title = "交易序號(DTOne No,交易序號(Series No,交易日期(Date),儲值方案(Promotion),扣款門號(MDN),扣款金額,家鄉儲門號(oversea MDN),OperatorName,PriceID,儲值結果(result),實際扣款,ErrorDesc,國外儲值開始時間,PLN,PLN PIN,介面,OCS,wholesale" + "\r\n";
                WriteFile(title, fullfilename, true);
                TransUtil transUtil = new TransUtil();

                if (list != null && !list.isEmpty()) {
                    Iterator it = list.iterator();
                    while (it.hasNext()) {
                        EPAY_TRANSACTION transaction = new EPAY_TRANSACTION();
                        transaction = (EPAY_TRANSACTION) it.next();

                        String status = "", paystatus = "", amount = "0";

                        //if (transaction.getStatus().equals("00")) {
                        String paytool = "";
                        if (transaction.getPaytool() != null) {
                            if (!"".equals(transaction.getPaytool())) {
                                int ipaytool = Integer.valueOf(transaction.getPaytool());
                                String payname = transaction.getPayname();
                                //paytool = getPayToolDesc(ipaytool, payname);
                                paytool = transUtil.getPayToolDesc(ipaytool, payname);
                            }
                        } else {

                        }

                        String errorDesc = "";
                        if (transaction.getStatus().equals("00")) {
                            status = "成功";
                            errorDesc = "";
                            amount = String.valueOf(transaction.getPayamount());
                        } else {
                            status = "失敗";
                            errorDesc = transaction.getErrdesc();
                            amount = "0";
                        }

                        String arcid = "";
                        if (transaction.getApisrcid() != null) {
                            if (!"".equals(transaction.getApisrcid())) {
                                int iarcid = Integer.valueOf(transaction.getApisrcid());
                                arcid = transUtil.getPayArcsID(iarcid);
                            }
                        }

                        String platformtype = "";
                        if (transaction.getPlatformtype() == 1) {
                            platformtype = "ALU";
                        } else {
                            platformtype = "ZTE";
                        }

                        //交易序號(Series No)/交易日期(Date)/儲值方案(Promotion))/扣款門號(MDN)/扣款金額(balance)
                        ///家鄉除門號(oversea MDN)/OperatorName/PriceID/交易結果(result)/實際扣款(actual Deduct)/ErrorDesc/儲值方式/儲值系統
                        String outputstr = transaction.getDtoneid() + ","
                                        + transaction.getLibm() + "," //交易序號(Series No)
                                        + convertStringToDate(transaction.getTradedate()) + "," //交易日期(Date)
                                        + transaction.getItemproductname() + "," //儲值方案(Promotion)
                                        + transaction.getInvoiceContactMobilePhone() + "," //扣款門號(MDN)
                                        + transaction.getPayamount() + "," //扣款金額(balance)
                                        + transaction.getCountrycodemdn() + "," //家鄉除門號(oversea MDN)
                                        + transaction.getOperatiorname() + "," //OperatorName
                                        + transaction.getPriceid() + "," //PriceID
                                        + status + "," //交易結果(result)
                                        + amount + "," //實際扣款(actual Deduct)
                                        + errorDesc + "," //ErrorDesc
                                        + transaction.getDtone_date() + ","
                                        + transaction.getDtone_electricitybill() + ","
                                        + transaction.getDtone_code() + ","
                                        + arcid + "," //儲值方式
                                        + platformtype + ","
                                        + transaction.getDtone_prices() + "\r\n"; ///儲值系統

//                        log.info("outputstr==>" + outputstr);
                        WriteFile(outputstr, fullfilename, true);

                        // }
                    }
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

        Textbox text_mdn = (Textbox) this.getFellow("text_mdn");
        Datebox start_db = (Datebox) this.getFellow("start_db");
        Timebox start_tb = (Timebox) this.getFellow("start_tb");
        Datebox end_db = (Datebox) this.getFellow("end_db");
        Timebox end_tb = (Timebox) this.getFellow("end_tb");
        Combobox cmbStatus = (Combobox) this.getFellow("cmbStatus");
        String ResultStatus = cmbStatus.getText();
        log.info("cmbStatus.getText() ===>" + cmbStatus.getText());

        TransUtil transUtil = new TransUtil();

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
        try {
            String mdn = text_mdn.getValue();
            List list = null;

            if ("全部".equals(ResultStatus)) {
                if ("".equalsIgnoreCase(mdn)) {
                    list = epaybusinesscontroller.getTxCountryCodeListByDate(startdatetime, enddatetime);
                } else {
                    list = epaybusinesscontroller.getTxCountryCodeListByDateByMdn(startdatetime, enddatetime, mdn);
                }
            } else if ("成功".equals(ResultStatus)) {
                String kkstatus1 = "='00'";
                if ("".equalsIgnoreCase(mdn)) {
                    list = epaybusinesscontroller.getTxCountryCodeListByDateBystatus(startdatetime, enddatetime, kkstatus1);
                } else {
                    list = epaybusinesscontroller.getTxCountryCodeListByDateByMdnByStatus(startdatetime, enddatetime, mdn, kkstatus1);
                }
            } else {
                String kkstatus2 = "!='00'";
                if ("".equalsIgnoreCase(mdn)) {
                    list = epaybusinesscontroller.getTxCountryCodeListByDateBystatus(startdatetime, enddatetime, kkstatus2);
                } else {
                    list = epaybusinesscontroller.getTxCountryCodeListByDateByMdnByStatus(startdatetime, enddatetime, mdn, kkstatus2);
                }
            }

            if (list != null && !list.isEmpty()) {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    EPAY_TRANSACTION transaction = new EPAY_TRANSACTION();
                    transaction = (EPAY_TRANSACTION) it.next();
                    Listitem listitemtx_log = new Listitem();
                    String status = "", paystatus = "", amount = "";

                    //交易序號(Series No)/交易日期(Date)/儲值方案(Promotion))/扣款門號(MDN)/扣款金額(balance)/家鄉除門號(oversea MDN)/OperatorName/PriceID/交易結果(result)/實際扣款(actual Deduct)/ErrorDesc/儲值方式/儲值系統
                    new Listcell(transaction.getDtoneid()).setParent(listitemtx_log);//交易序號(Series No)
                    new Listcell(transaction.getLibm()).setParent(listitemtx_log);//交易序號(Series No)
                    new Listcell(convertStringToDate(transaction.getTradedate())).setParent(listitemtx_log);//交易日期(Date)
                    new Listcell(transaction.getItemproductname()).setParent(listitemtx_log);//儲值方案(Promotion)
                    new Listcell(transaction.getInvoiceContactMobilePhone()).setParent(listitemtx_log);//扣款門號(MDN)

                    if (transaction.getPayamount() == null) {
                        new Listcell(amount).setParent(listitemtx_log);
                    } else {
                        new Listcell(String.valueOf(transaction.getPayamount())).setParent(listitemtx_log);
                    } ///扣款金額(balance)

                    new Listcell(transaction.getCountrycodemdn()).setParent(listitemtx_log); ///家鄉除門號(oversea MDN)

                    new Listcell(transaction.getOperatiorname()).setParent(listitemtx_log);//OperatorName
                    new Listcell(transaction.getPriceid()).setParent(listitemtx_log);//PriceID

                    if (transaction.getStatus().equals("00")) {
                        status = "成功";
                        amount = String.valueOf(transaction.getPayamount());
                    } else {
                        status = "失敗";
                        amount = "0";
                    }
                    new Listcell(status).setParent(listitemtx_log);//交易結果(result)         
                    new Listcell(amount).setParent(listitemtx_log); //實際扣款(actual Deduct)

                    String errorDesc = "";
                    if (transaction.getStatus().equals("00")) {
                        errorDesc = "";
                    } else {
                        errorDesc = transaction.getErrdesc();
                    }
                    new Listcell(errorDesc).setParent(listitemtx_log); //ErrorDesc
                    new Listcell(transaction.getDtone_date()).setParent(listitemtx_log); //ErrorDesc

                    String arcid = "";
                    if (transaction.getApisrcid() != null) {
                        if (!"".equals(transaction.getApisrcid())) {
                            int iarcid = Integer.valueOf(transaction.getApisrcid());
                            arcid = transUtil.getPayArcsID(iarcid);
                        }
                    }
                    new Listcell(transaction.getDtone_electricitybill()).setParent(listitemtx_log);
                    new Listcell(transaction.getDtone_code()).setParent(listitemtx_log);
                    new Listcell(arcid).setParent(listitemtx_log); //儲值方式

                    if (transaction.getPlatformtype() == 1) {
                        new Listcell("ALU").setParent(listitemtx_log);
                    } else if (transaction.getPlatformtype() == 2) {
                        new Listcell("ZTE").setParent(listitemtx_log);
                    } else {
                        new Listcell("Other").setParent(listitemtx_log);
                    }

                    new Listcell(String.valueOf(transaction.getDtone_prices())).setParent(listitemtx_log); //儲值方式

                    listitemtx_log.setParent(listbox_tx_log);
                    //}
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
