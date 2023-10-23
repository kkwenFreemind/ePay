/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.epay.ejb.bean.EPAY_BUCKET;
import com.epay.ejb.bean.EPAY_SERVICE_INFO;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
//import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class Bucket_qry_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");
    private String cpid = new ShareParm().PARM_EPAY_CPID;
    private String[] ch_serviceid = new String[100];

    public void onCreate() {

//        Textbox text_serviceid = (Textbox) this.getFellow("text_serviceid");
        Combobox combo_serviceid = (Combobox) getSpaceOwner().getFellow("combo_serviceid");
//        text_serviceid.setValue("");

        try {
            EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
            List serviceinfo1 = epaybusinesscontroller.listServiceInfo();
            Iterator itserviceinfo1 = serviceinfo1.iterator();
            int j = 1;
            while (itserviceinfo1.hasNext()) {

                EPAY_SERVICE_INFO serid = (EPAY_SERVICE_INFO) itserviceinfo1.next();

                Comboitem coitem_serviceid = new Comboitem();
                coitem_serviceid.setId("combo" + serid.getServiceId());
                coitem_serviceid.setLabel(serid.getServiceName());
                combo_serviceid.appendChild(coitem_serviceid);
                ch_serviceid[j] = String.valueOf(serid.getServiceId());

                j++;
            }
            combo_serviceid.setSelectedIndex(1);
        } catch (Exception ex) {

        }

        String sid = ch_serviceid[1];

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        Listbox listbox_bucketinfo_log = (Listbox) this.getFellow("listbox_bucketinfo_log");

        try {
            AbstractComponent[] acs = (AbstractComponent[]) listbox_bucketinfo_log.getChildren().toArray(new AbstractComponent[0]);

            for (int i = 0; i < acs.length; i++) {
                if (acs[i] instanceof Listhead) {
                    continue;
                }
                listbox_bucketinfo_log.removeChild(acs[i]);
            }
        } catch (Exception ex) {
        }

        try {
            List slist;
            slist = epaybusinesscontroller.getBucketListBySid(sid);

            if (slist != null && !slist.isEmpty()) {
                Iterator it = slist.iterator();
                while (it.hasNext()) {
                    EPAY_BUCKET bucketinfo = new EPAY_BUCKET();
                    bucketinfo = (EPAY_BUCKET) it.next();
                    Listitem listitemtx_log = new Listitem();

                    new Listcell(bucketinfo.getBucketId()).setParent(listitemtx_log);
                    
                    String bucketid = bucketinfo.getBucketId();
                    String tmp_amount = String.valueOf(bucketinfo.getAmount());
                    String amount = ShowAmountForBucketID(bucketid, tmp_amount);
            
                    new Listcell(bucketinfo.getRef()).setParent(listitemtx_log);
                    new Listcell(String.valueOf(amount)).setParent(listitemtx_log);
                    new Listcell(bucketinfo.getNote()).setParent(listitemtx_log);

                    listitemtx_log.setParent(listbox_bucketinfo_log);
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

    public void onClear() {

//        Textbox text_serviceid = (Textbox) this.getFellow("text_serviceid");
//        text_serviceid.setValue("");
    }

    public void doQuery() {

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Combobox combo_serviceid = (Combobox) getSpaceOwner().getFellow("combo_serviceid");
//        Textbox text_serviceid = (Textbox) this.getFellow("text_serviceid");
        String sid = ch_serviceid[combo_serviceid.getSelectedIndex()];

        Listbox listbox_bucketinfo_log = (Listbox) this.getFellow("listbox_bucketinfo_log");

        try {
            AbstractComponent[] acs = (AbstractComponent[]) listbox_bucketinfo_log.getChildren().toArray(new AbstractComponent[0]);

            for (int i = 0; i < acs.length; i++) {
                if (acs[i] instanceof Listhead) {
                    continue;
                }
                listbox_bucketinfo_log.removeChild(acs[i]);
            }
        } catch (Exception ex) {
        }

        try {
            List slist;
            slist = epaybusinesscontroller.getBucketListBySid(sid);

            if (slist != null && !slist.isEmpty()) {
                Iterator it = slist.iterator();
                while (it.hasNext()) {
                    EPAY_BUCKET bucketinfo = new EPAY_BUCKET();
                    bucketinfo = (EPAY_BUCKET) it.next();
                    Listitem listitemtx_log = new Listitem();

                    new Listcell(bucketinfo.getBucketId()).setParent(listitemtx_log);

                    String bucketid = bucketinfo.getBucketId();
                    String tmp_amount = String.valueOf(bucketinfo.getAmount());
                    String amount = ShowAmountForBucketID(bucketid, tmp_amount);
                    new Listcell(bucketinfo.getRef()).setParent(listitemtx_log);
                    new Listcell(String.valueOf(amount)).setParent(listitemtx_log);
                    new Listcell(bucketinfo.getNote()).setParent(listitemtx_log);

                    listitemtx_log.setParent(listbox_bucketinfo_log);
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

    private String ShowAmountForBucketID(String bucketid, String tmp_amount) {
        String result = "";
        if (bucketid.equals("610") || bucketid.equals("620")) {
            result = String.valueOf((int) Math.round(Float.valueOf(tmp_amount) / 10000));
        }

        if (bucketid.equals("720") || bucketid.equals("730")) {
            double ft = Double.valueOf(tmp_amount) / 1048576;
            result = new DecimalFormat("0").format(ft);
        }

        if (bucketid.equals("810")) {
            result = String.valueOf((int) Math.round(Float.valueOf(tmp_amount) * 0.033));
        }
        log.info("Show Bucket===>" + bucketid + ",amount" + result);
        return result;
    }
}
