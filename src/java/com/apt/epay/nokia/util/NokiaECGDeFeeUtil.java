/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.nokia.util;

import com.apt.epay.nokia.bean.NokiaResultBean;
import com.apt.epay.share.ShareParm;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.xml.soap.MimeHeaders;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;

/**
 *
 * @author kevinchang
 */
public class NokiaECGDeFeeUtil {

    private static final Logger log = Logger.getLogger("EPAY");

    public  StringBuffer retnNokiaDeFeeXML(String libm, String mdn, String tradedate, String bid, String Adjustment, int amount) {

        /*
        PARM_NOKIA_HLAPI_URL=http://10.107.192.5:8210/SvcMgr
        PARM_NOKIA_HLAPI_SYSTEM_ID=epayprov
        PARM_NOKIA_HLAPI_SYSTEM_PWD=epay@53362
        
        PARM_NOKIA_OCS_URL=http://10.107.192.15:8081/gateway_servlet/gateway
        PARM_NOKIA_OCS_SYSTEM_ID=123456
        PARM_NOKIA_OCS_SYSTEM_PWD=123456        
        
        */
        //kk configure
        String ocs_systemid = new ShareParm().PARM_NOKIA_OCS_SYSTEM_ID;
        String ocs_system_pwd = new ShareParm().PARM_NOKIA_OCS_SYSTEM_PWD;

        //change traddate format
        SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("MM/dd/yyyy.HH:mm:ss");
        Calendar nowDateTime = Calendar.getInstance();
        String nokia_tradedate = sdf15.format(nowDateTime.getTime());

        //adjust amount
        amount = amount * 10000;

        String mdn886 = "886" + mdn.substring(1);
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n"
                        + "<!DOCTYPE GatewayRequest SYSTEM \"http://10.108.20.44:8081/ecgs/dtd/gateway.dtd\">\n"
                        + "<GatewayRequest>\n"
                        + "<RequestHeader\n"
                        + "version=\"203\"\n"
                        + "requesting_system_id=\"" + ocs_systemid + "\"\n"
                        + "requesting_system_pwd=\"" + ocs_system_pwd + "\"\n"
                        + "request_tid=\"" + libm + "\"\n"
                        + "request_timestamp=\"" + nokia_tradedate + "\"\n"
                        + "additional_info=\"999999\">\n"
                        + "</RequestHeader>\n"
                        + "<SubscriberAccountInfo account_code=\"4000\" balance_type=\"primary\">\n"
                        + "<SubscriberID SubscriberIDType=\"00\">" + mdn886 + "</SubscriberID>\n"
                        + "</SubscriberAccountInfo>\n"
                        + "<QueryDataRequest\n"
                        + "OP=\"A\"\n"
                        + "Action=\"IMOM\"\n"
                        + "IMOMCommand=\"ADJ:EBUCKET,MSISDN=" + mdn886 + ",BucketSourceID=" + bid + ",Adjustment=" + Adjustment + ", Amount=" + amount + ",IDTYPE=S\"\n"
                        + ">\n"
                        + "</QueryDataRequest>\n"
                        + "</GatewayRequest>");

        log.info("NOKIA DeFee Request ==>" + sb.toString().replaceAll("[ \t\r\n]+", " ").trim());

        return sb;
    }

    public  NokiaResultBean putNokiaDeFeeOCSlet(String libm, String mdn, String tradedate, String bid, String Adjustment, int amount) throws Exception {

        //kk configure
        String PROXY_FLAG = new ShareParm().PARM_SCT_PROXY_FLAG;
        String sendURL = new ShareParm().PARM_NOKIA_OCS_URL;

        NokiaResultBean result;
        NokiaUtil nokiautil = new NokiaUtil();

        MimeHeaders mh = new MimeHeaders();
        StringBuffer DefeeXml = retnNokiaDeFeeXML(libm, mdn, tradedate, bid, Adjustment, amount);

        RequestEntity body;
        body = new StringRequestEntity(DefeeXml.toString(), "text/xml", "utf-8");

        result = nokiautil.sendNokiaHttpPostMsg(body, sendURL);

        if (result.getHttpstatus() == HttpStatus.SC_OK) {
            String tmp_xml;// = result.getXmdrecord();
            if ("PROD".equalsIgnoreCase(PROXY_FLAG)) {
                tmp_xml = result.getXmdrecord().replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://10.108.17.36:8081/ecgs/dtd/gateway.dtd\">", "");
            } else {
                tmp_xml = result.getXmdrecord().replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://192.168.20.100:8081/ecgs/dtd/gateway.dtd\">", "");
            }
            result.setXmdrecord(tmp_xml);
            log.info("NOKIA Defee Response ==>" + tmp_xml);
        }
        return result;
    }

    public static void main(String[] args) throws Exception {

//        String mdn = "0905001002";
//        Calendar nowDateTime = Calendar.getInstance();
//        SimpleDateFormat sdf_pincode = new SimpleDateFormat("MM/dd/yyyy.HH:mm:ss");
//        String tradeDate = sdf_pincode.format(nowDateTime.getTime());
//        String libm = "1234567890";
//        String Adjustment = "DECR"; //INCR DECR

//        NokiaResultBean bean = putNokiaDeFeeOCSlet(libm, mdn, tradeDate, "650", Adjustment, 5000);
    }
}
