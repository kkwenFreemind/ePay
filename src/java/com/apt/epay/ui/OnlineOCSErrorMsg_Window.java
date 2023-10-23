/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui;

import com.apt.epay.share.ShareParm;
import com.apt.util.toolsUtil;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class OnlineOCSErrorMsg_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");
    private String mdn;

    public void onCreate() {

        Label OCSErrorMsgLabel =  (Label) this.getFellow("OCSErrorMsgLabel");
        OCSErrorMsgLabel.setValue("用戶門號不存在，請洽亞太電信客服");
        try {
            mdn = (String) Sessions.getCurrent().getAttribute("mdn");
            log.info("OnlineOCSErrorMsg_Window mdn=========>"  + mdn);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if ( mdn != null) {
            OCSErrorMsgLabel.setValue("用戶門號【" + mdn + "】不存在，請洽亞太電信客服");
        } 
    }

    public void backURL() {
        String tmpurl = new ShareParm().PARM_ECARE_URL;
        if (tmpurl != null) {

        } else {
            log.info("ECARE URL IS NULL");
            tmpurl = "https://pv.www.aptg.com.tw/ecare/ecHome.seam";
        }
        log.info("ECARE URL==>" + tmpurl);
        Executions.getCurrent().sendRedirect(tmpurl);

//        Executions.sendRedirect("/");
    }
}
