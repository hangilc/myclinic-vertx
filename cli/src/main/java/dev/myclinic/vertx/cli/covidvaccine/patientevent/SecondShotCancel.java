package dev.myclinic.vertx.cli.covidvaccine.patientevent;

public class SecondShotCancel implements PatientEvent {

    @Override
    public String encode() {
        return "H";
    }

    @Override
    public SecondShotCancel copy() {
        return new SecondShotCancel();
    }

    public static SecondShotCancel decode(String src){
        if( "H".equals(src) ){
            return new SecondShotCancel();
        } else {
            throw new RuntimeException("Cannot convert to SecondShotCancel: " + src);
        }
    }
}
