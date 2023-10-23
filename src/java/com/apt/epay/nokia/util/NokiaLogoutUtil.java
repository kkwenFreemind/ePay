/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.nokia.util;

import com.apt.epay.nokia.bean.NokiaResultBean;
import com.apt.epay.share.ShareParm;
import javax.xml.soap.MimeHeaders;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;

/**
 *
 * @author kevinchang
 */
public class NokiaLogoutUtil {

    private static final Logger log = Logger.getLogger("EPAY");

    public static StringBuffer retnLogoutXML(String sessionId) {

        StringBuffer sb = new StringBuffer();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
                        + "  <soapenv:Body>\n"
                        + "    <LogoutRequest xmlns=\"http://alcatel-lucent.com/esm/ws/svcmgr/V2_0\">\n"
                        + "      <SessionInfo>\n"
                        + "        <sessionId>" + sessionId + "</sessionId>\n"
                        + "      </SessionInfo>\n"
                        + "    </LogoutRequest>\n"
                        + "  </soapenv:Body>\n"
                        + "</soapenv:Envelope>");

        log.info("NOKIA Logout Request ==>" + sb.toString().replaceAll("[ \t\r\n]+", " ").trim());
        return sb;
    }

    public NokiaResultBean putLogoutOCSlet(String sessionId) throws Exception {

        // kk config
        String sendURL = new ShareParm().PARM_NOKIA_HLAPI_URL;
        
        NokiaResultBean result = null;
        NokiaUtil nokiautil = new NokiaUtil();

        MimeHeaders mh = new MimeHeaders();
        StringBuffer bucketXml = retnLogoutXML(sessionId);

        RequestEntity body;
        body = new StringRequestEntity(bucketXml.toString(), "text/xml", "utf-8");

        result = nokiautil.sendNokiaHttpPostMsg(body, sendURL);
        log.info("NOKIA Logout Response ==>" + result.getHttpstatus());
        log.info("NOKIA Logout Response ==>" + result.getXmdrecord());
        return result;
    }

}
