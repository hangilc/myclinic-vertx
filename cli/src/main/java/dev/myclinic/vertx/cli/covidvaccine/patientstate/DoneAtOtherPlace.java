package dev.myclinic.vertx.cli.covidvaccine.patientstate;

public class DoneAtOtherPlace implements PatientState {
    @Override
    public String encode() {
        return "T";
    }

    public static DoneAtOtherPlace decode(String src){
        if( "T".equals(src) ){
            return new DoneAtOtherPlace();
        } else {
            throw new RuntimeException("Cannot decode to DoneAtOtherPlace: " + src);
        }
    }

}
