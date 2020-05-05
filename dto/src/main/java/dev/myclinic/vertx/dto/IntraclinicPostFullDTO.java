package dev.myclinic.vertx.dto;

import java.util.List;

public class IntraclinicPostFullDTO {
    public IntraclinicPostDTO post;
    public List<dev.myclinic.vertx.dto.IntraclinicCommentDTO> comments;

    @Override
    public String toString() {
        return "IntraclinicPostFullDTO{" +
                "post=" + post +
                ", comments=" + comments +
                '}';
    }
}
