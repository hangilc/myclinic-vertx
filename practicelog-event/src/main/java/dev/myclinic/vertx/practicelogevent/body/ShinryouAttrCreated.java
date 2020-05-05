package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ShinryouAttrDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ShinryouAttrCreated implements PracticeLogEventBody {

    public ShinryouAttrDTO created;

    public ShinryouAttrCreated() {

    }

    public ShinryouAttrCreated(ShinryouAttrDTO created) {
        this.created = created;
    }
}
