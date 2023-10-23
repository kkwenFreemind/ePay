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
import com.epay.ejb.bean.EPAY_SERVICE_INFO;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class ServiceInfo_add_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");
    private static String cpid = new ShareParm().PARM_EPAY_CPID;

    public void onClear() {

        Textbox text_serviceid = (Textbox) getSpaceOwner().getFellow("text_serviceid");
        Textbox text_servicename = (Textbox) getSpaceOwner().getFellow("text_servicename");
        Textbox text_glcode = (Textbox) getSpaceOwner().getFellow("text_glcode");
        Textbox text_price = (Textbox) getSpaceOwner().getFellow("text_price");
        Textbox text_day = (Textbox) getSpaceOwner().getFellow("text_day");
        Textbox text_note = (Textbox) getSpaceOwner().getFellow("text_note");
//        Textbox text_status = (Textbox) getSpaceOwner().getFellow("text_status");
//        Textbox text_flag = (Textbox) getSpaceOwner().getFellow("text_flag");
        Radiogroup grp_status = (Radiogroup) getSpaceOwner().getFellow("grp_status");
        Radiogroup grp_flag = (Radiogroup) getSpaceOwner().getFellow("grp_flag");
        Textbox text_cardtype = (Textbox) getSpaceOwner().getFellow("text_cardtype");
        Textbox text_platformtype = (Textbox) getSpaceOwner().getFellow("text_platformtype");
        Textbox text_priceplancode = (Textbox) getSpaceOwner().getFellow("text_priceplancode");
        Textbox text_promotioncode = (Textbox) getSpaceOwner().getFellow("text_promotioncode");
        Textbox text_ocsstatus = (Textbox) getSpaceOwner().getFellow("text_ocsstatus");
        Textbox text_cmsstatus = (Textbox) getSpaceOwner().getFellow("text_cmsstatus");

        text_serviceid.setValue("");
        text_servicename.setValue("");
        text_glcode.setValue("");
        text_price.setValue("");
        text_day.setValue("");
        text_note.setValue("");
//        text_status.setValue("");
//        text_flag.setValue("");
        grp_status.setSelectedIndex(-1);
        grp_flag.setSelectedIndex(-1);
        text_cardtype.setValue("");
        text_platformtype.setValue("");
        text_priceplancode.setValue("");
        text_promotioncode.setValue("");
        text_ocsstatus.setValue("");
        text_cmsstatus.setValue("");

    }

    public void onCreate() {
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        Textbox text_serviceid = (Textbox) getSpaceOwner().getFellow("text_serviceid");
        Textbox text_servicename = (Textbox) getSpaceOwner().getFellow("text_servicename");
        Textbox text_glcode = (Textbox) getSpaceOwner().getFellow("text_glcode");
        Textbox text_price = (Textbox) getSpaceOwner().getFellow("text_price");
        Textbox text_day = (Textbox) getSpaceOwner().getFellow("text_day");
        Textbox text_note = (Textbox) getSpaceOwner().getFellow("text_note");
        Textbox text_cardtype = (Textbox) getSpaceOwner().getFellow("text_cardtype");
        Textbox text_platformtype = (Textbox) getSpaceOwner().getFellow("text_platformtype");
        Textbox text_priceplancode = (Textbox) getSpaceOwner().getFellow("text_priceplancode");
        Textbox text_promotioncode = (Textbox) getSpaceOwner().getFellow("text_promotioncode");
        Textbox text_ocsstatus = (Textbox) getSpaceOwner().getFellow("text_ocsstatus");
        Textbox text_cmsstatus = (Textbox) getSpaceOwner().getFellow("text_cmsstatus");
//        Textbox text_status = (Textbox) getSpaceOwner().getFellow("text_status");
//        Textbox text_flag = (Textbox) getSpaceOwner().getFellow("text_flag");

        text_day.setValue(new ShareParm().OCS_SERVICE_DAY);
        Radiogroup grp_status = (Radiogroup) getSpaceOwner().getFellow("grp_status");
        for (int i = 0; i <= ShareParm.TYPE_DepositType.length - 1; i++) {
            Radio role_radio = new Radio();
//            role_radio.setId(ShareParm.TYPE_DepositType[i][1]);
            role_radio.setLabel(ShareParm.TYPE_DepositType[i][0]);
            role_radio.setValue(ShareParm.TYPE_DepositType[i][1]);
            role_radio.setParent(grp_status);
        }
        grp_status.setSelectedIndex(0);

        Radiogroup grp_flag = (Radiogroup) getSpaceOwner().getFellow("grp_flag");
        for (int i = 0; i <= ShareParm.STATUS_ServiceID.length - 1; i++) {
            Radio role_radio = new Radio();
//            role_radio.setId(ShareParm.STATUS_serviceIDArray[i][1]);
            role_radio.setLabel(ShareParm.STATUS_ServiceID[i][0]);
            role_radio.setValue(ShareParm.STATUS_ServiceID[i][1]);
            role_radio.setParent(grp_flag);
        }
        grp_flag.setSelectedIndex(0);
    }

    public void Add() throws Exception {

        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox text_serviceid = (Textbox) getSpaceOwner().getFellow("text_serviceid");
        Textbox text_servicename = (Textbox) getSpaceOwner().getFellow("text_servicename");
        Textbox text_glcode = (Textbox) getSpaceOwner().getFellow("text_glcode");
        Textbox text_price = (Textbox) getSpaceOwner().getFellow("text_price");
        Textbox text_day = (Textbox) getSpaceOwner().getFellow("text_day");
        Textbox text_note = (Textbox) getSpaceOwner().getFellow("text_note");
//        Textbox text_status = (Textbox) getSpaceOwner().getFellow("text_status");
//        Textbox text_flag = (Textbox) getSpaceOwner().getFellow("text_flag");
        Radiogroup grp_status = (Radiogroup) getSpaceOwner().getFellow("grp_status");
        Radiogroup grp_flag = (Radiogroup) getSpaceOwner().getFellow("grp_flag");
        Textbox text_cardtype = (Textbox) getSpaceOwner().getFellow("text_cardtype");
        Textbox text_platformtype = (Textbox) getSpaceOwner().getFellow("text_platformtype");
        Textbox text_priceplancode = (Textbox) getSpaceOwner().getFellow("text_priceplancode");
        Textbox text_promotioncode = (Textbox) getSpaceOwner().getFellow("text_promotioncode");
        Textbox text_ocsstatus = (Textbox) getSpaceOwner().getFellow("text_ocsstatus");
        Textbox text_cmsstatus = (Textbox) getSpaceOwner().getFellow("text_cmsstatus");

        String serviceid = text_serviceid.getValue();
        String servicename = text_servicename.getValue();
        String glcode = text_glcode.getValue();
        String price = text_price.getValue();
        String dday = text_day.getValue();
        String note = text_note.getValue();
        String cardtype = text_cardtype.getValue();
        String platformtype = text_platformtype.getValue();
        String priceplancode = text_priceplancode.getValue();
        String promotioncode =text_promotioncode.getValue();
        String ocsstatus =text_ocsstatus.getValue();
        String cmsstatus = text_cmsstatus.getValue();
        
//        String sstatus = text_status.getValue();
//        String sflag = text_flag.getValue();
        String sstatus = grp_status.getSelectedItem().getValue();
        String sflag = grp_flag.getSelectedItem().getValue();

        EPAY_CALLER ec = new EPAY_CALLER();
        ec.setCallerid(new ShareParm().MERCHANT_CALLER_ID);
        EPAY_SERVICE_INFO serviceinfo = new EPAY_SERVICE_INFO();
        serviceinfo.setServiceId(Long.valueOf(serviceid));
        serviceinfo.setServiceName(servicename);
        serviceinfo.setGlcode(glcode);
        serviceinfo.setPrice(Integer.valueOf(price));
        serviceinfo.setDday(Integer.valueOf(dday));
        serviceinfo.setNote(note);
        serviceinfo.setCpId(Integer.valueOf(cpid));
        serviceinfo.setStatus(sstatus);
        serviceinfo.setFlag(Integer.valueOf(sflag));
        serviceinfo.setCaller(ec);
        serviceinfo.setCardtype(cardtype);
        serviceinfo.setPlatformtype(Integer.valueOf(platformtype));
        serviceinfo.setPriceplancode(priceplancode);
        serviceinfo.setPromotioncode(promotioncode);
        serviceinfo.setOcsstatus(ocsstatus);
        serviceinfo.setCmsstatus(cmsstatus);

        if (epaybusinesscontroller.addServiceInfo(serviceinfo)) {
            Messagebox.show("新增服務成功", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
        } else {
            Messagebox.show("新增服務失敗", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
        }

    }
}
