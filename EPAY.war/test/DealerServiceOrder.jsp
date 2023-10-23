<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.apt.util.Utilities"%>
<%@page import="java.io.IOException"%>
<%@page import="org.apache.commons.httpclient.HttpException"%>
<%@page import="org.apache.commons.httpclient.HttpStatus"%>
<%@page import="org.apache.commons.httpclient.HttpClient"%>
<%@page import="org.apache.commons.httpclient.methods.PostMethod"%>
<%@page import="org.apache.commons.httpclient.methods.StringRequestEntity"%>
<%@page import="org.apache.commons.httpclient.methods.RequestEntity"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.util.Enumeration" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.security.Key" %>
<%@ page import="java.security.MessageDigest" %>
<%@ page import="javax.crypto.Cipher" %>
<%@ page import="javax.crypto.spec.SecretKeySpec" %>
<%@ page import="org.w3c.dom.*, javax.xml.parsers.*,java.io.ByteArrayInputStream,java.io.InputStream" %>


<%
    request.setCharacterEncoding("UTF-8");
    String CPID = request.getParameter("CPID");
    String MDN = request.getParameter("MDN");
    String SERVICEID = request.getParameter("SERVICEID");
    String PRICE = request.getParameter("PRICE");
    String SALESID = request.getParameter("SALESID");
    String STOREID = request.getParameter("STOREID");
    String STORENAME = request.getParameter("STORENAME");
    String APISRCID = request.getParameter("APISRCID");
    String PAYTOOL = request.getParameter("PAYTOOL");
    String PAYNAME = request.getParameter("PAYNAME");
    String CPLIBM = request.getParameter("CPLIBM");
        
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    String deskey = "123456789012345678901234";
    String identifyCode = "1mJqG0A6";

    Utilities util = new Utilities();
    
    java.util.Calendar nowDateTime = java.util.Calendar.getInstance();
    String responseTime = sdf.format(nowDateTime.getTime());

    StringBuffer originalParm = new StringBuffer();

    String XML_DOWNSTREM_URL = "http://epaytde.aptg.com.tw/EPAY/DealerServiceOrder?CPID="+CPID;
    System.out.println("@@@@: XML_DOWNSTREM_URL = " + XML_DOWNSTREM_URL);
 
    String tmprequestBody = "<ServiceOrderRequest><MDN>"+MDN+"</MDN>"
            + "<SERVICEID>"+SERVICEID+"</SERVICEID>"
            + "<CPLIBM>"+CPLIBM+"</CPLIBM>"            
            + "<PRICE>"+PRICE+"</PRICE>"
            + "<STOREID>"+STOREID+"</STOREID>"            
            + "<APISRCID>"+APISRCID+"</APISRCID>"  
            + "<PAYTOOL>"+PAYTOOL+"</PAYTOOL>"       
            + "<PAYNAME>"+PAYNAME+"</PAYNAME>"                
            + "<STORENAME>"+STORENAME+"</STORENAME></ServiceOrderRequest>";
    String requestBody = util.encrypt(deskey, tmprequestBody);
    
    String responseBody = "";

    RequestEntity body;
    System.out.println("@@requestBody:" + requestBody);
    body = new StringRequestEntity(requestBody, "text/xml", "big5");
    responseBody = (new SecuredMsg(deskey, identifyCode)).sendHttpPostMsg(body, XML_DOWNSTREM_URL);
    
    /**
	DocumentBuilderFactory docFactory = 
	DocumentBuilderFactory.newInstance();
	DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	Document doc = (Document)docBuilder.parse((new ByteArrayInputStream( responseBody.getBytes("utf-8") )));
	Element  element = doc.getDocumentElement(); 
	out.println(element.getNodeName());
	NodeList personNodes = element.getChildNodes(); 
	for (int i=0; i<personNodes.getLength(); i++){
		Node emp = personNodes.item(i);
		if (isTextNode(emp))
			continue;
		out.println( emp.getNodeName()+"</br>" );
		NodeList NameDOBCity = emp.getChildNodes(); 
		for (int j=0; j<NameDOBCity.getLength(); j++ ){
			Node node = NameDOBCity.item(j);
			if ( isTextNode(node)) 
				continue;
			out.println( emp.getNodeName()+util.decrypt(deskey, node.getFirstChild().getNodeValue())+"</br>" );
		}
	}
*/
	
    System.out.println("@@@@: requestBody = " + requestBody);
    System.out.println("@@@@: responseBody = " + responseBody );
    
	out.println("@@@@: requestBody = <textarea rows=10 cols=150>" + requestBody + "</textarea>");
	out.println("<br/>");
        String decode_resp = util.decrypt(deskey,responseBody);
    out.println("@@@@: responseBody = <textarea rows=30 cols=150>" + responseBody+ decode_resp + "</textarea>");

