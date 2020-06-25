package dev.myclinic.vertx.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.Image;
import dev.myclinic.vertx.appconfig.AppConfig;
import dev.myclinic.vertx.appconfig.types.ShohousenGrayStampInfo;
import dev.myclinic.vertx.drawer.DrawerColor;
import dev.myclinic.vertx.drawer.Op;
import dev.myclinic.vertx.drawer.PaperSize;
import dev.myclinic.vertx.drawer.pdf.PdfPrinter;
import dev.myclinic.vertx.drawer.printer.DrawerPrinter;
import dev.myclinic.vertx.dto.*;
import dev.myclinic.vertx.mastermap.MasterMap;
import dev.myclinic.vertx.romaji.Romaji;
import dev.myclinic.vertx.shohousendrawer.ShohousenData;
import dev.myclinic.vertx.shohousendrawer.ShohousenDrawer;
import dev.myclinic.vertx.util.*;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

class NoDatabaseRestHandler extends RestHandlerBase implements Handler<RoutingContext> {

    private static final Logger logger = LoggerFactory.getLogger(NoDatabaseRestHandler.class);

    interface NoDatabaseRestFunction {
        void call(RoutingContext ctx) throws Exception;
    }

    private final Map<String, NoDatabaseRestFunction> noDatabaseFuncMap = new HashMap<>();

    private final AppConfig appConfig;
    private final Vertx vertx;
    private final ExecutorService executorService = Executors.newFixedThreadPool(6);

    NoDatabaseRestHandler(AppConfig appConfig, ObjectMapper mapper, Vertx vertx, MasterMap masterMap) {
        super(mapper, masterMap);
        this.appConfig = appConfig;
        this.vertx = vertx;
    }

    private String cacheListDiseaseExample;

    private void listDiseaseExample(RoutingContext ctx) throws Exception {
        if (cacheListDiseaseExample != null) {
            ctx.response().end(cacheListDiseaseExample);
        } else {
            HttpServerRequest req = ctx.request();
            appConfig.listDiseaseExample()
                    .onComplete(ar -> {
                        if (ar.failed()) {
                            ctx.fail(500, new RuntimeException("Cannot get disease examples."));
                        } else {
                            cacheListDiseaseExample = jsonEncode(ar.result());
                            req.response().end(cacheListDiseaseExample);
                        }
                    });
        }
    }

    private List<String> implListHokensho(String storageDir, int patientId) throws IOException {
        String pat = String.format("glob:%d-hokensho-*.{jpg,jpeg,bmp}", patientId);
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher(pat);
        Path patientDir = Paths.get(storageDir, "" + patientId);
        if (Files.exists(patientDir) && Files.isDirectory(patientDir)) {
            return Files.list(patientDir)
                    .filter(p -> matcher.matches(p.getFileName()))
                    .map(p -> p.getFileName().toString())
                    .collect(toList());
        } else {
            return Collections.emptyList();
        }
    }

    private void listHokensho(RoutingContext ctx) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int patientId = Integer.parseInt(params.get("patient-id"));
        appConfig.getPaperScanDirectory()
                .onSuccess(scanDir -> {
                    vertx.<List<String>>executeBlocking(
                            promise -> {
                                try {
                                    List<String> hokenList = implListHokensho(scanDir, patientId);
                                    promise.complete(hokenList);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            },
                            ar2 -> {
                                if (ar2.failed()) {
                                    logger.error("Failed to list hokensho.", ar2.cause());
                                    ctx.response().setStatusCode(500).end("Failed to list hokensho.");
                                } else {
                                    ctx.response().end(jsonEncode(ar2.result()));
                                }
                            }
                    );
                })
                .onFailure(e -> {
                    ctx.fail(500, e);
                });
    }

    private String cacheClinicInfo;

    private void getClinicInfo(RoutingContext ctx) throws Exception {
        if (cacheClinicInfo != null) {
            ctx.response().end(cacheClinicInfo);
        } else {
            HttpServerRequest req = ctx.request();
            appConfig.getClinicInfo()
                    .onSuccess(dto -> {
                        cacheClinicInfo = jsonEncode(dto);
                        ctx.response().end(cacheClinicInfo);
                    })
                    .onFailure(e ->
                            ctx.fail(5000, new RuntimeException("Failed to get clinic info."))
                    );
        }
    }

