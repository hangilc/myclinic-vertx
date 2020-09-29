package dev.myclinic.vertx.rcpt.create.bill;

import dev.myclinic.vertx.consts.HoukatsuKensaKind;
import dev.myclinic.vertx.rcpt.create.input.Shinryou;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class HoukatsuKensaRep {

    //private static Logger logger = LoggerFactory.getLogger(HoukatsuKensaRep.class);
    private HoukatsuKensaKind kind;
    private Set<Integer> shinryouList = new LinkedHashSet<>();

    HoukatsuKensaRep(HoukatsuKensaKind kind, List<Shinryou> shinryouList) {
        this.kind = kind;
        for(Shinryou shinryou: shinryouList){
            this.shinryouList.add(shinryou.shinryoucode);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HoukatsuKensaRep that = (HoukatsuKensaRep) o;
        return kind == that.kind &&
                Objects.equals(shinryouList, that.shinryouList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, shinryouList);
    }
}
