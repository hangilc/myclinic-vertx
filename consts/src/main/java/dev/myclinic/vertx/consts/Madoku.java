package dev.myclinic.vertx.consts;

public enum Madoku {

    NoMadoku('0', "麻毒でない"),
    Mayaku('1', "麻薬"),
    Dokuyaku('2', "毒薬"),
    Kakuseizai('3', "覚せい剤原料"),
    Kouseishinyaku('5', "向精神薬");

    private char code;
    private String kanji;

    Madoku(char code, String kanji) {
        this.code = code;
        this.kanji = kanji;
    }

    public char getCode(){
        return code;
    }

    public String getKanjiRep(){
        return kanji;
    }

    public static dev.myclinic.vertx.consts.Madoku fromCode(char ch){
        for(dev.myclinic.vertx.consts.Madoku m: values()){
            if( m.code == ch ){
                return m;
            }
        }
        return null;
    }

}
