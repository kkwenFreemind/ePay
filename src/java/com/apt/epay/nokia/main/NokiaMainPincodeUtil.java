/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.nokia.main;

import com.apt.epay.nokia.bean.NokiaChangeLifeCycleResultBean;
import com.apt.epay.nokia.bean.NokiaPincodeResultBean;
import com.apt.epay.nokia.bean.NokiaResultBean;
import com.apt.epay.nokia.util.NokiaECGPinCodeUtil;
import com.apt.epay.nokia.util.NokiaHLChangeLifeCycleUtil;
import com.apt.epay.share.ShareParm;
import com.apt.util.MailUtil;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;

/**
 *
 * @author kevinchang
 */
public class NokiaMainPincodeUtil {

    private static final Logger log = Logger.getLogger("EPAY");

    public NokiaPincodeResultBean AddMainPincode(String promotion_type3, String libm, String mdn, String pincode, String tradedate) {

        NokiaPincodeResultBean bean = new NokiaPincodeResultBean();
        log.info(mdn + " Nokia Promotion Code==>" + promotion_type3);
        try {
//            if ("AIR".equalsIgnoreCase(promotion_type3) || "TRV".equalsIgnoreCase(promotion_type3)) {

            NokiaMainLoginAndLifeCycleUtil util = new NokiaMainLoginAndLifeCycleUtil();
            String pid = util.login();
            log.info(mdn + " Get Nokia OCS Pid:" + pid);

            if (!"".equals(pid)) {

                String LC = util.getMdnLifeCycle(libm, mdn, pid);

                log.info("AddMainPincode===>" + mdn + " Lifecycle status ==>" + LC + " PromotionCode(3)==>" + promotion_type3);
                if (LC != null) {
                    if (LC.length() > 0) {
                        if ("Preactive".equalsIgnoreCase(LC)) {
                            //變更成為Preactive1
                            log.info(mdn + " ##################################################");
                            log.info(mdn + " ############# 狀態為 " + LC + " ####################");
                            log.info(mdn + " ############# 要改LC狀態 #########################");
                            log.info(mdn + " Nokia LC ==>" + LC);
                            String newLC = "Preactive1";
                            NokiaHLChangeLifeCycleUtil change_util = new NokiaHLChangeLifeCycleUtil();
                            NokiaResultBean change_bean;// = new NokiaResultBean();

                            change_bean = change_util.putNokiaChangeLifeCycleOCSlet(libm, pid, mdn, newLC);
                            NokiaChangeLifeCycleResultBean xbean;// = new NokiaChangeLifeCycleResultBean();
                            xbean = change_util.parseNokiaXMLString(change_bean.getXmdrecord());

                            if ("0".equalsIgnoreCase(xbean.getErrorCode())) {
                                log.info(mdn + " ################################################");
                                log.info(mdn + " ############# 狀態為 " + LC + " ################");
                                log.info(mdn + " ############# 要改LC狀態 #######################");
                                log.info(mdn + " #############準備進行儲值 ######################");
                                log.info(mdn + " Change Lifecycle Success:" + xbean.getErrorCode() + "," + xbean.getErrorMsg() + "," + xbean.getResult());
                                bean = AddPincode(libm, pincode, mdn, tradedate);

                            } else {
                                //變更LC失敗
                                log.info(mdn + " ###############################################################");
                                log.info(mdn + " ############# 變更狀態失敗#####################################");
                                log.info(mdn + " ###############################################################");
                                log.info(mdn + " Change Lifecycle fail:" + xbean.getErrorCode() + "," + xbean.getErrorMsg() + "," + xbean.getResult());
                                bean.setResult_code("-1");
                                bean.setStatus("Change Lifecycle fail:" + xbean.getErrorMsg());

                                //發送告警簡訊/Email
                                String xmsg = "Nokia OCS 變更門號LifeCycle失敗：用戶" + mdn + "，訂單LIB:" + libm + "，請查修,ErrorMsg=>" + xbean.getErrorMsg();
                                String email_form = new ShareParm().PARM_MAIL_FROM;
                                String dst_user = new ShareParm().PARM_MAIL_TO_OC + ";" + new ShareParm().PARM_MAIL_TO_4GOCS;
                                MailUtil.sendMail(new ShareParm().PARM_MAIL_RELAY_HOST, email_form, dst_user, xmsg, xmsg);

                            }
                        } else {
                            log.info(mdn + " LC IN (Active, Expired, PreActive1)");
                            log.info(mdn + " ####################################################");
                            log.info(mdn + " ############# 狀態為 " + LC + " ####################");
                            log.info(mdn + " ###### 不須改變LC狀態，直接準備進行儲值 ############");
                            log.info(mdn + " ####################################################");

                            log.info(mdn + " Nokia LC ==>" + LC);
                            bean = AddPincode(libm, pincode, mdn, tradedate);

                        }
                    } else {
                        //取得LC失敗
                        log.info(mdn + " Get Lifecycle wrong value:" + LC);
                        bean.setResult_code("-1");
                        bean.setStatus("Lifecycle wrong:" + LC);
                    }
                } else {
                    //取得LC失敗
                    log.info(mdn + " Lifecycle wrong value:" + LC);
                    bean.setResult_code("-1");
                    bean.setStatus("Lifecycle wrong:" + LC);
                }
                boolean logoutflag = util.logout(pid);
                log.info(mdn + " Logout Nokia OCS PID result(" + pid + "):" + logoutflag);
            } else {
                //取得PID失敗
                log.info(mdn + " DidNOT Get Nokia OCS Pid :" + pid);
                bean.setResult_code("-1");
                bean.setStatus("OCS Login Fail:" + pid);
            }
        } catch (Exception ex) {
            log.info(ex);
        }
        return bean;
    }

    public NokiaPincodeResultBean AddPincode(String libm, String pincode, String mdn, String tradedate) {
        NokiaPincodeResultBean pincodebean = new NokiaPincodeResultBean();
        NokiaECGPinCodeUtil util = new NokiaECGPinCodeUtil();
        NokiaResultBean bean;

        try {
            bean = util.putNokiaPincodeOCSlet(libm, mdn, pincode, tradedate);

            if (bean.getHttpstatus() == HttpStatus.SC_OK) {
                pincodebean = util.parseNokiaXMLString(bean.getXmdrecord());
                log.info(mdn + " Pincode Result status:" + pincodebean.getStatus());
                log.info(mdn + " Pincode Result code:" + pincodebean.getResult_code());
            }
        } catch (Exception ex) {
            log.info(ex);
        }
        return pincodebean;
    }

    public static void main(String[] args) throws Exception {

//        Calendar nowDateTime = Calendar.getInstance();
//        SimpleDateFormat sdf_pincode = new SimpleDateFormat("MM/dd/yyyy.HH:mm:ss");
//        String tradeDate = sdf_pincode.format(nowDateTime.getTime());
//        String libm = "1234567890";
//        String pincode = "1111111111111111";
//        NokiaPincodeResultBean bean = new NokiaPincodeResultBean();
//        bean = addPincode("APT", libm, mdn, pincode, tradeDate);
//        System.out.println(bean.getResult_code());
//        System.out.println(bean.getStatus());
    }
}
