package dev.myclinic.vertx.drawersite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.myclinic.vertx.drawer.JacksonOpDeserializer;
import dev.myclinic.vertx.drawer.JacksonOpSerializer;
import dev.myclinic.vertx.drawer.Op;
import dev.myclinic.vertx.drawer.PrintRequest;
import dev.myclinic.vertx.drawerprinterwin.AuxSetting;
import dev.myclinic.vertx.drawerprinterwin.DevmodeInfo;
import dev.myclinic.vertx.drawerprinterwin.DevnamesInfo;
import dev.myclinic.vertx.drawerprinterwin.DrawerPrinter;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
        server.addContext("/print-dialog/", Main::handlePrintDialog);
        server.addContext("/web/", handler -> {
            if (cmdArgs.isDev) {
                Path root = Path.of("./drawer-site/src/main/resources");
                handleWebFromFile(handler, root);
            } else {
                handleWebFromResource(handler);
            }
        });
        server.addContext("/", Main::handleRoot);
        server.start();
    }

    private static void handlePrintDialog(Handler handler) throws IOException {
        if (handler.getMethod().equals("GET")) {
            String[] subpaths = handler.getSubPaths();
            if (subpaths.length == 0) {
                DrawerPrinter printer = new DrawerPrinter();
                DrawerPrinter.DialogResult result = printer.printDialog();
                if (result.ok) {
                    PrintSetting setting = new PrintSetting(
                            result.devmodeData, result.devnamesData
                    );
                    handler.sendEncodedJson(setting.serialize(mapper));
                } else {
                    handler.sendJson(null);
                }
                return;
            }
            if (subpaths.length == 1) {
                String name = subpaths[0];
                PrintSetting current = getSetting(name);
                DrawerPrinter printer = new DrawerPrinter();
                DrawerPrinter.DialogResult result = printer.printDialog(
                        current.devmode, current.devnames
                );
                if (result.ok) {
                    PrintSetting setting = new PrintSetting(
                            result.devmodeData, result.devnamesData, current.auxSetting
                    );
                    handler.sendEncodedJson(setting.serialize(mapper));
                } else {
                    handler.sendJson(null);
                }
                return;
            }
        }
        handler.sendError("Invalid print-dialog access.");
    }

    private static void handleRoot(Handler handler) throws IOException {
        if (handler.getMethod().equals("GET")) {
            String path = handler.getPath();
            if (path.equals("/")) {
                handler.sendRedirect("/web/index.html");
            } else {
                handler.sendNotFound();
            }
        } else {
            handler.sendError("Invalid access.");
        }
    }

    private static void handleWebFromResource(Handler handler) throws IOException {
        if (handler.getMethod().equals("GET")) {
            String path = handler.getPath();
            handler.sendResource(path);
        } else {
            handler.sendError("Invalid web access.");
        }
    }

    private static void handleWebFromFile(Handler handler, Path root) throws IOException {
        if (handler.getMethod().equals("GET")) {
            String path = handler.getPath().substring(1); // remove leading slash char
            Path target = root.resolve(path);
            handler.sendFile(target.toString());
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
            case "PUT":
                handleSettingPUT(handler);
                break;
            case "OPTIONS":
                handler.respondToOptions(List.of("GET", "OPIONS"));
                break;
            default:
                handler.sendError("Invalid setting access.");
                break;
        }
    }

    public static class PrintSettingDetail {
        public String printer;
        public String paperSize;
        public String orientation;
        public String tray;
        public String quality;
    }

    private static void handleSettingGET(Handler handler) throws IOException {
        String[] subpaths = handler.getSubPaths();
        if (subpaths.length == 0) {
            handler.allowCORS();
            handler.sendJson(listPrintSetting());
            return;
        }
        if (subpaths.length == 1) {
            String name = subpaths[0];
            handler.sendJson(getSetting(name));
            return;
        }
        if (subpaths.length == 2 && subpaths[1].equals("detail")) {
            handler.allowCORS();
            String name = subpaths[0];
            PrintSetting setting = getSetting(name);
            PrintSettingDetail detail = new PrintSettingDetail();
            DevmodeInfo devmodeInfo = new DevmodeInfo(setting.devmode);
            detail.paperSize = devmodeInfo.getPaperSizeLabel();
            detail.orientation = devmodeInfo.getOrientationLabel();
            detail.tray = devmodeInfo.getDefaultSourceLabel();
            detail.quality = devmodeInfo.getPrintQualityLabel();
            DevnamesInfo devnamesInfo = new DevnamesInfo(setting.devnames);
            detail.printer = devnamesInfo.getDevice();
            handler.sendJson(detail);
            return;
        }
        handler.sendError("Invalid setting access.");
    }

    private static void handleSettingPOST(Handler handler) throws IOException {
        String[] subpaths = handler.getSubPaths();
        if (subpaths.length == 1) {
            String name = subpaths[0];
            if (settingExists(name)) {
                handler.sendError(String.format("%s はすでに存在します。", name));
            } else {
                handler.allowCORS();
                createSetting(name);
                handler.sendError("done");
            }
        } else {
            handler.sendError("Invalid setting access.");
        }
    }

    private static void handleSettingPUT(Handler handler) throws IOException {
        String[] subpaths = handler.getSubPaths();
        if (subpaths.length == 1) {
            String name = subpaths[0];
            if (!settingExists(name)) {
                handler.sendError("No such setting: " + name);
                return;
            }
            byte[] body = handler.getBody();
            PrintSetting setting = PrintSetting.deserialize(mapper, body);
            savePrintSetting(name, setting);
            handler.sendText("done");
            return;
        }
        handler.sendError("Invalid setting access.");
    }

    public static void handlePrint(Handler handler) throws IOException {
        if( handler.getMethod().equals("OPTIONS") ){
            handler.respondToOptions(List.of("POST", "OPTIONS"));
            return;
        }
        if (handler.getMethod().equals("POST")) {
            handler.allowCORS();
            PrintRequest pr = mapper.readValue(handler.getExchange().getRequestBody(),
                    PrintRequest.class);
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

    private static PrintSetting getSetting(String name) throws IOException {
        Path dir = getDataDir();
        Path file = dir.resolve(String.format("%s.setting", name));
        byte[] bytes = Files.readAllBytes(file);
        return PrintSetting.deserialize(mapper, bytes);

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
