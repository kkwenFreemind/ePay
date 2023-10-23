/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.util;

import com.apt.epay.beans.BucketReqBean;
//import com.apt.epay.beans.PinCodeReqBean;
//import com.apt.epay.controller.EPayBusinessConreoller;
//import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
//import static com.apt.util.BundleUtil.retnBunldeXML;
//import static com.apt.util.BundleUtil.sendHttpPostMsg;
import static com.apt.util.PinCodeUtil.sendHttpPostMsg;
//import com.epay.ejb.bean.EPAY_BUCKET;
//import com.epay.ejb.bean.EPAY_SERVICE_INFO;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MimeHeaders;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
//import org.zkoss.zk.ui.Executions;

/**
 *
 * @author kevinchang
 */
public class AdjustBucket {

    private static final Logger log = Logger.getLogger("EPAY");
    private static String cpid = new ShareParm().PARM_EPAY_CPID;
    ServletContext ctx;

    public String putBucketOCSlet(String libm, String mdn, String tradedate, String bucketid, String amount, String acctype, String sid, int ttype,String tid, String ref) throws Exception {

        String result = "";
        String ocs_systemid = new ShareParm().OCS_SYSTEM_ID;
        String ocs_system_pwd = new ShareParm().OCS_SYSTEM_PWD;
        String sendURL = new ShareParm().PARM_4GOCS_URL;

        MimeHeaders mh = new MimeHeaders();
        log.info("retnBucketXML(ocs_systemid, ocs_system_pwd, libm, mdn, tradedate, bucketid, amount, acctype)==>" + ocs_systemid + "," + ocs_system_pwd + "," + libm + "," + mdn + "," + tradedate + "," + bucketid + "," + amount + "," + acctype);
        StringBuffer bucketXml = retnBucketXML(ocs_systemid, ocs_system_pwd, libm, mdn, tradedate, bucketid, amount, acctype, sid, ttype,tid,ref);

        log.info("VASProce(MDN:" + mdn + " OCS XML Request)==>" + bucketXml);
        log.info("PARM_4GOCS_URL==>" + sendURL);

        RequestEntity body;
        body = new StringRequestEntity(bucketXml.toString(), "text/xml", "utf-8");

        result = sendHttpPostMsg(body, sendURL).replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://10.108.17.36:8081/ecgs/dtd/gateway.dtd\">", "");
        log.info("putBucketOCSlet Result==>" + result);

        return result;
    }

    public String putBucketActiveOCSlet(String libm, String mdn, String tradedate) throws Exception {

        String result = "";
        String ocs_systemid = new ShareParm().OCS_SYSTEM_ID;
        String ocs_system_pwd = new ShareParm().OCS_SYSTEM_PWD;
        String sendURL = new ShareParm().PARM_4GOCS_URL;

        MimeHeaders mh = new MimeHeaders();
        log.info("retnBucketActiveXML(ocs_systemid, ocs_system_pwd, libm, mdn, tradedate, bucketid, amount)==>" + ocs_systemid + "," + ocs_system_pwd + "," + libm + "," + mdn + "," + tradedate);
        StringBuffer bucketXml = retnBucketActiveXML(ocs_systemid, ocs_system_pwd, libm, mdn, tradedate);

        log.info("VASProce(MDN:" + mdn + " OCS XML Request)==>" + bucketXml);
        log.info("PARM_4GOCS_URL==>" + sendURL);

        RequestEntity body;
        body = new StringRequestEntity(bucketXml.toString(), "text/xml", "utf-8");

        result = sendHttpPostMsg(body, sendURL).replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://10.108.17.36:8081/ecgs/dtd/gateway.dtd\">", "");
        log.info("putBucketActiveOCSlet Result==>" + result);

        return result;
    }

    public static StringBuffer retnBucketActiveXML(String system_id, String system_pwd, String libm, String mdn, String tradedate) {
        String mdn886 = "886" + mdn.substring(1);
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
        sb.append("<!DOCTYPE GatewayRequest SYSTEM \"http://10.108.20.44:8081/ecgs/dtd/gateway.dtd\">");
        sb.append("<GatewayRequest>");
        sb.append("<RequestHeader");
        sb.append(" version=\"2915\"");
        sb.append(" requesting_system_id=\"" + system_id + "\"");
        sb.append(" requesting_system_pwd=\"" + system_pwd + "\"");
        sb.append(" request_tid=\"" + libm + "\" request_timestamp=\"" + tradedate + "\"");
        sb.append(" additional_info=\"Change Expired Date\">");
        sb.append("</RequestHeader>");
        sb.append("<SubscriberAccountInfo account_code=\"2000\" balance_type=\"primary\">");
        sb.append("<SubscriberID");
        sb.append(" SubscriberIDType=\"00\">" + mdn886 + "</SubscriberID>");
        sb.append("</SubscriberAccountInfo>");
        sb.append("<QueryDataRequest");
        sb.append(" OP=\"A\"");
        sb.append(" Action=\"IMOM\"");
        sb.append(" IMOMCommand=\"ADJ:LIFECYCLE,ACCOUNTID=" + mdn886 + ",NEWSTATE=ACTIVE,IDTYPE=S,TRANS_ID=" + libm + "\"");
        sb.append(">");
        sb.append("</QueryDataRequest>");
        sb.append("</GatewayRequest>");
        return sb;
    }

