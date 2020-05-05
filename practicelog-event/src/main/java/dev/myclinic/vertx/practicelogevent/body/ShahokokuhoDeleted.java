package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.ShahokokuhoDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class ShahokokuhoDeleted implements PracticeLogEventBody {

    public ShahokokuhoDTO deleted;

    public ShahokokuhoDeleted() {
    }

    public ShahokokuhoDeleted(ShahokokuhoDTO deleted) {
        this.deleted = deleted;
    }
}
