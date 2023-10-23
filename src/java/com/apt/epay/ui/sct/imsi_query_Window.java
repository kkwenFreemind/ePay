/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.sct;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import com.apt.epay.share.ShareParm;
import com.epay.ejb.bean.EPAY_SCT_CARD;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import javax.servlet.ServletContext;
import org.json.JSONException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
//import java.util.StringTokenizer;

/**
 *
 * @author kevinchang
 */
public class imsi_query_Window extends Window {

    /*
    curl --write-out "%{http_code}\n" -x http://10.31.64.19:5901 -L https://sim.npnchina.com/api/v1/imsi_query
     */
    private static final Logger log = Logger.getLogger("EPAY");

    public void onClear() {
        Textbox text_imsi = (Textbox) getSpaceOwner().getFellow("text_imsi");
        Textbox text_status = (Textbox) getSpaceOwner().getFellow("text_status");
        Listbox listbox_imsi_log = (Listbox) this.getFellow("listbox_imsi_log");

        text_status.setValue("");
        try {
            AbstractComponent[] acs = (AbstractComponent[]) listbox_imsi_log.getChildren().toArray(new AbstractComponent[0]);

            for (AbstractComponent ac : acs) {
                if (ac instanceof Listhead) {
                    continue;
                }
                listbox_imsi_log.removeChild(ac);
            }
        } catch (Exception ex) {
        }
        text_imsi.setConstraint("");
        text_imsi.setValue("");
        text_imsi.setConstraint("no empty,/^[0-9]{15,16}/: CardId/IMSI為數字");

    }

    public void onCreate() {
        //ControlBusiness controlbusiness = (ControlBusiness) new ShareBean().getBusinessBean("controlbusiness", (ServletContext) getDesktop().getWebApp().getNativeContext());
//        Textbox text_imsi = (Textbox) getSpaceOwner().getFellow("text_imsi");
//        Textbox text_status = (Textbox) getSpaceOwner().getFellow("text_status");

    }

