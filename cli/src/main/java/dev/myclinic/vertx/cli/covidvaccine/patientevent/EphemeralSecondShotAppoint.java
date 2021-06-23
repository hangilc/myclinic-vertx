package dev.myclinic.vertx.cli.covidvaccine.patientevent;

import dev.myclinic.vertx.cli.covidvaccine.CovidMisc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EphemeralSecondShotAppoint implements PatientEvent {

    public LocalDateTime at;
    private static final Pattern pat = Pattern.compile("E(\\d+-\\d+-\\d+)T(\\d+):(\\d+).*");

    public EphemeralSecondShotAppoint(LocalDateTime at) {
        this.at = at;
    }

    @Override
    public String encode() {
        return String.format("E%s", CovidMisc.encodeAppointTime(at));
    }

    public static EphemeralSecondShotAppoint decode(String src){
        Matcher m = pat.matcher(src);
        if( m.matches() ){
            LocalDateTime at = LocalDateTime.of(
                    LocalDate.parse(m.group(1)),
                    LocalTime.of(Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)))
            );
            return new EphemeralSecondShotAppoint(at);
        } else {
            throw new RuntimeException("Cannot convert to EphemeralSecondShotAppoint: " + src);
        }
    }

    @Override
    public PatientEvent copy() {
        return new EphemeralSecondShotAppoint(at);
    }
}
