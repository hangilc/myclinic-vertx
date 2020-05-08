package dev.myclinic.vertx.dto;

import java.util.ArrayList;
import java.util.List;

public class DiseaseFullDTO {
    public DiseaseDTO disease;
    public ByoumeiMasterDTO master;
    public List<DiseaseAdjFullDTO> adjList;

    public DiseaseFullDTO(){

    }

    public DiseaseFullDTO(DiseaseDTO disease, ByoumeiMasterDTO master){
        this.disease = disease;
        this.master = master;
    }

    public DiseaseFullDTO(DiseaseDTO disease, ByoumeiMasterDTO master, List<DiseaseAdjFullDTO> adjList){
        this.disease = disease;
        this.master = master;
        this.adjList = adjList;
    }

    @Override
    public String toString() {
        return "DiseaseFullDTO{" +
                "disease=" + disease +
                ", master=" + master +
                ", adjList=" + adjList +
                '}';
    }

    public static DiseaseFullDTO create(DiseaseDTO disease, ByoumeiMasterDTO master){
        DiseaseFullDTO result = new DiseaseFullDTO();
        result.disease = disease;
        result.master = master;
        result.adjList = new ArrayList<>();
        return result;
    }
}
