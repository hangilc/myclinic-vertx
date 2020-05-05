package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ConductKizaiDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ConductKizaiCreated implements PracticeLogEventBody {

    public ConductKizaiDTO created;

    public ConductKizaiCreated(ConductKizaiDTO created) {
        this.created = created;
    }

    public ConductKizaiCreated() {
    }
}