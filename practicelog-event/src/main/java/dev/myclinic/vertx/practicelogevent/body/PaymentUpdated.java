package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.PaymentDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class PaymentUpdated implements PracticeLogEventBody {

    public PaymentDTO prev;
    public PaymentDTO updated;

    public PaymentUpdated() {
    }

    public PaymentUpdated(PaymentDTO prev, PaymentDTO updated) {
        this.prev = prev;
        this.updated = updated;
    }
}