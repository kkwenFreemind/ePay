<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
    String SERVICENAME = request.getParameter("SERVICENAME");
    String UUID = request.getParameter("UUID");
    String PRICE = request.getParameter("PRICE");

    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    String deskey = "25LTtRgN";
    String identifyCode = "1mJqG0A6";

    java.util.Calendar nowDateTime = java.util.Calendar.getInstance();
    String responseTime = sdf.format(nowDateTime.getTime());

    StringBuffer originalParm = new StringBuffer();

    String XML_DOWNSTREM_URL = "http://epaytde.aptg.com.tw/EPAY/VCardReserve";
    System.out.println("@@@@: XML_DOWNSTREM_URL = " + XML_DOWNSTREM_URL);
    String encode_mdn = (new SecuredMsg(deskey, identifyCode)).encode(MDN).toString();
    String encode_serviceid = (new SecuredMsg(deskey, identifyCode)).encode(SERVICEID).toString();
    String encode_servicename = (new SecuredMsg(deskey, identifyCode)).encode(SERVICENAME).toString();
    String encode_uuid = (new SecuredMsg(deskey, identifyCode)).encode(UUID).toString();
    String encode_price= (new SecuredMsg(deskey, identifyCode)).encode(PRICE).toString();
    String requestBody = "<ReserveRequest><CPID>" + CPID + "</CPID><MDN>"
            + encode_mdn + "</MDN><SERVICEID>"+encode_serviceid+"</SERVICEID><SERVICENAME>"+encode_servicename+"</SERVICENAME><UUID>"+encode_uuid+"</UUID><PRICE>"+encode_price+"</PRICE></ReserveRequest>";

	String responseBody = "";

    RequestEntity body;
    body = new StringRequestEntity(requestBody, "text/xml", "utf-8");
	responseBody = (new SecuredMsg(deskey, identifyCode)).sendHttpPostMsg(body, XML_DOWNSTREM_URL);
	
	DocumentBuilderFactory docFactory = 
	DocumentBuilderFactory.newInstance();
	DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	Document doc = (Document)docBuilder.parse((new ByteArrayInputStream( responseBody.getBytes("utf-8") )));
	Element  element = doc.getDocumentElement(); 
	NodeList personNodes = element.getChildNodes(); 
	for (int i=0; i<personNodes.getLength(); i++){
		Node emp = personNodes.item(i);
		if (isTextNode(emp))
			continue;
		NodeList NameDOBCity = emp.getChildNodes(); 
		for (int j=0; j<NameDOBCity.getLength(); j++ ){
			Node node = NameDOBCity.item(j);
			if ( isTextNode(node)) 
				continue;
			out.println( (new SecuredMsg(deskey, identifyCode)).decode(node.getFirstChild().getNodeValue())+"</br>" );
		}
	}
	
    System.out.println("@@@@: requestBody = " + requestBody);
    System.out.println("@@@@: responseBody = " + responseBody );
    
	out.println("@@@@: requestBody = <textarea rows=10 cols=150>" + requestBody + "</textarea>");
	out.println("<br/>");
    out.println("@@@@: responseBody = <textarea rows=30 cols=150>" + responseBody + "</textarea>");

    //originalParm.append("<SubscriberLookupRequest><channelID>").append(channelID).append("</channelID/><msisdn/>").append(encode_mdn).append("</msisdn></SubscriberLookupRequest/>");
    //originalParm.append("<SubscriberLookupRequest>");
    //String kk = originalParm.toString();

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

        public String sendHttpPostMsg(RequestEntity requestBody, String url) throws Exception {
            System.out.println(" ** sendHttpPostMsg step 01");
            String rtresult = null;
            PostMethod post = new PostMethod(url);
            post.setRequestEntity(requestBody);
            HttpClient hc = new HttpClient();
            post.addRequestHeader("SOAPAction", "http://www.carrier.com/Echo");
            post.addRequestHeader("Content-Type", "text/xml;charset=UTF-8");
            post.addRequestHeader("x-up-imsi", "6000428202");
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
//
    }%>
