/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.nokia.main;

import com.apt.epay.nokia.bean.NokiaResultBean;
import com.apt.epay.nokia.bean.NokiaSubscribeAgentInfoBean;
import com.apt.epay.nokia.bean.NokiaSubscribeBalanceBean;
import com.apt.epay.nokia.util.NokiaHLAgentInfoUtil;
import com.apt.epay.nokia.util.NokiaHLBalanceInfoUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author kevinchang
 */
public class NokiaMainBasicInfoUtil {

    private static final Logger log = Logger.getLogger("EPAY");

    public NokiaSubscribeBalanceBean GetMDNBalanceInfo(String libm, String mdn) {
        NokiaSubscribeBalanceBean sbean = new NokiaSubscribeBalanceBean();
        NokiaMainLoginAndLifeCycleUtil mutil = new NokiaMainLoginAndLifeCycleUtil();
        try {
            String pid = mutil.login();
            if (pid != null) {
                NokiaHLBalanceInfoUtil util = new NokiaHLBalanceInfoUtil();
                NokiaResultBean bean = util.putNokiaBasicInfoOCSlet(libm, pid, mdn);
                sbean = util.parseNokiaXMLString(bean.getXmdrecord());
                boolean logoutflag = mutil.logout(pid);
            } else {
                log.info(mdn + " Get Nokia Pid Failed:" + pid);
                return sbean;
            }
        } catch (Exception ex) {
            log.info(ex);
        }
        return sbean;
    }

    public NokiaSubscribeAgentInfoBean  GetMDNAgentInfo(String libm, String mdn) {

        NokiaResultBean sbean = new NokiaResultBean();
        NokiaSubscribeAgentInfoBean AgentBean = new NokiaSubscribeAgentInfoBean();
        NokiaMainLoginAndLifeCycleUtil mutil = new NokiaMainLoginAndLifeCycleUtil();
        try {
            String pid = mutil.login();
            if (pid != null) {
                NokiaHLAgentInfoUtil util = new NokiaHLAgentInfoUtil();
                sbean = util.putNokiaBasicInfoOCSlet(libm, pid, mdn);
                AgentBean = util.parseNokiaAgentXMLString(sbean.getXmdrecord());
                boolean logoutflag = mutil.logout(pid);
            } else {
                log.info(mdn + " Get Nokia Pid Failed:" + pid);
                return AgentBean;
            }
        } catch (Exception ex) {
            log.info(ex);
        }
        return AgentBean;

    }

    public static void main(String[] args) throws Exception {

//        Calendar nowDateTime = Calendar.getInstance();
//        SimpleDateFormat sdf_pincode = new SimpleDateFormat("MM/dd/yyyy.HH:mm:ss");
//        String tradeDate = sdf_pincode.format(nowDateTime.getTime());
//        String libm = "1234567890";
//        String pincode = "1111111111111111";
//
//        NokiaSubscribeBalanceBean bean = new NokiaSubscribeBalanceBean();
//        bean = GetMDNBalanceInfo(libm, mdn);
    }
}
