/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.*;

/**
 *
 * @author kevinchang
 */
public class LayoutTree extends Tree implements AfterCompose {

    public LayoutTree() {
//        System.out.println("LayoutTree");
    }

    public void onSelect() {

        Treeitem item = getSelectedItem();
        if (item != null) {
            Include inc = (Include) getSpaceOwner().getFellow("xcontents");
            Sessions.getCurrent().setAttribute("xcontents", inc);
            inc.setSrc((String) item.getValue());

            //     Executions.sendRedirect("./login.do?action=login");
        }
    }

    public void afterCompose() {
//        final Execution exec = Executions.getCurrent();
//        String id = exec.getParameter("id");
//        Treeitem item = null;
//
//        if (id != null) {
//            try {
//                item = (Treeitem) getSpaceOwner().getFellow(id);
//            } catch (ComponentNotFoundException ex) { //ignore
//            }
//        }
//        if (item == null) {
//            System.out.println("item is null!!");
//            item = (Treeitem) getSpaceOwner().getFellow("f1_1");
//            return;
//        }
//
//        exec.setAttribute("contentSrc", (String) item.getValue());
//        //so index.zul know which page to load based on the id parameter
//
//        selectItem(item);
    }
}
