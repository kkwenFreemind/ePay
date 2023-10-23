package com.apt.util;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import org.apache.log4j.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author 5om31
 */
public class ReturnURL {

    private static final Logger log = Logger.getLogger("EPAY");
    private String key;
    private String identifyCode;
    private String desParam = "&returnOutMac=";
    private String md5Param = "&identifyCode=";

    /**
     *
     * @param key
     * @param identifyCode
     */
    public ReturnURL(String key, String identifyCode) {
        this.key = key;
        this.identifyCode = identifyCode;
    }

    /**
     * to change destParm and md5Param
     *
     * @param key
     * @param identifyCode
     * @param desParam
     * @param md5Param
     */
    public ReturnURL(String key, String identifyCode, String desParam, String md5Param) {
        this.key = key;
        this.identifyCode = identifyCode;
        this.desParam = desParam;
        this.md5Param = md5Param;
    }

    /**
     * 帶入URL和參數字串, 加密後傳送到URL
     *
     * @param url - 傳送的URL
     * @param decodeMsg - 參數字串
     * @return
     */
    public boolean callReturnUrl(String url, String decodeMsg) {
        String USER_AGENT = "Mozilla/5.0";

        try {

            //其他系統的回傳URL
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            decodeMsg = decodeMsg.substring(0, decodeMsg.indexOf(desParam));
            ApolSecuredUrlMsg asum = new ApolSecuredUrlMsg(key, identifyCode);
            String encryptedMsg = asum.encode(decodeMsg, md5Param, desParam);

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes("encryptedMsg=" + URLEncoder.encode(encryptedMsg, "UTF-8"));
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();

            if (log.isDebugEnabled()) {
                log.info("\nSending 'POST' request to URL : " + url);
                log.info("Post parameters : " + encryptedMsg);
                log.info("Response Code : " + responseCode);

                java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(con.getInputStream()));
                String inpLine;
                StringBuffer response = new StringBuffer();
                while ((inpLine = in.readLine()) != null) {
                    response.append(inpLine);
                }
                in.close();
            }

        } catch (Exception e) {
            log.error(null, e);
            return false;
        }

        return true;
    }

    private static void trustAllHttpsCertificates() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[1];
        TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
    
   static class miTM implements TrustManager, X509TrustManager {

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(X509Certificate[] certs) {
            return true;
        }

//        public void checkServerTrusted(X509Certificate[] certs, String authType)
//                throws CertificateException {
//            return;
//        }
//
//        public void checkClientTrusted(X509Certificate[] certs, String authType)
//                throws CertificateException {
//            return;
//        }
        @Override
        public void checkClientTrusted(X509Certificate[] xcs, String string) throws java.security.cert.CertificateException {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void checkServerTrusted(X509Certificate[] xcs, String string) throws java.security.cert.CertificateException {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//            return;
        }
   }
}