    public String putChangeExpireDateOCSlet(String libm, String mdn, String tradedate) throws Exception {

        String result = "";
        String ocs_systemid = new ShareParm().OCS_SYSTEM_ID;
        String ocs_system_pwd = new ShareParm().OCS_SYSTEM_PWD;
        String sendURL = new ShareParm().PARM_4GOCS_URL;

        MimeHeaders mh = new MimeHeaders();
        log.info("retnChangeExpireDateXML(ocs_systemid, ocs_system_pwd, libm, mdn, tradedate, bucketid)===>" + ocs_systemid + "," + ocs_system_pwd + "," + libm + "," + mdn + "," + tradedate);
        StringBuffer bucketXml = retnChangeExpireDateXML(ocs_systemid, ocs_system_pwd, libm, mdn, tradedate);

        log.info("VASProce(MDN:" + mdn + " OCS XML Request)==>" + bucketXml);
        log.info("PARM_4GOCS_URL==>" + sendURL);

        RequestEntity body;
        body = new StringRequestEntity(bucketXml.toString(), "text/xml", "utf-8");

        result = sendHttpPostMsg(body, sendURL).replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://10.108.17.36:8081/ecgs/dtd/gateway.dtd\">", "");
        log.info("putChangeExpireDateOCSlet Result==>" + result);

        return result;
    }

    public String putChangeServiceExpireDateOCSlet(String libm, String mdn, String tradedate, String bucketid, int hr, String ptp_id) throws Exception {

        String result = "";
        String ocs_systemid = new ShareParm().OCS_SYSTEM_ID;
        String ocs_system_pwd = new ShareParm().OCS_SYSTEM_PWD;
        String sendURL = new ShareParm().PARM_4GOCS_URL;

        MimeHeaders mh = new MimeHeaders();
        log.info("retnChangeServiceExpireDateXML(ocs_systemid, ocs_system_pwd, libm, mdn, tradedate, bucketid, days, ptp_id)====>" + ocs_systemid + "," + ocs_system_pwd + "," + libm + "," + mdn + "," + tradedate + "," + bucketid + "," + hr + "," + ptp_id);
        StringBuffer bucketXml = retnChangeServiceExpireDateXML(ocs_systemid, ocs_system_pwd, libm, mdn, tradedate, bucketid, hr, ptp_id);

        log.info("Adjust Bundle Proce(MDN:" + mdn + " OCS XML Request)==>" + bucketXml);
        log.info("PARM_4GOCS_URL==>" + sendURL);

        RequestEntity body;
        body = new StringRequestEntity(bucketXml.toString(), "text/xml", "utf-8");

        result = sendHttpPostMsg(body, sendURL).replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://10.108.17.36:8081/ecgs/dtd/gateway.dtd\">", "");
        log.info("putBucketOCSlet Result==>" + result);

        return result;
    }

    public static StringBuffer retnChangeExpireDateXML(String system_id, String system_pwd, String libm, String mdn, String tradedate) {
        String mdn886 = "886" + mdn.substring(1);
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
        sb.append("<!DOCTYPE GatewayRequest SYSTEM \"http://10.108.20.44:8081/ecgs/dtd/gateway.dtd\">");
        sb.append("<GatewayRequest>");
        sb.append("<RequestHeader");
        sb.append(" version=\"2915\"");
        sb.append(" requesting_system_id=\"" + system_id + "\"");
        sb.append(" requesting_system_pwd=\"" + system_pwd + "\"");
        sb.append(" request_tid=\"" + libm + "\" request_timestamp=\"" + tradedate + "\"");
        sb.append(" additional_info=\"Change Expired Date\">");
        sb.append("</RequestHeader>");
        sb.append("<SubscriberAccountInfo account_code=\"2000\" balance_type=\"primary\">");
        sb.append("<SubscriberID");
        sb.append(" SubscriberIDType=\"00\">" + mdn886 + "</SubscriberID>");
        sb.append("</SubscriberAccountInfo>");
        sb.append("<QueryDataRequest");
        sb.append(" OP=\"A\"");
        sb.append(" Action=\"IMOM\"");
        sb.append(" IMOMCommand=\"ADJ:BALANCE,MSISDN=" + mdn886 + ",AMOUNT=0,ADJ=INCR,BAL=P,NO_LC=Y,Recharge=N,CED_SET=180,IDTYPE=S,TRANS_ID=" + libm + ",REF1=" + system_id + "\"");
        sb.append(">");
        sb.append("</QueryDataRequest>");
        sb.append("</GatewayRequest>");
        return sb;
    }

