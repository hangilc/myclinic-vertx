package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.TextDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class TextUpdated implements PracticeLogEventBody {

    public TextDTO prev;
    public TextDTO updated;

    public TextUpdated() {
    }

    public TextUpdated(TextDTO prev, TextDTO updated) {
        this.prev = prev;
        this.updated = updated;
    }
}
