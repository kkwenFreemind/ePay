/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

//import com.apt.epay.controller.EPayBusinessConreoller;
//import com.apt.epay.share.ShareBean;
//import com.apt.util.BundleDateVO;
import com.apt.util.RecordUtil;
//import com.epay.ejb.bean.EPAY_TRANSACTION;
import java.sql.ResultSet;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.GregorianCalendar;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
//import org.zkoss.zk.ui.AbstractComponent;
//import org.zkoss.zk.ui.Executions;
//import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
//import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
//import org.zkoss.zul.Messagebox;
//import org.zkoss.zul.Textbox;
//import org.zkoss.zul.Timebox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class record_query_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    public void onTimer() {
        log.info("Timer start now!");

        Listbox listbox_5f_log = (Listbox) this.getFellow("listbox_5f_log");
        Listbox listbox_3f_log = (Listbox) this.getFellow("listbox_3f_log");
        Listbox listbox_1f_log = (Listbox) this.getFellow("listbox_1f_log");

        listbox_5f_log.getItems().clear();
        listbox_3f_log.getItems().clear();
        listbox_1f_log.getItems().clear();
        
        RecordUtil recordutil = new RecordUtil();
        try {
            ResultSet rs = recordutil.GetRecord5F();
            while (rs.next()) {
//                Record5FVO record5f = new Record5FVO();
                String tx_amount = rs.getString("TX_AMOUNT");
                String tx_code = rs.getString("TX_CODE");
                String tx_datetime = rs.getString("TX_DATETIME");
                String tx_item_name = rs.getString("TX_ITEM_NAME");
                String tx_status = rs.getString("TX_STATUS");
                String tx_userid = rs.getString("TX_USER_ID");

                Listitem listitemtx_log = new Listitem();

                new Listcell(tx_datetime).setParent(listitemtx_log);
                new Listcell(tx_userid).setParent(listitemtx_log);
                new Listcell(tx_item_name).setParent(listitemtx_log);
                new Listcell(tx_code).setParent(listitemtx_log);
                new Listcell(tx_status).setParent(listitemtx_log);
                new Listcell(tx_amount).setParent(listitemtx_log);

                listitemtx_log.setParent(listbox_5f_log);
            }

        } catch (Exception ex) {

        }

        //3F
        RecordUtil recordutil3f = new RecordUtil();
        try {
            ResultSet rs3f = recordutil3f.GetRecord3F();
            while (rs3f.next()) {

                String user_id = rs3f.getString("USER_ID");
                String wakeupdate = rs3f.getString("WAKEUP_DATETIME");
                String wakeupstatus = rs3f.getString("WAKEUP_STATUS");

                Listitem listitemtx_3flog = new Listitem();

                new Listcell(user_id).setParent(listitemtx_3flog);
                new Listcell(wakeupdate).setParent(listitemtx_3flog);
                new Listcell(wakeupstatus).setParent(listitemtx_3flog);

                listitemtx_3flog.setParent(listbox_3f_log);
            }

        } catch (Exception ex) {

        }

        //1F
        RecordUtil recordutil1f = new RecordUtil();
        try {
            ResultSet rs1f = recordutil1f.GetRecord1F();
            while (rs1f.next()) {

                String user_id = rs1f.getString("USER_CODE");
                String in = rs1f.getString("IN_DATETIME");
                String out = rs1f.getString("OUT_DATETIME");
                String workingout_date = rs1f.getString("WORKINOUT_DATE");
                String status = rs1f.getString("STATUS");

                Listitem listitemtx_1flog = new Listitem();

                new Listcell(user_id).setParent(listitemtx_1flog);
                new Listcell(in).setParent(listitemtx_1flog);
                new Listcell(out).setParent(listitemtx_1flog);
                new Listcell(workingout_date).setParent(listitemtx_1flog);
                new Listcell(status).setParent(listitemtx_1flog);

                listitemtx_1flog.setParent(listbox_1f_log);
            }

        } catch (Exception ex) {

        }
    }

    public void onCreate() {

        Listbox listbox_5f_log = (Listbox) this.getFellow("listbox_5f_log");
        Listbox listbox_3f_log = (Listbox) this.getFellow("listbox_3f_log");
        Listbox listbox_1f_log = (Listbox) this.getFellow("listbox_1f_log");

        RecordUtil recordutil = new RecordUtil();
        try {
            ResultSet rs = recordutil.GetRecord5F();
            while (rs.next()) {
//                Record5FVO record5f = new Record5FVO();
                String tx_amount = rs.getString("TX_AMOUNT");
                String tx_code = rs.getString("TX_CODE");
                String tx_datetime = rs.getString("TX_DATETIME");
                String tx_item_name = rs.getString("TX_ITEM_NAME");
                String tx_status = rs.getString("TX_STATUS");
                String tx_userid = rs.getString("TX_USER_ID");

                Listitem listitemtx_log = new Listitem();

                new Listcell(tx_datetime).setParent(listitemtx_log);
                new Listcell(tx_userid).setParent(listitemtx_log);
                new Listcell(tx_item_name).setParent(listitemtx_log);
                new Listcell(tx_code).setParent(listitemtx_log);
                new Listcell(tx_status).setParent(listitemtx_log);
                new Listcell(tx_amount).setParent(listitemtx_log);

                listitemtx_log.setParent(listbox_5f_log);
            }

        } catch (Exception ex) {

        }

        //3F
        RecordUtil recordutil3f = new RecordUtil();
        try {
            ResultSet rs3f = recordutil3f.GetRecord3F();
            while (rs3f.next()) {

                String user_id = rs3f.getString("USER_ID");
                String wakeupdate = rs3f.getString("WAKEUP_DATETIME");
                String wakeupstatus = rs3f.getString("WAKEUP_STATUS");

                Listitem listitemtx_3flog = new Listitem();

                new Listcell(user_id).setParent(listitemtx_3flog);
                new Listcell(wakeupdate).setParent(listitemtx_3flog);
                new Listcell(wakeupstatus).setParent(listitemtx_3flog);

                listitemtx_3flog.setParent(listbox_3f_log);
            }

        } catch (Exception ex) {

        }

        //1F
        RecordUtil recordutil1f = new RecordUtil();
        try {
            ResultSet rs1f = recordutil1f.GetRecord1F();
            while (rs1f.next()) {

                String user_id = rs1f.getString("USER_CODE");
                String in = rs1f.getString("IN_DATETIME");
                String out = rs1f.getString("OUT_DATETIME");
                String workingout_date = rs1f.getString("WORKINOUT_DATE");
                String status = rs1f.getString("STATUS");

                Listitem listitemtx_1flog = new Listitem();

                new Listcell(user_id).setParent(listitemtx_1flog);
                new Listcell(in).setParent(listitemtx_1flog);
                new Listcell(out).setParent(listitemtx_1flog);
                new Listcell(workingout_date).setParent(listitemtx_1flog);
                new Listcell(status).setParent(listitemtx_1flog);

                listitemtx_1flog.setParent(listbox_1f_log);
            }

        } catch (Exception ex) {

        }
    }

}
