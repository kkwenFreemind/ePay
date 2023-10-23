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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class VCardType_add_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");
    private static String cpid = new ShareParm().PARM_EPAY_CPID;

    public void onClear() {

        Textbox text_cardtype = (Textbox) getSpaceOwner().getFellow("text_cardtype");
        Textbox text_cardname = (Textbox) getSpaceOwner().getFellow("text_cardname");
        Textbox text_price = (Textbox) getSpaceOwner().getFellow("text_price");
        Textbox text_lowthreshold = (Textbox) getSpaceOwner().getFellow("text_lowthreshold");

        text_cardtype.setValue("");
        text_cardname.setValue("");
        text_price.setValue("");
        text_lowthreshold.setValue("");

    }

    public void onCreate() {
        //EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        Textbox text_cardtype = (Textbox) getSpaceOwner().getFellow("text_cardtype");
        Textbox text_cardname = (Textbox) getSpaceOwner().getFellow("text_cardname");
        Textbox text_price = (Textbox) getSpaceOwner().getFellow("text_price");
        Textbox text_lowthreshold = (Textbox) getSpaceOwner().getFellow("text_lowthreshold");

        text_cardtype.setValue("");
        text_cardname.setValue("");
        text_price.setValue("");
        text_lowthreshold.setValue("");

    }

    public void Add() throws Exception {

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        Textbox text_cardtype = (Textbox) getSpaceOwner().getFellow("text_cardtype");
        Textbox text_cardname = (Textbox) getSpaceOwner().getFellow("text_cardname");
        Textbox text_price = (Textbox) getSpaceOwner().getFellow("text_price");
        Textbox text_lowthreshold = (Textbox) getSpaceOwner().getFellow("text_lowthreshold");

        String cardtype = text_cardtype.getValue();
        String cardname = text_cardname.getValue();
        String price = text_price.getValue();
        String lowthreshold = text_lowthreshold.getValue();

        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
        Calendar nowDateTime = Calendar.getInstance();
        String tradeDate = sdf.format(nowDateTime.getTime());

        EPAY_VCARDTYPE vcardtype = new EPAY_VCARDTYPE();
        vcardtype.setCardtype(cardtype);
        vcardtype.setCardname(cardname);
        vcardtype.setPrice(Integer.valueOf(price));
        vcardtype.setLowthreshold(Integer.valueOf(lowthreshold));
        vcardtype.setQuantity(0);
        vcardtype.setCreatedate(sdf.parse(tradeDate));

        if (epaybusinesscontroller.insertVCardType(vcardtype)) {
            Messagebox.show("新增CardType成功", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
        } else {
            Messagebox.show("新增CardType失敗", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
        }

    }
}
