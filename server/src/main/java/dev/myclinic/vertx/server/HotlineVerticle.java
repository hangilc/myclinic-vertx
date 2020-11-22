package dev.myclinic.vertx.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.ServerWebSocket;

import java.util.HashSet;
import java.util.Set;

class HotlineVerticle extends AbstractVerticle {

    private final Set<ServerWebSocket> clients = new HashSet<>();

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
    }

    private void broadcast(String msg){
        for(var client: clients){
            client.writeTextMessage(msg);
        }
    }

}
