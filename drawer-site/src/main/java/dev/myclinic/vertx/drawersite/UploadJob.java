package dev.myclinic.vertx.drawersite;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class UploadJob {

    public static class UploadFile {
        public String scannedFileName;
        public String uploadFileName;

        @JsonCreator()
        public UploadFile(@JsonProperty("scannedFileName") String scannedFileName,
                          @JsonProperty("uploadFileName") String uploadFileName) {
            this.scannedFileName = scannedFileName;
            this.uploadFileName = uploadFileName;
        }
    }

    public int patientId;
    public List<UploadFile> uploadFiles;

    @JsonCreator()
    public UploadJob(){

    }

    public UploadJob(int patientId){
        this.patientId = patientId;
        this.uploadFiles = new ArrayList<>();
    }

    public void addFile(String scannedFileName, String uploadFileName){
        UploadFile uf = new UploadFile(scannedFileName, uploadFileName);
        this.uploadFiles.add(uf);
    }
}
