/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author kevinchang
 */
public class SecuredMsg {

    private Key key = null;
    private String identifyCode = "1mJqG0A6";
    private String md5Param = "&identifyCode=";
    private String characterSet = "utf-8";
    //to PaymentGateway use 'callerInMac', Receive from PaymentGateway use 'returnOutMac'
    private String desParam = "&callerInMac=";

    public SecuredMsg(String deskey, String identifyCode) {
        this.key = new SecretKeySpec(deskey.getBytes(), "DES");
        this.identifyCode = identifyCode;
    }

    public String encode(String plainText) throws Exception {
        return encode(plainText, md5Param, desParam);
    }

    public String encode(String plainText, String md5Param, String desParam) throws Exception {
        String outMsg = null;
        Cipher cipher = null;
        String md5Value = doMd5(plainText + md5Param + this.identifyCode);
        byte[] byteInMsg = null;
        byte[] byteEncrypted = null;

        cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        plainText = plainText + desParam + md5Value;
        byteInMsg = plainText.getBytes(characterSet);
        byteEncrypted = cipher.doFinal(byteInMsg);
        outMsg = URLEncoder.encode(new String(byteEncrypted, "ISO-8859-1"), "ISO-8859-1");
        return outMsg;
    }

    public String decode(String cipherText) throws Exception {
        return decode(cipherText, md5Param, desParam);
    }

    public String decode(String cipherText, String md5Param, String desParam) throws Exception {
        String outMsg = null;
        Cipher cipher = null;
        byte[] byteInMsg = null;
        byte[] byteDecrypted = null;
        int md5Index = 0;
        String result = null;
        cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);

        byteInMsg = URLDecoder.decode(cipherText, "ISO-8859-1").getBytes("ISO-8859-1");
        byteDecrypted = cipher.doFinal(byteInMsg);
        outMsg = new String(byteDecrypted, characterSet);
        md5Index = outMsg.indexOf(desParam);
        result = outMsg.substring(0, md5Index);
        return (doMd5(result + md5Param + this.identifyCode).equals(outMsg.substring(md5Index + desParam.length(), outMsg.length()))) ? outMsg + "&isMd5Match=true" : outMsg + "&isMd5Match=false";
    }

    private String doMd5(String msg) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        return bin2Hex(md5.digest(msg.getBytes(characterSet)));
    }

    private String bin2Hex(byte byteAry[]) {
        int bufLength = byteAry.length;
        StringBuffer strbuf = new StringBuffer(bufLength * 2);

        for (int i = 0; i < bufLength; i++) {
            if (((int) byteAry[i] & 0xff) < 0x10) {
                strbuf.append("0");
            }
            strbuf.append(Long.toString((int) byteAry[i] & 0xff, 16));
        }
        return strbuf.toString();
    }
}
