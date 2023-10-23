/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.epay.ejb.bean.EPAY_VCARDTYPE;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.AbstractComponent;
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
public class VCardType_qry_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");
    private String cpid = new ShareParm().PARM_EPAY_CPID;

    public void onCreate() {

//        Textbox text_cardtype = (Textbox) this.getFellow("text_cardtype");
        Listbox listbox_cardtype_log = (Listbox) this.getFellow("listbox_cardtype_log");

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
       
        try {
            AbstractComponent[] acs = (AbstractComponent[]) listbox_cardtype_log.getChildren().toArray(new AbstractComponent[0]);

            for (int i = 0; i < acs.length; i++) {
                if (acs[i] instanceof Listhead) {
                    continue;
                }
                listbox_cardtype_log.removeChild(acs[i]);
            }
        } catch (Exception ex) {
        }
        
        try {
            List slist;
            slist = epaybusinesscontroller.listCardTypeInfo();

            if (slist != null && !slist.isEmpty()) {
                Iterator it = slist.iterator();
                while (it.hasNext()) {
                    EPAY_VCARDTYPE vcardtype = new EPAY_VCARDTYPE();
                    vcardtype = (EPAY_VCARDTYPE) it.next();
                    Listitem listitemtx_log = new Listitem();
                    new Listcell(vcardtype.getCardtype()).setParent(listitemtx_log);
                    new Listcell(vcardtype.getCardname()).setParent(listitemtx_log);
                    new Listcell(String.valueOf(vcardtype.getPrice())).setParent(listitemtx_log);
                    new Listcell(String.valueOf(vcardtype.getQuantity())).setParent(listitemtx_log);
                    new Listcell(String.valueOf(vcardtype.getLowthreshold())).setParent(listitemtx_log);
                    listitemtx_log.setParent(listbox_cardtype_log);
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

        Textbox text_cardtype = (Textbox) this.getFellow("text_cardtype");
        text_cardtype.setValue("");
    }

    public void doQuery() {

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        Textbox text_cardtype = (Textbox) this.getFellow("text_cardtype");
        String cardtype = text_cardtype.getValue();

        Listbox listbox_cardtype_log = (Listbox) this.getFellow("listbox_cardtype_log");
        
        try {
            AbstractComponent[] acs = (AbstractComponent[]) listbox_cardtype_log.getChildren().toArray(new AbstractComponent[0]);

            for (int i = 0; i < acs.length; i++) {
                if (acs[i] instanceof Listhead) {
                    continue;
                }
                listbox_cardtype_log.removeChild(acs[i]);
            }
        } catch (Exception ex) {
        }
        

        try {
            EPAY_VCARDTYPE vcardtype = new EPAY_VCARDTYPE();
            vcardtype = epaybusinesscontroller.queryCardTypeByCardType(cardtype);

            if (vcardtype != null) {
                Listitem listitemtx_log = new Listitem();
                new Listcell(vcardtype.getCardtype()).setParent(listitemtx_log);
                new Listcell(vcardtype.getCardname()).setParent(listitemtx_log);
                new Listcell(String.valueOf(vcardtype.getPrice())).setParent(listitemtx_log);
                new Listcell(String.valueOf(vcardtype.getQuantity())).setParent(listitemtx_log);
                new Listcell(String.valueOf(vcardtype.getLowthreshold())).setParent(listitemtx_log);

                listitemtx_log.setParent(listbox_cardtype_log);

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
