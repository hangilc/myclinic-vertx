package dev.myclinic.vertx.rcpt.check;

class CheckKouseishinyaku extends CheckBaseDrugShinryou {

    CheckKouseishinyaku(Scope scope){
        super(scope);
        this.setPredicate(this::isMadoku);
        this.setShinryoucode(getShinryouMaster().向精神薬);
        this.setShinryouName("調剤・処方料（麻・向・覚・毒）");
    }

}
