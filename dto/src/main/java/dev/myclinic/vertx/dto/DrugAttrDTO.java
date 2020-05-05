package dev.myclinic.vertx.dto;

import dev.myclinic.vertx.dto.annotation.Primary;

import java.util.Objects;

public class DrugAttrDTO {
    @Primary
    public int drugId;
    public String tekiyou;

    public static dev.myclinic.vertx.dto.DrugAttrDTO copy(dev.myclinic.vertx.dto.DrugAttrDTO src) {
        dev.myclinic.vertx.dto.DrugAttrDTO dst = new dev.myclinic.vertx.dto.DrugAttrDTO();
        dst.drugId = src.drugId;
        dst.tekiyou = src.tekiyou;
        return dst;
    }

    public static boolean isEmpty(dev.myclinic.vertx.dto.DrugAttrDTO attr) {
        return attr.tekiyou == null || attr.tekiyou.isEmpty();
    }

    public static String getTekiyou(dev.myclinic.vertx.dto.DrugAttrDTO attr, String tekiyou){
        return attr == null ? null : attr.tekiyou;
    }

    public static dev.myclinic.vertx.dto.DrugAttrDTO setTekiyou(int drugId, dev.myclinic.vertx.dto.DrugAttrDTO attr, String tekiyou){
        if( attr == null ){
            attr = new dev.myclinic.vertx.dto.DrugAttrDTO();
            attr.drugId = drugId;
        }
        attr.tekiyou = tekiyou;
        if( isEmpty(attr) ){
            attr = null;
        }
        return attr;
    }

    public static dev.myclinic.vertx.dto.DrugAttrDTO deleteTekiyou(int drugId, dev.myclinic.vertx.dto.DrugAttrDTO attr){
        return setTekiyou(drugId, attr, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        dev.myclinic.vertx.dto.DrugAttrDTO that = (dev.myclinic.vertx.dto.DrugAttrDTO) o;
        return drugId == that.drugId &&
                Objects.equals(tekiyou, that.tekiyou);
    }

    @Override
    public int hashCode() {
        return Objects.hash(drugId, tekiyou);
    }

    @Override
    public String toString() {
        return "DrugAttrDTO{" +
                "drugId=" + drugId +
                ", tekiyou='" + tekiyou + '\'' +
                '}';
    }
}
