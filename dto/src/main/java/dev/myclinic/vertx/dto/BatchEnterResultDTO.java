package dev.myclinic.vertx.dto;

import java.util.List;

public class BatchEnterResultDTO {
    public List<Integer> drugIds;
    public List<Integer> shinryouIds;
    public List<Integer> conductIds;

    public static void assign(BatchEnterResultDTO dst, dev.myclinic.vertx.dto.BatchEnterResultDTO src){
        dst.drugIds = src.drugIds;
        dst.shinryouIds = src.shinryouIds;
        dst.conductIds = src.conductIds;
    }

    @Override
    public String toString() {
        return "BatchEnterResultDTO{" +
                "drugIds=" + drugIds +
                ", shinryouIds=" + shinryouIds +
                ", conductIds=" + conductIds +
                '}';
    }
}
