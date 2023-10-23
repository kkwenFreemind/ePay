/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.util;

import com.apt.epay.share.ShareParm;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;

/**
 *
 * @author kevinchang
 */
public class SendSMS {

    private String PARM_XSMS_URL = new ShareParm().PARM_XSMS_URL;
    private String PARM_XSMS_UID = new ShareParm().PARM_XSMS_UID;
    private String PARM_XSMS_PWD = new ShareParm().PARM_XSMS_PWD;
    private String PARM_XSMS_MDN = new ShareParm().PARM_XSMS_MDN;
    private String PARM_XSMS_CALLBACK = new ShareParm().PARM_XSMS_CALLBACK;

    private String PARM_XSMS_OTP_URL = new ShareParm().PARM_XSMS_OTP_URL;
    private String PARM_XSMS_OTP_UID = new ShareParm().PARM_XSMS_OTP_UID;
    private String PARM_XSMS_OTP_PWD = new ShareParm().PARM_XSMS_OTP_PWD;
    private String PARM_XSMS_OTP_MDN = new ShareParm().PARM_XSMS_OTP_MDN;
    private String PARM_XSMS_OTP_CALLBACK = new ShareParm().PARM_XSMS_OTP_CALLBACK;

    private static final Logger log = Logger.getLogger("EPAY");

    public void sendOTPsms(String BNO, String message) throws Exception {

        String[] cArray = BNO.split(",");
        String reqUrl = PARM_XSMS_OTP_URL + "?MDN=" + PARM_XSMS_OTP_MDN + "&UID=" + PARM_XSMS_OTP_UID + "&UPASS=" + PARM_XSMS_OTP_PWD;
        PostMethod post = new PostMethod(reqUrl);

        String requestBody = "<Request><Subject>EPAY OPT:" + "</Subject><Retry>Y</Retry><AutoSplit>Y</AutoSplit><Callback>" + PARM_XSMS_OTP_CALLBACK + "</Callback><Message>" + message + "</Message>"
                        + "<MDNList>";

        for (String xmdn : cArray) {
            requestBody = requestBody + "<MSISDN>" + xmdn + "</MSISDN>";
        }

        requestBody = requestBody + "</MDNList></Request>";

        log.info("SendSMS.send==>" + requestBody);

        RequestEntity re = new StringRequestEntity(requestBody, "text/xml;charset=utf-8", "utf-8");
        post.setRequestEntity(re);

        /* 設定HTTP內容 */
        post.addParameter("Content", new String(requestBody.getBytes("UTF-8"), "iso-8859-1"));
        post.addParameter("Content", requestBody);

        HttpClient hc = new HttpClient();
        BufferedReader br = null;
        InputStreamReader is = null;

        try {

            int result = hc.executeMethod(post);
            if (result == HttpStatus.SC_OK) {
                log.info("succed in recieving of httpClient!");
                log.info(post.getResponseBodyAsString());
            } else {
                log.info("HTTP POST Fail HttpException;\n");
            }
        } catch (HttpException e) {
            log.info("catch HttpException;\n");
            e.printStackTrace();

        } catch (IOException e) {
            log.info("catch IOException;\n");
            e.printStackTrace();

        } finally {
            post.releaseConnection();
        }
    }

    public void sendsms(String BNO, String message) throws Exception {

        String[] cArray = BNO.split(",");
        String reqUrl = PARM_XSMS_URL + "?MDN=" + PARM_XSMS_MDN + "&UID=" + PARM_XSMS_UID + "&UPASS=" + PARM_XSMS_PWD;
        PostMethod post = new PostMethod(reqUrl);

        String requestBody = "<Request><Subject>預付卡資訊:" + "</Subject><Retry>Y</Retry><AutoSplit>Y</AutoSplit><Callback>" + PARM_XSMS_CALLBACK + "</Callback><Message>" + message + "</Message>"
                        + "<MDNList>";

        for (String xmdn : cArray) {
            requestBody = requestBody + "<MSISDN>" + xmdn + "</MSISDN>";
        }

        requestBody = requestBody + "</MDNList></Request>";

        log.info("SendSMS.send==>" + requestBody);

        RequestEntity re = new StringRequestEntity(requestBody, "text/xml;charset=utf-8", "utf-8");
        post.setRequestEntity(re);

        /* 設定HTTP內容 */
        post.addParameter("Content", new String(requestBody.getBytes("UTF-8"), "iso-8859-1"));
        post.addParameter("Content", requestBody);

        HttpClient hc = new HttpClient();
        BufferedReader br = null;
        InputStreamReader is = null;

        try {

            int result = hc.executeMethod(post);
            if (result == HttpStatus.SC_OK) {
                log.info("succed in recieving of httpClient!");
                log.info(post.getResponseBodyAsString());
            } else {
                log.info("HTTP POST Fail HttpException;\n");
            }
        } catch (HttpException e) {
            log.info("catch HttpException;\n");
            e.printStackTrace();

        } catch (IOException e) {
            log.info("catch IOException;\n");
            e.printStackTrace();

        } finally {
            post.releaseConnection();
        }
    }
}