    public void query_imsi() throws Exception {

        Textbox text_imsi = (Textbox) getSpaceOwner().getFellow("text_imsi");
        Textbox text_status = (Textbox) getSpaceOwner().getFellow("text_status");
        Listbox listbox_imsi_log = (Listbox) this.getFellow("listbox_imsi_log");

        String strImsi = text_imsi.getValue();
//        strImsi = "89852070180038741487";
        int lenImsi = strImsi.length();
        String apn = getAPN(strImsi);
        log.info(strImsi + "(apn)====>" + apn);

        try {
            AbstractComponent[] acs = (AbstractComponent[]) listbox_imsi_log.getChildren().toArray(new AbstractComponent[0]);

            for (AbstractComponent ac : acs) {
                if (ac instanceof Listhead) {
                    continue;
                }
                listbox_imsi_log.removeChild(ac);
            }
        } catch (Exception ex) {
        }

        if (lenImsi < 20) {

            String json_imsi = "{\"imsi\":\"" + strImsi + "\"}";

            String q_imsi = text_imsi.getValue();
            JSONObject imsi = new JSONObject();
            imsi.put("imsi", q_imsi);

            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
            SSLContext.setDefault(ctx);

            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.31.64.19", 5901));

            //URL url = new URL("https://sim.npnchina.com/api/v1/imsi_query");
            URL url = new URL("https://sim.npnchina.com/api/v1/imsi_query");
            String PROXY_FLAG = new ShareParm().PARM_SCT_PROXY_FLAG;
            HttpsURLConnection conn;
            if ("PROD".equals(PROXY_FLAG)) {
                conn = (HttpsURLConnection) url.openConnection(proxy);
            } else {
                conn = (HttpsURLConnection) url.openConnection();
            }
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

            JSONObject jj = new JSONObject(kkstr);
            int result = jj.getInt("result");

            if (result == 0) {

                int status = jj.getInt("status");
                String kkstatus = getImsiStatus(status);
                text_status.setValue(kkstatus);
                JSONArray str_packages;

//            String kk = kkstr.replace("{", "").replace("}", "");
//            StringTokenizer st = new StringTokenizer(kk, ",");
//            String packageStr ="";
//            while (st.hasMoreTokens()) {
//                String parms = st.nextElement().toString();
//                String returnItem[] = parms.split("\":");
//                returnItem[0] = returnItem[0].replace("\"", "");
//                returnItem[1] = returnItem[1].replace("\"", "");
//                if ("packages".equalsIgnoreCase(returnItem[0])) {
//                    packageStr = returnItem[1];
//                }
//            }
//
//            if(!"null".equalsIgnoreCase(packageStr)){
                try {
                    str_packages = jj.getJSONArray("packages");
                    for (int i = 0; i < str_packages.length(); i++) {
                        org.json.JSONObject c = str_packages.getJSONObject(i);
                        String expiryTime = "";
                        String openTime = "";
                        String location = "";
                        double total = 0;
                        double today = 0;
                        double usage = 0;
                        int packageId = 0;
                        int productId = 0;
                        int ipackagestatus = 0;
                        if (!c.isNull("packageId")) {
                            packageId = c.getInt("packageId");
                        }

                        if (!c.isNull("productId")) {
                            productId = c.getInt("productId");
                        }
                        String clocation = "";

                        if (!c.isNull("total")) {
                            double byte_total = c.getDouble("total");
                            if (byte_total == 0) {
                                total = byte_total;
                            } else {
                                total = byte_total;
                            }
                        }
                        if (!c.isNull("today")) {
                            double byte_today = c.getDouble("today");
                            if (byte_today == 0) {
                                today = byte_today;
                            } else {
                                today = byte_today;
                            }
                        }
                        if (!c.isNull("usage")) {
                            double byte_usage = c.getDouble("usage");
                            if (byte_usage == 0) {
                                usage = byte_usage;
                            } else {
                                usage = byte_usage;
                            }
                        }
                        if (!c.isNull("status")) {
                            ipackagestatus = c.getInt("status");
                        }

                        int days = 0;
                        if (!c.isNull("days")) {
                            days = c.getInt("days");
                        }

                        if (!c.isNull("expiryTime")) {
                            expiryTime = c.getString("expiryTime");
                        }

                        if (!c.isNull("openTime")) {
                            openTime = c.getString("openTime");
                        }
                        if (!c.isNull("location")) {
                            clocation = c.getString("location");
                            location = sctutil.getCountryName(clocation);
                        }

                        BigDecimal b1 = new BigDecimal(today);
                        double r_today = b1.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                        BigDecimal b2 = new BigDecimal(usage);
                        double r_usage = b2.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                        log.info("clocation==>" + clocation);
                        log.info("location==>" + location);
                        log.info(i + ": packageId:" + packageId + "," + " productId:" + productId + "," + "total:" + total + ","
                                        + "today:" + today + "," + "usage:" + usage + "," + "location:" + location + ","
                                        + "status:" + status + "," + "days:" + days + "," + "expiryTime:" + expiryTime + "," + "openTime:" + openTime);

                        String productname = getProductName(clocation, days, total);
                        String packagestatus = getPackageStatus(ipackagestatus);
                        Listitem listitemtx_log = new Listitem();
                        new Listcell(String.valueOf(productname)).setParent(listitemtx_log);
                        new Listcell(String.valueOf(days)).setParent(listitemtx_log);
                        new Listcell(packagestatus).setParent(listitemtx_log);
                        new Listcell(apn).setParent(listitemtx_log);
                        new Listcell(String.valueOf(total)).setParent(listitemtx_log);
                        new Listcell(String.valueOf(r_usage)).setParent(listitemtx_log);
                        new Listcell(String.valueOf(r_today)).setParent(listitemtx_log);
                        new Listcell(openTime).setParent(listitemtx_log);
                        new Listcell(expiryTime).setParent(listitemtx_log);
                        new Listcell(location).setParent(listitemtx_log);
                        listitemtx_log.setParent(listbox_imsi_log);
                    }

                } catch (JSONException ex) {
                    log.info(ex);
                    Messagebox.show("SCT回傳Package資料為空值", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
                }
//            }else{
//                Messagebox.show("SCT回傳Package資料為空值", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
//            }
            } else {
                Messagebox.show("Error Result code:" + result, "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
            }
        } else {
            String tde_url = "http://203.160.67.148/api/mealList/603/";
            String prod_url = "http://203.160.67.149/cardManager/api/mealList/121/";

            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.31.64.19", 5901));
            String PROXY_FLAG = new ShareParm().PARM_SCT_PROXY_FLAG;
            URL url;
            HttpURLConnection conn;

            if ("PROD".equals(PROXY_FLAG)) {
                String newUrl = prod_url + strImsi;
                url = new URL(newUrl);
                conn = (HttpURLConnection) url.openConnection(proxy);
            } else {
                String newUrl = tde_url + strImsi;
                url = new URL(newUrl);
                conn = (HttpURLConnection) url.openConnection();
            }

            log.info("CTS URL=>" + url);
            //add request header
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("GET");

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

            JSONObject jj = new JSONObject(kkstr);
            int code = jj.getInt("code");
            log.info("Code===========>" + code);

            if (code == 1) {

                JSONArray str_packages;
                String kkstatus = "inactive";
                try {
                    str_packages = jj.getJSONArray("meal_list");
                    for (int i = 0; i < str_packages.length(); i++) {
                        org.json.JSONObject c = str_packages.getJSONObject(i);

                        String expiryTime = "";
                        String openTime = "";
                        String location = "";
                        String productname = "";
                        String packagestatus = "";
                        String total = "";
                        String today = "";
                        int usage = 0;

//                        int ipackagestatus = 0;
                        if (!c.isNull("expireTime")) {
                            expiryTime = c.getString("expireTime");
                            packagestatus = "active";
                            kkstatus = "active";
                        } else {
                            packagestatus = "inactive";
                        }

                        if (!c.isNull("effectTime")) {
                            openTime = c.getString("effectTime");
                        }

                        if (!c.isNull("BALANCE")) {
                            total = c.getString("BALANCE");
                        }

                        if (!c.isNull("usedFlow")) {
                            usage = c.getInt("usedFlow");
                        }

                        if (!c.isNull("VARVALUE")) {
                            productname = c.getString("VARVALUE");
                        }

                        int days = 0;
                        if (!c.isNull("days")) {
                            days = c.getInt("days");
                        }

                        log.info(i + ": total:" + total + ","
                                        + "today:" + today + "," + "usage:" + usage + "," + "location:" + location + ","
                                        + "status:" + "Active" + "," + "days:" + days + "," + "expiryTime:" + expiryTime + "," + "openTime:" + openTime);

                        Listitem listitemtx_log = new Listitem();
                        new Listcell(String.valueOf(productname)).setParent(listitemtx_log);
                        new Listcell(String.valueOf(days)).setParent(listitemtx_log);
                        new Listcell(packagestatus).setParent(listitemtx_log);
                        new Listcell(apn).setParent(listitemtx_log);
                        new Listcell(String.valueOf(total)).setParent(listitemtx_log);
                        new Listcell(String.valueOf(usage)).setParent(listitemtx_log);
                        new Listcell(String.valueOf(today)).setParent(listitemtx_log);
                        new Listcell(openTime).setParent(listitemtx_log);
                        new Listcell(expiryTime).setParent(listitemtx_log);
                        new Listcell(location).setParent(listitemtx_log);
                        listitemtx_log.setParent(listbox_imsi_log);
                    }
                    text_status.setValue(kkstatus);

                } catch (JSONException ex) {
                    log.info("JSONException==>" + ex);
                    Messagebox.show("回傳Package資料為空值", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
                } catch (WrongValueException ex) {
                    log.info("WrongValueException==>" + ex);
                    Messagebox.show("回傳Package資料為空值", "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
                }
            } else {
                Messagebox.show("Error Result code:" + code, "亞太電信", Messagebox.OK, Messagebox.INFORMATION);
            }

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

    private String getImsiStatus(int status) {
        String result = "";
        /*
        0: not opened
        1: inactive
        2: activated
        3: freeze (will be destroyed)
        4: destroyed
        9: locked (suspend)
         */
        if (status == 0) {
            result = "not opened";
        }
        if (status == 1) {
            result = "inactive";
        }
        if (status == 2) {
            result = "activated";
        }
        if (status == 3) {
            result = "freeze ";
        }
        if (status == 4) {
            result = "destroyed";
        }
        if (status == 9) {
            result = "locked (suspend)";
        }

        return result;
    }

    private String getPackageStatus(int status) {
        String result = "";
        /*
        Package status
        0: inactive
        1: activated
        2: expired
        3: closed
        4: closing
         */
        if (status == 0) {
            result = "inactive";
        }
        if (status == 1) {
            result = "activated";
        }
        if (status == 2) {
            result = "expired";
        }
        if (status == 3) {
            result = "closed";
        }
        if (status == 4) {
            result = "closing";
        }
        return result;
    }

    public String getkkCountryName(String code) {
        String result = "";
        String[] tokens = code.split(",");
        for (String token : tokens) {
            if ("81".equals(token)) {
                result = result + "日本/";
            } else if ("82".equals(token)) {
                result = result + "南韓/";
            } else if ("1".equals(token)) {
                result = result + "美加/";
            } else if ("86".equals(token)) {
                result = result + "中國/";
            } else if ("853".equals(token)) {
                result = result + "澳門/";
            } else if ("852".equals(token)) {
                result = result + "香港/";
            } else if ("886".equals(token)) {
                result = result + "台灣/";
            } else if ("EU".equals(token)) {
                result = result + "歐洲/";
            } else {
                result = result + "其他/";
            }
        }
        result = result.substring(0, result.length() - 1);
        return result;
    }

    private String getProductName(String code, int days, double mb_total) {
        String result = "暢玩卡";
        String kkcountry = getkkCountryName(code);
        double gb_total = mb_total / 1024;
        if (gb_total > 100) {
            result = result + "_" + kkcountry + days + "日" + "無限上網卡";
        } else {
            result = result + "_" + kkcountry + days + "日" + gb_total + "GB上網卡";

        }

        return result;
    }

    private String getAPN(String iccid) {
        String result = "";
        try {
            EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
            EPAY_SCT_CARD ks_sctcard;// =  new EPAY_SCT_CARD();
            ks_sctcard = epaybusinesscontroller.getSCTCardInfoByIccid(iccid);
            if (ks_sctcard == null) {
                result = "";
            } else {
                result = ks_sctcard.getApn();
                log.info("result===>" + result);
            }
        } catch (Exception ex) {
            log.info(ex);
        }
        return result;
    }
}
