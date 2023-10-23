package com.apt.web.api;

import com.apt.epay.beans.BasicInfoReqBean;
import com.apt.epay.beans.BucketReqBean;
import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.util.AdjustAccUtil;
import com.apt.util.AdjustBucket;
import com.apt.util.BundleDateUtil;
import com.apt.util.OCS4GBasicInfoUtil;
import com.apt.util.SoaProfile;
import com.epay.ejb.bean.EPAY_BUCKET;
import com.epay.ejb.bean.EPAY_BUCKETHISTORY;
import com.epay.ejb.bean.EPAY_SERVICE_INFO;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * 線上儲值交易結果通知
 *
 * @author Administrator
 */
public class VoiceBucketCleanForiCallReq extends HttpServlet {

//    private static final long serialVersionUID = -8151109249141501757L;
    private static final Logger log = Logger.getLogger("EPAY");

    private final String cpid = new ShareParm().PARM_EPAY_CPID;

    private String mdn;
    private String promotionCode;
    private String adjustflag;

    private String libm = "";
    private String contractid;
    private String contractstatuscode;
    private String promotioncode;
    private String producttype;
    private String name;

    private String status;
    private String errCode;
    private String errDesc;
//    private String install;
    private String privateData;
    private String authDate;

    final String identifyCode = new ShareParm().PARM_PG_IDENT;
    final String key = new ShareParm().PARM_PG_KEY;
    final String md5Param = "&identifyCode=";
    final String desParam = "&returnOutMac=";

    SimpleDateFormat sdf8 = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT8);
    SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
    SimpleDateFormat sdf22 = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT22);
    SimpleDateFormat sdf3 = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT3);
    SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

