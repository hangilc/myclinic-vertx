package dev.myclinic.vertx.drawersite;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Handler {

    private final static ObjectMapper mapper = new ObjectMapper();
    private final HttpExchange exchange;
    private final String contextPath;
    private final Map<String, String> queryMap = new HashMap<>();

    public Handler(HttpExchange exchange, String contextPath) {
        this.exchange = exchange;
        this.contextPath = contextPath;
        parseQuery(exchange.getRequestURI().getQuery());
    }

    private void parseQuery(String query){
        if( query == null || query.isEmpty() ){
            return;
        }
        String[] parts = query.split("&");
        for(String part: parts){
            if( part.isEmpty() ){
                continue;
            }
            String[] subs = part.split("=");
            String key, value;
            if( subs.length == 1 ){
                key = subs[0];
                value = "";
            } else if( subs.length == 2 ){
                key = subs[0];
                value = subs[1];
            } else {
                throw new RuntimeException("Failed to parse query: " + query);
            }
            key = URLDecoder.decode(key, StandardCharsets.UTF_8);
            value = URLDecoder.decode(value, StandardCharsets.UTF_8);
            queryMap.put(key, value);
        }
    }

    public HttpExchange getExchange() {
        return exchange;
    }

    public String getMethod() {
        return getExchange().getRequestMethod();
    }

    public String getPath() {
        return getExchange().getRequestURI().getPath();
    }

    public String[] getSubPaths() {
        if (contextPath.endsWith("/")) {
            String subpath = exchange.getRequestURI().getPath().substring(contextPath.length());
            if (subpath.equals("")) {
                return new String[]{};
            } else {
                return subpath.split("/");
            }
        } else {
            return new String[]{};
        }
    }

    public byte[] getBody() throws IOException {
        return exchange.getRequestBody().readAllBytes();
    }

    public String getParam(String key){
        return queryMap.get(key);
    }

    public void allowCORS() {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
    }

    private void doError(Throwable e){
        try {
            String message = e.getMessage();
            byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("content-type", "text/plain;charset=UTF-8");
            exchange.sendResponseHeaders(500, bytes.length);
            exchange.getResponseBody().write(bytes);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public void respondToOptions(List<String> allowedMethods){
        try {
            exchange.getResponseHeaders().add("content-type", "text/plain");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods",
                    String.join(",", allowedMethods));
            exchange.sendResponseHeaders(204, -1);
            exchange.getResponseBody().close();
        } catch (Throwable e) {
            doError(e);
        } finally {
            exchange.close();
        }
    }

    public void send(InputStream is, long size, String contentType){
        try {
            exchange.getResponseHeaders().add("content-type", contentType);
            exchange.sendResponseHeaders(200, size);
            is.transferTo(exchange.getResponseBody());
            exchange.getResponseBody().close();
        } catch (Throwable e) {
            doError(e);
        } finally {
            exchange.close();
        }
    }

    public void send(byte[] body, String contentType) {
        try {
            exchange.getResponseHeaders().add("content-type", contentType);
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.getResponseBody().close();
        } catch (Throwable e) {
            doError(e);
        } finally {
            exchange.close();
        }
    }

    public void sendError(String message) {
        try {
            byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("content-type", "text/plain;charset=UTF-8");
            exchange.sendResponseHeaders(500, bytes.length);
            exchange.getResponseBody().write(bytes);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    public void sendNotFound() {
        try {
            exchange.sendResponseHeaders(404, 0);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    public void sendText(String text) {
        if (text == null) {
            text = "";
        }
        send(text.getBytes(StandardCharsets.UTF_8), "text/plain;charset=UTF-8");
    }

    public void sendJson(Object body) {
        try {
            send(mapper.writeValueAsBytes(body), "application/json");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            sendError(e.getMessage());
        }
    }

    public void sendEncodedJson(byte[] bytes){
        send(bytes, "application/json");
    }

    private static final Map<String, String> mimeMap = new HashMap<>();

    static {
        mimeMap.put(".html", "text/html;charset=UTF-8");
        mimeMap.put(".js", "text/javascript;charset=UTF-8");
        mimeMap.put(".css", "text/css;charset=UTF-8");
    }

    private String resolveContentType(String path){
        int index = path.lastIndexOf('.');
        if( index >= 0 ){
            String ext = path.substring(index);
            return mimeMap.getOrDefault(ext, "application/octet-stream");
        } else {
            return "application/octet-stream";
        }
    }

    public void sendResource(String path) throws IOException {
        byte[] bytes = Objects.requireNonNull(
                Main.class.getClassLoader().getResourceAsStream(path)).readAllBytes();
        send(bytes, resolveContentType(path));
    }

    public void sendFile(String path) throws IOException {
        File file = new File(path);
        long size = file.length();
        try(InputStream is = new FileInputStream(path)){
            send(is, size, resolveContentType(path));
        }
    }

    public void sendRedirect(String redirectPath){
        try {
            exchange.getResponseHeaders().add("Location", redirectPath);
            exchange.sendResponseHeaders(308, 0);
            exchange.getResponseBody().close();
        } catch(Throwable e){
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}
