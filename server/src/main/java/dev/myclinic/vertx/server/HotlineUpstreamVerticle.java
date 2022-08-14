package dev.myclinic.vertx.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.JsonObject;

public class HotlineUpstreamVerticle extends AbstractVerticle {
    private final Vertx vertx;
    private EventBus bus;
    private final ObjectMapper mapper;
    private WebSocket ws = null;

    public HotlineUpstreamVerticle(Vertx vertx, ObjectMapper mapper) {
        this.vertx = vertx;
        this.bus = vertx.eventBus();
        this.mapper = mapper;
    }

    @Override
    public void start(){
        HttpClient client = vertx.createHttpClient();
        client.webSocket(5000, "localhost", "/", res -> {
            if( res.succeeded() ){
                ws = res.result();
                System.out.println("Hotline upstream connected");
                ws.frameHandler(frame -> {
                    System.out.println("Got frame");
                    if( frame.isText() ){
                        String message = frame.textData();
                        System.out.printf("hotline upstream message: %s\n", message);
                        try {
                            JsonObject m = new JsonObject(message);
                            if( m.getString("kind").equals("hotline") ){
                                JsonObject h = new JsonObject();
                                h.put("hotlineId", m.getInteger("id"));
                                h.put("message", m.getString("message"));
                                h.put("sender", m.getString("sender"));
                                h.put("recipient", m.getString("recipient"));
                                h.put("createdAt", m.getString("createdAt"));
                                JsonObject c = new JsonObject();
                                c.put("created", h);
                                JsonObject r = new JsonObject();
                                r.put("kind", "hotline");
                                r.put("body", c.toString());
                                System.out.println(r.toString());
                                bus.send("hotline-streamer", r.toString());
                            }
                        } catch(Exception ex) {
                            System.err.println("Failed to parse JSON.");
                        }
                    }
                });
            } else {
                System.err.println(res.cause());
            }
        });
    }
}
