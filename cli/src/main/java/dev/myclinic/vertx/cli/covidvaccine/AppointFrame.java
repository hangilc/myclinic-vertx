package dev.myclinic.vertx.cli.covidvaccine;

import dev.myclinic.vertx.cli.covidvaccine.patientstate.FirstShotAppoint;
import dev.myclinic.vertx.cli.covidvaccine.patientstate.PatientState;
import dev.myclinic.vertx.cli.covidvaccine.patientstate.SecondShotAppoint;

import java.time.LocalDateTime;
import java.util.*;

class AppointFrame {

    public AppointDate appointDate;
    public List<AppointEntry> entries = new ArrayList<>();

    public AppointFrame(AppointDate appointDate) {
        this.appointDate = appointDate;
    }

    public AppointEntry findByPatientId(int patientId){
        for(AppointEntry entry: entries){
            if( entry.patient.patientId == patientId ){
                return entry;
            }
        }
        return null;
    }

    public boolean isFull(){
        return entries.size() >= appointDate.capacity;
    }

    enum PatientCalendar {
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

    public static Map<LocalDateTime, AppointFrame> readFromLogs(List<String> logs) {
        Map<LocalDateTime, AppointFrame> result = new LinkedHashMap<>();
        CovidVaccine.readAppointDates().forEach(appointDate -> {
            result.put(appointDate.at, new AppointFrame(appointDate));
        });
        CovidVaccine.executeLogbook(logs, (regularPatient, stateChanged) -> {
            PatientState state = regularPatient.state;
            if (state instanceof FirstShotAppoint && stateChanged) {
                FirstShotAppoint firstShotAppoint = (FirstShotAppoint) state;
                AppointFrame frame = result.get(firstShotAppoint.at);
                if( frame == null ){
                    throw new RuntimeException("Invalid appoint date: " + firstShotAppoint);
                }
                if( frame.isFull() ){
                    throw new RuntimeException("Overbooking! " + firstShotAppoint.at);
                }
                AppointEntry entry = frame.findByPatientId(regularPatient.patientId);
                if (entry != null) {
                    throw new RuntimeException("Duplicate appointments (1st): " + regularPatient);
                }
                frame.entries.add(new AppointEntry(PatientCalendar.FirstAppoint, regularPatient));
            } else if (state instanceof SecondShotAppoint && stateChanged) {
                SecondShotAppoint secondShotAppoint = (SecondShotAppoint) state;
                AppointFrame frame = result.get(secondShotAppoint.at);
                if( frame == null ){
                    throw new RuntimeException("Invalid appoint date: " + secondShotAppoint);
                }
                if( frame.isFull() ){
                    throw new RuntimeException("Overbooking! " + secondShotAppoint.at);
                }
                AppointEntry entry = frame.findByPatientId(regularPatient.patientId);
                if (entry != null) {
                    throw new RuntimeException("Duplicate appointments (2nd): " + regularPatient);
                }
                frame.entries.add(new AppointEntry(PatientCalendar.SecondAppoint, regularPatient));
            }
        });
        return result;
    }

}
