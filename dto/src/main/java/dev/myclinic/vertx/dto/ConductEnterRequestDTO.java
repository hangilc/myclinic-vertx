package dev.myclinic.vertx.dto;

import java.util.ArrayList;
import java.util.List;

public class ConductEnterRequestDTO {

    public int visitId;
    public int kind;
    public String gazouLabel;
    public List<ConductShinryouDTO> shinryouList;
    public List<ConductDrugDTO> drugs;
    public List<ConductKizaiDTO> kizaiList;

    public static ConductEnterRequestDTO create(int visitId, int kind, String gazouLabel) {
        ConductEnterRequestDTO dto = new ConductEnterRequestDTO();
        dto.visitId = visitId;
        dto.kind = kind;
        dto.gazouLabel = gazouLabel;
        dto.shinryouList = new ArrayList<>();
        dto.drugs = new ArrayList<>();
        dto.kizaiList = new ArrayList<>();
        return dto;
    }
}
