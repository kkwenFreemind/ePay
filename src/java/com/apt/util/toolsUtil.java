package com.apt.util;

//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;
//import java.awt.Image;
//import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * <p>
 * Title: 通用工具類
 * </p>
 * <p>
 * Description: 常用工具的集合，用來處理常見問題，比如中文亂碼的方法等。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: Towery
 * </p>
 *
 * @author
 * @version 1.0
 */
public class toolsUtil {

    private static final char HEXCHAR[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };
    private static final String HEXINDEX = "0123456789abcdef          ABCDEF";

    public toolsUtil() {
        //
    }

    public static String toGmtString(Date date) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sd.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sd.format(date);
    }

    public String maskString(String str) {
        
        String result ="";
        if (str != null) {

            int strlen = str.length();

            if (str.length() == 2) {
                result= str.substring(0, 1) + "*";
            }
            if (str.length() == 3) {
                result= str.substring(0, 1) + "*" + str.substring(2, 3);
            }

            if (str.length() == 4) {
                result = str.substring(0, 2) + "*" + str.substring(3, 4);
            }

            if (str.length() == 10) {
                result = str.substring(0, 3) + "****" + str.substring(7, 10);
            }

            if (str.length() > 6) {
                result = str.substring(0, 3) + "*****" + str.substring(strlen - 3, strlen);
            } else {
                result= str.substring(0, 3) + "*****";
            }
        } else {
            return result;
        }
        return result;
    }
    
    /**
     * 檢查字串內是否有重複
     *
     * @param str:String 欲輸入的 String
     * @param delim:String 分隔符號
     * @return boolean 若有重複回傳true,否之false
     */
    public static boolean chkHasDuplication(String str, String delim) {
        boolean rtresult = false;
        StringTokenizer stk = new StringTokenizer(str, delim);
        HashMap hashmap = new HashMap();
        loop:
        while (stk.hasMoreTokens()) {
            String key = stk.nextToken();
            if (!hashmap.containsKey(key)) {
                hashmap.put(key, null);
            } else {
                rtresult = true;
                break loop;
            }
        }
        hashmap = null;
        return rtresult;
    }

    public static Iterator getIterator_ByList(List list) {
        Iterator rtresult = null;
        if (list != null && !list.isEmpty()) {
            rtresult = list.iterator();
        }
        return rtresult;
    }

    public static String getStrByInt(int i, int length) {
        String rtresult = null;
        String tmpStr = String.valueOf(i);
        for (int idx = tmpStr.length(); idx < length; idx++) {
            tmpStr = "0" + tmpStr;
        }
        rtresult = tmpStr;
        return rtresult;
    }

    public static byte[] InputStreamToByteArray(InputStream input) throws Exception {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] data = new byte[1];
        byte[] rtresult = null;
        while (input.read(data, 0, 1) != -1) {
            byteout.write(data, 0, 1);
        }
        rtresult = byteout.toByteArray();

        return rtresult;
    }

    public static HashMap chkHasDuplication(List list_str) {

        HashMap map_duplication = null;
        HashMap hashmap = null;
        if (list_str != null) {
            Iterator it_str = list_str.iterator();

            hashmap = new HashMap();
            map_duplication = new HashMap();
            while (it_str.hasNext()) {
                String key = (String) it_str.next();
                int count = 1;
                if (!hashmap.containsKey(key)) {
                    hashmap.put(key, null);

                } else {
                    if (map_duplication.containsKey(key)) {
                        count = (Integer) map_duplication.get(key);
                    }
                    count++;

                    map_duplication.put(key, count);

                }
            }
            //     hashmap = null;
        }
        return map_duplication;
    }

    /**
     * 將轉為byte[]方式輸出
     *
     * @param hexStr:String 欲輸入的Hex String
     * @return byte[]
     */
    public static byte[] hexStrToBytes(String hexStr) {
        hexStr = hexStr.toLowerCase();
        //s = s.substring(3,s.length()); // Remove 'hex'
        byte[] bts = new byte[hexStr.length() / 2];
        for (int i = 0; i < bts.length; i++) {
            String src = hexStr.substring(2 * i, 2 * i + 2);
            bts[i] = (byte) Integer.parseInt(src, 16);
        }
        return bts;
    }

    public static byte[] my_int_to_bb_le(int myInteger, int allocate) {
        return ByteBuffer.allocate(allocate).order(ByteOrder.LITTLE_ENDIAN).putInt(myInteger).array();
    }

    public static int my_bb_to_int_le(byte[] byteBarray) {
        return ByteBuffer.wrap(byteBarray).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public static byte[] my_int_to_bb_be(int myInteger, int allocat) {
        return ByteBuffer.allocate(allocat).order(ByteOrder.BIG_ENDIAN).putInt(myInteger).array();
    }

    public static int my_bb_to_int_be(byte[] byteBarray) {
        return ByteBuffer.wrap(byteBarray).order(ByteOrder.BIG_ENDIAN).getInt();
    }

    public static byte[] my_bb_to_bb_be(byte[] byteBarray, int allocat) {
        return ByteBuffer.allocate(allocat).order(ByteOrder.BIG_ENDIAN).put(byteBarray).array();
    }

    public static byte[] my_bb_to_bb_le(byte[] byteBarray, int allocat) {
        return ByteBuffer.allocate(allocat).order(ByteOrder.LITTLE_ENDIAN).put(byteBarray).array();
    }

    /**
     * (2005/7/28) 說明：轉成中文全型字
     *
     * @author jonz
     * @param s
     * @return
     */
    public static String toChanisesFullChar(String s) {
        if (s == null || s.equals("")) {
            return "";
        }

        char[] ca = s.toCharArray();
        for (int i = 0; i < ca.length; i++) {
            if (ca[i] > '\200') {
                continue;
            }      //超過這個應該都是中文字了…      
            if (ca[i] == 32) {
                ca[i] = (char) 12288;
                continue;
            }  //半型空白轉成全型空白
            if (Character.isLetterOrDigit(ca[i])) {
                ca[i] = (char) (ca[i] + 65248);
                continue;
            }  //是有定義的字、數字及符號

            ca[i] = (char) 12288;  //其它不合要求的，全部轉成全型空白。
        }

        return String.valueOf(ca);
    }

    /**
     * 將byte[]轉為String方式輸出
     *
     * @param bs:byte[] 欲輸入的byte array
     * @return String
     */
    public static String toHexStr(byte[] bs) {
        String ansStr = "";
        for (byte b : bs) {
            String bStr = Integer.toHexString(b & 0xFF);
            if (bStr.length() == 1) {
                bStr = "0" + bStr;
            }
            ansStr += bStr;

        }
        return ansStr;
    }

    /**
     * 將檔案轉為Byte Array方式輸出
     *
     * @param file 欲輸入的檔案路徑
     * @return byte array byte[]
     */
    public static byte[] fileToByteArray(String file) {
        byte[] result = null;
        DataInputStream in = null;
        try {
            File f = new File(file);
            byte[] buffer = new byte[(int) f.length()];
            result = new byte[(int) f.length()];
            in = new DataInputStream(new FileInputStream(f));
            in.readFully(buffer);
            result = buffer;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) { /* ignore it */

            }
        }
        return result;
    }

    /**
     * 身分證檢查是否為合法
     *
     * @param id 欲檢查的身份證字串
     * @return 檢查後的結果 true/false
     */
    public static boolean VALID_ID(String id) {
        String v[][] = {{"A", "台北市"}, {"B", "台中市"}, {"C", "基隆市"},
        {"D", "台南市"}, {"E", "高雄市"}, {"F", "台北縣"}, {"G", "宜蘭縣"},
        {"H", "桃園縣"}, {"J", "新竹縣"}, {"K", "苗栗縣"}, {"L", "台中縣"},
        {"M", "南投縣"}, {"N", "彰化縣"}, {"P", "雲林縣"}, {"Q", "嘉義縣"},
        {"R", "台南縣"}, {"S", "高雄縣"}, {"T", "屏東縣"}, {"U", "花蓮縣"},
        {"V", "台東縣"}, {"X", "澎湖縣"}, {"Y", "陽明山"}, {"W", "金門縣"},
        {"Z", "連江縣"}, {"I", "嘉義市"}, {"O", "新竹市"}
        };

        int inte = -1;
        try {
            String s1 = String.valueOf(Character.toUpperCase(id.charAt(0)));
            for (int i = 0; i < 26; i++) {
                if (s1.compareTo(v[i][0]) == 0) {
                    inte = i;
                }
            }
            int total = 0;
            int all[] = new int[11];
            String E = String.valueOf(inte + 10);
            int E1 = Integer.parseInt(String.valueOf(E.charAt(0)));
            int E2 = Integer.parseInt(String.valueOf(E.charAt(1)));
            all[0] = E1;
            all[1] = E2;

            for (int j = 2; j <= 10; j++) {
                all[j] = Integer.parseInt(String.valueOf(id.charAt(j - 1)));
            }
            for (int k = 1; k <= 9; k++) {
                total += all[k] * (10 - k);
            }
            total += all[0] + all[10];
            if (total % 10 == 0) {
                return true;
            }
        } catch (Exception ee) {
        }
        return false;
    }
