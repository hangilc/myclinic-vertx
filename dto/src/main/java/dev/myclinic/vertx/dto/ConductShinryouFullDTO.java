package dev.myclinic.vertx.dto;

public class ConductShinryouFullDTO {
	public ConductShinryouDTO conductShinryou;
	public ShinryouMasterDTO master;

	public static dev.myclinic.vertx.dto.ConductShinryouFullDTO copy(dev.myclinic.vertx.dto.ConductShinryouFullDTO src){
		dev.myclinic.vertx.dto.ConductShinryouFullDTO dst = new dev.myclinic.vertx.dto.ConductShinryouFullDTO();
		dst.conductShinryou = ConductShinryouDTO.copy(src.conductShinryou);
		dst.master = src.master; // master is considered to be immutable
		return dst;
	}

	public static dev.myclinic.vertx.dto.ConductShinryouFullDTO create(ConductShinryouDTO conductShinryou,
                                                                 ShinryouMasterDTO master){
		dev.myclinic.vertx.dto.ConductShinryouFullDTO result = new dev.myclinic.vertx.dto.ConductShinryouFullDTO();
		result.conductShinryou = conductShinryou;
		result.master = master;
		return result;
	}

	@Override
	public String toString() {
		return "ConductShinryouFullDTO{" +
				"conductShinryou=" + conductShinryou +
				", master=" + master +
				'}';
	}
}