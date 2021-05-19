package dev.myclinic.vertx.appoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class AppointLogDTO {

    public LocalDateTime createdAt;
    public Map<String, Object> logData;

    private AppointLogDTO(){

    }

    public Map<String, Object> toJsonObject(){
        Map<String, Object> map = new HashMap<>();
        map.put("createdAt", Misc.toSqlDatetime(createdAt));
        map.put("logData", logData);
        return map;
    }

    public static AppointLogDTO created(ObjectMapper mapper, AppointDTO created)
            throws JsonProcessingException {
        AppointLogDTO log = new AppointLogDTO();
        log.createdAt = LocalDateTime.now();
        log.logData = new AppointCreatedLogData(created).toJsonObject();
        return log;
    }

    public static AppointLogDTO canceled(ObjectMapper mapper, AppointDTO canceled)
            throws JsonProcessingException {
        AppointLogDTO log = new AppointLogDTO();
        log.createdAt = LocalDateTime.now();
        log.logData = new AppointCanceledLogData(canceled).toJsonObject();
        return log;
    }

}
