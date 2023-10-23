/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui;


import com.apt.epay.share.ShareParm;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class OnlineReturnURL_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    public void onCreate() {


    }



    public void backURL() {
        String tmpurl = new ShareParm().PARM_ECARE_URL;
        if (tmpurl != null) {

        } else {
            log.info("ECARE URL IS NULL");
            tmpurl = "https://pv.www.aptg.com.tw/ecare/ecHome.seam";
        }
        log.info("ECARE URL==>"+tmpurl);
        Executions.getCurrent().sendRedirect(tmpurl);

//        Executions.sendRedirect("/");
    }
}
