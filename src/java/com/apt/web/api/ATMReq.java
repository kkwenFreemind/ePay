package com.apt.web.api;

import com.apt.epay.beans.APIATMRequestBean;
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
import com.epay.ejb.bean.EPAY_CALLER;
import com.epay.ejb.bean.EPAY_SERVICE_INFO;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class ATMReq extends HttpServlet {

//    private static final long serialVersionUID = -2546290059736670769L;
    private EPayBusinessConreoller epaybusinesscontroller = null;
//    private final String cpid = new ShareParm().PARM_EPAY_CPID;

    private static final Logger log = Logger.getLogger("EPAY");
    private static final String CALLID = ShareParm.EPAY_CALLID;

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

        log.info("ATMReq.processRequest");
        String logMsg = "Client IP :" + request.getRemoteAddr();
        log.info(logMsg);

        ServletOutputStream output = response.getOutputStream();
        response.setCharacterEncoding("UTF-8");

        epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", this.getServletContext());

        String encryptNotifyMsg = null;

        String key;
        String identifyCode;
        String md5Param = "&identifyCode=";
        String desParam = "&returnOutMac=";
        String decodeMsg = "";

        APIATMRequestBean apiAtmRequestBean = new APIATMRequestBean();
        encryptNotifyMsg = request.getParameter("encryptedMsg");

        log.info("ATM encryptedMsg==>" + encryptNotifyMsg);

        if ("".equals(encryptNotifyMsg) || encryptNotifyMsg == null) {
//            throw new ServletException("無接收資料");
            log.info("No Data Received, encryptResponseMsg is null");
            throw new ServletException("No Data Received, encryptResponseMsg is null");
        }

        try {

            EPAY_CALLER caller = epaybusinesscontroller.getCallerById(CALLID);
            log.info("epaybusinesscontroller.getCallerById(callId)==>" + CALLID);
            log.info("ApolSecuredUrlMsg(caller.getPgEnkey(), caller.getPgIdentify()==>(" + caller.getPgEnkey() + "," + caller.getPgIdentify() + ")");

            ApolSecuredUrlMsg asum = new ApolSecuredUrlMsg(caller.getPgEnkey(), caller.getPgIdentify());
            decodeMsg = asum.decode(encryptNotifyMsg, md5Param, desParam);
            log.info("ATM decodeMsg===>" + decodeMsg);

            StringTokenizer st = new StringTokenizer(decodeMsg, "&");
            while (st.hasMoreTokens()) {
                String parms = st.nextElement().toString();
                String returnItem[] = parms.split("=");
                apiAtmRequestBean = encapBean(apiAtmRequestBean, returnItem);
            }
//            System.out.println("@@@apiAtmRequestBean " + apiAtmRequestBean.getLibm());
            String libm = apiAtmRequestBean.getLibm();
            String atm_status = apiAtmRequestBean.getPayStatus();
            log.info("ATM Libm & Status ===>" + libm + "," + atm_status);

            EPAY_TRANSACTION trans = epaybusinesscontroller.getTransaction(libm);
            EPAY_TRANSACTION transx = epaybusinesscontroller.getTransaction(libm);

            if (transx != null && transx.getPaymethod() == 2) {

                log.info("transx.getPaystatus(" + libm + ")======================>" + transx.getPaystatus());

                if (transx.getPaystatus() == 0) { //只有尚未接受過授權回復的交易，才能進行儲值

                    updateTransaction(trans, apiAtmRequestBean);
                    String logmsg = "PaymentRequest,Update Transaction Success ,libm = " + apiAtmRequestBean.getLibm();
                    log.info(logmsg);
                    log.info("(apiAtmRequestBean Info)==>" + apiAtmRequestBean.getLibm() + "," + apiAtmRequestBean.getPayStatus());

                    String respon = "1";
                    output.write(respon.getBytes(ShareParm.PARM_CHARSETNAME_UTF8));

                    log.info("Find The Transaction Record(Libm):" + apiAtmRequestBean.getLibm());

                    String sid = transx.getServiceId();
                    String cpid = String.valueOf(transx.getCpId());
                    String mdn = transx.getInvoiceContactMobilePhone();
                    String itemproductname = trans.getItemproductname();
                    String tradedate = sdf_pincode.format(transx.getTradedate());
                    String libmx = transx.getLibm();
                    String expiredate = "";
                    String lifecyclestate = "";
                    boolean adjustflag = false;

                    //判斷是走ALU or ZTE
                    if (transx.getPlatformtype() == 1) {//ALU
                        //執行4G OCS儲值作業
                        //step1 get serviceid
                        log.info("Get Basic Info For OCS Quary==>" + sid + "," + mdn + "," + tradedate + "," + libm);
                        AdjustAccUtil ajacc = new AdjustAccUtil(this.getServletContext());
                        int ttype = ShareParm.TTYPE_CCACCOUNT;
                        adjustflag = ajacc.BucketInit(sid, libmx, mdn, tradedate, ttype);
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

                        if (adjustflag) {
                            //儲值成功
                            //SMS
                            SendSMS xsms = new SendSMS();
//                    String expiredate = ajacc.GetContractExpireDate(libmx, mdn, tradedate);
                            String xmsg = "親愛的用戶您好，您申購的【" + itemproductname + "】服務已生效。";
                            xsms.sendsms(mdn, xmsg);

                        } else {
                            //儲值失敗
                            SendSMS xsms = new SendSMS();
                            String ocmdn = new ShareParm().PARM_OC_MDN;
                            String xmsg = "Epay急件：用戶" + mdn + "，信用卡訂單LIB:" + libm + "儲值失敗，請查修";
//                    String message = itemproductname + " 儲值失敗(信用卡)";
                            xsms.sendsms(ocmdn, xmsg);

                            String email_form = new ShareParm().PARM_MAIL_FROM;
                            String dst_user = new ShareParm().PARM_MAIL_TO_OC + ";" + new ShareParm().PARM_MAIL_TO_4GOCS;
                            try {
                                MailUtil.sendMail(new ShareParm().PARM_MAIL_RELAY_HOST, email_form, dst_user, xmsg, xmsg);
                            } catch (Exception ex) {
                                log.info(ex);
                            }
                        }
                    } else if (transx.getPlatformtype() == 2) { //ZTE

                        int channeltype = 1;
                        ZTEAdjustAccUtil zteadjust = new ZTEAdjustAccUtil(this.getServletContext());
                        //adjustflag = zteadjust.ZTEAdjustBucket(cpid, sid, libm, mdn, tradedate, channeltype);
                        String resultflag = zteadjust.PosZTEAdjustBucket(cpid, sid, libm, mdn, tradedate, channeltype);
                        log.info(mdn + " ZTE zteadjust.PosZTEAdjustBucket Result==>" + resultflag);

                        EPAY_TRANSACTION kk_trans = epaybusinesscontroller.getTransaction(libm);
                        String errorDesc = kk_trans.getErrdesc();
                        boolean kkflag = sendMessage(resultflag, itemproductname, mdn, libm, errorDesc);
                        log.info(mdn + " ZTE sendMessage Result==>" + kkflag);

                    } else if (transx.getPlatformtype() == 3) {

                        // KK NOKIA
                        int Rchg_Type = 1; //1.网络储值  2.余额抵扣

                        NokiaMainPricePlanCodeUtil mutil = new NokiaMainPricePlanCodeUtil();

                        //get promotion code
                        SOAUtil soautil = new SOAUtil();
                        SOAReqBean soabean = soautil.getSOAInfo(mdn);
                        String promotioncode = soabean.getPromotioncode();
                        String promotion_type3 = promotioncode.substring(0, 3);
                        log.info(mdn + " Nokia Promotioncode ==>" + promotioncode + "," + promotion_type3);

                        cpid = String.valueOf(trans.getCpId());
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

                        log.info(mdn + " Nokia  AddMainPricePlanCode Result==>" + nbean.getResult_code());

                        if ("00".equalsIgnoreCase(nbean.getResult_code())) {
                            String Status_Desc = "儲值成功";
                            nokia_trans.setErrdesc(Status_Desc);
                        } else {
                            String Status_Desc = "儲值失敗" + nbean.getReason();
                            nokia_trans.setErrdesc(Status_Desc);
                        }
                        epaybusinesscontroller.updateTransaction(nokia_trans);

                        String errorDesc = kk_trans.getErrdesc();
                        String result_status = nbean.getResult_code();
                        boolean kkflag = sendMessage(result_status, itemproductname, mdn, libm, errorDesc);
                        log.info(mdn + " Nokia sendMessage Result==>" + kkflag);

                    } else {

                    }
                } else {
                    log.info("交易狀態【非】為尚未儲值, libm：" + transx.getLibm() + "private data:" + transx.getPrivatedata() + " Paystatus:" + trans.getPaystatus());
                    String xmsg = "Epay：訂單編號:" + transx.getLibm() + ", ATMReq被重複呼叫執行授權";

                    String email_form = new ShareParm().PARM_MAIL_FROM;
                    String dst_user = new ShareParm().PARM_MAIL_TO_OC;
                    try {
                        MailUtil.sendMail(new ShareParm().PARM_MAIL_RELAY_HOST, email_form, dst_user, xmsg, xmsg);
                    } catch (Exception ex) {
                        log.info(ex);
                    }
                }
            } else {
                log.info("Cannot Find Trans, libm：" + trans.getLibm() + "private data:" + trans.getPrivatedata());
            }

        } catch (Exception ex) {
//            Logger.getLogger(APIATMRequest.class.getName()).log(Level.SEVERE, "ATM繳款通知錯誤", ex);
            log.info("ATM payment notice error", ex);

        } finally {

            output.flush();
            output.close();

        }
    }

    private boolean updateTransaction(EPAY_TRANSACTION trans, APIATMRequestBean apiAtmRequestBean) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT); //20170302 111836
