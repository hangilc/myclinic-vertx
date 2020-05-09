package dev.myclinic.vertx.dto;


import java.util.ArrayList;
import java.util.List;

public class BatchEnterRequestDTO {

    public List<DrugWithAttrDTO> drugs;
    public List<ShinryouWithAttrDTO> shinryouList;
    public List<ConductEnterRequestDTO> conducts;

    public static BatchEnterRequestDTO create(){
        BatchEnterRequestDTO dto = new BatchEnterRequestDTO();
        dto.drugs = new ArrayList<>();
        dto.shinryouList = new ArrayList<>();
        dto.conducts = new ArrayList<>();
        return dto;
    }

    @Override
    public String toString() {
        return "BatchEnterRequestDTO{" +
                "drugs=" + drugs +
                ", shinryouList=" + shinryouList +
                ", conducts=" + conducts +
                '}';
    }
}
