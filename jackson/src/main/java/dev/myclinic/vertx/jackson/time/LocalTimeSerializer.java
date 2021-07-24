package dev.myclinic.vertx.jackson.time;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeSerializer extends JsonSerializer<LocalTime> {
    private static final DateTimeFormatter sqlTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void serialize(LocalTime localTime, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        if( localTime == null ){
            gen.writeNull();
        } else {
            gen.writeString(localTime.format(sqlTimeFormatter));
        }
    }
}
