package dev.myclinic.vertx.cli.covidvaccine.patientstate;

import dev.myclinic.vertx.cli.covidvaccine.CovidMisc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EphemeralSecondShotAppoint implements PatientState {

    public LocalDateTime at;
    public LocalDate firstShotAt;
    private static final Pattern pat = Pattern.compile("E(\\d+-\\d+-\\d+)T(\\d+):(\\d+)<(\\d+-\\d+-\\d+)");

    public EphemeralSecondShotAppoint(LocalDateTime at, LocalDate firstShotAt) {
        this.at = at;
        this.firstShotAt = firstShotAt;
    }

    @Override
    public String encode() {
        return String.format("E%s<%s", CovidMisc.encodeAppointTime(at), firstShotAt.toString());
    }

    public static EphemeralSecondShotAppoint decode(String src){
        Matcher m = pat.matcher(src);
        if( m.matches() ){
            LocalDateTime at = LocalDateTime.of(
                    LocalDate.parse(m.group(1)),
                    LocalTime.of(Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)))
            );
            LocalDate firstShotAt = LocalDate.parse(m.group(4));
            return new EphemeralSecondShotAppoint(at, firstShotAt);
        } else {
            throw new RuntimeException("Cannot convert to EphemeralSecondShotAppoint: " + src);
        }
    }

    @Override
    public PatientState copy() {
        return new EphemeralSecondShotAppoint(at, firstShotAt);
    }
}
