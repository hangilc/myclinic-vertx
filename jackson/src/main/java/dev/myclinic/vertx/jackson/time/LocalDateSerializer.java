package dev.myclinic.vertx.jackson.time;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateSerializer extends JsonSerializer<LocalDate> {
    private static final DateTimeFormatter sqlDateFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd");

    @Override
    public void serialize(LocalDate localDate, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        if( localDate == null ){
            gen.writeNull();
        } else {
            gen.writeString(localDate.format(sqlDateFormatter));
        }
    }
}
