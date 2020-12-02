package dev.myclinic.vertx.drawersite;

import java.util.ArrayList;
import java.util.List;

public class UploadJob {

    public static class UploadFile {
        public String scannedFileName;
        public String uploadFileName;

        public UploadFile(String scannedFileName, String uploadFileName) {
            this.scannedFileName = scannedFileName;
            this.uploadFileName = uploadFileName;
        }
    }

    public final int patientId;
    public final List<UploadFile> uploadFiles;

    public UploadJob(int patientId){
        this.patientId = patientId;
        this.uploadFiles = new ArrayList<>();
    }

    public void addFile(String scannedFileName, String uploadFileName){
        UploadFile uf = new UploadFile(scannedFileName, uploadFileName);
        this.uploadFiles.add(uf);
    }
}
