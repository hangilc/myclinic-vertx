package dev.myclinic.vertx.server;

import java.util.HashSet;
import java.util.Set;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.ServerWebSocket;

class HotlineStreamerVerticle extends AbstractVerticle {

    private final Set<ServerWebSocket> clients = new HashSet<>();

    @Override
    public void start() throws Exception {
        super.start();
        EventBus bus = vertx.eventBus();
        bus.<String>consumer("hotline-streamer", message -> this.broadcast(message.body()));
    }

    public void addClient(ServerWebSocket client) {
        System.out.printf("ws client added: %s\n", client);
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
        System.out.printf("before add: %s\n", clients);
        this.clients.add(client);
        System.out.printf("after add: %s\n", clients);
    }

    private void broadcast(String msg) {
        System.out.printf("broadcasting: %s\n", msg);
        for (var client : clients) {
            client.writeTextMessage(msg);
        }
    }

}
