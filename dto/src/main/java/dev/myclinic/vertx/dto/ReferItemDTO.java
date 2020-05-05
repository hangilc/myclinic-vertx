package dev.myclinic.vertx.dto;

public class ReferItemDTO {

    public String hospital;
    public String section;
    public String doctor;

    @Override
    public String toString() {
        return "ReferItemDTO{" +
                "hospital='" + hospital + '\'' +
                ", section='" + section + '\'' +
                ", doctor='" + doctor + '\'' +
                '}';
    }
}
