package dev.myclinic.vertx.dto;

import java.util.List;

public class DiseaseExampleDTO {
    public String label;
    public String byoumei;
    public List<String> adjList;

    @Override
    public String toString() {
        return "DiseaseExampleDTO{" +
                "label='" + label + '\'' +
                ", byoumei='" + byoumei + '\'' +
                ", adjList=" + adjList +
                '}';
    }
}
