package dev.myclinic.vertx.drawersite;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Server {

    public interface HandlerFunc {
        void handle(Handler handler) throws Exception;
    }

    private final HttpServer httpServer;

    public Server(String bind, int port, int backlog) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress(bind, port), backlog);
        this.httpServer.setExecutor(createExecutor());
    }

    private Executor createExecutor() {
        return Executors.newFixedThreadPool(6);
    }

    public void start() {
        httpServer.start();
    }

    public void addContext(String path, HandlerFunc func) {
        httpServer.createContext(path, exchange -> {
            Handler handler = null;
            try {
                handler = new Handler(exchange, path);
                func.handle(handler);
            } catch (Throwable e) {
                e.printStackTrace();
                if( handler != null ) {
                    handler.sendError(e.getMessage());
                }
            }
        });
    }

}
