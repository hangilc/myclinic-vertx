package dev.myclinic.vertx.cli.covidvaccine.patientevent;

public class FirstShotDone implements PatientEvent {

    @Override
    public String encode() {
        return "F";
    }

    @Override
    public FirstShotDone copy() {
        return new FirstShotDone();
    }

    public static FirstShotDone decode(String src){
        if( "F".equals(src) ){
            return new FirstShotDone();
        } else {
            throw new RuntimeException("Cannot convert to FirstShotDone: " + src);
        }
    }
}
