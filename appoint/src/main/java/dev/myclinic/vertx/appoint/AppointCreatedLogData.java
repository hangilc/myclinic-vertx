package dev.myclinic.vertx.appoint;

import java.util.HashMap;
import java.util.Map;

public class AppointCreatedLogData {

    public final String kind = "created";
    public final AppointDTO created;

    public AppointCreatedLogData(AppointDTO created){
        this.created = created;
    }

    public Map<String, Object> toJsonObject() {
        Map<String, Object> map = new HashMap<>();
        map.put("kind", kind);
        map.put("created", created.toJsonObject());
        return map;
    }
}
