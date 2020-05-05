package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.TextDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class TextDeleted implements PracticeLogEventBody {

    public TextDTO deleted;

    public TextDeleted() {
    }

    public TextDeleted(TextDTO deleted) {
        this.deleted = deleted;
    }
}
