<%-- 
    Document   : cpPaymentTest
    Created on : 2014/11/03, 12:28:10
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
    String libm = request.getParameter("libm");
    String status = request.getParameter("status");
    String errCode = request.getParameter("errCode");
    String errDesc = request.getParameter("errDesc");
    String orderTotal = request.getParameter("orderTotal");
    String installType = request.getParameter("installType");
    String install = request.getParameter("install");
    String firstAmt = request.getParameter("firstAmt");
    String eachAmt = request.getParameter("eachAmt");
    String installFee = request.getParameter("installFee");
    String privateData = request.getParameter("privateData");
    String authDate = request.getParameter("authDate");
    String returnOutMac = request.getParameter("returnOutMac");
    String isMd5Match = request.getParameter("isMd5Match");  
 
    
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    String deskey = "8v2r9Vb2";
    String identifyCode = "2a8eG95u";

    java.util.Calendar nowDateTime = java.util.Calendar.getInstance();
    String responseTime = sdf.format(nowDateTime.getTime());

    StringBuffer originalParm = new StringBuffer();

    originalParm.append("libm=").append(libm).append("&")
            .append("status=").append(status).append("&")
            .append("errCode=").append(errCode).append("&")
            .append("errDesc=").append(errDesc).append("&")
            .append("orderTotal=").append(orderTotal).append("&")
            .append("installType=").append(installType).append("&")
            .append("install=").append(install).append("&")
            .append("firstAmt=").append(firstAmt).append("&")
            .append("eachAmt=").append(eachAmt).append("&")
            .append("installFee=").append(installFee).append("&")
            .append("privateData=").append(privateData).append("&")
            .append("authDate=").append(authDate).append("&")
            .append("returnOutMac=").append(returnOutMac).append("&")
            .append("isMd5Match=").append(isMd5Match);
   System.out.println(originalParm.toString()); 
%>

<%--<%@page contentType="text/html" pageEncoding="UTF-8"%>--%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title></title>
    </head>
    <body>
        <form action="http://210.200.69.45/EPAY/api/TransactionRec" method="POST">
            <input type="hidden" id="data" name="encryptedMsg" value="<%= (new SecuredMsg(deskey,identifyCode)).encode(originalParm.toString())%>" /><br/>
        </form>
        <script language="javascript">
            document.forms[0].submit();
        </script>
    </body>
</html>

<%!class SecuredMsg
{

    private Key	   key = null;
    private String identifyCode = "2a8eG95u";
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
