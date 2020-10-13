package dev.myclinic.vertx.prescfax;

import java.util.List;

public class ShohousenGroup {

    public final Pharmacy pharmacy;
    public final List<Presc> prescList;

    public ShohousenGroup(Pharmacy pharmacy, List<Presc> prescList) {
        this.pharmacy = pharmacy;
        this.prescList = prescList;
    }

    @Override
    public String toString() {
        return "ShohousenGroup{" +
                "pharmacy=" + pharmacy +
                ", prescList=" + prescList +
                '}';
    }
}
