package dev.myclinic.vertx.appoint;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

class Misc {

    private static final DateTimeFormatter sqlDateFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd");
    private static final DateTimeFormatter sqlDateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
    private static final DateTimeFormatter sqlTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static String toSqlTime(LocalTime time){
        return time.format(sqlTimeFormatter);
    }

    public static String toSqlDatetime(LocalDateTime dateTime){
        return dateTime.format(sqlDateTimeFormatter);
    }
}
