package dev.myclinic.vertx.dto;

import dev.myclinic.vertx.dto.annotation.AutoInc;
import dev.myclinic.vertx.dto.annotation.Primary;

public class KoukikoureiDTO {
	@Primary
	@AutoInc
	public int koukikoureiId;
	public int patientId;
	public String hokenshaBangou;
	public String hihokenshaBangou;
	public int futanWari;
	public String validFrom;
	public String validUpto;

	public dev.myclinic.vertx.dto.KoukikoureiDTO copy(){
		dev.myclinic.vertx.dto.KoukikoureiDTO dst = new dev.myclinic.vertx.dto.KoukikoureiDTO();
		dst.koukikoureiId = koukikoureiId;
		dst.patientId = patientId;
		dst.hokenshaBangou = hokenshaBangou;
		dst.hihokenshaBangou = hihokenshaBangou;
		dst.futanWari = futanWari;
		dst.validFrom = validFrom;
		dst.validUpto = validUpto;
		return dst;
	}

	public void assign(dev.myclinic.vertx.dto.KoukikoureiDTO src){
		koukikoureiId = src.koukikoureiId;
		patientId = src.patientId;
		hokenshaBangou = src.hokenshaBangou;
		hihokenshaBangou = src.hihokenshaBangou;
		futanWari = src.futanWari;
		validFrom = src.validFrom;
		validUpto = src.validUpto;
	}

	@Override
	public String toString(){
		return "KoukikoureiDTO[" +
			"koukikoureiId=" + koukikoureiId + "," +
			"patientId=" + patientId + "," +
			"hokenshaBangou=" + hokenshaBangou + "," +
			"hihokenshaBangou=" + hihokenshaBangou + "," +
			"futanWari=" + futanWari + "," +
			"validFrom=" + validFrom + "," +
			"validUpto=" + validUpto +
		"]";
	}
}