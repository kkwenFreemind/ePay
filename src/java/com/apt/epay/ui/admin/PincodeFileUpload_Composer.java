/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.epay.ejb.bean.EPAY_VCARD;
import com.epay.ejb.bean.EPAY_VCARDTYPE;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

/**
 *
 * @author kevinchang
 */
@Scope("page")
public class PincodeFileUpload_Composer extends GenericForwardComposer implements Serializable {

//    MainCtrl mainCtr;
    private static final Logger log = Logger.getLogger("EPAY");
    Combobox combo_cardtype;
    Textbox text_cardtype;
    Textbox text_cardname;
    Textbox text_price;
    Textbox text_quantity;
    Textbox text_lowthreshold;
    Textbox text_status;

    private boolean fileFlag = true;

    private String[] ch_cardtype = new String[100];
    private String cardtype;
    private String filename_date = "";

    private String filename_cardtype = "";
    private String filename_price = "";
    private String filename_count = "";

    private int db_vcard_price;
    private String db_vcard_name;
    private String db_vcard_type;
    private int record_reall_count = 0;
    EPayBusinessConreoller epaybusinesscontroller;

    public void onMainCreate(Event event) throws Exception {
        log.info("@@@PincodeFileUpload_Composer");
        epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

        try {
            List cardtypeinfo = epaybusinesscontroller.listCardTypeInfo();
            Iterator itcardtypeinfo = cardtypeinfo.iterator();
            int j = 1;
            while (itcardtypeinfo.hasNext()) {

                EPAY_VCARDTYPE vcardtype = (EPAY_VCARDTYPE) itcardtypeinfo.next();

                Comboitem coitem_cardtypeid = new Comboitem();
                coitem_cardtypeid.setId("combo" + vcardtype.getCardtype());
                coitem_cardtypeid.setLabel(vcardtype.getCardtype() + " " + vcardtype.getCardname());
//                coitem_cardtypeid.setLabel(vcardtype.getCardtype());
                combo_cardtype.appendChild(coitem_cardtypeid);
                ch_cardtype[j] = String.valueOf(vcardtype.getCardtype());
                j++;
            }
            combo_cardtype.setSelectedIndex(0);
        } catch (Exception ex) {
            log.info(ex);
        }
    }

