package dev.myclinic.vertx.dto;

import java.util.ArrayList;
import java.util.List;

public class DiseaseFullDTO {
    public dev.myclinic.vertx.dto.DiseaseDTO disease;
    public dev.myclinic.vertx.dto.ByoumeiMasterDTO master;
    public List<dev.myclinic.vertx.dto.DiseaseAdjFullDTO> adjList;

    @Override
    public String toString() {
        return "DiseaseFullDTO{" +
                "disease=" + disease +
                ", master=" + master +
                ", adjList=" + adjList +
                '}';
    }

    public static dev.myclinic.vertx.dto.DiseaseFullDTO create(dev.myclinic.vertx.dto.DiseaseDTO disease, dev.myclinic.vertx.dto.ByoumeiMasterDTO master){
        dev.myclinic.vertx.dto.DiseaseFullDTO result = new dev.myclinic.vertx.dto.DiseaseFullDTO();
        result.disease = disease;
        result.master = master;
        result.adjList = new ArrayList<>();
        return result;
    }
}
