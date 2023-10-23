/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.epay.ejb.bean.EPAY_CALLER;
import com.epay.ejb.bean.EPAY_DTONESERVICE_INFO;
import javax.servlet.ServletContext;
import org.zkoss.zul.Window;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

/**
 *
 * @author kevinchang
 */
public class DTOne_ServiceInfo_add_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");
    private static String cpid = new ShareParm().PARM_EPAY_CPID;

    public void onClear() {
        Textbox text_serviceid = (Textbox) getSpaceOwner().getFellow("text_serviceid");
        Textbox text_servicename = (Textbox) getSpaceOwner().getFellow("text_servicename");
        Textbox text_price = (Textbox) getSpaceOwner().getFellow("text_price");
        Textbox text_note = (Textbox) getSpaceOwner().getFellow("text_note");
        Textbox text_operator_name = (Textbox) getSpaceOwner().getFellow("text_operator_name");
        Textbox text_priceId = (Textbox) getSpaceOwner().getFellow("text_priceId");
        Textbox text_promotioncode = (Textbox) getSpaceOwner().getFellow("text_promotioncode");

        text_serviceid.setValue("");
        text_servicename.setValue("");
        text_price.setValue("");
        text_note.setValue("");
        text_operator_name.setValue("");
        text_priceId.setValue("");
        //text_promotioncode.setValue("");

    }

    public void onCreate() {
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        Textbox text_serviceid = (Textbox) getSpaceOwner().getFellow("text_serviceid");
        Textbox text_servicename = (Textbox) getSpaceOwner().getFellow("text_servicename");
        Textbox text_price = (Textbox) getSpaceOwner().getFellow("text_price");
        Textbox text_note = (Textbox) getSpaceOwner().getFellow("text_note");
        Textbox text_operator_name = (Textbox) getSpaceOwner().getFellow("text_operator_name");
        Textbox text_priceId = (Textbox) getSpaceOwner().getFellow("text_priceId");
        Textbox text_promotioncode = (Textbox) getSpaceOwner().getFellow("text_promotioncode");

        text_serviceid.setValue("");
        text_servicename.setValue("");
        text_price.setValue("");
        text_note.setValue("");
        text_operator_name.setValue("");
        text_priceId.setValue("");
        //text_promotioncode.setValue("");
    }

    public void Add() throws Exception {
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        Textbox text_serviceid = (Textbox) getSpaceOwner().getFellow("text_serviceid");
        Textbox text_servicename = (Textbox) getSpaceOwner().getFellow("text_servicename");
        Textbox text_price = (Textbox) getSpaceOwner().getFellow("text_price");
        Textbox text_note = (Textbox) getSpaceOwner().getFellow("text_note");
        Textbox text_operator_name = (Textbox) getSpaceOwner().getFellow("text_operator_name");
        Textbox text_priceId = (Textbox) getSpaceOwner().getFellow("text_priceId");
        Textbox text_promotioncode = (Textbox) getSpaceOwner().getFellow("text_promotioncode");

        String serviceid = text_serviceid.getValue();
        String servicename = text_servicename.getValue();
        String price = text_price.getValue();
        String note = text_note.getValue();
        String operator_name = text_operator_name.getValue();
        String priceId = text_priceId.getValue();
        String promotioncode = text_promotioncode.getValue();

        EPAY_CALLER ec = new EPAY_CALLER();
        ec.setCallerid(new ShareParm().MERCHANT_CALLER_ID);

        EPAY_DTONESERVICE_INFO serviceinfo = new EPAY_DTONESERVICE_INFO();

        serviceinfo.setServiceId(Long.valueOf(serviceid));
        serviceinfo.setServiceName(servicename);
        serviceinfo.setGlcode("家鄉儲");
        serviceinfo.setPrice(Integer.valueOf(price));
        serviceinfo.setNote(note);
        serviceinfo.setCpId(Integer.valueOf(cpid));
        serviceinfo.setStatus("1");
        serviceinfo.setFlag(1);
        serviceinfo.setCaller(ec);
        serviceinfo.setPlatformtype(2);
        serviceinfo.setCmsstatus("/9");
        serviceinfo.setOcsstatus("/G/Active/E/");
        serviceinfo.setPromotioncode(promotioncode);
        serviceinfo.setOperator_name(operator_name);
        serviceinfo.setPrice_id(Integer.valueOf(priceId));
        
        if (epaybusinesscontroller.addDTOneServiceInfo(serviceinfo)) {
            Messagebox.show("新增服務成功", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
        } else {
            Messagebox.show("新增服務失敗", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
        }
    }
}
