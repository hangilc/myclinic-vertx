package dev.myclinic.vertx.rcpt.create.bill;

public enum SubShuukei {
    SUB_SHOSHIN(11),
    SUB_SAISHIN(12),
    SUB_SHIDOU(13),
    SUB_ZAITAKU(14),
    SUB_TOUYAKU_NAIFUKU(21),
    SUB_TOUYAKU_TONPUKU(22),
    SUB_TOUYAKU_GAIYOU(23),
    SUB_TOUYAKU_SHOHOU(25),
    SUB_TOUYAKU_MADOKU(26),
    SUB_TOUYAKU_CHOUKI(27),
    SUB_CHUUSHA_HIKA(31),
    SUB_CHUUSHA_JOUMYAKU(32),
    SUB_CHUUSHA_SONOTA(33),
    SUB_SHOCHI(40),
    SUB_SHUJUTSU(50),
    SUB_KENSA(60),
    SUB_GAZOU(70),
    SUB_SONOTA(80);

    private int code;

    SubShuukei(int code){
        this.code = code;
    }

    int getCode(){
        return code;
    }
}