    public static StringBuffer retnChangeServiceExpireDateXML(String system_id, String system_pwd, String libm, String mdn, String tradedate, String bucketid, int hr, String PTP_ID) {
        String mdn886 = "886" + mdn.substring(1);
        String enddate = "";
        String endtime = "";

        BundleDateUtil bdt = new BundleDateUtil();
        SimpleDateFormat formatDatex = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        log.info("bdt.getBundleDate(days)===>" + bdt.getBundleDate(hr));
        //bundledate.getBundledate()===>2015-08-31 16:51:54.0

//        String kk = "2015-08-31 16:51:54.0";
        String kk = bdt.getBundleDate(hr);
        String pattern = "yyyy-MM-dd HH:mm:ss.s";
        try {
            Date parseDate = new SimpleDateFormat(pattern).parse(kk);
            String pattern1 = "yyyy/MM/dd";
            String pattern2 = "HHmm";
            SimpleDateFormat sdf1 = new SimpleDateFormat(pattern1);
            SimpleDateFormat sdf2 = new SimpleDateFormat(pattern2);

            enddate = sdf1.format(parseDate);
            endtime = sdf2.format(parseDate);

//            System.out.println(k1+","+k2);
            //<Value>Parameter Error: End Date Value invalid.</Value>
            log.info("retnChangeServiceExpireDateXML(enddate,endtime)===>" + enddate + "," + endtime);

        } catch (Exception ex) {
            log.info(ex);
        }

        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
        sb.append("<!DOCTYPE GatewayRequest SYSTEM \"http://10.108.20.44:8081/ecgs/dtd/gateway.dtd\">");
        sb.append("<GatewayRequest>");
        sb.append("<RequestHeader");
        sb.append(" version=\"2915\"");
        sb.append(" requesting_system_id=\"" + system_id + "\"");
        sb.append(" requesting_system_pwd=\"" + system_pwd + "\"");
        sb.append(" request_tid=\"" + libm + "\" request_timestamp=\"" + tradedate + "\"");
        sb.append(" additional_info=\"Change End Date\">");
        sb.append("</RequestHeader>");
        sb.append("<SubscriberAccountInfo account_code=\"2000\" balance_type=\"primary\">");
        sb.append("<SubscriberID");
        sb.append(" SubscriberIDType=\"00\">" + mdn886 + "</SubscriberID>");
        sb.append("</SubscriberAccountInfo>");
        sb.append("<QueryDataRequest");
        sb.append(" OP=\"A\"");
        sb.append(" Action=\"IMOM\"");
        if (bucketid.equals("810")) {
            sb.append(" IMOMCommand=\"SUB:DTPBUNDLE,MSISDN=" + mdn886 + ",PTP_ID=" + "VOCVB" + ",END_DATE=" + enddate + ",END_TIME=" + endtime + ",INCR=Y,IDTYPE=S,TRANS_ID=" + libm + ",CHL_ID=" + system_id + "\"");
        } else {
            sb.append(" IMOMCommand=\"SUB:DTPBUNDLE,MSISDN=" + mdn886 + ",PTP_ID=" + PTP_ID + ",END_DATE=" + enddate + ",END_TIME=" + endtime + ",INCR=Y,IDTYPE=S,TRANS_ID=" + libm + ",CHL_ID=" + system_id + "\"");          
        }
        sb.append(">");
        sb.append("</QueryDataRequest>");
        sb.append("</GatewayRequest>");
        return sb;
    }

    public static StringBuffer retnBucketXML(String system_id, String system_pwd, String libm, String mdn, String tradedate, String bucketid, String amount, String acctype, String sid, int ttype, String tid,String ref) {
        String mdn886 = "886" + mdn.substring(1);
        StringBuffer sb = new StringBuffer();
//        String tid = "";
//        String ref = "";

        
//        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        if (ttype == 2) { //網路儲值
//            AdjustAccUtil adjacc = new AdjustAccUtil();
//            tid = adjacc.getTransID(sid, ttype);
//            ref = adjacc.getREF(sid, bucketid, ttype);
            log.info("TID===>" + tid);
            log.info("REF===>" + ref);
        }

