package dev.myclinic.vertx.dto;


import java.util.List;

public class BatchEnterRequestDTO {

    public List<DrugWithAttrDTO> drugs;
    public List<ShinryouWithAttrDTO> shinryouList;
    public List<ConductEnterRequestDTO> conducts;

    @Override
    public String toString() {
        return "BatchEnterRequestDTO{" +
                "drugs=" + drugs +
                ", shinryouList=" + shinryouList +
                ", conducts=" + conducts +
                '}';
    }
}
