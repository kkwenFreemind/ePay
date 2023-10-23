/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.epay.ejb.bean.EPAY_DTONESERVICE_INFO;
//import java.text.SimpleDateFormat;
//import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class DTOne_ServiceInfo_qry_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");
    private String cpid = new ShareParm().PARM_EPAY_CPID;

    public void onCreate() {

        Textbox text_serviceid = (Textbox) this.getFellow("text_serviceid");
        text_serviceid.setValue("");
        String sid = text_serviceid.getValue();

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        Listbox listbox_serviceinfo_log = (Listbox) this.getFellow("listbox_serviceinfo_log");

        try {
            AbstractComponent[] acs = (AbstractComponent[]) listbox_serviceinfo_log.getChildren().toArray(new AbstractComponent[0]);

            for (int i = 0; i < acs.length; i++) {
                if (acs[i] instanceof Listhead) {
                    continue;
                }
                listbox_serviceinfo_log.removeChild(acs[i]);
            }
        } catch (Exception ex) {
        }

        try {
            List slist;
            if (sid.equals("") || sid == null) {
                slist = epaybusinesscontroller.listDTOneServiceInfo();

            } else {
                slist = epaybusinesscontroller.listDTOneServiceInfoBySid(Long.valueOf(sid));
            }

            if (slist != null && !slist.isEmpty()) {
                Iterator it = slist.iterator();
                while (it.hasNext()) {
                    EPAY_DTONESERVICE_INFO serviceinfo;// = new EPAY_DTONESERVICE_INFO();
                    serviceinfo = (EPAY_DTONESERVICE_INFO) it.next();
                    Listitem listitemtx_log = new Listitem();
                    String flag ="";
                    if(serviceinfo.getFlag() == 1){
                        flag = "啟用";
                    }else{
                        flag = "停用";
                    }
                    new Listcell(String.valueOf(serviceinfo.getServiceId())).setParent(listitemtx_log);
                    new Listcell(serviceinfo.getServiceName()).setParent(listitemtx_log);
                    new Listcell(flag).setParent(listitemtx_log);
                    new Listcell(serviceinfo.getNote()).setParent(listitemtx_log);
                    new Listcell(String.valueOf(serviceinfo.getPrice())).setParent(listitemtx_log);
                    new Listcell(serviceinfo.getOperator_name()).setParent(listitemtx_log);
                    new Listcell(String.valueOf(serviceinfo.getPrice_id())).setParent(listitemtx_log);
                    new Listcell(serviceinfo.getPromotioncode()).setParent(listitemtx_log);

                    listitemtx_log.setParent(listbox_serviceinfo_log);
                }
            } else {
                try {
                    Messagebox.show("查詢無任何結果!!", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
                } catch (Exception ex) {
                    log.info(ex);
                }
            }
        } catch (Exception ex) {
            log.info(ex);
        }
    }

    public void onClear() {

        Textbox text_serviceid = (Textbox) this.getFellow("text_serviceid");
        text_serviceid.setValue("");
    }

    public void doQuery() {

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        Textbox text_serviceid = (Textbox) this.getFellow("text_serviceid");
        String sid = text_serviceid.getValue();

        Listbox listbox_serviceinfo_log = (Listbox) this.getFellow("listbox_serviceinfo_log");

        try {
            AbstractComponent[] acs = (AbstractComponent[]) listbox_serviceinfo_log.getChildren().toArray(new AbstractComponent[0]);

            for (int i = 0; i < acs.length; i++) {
                if (acs[i] instanceof Listhead) {
                    continue;
                }
                listbox_serviceinfo_log.removeChild(acs[i]);
            }
        } catch (Exception ex) {
        }

        try {
            List slist;
            if (sid.equals("") || sid == null) {
                slist = epaybusinesscontroller.listDTOneServiceInfo();
            } else {
                slist = epaybusinesscontroller.listDTOneServiceInfoBySid(Long.valueOf(sid));
            }

            if (slist != null && !slist.isEmpty()) {
                Iterator it = slist.iterator();
                while (it.hasNext()) {
                    EPAY_DTONESERVICE_INFO serviceinfo;// = new EPAY_DTONESERVICE_INFO();
                    serviceinfo = (EPAY_DTONESERVICE_INFO) it.next();
                    Listitem listitemtx_log = new Listitem();
                    
                    String flag ="";
                    if(serviceinfo.getFlag() == 1){
                        flag = "啟用";
                    }else{
                        flag = "停用";
                    }
                    new Listcell(String.valueOf(serviceinfo.getServiceId())).setParent(listitemtx_log);
                    new Listcell(serviceinfo.getServiceName()).setParent(listitemtx_log);
                    new Listcell(flag).setParent(listitemtx_log);
                    new Listcell(serviceinfo.getNote()).setParent(listitemtx_log);
                    new Listcell(String.valueOf(serviceinfo.getPrice())).setParent(listitemtx_log);
                    new Listcell(serviceinfo.getOperator_name()).setParent(listitemtx_log);
                    new Listcell(String.valueOf(serviceinfo.getPrice_id())).setParent(listitemtx_log);
                    new Listcell(serviceinfo.getPromotioncode()).setParent(listitemtx_log);
                    
//                    new Listcell(String.valueOf(serviceinfo.getServiceId())).setParent(listitemtx_log);
//                    new Listcell(serviceinfo.getServiceName()).setParent(listitemtx_log);
//                    new Listcell(serviceinfo.getNote()).setParent(listitemtx_log);
//                    new Listcell(String.valueOf(serviceinfo.getPrice())).setParent(listitemtx_log);
//                    new Listcell(serviceinfo.getOperator_name()).setParent(listitemtx_log);
//                    new Listcell(String.valueOf(serviceinfo.getPrice_id())).setParent(listitemtx_log);
//                    new Listcell(serviceinfo.getPromotioncode()).setParent(listitemtx_log);

                    /*                    
                    <listheader label="服務代碼(serviceID)"  sort="auto"/>
                    <listheader label="服務名稱(serviceName)"  sort="auto"/>
                    <listheader label="服務說明(Note)"  sort="auto"/>
                    <listheader label="價格(Price)" sort="auto"/>
                    <listheader label="Operator Name" sort="auto"/>
                    <listheader label="DTOne PriceId" sort="auto"/>
                    <listheader label="PP Promotioncode" sort="auto"/>
                     */
                    listitemtx_log.setParent(listbox_serviceinfo_log);
                }
            } else {
                try {
                    Messagebox.show("查詢無任何結果!!", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
                } catch (Exception ex) {
                    log.info(ex);
                }
            }
        } catch (Exception ex) {
            log.info(ex);
        }
    }
}
