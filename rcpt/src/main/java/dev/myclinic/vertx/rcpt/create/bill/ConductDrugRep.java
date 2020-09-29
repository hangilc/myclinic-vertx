package dev.myclinic.vertx.rcpt.create.bill;

import dev.myclinic.vertx.rcpt.create.input.ConductDrug;

import java.util.Objects;

class ConductDrugRep {

    //private static Logger logger = LoggerFactory.getLogger(ConductDrugRep.class);
    private int iyakuhincode;
    private double amount;

    ConductDrugRep(ConductDrug drug) {
        this.iyakuhincode = drug.iyakuhincode;
        this.amount = drug.amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConductDrugRep that = (ConductDrugRep) o;
        return iyakuhincode == that.iyakuhincode &&
                Double.compare(that.amount, amount) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(iyakuhincode, amount);
    }
}
