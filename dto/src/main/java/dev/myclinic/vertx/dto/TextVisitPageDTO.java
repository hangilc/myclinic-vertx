package dev.myclinic.vertx.dto;

import java.util.List;

public class TextVisitPageDTO {

    public int totalPages;
    public int page;
    public List<TextVisitDTO> textVisits;

    @Override
    public String toString() {
        return "TextVisitPageDTO{" +
                "totalPages=" + totalPages +
                ", page=" + page +
                ", textVisits=" + textVisits +
                '}';
    }
}
