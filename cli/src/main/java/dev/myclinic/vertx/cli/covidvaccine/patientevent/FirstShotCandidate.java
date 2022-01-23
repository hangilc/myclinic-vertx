package dev.myclinic.vertx.cli.covidvaccine.patientevent;

import java.time.LocalDateTime;

public class FirstShotCandidate implements PatientEvent, Appointable {
    @Override
    public PatientEvent registerAppoint(LocalDateTime at) {
        return new FirstShotAppoint(at);
    }

    @Override
    public String encode() {
        return "C";
    }

    @Override
    public PatientEvent copy() {
        return new FirstShotCandidate();
    }

    public static FirstShotCandidate decode(String src){
        if( "C".equals(src) ){
            return new FirstShotCandidate();
        } else {
            throw new RuntimeException("Cannot convert to FirstShotCandidate: " + src);
        }
    }
}
