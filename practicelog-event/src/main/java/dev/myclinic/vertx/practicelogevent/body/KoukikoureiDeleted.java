package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.KoukikoureiDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class KoukikoureiDeleted implements PracticeLogEventBody {

    public KoukikoureiDTO deleted;

    public KoukikoureiDeleted() {
    }

    public KoukikoureiDeleted(KoukikoureiDTO deleted) {
        this.deleted = deleted;
    }
}