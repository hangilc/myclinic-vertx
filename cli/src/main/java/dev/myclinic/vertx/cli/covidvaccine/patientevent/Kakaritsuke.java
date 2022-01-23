package dev.myclinic.vertx.cli.covidvaccine.patientevent;

public class Kakaritsuke implements PatientEvent {

    @Override
    public String encode() {
        return "K";
    }

    @Override
    public Kakaritsuke copy() {
        return new Kakaritsuke();
    }

    public static Kakaritsuke decode(String src){
        if( "K".equals(src) ){
            return new Kakaritsuke();
        } else {
            throw new RuntimeException("Cannot convert to Kakaritsuke: " + src);
        }
    }
}
