/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.dealer.api;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.dealer.util.DealerUtil;
import com.apt.epay.dealer.util.DearlerMDNReqBean;
import com.apt.epay.deposit.util.toolUtil;
import com.apt.epay.share.ShareBean;
import com.apt.util.PPMdnUtil;
import com.apt.util.Utilities;
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_DEALERMDN;
import java.io.IOException;
import java.io.PrintWriter;
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
public class DealerUserStateLookup extends HttpServlet {

    private final Logger log;

    public DealerUserStateLookup() {
        this.log = Logger.getLogger("EPAY");
    }

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

        log.info("DealerUserStateLookup.processRequest");
        log.info("Client IP :" + request.getRemoteAddr());

        String cpid = request.getParameter("CPID");
        log.info("CPID===>" + cpid);

        toolUtil tool = new toolUtil();
        Utilities util = new Utilities();
        String deskey = "";
        String responseStr = "";
        boolean cpkeyflag = false;

        PPMdnUtil mdntool = new PPMdnUtil();
        String responsetime = mdntool.getAPIResponseTime();

        String contractstatuscode = "";
        String ocs_status = "-1";

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
                log.info("===>" + encode_str_input);

                String str_input = util.decrypt(deskey, encode_str_input);
                log.info("DealerUserStateLookup INPUT==>" + str_input);

                try {
                    DealerUtil dealerutil = new DealerUtil();
                    DearlerMDNReqBean reqbean = new DearlerMDNReqBean();
                    reqbean = dealerutil.getMDNByparseXMLString(str_input, "UserStateLookupRequest");

                    String mdn = reqbean.getMdn();
                    String storeId = reqbean.getStoreId();
                    log.info("MDN===>" + mdn);
                    if (!"".equals(mdn)) {

                        EPAY_DEALERMDN epaydealermdn = epaybusinesscontroller.getDealerMDNByMDN(storeId, mdn);
                        log.info("epaydealermdn===>" + epaydealermdn);

                        if (epaydealermdn != null) {

                            PPMdnUtil ppmdn = new PPMdnUtil(mdn, false, true);
                            String checkresult = ppmdn.checkPPMDN(epaybusinesscontroller);

                            if ("1".equals(checkresult)) {
                                contractstatuscode = ppmdn.getContractstatuscode();
                                ocs_status = ppmdn.getOcsstatus();
                                Status = "0x00000000";
                                log.info("ppmdn.checkPPMDN(" + mdn + ", epaybusinesscontroller)==>" + checkresult);
                            } else {
                                Status = "0x01000001";
                                Status_Desc = "SOA檢驗失敗";
                            }
                        } else {
                            Status = "0x01000001";
                            Status_Desc = "認證失敗(門號與經銷商不符)";
                        }
                    } else { // mdn is null
                        Status = "0x01000001";
                        Status_Desc = "認證失敗(用戶門號不存在)";
                    }

                    responseStr = "<UserStateLookupResponse>\n"
                            + "<Result>\n"
                            + "<STATUS>" + Status + "</STATUS>\n"
                            + "<STATUS_DESC>" + Status_Desc + "</STATUS_DESC>\n"
                            + "<RESPONSE_TIMESTAMP>" + responsetime + "</RESPONSE_TIMESTAMP>\n"
                            + "<OCS_STATE>" + ocs_status + "</OCS_STATE>\n"
                            + "<CMS_STATE>" + contractstatuscode + "</CMS_STATE>\n"
                            + "</Result>\n"
                            + "</UserStateLookupResponse>";
                } catch (Exception ex) {
                    log.info(ex);
                }
            } else {
                Status = "0x02000010";
                Status_Desc = "未授權此功能";
                responseStr = "<UserStateLookupResponse>\n"
                        + "<Result>\n"
                        + "<STATUS>" + Status + "</STATUS>\n"
                        + "<STATUS_DESC>" + Status_Desc + "</STATUS_DESC>\n"
                        + "<RESPONSE_TIMESTAMP>" + responsetime + "</RESPONSE_TIMESTAMP>\n"
                        + "</Result>\n"
                        + "</UserStateLookupResponse>\n";
            }
        } catch (Exception ex) {
            log.info(ex);
        }

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            log.info("cpkeyflag====>" + cpkeyflag);
            if (cpkeyflag) {
                String encode_responseStr = util.encrypt(deskey, responseStr);
                log.info("DealerUserStateLookup ResponseStr ===>\n" + responseStr);
                log.info("DealerUserStateLookup encode_responseStr==>" + encode_responseStr);
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
