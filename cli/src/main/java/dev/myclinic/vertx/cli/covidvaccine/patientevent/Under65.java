package dev.myclinic.vertx.cli.covidvaccine.patientevent;

public class Under65 implements PatientEvent {
    @Override
    public String encode() {
        return "U";
    }

    @Override
    public PatientEvent copy() {
        return new Under65();
    }

    public static Under65 decode(String src){
        if( "U".equals(src) ){
            return new Under65();
        } else {
            throw new RuntimeException("Cannot convert to Under65: " + src);
        }
    }
}
