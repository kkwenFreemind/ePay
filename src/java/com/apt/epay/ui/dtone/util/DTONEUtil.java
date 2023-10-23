/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.dtone.util;

import com.apt.epay.share.ShareParm;
import com.apt.util.Base64Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.SecureRandom;
import org.apache.log4j.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author kevinchang
 */
public class DTONEUtil {

    private static final Logger log = Logger.getLogger("EPAY");

    private String dtone_mobile_url = new ShareParm().PARM_DTONE_URL;
    private String dtone_trans_url = new ShareParm().PARM_DTONE_TRANS_URL;
    private String dtone_trans_query_url = new ShareParm().PARM_DTONE_TRANS_QUERY_URL;
    private String username = new ShareParm().PARM_DTONE_USERNAME;
    private String password = new ShareParm().PARM_DTONE_PASSWORD;
    private String last_name = new ShareParm().PARM_DTONE_LAST_NAME;
    private String first_name = new ShareParm().PARM_DTONE_FIRST_NAME;

    public String getTransactionStatus(String tid) {
        String result = "-1";
        try {
            SSLContext ctx = SSLContext.getInstance("TLSv1.2");
            ctx.init(null, null, new SecureRandom());
            //ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
            SSLContext.setDefault(ctx);

            //System.setProperty("https.protocols", "TLSv1"); 
//            System.setProperty("https.protocols","TLSv1.2,TLSv1.1,SSLv3"); 
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.31.64.19", 5901));
            // String q_url = "https://preprod-dvs-api.dtone.com/v1/transactions/";
            String s_url = dtone_trans_query_url + "+" + tid;
            URL kk_url = new URL(s_url);

            String PROXY_FLAG = new ShareParm().PARM_SCT_PROXY_FLAG;
            HttpsURLConnection conn;
            if ("PROD".equals(PROXY_FLAG)) {
                conn = (HttpsURLConnection) kk_url.openConnection(proxy);
            } else {
                log.info("TDE env, NO proxy");
                conn = (HttpsURLConnection) kk_url.openConnection();
            }
            conn.setSSLSocketFactory(ctx.getSocketFactory());

            log.info("DTONE URL=>" + s_url);

            conn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });

            String auth = username + ":" + password;

            String client_credentials_base64 = "";
            byte[] a = auth.getBytes();
            client_credentials_base64 = Base64Util.encode(a);
            String authHeaderValue = "Basic " + new String(client_credentials_base64);

            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Authorization", authHeaderValue);
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();

            StringBuilder buffer = new StringBuilder();
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str;// = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 釋放資源
            inputStream.close();
            conn.disconnect();

            String kkstr = buffer.toString();

            log.info("kkstr==>" + kkstr);
            JSONObject kk = new JSONObject(kkstr);
            JSONObject kk_status = kk.getJSONObject("status");
            log.info("status object===>" + kk_status);

            String js_status = kk_status.getString("message");
            log.info("message value===>" + js_status);

