package dev.myclinic.vertx.cli.covidvaccine;

import dev.myclinic.vertx.cli.covidvaccine.appointslot.AppointSlot;

import java.util.ArrayList;
import java.util.List;

public class AppointBlock {

    public AppointDate appointDate;
    public List<AppointSlot> slots = new ArrayList<>();

    public AppointBlock(AppointDate appointDate) {
        this.appointDate = appointDate;
    }

    public void addSlot(AppointSlot slot){
        slots.add(slot);
    }

    public boolean isOverbooking(){
        return appointDate.capacity < slots.size();
    }

    public int getCapacity(){
        return appointDate.capacity;
    }

    public boolean hasVacancy(){
        return slots.size() < appointDate.capacity;
    }
}
