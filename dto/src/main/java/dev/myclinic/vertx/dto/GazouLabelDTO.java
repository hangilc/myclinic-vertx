package dev.myclinic.vertx.dto;

import dev.myclinic.vertx.dto.annotation.Primary;

public class GazouLabelDTO {
	@Primary
	public int conductId;
	public String label;

	public static dev.myclinic.vertx.dto.GazouLabelDTO copy(dev.myclinic.vertx.dto.GazouLabelDTO src){
		dev.myclinic.vertx.dto.GazouLabelDTO dst = new dev.myclinic.vertx.dto.GazouLabelDTO();
		dst.conductId = src.conductId;
		dst.label = src.label;
		return dst;
	}

	@Override
	public String toString(){
		return "GazouLabelDTO[" +
			"conductId=" + conductId + "," +
			"label=" + label +
		"]";
	}
}