    private String cacheGetMasterMapConfigFilePath;

    private void getMasterMapConfigFilePath(RoutingContext ctx) throws Exception {
        if (cacheGetMasterMapConfigFilePath != null) {
            ctx.response().end(cacheGetMasterMapConfigFilePath);
        } else {
            appConfig.getMasterMapConfigFilePath()
                    .onSuccess(path -> {
                        StringResultDTO dto = new StringResultDTO();
                        dto.value = path;
                        ctx.response().end(jsonEncode(dto));
                    })
                    .onFailure(e -> {
                                ctx.response()
                                        .setStatusCode(404).end("Failed to get location of master map file.");
                            }
                    );
        }
    }

    private void getShinryouByoumeiMapConfigFilePath(RoutingContext ctx) throws Exception {
        appConfig.getShinryouByoumeiMapConfigFilePath()
                .onSuccess(path -> {
                    StringResultDTO dto = new StringResultDTO();
                    dto.value = path;
                    ctx.response().end(jsonEncode(dto));
                })
                .onFailure(e -> {
                    ctx.fail(500, e);
                });
    }

    private static Map<String, String> mimeMap = new HashMap<>();

    {
        mimeMap.put("jpg", "image/jpeg");
        mimeMap.put("jpeg", "image/jpeg");
        mimeMap.put("png", "image/png");
        mimeMap.put("gif", "image/gif");
        mimeMap.put("pdf", "application/pdf");
    }

    private static Pattern fileExtPattern = Pattern.compile("\\.([^.]+)$");

    private String getFileExtension(String file) {
        Matcher m = fileExtPattern.matcher(file);
        if (m.find()) {
            return m.group(1);
        } else {
            return null;
        }
    }

