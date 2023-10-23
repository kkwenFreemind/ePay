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
public class CWSReqBean {
    
    private String ContractActDate;
    private String ContractStatus;
    private String ProductType;
    private String ContractStatusDesc;
    private String min;
    private String mdn;

    public String getContractActDate() {
        return ContractActDate;
    }

    public void setContractActDate(String ContractActDate) {
        this.ContractActDate = ContractActDate;
    }

    public String getContractStatus() {
        return ContractStatus;
    }

    public void setContractStatus(String ContractStatus) {
        this.ContractStatus = ContractStatus;
    }

    public String getProductType() {
        return ProductType;
    }

    public void setProductType(String ProductType) {
        this.ProductType = ProductType;
    }

    public String getContractStatusDesc() {
        return ContractStatusDesc;
    }

    public void setContractStatusDesc(String ContractStatusDesc) {
        this.ContractStatusDesc = ContractStatusDesc;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }
    
    
}
