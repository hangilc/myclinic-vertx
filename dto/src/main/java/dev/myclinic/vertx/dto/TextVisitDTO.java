package dev.myclinic.vertx.dto;

public class TextVisitDTO {

    public TextDTO text;
    public VisitDTO visit;

    public TextVisitDTO(TextDTO text, VisitDTO visit) {
        this.text = text;
        this.visit = visit;
    }

    @Override
    public String toString() {
        return "TextVisitDTO{" +
                "text=" + text +
                ", visit=" + visit +
                '}';
    }

}
