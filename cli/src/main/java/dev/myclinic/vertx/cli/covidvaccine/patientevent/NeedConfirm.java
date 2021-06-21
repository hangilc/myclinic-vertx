package dev.myclinic.vertx.cli.covidvaccine.patientevent;

public class NeedConfirm implements PatientEvent {
    @Override
    public String encode() {
        return "*";
    }

    @Override
    public PatientEvent copy() {
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
