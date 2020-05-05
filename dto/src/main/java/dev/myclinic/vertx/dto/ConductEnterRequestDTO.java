package dev.myclinic.vertx.dto;

import java.util.List;

public class ConductEnterRequestDTO {

    public int visitId;
    public int kind;
    public String gazouLabel;
    public List<ConductShinryouDTO> shinryouList;
    public List<ConductDrugDTO> drugs;
    public List<ConductKizaiDTO> kizaiList;

}
