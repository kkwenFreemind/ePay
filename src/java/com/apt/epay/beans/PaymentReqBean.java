package com.apt.epay.beans;

/**
 *
 * @author Administrator
 */
public class PaymentReqBean {
    
    private String libm;
    private String type;
    private String batchNo;
    private String orderTotal;
    private String actualAmt;
    private String status;
    private String responseMsg;
    private String responseTime;
//    private String returnOutMac;
    private String isMd5Match;

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
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the batchNo
     */
    public String getBatchNo() {
        return batchNo;
    }

    /**
     * @param batchNo the batchNo to set
     */
    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    /**
     * @return the orderTotal
     */
    public String getOrderTotal() {
        return orderTotal;
    }

    /**
     * @param orderTotal the orderTotal to set
     */
    public void setOrderTotal(String orderTotal) {
        this.orderTotal = orderTotal;
    }

    /**
     * @return the actualAmt
     */
    public String getActualAmt() {
        return actualAmt;
    }

    /**
     * @param actualAmt the actualAmt to set
     */
    public void setActualAmt(String actualAmt) {
        this.actualAmt = actualAmt;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the responseMsg
     */
    public String getResponseMsg() {
        return responseMsg;
    }

    /**
     * @param responseMsg the responseMsg to set
     */
    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    /**
     * @return the responseTime
     */
    public String getResponseTime() {
        return responseTime;
    }

    /**
     * @param responseTime the responseTime to set
     */
    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    /**
     * @return the returnOutMac
     */
//    public String getReturnOutMac() {
//        return returnOutMac;
//    }
//
//    /**
//     * @param returnOutMac the returnOutMac to set
//     */
//    public void setReturnOutMac(String returnOutMac) {
//        this.returnOutMac = returnOutMac;
//    }

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
