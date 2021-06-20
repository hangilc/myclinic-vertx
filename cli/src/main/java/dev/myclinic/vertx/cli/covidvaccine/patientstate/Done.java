package dev.myclinic.vertx.cli.covidvaccine.patientstate;

public class Done implements PatientState {

    @Override
    public String encode() {
        return "D";
    }

    public static Done decode(String src){
        if( "D".equals(src) ){
            return new Done();
        } else {
            throw new RuntimeException("Cannot decode to Done: " + src);
        }
    }

}
