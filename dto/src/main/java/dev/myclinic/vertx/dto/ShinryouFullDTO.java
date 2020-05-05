package dev.myclinic.vertx.dto;

public class ShinryouFullDTO {
	public ShinryouDTO shinryou;
	public ShinryouMasterDTO master;

	@Override
	public String toString() {
		return "ShinryouFullDTO{" +
				"shinryou=" + shinryou +
				", master=" + master +
				'}';
	}

	public static dev.myclinic.vertx.dto.ShinryouFullDTO create(ShinryouDTO shinryou, ShinryouMasterDTO master){
		dev.myclinic.vertx.dto.ShinryouFullDTO result = new dev.myclinic.vertx.dto.ShinryouFullDTO();
		result.shinryou = shinryou;
		result.master = master;
		return result;
	}
}