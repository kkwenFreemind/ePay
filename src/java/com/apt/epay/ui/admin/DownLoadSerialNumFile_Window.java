/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Window;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.util.AESUtil;
import com.epay.ejb.bean.EPAY_COMMON_USER;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Captcha;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class DownLoadSerialNumFile_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    public void onCreate() {
        //EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());

        Textbox text_cardname = (Textbox) getSpaceOwner().getFellow("text_cardname");
        Textbox text_price = (Textbox) getSpaceOwner().getFellow("text_price");
        Textbox text_quantity = (Textbox) getSpaceOwner().getFellow("text_quantity");


        text_cardname.setValue("");
        text_price.setValue("");
        text_quantity.setValue("");

    }
    
    public void onDownload(){
        
    }
}
