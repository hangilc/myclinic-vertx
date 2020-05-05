package dev.myclinic.vertx.dto;

import java.util.List;

public class VisitFullPageDTO {
    public int totalPages;
    public int page;
    public List<dev.myclinic.vertx.dto.VisitFullDTO> visits;
}
