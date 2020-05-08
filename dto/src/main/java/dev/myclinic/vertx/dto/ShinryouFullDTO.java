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

	public static ShinryouFullDTO create(ShinryouDTO shinryou, ShinryouMasterDTO master){
		ShinryouFullDTO result = new ShinryouFullDTO();
		result.shinryou = shinryou;
		result.master = master;
		return result;
	}
}