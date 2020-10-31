package dev.myclinic.vertx.drawersite;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.function.Consumer;

public class Server {

    private final HttpServer httpServer;

    public Server(String bind, int port, int backlog) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress(bind, port), backlog);
    }

    public void start(){
        httpServer.start();
    }

    public void addContext(String path, Consumer<Handler> cb){
        httpServer.createContext(path, exchange -> {
            Handler handler = new Handler(exchange, path);
            cb.accept(handler);
        });
    }

}
