/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.util;

import com.apt.epay.share.ShareParm;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author kevinchang
 */
public class BundleDateUtil {

    private static final String version = "apiCFCapture version 1.0.1";
    private static final String author = "Create By Booker Hsu";
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final Logger log = Logger.getLogger("EPAY");

    private Properties pros = null;
//    private String PARM_CAPTURE_URL = null;
//    private int urlTimeout = 0;

//    static {
//        PropertyConfigurator.configure(System.getProperty("user.dir") + "/conf/log4j.properties");
//    }
    public String getNowDate() throws Exception {
        String result = "";
        try {
            pros = new Properties();
            pros.load(new FileInputStream("/home/apdev/apiCFCapture/conf/apiCFCapture.properties"));
//            pros.load(new FileInputStream(System.getProperty("user.dir") + "/conf/apiCFCapture.properties"));
//            urlTimeout = Integer.parseInt(pros.getProperty("URLTimeout"));

        } catch (IOException ex) {
            log.info(ex);
            String xmsg = "EPay apiCFCapture.properties is NULL";
            String text = ex.toString();
            String email_form = new ShareParm().PARM_MAIL_FROM;
            String dst_user = new ShareParm().PARM_MAIL_TO_OC;
            MailUtil.sendMail(new ShareParm().PARM_MAIL_RELAY_HOST, email_form, dst_user, xmsg, text);

        }

        BundleDateVO bundledate = null;
        try {
            BundleDateDAO bundledateDao = new BundleDateDAO(JdbcUtils.getInstance(pros).getDefaultConnection());
            bundledate = bundledateDao.QueryParamNow();
            log.info("bundledate.getBundledate()===>" + bundledate.getBundledate());
            result = bundledate.getBundledate();
        } catch (Exception ex) {
            log.info(ex);
        }

        return result;
    }

    public String AddMin(int min) {
        String result = "";
        try {
            pros = new Properties();
            pros.load(new FileInputStream("/home/apdev/apiCFCapture/conf/apiCFCapture.properties"));
//            pros.load(new FileInputStream(System.getProperty("user.dir") + "/conf/apiCFCapture.properties"));
//            urlTimeout = Integer.parseInt(pros.getProperty("URLTimeout"));
        } catch (IOException ex) {
            log.info(ex);

        }

        BundleDateVO bundledate = null;
        try {
            BundleDateDAO bundledateDao = new BundleDateDAO(JdbcUtils.getInstance(pros).getDefaultConnection());
            bundledate = bundledateDao.QueryParamByIdKK(min);
            log.info("bundledate.getBundledate()===>" + bundledate.getBundledate());
            result = bundledate.getBundledate();
        } catch (Exception ex) {
            log.info(ex);
        }

        return result;
    }

    public String getBundleDate(int hr) {
        String result = "";
        try {
            pros = new Properties();
            pros.load(new FileInputStream("/home/apdev/apiCFCapture/conf/apiCFCapture.properties"));
//            pros.load(new FileInputStream(System.getProperty("user.dir") + "/conf/apiCFCapture.properties"));
//            urlTimeout = Integer.parseInt(pros.getProperty("URLTimeout"));
        } catch (IOException ex) {
            log.info(ex);

        }

        BundleDateVO bundledate = null;
        try {
            BundleDateDAO bundledateDao = new BundleDateDAO(JdbcUtils.getInstance(pros).getDefaultConnection());
            bundledate = bundledateDao.QueryParamById(hr);
            log.info("bundledate.getBundledate()===>" + bundledate.getBundledate());
            result = bundledate.getBundledate();
        } catch (Exception ex) {
            log.info(ex);
        }

        return result;
    }

    public int GetDiffDayOfContractDay(String d1) {
        int result = 0;
        try {
            pros = new Properties();
            pros.load(new FileInputStream("/home/apdev/apiCFCapture/conf/apiCFCapture.properties"));
//            pros.load(new FileInputStream(System.getProperty("user.dir") + "/conf/apiCFCapture.properties"));
//            urlTimeout = Integer.parseInt(pros.getProperty("URLTimeout"));
        } catch (IOException ex) {
            log.info(ex);

        }
        try {
            BundleDateDAO bundledateDao = new BundleDateDAO(JdbcUtils.getInstance(pros).getDefaultConnection());
            result = bundledateDao.QueryDateDiff(d1);
            log.info("bundledateDao.QueryDateDiff() ===>" + result);
        } catch (Exception ex) {
            log.info(ex);
        }

        return result;
    }

}
