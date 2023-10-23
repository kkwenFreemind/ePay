/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.zte.bean;

/**
 *
 * @author kevinchang
 */
public class VoucherInfoResultBean {

    private String libm;
    private String returncode;
    private String desc;
    private String cardtype;
    private String cardvalue;
    private String adddays;
    private String expiredate;
    private String vouchercardno;
    private String status;
    private String lockstate;
    private String pin;

    public String getLibm() {
        return libm;
    }

    public void setLibm(String libm) {
        this.libm = libm;
    }

    public String getReturncode() {
        return returncode;
    }

    public void setReturncode(String returncode) {
        this.returncode = returncode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCardtype() {
        return cardtype;
    }

    public void setCardtype(String cardtype) {
        this.cardtype = cardtype;
    }

    public String getCardvalue() {
        return cardvalue;
    }

    public void setCardvalue(String cardvalue) {
        this.cardvalue = cardvalue;
    }

    public String getAdddays() {
        return adddays;
    }

    public void setAdddays(String adddays) {
        this.adddays = adddays;
    }

    public String getExpiredate() {
        return expiredate;
    }

    public void setExpiredate(String expiredate) {
        this.expiredate = expiredate;
    }

    public String getVouchercardno() {
        return vouchercardno;
    }

    public void setVouchercardno(String vouchercardno) {
        this.vouchercardno = vouchercardno;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLockstate() {
        return lockstate;
    }

    public void setLockstate(String lockstate) {
        this.lockstate = lockstate;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

}
