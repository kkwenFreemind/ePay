/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Borderlayout;

/**
 *
 * @author kevinchang
 */
public class MainLayout extends Borderlayout {

    private static final Logger log = Logger.getLogger("EPAY");

    public MainLayout() {

        String auth = (String) Sessions.getCurrent().getAttribute("auth");
        String user_code = (String) Sessions.getCurrent().getAttribute("user_code");

//        log.info("user_code ===>" + user_code);

        if (!Executions.getCurrent().isVoided() && auth != null) {

            final Execution exec = Executions.getCurrent();
            final String sn = exec.getServerName();
            final int sp = exec.getServerPort();

            String gkey = null;

            if (gkey != null) {

                exec.getDesktop().getSession().setAttribute("gmapsKey", gkey);
            }
        } else {
            this.detach();
            Executions.getCurrent().createComponents("login.zul", null, null);

        }
    }
}
