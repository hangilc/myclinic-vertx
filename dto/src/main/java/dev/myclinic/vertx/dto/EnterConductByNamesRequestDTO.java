package dev.myclinic.vertx.dto;

import java.util.List;

public class EnterConductByNamesRequestDTO {
    public int kind;
    public String gazouLabel;
    public List<String> shinryouNames;
    public List<dev.myclinic.vertx.dto.EnterConductKizaiByNamesRequestDTO> kizaiList;

    @Override
    public String toString() {
        return "EnterConductByNamesRequestDTO{" +
                "kind=" + kind +
                ", gazouLabel='" + gazouLabel + '\'' +
                ", shinryouNames=" + shinryouNames +
                ", kizaiList=" + kizaiList +
                '}';
    }
}
