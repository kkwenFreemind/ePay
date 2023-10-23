/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.epay.ejb.bean.EPAY_SERVICE_INFO;
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
//import org.zkoss.zul.Radiogroup;
//import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class ServiceInfo_mod_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    public void onCreate() {

        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
//        Textbox text_serviceid = (Textbox) getSpaceOwner().getFellow("text_serviceid");
//        Textbox text_servicename = (Textbox) getSpaceOwner().getFellow("text_servicename");
//        Textbox text_glcode = (Textbox) getSpaceOwner().getFellow("text_glcode");
//        Textbox text_price = (Textbox) getSpaceOwner().getFellow("text_price");
//        Textbox text_status = (Textbox) getSpaceOwner().getFellow("text_status");
//        Textbox text_day = (Textbox) getSpaceOwner().getFellow("text_day");
//        Textbox text_note = (Textbox) getSpaceOwner().getFellow("text_note");
//        Textbox text_flag = (Textbox) getSpaceOwner().getFellow("text_flag");
        Listbox list_serviceinfox = (Listbox) getSpaceOwner().getFellow("list_serviceinfox");
//
//        text_serviceid.setValue("");
//        text_servicename.setValue("");
//        text_glcode.setValue("");
//        text_price.setValue("");
//        text_status.setValue("");
//        text_day.setValue("");
//        text_note.setValue("");
//        text_flag.setValue("");

        List list_serviceinfo = null;
        Iterator itserv = null;
        try {
            list_serviceinfo = epaybusinesscontroller.listServiceInfo();
            if (list_serviceinfo != null && !list_serviceinfo.isEmpty()) {
                EPAY_SERVICE_INFO serviceinfo = new EPAY_SERVICE_INFO();
                itserv = list_serviceinfo.iterator();
                while (itserv.hasNext()) {
                    serviceinfo = (EPAY_SERVICE_INFO) itserv.next();
                    Listitem useritem = new Listitem();
                    useritem.setId(String.valueOf(serviceinfo.getServiceId()));
                    new Listcell(String.valueOf(serviceinfo.getServiceId())).setParent(useritem);
                    new Listcell(serviceinfo.getServiceName()).setParent(useritem);
                    new Listcell(serviceinfo.getGlcode()).setParent(useritem);
                    new Listcell(String.valueOf(serviceinfo.getPrice())).setParent(useritem);
//                    new Listcell(serviceinfo.getStatus()).setParent(useritem);
                    new Listcell(String.valueOf(serviceinfo.getDday())).setParent(useritem);
                    new Listcell(serviceinfo.getNote()).setParent(useritem);
                    new Listcell(String.valueOf(serviceinfo.getFlag())).setParent(useritem);
                    new Listcell(serviceinfo.getCardtype()).setParent(useritem);
                    useritem.setParent(list_serviceinfox);
                }
            }
        } catch (Exception ex) {
            log.info(ex);
        }

    }

    public void onClear() {
//        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
//        Textbox text_serviceid = (Textbox) getSpaceOwner().getFellow("text_serviceid");
//        Textbox text_servicename = (Textbox) getSpaceOwner().getFellow("text_servicename");
//        Textbox text_glcode = (Textbox) getSpaceOwner().getFellow("text_glcode");
//        Textbox text_price = (Textbox) getSpaceOwner().getFellow("text_price");
//        Textbox text_status = (Textbox) getSpaceOwner().getFellow("text_status");
//        Textbox text_day = (Textbox) getSpaceOwner().getFellow("text_day");
//        Textbox text_note = (Textbox) getSpaceOwner().getFellow("text_note");
//        Textbox text_flag = (Textbox) getSpaceOwner().getFellow("text_flag");
//
//        text_serviceid.setValue("");
//        text_servicename.setValue("");
//        text_glcode.setValue("");
//        text_price.setValue("");
//        text_status.setValue("");
//        text_day.setValue("");
//        text_note.setValue("");
//        text_flag.setValue("");
    }

    public void onQuery() {
//        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
//
//        Textbox text_serviceid = (Textbox) getSpaceOwner().getFellow("text_serviceid");
//        Textbox text_servicename = (Textbox) getSpaceOwner().getFellow("text_servicename");
//        Textbox text_glcode = (Textbox) getSpaceOwner().getFellow("text_glcode");
//        Textbox text_price = (Textbox) getSpaceOwner().getFellow("text_price");
//        Textbox text_status = (Textbox) getSpaceOwner().getFellow("text_status");
//        Textbox text_day = (Textbox) getSpaceOwner().getFellow("text_day");
//        Textbox text_note = (Textbox) getSpaceOwner().getFellow("text_note");
//        Textbox text_flag = (Textbox) getSpaceOwner().getFellow("text_flag");
//        Listbox list_serviceinfox = (Listbox) getSpaceOwner().getFellow("list_serviceinfox");
//
//        List list_serviceinfo = null;
//        Iterator itserv = null;
//        try {
//            list_serviceinfo = epaybusinesscontroller.listServiceInfo();
//            if (list_serviceinfo != null && !list_serviceinfo.isEmpty()) {
//                EPAY_SERVICE_INFO serviceinfo = new EPAY_SERVICE_INFO();
//                itserv = list_serviceinfo.iterator();
//                while (itserv.hasNext()) {
//                    serviceinfo = (EPAY_SERVICE_INFO) itserv.next();
//                    Listitem useritem = new Listitem();
//                    log.info("serviceinfo.getServiceId()===>" + serviceinfo.getServiceId());
//                    useritem.setId(String.valueOf(serviceinfo.getServiceId()));
//                    new Listcell(String.valueOf(serviceinfo.getServiceId())).setParent(useritem);
//                    new Listcell(serviceinfo.getServiceName()).setParent(useritem);
//                    new Listcell(serviceinfo.getGlcode()).setParent(useritem);
//                    new Listcell(String.valueOf(serviceinfo.getPrice())).setParent(useritem);
//                    new Listcell(serviceinfo.getStatus()).setParent(useritem);
//                    new Listcell(String.valueOf(serviceinfo.getDday())).setParent(useritem);
//                    new Listcell(serviceinfo.getNote()).setParent(useritem);
//                    new Listcell(String.valueOf(serviceinfo.getFlag())).setParent(useritem);
//                    useritem.setParent(list_serviceinfox);
//                }
//            }
//        } catch (Exception ex) {
//            log.info(ex);
//        }

    }

    public void onSelect() {
        
        this.detach();
        Map model = new HashMap();
        Listbox list_serviceinfox = (Listbox) getSpaceOwner().getFellow("list_serviceinfox");
        
        
        String serviceid = list_serviceinfox.getSelectedItem().getId();

//        Textbox text_serviceid = (Textbox) getSpaceOwner().getFellow("text_serviceid");
//        Textbox text_servicename = (Textbox) getSpaceOwner().getFellow("text_servicename");
//        Textbox text_glcode = (Textbox) getSpaceOwner().getFellow("text_glcode");
//        Textbox text_price = (Textbox) getSpaceOwner().getFellow("text_price");
//        Textbox text_status = (Textbox) getSpaceOwner().getFellow("text_status");
//        Textbox text_day = (Textbox) getSpaceOwner().getFellow("text_day");
//        Textbox text_note = (Textbox) getSpaceOwner().getFellow("text_note");
//        Textbox text_flag = (Textbox) getSpaceOwner().getFellow("text_flag");

//        String user_code_text = "", user_name = "", user_mobile = "", user_email = "", user_role = "";
//        user_code_text = text_user_code.getValue();
//        user_name = text_user_name.getValue();
//        user_mobile = text_mobile.getValue();
//        user_email = text_email.getValue();
//        if (role_group.getSelectedItem() != null) {
//            user_role = role_group.getSelectedItem().getId();
//        }
        Map map_parms = new HashMap();
//
        map_parms.put("serviceid", serviceid);
        Executions.getCurrent().setAttribute("map_parms", map_parms);
//        Executions.getCurrent().setAttribute("url", "user_limit_mod.zul");
        Executions.getCurrent().setAttribute("serviceid", serviceid);

        Window contactWnd = (Window) Executions.createComponents("serviceinfo_profile.zul", null, model);
//        Executions.getCurrent().sendRedirect("serviceinfo_profile.zul");
 
        Include inc = (Include) Sessions.getCurrent().getAttribute("xcontents");
        inc.appendChild(contactWnd);
    }
}
