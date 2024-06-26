package dev.myclinic.vertx.drawersite;

import com.fasterxml.jackson.core.type.TypeReference;
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
import dev.myclinic.vertx.scanner.ScanTask;
import dev.myclinic.vertx.scanner.Scanner;
import dev.myclinic.vertx.scanner.wia.Wia;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {

    static ObjectMapper mapper = createMapper();
    static String allowedOrigins = null;

    public static void main(String[] args) throws Exception {
        CmdArgs cmdArgs = CmdArgs.parse(args);
        Main.allowedOrigins = resolveAllowedOrigins(cmdArgs);
        String bind = cmdArgs.bind;
        // String bind = "127.0.0.1";
        int port = cmdArgs.port;
        Server server = new Server(bind, port, 6);
        server.setAllowedOrigins(Main.allowedOrigins);
        System.out.printf("Drawer-site server is listening to %s:%d\n", bind, port);
        server.addContext("/ping", handler -> handler.sendText("pong"));
        server.addContext("/print/", Main::handlePrint);
        server.addContext("/setting/", Main::handleSetting);
        server.addContext("/print-dialog/", Main::handlePrintDialog);
        server.addContext("/scanner/device/", Main::handleScannerDevice);
        server.addContext("/scanner/scan", Main::handleScannerScan);
        server.addContext("/scanner/mock-scan", Main::handleScannerMockScan);
        server.addContext("/scanner/image/", Main::handleScannerImage);
        server.addContext("/upload-job", UploadJobHandlers::createJob);
        server.addContext("/upload-job/", handler -> {
            String[] subpaths = handler.getSubPaths();
            if( subpaths.length == 0 ) { // "/upload-job/"
                UploadJobHandlers.listJobs(handler);
            } else if( subpaths.length == 1 ){ // "/upload-job/jobname
                UploadJobHandlers.handleJob(handler, subpaths[0]);
            } else {
                handler.sendError("Invalid access to /upload-job/");
            }
        });
        server.addContext("/beep", Main::handleBeep);
        server.addContext("/web/", handler -> {
            if (cmdArgs.isDev) {
                Path root = Path.of("./drawer-site/src/main/resources");
                handleWebFromFile(handler, root);
            } else {
                handleWebFromResource(handler);
            }
        });
        server.addContext("/pref/", Main::handlePref);
        server.addContext("/", Main::handleRoot);
        server.start();
    }

    private static void handleBeep(Handler handler) throws Exception {
        switch (handler.getMethod()) {
            case "OPTIONS": {
                handler.respondToOptions(List.of("GET", "OPTIONS"));
                break;
            }
            case "GET": {
                handler.allowCORS();
                handler.sendJson(true);
                java.awt.Toolkit.getDefaultToolkit().beep();
                break;
            }
            default: {
                handler.sendError("Invalid beep access.");
                break;
            }
        }
    }

    private static void handleScannerImage(Handler handler) throws Exception {
        switch (handler.getMethod()) {
            case "OPTIONS": {
                handler.respondToOptions(List.of("GET", "DELETE", "OPTIONS"));
                break;
            }
            case "GET": {
                String[] subpaths = handler.getSubPaths();
                if (subpaths.length == 0) { // "/scanner/image/"
                    handler.allowCORS();
                    handler.sendJson(listScannedImage());
                } else if (subpaths.length == 1) {  // "/scanner/image/scanned-image...jpg"
                    handler.allowCORS();
                    String filename = subpaths[0];
                    Path path = getScannedImage(filename);
                    handler.sendFile(path.toString());
                } else {
                    handler.sendError("Invalid setting access.");
                }
                break;
            }
            case "DELETE": {
                String[] subpaths = handler.getSubPaths();
                if (subpaths.length == 1) {
                    handler.allowCORS();
                    String filename = subpaths[0];
                    Path path = getScannedImage(filename);
                    Files.delete(path);
                    handler.sendJson(true);
                } else {
                    handler.sendError("Invalid setting access.");
                }
                break;
            }
            default: {
                handler.sendError("Invalid setting access.");
                break;
            }
        }
    }

    private static void handleScannerDevice(Handler handler) throws Exception {
        switch (handler.getMethod()) {
            case "OPTIONS": {
                handler.respondToOptions(List.of("GET", "OPTIONS"));
                break;
            }
            case "GET": {
                String[] subpaths = handler.getSubPaths();
                if (subpaths.length == 0) { // "/scanner/device/"
                    handler.allowCORS();
                    Scanner.coInitialize();
                    try {
                        List<Wia.Device> devices = Wia.listDevices();
                        handler.sendJson(devices);
                    } finally {
                        Scanner.coUninitialize();
                    }
                } else {
                    handler.sendError("Invalid scanner/device access");
                }
                break;
            }
            default: {
                handler.sendError("Invalid setting access.");
                break;
            }
        }
    }

    private static void handleScannerScan(Handler handler) throws Exception {
        switch (handler.getMethod()) {
            case "OPTIONS": {
                handler.respondToOptions(List.of("GET", "OPTIONS"));
                break;
            }
            case "GET": {
                handler.allowCORS();
                Scanner.coInitialize();
                try {
                    String deviceId = handler.getParam("device-id");
                    if (deviceId == null) {
                        List<Wia.Device> devices = Wia.listDevices();
                        if (devices.size() > 0) {
                            deviceId = devices.get(0).deviceId;
                        } else {
                            handler.sendError("スキャナーがみつかりません。");
                            return;
                        }
                    }
                    System.out.printf("deviceId: %s\n", deviceId);
                    int resolution = 100;
                    String resolutionParam = handler.getParam("resolution");
                    if (resolutionParam != null) {
                        resolution = Integer.parseInt(resolutionParam);
                    }
                    Path savePath = createScannedImagePath();
                    handler.getExchange().getResponseHeaders().add("Content-type", "plain/text");
                    handler.getExchange().getResponseHeaders().add("Access-control-expose-headers", "X-saved-image");
                    handler.getExchange().getResponseHeaders().add("X-saved-image", savePath.getFileName().toString());
                    handler.getExchange().sendResponseHeaders(200, 10);
                    int[] ints = new int[]{0};
                    ScanTask task = new ScanTask(deviceId, savePath, resolution, pct -> {
                        int i = pct / 10;
                        if (i > ints[0]) {
                            System.out.printf("ENTER %d\n", i);
                            for (int j = ints[0] + 1; j <= i; j++) {
                                try {
                                    handler.getExchange().getResponseBody().write("*".getBytes());
                                    handler.getExchange().getResponseBody().flush();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                ints[0] = j;
                            }
                        }
                    });
                    boolean hasError = false;
                    try {
                        task.run();
                    } catch(Exception e){
                        String msg = task.getErrorMessage();
                        if( msg == null ){
                            System.out.println(msg);
                        }
                        hasError = true;
                    }
                    String progressChar = hasError ? "X" : "*";
                    for (int j = ints[0] + 1; j <= 10; j++) {
                        try {
                            handler.getExchange().getResponseBody().write(progressChar.getBytes());
                            handler.getExchange().getResponseBody().flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } finally {
                    Scanner.coUninitialize();
                    handler.getExchange().close();
                }
                break;
            }
            default: {
                handler.sendError("Invalid setting access.");
                break;
            }
        }
    }

    private static void handleScannerMockScan(Handler handler) throws Exception {
        switch (handler.getMethod()) {
            case "OPTIONS": {
                handler.respondToOptions(List.of("GET", "OPTIONS"));
                break;
            }
            case "GET": {
                handler.getExchange().getResponseHeaders().add("content-type", "plain/text");
                handler.getExchange().sendResponseHeaders(200, 10);
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                    handler.getExchange().getResponseBody().write("*".getBytes());
                    handler.getExchange().getResponseBody().flush();
                }
                handler.getExchange().getResponseBody().close();
                handler.getExchange().close();
                break;
            }
            default: {
                handler.sendError("Invalid setting access.");
                break;
            }
        }
    }

    private static void handlePrintDialog(Handler handler) throws IOException {
        if (handler.getMethod().equals("GET")) {
            String[] subpaths = handler.getSubPaths();
            if (subpaths.length == 0) {
                handler.allowCORS();
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
                handler.allowCORS();
                String name = subpaths[0];
                PrintSetting current = getSetting(name);
                System.out.println(current.toString());
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
            case "DELETE":
                handleSettingDELETE(handler);
                break;
            case "OPTIONS":
                handler.respondToOptions(List.of("GET", "OPTIONS", "POST", "PUT", "DELETE"));
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
        public AuxSetting auxSetting;
    }

    private static void handleSettingGET(Handler handler) throws IOException {
        String[] subpaths = handler.getSubPaths();
        if (subpaths.length == 0) {
            handler.allowCORS();
            handler.sendJson(listPrintSetting());
            return;
        }
        if (subpaths.length == 1) {
            handler.allowCORS();
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
            detail.auxSetting = setting.auxSetting;
            handler.sendJson(detail);
            return;
        }
        if (subpaths.length == 2 && subpaths[1].equals("aux")) {
            handler.allowCORS();
            String name = subpaths[0];
            PrintSetting setting = getSetting(name);
            handler.sendJson(setting.auxSetting);
            return;
        }
        handler.sendError("Invalid setting access.");
    }

    private static void handleSettingPOST(Handler handler) throws IOException {
        String[] subpaths = handler.getSubPaths();
        if (subpaths.length == 1) {
            handler.allowCORS();
            String name = subpaths[0];
            if (settingExists(name)) {
                handler.sendError(String.format("%s はすでに存在します。", name));
            } else {
                createSetting(name);
                handler.sendJson(true);
            }
        } else {
            handler.sendError("Invalid setting access.");
        }
    }

    private static void handleSettingPUT(Handler handler) throws IOException {
        String[] subpaths = handler.getSubPaths();
        if (subpaths.length == 1) {
            handler.allowCORS();
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
        if (subpaths.length == 2 && subpaths[1].equals("aux")) {
            handler.allowCORS();
            String name = subpaths[0];
            if (!settingExists(name)) {
                handler.sendError("No such setting: " + name);
                return;
            }
            PrintSetting current = getSetting(name);
            byte[] body = handler.getBody();
            current.auxSetting = mapper.readValue(body, AuxSetting.class);
            savePrintSetting(name, current);
            handler.sendJson(true);
            return;
        }
        handler.sendError("Invalid setting access.");
    }

    private static void handleSettingDELETE(Handler handler) throws IOException {
        String[] subpaths = handler.getSubPaths();
        if (subpaths.length == 1) {
            handler.allowCORS();
            String name = subpaths[0];
            deleteSetting(name);
            handler.sendJson(true);
            return;
        }
        handler.sendError("Invalid setting access.");
    }

    private static void handlePrint(Handler handler) throws IOException {
        if (handler.getMethod().equals("OPTIONS")) {
            handler.respondToOptions(List.of("POST", "OPTIONS"));
            return;
        }
        if (handler.getMethod().equals("POST")) {
            handler.allowCORS();
            String[] subpaths = handler.getSubPaths();
            String setting = null;
            if (subpaths.length == 0) {
                // nop
            } else if (subpaths.length == 1) {
                setting = subpaths[0];
            } else {
                handler.sendError("Invalid print path");
                return;
            }
            PrintRequest pr = mapper.readValue(handler.getExchange().getRequestBody(),
                    PrintRequest.class);
            List<List<Op>> pages = pr.convertToPages();
            DrawerPrinter printer = new DrawerPrinter();
            if (setting == null) {
                System.out.println("start printing");
                printer.printPages(pages);
            } else {
                PrintSetting printSetting = getSetting(setting);
                printer.printPages(pages, printSetting.devmode, printSetting.devnames,
                        printSetting.auxSetting);
            }
            handler.sendJson(true);
        } else {
            handler.sendError("Invalid print access.");
        }
    }

    private static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Op.class, new JacksonOpSerializer());
        module.addDeserializer(Op.class, new JacksonOpDeserializer());
        mapper.registerModule(module);
        return mapper;
    }

    static Path getDataDir() throws IOException {
        Path dir = Path.of(System.getProperty("user.home"), "drawer-site-data");
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        return dir;
    }

    private static Path getScannedImagesDir() throws IOException {
        Path dataDir = getDataDir();
        Path dir = dataDir.resolve("scanned-images");
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        return dir;
    }

    private static Path createScannedImagePath() throws IOException {
        Path dir = getScannedImagesDir();
        return Files.createTempFile(dir, "scanned-image-", ".jpg");
    }

    private static List<String> listScannedImage() throws IOException {
        Path dir = getScannedImagesDir();
        String[] list = dir.toFile().list();
        if (list == null) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(list);
        }
    }

    private static Path getScannedImage(String filename) throws IOException {
        if (filename.contains("/") || filename.contains("\\")) {
            throw new RuntimeException("Invalid image file name.");
        }
        return getScannedImagesDir().resolve(filename);
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

    private static void deleteSetting(String name) throws IOException {
        Path dir = getDataDir();
        Path file = dir.resolve(String.format("%s.setting", name));
        Files.delete(file);
    }

    // pref ///////////////////////////////////////////////////////////////////////////

    private static void handlePref(Handler handler) throws IOException {
        if (handler.getMethod().equals("OPTIONS")) {
            handler.respondToOptions(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            return;
        }
        switch (handler.getMethod()) {
            case "GET":
                handlePrefGET(handler);
                break;
            case "POST":
                handlePrefPOST(handler);
                break;
            case "DELETE":
                handlePrefDELETE(handler);
                break;
            default: {
                handler.sendError("Invalid pref access");
                break;
            }
        }
    }

    private static void handlePrefGET(Handler handler) throws IOException {
        handler.allowCORS();
        String[] subpaths = handler.getSubPaths();
        if (subpaths.length == 0) {
            handler.sendJson(getPrefMap());
            return;
        }
        if (subpaths.length == 1) {
            String key = subpaths[0];
            String pref = getPref(key);
            handler.sendJson(pref);
            return;
        }
        handler.sendError("Invalid pref GET access");
    }

    private static void handlePrefPOST(Handler handler) throws IOException {
        handler.allowCORS();
        String[] subpaths = handler.getSubPaths();
        if (subpaths.length == 1) {
            String key = subpaths[0];
            String curr = getPref(key);
            String value = mapper.readValue(handler.getBody(), String.class);
            setPref(key, value);
            handler.sendJson(curr);
            return;
        }
        handler.sendError("Invalid pref POST access");
    }

    private static void handlePrefDELETE(Handler handler) throws IOException {
        handler.allowCORS();
        String[] subpaths = handler.getSubPaths();
        if (subpaths.length == 0) {
            var curr = getPrefMap();
            deleteAllPref();
            handler.sendJson(curr);
            return;
        }
        if (subpaths.length == 1) {
            String key = subpaths[0];
            var curr = getPref(key);
            deletePref(key);
            handler.sendJson(curr);
            return;
        }
        handler.sendError("Invalid pref DELETE access");
    }

    private static Path getPrefMapPath() throws IOException {
        Path dir = getDataDir();
        return dir.resolve("prefs.json");
    }

    private static Map<String, String> getPrefMap() throws IOException {
        Path file = getPrefMapPath();
        if (!Files.exists(file)) {
            return Collections.emptyMap();
        }
        return mapper.readValue(file.toFile(), new TypeReference<>() {
        });
    }

    private static void savePrefMap(Map<String, String> map) throws IOException {
        Path file = getPrefMapPath();
        mapper.writeValue(file.toFile(), map);
    }

    private static String getPref(String key) throws IOException {
        return getPrefMap().get(key);
    }

    private static void setPref(String key, String pref) throws IOException {
        Map<String, String> map = new HashMap<>(getPrefMap());
        map.put(key, pref);
        savePrefMap(map);
    }

    private static void deleteAllPref() throws IOException {
        Path file = getPrefMapPath();
        Files.delete(file);
    }

    private static void deletePref(String key) throws IOException {
        var map = getPrefMap();
        map.remove(key);
        savePrefMap(map);
    }

    private static String resolveAllowedOrigins(CmdArgs cmdArgs){
        String oa = cmdArgs.allowedOrigins;
        if( oa == null ){
            oa = System.getenv("MYCLINIC_DRAWER_SITE_ALLOWED_ORIGINS");
        }
        if( oa == null ){
            oa = "*";
        }
        return oa;
    }

}
