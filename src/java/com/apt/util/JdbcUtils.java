package com.apt.util;

import com.apt.epay.share.ShareParm;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class JdbcUtils {

    private static JdbcUtils instance = null;
    private String jdbcDriver = null;
    private String jdbcUrl = null;
    private String jdbcId = null;
    private String jdbcPassword = null;

    private JdbcUtils() {
    }

    public static JdbcUtils getInstance(Properties pros) throws Exception {

        try {
            if (instance == null) {
                instance = new JdbcUtils();
                instance.jdbcDriver = pros.getProperty("jdbc.driverClassName");
                instance.jdbcUrl = pros.getProperty("jdbc.url");
                instance.jdbcId = pros.getProperty("jdbc.username");
                instance.jdbcPassword = pros.getProperty("jdbc.password");
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        return instance;
    }

    public Connection getConnection(String jdbcUrl, String userName, String password) throws Exception {
        return getConnection(this.jdbcDriver, jdbcUrl, userName, password);
    }

    public Connection getConnection(String jdbcDriver, String jdbcUrl, String userName, String password) throws Exception {

        try {
            Class.forName(jdbcDriver);
            return DriverManager.getConnection(jdbcUrl, userName, password);
        } catch (Exception e) {
            System.err.println(e.toString());
            String xmsg = "EPay Coonnect DB Error";
            String text = e.toString();
            String email_form = new ShareParm().PARM_MAIL_FROM;
            String dst_user = new ShareParm().PARM_MAIL_TO_OC;
            MailUtil.sendMail(new ShareParm().PARM_MAIL_RELAY_HOST, email_form, dst_user, xmsg, text);

        }
        return null;
    }

    public Connection getDefaultConnection() throws Exception {
        return this.getConnection(this.jdbcUrl, this.jdbcId, this.jdbcPassword);
    }

}
