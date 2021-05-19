package dev.myclinic.vertx.appoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

public class AppointLogDTO {

    public LocalDateTime createdAt;
    public String logData;

    private AppointLogDTO(){

    }

    public static AppointLogDTO created(ObjectMapper mapper, AppointDTO created)
            throws JsonProcessingException {
        AppointLogDTO log = new AppointLogDTO();
        log.createdAt = LocalDateTime.now();
        log.logData = mapper.writeValueAsString(new AppointCreatedLogData(created));
        return log;
    }

    public static AppointLogDTO canceled(ObjectMapper mapper, AppointDTO canceled)
            throws JsonProcessingException {
        AppointLogDTO log = new AppointLogDTO();
        log.createdAt = LocalDateTime.now();
        log.logData = mapper.writeValueAsString(new AppointCanceledLogData(canceled));
        return log;
    }

}
