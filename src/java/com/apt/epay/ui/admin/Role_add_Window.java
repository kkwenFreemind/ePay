/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.epay.ejb.bean.EPAY_SYS_ROLES;
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
public class Role_add_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    public void onClear() {
        Textbox text_user_code = (Textbox) getSpaceOwner().getFellow("text_user_code");
        Textbox text_role_name = (Textbox) getSpaceOwner().getFellow("text_role_name");
//        Textbox text_fun_id = (Textbox) getSpaceOwner().getFellow("text_fun_id");

        text_user_code.setValue("");
        text_role_name.setValue("");
//        text_fun_id.setValue("");

    }

    public void onCreate() {
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        Textbox text_user_code = (Textbox) getSpaceOwner().getFellow("text_user_code");
        Textbox text_role_name = (Textbox) getSpaceOwner().getFellow("text_role_name");
//        Textbox text_fun_id = (Textbox) getSpaceOwner().getFellow("text_fun_id");
        
        text_user_code.setValue("");
        text_role_name.setValue("");
//        text_fun_id.setValue("");        
    }

    public void addRole() throws Exception {
        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox text_user_code = (Textbox) getSpaceOwner().getFellow("text_user_code");
        Textbox text_role_name = (Textbox) getSpaceOwner().getFellow("text_role_name");
//        Textbox text_fun_id = (Textbox) getSpaceOwner().getFellow("text_fun_id");

        String user_code = null, rolename = null;

        user_code = text_user_code.getValue();
        rolename = text_role_name.getValue();
//        funcid = text_fun_id.getValue();

        EPAY_SYS_ROLES role = new EPAY_SYS_ROLES();

        role.setR_id(user_code);
        role.setR_name(rolename);
//        role.setR_func_id(funcid);

        if (epaybusinesscontroller.insertRole(role)) {

            Messagebox.show("新增角色設定成功", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);

        } else {
            Messagebox.show("新增角色設定失敗", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);

        }

    }
}
