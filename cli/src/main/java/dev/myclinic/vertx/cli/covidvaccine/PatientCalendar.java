package dev.myclinic.vertx.cli.covidvaccine;

import java.util.List;

public class PatientCalendar {

    public enum State {
        FirstShotAppoint, FirstShotDone, SecondShotAppoint, SecondShotDone
    }

    public int patientId;
    public State state;

    public PatientCalendar(int patientId, State state) {
        this.patientId = patientId;
        this.state = state;
    }

    public String stateLabel(){
        switch(state){
            case FirstShotAppoint: return "１回目予約";
            case FirstShotDone: return "１回目接種済";
            case SecondShotAppoint: return "２回目予約";
            case SecondShotDone: return "２回目接種済";
            default: throw new RuntimeException("Unknown state: " + state);
        }
    }

    public static PatientCalendar findByPatientId(int patientId, List<PatientCalendar> list){
        for(PatientCalendar cal: list){
            if( cal.patientId == patientId ){
                return cal;
            }
        }
        return null;
    }

}
