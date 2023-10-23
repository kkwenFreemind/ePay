/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.bak;

import com.apt.util.SendSMS;
import org.apache.log4j.Logger;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class CMS_SMSDeposit_Window extends Window {

//    private static final long serialVersionUID = -84547344457721L;
    private static final Logger log = Logger.getLogger("EPAY");
    private String mdn="";

    public void onCreate() throws Exception {

    }

    public void sendOrder() throws Exception {

//        Textbox textbox_mdn = (Textbox) this.getFellow("textbox_mdn"); //聯絡人手機號碼
//        mdn = textbox_mdn.getValue();
//        
//        SendSMS xsms = new SendSMS();
//        String msg="簡訊測試，123ABC。";
//        xsms.sendsms(mdn, msg); 
        
    }
}
