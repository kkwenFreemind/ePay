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
import com.epay.ejb.bean.EPAY_BUCKET;
import java.text.DecimalFormat;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class Bucket_profile_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");
    private String rid = "";

    public void onCreate() {
        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox text_serviceid = (Textbox) getSpaceOwner().getFellow("text_serviceid");
//        Textbox text_buckeid = (Textbox) getSpaceOwner().getFellow("text_buckeid");
        Combobox combo_bucketid = (Combobox) getSpaceOwner().getFellow("combo_bucketid");
        Textbox text_amount = (Textbox) getSpaceOwner().getFellow("text_amount");
        Textbox text_note = (Textbox) getSpaceOwner().getFellow("text_note");
        Textbox text_ref = (Textbox) getSpaceOwner().getFellow("text_ref");

        text_serviceid.setValue("");
//        text_buckeid.setValue("");
        text_amount.setValue("");
        text_note.setValue("");
        text_ref.setValue("");

        rid = (String) Executions.getCurrent().getAttribute("rid");

        EPAY_BUCKET bucketinfo = new EPAY_BUCKET();
        try {
            bucketinfo = epaybusinesscontroller.getBucketByRID(Integer.valueOf(rid));
            log.info("bucketinfo.getServiceId()==>" + bucketinfo.getServiceId());
            log.info("bucketinfo.getBucketId()==>" + bucketinfo.getBucketId());
            log.info("bucketinfo.getAmount()==>" + bucketinfo.getAmount());
            log.info("bucketinfo.getNote()==>" + bucketinfo.getNote());
            log.info("bucketinfo.getRef()==>" + bucketinfo.getRef());

            text_serviceid.setValue(String.valueOf(bucketinfo.getServiceId()));
//            text_buckeid.setValue(bucketinfo.getBucketId());

            String bucketid = bucketinfo.getBucketId();
            String tmp_amount = String.valueOf(bucketinfo.getAmount());
            String ref = bucketinfo.getRef();
            String amount = ShareBean.showAmountForBucketID(bucketid, tmp_amount);
            
            // BucketID 下拉清單 //
            try {
                for( int i = 0; i <= ShareParm.OCS_bucketIDArray.length - 1; i++){
                    Comboitem ci_bucketID = new Comboitem();
                    //ci_bucketID.setId( String.format("BucketID %s：%s",(i+""),(String)OCS_bucketIDArray[i]));
                    ci_bucketID.setLabel((String)OCS_bucketIDArray[i][0]+"/"+(String)OCS_bucketIDArray[i][1]);
                    ci_bucketID.setValue((String)OCS_bucketIDArray[i][1]);
                    combo_bucketid.appendChild(ci_bucketID);
                    if( ((String)OCS_bucketIDArray[i][1]).equals(bucketid) )
                        combo_bucketid.setSelectedIndex(i+1);
                }
            } catch (Exception ex) {
            }

            text_amount.setValue(String.valueOf(amount));
            text_note.setValue(bucketinfo.getNote());
            text_ref.setValue(ref);

        } catch (Exception ex) {
            log.info(ex);
        }
    }

    public void onModify() throws Exception {
        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        Textbox text_serviceid = (Textbox) getSpaceOwner().getFellow("text_serviceid");
//        Textbox text_buckeid = (Textbox) getSpaceOwner().getFellow("text_buckeid");
        Combobox combo_bucketid = (Combobox) getSpaceOwner().getFellow("combo_bucketid");
        Textbox text_amount = (Textbox) getSpaceOwner().getFellow("text_amount");
        Textbox text_note = (Textbox) getSpaceOwner().getFellow("text_note");
        Textbox text_ref = (Textbox) getSpaceOwner().getFellow("text_ref");

        EPAY_BUCKET bucketinfo = new EPAY_BUCKET();

        try {
            bucketinfo = epaybusinesscontroller.getBucketByRID(Integer.valueOf(rid));

            String bucketid = combo_bucketid.getSelectedItem().getValue();
            String tmp_amount = text_amount.getValue();
            String amount = ShareBean.getAmountForBucketID(bucketid, tmp_amount);
            String ref = text_amount.getValue();
            
//            Messagebox.show(String.format("帳本資料 bucketID=%s,tmp_amount=%s,amount=%s",bucketid,tmp_amount,amount));
            
            log.info("rid==>" + rid);
            log.info("text_serviceid.getValue()==>" + text_serviceid.getValue());
            log.info("text_buckeid.getValue()==>" + bucketid);
            log.info("text_amount.getValue()==>" + tmp_amount);
            log.info("text_note.getValue()==>" + text_note.getValue());
            log.info("text_ref.getValue()==>" + text_ref.getValue());

            bucketinfo.setId(Integer.valueOf(rid));
            bucketinfo.setServiceId(Long.valueOf(text_serviceid.getValue()));
//            bucketinfo.setBucketId(text_buckeid.getValue());
            bucketinfo.setBucketId(bucketid);
            bucketinfo.setAmount(Long.valueOf(amount));
            bucketinfo.setNote(text_note.getValue());
            bucketinfo.setRef(text_ref.getValue());

            if (epaybusinesscontroller.updateBucketInfo(bucketinfo)) {

                log.info("serviceinfo==>" + bucketinfo.getServiceId() + ","
                        + bucketinfo.getBucketId() + ","
                        + bucketinfo.getAmount() + ","
                        + bucketinfo.getNote());
                Messagebox.show("修改帳本資料成功", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
                this.detach();
                Executions.sendRedirect("/admin/main.zul");

            } else {
                Messagebox.show("修改帳本資料失敗", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
                this.detach();
                Executions.sendRedirect("/admin/main.zul");
            }
        } catch (Exception ex) {
            log.info(ex);
        }

    }

    private String getAmountForBucketID(String bucketid, String tmp_amount) {
        String result = "";
        if (bucketid.equals("610") || bucketid.equals("620")) {
            result = String.valueOf((int) Math.round(Float.valueOf(tmp_amount) * 10000));
        }

        if (bucketid.equals("720") || bucketid.equals("730")) {
            double ft = Double.valueOf(tmp_amount) * 1048576;
            result = new DecimalFormat("0").format(ft);
        }

        if (bucketid.equals("810")) {
            result = String.valueOf((int) Math.round(Float.valueOf(tmp_amount) / 0.033));
        }
        log.info("Modify Bucket===>" + bucketid + ",amount" + result);
        return result;
    }

// remark by Dennis Chung,
//    private String ShowAmountForBucketID(String bucketid, String tmp_amount) {
//        String result = "";
//        if (bucketid.equals("610") || bucketid.equals("620")) {
//            result = String.valueOf((int) Math.round(Float.valueOf(tmp_amount) / 10000));
//        }
//
//        if (bucketid.equals("720") || bucketid.equals("730")) {
//            double ft = Double.valueOf(tmp_amount) / 1048576;
//            result = new DecimalFormat("0").format(ft);
//        }
//
//        if (bucketid.equals("810")) {
//            result = String.valueOf((int) Math.round(Float.valueOf(tmp_amount) * 0.033));
//        }
//        log.info("Show Bucket===>" + bucketid + ",amount" + result);
//        return result;
//    }

}
