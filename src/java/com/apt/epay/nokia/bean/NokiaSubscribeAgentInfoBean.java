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
public class NokiaSubscribeAgentInfoBean {

    private String result;
    private String ErrorCode;
    private String ErrorMsg;
    private String ErrorCategory;

    private String ServiceID;
    private String ExternalID;
    private String IMSI;
    private String ContractID;
    private String Language;
    private String SIMState;
    private String LifecycleExpiryDate;
    private String AgentID;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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

    public String getServiceID() {
        return ServiceID;
    }

    public void setServiceID(String ServiceID) {
        this.ServiceID = ServiceID;
    }

    public String getExternalID() {
        return ExternalID;
    }

    public void setExternalID(String ExternalID) {
        this.ExternalID = ExternalID;
    }

    public String getIMSI() {
        return IMSI;
    }

    public void setIMSI(String IMSI) {
        this.IMSI = IMSI;
    }

    public String getContractID() {
        return ContractID;
    }

    public void setContractID(String ContractID) {
        this.ContractID = ContractID;
    }

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String Language) {
        this.Language = Language;
    }

    public String getSIMState() {
        return SIMState;
    }

    public void setSIMState(String SIMState) {
        this.SIMState = SIMState;
    }

    public String getLifecycleExpiryDate() {
        return LifecycleExpiryDate;
    }

    public void setLifecycleExpiryDate(String LifecycleExpiryDate) {
        this.LifecycleExpiryDate = LifecycleExpiryDate;
    }

    public String getAgentID() {
        return AgentID;
    }

    public void setAgentID(String AgentID) {
        this.AgentID = AgentID;
    }
    
    
}
