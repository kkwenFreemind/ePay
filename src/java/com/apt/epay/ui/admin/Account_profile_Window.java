/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.apt.util.AESUtil;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_COMMON_USER;
import com.epay.ejb.bean.EPAY_SYS_ROLES;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Include;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class Account_profile_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");
    private Integer uid;
    private String ustat;

    public void onCreate() {
        
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox text_user_code = (Textbox) getSpaceOwner().getFellow("text_user_code");
        Textbox text_user_name = (Textbox) getSpaceOwner().getFellow("text_user_name");
        Textbox text_email = (Textbox) getSpaceOwner().getFellow("text_email");
        Textbox text_mobile = (Textbox) getSpaceOwner().getFellow("text_mobile");
        Textbox password = (Textbox) getSpaceOwner().getFellow("password");

        Radiogroup role_group = (Radiogroup) getSpaceOwner().getFellow("role_group");

        String mod_user_code;// = "";
        mod_user_code = (String) Executions.getCurrent().getAttribute("mod_user_code");

        EPAY_COMMON_USER user;// = new EPAY_COMMON_USER();
        user = epaybusinesscontroller.getCommonUser_ByCode(mod_user_code);
        uid = user.getCuser_id();
        ustat = user.getCuser_status();
        text_user_code.setValue(mod_user_code);
        text_user_name.setValue(user.getCuser_name());
        text_email.setValue(user.getCuser_email());
        text_mobile.setValue(user.getCuser_mobile_number());
        try {

            password.setValue(AESUtil.Decrypt(user.getCuser_password()));

            List roles_list = epaybusinesscontroller.getAllRoles();
            Iterator roles_it = roles_list.iterator();
            while (roles_it.hasNext()) {
                EPAY_SYS_ROLES roles;// = new EPAY_SYS_ROLES();
                roles = (EPAY_SYS_ROLES) roles_it.next();
                Radio role_radio = new Radio();
                role_radio.setId(roles.getR_id());
                role_radio.setLabel(roles.getR_name());
                role_radio.setParent(role_group);
                if (roles.getR_id().equals(user.getPg_sys_roles().getR_id())) {
                    role_radio.setChecked(true);
                }
            }
        } catch (Exception ex) {
            log.info(ex);
        }
    }

    public void onCancel() throws Exception {

        this.detach();
        Map model = new HashMap();
        Window contactWnd = (Window) Executions.createComponents("account_mod.zul", null, model);
        Include inc = (Include) Sessions.getCurrent().getAttribute("xcontents");
        inc.appendChild(contactWnd);

    }

    public void onModify() throws Exception {
        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox text_user_code = (Textbox) getSpaceOwner().getFellow("text_user_code");
        Textbox text_user_name = (Textbox) getSpaceOwner().getFellow("text_user_name");
        Textbox text_email = (Textbox) getSpaceOwner().getFellow("text_email");
        Textbox text_mobile = (Textbox) getSpaceOwner().getFellow("text_mobile");
        Textbox password = (Textbox) getSpaceOwner().getFellow("password");

        Radiogroup role_group = (Radiogroup) getSpaceOwner().getFellow("role_group");
        String user_code, user_name, user_mobile, user_email, user_role = "";
        user_code = text_user_code.getValue();
        user_name = text_user_name.getValue();
        user_mobile = text_mobile.getValue();
        user_email = text_email.getValue();
        if (role_group.getSelectedItem() != null) {
            user_role = role_group.getSelectedItem().getId();
        }

        //檢查密碼強度
        String str_new_password = password.getValue();
        Utilities util = new Utilities();
        String result = util.checkPassword(str_new_password);
        log.info("checkPassword Result ===>" + result);
        if ("強".equals(result)) {

            try {
                EPAY_COMMON_USER user = new EPAY_COMMON_USER();
                user.setCuser_id(uid);
                user.setCuser_status(ustat);
                user.setCuser_code(user_code);
                user.setCuser_email(user_email);
                user.setCuser_mobile_number(user_mobile);
                user.setCuser_name(user_name);
                user.setCuser_password(AESUtil.Encrypt(password.getValue()));
                EPAY_SYS_ROLES roles;//= new EPAY_SYS_ROLES();
                roles = epaybusinesscontroller.getRoles_ByRolesCode(user_role);
                user.setPg_sys_roles(roles);
                String sesso_user_code = (String) Sessions.getCurrent().getAttribute("user_code");

                if (epaybusinesscontroller.updateUser(user)) {

                    log.info("IP:" + Executions.getCurrent().getRemoteAddr() + " Account:" + sesso_user_code + " modify account: " + user_code + " Success");

                    Messagebox.show("修改用戶資料成功", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);

                    this.detach();
                    Map model = new HashMap();
                    Window contactWnd = (Window) Executions.createComponents("account_mod.zul", null, model);
                    Include inc = (Include) Sessions.getCurrent().getAttribute("xcontents");
                    inc.appendChild(contactWnd);
                } else {
                    log.info("IP:" + Executions.getCurrent().getRemoteAddr() + " Account:" + sesso_user_code + " modify account: " + user_code + " Failed");
                    Messagebox.show("修改用戶資料失敗", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
                }
            } catch (Exception ex) {
                log.info(ex);
            }
        } else {
            log.info("IP:" + Executions.getCurrent().getRemoteAddr() + " account: " + user_name );
            log.info("new_password: " + str_new_password + " is Weak");
            org.zkoss.zhtml.Messagebox.show("新密碼強度不足，請重新設定", "亞太電信", org.zkoss.zhtml.Messagebox.OK, org.zkoss.zhtml.Messagebox.ERROR);
        }
    }
}
