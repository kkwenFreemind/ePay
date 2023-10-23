/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.beans.SOAReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.nokia.bean.NokiaPincodeResultBean;
import com.apt.epay.nokia.main.NokiaMainPincodeUtil;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.util.MailUtil;
import com.apt.util.PPMdnUtil;
import com.apt.util.PinCodeUtil;
import com.apt.util.SoaProfile;
import com.epay.ejb.bean.EPAY_PROMOTIONCODE;
import java.io.BufferedReader;
import java.io.File;
//import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Messagebox;
//import org.zkoss.zul.Window;
import org.apache.log4j.Logger;

/**
 *
 * @author kevinchang
 */
// KK PINCODE
public class ChannelPincodeFileUpload extends GenericForwardComposer {

    private Fileupload fileupload;
    private static final Logger log = Logger.getLogger("EPAY");

    @Override
    public void doAfterCompose(Component comp) {
        try {
            super.doAfterCompose(comp);
//            this.window = (Window) comp.getFellow("window");
            this.fileupload = (Fileupload) comp.getFellow("fileupload");
            this.fileupload.setLabel("上傳批次儲值檔案");

        } catch (Exception e) {
            log.info(e);
        }
    }

    private int getFileCount(String filename) {
        int result = 0;
        FileReader kk_fr;// = null;
        BufferedReader kk_bfr;// = null;
        try {
            File tmp_file = new File(filename);
            kk_fr = new FileReader(tmp_file);
            kk_bfr = new BufferedReader(kk_fr);
            String str;//="";
            while ((str = kk_bfr.readLine()) != null) {
                str = str.trim();
                if (str.contains(",")) {
                    result++;
                }
            }
        } catch (IOException ex) {
            log.info(ex);
            result = 0;
        }
        return result;
    }

    private boolean checkCSV(File filename) {
        boolean result = false;
        if (filename.getName().matches(ShareParm.PARM_REGULAR_CSVFILE)) {
            result = true;
        }
        return result;
    }

    private int getCount(String ss) {
        int result = -1;
        try {
            result = Integer.valueOf(ss);
        } catch (NumberFormatException ex) {
            log.info(ex);
        }
        return result;
    }

