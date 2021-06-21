package dev.myclinic.vertx.cli.covidvaccine.patientevent;

public class WaitingReply implements PatientEvent {
    @Override
    public String encode() {
        return "P";
    }

    @Override
    public PatientEvent copy() {
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
