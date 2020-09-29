package dev.myclinic.vertx.rcpt.create.bill;

import dev.myclinic.vertx.rcpt.create.input.ConductKizai;

import java.util.Objects;

class ConductKizaiRep {

    //private static Logger logger = LoggerFactory.getLogger(ConductKizai.class);
    private int kizaicode;
    private double amount;

    public ConductKizaiRep(ConductKizai kizai) {
        this.kizaicode = kizai.kizaicode;
        this.amount = kizai.amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConductKizaiRep that = (ConductKizaiRep) o;
        return kizaicode == that.kizaicode &&
                Double.compare(that.amount, amount) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(kizaicode, amount);
    }
}
