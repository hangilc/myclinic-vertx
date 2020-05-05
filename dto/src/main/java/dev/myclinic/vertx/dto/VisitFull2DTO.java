package dev.myclinic.vertx.dto;

import java.util.List;

public class VisitFull2DTO {
    public dev.myclinic.vertx.dto.VisitDTO visit;
    public List<dev.myclinic.vertx.dto.TextDTO> texts;
    public List<ShinryouFullDTO> shinryouList;
    public List<DrugFullDTO> drugs;
    public List<ConductFullDTO> conducts;
    public HokenDTO hoken;
    public ChargeDTO charge;
}
