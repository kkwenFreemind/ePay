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
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;

import org.zkoss.zul.Timebox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class addon_report_Window extends Window {

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

    }

    public void onClear() {

        Datebox start_db = (Datebox) this.getFellow("start_db");
        Timebox start_tb = (Timebox) this.getFellow("start_tb");
        Datebox end_db = (Datebox) this.getFellow("end_db");
        Timebox end_tb = (Timebox) this.getFellow("end_tb");
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

        start_db.setValue(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24));
        start_tb.setValue(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24));
        end_db.setValue(new Date());
        end_tb.setValue(new Date());

    }

    public void onListService() {
//        ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
//        Listbox listbox_cp = (Listbox) this.getFellow("listbox_cp");
//        Listbox listbox_service = (Listbox) this.getFellow("listbox_service");
//        List listservice = null;
//        Iterator itservice = null;
//
//        try {
//            AbstractComponent[] acs = (AbstractComponent[]) listbox_service.getChildren().toArray(new AbstractComponent[0]);
//
//            for (int i = 0; i < acs.length; i++) {
//                if (acs[i].getId().equals("select_service_all")) {
//                    continue;
//                }
//                listbox_service.removeChild(acs[i]);
//            }
//        } catch (Exception ex) {
//        }
//
//        String selected_cp_code = (String) listbox_cp.getSelectedItem().getValue();
//
//        if (selected_cp_code != null && !selected_cp_code.equals("*")) {
//            listservice = controlbusiness.getService_ByCpCode(selected_cp_code);
//        } else {
//            listservice = controlbusiness.getAllService();
//        }
//
//        if (listservice != null && !listservice.isEmpty()) {
//            itservice = listservice.iterator();
//            PG_SERVICE service = new PG_SERVICE();
//            while (itservice.hasNext()) {
//                service = (PG_SERVICE) itservice.next();
//                Listitem itemservice = new Listitem();
//                itemservice.setLabel(service.getService_code() + " " + service.getService_name());
//                itemservice.setValue(service.getService_code());
//                itemservice.setParent(listbox_service);
//
//            }
//            listbox_service.setSelectedIndex(0);
//        }

    }

    public void onSelect() {
        this.detach();
        Map model = new HashMap();
        //  model.put("test",id);
        //  Executions.getCurrent().setAttribute("test",id);
        //   Executions.getCurrent().setAttribute("url","cp_mod.zul");
//        Listbox listbox_tx_log = (Listbox) this.getFellow("listbox_tx_log");
//        String tx_code = "";
//        tx_code = listbox_tx_log.getSelectedItem().getId();
//        Executions.getCurrent().setAttribute("tx_code", tx_code);
//        Window contactWnd = (Window) Executions.createComponents("tx_log_query_profile.zul", null, null);

        //       Page page =  Executions.getCurrent().getDesktop().getPage("cp_profile.zul");
//       Executions.createComponents 
//       Include inc = (Include) getSpaceOwner().getFellow("xcontents");
//        inc.setSrc("cp_mgn.zul");
//    //     Executions.sendRedirect("./login.do?action=login");
    }

    public void doQuery() {

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        Datebox start_db = (Datebox) this.getFellow("start_db");
        Timebox start_tb = (Timebox) this.getFellow("start_tb");
        Datebox end_db = (Datebox) this.getFellow("end_db");
        Timebox end_tb = (Timebox) this.getFellow("end_tb");
//        Textbox text_mdn = (Textbox) this.getFellow("textbox_mdn");

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
//        String mdn = (String) text_mobilenumber.getValue();
//        String cp_code = (String) listbox_cp.getSelectedItem().getValue();
//        String service_code = (String) listbox_service.getSelectedItem().getValue();
//        String service_type = (String) listbox_service_type.getSelectedItem().getValue();
//        String tx_result_code = (String) list_tx_status.getSelectedItem().getValue();
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
//            String mdn = text_mdn.getValue();
            List list = epaybusinesscontroller.getTxLogList(startdatetime, enddatetime);
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
                    } else {

                    }
                    new Listcell(paytool).setParent(listitemtx_log);

                    if (transaction.getStatus().equals("00")) {
                        status = "成功";
                    } else {
                        status = "失敗";
                    }
                    new Listcell(status).setParent(listitemtx_log);

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
                    if (transaction.getPlatformtype() == 1) {
                        new Listcell("ALU").setParent(listitemtx_log);
                    } else {
                        new Listcell("ZTE").setParent(listitemtx_log);
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
        return result;
    }
}
