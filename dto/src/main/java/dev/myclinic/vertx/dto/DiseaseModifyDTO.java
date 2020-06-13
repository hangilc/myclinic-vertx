package dev.myclinic.vertx.dto;

import java.util.List;

public class DiseaseModifyDTO {
    public DiseaseDTO disease;
    public List<Integer> shuushokugocodes;

    @Override
    public String toString() {
        return "DiseaseModifyDTO{" +
                "disease=" + disease +
                ", shuushokugocodes=" + shuushokugocodes +
                '}';
    }
}
