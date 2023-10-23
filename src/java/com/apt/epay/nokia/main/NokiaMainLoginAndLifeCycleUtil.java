/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.nokia.main;

import com.apt.epay.nokia.bean.NokiaResultBean;
import com.apt.epay.nokia.bean.NokiaSubscriberStatusBean;
import com.apt.epay.nokia.util.NokiaHLAgentInfoUtil;
import com.apt.epay.nokia.util.NokiaHLLoginUtil;
import com.apt.epay.nokia.util.NokiaHLMdnLCUtil;
import com.apt.epay.nokia.util.NokiaLogoutUtil;
import com.apt.epay.share.ShareParm;
import com.apt.util.MailUtil;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;

/**
 *
 * @author kevinchang
 */
public class NokiaMainLoginAndLifeCycleUtil {
    
    private static final Logger log = Logger.getLogger("EPAY");
    
    public String login() {
        String pid = "";
        boolean successflag = false;
        int count = 0;
        while (!successflag) {
            count++;
            try {
                NokiaResultBean bean;// = new NokiaResultBean();
                NokiaHLLoginUtil util = new NokiaHLLoginUtil();
                
                bean = util.putLoginOCSlet();
                if (bean.getHttpstatus() == HttpStatus.SC_OK) {
                    String xml = bean.getXmdrecord();
                    pid = util.getSeesionId(xml);
                    successflag = true;
                } else {
                    //呼叫失敗，可能是The user has reached the maximum number of login sessions
                    log.info("Get pid failed status ==>" + bean.getHttpstatus());
                    log.info("Get pid failed xml ==>" + bean.getXmdrecord());
                }
                if (count == 10) {
                    break;
                }
                
            } catch (Exception ex) {
                log.info(ex);
            }
        }
        return pid;
    }
    
    public boolean logout(String pid) {
        boolean result = false;
        boolean successflag = false;
        int count = 0;
        while (!successflag) {
            count++;
            try {
                NokiaLogoutUtil util = new NokiaLogoutUtil();
                NokiaResultBean bean = util.putLogoutOCSlet(pid);
                if (bean.getHttpstatus() == HttpStatus.SC_OK) {
                    result = true;
                    successflag = true;
                }
                if (count == 10) {
                    break;
                }
            } catch (Exception ex) {
                log.info(ex);
            }
            
        }
        log.info(pid + " logout result==>" + result);
        return result;
    }
    

    
    public String getMdnLifeCycle(String libm, String mdn, String pid) {
        String result = "";
        try {
            NokiaSubscriberStatusBean bean;// = new NokiaSubscriberStatusBean();
            NokiaHLMdnLCUtil util = new NokiaHLMdnLCUtil();
            NokiaResultBean nokia_response = util.putNokiaOCSlet(libm, mdn, pid);
            if (nokia_response.getHttpstatus() == HttpStatus.SC_OK) {
                bean = util.parseNokiaXMLString(nokia_response.getXmdrecord());
                result = bean.getSimstatus();
                log.info(mdn + " Nokia LC value ==>" + result);

                //判斷若屬於CED，則發送告警EMail
                if (result.toUpperCase().substring(0, 3).contentEquals("CED")) {                    
                    String xmsg = "Epay：用戶" + mdn + "，Nokia LifeCycle:" + libm + "==>" + result;
                    MailUtil mailUtil = new MailUtil();
                    boolean sendresult = mailUtil.sendSimpleMail(xmsg);
                }
            }
        } catch (Exception ex) {
            log.info(ex);
        }
        return result;
    }
    
    public static void main(String[] args) throws Exception {
        
//        String mdn = "0906400309";
//        Calendar nowDateTime = Calendar.getInstance();
//        SimpleDateFormat sdf_pincode = new SimpleDateFormat("MM/dd/yyyy.HH:mm:ss");
//        String tradeDate = sdf_pincode.format(nowDateTime.getTime());
//        String libm = "1234567890";

        //Change LifeCycle
//        String pid = login();
//
//        String LC = getMdnLifeCycle(pid, mdn, pid);
//        System.out.println(mdn + " LC============>" + LC);
//
//        NokiaHLChangeLifeCycleUtil util = new NokiaHLChangeLifeCycleUtil();
//        NokiaResultBean bean = new NokiaResultBean();
//        bean = util.putNokiaChangeLifeCycleOCSlet(libm, pid, mdn, LC);
//        NokiaChangeLifeCycleResultBean xbean = new NokiaChangeLifeCycleResultBean();
//        xbean = util.parseNokiaXMLString(bean.getXmdrecord());
//        System.out.println(xbean.getResult());
//        System.out.println(xbean.getErrorMsg());
//        System.out.println(xbean.getErrorCode());
//
//        boolean result = logout(pid);
//        System.out.println(result);
        //帳本資訊
//        String pid = login();
//
//        NokiaHLBalanceInfoUtil util = new NokiaHLBalanceInfoUtil();
//        NokiaResultBean bean = util.putNokiaBasicInfoOCSlet(libm, pid, mdn);
//        NokiaSubscribeBalanceBean sbean;// = new NokiaSubscribeBalanceBean();
//        sbean = util.parseNokiaXMLString(bean.getXmdrecord());
//        System.out.println("Bean result:" + sbean.getResult() + "," + sbean.getErrorCode() + "," + sbean.getErrorMsg());
//
//        boolean result = logout(pid);
//        System.out.println(result);
        //餘額抵扣，網路儲值 PricePlan Code
//        NokiaECGPricePlanCodeUtil util  = new NokiaECGPricePlanCodeUtil();
//        NokiaResultBean bean;
//        String serviceId="APTWB20010";
//        bean = util.putNokiaPricePlanOCSlet(libm, mdn, serviceId, tradeDate);
//        System.out.println("bean==>"+bean.getHttpstatus());
//        NokiaPricePlanResultBean pbean ;//= new NokiaPricePlanResultBean();
//        pbean = util.parseNokiaXMLString(bean.getXmdrecord());
//        System.out.println("pbean==>"+pbean.getResult_code());
//        System.out.println("pbean==>"+pbean.getStatus());
        // 取得門號狀態LifeCycle & 變更門號狀態
//        String pid = login();
//        String lifecycle = getMdnLifeCycle(libm,mdn,pid);
//        System.out.println("lifecycle==>"+lifecycle);
//
//        NokiaResultBean bean = new NokiaResultBean();
//        NokiaHLChangeLifeCycleUtil util = new NokiaHLChangeLifeCycleUtil();
//        bean = util.putNokiaChangeLifeCycleOCSlet(libm, pid, mdn, lifecycle);
//        
//        NokiaChangeLifeCycleResultBean xbean = new NokiaChangeLifeCycleResultBean();
//        xbean = util.parseNokiaXMLString(bean.getXmdrecord());
//        System.out.println(xbean.getErrorCode());
//        
//        boolean result = logout(pid);
//        System.out.println(result);
        //儲值卡儲值 Pincode 
//        String pincode = "1111111111111111";
//        NokiaPincodeResultBean result = AddPincode(libm, pincode, mdn, tradeDate);
//        System.out.println(result.getStatus());
//        System.out.println(result.getResult_code());
    }
}
