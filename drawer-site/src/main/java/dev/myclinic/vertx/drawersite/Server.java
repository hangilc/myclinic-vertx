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
    private String[] allowedOrigins;

    public Server(String bind, int port, int backlog) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress(bind, port), backlog);
        this.httpServer.setExecutor(createExecutor());
    }

    public void setAllowedOrigins(String allowedOrigins){
        this.allowedOrigins = allowedOrigins.split(",");
        for(String ori: this.allowedOrigins){
            System.out.printf("alloed-origin: %s\n", ori);
        }
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
                String allowedOrigin = null;
                if( exchange.getRequestHeaders().containsKey("Origin") ){
                    String origin = exchange.getRequestHeaders().getFirst("Origin");
                    for(String ori: allowedOrigins){
                        System.out.printf("allowed: %s, origin: %s\n", ori, origin);
                        if( "*".equals(ori) ){
                            allowedOrigin = "*";
                            break;
                        } else if( ori.equals(origin) ){
                            allowedOrigin = origin;
                            break;
                        }
                    }
                    if( allowedOrigin == null ){
                        throw new RuntimeException("CORS not allowed");
                    }
                }
                handler.setAllowedOrigins(allowedOrigin);
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
