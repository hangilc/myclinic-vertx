package dev.myclinic.vertx.appoint;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Misc {

    private static final DateTimeFormatter sqlDateFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd");
    private static final DateTimeFormatter sqlDateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
    private static final DateTimeFormatter sqlTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static String toSqlDate(LocalDate date) {
        return date.toString();
    }

    public static String toSqlTime(LocalTime time){
        return time.format(sqlTimeFormatter);
    }

    public static String toSqlDatetime(LocalDateTime dateTime){
        return dateTime.format(sqlDateTimeFormatter);
    }

    public static LocalDate fromSqlDate(String sqldate){
        return LocalDate.parse(sqldate, sqlDateFormatter);
    }

    public static LocalDateTime fromSqlDatetime(String datetime){
        return LocalDateTime.parse(datetime, sqlDateTimeFormatter);
    }

    public static LocalTime fromSqlTime(String time){
        return LocalTime.parse(time, sqlTimeFormatter);
    }

}
