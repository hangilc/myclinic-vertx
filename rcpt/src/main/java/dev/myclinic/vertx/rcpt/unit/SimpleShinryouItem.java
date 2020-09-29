package dev.myclinic.vertx.rcpt.unit;

import dev.myclinic.vertx.dto.ShinryouMasterDTO;

class SimpleShinryouItem extends CountableBase {

    //private static Logger logger = LoggerFactory.getLogger(SimpleShinryouItem.class);
    private ShinryouMasterDTO master;

    SimpleShinryouItem(ShinryouMasterDTO master) {
        this.master = master;
    }

    @Override
    public String toString() {
        return "SimpleShinryouItem{" +
                "master=" + master +
                '}';
    }
}
