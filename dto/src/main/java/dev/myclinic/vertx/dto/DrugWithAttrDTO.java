package dev.myclinic.vertx.dto;

public class DrugWithAttrDTO {

    public DrugDTO drug;
    public DrugAttrDTO attr;

    public static dev.myclinic.vertx.dto.DrugWithAttrDTO create(DrugDTO drug, DrugAttrDTO attr){
        dev.myclinic.vertx.dto.DrugWithAttrDTO result = new dev.myclinic.vertx.dto.DrugWithAttrDTO();
        result.drug = drug;
        result.attr = attr;
        return result;
    }

    @Override
    public String toString() {
        return "DrugWithAttrDTO{" +
                "drug=" + drug +
                ", attr=" + attr +
                '}';
    }
}
