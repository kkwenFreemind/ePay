/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.beans;

/**
 *
 * @author Administrator
 */
public class APIATMBackReqBean {
    
    private String libm;
    private String account;
    private String bankid;

    public String getLibm() {
        return libm;
    }

    public void setLibm(String libm) {
        this.libm = libm;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getBankid() {
        return bankid;
    }

    public void setBankid(String bankid) {
        this.bankid = bankid;
    }

}
