package dev.myclinic.vertx.dto;

public class DiseaseModifyEndReasonDTO {
    public int diseaseId;
    public String endDate;
    public char endReason;

    @Override
    public String toString() {
        return "DiseaseModifyEndReasonDTO{" +
                "diseaseId=" + diseaseId +
                ", endDate='" + endDate + '\'' +
                ", endReason=" + endReason +
                '}';
    }
}
