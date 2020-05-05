package dev.myclinic.vertx.dto;

import dev.myclinic.vertx.dto.annotation.Primary;

import java.util.Objects;

public class WqueueDTO {
	@Primary
	public int visitId;
	public int waitState;

	public static dev.myclinic.vertx.dto.WqueueDTO copy(dev.myclinic.vertx.dto.WqueueDTO src){
		dev.myclinic.vertx.dto.WqueueDTO dst = new dev.myclinic.vertx.dto.WqueueDTO();
		dst.visitId = src.visitId;
		dst.waitState = src.waitState;
		return dst;
	}

	@Override
	public String toString() {
		return "WqueueDTO{" +
				"visitId=" + visitId +
				", waitState=" + waitState +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		dev.myclinic.vertx.dto.WqueueDTO wqueueDTO = (dev.myclinic.vertx.dto.WqueueDTO) o;
		return visitId == wqueueDTO.visitId &&
				waitState == wqueueDTO.waitState;
	}

	@Override
	public int hashCode() {

		return Objects.hash(visitId, waitState);
	}
}