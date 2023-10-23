/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.beans;

import java.util.List;

public class InvoiceReqBean {

    private String libm;
    private String type;
    private String status;
    private String discount_no;
    private String invoice_no;
    private String invoice_created_date;
    private String random_no;
    private String buyer_id;
    private String tax_type;
    private String response_msg;
    private String response_time;
    private String no;
    private String product_name;
    private String quantity;
    private String price;
    private String not_include_tax;
    private String tax;
    private String subtotal;
    private String donate;
    private List<InvoiceItemReqBean> invoiceItems;

    public String getDonate() {
        return donate;
    }

    public void setDonate(String donate) {
        this.donate = donate;
    }

    
    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNot_include_tax() {
        return not_include_tax;
    }

    public void setNot_include_tax(String not_include_tax) {
        this.not_include_tax = not_include_tax;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
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
     * @return the discount_no
     */
    public String getDiscount_no() {
        return discount_no;
    }

    /**
     * @param discount_no the discount_no to set
     */
    public void setDiscount_no(String discount_no) {
        this.discount_no = discount_no;
    }

    /**
     * @return the invoice_no
     */
    public String getInvoice_no() {
        return invoice_no;
    }

    /**
     * @param invoice_no the invoice_no to set
     */
    public void setInvoice_no(String invoice_no) {
        this.invoice_no = invoice_no;
    }

    /**
     * @return the invoice_created_date
     */
    public String getInvoice_created_date() {
        return invoice_created_date;
    }

    /**
     * @param invoice_created_date the invoice_created_date to set
     */
    public void setInvoice_created_date(String invoice_created_date) {
        this.invoice_created_date = invoice_created_date;
    }

    /**
     * @return the random_no
     */
    public String getRandom_no() {
        return random_no;
    }

    /**
     * @param random_no the random_no to set
     */
    public void setRandom_no(String random_no) {
        this.random_no = random_no;
    }

    /**
     * @return the buyer_id
     */
    public String getBuyer_id() {
        return buyer_id;
    }

    /**
     * @param buyer_id the buyer_id to set
     */
    public void setBuyer_id(String buyer_id) {
        this.buyer_id = buyer_id;
    }

    /**
     * @return the tax_type
     */
    public String getTax_type() {
        return tax_type;
    }

    /**
     * @param tax_type the tax_type to set
     */
    public void setTax_type(String tax_type) {
        this.tax_type = tax_type;
    }

    /**
     * @return the response_msg
     */
    public String getResponse_msg() {
        return response_msg;
    }

    /**
     * @param response_msg the response_msg to set
     */
    public void setResponse_msg(String response_msg) {
        this.response_msg = response_msg;
    }

    /**
     * @return the response_time
     */
    public String getResponse_time() {
        return response_time;
    }

    /**
     * @param response_time the response_time to set
     */
    public void setResponse_time(String response_time) {
        this.response_time = response_time;
    }

    /**
     * @return the invoiceItems
     */
    public List<InvoiceItemReqBean> getInvoiceItems() {
        return invoiceItems;
    }

    /**
     * @param invoiceItems the invoiceItems to set
     */
    public void setInvoiceItems(List<InvoiceItemReqBean> invoiceItems) {
        this.invoiceItems = invoiceItems;
    }

}
