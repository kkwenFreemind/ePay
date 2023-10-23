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
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

/**
 *
 * @author kevinchang
 */
public class Role_profile_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");
    
    private String uid;
    private String ustat;

    public void onCreate() throws Exception {
        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox text_role_code = (Textbox) getSpaceOwner().getFellow("text_role_code");
        Textbox text_role_name = (Textbox) getSpaceOwner().getFellow("text_role_name");

        String user_code = "";
        user_code = (String) Executions.getCurrent().getAttribute("user_code");
        
        log.info("user_code=====>"+user_code);

        EPAY_SYS_ROLES role;// = new EPAY_SYS_FUNCTIONS();
        role = epaybusinesscontroller.getRoles_ByRolesCode(user_code);
        uid = role.getR_id();
        text_role_code.setValue(uid);
        text_role_name.setValue(role.getR_name());

    }

    public void onModify() throws Exception {
        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox text_role_code = (Textbox) getSpaceOwner().getFellow("text_role_code");
        Textbox text_role_name = (Textbox) getSpaceOwner().getFellow("text_role_name");

        String role_id = "", role_name = "";
        role_id = text_role_code.getValue();
        role_name = text_role_name.getValue();

        EPAY_SYS_ROLES role = new EPAY_SYS_ROLES();
        role.setR_id(role_id);
        role.setR_name(role_name);
        
        if (epaybusinesscontroller.updateRole(role)) {
            //************Micropayment Logger start***************************
            try {
                String log_user_id = "";
                log_user_id = (String) Sessions.getCurrent().getAttribute("user_code");
                log.info( " modify Role " + role.getR_id() + " success!");
            } catch (Exception ex) {
            }
            Messagebox.show("修改角色資料成功", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);

        } else {
            Messagebox.show("修改角色資料失敗", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);

        }

    }    
}
