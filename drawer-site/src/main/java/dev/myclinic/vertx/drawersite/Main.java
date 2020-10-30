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
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
            System.err.println("Accepted print request");
            PrintRequest pr = mapper.readValue(exchange.getRequestBody(), PrintRequest.class);
            DrawerPrinter printer = new DrawerPrinter();
            printer.printPages(pr.convertToPages());
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            replyText(exchange, "done");
        });
        server.createContext("/setting", exchange -> {
            List<String> settings = listPrintSetting();
            replyJson(exchange, settings);
        });
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            if( path.equals("/") ){
                replyHtml(exchange, "/web/index.html");
            } else {
                exchange.sendResponseHeaders(404, 0);
                exchange.getResponseBody().close();
            }
        });
        System.err.printf("Drawer-site server is listening to port %d\n", port);
        server.start();
    }

    private static List<String> listPrintSetting(){
        return Collections.emptyList();
    }

    private static void replyHtml(HttpExchange exchange, String path){
        try {
            byte[] bytes = Objects.requireNonNull(
                    Main.class.getClassLoader().getResourceAsStream(path)).readAllBytes();
            exchange.getResponseHeaders().add("content-type", "text/html;charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.getRequestBody().close();
        } catch(Throwable e){
            try {
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private static void replyJson(HttpExchange exchange, Object obj) {
        try {
        byte[] body = mapper.writeValueAsBytes(obj);
        exchange.getResponseHeaders().add("content-type", "application/json");
        exchange.sendResponseHeaders(200, body.length);
        exchange.getResponseBody().write(body);
        exchange.getRequestBody().close();
        } catch(Throwable e){
            try {
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private static void replyText(HttpExchange exchange, String reply) {
        try {
        byte[] body = reply.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("content-type", "text/plain");
        exchange.sendResponseHeaders(200, body.length);
        exchange.getResponseBody().write(body);
        exchange.getRequestBody().close();
        } catch(Throwable e){
            try {
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
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
