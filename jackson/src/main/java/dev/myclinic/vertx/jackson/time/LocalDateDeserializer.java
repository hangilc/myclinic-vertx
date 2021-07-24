package dev.myclinic.vertx.jackson.time;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
    private static final DateTimeFormatter sqlDateFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd");

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext ctx)
            throws IOException, JsonProcessingException {
        if( parser.currentToken() == JsonToken.VALUE_NULL ){
            return null;
        } else {
            String src = parser.getText();
            return LocalDate.parse(src, sqlDateFormatter);
        }
    }
}
