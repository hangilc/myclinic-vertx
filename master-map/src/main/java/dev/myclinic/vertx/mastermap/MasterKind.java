package dev.myclinic.vertx.mastermap;

import java.util.Optional;

public enum MasterKind {
    Shinryou("shinryou"),
    Yakuzai("yakuzai"),
    Kizai("kizai"),
    Byoumei("byoumei"),
    Shuushokugo("shuushokugo");

    private final String name;

    MasterKind(String name){
        this.name = name;
    }

    public static Optional<MasterKind> fromName(String name){
        for(var e: values()){
            if( e.name.equals(name) ){
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }

    public String getName(){
        return name;
    }
}
