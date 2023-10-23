/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.util;

//import com.apt.epay.share.ShareParm;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author kevinchang
 */
public class RecordUtil {

    private static final String version = "apiCFCapture version 1.0.1";
    private static final String author = "Create By Booker Hsu";
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final Logger log = Logger.getLogger("EPAY");

    private Properties pros = null;

    public ResultSet GetRecord5F() throws Exception {

        ResultSet rs=null;
        try {
            pros = new Properties();
            pros.load(new FileInputStream("/home/apdev/apiCFCapture/conf/mcpay.properties"));
        } catch (IOException ex) {
            log.info(ex);
        }

//        Record5FVO record5fvo = null;
        try {
            RecordDAO recordDao = new RecordDAO(JdbcUtils.getInstance(pros).getDefaultConnection());
            rs = recordDao.QueryF5();

        } catch (Exception ex) {
            log.info(ex);
        }

        return rs;
    }

    public ResultSet GetRecord3F() throws Exception {

        ResultSet rs=null;
        try {
            pros = new Properties();
            pros.load(new FileInputStream("/home/apdev/apiCFCapture/conf/mcpay.properties"));
        } catch (IOException ex) {
            log.info(ex);
        }

//        Record5FVO record5fvo = null;
        try {
            RecordDAO recordDao = new RecordDAO(JdbcUtils.getInstance(pros).getDefaultConnection());
            rs = recordDao.QueryF3();

        } catch (Exception ex) {
            log.info(ex);
        }

        return rs;
    }

    public ResultSet GetRecord1F() throws Exception {

        ResultSet rs=null;
        try {
            pros = new Properties();
            pros.load(new FileInputStream("/home/apdev/apiCFCapture/conf/mcpay.properties"));
        } catch (IOException ex) {
            log.info(ex);
        }

//        Record5FVO record5fvo = null;
        try {
            RecordDAO recordDao = new RecordDAO(JdbcUtils.getInstance(pros).getDefaultConnection());
            rs = recordDao.QueryF1();

        } catch (Exception ex) {
            log.info(ex);
        }

        return rs;
    }
    
}
