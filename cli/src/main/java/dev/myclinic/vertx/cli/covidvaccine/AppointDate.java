package dev.myclinic.vertx.cli.covidvaccine;

import dev.myclinic.vertx.cli.Misc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class AppointDate {
    public LocalDateTime at;
    public int capacity;

    public AppointDate(LocalDateTime at, int capacity) {
        this.at = at;
        this.capacity = capacity;
    }

    private static final Pattern pat = Pattern.compile("^(\\d+-\\d+-\\d+)\\s+(\\d+):(\\d+)\\s+(\\d+).*");

    public static AppointDate parse(String line) {
        Matcher m = pat.matcher(line);
        if (m.matches()) {
            LocalDateTime at = LocalDateTime.of(
                    LocalDate.parse(m.group(1)),
                    LocalTime.of(Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3))));
            return new AppointDate(
                    at,
                    Integer.parseInt(m.group(4))
            );
        } else {
            throw new RuntimeException("Invalid appoint date line: " + line);
        }
    }

    @Override
    public String toString() {
        return String.format("%s %02d時%02d分 %d名",
                Misc.localDateToKanji(at.toLocalDate(), true, true),
                at.getHour(),
                at.getMinute(),
                capacity);
    }
}
