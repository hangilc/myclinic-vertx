package dev.myclinic.vertx.appoint;

public class AppointEventDTO {

    public int id;
    public String body;

    public AppointEventDTO(int id, String body) {
        this.id = id;
        this.body = body;
    }
}
