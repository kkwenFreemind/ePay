/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.apt.util.AESUtil;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_COMMON_USER;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
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
public class LoginWindow extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    public void onLogin() throws Exception {

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        Captcha chkcaptcha = (Captcha) getSpaceOwner().getFellow("cpa");
        Textbox chkcode = (Textbox) this.getFellow("chkcode");
        Textbox username_t = (Textbox) this.getFellow("username");
        Textbox password_t = (Textbox) this.getFellow("password");

        try {
            if (chkcode.getValue() != null && chkcode.getValue().equals(chkcaptcha.getValue())) {
                String user_code = username_t.getValue();
                String user_pwd = password_t.getValue();
                EPAY_COMMON_USER c_user;// = new EPAY_COMMON_USER();
                c_user = epaybusinesscontroller.getCommonUser_ByCode(user_code);

                if (c_user != null && AESUtil.Decrypt(c_user.getCuser_password()).equals(user_pwd)) {
                    Sessions.getCurrent().setAttribute("auth", "true");
                    Sessions.getCurrent().setAttribute("user_code", user_code);
                    Sessions.getCurrent().setAttribute("email", c_user.getCuser_email());

                    String log_user_id = (String) Sessions.getCurrent().getAttribute("user_code");
                    log.info("IP:" + Executions.getCurrent().getRemoteAddr() + " account: " + log_user_id + " login EPAY success!");

                    //檢查密碼強度
                    String str_new_password = user_pwd;
                    Utilities util = new Utilities();
                    String result = util.checkPassword(str_new_password);
                    log.info("checkPassword Result ===>" + result);

                    if (!"強".equals(result)) {
                        Messagebox.show("密碼強度不足，請變更密碼，謝謝", "亞太電信", Messagebox.OK, Messagebox.ERROR);
                        this.detach();
                        Executions.getCurrent().createComponents("change_password.zul", null, null);
                    } else {
                        this.detach();
                        Executions.getCurrent().createComponents("main.zul", null, null);
                    }
                } else {
                    Messagebox.show("請輸入正確的帳號密碼", "亞太電信", Messagebox.OK, Messagebox.ERROR);
                }
            } else {
                Messagebox.show("請輸入正確的檢核碼", "亞太電信", Messagebox.OK, Messagebox.ERROR);
                chkcaptcha.randomValue();
                chkcode.setValue("");
                chkcode.setFocus(true);
            }

        } catch (Exception ex) {
            log.info(ex);
        }

    }

}
