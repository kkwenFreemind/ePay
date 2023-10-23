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
public class VoucherInfoReqBean {
    
    private String VoucherCard;
    private String VoucherCardFlag;
    private String StoreName;
    private String StoreId;

    public String getStoreName() {
        return StoreName;
    }

    public void setStoreName(String StoreName) {
        this.StoreName = StoreName;
    }

    public String getStoreId() {
        return StoreId;
    }

    public void setStoreId(String StoreId) {
        this.StoreId = StoreId;
    }
    

    public String getVoucherCard() {
        return VoucherCard;
    }

    public void setVoucherCard(String VoucherCard) {
        this.VoucherCard = VoucherCard;
    }

    public String getVoucherCardFlag() {
        return VoucherCardFlag;
    }

    public void setVoucherCardFlag(String VoucherCardFlag) {
        this.VoucherCardFlag = VoucherCardFlag;
    }
    
    
}
