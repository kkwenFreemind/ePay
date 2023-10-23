/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.util;

import com.apt.epay.share.ShareParm;
import java.io.IOException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
//import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;

/**
 *
 * @author kevinchang
 */
public class HttpClientUtil {

    private static final Logger log = Logger.getLogger("EPAY");
//    private static String deskey = ShareParm.PARM_PG_KEY;
//    private static String identifyCode = ShareParm.PARM_PG_IDENT;

    public String sendVASPostMsg(String cpid, String sendURL, StringBuffer buffer) throws Exception {

        
        String rtresult = null;
        String data=(new SecuredMsg(new ShareParm().PARM_PG_KEY, new ShareParm().PARM_PG_IDENT).encode(buffer.toString())).toString();
        log.info("data==>"+data);
        
        
        String url = sendURL + "system=" + cpid + "&data=" + data;
        log.info("sendURL====>" + url);

        PostMethod post = new PostMethod(sendURL);
        post.addParameter("system",cpid);
        post.addParameter("data",data);

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
        } catch (IOException e) {
        } finally {
            post.releaseConnection();
        }
        return rtresult;
    }

    public String sendKKHttpPostMsg(String url) throws Exception {
//        log.info(" ** sendHttpPostMsg step 01");
        String rtresult = null;
        GetMethod post = new GetMethod(url);
//        post.setRequestEntity(requestBody);
        HttpClient hc = new HttpClient();

//        log.info(" ** setConnectionManagerTimeout(10) step 02");

        try {
            hc.getParams().setConnectionManagerTimeout(10);
//        System.out.println(" ** .setSoTimeout(10) step 02");
//            hc.getParams().setSoTimeout(10);

            int result = hc.executeMethod(post);
            if (result == HttpStatus.SC_OK) {
                rtresult = post.getResponseBodyAsString();
            } else {
                rtresult = null;
            }
        } catch (HttpException e) {
        } catch (IOException e) {
        } finally {
            post.releaseConnection();
        }
        return rtresult;
    }
    public static String sendHttpPostMsg(RequestEntity requestBody, String url) throws Exception {
//        System.out.println(" ** sendHttpPostMsg step 01");
        String rtresult = null;
        PostMethod post = new PostMethod(url);
        post.setRequestEntity(requestBody);
        HttpClient hc = new HttpClient();

//        System.out.println(" ** setConnectionManagerTimeout(10) step 02");
        try {
            hc.getParams().setConnectionManagerTimeout(ShareParm.HTTP_REQ_TIMEOUT);
            log.info("ShareParm.HTTP_REQ_TIMEOUT===>"+ShareParm.HTTP_REQ_TIMEOUT);
//        System.out.println(" ** .setSoTimeout(10) step 02");
            hc.getParams().setSoTimeout(1);

            int result = hc.executeMethod(post);
//            System.out.println(" ** sendHttpPostMsg step 03");
            if (result == HttpStatus.SC_OK) {
                rtresult = post.getResponseBodyAsString();
//                log.info("HTTP Client SC_OK="+rtresult);
            } else {
                rtresult = null;
//                log.info("HTTP Client null="+rtresult);
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
    public static String sendHttpPostMsg(NameValuePair[] nvp, String url) {
//        System.out.println(" ** sendHttpPostMsg step 01");
        String rtresult = null;
        PostMethod post = new PostMethod(url);
        post.setRequestBody(nvp);
        HttpClient hc = new HttpClient();

//        System.out.println(" ** setConnectionManagerTimeout(10) step 02");
        try {
            hc.getParams().setConnectionManagerTimeout(ShareParm.HTTP_REQ_TIMEOUT);
            log.info("ShareParm.HTTP_REQ_TIMEOUT===>"+ShareParm.HTTP_REQ_TIMEOUT);
            
//        System.out.println(" ** .setSoTimeout(10) step 02");
            hc.getParams().setSoTimeout(200);

            int result = hc.executeMethod(post);
//            System.out.println(" ** sendHttpPostMsg step 03");
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

    public String KKStringSplit(String str) {
        String result = "";
        String[] strArray0 = str.split("&");
//        String showStr0 = "";
        for (int index = 0; index < strArray0.length; index++) {
            int pos = strArray0[index].indexOf("Balance=");
            if (pos != -1) {
//                log.info(index + ":" + strArray0[index].substring("Balance=".length()));
//                LLStringSplit(strArray0[index].substring("Balance=".length()));
                result = strArray0[index].substring("Balance=".length());
            } else {
                result = "";
            }
        }
        return result;
    }

//    public String LLStringSplit(String str) {
//        String result = "";
//        String[] strArray0 = str.split(";");
//        String showStr0 = "";
//        for (int index = 0; index < strArray0.length; index++) {
//            int pos = strArray0[index].indexOf("1,");
//            if (pos != -1) {
//                log.info("1--->"+ strArray0[index].substring("1,".length()));
//                PPStringSplit(strArray0[index].substring("1,".length()));
//                result = strArray0[index].substring("1,".length());                
//            }
//        }
//        for (int index = 0; index < strArray0.length; index++) {
//            int pos = strArray0[index].indexOf("2,");
//            if (pos != -1) {
//                log.info("2--->"+ strArray0[index].substring("2,".length()));
//                PPStringSplit(strArray0[index].substring("2,".length()));
//                result = strArray0[index].substring("2,".length());                
//            }
//        }
//        for (int index = 0; index < strArray0.length; index++) {
//            int pos = strArray0[index].indexOf("4,");
//            if (pos != -1) {
//                log.info("4--->"+ strArray0[index].substring("4,".length()));
//                PPStringSplit(strArray0[index].substring("4,".length()));
//                result = strArray0[index].substring("4,".length());                
//            }
//        }        
//        return result;
//    }    
//    
//    public String PPStringSplit(String str) {
//        String result = "";
//        String[] strArray0 = str.split(",");
//        String showStr0 = "";
//        for (int index = 0; index < strArray0.length; index++) {
//            log.info(strArray0[index]);
//        }
//
//        return result;
//    }       
}
