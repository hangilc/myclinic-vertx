package dev.myclinic.vertx.cli.covidvaccine.patientstate;

public class NotCurrentCandidate implements PatientState {
    @Override
    public String encode() {
        return "x";
    }

    public static NotCurrentCandidate decode(String src){
        if( "x".equals(src) ){
            return new NotCurrentCandidate();
        } else {
            throw new RuntimeException("Cannot convert to NotCurrentCandidate: " + src);
        }
    }
}