//            public static final String PARM_DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
//libm=170302124047334&type=CAP&orderTotal=300&status=Y&responseMsg=請款完成&responseTime=2017-03-02 00:00:00&returnOutMac=526c95e68c442237f17b5a8f4cbc6aa7&isMd5Match=true

        boolean result = false;

        if (trans != null) {
            if ("success".equals(apiAtmRequestBean.getPayStatus())) {
                log.info("apiAtmRequestBean.getPayStatus()==>" + apiAtmRequestBean.getPayStatus());
                trans.setPaystatus(1);
                trans.setErrcode("00"); //繳款成功後設定為00
            } else {
                trans.setPaystatus(0);
            }

//            trans.setPayamount(Integer.parseInt(apiAtmRequestBean.getPayAmount()));
//            log.info("apiAtmRequestBean.getPayAmount()==>" + apiAtmRequestBean.getPayAmount());
            log.info("apiAtmRequestBean.getPayMethod()==>" + apiAtmRequestBean.getPayMethod());
            if ("ATM".equalsIgnoreCase(apiAtmRequestBean.getPayMethod())) {
                trans.setPaymethod(2);
            }

            log.info("apiAtmRequestBean.getPayTime()==>" + apiAtmRequestBean.getPayTime());
            trans.setPaytime(sdf.parse(apiAtmRequestBean.getPayTime()));

            result = epaybusinesscontroller.updateTransaction(trans);
            log.info("epaybusinesscontroller.updateTransaction(trans)==>" + trans.getLibm() + "," + result);

        } else {
//            throw new Exception("無交易資料,訂單編號:"+apiAtmRequestBean.getLibm());
            throw new Exception("No Transaction data, libm:" + apiAtmRequestBean.getLibm());
        }
        return result;
    }

    private APIATMRequestBean encapBean(APIATMRequestBean apiAtmRequestBean, String... items) {
        if ("libm".equalsIgnoreCase(items[0])) {
            apiAtmRequestBean.setLibm(items.length > 1 ? items[1] : "");
            log.info("apiAtmRequestBean.getLibm==>" + apiAtmRequestBean.getLibm());
        }
        if ("payTime".equalsIgnoreCase(items[0])) {
            apiAtmRequestBean.setPayTime(items.length > 1 ? items[1] : "");
            log.info("apiAtmRequestBean.getPayTime==>" + apiAtmRequestBean.getPayTime());
        }
        if ("payMethod".equalsIgnoreCase(items[0])) {
            apiAtmRequestBean.setPayMethod(items.length > 1 ? items[1] : "");
            log.info("apiAtmRequestBean.getPayMethod=>" + apiAtmRequestBean.getPayMethod());
        }
        if ("payStatus".equalsIgnoreCase(items[0])) {
            apiAtmRequestBean.setPayStatus(items.length > 1 ? items[1] : "");
            log.info("apiAtmRequestBean.getPayStatus==>" + apiAtmRequestBean.getPayStatus());
        }
        if ("payAmount".equalsIgnoreCase(items[0])) {
            apiAtmRequestBean.setPayAmount(items.length > 1 ? items[1] : "");
            log.info("apiAtmRequestBean.getPayAmount==>" + apiAtmRequestBean.getPayAmount());
        }
        if ("tel".equalsIgnoreCase(items[0])) {
            apiAtmRequestBean.setTel(items.length > 1 ? items[1] : "");
            log.info("apiAtmRequestBean.getTel==>" + apiAtmRequestBean.getTel());
        }
        if ("returnOutMac".equalsIgnoreCase(items[0])) {
            apiAtmRequestBean.setReturnOutMac(items.length > 1 ? items[1] : "");
            log.info("apiAtmRequestBean.getReturnOutMac==>" + apiAtmRequestBean.getReturnOutMac());
        }
        if ("isMd5Match".equalsIgnoreCase(items[0])) {
            apiAtmRequestBean.setIsMd5Match(items.length > 1 ? items[1] : "");
            log.info("apiAtmRequestBean.getIsMd5Match==>" + apiAtmRequestBean.getIsMd5Match());
        }
        return apiAtmRequestBean;
    }

    private boolean sendMessage(String resultflag, String itemproductname, String mdn, String libm, String errorDesc) {
        boolean result = true;

        if ("00".equals(resultflag)) {
            //儲值成功
            //SMS
            SendSMS xsms = new SendSMS();
//                    String expiredate = ajacc.GetContractExpireDate(libmx, mdn, tradedate);
            String xmsg = "親愛的用戶您好，您申購的【" + itemproductname + "】服務已生效";
            try {
                xsms.sendsms(mdn, xmsg);
            } catch (Exception ex) {
                result = false;
                ex.printStackTrace();

            }
        } else {
            //儲值失敗
            SendSMS xsms = new SendSMS();
            String ocmdn = new ShareParm().PARM_OC_MDN;
            String xmsg = "Epay急件：用戶" + mdn + "，信用卡訂單LIB:" + libm + "儲值失敗，請查修，失敗原因:" + errorDesc;
//                    String message = itemproductname + " 儲值失敗(信用卡)";
            try {
                xsms.sendsms(ocmdn, xmsg);
            } catch (Exception ex) {
                result = false;
                ex.printStackTrace();
            }
            String email_form = new ShareParm().PARM_MAIL_FROM;
            String dst_user = new ShareParm().PARM_MAIL_TO_OC + ";" + new ShareParm().PARM_MAIL_TO_4GOCS;
            try {
                MailUtil.sendMail(new ShareParm().PARM_MAIL_RELAY_HOST, email_form, dst_user, xmsg, xmsg);
            } catch (Exception ex) {
                result = false;
                log.info(ex);
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
