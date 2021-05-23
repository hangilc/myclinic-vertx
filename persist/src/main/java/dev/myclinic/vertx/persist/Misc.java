package dev.myclinic.vertx.persist;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

class Misc {

    private static final DateTimeFormatter sqlDateFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd");

    public static LocalDate parseSqlDate(String sqlDate) {
        if (sqlDate.length() > 10) {
            sqlDate = sqlDate.substring(0, 10);
        }
        return LocalDate.parse(sqlDate, sqlDateFormatter);
    }

}
