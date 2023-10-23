package com.apt.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

/**
 *
 * @author Administrator
 */
public class Base64Util {

    public static byte[] base64Decode(String base64_Result) {
        Base64Util base64de = new Base64Util();
        byte[] base64_BaseData = null;
        try {
            base64_BaseData = base64de.decode(base64_Result);
        } catch (Exception e) {
        }
        return base64_BaseData;
    }

    public static String base64Encode(byte[] bytes) {
        Base64Util base64en = new Base64Util();
        String base64_Result = base64en.encode(bytes);
        return base64_Result;
    }

    public static char[] readChars(String string) {
        CharArrayWriter caw = new CharArrayWriter();
        try {
            Reader sr = new StringReader(string.trim());
            Reader in = new BufferedReader(sr);
            int count = 0;
            char[] buf = new char[16384];
            while ((count = in.read(buf)) != -1) {
                if (count > 0) {
                    caw.write(buf, 0, count);
                }
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return caw.toCharArray();
    }

    public static byte[] readBytes(File file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            InputStream fis = new FileInputStream(file);
            InputStream is = new BufferedInputStream(fis);
            int count = 0;
            byte[] buf = new byte[16384];
            while ((count = is.read(buf)) != -1) {
                if (count > 0) {
                    baos.write(buf, 0, count);
                }
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    static public String encode(byte[] data) {
        char[] out = new char[((data.length + 2) / 3) * 4];
        String rtStr;

        for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
            boolean quad = false;
            boolean trip = false;
            int val = (0xFF & (int) data[i]);
            val <<= 8;
            if ((i + 1) < data.length) {
                val |= (0xFF & (int) data[i + 1]);
                trip = true;
            }
            val <<= 8;
            if ((i + 2) < data.length) {
                val |= (0xFF & (int) data[i + 2]);
                quad = true;
            }
            out[index + 3] = alphabet[(quad ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 2] = alphabet[(trip ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 1] = alphabet[val & 0x3F];
            val >>= 6;
            out[index + 0] = alphabet[val & 0x3F];
        }
        rtStr = new String(out);
        return rtStr;
    }

    static public byte[] decode(String str) {

        char[] data = str.toCharArray();
        int tempLen = data.length;
        for (int ix = 0; ix < data.length; ix++) {
            if ((data[ix] > 255) || codes[data[ix]] < 0) {
                --tempLen; // 去除無效的字符

            }
        }
        int len = (tempLen / 4) * 3;
        if ((tempLen % 4) == 3) {
            len += 2;
        }
        if ((tempLen % 4) == 2) {
            len += 1;
        }
        byte[] out = new byte[len];
        int shift = 0;
        int accum = 0;
        int index = 0;

        for (int ix = 0; ix < data.length; ix++) {
            int value = (data[ix] > 255) ? -1 : codes[data[ix]];
            if (value >= 0) {
                accum <<= 6;
                shift += 6;
                accum |= value;
                if (shift >= 8) {
                    shift -= 8;
                    out[index++]
                            = (byte) ((accum >> shift) & 0xff);
                }
            }
        }

        if (index != out.length) {
            throw new Error("數據長度不一致(輸入了 " + index + "字?，但是系統指示有" + out.length + "字?)");
        }
        return out;
    }
    static private char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
    static private byte[] codes = new byte[256];

    static {
        for (int i = 0; i < 256; i++) {
            codes[i] = -1;
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            codes[i] = (byte) (i - 'A');
        }
        for (int i = 'a'; i <= 'z'; i++) {
            codes[i] = (byte) (26 + i - 'a');
        }
        for (int i = '0'; i <= '9'; i++) {
            codes[i] = (byte) (52 + i - '0');
        }
        codes['+'] = 62;
        codes['/'] = 63;
    }

    public static void main(String[] args) throws Exception {
        String key = new String("Spider");
        byte[] a = key.getBytes();
        String b = Base64Util.encode(a);
        System.out.println(b);

        byte[] c = Base64Util.decode(b);
        System.out.println(new String(c, "big5"));
    }
}
