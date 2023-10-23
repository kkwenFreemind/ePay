/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import static com.apt.epay.share.ShareParm.OCS_bucketIDArray;
//import static com.apt.epay.share.ShareParm.OCS_bucketIDArray;
import com.epay.ejb.bean.EPAY_BUCKET;
import com.epay.ejb.bean.EPAY_SERVICE_INFO;
//import java.io.File;
//import java.math.BigDecimal;
//import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
//import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import javax.servlet.ServletContext;
//import org.zkoss.zk.ui.Sessions;

/**
 *
 * @author kevinchang
 */
public class Bucket_add_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");
    private static String cpid = new ShareParm().PARM_EPAY_CPID;
    private String[] ch_serviceid = new String[100];

    public void onClear() {

        Textbox text_serviceid = (Textbox) getSpaceOwner().getFellow("text_serviceid");
        Textbox text_servicename = (Textbox) getSpaceOwner().getFellow("text_servicename");
        Textbox text_glcode = (Textbox) getSpaceOwner().getFellow("text_glcode");
        Textbox text_price = (Textbox) getSpaceOwner().getFellow("text_price");
        Textbox text_day = (Textbox) getSpaceOwner().getFellow("text_day");
        Textbox text_note = (Textbox) getSpaceOwner().getFellow("text_note");
        Textbox text_ref = (Textbox) getSpaceOwner().getFellow("text_ref");
        Textbox text_status = (Textbox) getSpaceOwner().getFellow("text_status");

        text_serviceid.setValue("");
        text_servicename.setValue("");
        text_glcode.setValue("");
        text_price.setValue("");
        text_day.setValue("");
        text_note.setValue("");
        text_status.setValue("");
        text_ref.setValue("");
    }

    public void onCreate() {
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        Combobox combo_serviceid = (Combobox) getSpaceOwner().getFellow("combo_serviceid");
        Combobox combo_bucketid = (Combobox) getSpaceOwner().getFellow("combo_bucketid");
        Textbox text_amount = (Textbox) getSpaceOwner().getFellow("text_amount");
        Textbox text_note = (Textbox) getSpaceOwner().getFellow("text_note");
        Textbox text_ref = (Textbox) getSpaceOwner().getFellow("text_ref");
//        Textbox text_ServletPath = (Textbox) getSpaceOwner().getFellow("text_ServletPath");
//        //String path = getServletContext().getRealPath("/");
//        
//        File getPath = new File(".");
//        String gPath = getPath.getPath();
//        
//        //text_ServletPath.setValue(Sessions.getCurrent().getWebApp().getRealPath("/"));
//        text_ServletPath.setValue(gPath);

        // 資費代碼下拉清單 //
        try {
            List serviceinfo1 = epaybusinesscontroller.listServiceInfo();
            Iterator itserviceinfo1 = serviceinfo1.iterator();
            int j = 1;
            while (itserviceinfo1.hasNext()) {

                EPAY_SERVICE_INFO serid = (EPAY_SERVICE_INFO) itserviceinfo1.next();

                Comboitem coitem_serviceid = new Comboitem();
                coitem_serviceid.setId("combo" + serid.getServiceId());
                coitem_serviceid.setLabel(serid.getServiceName());
                combo_serviceid.appendChild(coitem_serviceid);
                ch_serviceid[j] = String.valueOf(serid.getServiceId());

                j++;
            }
            combo_serviceid.setSelectedIndex(1);
        } catch (Exception ex) {

        }
        
        // BucketID 下拉清單 //
        try {
            for( int i = 0; i <= ShareParm.OCS_bucketIDArray.length - 1; i++){
                Comboitem ci_bucketID = new Comboitem();
                //ci_bucketID.setId( String.format("BucketID %s：%s",(i+""),(String)OCS_bucketIDArray[i]));
                ci_bucketID.setLabel((String)OCS_bucketIDArray[i][0]+"/"+(String)OCS_bucketIDArray[i][1]);
                ci_bucketID.setValue((String)OCS_bucketIDArray[i][1]);
                combo_bucketid.appendChild(ci_bucketID);
            }
            combo_bucketid.setSelectedIndex(1);
        } catch (Exception ex) {
        }
    }

    public void Add() throws Exception {

        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Combobox combo_serviceid = (Combobox) getSpaceOwner().getFellow("combo_serviceid");
        Combobox combo_bucketid = (Combobox) getSpaceOwner().getFellow("combo_bucketid");
        Textbox text_amount = (Textbox) getSpaceOwner().getFellow("text_amount");
        Textbox text_note = (Textbox) getSpaceOwner().getFellow("text_note");
        Textbox text_ref = (Textbox) getSpaceOwner().getFellow("text_ref");

        String serviceid = ch_serviceid[combo_serviceid.getSelectedIndex()];
        String bucketid = combo_bucketid.getSelectedItem().getValue();
        
        String tmp_amount = text_amount.getValue();
        String amount = ShareBean.getAmountForBucketID(bucketid, tmp_amount);
        String note = text_note.getValue();
        String ref = text_ref.getValue();

        EPAY_BUCKET bucket = new EPAY_BUCKET();
        bucket.setServiceId(Long.valueOf(serviceid));
        bucket.setBucketId(bucketid);
        bucket.setAmount(Long.valueOf(amount));
        bucket.setNote(note);
        bucket.setRef(ref);

        if (epaybusinesscontroller.addBucket(bucket)) {
            Messagebox.show("新增帳本成功", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
        } else {
            Messagebox.show("新增帳本失敗", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
        }

    }

//    private String getAmountForBucketID(String bucketid, String tmp_amount) {
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
