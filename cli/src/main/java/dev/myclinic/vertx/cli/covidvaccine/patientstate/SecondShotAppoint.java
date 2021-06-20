package dev.myclinic.vertx.cli.covidvaccine.patientstate;

import dev.myclinic.vertx.cli.covidvaccine.CovidMisc;
import dev.myclinic.vertx.cli.covidvaccine.CovidVaccine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecondShotAppoint implements PatientState {
    public static Pattern pat = Pattern.compile("B(\\d+-\\d+-\\d+)T(\\d+):(\\d+)");
    public LocalDateTime at;

    public SecondShotAppoint(LocalDateTime at) {
        this.at = at;
    }

    @Override
    public String toString() {
        return "B" + CovidMisc.encodeAppointTime(at);
    }

    @Override
    public String encode() {
        return "B" + CovidMisc.encodeAppointTime(at);
    }

    @Override
    public PatientState copy() {
        return new SecondShotAppoint(at);
    }

    public static SecondShotAppoint decode(String src){
        Matcher m = SecondShotAppoint.pat.matcher(src);
        if (m.matches()) {
            LocalDateTime at = LocalDateTime.of(
                    LocalDate.parse(m.group(1)),
                    LocalTime.of(
                            Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3)))
            );
            return new SecondShotAppoint(at);
        } else {
            throw new RuntimeException("Cannot convert to SecondShotAppoint: " + src);
        }
    }

}
