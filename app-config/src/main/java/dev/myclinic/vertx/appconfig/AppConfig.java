package dev.myclinic.vertx.appconfig;

import dev.myclinic.vertx.appconfig.types.ShohousenGrayStampInfo;
import dev.myclinic.vertx.appconfig.types.StampInfo;
import dev.myclinic.vertx.houkatsukensa.HoukatsuKensa;
import dev.myclinic.vertx.mastermap.MasterMap;
import dev.myclinic.vertx.dto.ClinicInfoDTO;
import dev.myclinic.vertx.dto.DiseaseExampleDTO;
import dev.myclinic.vertx.dto.PracticeConfigDTO;
import dev.myclinic.vertx.dto.ReferItemDTO;
import io.vertx.core.Future;

import java.util.List;

public interface AppConfig {
    Future<ClinicInfoDTO> getClinicInfo();
    Future<List<DiseaseExampleDTO>> listDiseaseExample();
    Future<String> getPaperScanDirectory();
    Future<String> getMasterMapConfigFilePath();
    Future<String> getShinryouByoumeiMapConfigFilePath();
    Future<List<ReferItemDTO>> getReferList();
    Future<String> getNameMapConfigFilePath();
    Future<String> getPowderDrugConfigFilePath();
    Future<PracticeConfigDTO> getPracticeConfig();
    MasterMap getMasterMap();
    HoukatsuKensa getHoukatsuKensa();
    ShohousenGrayStampInfo getShohousenGrayStampInfo();
    StampInfo getReferStampInfo(); // deprecated
    StampInfo getStampInfo(String name);
}
