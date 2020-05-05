package dev.myclinic.vertx.dto;

import java.util.List;

public class TextVisitPatientPageDTO {
    public int totalPages;
    public int page;
    public List<TextVisitPatientDTO> textVisitPatients;

    @Override
    public String toString() {
        return "TextVisitPatientPageDTO{" +
                "totalPages=" + totalPages +
                ", page=" + page +
                ", textVisitPatients=" + textVisitPatients +
                '}';
    }
}
