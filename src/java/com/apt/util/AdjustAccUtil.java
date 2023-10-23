/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.util;

import com.apt.epay.beans.BasicInfoReqBean;
import com.apt.epay.beans.BucketReqBean;
import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.epay.ejb.bean.EPAY_BUCKET;
import com.epay.ejb.bean.EPAY_BUCKETHISTORY;
import com.epay.ejb.bean.EPAY_SERVICE_INFO;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;

/**
 *
 * @author kevinchang
 */
public class AdjustAccUtil {

    private static final Logger log = Logger.getLogger("EPAY");
    private static String cpid = new ShareParm().PARM_EPAY_CPID;
    SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);
    ServletContext ctx;

    public AdjustAccUtil() {

    }

    public AdjustAccUtil(ServletContext ctx) {
        this.ctx = ctx;
    }

    public boolean BucketInit(String sid, String libm, String mdn, String tradedate, int ttype) {
        log.info("BucketInit(String sid, String libm, String mdn, String tradedate)" + sid + "," + libm + "," + mdn + "," + tradedate);
        boolean successflag = true;

        try {
            EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", ctx);

            Calendar nowDateTime = Calendar.getInstance();
            OCS4GBasicInfoUtil basicinfoutil = new OCS4GBasicInfoUtil();
            String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());
            String basicinfo = basicinfoutil.putOCS4GBasicInfoSlet(libm, mdn, tradeDate_Pincode);
            BasicInfoReqBean basicinforeqbean = new BasicInfoReqBean();
            basicinforeqbean = basicinfoutil.parseBasicInfoXMLString(basicinfo);
            String ptp_id = basicinforeqbean.getPromotionalTariffPlanCOSP_ID();

            List list = epaybusinesscontroller.getBucketListBySid(sid);
            int count = list.size();
            log.info("list.size====>" + count);
            boolean new7x0 = false;
            boolean new8x0 = false;
            if (list != null && !list.isEmpty()) {
                Iterator it = list.iterator();

                log.info("basicinforeqbean.getID3()==>" + basicinforeqbean.getID3());
                log.info("basicinforeqbean.getID4()==>" + basicinforeqbean.getID4());
                log.info("basicinforeqbean.getID5()==>" + basicinforeqbean.getID5());
                while (it.hasNext()) {
                    EPAY_BUCKET bucket = new EPAY_BUCKET();
                    bucket = (EPAY_BUCKET) it.next();

                    String bucketid = bucket.getBucketId();
                    log.info("bucketid==>" + bucketid);

                    if (bucketid.equals("810")) {
                        if (basicinforeqbean.getID5().equals("") || basicinforeqbean.getID5() == null) {
                            //810帳本不存在，需要新增
                            log.info("Note:The Bucketid 810 is NULL");
                            new8x0 = true;
                        } else {
                            log.info("Note:The Bucketid 810 is Not NULL");
                        }
                    }

                    if (bucketid.equals("720")) {
                        if (basicinforeqbean.getID3().equals("") || basicinforeqbean.getID3() == null) {
                            //720帳本不存在，需要新增
                            log.info("Note:The Bucketid 720 is NULL");
                            new7x0 = true;
                        } else {
                            log.info("Note:The Bucketid 720 is Not NULL");
                        }
                    }

                    if (bucketid.equals("730")) {
                        if (basicinforeqbean.getID4().equals("") || basicinforeqbean.getID4() == null) {
                            //730帳本不存在，需要新增
                            log.info("Note:The Bucketid 730 is NULL");
                            new7x0 = true;
                        } else {
                            log.info("Note:The Bucketid 730 is Not NULL");
                        }
                    }

                }

                EPAY_SERVICE_INFO serviceinfo = epaybusinesscontroller.getServiceInfoById(Long.valueOf(sid), Integer.valueOf(cpid));
                int hr = serviceinfo.getDday() * 24;
                if (new7x0) {
                    log.info("新增7x0帳本，並調整服務到期日");
                    log.info("Noe:We DO Need to Create 7x0 Bucket");
                    BundleUtil newbucket = new BundleUtil();
                    int newhr = hr + 2;
                    newbucket.putBundleOCSlet(libm, mdn, tradedate, "720", newhr, 1, ttype);
                } else {
                    log.info("We Dont Need New 7x0 Bucket");
                }
                if (new8x0) {
                    log.info("新增8x0帳本，並調整服務到期日");
                    log.info("Note:We DO Need to Create 8x0 Bucket");
                    BundleUtil newbucket = new BundleUtil();
                    int newhr = hr + 2;
                    newbucket.putBundleOCSlet(libm, mdn, tradedate, "810", newhr, 2, ttype);
                } else {
                    log.info("We Dont Need New 8x0 Bucket");
                }

                if (AdjustContractExpireDate(libm, mdn, tradedate)) {//合約到期日展延)
                    successflag = true;
                } else {
                    successflag = false;
                    log.info("Error:合約到期日展延Fail");
                }

                if (AdjustMDNActive(libm, mdn, tradedate)) {//MDN Active )
                    successflag = true;
                } else {
                    successflag = false;
                    log.info("Error:MDN Active Fail");
                }

                List listx = epaybusinesscontroller.getBucketListBySid(sid);
                if (listx != null && !listx.isEmpty()) {
                    Iterator itx = listx.iterator();
                    while (itx.hasNext()) {

                        EPAY_BUCKET bucket = new EPAY_BUCKET();
                        bucket = (EPAY_BUCKET) itx.next();
                        String bucketid = bucket.getBucketId();
                        String amount = String.valueOf(bucket.getAmount());
                        AdjustBucket adjustbucket = new AdjustBucket();

                        //將每個儲值帳本記錄至table
                        EPAY_BUCKETHISTORY bucket_history = new EPAY_BUCKETHISTORY();
                        bucket_history.setLibm(libm);
                        bucket_history.setMdn(mdn);
                        bucket_history.setBucketid(bucketid);
                        bucket_history.setAmount(Long.valueOf(amount));
                        epaybusinesscontroller.insertbuckethistory(bucket_history);

                        log.info("adjustbucket.putBucketOCSlet(libm, mdn, tradedate, bucketid, amount, incr)==>" + libm + "," + mdn + "," + tradedate + "," + bucketid + "," + amount + "," + "incr");
                        //帳本Value調整

                        //get tid
                        String cardtype = serviceinfo.getCardtype();
                        BundleDateUtil bdt = new BundleDateUtil();
                        String kk = bdt.getNowDate();
                        String tid = ShareParm.TRANS_ID + kk.substring(2, 4) + cardtype;

                        //get ref                        
                        String bucket_ref = bucket.getRef();

                        String xmlresult = adjustbucket.putBucketOCSlet(libm, mdn, tradedate, bucketid, amount, "incr", sid, ttype, tid, bucket_ref);
                        if (xmlresult != null) {
                            BucketReqBean apirequestbean = new BucketReqBean();
                            apirequestbean = adjustbucket.parseBucketCodeXMLString(xmlresult);
                            String resultcode = apirequestbean.getResultcode();

                            EPAY_TRANSACTION trans = epaybusinesscontroller.getTransaction(libm);
                            trans.setSales_id(String.valueOf(count));
                            trans.setSales_name(resultcode);
                            epaybusinesscontroller.updateTransaction(trans);

                            if (resultcode.equals("00")) {
                                //帳本服務到期日調整
                                if (!new7x0 && (bucketid.equals("720") || bucketid.equals("730"))) {
                                    AdjustServiceExpireDate(libm, mdn, tradedate, bucketid, hr, ptp_id);
                                }
                                //帳本服務到期日調整
                                //parameter value --> tradedate, basicinforeqbean.getEndDate5(), hr
                                if (!new8x0 && bucketid.equals("810")) {
                                    AdjustServiceExpireDate(libm, mdn, tradedate, bucketid, hr, ptp_id);
                                }
                            } else {
                                successflag = false;
                                log.info("Error:Adjust ACC BucketID:" + bucketid + " Fail");
                                break;
                            }
                        }

                    }
                    if (successflag) {
                        EPAY_TRANSACTION transx = epaybusinesscontroller.getTransaction(libm);
                        log.info("=========>" + libm + "==>" + transx.getStatus());
                        transx.setStatus("00");
                        boolean updateflag = epaybusinesscontroller.updateTransaction(transx);
                        
                        EPAY_TRANSACTION transdebug = epaybusinesscontroller.getTransaction(libm);                        
                        log.info("=========>" + libm + "==>" + transdebug.getStatus());
                        if (updateflag) {
                            log.info("BucketInit finished & update successed ===>" + updateflag);
                        } else {
                            log.info("BucketInit finished & update failed ===>" + updateflag);
                        }
                    } else {
                        log.info("BucketInit not finished ===>" + successflag);
                        log.info("The Result is Failed");
                    }
                }

            } else {
                log.info("Cannot find Data (sid,libm)==>(" + sid + ")");
            }
        } catch (Exception ex) {
//            log.info(ex);
            ex.printStackTrace();
        }
        return successflag;
    }

    public boolean AdjustServiceExpireDate(String libm, String mdn, String tradedate, String bucketid, int hr, String ptp_id) {
        boolean result = false;
        try {
            AdjustBucket adjustservicechangeexpiredate = new AdjustBucket();
            String serviceexpiredatexml = adjustservicechangeexpiredate.putChangeServiceExpireDateOCSlet(libm, mdn, tradedate, bucketid, hr, ptp_id);
            if (serviceexpiredatexml != null) {
                BucketReqBean apirequestbean = new BucketReqBean();
                AdjustBucket adjustbucket = new AdjustBucket();
                apirequestbean = adjustbucket.parseBucketCodeXMLString(serviceexpiredatexml);
                String resultcode = apirequestbean.getResultcode();
                if (resultcode.equals("00")) {
                    result = true;
                } else {
                    result = false;
                }
            } else {
                result = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public String GetContractExpireDate(String libm, String mdn, String tradedate) {

        String result = "";
        try {
            SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
            SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);

            Calendar nowDateTime = Calendar.getInstance();
            String libmz = sdf15.format(nowDateTime.getTime());
            OCS4GBasicInfoUtil basicinfoutil = new OCS4GBasicInfoUtil();
            String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());

            String basicinfo = basicinfoutil.putOCS4GBasicInfoSlet(libmz, mdn, tradeDate_Pincode);
            log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfo);

            BasicInfoReqBean basicinforeqbean = new BasicInfoReqBean();
            basicinforeqbean = basicinfoutil.parseBasicInfoXMLString(basicinfo);

            if (!basicinforeqbean.getEndDate3().equals("")) {
                result = TransDateForamt(basicinforeqbean.getEndDate3());
            }

        } catch (Exception ex) {
            log.info(ex);
        }
        return result;
    }

    //adjust 合約到期日
    public boolean AdjustContractExpireDate(String libm, String mdn, String tradedate) {
        boolean result = false;
        try {
            AdjustBucket adjustchangeexpiredate = new AdjustBucket();
            String changeexpirexmlresult = adjustchangeexpiredate.putChangeExpireDateOCSlet(libm, mdn, tradedate);
            if (changeexpirexmlresult != null) {
                BucketReqBean changeapirequestbean = new BucketReqBean();
                changeapirequestbean = adjustchangeexpiredate.parseBucketCodeXMLString(changeexpirexmlresult);
                log.info("adjustchangeexpiredate.parseBucketCodeXMLString(changeexpirexmlresult)==>" + changeexpirexmlresult);
                String changeresultcode = changeapirequestbean.getResultcode();
                if (changeresultcode.equals("00")) {
                    result = true;
                    log.info("ChangeExpireDate MDN Success==>Libm:" + libm + ",mdn:" + mdn);
                } else {
                    result = false;
                    log.info("ChangeExpireDate MDN Fail==>Libm:" + libm + ",mdn:" + mdn);
                }
            } else {
                result = false;
            }
        } catch (Exception ex) {
            log.info(ex);
        }
        return result;
    }

    public String getLifeCycleState(String libm, String mdn, String tradedate) {
        String result="";
         try {
            Calendar nowDateTime = Calendar.getInstance();
            OCS4GBasicInfoUtil basicinfoutil = new OCS4GBasicInfoUtil();
            String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());
            String basicinfo = basicinfoutil.putOCS4GBasicInfoSlet(libm, mdn, tradeDate_Pincode);
            BasicInfoReqBean basicinforeqbean = new BasicInfoReqBean();
            basicinforeqbean = basicinfoutil.parseBasicInfoXMLString(basicinfo);
            String ptp_id = basicinforeqbean.getPromotionalTariffPlanCOSP_ID();

            log.info("basicinforeqbean.getPromotionalTariffPlanCOSP_ID()=====>" + basicinforeqbean.getPromotionalTariffPlanCOSP_ID());
            log.info("basicinforeqbean.getLifeCycleState()====>" + basicinforeqbean.getLifeCycleState());
 
            result = basicinforeqbean.getLifeCycleState();
        } catch (Exception ex) {
            log.info(ex);
        }       
        return result;        
    }
    
    public boolean AdjustMDNActive(String libm, String mdn, String tradedate) {
        boolean result = false;
        try {
            Calendar nowDateTime = Calendar.getInstance();
            OCS4GBasicInfoUtil basicinfoutil = new OCS4GBasicInfoUtil();
            String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());
            String basicinfo = basicinfoutil.putOCS4GBasicInfoSlet(libm, mdn, tradeDate_Pincode);
            BasicInfoReqBean basicinforeqbean = new BasicInfoReqBean();
            basicinforeqbean = basicinfoutil.parseBasicInfoXMLString(basicinfo);
            String ptp_id = basicinforeqbean.getPromotionalTariffPlanCOSP_ID();

            log.info("basicinforeqbean.getPromotionalTariffPlanCOSP_ID()=====>" + basicinforeqbean.getPromotionalTariffPlanCOSP_ID());
            log.info("basicinforeqbean.getLifeCycleState()====>" + basicinforeqbean.getLifeCycleState());
            if (basicinforeqbean.getLifeCycleState().equalsIgnoreCase("ACTIVE")) {
                result = true;
                log.info("MDN:" + mdn + " LifeCycleState is ACTIVE");
            } else {
                log.info("MDN:" + mdn + " LifeCycleState is " + basicinforeqbean.getLifeCycleState());
                result = true;
                /*
                2018/6/26
                改變狀態always失敗，故取消這段功能
                */
//                AdjustBucket adjustbucketactive = new AdjustBucket();
//                String activexmlresult = adjustbucketactive.putBucketActiveOCSlet(libm, mdn, tradedate);
//                if (activexmlresult != null) {
//                    BucketReqBean activeapirequestbean = new BucketReqBean();
//                    activeapirequestbean = adjustbucketactive.parseBucketCodeXMLString(activexmlresult);
//                    log.info("adjustbucketactive.parseBucketCodeXMLString(activexmlresult)==>" + activexmlresult);
//
//                    String activeresultcode = activeapirequestbean.getResultcode();
//                    log.info("activeapirequestbean.getResultcode()==>" + activeapirequestbean.getResultcode());
//                    if (activeresultcode.equals("00")) {
//                        result = true;
//                        log.info("Active MDN Success==>Libm:" + libm + ",mdn:" + mdn);
//                    } else {
//                        result = false;
//                        log.info("Active MDN Fail==>Libm:" + libm + ",mdn:" + mdn);
//                    }
//                }
            }
        } catch (Exception ex) {
            log.info(ex);
        }
        return result;
    }

    public int GetSOAPromotioncode6(String mdn) {
        int promotioncode6 = 0;

        try {
            SoaProfile soa = new SoaProfile();
            String result = soa.putSoaProxyletByMDN(mdn);
            SOAReqBean apirequestbean = new SOAReqBean();
            apirequestbean = soa.parseXMLString(result);
            String promotioncode = apirequestbean.getPromotioncode();
            promotioncode6 = Integer.valueOf(promotioncode.substring(5, 6));

        } catch (Exception ex) {
            log.info(ex);
        }
        return promotioncode6;
    }

    public int GetMdnCardType(String mdn) {
        int cardtype = 0;
        //計量型: value = 1
        //計日型: value = 2
        try {
            SoaProfile soa = new SoaProfile();
            String result = soa.putSoaProxyletByMDN(mdn);
            SOAReqBean apirequestbean = new SOAReqBean();
            apirequestbean = soa.parseXMLString(result);
            String promotioncode = apirequestbean.getPromotioncode();
            int temp_cardtype = Integer.valueOf(promotioncode.substring(5, 6));
            log.info("promotioncode.substring(5, 6)==>" + promotioncode.substring(5, 6));
            if (temp_cardtype == 1 || temp_cardtype == 3) {
                cardtype = 1;
            }

            if (temp_cardtype == 2 || temp_cardtype == 4) {
                cardtype = 2;
            }

        } catch (Exception ex) {
            log.info(ex);
        }

        //DayPass測試，所以通通回傳1
//        return cardtype;
        return 1;
    }

    public boolean VerifyUserStatus(String contractstatuscode, String promotioncode) {
        boolean result = false;
        log.info("contractstatuscode==>" + contractstatuscode);
        log.info("promotioncode=======>" + promotioncode);
        if (promotioncode != null) {
            if (contractstatuscode.equals("9") || contractstatuscode.equals("43")) {
                if (promotioncode.equals("PPP") || promotioncode.equals("P4G") || promotioncode.equals("D3G")) {
                    result = true;
                }
            } else {
                log.info("contractstatuscode==>" + contractstatuscode);
                log.info("promotioncode=======>" + promotioncode);
                log.info("The Pincode User Check is False");
            }
        } else {
            log.info("contractstatuscode==>" + contractstatuscode);
            log.info("promotioncode=======>" + promotioncode);
            log.info("The Pincode User Check is False");
        }
        return result;
    }

    public boolean AdjustAccBucketInit(String sid, String libm, String mdn, String tradedate, int ttype) {
        log.info("BucketInit(String sid, String libm, String mdn, String tradedate)" + sid + "," + libm + "," + mdn + "," + tradedate);
        boolean successflag = true;
        EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        try {
            Calendar nowDateTime = Calendar.getInstance();
            OCS4GBasicInfoUtil basicinfoutil = new OCS4GBasicInfoUtil();
            String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());
            String basicinfo = basicinfoutil.putOCS4GBasicInfoSlet(libm, mdn, tradeDate_Pincode);
            BasicInfoReqBean basicinforeqbean = new BasicInfoReqBean();
            basicinforeqbean = basicinfoutil.parseBasicInfoXMLString(basicinfo);
            String ptp_id = basicinforeqbean.getPromotionalTariffPlanCOSP_ID();

            List list = epaybusinesscontroller.getBucketListBySid(sid);
            int count = list.size();
            boolean new7x0 = false;
            boolean new8x0 = false;
            if (list != null && !list.isEmpty()) {
                Iterator it = list.iterator();

                log.info("basicinforeqbean.getID3()==>" + basicinforeqbean.getID3());
                log.info("basicinforeqbean.getID4()==>" + basicinforeqbean.getID4());
                log.info("basicinforeqbean.getID5()==>" + basicinforeqbean.getID5());
                while (it.hasNext()) {
                    EPAY_BUCKET bucket = new EPAY_BUCKET();
                    bucket = (EPAY_BUCKET) it.next();

                    String bucketid = bucket.getBucketId();
                    log.info("bucketid==>" + bucketid);

                    if (bucketid.equals("810")) {
                        if (basicinforeqbean.getID5().equals("") || basicinforeqbean.getID5() == null) {
                            //810帳本不存在，需要新增
                            log.info("Note:The Bucketid 810 is NULL");
                            new8x0 = true;
                        } else {
                            log.info("Note:The Bucketid 810 is Not NULL");
                        }
                    }

                    if (bucketid.equals("720")) {
                        if (basicinforeqbean.getID3().equals("") || basicinforeqbean.getID3() == null) {
                            //720帳本不存在，需要新增
                            log.info("Note:The Bucketid 720 is NULL");
                            new7x0 = true;
                        } else {
                            log.info("Note:The Bucketid 720 is Not NULL");
                        }
                    }

                    if (bucketid.equals("730")) {
                        if (basicinforeqbean.getID4().equals("") || basicinforeqbean.getID4() == null) {
                            //730帳本不存在，需要新增
                            log.info("Note:The Bucketid 730 is NULL");
                            new7x0 = true;
                        } else {
                            log.info("Note:The Bucketid 730 is Not NULL");
                        }
                    }

                }

                EPAY_SERVICE_INFO serviceinfo = epaybusinesscontroller.getServiceInfoById(Long.valueOf(sid), Integer.valueOf(cpid));
                int hr = serviceinfo.getDday() * 24;
                if (new7x0) {
                    log.info("新增7x0帳本，並調整服務到期日");
                    log.info("Noe:We DO Need to Create 7x0 Bucket");
                    BundleUtil newbucket = new BundleUtil();
                    int newhr = hr + 2;
                    newbucket.putBundleOCSlet(libm, mdn, tradedate, "720", newhr, 1, ttype);
                } else {
                    log.info("We Dont Need New 7x0 Bucket");
                }
                if (new8x0) {
                    log.info("新增8x0帳本，並調整服務到期日");
                    log.info("Note:We DO Need to Create 8x0 Bucket");
                    BundleUtil newbucket = new BundleUtil();
                    int newhr = hr + 2;
                    newbucket.putBundleOCSlet(libm, mdn, tradedate, "810", newhr, 2, ttype);
                } else {
                    log.info("We Dont Need New 8x0 Bucket");
                }

//                if (AdjustContractExpireDate(libm, mdn, tradedate)) {//合約到期日展延)
//                    suucessflag = true;
//                } else {
//                    suucessflag = false;
//                    log.info("Error:合約到期日展延Fail");
//                }
//
//                if (AdjustMDNActive(libm, mdn, tradedate)) {//MDN Active )
//                    suucessflag = true;
//                } else {
//                    suucessflag = false;
//                    log.info("Error:MDN Active Fail");
//                }
                List listx = epaybusinesscontroller.getBucketListBySid(sid);
                if (listx != null && !listx.isEmpty()) {
                    Iterator itx = listx.iterator();
                    while (itx.hasNext()) {

                        EPAY_BUCKET bucket = new EPAY_BUCKET();
                        bucket = (EPAY_BUCKET) itx.next();
                        String bucketid = bucket.getBucketId();
                        String amount = String.valueOf(bucket.getAmount());

                        if (amount.equals("0")) {
                            log.info("In the table configure , The BucketID " + bucketid + " amount is 0");
                        } else {
                            AdjustBucket adjustbucket = new AdjustBucket();

                            //將每個儲值帳本記錄至table
                            EPAY_BUCKETHISTORY bucket_history = new EPAY_BUCKETHISTORY();
                            bucket_history.setLibm(libm);
                            bucket_history.setMdn(mdn);
                            bucket_history.setBucketid(bucketid);
                            bucket_history.setAmount(Long.valueOf(amount));
                            epaybusinesscontroller.insertbuckethistory(bucket_history);

                            log.info("adjustbucket.putBucketOCSlet(libm, mdn, tradedate, bucketid, amount, incr)==>" + libm + "," + mdn + "," + tradedate + "," + bucketid + "," + amount + "," + "incr");
                            //帳本Value調整

                            //get tid
                            String cardtype = serviceinfo.getCardtype();
                            BundleDateUtil bdt = new BundleDateUtil();
                            String kk = bdt.getNowDate();
                            String tid = ShareParm.TRANS_ID + kk.substring(2, 4) + cardtype;
                            //get ref
                            String bucket_ref = bucket.getRef();

                            String xmlresult = adjustbucket.putBucketOCSlet(libm, mdn, tradedate, bucketid, amount, "incr", sid, ttype, tid, bucket_ref);
                            if (xmlresult != null) {
                                BucketReqBean apirequestbean = new BucketReqBean();
                                apirequestbean = adjustbucket.parseBucketCodeXMLString(xmlresult);
                                String resultcode = apirequestbean.getResultcode();

                                EPAY_TRANSACTION trans = epaybusinesscontroller.getTransaction(libm);
                                trans.setSales_id(String.valueOf(count));
                                trans.setSales_name(resultcode);
                                epaybusinesscontroller.updateTransaction(trans);

                                if (resultcode.equals("00")) {
                                    //帳本服務到期日調整

                                    if (!new7x0 && bucketid.equals("720")) {
                                        String expiredate720 = basicinforeqbean.getEndDate3();
                                        if (ExpireDateDiff(expiredate720, hr)) {
                                            log.info("帳本服務到期日調整==>720帳本" + expiredate720 + ",tradeDate:" + tradedate + ",hr:" + hr);
                                            AdjustServiceExpireDate(libm, mdn, tradedate, bucketid, hr, ptp_id);
                                        } else {
                                            log.info("服務到期日大於交易日+展延天數==>720帳本" + expiredate720 + ",tradeDate:" + tradedate + ",hr:" + hr);
                                        }
                                    }

                                    if (!new7x0 && bucketid.equals("730")) {
                                        String expiredate730 = basicinforeqbean.getEndDate4();
                                        if (ExpireDateDiff(expiredate730, hr)) {
                                            log.info("帳本服務到期日調整==>730帳本" + expiredate730 + ",tradeDate:" + tradedate + ",hr:" + hr);
                                            AdjustServiceExpireDate(libm, mdn, tradedate, bucketid, hr, ptp_id);
                                        } else {
                                            log.info("服務到期日大於交易日+展延天數==>730帳本" + expiredate730 + ",tradeDate:" + tradedate + ",hr:" + hr);
                                        }
                                    }
                                    //帳本服務到期日調整

                                    if (!new8x0 && bucketid.equals("810")) {
                                        String expiredate810 = basicinforeqbean.getEndDate5();
                                        if (ExpireDateDiff(expiredate810, hr)) {
                                            log.info("帳本服務到期日調整==>810帳本" + expiredate810 + ",tradeDate:" + tradedate + ",hr:" + hr);
                                            AdjustServiceExpireDate(libm, mdn, tradedate, bucketid, hr, ptp_id);
                                        } else {
                                            log.info("服務到期日大於交易日+展延天數==>810帳本" + expiredate810 + ",tradeDate:" + tradedate + ",hr:" + hr);
                                        }
                                    }
                                } else {
                                    successflag = false;
                                    log.info("Error:Adjust ACC BucketID:" + bucketid + " Fail");
                                    break;
                                }
                            }
                        }

                    }
                    if (successflag) {
                        EPAY_TRANSACTION transx = epaybusinesscontroller.getTransaction(libm);
                        transx.setStatus("00");
                        epaybusinesscontroller.updateTransaction(transx);
                    } else {
                        log.info("The Result is Failed");
                    }
                }
            } else {
                log.info("Cannot find Data (sid,libm)==>(" + sid + ")");
            }
        } catch (Exception ex) {
            log.info(ex);
        }
        return successflag;
    }

    private String TransDateForamt(String datestr) {

        String result = "";

        try {
            SimpleDateFormat sdf17 = new java.text.SimpleDateFormat(ShareParm.PARM_DATEFORMAT7);
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
            if (datestr.equals("")) {
                result = "";
            } else {
                Date date = sdf.parse(datestr);
                result = sdf17.format(date);
            }

        } catch (Exception ex) {
            log.info(ex);
        }
        return result;

    }

    public boolean ExpireDateDiff(String expiredate, int hr) {
        boolean result = false;

        //parameter value --> tradedate, basicinforeqbean.getEndDate5(), hr
        //服務到期日 減去 交易日  < 展延天數 回傳true 表示要異動服務到期日
        BundleDateUtil bdt = new BundleDateUtil();
        SimpleDateFormat formatDatex = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String d1 = TransDateForamt(expiredate);

        int kk = bdt.GetDiffDayOfContractDay(d1) * 24;
        log.info("bdt.GetDiffDayOfContractDay(" + d1 + ")===>" + kk);

        if (kk < hr) {
            //服務到期日 減去 交易日  < 展延天數 回傳true 表示要異動服務到期日
            log.info("ExpireDateDiff: expirdate,hr,diffvalue====>" + d1 + "," + hr + "," + kk);
            result = true;
        } else {
            log.info("ExpireDateDiff: expirdate,hr,diffvalue====>" + expiredate + "," + hr + "," + kk);
            result = false;
        }

        return result;
    }

}
