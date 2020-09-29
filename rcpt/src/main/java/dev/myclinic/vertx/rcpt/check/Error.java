package dev.myclinic.vertx.rcpt.check;

import dev.myclinic.vertx.dto.PatientDTO;

public class Error {

    private PatientDTO patient;
    private String message;
    private String fixMessage;
    private Runnable fixFun;

    Error(PatientDTO patient, String message, String fixMessage, Runnable fixFun) {
        this.patient = patient;
        this.message = message;
        this.fixMessage = fixMessage;
        this.fixFun = fixFun;
    }

    public PatientDTO getPatient() {
        return patient;
    }

    public String getMessage() {
        return message;
    }

    public String getFixMessage() {
        return fixMessage;
    }

    public Runnable getFixFun() {
        return fixFun;
    }
}
