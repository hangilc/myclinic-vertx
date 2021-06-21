package dev.myclinic.vertx.cli.covidvaccine.patientevent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecondShotCandidate implements PatientEvent, Appointable {
    public static Pattern pat = Pattern.compile("S(\\d\\d)(\\d\\d)");
    public LocalDate firstShotDate;

    public SecondShotCandidate(LocalDate firstShotDate) {
        this.firstShotDate = firstShotDate;
    }

    @Override
    public PatientEvent registerAppoint(LocalDateTime at) {
        LocalDate dueDate = firstShotDate.plus(21, ChronoUnit.DAYS);
        if (at.toLocalDate().isBefore(dueDate)) {
            throw new RuntimeException("Too early second shot appointment.");
        }
        return new SecondShotAppoint(at);
    }

    @Override
    public String encode() {
        return String.format("S%02d%02d", firstShotDate.getMonthValue(), firstShotDate.getDayOfMonth());
    }

    @Override
    public PatientEvent copy() {
        return new SecondShotCandidate(firstShotDate);
    }

    public static SecondShotCandidate decode(String src){
        Matcher m = SecondShotCandidate.pat.matcher(src);
        if (m.matches()) {
            int month = Integer.parseInt(m.group(1));
            int day = Integer.parseInt(m.group(2));
            int year = LocalDate.now().getYear();
            return new SecondShotCandidate(LocalDate.of(year, month, day));
        } else {
            throw new RuntimeException("Cannot convert to SecondShotCandidate: " + src);
        }
    }
}
