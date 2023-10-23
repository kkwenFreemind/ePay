<%-- 
    Document   : cpCreditCardResp
    Created on : 2014/10/24, 下午 04:28:10
    Author     : Booker Hsu
--%>
<%@page import="java.util.Enumeration" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.security.Key" %>
<%@ page import="java.security.MessageDigest" %>
<%@ page import="javax.crypto.Cipher" %>
<%@ page import="javax.crypto.spec.SecretKeySpec" %>
<%@ page import="org.apache.log4j.Logger"%> 
<%
    String deskey = "25LTtRgN";
    String identifyCode = "1mJqG0A6";
    Logger log = Logger.getLogger(this.getClass());
    
    //request.setCharacterEncoding("UTF-8");
    Enumeration<String> parameterNames = request.getParameterNames();
    while (parameterNames.hasMoreElements()) {
        String paramName = parameterNames.nextElement();
        System.out.print(paramName);
        String[] paramValues = request.getParameterValues(paramName);
        for (int i = 0; i < paramValues.length; i++) {
            String paramValue = paramValues[i];
            System.out.print("\t" + paramValue);
            System.out.println();
        }
    }
    
    String encryptedMsg = request.getParameter("encryptedMsg");
    log.info((new SecuredMsg(deskey,identifyCode)).decode(encryptedMsg));
%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title></title>
    </head>
    <body>
    </body>
</html>

<%!class SecuredMsg
{

    private Key	   key = null;
    private String identifyCode = "1mJqG0A6";
    private String characterSet = "utf-8";
    private String md5Param = "&identifyCode=";
    //to PaymentGateway use 'callerInMac', Receive from PaymentGateway use 'returnOutMac'
    private String desParam = "&returnOutMac=";
    
    public SecuredMsg(String deskey, String identifyCode)
    {
        this.key = new SecretKeySpec(deskey.getBytes(), "DES");
        this.identifyCode = identifyCode;
        //this.characterSet = characterSet;
    }
    
    public String decode(String cipherText) throws Exception {
        return decode(cipherText, md5Param, desParam);
    }
    
    public String decode(String cipherText, String md5Param, String desParam) throws Exception {
        String outMsg = null;
        Cipher cipher = null;
        byte[] byteInMsg = null;
        byte[] byteDecrypted = null;
        int md5Index = 0;
        String result = null;
        cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);

        byteInMsg = URLDecoder.decode(cipherText, "ISO-8859-1").getBytes("ISO-8859-1");
        byteDecrypted = cipher.doFinal(byteInMsg);
        outMsg = new String(byteDecrypted, characterSet);
        md5Index = outMsg.indexOf(desParam);
        result = outMsg.substring(0, md5Index);
        return (doMd5(result + md5Param + this.identifyCode).equals(outMsg.substring(md5Index + desParam.length(), outMsg.length()))) ? outMsg + "&isMd5Match=true" : outMsg + "&isMd5Match=false";
    }

    private String doMd5(String msg) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        return bin2Hex(md5.digest(msg.getBytes(characterSet)));
    }

    private String bin2Hex(byte byteAry[]) {
        int bufLength = byteAry.length;
        StringBuffer strbuf = new StringBuffer(bufLength * 2);

        for (int i = 0; i < bufLength; i++) {
            if (((int) byteAry[i] & 0xff) < 0x10) {
                strbuf.append("0");
            }
            strbuf.append(Long.toString((int) byteAry[i] & 0xff, 16));
        }
        return strbuf.toString();
    }

//
}%>