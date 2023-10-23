/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;

/**
 *
 * @author booker
 */
public class TransDateDAO {

    private Connection conn;
    private PreparedStatement psSystemParmQuery;
    private PreparedStatement psDateParmQuery;
    private static final Logger log = Logger.getLogger("EPAY");

    public TransDateDAO(Connection conn) throws Exception {

        this.conn = conn;
        conn.setAutoCommit(true);

        StringBuilder sb = new StringBuilder();
//        sb.append("SELECT TIMEDIFF(NOW(), ? ) AS KK; ");
        sb.append("SELECT (UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(?))/60 AS KK;");
        psSystemParmQuery = conn.prepareStatement(sb.toString());


    }
   

    TransDateVO QueryParamById(String newdate) throws Exception {

        TransDateVO transtime = null;
        log.info("Diff Time Now ==>" + newdate);

        try {
            transtime = new TransDateVO();
            
            psSystemParmQuery.setString( 1, newdate);
            ResultSet rs = psSystemParmQuery.executeQuery();
            if (rs.next()) {
               
                transtime.setDifftime(rs.getString("KK"));
                log.info("Diff Time is ==>" + transtime.getDifftime());
            }
            
            rs.close();

        } catch (Exception ex) {
            log.info(ex);
        } finally {
            return transtime;
        }
    }


}
