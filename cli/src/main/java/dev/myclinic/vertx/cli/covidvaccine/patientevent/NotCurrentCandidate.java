package dev.myclinic.vertx.cli.covidvaccine.patientevent;

public class NotCurrentCandidate implements PatientEvent {
    @Override
    public String encode() {
        return "x";
    }

    @Override
    public PatientEvent copy() {
        return new NotCurrentCandidate();
    }

    public static NotCurrentCandidate decode(String src){
        if( "x".equals(src) ){
            return new NotCurrentCandidate();
        } else {
            throw new RuntimeException("Cannot convert to NotCurrentCandidate: " + src);
        }
    }
}
