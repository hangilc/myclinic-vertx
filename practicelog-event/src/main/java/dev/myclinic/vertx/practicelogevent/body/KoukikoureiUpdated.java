package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.KoukikoureiDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class KoukikoureiUpdated implements PracticeLogEventBody {

    public KoukikoureiDTO prev;
    public KoukikoureiDTO updated;

    public KoukikoureiUpdated() {
    }

    public KoukikoureiUpdated(KoukikoureiDTO prev, KoukikoureiDTO updated) {
        this.prev = prev;
        this.updated = updated;
    }
}