package dev.myclinic.vertx.dto;

import dev.myclinic.vertx.dto.annotation.AutoInc;
import dev.myclinic.vertx.dto.annotation.Primary;

public class IntraclinicTagDTO {

    @Primary
    @AutoInc
    public int tagId;
    public String name;

    @Override
    public String toString() {
        return "IntraclinicTagDTO{" +
                "tagId=" + tagId +
                ", name='" + name + '\'' +
                '}';
    }
}