        if (ttype == 1) {
//            AdjustAccUtil adjacc = new AdjustAccUtil();
//            ref = adjacc.getREF(sid, bucketid, ttype);
            tid = ""; //餘額抵扣
            log.info("TID===>" + tid);
            log.info("REF===>" + ref);
        }

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
        sb.append("<!DOCTYPE GatewayRequest SYSTEM \"http://10.108.20.44:8081/ecgs/dtd/gateway.dtd\">");
        sb.append("<GatewayRequest>");
        sb.append("<RequestHeader");
        sb.append(" version=\"2915\"");
        sb.append(" requesting_system_id=\"" + system_id + "\"");
        sb.append(" requesting_system_pwd=\"" + system_pwd + "\"");
        sb.append(" request_tid=\"" + libm + "\" request_timestamp=\"" + tradedate + "\"");
        sb.append(" additional_info=\"Adjust Bucket\">");
        sb.append("</RequestHeader>");
        sb.append("<SubscriberAccountInfo account_code=\"2000\" balance_type=\"primary\">");
        sb.append("<SubscriberID");
        sb.append(" SubscriberIDType=\"00\">" + mdn886 + "</SubscriberID>");
        sb.append("</SubscriberAccountInfo>");
        sb.append("<QueryDataRequest");
        sb.append(" OP=\"A\"");
        sb.append(" Action=\"IMOM\"");
        if (acctype.equals("incr")) {
            sb.append(" IMOMCommand=\"ADJ:EBUCKET,MSISDN=" + mdn886 + ",BucketSourceID=" + bucketid + ",Adjustment=INCR,Amount=" + amount + ",IDTYPE=S,TRANS_ID=" + tid + ",REF1=" + ref + "\"");
        }
        if (acctype.equals("decr")) {
            sb.append(" IMOMCommand=\"ADJ:EBUCKET,MSISDN=" + mdn886 + ",BucketSourceID=" + bucketid + ",Adjustment=DECR,Amount=" + amount + ",IDTYPE=S,TRANS_ID=" + tid + ",REF1=" + ref + "\"");
        }
        sb.append(">");
        sb.append("</QueryDataRequest>");
        sb.append("</GatewayRequest>");
        return sb;
    }

    public static String sendHttpPostMsg(RequestEntity requestBody, String url) throws Exception {
//        System.out.println(" ** sendHttpPostMsg step 01");
        String rtresult = null;
        PostMethod post = new PostMethod(url);
        post.setRequestEntity(requestBody);
        HttpClient hc = new HttpClient();

//        System.out.println(" ** setConnectionManagerTimeout(10) step 02");
        try {
            hc.getParams().setConnectionManagerTimeout(10);
//        System.out.println(" ** .setSoTimeout(10) step 02");
//            hc.getParams().setSoTimeout(10);

            int result = hc.executeMethod(post);
//            System.out.println(" ** sendHttpPostMsg step 03");
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

    public BucketReqBean parseBucketCodeXMLString(String xmlRecords) throws Exception {

//        log.debug("PinCode xmlRecords==>" + xmlRecords);
        BucketReqBean aPIBucketRequestBean = new BucketReqBean();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));

            Document doc = db.parse(is);
            NodeList PinCodeRes = doc.getElementsByTagName("GatewayResponse");

            for (int i = 0; i < PinCodeRes.getLength(); i++) {
                Element element = (Element) PinCodeRes.item(i);

                NodeList nodes = element.getElementsByTagName("ResponseHeader");
                Element line = (Element) nodes.item(0);

                log.debug("status--->" + line.getAttribute("status"));
                aPIBucketRequestBean.setStatus(line.getAttribute("status"));

                log.debug("result_code----->" + line.getAttribute("result_code"));
                aPIBucketRequestBean.setResultcode(line.getAttribute("result_code"));

                nodes = element.getElementsByTagName("RequestHeader");
                log.debug("request_tid------>" + line.getAttribute("request_tid"));
                aPIBucketRequestBean.setRequestid(line.getAttribute("request_tid"));

                nodes = element.getElementsByTagName("SubscriberID");
                line = (Element) nodes.item(0);
                log.debug("SubscriberID---->" + getCharacterDataFromElement(line));
                aPIBucketRequestBean.setMdn(getCharacterDataFromElement(line));

            }

        } catch (Exception e) {
            throw e;
        }
        return aPIBucketRequestBean;
    }

    private String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
//            log.info("cd.getData()==>" + cd.getData());
            return cd.getData();
        }
        return "";
    }

}
