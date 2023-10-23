/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.util.MailUtil;
import com.apt.util.PPMdnUtil;
import com.apt.util.PinCodeUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
@Scope("page")
public class ChannelPincodeFileUpload_Composer1 extends GenericForwardComposer implements Serializable {

    private static final Logger log = Logger.getLogger("EPAY");
    int record_reall_count = 0;

//    private String filename_date;
//    private String filename_cardtype;
    private String filename_count;
//    private Window window;
//    private Fileupload fileupload;

    public void onMainCreate(Event event) throws Exception {
        log.info("onMainCreate");
    }

    public void onUpload$fileupload_pincode_file(UploadEvent event) {

//        this.fileupload = (Fileupload) this.doAfterCompose(comp).getFellow("fileupload_pincode_file");
//        this.fileupload.setDisabled(true);

        String user_code = (String) Sessions.getCurrent().getAttribute("user_code");
        String user_email = (String) Sessions.getCurrent().getAttribute("email");
        log.info("IP:" + Executions.getCurrent().getRemoteAddr() + " Account:" + user_code + "--> batch upload pincod file for EPAY");
        log.info(user_code + "'s Email:" + user_email);

        File tmp_check_file;
        tmp_check_file = new File(new ShareParm().PARM_SYS_UPLOAD_TMP_PATH + user_code + ".txt");

        if (!tmp_check_file.exists()) {

            int success_count = 0;
            int fail_count = 0;

            EPayBusinessConreoller epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

            record_reall_count = 0; // 筆數歸零
            Media media = event.getMedia();

            if (media != null) {
                FileOutputStream fout;// = null;
                File tmp_upload_file;// = null;
                InputStream in;//= null;
                try {

                    tmp_check_file.createNewFile();

                    tmp_upload_file = new File(new ShareParm().PARM_SYS_UPLOAD_TMP_PATH + media.getName());
                    log.info("tmp_upload_file==>" + new ShareParm().PARM_SYS_UPLOAD_TMP_PATH + "/" + media.getName());

                    //檢查是否之前已經有Load過
                    String filename = media.getName().toLowerCase();
                    List list = epaybusinesscontroller.getTxLogListByBatchFie(filename);
                    if (list.isEmpty()) {

                        String[] AfterSplit = (filename.split("\\.")[0]).split("_");
                        log.info("AfterSplit.lenght===>" + AfterSplit.length);

                        if (AfterSplit.length == 6) {
                            filename_count = AfterSplit[5];
                            try {
                                int filenamecount = Integer.valueOf(filename_count);

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

                                FileReader kk_fr;// = null;
                                BufferedReader kk_bfr;// = null;
                                kk_fr = new FileReader(tmp_upload_file);
                                kk_bfr = new BufferedReader(kk_fr);
                                boolean checkLength = true;
                                String chkMsg = "";
                                String kkstr = "";

                                while ((kkstr = kk_bfr.readLine()) != null) {
                                    kkstr = kkstr.trim();
                                    record_reall_count++;
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

                                if (checkLength) {

                                    FileReader fr;// = null;
                                    BufferedReader bfr;// = null;

                                    try {
                                        if (tmp_upload_file.getName().matches(ShareParm.PARM_REGULAR_CSVFILE) || tmp_upload_file.getName().matches(ShareParm.PARM_REGULAR_TXTFILE)) {

                                            String str;// = null;
                                            String mail_content = "行動電話門號,執行結果<br>";

                                            if (tmp_upload_file.exists()) {
                                                fr = new FileReader(tmp_upload_file);
                                                bfr = new BufferedReader(fr);

                                                log.info("record_reall_count==>" + record_reall_count);
                                                log.info("filename_count    ==>" + filename_count);

                                                if (record_reall_count == filenamecount) {
                                                    while ((str = bfr.readLine()) != null) {
                                                        str = str.trim();

                                                        String v_Mdn;// = null;
                                                        String v_PincodeNumber;// = null;

                                                        StringTokenizer stk_upload_process = new StringTokenizer(str, ",");
                                                        v_Mdn = stk_upload_process.nextToken();
                                                        v_PincodeNumber = stk_upload_process.nextToken();

                                                        PinCodeUtil sendPincode = new PinCodeUtil();
                                                        boolean kkresult = sendPincode.sendOrder(v_Mdn, v_PincodeNumber, filename);
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
                                                    String dst_user = user_email + ";" + new ShareParm().PARM_MAIL_TO_4GOCS + ";" + new ShareParm().PARM_MAIL_TO_OC;
                                                    String xmsg = media.getName() + "批次儲值結果";
                                                    try {
                                                        MailUtil.sendMail(new ShareParm().PARM_MAIL_RELAY_HOST, email_form, dst_user, xmsg, mail_content);
                                                    } catch (Exception ex) {
                                                        log.info(ex);
                                                    }
                                                    tmp_check_file.delete();
                                                    Messagebox.show(media.getName() + "檔案處理完成!", new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.INFORMATION);
                                                } else {
                                                    tmp_check_file.delete();
                                                    Messagebox.show("檔案筆數與實際不符!", new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.INFORMATION);
                                                }
                                            }
                                        } else {
                                            tmp_check_file.delete();
                                            Messagebox.show("請上傳副檔名為CSV之檔案!", new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.ERROR);
                                        }

                                    } catch (Exception ex) {
                                        log.info(ex);
                                    }
                                } else {
                                    tmp_check_file.delete();
                                    Messagebox.show("該檔案資料格式或門號錯誤!" + chkMsg, new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.ERROR);
                                }
                            } catch (IOException ex) {
                                log.info(ex);
                                tmp_check_file.delete();
                                Messagebox.show("IO錯誤!", new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.ERROR);

                            } catch (NumberFormatException ex) {
                                log.info(ex);
                                tmp_check_file.delete();
                                Messagebox.show("錯誤!", new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.ERROR);
                            }
                        } else {
                            tmp_check_file.delete();
                            Messagebox.show("該檔案名稱筆數格式錯誤!", new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.ERROR);

                        }
                    } else {

                        tmp_check_file.delete();
                        Messagebox.show("該檔案名稱重複!", new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.ERROR);
                    }
                    tmp_check_file.delete();
                } catch (Exception ex) {
//                    fileupload.setDisabled(false);
                    tmp_check_file.delete();
                    log.info(ex);
                } finally {
//                    fileupload.setDisabled(false);
                }
            }
        } else {
            Messagebox.show(user_code + "該帳號重複執行批次儲值!", new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.ERROR);
        }
    }

    @Override
    public void doAfterCompose(Component comp) throws Exception {
//

        try {
            super.doAfterCompose(comp);
//            this.window = (Window) comp.getFellow("UploadPincodeFile_Window");
//            this.fileupload = (Fileupload) comp.getFellow("fileupload_pincode_file");
            Events.postEvent("onMainCreate", comp, null);
        } catch (Exception ex) {
            log.info(ex);
        }
    }
}
