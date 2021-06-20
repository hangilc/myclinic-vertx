package dev.myclinic.vertx.cli.covidvaccine.patientstate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

public class SecondShotCandidate implements PatientState, Appointable {
    public static Pattern pat = Pattern.compile("S(\\d\\d)(\\d\\d)");
    public LocalDate firstShotDate;

    public SecondShotCandidate(LocalDate firstShotDate) {
        this.firstShotDate = firstShotDate;
    }

    @Override
    public PatientState registerAppoint(LocalDateTime at) {
        LocalDate dueDate = firstShotDate.plus(21, ChronoUnit.DAYS);
        if (at.toLocalDate().isBefore(dueDate)) {
            throw new RuntimeException("Too early second shot appointment.");
        }
        return new SecondShotAppoint(at);
    }

    @Override
    public String encode() {
        throw new RuntimeException("not implemented");
    }
}
