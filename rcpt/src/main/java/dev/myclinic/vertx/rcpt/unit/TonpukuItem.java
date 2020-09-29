package dev.myclinic.vertx.rcpt.unit;

import dev.myclinic.vertx.consts.DrugCategory;
import dev.myclinic.vertx.dto.DrugFullDTO;
import dev.myclinic.vertx.dto.IyakuhinMasterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TonpukuItem extends CountableBase implements Mergeable<TonpukuItem> {

    private static Logger logger = LoggerFactory.getLogger(TonpukuItem.class);
    private IyakuhinMasterDTO master;
    private double amount;

    TonpukuItem(DrugFullDTO drug) {
        assert DrugCategory.fromCode(drug.drug.category) == DrugCategory.Tonpuku;
        this.master = drug.master;
        this.amount = drug.drug.amount;
        setCount(drug.drug.days);
    }

    @Override
    public boolean isMergeableWith(TonpukuItem arg) {
        return master.iyakuhincode == arg.master.iyakuhincode && amount == arg.amount;
    }

    @Override
    public String toString() {
        return "TonpukuItem{" +
                "master=" + master +
                ", amount=" + amount +
                '}';
    }
}
