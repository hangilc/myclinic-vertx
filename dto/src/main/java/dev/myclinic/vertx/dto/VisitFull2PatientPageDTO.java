package dev.myclinic.vertx.dto;

import java.util.List;

public class VisitFull2PatientPageDTO {

    public int totalPages;
    public int page;
    public List<VisitFull2PatientDTO> visitPatients;

    @Override
    public String toString() {
        return "VisitFull2PatientPageDTO{" +
                "totalPages=" + totalPages +
                ", page=" + page +
                ", visitPatients=" + visitPatients +
                '}';
    }
}
