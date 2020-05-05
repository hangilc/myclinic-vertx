package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.PaymentDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class PaymentDeleted implements PracticeLogEventBody {

    public PaymentDTO deleted;

    public PaymentDeleted() {
    }

    public PaymentDeleted(PaymentDTO deleted) {
        this.deleted = deleted;
    }
}