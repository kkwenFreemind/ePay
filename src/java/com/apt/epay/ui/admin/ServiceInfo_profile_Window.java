/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
//import com.apt.util.AESUtil;
//import com.epay.ejb.bean.EPAY_COMMON_USER;
import com.epay.ejb.bean.EPAY_SERVICE_INFO;
//import com.epay.ejb.bean.EPAY_SYS_ROLES;
//import java.util.Iterator;
//import java.util.List;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
//import org.zkoss.zk.ui.Sessions;
//import org.zkoss.zul.Button;
//import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class ServiceInfo_profile_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");
    private String sid = "";

    public void onCreate() {
        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox text_serviceid = (Textbox) getSpaceOwner().getFellow("text_serviceid");
        Textbox text_servicename = (Textbox) getSpaceOwner().getFellow("text_servicename");
        Textbox text_glcode = (Textbox) getSpaceOwner().getFellow("text_glcode");
        Textbox text_price = (Textbox) getSpaceOwner().getFellow("text_price");
//        Textbox text_status = (Textbox) getSpaceOwner().getFellow("text_status");
        Textbox text_day = (Textbox) getSpaceOwner().getFellow("text_day");
        Textbox text_note = (Textbox) getSpaceOwner().getFellow("text_note");
//        Textbox text_flag = (Textbox) getSpaceOwner().getFellow("text_flag");
//        Radiogroup grp_status = (Radiogroup) getSpaceOwner().getFellow("grp_status");
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
//        text_status.setValue("");
        text_day.setValue("");
        text_note.setValue("");
        text_cardtype.setValue("");
//        text_flag.setValue("");
        text_platformtype.setValue("");
        text_priceplancode.setValue("");
        text_promotioncode.setValue("");
        text_ocsstatus.setValue("");
        text_cmsstatus.setValue("");

        String serviceid = "";
        serviceid = (String) Executions.getCurrent().getAttribute("serviceid");
        sid = serviceid;

        EPAY_SERVICE_INFO serviceinfo = new EPAY_SERVICE_INFO();
        try {
            serviceinfo = epaybusinesscontroller.getServiceInfoById_NoFlag(Long.valueOf(serviceid), Integer.valueOf(new ShareParm().PARM_EPAY_CPID));
            text_serviceid.setValue(String.valueOf(serviceinfo.getServiceId()));
            text_servicename.setValue(serviceinfo.getServiceName());
            text_glcode.setValue(serviceinfo.getGlcode());
            text_price.setValue(String.valueOf(serviceinfo.getPrice()));
            //text_status.setValue(serviceinfo.getStatus());
            text_day.setValue(String.valueOf(serviceinfo.getDday()));
            text_note.setValue(serviceinfo.getNote());
            text_cardtype.setValue(serviceinfo.getCardtype());
            //text_flag.setValue(String.valueOf(serviceinfo.getFlag()));
            text_platformtype.setValue(String.valueOf(serviceinfo.getPlatformtype()));
            text_priceplancode.setValue(serviceinfo.getPriceplancode());
            text_promotioncode.setValue(serviceinfo.getPromotioncode());
            text_ocsstatus.setValue(serviceinfo.getOcsstatus());
            text_cmsstatus.setValue(serviceinfo.getCmsstatus());
            
            int flag = serviceinfo.getFlag();
            
            if(flag ==1 ){
                grp_flag.setSelectedIndex(0);
            }else{
                grp_flag.setSelectedIndex(1);
            }

//            for (int i = 0; i <= ShareParm.TYPE_DepositType.length - 1; i++) {
//                Radio role_radio = new Radio();
//                role_radio.setLabel(ShareParm.TYPE_DepositType[i][0]);
//                role_radio.setValue(ShareParm.TYPE_DepositType[i][1]);
//                role_radio.setParent(grp_status);
//                if (serviceinfo.getStatus().equals(ShareParm.TYPE_DepositType[i][1])) {
//                    role_radio.setSelected(true);
//                }
//            }
//            grp_status.setSelectedIndex(0);

//            for (int i = 0; i <= ShareParm.STATUS_ServiceID.length - 1; i++) {
//                Radio role_radio = new Radio();
//                role_radio.setLabel(ShareParm.STATUS_ServiceID[i][0]);
//                role_radio.setValue(ShareParm.STATUS_ServiceID[i][1]);
//                role_radio.setParent(grp_flag);
//                
//   
//                if (serviceinfo.getFlag() == Integer.valueOf(ShareParm.STATUS_ServiceID[i][1])) {
//                    role_radio.setSelected(true);
//                }
//            }
//            grp_flag.setSelectedIndex(0);        

        } catch (Exception ex) {
            log.info(ex);
        }
    }

    public void onModify() throws Exception {
        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox text_serviceid = (Textbox) getSpaceOwner().getFellow("text_serviceid");
        Textbox text_servicename = (Textbox) getSpaceOwner().getFellow("text_servicename");
        Textbox text_glcode = (Textbox) getSpaceOwner().getFellow("text_glcode");
        Textbox text_price = (Textbox) getSpaceOwner().getFellow("text_price");
//        Textbox text_status = (Textbox) getSpaceOwner().getFellow("text_status");
        Textbox text_day = (Textbox) getSpaceOwner().getFellow("text_day");
        Textbox text_note = (Textbox) getSpaceOwner().getFellow("text_note");
//        Textbox text_flag = (Textbox) getSpaceOwner().getFellow("text_flag");
//        Radiogroup grp_status = (Radiogroup) getSpaceOwner().getFellow("grp_status");
        Radiogroup grp_flag = (Radiogroup) getSpaceOwner().getFellow("grp_flag");
        Textbox text_cardtype = (Textbox) getSpaceOwner().getFellow("text_cardtype");
        Textbox text_platformtype = (Textbox) getSpaceOwner().getFellow("text_platformtype");
        Textbox text_priceplancode = (Textbox) getSpaceOwner().getFellow("text_priceplancode");
        Textbox text_promotioncode = (Textbox) getSpaceOwner().getFellow("text_promotioncode");
        Textbox text_ocsstatus = (Textbox) getSpaceOwner().getFellow("text_ocsstatus");
        Textbox text_cmsstatus = (Textbox) getSpaceOwner().getFellow("text_cmsstatus");

        String platformtype = text_platformtype.getValue();
        String priceplancode = text_priceplancode.getValue();
        String promotioncode = text_promotioncode.getValue();
        String ocsstatus = text_ocsstatus.getValue();
        String cmsstatus = text_cmsstatus.getValue();

        EPAY_SERVICE_INFO serviceinfo = new EPAY_SERVICE_INFO();
        try {
            serviceinfo = epaybusinesscontroller.getServiceInfoById_NoFlag(Long.valueOf(sid), Integer.valueOf(new ShareParm().PARM_EPAY_CPID));

            serviceinfo.setServiceId(Long.valueOf(text_serviceid.getValue()));
            serviceinfo.setServiceName(text_servicename.getValue());
            serviceinfo.setGlcode(text_glcode.getValue());
            serviceinfo.setPrice(Integer.valueOf(text_price.getValue()));
//            serviceinfo.setStatus((String) grp_status.getSelectedItem().getValue());
            serviceinfo.setDday(Integer.valueOf(text_day.getValue()));
            serviceinfo.setNote(text_note.getValue());
            serviceinfo.setFlag(Integer.valueOf((String) grp_flag.getSelectedItem().getValue()));
            serviceinfo.setCardtype(text_cardtype.getValue());
            serviceinfo.setPlatformtype(Integer.valueOf(platformtype));
            serviceinfo.setPriceplancode(priceplancode);
            serviceinfo.setPromotioncode(promotioncode);
            serviceinfo.setOcsstatus(ocsstatus);
            serviceinfo.setCmsstatus(cmsstatus);

            if (epaybusinesscontroller.updateServiceInfo(serviceinfo)) {

                log.info("serviceinfo==>" + serviceinfo.getServiceId() + ","
                        + serviceinfo.getServiceName() + ","
                        + serviceinfo.getPrice() + ","
                        + serviceinfo.getStatus() + ","
                        + serviceinfo.getDday() + ","
                        + serviceinfo.getNote() + ","
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
