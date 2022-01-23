package dev.myclinic.vertx.cli.covidvaccine.patientevent;

public class Done implements PatientEvent {

    @Override
    public String encode() {
        return "D";
    }

    @Override
    public PatientEvent copy() {
        return new Done();
    }

    public static Done decode(String src){
        if( "D".equals(src) ){
            return new Done();
        } else {
            throw new RuntimeException("Cannot decode to Done: " + src);
        }
    }

}
