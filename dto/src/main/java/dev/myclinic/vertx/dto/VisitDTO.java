package dev.myclinic.vertx.dto;

import dev.myclinic.vertx.dto.annotation.AutoInc;
import dev.myclinic.vertx.dto.annotation.Primary;

import java.util.Objects;

public class VisitDTO {
	@Primary
	@AutoInc
	public int visitId;
	public int patientId;
	public String visitedAt;
	public int shahokokuhoId;
	public int koukikoureiId;
	public int roujinId;
	public int kouhi1Id;
	public int kouhi2Id;
	public int kouhi3Id;
	public String attributes;

	public static VisitDTO copy(VisitDTO src){
		VisitDTO dst = new VisitDTO();
		dst.visitId = src.visitId;
		dst.patientId = src.patientId;
		dst.visitedAt = src.visitedAt;
		dst.shahokokuhoId = src.shahokokuhoId;
		dst.koukikoureiId = src.koukikoureiId;
		dst.roujinId = src.roujinId;
		dst.kouhi1Id = src.kouhi1Id;
		dst.kouhi2Id = src.kouhi2Id;
		dst.kouhi3Id = src.kouhi3Id;
		dst.attributes = src.attributes;
		return dst;
	}

	@Override
	public String toString() {
		return "VisitDTO{" +
				"visitId=" + visitId +
				", patientId=" + patientId +
				", visitedAt='" + visitedAt + '\'' +
				", shahokokuhoId=" + shahokokuhoId +
				", koukikoureiId=" + koukikoureiId +
				", roujinId=" + roujinId +
				", kouhi1Id=" + kouhi1Id +
				", kouhi2Id=" + kouhi2Id +
				", kouhi3Id=" + kouhi3Id +
				", attributes=" + attributes +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VisitDTO visitDTO = (VisitDTO) o;
		return visitId == visitDTO.visitId &&
				patientId == visitDTO.patientId &&
				shahokokuhoId == visitDTO.shahokokuhoId &&
				koukikoureiId == visitDTO.koukikoureiId &&
				roujinId == visitDTO.roujinId &&
				kouhi1Id == visitDTO.kouhi1Id &&
				kouhi2Id == visitDTO.kouhi2Id &&
				kouhi3Id == visitDTO.kouhi3Id &&
				Objects.equals(attributes, visitDTO.attributes) &&
				Objects.equals(visitedAt, visitDTO.visitedAt);
	}

	@Override
	public int hashCode() {

		return Objects.hash(visitId, patientId, visitedAt, shahokokuhoId, koukikoureiId, roujinId, kouhi1Id, kouhi2Id,
				kouhi3Id, attributes);
	}
}