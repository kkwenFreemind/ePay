/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.epay.ejb.bean.EPAY_VCARDTYPE;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Include;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
//import org.zkoss.zul.Radiogroup;
//import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class VCardType_mod_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    public void onCreate() {

        Listbox list_vcardtype = (Listbox) this.getFellow("list_vcardtype");

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        try {
            List slist;
            slist = epaybusinesscontroller.listCardTypeInfo();

            if (slist != null && !slist.isEmpty()) {
                Iterator it = slist.iterator();
                while (it.hasNext()) {
                    EPAY_VCARDTYPE vcardtype = new EPAY_VCARDTYPE();
                    vcardtype = (EPAY_VCARDTYPE) it.next();
                    Listitem listitemtx_log = new Listitem();
                    listitemtx_log.setId(vcardtype.getCardtype());
                    new Listcell(vcardtype.getCardtype()).setParent(listitemtx_log);
                    new Listcell(vcardtype.getCardname()).setParent(listitemtx_log);
                    new Listcell(String.valueOf(vcardtype.getPrice())).setParent(listitemtx_log);
                    new Listcell(String.valueOf(vcardtype.getQuantity())).setParent(listitemtx_log);
                    new Listcell(String.valueOf(vcardtype.getLowthreshold())).setParent(listitemtx_log);
                    listitemtx_log.setParent(list_vcardtype);
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
        Listbox list_vcardtype = (Listbox) getSpaceOwner().getFellow("list_vcardtype");

        String cardtype = list_vcardtype.getSelectedItem().getId();

        Map map_parms = new HashMap();

        map_parms.put("cardtype", cardtype);
        Executions.getCurrent().setAttribute("map_parms", map_parms);
        Executions.getCurrent().setAttribute("cardtype", cardtype);

        Window contactWnd = (Window) Executions.createComponents("vcardtype_profile.zul", null, model);
        Include inc = (Include) Sessions.getCurrent().getAttribute("xcontents");
        inc.appendChild(contactWnd);
    }
}
