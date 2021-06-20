package dev.myclinic.vertx.cli.covidvaccine.patientstate;

import dev.myclinic.vertx.cli.covidvaccine.CovidMisc;
import dev.myclinic.vertx.cli.covidvaccine.CovidVaccine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FirstShotAppoint implements PatientState, Appointable {
    public static Pattern pat = Pattern.compile("A(\\d+-\\d+-\\d+)T(\\d+):(\\d+)(:E(\\d+-\\d+-\\d+)T(\\d+):(\\d+))?");
    public LocalDateTime at;
    public LocalDateTime tmpSecondAppoint;

    public FirstShotAppoint(LocalDateTime at) {
        this(at, null);
    }

    public FirstShotAppoint(LocalDateTime at, LocalDateTime tmpSecondAppoint) {
        this.at = at;
        this.tmpSecondAppoint = tmpSecondAppoint;
    }

    @Override
    public PatientState copy() {
        return new FirstShotAppoint(at, tmpSecondAppoint);
    }

    @Override
    public String toString() {
        return "A" + CovidMisc.encodeAppointTime(at);
    }

    @Override
    public PatientState registerAppoint(LocalDateTime registerAt) {
        LocalDate due = at.toLocalDate().plus(21, ChronoUnit.DAYS);
        if (registerAt.toLocalDate().equals(due) || registerAt.toLocalDate().isAfter(due)) {
            return new SecondShotAppoint(registerAt);
        } else {
            throw new RuntimeException("Cannot put appointment at " + registerAt);
        }
    }

    @Override
    public String encode() {
        if( tmpSecondAppoint == null ) {
            return "A" + CovidMisc.encodeAppointTime(at);
        } else {
            return "A" + CovidMisc.encodeAppointTime(at) + ":E" + CovidMisc.encodeAppointTime(tmpSecondAppoint);
        }
    }

    public static FirstShotAppoint decode(String src){
        Matcher m = FirstShotAppoint.pat.matcher(src);
        if (m.matches()) {
            LocalDateTime at = LocalDateTime.of(
                    LocalDate.parse(m.group(1)),
                    LocalTime.of(
                            Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3)))
            );
            LocalDateTime tmp = null;
            if( m.group(4) != null ){
                tmp = LocalDateTime.of(
                        LocalDate.parse(m.group(5)),
                        LocalTime.of(
                                Integer.parseInt(m.group(6)),
                                Integer.parseInt(m.group(7)))
                );
            }
            return new FirstShotAppoint(at, tmp);
        } else {
            throw new RuntimeException("Cannot convert to FirstShotAppoint: " + src);
        }
    }

}
