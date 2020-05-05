package dev.myclinic.vertx.db;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TableHelper {

    private static final DateTimeFormatter sqlDateTimeFormatter =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");

    private TableHelper() { }


    public static String validUptoFromLocalDateToString(LocalDate dbValue){
        return dbValue == null ? "0000-00-00" : dbValue.toString();
    }

    public static LocalDate validUptoFromStringToLocalDate(String value){
        if( value == null || value.equals("0000-00-00") ){
            return null;
        } else {
            return LocalDate.parse(value);
        }
    }

    public static String localDateTimeToString(LocalDateTime at){
        return at.format(sqlDateTimeFormatter);
    }

    public static LocalDateTime stringToLocalDateTime(String str){
        return LocalDateTime.parse(str, sqlDateTimeFormatter);
    }

    public static Integer tensuuToInteger(String tensuu){
        return (int)(Double.parseDouble(tensuu));
    }

}
