package dev.myclinic.vertx.appoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class AppointDTO {

    public LocalDate appointDate;
    public LocalTime appointTime;
    public String patientName;
    public Map<String, Object> attrs;

    public AppointDTO() {

    }

    public String getAttrsAsJson(ObjectMapper mapper) throws JsonProcessingException {
        if( attrs == null ){
            return null;
        } else {
            return mapper.writeValueAsString(attrs);
        }
    }

    public Map<String, Object> toJsonObject(){
        Map<String, Object> map = new HashMap<>();
        map.put("appontDate", appointDate.toString());
        map.put("appointTime", Misc.toSqlTime(appointTime));
        map.put("patientName", patientName);
        map.put("attrs", attrs);
        return map;
    }

}
