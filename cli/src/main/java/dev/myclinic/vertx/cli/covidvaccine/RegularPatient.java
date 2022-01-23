package dev.myclinic.vertx.cli.covidvaccine;

import dev.myclinic.vertx.cli.covidvaccine.patientevent.PatientEvent;

public class RegularPatient {
    int patientId;
    String name;
    int age;
    String phone;
    PatientEvent state;

    public RegularPatient(int patientId, String name, int age, String phone, PatientEvent state) {
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.state = state;
    }

    RegularPatient() {

    }

    public String toStringWithoutAttr() {
        return String.format("%d %s %d才 %s", patientId, name, age, phone);
    }

    @Override
    public String toString() {
        return state.encode() + " " + toStringWithoutAttr();
    }

    RegularPatient copy() {
        RegularPatient c = new RegularPatient();
        c.patientId = patientId;
        c.name = name;
        c.age = age;
        c.phone = phone;
        c.state = state.copy();
        return c;
    }
}
