/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.epay.ejb.bean.EPAY_SYS_FUNCTIONS;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;

/**
 *
 * @author kevinchang
 */
public class Main_Menu_Tree extends Treechildren {

    private static final Logger log = Logger.getLogger("EPAY");

    public void onCreate() {

        String auth = (String) Sessions.getCurrent().getAttribute("auth");
        String user_code = (String) Sessions.getCurrent().getAttribute("user_code");

        log.info("user_code ===>" + user_code);

        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        List R_list, S_list;
        Iterator R_it, S_it;
        int index = 0;
        ArrayList<String> rtmenu = new ArrayList<String>();
        String user_id = (String) Sessions.getCurrent().getAttribute("user_code");
//        log.info("Main_Menu===>" + user_id);

        if (user_id != null) {
            try {
                R_list = epaybusinesscontroller.getParentFunctionName_byID(user_id);
                S_list = epaybusinesscontroller.getFunction_byID(user_id);
                R_it = R_list.iterator();

                while (R_it.hasNext()) {
                    rtmenu.add((String) R_it.next());
                }
                while (rtmenu.size() > index) {
                    Treeitem L1_treeitem = new Treeitem();
                    String pfunname = rtmenu.get(index);
                    L1_treeitem.setLabel(pfunname);
//                    L1_treeitem.setStyle("\"background-color: #92a8d1\"");
                    Treechildren L1_treechild = new Treechildren();
                    S_it = S_list.iterator();
                    while (S_it.hasNext()) {
                        Treeitem L2_treeitem = new Treeitem();
                        EPAY_SYS_FUNCTIONS functions;// = new EPAY_SYS_FUNCTIONS();

                        functions = (EPAY_SYS_FUNCTIONS) S_it.next();

                        if (rtmenu.get(index).equals(functions.getP_name())) {
//                            L2_treeitem.setStyle("\"background-color: #92a8d1\"");
                            L2_treeitem.setLabel(functions.getF_name());
                            L2_treeitem.setId(functions.getF_id());
                            L2_treeitem.setValue(functions.getF_url());
                            L1_treechild.appendChild(L2_treeitem);
                            log.info(user_id + " Function Name==>" + functions.getF_name());
                        }
                    }
                    L1_treeitem.appendChild(L1_treechild);
                    this.appendChild(L1_treeitem);
                    index++;
                }

                Treeitem changePasswd_item = new Treeitem();
                changePasswd_item.setLabel("變更密碼");
                changePasswd_item.setValue("change_password.zul");
//                changePasswd_item.setStyle("\"background-color: #92a8d1;\"");
                this.appendChild(changePasswd_item);

                Treeitem logoutitem = new Treeitem();
                logoutitem.setLabel("登出");
                logoutitem.setValue("logout.zul");
//                logoutitem.setStyle("background-color: #92a8d1;");
                this.appendChild(logoutitem);

            } catch (Exception ex) {
                Executions.getCurrent().sendRedirect("../");
            }
        } else {
            Executions.getCurrent().sendRedirect("../");
        }
    }
}
