/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.nokia.util.bak;

import com.apt.epay.nokia.bean.NokiaResultBean;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.xml.soap.MimeHeaders;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;

/**
 *
 * @author kevinchang
 */
public class KKNokiaHLBasicInfoUtil {

    private static final Logger log = Logger.getLogger("EPAY");

    public static String sendHttpPostMsg(RequestEntity requestBody, String url) throws Exception {
//        System.out.println(" ** sendHttpPostMsg step 01");
        String rtresult = null;
        PostMethod post = new PostMethod(url);
        post.setRequestEntity(requestBody);
        HttpClient hc = new HttpClient();

        try {
            hc.getParams().setConnectionManagerTimeout(10);
            int result = hc.executeMethod(post);
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

    public static StringBuffer retnBucketActiveXML(String system_id, String system_pwd, String libm, String mdn, String tradedate) {
        String mdn886 = "886" + mdn.substring(1);
        StringBuffer sb = new StringBuffer();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
                        + "   <soapenv:Body>\n"
                        + "      <RetrieveRequest xmlns=\"http://alcatel-lucent.com/esm/ws/svcmgr/V2_0\">\n"
                        + "         <SessionInfo>\n"
                        + "            <sessionId>"+libm+"</sessionId>\n"
                        + "         </SessionInfo>\n"
                        + "         <RequestInfo>\n"
                        + "            <ReqID>"+libm+"</ReqID>\n"
                        + "         </RequestInfo>\n"
                        + "         <TaskList>\n"
                        + "            <Task>\n"
                        + "            <Name>Query Subscriber Account</Name>\n"
                        + "               <QueryCriteria>\n"
                        + "                   <Param>\n"
                        + "                       <Name>Account ID</Name>\n"
                        + "                       <Value>"+mdn886+"</Value>\n"
                        + "                   </Param>\n"
                        + "	           </QueryCriteria>\n"
                        + "	             <QueryData>\n"
                        + "	                 <Collection>\n"
                        + "	                     <CollectionName>Subscriber Account</CollectionName>\n"
                        + "	                     <Attributes>\n"
                        + "	                        <item>Class of Service ID</item>\n"
                        + "	                        <item>External ID</item>\n"
                        + "                            <item>IMSI 1</item>\n"
                        + "	                        <item>Contract ID</item>\n"
                        + "	                        <item>Language Label</item>\n"
                        + "	                        <item>SIM State</item>\n"
                        + "                            <item>Lifecycle Expiry Date 1</item>                            \n"
                        + "	                     </Attributes>\n"
                        + "	                 </Collection>\n"
                        + "	             </QueryData>\n"
                        + "             </Task>         \n"
                        + "            <Task>\n"
                        + "               <Name>Query Bundle</Name>\n"
                        + "               <QueryCriteria>\n"
                        + "                   <Param>\n"
                        + "                       <Name>Account ID</Name>\n"
                        + "                       <Value>"+mdn886+"</Value>\n"
                        + "                   </Param>\n"
                        + "                   <Param>\n"
                        + "                       <Name>Account Type</Name>\n"
                        + "                       <Value>Subscriber</Value>\n"
                        + "                   </Param>\n"
                        + "                   <Param>\n"
                        + "                       <Name>Operation Type</Name>\n"
                        + "                       <Value>Query Bundle only</Value>\n"
                        + "                   </Param>\n"
                        + "	            </QueryCriteria>\n"
                        + "             </Task>\n"
                        + "         </TaskList>\n"
                        + "      </RetrieveRequest>\n"
                        + "   </soapenv:Body>\n"
                        + "</soapenv:Envelope>");

        return sb;
    }

    public static String putBucketActiveOCSlet(String libm, String mdn, String tradedate) throws Exception {

        String result = "";
//        String ocs_systemid = new ShareParm().OCS_SYSTEM_ID;
//        String ocs_system_pwd = new ShareParm().OCS_SYSTEM_PWD;
//        String sendURL = new ShareParm().PARM_4GOCS_URL;

//        String ocs_systemid = "123456";
//        String ocs_system_pwd = "123456";
//        String sendURL = "http://localhost:1234/gateway_servlet/gateway";
        String ocs_systemid = "aluprov";
        String ocs_system_pwd = "Alu_prov_d3";
        String sendURL = "http://localhost:4312/SvcMgr";

        MimeHeaders mh = new MimeHeaders();
        System.out.println("retnBucketActiveXML(ocs_systemid, ocs_system_pwd, libm, mdn, tradedate, bucketid, amount)==>" + ocs_systemid + "," + ocs_system_pwd + "," + libm + "," + mdn + "," + tradedate);
        StringBuffer bucketXml = retnBucketActiveXML(ocs_systemid, ocs_system_pwd, libm, mdn, tradedate);

        System.out.println("VASProce(MDN:" + mdn + " OCS XML Request)==>" + bucketXml);
        System.out.println("PARM_4GOCS_URL==>" + sendURL);

        RequestEntity body;
        body = new StringRequestEntity(bucketXml.toString(), "text/xml", "utf-8");

        result = sendHttpPostMsg(body, sendURL).replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://10.108.17.36:8081/ecgs/dtd/gateway.dtd\">", "");
        System.out.println("putBucketActiveOCSlet Result==>" + result);

        return result;
    }

    public static void main(String[] args) {
//        String ocs_systemid = "123456";
//        String ocs_system_pwd = "123456";
//        String sendURL = "http://localhost:1234/gateway_servlet/gateway";

//        try {
//            NokiaResultBean result = new NokiaResultBean();
//            result = putLoginOCSlet();
//            if (result.getHttpstatus() == HttpStatus.SC_OK) {
//
//                String xml = result.getXmdrecord();
//                String pid = getSeesionId(xml);
//
//                System.out.println(pid);
//                String contactCellPhone = "0906400314";
//                Calendar nowDateTime = Calendar.getInstance();
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                String tradeDate = sdf.format(nowDateTime.getTime());
//                SimpleDateFormat sdf_pincode = new SimpleDateFormat("MM/dd/yyyy.HH:mm:ss");
//                String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());
//                System.out.println("Nokia NokiaChangeLifeCycle test");
//                String kk = putBucketActiveOCSlet(pid, contactCellPhone, tradeDate_Pincode);
//                System.out.println(kk);
//
//                NokiaLogoutUtil logout = new NokiaLogoutUtil();
//                String logoutresult = logout.putLoginOCSlet(pid);
//                System.out.println(logoutresult);
//            } else {
//
//            }
//
//        } catch (Exception ex) {
//            System.out.println(ex);
//        }

    }
}
