package dev.myclinic.vertx.cli.covidvaccine.patientstate;

public class Under65 implements PatientState {
    @Override
    public String encode() {
        return "U";
    }

    @Override
    public PatientState copy() {
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
