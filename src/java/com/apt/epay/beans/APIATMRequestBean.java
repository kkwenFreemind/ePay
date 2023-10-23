/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.beans;

/**
 *
 * @author Administrator
 */
public class APIATMRequestBean {
    
    private String libm;
    private String payTime;
    private String payMethod;
    private String payStatus;
    private String payAmount;
    private String tel;
    private String returnOutMac;
    private String isMd5Match;
    private int Code;
    private String Reason;

    public void setCode(int Code) {
        this.Code = Code;
    }

    public int getCode() {
        return this.Code;
    }

    public void setReason(String Reason) {
        this.Reason = Reason;
    }

    public String getReason() {
        return this.Reason;
    }

    /**
     * @return the libm
     */
    public String getLibm() {
        return libm;
    }

    /**
     * @param libm the libm to set
     */
    public void setLibm(String libm) {
        this.libm = libm;
    }

    /**
     * @return the payTime
     */
    public String getPayTime() {
        return payTime;
    }

    /**
     * @param payTime the payTime to set
     */
    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    /**
     * @return the payMethod
     */
    public String getPayMethod() {
        return payMethod;
    }

    /**
     * @param payMethod the payMethod to set
     */
    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    /**
     * @return the payStatus
     */
    public String getPayStatus() {
        return payStatus;
    }

    /**
     * @param payStatus the payStatus to set
     */
    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    /**
     * @return the payAmount
     */
    public String getPayAmount() {
        return payAmount;
    }

    /**
     * @param payAmount the payAmount to set
     */
    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }

    /**
     * @return the tel
     */
    public String getTel() {
        return tel;
    }

    /**
     * @param tel the tel to set
     */
    public void setTel(String tel) {
        this.tel = tel;
    }

    /**
     * @return the returnOutMac
     */
    public String getReturnOutMac() {
        return returnOutMac;
    }

    /**
     * @param returnOutMac the returnOutMac to set
     */
    public void setReturnOutMac(String returnOutMac) {
        this.returnOutMac = returnOutMac;
    }

    /**
     * @return the isMd5Match
     */
    public String getIsMd5Match() {
        return isMd5Match;
    }

    /**
     * @param isMd5Match the isMd5Match to set
     */
    public void setIsMd5Match(String isMd5Match) {
        this.isMd5Match = isMd5Match;
    }
}
