package dev.myclinic.vertx.util.kanjidate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class KanjiDateRepBuilder {

    private LocalDate date;
    private LocalTime time;
    private Gengou gengou;
    private int nen;
    private StringBuilder stringBuilder = new StringBuilder();

    public static DateTimeFormatter sqlDateFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd");
    public static DateTimeFormatter sqlDateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");

    public KanjiDateRepBuilder(LocalDate date) {
        this.date = date;
        this.time = null;
        GengouNenPair gn = KanjiDate.yearToGengou(date);
        this.gengou = gn.gengou;
        this.nen = gn.nen;
    }

    public KanjiDateRepBuilder(LocalDateTime dateTime) {
        this(dateTime.toLocalDate());
        this.time = dateTime.toLocalTime();
    }

    public String build() {
        return stringBuilder.toString();
    }

    public KanjiDateRepBuilder gengou() {
        stringBuilder.append(gengou.getKanjiRep());
        return this;
    }

    public KanjiDateRepBuilder gengouAlphaShort() {
        stringBuilder.append(gengou.getAlphaRep().substring(0, 1));
        return this;
    }

    public KanjiDateRepBuilder nen() {
        return nen("%d");
    }

    public KanjiDateRepBuilder nen(String fmt) {
        stringBuilder.append(String.format(fmt, nen));
        return this;
    }

    public KanjiDateRepBuilder month() {
        return month("%d");
    }

    public KanjiDateRepBuilder month(String fmt) {
        stringBuilder.append(String.format(fmt, date.getMonthValue()));
        return this;
    }

    public KanjiDateRepBuilder day() {
        return day("%d");
    }

    public KanjiDateRepBuilder day(String fmt) {
        stringBuilder.append(String.format(fmt, date.getDayOfMonth()));
        return this;
    }

    public KanjiDateRepBuilder youbiShort() {
        String youbi = toYoubi(date.getDayOfWeek());
        return str(youbi.substring(0, 1));
    }

    public KanjiDateRepBuilder hour() {
        return hour("%d");
    }

    public KanjiDateRepBuilder hour(String fmt) {
        stringBuilder.append(String.format(fmt, time.getHour()));
        return this;
    }

    public KanjiDateRepBuilder minute() {
        return minute("%d");
    }

    public KanjiDateRepBuilder minute(String fmt) {
        stringBuilder.append(String.format(fmt, time.getMinute()));
        return this;
    }

    public KanjiDateRepBuilder second() {
        return second("%d");
    }

    public KanjiDateRepBuilder second(String fmt) {
        stringBuilder.append(String.format(fmt, time.getSecond()));
        return this;
    }

    public KanjiDateRepBuilder str(String str) {
        stringBuilder.append(str);
        return this;
    }

    public KanjiDateRepBuilder sqldate() {
        stringBuilder.append(date.format(sqlDateFormatter));
        return this;
    }

    public KanjiDateRepBuilder sqldatetime() {
        if (date != null && time != null) {
            LocalDateTime datetime = LocalDateTime.of(date, time);
            stringBuilder.append(datetime.format(sqlDateTimeFormatter));
        } else if (date != null) {
            LocalDateTime datetime = LocalDateTime.of(date, LocalTime.of(0, 0, 0));
            stringBuilder.append(datetime.format(sqlDateTimeFormatter));
        } else {
            throw new IllegalArgumentException("Cannot convert to LocalDateTime.");
        }
        return this;
    }

    // format1: "Gy年M月d日"
    public KanjiDateRepBuilder format1() {
        return gengou().nen().str("年").month().str("月").day().str("日");
    }

    // format2: "Gyy年MM月dd日"
    public KanjiDateRepBuilder format2() {
        return gengou().nen("%02d").str("年").month("%02d").str("月").day("%02d").str("日");
    }

    // format3: "Gyy年MM月dd日（E）"
    public KanjiDateRepBuilder format3() {
        format2();
        str("（");
        youbiShort();
        str("）");
        return this;
    }

    // format4: "HH時mm分"
    public KanjiDateRepBuilder format4() {
        if (time != null) {
            return hour("%02d").str("時").minute("%02d").str("分");
        } else {
            throw new IllegalArgumentException("Cannot format null time.");
        }
    }

    // format5: "GGGGGy.M.d"
    public KanjiDateRepBuilder format5() {
        return gengouAlphaShort().nen().str(".").month().str(".").day();
    }

    // format6: "H時m分"
    public KanjiDateRepBuilder format6(){
        if( time != null ){
            return hour().str("時").minute().str("分");
        } else {
            throw new IllegalArgumentException("Cannot format null time.");
        }
    }

    // format7: "Gy年M月d日（E）"
    public KanjiDateRepBuilder format7() {
        format1();
        str("（");
        youbiShort();
        str("）");
        return this;
    }

    private String toYoubi(DayOfWeek dow) {
        switch (dow) {
            case SUNDAY:
                return "日曜";
            case MONDAY:
                return "月曜";
            case TUESDAY:
                return "火曜";
            case WEDNESDAY:
                return "水曜";
            case THURSDAY:
                return "木曜";
            case FRIDAY:
                return "金曜";
            case SATURDAY:
                return "土曜";
            default:
                throw new IllegalArgumentException("Cannot convert to youbi.");
        }
    }
}
