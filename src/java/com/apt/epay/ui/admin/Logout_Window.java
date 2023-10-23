/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class Logout_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    public void onCreate() { //does initialization


//        String user_code = (String) Sessions.getCurrent().getAttribute("user_code");

        String log_user_id = (String) Sessions.getCurrent().getAttribute("user_code");
        log.info("IP:" + Executions.getCurrent().getRemoteAddr() + " account: " + log_user_id + " Logout EPAY success!");

        Executions.sendRedirect("login.zul");
        Sessions.getCurrent().invalidate();
    }
}
