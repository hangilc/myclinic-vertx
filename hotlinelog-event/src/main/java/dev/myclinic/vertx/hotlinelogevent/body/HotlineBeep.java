package dev.myclinic.vertx.hotlinelogevent.body;

public class HotlineBeep implements dev.myclinic.vertx.hotlinelogevent.HotlineEventBody {

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
