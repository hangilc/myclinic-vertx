package dev.myclinic.vertx.appconfig;

import dev.myclinic.vertx.dto.ClinicInfoDTO;
import dev.myclinic.vertx.dto.DiseaseExampleDTO;
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
}
