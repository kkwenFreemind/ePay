/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.epay.ejb.bean.EPAY_COMMON_USER;
import com.epay.ejb.bean.EPAY_SYS_ROLES;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Include;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class Account_del_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    public void onCreate() {

//        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
//        Radiogroup role_group = (Radiogroup) getSpaceOwner().getFellow("role_group");
//
//        List roles_list = epaybusinesscontroller.getAllRoles();
//        Iterator roles_it = roles_list.iterator();
//        while (roles_it.hasNext()) {
//            EPAY_SYS_ROLES roles;// = new EPAY_SYS_ROLES();
//            roles = (EPAY_SYS_ROLES) roles_it.next();
//            Radio role_radio = new Radio();
//            role_radio.setId(roles.getR_id());
//            role_radio.setLabel(roles.getR_name());
//            role_radio.setParent(role_group);
//        }
        onQuery();

    }

    public void onClear() {
//        Textbox text_user_code = (Textbox) getSpaceOwner().getFellow("text_user_code");
//        Textbox text_user_name = (Textbox) getSpaceOwner().getFellow("text_user_name");
//        Textbox text_email = (Textbox) getSpaceOwner().getFellow("text_email");
//        Textbox text_mobile = (Textbox) getSpaceOwner().getFellow("text_mobile");
//        Radiogroup role_group = (Radiogroup) getSpaceOwner().getFellow("role_group");
//        Listbox list_user = (Listbox) getSpaceOwner().getFellow("list_user");
//        try {
//            AbstractComponent[] acs = (AbstractComponent[]) list_user.getChildren().toArray(new Component[0]);
//
//            for (AbstractComponent ac : acs) {
//                if (ac instanceof Listhead) {
//                    continue;
//                }
//                list_user.removeChild(ac);
//            }
//        } catch (Exception ex) {
//        }
//        text_user_code.setValue("");
//        text_user_name.setValue("");
//        text_email.setValue("");
//        text_mobile.setValue("");
//        role_group.setSelectedIndex(-1);
    }

    public void onQuery() {
        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
//        Textbox text_user_code = (Textbox) getSpaceOwner().getFellow("text_user_code");
//        Textbox text_user_name = (Textbox) getSpaceOwner().getFellow("text_user_name");
//        Textbox text_email = (Textbox) getSpaceOwner().getFellow("text_email");
//        Textbox text_mobile = (Textbox) getSpaceOwner().getFellow("text_mobile");
//        Radiogroup role_group = (Radiogroup) getSpaceOwner().getFellow("role_group");
        Listbox list_user = (Listbox) getSpaceOwner().getFellow("list_user");
        String user_code = "", user_name = "", user_mobile = "", user_email = "", user_role = "";
//        user_code = text_user_code.getValue();
//        user_name = text_user_name.getValue();
//        user_mobile = text_mobile.getValue();
//        user_email = text_email.getValue();
//        if (role_group.getSelectedItem() != null) {
//            user_role = role_group.getSelectedItem().getId();
//        }

        try {
            AbstractComponent[] acs = (AbstractComponent[]) list_user.getChildren().toArray(new Component[0]);

            for (AbstractComponent ac : acs) {
                if (ac instanceof Listhead) {
                    continue;
                }
                list_user.removeChild(ac);
            }
        } catch (Exception ex) {
            log.info(ex);
        }

        List listuser;// = null;
        Iterator ituser;// = null;
        listuser = epaybusinesscontroller.getUserList(user_code, user_name, user_mobile, user_role);
        if (listuser != null && !listuser.isEmpty()) {
            EPAY_COMMON_USER user;// = new EPAY_COMMON_USER();
            ituser = listuser.iterator();
            while (ituser.hasNext()) {
                user = (EPAY_COMMON_USER) ituser.next();
                Listitem useritem = new Listitem();
                useritem.setId(user.getCuser_code());
                new Listcell(user.getPg_sys_roles().getR_name()).setParent(useritem);
                new Listcell(user.getCuser_code()).setParent(useritem);
                new Listcell(user.getCuser_name()).setParent(useritem);
                new Listcell(user.getCuser_email()).setParent(useritem);
                new Listcell(user.getCuser_mobile_number()).setParent(useritem);
                useritem.setParent(list_user);
            }
        }
    }

    public void onDelete() throws Exception {

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Listbox list_user = (Listbox) getSpaceOwner().getFellow("list_user");
        Set selecteditems = list_user.getSelectedItems();
        Iterator selectedit = selecteditems.iterator();
        ArrayList<String> list = new ArrayList<String>();
        while (selectedit.hasNext()) {
            Listitem item = (Listitem) selectedit.next();
            list.add(item.getId());
        }
        if (list_user.getSelectedItem() != null) {
            if (epaybusinesscontroller.deleteUser_ByID(list)) {

                try {
                    String sessopm_user_code = (String) Sessions.getCurrent().getAttribute("user_code");
                    for (String id : list) {
                        log.info("IP:" + Executions.getCurrent().getRemoteAddr() + " Account:" + sessopm_user_code + " del Account: :" + id + " Success");
                    }
                } catch (Exception ex) {
                    log.info(ex);
                }
                Messagebox.show("刪除用戶成功", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);

                this.detach();
                Map model = new HashMap();
                Window contactWnd = (Window) Executions.createComponents("account_del.zul", null, model);
                Include inc = (Include) Sessions.getCurrent().getAttribute("xcontents");
                inc.appendChild(contactWnd);

            } else {
                Messagebox.show("刪除用戶失敗", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
            }
        } else {
            Messagebox.show("無選擇可刪除用戶", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
        }

//        Include inc = null;
//        try {
//            inc = (Include) getDesktop().getComponentByUuid((String) Sessions.getCurrent().getAttribute("xcontents"));
//        } catch (Exception ex) {
//            log.info(ex);
//        }
//        ShareBean.backToPage(Sessions.getCurrent(), Executions.getCurrent(), inc, this);
//        onClear();
    }
}
