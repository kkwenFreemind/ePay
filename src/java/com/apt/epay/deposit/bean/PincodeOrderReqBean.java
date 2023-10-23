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
public class PincodeOrderReqBean {

    private String channelID;
    private String mdn;
    private String pincode;
    private String tokenId;
    private String cporderid;
    private String storeid;
    private String saleid;
    private String stroename;
    private String paytool;
    private String payname;
    private String apisrcid;
    private String cplibm;
    private String libm;
    private String cardno;

    public String getCardno() {
        return cardno;
    }

    public void setCardno(String cardno) {
        this.cardno = cardno;
    }
    
    

    public String getChannelID() {
        return channelID;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getCporderid() {
        return cporderid;
    }

    public void setCporderid(String cporderid) {
        this.cporderid = cporderid;
    }

    public String getStoreid() {
        return storeid;
    }

    public void setStoreid(String storeid) {
        this.storeid = storeid;
    }

    public String getSaleid() {
        return saleid;
    }

    public void setSaleid(String saleid) {
        this.saleid = saleid;
    }

    public String getStroename() {
        return stroename;
    }

    public void setStroename(String stroename) {
        this.stroename = stroename;
    }

    public String getPaytool() {
        return paytool;
    }

    public void setPaytool(String paytool) {
        this.paytool = paytool;
    }

    public String getPayname() {
        return payname;
    }

    public void setPayname(String payname) {
        this.payname = payname;
    }

    public String getApisrcid() {
        return apisrcid;
    }

    public void setApisrcid(String apisrcid) {
        this.apisrcid = apisrcid;
    }

    public String getCplibm() {
        return cplibm;
    }

    public void setCplibm(String cplibm) {
        this.cplibm = cplibm;
    }

    public String getLibm() {
        return libm;
    }

    public void setLibm(String libm) {
        this.libm = libm;
    }
    
    
}
