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
public class SCTCard_add_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    public void onClear() {

        Textbox text_cardname = (Textbox) getSpaceOwner().getFellow("text_cardname");
        Textbox text_iccid_start = (Textbox) getSpaceOwner().getFellow("text_iccid_start");
        Textbox text_iccid_end = (Textbox) getSpaceOwner().getFellow("text_iccid_end");
        Textbox text_support = (Textbox) getSpaceOwner().getFellow("text_support");
        Textbox text_apn = (Textbox) getSpaceOwner().getFellow("text_apn");
        Textbox text_pin = (Textbox) getSpaceOwner().getFellow("text_pin");
        Textbox text_memo = (Textbox) getSpaceOwner().getFellow("text_memo");

        text_cardname.setValue("");
        text_iccid_start.setValue("");
        text_iccid_end.setValue("");
        text_support.setValue("");
        text_apn.setValue("");
        text_memo.setValue("");
        text_pin.setValue("");

    }

    public void onCreate() {
        //EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
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
    }

    public void Add() throws Exception {

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        Textbox text_cardname = (Textbox) getSpaceOwner().getFellow("text_cardname");
        Textbox text_iccid_start = (Textbox) getSpaceOwner().getFellow("text_iccid_start");
        Textbox text_iccid_end = (Textbox) getSpaceOwner().getFellow("text_iccid_end");
        Textbox text_support = (Textbox) getSpaceOwner().getFellow("text_support");
        Textbox text_apn = (Textbox) getSpaceOwner().getFellow("text_apn");
        Textbox text_memo = (Textbox) getSpaceOwner().getFellow("text_memo");
        Textbox text_pin = (Textbox) getSpaceOwner().getFellow("text_pin");

        String cardname = text_cardname.getValue();
        String iccid_star = text_iccid_start.getValue();
        String iccid_end = text_iccid_end.getValue();
        String support = text_support.getValue();
        String apn = text_apn.getValue();
        String memo = text_memo.getValue();
        String pin = text_pin.getValue();

        String A1 = iccid_star;
        String A2 = iccid_end;

        BigInteger count1 = new BigInteger(A1);
        BigInteger count2 = new BigInteger(A2);

        BigInteger result = count2.subtract(count1);
        int s = result.intValue() + 1;
        BigInteger bi2 = new BigInteger("2");

        try {

            if (result.compareTo(bi2) == 1) {
                EPAY_SCT_CARD ks_sctcard = epaybusinesscontroller.getSCTCardInfoByIccid(iccid_star);
                EPAY_SCT_CARD ke_sctcard = epaybusinesscontroller.getSCTCardInfoByIccid(iccid_end);

                if (ks_sctcard == null) {

                    log.info("ks_sctcard == null");

                    if (ke_sctcard == null) {

                        log.info("ke_sctcard == null");

                        ks_sctcard = new EPAY_SCT_CARD();
                        ks_sctcard.setSct_name(cardname);
                        ks_sctcard.setIccid_start(iccid_star);
                        ks_sctcard.setIccid_end(iccid_end);
                        ks_sctcard.setSupport_name(support);
                        ks_sctcard.setApn(apn);
                        ks_sctcard.setMemo(memo);
                        ks_sctcard.setAmount(String.valueOf(s));
                        ks_sctcard.setPin(pin);

                        if (epaybusinesscontroller.insertSctCard(ks_sctcard)) {
                            Messagebox.show("新增SCT設定成功", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
                        } else {
                            Messagebox.show("新增SCT設定失敗", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
                        }
                    } else {
                        log.info("ke_sctcard != null=======>" + ks_sctcard.getIccid_start() + "," + ks_sctcard.getIccid_end());
                        Messagebox.show("新增SCT設定失敗，ICCID End 區間重複", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
                    }
                } else {
                    log.info("ks_sctcard != null=======>" + ks_sctcard.getIccid_start() + "," + ks_sctcard.getIccid_end());
                    Messagebox.show("新增SCT設定失敗，ICCID Start區間重複", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);

                }
            } else {
                Messagebox.show("新增SCT設定失敗，ICCID Start 大於 End", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
            }
        } catch (Exception ex) {
            log.info(ex);
        }

    }
}
