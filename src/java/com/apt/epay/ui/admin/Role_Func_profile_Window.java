/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;


import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;

import com.epay.ejb.bean.EPAY_SYS_ROLEFUNCS;

import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
//import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class Role_Func_profile_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");
    private String uid;
    private String ustat;

    public void onCreate() throws Exception {
        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox text_role_code = (Textbox) getSpaceOwner().getFellow("text_role_code");
        Textbox text_func_id = (Textbox) getSpaceOwner().getFellow("text_func_id");

        String user_code = "";
        user_code = (String) Executions.getCurrent().getAttribute("user_code");

        log.info("user_code=====>" + user_code);

        EPAY_SYS_ROLEFUNCS rulefunc;// = new EPAY_SYS_FUNCTIONS();
        int id = Integer.valueOf(user_code);
        rulefunc = epaybusinesscontroller.queryRuleFuncById(id);
        text_role_code.setValue(rulefunc.getFr_id());
        text_func_id.setValue(rulefunc.getF_id());
    }

    public void onModify() throws Exception {
        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox text_role_code = (Textbox) getSpaceOwner().getFellow("text_role_code");
        Textbox text_func_id = (Textbox) getSpaceOwner().getFellow("text_func_id");

        String rid = "", fid = "";
        rid = text_func_id.getValue();
        fid = text_func_id.getValue();

        EPAY_SYS_ROLEFUNCS func = new EPAY_SYS_ROLEFUNCS();
        func.setFr_id(rid);
        func.setF_id(fid);

        if (epaybusinesscontroller.updateRoleFunc(func)) {
            //************Micropayment Logger start***************************
            try {
                String log_user_id = "";
                log_user_id = (String) Sessions.getCurrent().getAttribute("user_code");
                log.info(" modify Func " + func.getFr_id() + " success!");
            } catch (Exception ex) {
            }
            Messagebox.show("修改資料成功", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);

        } else {
            Messagebox.show("修改資料失敗", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);

        }

    }
}
