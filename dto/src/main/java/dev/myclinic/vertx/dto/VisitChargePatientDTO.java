package dev.myclinic.vertx.dto;

public class VisitChargePatientDTO {

    public VisitDTO visit;
    public ChargeDTO charge;
    public PatientDTO patient;

    public VisitChargePatientDTO() {
    }

    public VisitChargePatientDTO(VisitDTO visit, ChargeDTO charge, PatientDTO patient) {
        this.visit = visit;
        this.charge = charge;
        this.patient = patient;
    }

    @Override
    public String toString() {
        return "VisitChargePatientDTO{" +
                "visit=" + visit +
                ", charge=" + charge +
                ", patient=" + patient +
                '}';
    }
}
