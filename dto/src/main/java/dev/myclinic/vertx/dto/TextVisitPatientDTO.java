package dev.myclinic.vertx.dto;

public class TextVisitPatientDTO {
    public dev.myclinic.vertx.dto.TextDTO text;
    public dev.myclinic.vertx.dto.VisitDTO visit;
    public dev.myclinic.vertx.dto.PatientDTO patient;

    @Override
    public String toString() {
        return "TextVisitPatientDTO{" +
                "text=" + text +
                ", visit=" + visit +
                ", patient=" + patient +
                '}';
    }
}