            log.info(conn.getResponseCode() + ", " + conn.getResponseMessage());
            return js_status;

        } catch (IOException ex) {
            log.info(ex);
        } catch (KeyManagementException ex) {
            log.info(ex);
        } catch (NoSuchAlgorithmException ex) {
            log.info(ex);
        }

        return result;
    }

    public DTOneResultBean setCountryCodeAddValue(String apt_mdn, String dtone_mdn, int product_id) {
//        String result = "-1";
        DTOneResultBean dtonebean = new DTOneResultBean();
        String new_apt_mdn = "+886" + apt_mdn.substring(1);

        try {
            SSLContext ctx = SSLContext.getInstance("TLSv1.2");
            ctx.init(null, null, new SecureRandom());
            //ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
            SSLContext.setDefault(ctx);

            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.31.64.19", 5901));
            URL kk_url = new URL(dtone_trans_url);
            String PROXY_FLAG = new ShareParm().PARM_SCT_PROXY_FLAG;
            HttpsURLConnection conn;
            if ("PROD".equals(PROXY_FLAG)) {
                conn = (HttpsURLConnection) kk_url.openConnection(proxy);
            } else {
                log.info("TDE env, no proxy");
                conn = (HttpsURLConnection) kk_url.openConnection();
            }

            conn.setSSLSocketFactory(ctx.getSocketFactory());
            log.info("DTONE URL=>" + dtone_trans_url);

            conn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });

            String auth = username + ":" + password;

            String client_credentials_base64 = "";
            byte[] a = auth.getBytes();
            client_credentials_base64 = Base64Util.encode(a);
            log.info("client_credentials_base64===>" + client_credentials_base64);
            String authHeaderValue = "Basic " + new String(client_credentials_base64);

            conn.setConnectTimeout(50000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Authorization", authHeaderValue);
            conn.setRequestMethod("POST");

            SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
            Calendar nowDateTime = Calendar.getInstance();
            String libm = sdf15.format(nowDateTime.getTime());
            String jsonbody = "";

            jsonbody = "{"
                            + "\"debit_party_identifier\": {"
                            + "\"mobile_number\": \"" + new_apt_mdn + "\""
                            + "},"
                            + "\"sender\": {"
                            + "\"last_name\": \"" + last_name + "\","
                            + "\"first_name\": \"" + first_name + "\""
                            + "},"
                            + "\"product_id\": " + product_id + ","
                            + "\"auto_confirm\": true,"
                            + "\"external_id\": \"" + libm + "\","
                            + "\"credit_party_identifier\": {"
                            + "\"mobile_number\": \"+" + dtone_mdn + "\""
                            + "}}";

            log.info("jsonbody==>" + jsonbody);

            OutputStream os = conn.getOutputStream();
            os.write(jsonbody.getBytes("UTF-8"));
            os.close();

            StringBuilder buffer = new StringBuilder();

            log.info(conn.getResponseCode() + ", " + conn.getResponseMessage());

            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str;// = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 釋放資源
            inputStream.close();
            conn.disconnect();

            String kkstr = buffer.toString();

            log.info("kkstr==>" + kkstr);
            log.info(conn.getResponseCode() + ", " + conn.getResponseMessage());

            double tid;
            if (201 == conn.getResponseCode()) {
                JSONObject jj = new JSONObject(kkstr);
                tid = jj.getDouble("id");
                NumberFormat nf = NumberFormat.getInstance();
                nf.setGroupingUsed(false);
                String ss_tid = String.valueOf(nf.format(tid));
                log.info("trasaction id ==>" + ss_tid);

                String ddate = jj.getString("confirmation_date");

                double wholesale_amount = 0;
                JSONObject prices = jj.getJSONObject("prices");
                JSONObject wholesale = prices.getJSONObject("wholesale");
                wholesale_amount = wholesale.getDouble("amount");
                
                log.info("ddate===>" + ddate);

                JSONObject status = jj.getJSONObject("status");
                String sstatus = status.toString();
                JSONObject xx = new JSONObject(sstatus);
                String message = xx.getString("message");
                log.info("message===>" + message);

                String code = "";
                try {
                    JSONObject pin = jj.getJSONObject("pin");
                    String kk = pin.toString();
                    JSONObject dd = new JSONObject(kk);
                    code = dd.getString("code");
                    log.info("pincode======>" + code);
                } catch (Exception ex) {
                    log.info(ex);
                }

                dtonebean.setTid(ss_tid);
                dtonebean.setMessage(message);
                dtonebean.setCode(code);
                dtonebean.setConfirmation_date(ddate);
                dtonebean.setWholesale(wholesale_amount);

                log.info("dtonebean.getTid====>" + dtonebean.getTid());
                log.info("dtonebean.getMessage()=====>" + dtonebean.getMessage());
                log.info("dtonebean.getConfirmation_date()=====>" + dtonebean.getConfirmation_date());
                log.info("dtonebean.getWholesale()=====>" + dtonebean.getWholesale());
                
                return dtonebean;
            }
        } catch (Exception ex) {
            log.info(ex);
        }
        return dtonebean;
    }

    public DTOneResultBean setCountryCodeAddBillValue(String apt_mdn, String dtone_mdn, int product_id, String elect_bill) {
//        String result = "-1";
        DTOneResultBean dtonebean = new DTOneResultBean();
        String new_apt_mdn = "+886" + apt_mdn.substring(1);

        try {
            SSLContext ctx = SSLContext.getInstance("TLSv1.2");
            ctx.init(null, null, new SecureRandom());
            //ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
            SSLContext.setDefault(ctx);

            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.31.64.19", 5901));
            URL kk_url = new URL(dtone_trans_url);
            String PROXY_FLAG = new ShareParm().PARM_SCT_PROXY_FLAG;
            HttpsURLConnection conn;
            if ("PROD".equals(PROXY_FLAG)) {
                conn = (HttpsURLConnection) kk_url.openConnection(proxy);
            } else {
                log.info("TDE env, no proxy");
                conn = (HttpsURLConnection) kk_url.openConnection();
            }

            conn.setSSLSocketFactory(ctx.getSocketFactory());
            log.info("DTONE URL=>" + dtone_trans_url);

            conn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });

            String auth = username + ":" + password;

            String client_credentials_base64 = "";
            byte[] a = auth.getBytes();
            client_credentials_base64 = Base64Util.encode(a);
            log.info("client_credentials_base64===>" + client_credentials_base64);
            String authHeaderValue = "Basic " + new String(client_credentials_base64);

            conn.setConnectTimeout(50000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Authorization", authHeaderValue);
            conn.setRequestMethod("POST");

            SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
            Calendar nowDateTime = Calendar.getInstance();
            String libm = sdf15.format(nowDateTime.getTime());
            String jsonbody = "";

            jsonbody = "{"
                            + "\"debit_party_identifier\": {"
                            + "\"mobile_number\": \"" + new_apt_mdn + "\""
                            + "},"
                            + "\"sender\": {"
                            + "\"last_name\": \"" + last_name + "\","
                            + "\"first_name\": \"" + first_name + "\""
                            + "},"
                            + "\"product_id\": " + product_id + ","
                            + "\"auto_confirm\": true,"
                            + "\"external_id\": \"" + libm + "\","
                            + "\"credit_party_identifier\": {"
                            + "\"mobile_number\": \"+" + dtone_mdn + "\"" + ","
                            + "\"account_number\":\"" + elect_bill + "\""
                            + "}}";

            log.info("jsonbody==>" + jsonbody);

            OutputStream os = conn.getOutputStream();
            os.write(jsonbody.getBytes("UTF-8"));
            os.close();

            StringBuilder buffer = new StringBuilder();

            log.info(conn.getResponseCode() + ", " + conn.getResponseMessage());

            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str;// = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 釋放資源
            inputStream.close();
            conn.disconnect();

            String kkstr = buffer.toString();

            log.info("kkstr==>" + kkstr);
            log.info(conn.getResponseCode() + ", " + conn.getResponseMessage());

            double tid;
            if (201 == conn.getResponseCode()) {
                JSONObject jj = new JSONObject(kkstr);
                tid = jj.getDouble("id");
                NumberFormat nf = NumberFormat.getInstance();
                nf.setGroupingUsed(false);
                String ss_tid = String.valueOf(nf.format(tid));

//                JSONObject confirmation_date = jj.getJSONObject("confirmation_date");
//                String ddate = confirmation_date.toString();
                String ddate = jj.getString("confirmation_date");

                log.info("ddate===>" + ddate);

                String code = "";
                try {
                    JSONObject pin = jj.getJSONObject("pin");
                    String kk = pin.toString();
                    JSONObject dd = new JSONObject(kk);
                    code = dd.getString("code");
                    log.info("pincode======>" + code);
                } catch (Exception ex) {
                    log.info(ex);
                }

                JSONObject status = jj.getJSONObject("status");
                String sstatus = status.toString();
                JSONObject xx = new JSONObject(sstatus);
                String message = xx.getString("message");

                double wholesale_amount = 0;
                JSONObject prices = jj.getJSONObject("prices");
                JSONObject wholesale = prices.getJSONObject("wholesale");
                wholesale_amount = wholesale.getDouble("amount");

                dtonebean.setTid(ss_tid);
                dtonebean.setMessage(message);
                dtonebean.setCode(code);
                dtonebean.setConfirmation_date(ddate);
                dtonebean.setWholesale(wholesale_amount);

                log.info("dtonebean.getTid====>" + dtonebean.getTid());
                log.info("dtonebean.getMessage()=====>" + dtonebean.getMessage());
                log.info("dtonebean.getCode()=====>" + dtonebean.getCode());
                log.info("dtonebean.getConfirmation_date()=====>" + dtonebean.getConfirmation_date());
                log.info("dtonebean.getWholesale()=====>" + dtonebean.getWholesale());

                return dtonebean;
            }
        } catch (Exception ex) {
            log.info(ex);
        }
        return dtonebean;
    }

    public String getMobileNumberInfo(String mdn) {

        String result = "-1";
        try {

            SSLContext ctx = SSLContext.getInstance("TLSv1.2");
            ctx.init(null, null, new SecureRandom());
            //ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
            SSLContext.setDefault(ctx);

            //System.setProperty("https.protocols", "TLSv1"); 
//            System.setProperty("https.protocols","TLSv1.2,TLSv1.1,SSLv3"); 
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.31.64.19", 5901));

            String s_url = dtone_mobile_url + "+" + mdn;
            URL kk_url = new URL(s_url);

            String PROXY_FLAG = new ShareParm().PARM_SCT_PROXY_FLAG;
            HttpsURLConnection conn;
            if ("PROD".equals(PROXY_FLAG)) {
                conn = (HttpsURLConnection) kk_url.openConnection(proxy);
            } else {
                log.info("TDE env, no proxy");
                conn = (HttpsURLConnection) kk_url.openConnection();
            }
            conn.setSSLSocketFactory(ctx.getSocketFactory());

            log.info("DTONE URL=>" + s_url);

            conn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });

            String auth = username + ":" + password;

            String client_credentials_base64 = "";
            byte[] a = auth.getBytes();
            client_credentials_base64 = Base64Util.encode(a);
            String authHeaderValue = "Basic " + new String(client_credentials_base64);

            conn.setConnectTimeout(50000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Authorization", authHeaderValue);
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();

            StringBuilder buffer = new StringBuilder();
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str;// = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 釋放資源
            inputStream.close();
            conn.disconnect();

            String kkstr = buffer.toString();

            log.info("kkstr==>" + kkstr);

            if ("OK".equals(conn.getResponseMessage())) {
                JSONArray str_packages = new JSONArray(kkstr);

                for (int i = 0; i < str_packages.length(); i++) {

                    org.json.JSONObject c = str_packages.getJSONObject(i);
                    int id = c.getInt("id");
                    boolean identified = c.getBoolean("identified");
                    if (identified) {
                        String country_name = c.getString("name");
                        log.info("id==>" + id + ",country name==>" + country_name);
                        result = country_name;
                        return result;
                    }
                }
            }

            log.info(conn.getResponseCode() + ", " + conn.getResponseMessage());
            return result;

        } catch (IOException ex) {
            log.info(ex);
        } catch (KeyManagementException ex) {
            log.info(ex);
        } catch (NoSuchAlgorithmException ex) {
            log.info(ex);
        }
        return result;
    }

}
