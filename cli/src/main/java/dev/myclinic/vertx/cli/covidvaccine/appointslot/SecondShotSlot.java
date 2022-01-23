package dev.myclinic.vertx.cli.covidvaccine.appointslot;

import dev.myclinic.vertx.cli.covidvaccine.SecondShotState;

public class SecondShotSlot extends AppointSlot {

    public SecondShotState state;

    public SecondShotSlot(int patientId, SecondShotState state) {
        super(patientId);
        this.state = state;
    }

    @Override
    public String renderState() {
        return state.toString();
    }
}
