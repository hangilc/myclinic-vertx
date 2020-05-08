package dev.myclinic.vertx.dto;

public class ConductDrugFullDTO {
    public ConductDrugDTO conductDrug;
    public IyakuhinMasterDTO master;

    public static ConductDrugFullDTO copy(ConductDrugFullDTO src) {
        ConductDrugFullDTO dst = new ConductDrugFullDTO();
        dst.conductDrug = ConductDrugDTO.copy(src.conductDrug);
        dst.master = src.master;
        return dst;
    }


    public static ConductDrugFullDTO create(ConductDrugDTO conductDrug,
                                                             IyakuhinMasterDTO master) {
        ConductDrugFullDTO result = new ConductDrugFullDTO();
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