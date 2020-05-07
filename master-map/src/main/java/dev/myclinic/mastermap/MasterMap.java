package dev.myclinic.mastermap;

import java.time.LocalDate;

public class MasterMap {

    private MasterNameMap nameMap;
    private MasterChronoMap chronoMap;

    public MasterMap(MasterNameMap nameMap, MasterChronoMap chronoMap) {
        this.nameMap = nameMap;
        this.chronoMap = chronoMap;
    }

    public int resolve(MasterKind kind, String name){
        return nameMap.resolve(kind, name)
                .orElseThrow(() -> new RuntimeException("Cannot find code for " + name));
    }

    public int resolve(MasterKind kind, String name, LocalDate at){
        return chronoMap.resolve(kind, resolve(kind, name), at);
    }

    public int resolve(MasterKind kind, int code, LocalDate at){
        return chronoMap.resolve(kind, code, at);
    }

}
