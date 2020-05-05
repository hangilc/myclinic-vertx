package dev.myclinic.vertx.dto;

import dev.myclinic.vertx.dto.annotation.AutoInc;
import dev.myclinic.vertx.dto.annotation.Primary;

public class HotlineDTO {
    @Primary
    @AutoInc
    public int hotlineId;
    public String message;
    public String sender;
    public String recipient;
    public String postedAt;

    @Override
    public String toString() {
        return "HotlineDTO{" +
                "hotlineId=" + hotlineId +
                ", message='" + message + '\'' +
                ", sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", postedAt='" + postedAt + '\'' +
                '}';
    }
}
