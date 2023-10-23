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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Include;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class Account_mod_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    public void onCreate() {
        
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Listbox list_user = (Listbox) getSpaceOwner().getFellow("list_user");

        try {
            List listuser;//= null;
            Iterator ituser;
            listuser = epaybusinesscontroller.getAllUserList();
            if (listuser != null && !listuser.isEmpty()) {
                EPAY_COMMON_USER user;// = new EPAY_COMMON_USER();
                ituser = listuser.iterator();
                while (ituser.hasNext()) {
                    user = (EPAY_COMMON_USER) ituser.next();
                    Listitem useritem = new Listitem();
                    useritem.setId(user.getCuser_code());

                    String dd = AESUtil.Decrypt(user.getCuser_password());
                    Utilities util = new Utilities();
                    String result = util.checkPassword(dd);
                    
                    new Listcell(user.getPg_sys_roles().getR_name()).setParent(useritem);
                    new Listcell(user.getCuser_code()).setParent(useritem);
                    new Listcell(user.getCuser_name()).setParent(useritem);
                    new Listcell(result).setParent(useritem);
                    new Listcell(user.getCuser_email()).setParent(useritem);
                    new Listcell(user.getCuser_mobile_number()).setParent(useritem);
                    useritem.setParent(list_user);
                }
            }
        } catch (Exception ex) {
            log.info(ex);
        }
    }


    public void onClear() {

    }

    public void onQuery() {

    }

    public void onSelect() {

        this.detach();
        Map model = new HashMap();
        Listbox list_user = (Listbox) getSpaceOwner().getFellow("list_user");

        String mod_user_code;// = "";
        mod_user_code = list_user.getSelectedItem().getId();

        Map map_parms = new HashMap();

        map_parms.put("mod_user_code", mod_user_code);

        Executions.getCurrent().setAttribute("map_parms", map_parms);
        Executions.getCurrent().setAttribute("mod_user_code", mod_user_code);

        Window contactWnd = (Window) Executions.createComponents("account_profile.zul", null, model);
        System.out.println(contactWnd);
        
        Include inc = (Include) Sessions.getCurrent().getAttribute("xcontents");
        inc.appendChild(contactWnd);

    }
}
