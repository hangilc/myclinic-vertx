package dev.myclinic.vertx.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.ServerWebSocket;

import java.util.HashSet;
import java.util.Set;

class FaxStreamingVerticle extends AbstractVerticle {

    private Set<ServerWebSocket> clients = new HashSet<>();

    @Override
    public void start() throws Exception {
        super.start();
        EventBus bus = vertx.eventBus();
        bus.<String>consumer("fax-streaming", message -> {
            for(ServerWebSocket sock: clients){
                sock.writeTextMessage(message.body());
            }
        });
    }

    public void addClient(ServerWebSocket client){
        client.closeHandler(_dummy -> {
            clients.remove(client);
            System.out.println("client removed: " + client);
        });
        client.endHandler(_dummy -> {
            clients.remove(client);
            System.out.println("client ended: " + client);
        });
        client.exceptionHandler(_dummy -> {
            clients.remove(client);
            System.out.println("client throwed: " + client);
        });
        this.clients.add(client);
        client.writeTextMessage("hello");
    }
}
