package dev.myclinic.vertx.dto;

import dev.myclinic.vertx.dto.annotation.Primary;

public class ShoukiDTO {
    @Primary
    public int visitId;
    public String shouki;

    public static dev.myclinic.vertx.dto.ShoukiDTO copy(dev.myclinic.vertx.dto.ShoukiDTO src){
        dev.myclinic.vertx.dto.ShoukiDTO dst = new dev.myclinic.vertx.dto.ShoukiDTO();
        dst.visitId = src.visitId;
        dst.shouki = src.shouki;
        return dst;
    }

    @Override
    public String toString() {
        return "ShoukiDTO{" +
                "visitId=" + visitId +
                ", shouki='" + shouki + '\'' +
                '}';
    }
}
