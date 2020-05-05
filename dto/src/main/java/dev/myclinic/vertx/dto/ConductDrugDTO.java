package dev.myclinic.vertx.dto;

import dev.myclinic.vertx.dto.annotation.AutoInc;
import dev.myclinic.vertx.dto.annotation.Primary;

public class ConductDrugDTO {
	@Primary
	@AutoInc
	public int conductDrugId;
	public int conductId;
	public int iyakuhincode;
	public double amount;

	public static dev.myclinic.vertx.dto.ConductDrugDTO copy(dev.myclinic.vertx.dto.ConductDrugDTO src){
		dev.myclinic.vertx.dto.ConductDrugDTO dst = new dev.myclinic.vertx.dto.ConductDrugDTO();
		dst.conductDrugId = src.conductDrugId;
		dst.conductId = src.conductId;
		dst.iyakuhincode = src.iyakuhincode;
		dst.amount = src.amount;
		return dst;
	}
	
	@Override
	public String toString(){
		return "ConductDrugDTO[" +
			"conductDrugId=" + conductDrugId + ", " +
			"conductId=" + conductId + ", " +
			"iyakuhincode=" + iyakuhincode + ", " +
			"amount=" + amount + 
		"]";
	}
}