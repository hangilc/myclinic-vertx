package dev.myclinic.vertx.cli.covidvaccine.patientstate;

import java.time.LocalDateTime;

public class FirstShotCandidate implements PatientState, Appointable {
    @Override
    public PatientState registerAppoint(LocalDateTime at) {
        return new FirstShotAppoint(at);
    }

    @Override
    public String encode() {
        return "C";
    }

    public static FirstShotCandidate decode(String src){
        if( "C".equals(src) ){
            return new FirstShotCandidate();
        } else {
            throw new RuntimeException("Cannot convert to FirstShotCandidate: " + src);
        }
    }
}
