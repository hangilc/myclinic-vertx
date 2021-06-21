package dev.myclinic.vertx.cli.covidvaccine;

import dev.myclinic.vertx.cli.covidvaccine.patientstate.EphemeralSecondShotAppoint;
import dev.myclinic.vertx.cli.covidvaccine.patientstate.FirstShotAppoint;
import dev.myclinic.vertx.cli.covidvaccine.patientstate.PatientState;
import dev.myclinic.vertx.cli.covidvaccine.patientstate.SecondShotAppoint;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

class AppointFrame {

    public AppointDate appointDate;
    private final List<AppointEntry> entries = new ArrayList<>();

    public AppointFrame(AppointDate appointDate) {
        this.appointDate = appointDate;
    }

    public AppointEntry findByPatientId(int patientId) {
        for (AppointEntry entry : entries) {
            if (entry.patient.patientId == patientId) {
                return entry;
            }
        }
        return null;
    }

    public boolean isFull() {
        return entries.size() >= appointDate.capacity;
    }

    public void addEntry(PatientCalendar patientCalendar, RegularPatient patient){
        if( isFull() ){
            throw new RuntimeException("Overbooking! " + appointDate.at);
        }
        entries.add(new AppointEntry(patientCalendar, patient));
    }

    public List<AppointEntry> getEntries(){
        return new ArrayList<>(entries);
    }

    public enum PatientCalendar {
        FirstAppoint,
        FirstDone,
        TemporarySecondAppoint,
        SecondAppoint,
        SecondDone
    }

    static class AppointEntry {
        PatientCalendar patientCalendar;
        RegularPatient patient;

        public AppointEntry(PatientCalendar patientCalendar, RegularPatient patient) {
            this.patientCalendar = patientCalendar;
            this.patient = patient;
        }

        private static String calendarLabel(PatientCalendar calendar) {
            switch (calendar) {
                case FirstAppoint:
                    return "１回目予約";
                case FirstDone:
                    return "１回目接種済";
                case TemporarySecondAppoint:
                    return "２回目暫定予約";
                case SecondAppoint:
                    return "２回目予約";
                case SecondDone:
                    return "２回目接種済";
                default:
                    throw new RuntimeException("Unknown calendar state: " + calendar);
            }
        }

        @Override
        public String toString() {
            return String.format("%s %d %s", calendarLabel(patientCalendar),
                    patient.patientId, patient.name);
        }
    }

//    public static Map<LocalDateTime, AppointFrame> readFromLogs(List<String> logs) {
//        Map<LocalDateTime, AppointFrame> result = new LinkedHashMap<>();
//        CovidVaccine.readAppointDates().forEach(appointDate -> {
//            result.put(appointDate.at, new AppointFrame(appointDate));
//        });
//        CovidVaccine.executeLogbook(logs, (regularPatient, stateChanged) -> {
//            PatientState state = regularPatient.state;
//            if (state instanceof FirstShotAppoint && stateChanged) {
//                FirstShotAppoint firstShotAppoint = (FirstShotAppoint) state;
//                AppointFrame frame = result.get(firstShotAppoint.at);
//                if (frame == null) {
//                    throw new RuntimeException("Invalid appoint date: " + firstShotAppoint);
//                }
//                if (frame.isFull()) {
//                    frame.getEntries().forEach(e -> {
//                        System.out.println(e.patient);
//                    });
//                    System.out.println(regularPatient);
//                    throw new RuntimeException("Overbooking! " + firstShotAppoint.at);
//                }
//                AppointEntry entry = frame.findByPatientId(regularPatient.patientId);
//                if (entry == null) {
//                    frame.entries.add(new AppointEntry(PatientCalendar.FirstAppoint, regularPatient));
//                } else {
//                    entry.patient = regularPatient;
//                }
//            } else if (state instanceof SecondShotAppoint && stateChanged) {
//                SecondShotAppoint secondShotAppoint = (SecondShotAppoint) state;
//                AppointFrame frame = result.get(secondShotAppoint.at);
//                if (frame == null) {
//                    throw new RuntimeException("Invalid appoint date: " + secondShotAppoint);
//                }
//                if (frame.isFull()) {
//                    throw new RuntimeException("Overbooking! " + secondShotAppoint.at);
//                }
//                AppointEntry entry = frame.findByPatientId(regularPatient.patientId);
//                if (entry != null) {
//                    throw new RuntimeException("Duplicate appointments (2nd): " + regularPatient);
//                }
//                frame.entries.add(new AppointEntry(PatientCalendar.SecondAppoint, regularPatient));
//            } else if (state instanceof EphemeralSecondShotAppoint && stateChanged) {
//                EphemeralSecondShotAppoint estate = (EphemeralSecondShotAppoint) state;
//                AppointFrame frame = result.get(estate.at);
//                if (frame == null) {
//                    throw new RuntimeException("Invalid appoint date: " + estate.at);
//                }
//                if (frame.isFull()) {
//                    throw new RuntimeException("Overbooking! " + estate.at);
//                }
//                AppointEntry entry = frame.findByPatientId(regularPatient.patientId);
//                if (entry != null) {
//                    throw new RuntimeException("Duplicate appointments (ephemeral 2nd): " + regularPatient);
//                }
//                frame.entries.add(new AppointEntry(PatientCalendar.TemporarySecondAppoint, regularPatient));
//            }
//        });
//        return result;
//    }

//    public static LocalDateTime findVacancy(LocalDate start, Map<LocalDateTime, AppointFrame> calendar,
//                                            Function<LocalDateTime, Boolean> acceptable) {
//        for (LocalDateTime at : calendar.keySet()) {
//            if (at.toLocalDate().isBefore(start)) {
//                continue;
//            }
//            AppointFrame frame = calendar.get(at);
//            if (frame.isFull() || !acceptable.apply(at)) {
//                continue;
//            }
//            return at;
//        }
//        return null;
//    }

}
