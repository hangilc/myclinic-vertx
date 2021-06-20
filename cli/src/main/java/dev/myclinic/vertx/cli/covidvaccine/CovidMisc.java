package dev.myclinic.vertx.cli.covidvaccine;

import dev.myclinic.vertx.cli.Misc;
import dev.myclinic.vertx.cli.covidvaccine.patientstate.*;
import dev.myclinic.vertx.dto.PatientDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.regex.Matcher;

public class CovidMisc {

    public static RegularPatient patientToRegularPatient(PatientDTO patient){
        int age = Misc.ageAt(LocalDate.parse(patient.birthday), LocalDate.of(2022, 3, 31));
        return new RegularPatient(patient.patientId,
                patient.lastName + patient.firstName,
                age, patient.phone, new NeedConfirm());
    }

    public static String encodeAppointTime(LocalDateTime at) {
        return String.format("%d-%02d-%02dT%02d:%02d", at.getYear(), at.getMonthValue(), at.getDayOfMonth(),
                at.getHour(), at.getMinute());
    }

    public static PatientState parsePatientAttr(String attr) {
        attr = attr.trim();
        if (attr.length() > 0) {
            switch (attr.charAt(0)) {
                case 'C':
                    return new FirstShotCandidate();
                case 'x':
                    return new NotCurrentCandidate();
                case 'P':
                    return new WaitingReply();
                case '*':
                    return new NeedConfirm();
                case 'T':
                    return new DoneAtOtherPlace();
                case 'U':
                    return new Under65();
                case 'S': {
                    Matcher m = SecondShotCandidate.pat.matcher(attr);
                    if (m.matches()) {
                        int month = Integer.parseInt(m.group(1));
                        int day = Integer.parseInt(m.group(2));
                        int year = LocalDate.now().getYear();
                        return new SecondShotCandidate(LocalDate.of(year, month, day));
                    }
                    break;
                }
                case 'A': {
                    Matcher m = FirstShotAppoint.pat.matcher(attr);
                    if (m.matches()) {
                        LocalDateTime at = LocalDateTime.of(
                                LocalDate.parse(m.group(1)),
                                LocalTime.of(
                                        Integer.parseInt(m.group(2)),
                                        Integer.parseInt(m.group(3)))
                        );
                        return new FirstShotAppoint(at);
                    }
                    break;
                }
                case 'B': {
                    Matcher m = SecondShotAppoint.pat.matcher(attr);
                    if (m.matches()) {
                        LocalDateTime at = LocalDateTime.of(
                                LocalDate.parse(m.group(1)),
                                LocalTime.of(
                                        Integer.parseInt(m.group(2)),
                                        Integer.parseInt(m.group(3)))
                        );
                        return new SecondShotAppoint(at);
                    }
                    break;
                }
                case 'D': {
                    return new Done();
                }
            }
        }
        throw new RuntimeException("Invalid attribute: " + attr);
    }
}
