/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.test;

import com.apt.util.Base64Util;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;

/**
 *
 * @author kevinchang
 */
public class SecurityMsg {

    private Key key = null;
    private String identifyCode = "1mJqG0A6";
    private String md5Param = "&identifyCode=";
    private String characterSet = "utf-8";
    //to PaymentGateway use 'callerInMac', Receive from PaymentGateway use 'returnOutMac'
    private String desParam = "&callerInMac=";

    public SecurityMsg(String deskey, String identifyCode) {
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

    public String sendHttpPostMsg(RequestEntity requestBody, String url) throws Exception {
        System.out.println(" ** sendHttpPostMsg step 01");
        String rtresult = null;
        PostMethod post = new PostMethod(url);
        post.setRequestEntity(requestBody);
        System.out.println("@@@requestBody:" + requestBody.toString());
        HttpClient hc = new HttpClient();
        post.addRequestHeader("Content-Type", "x-www-form-urlencoded");
        post.addRequestHeader("x-up-calling-line-id", "0928691763");
        System.out.println(" ** setConnectionManagerTimeout(10) step 02");

        try {
            hc.getParams().setConnectionManagerTimeout(10);
            int result = hc.executeMethod(post);
            System.out.println(" ** sendHttpPostMsg step 03:" + result);
            if (result == HttpStatus.SC_OK) {
                rtresult = post.getResponseBodyAsString();
            } else {
                rtresult = null;
            }
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            post.releaseConnection();
        }
        return rtresult;
    }

    public String decrypt(String baseKey, String message) {
        //Base64.Decoder decoder = Base64.getDecoder();
        //byte[] bytesrc = decoder.decode(message);
        byte[] bytesrc = Base64Util.decode(message);
        byte[] retByte = null;

        try {
            DESedeKeySpec dks = new DESedeKeySpec(baseKey.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
            SecretKey securekey = keyFactory.generateSecret(dks);

            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, securekey);
            retByte = cipher.doFinal(bytesrc);
        } catch (UnsupportedEncodingException ex) {

        } catch (InvalidKeyException ex) {

        } catch (NoSuchAlgorithmException ex) {

        } catch (InvalidKeySpecException ex) {

        } catch (BadPaddingException ex) {

        } catch (IllegalBlockSizeException ex) {

        } catch (NoSuchPaddingException ex) {

        }
        String kk_result = "";
        try {
            kk_result = new String(retByte, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return kk_result;
    }
}
