package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.TextDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class TextCreated implements PracticeLogEventBody {

    public TextDTO created;

    public TextCreated() {
    }

    public TextCreated(TextDTO created) {
        this.created = created;
    }
}
