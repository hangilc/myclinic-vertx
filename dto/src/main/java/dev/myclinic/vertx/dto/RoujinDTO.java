package dev.myclinic.vertx.dto;

import dev.myclinic.vertx.dto.annotation.AutoInc;
import dev.myclinic.vertx.dto.annotation.Primary;

public class RoujinDTO {
	@Primary
	@AutoInc
	public int roujinId;
	public int patientId;
	public int shichouson;
	public int jukyuusha;
	public int futanWari;
	public String validFrom;
	public String validUpto;

	public dev.myclinic.vertx.dto.RoujinDTO copy(){
		dev.myclinic.vertx.dto.RoujinDTO dst = new dev.myclinic.vertx.dto.RoujinDTO();
		dst.roujinId = roujinId;
		dst.patientId = patientId;
		dst.shichouson = shichouson;
		dst.jukyuusha = jukyuusha;
		dst.futanWari = futanWari;
		dst.validFrom = validFrom;
		dst.validUpto = validUpto;
		return dst;
	}

	public void assign(dev.myclinic.vertx.dto.RoujinDTO src){
		roujinId = src.roujinId;
		patientId = src.patientId;
		shichouson = src.shichouson;
		jukyuusha = src.jukyuusha;
		futanWari = src.futanWari;
		validFrom = src.validFrom;
		validUpto = src.validUpto;
	}

	@Override
	public String toString(){
		return "RoujinDTO[" +
			"roujinId=" + roujinId + "," +
			"patientId=" + patientId + "," +
			"shichouson=" + shichouson + "," +
			"jukyuusha=" + jukyuusha + "," +
			"futanWari=" + futanWari + "," +
			"validFrom=" + validFrom + "," +
			"validUpto=" + validUpto +
		"]";
	}
}