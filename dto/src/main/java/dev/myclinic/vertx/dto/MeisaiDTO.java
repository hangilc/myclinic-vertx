package dev.myclinic.vertx.dto;

import java.util.List;

/**
 * Created by hangil on 2017/05/10.
 */
public class MeisaiDTO {
    public List<MeisaiSectionDTO> sections;
    public int totalTen;
    public int futanWari;
    public int charge;
    public HokenDTO hoken;

    @Override
    public String toString() {
        return "MeisaiDTO{" +
                "sections=" + sections +
                ", totalTen=" + totalTen +
                ", futanWari=" + futanWari +
                ", charge=" + charge +
                ", hoken=" + hoken +
                '}';
    }
}
