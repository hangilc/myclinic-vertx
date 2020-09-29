package dev.myclinic.vertx.rcpt.create.bill;

import dev.myclinic.vertx.rcpt.create.input.Shinryou;

import java.util.Objects;

class ShinryouRep {

    //private static Logger logger = LoggerFactory.getLogger(ShinryouRep.class);
    private int shinryoucode;
    private String tekiyou;

    ShinryouRep(Shinryou shinryou) {
        this.shinryoucode = shinryou.shinryoucode;
        this.tekiyou = shinryou.tekiyou;
    }

    ShinryouRep(int shinryoucode, String tekiyou){
        this.shinryoucode = shinryoucode;
        this.tekiyou = tekiyou;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShinryouRep that = (ShinryouRep) o;
        return shinryoucode == that.shinryoucode &&
                Objects.equals(tekiyou, that.tekiyou);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shinryoucode, tekiyou);
    }
}
