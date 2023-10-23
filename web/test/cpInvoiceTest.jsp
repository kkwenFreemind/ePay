<%-- 
    Document   : cpInvoiceTest
    Created on : 2014/11/03, 14:28:10
    Author     : Booker Hsu
--%>
<%@page import="java.util.Enumeration" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.security.Key" %>
<%@ page import="java.security.MessageDigest" %>
<%@ page import="javax.crypto.Cipher" %>
<%@ page import="javax.crypto.spec.SecretKeySpec" %>

<%
    request.setCharacterEncoding("UTF-8");
    String invoice = request.getParameter("invoice");
//    String status = request.getParameter("status");
//    String orderTotal = request.getParameter("orderTotal");
    
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    String deskey = "Tcqp97Yv";
    String identifyCode = "jBWC5E7K";

    java.util.Calendar nowDateTime = java.util.Calendar.getInstance();
    String responseTime = sdf.format(nowDateTime.getTime());

//    StringBuffer originalParm = new StringBuffer();

//    originalParm.append("libm=").append(libm).append("&")
//            .append("type=").append("CAP").append("&")
//            .append("batchNo=").append("").append("&")
//            .append("orderTotal=").append(orderTotal).append("&")
//            .append("actualAmt=").append(orderTotal).append("&")
//            .append("status=").append(status).append("&")
//            .append("responseMsg=").append("").append("&")
//            .append("responseTime=").append(responseTime);
    
%>

<%--<%@page contentType="text/html" pageEncoding="UTF-8"%>--%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title></title>
    </head>
    <body>
        <form action="http://localhost:8080/CPECF/api/invoice/A015" method="POST">
            <input type="hidden" id="encryptResponseMsg" name="encryptResponseMsg" value="<%= (new SecuredMsg(deskey,identifyCode)).encode("xmlResponse=" + invoice)%>" /><br/>
        </form>
        <script language="javascript">
            document.forms[0].submit();
        </script>
    </body>
</html>

<%!class SecuredMsg
{

    private Key	   key = null;
    private String identifyCode = "1mJqG0A6";
    private String md5Param = "&identifyCode=";
    private String characterSet = "utf-8";
    //to PaymentGateway use 'callerInMac', Receive from PaymentGateway use 'returnOutMac'
    private String desParam = "&returnOutMac=";
    
    public SecuredMsg(String deskey, String identifyCode)
    {
        this.key = new SecretKeySpec(deskey.getBytes(), "DES");
        this.identifyCode = identifyCode;
    }

    public String encode (String plainText) throws Exception
    {
        return encode (plainText, md5Param, desParam);
    }
    
    public String encode (String plainText, String md5Param, String desParam) throws Exception
    {
        String outMsg = null;
        Cipher cipher = null; 
        String md5Value = doMd5(plainText + md5Param + this.identifyCode);
        byte [] byteInMsg = null;
        byte [] byteEncrypted = null;

        cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        plainText = plainText + desParam + md5Value; 
        byteInMsg = plainText.getBytes(characterSet);
        byteEncrypted = cipher.doFinal(byteInMsg);
        outMsg = URLEncoder.encode(new String(byteEncrypted,"ISO-8859-1"),"ISO-8859-1"); 
        return outMsg;
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