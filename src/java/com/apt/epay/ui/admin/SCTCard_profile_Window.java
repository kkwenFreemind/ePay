/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.epay.ejb.bean.EPAY_SCT_CARD;
import java.math.BigInteger;
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
public class SCTCard_profile_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    private String cardtype = "";

    public void onCreate() {

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox text_cardname = (Textbox) getSpaceOwner().getFellow("text_cardname");
        Textbox text_iccid_start = (Textbox) getSpaceOwner().getFellow("text_iccid_start");
        Textbox text_iccid_end = (Textbox) getSpaceOwner().getFellow("text_iccid_end");
        Textbox text_support = (Textbox) getSpaceOwner().getFellow("text_support");
        Textbox text_apn = (Textbox) getSpaceOwner().getFellow("text_apn");
        Textbox text_memo = (Textbox) getSpaceOwner().getFellow("text_memo");
        Textbox text_pin = (Textbox) getSpaceOwner().getFellow("text_pin");

        text_cardname.setValue("");
        text_iccid_start.setValue("");
        text_iccid_end.setValue("");
        text_support.setValue("");
        text_apn.setValue("");
        text_memo.setValue("");
        text_pin.setValue("");

        cardtype = (String) Executions.getCurrent().getAttribute("cardtype");
        log.info("sctid========>" + cardtype);
        EPAY_SCT_CARD sctcard = new EPAY_SCT_CARD();
        try {
            int ssctid = Integer.valueOf(cardtype);
            sctcard = epaybusinesscontroller.getSCTCardInfoById(ssctid);

            text_cardname.setValue(sctcard.getSct_name());
            text_iccid_start.setValue(sctcard.getIccid_start());
            text_iccid_end.setValue(sctcard.getIccid_end());
            text_support.setValue(sctcard.getSupport_name());
            text_apn.setValue(sctcard.getApn());
            text_memo.setValue(sctcard.getMemo());
            text_pin.setValue(sctcard.getPin());

        } catch (Exception ex) {
            log.info(ex);
        }
    }

    public void onModify() throws Exception {

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        Textbox text_cardname = (Textbox) getSpaceOwner().getFellow("text_cardname");
        Textbox text_iccid_start = (Textbox) getSpaceOwner().getFellow("text_iccid_start");
        Textbox text_iccid_end = (Textbox) getSpaceOwner().getFellow("text_iccid_end");
        Textbox text_support = (Textbox) getSpaceOwner().getFellow("text_support");
        Textbox text_apn = (Textbox) getSpaceOwner().getFellow("text_apn");
        Textbox text_memo = (Textbox) getSpaceOwner().getFellow("text_memo");
        Textbox text_pin = (Textbox) getSpaceOwner().getFellow("text_pin");

        String iccid_star = text_iccid_start.getValue();
        String iccid_end = text_iccid_end.getValue();

        String A1 = iccid_star;
        String A2 = iccid_end;

        BigInteger count1 = new BigInteger(A1);
        BigInteger count2 = new BigInteger(A2);

        BigInteger result = count2.subtract(count1);
        int s = result.intValue() + 1;
        BigInteger bi2 = new BigInteger("2");

        EPAY_SCT_CARD sctcart;// = new EPAY_SCT_CARD();
        if (result.compareTo(bi2) == 1) {

//            EPAY_SCT_CARD ks_sctcard = epaybusinesscontroller.getSCTCardInfoByIccid(iccid_star);
//            EPAY_SCT_CARD ke_sctcard = epaybusinesscontroller.getSCTCardInfoByIccid(iccid_end);

            try {
                int ssctid = Integer.valueOf(cardtype);
                sctcart = epaybusinesscontroller.getSCTCardInfoById(ssctid);

                sctcart.setSct_name(text_cardname.getValue());
                sctcart.setIccid_start(text_iccid_start.getValue());
                sctcart.setIccid_end(text_iccid_end.getValue());
                sctcart.setSupport_name(text_support.getValue());
                sctcart.setApn(text_apn.getValue());
                sctcart.setMemo(text_memo.getValue());
                sctcart.setPin(text_pin.getValue());
                sctcart.setAmount(String.valueOf(s));

                if (epaybusinesscontroller.updateSctCard(sctcart)) {

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

        } else {
            Messagebox.show("修改SCT設定失敗，ICCID Start 大於 End", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
        }
    }
}
