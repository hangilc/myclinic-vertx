package dev.myclinic.vertx.dto;

import dev.myclinic.vertx.dto.annotation.AutoInc;
import dev.myclinic.vertx.dto.annotation.Primary;

public class ConductKizaiDTO {
	@Primary
	@AutoInc
	public int conductKizaiId;
	public int conductId;
	public int kizaicode;
	public double amount;

	public static dev.myclinic.vertx.dto.ConductKizaiDTO copy(dev.myclinic.vertx.dto.ConductKizaiDTO src){
		dev.myclinic.vertx.dto.ConductKizaiDTO dst = new dev.myclinic.vertx.dto.ConductKizaiDTO();
		dst.conductKizaiId = src.conductKizaiId;
		dst.conductId = src.conductId;
		dst.kizaicode = src.kizaicode;
		dst.amount = src.amount;
		return dst;
	}
	
	@Override
	public String toString(){
		return "ConductKizaiDTO[" +
			"conductKizaiId=" + conductKizaiId + ", " +
			"conductId=" + conductId + ", " +
			"kizaicode=" + kizaicode + ", " +
			"amount=" + amount + 
		"]";
	}
}