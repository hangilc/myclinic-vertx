package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ShinryouAttrDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ShinryouAttrDeleted implements PracticeLogEventBody {

    public ShinryouAttrDTO deleted;

    public ShinryouAttrDeleted() {

    }

    public ShinryouAttrDeleted(ShinryouAttrDTO deleted) {
        this.deleted = deleted;
    }
}
