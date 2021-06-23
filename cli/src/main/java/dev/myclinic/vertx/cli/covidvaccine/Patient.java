package dev.myclinic.vertx.cli.covidvaccine;

public class Patient {

    public int patientId;
    public String name;
    public int age;
    public String phone;

    public Patient(int patientId, String name, int age, String phone) {
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return String.format("(%d) %s %d才 %s", patientId, name, age, phone);
    }
}
