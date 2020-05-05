package dev.myclinic.vertx.dto;

import dev.myclinic.vertx.dto.annotation.AutoInc;
import dev.myclinic.vertx.dto.annotation.Primary;

public class PracticeLogDTO {

    @Primary
    @AutoInc
    public int serialId;
    public String createdAt;
    public String kind;
    public String body;

    @Override
    public String toString() {
        return "PracticeLogDTO{" +
                "serialId=" + serialId +
                ", createdAt='" + createdAt + '\'' +
                ", kind='" + kind + '\'' +
                ", body=" + body +
                '}';
    }
}
