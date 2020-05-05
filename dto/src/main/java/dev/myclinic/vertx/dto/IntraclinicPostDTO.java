package dev.myclinic.vertx.dto;

import dev.myclinic.vertx.dto.annotation.AutoInc;
import dev.myclinic.vertx.dto.annotation.Primary;

public class IntraclinicPostDTO {
    @Primary
    @AutoInc
    public Integer id;
    public String content;
    public String createdAt;

    @Override
    public String toString() {
        return "IntraclinicPostDTO{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
