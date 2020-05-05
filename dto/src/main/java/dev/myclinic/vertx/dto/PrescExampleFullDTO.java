package dev.myclinic.vertx.dto;

public class PrescExampleFullDTO {
    public PrescExampleDTO prescExample;
    public IyakuhinMasterDTO master;

    public static dev.myclinic.vertx.dto.PrescExampleFullDTO copy(dev.myclinic.vertx.dto.PrescExampleFullDTO src){
        dev.myclinic.vertx.dto.PrescExampleFullDTO dst = new dev.myclinic.vertx.dto.PrescExampleFullDTO();
        dst.prescExample = src.prescExample;
        dst.master = src.master;
        return dst;
    }

    public static dev.myclinic.vertx.dto.PrescExampleFullDTO create(PrescExampleDTO example, IyakuhinMasterDTO master){
        dev.myclinic.vertx.dto.PrescExampleFullDTO result = new dev.myclinic.vertx.dto.PrescExampleFullDTO();
        result.prescExample = example;
        result.master = master;
        return result;
    }

    @Override
    public String toString() {
        return "PrescExampleFullDTO{" +
                "prescExample=" + prescExample +
                ", master=" + master +
                '}';
    }
}
