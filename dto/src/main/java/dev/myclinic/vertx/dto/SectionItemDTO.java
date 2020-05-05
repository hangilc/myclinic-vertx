package dev.myclinic.vertx.dto;

/**
 * Created by hangil on 2017/05/10.
 */
public class SectionItemDTO {
    public String label;
    public int tanka;
    public int count;

    @Override
    public String toString() {
        return "SectionItemDTO{" +
                "label='" + label + '\'' +
                ", tanka=" + tanka +
                ", count=" + count +
                '}';
    }
}