    private void getHokensho(RoutingContext ctx) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int patientId = Integer.parseInt(params.get("patient-id"));
        String file = params.get("file");
        String ext = getFileExtension(file);
        if (ext == null) {
            throw new RuntimeException("Cannot find file extension.");
        }
        String mime = mimeMap.get(ext);
        if (mime == null) {
            throw new RuntimeException("Invalid file extension.");
        }
        appConfig.getPaperScanDirectory()
                .onSuccess(storageDir -> {
                    String fullPath = Paths.get(storageDir, "" + patientId, file).toString();
                    ctx.response()
                            .putHeader("content-type", mime)
                            .sendFile(fullPath);
                })
                .onFailure(e -> ctx.fail(5000, e));
    }

    private void getReferList(RoutingContext ctx) throws Exception {
        appConfig.getReferList()
                .onSuccess(result -> {
                    ctx.response().end(jsonEncode(result));
                })
                .onFailure(e -> ctx.fail(500, e));
    }

    private void getNameMapConfigFilePath(RoutingContext ctx) throws Exception {
        appConfig.getNameMapConfigFilePath()
                .onSuccess(path -> {
                    StringResultDTO dto = new StringResultDTO();
                    dto.value = path;
                    ctx.response().end(jsonEncode(dto));
                })
                .onFailure(e -> ctx.fail(500, e));
    }

    private void getPowderDrugConfigFilePath(RoutingContext ctx) throws Exception {
        appConfig.getPowderDrugConfigFilePath()
                .onSuccess(path -> {
                    StringResultDTO dto = new StringResultDTO();
                    dto.value = path;
                    ctx.response().end(jsonEncode(dto));
                })
                .onFailure(e -> ctx.fail(500, e));
    }

    private void getPracticeConfig(RoutingContext ctx) throws Exception {
        appConfig.getPracticeConfig()
                .onSuccess(config -> ctx.response().end(jsonEncode(config)))
                .onFailure(e -> ctx.fail(500, e));
    }

    {
        noDatabaseFuncMap.put("list-disease-example", this::listDiseaseExample);
        noDatabaseFuncMap.put("list-hokensho", this::listHokensho);
        noDatabaseFuncMap.put("get-clinic-info", this::getClinicInfo);
        noDatabaseFuncMap.put("get-master-map-config-file-path", this::getMasterMapConfigFilePath);
        noDatabaseFuncMap.put("get-shinryou-byoumei-map-config-file-path", this::getShinryouByoumeiMapConfigFilePath);
        noDatabaseFuncMap.put("get-hokensho", this::getHokensho);
        noDatabaseFuncMap.put("get-refer-list", this::getReferList);
        noDatabaseFuncMap.put("get-name-map-config-file-path", this::getNameMapConfigFilePath);
        noDatabaseFuncMap.put("get-powder-drug-config-file-path", this::getPowderDrugConfigFilePath);
        noDatabaseFuncMap.put("get-practice-config", this::getPracticeConfig);
        noDatabaseFuncMap.put("hoken-rep", this::hokenRep);
        noDatabaseFuncMap.put("shahokokuho-rep", this::shahokokuhoRep);
        noDatabaseFuncMap.put("koukikourei-rep", this::koukikoureiRep);
        noDatabaseFuncMap.put("roujin-rep", this::roujinRep);
        noDatabaseFuncMap.put("kouhi-rep", this::kouhiRep);
        noDatabaseFuncMap.put("shohousen-drawer", this::shohousenDrawer);
        noDatabaseFuncMap.put("calc-rcpt-age", this::calcRcptAge);
        noDatabaseFuncMap.put("calc-futan-wari", this::calcFutanWari);
        noDatabaseFuncMap.put("print-drawer", this::printDrawer);
        noDatabaseFuncMap.put("save-drawer-as-pdf", this::saveDrawerAsPdf);
        noDatabaseFuncMap.put("save-shohousen-pdf", this::saveShohousenPdf);
        noDatabaseFuncMap.put("get-shohousen-save-pdf-path", this::getShohousenSavePdfPath);
        noDatabaseFuncMap.put("convert-to-romaji", this::convertToRomaji);
        noDatabaseFuncMap.put("shohousen-gray-stamp-info", this::shohousenGrayStampInfo);
        noDatabaseFuncMap.put("send-fax", this::sendFax);
        noDatabaseFuncMap.put("poll-fax", this::pollFax);
        noDatabaseFuncMap.put("probe-shohousen-fax-image", this::probeShohousenFaxImage);
        noDatabaseFuncMap.put("show-pdf", this::showPdf);
        noDatabaseFuncMap.put("list-shujii-patient", this::listShujiiPatient);
        noDatabaseFuncMap.put("get-shujii-master-text", this::getShujiiMasterText);
        noDatabaseFuncMap.put("save-shujii-master-text", this::saveShujiiMasterText);
        noDatabaseFuncMap.put("save-printer-setting", this::savePrinterSetting);
        noDatabaseFuncMap.put("list-printer-setting", this::listPrinterSetting);
    }

    private void listPrinterSetting(RoutingContext ctx) {
        try {
            String dir = System.getenv("MYCLINIC_PRINTER_SETTINGS_DIR");
            if( dir == null || dir.isEmpty() ){
                throw new RuntimeException("Missing env var: MYCLINIC_PRINTER_SETTINGS_DIR");
            }
            List<String> names = Files.list(Path.of(dir)).filter(path -> path.getFileName().endsWith(".devmode"))
                    .map(path -> path.getFileName().toString().replaceAll("\\.devmode$", ""))
                    .collect(toList());
            ctx.response().end(jsonEncode(names));
        } catch(Exception e){
            ctx.fail(e);
        }
    }

    private void savePrinterSetting(RoutingContext ctx) {
        try {
            String dir = System.getenv("MYCLINIC_PRINTER_SETTINGS_DIR");
            if( dir == null || dir.isEmpty() ){
                throw new RuntimeException("Missing env var: MYCLINIC_PRINTER_SETTINGS_DIR");
            }
            String name = ctx.request().getParam("name");
            if( name == null || name.isEmpty() ){
                throw new RuntimeException("Missing parameter: name");
            }
            executorService.execute(() -> {
                DrawerPrinter printer = new DrawerPrinter();
                DrawerPrinter.DialogResult result = printer.printDialog(null, null);
                if( result.ok ){
                    try {
                        Path devFile = Path.of(dir, name + ".devmode");
                        Files.write(devFile, result.devmodeData);
                        Path namesFile = Path.of(dir, name + ".devnames");
                        Files.write(namesFile, result.devnamesData);
                        Path jsonFile = Path.of(dir, name + ".json");
                        Files.write(jsonFile, "{}".getBytes());
                    } catch (IOException e) {
                        logger.error("Files.write failed.", e);
                    }
                }
            });
            ctx.response().end("true");
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    private void saveShujiiMasterText(RoutingContext ctx) {
        vertx.<String>executeBlocking(promise -> {
            try {
                String name = ctx.request().getParam("name");
                if( name == null || name.isEmpty() ){
                    throw new RuntimeException("Missing parameter: name");
                }
                String text = this.mapper.readValue(ctx.getBody().getBytes(), String.class);
                String shujiiDir = System.getenv("MYCLINIC_SHUJII_DIR");
                if( shujiiDir == null ){
                    throw new RuntimeException("Cannot find env var MYCLINIC_SHUJII_DIR.");
                }
                Path patientDir = Path.of(shujiiDir, name);
                if( !Files.isDirectory(patientDir) ){
                    //noinspection ResultOfMethodCallIgnored
                    patientDir.toFile().mkdirs();
                }
                Path patientFile = patientDir.resolve(name + ".txt");
                Files.write(patientFile, text.getBytes(StandardCharsets.UTF_8));
                promise.complete("true");
            } catch(Exception e){
                throw new RuntimeException(e);
            }
        }, ar -> {
            if( ar.succeeded() ){
                ctx.response().end(ar.result());
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    private void getShujiiMasterText(RoutingContext ctx) {
        try {
            String shujiiDir = System.getenv("MYCLINIC_SHUJII_DIR");
            if( shujiiDir == null ){
                throw new RuntimeException("Cannot find env var MYCLINIC_SHUJII_DIR.");
            }
            PatientDTO patient = this.mapper.readValue(ctx.getBody().getBytes(), PatientDTO.class);
            String patientName = patient.lastName + patient.firstName;
            Path patientDir = Path.of(shujiiDir, patientName);
            Path masterPath = patientDir.resolve(patientName + ".txt");
            if( !(Files.exists(masterPath)) ){
                ctx.response().end(jsonEncode(null));
            } else {
                ctx.response().end(jsonEncode(readFileContent(masterPath)));
            }
        } catch(Exception e){
            ctx.fail(e);
        }
    }

    private List<String> readTextFile(Path path) throws Exception{
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch(MalformedInputException ex){
            return Files.readAllLines(path, Charset.defaultCharset());
        }
    }

    private String readFileContent(Path path) throws Exception {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch(MalformedInputException ex){
            return Files.readString(path, Charset.defaultCharset());
        }
    }

    private void listShujiiPatient(RoutingContext ctx) {
        Pattern patientIdPat = Pattern.compile("\\((\\d+)\\)");
        vertx.<String>executeBlocking(promise -> {
            try {
                String shujiiDir = System.getenv("MYCLINIC_SHUJII_DIR");
                if( shujiiDir == null ){
                    throw new RuntimeException("Cannot find env var MYCLINIC_SHUJII_DIR.");
                }
                List<Integer> patientIds = new ArrayList<>();
                for(Path path : Files.list(Path.of(shujiiDir)).collect(toList())){
                    String name = path.getFileName().toString();
                    if( "arch".equals(name) ){
                        continue;
                    }
                    if( !Files.isDirectory(path) ){
                        continue;
                    }
                    String repFile = name + ".txt";
                    for(Path subpath : Files.list(path).collect(toList())){
                        String subname = subpath.getFileName().toString();
                        int patientId = 0;
                        if( repFile.equals(subname) ){
                            List<String> lines = readTextFile(subpath);
                            String firstLine = null;
                            for(String line: lines){
                                line = line.trim();
                                if( !"".equals(line) ){
                                    firstLine = line;
                                    break;
                                }
                            }
                            if( firstLine == null ){
                                throw new RuntimeException("Empty file: " + subpath.toString());
                            }
                            Matcher m = patientIdPat.matcher(firstLine);
                            if( m.find() ){
                                patientId = Integer.parseInt(m.group(1));
                                if( !firstLine.contains(name) ){
                                    throw new RuntimeException("Cannot find patient name: " + subpath.toString());
                                }
                                patientIds.add(patientId);
                            } else {
                                throw new RuntimeException("Cannot find patientId: " + subpath.toString());
                            }
                            break;
                        }
                    }
                }
                promise.complete(jsonEncode(patientIds));
            } catch(Exception e){
                throw new RuntimeException(e);
            }

        }, ar -> {
            if( ar.succeeded() ){
                ctx.response().end(ar.result());
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    private void showPdf(RoutingContext ctx) {
        String pdfFile = ctx.request().getParam("file");
        if (pdfFile == null) {
            throw new RuntimeException("Missing parameter: file");
        }
        if (!new File(pdfFile).exists()) {
            throw new RuntimeException("No such file: " + pdfFile);
        }
        ctx.response().putHeader("content-type", "application/pdf");
        ctx.response().sendFile(pdfFile);
    }

    private void probeShohousenFaxImage(RoutingContext ctx) {
        String textIdPara = ctx.request().getParam("text-id");
        String date = ctx.request().getParam("date");
        String dir = System.getenv("MYCLINIC_SHOHOUSEN_DIR");
        if (dir == null) {
            throw new RuntimeException("Cannot find env var: MYCLINIC_SHOHOUSEN_DIR");
        }
        String month = date.substring(0, 7);
        Path shohousenDir = Path.of(dir, month);
        Pattern pat = Pattern.compile(String.format("^[a-zA-Z]+-%s-.+\\.pdf$", textIdPara));
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(shohousenDir)) {
            for (Path path : stream) {
                String name = path.getFileName().toString();
                if (pat.matcher(name).matches()) {
                    ctx.response().end(jsonEncode(path.toFile().getAbsolutePath()));
                    return;
                }
            }
        } catch (IOException e) {
            ctx.fail(e);
        }
        ctx.response().end("null");
    }

    private void sendFax(RoutingContext ctx) {
        String faxNumber = ctx.request().getParam("fax-number"); // "+8133335..."
        String pdfFile = ctx.request().getParam("pdf-file");
        if (faxNumber == null) {
            throw new RuntimeException("fax-number parameter is missing");
        }
        if (pdfFile == null) {
            throw new RuntimeException("pdf-file parameter is missing");
        }
        vertx.<String>executeBlocking(promise -> {
            EventBus bus = vertx.eventBus();
            SendFax.send(faxNumber, pdfFile, msg -> {
                        bus.send("fax-streaming", msg);
                        logger.info("Fax {} {} {}", msg, faxNumber, pdfFile);
                    },
                    faxSid -> promise.complete(jsonEncode(faxSid)),
                    vertx);
        }, arr -> {
            if (arr.succeeded()) {
                ctx.response().end(arr.result());
            } else {
                ctx.fail(arr.cause());
            }
        });
    }

    private void pollFax(RoutingContext ctx) {
        String faxSid = ctx.request().getParam("fax-sid");
        if (faxSid == null) {
            throw new RuntimeException("Missing parameter: fax-sid");
        }
        String status = SendFax.pollStatus(faxSid);
        ctx.response().end(jsonEncode(status));
    }

    private void shohousenGrayStampInfo(RoutingContext ctx) {
        try {
            var info = appConfig.getShohousenGrayStampInfo();
            String rep = mapper.writeValueAsString(info);
            ctx.response().end(rep);
        } catch (Exception e) {
            ctx.fail(e);
        }
    }

    private void convertToRomaji(RoutingContext ctx) {
        String text = ctx.request().getParam("text");
        if (text == null) {
            throw new RuntimeException("Missing parameter (text).");
        }
        String romaji = Romaji.toRomaji(text);
        StringResultDTO result = new StringResultDTO();
        result.value = romaji;
        try {
            String json = mapper.writeValueAsString(result);
            ctx.response().end(json);
        } catch (Exception e) {
            ctx.fail(e);
        }
    }

    private String composeShohousenSavePdfPath(String name, int textId, int patientId,
                                               LocalDate date) {
        String nameRomaji = name;
        if (isNotRomaji(nameRomaji)) {
            nameRomaji = Romaji.toRomaji(nameRomaji);
        }
        String dir = System.getenv("MYCLINIC_SHOHOUSEN_DIR");
        if (dir == null) {
            throw new RuntimeException("Cannot find env var: MYCLINIC_SHOHOUSEN_DIR");
        }
        String month = date.toString().substring(0, 7);
        String file = String.format("%s-%d-%d-%s-stamped.pdf", nameRomaji,
                textId, patientId, date.toString().replace("-", ""));
        return Path.of(dir, month, file).toString();
    }

    private boolean isNotRomaji(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
                return true;
            }
        }
        return false;
    }

    private void getShohousenSavePdfPath(RoutingContext ctx) {
        String textId = ctx.request().getParam("text-id");
        String patientId = ctx.request().getParam("patient-id");
        String name = ctx.request().getParam("name");
        String date = ctx.request().getParam("date");
        if (!(textId != null && patientId != null && name != null && date != null)) {
            throw new RuntimeException("Missing parameter");
        }
        if (isNotRomaji(name)) {
            name = Romaji.toRomaji(name);
        }
        String pdfPath = composeShohousenSavePdfPath(name, Integer.parseInt(textId),
                Integer.parseInt(patientId), LocalDate.parse(date));
        String mkdir = ctx.request().getParam("mkdir");
        Path path = Path.of(pdfPath);
        if ("true".equals(mkdir)) {
            //noinspection ResultOfMethodCallIgnored
            path.toFile().getParentFile().mkdirs();
        }
        ctx.response().end(jsonEncode(path.toString()));
    }

    private PaperSize resolvePaperSize(String arg) {
        if (PaperSize.standard.containsKey(arg)) {
            return PaperSize.standard.get(arg);
        } else {
            String[] parts = arg.split(",");
            if (parts.length == 2) {
                double width = Double.parseDouble(parts[0].trim());
                double height = Double.parseDouble(parts[1].trim());
                return new PaperSize(width, height);
            } else {
                throw new RuntimeException("Invalid paper size: " + arg);
            }
        }
    }

    public static class StampRequest {
        public String path;
        public double scale;
        public double offsetX;
        public double offsetY;
    }

    public static class SaveDrawerAsPdfRequest {
        public List<List<Op>> pages;
        public String paperSize;
        public String savePath;
        public StampRequest stamp;
    }

    private void doSaveDrawerAsPdf(SaveDrawerAsPdfRequest req) {
        PaperSize paperSize = resolvePaperSize(req.paperSize);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PdfPrinter printer = new PdfPrinter(paperSize);
        PdfPrinter.Callback callback = null;
        if (req.stamp != null) {
            StampRequest stamp = req.stamp;
            callback = (cb, page, graphicMode, textMode) -> {
                graphicMode.run();
                Image image = Image.getInstance(stamp.path);
                image.scalePercent((float) (stamp.scale * 100));
                image.setAbsolutePosition((float) stamp.offsetX, (float) stamp.offsetY);
                cb.addImage(image);
            };
        }
        try {
            printer.print(req.pages, outStream, callback);
            byte[] pdfBytes = outStream.toByteArray();
            Files.write(Path.of(req.savePath), pdfBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveDrawerAsPdf(RoutingContext ctx) {
        vertx.<String>executeBlocking(promise -> {
            try {
                byte[] bytes = ctx.getBody().getBytes();
                SaveDrawerAsPdfRequest req = mapper.readValue(bytes, SaveDrawerAsPdfRequest.class);
                doSaveDrawerAsPdf(req);
                promise.complete("true");
            } catch (Exception e) {
                ctx.fail(e);
            }
        }, ar -> {
            if (ar.succeeded()) {
                ctx.response().end(ar.result());
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    private void printDrawer(RoutingContext ctx) {
        executorService.execute(() -> {
            try {
                byte[] bytes = ctx.getBody().getBytes();
                List<List<Op>> pages = mapper.readValue(bytes, new TypeReference<>() {
                });
                DrawerPrinter printer = new DrawerPrinter();
                printer.printPages(pages);
            } catch (Exception e) {
                logger.error("print-drawer failed", e);
            }
        });
        ctx.response().end("true");
    }

    private void calcRcptAge(RoutingContext ctx) {
        try {
            String birthdayArg = ctx.request().getParam("birthday");
            String atArg = ctx.request().getParam("at");
            LocalDate birthday = LocalDate.parse(birthdayArg);
            LocalDate at = LocalDate.parse(atArg);
            int age = HokenUtil.calcRcptAge(birthday.getYear(), birthday.getMonthValue(),
                    birthday.getDayOfMonth(), at.getYear(), at.getMonthValue());
            ctx.response().end(String.format("%d", age));
        } catch (Exception e) {
            ctx.fail(e);
        }
    }

    private void calcFutanWari(RoutingContext ctx) {
        try {
            byte[] bytes = ctx.getBody().getBytes();
            CalcFutanWariRequestDTO req = mapper.readValue(bytes, CalcFutanWariRequestDTO.class);
            int futanWari = HokenUtil.calcFutanWari(req.hoken, req.rcptAge);
            ctx.response().end(String.format("%d", futanWari));
        } catch (Exception e) {
            ctx.fail(e);
        }
    }

    public static class ShohousenRequest {
        public ClinicInfoDTO clinicInfo;
        public HokenDTO hoken;
        public Integer futanWari;
        public PatientDTO patient;
        public String drugs;
        public String issueDate;
        public String validUpto;
        public String color;
    }

    private ShohousenData convertToShohousenData(ShohousenRequest req) {
        ShohousenData data = new ShohousenData();
        if (req.clinicInfo != null) {
            data.setClinicInfo(req.clinicInfo);
        }
        if (req.hoken != null) {
            data.setHoken(req.hoken);
        }
        if (req.futanWari != null) {
            data.setFutanWari(req.futanWari);
        }
        if (req.patient != null) {
            data.setPatient(req.patient);
        }
        if (req.drugs != null) {
            data.setDrugs(req.drugs);
        }
        if (req.issueDate != null) {
            LocalDate date = LocalDate.parse(req.issueDate);
            data.setKoufuDate(date);
        } else {
            data.setKoufuDate(LocalDate.now());
        }
        if (req.validUpto != null) {
            LocalDate date = LocalDate.parse(req.validUpto);
            data.setValidUptoDate(date);
        }
        return data;
    }

    private void shohousenDrawer(RoutingContext ctx) {
        try {
            byte[] bytes = ctx.getBody().getBytes();
            ShohousenRequest req = mapper.readValue(bytes, ShohousenRequest.class);
            ShohousenData data = convertToShohousenData(req);
            ShohousenDrawer drawer = new ShohousenDrawer();
            if (req.color != null) {
                DrawerColor defaultColor = DrawerColor.resolve(req.color);
                drawer.setDefaultColor(defaultColor);
            }
            drawer.init();
            data.applyTo(drawer);
            List<Op> ops = drawer.getOps();
            ctx.response().end(jsonEncode(ops));
        } catch (Exception e) {
            ctx.fail(e);
        }
    }

    private void saveShohousenPdf(RoutingContext ctx) {
        vertx.<String>executeBlocking(promise -> {
            try {
                String textIdArg = ctx.request().getParam("text-id");
                if (textIdArg == null) {
                    throw new RuntimeException("text-id is missing");
                }
                int textId = Integer.parseInt(textIdArg);
                byte[] bytes = ctx.getBody().getBytes();
                ShohousenRequest req = mapper.readValue(bytes, ShohousenRequest.class);
                if (req.patient == null) {
                    throw new RuntimeException("patient info is missing");
                }
                if (req.issueDate == null) {
                    throw new RuntimeException("issue date is missing");
                }
                ShohousenData data = convertToShohousenData(req);
                ShohousenDrawer drawer = new ShohousenDrawer();
                if (req.color != null) {
                    DrawerColor defaultColor = DrawerColor.resolve(req.color);
                    drawer.setDefaultColor(defaultColor);
                }
                drawer.init();
                data.applyTo(drawer);
                List<Op> ops = drawer.getOps();
                String savePath = composeShohousenSavePdfPath(
                        req.patient.lastNameYomi + req.patient.firstNameYomi,
                        textId,
                        req.patient.patientId,
                        LocalDate.parse(req.issueDate)
                );
                SaveDrawerAsPdfRequest saveReq = new SaveDrawerAsPdfRequest();
                saveReq.pages = List.of(ops);
                saveReq.paperSize = "A5";
                saveReq.savePath = savePath;
                ShohousenGrayStampInfo stampInfo = appConfig.getShohousenGrayStampInfo();
                StampRequest stampReq = new StampRequest();
                stampReq.path = stampInfo.path;
                stampReq.scale = stampInfo.scale;
                stampReq.offsetX = stampInfo.offsetX;
                stampReq.offsetY = stampInfo.offsetY;
                saveReq.stamp = stampReq;
                doSaveDrawerAsPdf(saveReq);
                promise.complete(jsonEncode(savePath));
            } catch (Exception e) {
                promise.fail(e);
            }
        }, ar -> {
            if (ar.succeeded()) {
                ctx.response().end(ar.result());
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    private void hokenRep(RoutingContext ctx) {
        try {
            HokenDTO hoken = mapper.readValue(ctx.getBodyAsString().getBytes(), HokenDTO.class);
            String rep = mapper.writeValueAsString(HokenUtil.hokenRep(hoken));
            ctx.response().end(rep);
        } catch (Exception e) {
            ctx.fail(e);
        }
    }

    private void shahokokuhoRep(RoutingContext ctx){
        try {
            ShahokokuhoDTO hoken = mapper.readValue(ctx.getBodyAsString().getBytes(), ShahokokuhoDTO.class);
            String rep = mapper.writeValueAsString(ShahokokuhoUtil.rep(hoken));
            ctx.response().end(rep);
        } catch (Exception e) {
            ctx.fail(e);
        }
    }

    private void koukikoureiRep(RoutingContext ctx){
        try {
            KoukikoureiDTO hoken = mapper.readValue(ctx.getBodyAsString().getBytes(), KoukikoureiDTO.class);
            String rep = mapper.writeValueAsString(KoukikoureiUtil.rep(hoken));
            ctx.response().end(rep);
        } catch (Exception e) {
            ctx.fail(e);
        }
    }

    private void roujinRep(RoutingContext ctx){
        try {
            RoujinDTO hoken = mapper.readValue(ctx.getBodyAsString().getBytes(), RoujinDTO.class);
            String rep = mapper.writeValueAsString(RoujinUtil.rep(hoken));
            ctx.response().end(rep);
        } catch (Exception e) {
            ctx.fail(e);
        }
    }

    private void kouhiRep(RoutingContext ctx){
        try {
            KouhiDTO hoken = mapper.readValue(ctx.getBodyAsString().getBytes(), KouhiDTO.class);
            String rep = mapper.writeValueAsString(KouhiUtil.rep(hoken));
            ctx.response().end(rep);
        } catch (Exception e) {
            ctx.fail(e);
        }
    }

    @Override
    public void handle(RoutingContext routingContext) {
        String action = routingContext.request().getParam("action");
        NoDatabaseRestFunction f = noDatabaseFuncMap.get(action);
        if (f == null) {
            routingContext.next();
        } else {
            try {
                routingContext.response().putHeader("content-type", "application/json; charset=UTF-8");
                f.call(routingContext);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
