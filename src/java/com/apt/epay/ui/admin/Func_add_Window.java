/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.epay.ejb.bean.EPAY_SYS_FUNCTIONS;
import com.epay.ejb.bean.EPAY_SYS_ROLES;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class Func_add_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    public void onClear() {
        Textbox text_func_id = (Textbox) getSpaceOwner().getFellow("text_func_id");
        Textbox text_func_pname = (Textbox) getSpaceOwner().getFellow("text_func_pname");
        Textbox text_func_fname = (Textbox) getSpaceOwner().getFellow("text_func_fname");
        Textbox text_url = (Textbox) getSpaceOwner().getFellow("text_url");

        text_func_id.setValue("");
        text_func_pname.setValue("");
        text_func_fname.setValue("");
        text_url.setValue("");

    }

    public void onCreate() throws Exception {
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        Textbox text_func_id = (Textbox) getSpaceOwner().getFellow("text_func_id");
        Textbox text_func_pname = (Textbox) getSpaceOwner().getFellow("text_func_pname");
        Textbox text_func_fname = (Textbox) getSpaceOwner().getFellow("text_func_fname");
        Textbox text_url = (Textbox) getSpaceOwner().getFellow("text_url");

        Listbox list_func = (Listbox) getSpaceOwner().getFellow("list_func");

        List listfunc;//= null;
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

    public void addFunc() throws Exception {
        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox text_func_id = (Textbox) getSpaceOwner().getFellow("text_func_id");
        Textbox text_func_pname = (Textbox) getSpaceOwner().getFellow("text_func_pname");
        Textbox text_func_fname = (Textbox) getSpaceOwner().getFellow("text_func_fname");
        Textbox text_url = (Textbox) getSpaceOwner().getFellow("text_url");

        String func_id , func_pname, func_fname , func_url ;

        func_id = text_func_id.getValue();
        func_pname = text_func_pname.getValue();
        func_fname = text_func_fname.getValue();
        func_url = text_url.getValue();

        EPAY_SYS_FUNCTIONS func = new EPAY_SYS_FUNCTIONS();

        func.setF_id(func_id);
        func.setF_name(func_fname);
        func.setP_name(func_pname);
        func.setF_url(func_url);

        if (epaybusinesscontroller.insert_Sys_Func(func)) {

            Messagebox.show("新增功能設定成功", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);

            text_func_id.setValue("");
            text_func_pname.setValue("");
            text_func_fname.setValue("");
            text_url.setValue("");

            Listbox list_func = (Listbox) getSpaceOwner().getFellow("list_func");

            List listfunc;//= null;
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

        } else {
            Messagebox.show("新增功能設定失敗", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);

        }

    }
}
