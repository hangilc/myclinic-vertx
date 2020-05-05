package dev.myclinic.vertx.hotlinelogevent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.dto.HotlineDTO;
import dev.myclinic.vertx.dto.HotlineLogDTO;
import dev.myclinic.vertx.hotlinelogevent.body.HotlineBeep;
import dev.myclinic.vertx.hotlinelogevent.body.HotlineCreated;

import java.util.function.Consumer;

public class HotlineLogger {

    private final static ObjectMapper mapper = new ObjectMapper();
    private final Consumer<HotlineLogDTO> onLogHandler;

    public HotlineLogger(Consumer<HotlineLogDTO> onLogHandler){
        this.onLogHandler = onLogHandler;
    }

    public void logCreated(HotlineDTO created){
        HotlineLogDTO dto = new HotlineLogDTO();
        dto.kind = "created";
        try {
            dto.body = mapper.writeValueAsString(new HotlineCreated(created));
        } catch(JsonProcessingException e){
            throw new RuntimeException(e);
        }
        onLogHandler.accept(dto);
    }

    public void logBeep(String target){
        HotlineLogDTO dto = new HotlineLogDTO();
        dto.kind = "beep";
        try {
            dto.body = mapper.writeValueAsString(new HotlineBeep(target));
        } catch(JsonProcessingException e){
            throw new RuntimeException(e);
        }
        onLogHandler.accept(dto);
    }

}
