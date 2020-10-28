package dev.myclinic.vertx.drawersite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import dev.myclinic.vertx.drawer.JacksonOpDeserializer;
import dev.myclinic.vertx.drawer.JacksonOpSerializer;
import dev.myclinic.vertx.drawer.Op;
import dev.myclinic.vertx.drawer.PrintRequest;
import dev.myclinic.vertx.drawerprinterwin.DrawerPrinter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Main {

    static ObjectMapper mapper = createMapper();

    public static void main(String[] args) throws Exception {
        int port = 48080;
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 4);
        server.setExecutor(createExecutor());
        server.createContext("/ping", exchange -> {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            replyText(exchange, "pong");
        });
        server.createContext("/print", exchange -> {
            System.err.printf("Accepted print request\n");
            PrintRequest pr = mapper.readValue(exchange.getRequestBody(), PrintRequest.class);
            DrawerPrinter printer = new DrawerPrinter();
            printer.printPages(pr.convertToPages());
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            replyText(exchange, "done");
        });
        System.err.printf("Drawer-site server is listening to port %d\n", port);
        server.start();
    }

    private static void replyText(HttpExchange exchange, String reply) throws IOException {
        byte[] body = reply.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("content-type", "text/plain");
        exchange.sendResponseHeaders(200, body.length);
        exchange.getResponseBody().write(body);
        exchange.getRequestBody().close();

    }

    private static Executor createExecutor(){
        return Executors.newFixedThreadPool(6);
    }

    private static ObjectMapper createMapper(){
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Op.class, new JacksonOpSerializer());
        module.addDeserializer(Op.class, new JacksonOpDeserializer());
        mapper.registerModule(module);
        return mapper;
    }

    private static Path getDataDir(){
        return Path.of(System.getProperty("user.home"), "drawer-site-data");
    }

}
