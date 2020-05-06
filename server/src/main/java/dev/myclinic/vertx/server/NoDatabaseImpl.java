package dev.myclinic.vertx.server;

import dev.myclinic.vertx.appconfig.AppConfig;
import dev.myclinic.vertx.dto.*;

import java.util.List;

class NoDatabaseImpl {

    private final AppConfig appConfig;

    NoDatabaseImpl(AppConfig appConfig){
        this.appConfig = appConfig;
    }

    public StringResultDTO getMasterMapConfigFilePath() throws Exception {
        throw new RuntimeException("Not implemented: getMasterMapConfigFilePath");
    }

    public StringResultDTO getShinryouByoumeiMapConfigFilePath() throws Exception {
        throw new RuntimeException("Not implemented: getShinryouByoumeiMapConfigFilePath");
    }

    public byte[] getHokensho(int patientId, String file) throws Exception {
        throw new RuntimeException("getClinicInfo");
    }

    public List<ReferItemDTO> getReferList() throws Exception {
        throw new RuntimeException("getReferList");
    }

    public StringResultDTO getNameMapConfigFilePath() throws Exception {
        throw new RuntimeException("Not implemented: getNameMapConfigFilePath");
    }

    public StringResultDTO getPowderDrugConfigFilePath() throws Exception {
        throw new RuntimeException("Not implemented: getPowderDrugConfigFilePath");
    }

    public PracticeConfigDTO getPracticeConfig() throws Exception {
        throw new RuntimeException("getPracticeConfig");
    }

}
