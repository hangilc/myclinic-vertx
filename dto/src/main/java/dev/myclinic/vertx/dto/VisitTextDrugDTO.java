package dev.myclinic.vertx.dto;

import java.util.List;

public class VisitTextDrugDTO {
    public dev.myclinic.vertx.dto.VisitDTO visit;
    public List<dev.myclinic.vertx.dto.TextDTO> texts;
    public List<dev.myclinic.vertx.dto.DrugFullDTO> drugs;
}
