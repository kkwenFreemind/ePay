/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class Tx_log_batchfile_query_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    public void onCreate() {

    }

    public void onClear() {

        Textbox textbox_batchfile = (Textbox) this.getFellow("textbox_batchfile");
        Label label_result = (Label) this.getFellow("label_result");
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

        textbox_batchfile.setValue("");
        label_result.setValue("");

    }

    public void doQuery() {

        Messagebox.show("僅顯示儲值失敗資訊!!", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        Textbox textbox_batchfile = (Textbox) this.getFellow("textbox_batchfile");
        Label label_result = (Label) this.getFellow("label_result");
        Listbox listbox_tx_log = (Listbox) this.getFellow("listbox_tx_log");
        int success_count = 0;
        int fail_count = 0;

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
            String tt_batchfile = textbox_batchfile.getValue() + ".csv";
            String batchfile = tt_batchfile.toLowerCase();
            log.info("batchfile ====>" + batchfile);
            List list = epaybusinesscontroller.getTxLogListByBatchFie(batchfile);
            if (list != null && !list.isEmpty()) {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    EPAY_TRANSACTION transaction;//= new EPAY_TRANSACTION();
                    transaction = (EPAY_TRANSACTION) it.next();
                    Listitem listitemtx_log = new Listitem();
                    String status = "", paystatus = "", amount = "";

                    if (!transaction.getStatus().equals("00")) {
                        new Listcell(transaction.getLibm()).setParent(listitemtx_log);
                        new Listcell(convertStringToDate(transaction.getTradedate())).setParent(listitemtx_log);
                        new Listcell(transaction.getItemproductname()).setParent(listitemtx_log);
                        new Listcell(transaction.getInvoiceContactMobilePhone()).setParent(listitemtx_log);
                        String errorDesc = "";
                        if (transaction.getStatus().equals("00")) {
                            status = "成功";
                            success_count = success_count + 1;
                        } else {
                            status = "失敗";
                            errorDesc = transaction.getErrdesc();
                            fail_count = fail_count + 1;

                        }

                        new Listcell(status).setParent(listitemtx_log);
                        new Listcell(errorDesc).setParent(listitemtx_log);

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
                            new Listcell("ALU").setParent(listitemtx_log);
                        } else if (transaction.getPlatformtype() == 2) {
                            new Listcell("ZTE").setParent(listitemtx_log);
                        } else if  (transaction.getPlatformtype() == 3) {
                            new Listcell("NOKIA").setParent(listitemtx_log);
                        }

                        listitemtx_log.setParent(listbox_tx_log);

                    } else {
                        success_count = success_count + 1;
                    }
                }
                label_result.setValue("總筆數:" + list.size() + ",成功筆數:" + success_count + ",失敗筆數:" + fail_count);
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

//    public String getPayToolDesc(int paytool, String payname) {
//        String result = "";
//
//        //付款方式(TRANSACTION.PAYTOOL)：(1)信用卡(2)ATM(3)餘額抵扣(4)電信帳單(5)小額支付(6)現金(7)悠遊卡(8)一卡通(9)iCash(10)GtPay(11)玉山支付寶(12)u2bill
//        if (paytool == 1) {
//            return "信用卡";
//        }
//        if (paytool == 2) {
//            return "ATM";
//        }
//        if (paytool == 3) {
//            return "餘額抵扣";
//        }
//        if (paytool == 4) {
//            return "電信帳單";
//        }
//        if (paytool == 5) {
//            return "小額支付";
//        }
//        if (paytool == 6) {
//            return "現金";
//        }
//
//        if (paytool == 7) {
//            if (!"".equals(payname)) {
//                return "電子票證(" + payname + ")";
//            } else {
//                return "電子票證";
//            }
//        }
//        if (paytool == 8) {
//            if (!"".equals(payname)) {
//                return "第三方支付(" + payname + ")";
//            } else {
//                return "第三方支付";
//            }
//        }
//        if (paytool == 9) {
//            if (!"".equals(payname)) {
//                return "GtPay(" + payname + ")";
//            } else {
//                return "GtPay";
//            }
//        }
//        if (paytool == 10) {
//            if (!"".equals(payname)) {
//                return "贈送(" + payname + ")";
//            } else {
//                return "贈送";
//            }
//        }
//        return result;
//    }
//    
    public void onExport() {
        Textbox textbox_batchfile = (Textbox) this.getFellow("textbox_batchfile");
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        String batchfile = textbox_batchfile.getValue() + ".csv";
        int success_count = 0;
        int fail_count = 0;

        if (batchfile != null) {
            try {

                List list = epaybusinesscontroller.getTxLogListByBatchFie(batchfile);
                if (Messagebox.show("確認匯出嗎?", "亞太電信CP金流", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) == Messagebox.YES) {
                    String ref = "APT_Export_" + batchfile + ".csv";
                    String filePath = "/tmp/";
                    String fullfilename = filePath + ref;

                    File filex;// = null;
                    filex = new File(fullfilename);
                    if (filex.exists()) {
                        log.info(fullfilename + " is exist, then delete()");
                        filex.delete();
                    }

                    String title = "交易序號,交易日期,儲值方案,儲值門號,儲值結果,ErrorDesc,儲值方式,金 額(元),儲值系統" + "\r\n";
                    WriteFile(title, fullfilename, true);

                    if (list != null && !list.isEmpty()) {
                        Iterator it = list.iterator();
                        while (it.hasNext()) {
                            EPAY_TRANSACTION transaction;//= new EPAY_TRANSACTION();
                            transaction = (EPAY_TRANSACTION) it.next();
                            String errorDesc = "";
                            String status = "";
                            if (transaction.getStatus().equals("00")) {
                                status = "成功";
                                success_count = success_count + 1;
                            } else {
                                status = "失敗";
                                errorDesc = transaction.getErrdesc();
                                fail_count = fail_count + 1;
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
//                    String kkamount = "";
//                    if (transaction.getPayamount() == null) {
//                        new Listcell(amount).setParent(listitemtx_log);
//                    } else {
//                        new Listcell(String.valueOf(transaction.getPayamount())).setParent(listitemtx_log);
//                    }
//                    new Listcell(transaction.getPoscode()).setParent(listitemtx_log);
//                    new Listcell(transaction.getPossaleid()).setParent(listitemtx_log);
                            String kkstr = "";
                            if (transaction.getPlatformtype() == 1) {
                                kkstr = "ALU";
                            } else if (transaction.getPlatformtype() == 2) {
                                kkstr = "ZTE";
                            } else if (transaction.getPlatformtype() == 3) {
                                kkstr = "NOKIA";
                            }

                            String outputstr = transaction.getLibm() + "," + convertStringToDate(transaction.getTradedate())
                                            + "," + transaction.getItemproductname() + "," + transaction.getInvoiceContactMobilePhone()
                                            + "," + status + "," + errorDesc + "," + arcid + "," + transaction.getPayamount() + "," + kkstr + "\r\n";

                            log.info("outputstr==>" + outputstr);
                            WriteFile(outputstr, fullfilename, true);
                        }

                        File file_dst_file = new File(fullfilename);
                        if (file_dst_file.exists()) {
                            Filedownload.save(file_dst_file, null);
                        }
                    } else {

                    }
                }
            } catch (Exception ex) {
                log.info(ex);
            }
        } else {
            Messagebox.show("檔名欄位為空白，查詢無任何結果!!", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
        }
    }

    public static void WriteFile(String str, String path, boolean append) { // 寫檔

        try {
            File file = new File(path);// 建立檔案，準備寫檔
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), "big5"));// 設定為BIG5格式
            // 參數append代表要不要繼續許入該檔案中
            writer.write(str); // 寫入該字串
//            writer.newLine(); // 寫入換行
            writer.close();
        } catch (IOException e) {
            log.info(e);
            System.out.println(path + "寫檔錯誤!!");
        }

    }
}
