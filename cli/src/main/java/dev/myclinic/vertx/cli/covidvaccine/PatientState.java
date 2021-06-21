package dev.myclinic.vertx.cli.covidvaccine;

import dev.myclinic.vertx.cli.covidvaccine.patientevent.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class PatientState {

    enum FirstShotState {
        None,
        Appointed,
        External,
        Done
    }

    enum SecondShotState {
        None,
        Ephemeral,
        Appointed,
        External,
        Done
    }

    public static String renderFirstShotState(FirstShotState state){
        switch(state){
            case None: return "(None)";
            case Appointed: return "１回目予約";
            case External: return "１回目外部接種";
            case Done: return "１回目接種済";
            default: return "(Unkown)";
        }
    }

    public static String renderSecondShotState(SecondShotState state){
        switch(state){
            case None: return "(None)";
            case Ephemeral: return "２回目暫定予約";
            case Appointed: return "２回目予約";
            case External: return "２回目外部接種";
            case Done: return "２回目接種済";
            default: return "(Unkown)";
        }
    }

    public FirstShotState firstShotState = FirstShotState.None;
    public LocalDateTime firstShotTime;
    public SecondShotState secondShotState = SecondShotState.None;
    public LocalDateTime secondShotTime;

    public void apply(PatientEvent e){
        if( e instanceof FirstShotAppoint){
            FirstShotAppoint evt = (FirstShotAppoint) e;
            firstShotState = FirstShotState.Appointed;
            firstShotTime = evt.at;
            if( evt.tmpSecondAppoint != null ){
                secondShotState = SecondShotState.Ephemeral;
                secondShotTime = evt.tmpSecondAppoint;
            }
        } else if( e instanceof FirstShotDone){
            firstShotState = FirstShotState.Done;
        } else if( e instanceof FirstShotCancel ) {
            if (firstShotState == FirstShotState.Appointed) {
                firstShotState = FirstShotState.None;
                firstShotTime = null;
            } else {
                throw new RuntimeException("Cannot apply cancel first shot to " + firstShotState);
            }
        } else if( e instanceof SecondShotCandidate ){
            SecondShotCandidate evt = (SecondShotCandidate) e;
            firstShotState = FirstShotState.External;
            firstShotTime = LocalDateTime.of(evt.firstShotDate, LocalTime.of(0, 0));
        } else if( e instanceof EphemeralSecondShotAppoint ){
            EphemeralSecondShotAppoint evt = (EphemeralSecondShotAppoint) e;
            secondShotState = SecondShotState.Ephemeral;
            secondShotTime = evt.at;
        } else if( e instanceof SecondShotAppoint ){
            SecondShotAppoint evt = (SecondShotAppoint) e;
            secondShotState = SecondShotState.Appointed;
            secondShotTime = evt.at;
        } else if( e instanceof Done ){
            secondShotState = SecondShotState.Done;
        } else if( e instanceof SecondShotCancel ){
            secondShotState = SecondShotState.None;
            secondShotTime = null;
        } else if( e instanceof SecondShotExternal ){
            secondShotState = SecondShotState.External;
            secondShotTime = null;
        }
    }

}
