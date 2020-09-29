package dev.myclinic.vertx.rcpt.unit;

import dev.myclinic.vertx.consts.DrugCategory;
import dev.myclinic.vertx.dto.DrugFullDTO;
import dev.myclinic.vertx.dto.IyakuhinMasterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GaiyouItem extends CountableBase implements Mergeable<GaiyouItem> {

    private static Logger logger = LoggerFactory.getLogger(GaiyouItem.class);
    private IyakuhinMasterDTO master;
    private double amount;

    GaiyouItem(DrugFullDTO drug) {
        assert DrugCategory.fromCode(drug.drug.category) == DrugCategory.Gaiyou;
        this.master = drug.master;
        this.amount = drug.drug.amount;
    }

    @Override
    public boolean isMergeableWith(GaiyouItem arg) {
        return master.iyakuhincode == arg.master.iyakuhincode && amount == arg.amount;
    }

    @Override
    public String toString() {
        return "GaiyouItem{" +
                "master=" + master +
                ", amount=" + amount +
                '}';
    }
}
