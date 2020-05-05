package dev.myclinic.vertx.dto;

public class ConductKizaiFullDTO {
    public ConductKizaiDTO conductKizai;
    public KizaiMasterDTO master;

    public static dev.myclinic.vertx.dto.ConductKizaiFullDTO copy(dev.myclinic.vertx.dto.ConductKizaiFullDTO src) {
        dev.myclinic.vertx.dto.ConductKizaiFullDTO dst = new dev.myclinic.vertx.dto.ConductKizaiFullDTO();
        dst.conductKizai = ConductKizaiDTO.copy(src.conductKizai);
        dst.master = src.master;
        return dst;
    }

    public static dev.myclinic.vertx.dto.ConductKizaiFullDTO create(ConductKizaiDTO conductKizai,
                                                              KizaiMasterDTO master) {
        dev.myclinic.vertx.dto.ConductKizaiFullDTO result = new dev.myclinic.vertx.dto.ConductKizaiFullDTO();
        result.conductKizai = conductKizai;
        result.master = master;
        return result;
    }

    @Override
    public String toString() {
        return "ConductKizaiFullDTO{" +
                "conductKizai=" + conductKizai +
                ", master=" + master +
                '}';
    }
}