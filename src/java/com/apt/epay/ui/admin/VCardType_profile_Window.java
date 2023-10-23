/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.epay.ejb.bean.EPAY_SERVICE_INFO;
import com.epay.ejb.bean.EPAY_VCARDTYPE;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class VCardType_profile_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");
    private String cardtype = "";

    public void onCreate() {

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox text_cardname = (Textbox) getSpaceOwner().getFellow("text_cardname");
        Textbox text_lowthreshold = (Textbox) getSpaceOwner().getFellow("text_lowthreshold");

        text_cardname.setValue("");
        text_lowthreshold.setValue("");

        cardtype = (String) Executions.getCurrent().getAttribute("cardtype");

        EPAY_VCARDTYPE vcardtype = new EPAY_VCARDTYPE();
        try {
            vcardtype = epaybusinesscontroller.queryCardTypeByCardType(cardtype);
            text_cardname.setValue(vcardtype.getCardname());
            text_lowthreshold.setValue(String.valueOf(vcardtype.getLowthreshold()));
        } catch (Exception ex) {
            log.info(ex);
        }
    }

    public void onModify() throws Exception {

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox text_cardname = (Textbox) getSpaceOwner().getFellow("text_cardname");
        Textbox text_lowthreshold = (Textbox) getSpaceOwner().getFellow("text_lowthreshold");

        EPAY_VCARDTYPE vcardtype = new EPAY_VCARDTYPE();
        try {
            vcardtype = epaybusinesscontroller.queryCardTypeByCardType(cardtype);

            vcardtype.setCardname(text_cardname.getValue());
            vcardtype.setLowthreshold(Integer.valueOf(text_lowthreshold.getValue()));

            if (epaybusinesscontroller.updateVCardType(vcardtype)) {

                Messagebox.show("修改資料成功", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
                this.detach();
                Executions.sendRedirect("/admin/main.zul");

            } else {
                Messagebox.show("修改資料失敗", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
                this.detach();
                Executions.sendRedirect("/admin/main.zul");
            }
        } catch (Exception ex) {
            log.info(ex);
        }

    }
}
