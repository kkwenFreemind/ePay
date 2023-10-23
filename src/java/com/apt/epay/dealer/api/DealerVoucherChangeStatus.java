/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.dealer.api;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.dealer.util.DealerUtil;
import com.apt.epay.dealer.util.VoucherChangeResultBean;
import com.apt.epay.deposit.bean.VoucherChangeStatusReqBean;
import com.apt.epay.deposit.util.toolUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.epay.zte.util.ZTEPinCodeUtil;
import com.apt.util.PPMdnUtil;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_DEALERCARD;
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
public class DealerVoucherChangeStatus extends HttpServlet {

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
                log.info("DealerVoucherChangeStatus INPUT==>" + str_input);

                String libm = "";

                VoucherChangeStatusReqBean aVoucherChangeStatusReqBean;
                aVoucherChangeStatusReqBean = mdntool.VoucherChangeStatusParseXMLString(str_input, "VoucherChangeStatusRequest");

                String StartSerialNo = aVoucherChangeStatusReqBean.getStartserialno();
                String EndSerialNo = aVoucherChangeStatusReqBean.getEndserialno();
                String storeid = aVoucherChangeStatusReqBean.getStoreid();
                String storename = aVoucherChangeStatusReqBean.getStorename();

                boolean checkflag = false;

                long i_startno = Long.valueOf(StartSerialNo);
                long i_endno = Long.valueOf(EndSerialNo);

                for (long i = i_startno; i <= i_endno; i++) {
                    String cardno = String.valueOf(i);
                    EPAY_DEALERCARD epaydealercard = epaybusinesscontroller.getDealerCardByCardId(storeid, cardno);
                    log.info("epaydealercard===>" + epaydealercard);
                    if (epaydealercard != null) {
                        checkflag = true;
                        log.info("epaydealercard is NOT null and checkflag ====>" + checkflag);
                    } else {
                        checkflag = false;
                        log.info("epaydealercard is null and checkflag ====>" + checkflag);
                        break;
                    }
                    i++;
                }

                log.info("CheckCardId Result===>" + checkflag);

                if (checkflag) {
                    Calendar nowDateTime = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
                    SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
                    SimpleDateFormat sdf_pincode = new SimpleDateFormat(ShareParm.PARM_PINCODE_DATEFORMAT);
                    libm = sdf15.format(nowDateTime.getTime());

                    String tradeDate_Pincode = sdf_pincode.format(nowDateTime.getTime());
                    String resultcode = "";

                    ZTEPinCodeUtil ztePincode = new ZTEPinCodeUtil();
                    String result = ztePincode.putVoucherChangeStatusOCSlet(libm, StartSerialNo, EndSerialNo);
                    log.info("ZTE OCS XML Resonse)==>" + result);

                    if (result != null) {
                        VoucherChangeResultBean resultbean = new VoucherChangeResultBean();
                        DealerUtil dealerutil = new DealerUtil();
                        resultbean = dealerutil.parseZTEVoucherChangResultXMLString(result);
                        log.info("resultbean.getReturnCode()===>" + resultbean.getReturnCode());
                        log.info("resultbean.getReturnDesc()===>" + resultbean.getReturnDesc());

                        if (resultbean.getReturnCode().equals("0000")) {
                            Status = "0x00000000";
                            Status_Desc = "異動成功";
                        } else {
                            Status = "0x03000007";
                            Status_Desc = "異動失敗";
                        }
                    } else {
                        Status = "0x03000007";
                        Status_Desc = "異動失敗";
                    }
                } else {
                    Status = "0x02000010";
                    Status_Desc = "經銷商代碼與卡號不符";

                }
                responseStr = "<VoucherInfoResponse>\n"
                        + "<Result>\n"
                        + "<STATUS>" + Status + "</STATUS>\n"
                        + "<STATUS_DESC>" + Status_Desc + "</STATUS_DESC>\n"
                        + "<RESPONSE_TIMESTAMP>" + responsetime + "</RESPONSE_TIMESTAMP>\n"
                        + "</Result>\n"
                        + "</VoucherInfoResponse>\n";

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
                log.info("DealerVoucherChangeStatus ResponseStr ===>" + responseStr);
                log.info("DealerVoucherChangeStatus encode_responseStr==>" + encode_responseStr);
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
