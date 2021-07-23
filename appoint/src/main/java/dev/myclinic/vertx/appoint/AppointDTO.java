package dev.myclinic.vertx.appoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class AppointDTO {

    public LocalDate date;
    public LocalTime time;
    public String patientName;
    public int patientId;
    public String memo;

    public AppointDTO(){

    }

    public AppointDTO(LocalDate date, LocalTime time) {
        this.date = date;
        this.time = time;
        this.memo = "";
    }

    public Map<String, Object> toJsonObject(){
        Map<String, Object> m = new HashMap<>();
        m.put("date", date.toString());
        m.put("time", Misc.toSqlTime(time));
        m.put("patientName", patientName);
        m.put("patientId", patientId);
        m.put("memo", memo);
        return m;
    }

    @Override
    public String toString() {
        return "AppointDTO{" +
                "date=" + date +
                ", time=" + time +
                ", patientName='" + patientName + '\'' +
                ", patientId=" + patientId +
                ", memo=" + memo +
                '}';
    }

}
