package dev.myclinic.vertx.rcpt.unit;

import dev.myclinic.vertx.dto.DrugFullDTO;
import dev.myclinic.vertx.dto.IyakuhinMasterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class NaifukuItem extends CountableBase implements Extendable<NaifukuItem>, Mergeable<NaifukuItem> {

    private final class MasterAmount {
        int iyakuhincode;
        double amount;

        MasterAmount(DrugFullDTO drug){
            this.iyakuhincode = drug.master.iyakuhincode;
            this.amount = drug.drug.amount;
        }

        @Override
        public boolean equals(Object arg){
            if( arg == null ){
                return false;
            }
            if( arg.getClass() != MasterAmount.class ){
                return false;
            }
            MasterAmount src = (MasterAmount)arg;
            return iyakuhincode == src.iyakuhincode && amount == src.amount;
        }

        @Override
        public int hashCode() {
            return Objects.hash(iyakuhincode, amount);
        }

        @Override
        public String toString() {
            return "MasterAmount{" +
                    "iyakuhincode=" + iyakuhincode +
                    ", amount=" + amount +
                    '}';
        }
    }

    private static Logger logger = LoggerFactory.getLogger(NaifukuItem.class);
    private String usage;
    private Set<MasterAmount> masterAmounts = new LinkedHashSet<>();
    private Map<Integer, IyakuhinMasterDTO> masterMap = new HashMap<>();

    NaifukuItem(DrugFullDTO drug) {
        String usage = drug.drug.usage;
        if( "就寝前".equals(usage) ){
            usage = "寝る前";
        }
        this.usage = usage;
        masterAmounts.add(new MasterAmount(drug));
        masterMap.put(drug.master.iyakuhincode, drug.master);
        setCount(drug.drug.days);
    }

    @Override
    public boolean isExtendableWith(NaifukuItem a) {
        return usage.equals(a.usage) && getCount() == a.getCount();
    }

    @Override
    public void extendWith(NaifukuItem a) {
        masterAmounts.addAll(a.masterAmounts);
        masterMap.putAll(a.masterMap);
    }

    @Override
    public boolean isMergeableWith(NaifukuItem arg) {
        return usage.equals(arg.usage) && masterAmounts.equals(arg.masterAmounts);
    }

    @Override
    public String toString() {
        return "NaifukuItem{" +
                "usage='" + usage + '\'' +
                ", masterAmounts=" + masterAmounts +
                ", masterMap=" + masterMap +
                '}';
    }
}
