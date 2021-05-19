package dev.myclinic.vertx.appoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class AppointCreatedLogData {

    public final String kind = "created";
    public final AppointDTO created;

    public AppointCreatedLogData(AppointDTO created){
        this.created = created;
    }

    public String toJson(ObjectMapper mapper) throws JsonProcessingException {
        Map<String, Object> map = new HashMap<>();
        map.put("kind", kind);
        map.put("created", created.toJsonObject());
        return mapper.writeValueAsString(map);
    }
}
