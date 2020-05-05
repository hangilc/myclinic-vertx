package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ConductKizaiDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ConductKizaiUpdated implements PracticeLogEventBody {

    public ConductKizaiDTO prev;
    public ConductKizaiDTO updated;

    public ConductKizaiUpdated() {
    }

    public ConductKizaiUpdated(ConductKizaiDTO prev, ConductKizaiDTO updated) {
        this.prev = prev;
        this.updated = updated;
    }
}