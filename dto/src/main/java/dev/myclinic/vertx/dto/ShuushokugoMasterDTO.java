package dev.myclinic.vertx.dto;

import dev.myclinic.vertx.dto.annotation.Primary;

public class ShuushokugoMasterDTO {
    @Primary
    public int shuushokugocode;
    public String name;

    @Override
    public String toString() {
        return "ShuushokugoMasterDTO{" +
                "shuushokugocode=" + shuushokugocode +
                ", name='" + name + '\'' +
                '}';
    }
}
