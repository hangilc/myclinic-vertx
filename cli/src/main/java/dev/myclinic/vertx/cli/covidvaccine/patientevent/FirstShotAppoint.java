package dev.myclinic.vertx.cli.covidvaccine.patientevent;

import dev.myclinic.vertx.cli.covidvaccine.CovidMisc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FirstShotAppoint implements PatientEvent {
    public static Pattern oldPattern = Pattern.compile("(A\\d+-\\d+-\\d+T\\d+:\\d+)>(E\\d+-\\d+-\\d+T\\d+:\\d+)");
    public static Pattern pat = Pattern.compile("A(\\d+-\\d+-\\d+)T(\\d+):(\\d+)");
    public LocalDateTime at;

    public FirstShotAppoint(LocalDateTime at) {
        this.at = at;
    }

    @Override
    public FirstShotAppoint copy() {
        return new FirstShotAppoint(at);
    }

    @Override
    public String toString() {
        return "A" + CovidMisc.encodeAppointTime(at);
    }

    @Override
    public String encode() {
        return "A" + CovidMisc.encodeAppointTime(at);
    }

    public static FirstShotAppoint decode(String src) {
        Matcher m = FirstShotAppoint.pat.matcher(src);
        if (m.matches()) {
            LocalDateTime at = LocalDateTime.of(
                    LocalDate.parse(m.group(1)),
                    LocalTime.of(
                            Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3)))
            );
            return new FirstShotAppoint(at);
        } else {
            throw new RuntimeException("Cannot convert to FirstShotAppoint: " + src);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FirstShotAppoint that = (FirstShotAppoint) o;
        return at.equals(that.at);
    }

    @Override
    public int hashCode() {
        return Objects.hash(at);
    }
}
