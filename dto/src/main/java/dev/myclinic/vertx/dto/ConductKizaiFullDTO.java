package dev.myclinic.vertx.dto;

public class ConductKizaiFullDTO {
    public ConductKizaiDTO conductKizai;
    public KizaiMasterDTO master;

    public static dev.myclinic.vertx.dto.ConductKizaiFullDTO copy(ConductKizaiFullDTO src) {
        ConductKizaiFullDTO dst = new ConductKizaiFullDTO();
        dst.conductKizai = ConductKizaiDTO.copy(src.conductKizai);
        dst.master = src.master;
        return dst;
    }

    public static ConductKizaiFullDTO create(ConductKizaiDTO conductKizai,
                                                              KizaiMasterDTO master) {
        ConductKizaiFullDTO result = new ConductKizaiFullDTO();
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