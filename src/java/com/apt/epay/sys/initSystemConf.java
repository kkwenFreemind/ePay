/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.sys;

import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import com.apt.epay.share.ShareParm;
import java.io.IOException;

public class initSystemConf extends HttpServlet {

    private static Logger log = Logger.getLogger(initSystemConf.class);

    @Override
    public void init() throws ServletException {
        initlog4j();
        initSysConf();
    }

    public void initlog4j() {

        String prefix = getServletContext().getRealPath("/");
        String file = getInitParameter("log4jConfigLocation");
        Properties log_prop = new Properties();
        try {
            if (file != null) {
                // PropertyConfigurator.configure("/"+prefix + file);
                FileInputStream inp = new FileInputStream("/" + prefix + file);
                log_prop.load(inp);
                PropertyConfigurator.configure(log_prop);
            }

        } catch (IOException ex) {
            log.info(ex);
        }
    }

    public synchronized void initSysConf() {

        String file;// = null;

        file = getInitParameter("SYSConfigLocation");

        Properties prop = new Properties();
        try {
            if (file != null) {
                String prop_file_path = "/" + file;
                System.out.println("prop_file_path===>" + prop_file_path);
                FileInputStream inp = new FileInputStream(prop_file_path);

                prop.load(inp);
                Enumeration settings = prop.keys();
                while (settings.hasMoreElements()) {
                    String setting = (String) settings.nextElement();
                    System.out.println("setting==>" + setting);
                    System.getProperties().put(setting, prop.get(setting));
                }
                ShareParm.PARM_ServletContext_PROP_FILE_PATH = prop_file_path;
                System.out.println("ShareParm.PARM_ServletContext_PROP_FILE_PATH==>" + ShareParm.PARM_ServletContext_PROP_FILE_PATH);
            }

        } catch (IOException ex) {
            log.info(ex);
        }
    }
}
