/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.deposit.bean;

/**
 *
 * @author kevinchang
 */
public class NOSSOReqBean {
    
    //mdn1, mdn2, cplibm, salesid, storeid, storename, apisrcid;
    private String mdn1;
    private String mdn2;
    private String cplibm;
    private String storeid;
    private String storename;
    private String apisrcid;
    private String salesid;

    public String getMdn1() {
        return mdn1;
    }

    public void setMdn1(String mdn1) {
        this.mdn1 = mdn1;
    }

    public String getMdn2() {
        return mdn2;
    }

    public void setMdn2(String mdn2) {
        this.mdn2 = mdn2;
    }

    public String getCplibm() {
        return cplibm;
    }

    public void setCplibm(String cplibm) {
        this.cplibm = cplibm;
    }

    public String getStoreid() {
        return storeid;
    }

    public void setStoreid(String storeid) {
        this.storeid = storeid;
    }

    public String getStorename() {
        return storename;
    }

    public void setStorename(String storename) {
        this.storename = storename;
    }

    public String getApisrcid() {
        return apisrcid;
    }

    public void setApisrcid(String apisrcid) {
        this.apisrcid = apisrcid;
    }

    public String getSalesid() {
        return salesid;
    }

    public void setSalesid(String salesid) {
        this.salesid = salesid;
    }
    
    
}
