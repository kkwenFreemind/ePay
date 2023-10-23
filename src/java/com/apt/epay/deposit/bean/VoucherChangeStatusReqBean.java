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
public class VoucherChangeStatusReqBean {

    private String storeid;
    private String storename;
    private String startserialno;
    private String endserialno;

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

    public String getStartserialno() {
        return startserialno;
    }

    public void setStartserialno(String startserialno) {
        this.startserialno = startserialno;
    }

    public String getEndserialno() {
        return endserialno;
    }

    public void setEndserialno(String endserialno) {
        this.endserialno = endserialno;
    }

}
