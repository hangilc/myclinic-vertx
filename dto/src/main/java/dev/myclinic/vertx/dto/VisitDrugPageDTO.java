package dev.myclinic.vertx.dto;

import java.util.List;

public class VisitDrugPageDTO {

    public int page;
    public int totalPages;
    public List<dev.myclinic.vertx.dto.VisitDrugDTO> visitDrugs;

    @Override
    public String toString() {
        return "VisitDrugPageDTO{" +
                "page=" + page +
                ", totalPages=" + totalPages +
                ", visitDrugs=" + visitDrugs +
                '}';
    }
}
