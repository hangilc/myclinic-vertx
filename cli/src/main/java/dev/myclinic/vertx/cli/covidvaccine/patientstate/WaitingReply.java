package dev.myclinic.vertx.cli.covidvaccine.patientstate;

public class WaitingReply implements PatientState {
    @Override
    public String encode() {
        return "P";
    }

    @Override
    public PatientState copy() {
        return new WaitingReply();
    }

    public static WaitingReply decode(String src){
        if( "P".equals(src) ){
            return new WaitingReply();
        } else {
            throw new RuntimeException("Cannot convert to WaitingReply: " + src);
        }
    }
}
