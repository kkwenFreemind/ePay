/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import org.zkoss.zul.Window;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.apt.util.AESUtil;
import com.epay.ejb.bean.EPAY_SYS_FUNCTIONS;

import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
//import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class Func_profile_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");
    private String uid;
    private String ustat;

    public void onCreate() throws Exception {
        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox text_func_id = (Textbox) getSpaceOwner().getFellow("text_func_id");
        Textbox text_pname = (Textbox) getSpaceOwner().getFellow("text_pname");
        Textbox text_fname = (Textbox) getSpaceOwner().getFellow("text_fname");
        Textbox text_url = (Textbox) getSpaceOwner().getFellow("text_url");

        String user_code = "";
        user_code = (String) Executions.getCurrent().getAttribute("user_code");
        
        log.info("user_code=====>"+user_code);

        EPAY_SYS_FUNCTIONS func;// = new EPAY_SYS_FUNCTIONS();
        func = epaybusinesscontroller.getFunc(user_code);
        uid = func.getF_id();
        text_func_id.setValue(user_code);
        text_pname.setValue(func.getP_name());
        text_fname.setValue(func.getF_name());
        text_url.setValue(func.getF_url());

    }

    public void onModify() throws Exception {
        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox text_func_id = (Textbox) getSpaceOwner().getFellow("text_func_id");
        Textbox text_pname = (Textbox) getSpaceOwner().getFellow("text_pname");
        Textbox text_fname = (Textbox) getSpaceOwner().getFellow("text_fname");
        Textbox text_url = (Textbox) getSpaceOwner().getFellow("text_url");

        String func_id = "", fname = "", pname = "", url = "";
        func_id = text_func_id.getValue();
        fname = text_pname.getValue();
        pname = text_fname.getValue();
        url = text_url.getValue();

        EPAY_SYS_FUNCTIONS func = new EPAY_SYS_FUNCTIONS();
        func.setF_id(func_id);
        func.setF_name(fname);
        func.setP_name(pname);
        func.setF_url(url);
        
        if (epaybusinesscontroller.updateFunc(func)) {
            //************Micropayment Logger start***************************
            try {
                String log_user_id = "";
                log_user_id = (String) Sessions.getCurrent().getAttribute("user_code");
                log.info( " modify Func " + func.getF_id() + " success!");
            } catch (Exception ex) {
            }
            Messagebox.show("修改功能資料成功", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);

        } else {
            Messagebox.show("修改功能資料失敗", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);

        }

    }
}
