/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.nokia.main;

import com.apt.epay.nokia.bean.NokiaChangeLifeCycleResultBean;
import com.apt.epay.nokia.bean.NokiaPincodeResultBean;
import com.apt.epay.nokia.bean.NokiaPricePlanResultBean;
import com.apt.epay.nokia.bean.NokiaResultBean;
import com.apt.epay.nokia.util.NokiaECGPinCodeUtil;
import com.apt.epay.nokia.util.NokiaECGPricePlanCodeUtil;
import com.apt.epay.nokia.util.NokiaHLChangeLifeCycleUtil;
import com.apt.epay.share.ShareParm;
import com.apt.util.MailUtil;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.log4j.Logger;

/**
 *
 * @author kevinchang
 */
public class NokiaMainPricePlanCodeUtil {

    private static final Logger log = Logger.getLogger("EPAY");

    public NokiaPricePlanResultBean AddMainPricePlanCode(String promotion_type3, String libm, String mdn, String priceplancode, String tradeDate, int Rchg_Type) {

        NokiaPricePlanResultBean bean = null;

        log.info(mdn + " Nokia==>AddMainPricePlanCode:" + promotion_type3 + "," + libm);
        log.info(mdn + " Nokia==>AddMainPricePlanCode:" + priceplancode);
        log.info(mdn + " Nokia(AIR,TRV)=============>" + promotion_type3);

        try {

            NokiaMainLoginAndLifeCycleUtil util = new NokiaMainLoginAndLifeCycleUtil();
            String pid = util.login();
            log.info(mdn + " Get Nokia Pid Login:" + pid);

            if (!"".equals(pid)) {

                String LC = util.getMdnLifeCycle(libm, mdn, pid);
                log.info(mdn + " Nokia Lifecycle status ==>" + LC);

                if (LC != null) {
                    if (LC.length() > 0) {

                        if (Rchg_Type == 1) {
                            log.info(mdn + " 網路儲值  Rchg_Type==>" + Rchg_Type);
                        } else if (Rchg_Type == 2) {
                            log.info(mdn + " 餘額抵扣  Rchg_Type==>" + Rchg_Type);
                        }

                        if ("Preactive".equalsIgnoreCase(LC)) {

                            //變更成為Preactive1
                            log.info(mdn + " Rchg_Type===>" + Rchg_Type);
                            log.info(mdn + " ############# 狀態為 " + LC + " ##################");
                            log.info(mdn + " ############# 要改LC狀態 #########################");
                            log.info(mdn + " Nokia LC ==>" + LC);
                            String newLC = "Preactive1";

                            NokiaHLChangeLifeCycleUtil change_util = new NokiaHLChangeLifeCycleUtil();
                            NokiaResultBean change_bean;// = new NokiaResultBean();
                            change_bean = change_util.putNokiaChangeLifeCycleOCSlet(libm, pid, mdn, newLC);
                            NokiaChangeLifeCycleResultBean xbean;// = new NokiaChangeLifeCycleResultBean();
                            xbean = change_util.parseNokiaXMLString(change_bean.getXmdrecord());

                            if ("0".equalsIgnoreCase(xbean.getErrorCode())) {

                                log.info(mdn + " #################################################");
                                log.info(mdn + " #################################################");
                                log.info(mdn + " ############# 狀態為 " + LC + " #################");
                                log.info(mdn + " ############ 改LC狀態成功 ，準備進行儲值 ########");

                                log.info(mdn + " Nokia Change Lifecycle Success:" + xbean.getErrorCode() + "," + xbean.getErrorMsg() + "," + xbean.getResult());
                                bean = AddPricePlanCode(libm, mdn, priceplancode, tradeDate, Rchg_Type);

                            } else {
                                //變更LC失敗
                                log.info(mdn + " #################################################");
                                log.info(mdn + " ############# 狀態為 " + LC + " ####################");
                                log.info(mdn + " ############# 改LC狀態失敗 #######################");

                                log.info(mdn + " Nokia Change Lifecycle fail:" + xbean.getErrorCode() + "," + xbean.getErrorMsg() + "," + xbean.getResult());
                                bean.setResult_code("-1");
                                bean.setStatus("Change Lifecycle fail:" + xbean.getErrorMsg());

                                //發送告警簡訊/Email
                                String xmsg = "Nokia OCS 變更門號LifeCycle失敗：用戶" + mdn + "，訂單LIB:" + libm + "," + promotion_type3 + "，請查修,ErrorMsg=>" + xbean.getErrorMsg();
                                String email_form = new ShareParm().PARM_MAIL_FROM;
                                String dst_user = new ShareParm().PARM_MAIL_TO_OC + ";" + new ShareParm().PARM_MAIL_TO_4GOCS;
                                MailUtil.sendMail(new ShareParm().PARM_MAIL_RELAY_HOST, email_form, dst_user, xmsg, xmsg);

                            }
                        } else {
                            
                            log.info(mdn + " LC IN (Active, Expired, PreActive1)");
                            log.info(mdn + " ####################################################");
                            log.info(mdn + " ############# 狀態為 " + LC + " ####################");
                            log.info(mdn + " ####################################################");

                            bean = AddPricePlanCode(libm, mdn, priceplancode, tradeDate, Rchg_Type);

                        }
                    } else {
                        //取得LC失敗
                        log.info(mdn + " Nokia Lifecycle wrong value:" + LC);
                        bean.setResult_code("-1");
                        bean.setStatus("Lifecycle wrong:" + LC);

                    }
                } else {
                    //取得LC失敗
                    log.info(mdn + " Nokia Lifecycle wrong value:" + LC);
                    bean.setResult_code("-1");
                    bean.setStatus("Lifecycle wrong:" + LC);
                }
                boolean logoutflag = util.logout(pid);
                log.info(mdn + " Logout Nokia OCS PID result(" + pid + "):" + logoutflag);
            } else {
                //取得PID失敗
                log.info(mdn + " Get Nokia Login Fail");
                bean.setResult_code("-1");
                bean.setStatus("Nokia Login Fail");
            }

        } catch (Exception ex) {
            log.info(ex);
        }

        return bean;
    }

    public NokiaPricePlanResultBean AddPricePlanCode(String libm, String mdn, String priceplancode, String tradeDate, int Rchg_Type) {

        NokiaPricePlanResultBean pbean = new NokiaPricePlanResultBean();
        try {
            NokiaECGPricePlanCodeUtil util = new NokiaECGPricePlanCodeUtil();
            NokiaResultBean resultbean = util.putNokiaPricePlanOCSlet(libm, mdn, priceplancode, tradeDate, Rchg_Type);
            pbean = util.parseNokiaXMLString(resultbean.getXmdrecord());
        } catch (Exception ex) {
            log.info(ex);
        }
        return pbean;
    }

    public static void main(String[] args) throws Exception {

//        String mdn = "0905001003";
//        Calendar nowDateTime = Calendar.getInstance();
//        SimpleDateFormat sdf_pincode = new SimpleDateFormat("MM/dd/yyyy.HH:mm:ss");
//        String tradeDate = sdf_pincode.format(nowDateTime.getTime());
//        String libm = "1234567890";
//        String priceplancode = "APTWB20010";

//        NokiaPricePlanResultBean pbean = AddMainPricePlanCode("AIR", libm, mdn, priceplancode, tradeDate);
//        System.out.println(pbean.getResult_code());
//        System.out.println(pbean.getStatus());
    }
}
