package dev.myclinic.vertx.cli.covidvaccine.patientstate;

public class NeedConfirm implements PatientState {
    @Override
    public String encode() {
        return "*";
    }

    @Override
    public PatientState copy() {
        return new NeedConfirm();
    }

    public static NeedConfirm decode(String src){
        if( "*".equals(src) ){
            return new NeedConfirm();
        } else {
            throw new RuntimeException("Cannot convert to NeedConfirm: " + src);
        }
    }
}
