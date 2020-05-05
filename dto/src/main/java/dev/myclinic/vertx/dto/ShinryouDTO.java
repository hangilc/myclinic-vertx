package dev.myclinic.vertx.dto;

import dev.myclinic.vertx.dto.annotation.AutoInc;
import dev.myclinic.vertx.dto.annotation.Primary;

import java.util.Objects;

public class ShinryouDTO {
	@Primary
	@AutoInc
	public int shinryouId;
	public int visitId;
	public int shinryoucode;

	public static dev.myclinic.vertx.dto.ShinryouDTO copy(dev.myclinic.vertx.dto.ShinryouDTO src){
		dev.myclinic.vertx.dto.ShinryouDTO dst = new dev.myclinic.vertx.dto.ShinryouDTO();
		dst.shinryouId = src.shinryouId;
		dst.visitId = src.visitId;
		dst.shinryoucode = src.shinryoucode;
		return dst;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		dev.myclinic.vertx.dto.ShinryouDTO that = (dev.myclinic.vertx.dto.ShinryouDTO) o;
		return shinryouId == that.shinryouId &&
				visitId == that.visitId &&
				shinryoucode == that.shinryoucode;
	}

	@Override
	public int hashCode() {

		return Objects.hash(shinryouId, visitId, shinryoucode);
	}

	@Override
	public String toString() {
		return "ShinryouDTO{" +
				"shinryouId=" + shinryouId +
				", visitId=" + visitId +
				", shinryoucode=" + shinryoucode +
				'}';
	}
}