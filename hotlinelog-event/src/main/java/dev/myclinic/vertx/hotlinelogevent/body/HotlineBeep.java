package dev.myclinic.vertx.hotlinelogevent.body;

import dev.myclinic.vertx.hotlinelogevent.HotlineEventBody;

public class HotlineBeep implements HotlineEventBody {

    public String target;

    public HotlineBeep() {
    }

    public HotlineBeep(String target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "HotlineBeep{" +
                "target='" + target + '\'' +
                '}';
    }
}
