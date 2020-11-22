package dev.myclinic.vertx.hotlinelogevent;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.myclinic.vertx.dto.HotlineDTO;
import dev.myclinic.vertx.hotlinelogevent.body.HotlineBeep;
import dev.myclinic.vertx.hotlinelogevent.body.HotlineCreated;

public class HotlineEvent {

    public String kind;
    public HotlineEventBody body;

    public HotlineEvent(){

    }

    public HotlineEvent(String kind, HotlineEventBody body){
        this.kind = kind;
        this.body = body;
    }

    public static HotlineEvent created(HotlineDTO created){
        return new HotlineEvent("created", new HotlineCreated(created));
    }

    public static HotlineEvent beep(String target){
        return new HotlineEvent("beep", new HotlineBeep(target));
    }

    @JsonIgnore
    public HotlineCreated getBodyAsCreated(){
        return (HotlineCreated)body;
    }

    @JsonIgnore
    public HotlineBeep getBodyAsBeep(){
        return (HotlineBeep)body;
    }


    @Override
    public String toString() {
        return "HotlineEvent{" +
                "kind='" + kind + '\'' +
                ", body=" + body +
                '}';
    }
}
