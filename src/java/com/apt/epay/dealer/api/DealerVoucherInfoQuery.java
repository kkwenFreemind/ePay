/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.dealer.api;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.deposit.bean.VoucherInfoReqBean;
import com.apt.epay.deposit.util.toolUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.epay.zte.bean.VoucherInfoResultBean;
import com.apt.epay.zte.util.ZTEPinCodeUtil;
import com.apt.util.PPMdnUtil;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_CP_INFO;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
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
public class DealerVoucherInfoQuery extends HttpServlet {

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
            throws ServletException, IOException, Exception {

        log.info("PosUserStateLookup.processRequest");
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
                log.info("PincodeOrder INPUT==>" + str_input);

                String libm = "";
                String basicinfo = "";
                String transtatus = "";

                VoucherInfoReqBean aVoucherInfoReqBean;
                aVoucherInfoReqBean = mdntool.VoucherInfoParseXMLString(str_input, "VoucherInfoRequest");

//                String mdn = aPincodeOrderReqBean.getMdn();
                String vouchercard = aVoucherInfoReqBean.getVoucherCard();
                String storeid = aVoucherInfoReqBean.getStoreId();
                String storename = aVoucherInfoReqBean.getStoreName();

                Calendar nowDateTime = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
                SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);
                libm = sdf15.format(nowDateTime.getTime());

                String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());
                String resultcode = "";
               
                ZTEPinCodeUtil ztePincode = new ZTEPinCodeUtil();
                String result = ztePincode.putVoucherInfoQueryOCSlet(libm, vouchercard);
                log.info("ZTE OCS XML Resonse)==>" + result);

                
                if (result != null) {
                    VoucherInfoResultBean apivoucherinfoResultbean = new VoucherInfoResultBean();
                    apivoucherinfoResultbean = ztePincode.parseZTEVoucherInfoXMLString(result);
                    log.info("apivoucherinfoResultbean.returnCode==>" + apivoucherinfoResultbean.getReturncode());
                    
                    
                    if ("0000".equals(apivoucherinfoResultbean.getReturncode())) {
                        Status = "0x00000000";
                        Status_Desc = "查詢成功";
                        basicinfo ="<CARDSTATUS>"+apivoucherinfoResultbean.getStatus()+"</CARDSTATUS>"
                                +"<LOCKSTATE>"+apivoucherinfoResultbean.getLockstate()+"</LOCKSTATE>";

                    } else {
                        Status = "0x03000007";
                        Status_Desc = "查詢失敗";
                    }

                    responseStr = "<VoucherInfoResponse><Result><STATUS>" + Status + "</STATUS><STATUS_DESC>" + Status_Desc + "</STATUS_DESC>"
                            + "<CPLIBM>" + libm + "</CPLIBM>\n"
                            + "<LIBM>" + libm + "</LIBM>"
                            + "<RESPONSE_TIMESTAMP>" + responsetime + "</RESPONSE_TIMESTAMP>"
                            + basicinfo
                            + "</Result></VoucherInfoResponse>";

                } else {
                    Status = "0x02000010";
                    Status_Desc = "未授權此功能";
                    responseStr = "<VoucherInfoResponse>\n"
                            + "<Result>\n"
                            + "<STATUS>" + Status + "</STATUS>\n"
                            + "<STATUS_DESC>" + Status_Desc + "</STATUS_DESC>\n"
                            + "<RESPONSE_TIMESTAMP>" + responsetime + "</RESPONSE_TIMESTAMP>\n"
                            + "</Result>\n"
                            + "</VoucherInfoResponse>\n";
                }
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
                log.info("VoucherInfoResponse ResponseStr ===>" + responseStr);
                log.info("VoucherInfoResponse encode_responseStr==>" + encode_responseStr);
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
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(DealerVoucherInfoQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(DealerVoucherInfoQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
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
