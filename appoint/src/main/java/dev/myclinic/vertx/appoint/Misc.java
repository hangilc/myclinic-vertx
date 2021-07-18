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

    public static LocalDate readAppointDate(String str){
        Pattern pat = Pattern.compile("(\\d+-)?(\\d+)-(\\d+)");
        Matcher m = pat.matcher(str);
        if( m.matches() ){
            int year;
            if( m.group(1) == null ){
                year = LocalDate.now().getYear();
            } else {
                year = Integer.parseInt(m.group(1));
            }
            int month, day;
            month = Integer.parseInt(m.group(2));
            day = Integer.parseInt(m.group(3));
            return LocalDate.of(year, month, day);
        } else {
            throw new RuntimeException("Invalid date: " + str);
        }
    }

    public static LocalTime readAppointTime(String str){
        Pattern pat = Pattern.compile("(\\d+):(\\d+)");
        Matcher m = pat.matcher(str);
        if( m.matches() ){
            return LocalTime.of(
                    Integer.parseInt(m.group(1)),
                    Integer.parseInt(m.group(2))
            );
        } else {
            throw new RuntimeException("Invalid time: " + str);
        }
    }
}
