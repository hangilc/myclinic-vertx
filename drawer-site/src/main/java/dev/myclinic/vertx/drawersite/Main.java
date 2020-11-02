package dev.myclinic.vertx.drawersite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import dev.myclinic.vertx.drawer.JacksonOpDeserializer;
import dev.myclinic.vertx.drawer.JacksonOpSerializer;
import dev.myclinic.vertx.drawer.Op;
import dev.myclinic.vertx.drawer.PrintRequest;
import dev.myclinic.vertx.drawerprinterwin.AuxSetting;
import dev.myclinic.vertx.drawerprinterwin.DrawerPrinter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Main {

    static ObjectMapper mapper = createMapper();

    public static void main(String[] args) throws Exception {
        String bind = "0.0.0.0";
        int port = 48080;
        Server server = new Server(bind, port, 6);
        System.out.printf("Drawer-site server is listening to %s:%d\n", bind, port);
        server.addContext("/ping", handler -> handler.sendText("pong"));
        server.addContext("/setting/", Main::handleSetting);
        server.addContext("/web/", Main::handleWeb);
        server.addContext("/", Main::handleRoot);
        server.start();
    }

    private static void handleRoot(Handler handler) throws IOException {
        if( handler.getMethod().equals("GET") ){
            String path = handler.getPath();
            if( path.equals("/") ){
                handler.sendRedirect("/web/index.html");
            } else {
                handler.sendNotFound();
            }
        } else {
            handler.sendError("Invalid access.");
        }
    }

    private static void handleWeb(Handler handler) throws IOException {
        if( handler.getMethod().equals("GET") ){
            String path = handler.getPath();
            handler.sendResource(path);
        } else {
            handler.sendError("Invalid web access.");
        }
    }

    private static void handleSetting(Handler handler) throws IOException {
        switch (handler.getMethod()) {
            case "GET":
                handleSettingGET(handler);
                break;
            case "POST":
                handleSettingPOST(handler);
                break;
            default:
                handler.sendError("Invalid setting access.");
                break;
        }
    }

    private static void handleSettingGET(Handler handler) throws IOException {
        String[] subpaths = handler.getSubPaths();
        if (subpaths.length == 0) {
            handler.sendJson(listPrintSetting());
        } else {
            handler.sendError("Invalid setting access.");
        }
    }

    private static void handleSettingPOST(Handler handler) throws IOException {
        String[] subpaths = handler.getSubPaths();
        if( subpaths.length == 1 ){
            String name = subpaths[0];
            if( settingExists(name) ){
                handler.sendError(String.format("%s はすでに存在します。", name));
            } else {
                createSetting(name);
                handler.sendError("done");
            }
        } else {
            System.out.println(handler.getExchange().getRequestURI().getPath());
            handler.sendError("Invalid setting access.");
        }
    }

    public static void main2(String[] args) throws Exception {
        int port = 48080;
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 4);
        server.setExecutor(createExecutor());
        server.createContext("/ping", exchange -> {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            replyText(exchange, "pong");
        });
        server.createContext("/print", exchange -> {
            PrintRequest pr = mapper.readValue(exchange.getRequestBody(), PrintRequest.class);
            DrawerPrinter printer = new DrawerPrinter();
            printer.printPages(pr.convertToPages());
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            replyText(exchange, "done");
        });
        server.createContext("/setting/", exchange -> {
            try {
                String path = exchange.getRequestURI().getPath().substring(9);
                String method = exchange.getRequestMethod();
                System.err.printf("enter /setting/ %s %s\n", method, path);
                if (method.equals("GET")) {
                    if (path.equals("")) {
                        List<String> settings = listPrintSetting();
                        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                        replyJson(exchange, settings);
                    } else {
                        replyNotFound(exchange);
                    }
                } else if (method.equals("POST")) {
                    if (path.contains("/")) {
                        replyJson(exchange, listPrintSetting());
                    } else {
                        String name = path;
                        DrawerPrinter drawerPrinter = new DrawerPrinter();
                        DrawerPrinter.DialogResult result = drawerPrinter.printDialog();
                        PrintSetting setting = new PrintSetting();
                        setting.devmode = result.devmodeData;
                        setting.devnames = result.devnamesData;
                        setting.auxSetting = new AuxSetting();
                        savePrintSetting(name, setting);
                        replyText(exchange, "done");
                    }
                } else {
                    replyNotFound(exchange);
                }
            } catch (Throwable e) {
                e.printStackTrace();
                replyError(exchange, e.toString());
            }
        });
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                exchange.getResponseHeaders().add("Location", "/web/index.html");
                exchange.sendResponseHeaders(308, 0);
                exchange.getResponseBody().close();
            } else {
                replyNotFound(exchange);
            }
        });
        server.createContext("/web/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            System.out.printf("accept web request %s\n", path);
            replyFile(exchange, path);
        });
        System.err.printf("Drawer-site server is listening to port %d\n", port);
        server.start();
    }

    private static void replyError(HttpExchange ex, String msg) {
        try {
            byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
            ex.sendResponseHeaders(500, bytes.length);
            ex.getResponseBody().write(bytes);
            ex.getResponseBody().close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private static void replyNotFound(HttpExchange ex) {
        try {
            ex.sendResponseHeaders(404, 0);
            ex.getResponseBody().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void replyFile(HttpExchange exchange, String path) {
        try {
            byte[] bytes = Objects.requireNonNull(
                    Main.class.getClassLoader().getResourceAsStream(path)).readAllBytes();
            String contentType;
            if (path.endsWith(".html")) {
                contentType = "text/html;charset=UTF-8";
            } else if (path.endsWith(".js")) {
                contentType = "text/javascript;charset=UTF-8";
            } else if (path.endsWith(".css")) {
                contentType = "text/css;charset=UTF-8";
            } else {
                throw new RuntimeException("Unknown file type");
            }
            exchange.getResponseHeaders().add("content-type", contentType);
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.getRequestBody().close();
            exchange.close();
        } catch (Throwable e) {
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
        } catch (Throwable e) {
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
        } catch (Throwable e) {
            try {
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private static Executor createExecutor() {
        return Executors.newFixedThreadPool(6);
    }

    private static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Op.class, new JacksonOpSerializer());
        module.addDeserializer(Op.class, new JacksonOpDeserializer());
        mapper.registerModule(module);
        return mapper;
    }

    private static Path getDataDir() {
        return Path.of(System.getProperty("user.home"), "drawer-site-data");
    }

    private static void savePrintSetting(String name, PrintSetting setting) throws IOException {
        Path dir = getDataDir();
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        Path file = dir.resolve(String.format("%s.setting", name));
        byte[] bytes = setting.serialize(mapper);
        Files.write(file, bytes);
    }

    private static void createSetting(String name) throws IOException {
        DrawerPrinter drawerPrinter = new DrawerPrinter();
        DrawerPrinter.DialogResult result = drawerPrinter.printDialog();
        PrintSetting setting = new PrintSetting();
        setting.devmode = result.devmodeData;
        setting.devnames = result.devnamesData;
        setting.auxSetting = new AuxSetting();
        savePrintSetting(name, setting);
    }

    private static List<String> listPrintSetting() throws IOException {
        Path dir = getDataDir();
        if (!Files.exists(dir)) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        String ext = ".setting";
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                String fname = path.getFileName().toString();
                if (fname.endsWith(ext)) {
                    result.add(fname.substring(0, fname.length() - ext.length()));
                }
            }
        }
        return result;
    }

    private static boolean settingExists(String name) throws IOException {
        List<String> list = listPrintSetting();
        return list.contains(name);
    }

}
