package dev.myclinic.vertx.dto;

import java.util.Objects;

public class WqueueFullDTO {
	public WqueueDTO wqueue;
	public VisitDTO visit;
	public PatientDTO patient;

	@Override
	public String toString() {
		return "WqueueFullDTO{" +
				"wqueue=" + wqueue +
				", visit=" + visit +
				", patient=" + patient +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		dev.myclinic.vertx.dto.WqueueFullDTO that = (dev.myclinic.vertx.dto.WqueueFullDTO) o;
		return Objects.equals(wqueue, that.wqueue) &&
				Objects.equals(visit, that.visit) &&
				Objects.equals(patient, that.patient);
	}

	@Override
	public int hashCode() {

		return Objects.hash(wqueue, visit, patient);
	}
}