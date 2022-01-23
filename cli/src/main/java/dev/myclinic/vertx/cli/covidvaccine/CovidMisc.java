package dev.myclinic.vertx.cli.covidvaccine;

import dev.myclinic.vertx.cli.Misc;
import dev.myclinic.vertx.cli.covidvaccine.logentry.AddPatientLog;
import dev.myclinic.vertx.cli.covidvaccine.logentry.LogEntry;
import dev.myclinic.vertx.cli.covidvaccine.logentry.PhoneLog;
import dev.myclinic.vertx.cli.covidvaccine.logentry.StateLog;
import dev.myclinic.vertx.cli.covidvaccine.patientevent.*;
import dev.myclinic.vertx.dto.PatientDTO;
import static dev.myclinic.vertx.cli.covidvaccine.CovidVaccine.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CovidMisc {

    public static RegularPatient patientDtoToRegularPatient(PatientDTO patient){
        int age = Misc.ageAt(LocalDate.parse(patient.birthday), LocalDate.of(2022, 3, 31));
        return new RegularPatient(patient.patientId,
                patient.lastName + patient.firstName,
                age, patient.phone, new NeedConfirm());
    }

    public static Patient regularPatientToPatient(RegularPatient patient){
        return new Patient(
                patient.patientId,
                patient.name,
                patient.age,
                patient.phone
        );
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

    public static LogEntry patchCommandToLogEntry(PatchCommand patch){
        if( patch instanceof PatchAdd ){
            PatchAdd patchAdd = (PatchAdd) patch;
            Patient patient = regularPatientToPatient(patchAdd.patient);
            return new AddPatientLog(patient);
        } else if( patch instanceof PatchState ){
            PatchState patchState = (PatchState) patch;
            return new StateLog(patchState.patientId, parsePatientAttr(patchState.attr));
        } else if( patch instanceof PatchPhone ){
            PatchPhone patchPhone = (PatchPhone) patch;
            return new PhoneLog(patchPhone.patientId, patchPhone.phone);
        } else {
            throw new RuntimeException("Unknown patch command: " + patch);
        }
    }

    public static LocalDate tryParseDate(String src){
        Pattern pat = Pattern.compile("((\\d+)-)?(\\d+)-(\\d+)");
        Matcher m = pat.matcher(src);
        if( m.matches() ){
            int month = Integer.parseInt(m.group(3));
            int day = Integer.parseInt(m.group(4));
            int year;
            if( m.group(1) == null ){
                year = LocalDate.now().getYear();
                if( month == 12 || month == 11 ){
                    year += 1;
                }
            } else {
                year = Integer.parseInt(m.group(2));
            }
            return LocalDate.of(year, month, day);
        } else {
            return null;
        }
    }
}
