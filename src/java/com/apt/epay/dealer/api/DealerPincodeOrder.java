/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.dealer.api;

import com.apt.epay.beans.PinCodeReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.deposit.bean.PincodeOrderReqBean;
import com.apt.epay.deposit.util.PosUtil;
//import com.apt.epay.deposit.util.VVCMUtil;
import com.apt.epay.deposit.util.toolUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.epay.zte.bean.PinCodeResultBean;
//import com.apt.epay.zte.bean.VVCMResultBean;
import com.apt.epay.zte.util.ZTEPinCodeUtil;
import com.apt.util.PPMdnUtil;
import com.apt.util.PinCodeUtil;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_DEALERCARD;
import com.epay.ejb.bean.EPAY_DEALERMDN;
import com.epay.ejb.bean.EPAY_PROMOTIONCODE;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author kevinchang
 */
public class DealerPincodeOrder extends HttpServlet {

    //沒有LOG
    private final Logger log = Logger.getLogger("EPAY");

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

        log.info("DealerPincodeOrder.processRequest");
        log.info("Client IP :" + request.getRemoteAddr());

        //經銷商API儲值
        String cpid = request.getParameter("CPID");
        log.info("CPID===>" + cpid);
        toolUtil tool = new toolUtil();
        Utilities util = new Utilities();
        String deskey = "";
        String responseStr = "";
        boolean cpkeyflag = false;

        PPMdnUtil mdntool = new PPMdnUtil();
        String responsetime = mdntool.getAPIResponseTime();
        String Status = "0x00000000";
        String Status_Desc = "成功";

