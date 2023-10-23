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
public class OnlineUserErrorMsg_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");
    private String status;

    public void onCreate() {

        Label UserErrorMsgLabel =  (Label) this.getFellow("UserErrorMsgLabel");
        UserErrorMsgLabel.setValue("合約狀態不符合儲值權限");
        try {
            status = (String) Sessions.getCurrent().getAttribute("status");
            log.info("OnlineUserErrorMsg_Window contract status=========>"  + status);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if ( status != null) {
            int istatus = Integer.valueOf(status);
            toolsUtil tools = new toolsUtil();
            String desc = null;
            desc = tools.getContractStatusDesc(istatus);
            UserErrorMsgLabel.setValue("合約狀態" + desc + "不符合儲值權限");
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
