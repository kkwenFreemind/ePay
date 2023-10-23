/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.epay.ejb.bean.EPAY_DTONESERVICE_INFO;
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
public class DTOne_ServiceInfo_mod_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    public void onCreate() {

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Listbox list_serviceinfox = (Listbox) getSpaceOwner().getFellow("list_serviceinfox");

        List list_serviceinfo = null;
        Iterator itserv = null;
        try {
            list_serviceinfo = epaybusinesscontroller.listDTOneServiceInfo();
            if (list_serviceinfo != null && !list_serviceinfo.isEmpty()) {
                EPAY_DTONESERVICE_INFO serviceinfo;// = new EPAY_DTONESERVICE_INFO();
                itserv = list_serviceinfo.iterator();
                while (itserv.hasNext()) {
                    serviceinfo = (EPAY_DTONESERVICE_INFO) itserv.next();
                    Listitem useritem = new Listitem();
                    useritem.setId(String.valueOf(serviceinfo.getServiceId()));

                    String flag = "";
                    if (serviceinfo.getFlag() == 1) {
                        flag = "啟用";
                    } else {
                        flag = "停用";
                    }

                    new Listcell(String.valueOf(serviceinfo.getServiceId())).setParent(useritem);
                    new Listcell(serviceinfo.getServiceName()).setParent(useritem);
                    new Listcell(flag).setParent(useritem);
                    new Listcell(serviceinfo.getNote()).setParent(useritem);
                    new Listcell(String.valueOf(serviceinfo.getPrice())).setParent(useritem);
                    new Listcell(serviceinfo.getOperator_name()).setParent(useritem);
                    new Listcell(String.valueOf(serviceinfo.getPrice_id())).setParent(useritem);
                    new Listcell(serviceinfo.getPromotioncode()).setParent(useritem);
                    useritem.setParent(list_serviceinfox);
                }
            }
        } catch (Exception ex) {
            log.info(ex);
        }

    }

    public void onSelect() {

        this.detach();
        Map model = new HashMap();
        Listbox list_serviceinfox = (Listbox) getSpaceOwner().getFellow("list_serviceinfox");

        String serviceid = list_serviceinfox.getSelectedItem().getId();
        Map map_parms = new HashMap();

        map_parms.put("serviceid", serviceid);
        Executions.getCurrent().setAttribute("map_parms", map_parms);
        Executions.getCurrent().setAttribute("serviceid", serviceid);
        Window contactWnd = (Window) Executions.createComponents("dtoneserviceinfo_profile.zul", null, model);

        Include inc = (Include) Sessions.getCurrent().getAttribute("xcontents");
        inc.appendChild(contactWnd);
    }
}
