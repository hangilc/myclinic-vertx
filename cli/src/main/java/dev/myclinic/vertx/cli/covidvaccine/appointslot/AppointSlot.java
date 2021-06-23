package dev.myclinic.vertx.cli.covidvaccine.appointslot;

public abstract class AppointSlot {
    public int patientId;
    abstract public String renderState();

    public AppointSlot(int patientId) {
        this.patientId = patientId;
    }
}
