package dev.myclinic.vertx.jackson.time;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeDeserializer extends JsonDeserializer<LocalTime> {
    private static final DateTimeFormatter sqlTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public LocalTime deserialize(JsonParser parser, DeserializationContext provider)
            throws IOException, JsonProcessingException {
        if( parser.currentToken() == JsonToken.VALUE_NULL ){
            return null;
        } else {
            String src = parser.getText();
            return LocalTime.parse(src, sqlTimeFormatter);
        }
    }
}
