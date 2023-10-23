/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui;


import com.apt.epay.share.ShareParm;
import com.apt.util.ApolSecuredUrlMsg;
import java.security.Key;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class OnlineCancelAuthDeposit_Window extends Window {

//    private static final long serialVersionUID = -84547354457720L;
    private static final Logger log = Logger.getLogger("EPAY");

    private String cpid = new ShareParm().PARM_EPAY_CPID;
    private String libm = "";

    private Key key = null;
    private String returnUrl = "";

    private String md5Param = "&identifyCode=";
    private String desParam = "&callerInMac="; //to PaymentGateway use callerInMac, Receive from PaymentGateway use returnOutMac

    private String encryptedParm = null;

    public void onCreate() throws Exception {

    }

    public void sendCreditCardOrder() throws Exception {

        Textbox textbox_libm = (Textbox) this.getFellow("textbox_libm");
        libm = textbox_libm.getValue();

        /*
         發送至CP金流平台                
         */
        StringBuffer originalParm = new StringBuffer();
        originalParm.append("libm=").append(libm).append("&")
                .append("action=").append("cancelAuth").append("&")
                .append("privatedata=").append("10001").append("&")
                .append("callerResUrl=").append("http://tde.cpedf:8080/CPECF/api/CancelAuthResp").append("&")
                .append("tradedate=").append("2016-03-07 15:30:00");
        //.append("&")
//                .append("callerInMac=").append("38dc7020a508b59ba409615273495ab5").append("&")
//                .append("isMd5Match=").append("true");;

        log.info("originalParm==>" + originalParm);

        try {
            ApolSecuredUrlMsg asum = new ApolSecuredUrlMsg("fd68w577", "Gf67iuWa");
            encryptedParm = asum.encode(originalParm.toString(), md5Param, desParam);
        } catch (Exception ex) {
            log.error(null, ex);
            throw new Exception("加密資料有誤");
        }

        String sendURL = ""; //default value

        Sessions.getCurrent().setAttribute("callerMerchantId", ShareParm.EPAY_CALLID);
        Sessions.getCurrent().setAttribute("encryptRequestMsg", encryptedParm);
        log.info("callerMerchantId==>" + ShareParm.EPAY_CALLID);
        log.info("encryptRequestMsg==>" + encryptedParm);
//        Sessions.getCurrent().setAttribute("odBean", odBean);

        sendURL = "http://210.200.66.50/CPECF/api/CancelAuthReq";
//        sendURL = "http://10.31.80.210:8080/CPECF/api/CancelAuthResp";
        
//        log.info("Sending data to PaymentgateWay(CreditCard), Paymethod type :" + trans.getPaymethod() + ", url :" + sendURL);
        Sessions.getCurrent().setAttribute("sendURL", sendURL);
        Executions.getCurrent().setAttribute(org.zkoss.zk.ui.sys.Attributes.NO_CACHE, Boolean.TRUE);
        Window window = (Window) Executions.createComponents("/deposit/OnlineDepositSendGW.zul", null, null);

    }

        public void sendCancelResp() throws Exception {

//        Textbox textbox_libm = (Textbox) this.getFellow("textbox_libm");
//        libm = textbox_libm.getValue();

        /*
         發送至CP金流平台                
         */
//        StringBuffer originalParm = new StringBuffer();
//        originalParm.append("libm=").append(libm).append("&")
//                .append("action=").append("cancelAuth").append("&")
//                .append("privatedata=").append("10001").append("&")
//                .append("callerResUrl=").append("http://tde.cpedf:8080/CPECF/api/CancelAuthResp").append("&")
//                .append("tradedate=").append("2016-03-07 15:30:00");
//        //.append("&")
////                .append("callerInMac=").append("38dc7020a508b59ba409615273495ab5").append("&")
////                .append("isMd5Match=").append("true");;
//
//        log.info("originalParm==>" + originalParm);
//
//        try {
//            ApolSecuredUrlMsg asum = new ApolSecuredUrlMsg("fd68w577", "Gf67iuWa");
//            encryptedParm = asum.encode(originalParm.toString(), md5Param, desParam);
//        } catch (Exception ex) {
//            log.error(null, ex);
//            throw new Exception("加密資料有誤");
//        }

        String sendURL = ""; //default value
        String tt_encryptedParm=".%EAW%96%9A%E1%7F%5D%01%9BV%AB%FBt4%D3%17Ee%EB%91%A9b%AC%C5%F3K%17s%D0%0F%B9v%17%5C%B6%C3%91%BC%21Pk%8BT_%AF%8C%1F%28%0F%91%FF%13xw%D5k%0B%60%B6Q%9C%14%EC%7C%CEv%C4%5E%5D%3E+%A8%2B%D3S%CD%3A%EEU%85%0F%96%EA%F7%24%24%D9%09%DFS%B3%92%FB%FA%5E-%F3e%13%D5%B5s%80%5E+%F8%CE%85h%E6%FC%CA%04%A1%F8%F9%A9%89z%E6h%29%5E%5B%2C%12Y%7E%11%E2%40M%7CV%E1%F3%F2-vC%D0%FE%EE%25T%9D%FE%F4%D1%A0%FC%82%B9uYN%CE%DA%1C%FAs3A%D7%CA%2F%F6%243la%E0%EE%89%D3%CB%A6%91%2C%EB0gv";

        Sessions.getCurrent().setAttribute("callerMerchantId", ShareParm.EPAY_CALLID);
        Sessions.getCurrent().setAttribute("encryptResponseMsg", tt_encryptedParm);
        log.info("callerMerchantId==>" + ShareParm.EPAY_CALLID);
        log.info("encryptResponseMsg==>" + encryptedParm);
//        Sessions.getCurrent().setAttribute("odBean", odBean);

//        sendURL = "http://10.31.80.210:8080/CPECF/api/CancelAuthReq";
        sendURL = "http://210.200.66.50/CPECF/api/CancelAuthResp";
        
//        log.info("Sending data to PaymentgateWay(CreditCard), Paymethod type :" + trans.getPaymethod() + ", url :" + sendURL);
        Sessions.getCurrent().setAttribute("sendURL", sendURL);
        Executions.getCurrent().setAttribute(org.zkoss.zk.ui.sys.Attributes.NO_CACHE, Boolean.TRUE);
        Window window = (Window) Executions.createComponents("/test/OnlineDepositSendGW.zul", null, null);

    }
}