%><%!
  public boolean isTextNode(Node n){
	return n.getNodeName().equals("#text");
  }
%><%!class SecuredMsg {

        private Key key = null;
        private String identifyCode = "1mJqG0A6";
        private String md5Param = "&identifyCode=";
        private String characterSet = "utf-8";
        //to PaymentGateway use 'callerInMac', Receive from PaymentGateway use 'returnOutMac'
        private String desParam = "&callerInMac=";

        public SecuredMsg(String deskey, String identifyCode) {
            this.key = new SecretKeySpec(deskey.getBytes(), "DES");
            this.identifyCode = identifyCode;
        }

        public String encode(String plainText) throws Exception {
            return encode(plainText, md5Param, desParam);
        }

        public String encode(String plainText, String md5Param, String desParam) throws Exception {
            String outMsg = null;
            Cipher cipher = null;
            String md5Value = doMd5(plainText + md5Param + this.identifyCode);
            byte[] byteInMsg = null;
            byte[] byteEncrypted = null;

            cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            plainText = plainText + desParam + md5Value;
            byteInMsg = plainText.getBytes(characterSet);
            byteEncrypted = cipher.doFinal(byteInMsg);
            outMsg = URLEncoder.encode(new String(byteEncrypted, "ISO-8859-1"), "ISO-8859-1");
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

        
    public  String sendHttpPostMsg(RequestEntity requestBody, String url) throws Exception {
        System.out.println(" ** sendHttpPostMsg step 01");
        String rtresult = null;
        PostMethod post = new PostMethod(url);
        post.setRequestEntity(requestBody);
        System.out.println("@@@requestBody:" + requestBody.toString());
        HttpClient hc = new HttpClient();
        //   post.addRequestHeader("SOAPAction", "http://www.carrier.com/Echo");
        post.addRequestHeader("Content-Type", "x-www-form-urlencoded");
//        post.addRequestHeader("Content-Type", "text/xml charset=utf-8");
//         post.addRequestHeader("Content-Type", "tapplication/x-www-form-urlencoded");
        post.addRequestHeader("x-up-calling-line-id", "0928691763");
//        post.addParameter("ch_sn", "10");
//        post.addParameter("api_key", "b605fe32f7eb4504b1882fcd3c796a84");
//        post.addParameter("content_type", "1");
//        post.addParameter("content_type", "EIM");
//        post.addParameter("text_content", "%e5%85%ac%e5%91%8a%e8%a8%8a%e6%81%af%e9%80%9a%e7%9f%a5%ef%bc%9aKM+Notice+Test_%e3%80%8c%e7%ac%ac13%e5%b1%86%e6%9c%8d%e5%8b%99%e7%ac%ac%e5%a3%b9%e5%a4%a7%e7%8d%8e%e3%80%8d%e4%b8%80%e4%ba%ba%e4%b8%80%e6%97%a5%e4%b8%80%e7%a5%a8%ef%bc%81%e8%ab%8b%e8%b8%b4%e8%ba%8d%e6%8a%95%e7%a5%a8%ef%bc%818%2f10%7e9%2f27%3bhttp%3a%2f%2fservice2016.nextmag.com.tw%2f");
//        post.addParameter("account_list", "[\"ericyueh@aptg.com.tw\"]");
//        String sendto = URLEncoder.encode("[\"ryanchen11111221221@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\"]", "utf-8");
//        String sendto = URLEncoder.encode("[\"ryanchen@aptg.com.tw\",\"ryanchn1@aptg.com.tw\",\"ryanche@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"tcchin@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\"]", "utf-8");

//        String sendto = URLEncoder.encode("[\"ryanchen@aptg.com.tw\",\"ryanchen@aptg.com.tw\"]", "utf-8");
//        post.addParameter("account_list", sendto);
        System.out.println(" ** setConnectionManagerTimeout(10) step 02");

        try {
            hc.getParams().setConnectionManagerTimeout(10);
//        System.out.println(" ** .setSoTimeout(10) step 02");
//            hc.getParams().setSoTimeout(10);

            int result = hc.executeMethod(post);
            System.out.println(" ** sendHttpPostMsg step 03:" + result);
            if (result == HttpStatus.SC_OK) {
                rtresult = post.getResponseBodyAsString();
            } else {
                rtresult = null;
            }
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            post.releaseConnection();
        }
        return rtresult;
    }
    }%>
