package dev.myclinic.vertx.cli.covidvaccine.patientevent;

import java.time.LocalDateTime;

public interface Appointable {
    PatientEvent registerAppoint(LocalDateTime at);
}
