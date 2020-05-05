package dev.myclinic.vertx.dto;

import dev.myclinic.vertx.dto.annotation.Primary;

public class ChargeDTO {
	@Primary
	public int visitId;
	public int charge;

	public static dev.myclinic.vertx.dto.ChargeDTO create(int visitId, int charge){
		dev.myclinic.vertx.dto.ChargeDTO dst = new dev.myclinic.vertx.dto.ChargeDTO();
		dst.visitId = visitId;
		dst.charge = charge;
		return dst;
	}

	public static dev.myclinic.vertx.dto.ChargeDTO copy(dev.myclinic.vertx.dto.ChargeDTO src){
		dev.myclinic.vertx.dto.ChargeDTO dst = new dev.myclinic.vertx.dto.ChargeDTO();
		dst.visitId = src.visitId;
		dst.charge = src.charge;
		return dst;
	}

	@Override
	public String toString() {
		return "ChargeDTO{" +
				"visitId=" + visitId +
				", charge=" + charge +
				'}';
	}
}