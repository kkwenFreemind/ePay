/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.util;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

/**
 *
 * @author kevinchang
 */
public class Utilities {

    private final Logger log;//=

    public Utilities() {
        this.log = Logger.getLogger("EPAY");
    }

    public String checkPassword(String passwordStr) {
        String regexZ = "\\d*";
        String regexS = "[a-zA-Z]+";
        String regexT = "\\W+$";
        String regexZT = "\\D*";
        String regexST = "[\\d\\W]*";
        String regexZS = "\\w*";
        String regexZST = "[\\w\\W]*";

        if (passwordStr.matches(regexZ)) {
            return "弱";
        }
        if (passwordStr.matches(regexS)) {
            return "弱";
        }
        if (passwordStr.matches(regexT)) {
            return "弱";
        }
        if (passwordStr.matches(regexZT)) {
            return "中";
        }
        if (passwordStr.matches(regexST)) {
            return "中";
        }
        if (passwordStr.matches(regexZS)) {
            return "中";
        }
        if (passwordStr.matches(regexZST)) {
            return "強";
        }
        return passwordStr;

    }

    public String encrypt(String baseKey, String message) throws Exception {
        DESedeKeySpec dks = new DESedeKeySpec(baseKey.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey securekey = keyFactory.generateSecret(dks);

        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, securekey);
        byte[] b = cipher.doFinal(message.getBytes("UTF-8"));

        //Base64.Encoder encoder = Base64.getEncoder();
        //return encoder.encodeToString(b).replaceAll("\r", "").replaceAll("\n", "");
        return Base64Util.encode(b).replaceAll("\r", "").replaceAll("\n", "");
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
            log.info(ex);
        } catch (InvalidKeyException ex) {
            log.info(ex);
        } catch (NoSuchAlgorithmException ex) {
            log.info(ex);
        } catch (InvalidKeySpecException ex) {
            log.info(ex);
        } catch (BadPaddingException ex) {
            log.info(ex);
        } catch (IllegalBlockSizeException ex) {
            log.info(ex);
        } catch (NoSuchPaddingException ex) {
            log.info(ex);
        }
        String kk_result = "";
        try {
            kk_result = new String(retByte, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return kk_result;
    }

    public static String GetRandomString(int Len) {
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

    public static String GetRandomString6Digits(int Len) {
        String[] baseString = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
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

//    public static HttpResponse getHTTPResp(CloseableHttpClient httpClient, String url) throws IOException {
//        HttpResponse http_resp = null;
//        HttpPost post = new HttpPost(url);
//        http_resp = httpClient.execute(post);
//        return http_resp;
//    }
//    public static CloseableHttpResponse getHTTPResp(CloseableHttpClient httpClient, String url, String requestBody) throws IOException {
//        CloseableHttpResponse http_resp = null;
//        HttpPost post = new HttpPost(url);
//        post.addHeader("Content-type", "application/json;charset=utf-8");
//        StringEntity cntnt = new StringEntity(String.format("%s", new String(requestBody.getBytes("UTF-8"), "iso-8859-1")));
//        post.setEntity(cntnt);
//        http_resp = httpClient.execute(post);
//        return http_resp;
//    }
    public static boolean isWindows() {
        boolean isWin = false;
        int isWinIdx = System.getProperty("os.name").toLowerCase().indexOf("win");
        if (isWinIdx >= 0) {
            isWin = true;
        }
        return isWin;
    }

    public static String getNewActivationExpireDate(int sec) throws Exception {
        String tmpStr;// = "";
        String date_format = "yyyy-MM-dd HH:mm:ss";

        SimpleDateFormat sdf = new SimpleDateFormat(date_format);
        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        //calendar.setTimeInMillis(now);
        calendar.add(Calendar.MILLISECOND, +1 * (sec * 1000));
        tmpStr = sdf.format(calendar.getTime());
        return tmpStr;
    }

    public static String getCurrentDateByDateFormat(String dateFormat) throws Exception {
        String tmpStr;// = "";
        String date_format = dateFormat;

        SimpleDateFormat sdf = new SimpleDateFormat(date_format);
        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        tmpStr = sdf.format(calendar.getTime());
        return tmpStr;
    }

    public static Document parseDOM(Reader charArrayReader) throws DocumentException {
        Document document;// = null;
        SAXReader reader = new SAXReader();
        document = reader.read(charArrayReader);
        return document;
    }

    public static boolean isExpireTimeExpired(String expire_time) {
        if (expire_time.isEmpty() || expire_time.trim().equals("")) {
            return false;
        } else {
            expire_time = expire_time.substring(0, 10);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date expire_time_d;// = null;
            Date today_d;// = null;
            String today_s = getToday("yyyy-MM-dd");
            try {
                expire_time_d = sdf.parse(expire_time);
                today_d = sdf.parse(today_s);
                //if (today_d.compareTo(expire_time_d) <= 0) { // not expired.
                if (today_d.compareTo(expire_time_d) <= 0) { // not expired.
                    return false;
                } else {
                    return true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public static String getToday(String format) {
        Date date = new Date();
        return new SimpleDateFormat(format).format(date);
    }

    public static String getPastMonth(Date day, int interval) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
            Calendar c = Calendar.getInstance();
            c.setTime(day);
            c.add(Calendar.MONTH, interval);  //not sure
            return dateFormat.format(c.getTime());
        } catch (Exception exp) {
            return null;
        }
    }

    public static String getAmountIntoDecimalFormat(int amount, String decimalFormat) {
        // "#.##"
        String tmpStr = "";
        DecimalFormat mDecimalFormat = new DecimalFormat("#.##");
        tmpStr = mDecimalFormat.format((double) amount / 100);
        return tmpStr;
    }

    public static String getOpenIDNameByMemType(String mem_type, String openid_pkgs) {
        String tmpStr = "";
        HashMap pksHM = new HashMap<Integer, String>();
        if (openid_pkgs != null) {
            StringTokenizer st_pks_str = new StringTokenizer(openid_pkgs, ",");
            while (st_pks_str.hasMoreElements()) {
                String st_pk_str = st_pks_str.nextToken();
                StringTokenizer st_pk_str_sub = new StringTokenizer(st_pk_str, ":");
                while (st_pk_str_sub.hasMoreElements()) {
                    String st_pk_no = st_pk_str_sub.nextToken();
                    String st_pk_name = "";
                    if (st_pk_str_sub.hasMoreTokens()) {
                        st_pk_name = st_pk_str_sub.nextToken();
                    }
                    pksHM.put(st_pk_no, st_pk_name);
                }
            }
        }
        if (pksHM.containsKey(mem_type)) {
            tmpStr = pksHM.get(mem_type).toString();
        }
        return tmpStr;
    }

    public static String getStoreNameByTransType(String trans_type, String store_pkgs) {
        String tmpStr = "";
        HashMap pksHM = new HashMap<Integer, String>();
        if (store_pkgs != null) {
            StringTokenizer st_pks_str = new StringTokenizer(store_pkgs, ",");
            while (st_pks_str.hasMoreElements()) {
                String st_pk_str = st_pks_str.nextToken();
                StringTokenizer st_pk_str_sub = new StringTokenizer(st_pk_str, ":");
                while (st_pk_str_sub.hasMoreElements()) {
                    String st_pk_no = st_pk_str_sub.nextToken();
                    String st_pk_name = "";
                    if (st_pk_str_sub.hasMoreTokens()) {
                        st_pk_name = st_pk_str_sub.nextToken();
                    }
                    pksHM.put(st_pk_no, st_pk_name);
                }
            }
        }
        if (pksHM.containsKey(trans_type)) {
            tmpStr = pksHM.get(trans_type).toString();
        }
        return tmpStr;
    }
}
