package dev.myclinic.vertx.cli.covidvaccine.patientevent;

public interface PatientEvent {
    String encode();
    PatientEvent copy();
}
