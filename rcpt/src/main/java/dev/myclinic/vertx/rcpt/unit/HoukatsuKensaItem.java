package dev.myclinic.vertx.rcpt.unit;

import dev.myclinic.vertx.consts.HoukatsuKensaKind;
import dev.myclinic.vertx.dto.ShinryouMasterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class HoukatsuKensaItem extends CountableBase implements Extendable<HoukatsuKensaItem>, Mergeable<HoukatsuKensaItem> {

    private static Logger logger = LoggerFactory.getLogger(HoukatsuKensaItem.class);
    private HoukatsuKensaKind kind;
    private Set<Integer> shinryoucodes = new HashSet<>();
    private Map<Integer, ShinryouMasterDTO> masterMap = new HashMap<>();

    HoukatsuKensaItem(ShinryouMasterDTO master) {
        assert HoukatsuKensaKind.fromCode(master.houkatsukensa) != HoukatsuKensaKind.NONE;
        this.kind = HoukatsuKensaKind.fromCode(master.houkatsukensa);
        addMaster(master);
    }

    HoukatsuKensaKind getKind(){
        return kind;
    }

    private void addMaster(ShinryouMasterDTO master){
        assert HoukatsuKensaKind.fromCode(master.houkatsukensa) == kind;
        shinryoucodes.add(master.shinryoucode);
        masterMap.put(master.shinryoucode, master);
    }

    @Override
    public boolean isExtendableWith(HoukatsuKensaItem a) {
        return kind == a.kind;
    }

    @Override
    public void extendWith(HoukatsuKensaItem master){
        master.masterMap.values().forEach(this::addMaster);
    }

    @Override
    public boolean isMergeableWith(HoukatsuKensaItem arg){
        return kind == arg.kind && shinryoucodes.equals(arg.shinryoucodes);
    }

    @Override
    public String toString() {
        return "HoukatsuKensaItem{" +
                "kind=" + kind +
                ", shinryoucodes=" + shinryoucodes +
                ", masterMap=" + masterMap +
                '}';
    }
}
