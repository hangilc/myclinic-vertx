package dev.myclinic.vertx.dto;

public class ShinryouFullWithAttrDTO {

    public dev.myclinic.vertx.dto.ShinryouFullDTO shinryou;
    public dev.myclinic.vertx.dto.ShinryouAttrDTO attr;

    public static dev.myclinic.vertx.dto.ShinryouFullWithAttrDTO create(dev.myclinic.vertx.dto.ShinryouFullDTO shinryou, dev.myclinic.vertx.dto.ShinryouAttrDTO attr){
        dev.myclinic.vertx.dto.ShinryouFullWithAttrDTO result = new dev.myclinic.vertx.dto.ShinryouFullWithAttrDTO();
        result.shinryou = shinryou;
        result.attr = attr;
        return result;
    }
}
