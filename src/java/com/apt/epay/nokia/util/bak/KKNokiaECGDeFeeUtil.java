/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.nokia.util.bak;

import com.apt.epay.nokia.bean.NokiaReqBean;
import com.apt.epay.nokia.util.NokiaUtil;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.xml.soap.MimeHeaders;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;

/**
 *
 * @author kevinchang
 */
public class KKNokiaECGDeFeeUtil {

    private static final Logger log = Logger.getLogger("EPAY");

    public static StringBuffer retnNokiaDeFeeXML(String system_id, String system_pwd, String libm, String mdn, String value, String tradedate) {
        String mdn886 = "886" + mdn.substring(1);
        StringBuffer sb = new StringBuffer();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n"
                        + "<!DOCTYPE GatewayRequest SYSTEM \"http://10.107.192.15:8081/ecgs/dtd/gateway.dtd\">\n"
                        + "<GatewayRequest>\n"
                        + "    <RequestHeader \n"
                        + "         version=\"203\" \n"
                        + "         requesting_system_id=\"" + system_id + "\"\n"
                        + "         requesting_system_pwd=\"" + system_pwd + "\"\n"
                        + "         request_tid=\""+libm+"\"\n"
                        + "         request_timestamp=\""+tradedate+"\"\n"
                        + "         additional_info=\"999999\">\n"
                        + "	</RequestHeader>\n"
                        + "     <SubscriberAccountInfo account_code=\"4000\" balance_type=\"primary\">\n"
                        + "         <SubscriberID SubscriberIDType=\"00\">"+mdn886+"</SubscriberID>\n"
                        + "     </SubscriberAccountInfo>\n"
                        + "     <QueryDataRequest \n"
                        + "         OP=\"A\"\n"
                        + "         Action=\"IMOM\"\n"
                        + "         IMOMCommand=\"ADJ:EBUCKET,MSISDN="+mdn886+",BucketSourceID=650,Adjustment=DECR, Amount="+value+",IDTYPE=S\"\n"
                        + "	>\n"
                        + "  </QueryDataRequest>\n"
                        + "</GatewayRequest>");
        return sb;
    }

    public static String putNokiaDeFeeOCSlet(String libm, String mdn, String value, String tradedate) throws Exception {
        String result = "";
        NokiaUtil nokiautil = new NokiaUtil();

        String ocs_systemid = "123456";
        String ocs_system_pwd = "123456";
        String sendURL = "http://localhost:1234/gateway_servlet/gateway";
        
//        String ocs_systemid = new ShareParm().NOKIA_OCS_SYSTEM_ID;
//        String ocs_system_pwd = new ShareParm().NOKIA_OCS_SYSTEM_PWD;
//        String sendURL = new ShareParm().PARM_NOKIA_OCS_URL;

        MimeHeaders mh = new MimeHeaders();
        StringBuffer DefeeCodeXml = retnNokiaDeFeeXML(ocs_systemid, ocs_system_pwd, libm, mdn, value, tradedate);

        log.info("Nokia putNokiaDeFeeOCSlet (MDN:" + mdn + " Nokia OCS XML Request)==>" + DefeeCodeXml);
        log.info("Nokia putNokiaDeFeeOCSlet ==>" + sendURL);

        RequestEntity body;
        body = new StringRequestEntity(DefeeCodeXml.toString(), "text/xml", "utf-8");

//        String PROXY_FLAG = new ShareParm().PARM_SCT_PROXY_FLAG;
        String PROXY_FLAG = "TDE";

//        if ("PROD".equals(PROXY_FLAG)) {
//            result = nokiautil.sendHttpPostMsg(body, sendURL).replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://10.107.20.44:8081/ecgs/dtd/gateway.dtd\">", "");
//        } else {
//            System.out.println("TDE env, no proxy");
//            result = nokiautil.sendHttpPostMsg(body, sendURL).replaceFirst("<!DOCTYPE GatewayResponse SYSTEM \"http://192.168.20.100:8081/ecgs/dtd/gateway.dtd\">", "");
//        }
//        log.info("PricePlanCode Result==>" + result);

        return result;
    }

    public static void main(String[] args) {
//        String ocs_systemid = "123456";
//        String ocs_system_pwd = "123456";
//        String sendURL = "http://localhost:1234/gateway_servlet/gateway";
//        String libm = "223456";
//        String contactCellPhone = "0906001002";
//        //String serviceId = "APTWB03000";
//        String value = "100";
//
//        Calendar nowDateTime = Calendar.getInstance();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String tradeDate = sdf.format(nowDateTime.getTime());
//        SimpleDateFormat sdf_pincode = new SimpleDateFormat("MM/dd/yyyy.HH:mm:ss");
//        String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());
//        System.out.println("Nokia PricePlanCode test");
//
//        try {
//            String result = putNokiaDeFeeOCSlet(libm, contactCellPhone, value, tradeDate_Pincode);
//            System.out.println("Result:" + result);
//
//            if (result != null) {
//                NokiaReqBean apirequestbean;// = new PinCodeReqBean();
//                NokiaUtil nokiaUtil = new NokiaUtil();
//                apirequestbean = nokiaUtil.parseNokiaResultXMLString(result);
//
//                String resultcode = "";
//                try {
//                    resultcode = apirequestbean.getResultcode();
//                    System.out.println(resultcode);
//
//                } catch (Exception ex) {
//                    System.out.println(ex);
//                }
//
//            } else {
//                System.out.println("CMS Admin Exception===>THE PINCODE Result From 4G OCS IS NULL!!!!!");
//            }
//        } catch (Exception ex) {
//            System.out.println(ex);
//        }
    }
}
