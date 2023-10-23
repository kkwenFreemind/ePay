/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.sct;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 *
 * @author kevinchang
 */
public class SctUtils {

    public String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public String md5(String str) {
        String md5 = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] barr = md.digest(str.getBytes());  //將 byte 陣列加密
            StringBuilder sb = new StringBuilder();  //將 byte 陣列轉成 16 進制
            for (int i = 0; i < barr.length; i++) {
                sb.append(byte2Hex(barr[i]));
            }
            String hex = sb.toString();
            md5 = hex.toUpperCase(); //一律轉成大寫
        } catch (NoSuchAlgorithmException e) {
        }
        return md5;
    }

    public static String byte2Hex(byte b) {
        String[] h = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
        int i = b;
        if (i < 0) {
            i += 256;
        }
        return h[i / 16] + h[i % 16];
    }

    public String getCountryName(String code) {
        String result = "";
        String[] tokens = code.split(",");
        for (String token : tokens) {
            if ("81".equals(token)) {
                result = result + "日本,";
            } else if ("82".equals(token)) {
                result = result + "南韓,";
            } else if ("1".equals(token)) {
                result = result + "美加,";
            } else if ("86".equals(token)) {
                result = result + "中國,";
            } else if ("853".equals(token)) {
                result = result + "澳門,";
            } else if ("852".equals(token)) {
                result = result + "香港,";
            } else if ("886".equals(token)) {
                result = result + "台灣,";
            }else if ("EU".equals(token)) {
                result = result + "歐洲,";
            }
        }
        result = result.substring(0, result.length() - 1);
        return result;
    }
}
