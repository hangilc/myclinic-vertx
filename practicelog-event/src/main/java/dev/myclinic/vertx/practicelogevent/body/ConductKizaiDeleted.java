package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ConductKizaiDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ConductKizaiDeleted implements PracticeLogEventBody {

    public ConductKizaiDTO deleted;

    public ConductKizaiDeleted() {
    }

    public ConductKizaiDeleted(ConductKizaiDTO deleted) {
        this.deleted = deleted;
    }
}