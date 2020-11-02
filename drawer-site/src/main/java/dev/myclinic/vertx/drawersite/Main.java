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
        CmdArgs cmdArgs = CmdArgs.parse(args);
        String bind = "0.0.0.0";
        int port = 48080;
        Server server = new Server(bind, port, 6);
        System.out.printf("Drawer-site server is listening to %s:%d\n", bind, port);
        server.addContext("/ping", handler -> handler.sendText("pong"));
        server.addContext("/print/", Main::handlePrint);
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
            handler.allowCORS();
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
                handler.allowCORS();
                createSetting(name);
                handler.sendError("done");
            }
        } else {
            System.out.println(handler.getExchange().getRequestURI().getPath());
            handler.sendError("Invalid setting access.");
        }
    }

    public static void handlePrint(Handler handler) throws IOException {
        if( handler.getMethod().equals("POST") ){
            handler.allowCORS();
            PrintRequest pr = mapper.readValue(handler.getExchange().getRequestBody(), PrintRequest.class);
            DrawerPrinter printer = new DrawerPrinter();
            printer.printPages(pr.convertToPages());
            handler.sendText("done");
        } else {
            handler.sendError("Invalid print access.");
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
