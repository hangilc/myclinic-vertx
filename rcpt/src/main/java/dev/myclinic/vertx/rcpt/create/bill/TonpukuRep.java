package dev.myclinic.vertx.rcpt.create.bill;

import dev.myclinic.vertx.rcpt.create.input.Tonpuku;

import java.util.Objects;

class TonpukuRep {

    //private static Logger logger = LoggerFactory.getLogger(TonpukuRep.class);
    private int iyakuhoncode;
    private double amount;
    private String usage;
    private String tekiyouText;

    TonpukuRep(Tonpuku drug) {
        this.iyakuhoncode = drug.iyakuhincode;
        this.amount = drug.amount;
        this.usage = drug.usage;
        this.tekiyouText = drug.tekiyou;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TonpukuRep that = (TonpukuRep) o;
        return iyakuhoncode == that.iyakuhoncode &&
                Double.compare(that.amount, amount) == 0 &&
                Objects.equals(usage, that.usage) &&
                Objects.equals(tekiyouText, that.tekiyouText);
    }

    @Override
    public int hashCode() {

        return Objects.hash(iyakuhoncode, amount, usage, tekiyouText);
    }
}
