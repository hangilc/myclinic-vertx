package dev.myclinic.vertx.dto;

public class ShinryouWithAttrDTO {

    public ShinryouDTO shinryou;
    public ShinryouAttrDTO attr;

    public static dev.myclinic.vertx.dto.ShinryouWithAttrDTO create(ShinryouDTO shinryou, ShinryouAttrDTO attr){
        dev.myclinic.vertx.dto.ShinryouWithAttrDTO result = new dev.myclinic.vertx.dto.ShinryouWithAttrDTO();
        result.shinryou = shinryou;
        result.attr = attr;
        return result;
    }

    @Override
    public String toString() {
        return "ShinryouWithAttrDTO{" +
                "shinryou=" + shinryou +
                ", attr=" + attr +
                '}';
    }
}
