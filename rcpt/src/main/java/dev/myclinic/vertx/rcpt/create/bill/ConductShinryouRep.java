package dev.myclinic.vertx.rcpt.create.bill;

import dev.myclinic.vertx.rcpt.create.input.ConductShinryou;

import java.util.Objects;

class ConductShinryouRep {

    //private static Logger logger = LoggerFactory.getLogger(ConductShinryouRep.class);
    private int shinryoucode;

    ConductShinryouRep(ConductShinryou shinryou) {
        this.shinryoucode = shinryou.shinryoucode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConductShinryouRep that = (ConductShinryouRep) o;
        return shinryoucode == that.shinryoucode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(shinryoucode);
    }
}
