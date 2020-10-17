package dev.myclinic.vertx.prescfax;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ShohousenGroup {

    public final Pharmacy pharmacy;
    public final List<Presc> items;

    public ShohousenGroup(@JsonProperty("pharmacy") Pharmacy pharmacy,
                          @JsonProperty("items") List<Presc> items) {
        this.pharmacy = pharmacy;
        this.items = items;
    }

    @Override
    public String toString() {
        return "ShohousenGroup{" +
                "pharmacy=" + pharmacy +
                ", prescList=" + items +
                '}';
    }
}
