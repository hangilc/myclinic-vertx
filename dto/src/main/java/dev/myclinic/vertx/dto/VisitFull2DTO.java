package dev.myclinic.vertx.dto;

import java.util.List;

public class VisitFull2DTO {
    public VisitDTO visit;
    public List<TextDTO> texts;
    public List<ShinryouFullDTO> shinryouList;
    public List<DrugFullDTO> drugs;
    public List<ConductFullDTO> conducts;
    public HokenDTO hoken;
    public ChargeDTO charge;
}
