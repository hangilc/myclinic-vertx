package dev.myclinic.vertx.appoint;

import java.util.HashMap;
import java.util.Map;

public class AppointCanceledLogData {

    public String kind = "canceled";
    public AppointDTO canceled;

    public AppointCanceledLogData(AppointDTO canceled) {
        this.canceled = canceled;
    }

    public Map<String, Object> toJsonObject() {
        Map<String, Object> map = new HashMap<>();
        map.put("kind", kind);
        map.put("canceled", canceled.toJsonObject());
        return map;
    }

}
