package dev.myclinic.vertx.dto;

public class VisitChargePatientDTO {

    public VisitDTO visit;
    public ChargeDTO charge;
    public PatientDTO patient;

    @Override
    public String toString() {
        return "VisitChargePatientDTO{" +
                "visit=" + visit +
                ", charge=" + charge +
                ", patient=" + patient +
                '}';
    }
}
