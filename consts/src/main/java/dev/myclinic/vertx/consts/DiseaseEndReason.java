package dev.myclinic.vertx.consts;

public enum DiseaseEndReason {
    NotEnded('N', "継続"),
    Stopped('S', "中止"),
    Cured('C', "治癒"),
    Dead('D', "死亡");

    private char code;
    private String rep;

    DiseaseEndReason(char code, String rep){
        this.code = code;
        this.rep = rep;
    }

    public char getCode(){
        return code;
    }

    public String getKanjiRep(){
        return rep;
    }

    public static class NotFoundException extends RuntimeException {
        NotFoundException(String message){
            super(message);
        }
    }

    public static dev.myclinic.vertx.consts.DiseaseEndReason fromCode(char code){
        for(dev.myclinic.vertx.consts.DiseaseEndReason r: values()){
            if( r.code == code ){
                return r;
            }
        }
        throw new NotFoundException("cannot find code: " + code);
    }

    public static dev.myclinic.vertx.consts.DiseaseEndReason fromKanjiRep(String rep){
        for(dev.myclinic.vertx.consts.DiseaseEndReason r: values()){
            if( r.rep.equals(rep) ){
                return r;
            }
        }
        throw new NotFoundException("cannot find rep: " + rep);
    }
}
