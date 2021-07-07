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
    public Integer patientId;
    public LocalDateTime appointedAt;

    public AppointDTO(LocalDate date, LocalTime time) {
        this.date = date;
        this.time = time;
    }

    @Override
    public String toString() {
        return "AppointDTO{" +
                "date=" + date +
                ", time=" + time +
                ", patientName='" + patientName + '\'' +
                ", patientId=" + patientId +
                ", appointedAt=" + appointedAt +
                '}';
    }

}
