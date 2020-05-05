package dev.myclinic.vertx.dto;

/**
 * Created by hangil on 2017/05/20.
 */
public class PaymentVisitPatientDTO {
    public dev.myclinic.vertx.dto.PaymentDTO payment;
    public dev.myclinic.vertx.dto.VisitDTO visit;
    public PatientDTO patient;

    @Override
    public String toString() {
        return "PaymentVisitPatientDTO{" +
                "payment=" + payment +
                ", visit=" + visit +
                ", patient=" + patient +
                '}';
    }
}
