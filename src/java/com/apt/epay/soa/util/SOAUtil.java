/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.soa.util;

import com.apt.epay.beans.SOAReqBean;
import com.apt.util.SoaProfile;
import org.apache.log4j.Logger;

/**
 *
 * @author kevinchang
 */
public class SOAUtil {

    private static final Logger log = Logger.getLogger("EPAY");

    public SOAReqBean getSOAInfo(String mdn) {
        SoaProfile soa = new SoaProfile();
        SOAReqBean apirequestbean = new SOAReqBean();
        try {

            String result = soa.putSoaProxyletByMDN(mdn);
            apirequestbean = soa.parseXMLString(result);

        } catch (Exception ex) {
            log.info(ex);
        }
        return apirequestbean;
    }
}
