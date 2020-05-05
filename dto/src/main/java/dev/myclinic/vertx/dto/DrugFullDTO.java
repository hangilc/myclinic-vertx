package dev.myclinic.vertx.dto;

public class DrugFullDTO {
	public DrugDTO drug;
	public IyakuhinMasterDTO master;

	public static dev.myclinic.vertx.dto.DrugFullDTO copy(dev.myclinic.vertx.dto.DrugFullDTO src){
		dev.myclinic.vertx.dto.DrugFullDTO dst = new dev.myclinic.vertx.dto.DrugFullDTO();
		dst.drug = src.drug;
		dst.master = src.master;
		return dst;
	}

	public static dev.myclinic.vertx.dto.DrugFullDTO create(DrugDTO drug, IyakuhinMasterDTO master){
		dev.myclinic.vertx.dto.DrugFullDTO result = new dev.myclinic.vertx.dto.DrugFullDTO();
		result.drug = drug;
		result.master = master;
		return result;
	}

	@Override
	public String toString() {
		return "DrugFullDTO{" +
				"drug=" + drug +
				", master=" + master +
				'}';
	}
}