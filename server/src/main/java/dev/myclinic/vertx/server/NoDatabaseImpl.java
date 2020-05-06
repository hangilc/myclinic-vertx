package dev.myclinic.vertx.server;

import dev.myclinic.vertx.appconfig.AppConfig;
import dev.myclinic.vertx.dto.*;

import java.util.List;

class NoDatabaseImpl {

    private AppConfig appConfig;

    NoDatabaseImpl(AppConfig appConfig){
        this.appConfig = appConfig;
    }

    public List<DiseaseExampleDTO> listDiseaseExample() throws Exception {
        throw new RuntimeException("Not implemented: listDiseaseExample");
    }

    public List<String> listHokensho(int patientId) throws Exception {
        throw new RuntimeException("Not implemented: listHokensho");
    }

    public ClinicInfoDTO getClinicInfo() throws Exception {
        throw new RuntimeException("getClinicInfo");
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
