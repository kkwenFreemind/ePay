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
public class NokiaSubscriberStatusBean {

    private String result;
    private String serviceId;
    private String language;
    private String simstatus;
    private String contract;
    private String externalId;
    private String ErrorCode;
    private String ErrorMsg;
    private String ErrorCategory;
    private String lifecycle_expire_date;

    public String getLifecycle_expire_date() {
        return lifecycle_expire_date;
    }

    public void setLifecycle_expire_date(String lifecycle_expire_date) {
        this.lifecycle_expire_date = lifecycle_expire_date;
    }

    public String getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(String ErrorCode) {
        this.ErrorCode = ErrorCode;
    }

    public String getErrorMsg() {
        return ErrorMsg;
    }

    public void setErrorMsg(String ErrorMsg) {
        this.ErrorMsg = ErrorMsg;
    }

    public String getErrorCategory() {
        return ErrorCategory;
    }

    public void setErrorCategory(String ErrorCategory) {
        this.ErrorCategory = ErrorCategory;
    }
    

    
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSimstatus() {
        return simstatus;
    }

    public void setSimstatus(String simstatus) {
        this.simstatus = simstatus;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

}
