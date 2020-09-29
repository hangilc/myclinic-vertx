package dev.myclinic.vertx.rcpt.check;

class CheckNaifuku extends CheckBaseDrugShinryou {

    CheckNaifuku(Scope scope) {
        super(scope);
        setPredicate(d -> isNaifuku(d) || isTonpuku(d));
        setShinryoucode(getShinryouMaster().内服調剤);
        setShinryouName("調剤料（内服薬）");
    }

}
