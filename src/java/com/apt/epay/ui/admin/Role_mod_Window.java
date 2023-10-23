/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.epay.ejb.bean.EPAY_SYS_ROLES;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Include;
/**
 *
 * @author kevinchang
 */
public class Role_mod_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");
    
    public void onCreate() throws Exception {
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Listbox list_role = (Listbox) getSpaceOwner().getFellow("list_role");

        List listrole ;//= null;
        Iterator itrole;// = null;
        listrole = epaybusinesscontroller.getAllRoles();
        if (listrole != null && !listrole.isEmpty()) {
            EPAY_SYS_ROLES roles;// = new EPAY_SYS_FUNCTIONS();
            itrole = listrole.iterator();
            while (itrole.hasNext()) {
                roles = (EPAY_SYS_ROLES) itrole.next();
                Listitem useritem = new Listitem();
                useritem.setId(roles.getR_id());
                new Listcell(roles.getR_id()).setParent(useritem);
                new Listcell(roles.getR_name()).setParent(useritem);
                useritem.setParent(list_role);
            }
        }
    }

    public void onSelect() {
        
        this.detach();
        Map model = new HashMap();
        Listbox list_role = (Listbox) getSpaceOwner().getFellow("list_role");
        
        String user_code = "";
        user_code = list_role.getSelectedItem().getId();
        log.info("user_code=============>"+user_code);
        
        
        Map map_parms = new HashMap();

        map_parms.put("user_code", user_code);

        Executions.getCurrent().setAttribute("map_parms", map_parms);
//        Executions.getCurrent().setAttribute("url", "user_limit_mod.zul");
        Executions.getCurrent().setAttribute("user_code", user_code);
        //Window contactWnd = (Window) Executions.createComponents("account_profile.zul", inc, model);

        Window contactWnd = (Window) Executions.createComponents("role_profile.zul", null, model);

        Include inc = (Include) Sessions.getCurrent().getAttribute("xcontents");
        inc.appendChild(contactWnd);

    }    
}
