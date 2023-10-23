/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;


import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.epay.ejb.bean.EPAY_SYS_ROLEFUNCS;
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
public class Role_Func_add_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    public void onClear() {
        Textbox text_user_code = (Textbox) getSpaceOwner().getFellow("text_user_code");
        Textbox text_func_id = (Textbox) getSpaceOwner().getFellow("text_func_id");

        text_user_code.setValue("");
        text_func_id.setValue("");

    }

    public void onCreate() {
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        Textbox text_user_code = (Textbox) getSpaceOwner().getFellow("text_user_code");
        Textbox text_func_id = (Textbox) getSpaceOwner().getFellow("text_func_id");
//        Textbox text_fun_id = (Textbox) getSpaceOwner().getFellow("text_fun_id");

        text_user_code.setValue("");
        text_func_id.setValue("");
//        text_fun_id.setValue("");        
    }

    public void add() throws Exception {
        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox text_user_code = (Textbox) getSpaceOwner().getFellow("text_user_code");
        Textbox text_func_id = (Textbox) getSpaceOwner().getFellow("text_func_id");

        String user_code = null, func_id = null;

        user_code = text_user_code.getValue();
        func_id = text_func_id.getValue();

        EPAY_SYS_ROLEFUNCS rolefunc = new EPAY_SYS_ROLEFUNCS();

        rolefunc.setFr_id(user_code);
        rolefunc.setF_id(func_id);

        if (epaybusinesscontroller.insertRoleFunc(rolefunc)){
            Messagebox.show("新增角色功能設定成功", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);

        } else {
            Messagebox.show("新增角色功能設定失敗", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);

        }

    }
}
