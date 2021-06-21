package dev.myclinic.vertx.cli.covidvaccine.patientevent;

public class FirstShotCancel implements PatientEvent {

    @Override
    public String encode() {
        return "G";
    }

    @Override
    public FirstShotCancel copy() {
        return new FirstShotCancel();
    }

    public static FirstShotCancel decode(String src){
        if( "G".equals(src) ){
            return new FirstShotCancel();
        } else {
            throw new RuntimeException("Cannot convert to FirstShotCancel: " + src);
        }
    }
}
