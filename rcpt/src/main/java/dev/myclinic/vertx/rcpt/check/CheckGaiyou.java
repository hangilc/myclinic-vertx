package dev.myclinic.vertx.rcpt.check;

class CheckGaiyou extends CheckBaseDrugShinryou {

    CheckGaiyou(Scope scope){
        super(scope);
        setPredicate(this::isGaiyou);
        setShinryoucode(getShinryouMaster().外用調剤);
        setShinryouName("調剤料（外用薬）");
    }

}
