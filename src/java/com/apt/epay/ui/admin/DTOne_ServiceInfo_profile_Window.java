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
import com.epay.ejb.bean.EPAY_SERVICE_INFO;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class DTOne_ServiceInfo_profile_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");
    private String sid = "";

    public void onCreate() {
        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox text_serviceid = (Textbox) getSpaceOwner().getFellow("text_serviceid");
        Textbox text_servicename = (Textbox) getSpaceOwner().getFellow("text_servicename");
        Textbox text_price = (Textbox) getSpaceOwner().getFellow("text_price");
        Textbox text_note = (Textbox) getSpaceOwner().getFellow("text_note");
        Textbox text_operator_name = (Textbox) getSpaceOwner().getFellow("text_operator_name");
        Textbox text_priceId = (Textbox) getSpaceOwner().getFellow("text_priceId");
        Textbox text_promotioncode = (Textbox) getSpaceOwner().getFellow("text_promotioncode");
        Radiogroup grp_flag = (Radiogroup) getSpaceOwner().getFellow("grp_flag");

        text_serviceid.setValue("");
        text_servicename.setValue("");
        text_price.setValue("");
        text_note.setValue("");
        text_operator_name.setValue("");
        text_priceId.setValue("");
        text_promotioncode.setValue("");

        String serviceid = "";
        serviceid = (String) Executions.getCurrent().getAttribute("serviceid");
        sid = serviceid;
        log.info("serviceid==>" + serviceid);

        EPAY_DTONESERVICE_INFO serviceinfo;// = new EPAY_DTONESERVICE_INFO();
        try {
            serviceinfo = epaybusinesscontroller.getDtoneServiceInfoByIdOnly(Long.valueOf(serviceid), Integer.valueOf(new ShareParm().PARM_EPAY_CPID));

            int flag = serviceinfo.getFlag();

            if (flag == 1) {
                grp_flag.setSelectedIndex(0);
            } else {
                grp_flag.setSelectedIndex(1);
            }

            text_serviceid.setValue(String.valueOf(serviceinfo.getServiceId()));
            text_servicename.setValue(serviceinfo.getServiceName());
            text_price.setValue(String.valueOf(serviceinfo.getPrice()));
            text_note.setValue(serviceinfo.getNote());
            text_promotioncode.setValue(serviceinfo.getPromotioncode());
            text_operator_name.setValue(serviceinfo.getOperator_name());
            text_priceId.setValue(String.valueOf(serviceinfo.getPrice_id()));

        } catch (Exception ex) {
            log.info(ex);
        }
    }

    public void onModify() throws Exception {
        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox text_serviceid = (Textbox) getSpaceOwner().getFellow("text_serviceid");
        Textbox text_servicename = (Textbox) getSpaceOwner().getFellow("text_servicename");
        Textbox text_price = (Textbox) getSpaceOwner().getFellow("text_price");
        Textbox text_note = (Textbox) getSpaceOwner().getFellow("text_note");
        Textbox text_promotioncode = (Textbox) getSpaceOwner().getFellow("text_promotioncode");
        Textbox text_operator_name = (Textbox) getSpaceOwner().getFellow("text_operator_name");
        Textbox text_priceId = (Textbox) getSpaceOwner().getFellow("text_priceId");

        Radiogroup grp_flag = (Radiogroup) getSpaceOwner().getFellow("grp_flag");

        String promotioncode = text_promotioncode.getValue();

        EPAY_DTONESERVICE_INFO serviceinfo;// = new EPAY_DTONESERVICE_INFO();
        try {
            serviceinfo = epaybusinesscontroller.getDtoneServiceInfoByIdOnly(Long.valueOf(sid), Integer.valueOf(new ShareParm().PARM_EPAY_CPID));

            serviceinfo.setServiceId(Long.valueOf(text_serviceid.getValue()));
            serviceinfo.setServiceName(text_servicename.getValue());
            serviceinfo.setPrice(Integer.valueOf(text_price.getValue()));
            serviceinfo.setNote(text_note.getValue());
            serviceinfo.setPromotioncode(promotioncode);
            serviceinfo.setOperator_name(text_operator_name.getValue());
            serviceinfo.setPrice_id(Integer.valueOf(text_priceId.getValue()));
            serviceinfo.setFlag(Integer.valueOf((String) grp_flag.getSelectedItem().getValue()));

            if (epaybusinesscontroller.updateDTOneServiceInfo(serviceinfo)) {

                log.info("serviceinfo==>" + serviceinfo.getServiceId() + ","
                                + serviceinfo.getServiceName() + ","
                                + serviceinfo.getPrice() + ","
                                + serviceinfo.getFlag());

                Messagebox.show("修改服務資料成功", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
                this.detach();
                Executions.sendRedirect("/admin/main.zul");

            } else {
                Messagebox.show("修改服務資料失敗", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
                this.detach();
                Executions.sendRedirect("/admin/main.zul");
            }
        } catch (Exception ex) {
            log.info(ex);
        }

    }
}
