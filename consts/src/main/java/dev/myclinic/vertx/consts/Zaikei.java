package dev.myclinic.vertx.consts;

public enum Zaikei {

    Naifuku('1'),
    Other('3'),
    Chuusha('4'),
    Gaiyou('6'),
    ShikaYakuzai('8'),
    ShikaTokutei('9');

    private char code;

    Zaikei(char code){
        this.code = code;
    }

    public char getCode(){
        return code;
    }

    public static dev.myclinic.vertx.consts.Zaikei fromCode(char code){
        for(dev.myclinic.vertx.consts.Zaikei zaikei: values()){
            if( zaikei.code == code ){
                return zaikei;
            }
        }
        return null;
    }
}
