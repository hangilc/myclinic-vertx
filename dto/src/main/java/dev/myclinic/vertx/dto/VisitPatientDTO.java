package dev.myclinic.vertx.dto;

public class VisitPatientDTO {
	public VisitDTO visit;
	public PatientDTO patient;

	public VisitPatientDTO(){

	}

	public VisitPatientDTO(VisitDTO visit, PatientDTO patient) {
		this.visit = visit;
		this.patient = patient;
	}

	@Override
	public String toString() {
		return "VisitPatientDTO{" +
				"visit=" + visit +
				", patient=" + patient +
				'}';
	}

	public static VisitPatientDTO create(dev.myclinic.vertx.dto.VisitDTO visit, PatientDTO patient){
		dev.myclinic.vertx.dto.VisitPatientDTO result = new dev.myclinic.vertx.dto.VisitPatientDTO();
		result.visit = visit;
		result.patient = patient;
		return result;
	}
}