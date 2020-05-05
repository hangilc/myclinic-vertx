package dev.myclinic.vertx.dto;

import dev.myclinic.vertx.dto.annotation.Primary;

public class IntraclinicTagPostDTO {
    @Primary
    public int tagId;
    @Primary
    public int postId;
}
