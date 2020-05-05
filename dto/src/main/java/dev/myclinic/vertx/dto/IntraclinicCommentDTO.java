package dev.myclinic.vertx.dto;

import dev.myclinic.vertx.dto.annotation.AutoInc;
import dev.myclinic.vertx.dto.annotation.Primary;

public class IntraclinicCommentDTO {
    @Primary
    @AutoInc
    public int id;
    public String name;
    public String content;
    public int postId;
    public String createdAt;

    @Override
    public String toString() {
        return "IntraclinicCommentDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", postId=" + postId +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
