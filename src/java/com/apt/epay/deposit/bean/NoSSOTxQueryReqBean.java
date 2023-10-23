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
public class NoSSOTxQueryReqBean {

    private String mdn;
    private String storeid;
    private String storename;
    private String apisrcid;
    private String salesid;
    private String stardatetime;
    private String enddatatime;

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
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

    public String getStardatetime() {
        return stardatetime;
    }

    public void setStardatetime(String stardatetime) {
        this.stardatetime = stardatetime;
    }

    public String getEnddatatime() {
        return enddatatime;
    }

    public void setEnddatatime(String enddatatime) {
        this.enddatatime = enddatatime;
    }

}
