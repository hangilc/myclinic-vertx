package dev.myclinic.vertx.dto;

public class VisitFull2PatientDTO {

    public dev.myclinic.vertx.dto.VisitFull2DTO visitFull;
    public dev.myclinic.vertx.dto.PatientDTO patient;

    @Override
    public String toString() {
        return "VisitFull2PatientDTO{" +
                "visitFull=" + visitFull +
                ", patient=" + patient +
                '}';
    }
}
