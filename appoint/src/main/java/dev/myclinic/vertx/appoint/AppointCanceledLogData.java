package dev.myclinic.vertx.appoint;

public class AppointCanceledLogData {

    public String kind = "canceled";
    public AppointDTO canceled;

    public AppointCanceledLogData(AppointDTO canceled) {
        this.canceled = canceled;
    }

}