    public void onUpload$fileupload(UploadEvent event) {

        String user_code = (String) Sessions.getCurrent().getAttribute("user_code");
        String user_email = (String) Sessions.getCurrent().getAttribute("email");
        log.info("IP:" + Executions.getCurrent().getRemoteAddr() + " Account:" + user_code + "--> batch upload pincod file for EPAY");
        log.info(user_code + "'s Email:" + user_email);

        String filename_count;
        File tmp_check_file;
        tmp_check_file = new File(new ShareParm().PARM_SYS_UPLOAD_TMP_PATH + user_code + ".txt");

        if (!tmp_check_file.exists()) {

            int success_count = 0;
            int fail_count = 0;

            EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

            int record_reall_count;// = 0; // 筆數歸零
            Media media = event.getMedia();

            if (media != null) {
                FileOutputStream fout;// = null;
                File tmp_upload_file;// = null;
                InputStream in;//= null;

                try {
                    tmp_check_file.createNewFile();
                    tmp_upload_file = new File(new ShareParm().PARM_SYS_UPLOAD_TMP_PATH + media.getName());
                    log.info("tmp_upload_file==>" + new ShareParm().PARM_SYS_UPLOAD_TMP_PATH + "/" + media.getName());

                    boolean chkcsv = checkCSV(tmp_upload_file);
                    if (chkcsv) {

                        //檢查是否之前已經有Load過
                        String filename = media.getName().toLowerCase();
                        List list = epaybusinesscontroller.getTxLogListByBatchFie(filename);
                        if (list.isEmpty()) {

                            String[] AfterSplit = (filename.split("\\.")[0]).split("_");
                            log.info("AfterSplit.lenght===>" + AfterSplit.length);

                            if (AfterSplit.length == 6) {
                                filename_count = AfterSplit[5];
                                String fileuser = AfterSplit[3];
                                log.info("fileuser==>" + fileuser + ",user_code==>" + user_code);

                                if (fileuser.equalsIgnoreCase(user_code)) {

                                    int filenamecount = getCount(filename_count);

                                    if (filenamecount > 0) {

                                        log.info("===>" + new ShareParm().PARM_SYS_UPLOAD_TMP_PATH + media.getName());
                                        log.info("tmp_upload_file.exists()==>" + tmp_upload_file.exists());

                                        if (tmp_upload_file.exists()) {
                                            tmp_upload_file.delete();
                                        }

                                        fout = new FileOutputStream(tmp_upload_file);
                                        byte[] buffer = new byte[1024];
                                        int bytes_read;
                                        in = media.getStreamData();
                                        while ((bytes_read = in.read(buffer)) != -1) {
                                            fout.write(buffer, 0, bytes_read);
                                        }
                                        fout.flush();

                                        record_reall_count = getFileCount(new ShareParm().PARM_SYS_UPLOAD_TMP_PATH + "/" + media.getName());
                                        log.info("record_reall_count==>" + record_reall_count);
                                        log.info("filename_count    ==>" + filename_count);

                                        if (record_reall_count == filenamecount) {

                                            FileReader kk_fr;// = null;
                                            BufferedReader kk_bfr;// = null;
                                            kk_fr = new FileReader(tmp_upload_file);
                                            kk_bfr = new BufferedReader(kk_fr);
                                            boolean checkLength = true;
                                            String chkMsg = "";
                                            String kkstr;// = "";

                                            Messagebox.show("執行門號檢查，請按確定!" + chkMsg, new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.ERROR);
                                            this.fileupload.setDisabled(true);
                                            while ((kkstr = kk_bfr.readLine()) != null) {
                                                kkstr = kkstr.trim();
                                                if (kkstr.contains(",")) {
//                                                    record_reall_count++;
                                                    log.info("==>" + kkstr);
                                                    if (kkstr.length() != 25) {
                                                        checkLength = false;
                                                        chkMsg = chkMsg + kkstr + "\n";
                                                    } else {
                                                        StringTokenizer tmpstrtok = new StringTokenizer(kkstr, ",");
                                                        String v_Mdn = tmpstrtok.nextToken();
                                                        PPMdnUtil ppmdn = new PPMdnUtil(v_Mdn, true, true); //PPMdnUtil(String mdn, boolean aluflag, boolean zteflag)
                                                        String checkresult = ppmdn.checkPPMDN(epaybusinesscontroller);

                                                        if ("-1".equals(checkresult)) {
                                                            log.info(v_Mdn + "====>" + checkresult);
                                                            checkLength = false;
                                                            chkMsg = chkMsg + kkstr + "\n";
                                                        }
                                                    }
                                                }
                                            }
                                            this.fileupload.setDisabled(false);

                                            if (checkLength) {

                                                Messagebox.show("資料檢查完畢，準備批次儲值，請按確定" + chkMsg, new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.ERROR);
                                                this.fileupload.setDisabled(true);

                                                FileReader fr;// = null;
                                                BufferedReader bfr;// = null;

                                                String str;// = null;
                                                String mail_content = "行動電話門號,執行結果<br>";

                                                if (tmp_upload_file.exists()) {
                                                    fr = new FileReader(tmp_upload_file);
                                                    bfr = new BufferedReader(fr);

                                                    while ((str = bfr.readLine()) != null) {
                                                        str = str.trim();

                                                        String v_Mdn;// = null;
                                                        String v_PincodeNumber;// = null;

                                                        StringTokenizer stk_upload_process = new StringTokenizer(str, ",");
                                                        v_Mdn = stk_upload_process.nextToken();
                                                        v_PincodeNumber = stk_upload_process.nextToken();

                                                        boolean kkresult = false;

                                                        PinCodeUtil sendPincode = new PinCodeUtil();
                                                        kkresult = sendPincode.sendOrder(v_Mdn, v_PincodeNumber, filename);

                                                        log.info("v_mdn:" + v_Mdn + "," + "v_PincodeNumber:" + v_PincodeNumber + "," + kkresult);
                                                        if (kkresult == true) {
                                                            mail_content = mail_content + v_Mdn + ",執行成功<br>";
                                                            success_count = success_count + 1;
                                                        } else {
                                                            mail_content = mail_content + v_Mdn + ",執行失敗<br>";
                                                            fail_count = fail_count + 1;
                                                        }
                                                    }
                                                    mail_content = mail_content + "總筆數:" + record_reall_count + ",成功筆數:" + success_count + ",失敗筆數:" + fail_count;

                                                    String email_form = new ShareParm().PARM_MAIL_FROM;
                                                    String dst_user;
                                                    if (!"".equals(user_email)) {
                                                        dst_user = user_email + ";" + new ShareParm().PARM_MAIL_TO_4GOCS + ";" + new ShareParm().PARM_MAIL_TO_OC;
                                                    } else {
                                                        dst_user = new ShareParm().PARM_MAIL_TO_4GOCS + ";" + new ShareParm().PARM_MAIL_TO_OC;
                                                    }

                                                    String xmsg = media.getName() + "批次儲值結果,總筆數:" + record_reall_count + " 失敗筆數:" + fail_count;
                                                    log.info("Mail Title ===>" + xmsg);

                                                    MailUtil.sendMail(new ShareParm().PARM_MAIL_RELAY_HOST, email_form, dst_user, xmsg, mail_content);

                                                    tmp_check_file.delete();
                                                    this.fileupload.setDisabled(false);
                                                    Messagebox.show(media.getName() + "批次儲值完成!", new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.INFORMATION);

                                                }

                                            } else {
                                                tmp_check_file.delete();
                                                this.fileupload.setDisabled(false);
                                                Messagebox.show("該檔案資料格式或門號有錯誤!" + chkMsg, new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.ERROR);
                                            }

                                        } else {
                                            tmp_check_file.delete();
                                            this.fileupload.setDisabled(false);
                                            Messagebox.show("檔案筆數錯誤!", new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.INFORMATION);
                                        }

                                    } else {
                                        tmp_check_file.delete();
                                        this.fileupload.setDisabled(false);
                                        Messagebox.show("檔案筆數格式錯誤!", new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.INFORMATION);
                                    }
                                } else {
                                    tmp_check_file.delete();
                                    this.fileupload.setDisabled(false);
                                    Messagebox.show("檔名帳號欄位錯誤!", new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.INFORMATION);
                                }
                            } else {
                                tmp_check_file.delete();
                                Messagebox.show("該檔案名稱筆數格式錯誤!", new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.ERROR);
                            }
                        } else {
                            this.fileupload.setDisabled(false);
                            tmp_check_file.delete();
                            Messagebox.show("該檔案名稱重複!", new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.ERROR);
                        }
                    } else {
                        tmp_check_file.delete();
                        this.fileupload.setDisabled(false);
                        Messagebox.show("請上傳副檔名為CSV之檔案!", new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.ERROR);
                    }
                    tmp_check_file.delete();
                    this.fileupload.setDisabled(false);
                } catch (Exception ex) {
                    this.fileupload.setDisabled(false);
                    tmp_check_file.delete();
                    log.info(ex);
                } finally {
                    tmp_check_file.delete();
                    this.fileupload.setDisabled(false);
                }
            }
        } else {
            Messagebox.show("該帳號(" + user_code + "," + user_email + ")重複登入執行批次儲值!", new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.ERROR);
        }
    }

}
