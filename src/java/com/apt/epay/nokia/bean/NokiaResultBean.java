/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.nokia.bean;

/**
 *
 * @author kevinchang
 */
public class NokiaResultBean {

    private int httpstatus;
    private String xmdrecord;
    
    public void NokiaResultBean(){
        
    }

    public int getHttpstatus() {
        return httpstatus;
    }

    public void setHttpstatus(int httpstatus) {
        this.httpstatus = httpstatus;
    }

    public String getXmdrecord() {
        return xmdrecord;
    }

    public void setXmdrecord(String xmdrecord) {
        this.xmdrecord = xmdrecord;
    }

}
