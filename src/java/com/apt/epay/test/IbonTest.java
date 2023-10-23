/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.test;

import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;

/**
 *
 * @author kevinchang
 */
public class IbonTest {

//    private String CPID = "50001";
//    private String MDN = "0906001001";
//    private String CPLIBM = "";
//    private String PINCODE = "";
//    private String SALESID = "";
//    private String STOREID = "";
//    private String APISRCID = "";
//    private String PAYTOOL = "";
//    private String PAYNAME = "";
//    private String STORENAME = "";
//
//    private static String poskey = "ABCDFEGHIJ12345678901234";
//    private static String deskey = "Ad68w577";
//    private static String identifyCode = "Af67iuWa";
//
//    private static String IBonUserStateLookup_URL = "http://epaytde.aptg.com.tw/EPAY/IBonUserStateLookup?CPID=50001";
//    private static String IBonUserStateLookup_RequestBody = "<UserStateLookupRequest><MDN>0906001001</MDN></UserStateLookupRequest>";
//
//    
//    private static String IBonPincodeOrderStatus_URL = "http://epaytde.aptg.com.tw/EPAY/IBonPincodeOrderStatus?CPID=50001";                   
//    private String IBonPincodeOrderStatus_RequestBody = "<PincodeOrderStatusRequest><MDN>" + MDN + "</MDN><CPLIBM>" + CPLIBM + "</CPLIBM></PincodeOrderStatusRequest>";
//
//    private static String IBonPincodeOrder_URL = "http://epaytde.aptg.com.tw/EPAY/IBonPincodeOrder?CPID=50001";
//    private String IBonPincodeOrder_RequestBody = "<PincodeOrderRequest><MDN>" + MDN + "</MDN>"
//                    + "<PINCODE>" + PINCODE + "</PINCODE>"
//                    + "<CPLIBM>" + CPLIBM + "</CPLIBM>"
//                    + "<SALESID>" + SALESID + "</SALESID>"
//                    + "<STOREID>" + STOREID + "</STOREID>"
//                    + "<APISRCID>" + APISRCID + "</APISRCID>"
//                    + "<PAYTOOL>" + PAYTOOL + "</PAYTOOL>"
//                    + "<PAYNAME>" + PAYNAME + "</PAYNAME>"
//                    + "<STORENAME>" + STORENAME + "</STORENAME></PincodeOrderRequest>";
//
//    public static void main(String[] args) throws Exception {
//        
//        String ss = "Hello! World!";
//        
//        System.out.println(ss.indexOf("Hello") );
//
//        String kkbody = new SecurityMsg(deskey, identifyCode).encode(IBonUserStateLookup_RequestBody);
//        String responseBody = "";
//
//        RequestEntity body;
//        System.out.println("@@requestBody:" + kkbody);
//        body = new StringRequestEntity(kkbody, "text/xml", "big5");
//
//        responseBody = (new SecurityMsg(deskey, identifyCode)).sendHttpPostMsg(body, IBonUserStateLookup_URL);
//
//        String decode_resp = new SecurityMsg(deskey, identifyCode).decrypt(poskey, responseBody);
//
//        System.out.println("@@responseBody:" + decode_resp);
//    }
}