        try {
            EPayBusinessConreoller epaybusinesscontroller;
            epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", this.getServletContext());

            int icpid = Integer.valueOf(cpid);
            EPAY_CP_INFO cpinfo = epaybusinesscontroller.getCpInfo(icpid);

            if (cpinfo != null) {
                deskey = cpinfo.getPoskey();
                log.info("deskey===>" + deskey);
                if (!"".equals(deskey)) {
                    cpkeyflag = true;
                }
            } else {
                //cpinfo is null
                log.info("cannot get cpinfo & pos key");
            }

            if (cpkeyflag) {

                ServletInputStream input = request.getInputStream();

                String encode_str_input = tool.getStringFromInputStream(input);
                String str_input = util.decrypt(deskey, encode_str_input);
                log.info("DealerPincodeOrder INPUT==>" + str_input);

                String libm = "";
                String basicinfo = "";
                String transtatus = "";

                PincodeOrderReqBean aPincodeOrderReqBean;
                aPincodeOrderReqBean = mdntool.DearlerPincodeOrderServParseXMLString(str_input, "PincodeOrderRequest");

                String mdn = aPincodeOrderReqBean.getMdn();
                String pincode = aPincodeOrderReqBean.getPincode();
                String apisrcid = aPincodeOrderReqBean.getApisrcid();
                String paytool = aPincodeOrderReqBean.getPaytool();
                String payname = aPincodeOrderReqBean.getPayname();
                String cplibm = aPincodeOrderReqBean.getCplibm();
                String cardno = aPincodeOrderReqBean.getCardno();
                String storeId = aPincodeOrderReqBean.getStoreid();
                PPMdnUtil kk_ppmdn = new PPMdnUtil(mdn, true, true); // zte & alu both use pincode order

                boolean apisrcflag = false;
                boolean paytoolflag = false;

                if (!"".equals(apisrcid)) {
                    apisrcflag = kk_ppmdn.checkAPISrcID(apisrcid);
                    if (!apisrcflag) {
                        Status = "0x03000009";
                        Status_Desc = "APISRCID參數不正確";
                    }
                } else {
                    Status = "0x03000009";
                    Status_Desc = "APISRCID參數不正確(不得為空白)";
                }

                if (!"".equals(paytool)) {
                    paytoolflag = kk_ppmdn.checkPaytoolID(paytool);
                    if (!paytoolflag) {
                        Status = "0x03000010";
                        Status_Desc = "PAYTOOL參數不正確";
                    }
                } else {
                    Status = "0x03000010";
                    Status_Desc = "PAYTOOL參數不正確(不得為空白)";
                }

                if (paytoolflag && apisrcflag) {
                    log.info("MDN===>" + mdn);
                    
                    if (!"".equals(mdn)) {

                        EPAY_DEALERCARD epaydealercard = epaybusinesscontroller.getDealerCardByCardId(storeId, cardno);
                        EPAY_DEALERMDN epaydealermdn = epaybusinesscontroller.getDealerMDNByMDN(storeId, mdn);

                        log.info("epaydealercard===>" + epaydealercard);
                        log.info("epaydealermdn===>" + epaydealermdn);

                        if (epaydealercard != null && epaydealermdn != null) {

                            log.info("EPAY_DEALERCARD AND EPAY_DEALERMDN ARE NOT NULL");

                            PPMdnUtil ppmdn = new PPMdnUtil(mdn, true, true);
                            String checkresult = ppmdn.checkPPMDN(epaybusinesscontroller);
                            log.info("ppmdn.checkPPMDN(" + mdn + ", epaybusinesscontroller)==>" + checkresult);

                            if ("1".equals(checkresult)) {
                                //pincode order star
                                Calendar nowDateTime = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
                                SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                                SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);
                                libm = sdf15.format(nowDateTime.getTime());
//                            String tradeDate = sdf.format(nowDateTime.getTime());
                                String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());

                                String promotioncode = ppmdn.getPromotioncode();
                                PosUtil posutil = new PosUtil();

                                boolean insertresult = posutil.insertPincodeOrderTransaction(ppmdn, aPincodeOrderReqBean, libm, cpid, epaybusinesscontroller);

                                if (insertresult) {
                                    //判斷是要到ALU還是ZTE做儲值
                                    EPAY_PROMOTIONCODE epay_promotioncode = new EPAY_PROMOTIONCODE();
                                    epay_promotioncode = epaybusinesscontroller.getPomtionCode(promotioncode);

                                    log.info("MDN:" + mdn + " ===>" + epay_promotioncode.getPlatformtype() + "," + epay_promotioncode.getPromotioncode());

                                    String resultcode = "";

                                    if (epay_promotioncode.getPlatformtype() == 1) { //ALU

                                        PinCodeUtil pincodeutil = new PinCodeUtil();
                                        String result = pincodeutil.putPincodeOCSlet(libm, mdn, pincode, tradeDate_Pincode);

                                        log.info("DealerPinCodeProce(MDN:" + mdn + " OCS XML Resonse)==>" + result);

                                        if (result != null) {
                                            PinCodeReqBean apirequestbean;// = new PinCodeReqBean();
                                            apirequestbean = pincodeutil.parsePinCodeXMLString(result);

                                            resultcode = apirequestbean.getResultcode();

                                            if ("00".equals(resultcode)) {
                                                Status = "0x00000000";
                                                Status_Desc = "儲值成功";
//                                                basicinfo = ppmdn.getBasicOCSInfo(epaybusinesscontroller);
                                            } else {
                                                Status = "0x03000007";
                                                Status_Desc = "儲值失敗";
                                            }

                                            EPAY_TRANSACTION kktrans = epaybusinesscontroller.getTransaction(libm);
                                            kktrans.setStatus(resultcode);
                                            kktrans.setErrdesc(Status_Desc);
                                            boolean brst = epaybusinesscontroller.updateTransaction(kktrans);
                                            log.info(mdn + "==>pincode order resultcode:" + resultcode);
                                            log.info(mdn + "==>epaybusinesscontroller.updateTransaction(resultcode):" + brst);

                                        } else {
                                            log.info(mdn + "==>Dealer Admin Exception:THE ALUPINCODE Result From 4G ALU OCS IS NULL!!!!!");
                                            Status = "0x03000007";
                                            Status_Desc = "儲值失敗";

                                            EPAY_TRANSACTION kktrans = epaybusinesscontroller.getTransaction(libm);
                                            kktrans.setStatus("-1");
                                            kktrans.setErrdesc(Status_Desc);
                                            boolean brst = epaybusinesscontroller.updateTransaction(kktrans);
                                            log.info(mdn + "==>Dealer pincode order resultcode:" + "-1");
                                            log.info(mdn + "==>epaybusinesscontroller.updateTransaction(resultcode):" + brst);
                                        }
                                    } else if(epay_promotioncode.getPlatformtype() == 2){ // ZTE
                                        ZTEPinCodeUtil ztePincode = new ZTEPinCodeUtil();
                                        String result = ztePincode.putPincodeOCSlet(libm, mdn, pincode);
                                        log.info("ZTE DealerPinCodeProce(MDN:" + mdn + " ZTE OCS XML Resonse)==>" + result);

                                        if (result != null) {

                                            if (!result.contains("Fault")) {
                                                PinCodeResultBean apirequestbean = new PinCodeResultBean();
                                                apirequestbean = ztePincode.parseZTEPinCodeXMLString(result);

                                                String zteresultcode = apirequestbean.getReturncode();
                                                log.info("zteresultcode====>" + zteresultcode);

                                                int amt = 0;
                                                EPAY_TRANSACTION sstrans = epaybusinesscontroller.getTransaction(libm);
                                                if (zteresultcode.equals("0000")) {
                                                    resultcode = "00";
                                                    amt = Integer.valueOf(apirequestbean.getChargemoney());

                                                    sstrans.setItemunitprice(amt);
                                                    sstrans.setPayamount(amt);
                                                    sstrans.setOrdertotal(amt);
                                                } else {
                                                    resultcode = zteresultcode;
                                                }

                                                if ("00".equals(resultcode)) {
                                                    Status = "0x00000000";
                                                    Status_Desc = "儲值成功";
//                                                    basicinfo = ppmdn.getBasicOCSInfo(epaybusinesscontroller);
                                                } else {
                                                    Status = "0x03000007";
                                                    Status_Desc = "儲值失敗";
                                                }

                                                sstrans.setStatus(resultcode);
                                                sstrans.setErrdesc(Status_Desc);
                                                boolean brst = epaybusinesscontroller.updateTransaction(sstrans);
                                                log.info(mdn + "==>Dealer pincode order resultcode:" + resultcode);
                                                log.info(mdn + "==>epaybusinesscontroller.updateTransaction(resultcode):" + brst);
                                            } else {
                                                Status = "0x03000007";
                                                Status_Desc = "儲值失敗";

                                                EPAY_TRANSACTION kktrans = epaybusinesscontroller.getTransaction(libm);
                                                kktrans.setStatus("-1");
                                                kktrans.setErrdesc(Status_Desc);
                                                boolean brst = epaybusinesscontroller.updateTransaction(kktrans);
                                                log.info(mdn + "==>Dealer pincode order resultcode:" + "-1");
                                                log.info(mdn + "==>epaybusinesscontroller.updateTransaction(resultcode):" + brst);
                                            }
                                        } else {
                                            log.info(mdn + "==>Admin Exception:THE ZTEPINCODE Result From 4G ZTE OCS IS NULL!!!!!");
                                            Status = "0x03000007";
                                            Status_Desc = "儲值失敗";

                                            EPAY_TRANSACTION kktrans = epaybusinesscontroller.getTransaction(libm);
                                            kktrans.setStatus("-1");
                                            kktrans.setErrdesc(Status_Desc);
                                            boolean brst = epaybusinesscontroller.updateTransaction(kktrans);
                                            log.info(mdn + "==>Dealer pincode order resultcode:" + "-1");
                                            log.info(mdn + "==>epaybusinesscontroller.updateTransaction(resultcode):" + brst);
                                        }
                                    }else if(epay_promotioncode.getPlatformtype() == 3){ //NOKIA
                                        
                                    }

                                } else {
                                    log.info("posutil.insertPosTransaction(ppmdn, aPIServiceOrdrReqBean," + libm + "," + cpid + ", epaybusinesscontroller)==>" + insertresult);
                                    Status = "0x01000001";
                                    Status_Desc = "新增交易紀錄失敗";
                                }

                            } else if ("-1".equals(checkresult)) {
                                Status = "0x01000001";
                                Status_Desc = "認證失敗(用戶門號不存在)";
                            } else if ("0".equals(checkresult)) {
                                Status = "0x02000004";
                                Status_Desc = "用戶門號不允許存取)";
                            }

                        } else {
                            log.info("EPAY_DEALERCARD IS NULL");
                            Status = "0x01000001";
                            Status_Desc = "認證失敗(卡號資料與經銷商代碼不一致)";
                        }

                    } else { // mdn is null
                        Status = "0x01000001";
                        Status_Desc = "認證失敗(用戶門號不存在)";
                    }

                }
                responseStr = "<PincodeOrderResponse><Result><STATUS>" + Status + "</STATUS><STATUS_DESC>" + Status_Desc + "</STATUS_DESC>"
                        + "<CPLIBM>" + cplibm + "</CPLIBM>\n"
                        + "<LIBM>" + libm + "</LIBM>"
                        + "<RESPONSE_TIMESTAMP>" + responsetime + "</RESPONSE_TIMESTAMP>"
                        //                        + basicinfo
                        + "</Result></PincodeOrderResponse>";

            } else {
                Status = "0x02000010";
                Status_Desc = "未授權此功能";
                responseStr = "<PincodeOrderResponse>\n"
                        + "<Result>\n"
                        + "<STATUS>" + Status + "</STATUS>\n"
                        + "<STATUS_DESC>" + Status_Desc + "</STATUS_DESC>\n"
                        + "<RESPONSE_TIMESTAMP>" + responsetime + "</RESPONSE_TIMESTAMP>\n"
                        + "</Result>\n"
                        + "</PincodeOrderResponse>\n";
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            log.info("cpkeyflag====>" + cpkeyflag);
            if (cpkeyflag) {
                String encode_responseStr = util.encrypt(deskey, responseStr);
                log.info("DealerPincodeOrder ResponseStr ===>" + responseStr);
                log.info("DealerPincodeOrder encode_responseStr==>" + encode_responseStr);
                /* TODO output your page here. You may use following sample code. */
                out.println(encode_responseStr);
            } else {
                response.sendError(500);
                out.println(responseStr);
            }
        } catch (Exception ex) {
            log.info(ex);
        } finally {
            out.close();
        }
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
