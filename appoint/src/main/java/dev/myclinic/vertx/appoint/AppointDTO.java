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
    public Map<String, Object> attributes;

    public AppointDTO() {

    }

    public String getAttrsAsJson(ObjectMapper mapper) throws JsonProcessingException {
        if( attributes == null ){
            return null;
        } else {
            return mapper.writeValueAsString(attributes);
        }
    }

    public Map<String, Object> toJsonObject(){
        Map<String, Object> map = new HashMap<>();
        map.put("appointDate", appointDate.toString());
        map.put("appointTime", Misc.toSqlTime(appointTime));
        map.put("patientName", patientName);
        map.put("attributes", attributes);
        return map;
    }

}
