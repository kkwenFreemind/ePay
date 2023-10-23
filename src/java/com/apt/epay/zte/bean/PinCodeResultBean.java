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
public class PinCodeResultBean {

    private String libm;
    private String returncode;
    private String desc;
    private String userbalance;
    private String expiredate;
    private String chargemoney;

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

    public String getUserbalance() {
        return userbalance;
    }

    public void setUserbalance(String userbalance) {
        this.userbalance = userbalance;
    }

    public String getExpiredate() {
        return expiredate;
    }

    public void setExpiredate(String expiredate) {
        this.expiredate = expiredate;
    }

    public String getChargemoney() {
        return chargemoney;
    }

    public void setChargemoney(String chargemoney) {
        this.chargemoney = chargemoney;
    }

}
