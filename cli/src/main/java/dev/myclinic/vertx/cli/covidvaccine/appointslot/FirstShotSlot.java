package dev.myclinic.vertx.cli.covidvaccine.appointslot;

import dev.myclinic.vertx.cli.covidvaccine.FirstShotState;

public class FirstShotSlot extends AppointSlot {

    public FirstShotState state;

    public FirstShotSlot(int patientId, FirstShotState state) {
        super(patientId);
        this.state = state;
    }

    @Override
    public String renderState() {
        return state.toString();
    }
}
