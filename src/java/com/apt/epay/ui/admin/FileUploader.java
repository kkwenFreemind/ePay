/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.ui.admin;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Window;

/**
 *
 * @author kevinchang
 */
public class FileUploader extends GenericForwardComposer {

    private Window window;
    private Fileupload fileupload;

    @Override
    public void doAfterCompose(Component comp) {
        try {
            super.doAfterCompose(comp);
            this.window = (Window) comp.getFellow("window");
            this.fileupload = (Fileupload) comp.getFellow("fileupload");
            this.fileupload.setLabel("Upload");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onUpload$fileupload(UploadEvent event){
        System.out.println("onUpload event");
        this.fileupload.setDisabled(true);
        this.fileupload.setLabel("Upload_AA");
    }

}
