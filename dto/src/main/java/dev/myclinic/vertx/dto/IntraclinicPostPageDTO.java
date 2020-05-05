package dev.myclinic.vertx.dto;

import java.util.List;

public class IntraclinicPostPageDTO {
    public int totalPages;
    public List<dev.myclinic.vertx.dto.IntraclinicPostDTO> posts;

    @Override
    public String toString() {
        return "IntraclinicPostPageDTO{" +
                "totalPages=" + totalPages +
                ", posts=" + posts +
                '}';
    }
}
