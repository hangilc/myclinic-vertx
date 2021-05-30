package dev.myclinic.vertx.util;

import dev.myclinic.vertx.util.kanjidate.KanjiDateRepBuilder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

public class DateTimeUtil {

    private static final DateTimeFormatter sqlDateFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd");
    private static final DateTimeFormatter sqlDateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
    private static final DateTimeFormatter sqlTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter packedSqlDateTimeFormatter = DateTimeFormatter.ofPattern("uuuuMMddHHmmss");
    public static Function<KanjiDateRepBuilder, KanjiDateRepBuilder> kanjiFormatter1 = KanjiDateRepBuilder::format1;
//    public static Function<KanjiDateRepBuilder, KanjiDateRepBuilder> kanjiFormatter2 = KanjiDateRepBuilder::format2;
//    public static Function<KanjiDateRepBuilder, KanjiDateRepBuilder> kanjiFormatter3 = KanjiDateRepBuilder::format3;
//    public static Function<KanjiDateRepBuilder, KanjiDateRepBuilder> kanjiFormatter4 = KanjiDateRepBuilder::format4;
//    public static Function<KanjiDateRepBuilder, KanjiDateRepBuilder> kanjiFormatter5 = KanjiDateRepBuilder::format5;
//    public static Function<KanjiDateRepBuilder, KanjiDateRepBuilder> kanjiFormatter6 = KanjiDateRepBuilder::format6;
//    public static Function<KanjiDateRepBuilder, KanjiDateRepBuilder> kanjiFormatter7 = KanjiDateRepBuilder::format7;

    public static String toKanji(LocalDate date,
                                 Function<KanjiDateRepBuilder, KanjiDateRepBuilder> formatter) {
        KanjiDateRepBuilder b = new KanjiDateRepBuilder(date);
        formatter.apply(b);
        return b.build();
    }

	public static String toKanji(LocalDate date){
		return toKanji(date, kanjiFormatter1);
	}

	public static String toKanji(LocalDateTime datetime,
                                 Function<KanjiDateRepBuilder, KanjiDateRepBuilder> dateFormatter,
                                 Function<KanjiDateRepBuilder, KanjiDateRepBuilder> timeFormatter,
                                 String separator){
        KanjiDateRepBuilder b = new KanjiDateRepBuilder(datetime);
        if( dateFormatter != null ) {
            dateFormatter.apply(b);
        }
        b.str(separator);
        if( timeFormatter != null ) {
            timeFormatter.apply(b);
        }
        return b.build();
	}

    public static LocalDateTime parseSqlDateTime(String sqlDateTime) {
        return LocalDateTime.parse(sqlDateTime, sqlDateTimeFormatter);
    }

    public static LocalDate parseSqlDate(String sqlDate) {
        if (sqlDate.length() > 10) {
            sqlDate = sqlDate.substring(0, 10);
        }
        return LocalDate.parse(sqlDate, sqlDateFormatter);
    }

    public static LocalTime parseSqlTime(String sqlTime){
        return LocalTime.parse(sqlTime, sqlTimeFormatter);
    }

	public static String sqlDateToKanji(String sqlDate,
                                        Function<KanjiDateRepBuilder, KanjiDateRepBuilder> formatter){
        LocalDate date = parseSqlDate(sqlDate);
        return toKanji(date, formatter);
	}

    public static String sqlDateTimeToKanji(
            String sqlDateTime,
            Function<KanjiDateRepBuilder, KanjiDateRepBuilder> dateFormatter,
            Function<KanjiDateRepBuilder, KanjiDateRepBuilder> timeFormatter,
            String separator) {
        LocalDateTime dt = parseSqlDateTime(sqlDateTime);
        return toKanji(dt, dateFormatter, timeFormatter, separator);
    }

    public static String sqlDateTimeToKanji(
            String sqlDateTime,
            Function<KanjiDateRepBuilder, KanjiDateRepBuilder> dateFormatter,
            Function<KanjiDateRepBuilder, KanjiDateRepBuilder> timeFormatter) {
        return sqlDateTimeToKanji(sqlDateTime, dateFormatter, timeFormatter, " ");
    }

	public static String toSqlDateTime(LocalDateTime at){
		return at.format(sqlDateTimeFormatter);
	}

	public static String toPackedSqlDateTime(LocalDateTime at){
        return at.format(packedSqlDateTimeFormatter);
    }

    public static int calcAge(LocalDate birthday, LocalDate at) {
        return (int) birthday.until(at, ChronoUnit.YEARS);
    }

    public static int calcAge(LocalDate birthday) {
        return calcAge(birthday, LocalDate.now());
    }

    private static final String[] youbiKanji = new String[]{
            "日", "月", "火", "水", "木", "金", "土"
    };

    public static String youbiIndexToKanji(int index){
        if( index == 7 ){
            index = 0;
        }
        return youbiKanji[index];
    }

    public static String youbiAsKanji(DayOfWeek dow){
        return youbiIndexToKanji(dow.getValue());
    }
}