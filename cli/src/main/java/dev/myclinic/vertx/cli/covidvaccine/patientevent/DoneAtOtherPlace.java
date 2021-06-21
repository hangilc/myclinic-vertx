package dev.myclinic.vertx.cli.covidvaccine.patientevent;

public class DoneAtOtherPlace implements PatientEvent {
    @Override
    public String encode() {
        return "T";
    }

    @Override
    public PatientEvent copy() {
        return new DoneAtOtherPlace();
    }

    public static DoneAtOtherPlace decode(String src){
        if( "T".equals(src) ){
            return new DoneAtOtherPlace();
        } else {
            throw new RuntimeException("Cannot decode to DoneAtOtherPlace: " + src);
        }
    }

}
