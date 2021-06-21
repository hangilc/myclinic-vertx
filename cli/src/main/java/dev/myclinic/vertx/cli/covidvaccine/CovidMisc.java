package dev.myclinic.vertx.cli.covidvaccine;

import dev.myclinic.vertx.cli.Misc;
import dev.myclinic.vertx.cli.covidvaccine.patientevent.*;
import dev.myclinic.vertx.dto.PatientDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    public static PatientEvent parsePatientAttr(String attr) {
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
                case 'S':
                    return SecondShotCandidate.decode(attr);
                case 'A':
                    return FirstShotAppoint.decode(attr);
                case 'B':
                    return SecondShotAppoint.decode(attr);
                case 'D':
                    return new Done();
                case 'F':
                    return new FirstShotDone();
                case 'G': return new FirstShotCancel();
                case 'H': return new SecondShotCancel();
                case 'I': return new SecondShotExternal();
                case 'K':
                    return new Kakaritsuke();
                case 'E': return EphemeralSecondShotAppoint.decode(attr);
            }
        }
        throw new RuntimeException("Invalid attribute: " + attr);
    }
}
