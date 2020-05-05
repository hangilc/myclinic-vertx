package dev.myclinic.vertx.dto;

import java.util.List;

public class VisitDrugDTO {

    public dev.myclinic.vertx.dto.VisitDTO visit;
    public List<dev.myclinic.vertx.dto.DrugFullDTO> drugs;

    @Override
    public String toString() {
        return "VisitDrugDTO{" +
                "visit=" + visit +
                ", drugs=" + drugs +
                '}';
    }
}
