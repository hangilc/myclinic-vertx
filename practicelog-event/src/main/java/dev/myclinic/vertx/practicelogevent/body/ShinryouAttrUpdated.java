package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ShinryouAttrDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ShinryouAttrUpdated implements PracticeLogEventBody {

    public ShinryouAttrDTO prev;
    public ShinryouAttrDTO updated;

    public ShinryouAttrUpdated() {

    }

    public ShinryouAttrUpdated(ShinryouAttrDTO prev, ShinryouAttrDTO updated) {
        this.prev = prev;
        this.updated = updated;
    }
}
