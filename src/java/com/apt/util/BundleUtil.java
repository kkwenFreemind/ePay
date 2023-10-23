/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.util;

import com.apt.epay.beans.BasicInfoReqBean;
import com.apt.epay.share.ShareParm;

import java.io.IOException;
//import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
public class BundleUtil {

    private static final Logger log = Logger.getLogger("EPAY");

    public boolean Check710Exist(String libm, String mdn) {
        boolean result = false;
        SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);
        Calendar nowDateTime = Calendar.getInstance();
        OCS4GBasicInfoUtil basicinfoutil = new OCS4GBasicInfoUtil();
        String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());

        try {
            String basicinfo = basicinfoutil.putOCS4GBasicInfoSlet(libm, mdn, tradeDate_Pincode);
            log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfo);

            //get basicinforeqbean data
            BasicInfoReqBean basicinforeqbean = new BasicInfoReqBean();
            basicinforeqbean = basicinfoutil.parseBasicInfoXMLString(basicinfo);

            //判斷710/720/730
            String str710 = basicinforeqbean.getID7();
            String str720 = basicinforeqbean.getID3();
            String str730 = basicinforeqbean.getID4();
            log.info("str710,str720,str730=====>" + str710 + "," + str720 + "," + str730);

            if (str710 != null || !str710.equals("")) {
                if ((basicinforeqbean.getID3() == null || basicinforeqbean.getID3().equals("")) || (basicinforeqbean.getID4() == null || basicinforeqbean.getID4().equals(""))) {
                    log.info("str710,str720,str730=====>" + str710 + "," + str720 + "," + str730);
                    log.info("basicinforeqbean.getID7() == true");
                    result = true;
                }
            } else {
                log.info("str710,str720,str730=====>" + str710 + "," + str720 + "," + str730);
                log.info("basicinforeqbean.getID7() == null");
            }

        } catch (Exception ex) {
            log.info(ex);
        }
        return result;
    }

    public String putBundleOCSlet(String libm, String mdn, String tradedate, String bucketid, int hr, int newtype, int acctype) throws Exception {

        String result = "";
        String ocs_systemid = new ShareParm().OCS_SYSTEM_ID;
        String ocs_system_pwd = new ShareParm().OCS_SYSTEM_PWD;
        String sendURL = new ShareParm().PARM_4GOCS_URL;

        MimeHeaders mh = new MimeHeaders();
        StringBuffer bucketXml = retnBunldeXML(ocs_systemid, ocs_system_pwd, libm, mdn, tradedate, bucketid, hr, newtype, acctype);

        log.info("Adjust Bundle Proce(MDN:" + mdn + " OCS XML Request)==>" + bucketXml);
        log.info("PARM_4GOCS_URL==>" + sendURL);

        RequestEntity body;
        body = new StringRequestEntity(bucketXml.toString(), "text/xml", "utf-8");

        result = sendHttpPostMsg(body, sendURL).replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://10.108.17.36:8081/ecgs/dtd/gateway.dtd\">", "");
        log.info("putBucketOCSlet Result==>" + result);

        return result;
    }

    public static StringBuffer retnBunldeXML(String system_id, String system_pwd, String libm, String mdn, String tradedate, String bucketid, int hr, int newtype, int acctype) throws ParseException {
        String mdn886 = "886" + mdn.substring(1);
        String enddate = "";
        String endtime = "";
        String chl_id = "";

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
            log.info("enddate,endtime===>" + enddate + "," + endtime);

        } catch (Exception ex) {
            System.out.println(ex);
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

        if (newtype == 1) {// 720/730
            if (acctype == 2) {
                chl_id = ShareParm.CHIL_ID_VOLVB; //345000:網路儲值
            }
            if (acctype == 1) {
                chl_id = ShareParm.ACC_CHIL_ID_VOLVB; //304610:餘額抵扣
            }
            sb.append(" IMOMCommand=\"SUB:DTPBUNDLE,MSISDN=" + mdn886 + ",PTP_ID=VOLVB,END_DATE=" + enddate + ",END_TIME=" + endtime + ",INCR=Y,IDTYPE=S,TRANS_ID=" + ",CHL_ID=" + chl_id + "\"");
        }
        if (newtype == 2) {
            if (acctype == 2) {
                chl_id = ShareParm.CHIL_ID_VOCVB; //310113:網路儲值
            }
            sb.append(" IMOMCommand=\"SUB:DTPBUNDLE,MSISDN=" + mdn886 + ",PTP_ID=VOCVB,END_DATE=" + enddate + ",END_TIME=" + endtime + ",INCR=Y,IDTYPE=S,TRANS_ID=" + ",CHL_ID=" + chl_id + "\"");
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

}
