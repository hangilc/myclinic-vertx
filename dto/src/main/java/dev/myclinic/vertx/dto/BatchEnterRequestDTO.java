package dev.myclinic.vertx.dto;


import java.util.List;

public class BatchEnterRequestDTO {

    public List<dev.myclinic.vertx.dto.DrugWithAttrDTO> drugs;
    public List<dev.myclinic.vertx.dto.ShinryouWithAttrDTO> shinryouList;
    public List<dev.myclinic.vertx.dto.ConductEnterRequestDTO> conducts;

    @Override
    public String toString() {
        return "BatchEnterRequestDTO{" +
                "drugs=" + drugs +
                ", shinryouList=" + shinryouList +
                ", conducts=" + conducts +
                '}';
    }
}
