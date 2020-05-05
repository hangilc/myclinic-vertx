package dev.myclinic.vertx.dto;


import java.util.List;

/**
 * Created by hangil on 2017/05/10.
 */
public class MeisaiSectionDTO {
    public String name;
    public String label;
    public List<dev.myclinic.vertx.dto.SectionItemDTO> items;
    public int sectionTotalTen;

    @Override
    public String toString() {
        return "MeisaiSectionDTO{" +
                "name='" + name + '\'' +
                ", label='" + label + '\'' +
                ", items=" + items +
                ", sectionTotalTen=" + sectionTotalTen +
                '}';
    }
}
