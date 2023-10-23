/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;


/**
 *
 * @author kevinchang
 */
public class TransDateUtil {

//    private static final String version = "apiCFCapture version 1.0.1";
//    private static final String author = "Create By Booker Hsu";
//    private static final String USER_AGENT = "Mozilla/5.0";
    private static final Logger log = Logger.getLogger("EPAY");
    private static final int TenMin = 600;

    private Properties pros = null;

    public boolean getDiffTime(String newdate) {
        boolean result = false;
        try {
            pros = new Properties();
            pros.load(new FileInputStream("/home/apdev/apiCFCapture/conf/apiCFCapture.properties"));
//            pros.load(new FileInputStream("/home/apdev/apiCFCapture/conf/apiCFCapture.properties"));

        } catch (IOException ex) {
            log.info(ex);
        }

        TransDateVO transdate = null;
        try {
            TransDateDAO transdateDao = new TransDateDAO(JdbcUtils.getInstance(pros).getDefaultConnection());
            transdate = transdateDao.QueryParamById(newdate);
            log.info("transdate.getDifftime()===>" + transdate.getDifftime());

            float min = Float.valueOf(transdate.getDifftime());

//            String tokens[] = str.split(":");
//
//            int hh = Integer.valueOf(tokens[0]);
//            int mm = Integer.valueOf(tokens[1]);
//            int ss = Integer.valueOf(tokens[2]);
//            if(hh<0)  hh=hh*(-1);
//            int count = hh*60*60 + mm*60 + ss;
//            log.info("The Count of min is ===>"+ count);
//            
            if(min < 10){
                result = true;
                log.info(min +"小於600秒");
            }else{
                result = false;
                log.info(min +"大於600秒");
            }

        } catch (Exception ex) {
            log.info(ex);
        }

        return result;
    }

}
