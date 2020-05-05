package dev.myclinic.vertx.practicelogevent;

public class PracticeLogEvent {

    public int serialId;
    public String kind;
    public String createdAt;
    public dev.myclinic.vertx.practicelogevent.PracticeLogEventBody body;

    public PracticeLogEvent() {
    }

    public PracticeLogEvent(int serialId, String kind, String createdAt, dev.myclinic.vertx.practicelogevent.PracticeLogEventBody body) {
        this.serialId = serialId;
        this.kind = kind;
        this.createdAt = createdAt;
        this.body = body;
    }

    @Override
    public String toString() {
        return "PracticeLogEvent{" +
                "serialId=" + serialId +
                ", kind='" + kind + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", body=" + body +
                '}';
    }
}
