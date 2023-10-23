<%-- 
    Document   : cpCreditCard
    Created on : 2014/10/21, ?? 10:43:14
    Author     : Booker Hsu
--%>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.security.Key" %>
<%@ page import="java.security.MessageDigest" %>
<%@ page import="javax.crypto.Cipher" %>
<%@ page import="javax.crypto.spec.SecretKeySpec" %>

<%
    request.setCharacterEncoding("UTF-8");
    String cpid = request.getParameter("cpid");
    String sid = request.getParameter("sid");
    String sidname = request.getParameter("sidname");
    String orderTotal = request.getParameter("price");
    String phone = request.getParameter("phone");
    
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    java.text.SimpleDateFormat sdf8 = new java.text.SimpleDateFormat("yyyyMMddHHmmss");

    String deskey = "25LTtRgN";
    String identifyCode = "1mJqG0A6";
    String receiptType = "3";
    String receiptTitle = "APT";
    String vatNo = "70771579";
    String receiptAddress = "??????99?";
    String contactName = "????";
    String contactPhone = "02-55567777";
    String contactCellPhone = phone; //"0921009024";
    String contactEmail = "Joy@hello.com.tw";
    String returnUrl = "http://localhost:8080/cpCreditTest/cpIbonResp.jsp";

    java.util.Calendar nowDateTime = java.util.Calendar.getInstance();
    String tradeDate = sdf8.format(nowDateTime.getTime());

    String libm = "BON" + tradeDate;
    String privateData = "libm:" + libm + ",serviceid:" + sid;

    StringBuffer originalParm = new StringBuffer();

    originalParm//.append("callerMerchantId=").append(callerId).append("&")
            .append("libm=").append(libm).append("&")
            .append("serviceid=").append(sid).append("&")
            .append("serviceName=").append(sidname).append("&")
            .append("txType=").append("3").append("&")
            .append("orderTotal=").append(orderTotal).append("&")
            .append("tradeDate=").append(tradeDate).append("&")
            .append("callerResUrl=").append(returnUrl).append("&")
            .append("invoiceType=").append(receiptType).append("&")
            .append("invoiceTitle=").append(receiptTitle).append("&")
            .append("uniform=").append(vatNo).append("&")
            .append("invoiceAddress=").append(receiptAddress).append("&")
            .append("invoiceContact=").append(contactName).append("&")
            .append("invoiceContactTel=").append(contactPhone).append("&")
            .append("invoiceContactMobilePhone=").append(contactCellPhone).append("&")
            .append("invoiceContactEmail=").append(contactEmail).append("&")
            .append("privateData=").append(privateData);
    
%>

<%--<%@page contentType="text/html" pageEncoding="UTF-8"%>--%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title></title>
    </head>
    <body>
        <form action="http://localhost:8080/CPECF/deposit/OnlineDeposit.zul" method="POST"> <!-- target="_blank"> -->
            <input type="hidden" id="system" name="system" value="<%=cpid%>" />
            <input type="hidden" id="data" name="data" value="<%= (new SecuredMsg(deskey,identifyCode)).encode(originalParm.toString())%>" /><br/>
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
    private String desParam = "&callerInMac=";
    
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
