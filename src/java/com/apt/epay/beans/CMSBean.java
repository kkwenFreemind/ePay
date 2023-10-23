/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.beans;

/**
 *
 * @author kevinchang
 */
public class CMSBean {
            // http://10.31.80.210:8080/EPAY/cms/CMS_OnlinePinCodeDeposit.zhtml?PKEY=11223344&APTCMS&MDN=0906044025&STORE_ID=70771235&SALES_ID=09600333&SRC_ID=1&
    //INVOICE_NO=A1234567890&TIMESTAMP=201508171700

    private String mdn;
    private String store_id;
    private String scr_id;
    private String inv_no;
    private String cmstime;
    private String saleid;
    private String transdatetime;
    private String oc_checkpoint;

    public String getTransdatetime() {
        return transdatetime;
    }

    public void setTransdatetime(String transdatetime) {
        this.transdatetime = transdatetime;
    }

    public String getSaleid() {
        return saleid;
    }

    public void setSaleid(String saleid) {
        this.saleid = saleid;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getScr_id() {
        return scr_id;
    }

    public void setScr_id(String scr_id) {
        this.scr_id = scr_id;
    }

    public String getInv_no() {
        return inv_no;
    }

    public void setInv_no(String inv_no) {
        this.inv_no = inv_no;
    }

    public String getCmstime() {
        return cmstime;
    }

    public void setCmstime(String cmstime) {
        this.cmstime = cmstime;
    }

}
