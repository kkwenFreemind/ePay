/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.util;

//import com.apt.epay.share.ShareParm;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;

/**
 *
 * @author booker
 */
public class RecordDAO {

    private Connection conn;
    private PreparedStatement ps5FQuery;
    private PreparedStatement ps1FQuery;
    private PreparedStatement ps3FQuery;
    private static final Logger log = Logger.getLogger("EPAY");

    public RecordDAO(Connection conn) throws Exception {

        this.conn = conn;
        conn.setAutoCommit(true);

        StringBuilder sb_f5 = new StringBuilder();
        sb_f5.append("SELECT * From TRANSACTION order by tx_datetime desc; ");
        ps5FQuery = conn.prepareStatement(sb_f5.toString());

        StringBuilder sb_f3 = new StringBuilder();
        sb_f3.append("SELECT * From WAKEUPLOG order by WAKEUP_DATETIME desc ; ");
        ps3FQuery = conn.prepareStatement(sb_f3.toString());

        StringBuilder sb_f1 = new StringBuilder();
        sb_f1.append("SELECT * From WORKINOUTLOG order by WORKINOUT_DATE desc ; ");
        ps1FQuery = conn.prepareStatement(sb_f1.toString());

    }

    ResultSet QueryF5() throws Exception {

        ResultSet rs = null;
        try {
            rs = ps5FQuery.executeQuery();
        } catch (Exception ex) {
            throw ex;
        }
        return rs;
    }

    ResultSet QueryF3() throws Exception {

        ResultSet rs = null;
        try {
            rs = ps3FQuery.executeQuery();
        } catch (Exception ex) {
            throw ex;
        }
        return rs;
    }

    ResultSet QueryF1() throws Exception {

        ResultSet rs = null;
        try {
            rs = ps1FQuery.executeQuery();
        } catch (Exception ex) {
            throw ex;
        }
        return rs;
    }    
}