//        String msg = new String(request.getParameter("Msg").getBytes("ISO-8859-1"), "UTF-8");
        String logMsg = "Client IP :" + request.getRemoteAddr() + ", getQueryString():" + request.getQueryString();
        String ibonresult = request.getQueryString();
        log.info(new String(logMsg.getBytes("ISO-8859-1"), "UTF-8"));

        mdn = request.getParameter("mdn");
        promotionCode = request.getParameter("promotionCode");
        adjustflag = request.getParameter("adjustflag");

        ServletOutputStream output = response.getOutputStream();
        response.setContentType("text/html");

        /*@@@@@ TransactionRec decodeedText 
         mdn=0982158008&procode=00
         */
        try {

            if (mdn != null) {
                SoaProfile soa = new SoaProfile();
                String result = soa.putSoaProxyletByMDN(mdn);
                SOAReqBean apirequestbean = new SOAReqBean();
                apirequestbean = soa.parseXMLString(result);

                SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);
                Calendar nowDateTime = Calendar.getInstance();
                String libm = sdf15.format(nowDateTime.getTime());
                String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());

                //金額取得
                OCS4GBasicInfoUtil basicinfoutilx = new OCS4GBasicInfoUtil();
                String basicinfox = basicinfoutilx.putOCS4GBasicInfoSlet(libm, mdn, tradeDate_Pincode);
                log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfox);

                BasicInfoReqBean basicinforeqbeanx = new BasicInfoReqBean();
                basicinforeqbeanx = basicinfoutilx.parseBasicInfoXMLString(basicinfox);

                contractid = apirequestbean.getContractid();
                contractstatuscode = apirequestbean.getContract_status_code();
                producttype = apirequestbean.getProducttype();
                mdn = apirequestbean.getMdn();

                if ((apirequestbean.getPromotioncode() != null) || (!apirequestbean.getPromotioncode().equals(""))) {
                    promotioncode = apirequestbean.getPromotioncode().substring(0, 3);
                } else {
                    log.info("VoiceBucketClean For ICall Exception==> The REAL promotioncode is" + apirequestbean.getPromotioncode());
                }

                log.info("promotionCode===>" + apirequestbean.getPromotioncode() + "," + promotionCode);

                if (apirequestbean.getPromotioncode().equals(promotionCode)) {
                    if (!adjustflag.equals("y")) {
                        output.print("0" + "," + mdn + "," + "1," + "VoiceBucketClean promotioncode Check Succss");
                        log.info("VoiceBucketClean AdjustAccPoc==>VoiceBucketClean promotioncode Check Succss");
                    }
                    
                    if (adjustflag.equals("y")) {
                        AdjustAccUtil adaccutil = new AdjustAccUtil();
                        int cardtype = adaccutil.GetMdnCardType(mdn);
                        boolean verifyUserStatus = adaccutil.VerifyUserStatus(contractstatuscode, promotioncode);

                        if ((producttype.equals("P4G")) || (producttype.equals("D3G"))) {
                            String xserviceid = "1000120001";
                            EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", this.getServletContext());
                            EPAY_SERVICE_INFO serviceinfo = epaybusinesscontroller.getServiceInfoById(Long.valueOf(xserviceid), Integer.valueOf(cpid));
                            String itemName = serviceinfo.getServiceName();
                            String itemUnitPrice = serviceinfo.getPrice().toString();
                            String itemCode = serviceinfo.getGlcode();
                            String note = serviceinfo.getNote();
                            log.info("ServiceInfo===>" + itemName + "," + itemUnitPrice + "," + itemCode + "," + note);

                            String contactCellPhone = mdn;//聯絡手機號碼
                            int tradeAmount = Integer.valueOf(itemUnitPrice);
                            int tradeQuantity = 1;

                            String orderTotal = String.valueOf(tradeAmount * tradeQuantity);
                            String tradeDate = sdf.format(nowDateTime.getTime());

                            //transaction insert
                            EPAY_TRANSACTION trans = epaybusinesscontroller.getTransaction(libm);
                            if (trans == null && !"".equalsIgnoreCase(libm) && !"null".equalsIgnoreCase(libm)) {
                                trans = new EPAY_TRANSACTION();
                                trans.setLibm(libm);
                                trans.setItemcode(itemCode);
                                trans.setItemproductname(itemName);
                                trans.setItemunitprice(Integer.parseInt(itemUnitPrice));//0
                                trans.setItemquantity(tradeQuantity);//1
                                trans.setOrdertotal(Integer.parseInt(orderTotal));//0
                                trans.setFee(0);
                                trans.setDiscount(0);
                                trans.setTradedate(sdf.parse(tradeDate));
                                trans.setPaymethod(ShareParm.PAYMETHOD_VoiceBucketCleanForiCallACC); //付款方式 信用卡:value = 1 
                                trans.setStatus("N"); //OCS尚未儲值完成
//                trans.setPaystatus(0); //無須繳費
                                trans.setPayamount(Integer.parseInt(orderTotal));
                                trans.setPrivatedata(libm); //PinCode number

                                trans.setServiceId(xserviceid);//
                                trans.setCpLibm(libm);
                                trans.setCpId(Integer.parseInt(new ShareParm().PARM_EPAY_CPID));//
                                trans.setFeeType("0"); //無拆帳需求
                                trans.setInvoiceContactMobilePhone(contactCellPhone);
                                trans.setContractID(contractid);

                                log.info("Voice Bucket Clean Proce(insert EPAY_TRANSACTION Table)==>MDN:" + mdn + ",Libm:" + libm);
                                epaybusinesscontroller.insertTransaction(trans);
                                /*
                                 查詢4G OCS的 620帳本餘額                
                                 */
                                String sid = trans.getServiceId();
                                String mdn = trans.getInvoiceContactMobilePhone();
                                String tradedate = sdf_pincode.format(trans.getTradedate());
                                String libmx = trans.getLibm();
                                String ref = trans.getLibm();

                                OCS4GBasicInfoUtil basicinfoutil = new OCS4GBasicInfoUtil();
                                String basicinfo = basicinfoutil.putOCS4GBasicInfoSlet(libm, mdn, tradeDate_Pincode);
                                log.info("QueryBasicInfo===>MDN:" + mdn + " OCSXmlResult=>" + basicinfo);

                                BasicInfoReqBean basicinforeqbean = new BasicInfoReqBean();
                                basicinforeqbean = basicinfoutil.parseBasicInfoXMLString(basicinfo);

                                String LifeCycleState = basicinforeqbean.getLifeCycleState();
                                String xmlresult = "";
                                String resultcode = "";
                                boolean flag = true;
                                AdjustBucket adjustbucket = new AdjustBucket();
                                int ttype = ShareParm.TTYPE_ADJUSTACCOUNT;

                                if (!LifeCycleState.equals("PREACTIVE")) {

                                    String count2 = basicinforeqbean.getCounterValue2();
                                    log.info("BucketOCSlet==>" + libm + "," + mdn + "," + tradedate + ",620," + count2 + "," + ref + ",decr");

                                    //get tid
                                    String cardtype620 = serviceinfo.getCardtype();
                                    BundleDateUtil bdt620 = new BundleDateUtil();
                                    String kk620 = bdt620.getNowDate();
                                    String tid620 = ShareParm.TRANS_ID + kk620.substring(2, 4) + cardtype620;
                                    log.info("tid ===>" + tid620);
                                    //get ref
                                    EPAY_BUCKET bucket_ref620 = new EPAY_BUCKET();
                                    bucket_ref620 = epaybusinesscontroller.getBucketListBySidAndBid(sid, "620");
                                    String ref620 = bucket_ref620.getRef();
                                    log.info("ref620 ===>" + ref620);

                                    xmlresult = adjustbucket.putBucketOCSlet(libm, mdn, tradedate, "620", count2, "decr", sid, ttype, tid620, ref620);

                                    log.info("VoiceBucketClean AdjustAccPoc==>Libm:" + libm + ",AdjustAmt:" + serviceinfo.getPrice() + ",ACC620:" + count2);

                                    BucketReqBean apirequestbean620 = new BucketReqBean();
                                    apirequestbean620 = adjustbucket.parseBucketCodeXMLString(xmlresult);
                                    resultcode = apirequestbean620.getResultcode();
                                    
                                    flag = ProcessFlag(resultcode);

                                    EPAY_BUCKETHISTORY bucket_history = new EPAY_BUCKETHISTORY();
                                    bucket_history.setLibm(libm);
                                    bucket_history.setMdn(mdn);
                                    bucket_history.setBucketid("620");
                                    bucket_history.setAmount(Integer.valueOf(count2) * (-1));
                                    epaybusinesscontroller.insertbuckethistory(bucket_history);

                                    if (flag) {
                                        output.print(libm + "," + mdn + "," + "1," + "VoiceBucketClean AdjustAccPoc==>Clean Succss");
                                        log.info("VoiceBucketClean AdjustAccPoc==>Clean Succss");
                                        //620 clean success
                                    } else {
                                        output.print(libm + "," + mdn + "," + "0," + "VoiceBucketClean AdjustAccPoc==>Clean Fail");
                                        log.info("VoiceBucketClean AdjustAccPoc==>Clean Fail");
                                        //620 clean fail
                                    }
                                } else {
                                    //不扣620帳本
                                    output.print(libm + "," + mdn + "," + "0," + "LifeCycleState:" + LifeCycleState);
                                    log.info("MDN:" + mdn + ",LifeCycleState" + LifeCycleState);
                                }
                            } else {
                                //如果有編號,檢查是否已經交易, 如果還未交易可在更新資料。   
                            }

                        } else {
                            log.info("VoiceBucketClean Fail: producttype is " + producttype);
                        }
                    } else {

                    }
                } else {
                    output.print("0" + "," + mdn + "," + "0," + "Promotion code not equal");
                    log.info("Promotion code not equal");
                }
            } else {
                log.info("VoiceBucketClean Fail: MDN is NULL");
            }
        } catch (Exception ex) {
            log.info(ex);
        }

    }

    public boolean ProcessFlag(String resultcode) {
        boolean flag = true;
        if (!resultcode.equals("00")) {
            flag = false;
        }
        return flag;
    }
// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
