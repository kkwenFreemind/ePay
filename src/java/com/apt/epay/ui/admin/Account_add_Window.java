/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.util.AESUtil;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_COMMON_USER;
import com.epay.ejb.bean.EPAY_SYS_ROLES;

import java.util.Iterator;
import java.util.List;
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
public class Account_add_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    public void onClear() {

        Textbox text_user_code = (Textbox) getSpaceOwner().getFellow("text_user_code");
        Textbox text_user_name = (Textbox) getSpaceOwner().getFellow("text_user_name");
        Textbox text_email = (Textbox) getSpaceOwner().getFellow("text_email");
        Textbox text_mobile = (Textbox) getSpaceOwner().getFellow("text_mobile");
        Radiogroup role_group = (Radiogroup) getSpaceOwner().getFellow("role_group");
        Textbox password = (Textbox) getSpaceOwner().getFellow("password");

        text_user_code.setValue("");
        text_user_name.setValue("");
        text_email.setValue("");
        text_mobile.setValue("");
        role_group.setSelectedIndex(-1);
        password.setValue("");
    }

    public void onCreate() {
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Radiogroup role_group = (Radiogroup) getSpaceOwner().getFellow("role_group");
        Textbox password = (Textbox) getSpaceOwner().getFellow("password");

        List roles_list = epaybusinesscontroller.getAllRoles();
        Iterator roles_it = roles_list.iterator();
        while (roles_it.hasNext()) {
            EPAY_SYS_ROLES roles;// = new EPAY_SYS_ROLES();
            roles = (EPAY_SYS_ROLES) roles_it.next();
            Radio role_radio = new Radio();
            role_radio.setId(roles.getR_id());
            role_radio.setLabel(roles.getR_name());
            role_radio.setParent(role_group);
        }
    }

    public void addAccount() throws Exception {

        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox text_user_code = (Textbox) getSpaceOwner().getFellow("text_user_code");
        Textbox text_user_name = (Textbox) getSpaceOwner().getFellow("text_user_name");
        Textbox text_email = (Textbox) getSpaceOwner().getFellow("text_email");
        Textbox text_mobile = (Textbox) getSpaceOwner().getFellow("text_mobile");
        Radiogroup role_group = (Radiogroup) getSpaceOwner().getFellow("role_group");
        Textbox password = (Textbox) getSpaceOwner().getFellow("password");

        String user_code, user_name, user_password = "", user_mobile, user_email, user_role = "";

        user_code = text_user_code.getValue();
        user_name = text_user_name.getValue();

        String sessopm_user_code = (String) Sessions.getCurrent().getAttribute("user_code");
        EPAY_COMMON_USER ppuser = new EPAY_COMMON_USER();
        ppuser = epaybusinesscontroller.getCommonUser_ByCode(user_code);

        if (ppuser == null) {

            if (password.getValue() != null && !password.getValue().equals("")) {
                Utilities util = new Utilities();
                String result = util.checkPassword(password.getValue());
                if ("強".equals(result)) {

                    user_password = AESUtil.Encrypt(password.getValue());
                    user_mobile = text_mobile.getValue();
                    user_email = text_email.getValue();
                    if (role_group.getSelectedItem() != null) {
                        user_role = role_group.getSelectedItem().getId();
                    }
                    EPAY_COMMON_USER user = new EPAY_COMMON_USER();
                    user.setCuser_code(user_code);
                    user.setCuser_email(user_email);
                    user.setCuser_mobile_number(user_mobile);
                    user.setCuser_name(user_name);
                    user.setCuser_password(user_password);
                    user.setCuser_status(ShareParm.PARM_MOBILE_USER_DEFAULT_USER_STATUS);
                    EPAY_SYS_ROLES sys_roles;// = new EPAY_SYS_ROLES();
                    if (user_role != null && !user_role.equals("")) {
                        sys_roles = epaybusinesscontroller.getRoles_ByRolesCode(user_role);
                        user.setPg_sys_roles(sys_roles);
                        if (epaybusinesscontroller.addAccount(user)) {

                            log.info("IP:" + Executions.getCurrent().getRemoteAddr() + " Account:" + sessopm_user_code + " add Account :" + user_code + "," + user_name + " Success");
                            int show = Messagebox.show("新增用戶成功", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);

                            Include inc = null;
                            try {
                                inc = (Include) getDesktop().getComponentByUuid((String) Sessions.getCurrent().getAttribute("xcontents"));
                            } catch (Exception ex) {
                                log.info(ex);
                            }
                            ShareBean.backToPage(Sessions.getCurrent(), Executions.getCurrent(), inc, this);
                            onClear();

                        } else {
                            Messagebox.show("新增用戶失敗", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
                        }
                    } else {
                        Messagebox.show("請選擇權限角色", "亞太電信", Messagebox.OK, Messagebox.ERROR);
                    }
                } else {
                    Messagebox.show("密碼強度不足:" + result, "亞太電信", Messagebox.OK, Messagebox.ERROR);
                }
            }
        } else {
            Messagebox.show("帳號ID重複", "亞太電信", Messagebox.OK, Messagebox.ERROR);
        }
    }
}
