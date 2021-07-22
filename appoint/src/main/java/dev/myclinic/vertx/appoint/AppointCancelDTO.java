package dev.myclinic.vertx.appoint;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AppointCancelDTO {

    public LocalDate date;
    public LocalTime time;
    public String patientName;
    public Integer patientId;
    public LocalDateTime canceledAt;


}
