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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class Change_password_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    public void onCreate() {

        Textbox text_user_code = (Textbox) getSpaceOwner().getFellow("text_user_code");
        String user_id = (String) Sessions.getCurrent().getAttribute("user_code");
        text_user_code.setValue(user_id);

    }

    public void onChangePwd() throws Exception {
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        Textbox old_password = (Textbox) getSpaceOwner().getFellow("old_password");
        Textbox new_1_password = (Textbox) getSpaceOwner().getFellow("new_1_password");
        Textbox new_2_password = (Textbox) getSpaceOwner().getFellow("new_2_password");
        String user_id = (String) Sessions.getCurrent().getAttribute("user_code");
        String str_old_password = old_password.getValue();
        String str_new_password = new_1_password.getValue();
        String new_password;//= "";
        String log_user_id = (String) Sessions.getCurrent().getAttribute("user_code");

        if (new_1_password.getValue().equals(new_2_password.getValue())) {
            EPAY_COMMON_USER user;// = new EPAY_COMMON_USER();
            user = epaybusinesscontroller.getCommonUser_ByCode(user_id);
            if (user != null && AESUtil.Decrypt(user.getCuser_password()).equals(str_old_password)) {

                //檢查密碼強度
                Utilities util = new Utilities();
                String result = util.checkPassword(str_new_password);
                log.info("checkPassword Result ===>" + result);
                if ("強".equals(result)) {
                    new_password = AESUtil.Encrypt(str_new_password);
                    user.setCuser_password(new_password);
                    if (epaybusinesscontroller.updateUser(user)) {
                        Messagebox.show("變更密碼成功", "亞太電信", Messagebox.OK, Messagebox.ERROR);
                        log.info("IP:" + Executions.getCurrent().getRemoteAddr() + " account: " + log_user_id + " Change Password Success");
                        Executions.sendRedirect("login.zul");
                        Sessions.getCurrent().invalidate();
                    }
                } else {
                    log.info("IP:" + Executions.getCurrent().getRemoteAddr() + " account: " + log_user_id + " Change Password Failed");
                    log.info("new_password: " + str_new_password  + " is Weak");
                    Messagebox.show("新密碼強度不足，請重新設定", "亞太電信", Messagebox.OK, Messagebox.ERROR);
                }

            } else {
                log.info("IP:" + Executions.getCurrent().getRemoteAddr() + " account: " + log_user_id + " Change Password Failed");
                Messagebox.show("輸入的舊密碼與系統不符", "亞太電信", Messagebox.OK, Messagebox.ERROR);
            }
        } else {
            log.info("IP:" + Executions.getCurrent().getRemoteAddr() + " account: " + log_user_id + " Change Password Failed");
            Messagebox.show("輸入的新密碼與第二次不一致", "亞太電信", Messagebox.OK, Messagebox.ERROR);
        }
    }

}
