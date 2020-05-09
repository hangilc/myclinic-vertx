package dev.myclinic.vertx.dto;

import java.util.List;

public class VisitDrugDTO {

    public VisitDTO visit;
    public List<DrugFullDTO> drugs;

    public VisitDrugDTO() {
    }

    public VisitDrugDTO(VisitDTO visit, List<DrugFullDTO> drugs) {
        this.visit = visit;
        this.drugs = drugs;
    }

    @Override
    public String toString() {
        return "VisitDrugDTO{" +
                "visit=" + visit +
                ", drugs=" + drugs +
                '}';
    }
}
