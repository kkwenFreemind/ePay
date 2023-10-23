/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.epay.ejb.bean.EPAY_SYS_FUNCTIONS;
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
public class Func_mod_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    public void onCreate() throws Exception {
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Listbox list_func = (Listbox) getSpaceOwner().getFellow("list_func");

        List listfunc ;//= null;
        Iterator itfunc;// = null;
        listfunc = epaybusinesscontroller.listAllFunc();
        if (listfunc != null && !listfunc.isEmpty()) {
            EPAY_SYS_FUNCTIONS funcs;// = new EPAY_SYS_FUNCTIONS();
            itfunc = listfunc.iterator();
            while (itfunc.hasNext()) {
                funcs = (EPAY_SYS_FUNCTIONS) itfunc.next();
                Listitem useritem = new Listitem();
                useritem.setId(funcs.getF_id());
                new Listcell(funcs.getF_id()).setParent(useritem);
                new Listcell(funcs.getF_name()).setParent(useritem);
                new Listcell(funcs.getP_name()).setParent(useritem);
                new Listcell(funcs.getF_url()).setParent(useritem);
                useritem.setParent(list_func);
            }
        }
    }

    public void onSelect() {
        
        this.detach();
        Map model = new HashMap();
        Listbox list_func = (Listbox) getSpaceOwner().getFellow("list_func");
        
        String user_code;// = "";
        user_code = list_func.getSelectedItem().getId();
        log.info("user_code=============>"+user_code);
        
        
        Map map_parms = new HashMap();

        map_parms.put("user_code", user_code);

        Executions.getCurrent().setAttribute("map_parms", map_parms);
//        Executions.getCurrent().setAttribute("url", "user_limit_mod.zul");
        Executions.getCurrent().setAttribute("user_code", user_code);
        //Window contactWnd = (Window) Executions.createComponents("account_profile.zul", inc, model);

        Window contactWnd = (Window) Executions.createComponents("func_profile.zul", null, model);

        Include inc = (Include) Sessions.getCurrent().getAttribute("xcontents");
        inc.appendChild(contactWnd);

    }
}
