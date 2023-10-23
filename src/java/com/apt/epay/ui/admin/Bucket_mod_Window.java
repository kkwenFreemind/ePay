/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.epay.ejb.bean.EPAY_BUCKET;
//import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Include;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class Bucket_mod_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    public void onCreate() {

        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        Listbox listbox_bucketinfo_log = (Listbox) getSpaceOwner().getFellow("listbox_bucketinfo_log");

        List list_bucketinfo = null;
        Iterator itserv = null;
        try {
            list_bucketinfo = epaybusinesscontroller.ListAllBucketInfo();
            if (list_bucketinfo != null && !list_bucketinfo.isEmpty()) {
                EPAY_BUCKET bucketinfo = new EPAY_BUCKET();
                itserv = list_bucketinfo.iterator();
                while (itserv.hasNext()) {
                    bucketinfo = (EPAY_BUCKET) itserv.next();
                    Listitem useritem = new Listitem();
                    useritem.setId(String.valueOf(bucketinfo.getId()));

                    new Listcell(String.valueOf(bucketinfo.getServiceId())).setParent(useritem);
                    new Listcell(bucketinfo.getBucketId()).setParent(useritem);

                    String bucketid = bucketinfo.getBucketId();
                    String tmp_amount = String.valueOf(bucketinfo.getAmount());
                    String amount = ShareBean.getAmountForBucketID(bucketid, tmp_amount);
                    new Listcell(bucketinfo.getRef()).setParent(useritem);
                    new Listcell(String.valueOf(amount)).setParent(useritem);
                    new Listcell(bucketinfo.getNote()).setParent(useritem);

                    useritem.setParent(listbox_bucketinfo_log);
                }
            }
        } catch (Exception ex) {
            log.info(ex);
        }

    }

    public void onClear() {

    }

    public void onQuery() {

    }

    public void onSelect() {

        this.detach();
        Map model = new HashMap();
        Listbox listbox_bucketinfo_log = (Listbox) getSpaceOwner().getFellow("listbox_bucketinfo_log");

        String rid = listbox_bucketinfo_log.getSelectedItem().getId();

        Map map_parms = new HashMap();

        map_parms.put("rid", rid);
        Executions.getCurrent().setAttribute("map_parms", map_parms);
//        Executions.getCurrent().setAttribute("url", "user_limit_mod.zul");
        Executions.getCurrent().setAttribute("rid", rid);

        
        Window contactWnd = (Window) Executions.createComponents("bucket_profile.zul", null, model);
//        Executions.getCurrent().sendRedirect("serviceinfo_profile.zul");
//        Include inc = (Include) getSpaceOwner().getFellow("xcontents");
        Include inc = (Include) Sessions.getCurrent().getAttribute("xcontents");
        inc.appendChild(contactWnd);

    }

//    private String ShowAmountForBucketID(String bucketid, String tmp_amount) {
//        String result = tmp_amount;
//        if (bucketid.equals("610") || bucketid.equals("620")) {
//            result = String.valueOf((int) Math.round(Float.valueOf(tmp_amount) * 10000));
//        }
//
//        if (bucketid.equals("720") || bucketid.equals("730")) {
//            double ft = Double.valueOf(tmp_amount) * 1048576;
//            result = new DecimalFormat("0").format(ft);
//        }
//
//        if (bucketid.equals("810")) {
//            result = String.valueOf((int) Math.round(Float.valueOf(tmp_amount) / 0.033));
//        }
//        
//        log.info("Add Bucket===>" + bucketid + ",amount" + result);
//        return result;
//    }

}
