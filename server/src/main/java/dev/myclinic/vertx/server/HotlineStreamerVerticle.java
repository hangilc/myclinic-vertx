package dev.myclinic.vertx.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.dto.HotlineLogDTO;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.ServerWebSocket;

import java.util.HashSet;
import java.util.Set;

class HotlineStreamerVerticle extends AbstractVerticle {

    private final Set<ServerWebSocket> clients = new HashSet<>();
    private final ObjectMapper mapper;

    public HotlineStreamerVerticle(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void start() throws Exception {
        super.start();
        EventBus bus = vertx.eventBus();
        bus.<String>consumer("hotline-streamer", message -> this.handleMessage(message.body()));
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
    }

    private void handleMessage(String msg){
        try {
            HotlineLogDTO log = mapper.readValue(msg, HotlineLogDTO.class);
            System.out.println(log);

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void broadcast(String msg){
        for(var client: clients){
            client.writeTextMessage(msg);
        }
    }

}
