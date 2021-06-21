package dev.myclinic.vertx.cli.covidvaccine.patientevent;

public class SecondShotExternal implements PatientEvent {

    @Override
    public String encode() {
        return "I";
    }

    @Override
    public SecondShotExternal copy() {
        return new SecondShotExternal();
    }

    public static SecondShotExternal decode(String src){
        if( "I".equals(src) ){
            return new SecondShotExternal();
        } else {
            throw new RuntimeException("Cannot convert to SecondShotExternal: " + src);
        }
    }
}
