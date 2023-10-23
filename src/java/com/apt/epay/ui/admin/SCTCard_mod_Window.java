/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.epay.ejb.bean.EPAY_SCT_CARD;
import com.epay.ejb.bean.EPAY_VCARDTYPE;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Include;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class SCTCard_mod_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    public void onCreate() {

        Listbox list_sctcard = (Listbox) this.getFellow("list_sctcard");

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        try {
            List slist;
            slist = epaybusinesscontroller.listSctCardInfo();

            if (slist != null && !slist.isEmpty()) {

                Iterator it = slist.iterator();
                while (it.hasNext()) {
                    EPAY_SCT_CARD sctcard;//= new EPAY_SCT_CARD();
                    sctcard = (EPAY_SCT_CARD) it.next();
                    Listitem listitemtx_log = new Listitem();
                    log.info("sctid===>" + String.valueOf(sctcard.getId()));

                    listitemtx_log.setId(String.valueOf(sctcard.getId()));
                    new Listcell(String.valueOf(sctcard.getId())).setParent(listitemtx_log);
                    new Listcell(sctcard.getSct_name()).setParent(listitemtx_log);
                    new Listcell(sctcard.getIccid_start()).setParent(listitemtx_log);
                    new Listcell(sctcard.getIccid_end()).setParent(listitemtx_log);
                    new Listcell(sctcard.getSupport_name()).setParent(listitemtx_log);
                    new Listcell(sctcard.getApn()).setParent(listitemtx_log);
                    new Listcell(sctcard.getPin()).setParent(listitemtx_log);
                    new Listcell(sctcard.getMemo()).setParent(listitemtx_log);
                    listitemtx_log.setParent(list_sctcard);
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

    public void onSelect() {

        this.detach();
        Map model = new HashMap();
        Listbox list_sctcard = (Listbox) getSpaceOwner().getFellow("list_sctcard");

        String cardtype = list_sctcard.getSelectedItem().getId();

        Map map_parms = new HashMap();

        map_parms.put("cardtype", cardtype);
        Executions.getCurrent().setAttribute("map_parms", map_parms);
        Executions.getCurrent().setAttribute("cardtype", cardtype);

        Window contactWnd = (Window) Executions.createComponents("sctcard_profile.zul", null, model);
        Include inc = (Include) Sessions.getCurrent().getAttribute("xcontents");
        inc.appendChild(contactWnd);
    }
}
