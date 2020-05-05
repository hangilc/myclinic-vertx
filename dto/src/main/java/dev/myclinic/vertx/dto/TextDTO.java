package dev.myclinic.vertx.dto;

import dev.myclinic.vertx.dto.annotation.AutoInc;
import dev.myclinic.vertx.dto.annotation.Primary;

import java.util.Objects;

/**
 * Created by hangil on 2017/06/07.
 */
public class TextDTO {
    @Primary
    @AutoInc
    public int textId;
    public int visitId;
    public String content;

    public static dev.myclinic.vertx.dto.TextDTO create(int visitId, String content){
        dev.myclinic.vertx.dto.TextDTO text = new dev.myclinic.vertx.dto.TextDTO();
        text.visitId = visitId;
        text.content = content;
        return text;
    }

    public dev.myclinic.vertx.dto.TextDTO copy(){
        dev.myclinic.vertx.dto.TextDTO textDTO = new dev.myclinic.vertx.dto.TextDTO();
        textDTO.textId = textId;
        textDTO.visitId = visitId;
        textDTO.content = content;
        return textDTO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        dev.myclinic.vertx.dto.TextDTO textDTO = (dev.myclinic.vertx.dto.TextDTO) o;
        return textId == textDTO.textId &&
                visitId == textDTO.visitId &&
                Objects.equals(content, textDTO.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(textId, visitId, content);
    }

    @Override
    public String toString() {
        return "TextDTO{" +
                "textId=" + textId +
                ", visitId=" + visitId +
                ", content='" + content + '\'' +
                '}';
    }
}
