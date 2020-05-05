package dev.myclinic.vertx.dto;

public class VisitPatientDTO {
	public dev.myclinic.vertx.dto.VisitDTO visit;
	public PatientDTO patient;

	@Override
	public String toString() {
		return "VisitPatientDTO{" +
				"visit=" + visit +
				", patient=" + patient +
				'}';
	}

	public static dev.myclinic.vertx.dto.VisitPatientDTO create(dev.myclinic.vertx.dto.VisitDTO visit, PatientDTO patient){
		dev.myclinic.vertx.dto.VisitPatientDTO result = new dev.myclinic.vertx.dto.VisitPatientDTO();
		result.visit = visit;
		result.patient = patient;
		return result;
	}
}