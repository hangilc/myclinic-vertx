package dev.myclinic.vertx.dto;

import java.util.List;

public class TodaysVisitsWithLogInfoDTO {

    public String serverId;
    public int serialId;
    public List<VisitFull2PatientDTO> visits;

    @Override
    public String toString() {
        return "TodaysVisitsWithLogInfoDTO{" +
                "serverId='" + serverId + '\'' +
                ", serialId='" + serialId + '\'' +
                ", visits=" + visits +
                '}';
    }
}
