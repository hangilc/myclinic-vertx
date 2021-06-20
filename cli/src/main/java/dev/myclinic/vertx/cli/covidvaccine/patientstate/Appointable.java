package dev.myclinic.vertx.cli.covidvaccine.patientstate;

import java.time.LocalDateTime;

public interface Appointable {
    PatientState registerAppoint(LocalDateTime at);
}
