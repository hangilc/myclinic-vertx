package dev.myclinic.vertx.practicelogevent.body;

import dev.myclinic.vertx.dto.PaymentDTO;
import dev.myclinic.vertx.practicelogevent.PracticeLogEventBody;

public class PaymentCreated implements PracticeLogEventBody {

    public PaymentDTO created;

    public PaymentCreated() {
    }

    public PaymentCreated(PaymentDTO created) {
        this.created = created;
    }
}