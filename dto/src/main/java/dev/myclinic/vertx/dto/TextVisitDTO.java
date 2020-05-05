package dev.myclinic.vertx.dto;

public class TextVisitDTO {

    public dev.myclinic.vertx.dto.TextDTO text;
    public dev.myclinic.vertx.dto.VisitDTO visit;

    @Override
    public String toString() {
        return "TextVisitDTO{" +
                "text=" + text +
                ", visit=" + visit +
                '}';
    }

    public static dev.myclinic.vertx.dto.TextVisitDTO create(dev.myclinic.vertx.dto.TextDTO text, dev.myclinic.vertx.dto.VisitDTO visit){
        dev.myclinic.vertx.dto.TextVisitDTO result = new dev.myclinic.vertx.dto.TextVisitDTO();
        result.text = text;
        result.visit = visit;
        return result;
    }
}
