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
public class ServiceOrderStatusReqBean {
    
    private String mdn;
    private String libm;

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getLibm() {
        return libm;
    }

    public void setLibm(String libm) {
        this.libm = libm;
    }
    
    
}
