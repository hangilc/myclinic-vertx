package dev.myclinic.vertx.dto;

import java.util.List;

public class VisitTextDrugDTO {
    public VisitDTO visit;
    public List<TextDTO> texts;
    public List<DrugFullDTO> drugs;
}
