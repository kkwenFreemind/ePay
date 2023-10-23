/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.util;

import com.apt.epay.share.ShareParm;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;

/**
 *
 * @author booker
 */
public class BundleDateDAO {

    private Connection conn;
    private PreparedStatement psSystemParmQuery;
    private PreparedStatement psSystemParmMinQuery;
    private PreparedStatement psDateDiffQuery;
    private PreparedStatement psNowQuery;
    private static final Logger log = Logger.getLogger("EPAY");

    public BundleDateDAO(Connection conn) throws Exception {

        this.conn = conn;
        conn.setAutoCommit(true);

        if (conn == null) {
            //email
            String xmsg = "EPay Database(MySQL) Connect Error";
            String email_form = new ShareParm().PARM_MAIL_FROM;
            String dst_user = new ShareParm().PARM_MAIL_TO_OC;
            try {
                MailUtil.sendMail(new ShareParm().PARM_MAIL_RELAY_HOST, email_form, dst_user, xmsg, xmsg);
            } catch (Exception ex) {
                log.info(ex);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT DATE_ADD(NOW( ),INTERVAL ? HOUR) AS KK; ");
        psSystemParmQuery = conn.prepareStatement(sb.toString());

        StringBuilder sbmin = new StringBuilder();
        sbmin.append("SELECT DATE_ADD(NOW( ),INTERVAL ? MINUTE) AS KK; ");
        psSystemParmMinQuery = conn.prepareStatement(sbmin.toString());
        
        StringBuilder sbdiff = new StringBuilder();
        sbdiff.append("SELECT DATEDIFF(?,NOW()) AS KK; ");
        psDateDiffQuery = conn.prepareStatement(sbdiff.toString());

        StringBuilder sbnow = new StringBuilder();
        sbnow.append("SELECT NOW() AS KK; ");
        psNowQuery = conn.prepareStatement(sbnow.toString());

    }

    BundleDateVO QueryParamByIdKK(int minx) throws Exception {

        BundleDateVO bundledate = null;
        String min = String.valueOf(minx);
        log.info("ADD total min===>" + min);

        try {
            psSystemParmMinQuery.setString(1, min);
            ResultSet rs = psSystemParmMinQuery.executeQuery();
            if (rs.next()) {
                bundledate = new BundleDateVO();
                bundledate.setBundledate(rs.getString("KK"));
                log.info("bundledate==>" + bundledate.getBundledate());
            }
            rs.close();
        } catch (Exception ex) {
            throw ex;
        } finally {
            return bundledate;
        }
    }

    BundleDateVO QueryParamById(int hrx) throws Exception {

        BundleDateVO bundledate = null;
        String hr = String.valueOf(hrx);
        log.info("ADD total hours===>" + hr);

        try {
            psSystemParmQuery.setString(1, hr);
            ResultSet rs = psSystemParmQuery.executeQuery();
            if (rs.next()) {
                bundledate = new BundleDateVO();
                bundledate.setBundledate(rs.getString("KK"));
                log.info("bundledate==>" + bundledate.getBundledate());
            }
            rs.close();
        } catch (Exception ex) {
            throw ex;
        } finally {
            return bundledate;
        }
    }

    int QueryDateDiff(String pdate) throws Exception {

        int result = 0;
        log.info("QueryDateDiff date ===>" + pdate);

        try {
            psDateDiffQuery.setString(1, pdate);
            ResultSet rs = psDateDiffQuery.executeQuery();
            if (rs.next()) {
                result = rs.getInt("KK");
                log.info("QueryDateDiff Result ==>" + result);
            }
            rs.close();
        } catch (Exception ex) {
            log.info(ex);
        } finally {
            return result;
        }
    }

    BundleDateVO QueryParamNow() throws Exception {

        BundleDateVO bundledate = null;
        try {

            ResultSet rs = psNowQuery.executeQuery();
            if (rs.next()) {
                bundledate = new BundleDateVO();
                bundledate.setBundledate(rs.getString("KK"));
                log.info("bundledate NOW()==>" + bundledate.getBundledate());
            }
            rs.close();
        } catch (Exception ex) {
            throw ex;
        } finally {
            return bundledate;
        }
    }
}
