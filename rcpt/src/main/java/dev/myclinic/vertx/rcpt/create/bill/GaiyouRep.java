package dev.myclinic.vertx.rcpt.create.bill;

import dev.myclinic.vertx.rcpt.create.input.Gaiyou;

import java.util.Objects;

class GaiyouRep {

    //private static Logger logger = LoggerFactory.getLogger(GaiyouRep.class);
    private int iyakuhincode;
    private double amount;
    private String usage;
    private String tekiyouText;

    GaiyouRep(Gaiyou drug) {
        this.iyakuhincode = drug.iyakuhincode;
        this.amount = drug.amount;
        this.usage = drug.usage;
        this.tekiyouText = drug.tekiyou;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GaiyouRep gaiyouRep = (GaiyouRep) o;
        return iyakuhincode == gaiyouRep.iyakuhincode &&
                Double.compare(gaiyouRep.amount, amount) == 0 &&
                Objects.equals(usage, gaiyouRep.usage) &&
                Objects.equals(tekiyouText, gaiyouRep.tekiyouText);
    }

    @Override
    public int hashCode() {

        return Objects.hash(iyakuhincode, amount, usage, tekiyouText);
    }
}
