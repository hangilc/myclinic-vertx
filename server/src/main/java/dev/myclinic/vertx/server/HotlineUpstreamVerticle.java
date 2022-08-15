package dev.myclinic.vertx.server;

import java.util.function.Function;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.JsonObject;

public class HotlineUpstreamVerticle extends AbstractVerticle {
    private WebSocket ws = null;

    public static JsonObject encodeListTodaysHotline() {
        JsonObject req = new JsonObject();
        req.put("request", "list-todays-hotline");
        return req;
    }

    public static JsonObject encodePostHotline(String sender, String recipient, String message) {
        JsonObject data = new JsonObject();
        data.put("sender", sender);
        data.put("recipient", recipient);
        data.put("message", message);
        JsonObject req = new JsonObject();
        req.put("request", "post-hotline");
        req.put("data", data);
        return req;
    }

    @Override
    public void start() {
        restart(1);
    }

    private void doStart(Function<Boolean, Void> onComplete) {
        try {
            HttpClient client = vertx.createHttpClient();
            client.webSocket(5000, "localhost", "/", res -> {
                if (res.succeeded()) {
                    ws = res.result();
                    System.out.println("Hotline upstream connected");
                    ws.frameHandler(frame -> {
                        System.out.println("Got frame");
                        if (frame.isText()) {
                            String message = frame.textData();
                            System.out.printf("hotline upstream message: %s\n", message);
                            try {
                                JsonObject m = new JsonObject(message);
                                if (m.getString("kind").equals("hotline")) {
                                    JsonObject h = new JsonObject();
                                    h.put("hotlineId", m.getInteger("id"));
                                    h.put("message", m.getString("message"));
                                    h.put("sender", m.getString("sender"));
                                    h.put("recipient", m.getString("recipient"));
                                    h.put("createdAt", m.getString("createdAt"));
                                    JsonObject c = new JsonObject();
                                    c.put("created", h);
                                    JsonObject r = new JsonObject();
                                    r.put("kind", "created");
                                    r.put("body", c.toString());
                                    System.out.println(r.toString());
                                    vertx.eventBus().send("hotline-streamer", r.toString());
                                }
                            } catch (Exception ex) {
                                System.err.println("Failed to parse JSON.");
                            }
                        }
                    });
                    ws.exceptionHandler(ex -> {
                        System.err.printf("Error: %s\n", ex);
                        restart(1);
                    });
                    ws.closeHandler(_x -> {
                        System.err.println("Hotline upstread disconnected");
                        restart(1);
                    });
                    vertx.eventBus().<JsonObject>consumer("hotline-request", message -> {
                        String m = message.body().toString();
                        System.out.printf("sending hotline request: %s\n", m);
                        ws.writeTextMessage(m);
                    });
                    onComplete.apply(true);
                } else {
                    System.err.println(res.cause());
                    onComplete.apply(false);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            onComplete.apply(false);
        }
    }

    private void restart(int wait) {
        vertx.setTimer(wait * 1000, id -> {
            doStart(ok -> {
                if( !ok ){
                    int nextWait = wait * 2;
                    if( nextWait > 10 ){
                        nextWait = 10;
                    }
                    restart(nextWait);
                }
                return null;
            });
        });
    }
}
