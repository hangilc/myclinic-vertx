package dev.myclinic.vertx.dto;

import java.util.List;

public class VisitFull2PageDTO {
    public int totalPages;
    public int page;
    public List<dev.myclinic.vertx.dto.VisitFull2DTO> visits;
}