    public void onUpload$fileupload_pincode_file(UploadEvent event) throws Exception {

        record_reall_count = 0; // 筆數歸零
        log.info("onUpload$fileupload_pincode_file");
        Media media = event.getMedia();
        log.info("@@@media:" + media);
        if (media != null) {
            FileOutputStream fout = null;
            File tmp_upload_file = null;
            InputStream in = null;
            try {
                log.info(" ShareParm().PARM_SYS_UPLOAD_TMP_PATH==>" + new ShareParm().PARM_SYS_UPLOAD_TMP_PATH);
                
                tmp_upload_file = new File(new ShareParm().PARM_SYS_UPLOAD_TMP_PATH + media.getName());
                epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());

                //檢查是否之前已經有Load過
                fileFlag = epaybusinesscontroller.findVCardByFilename(media.getName());

                //check filename
                cardtype = ch_cardtype[combo_cardtype.getSelectedIndex()];

                EPAY_VCARDTYPE vcardtype = epaybusinesscontroller.queryCardTypeByCardType(cardtype);

                db_vcard_price = vcardtype.getPrice();
                db_vcard_name = vcardtype.getCardname();
                db_vcard_type = vcardtype.getCardtype();
                log.info("vcard info==>" + db_vcard_type + "," + db_vcard_name + "," + db_vcard_price);

                //YYYYMMDD-CARDTYPE-PRICE-RECORDS.CSV
                String filename = media.getName();
                String[] AfterSplit = (filename.split("\\.")[0]).split("-");
//                for (int i = 0; i < AfterSplit.length; i++) {
//                    log.info("(AfterSplit["+i+"])===>"+(AfterSplit[i]));
//                }
                filename_date = AfterSplit[0];
                filename_cardtype = AfterSplit[1];
                filename_price = AfterSplit[2];
                filename_count = AfterSplit[3];

                log.info("===>"+new ShareParm().PARM_SYS_UPLOAD_TMP_PATH + media.getName());
                if (tmp_upload_file.exists()) {
                    record_reall_count=0;
                    FileReader frx = null;
                    BufferedReader bfrx = null;

//                    String strx = null;
                    frx = new FileReader(tmp_upload_file);
                    bfrx = new BufferedReader(frx);

                    while (bfrx.readLine() != null) {
                        record_reall_count++;
                    }
                }

                fout = new FileOutputStream(tmp_upload_file);
                byte[] buffer = new byte[1024];
                //   media.getStreamData().read(buffer);
                int bytes_read;
                in = media.getStreamData();
                while ((bytes_read = in.read(buffer)) != -1) {
                    fout.write(buffer, 0, bytes_read);
                }
                //   fout.write(media.getByteData());
                fout.flush();

                if (fileFlag == true) {

                    if (tmp_upload_file != null) {
                        //get file recordcount

                        log.info("file real record count ==>" + record_reall_count);
                        log.info("filename info==>" + filename_cardtype + "," + filename_price + "," + filename_count);
                        if (!"".equals(filename_date) && !"".equals(filename_cardtype) && !"".equals(filename_price) && !"".equals(filename_count)) {
                            //  File tmp_unzip_path = new File(ShareParm.PARM_PROCESSING_TMP_UNZIP_PATH + System.currentTimeMillis() + "/");
                            if (record_reall_count == Integer.valueOf(filename_count)) {
                                if (db_vcard_price == Integer.valueOf(filename_price)) {
                                    if (db_vcard_type.equals(filename_cardtype)) {
                                        FileReader fr = null;
                                        BufferedReader bfr = null;
                                        try {

                                            if (tmp_upload_file.getName().matches(ShareParm.PARM_REGULAR_CSVFILE) || tmp_upload_file.getName().matches(ShareParm.PARM_REGULAR_TXTFILE)) {
                                                log.info("@@@FileUpload_Composer.onUpload$fileupload_file tmp_upload_file:" + tmp_upload_file.getName());
                                                String str = null;
                                                if (tmp_upload_file.exists()) {
                                                    fr = new FileReader(tmp_upload_file);
                                                    bfr = new BufferedReader(fr);

                                                    SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
                                                    Calendar nowDateTime = Calendar.getInstance();
                                                    String tradeDate = sdf.format(nowDateTime.getTime());

                                                    while ((str = bfr.readLine()) != null) {
                                                        try {
                                                            str = str.trim();

                                                            String vcno = null;
                                                            String vcpass = null;

                                                            StringTokenizer stk_upload_process = new StringTokenizer(str, ",");
                                                            vcno = stk_upload_process.nextToken();
                                                            vcpass = stk_upload_process.nextToken();

                                                            EPAY_VCARD vcard = new EPAY_VCARD();
                                                            vcard.setVcno(vcno);
                                                            vcard.setVcpass(vcpass);
                                                            vcard.setStatus("0");
                                                            vcard.setFilename(tmp_upload_file.getName());
                                                            vcard.setCreatedate(sdf.parse(tradeDate));
                                                            vcard.setCardtype(cardtype);
                                                            vcard.setMemo(filename_date);
                                                            epaybusinesscontroller.insertVcard(vcard);
                                                        } catch (Exception ex) {
                                                            ex.printStackTrace();
                                                            log.log(Level.INFO, " DenyUserBatchFileUpload_Composer.onUpload$fileupload_deny_user_file  has exception :" + ex.getMessage() + "!");
                                                        }
                                                    }

                                                    EPAY_VCARDTYPE vcardtypeX = epaybusinesscontroller.queryCardTypeByCardType(cardtype);
                                                    int old_qty = vcardtypeX.getQuantity();
                                                    int new_qty = old_qty + record_reall_count;
                                                    vcardtypeX.setQuantity(new_qty);
                                                    epaybusinesscontroller.updateVCardType(vcardtypeX);

                                                    Messagebox.show("檔案處理完成!", new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.INFORMATION);
                                                }
                                            } else {
                                                Messagebox.show("請上傳副檔名為CSV之檔案!", new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.ERROR);
                                            }
                                        } catch (Exception ex) {
                                        }
                                    } else {
                                        Messagebox.show("檔案上傳失敗，CardType不符!(內容:" + db_vcard_type + "/檔名:" + filename_cardtype + ")", new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.ERROR);
                                    }
                                } else {
                                    Messagebox.show("檔案上傳失敗，價格不符!(內容:" + db_vcard_price + "/檔名:" + filename_price + ")", new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.ERROR);
                                }
                            } else {
                                Messagebox.show("檔案上傳失敗，筆數不符!(內容:" + record_reall_count + "/檔名:" + filename_count + ")", new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.ERROR);
                            }
                        } else {
                            Messagebox.show("檔案格式錯誤", new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.ERROR);
                        }
                    } else {
                        Messagebox.show("檔案上傳失敗!", new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.ERROR);
                    }
                } else {
                    Messagebox.show("檔案重複上傳!", new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.ERROR);
                }
            } catch (Exception ex) {
                Messagebox.show("檔案格式錯誤", new ShareParm().PARM_I18N_SYS_NAME, Messagebox.OK, Messagebox.ERROR);
                log.info(ex);
            } finally {
                Executions.sendRedirect("/admin/UploadPincodeFile.zul");
            }
        }
    }

    //    public void onSelect$combo_cardtype() throws Exception {
//    @Listen("onChange = #combo_cardtype")
    public void onChange$combo_cardtype() throws Exception {

        String cardtypeid = ch_cardtype[combo_cardtype.getSelectedIndex()];

        log.info("cardtypeid===>" + cardtypeid);
        epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
        EPAY_VCARDTYPE vcardtype = epaybusinesscontroller.queryCardTypeByCardType(cardtypeid);

        log.info("===>" + vcardtype.getCardtype());
        text_cardtype.setValue(vcardtype.getCardtype());
        text_cardname.setValue(vcardtype.getCardname());
        text_price.setValue(String.valueOf(vcardtype.getPrice()));
        text_quantity.setValue(String.valueOf(vcardtype.getQuantity()));
        text_lowthreshold.setValue(String.valueOf(vcardtype.getLowthreshold()));
        text_status.setValue(vcardtype.getStatus());

    }

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        Events.postEvent("onMainCreate", comp, null);
    }

}
