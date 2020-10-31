package dev.myclinic.vertx.drawersite;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Handler {

    private final HttpExchange exchange;
    private final String contextPath;

    public Handler(HttpExchange exchange, String contextPath) {
        this.exchange = exchange;
        this.contextPath = contextPath;
    }

    public HttpExchange getExchange() {
        return exchange;
    }

    public String getSubPath(){
        if( contextPath.endsWith("/") ){
            String path = exchange.getRequestURI().getPath();
            return path.substring(contextPath.length());
        } else {
            return "";
        }
    }

    public void send(byte[] body, String contentType){
        try {
            exchange.getResponseHeaders().add("content-type", contentType);
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.getResponseBody().close();
            exchange.close();
        } catch(Throwable e){
            body = e.toString().getBytes(StandardCharsets.UTF_8);
            exchange.getRequestHeaders().add("content-type", "text/plain;charset=UTF-8");
            try {
                exchange.sendResponseHeaders(500, body.length);
                exchange.getResponseBody().write(body);
                exchange.getResponseBody().close();
                exchange.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void sendText(String text){
        if( text == null ){
            text = "";
        }
        send(text.getBytes(StandardCharsets.UTF_8), "text/plain;charset=UTF-8");
    }
}
