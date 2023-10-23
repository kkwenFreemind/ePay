/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.util;

import com.apt.epay.share.ShareParm;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class MailUtil {

    private static final Logger log = Logger.getLogger("EPAY");

    public boolean sendSimpleMail(String message) {
        boolean result = false;
        String email_form = new ShareParm().PARM_MAIL_FROM;
        String dst_user = new ShareParm().PARM_MAIL_TO_OC + ";" + new ShareParm().PARM_MAIL_TO_4GOCS;
        try {
            boolean sendResult = MailUtil.sendMail(new ShareParm().PARM_MAIL_RELAY_HOST, email_form, dst_user, message, message);
            log.info("send Email ==>" + sendResult + " , " + message);
        } catch (Exception ex) {
            log.info(ex);
        }

        return result;
    }

    public static boolean sendMail(String mailHost, String from, String to, String subject, String body) throws Exception {
        InternetAddress[] address = null;

        boolean rtsuccess = false;

        if ((from != null) && (to != null) && (subject != null) && (body != null)) // we have mail to send
        {
            try {

                //Get system properties
                Properties props = System.getProperties();

                //Specify the desired SMTP server
                props.put("mail.host", mailHost);
                props.put("mail.transport.protocol", "smtp");
                // create a new Session object
                Session session = Session.getInstance(props, null);
                session.setDebug(false);
                // create a new MimeMessage object (using the Session created above)
                MimeMessage message = new MimeMessage(session);

                message.setFrom(new InternetAddress(from));
                try {
                    if (to.indexOf(";") != -1) {
                        to = to.replaceAll(";", ",");
                    }
                } catch (Exception ex) {
                }
                address = InternetAddress.parse(to, false);
                //  Multipart multipart = new Multopart();

                message.setRecipients(Message.RecipientType.TO, address);
                message.setSubject(subject, "UTF-8");
                message.setSentDate(new Date());
                message.setText(body, "UTF-8");

                //         message.setHeader("Return-Path","ryanchen@aptg.com.tw");
                //    message.setContent(body, "text/plain");
                message.setSentDate(new Date());
                message.setContent(body, "text/html; charset=UTF-8");
                Transport.send(message);

                //       String nowuser =(String) req.getSession().getAttribute("user_id");
                // it worked!
                //      System.out.println("<b>Thank you.  Your message to " + to + " was successfully sent.</b>");
                rtsuccess = true;

            } catch (Exception ex) {
                rtsuccess = false;
                System.out.println("Unable to send message: " + body + " to " + to);
                //  ex.printStackTrace();
                throw ex;
            }

        }
        return rtsuccess;
    }

    public static boolean sendMail(String mailHost, String from_address, String from_name, String to, String subject, String body, Map args) throws Exception {
        InternetAddress[] address = null;

        boolean rtsuccess = false;

        if ((from_address != null) && (from_name != null) && (to != null) && (subject != null) && (body != null)) // we have mail to send
        {
            try {

                //Get system properties
                Properties props = System.getProperties();

                //Specify the desired SMTP server
                props.put("mail.host", mailHost);
                props.put("mail.transport.protocol", "smtp");
                // create a new Session object
                Session session = Session.getInstance(props, null);
                session.setDebug(false);
                // create a new MimeMessage object (using the Session created above)
                MimeMessage message = new MimeMessage(session);

                message.setFrom(new InternetAddress(from_address, from_name));
                try {
                    if (to.indexOf(";") != -1) {
                        to = to.replaceAll(";", ",");
                    }
                } catch (Exception ex) {
                }
                address = InternetAddress.parse(to, false);
                //  Multipart multipart = new Multopart();

                message.setRecipients(Message.RecipientType.TO, address);
                message.setSubject(subject, "UTF-8");
                message.setSentDate(new Date());
                String formatedbody = formatMailBody(body, args);
                message.setText(formatedbody, "UTF-8");

                //         message.setHeader("Return-Path","ryanchen@aptg.com.tw");
                //    message.setContent(body, "text/plain");
                message.setSentDate(new Date());
                message.setContent(formatedbody, "text/html; charset=UTF-8");
                Transport.send(message);

                //       String nowuser =(String) req.getSession().getAttribute("user_id");
                // it worked!
                //      System.out.println("<b>Thank you.  Your message to " + to + " was successfully sent.</b>");
                rtsuccess = true;

            } catch (Exception ex) {
                rtsuccess = false;
                System.out.println("Unable to send message: " + body + " to " + to);
                ex.printStackTrace();
                throw ex;
            }

        }
        return rtsuccess;
    }

    public static boolean sendMail(String mailHost, String from, String to, String subject, String body, Map args) throws Exception {
        InternetAddress[] address = null;

        boolean rtsuccess = false;

        if ((from != null) && (to != null) && (subject != null) && (body != null)) // we have mail to send
        {
            try {

                //Get system properties
                Properties props = System.getProperties();

                //Specify the desired SMTP server
                props.put("mail.host", mailHost);
                props.put("mail.transport.protocol", "smtp");
                // create a new Session object
                Session session = Session.getInstance(props, null);
                session.setDebug(false);
                // create a new MimeMessage object (using the Session created above)
                MimeMessage message = new MimeMessage(session);

                message.setFrom(new InternetAddress(from));
                try {
                    if (to.indexOf(";") != -1) {
                        to = to.replaceAll(";", ",");
                    }
                } catch (Exception ex) {
                }
                address = InternetAddress.parse(to, false);
                //  Multipart multipart = new Multopart();

                message.setRecipients(Message.RecipientType.TO, address);
                message.setSubject(subject, "UTF-8");
                message.setSentDate(new Date());
                String formatedbody = formatMailBody(body, args);
                message.setText(formatedbody, "UTF-8");

                //         message.setHeader("Return-Path","ryanchen@aptg.com.tw");
                //    message.setContent(body, "text/plain");
                message.setSentDate(new Date());
                message.setContent(formatedbody, "text/html; charset=UTF-8");
                Transport.send(message);

                //       String nowuser =(String) req.getSession().getAttribute("user_id");
                // it worked!
                //      System.out.println("<b>Thank you.  Your message to " + to + " was successfully sent.</b>");
                rtsuccess = true;

            } catch (Exception ex) {
                rtsuccess = false;
                System.out.println("Unable to send message: " + body + " to " + to);
                ex.printStackTrace();
                throw ex;
            }

        }
        return rtsuccess;
    }

    public static boolean sendMailWithAttachment(String mailHost, String from, String to, String subject, String body, String attachment_file) throws Exception {
        InternetAddress[] address = null;

        boolean rtsuccess = false;

        if ((from != null) && (to != null) && (subject != null) && (body != null)) // we have mail to send
        {
            try {

                //Get system properties
                Properties props = System.getProperties();

                //Specify the desired SMTP server
                props.put("mail.host", mailHost);
                props.put("mail.transport.protocol", "smtp");
                // create a new Session object
                Session session = Session.getInstance(props, null);
                session.setDebug(false);
                // create a new MimeMessage object (using the Session created above)
                MimeMessage message = new MimeMessage(session);

                message.setFrom(new InternetAddress(from));
                try {
                    if (to.indexOf(";") != -1) {
                        to = to.replaceAll(";", ",");
                    }
                } catch (Exception ex) {
                }
                address = InternetAddress.parse(to, false);
                //  Multipart multipart = new Multopart();

                message.setRecipients(Message.RecipientType.TO, address);
                message.setSubject(subject, "UTF-8");
                message.setSentDate(new Date());
                message.setText(body, "UTF-8");
                if (attachment_file != null && !attachment_file.equals("")) {
                    DataSource fds = new FileDataSource(attachment_file);
                    MimeBodyPart mbp = new MimeBodyPart();
                    Multipart mp = new MimeMultipart();
                    mbp.setDataHandler(new DataHandler(fds));
                    mbp.setFileName(fds.getName());
                    mp.addBodyPart(mbp);
                    message.setContent(mp);

                }
                //         message.setHeader("Return-Path","ryanchen@aptg.com.tw");
                //    message.setContent(body, "text/plain");
                message.setSentDate(new Date());
                //  message.setContent(body, "text/html; charset=UTF-8");
                Transport.send(message);

                //       String nowuser =(String) req.getSession().getAttribute("user_id");
                // it worked!
                //      System.out.println("<b>Thank you.  Your message to " + to + " was successfully sent.</b>");
                rtsuccess = true;

            } catch (Exception ex) {
                rtsuccess = false;
                System.out.println("Unable to send message: ");
                ex.printStackTrace();
                throw ex;
            }

        }
        return rtsuccess;
    }

    private static String formatMailBody(String pattern, Map args) {
        String rtresult = null;
        if (pattern != null && args != null) {
            String tmpStr = pattern;
            Set set = args.keySet();
            Iterator it = set.iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                String value = (String) args.get(key);
                tmpStr = ReplaceAllStr(tmpStr, "${" + key + "}", value);
            }
            rtresult = tmpStr;
        }

        return rtresult;
    }

    private static String ReplaceAllStr(String str, String key, String value) {
        if (str.indexOf(key) != -1) {
            str = str.replace(key, value);
        } else {
            return str;
        }
        return ReplaceAllStr(str, key, value);
    }

    public static void main(String[] args) throws Exception {
//        InternetAddress[] aaa = InternetAddress.parse("aaa@aaa.aaaaaaa@aa.cc");
        //      System.out.println(aaa.length);
        //     System.out.println("@@@@:" + ReplaceAllStr("testaaaaaas", "${NAME}", "測123"));
        Map map_mail_args = new HashMap();

        map_mail_args.put("MDN", "0982347991");

        String message = "test123,${MDN}";
        String subject = "test";
        String dst_user = "ryanchen@aptg.com.tw";
        String email_form = "ryanchen@aptg.com.tw";
        MailUtil.sendMail("10.31.7.150", email_form, dst_user, subject, message, map_mail_args);

    }
}
