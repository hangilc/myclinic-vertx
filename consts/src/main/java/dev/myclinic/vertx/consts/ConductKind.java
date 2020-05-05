package dev.myclinic.vertx.consts;

public enum ConductKind {
    HikaChuusha(0, "皮下・筋肉注射"),
    JoumyakuChuusha(1, "静脈注射"),
    OtherChuusha(2, "その他の注射"),
    Gazou(3, "画像");

    private int code;
    private String kanjiRep;

    ConductKind(int code, String kanjiRep){
        this.code = code;
        this.kanjiRep = kanjiRep;
    }

    public int getCode() {
        return code;
    }

    public String getKanjiRep() {
        return kanjiRep;
    }

    public static dev.myclinic.vertx.consts.ConductKind fromCode(int code){
        for(dev.myclinic.vertx.consts.ConductKind conductKind: values()){
            if( conductKind.code == code ){
                return conductKind;
            }
        }
        return null;
    }

    public static dev.myclinic.vertx.consts.ConductKind fromKanjiRep(String kanjiRep){
        for(dev.myclinic.vertx.consts.ConductKind conductKind: values()){
            if( conductKind.kanjiRep.equals(kanjiRep) ){
                return conductKind;
            }
        }
        return null;
    }

    public static String codeToKanjiRep(int code){
        dev.myclinic.vertx.consts.ConductKind kind = fromCode(code);
        if( kind == null ){
            return "??";
        } else {
            return kind.getKanjiRep();
        }
    }
}
