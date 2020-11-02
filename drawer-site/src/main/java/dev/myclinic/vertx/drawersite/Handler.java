package dev.myclinic.vertx.drawersite;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Handler {

    private final HttpExchange exchange;
    private final String contextPath;
    private final static ObjectMapper mapper = new ObjectMapper();

    public Handler(HttpExchange exchange, String contextPath) {
        this.exchange = exchange;
        this.contextPath = contextPath;
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

    public void allowCORS() {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
    }

    public void send(byte[] body, String contentType) {
        try {
            exchange.getResponseHeaders().add("content-type", contentType);
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.getResponseBody().close();
        } catch (Throwable e) {
            try {
                String message = e.getMessage();
                byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("content-type", "text/plain;charset=UTF-8");
                exchange.sendResponseHeaders(500, bytes.length);
                exchange.getResponseBody().write(bytes);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
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

    private static final Map<String, String> mimeMap = new HashMap<>();

    static {
        mimeMap.put(".html", "text/html;charset=UTF-8");
        mimeMap.put(".js", "text/javascript;charset=UTF-8");
        mimeMap.put(".css", "text/css;charset=UTF-8");
    }

    public void sendResource(String path) throws IOException {
        byte[] bytes = Objects.requireNonNull(
                Main.class.getClassLoader().getResourceAsStream(path)).readAllBytes();
        String contentType;
        int index = path.lastIndexOf('.');
        if (index >= 0) {
            String ext = path.substring(index);
            contentType = mimeMap.getOrDefault(ext, "application/octet-stream");
        } else {
            contentType = "application/octet-stream";
        }
        send(bytes, contentType);
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
