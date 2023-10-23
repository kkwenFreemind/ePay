/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.share;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zul.Include;

/**
 *
 * @author Administrator
 */
public class ShareBean {

    private static final Logger log = Logger.getLogger("EPAY");

    public void loadSysConfParameters() {

        Properties prop = new Properties();
        FileInputStream inp = null;
        try {

            String prop_file_path = ShareParm.PARM_ServletContext_PROP_FILE_PATH;
            if (prop_file_path == null || prop_file_path.equals("")) {
                prop_file_path = ShareParm.PARM_DEFAULT_PROP_FILE_PATH;
                System.out.println(ShareParm.PARM_DEFAULT_PROP_FILE_PATH);
            }
            inp = new FileInputStream(prop_file_path);

            prop.load(inp);
            Enumeration settings = prop.keys();
            while (settings.hasMoreElements()) {
                String setting = (String) settings.nextElement();
                System.getProperties().put(setting, prop.get(setting));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                inp.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void forwardToPage(Session session, Execution execution, String from_page, String to_page, Component parent_component, Component detach_component) {
        if (parent_component instanceof Include) {
            execution.getDesktop().setBookmark(execution.getDesktop().getBookmark() + "_s2");
            session.setAttribute("tmp_history_bookmark_" + execution.getDesktop().getBookmark(), to_page);
            session.setAttribute("tmp_return_comp", detach_component);
            session.setAttribute("tmp_has_from_page", from_page);
            Include include = (Include) parent_component;
            include.setSrc(ShareParm.PARM_UI_PATH + to_page);
            include.invalidate();
        } else {
            Executions.createComponents(ShareParm.PARM_UI_PATH + to_page, parent_component, null);
        }
        detach_component.detach();
    }

    public static void backToPage(Session session, Execution execution, Component parent_component, Component detach_component) {

        if (session.getAttribute("tmp_has_from_page") != null) {
            String back_page = (String) session.getAttribute("tmp_has_from_page");
            if (parent_component instanceof Include) {
                //execution.getDesktop().setBookmark(execution.getDesktop().getBookmark().replace("_s2", ""));
                if (execution.getDesktop().getBookmark().indexOf("_s2") != -1) {
                    String bookMark = execution.getDesktop().getBookmark().substring(0, execution.getDesktop().getBookmark().indexOf("_s2")) + execution.getDesktop().getBookmark().substring(execution.getDesktop().getBookmark().indexOf("_s2") + 3);
                    execution.getDesktop().setBookmark(bookMark);
                }
                //    session.removeAttribute("tmp_history_bookmark_" + execution.getDesktop().getBookmark());
                Include include = (Include) parent_component;

                include.setSrc(ShareParm.PARM_UI_PATH + back_page);
                include.invalidate();
            } else {
                Executions.createComponents(ShareParm.PARM_UI_PATH + back_page, parent_component, null);
            }
            detach_component.detach();
        }
    }

    public static void refreshPage(Session session, Execution execution, String page, Component parent_component, Component detach_component) {
        detach_component.detach();
        if (parent_component instanceof Include) {

            Include include = (Include) parent_component;
            include.setSrc(ShareParm.PARM_UI_PATH + page);
            include.invalidate();
        } else {
            Executions.createComponents(ShareParm.PARM_UI_PATH + page, parent_component, null);
        }
    }

    public static String getNewLineStr(String inStr, int strSize) {
        String rtresult = "";
        for (int i = 0; i < inStr.length(); i++) {
            if (i == 0) {
                rtresult += inStr.charAt(i);
            } else if (i % strSize != 0) {
                rtresult += inStr.charAt(i);
            } else {
                rtresult += inStr.charAt(i);
                rtresult += "\n";
            }
        }
        return rtresult;
    }

//    public static synchronized void clearApiOnlineUser() {
//        try {
//            onLineUser.clear();
//        } catch (Exception ex) {
//        }
//    }
    public ApplicationContext getApplicationContext(ServletContext ctx) {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(ctx);
    }

    public Object getBusinessBean(String beanname, ServletContext ctx) {
        ApplicationContext appctx = getApplicationContext(ctx);
        Object object = appctx.getBean(beanname);
        return object;
    }

    public double getCpuUsage() {
        double cpu = -1;
        try {
            // start up the command in child process
            String cmd = "top -n 2 -b -d 0.2";
            Process child = Runtime.getRuntime().exec(cmd);

            // hook up child process output to parent
            InputStream lsOut = child.getInputStream();
            InputStreamReader isr = new InputStreamReader(lsOut);
            BufferedReader bfr = new BufferedReader(isr);

            // read the child process' output
            String line;
            int emptyLines = 0;
            while (emptyLines < 3) {
                line = bfr.readLine();
                if (line.length() < 1) {
                    emptyLines++;
                }
            }
            bfr.readLine();
            bfr.readLine();
            line = bfr.readLine();
            //  System.out.println("Parsing line " + line);
            String delims = "%";
            String[] parts = line.split(delims);
            //  System.out.println("Parsing fragment " + parts[0]);
            delims = " ";

            parts = parts[0].split(delims);
            cpu = Double.parseDouble(parts[parts.length - 1]);
        } catch (Exception ex) { // exception thrown
            ex.printStackTrace();
        }

        return cpu;
    }

    public void printStackTrace(Throwable ex) {
        if (new ShareParm().PARM_DEBUGGING_MODE != null && new ShareParm().PARM_DEBUGGING_MODE.equalsIgnoreCase("Y")) {
            ex.printStackTrace();
        }
    }

    public void printMsgToConsole(String msg) {
        if (new ShareParm().PARM_DEBUGGING_MODE != null && new ShareParm().PARM_DEBUGGING_MODE.equalsIgnoreCase("Y")) {
            log.info(msg);
        }
    }

    public boolean isCachePoolMode() {
        boolean rtresult = false;
        if (new ShareParm().PARM_CACHEPOOL_MODE != null && new ShareParm().PARM_CACHEPOOL_MODE.equalsIgnoreCase("Y")) {
            rtresult = true;
        }
        return rtresult;
    }


    public Object getBusinessBeanWithRealFIlePath(String beanname, String path) {
        //      ApplicationContext appctx = new FileSystemXmlApplicationContext("D:\\netbeans\\MicroPaymentWeb\\web\\WEB-INF\\web-config.xml");
        ApplicationContext appctx = new FileSystemXmlApplicationContext(path);

        Object object = appctx.getBean(beanname);
        return object;
    }

    public String convertIntToStr(int inInt, int dig_length) {
        String rtresult = null;
        try {
            String tmpStr = String.valueOf(inInt);
            while (tmpStr.length() < dig_length) {
                tmpStr = "0" + tmpStr;
            }
            rtresult = tmpStr;
        } catch (Exception ex) {
        }
        return rtresult;
    }

    public Timestamp getSqlTimestamp_byNow() {
        java.sql.Timestamp result = new java.sql.Timestamp(new java.util.Date().getTime());

        return result;
    }

    public Timestamp getSqlTimestamp_byDate(Date date) {
        java.sql.Timestamp result = new java.sql.Timestamp(date.getTime());
        return result;
    }

    public Connection getConnection_byJNDI() throws Exception {
        return getConnection_byJNDI("SMSDPDS");
    }

    // added start by chou in 20121120 for new request 
    public Connection getConnection_byJNDI(String ds_name) throws Exception {
        Connection conn = null;

        Context envCtx = new InitialContext();
        DataSource ds = (DataSource) envCtx.lookup("java:/" + ds_name);

        conn = ds.getConnection();

        return conn;
    }
 

    public String getValidSourceAddr(String src_addr) {
        String rtresult = null;
        if (src_addr != null) {
            if (src_addr.startsWith("09")) {
                rtresult = src_addr.substring(1);
            }
        }
        return rtresult;
    }

    public boolean isIptablesAuth(int flag, boolean ip_valid) {
        boolean rtresult = false;
        if (flag == new ShareParm().PARM_IP_TABLES_BLACK) {
            if (ip_valid) {
                rtresult = false;
            } else {
                rtresult = true;
            }

        } else if (flag == new ShareParm().PARM_IP_TABLES_WHITE) {
            if (ip_valid) {
                rtresult = true;
            } else {
                rtresult = false;
            }
        }

        return rtresult;
    }

    public String getValidDstAddr(String dst_addr) {
        String rtresult = null;
        if (dst_addr.matches(ShareParm.PARM_REGULAR_MDN_2)) {
            rtresult = "0" + dst_addr;
        } else if (dst_addr.matches(ShareParm.PARM_REGULAR_MDN_3)) {
            rtresult = "0" + dst_addr.substring(3);
        } else {
            rtresult = dst_addr;
        }
        return rtresult;
    }

    public boolean isCorrectDstAddr(String dst_addr) {
        boolean rtresult = false;
        if (dst_addr != null && !dst_addr.equals("")) {
            if (dst_addr.matches(ShareParm.PARM_REGULAR_MDN)
                    || dst_addr.matches(ShareParm.PARM_REGULAR_MDN_2)
                    || dst_addr.matches(ShareParm.PARM_REGULAR_MDN_3)
                    || dst_addr.matches(ShareParm.PARM_REGULAR_MDN_4)
                    || dst_addr.matches(ShareParm.PARM_REGULAR_MDN_5)) {

                rtresult = true;

            }
        }
        return rtresult;
    }

    public String getValidMsg(String msg) {
        String rtresult = null;
        if (msg != null) {
            msg = msg.replace("&", "＆");
            if (msg.indexOf("<Message>") != -1 && msg.indexOf("</Message>") != -1) {
                int start_pos = msg.indexOf("<Message>") + 9;
                int stop_pos = msg.indexOf("</Message>");
                String tmp_str = msg.substring(start_pos, stop_pos);
                String tmp_str_mod = tmp_str;
                tmp_str_mod = tmp_str_mod.replace("<", "＜");
                tmp_str_mod = tmp_str_mod.replace(">", "＞");
                msg = msg.replace(tmp_str, tmp_str_mod);
                // System.out.println("@@@@msg:"+msg);
            }
            if (msg.indexOf("<message>") != -1 && msg.indexOf("</message>") != -1) {
                int start_pos = msg.indexOf("<message>") + 9;
                int stop_pos = msg.indexOf("</message>");
                String tmp_str = msg.substring(start_pos, stop_pos);
                String tmp_str_mod = tmp_str;
                tmp_str_mod = tmp_str_mod.replace("<", "＜");
                tmp_str_mod = tmp_str_mod.replace(">", "＞");
                msg = msg.replace(tmp_str, tmp_str_mod);
                // System.out.println("@@@@msg:"+msg);
            }
            if (msg.indexOf("<MESSAGE>") != -1 && msg.indexOf("</MESSAGE>") != -1) {
                int start_pos = msg.indexOf("<MESSAGE>") + 9;
                int stop_pos = msg.indexOf("</MESSAGE>");
                String tmp_str = msg.substring(start_pos, stop_pos);
                String tmp_str_mod = tmp_str;
                tmp_str_mod = tmp_str_mod.replace("<", "＜");
                tmp_str_mod = tmp_str_mod.replace(">", "＞");
                msg = msg.replace(tmp_str, tmp_str_mod);
                // System.out.println("@@@@msg:"+msg);
            }
            if (msg.indexOf("<Subject>") != -1 && msg.indexOf("</Subject>") != -1) {
                int start_pos = msg.indexOf("<Subject>") + 9;
                int stop_pos = msg.indexOf("</Subject>");
                String tmp_str = msg.substring(start_pos, stop_pos);
                String tmp_str_mod = tmp_str;
                tmp_str_mod = tmp_str_mod.replace("<", "＜");
                tmp_str_mod = tmp_str_mod.replace(">", "＞");
                msg = msg.replace(tmp_str, tmp_str_mod);
                // System.out.println("@@@@msg:"+msg);
            }
            if (msg.indexOf("<SUBJECT>") != -1 && msg.indexOf("</SUBJECT>") != -1) {
                int start_pos = msg.indexOf("<SUBJECT>") + 9;
                int stop_pos = msg.indexOf("</SUBJECT>");
                String tmp_str = msg.substring(start_pos, stop_pos);
                String tmp_str_mod = tmp_str;
                tmp_str_mod = tmp_str_mod.replace("<", "＜");
                tmp_str_mod = tmp_str_mod.replace(">", "＞");
                msg = msg.replace(tmp_str, tmp_str_mod);
                // System.out.println("@@@@msg:"+msg);
            }
            if (msg.indexOf("<subject>") != -1 && msg.indexOf("</subject>") != -1) {
                int start_pos = msg.indexOf("<subject>") + 9;
                int stop_pos = msg.indexOf("</subject>");
                String tmp_str = msg.substring(start_pos, stop_pos);
                String tmp_str_mod = tmp_str;
                tmp_str_mod = tmp_str_mod.replace("<", "＜");
                tmp_str_mod = tmp_str_mod.replace(">", "＞");
                msg = msg.replace(tmp_str, tmp_str_mod);
                // System.out.println("@@@@msg:"+msg);
            }
            rtresult = msg;
        }
        return rtresult;
    }

//    public boolean hasChinessStr(String str) {
//        boolean rtresult = false;
//        for (int i = 0; i < str.length(); i++) {
//            String tmp_str = str.substring(i, i + 1);
//            if (tmp_str.matches(ShareParm.PARM_REGULAR_CHINESE)) {
//                rtresult = true;
//            }
//        }
//        return rtresult;
//    }

//    public boolean isAsciiStr(String str) {
//        boolean rtresult = false;
//        //   for (int i = 0; i < str.length(); i++) {
//        //     String tmp_str = str.substring(i, i + 1);
//        if (str != null) {
//            //  if (str.matches(ShareParm.PARM_REGULAR_ASCII)) {
//            rtresult = str.matches(ShareParm.PARM_REGULAR_ASCII);
//            //  }
//        }
//        //    }
//        return rtresult;
//    }

//    public String getRandEventId() {
////        System.out.println("@@@@@getSeqSeed:" + getSeqSeed());
//        return (DateUtil.getCurrentDateTime("yyyyMMddHHmmss") + toolsUtil.getRandStr(4, getSeqSeed()) + new ShareParm().PARM_AP_SERVER_CODE);
//    }
//
//    //訂單編號
//    public String getLibm() {
//        return (System.currentTimeMillis() + toolsUtil.getRandDig(2, getSeqSeed()));
//
//    }

//    public synchronized long getSeqSeed() {
//        return ShareParm.systemSeed.incrementAndGet();
//    }

    public void clearChildComponet(AbstractComponent componet) {
        if (componet.getChildren() == null || componet.getChildren().isEmpty()) {
            componet.detach();
        } else {
            AbstractComponent[] acs = (AbstractComponent[]) componet.getChildren().toArray(new AbstractComponent[0]);
            for (int i = 0; i < acs.length; i++) {
                clearChildComponet(acs[i]);
            }
        }
    }

    public Connection getConnectionByOord(Integer oord_type) throws Exception {
        switch (oord_type) {
            case 0:
            case 1:
                return new ShareBean().getConnection_byJNDI("SMSDP_OLD_DS");

            case 2:
            case 3:
                return new ShareBean().getConnection_byJNDI("SMSDPDS");
            default:
                return new ShareBean().getConnection_byJNDI("SMSDPDS");
        }
    }

    // Epay function
    public static String getAmountForBucketID(String bucketid, String tmp_amount) {
        String result = tmp_amount;
        if (bucketid.equals("610") || bucketid.equals("620")) {
            result = String.valueOf((int) Math.round(Float.valueOf(tmp_amount) * 10000));
        }

        if (bucketid.equals("720") || bucketid.equals("730")) {
            double ft = Double.valueOf(tmp_amount) * 1048576;
            result = new DecimalFormat("0").format(ft);
        }

        if (bucketid.equals("810")) {
            result = String.valueOf((int) Math.round(Float.valueOf(tmp_amount) / 0.033));
        }

        log.info("Add Bucket===>" + bucketid + ",amount" + result);
        return result;
    }

    public static String showAmountForBucketID(String bucketid, String tmp_amount) {
        String result = tmp_amount;
        if (bucketid.equals("610") || bucketid.equals("620")) {
            result = String.valueOf((int) Math.round(Float.valueOf(tmp_amount) / 10000));
        }

        if (bucketid.equals("720") || bucketid.equals("730")) {
            double ft = Double.valueOf(tmp_amount) / 1048576;
            result = new DecimalFormat("0").format(ft);
        }

        if (bucketid.equals("810")) {
            result = String.valueOf((int) Math.round(Float.valueOf(tmp_amount) * 0.033));
        }
        log.info("Show Bucket===>" + bucketid + ",amount" + result);
        return result;
    }
}
