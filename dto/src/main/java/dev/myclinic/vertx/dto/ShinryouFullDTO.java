package dev.myclinic.vertx.dto;

public class ShinryouFullDTO {
	public ShinryouDTO shinryou;
	public ShinryouMasterDTO master;
	public ShinryouAttrDTO attr;

	@Override
	public String toString() {
		return "ShinryouFullDTO{" +
				"shinryou=" + shinryou +
				", master=" + master +
				", attr=" + attr +
				'}';
	}

	public static ShinryouFullDTO create(ShinryouDTO shinryou, ShinryouMasterDTO master){
		return createWithAttr(shinryou, master, null);
	}

	public static ShinryouFullDTO createWithAttr(ShinryouDTO shinryou, ShinryouMasterDTO master,
												 ShinryouAttrDTO attr){
		ShinryouFullDTO result = new ShinryouFullDTO();
		result.shinryou = shinryou;
		result.master = master;
		result.attr = attr;
		return result;
	}

}