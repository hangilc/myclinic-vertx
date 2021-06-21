package dev.myclinic.vertx.cli.covidvaccine;

import dev.myclinic.vertx.cli.covidvaccine.patientstate.EphemeralSecondShotAppoint;
import dev.myclinic.vertx.cli.covidvaccine.patientstate.FirstShotAppoint;
import dev.myclinic.vertx.cli.covidvaccine.patientstate.PatientState;
import dev.myclinic.vertx.cli.covidvaccine.patientstate.SecondShotAppoint;

import static dev.myclinic.vertx.cli.covidvaccine.AppointFrame.AppointEntry;
import static dev.myclinic.vertx.cli.covidvaccine.AppointFrame.PatientCalendar;
import static dev.myclinic.vertx.cli.covidvaccine.AppointFrame.PatientCalendar.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class AppointCalendar {

    private final Map<LocalDateTime, AppointFrame> cal = new LinkedHashMap<>();

    public void init(List<String> logs, List<AppointDate> appointDates){
        appointDates.forEach(appointDate -> {
            cal.put(appointDate.at, new AppointFrame(appointDate));
        });
        CovidVaccine.executeLogbook(logs, (patient, stateChanged) -> {
            if( !stateChanged ){
                return;
            }
            PatientState state = patient.state;
            if( state instanceof FirstShotAppoint ){
                handleFirstShotAppoint((FirstShotAppoint) state, patient);
            } else if( state instanceof SecondShotAppoint ){
                handleSecondShotAppoint((SecondShotAppoint) state, patient);
            } else if( state instanceof EphemeralSecondShotAppoint ){
                handleEphemeralSecondShotAppoint((EphemeralSecondShotAppoint) state, patient);
            }
        });
    }

    public List<AppointFrame> listFrames(){
        List<AppointFrame> result = new ArrayList<>();
        for(LocalDateTime at: cal.keySet()){
            result.add(cal.get(at));
        }
        return result;
    }

    public AppointFrame getFrame(LocalDateTime at){
        AppointFrame frame = cal.get(at);
        if( frame == null ){
            throw new RuntimeException("Invalid appoint time: " + at);
        }
        return frame;
    }

    public LocalDateTime findVacancy(LocalDate start, Function<LocalDateTime, Boolean> acceptable) {
        for (LocalDateTime at : cal.keySet()) {
            if (at.toLocalDate().isBefore(start)) {
                continue;
            }
            AppointFrame frame = cal.get(at);
            if (frame.isFull() || !acceptable.apply(at)) {
                continue;
            }
            return at;
        }
        return null;
    }
    public void setEntry(LocalDateTime at, PatientCalendar patientCalendar, RegularPatient patient){
        AppointFrame frame = getFrame(at);
        AppointEntry entry = frame.findByPatientId(patient.patientId);
        if( entry != null ){
            if( entry.patientCalendar != patientCalendar ){
                throw new RuntimeException("Inconsistent appointment. " + patient);
            }
            entry.patient = patient;
        } else {
            frame.addEntry(patientCalendar, patient);
        }
    }

    private void handleFirstShotAppoint(FirstShotAppoint state, RegularPatient patient){
        setEntry(state.at, FirstAppoint, patient);
        if( state.tmpSecondAppoint != null ){
            setEntry(state.tmpSecondAppoint, TemporarySecondAppoint, patient);
        }
    }

    private void handleSecondShotAppoint(SecondShotAppoint state, RegularPatient patient){
        setEntry(state.at, SecondAppoint, patient);
    }

    private void handleEphemeralSecondShotAppoint(EphemeralSecondShotAppoint state, RegularPatient patient){
        setEntry(state.at, TemporarySecondAppoint, patient);
    }

}
