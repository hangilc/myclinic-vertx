package dev.myclinic.vertx.dto;

public class TextVisitDTO {

    public TextDTO text;
    public VisitDTO visit;

    @Override
    public String toString() {
        return "TextVisitDTO{" +
                "text=" + text +
                ", visit=" + visit +
                '}';
    }

    public static TextVisitDTO create(TextDTO text, VisitDTO visit){
        TextVisitDTO result = new TextVisitDTO();
        result.text = text;
        result.visit = visit;
        return result;
    }
}
