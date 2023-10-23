/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.deposit.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.log4j.Logger;

/**
 *
 * @author kevinchang
 */
public class toolUtil {

    private final Logger log;// = Logger.getLogger("EPAY");

    public toolUtil() {
        this.log = Logger.getLogger("EPAY");
    }

    public String sendHttpPostMsg(RequestEntity requestBody, String url) throws Exception {

        String rtresult = null;
        PostMethod post = new PostMethod(url);
        post.setRequestEntity(requestBody);
        HttpClient hc = new HttpClient();

        try {
            hc.getParams().setConnectionManagerTimeout(10);
            int result = hc.executeMethod(post);

            if (result == HttpStatus.SC_OK) {
                rtresult = post.getResponseBodyAsString();
            } else {
                rtresult = null;
            }
        } catch (HttpException e) {
            log.info(e);
        } catch (IOException e) {
            log.info(e);
        } finally {
            post.releaseConnection();
        }
        return rtresult;
    }

    public String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            log.info(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.info(e);
                }
            }
        }
        return sb.toString();
    }

    public String GetRandomString(int Len) {
        String[] baseString = {"0", "1", "2", "3",
            "4", "5", "6", "7", "8", "9",
            "a", "b", "c", "d", "e",
            "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o",
            "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y",
            "z", "A", "B", "C", "D",
            "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S",
            "T", "U", "V", "W", "X", "Y", "Z"};
        Random random = new Random();
        int length = baseString.length;
        String randomString = "";
        for (int i = 0; i < length; i++) {
            randomString += baseString[random.nextInt(length)];
        }
        random = new Random(System.currentTimeMillis());
        String resultStr = "";
        for (int i = 0; i < Len; i++) {
            resultStr += randomString.charAt(random.nextInt(randomString.length() - 1));
        }
        return resultStr;
    }
}
