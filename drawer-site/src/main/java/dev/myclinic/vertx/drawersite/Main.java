package dev.myclinic.vertx.drawersite;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws Exception {
        int port = 48080;
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 4);
        server.setExecutor(createExecutor());
        server.createContext("/ping", exchange -> {
            byte[] body = "pong".getBytes();
            exchange.getResponseHeaders().add("content-type", "text/plain");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.getRequestBody().close();
        });
        System.err.printf("Drawer-site server is listening to port %d\n", port);
        server.start();
    }

    private static Executor createExecutor(){
        return Executors.newFixedThreadPool(6);
    }

}
