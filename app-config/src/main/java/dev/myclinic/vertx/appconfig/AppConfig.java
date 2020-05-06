package dev.myclinic.vertx.appconfig;

import dev.myclinic.vertx.dto.ClinicInfoDTO;
import io.vertx.core.Future;

public interface AppConfig {
    Future<ClinicInfoDTO> getClinicInfo();
}
