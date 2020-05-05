package dev.myclinic.vertx.dto;

import java.util.List;
import java.util.Objects;

public class DiseaseNewDTO {
    public dev.myclinic.vertx.dto.DiseaseDTO disease;
    public List<dev.myclinic.vertx.dto.DiseaseAdjDTO> adjList;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        dev.myclinic.vertx.dto.DiseaseNewDTO that = (dev.myclinic.vertx.dto.DiseaseNewDTO) o;
        return Objects.equals(disease, that.disease) &&
                Objects.equals(adjList, that.adjList);
    }

    @Override
    public int hashCode() {

        return Objects.hash(disease, adjList);
    }

    @Override
    public String toString() {
        return "DiseaseNewDTO{" +
                "disease=" + disease +
                ", adjList=" + adjList +
                '}';
    }
}
