/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.apt.epay.beans;

/**
 *
 * @author kevinchang
 */
public class CancelAuthRespBean {
    private String status;
    private String errcode;
    private String errdesc;
    private String libm;
    private String action;
    private String paytype;
    private String paymentstatus;
    private String capturestatus;
    private String refundstatus;
    private String invoicestatus;
    private String ordertotal;
    private String actualamt;
    private String ordercreatedate;
    private String payaccount;
    private String invoiceno;
    private String privatedata;
    private String authdate;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrdesc() {
        return errdesc;
    }

    public void setErrdesc(String errdesc) {
        this.errdesc = errdesc;
    }

    public String getLibm() {
        return libm;
    }

    public void setLibm(String libm) {
        this.libm = libm;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPaytype() {
        return paytype;
    }

    public void setPaytype(String paytype) {
        this.paytype = paytype;
    }

    public String getPaymentstatus() {
        return paymentstatus;
    }

    public void setPaymentstatus(String paymentstatus) {
        this.paymentstatus = paymentstatus;
    }

    public String getCapturestatus() {
        return capturestatus;
    }

    public void setCapturestatus(String capturestatus) {
        this.capturestatus = capturestatus;
    }

    public String getRefundstatus() {
        return refundstatus;
    }

    public void setRefundstatus(String refundstatus) {
        this.refundstatus = refundstatus;
    }

    public String getInvoicestatus() {
        return invoicestatus;
    }

    public void setInvoicestatus(String invoicestatus) {
        this.invoicestatus = invoicestatus;
    }

    public String getOrdertotal() {
        return ordertotal;
    }

    public void setOrdertotal(String ordertotal) {
        this.ordertotal = ordertotal;
    }

    public String getActualamt() {
        return actualamt;
    }

    public void setActualamt(String actualamt) {
        this.actualamt = actualamt;
    }

    public String getOrdercreatedate() {
        return ordercreatedate;
    }

    public void setOrdercreatedate(String ordercreatedate) {
        this.ordercreatedate = ordercreatedate;
    }

    public String getPayaccount() {
        return payaccount;
    }

    public void setPayaccount(String payaccount) {
        this.payaccount = payaccount;
    }

    public String getInvoiceno() {
        return invoiceno;
    }

    public void setInvoiceno(String invoiceno) {
        this.invoiceno = invoiceno;
    }

    public String getPrivatedata() {
        return privatedata;
    }

    public void setPrivatedata(String privatedata) {
        this.privatedata = privatedata;
    }

    public String getAuthdate() {
        return authdate;
    }

    public void setAuthdate(String authdate) {
        this.authdate = authdate;
    }
    
}
