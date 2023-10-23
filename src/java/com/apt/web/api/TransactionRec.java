package com.apt.web.api;

import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.nokia.bean.NokiaPricePlanResultBean;
import com.apt.epay.nokia.main.NokiaMainPricePlanCodeUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.epay.soa.util.SOAUtil;
import com.apt.epay.zte.util.ZTEAdjustAccUtil;
import com.apt.util.AdjustAccUtil;
import com.apt.util.ApolSecuredUrlMsg;
import com.apt.util.MailUtil;
import com.apt.util.SendSMS;
import com.epay.ejb.bean.EPAY_SERVICE_INFO;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * 線上儲值交易結果通知
 *
 * @author Administrator
 */
public class TransactionRec extends HttpServlet {

    private static final Logger log = Logger.getLogger("EPAY");

    private String libm;
    private String status;
    private String errCode;
    private String errDesc;
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

        String msg;//= null;
//        EPAY_TRANSACTION epayTransaction = new EPAY_TRANSACTION();

        msg = request.getParameter("encryptedMsg");

        /*@@@@@ TransactionRec decodeedText 
         status=0&errCode=00&
         errDesc=成功交易519154&
         libm=150623104921461&
         orderTotal=3
         &installType=&install=1&firstAmt=0&eachAmt=0&installFee=0&privateData=null&
         authDate=2015-06-23 11:00:23&
         returnOutMac=3d58dc0c946e2e5e8353c2bd52c20400&isMd5Match=true
         */
        try {
            ApolSecuredUrlMsg asum = new ApolSecuredUrlMsg(key, identifyCode);
            String decodeedText = asum.decode(msg.toString(), md5Param, desParam);
            log.info("@@@@@ TransactionRec decodeedText " + decodeedText);
            StringTokenizer st = new StringTokenizer(decodeedText);
            while (st.hasMoreTokens()) {
                String errDatas[] = st.nextToken("&").split("=");
                if ("status".equalsIgnoreCase(errDatas[0])) {
                    if (errDatas.length > 1) {
                        status = errDatas[1];
//                        log.info("status===>" + status);
                    }
                }
                if ("errCode".equalsIgnoreCase(errDatas[0])) {
                    if (errDatas.length > 1) {
                        errCode = errDatas[1];
//                        log.info("errCode===>" + errCode);
                    }
                }
                if ("errDesc".equalsIgnoreCase(errDatas[0])) {
                    if (errDatas.length > 1) {
                        errDesc = errDatas[1];
//                        log.info("errDesc===>" + errDesc);
                    }
                }
                if ("libm".equalsIgnoreCase(errDatas[0])) {
                    if (errDatas.length > 1) {
                        libm = errDatas[1];
//                        log.info("libm===>" + libm);
                    }
                }

                if ("privateData".equalsIgnoreCase(errDatas[0])) {
                    if (errDatas.length > 1) {
                        privateData = errDatas[1];
//                        log.info("privateData===>" + privateData);
                    }
                }
                if ("authDate".equalsIgnoreCase(errDatas[0])) {
                    if (errDatas.length > 1) {
                        authDate = errDatas[1];
//                        log.info("authDate===>" + authDate);
                    }
                }
            }

            EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", this.getServletContext());
            log.info("epaybusinesscontroller.getTransaction(libm)==>" + libm);
            EPAY_TRANSACTION trans = epaybusinesscontroller.getTransaction(libm);

            if (trans != null) {
                if (trans.getPaystatus() == 0) { //只有尚未接受過授權回復的交易，才能進行儲值

                    if (trans.getPaymethod() == 1) {
                        /*
                     status=0&errCode=00&errDesc=成功交易640182&
                     libm=150529143835176&orderTotal=3&installType=&install=1&
                     firstAmt=0&eachAmt=0&installFee=0&privateData=150529143829052&
                     authDate=2015-05-29 14:49:45&
                     returnOutMac=960bf13ad69b856e468be1d0519a227e&isMd5Match=true
                         */
                        trans.setInstalltype("I");
//                    trans.setStatus(errCode);
                        trans.setPrivatedata(libm);
                        trans.setErrcode(errCode);
                        trans.setErrdesc(errDesc);

                        log.info("processRequest status==>" + status);

                        boolean resultflag = false;
                        String sid = trans.getServiceId();
                        String mdn = trans.getInvoiceContactMobilePhone();
                        String itemproductname = trans.getItemproductname();
                        String tradedate = sdf_pincode.format(trans.getTradedate());
                        String libmx = trans.getLibm();
                        String expiredate = "";
                        String lifecyclestate = "";

                        if (status.equals("0")) {
                            trans.setPaytime(sdf.parse(authDate));
                            trans.setPaystatus(1);
                            log.info("trans.setPaystatus(1)");
                            epaybusinesscontroller.updateTransaction(trans);

                            //執行4G OCS儲值作業
                            //step1 get serviceid
                            log.info("trans.getLibm()" + trans.getServiceId());
                            log.info("trans.getPlatformtype()=========>" + trans.getPlatformtype());

                            if (trans.getPlatformtype() == 1) { //ALU
                                log.info("===============> ALU");
                                AdjustAccUtil ajacc = new AdjustAccUtil(this.getServletContext());
                                int ttype = ShareParm.TTYPE_CCACCOUNT;
                                resultflag = ajacc.BucketInit(sid, libmx, mdn, tradedate, ttype);
                                expiredate = ajacc.GetContractExpireDate(libmx, mdn, tradedate);
                                lifecyclestate = ajacc.getLifeCycleState(libm, mdn, tradedate);

                                log.info("expiredate==>" + expiredate);
                                log.info("lifecyclestate==>" + lifecyclestate);

                                if (("CED1".equalsIgnoreCase(lifecyclestate)) || ("CED10".equalsIgnoreCase(lifecyclestate)) || ("CED3".equalsIgnoreCase(lifecyclestate))) {

                                    //通知【產品用Pincode儲值方式將狀態改為Active】
                                    SendSMS xsms = new SendSMS();
                                    String ocmdn = new ShareParm().PARM_OC_MDN;
                                    String xmsg = "Epay急件：用戶" + mdn + "，信用卡訂單LIB:" + libm + "狀態為:" + lifecyclestate;

                                    String email_form = new ShareParm().PARM_MAIL_FROM;
                                    String dst_user = new ShareParm().PARM_MAIL_TO_OC + ";" + new ShareParm().PARM_MAIL_TO_4GOCS;
                                    try {
                                        MailUtil.sendMail(new ShareParm().PARM_MAIL_RELAY_HOST, email_form, dst_user, xmsg, xmsg);
                                    } catch (Exception ex) {
                                        log.info(ex);
                                    }
                                    xsms.sendsms(ocmdn, xmsg);
                                }
//                            boolean activeflag = ajacc.AdjustMDNActive(libmx, mdn, tradedate);
//                            log.info(mdn+" ajacc.AdjustMDNActive==>"+activeflag);

                                EPAY_TRANSACTION transdebug = epaybusinesscontroller.getTransaction(libm);
                                log.info("=========>" + libm + "  transdebug==>" + transdebug.getStatus());
                                log.info("=========>" + libm + "  resultflag==>" + resultflag);

                                if (resultflag) {

                                    //親愛的用戶您好，您申購的【4GLTE數據儲值699元5.0 GB】服務已生效，服務有效期限2015-10-10 02:00:00。
                                    //儲值成功
                                    //for SMS測試
                                    SendSMS xsms = new SendSMS();

                                    String xmsg = "親愛的用戶您好，您申購的【" + itemproductname + "】服務已生效。";
                                    xsms.sendsms(mdn, xmsg);

                                } else {
                                    //儲值失敗
                                    SendSMS xsms = new SendSMS();
                                    String ocmdn = new ShareParm().PARM_OC_MDN;
                                    String xmsg = "Epay急件：用戶" + mdn + "，信用卡訂單LIB:" + libm + "儲值失敗，請查修";

                                    String email_form = new ShareParm().PARM_MAIL_FROM;
                                    String dst_user = new ShareParm().PARM_MAIL_TO_OC + ";" + new ShareParm().PARM_MAIL_TO_4GOCS;
                                    try {
                                        MailUtil.sendMail(new ShareParm().PARM_MAIL_RELAY_HOST, email_form, dst_user, xmsg, xmsg);
                                    } catch (Exception ex) {
                                        log.info(ex);
                                    }

                                    xsms.sendsms(ocmdn, xmsg);
                                }
                            } else if (trans.getPlatformtype() == 2) { // ZTE

                                log.info("===============> ZTE");
                                int channeltype = 1; //1.网络储值  2.余额抵扣
                                ZTEAdjustAccUtil zteadjust = new ZTEAdjustAccUtil(this.getServletContext());
                                String cpid = String.valueOf(trans.getCpId());
                                //resultflag = zteadjust.ZTEAdjustBucket(cpid, sid, libm, mdn, tradedate, channeltype);
                                String kkresultflag = zteadjust.PosZTEAdjustBucket(cpid, sid, libm, mdn, tradedate, channeltype);
                                log.info("zteadjust.PosZTEAdjustBucket Result==>" + kkresultflag);

                                EPAY_TRANSACTION kk_trans = epaybusinesscontroller.getTransaction(libm);
                                String errorDesc = kk_trans.getErrdesc();
                                boolean kkflag = sendMessage(kkresultflag, itemproductname, mdn, libm, errorDesc);
                                log.info("sendMessage Result==>" + kkflag);

                            } else if (trans.getPlatformtype() == 3 ) {

                                // KK NOKIA
                                log.info("===============> Nokai");
                                int Rchg_Type = 1; //1.网络储值  2.余额抵扣

                                NokiaMainPricePlanCodeUtil mutil = new NokiaMainPricePlanCodeUtil();

                                //get promotion code
                                SOAUtil soautil = new SOAUtil();
                                SOAReqBean soabean = soautil.getSOAInfo(mdn);
                                String promotioncode = soabean.getPromotioncode();
                                String promotion_type3 = promotioncode.substring(0, 3);
                                log.info(mdn + " promotioncode ==>" + promotioncode + "," + promotion_type3);

                                String cpid = String.valueOf(trans.getCpId());
                                Integer epay_cpid = Integer.valueOf(cpid);

                                EPAY_TRANSACTION kk_trans = epaybusinesscontroller.getTransaction(libm);
                                String nokia_sid = kk_trans.getServiceId();
                                Long serviceid = Long.valueOf(nokia_sid);

                                EPAY_SERVICE_INFO serviceinfo = epaybusinesscontroller.getServiceInfoById(serviceid, epay_cpid);
                                String priceplancode = serviceinfo.getPriceplancode();

                                NokiaPricePlanResultBean nbean = mutil.AddMainPricePlanCode(promotion_type3, libm, mdn, priceplancode, tradedate, Rchg_Type);

                                EPAY_TRANSACTION nokia_trans = epaybusinesscontroller.getTransaction(libm);
                                nokia_trans.setStatus(nbean.getResult_code());
                                nokia_trans.setErrcode(nbean.getResult_code());
                                if ("00".equalsIgnoreCase(nbean.getResult_code())) {
                                    String Status_Desc = "儲值成功";
                                    nokia_trans.setErrdesc(Status_Desc);
                                } else {
                                    String Status_Desc = "儲值失敗"+nbean.getReason();
                                    nokia_trans.setErrdesc(Status_Desc);
                                }
                                epaybusinesscontroller.updateTransaction(nokia_trans);

                                String errorDesc = kk_trans.getErrdesc();
                                String result_status = nbean.getResult_code();
                                boolean kkflag = sendMessage(result_status, itemproductname, mdn, libm, errorDesc);
                                log.info(mdn + " sendMessage Result==>" + kkflag);
                            }

                        } else {
//                        trans.setPaystatus(Integer.valueOf("-1"));
//                        epaybusinesscontroller.updateTransaction(trans);

                        }

//                    epaybusinesscontroller.updateTransaction(trans);
                    } else {
                        log.info("Cannot Find Trans, libm：" + privateData + "private data:" + privateData);
                    }
                } else {
                    log.info("交易狀態【非】為尚未儲值, libm：" + privateData + "private data:" + privateData + " Paystatus:" + trans.getPaystatus());
//                    SendSMS xsms = new SendSMS();
//                    String ocmdn = new ShareParm().PARM_OC_MDN;
                    String xmsg = "Epay：訂單編號:" + privateData + ", 被重複呼叫授權結果API";
//                    String message = itemproductname + " 儲值失敗(信用卡)";

                    String email_form = new ShareParm().PARM_MAIL_FROM;
                    String dst_user = new ShareParm().PARM_MAIL_TO_OC;
                    try {
                        MailUtil.sendMail(new ShareParm().PARM_MAIL_RELAY_HOST, email_form, dst_user, xmsg, xmsg);
                    } catch (Exception ex) {
                        log.info(ex);
                    }

                }
            } else {
                log.info("Cannot Find Trans, libm：" + privateData + "private data:" + privateData);

                SendSMS xsms = new SendSMS();
                String ocmdn = new ShareParm().PARM_OC_MDN;
                String xmsg = "Epay急件：查無此訂單編號(信用卡):" + privateData;
//                    String message = itemproductname + " 儲值失敗(信用卡)";

                String email_form = new ShareParm().PARM_MAIL_FROM;
                String dst_user = new ShareParm().PARM_MAIL_TO_OC + ";" + new ShareParm().PARM_MAIL_TO_4GOCS;
                try {
                    MailUtil.sendMail(new ShareParm().PARM_MAIL_RELAY_HOST, email_form, dst_user, xmsg, xmsg);
                } catch (Exception ex) {
                    log.info(ex);
                }

                xsms.sendsms(ocmdn, xmsg);
            }

        } catch (Exception ex) {
            log.info(ex);
        }

    }

    private boolean sendMessage(String kkresultflag, String itemproductname, String mdn, String libm, String errorDesc) {
        boolean result = true;

        if ("00".equals(kkresultflag)) {

            //親愛的用戶您好，您申購的【4GLTE數據儲值699元5.0 GB】服務已生效，服務有效期限2015-10-10 02:00:00。
            //儲值成功
            //for SMS測試
            SendSMS xsms = new SendSMS();

            String xmsg = "親愛的用戶您好，您申購的【" + itemproductname + "】服務已生效";
            try {
                xsms.sendsms(mdn, xmsg);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } else {
            //儲值失敗
            SendSMS xsms = new SendSMS();
            String ocmdn = new ShareParm().PARM_OC_MDN;
            String xmsg = "Epay急件：用戶" + mdn + "，信用卡訂單LIB:" + libm + "儲值失敗，請查修，失敗原因:" + errorDesc;
//                    String message = itemproductname + " 儲值失敗(信用卡)";

            String email_form = new ShareParm().PARM_MAIL_FROM;
            String dst_user = new ShareParm().PARM_MAIL_TO_OC + ";" + new ShareParm().PARM_MAIL_TO_4GOCS;
            try {
                MailUtil.sendMail(new ShareParm().PARM_MAIL_RELAY_HOST, email_form, dst_user, xmsg, xmsg);
            } catch (Exception ex) {
                log.info(ex);
            }
            try {
                xsms.sendsms(ocmdn, xmsg);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result;
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
