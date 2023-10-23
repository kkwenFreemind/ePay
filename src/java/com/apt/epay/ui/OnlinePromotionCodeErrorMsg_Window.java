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
public class OnlinePromotionCodeErrorMsg_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    private String promotioncode;

    public void onCreate() {

        Label PromotionCodeErrorMsgLabel =  (Label) this.getFellow("PromotionCodeErrorMsgLabel");
        PromotionCodeErrorMsgLabel.setValue("門號卡不符合儲值條件");
        try {
            promotioncode = (String) Sessions.getCurrent().getAttribute("promotioncode");
            log.info("OnlinePromotionCodeErrorMsg_Window promotioncode=========>"  + promotioncode);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if ( promotioncode != null) {
            PromotionCodeErrorMsgLabel.setValue("門號卡【" + promotioncode + "】不符合儲值權限");
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
