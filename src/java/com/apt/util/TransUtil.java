/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.util;

/**
 *
 * @author kevinchang
 */
public class TransUtil {

    public String getPayToolDesc(int paytool, String payname) {
        String result = "";

        //付款方式(TRANSACTION.PAYTOOL)：(1)信用卡(2)ATM(3)餘額抵扣(4)電信帳單(5)小額支付(6)現金(7)悠遊卡(8)一卡通(9)iCash(10)GtPay(11)玉山支付寶(12)u2bill
        if (paytool == 1) {
            return "信用卡";
        }
        if (paytool == 2) {
            return "ATM";
        }
        if (paytool == 3) {
            return "餘額抵扣";
        }
        if (paytool == 4) {
            return "電信帳單";
        }
        if (paytool == 5) {
            return "小額支付";
        }
        if (paytool == 6) {
            return "現金";
        }

        if (paytool == 7) {
            if (!"".equals(payname)) {
                return "電子票證(" + payname + ")";
            } else {
                return "電子票證";
            }
        }
        if (paytool == 8) {
            if (!"".equals(payname)) {
                return "第三方支付(" + payname + ")";
            } else {
                return "第三方支付";
            }
        }
        if (paytool == 9) {
            if (!"".equals(payname)) {
                return "GtPay(" + payname + ")";
            } else {
                return "GtPay";
            }
        }
        if (paytool == 10) {
            if (!"".equals(payname)) {
                return "贈送(" + payname + ")";
            } else {
                return "贈送";
            }
        }
        if (paytool == 13) {
            return "IBon";
        }
        
        if (paytool == 14) {
            return "家鄉儲";
        }
        
        return result;
    }

    public String getPayArcsID(int arcid) {
        String result = "";
        //儲值方式(TRANSACTION.PAYAPISRCID)：(1)Gt網站(2)GtPay(3)Gt客服(4)Gt行動客服(5)Gt門市(XXX)(6)超商立即儲(7)代理商

        if (arcid == 1) {
            return "Gt網站";
        }
        if (arcid == 2) {
            return "GtPay";
        }
        if (arcid == 3) {
            return "Gt客服";
        }
        if (arcid == 4) {
            return "Gt行動客服";
        }
        if (arcid == 5) {
            return "Gt門市";
        }
        if (arcid == 6) {
            return "超商立即儲";
        }
        if (arcid == 7) {
            return "代理商";
        }
        if (arcid == 8) {
            return "促案平台";
        }
        return result;
    }

}