//    public static boolean VALID_ID(String id) {
//        boolean retresult = false;
//        int[] num = new int[10];
//        int[] rdd = {10, 11, 12, 13, 14, 15, 16, 17, 34, 18, 19, 20, 21, 22, 35, 23, 24, 25, 26, 27, 28, 29, 32, 30, 31, 33};
//        id = id.toUpperCase();
//        try {
//            if (id.charAt(0) < 'A' || id.charAt(0) > 'Z') {
//                System.out.println("第一個字錯誤!!");
//                retresult = false;
//            }
//            if (id.charAt(1) != '1' && id.charAt(1) != '2') {
//                System.out.println("第二個字錯誤!!");
//                retresult = false;
//            }
//
//            for (int i = 1; i < 10; i++) {
//                if (id.charAt(i) < '0' || id.charAt(i) > '9') {
//                    System.out.println("輸入錯誤!!");
//                    retresult = false;
//                }
//            }
//            for (int i = 1; i < 10; i++) {
//                num[i] = (id.charAt(i) - '0');
//            }
//            num[0] = rdd[id.charAt(0) - 'A'];
//            int sum = ((int) num[0] / 10 + (num[0] % 10) * 9);
//            for (int i = 0; i < 8; i++) {
//                sum += num[i + 1] * (8 - i);
//            }
//            if (10 - sum % 10 == num[9]) {
//                System.out.println("身分證號正確");
//                retresult = true;
//            } else {
//                System.out.println("身分證號錯誤");
//                retresult = false;
//            }
//        } catch (Exception ex) {
//            retresult = false;
//        }
//        return retresult;
//    }

    /**
     * 字串替換，將 source 中的 oldString 全部換成 newString
     *
     * @param source 源字串
     * @param key 檢查的key
     * @param value 替換的value
     * @return 替換後的字串
     */
    public static String ReplaceStr(String source, String key, String value) {
        if (source.indexOf(key) != -1) {
            source = source.replace(key, value);
        } else {
            return source;
        }
        return ReplaceStr(source, key, value);
    }

    /**
     * 字串替換，將 source 中的 oldString 全部換成 newString
     *
     * @param source 源字串
     * @param oldString 老的字串
     * @param newString 新的字串
     * @return 替換後的字串
     */
    public static String Replace(String source, String oldString,
            String newString) {
        StringBuffer output = new StringBuffer();
        int lengthOfSource = source.length(); // 源字串長度

        int lengthOfOld = oldString.length(); // 老字串長度

        int posStart = 0; // 開始搜索位置

        int pos; // 搜索到老字串的位置

        while ((pos = source.indexOf(oldString, posStart)) >= 0) {
            output.append(source.substring(posStart, pos));
            output.append(newString);
            posStart = pos + lengthOfOld;
        }
        if (posStart < lengthOfSource) {
            output.append(source.substring(posStart));
        }
        return output.toString();
    }

    /**
     * 轉換SQL中的特殊符號，比如將單引號"'"替換為"''"，以免產生SQLException
     *
     * @param sqlstr 原SQL
     * @return 處理後的SQL
     */
    public static String toSql(String sqlstr) {
        String strsql = sqlstr;
        if (strsql == null) {
            return "";
        }
        strsql = Replace(strsql, "'", "''");
        return strsql;
    }

    /**
     * 將ISO8859_1編碼的字串轉化為BIG5編碼的字串，主要用來處理中文顯示亂碼的問題
     *
     * @param ISO8859_1str 通過ISO8859_1編碼的字串
     * @return 通過BIG5編碼的字串
     */
    public String BIGString(String ISO8859_1str) {
        if (ISO8859_1str == null) {
            return "";
        } else {
            try {
                return new String(ISO8859_1str.getBytes("ISO8859_1"), "BIG5");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * 將ISO8859_1編碼的字串轉化為BIG5編碼的字串，主要用來處理中文顯示亂碼的問題
     *
     * @param ISO8859_1str 通過ISO8859_1編碼的字串
     * @return 通過BIG5編碼的字串
     */
    public static String BIG5FromISO8859_1(String ISO8859_1str) {
        if (ISO8859_1str == null) {
            return "";
        } else {
            try {
                return new String(ISO8859_1str.getBytes("ISO8859_1"), "BIG5");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * 清除換行符號
     *
     * @param Str
     *
     * @return Str
     */
    public String replace0D0AStr(String str) {
        String rtStr = "";
        try {
            rtStr = URLEncoder.encode(str, "utf-8");
            //   System.out.println("@@@@@@test:" + result);
            //   System.out.println("@@@@@@test_1:"+result.substring(result.indexOf("%0A")+3,result.indexOf("%0A")+12));
            if (rtStr.indexOf("%0D%0A") != -1) {
                rtStr = rtStr.replace("%0D%0A", "");
                //    System.out.println("@@@@@@test1");
            }
            if (rtStr.indexOf("%0A") != -1) {
                rtStr = rtStr.replace("%0A", "");
                //    System.out.println("@@@@@@test2");
            }
            rtStr = URLDecoder.decode(rtStr, "utf-8");
        } catch (Exception ex) {
            //   ex.printStackTrace();
        }
        return rtStr;
    }

    public String trim1(String str) {
        String str1 = "";
        if (str.substring(0, 1).equals("?")) {
            str1 = str.substring(1, str.length());
            System.out.println("str1=" + str1);
        } else {
            str1 = str;
        }
        return str1.trim();
    }

    public String dateformat(String str) {
        String day = str.substring(0, 2);
        String month = str.substring(3, 5);
        String year = str.substring(6, 10);
        str = year + "-" + month + "-" + day;
        return str;
    }

    public String tenformat(String str) {
        if (str.length() == 8) {
            String year = str.substring(0, 4);
            String month = "0" + str.substring(5, 6);
            String day = "0" + str.substring(7, 8);
            str = year + "-" + month + "-" + day;
        } else if (str.length() == 9) {
            if (str.substring(6, 7).equals("-")) {
                str = str.substring(0, 5) + "0" + str.substring(5, 9);
            } else {
                str = str.substring(0, 8) + "0" + str.substring(8, 9);
            }
        }
        return str;
    }

    /**
     * 計算兩個時間
     *
     * @param str 原時間，strsub，需減少的時間
     * @return 計算後的時間
     */
    public String subTime(String str, String strSub) {
        String rsTime = "";
        int hour = 0;
        int sec = 0;
        int secsub = 0;
        str = trim(str);
        strSub = trim(strSub);
        if (str.length() == 5) {
            hour = Integer.parseInt(str.substring(0, 2));
            sec = Integer.parseInt(str.substring(3, 5));
        } else if (str.length() == 4) {
            hour = Integer.parseInt(str.substring(0, 1));
            sec = Integer.parseInt(str.substring(2, 4));
        }
        if (trim(strSub).length() == 5) {
            secsub = Integer.parseInt(strSub.substring(0, 2));
        } else if (trim(strSub).length() == 4) {
            secsub = Integer.parseInt(strSub.substring(0, 1));
        }
        //int a = sec + secsub;
        if (sec < secsub) {
            //System.out.println("sub <");
            String jstr = Integer.toString(sec + 60 - secsub);
            String hstr = Integer.toString(hour - 1);
            //System.out.println("jstr="+jstr);
            //System.out.println("hstr="+hstr);
            if (jstr.length() == 1) {
                jstr = "0" + jstr;
            }
            if (hstr.length() == 1) {
                hstr = "0" + hstr;
            }
            rsTime = hstr + ":" + jstr;
        } else if (sec == secsub) {
            //System.out.println("sub =");
            String strh = Integer.toString(hour);
            //System.out.println("strh="+strh);
            if (strh.length() == 1) {
                strh = "0" + strh;
            }
            rsTime = strh + ":00";
        } else if (sec > secsub) {
            //System.out.println("sub >");
            String jstr = Integer.toString(sec - secsub);
            //System.out.println("jstr="+jstr);
            String hstr = Integer.toString(hour);
            //System.out.println("hstr="+hstr);
            if (jstr.length() == 1) {
                jstr = "0" + jstr;
            }
            if (hstr.length() == 1) {
                hstr = "0" + hstr;
            }
            rsTime = hstr + ":" + jstr;
        }
        return rsTime;
    }

    public String toSENDstr(String input) {
        String r = input;
        r = replace(r, "&", "");
        r = replace(r, "/", "|");
        r = replace(r, "\r", "");
        r = replace(r, "\n", "");
        r = replace(r, "'", "");
        r = replace(r, " ", "");
        return r;
    }

    public String replace(String str, String strOld, String strNew) {
        String r = str;
        if (str != null && strOld != null && strNew != null) {
            int idx = str.indexOf(strOld);
            if (idx != -1) {
                String strPre = "";
                r = "";
                String strSuf = str;
                for (; idx != -1; idx = strSuf.indexOf(strOld)) {
                    strPre = strSuf.substring(0, idx);
                    strSuf = strSuf.substring(idx + strOld.length());
                    r = r + strPre + strNew;
                }
                r = r + strSuf;
            }
        }
        return r;
    }

    /**
     * 計算兩個時間相差的分鐘數
     *
     * @param time1 string，time2，string
     * @return string
     */
    public String diffTime(String time1, String time2) {
        String rsTime = "";
        int hour = 0;
        int hour2 = 0;
        int sec = 0;
        int sec2 = 0;
        String str1 = trim(time1);
        String str2 = trim(time2);
        if (str1.length() == 5) {
            hour = Integer.parseInt(str1.substring(0, 2));
            sec = Integer.parseInt(str1.substring(3, 5));
        } else if (str1.length() == 4) {
            hour = Integer.parseInt(str1.substring(0, 1));
            sec = Integer.parseInt(str1.substring(2, 4));
        }
        if (str2.length() == 5) {
            hour2 = Integer.parseInt(str2.substring(0, 2));
            sec2 = Integer.parseInt(str2.substring(3, 5));
        } else if (str2.length() == 4) {
            hour2 = Integer.parseInt(str2.substring(0, 1));
            sec2 = Integer.parseInt(str2.substring(2, 4));
        }
        //int a = sec + secsub;
        if (sec < sec2) {
            //System.out.println("sub <");
            String jstr = Integer.toString(sec + 60 - sec2);
            if (jstr.length() == 1) {
                jstr = "0" + jstr;
            }
            if ((hour - 1) != hour2) {
                String hstr = Integer.toString(hour - 1 - hour2);
                if (hstr.length() == 1) {
                    hstr = "0" + hstr;
                }
                rsTime = hstr + ":" + jstr + ":00";
            } else {
                rsTime = jstr + ":00";
            }
        } else if (sec == sec2) {
            //System.out.println("sub =");
            if (hour != hour2) {
                String strh = Integer.toString(hour - hour2);
                //System.out.println("strh="+strh);
                if (strh.length() == 1) {
                    strh = "0" + strh;
                }
                rsTime = strh + ":00" + ":00";
            } else {
                rsTime = "00:00";
            }
        } else if (sec > sec2) {
            //System.out.println("sub >");
            String jstr = Integer.toString(sec - sec2);
            //System.out.println("jstr="+jstr);
            if (jstr.length() == 1) {
                jstr = "0" + jstr;
            }
            if (hour != hour2) {
                String hstr = Integer.toString(hour - hour2);
                //System.out.println("hstr="+hstr);
                if (hstr.length() == 1) {
                    hstr = "0" + hstr;
                }
                rsTime = hstr + ":" + jstr + ":00";
            } else {
                rsTime = jstr + ":00";
            }
        }
        return rsTime;
    }

    /**
     * 計算兩個時間
     *
     * @param str 原時間，stradd，需增加的時間
     * @return 計算後的時間
     */
    public String addTime(String str, String stradd) {
        String rsTime = "";
        int hour = 0;
        int sec = 0;
        int secadd = 0;
        int houradd = 0;
        str = trim(str);
        stradd = trim(stradd);
        if (str.length() == 5) {
            hour = Integer.parseInt(str.substring(0, 2));
            sec = Integer.parseInt(str.substring(3, 5));
        } else if (str.length() == 4) {
            hour = Integer.parseInt(str.substring(0, 1));
            sec = Integer.parseInt(str.substring(2, 4));
        }
        if (trim(stradd).length() == 5) {
            secadd = Integer.parseInt(stradd.substring(0, 2));
        } else if (trim(stradd).length() == 4) {
            secadd = Integer.parseInt(stradd.substring(0, 1));
        } else if (trim(stradd).length() == 7) {
            houradd = Integer.parseInt(stradd.substring(0, 1));
            secadd = Integer.parseInt(stradd.substring(2, 4));
        }
        int a = sec + secadd;
        if (a < 60) {
            String stra = Integer.toString(a);
            String strh = Integer.toString(hour + houradd);
            if (stra.length() == 1) {
                stra = "0" + stra;
            }
            if (strh.length() == 1) {
                strh = "0" + strh;
            } else if (Integer.parseInt(strh) > 24) {
                int h = Integer.parseInt(strh) / 24;
                strh = Integer.toString(h);
                if (h < 10) {
                    strh = "0" + Integer.toString(h);
                }
            }
            rsTime = strh + ":" + stra;
        } else if (a == 60) {
            String strh = Integer.toString(hour + houradd + 1);
            if (strh.length() == 1) {
                strh = "0" + strh;
            } else if (Integer.parseInt(strh) > 24) {
                int h = Integer.parseInt(strh) / 24;
                strh = Integer.toString(h);
                if (h < 10) {
                    strh = "0" + Integer.toString(h);
                }
            }
            rsTime = strh + ":00";
        } else if (a > 60) {
            int i = a / 60;
            int j = a % 60;
            String strj = Integer.toString(j);
            if (strj.length() == 1) {
                strj = "0" + strj;
            }
            String strh = Integer.toString(hour + houradd + i);
            if (strh.length() == 1) {
                strh = "0" + strh;
            } else if (Integer.parseInt(strh) > 24) {
                int h = Integer.parseInt(strh) / 24;
                strh = Integer.toString(h);
                if (h < 10) {
                    strh = "0" + Integer.toString(h);
                }
            }
            rsTime = strh + ":" + strj;
            if (j == 0) {
                rsTime = strh + ":00";
            }
        }
        return rsTime;
    }

    /**
     * 確認IP是否在合法網段內
     *
     * @param target_ip String 欲比對的IP
     * @param ip String 設定IP
     * @param submask String 子網路遮罩
     * @return boolean
     */
    public static boolean chkIpAuth(String target_ip, String ip, String submask) {
        boolean rtresult = false;
        if (target_ip != null && ip != null && submask != null) {
            StringTokenizer stk_ip = new StringTokenizer(ip, ".");
            String ip_sec_1 = stk_ip.nextToken();
            String ip_sec_2 = stk_ip.nextToken();
            String ip_sec_3 = stk_ip.nextToken();
            String ip_sec_4 = stk_ip.nextToken();
            StringTokenizer stk_submask = new StringTokenizer(submask, ".");
            String submask_sec_1 = stk_submask.nextToken();
            String submask_sec_2 = stk_submask.nextToken();
            String submask_sec_3 = stk_submask.nextToken();
            String submask_sec_4 = stk_submask.nextToken();

            StringTokenizer stk_target_ip = new StringTokenizer(target_ip, ".");
            String target_ip_sec_1 = stk_target_ip.nextToken();
            String target_ip_sec_2 = stk_target_ip.nextToken();
            String target_ip_sec_3 = stk_target_ip.nextToken();
            String target_ip_sec_4 = stk_target_ip.nextToken();

            rtresult = (Integer.valueOf(ip_sec_1) == (Integer.valueOf(target_ip_sec_1) & Integer.valueOf(submask_sec_1)))
                    && (Integer.valueOf(ip_sec_2) == (Integer.valueOf(target_ip_sec_2) & Integer.valueOf(submask_sec_2)))
                    && (Integer.valueOf(ip_sec_3) == (Integer.valueOf(target_ip_sec_3) & Integer.valueOf(submask_sec_3)))
                    && (Integer.valueOf(ip_sec_4) == (Integer.valueOf(target_ip_sec_4) & Integer.valueOf(submask_sec_4)));
        }
        return rtresult;
    }

    /**
     * 將UTF編碼的字串轉化為BIG5編碼的字串，主要用來處理中文顯示亂碼的問題
     *
     * @param UTF 通過UTF編碼的字串
     * @return 通過BIG5編碼的字串
     */
    public static String BIG5FromUTF(String UTF) {
        if (UTF == null) {
            return "";
        } else {
            try {
                return new String(UTF.getBytes("UTF-8"), "BIG5");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * 將BIG5編碼的字串轉化為UTF-8編碼的字串，主要用來處理中文顯示亂碼的問題
     *
     * @param BIG5 通過BIG5編碼的字串
     * @return 通過UTF-8編碼的字串
     */
    public static String UTFFromBIG5(String BIG5) {
        if (BIG5 == null) {
            return "";
        } else {
            try {
                return new String(BIG5.getBytes("BIG5"), "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static String BIGFromISO8859_1(String ISO8859_1) {
        if (ISO8859_1 == null) {
            return "";
        } else {
            try {
                return new String(ISO8859_1.getBytes("ISO8859_1"), "BIG5");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static String BIGFromUTF(String UTF) {
        if (UTF == null) {
            return "";
        } else {
            try {
                return new String(UTF.getBytes("UTF-8"), "BIG5");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * 將ISO8859_1編碼的字串轉化為UTF-8編碼的字串，主要用來處理中文顯示亂碼的問題
     *
     * @param ISO8859_1str 通過ISO8859_1編碼的字串
     * @return 通過UTF-8編碼的字串
     */
    public static String UTFFromISO8859_1(String ISO8859_1str) {
        return ISO8859_1str;
    }

    public static String UTFFromBIG(String BIG5) {
        if (BIG5 == null) {
            return "";
        } else {
            try {
                return new String(BIG5.getBytes("BIG5"), "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * 將UTF-8編碼的字串轉化為ISO8859_1編碼的字串，主要用來處理中文顯示亂碼的問題
     *
     * @param UTF 通過UTF編碼的字串
     * @return 通過ISO8859_1編碼的字串
     */
    public static String ISO8859_1FromUTF(String UTFstr) {
        if (UTFstr == null) {
            return "";
        } else {
            try {
                return new String(UTFstr.getBytes("UTF-8"), "ISO8859_1");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * 將BIG5編碼的字串轉化為ISO8859_1編碼的字串
     *
     * @param GBstr BIG5編碼的字串
     * @return ISO8859_1編碼的字串
     */
    public static String ISO8859_1String(String BIG5) {
        if (BIG5 == null) {
            return "";
        } else {
            try {
                return new String(BIG5.getBytes("BIG5"), "ISO8859_1");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * 將BIG5編碼的字串轉化為ISO8859_1編碼的字串
     *
     * @param GBstr BIG5編碼的字串
     * @return ISO8859_1編碼的字串
     */
    public String ISO8859_1FromBIG5(String BIG5) {
        if (BIG5 == null) {
            return "";
        } else {
            try {
                return new String(BIG5.getBytes("BIG5"), "ISO8859_1");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static String ISO8859_1FromBIG(String BIG5) {
        if (BIG5 == null) {
            return "";
        } else {
            try {
                return new String(BIG5.getBytes("BIG5"), "ISO8859_1");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * 去除字串兩端空格。
     *
     * @param str 需要處理的字串
     * @return 去掉了兩端空格的字串，如果str 為 null 則返回 ""
     */
    public static String trim(String str) {
        if (str != null) {
            return str.trim();
        } else {
            return "";
        }
    }

    public static String convBytes(byte[] bytes, boolean forHtml) {
        if (bytes == null) {
            return "bytes can not be null";
        }
        String nL = "\n";
        if (forHtml) {
            nL = "<br>";
        }
        StringBuffer sb = new StringBuffer(nL);
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            String bStr = Integer.toHexString(b & 0xFF).toUpperCase();
            if (bStr.length() < 2) {
                bStr = "0" + bStr;
            }
            sb.append(bStr);
            sb.append(' ');
            if (i % 10 == 9) {
                sb.append(nL);
            }
        }
        System.out.println("bytes:" + sb.toString());
        return sb.toString();
    }

    public static void printBytes(byte[] bytes) {
        if (bytes == null) {
            System.out.println("bytes can not be null");
            return;
        }
        StringBuffer sb = new StringBuffer("\n");
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            String bStr = Integer.toHexString(b & 0xFF).toUpperCase();
            if (bStr.length() < 2) {
                bStr = "0" + bStr;
            }
            sb.append(bStr);
            sb.append(' ');
            if (i % 10 == 9) {
                sb.append('\n');
            }
        }
        System.out.println("bytes:" + sb.toString());
    }

    public static String eatingBits(String rawData, int nBitToEat) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < rawData.length() - 3; i = i + 2) {
            String twoByteStr = rawData.charAt(i) + "" + rawData.charAt(i + 1) + rawData.charAt(i + 2) + rawData.charAt(i + 3);
            sb.append(getLeftShitBitForOneByte(twoByteStr, nBitToEat));
        }
        return sb.toString();
    }

    public static String getLeftShitBitForOneByte(String twoByteStr, int leftShiftNBits) {
        try {
            int i1 = Integer.parseInt(twoByteStr, 16);
            i1 <<= leftShiftNBits;
            i1 = i1 & 0xFF00;
            i1 >>= 8;
            return getHexStr(i1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Error";
    }

    public static String getHexStr(int i1) {
        String ans = Integer.toHexString(i1);
        while (ans.length() < 2) {
            ans = "0" + ans;
        }
        return ans.toUpperCase();
    }

    public static String getChineseStr(int dig) {
        String[] lettersarray = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};

        Random random = new Random();

        String rtResult = "";

        rtResult = lettersarray[dig];
        return rtResult;
    }

    public static String getRandStr(int len) {
        String[] lettersarray = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

        Random random = new Random();

        String rand = "";

        for (int i = 0; i < len; i++) {
            rand += random.nextInt(10) < 5 ? String.valueOf(random.nextInt(10)) : random.nextInt(10) < 5 ? lettersarray[random.nextInt(26)] : lettersarray[random.nextInt(26)].toUpperCase();
        }
        return rand;
    }

    public static String getRandStr(int len, long seed) {
        String[] lettersarray = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

        Random random = new Random(seed);

        String rand = "";

        for (int i = 0; i < len; i++) {
            rand += random.nextInt(10) < 5 ? String.valueOf(random.nextInt(10)) : random.nextInt(10) < 5 ? lettersarray[random.nextInt(26)] : lettersarray[random.nextInt(26)].toUpperCase();
        }
        return rand;
    }

    public static String getRandDig(int len, long seed) {
        String[] lettersarray = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

        Random random = new Random(seed);
        String rand = "";

        for (int i = 0; i < len; i++) {

            rand += random.nextInt(10);
        }
        return rand;
    }

    public static final String toStrUTF8(String ioStr) throws Exception {
        String rtStr;
        rtStr = new String(ioStr.getBytes("ISO-8859-1"), "UTF-8");
        return rtStr;
    }

    public static void toZipDir(File srcdir, File dstzipfile, int BUFFER) throws Exception {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(dstzipfile);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            //out.setMethod(ZipOutputStream.DEFLATED);
            byte data[] = new byte[BUFFER];
            // get a list of files from current directory

            String files[] = srcdir.list();

            for (int i = 0; i < files.length; i++) {

                System.out.println("Adding: " + files[i]);
                FileInputStream fi = new FileInputStream(srcdir.getAbsolutePath() + "/" + files[i]);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(files[i]);
                entry.setMethod(ZipEntry.DEFLATED);
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0,
                        BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void toZipFile(File srcfile, File dstzipfile, int BUFFER) throws Exception {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(dstzipfile);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            //out.setMethod(ZipOutputStream.DEFLATED);
            byte data[] = new byte[BUFFER];
            // get a list of files from current directory

            //   String files[] = f.list();
            //  for (int i=0; i<files.length; i++) {
            System.out.println("Adding: " + srcfile);
            FileInputStream fi = new FileInputStream(srcfile.getAbsolutePath());
            origin = new BufferedInputStream(fi, BUFFER);
            ZipEntry entry = new ZipEntry(srcfile.getName());
            entry.setMethod(ZipEntry.DEFLATED);
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0,
                    BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            origin.close();
            //  }
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public static void toUnZipFile(File srczipfile, File dstpath, int BUFFER) throws Exception {
        try {
            BufferedOutputStream dest = null;
            FileInputStream fis = new //   FileInputStream(argv[0]);
                    FileInputStream(srczipfile);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                System.out.println("Extracting: " + entry);
                int count;
                byte data[] = new byte[BUFFER];
                // write the files to the disk
                FileOutputStream fos = new FileOutputStream(dstpath.getAbsolutePath() + File.separator + entry.getName());

                dest = new BufferedOutputStream(fos, BUFFER);
                while ((count = zis.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, count);
                }
                dest.flush();
                dest.close();
            }
            zis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public static final ArrayList<String> sliptStrWithDelim(String str, String delim) throws Exception {
        StringTokenizer stk = new StringTokenizer(str, delim);
        ArrayList<String> rtresult = new ArrayList<String>();
        while (stk.hasMoreTokens()) {
            rtresult.add(stk.nextToken());
        }
        return rtresult;
    }

    /**
     * Method declaration
     *
     * @param Hex String
     * @return Get byte from Hex String
     */
    public static byte[] hexToByte(String hesString) {
        int hexLen = hesString.length() / 2;
        byte data[] = new byte[hexLen];

        int j = 0;
        for (int i = 0; i < hexLen; i++) {
            char c = hesString.charAt(j++);
            int n, b;
            n = HEXINDEX.indexOf(c);
            b = (n & 0xf) << 4;
            c = hesString.charAt(j++);
            n = HEXINDEX.indexOf(c);
            b += (n & 0xf);
            data[i] = (byte) b;
        }
        return data;
    }

    /**
     * Method declaration
     *
     * @param byte Array
     * @return Get Hex String from byte
     */
    static String byteToHex(byte byteArray[]) {
        int byteLen = byteArray.length;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteLen; i++) {
            int c = ((int) byteArray[i]) & 0xff;
            sb.append(HEXCHAR[c >> 4 & 0xf]);
            sb.append(HEXCHAR[c & 0xf]);
        }
        return sb.toString();
    }

    /**
     * Method declaration
     *
     * @param unicode String
     * @return Get Hex String from unicode String
     */
    static String unicodeToHexString(String unicodeString) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bout);
        try {
            out.writeUTF(unicodeString);
            out.close();
            bout.close();
        } catch (IOException e) {
            return null;
        }
        return byteToHex(bout.toByteArray());
    }

    /**
     * Method declaration
     *
     * @param Hex String
     * @return Get Unicode String from Hex String
     */
    public static String hexStringToUnicode(String hexString) {
        byte[] byteArray = hexToByte(hexString);
        ByteArrayInputStream bin = new ByteArrayInputStream(byteArray);
        DataInputStream in = new DataInputStream(bin);
        try {
            return in.readUTF();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Method declaration
     *
     * @param unicode String
     * @return Get Ascii String from unicode String
     */
    public static String unicodeToAscii(String unicodeString) {
        if (unicodeString == null || unicodeString.equals("")) {
            return unicodeString;
        }
        int ucLen = unicodeString.length();
        StringBuffer sb = new StringBuffer(ucLen);
        for (int i = 0; i < ucLen; i++) {
            char c = unicodeString.charAt(i);
            if (c == '\\') {
                if (i < ucLen - 1 && unicodeString.charAt(i + 1) == 'u') {
                    sb.append(c);    			// encode the \ as unicode, so 'u' is ignored
                    sb.append("u005c");    // splited so the source code is not changed...
                } else {
                    sb.append(c);
                }
            } else if ((c >= 0x0020) && (c <= 0x007f)) {
                sb.append(c);    // this is 99%
            } else {
                sb.append("\\u");
                sb.append(HEXCHAR[(c >> 12) & 0xf]);
                sb.append(HEXCHAR[(c >> 8) & 0xf]);
                sb.append(HEXCHAR[(c >> 4) & 0xf]);
                sb.append(HEXCHAR[c & 0xf]);
            }
        }
        return sb.toString();
    }

    /**
     * Method declaration
     *
     * @param Ascii String
     * @return Get unicode String from Ascii String
     */
    public static String asciiToUnicode(String asciiString) {
        if (asciiString == null || asciiString.indexOf("\\u") == -1) {
            return asciiString;
        }
        int aiLen = asciiString.length();
        char b[] = new char[aiLen];
        int j = 0;
        for (int i = 0; i < aiLen; i++) {
            char c = asciiString.charAt(i);
            if (c != '\\' || i == aiLen - 1) {
                b[j++] = c;
            } else {
                c = asciiString.charAt(++i);
                if (c != 'u' || i == aiLen - 1) {
                    b[j++] = '\\';
                    b[j++] = c;
                } else {
                    int k = (HEXINDEX.indexOf(asciiString.charAt(++i)) & 0xf) << 12;
                    k += (HEXINDEX.indexOf(asciiString.charAt(++i)) & 0xf) << 8;
                    k += (HEXINDEX.indexOf(asciiString.charAt(++i)) & 0xf) << 4;
                    k += (HEXINDEX.indexOf(asciiString.charAt(++i)) & 0xf);
                    b[j++] = (char) k;
                }
            }
        }
        return new String(b, 0, j);
    }

    /**
     * Method declaration
     *
     * @param InputStream object
     * @return Get String from Input Stream object
     * @throws SQLException
     */
    public static String InputStreamToString(InputStream is) throws SQLException {
        InputStreamReader in = new InputStreamReader(is);
        StringWriter write = new StringWriter();
        int blocksize = 8 * 1024;    // todo: is this a good value?
        char buffer[] = new char[blocksize];
        try {
            while (true) {
                int l = in.read(buffer, 0, blocksize);
                if (l == -1) {
                    break;
                }
                write.write(buffer, 0, l);
            }
            write.close();
            is.close();
        } catch (IOException e) {
            //throw Trace.error(Trace.INPUTSTREAM_ERROR, e.getMessage());
        }
        return write.toString();
    }

    /**
     * Method 轉換成JPEG圖檔
     *
     * @param sourceFile File
     * @param toFile File
     * @param size int
     * @return
     */
//    public static void convertToJpeg(File sourceFile, File toFile, int divisor) {
//        FileOutputStream out = null;
//        try {
//            Image src = javax.imageio.ImageIO.read(sourceFile);                     //構造Image物件
//            int wideth = src.getWidth(null);                                     //得到源圖寬
//            int height = src.getHeight(null);                                    //得到源圖長
//
//            BufferedImage tag = new BufferedImage(wideth / divisor, height / divisor, BufferedImage.TYPE_INT_RGB);
//            tag.getGraphics().drawImage(src, 0, 0, wideth / divisor, height / divisor, null);       //繪製縮小後的圖
//            out = new FileOutputStream(toFile);          //輸出到檔流
//            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//            encoder.encode(tag);                                               //近JPEG編碼
//            out.flush();
//        } catch (Exception ex) {
//        } finally {
//            try {
//                out.close();
//            } catch (Exception ex) {
//            }
//        }
//    }
    /**
     * Method 轉換成JPEG圖檔
     *
     * @param sourceFile File
     * @param toFile File
     * @param size int
     * @return
     */
//    public static void convertToJpeg_ToSize(File sourceFile, File toFile, int max_size) {
//        FileOutputStream out = null;
//        try {
//            Image src = javax.imageio.ImageIO.read(sourceFile);                     //構造Image物件
//            int wideth = src.getWidth(null);                                     //得到源圖寬
//            int height = src.getHeight(null);                                  //得到源圖長
//            int divisor = 1;
//            if (wideth > height) {
//                divisor = wideth / max_size;
//            } else {
//                divisor = height / max_size;
//            }
//            BufferedImage tag = new BufferedImage(wideth / divisor, height / divisor, BufferedImage.TYPE_INT_RGB);
//            tag.getGraphics().drawImage(src, 0, 0, wideth / divisor, height / divisor, null);       //繪製縮小後的圖
//            out = new FileOutputStream(toFile);          //輸出到檔流
//            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//            encoder.encode(tag);                                               //近JPEG編碼
//            out.flush();
//        } catch (Exception ex) {
//        } finally {
//            try {
//                out.close();
//            } catch (Exception ex) {
//            }
//        }
//    }
    /**
     * 依照輸入的編碼確認是否可轉碼
     *
     * @param inStr String 欲檢查之字串
     * @param charSet String 欲轉之字碼
     * @param isSuccess boolean 取得正向或負向結果
     * @return List<String> 回傳結果字串集
     */
    public static String chkCanEncode(String inStr, String charSet, boolean isSuccess) {
        String rtresult = null;
        if (inStr != null && !inStr.isEmpty()) {

            try {
                StringBuilder buf = new StringBuilder(inStr.length());

                CharsetEncoder enc = Charset.forName(charSet).newEncoder();

                for (int idx = 0; idx < inStr.length(); idx++) {

                    char ch = inStr.charAt(idx);
                    if (enc.canEncode(ch)) {

                        if (isSuccess) {
                            buf.append(ch).append(';');
                        }
                    } else {
                        //    buf.append("&#").append((int) ch).append(';');
                        if (!isSuccess) {
                            buf.append(ch).append(';');
                        }
                    }
                }
                rtresult = buf.toString();
                if (rtresult.isEmpty()) {
                    rtresult = null;
                }
            } catch (Exception ex) {
                rtresult = null;
            }

        }
        return rtresult;
    }

    /**
     * Method 轉換成JPEG圖檔
     *
     * @param sourceFile File
     * @param toFile File
     * @param size int
     * @return
     */
//    public static byte[] convertToJpeg_ToSize(byte[] bytes_img, int max_size) {
//        ByteArrayOutputStream baos = null;
//        byte[] rtresult = null;
//        try {
//            ByteArrayInputStream bais = new ByteArrayInputStream(bytes_img);
//            Image src = javax.imageio.ImageIO.read(bais);                     //構造Image物件
//            int wideth = src.getWidth(null);                                     //得到源圖寬
//            int height = src.getHeight(null);                                  //得到源圖長
//            int divisor = 1;
//            if (wideth > height) {
//                divisor = wideth / max_size;
//            } else {
//                divisor = height / max_size;
//            }
//            BufferedImage tag = new BufferedImage(wideth / divisor, height / divisor, BufferedImage.TYPE_INT_RGB);
//            tag.getGraphics().drawImage(src, 0, 0, wideth / divisor, height / divisor, null);       //繪製縮小後的圖
//            baos = new ByteArrayOutputStream();          //輸出到檔流
//            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(baos);
//            encoder.encode(tag);                                               //近JPEG編碼
//            baos.flush();
//            rtresult = baos.toByteArray();
//        } catch (Exception ex) {
//        } finally {
//            try {
//                baos.close();
//            } catch (Exception ex) {
//            }
//        }
//        return rtresult;
//    }
    public static boolean isFileCharSetUTF8(File file) {
        boolean rtResult = false;
        try {
            java.io.InputStream ios = new java.io.FileInputStream(file);
            byte[] b = new byte[3];
            ios.read(b);
            ios.close();
            if (b[0] == -17 && b[1] == -69 && b[2] == -65) {
                System.out.println(file.getName() + "編碼為UTF-8");
                rtResult = true;
            } else {
                System.out.println(file.getName() + "編碼不是UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rtResult;
    }

    public static void main(String[] args) throws Exception {
//        System.out.println(getRandStr(10, System.currentTimeMillis()));
//        String dateformat = "yyyy-MM-dd 02:mm:ss";
//        System.out.println("@@@:" + dateformat.indexOf("HH"));
//        String aaaa = "ryanchen@aptg.com.tw;";
//        List list = sliptStrWithDelim(aaaa, ";");
//        Iterator it = list.iterator();
//        while (it.hasNext()) {
//            System.out.println("@@@@a:" + it.next());
//        }
//        System.out.println("@@@@@:" + URLDecoder.decode(new String(hexToByte("436f6e74656e743d253343526571756573742533452530412530392533435375626a65637425334558534d532b4150492543332541352543322538442543322542332543332541362543322539392543322538322543332541372543322542302543322541312543332541382543322541382543322538412543332541372543322539392543322542432543332541392543322538302543322538312532383230313031313131313133312532392533432532465375626a65637425334525304125303925334352657472792533455925334325324652657472792533452530412530392533434175746f53706c69742533454e2533432532464175746f53706c697425334525304125303925334353746f704461746554696d6525334532303130313131313131333625334325324653746f704461746554696d652533452530412530392533434d657373616765253345254333254135254332254243254332254235254333254135254332253835254332253838254333254137254332253934254332253946254333254146254332254243254332253843254333254136254332253838254332253931254333254136254332253938254332254146254333254139254332253930254332253938254333254136254332253839254332253844254333254138254332253832254332254232254333254146254332254243254332253843254333254135254332253839254332253942254333254138254332253838254332253837254333254136254332253832254332254138254333254137254332254234254332253834313125324631322b3134253341303025374531362533413030254333254135254332254239254332254142254333254136254332253838254332253931254333254136254332253942254332254234254333254136254332253846254332253942254333254136254332254230254332254234254333254139254332254245254332253844254333254139254332254130254332254144254333254137254332253941254332253834254333254139254332253936254332253842254333254139254332253937254332253943254333254146254332254243254332253843254333254135254332253943254332254230254333254135254332253944254332253830254333254136254332253938254332254146253238254333254136254332253936254332254230254333254135254332253844254332253937254333254136254332254238254332254146253239254333254136254332254231254332253930254333254136254332254144254332254132254333254135254332254238254332253832254333254135254332254237254332254135254333254135254332254242254332254241254333254138254332254237254332254146393225433325413825433225393925433225394639254333254136254332254138254332253933254333254134254332254239254332253842332532432543332541382543322541432543322539442543332541382543322541432543322539442533432532464d6573736167652533452530412530392533434d444e4c6973742533452530412530392530392533434d534953444e253345303938303633373931372533432532464d534953444e2533452530412530392533432532464d444e4c69737425334525304125334325324652657175657374253345253041")), "utf-8"));
//        System.out.println("@@@@@:" + URLDecoder.decode("%E7%9B%8A%E5%9C%92%E6%B1%BD%E8%BB%8A%E9%97%9C%E5%BF%83%E6%82%A8%3A%3C%3CCarNumber%3E%3E%E9%A9%97%E8%BB%8A%E5%B0%87%E5%88%B0%E6%9C%9F%2C%E8%AB%8B%E6%96%BC%3C%3CNextTestEnd%3E%3E%E5%89%8D%E5%9B%9E%E5%BB%A0%E9%A9%97%E8%BB%8A%2C%E4%BB%A5%E5%85%8D%E9%81%8E%E6%9C%9F%E5%96%B2%21%E8%AB%8B%E6%B4%BD%E9%9B%BB%E8%A9%B1%3A03-47907", "utf-8"));

        printBytes(hexStrToBytes("80001F"));
        System.out.println(byteToHex(DatatypeConverter.parseHexBinary("00000031")));
        System.out.println("@@@" + toChanisesFullChar("１２３"));
        System.out.println("@@@isFileCharSetUTF8:" + (isFileCharSetUTF8(new File("d:\\22新.txt"))));
    }

    
    public String getContractStatusDesc (int status){
        /*
        A.	合約狀態9：   正常9
B.	合約狀態25： 掛停25
C.	合約狀態27： 掛限27
D.	合約狀態26： 強停26
E.	正本未回31： 正本未回限話31
F.	到期雙停43： 到期雙停43
G.	合約終止12： 合約終止12

        */
        String result = "";
        if(status == 25) result = "【掛停25】";
        if(status == 27) result = "【掛限27】";
        if(status == 26) result = "【強停26】";
        if(status == 31) result = "【正本未回限話31】";
        if(status == 12) result = "【合約終止12】";        
        return result;
    }
    
}
