package dev.myclinic.vertx.dto;

public class ConductDrugFullDTO {
    public dev.myclinic.vertx.dto.ConductDrugDTO conductDrug;
    public IyakuhinMasterDTO master;

    public static dev.myclinic.vertx.dto.ConductDrugFullDTO copy(dev.myclinic.vertx.dto.ConductDrugFullDTO src) {
        dev.myclinic.vertx.dto.ConductDrugFullDTO dst = new dev.myclinic.vertx.dto.ConductDrugFullDTO();
        dst.conductDrug = dev.myclinic.vertx.dto.ConductDrugDTO.copy(src.conductDrug);
        dst.master = src.master;
        return dst;
    }


    public static dev.myclinic.vertx.dto.ConductDrugFullDTO create(dev.myclinic.vertx.dto.ConductDrugDTO conductDrug,
                                                             IyakuhinMasterDTO master) {
        dev.myclinic.vertx.dto.ConductDrugFullDTO result = new dev.myclinic.vertx.dto.ConductDrugFullDTO();
        result.conductDrug = conductDrug;
        result.master = master;
        return result;
    }

    @Override
    public String toString() {
        return "ConductDrugFullDTO{" +
                "conductDrug=" + conductDrug +
                ", master=" + master +
                '}';
    }
}