/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.sct;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.log4j.Logger;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.json.JSONObject;
import org.json.JSONArray;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;

/**
 *
 * @author kevinchang
 */
public class cdr_query_day_Window extends Window {

    private static final Logger log = Logger.getLogger("EPAY");

    public void onClear() {
        Textbox text_imsi = (Textbox) getSpaceOwner().getFellow("text_imsi");
        Listbox listbox_cdr_log = (Listbox) this.getFellow("listbox_cdr_log");

        text_imsi.setConstraint("");
        text_imsi.setValue("");
        text_imsi.setConstraint("no empty,/^[0-9]{15,16}/: CardId/IMSI為數字");
        
        try {
            AbstractComponent[] acs = (AbstractComponent[]) listbox_cdr_log.getChildren().toArray(new AbstractComponent[0]);

            for (AbstractComponent ac : acs) {
                if (ac instanceof Listhead) {
                    continue;
                }
                listbox_cdr_log.removeChild(ac);
            }
        } catch (Exception ex) {
        }
    }

    public void onCreate() {
        Textbox text_imsi = (Textbox) getSpaceOwner().getFellow("text_imsi");
    }

    public void query_imsi() throws Exception {

        Textbox text_imsi = (Textbox) getSpaceOwner().getFellow("text_imsi");
        Listbox listbox_cdr_log = (Listbox) this.getFellow("listbox_cdr_log");

        String strImsi = text_imsi.getValue();
        String json_imsi = "{\"imsi\":\"" + strImsi + "\"}";

        try {
            AbstractComponent[] acs = (AbstractComponent[]) listbox_cdr_log.getChildren().toArray(new AbstractComponent[0]);

            for (AbstractComponent ac : acs) {
                if (ac instanceof Listhead) {
                    continue;
                }
                listbox_cdr_log.removeChild(ac);
            }
        } catch (Exception ex) {
        }

        String q_imsi = text_imsi.getValue();
        JSONObject imsi = new JSONObject();
        imsi.put("imsi", q_imsi);

        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
        SSLContext.setDefault(ctx);

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.31.64.19", 5901));

//        URL url = new URL("https://sim.npnchina.com/api/v1/imsi_query_cdr");   
        URL url = new URL("https://sim.npnchina.com/api/v1/imsi_query_cdr");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection(proxy);
        log.info("CTS URL=>" + url);

        conn.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        });

        SctUtils sctutil = new SctUtils();
        SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar nowDateTime = Calendar.getInstance();

        String Account = "APT";
        String NONCE = sctutil.getRandomString(8);
        String KK_TIMESTAMP = sdf15.format(nowDateTime.getTime());
        String POSTDATA = json_imsi;
        String KEY = "71c3b69a7957b782e2b40ad9df57afd5";
        String sign = Account + NONCE + KK_TIMESTAMP + POSTDATA + KEY;
        String md5_sign = sctutil.md5(sign);

        log.info("NONCE=====>" + NONCE);
        log.info("TIMESTAMP====>" + KK_TIMESTAMP);
        log.info("POSTDATA====>" + POSTDATA);
        log.info("SIGN========>" + sign);
        log.info("MD5=========>" + md5_sign);

        conn.setRequestProperty("SCT-ACCOUNT", Account);
        conn.setRequestProperty("SCT-NONCE", NONCE);
        conn.setRequestProperty("SCT-TIMESTAMP", KK_TIMESTAMP);
        conn.setRequestProperty("SCT-SIGN", md5_sign);
        conn.setConnectTimeout(5000);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");

        OutputStream os = conn.getOutputStream();
        os.write(imsi.toString().getBytes("UTF-8"));
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

        org.json.JSONObject jj = new org.json.JSONObject(kkstr);
        int result = jj.getInt("result");

        if (result == 0) {
            JSONArray str_packages = jj.getJSONArray("cdr");
            for (int i = 0; i < str_packages.length(); i++) {
                int usage = 0;
                String location = "";
                org.json.JSONObject c = str_packages.getJSONObject(i);
                String cdrtime = c.getString("time");

                if (!c.isNull("usage")) {
                    int byte_usage = c.getInt("usage");
                    usage = byte_usage / 1048576;
                }

                if (!c.isNull("location")) {
                    String code_location = c.getString("location");
                    location = sctutil.getCountryName(code_location);
                }

                log.info(i + ": cdrtime:" + cdrtime + "," + "usage:" + usage + "," + "location:" + location);
                Listitem listitemtx_log = new Listitem();
                new Listcell(cdrtime).setParent(listitemtx_log);
                new Listcell(String.valueOf(usage)).setParent(listitemtx_log);
                new Listcell(location).setParent(listitemtx_log);
                listitemtx_log.setParent(listbox_cdr_log);
            }
        } else {
            Messagebox.show("Error Result code:" + result, "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
        }

    }

    private static class DefaultTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

}
