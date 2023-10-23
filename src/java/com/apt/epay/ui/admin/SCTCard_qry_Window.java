/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.epay.ejb.bean.EPAY_SCT_CARD;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
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
public class SCTCard_qry_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    public void onCreate() {

//        Textbox text_cardtype = (Textbox) this.getFellow("text_cardtype");
        Listbox listbox_sctcard_log = (Listbox) this.getFellow("listbox_sctcard_log");
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        try {
            AbstractComponent[] acs = (AbstractComponent[]) listbox_sctcard_log.getChildren().toArray(new AbstractComponent[0]);

            for (int i = 0; i < acs.length; i++) {
                if (acs[i] instanceof Listhead) {
                    continue;
                }
                listbox_sctcard_log.removeChild(acs[i]);
            }
        } catch (Exception ex) {
        }

        try {
            List slist;
            slist = epaybusinesscontroller.listSctCardInfo();

            if (slist != null && !slist.isEmpty()) {

                Iterator it = slist.iterator();
                while (it.hasNext()) {
                    EPAY_SCT_CARD sctcard;// = new EPAY_SCT_CARD();
                    sctcard = (EPAY_SCT_CARD) it.next();
                    Listitem listitemtx_log = new Listitem();

                    new Listcell(sctcard.getSct_name()).setParent(listitemtx_log);
                    new Listcell(sctcard.getIccid_start()).setParent(listitemtx_log);
                    new Listcell(sctcard.getIccid_end()).setParent(listitemtx_log);
                    new Listcell(sctcard.getAmount()).setParent(listitemtx_log);
                    new Listcell(sctcard.getSupport_name()).setParent(listitemtx_log);
                    new Listcell(sctcard.getApn()).setParent(listitemtx_log);
                    new Listcell(sctcard.getPin()).setParent(listitemtx_log);
                    new Listcell(sctcard.getMemo()).setParent(listitemtx_log);
                    listitemtx_log.setParent(listbox_sctcard_log);
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

        Textbox text_apn = (Textbox) this.getFellow("text_apn");
        text_apn.setValue("");

        Listbox listbox_sctcard_log = (Listbox) this.getFellow("listbox_sctcard_log");
        try {
            AbstractComponent[] acs = (AbstractComponent[]) listbox_sctcard_log.getChildren().toArray(new AbstractComponent[0]);

            for (int i = 0; i < acs.length; i++) {
                if (acs[i] instanceof Listhead) {
                    continue;
                }
                listbox_sctcard_log.removeChild(acs[i]);
            }
        } catch (Exception ex) {
        }

    }

    public void doQuery() {

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        Textbox text_iccid = (Textbox) this.getFellow("text_iccid");
        String iccid = text_iccid.getValue();

        Listbox listbox_sctcard_log = (Listbox) this.getFellow("listbox_sctcard_log");

        try {
            AbstractComponent[] acs = (AbstractComponent[]) listbox_sctcard_log.getChildren().toArray(new Component[0]);

            for (AbstractComponent ac : acs) {
                if (ac instanceof Listhead) {
                    continue;
                }
                listbox_sctcard_log.removeChild(ac);
            }
        } catch (Exception ex) {
        }

        try {

            EPAY_SCT_CARD ks_sctcard;
            ks_sctcard = epaybusinesscontroller.getSCTCardInfoByIccid(iccid);
            if (ks_sctcard == null) {
                try {
                    Messagebox.show("查詢無任何結果!!", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
                } catch (Exception ex) {
                    log.info(ex);
                }
            } else {
                Listitem listitemtx_log = new Listitem();
                new Listcell(ks_sctcard.getSct_name()).setParent(listitemtx_log);
                new Listcell(ks_sctcard.getIccid_start()).setParent(listitemtx_log);
                new Listcell(ks_sctcard.getIccid_end()).setParent(listitemtx_log);
                new Listcell(ks_sctcard.getAmount()).setParent(listitemtx_log);
                new Listcell(ks_sctcard.getSupport_name()).setParent(listitemtx_log);
                new Listcell(ks_sctcard.getApn()).setParent(listitemtx_log);
                new Listcell(ks_sctcard.getPin()).setParent(listitemtx_log);
                new Listcell(ks_sctcard.getMemo()).setParent(listitemtx_log);
                listitemtx_log.setParent(listbox_sctcard_log);

            }
        } catch (Exception ex) {
            log.info(ex);
        }
    }
}
