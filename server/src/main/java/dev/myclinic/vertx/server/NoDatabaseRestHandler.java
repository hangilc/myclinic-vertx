package dev.myclinic.vertx.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.appconfig.AppConfig;
import dev.myclinic.vertx.drawer.Op;
import dev.myclinic.vertx.drawer.PaperSize;
import dev.myclinic.vertx.drawer.pdf.PdfPrinter;
import dev.myclinic.vertx.drawer.printer.DrawerPrinter;
import dev.myclinic.vertx.dto.*;
import dev.myclinic.vertx.mastermap.MasterMap;
import dev.myclinic.vertx.romaji.Romaji;
import dev.myclinic.vertx.shohousendrawer.ShohousenData;
import dev.myclinic.vertx.shohousendrawer.ShohousenDrawer;
import dev.myclinic.vertx.util.HokenUtil;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class NoDatabaseRestHandler extends RestHandlerBase implements Handler<RoutingContext> {

    private static final Logger logger = LoggerFactory.getLogger(NoDatabaseRestHandler.class);

    interface NoDatabaseRestFunction {
        void call(RoutingContext ctx) throws Exception;
    }

    private final Map<String, NoDatabaseRestFunction> noDatabaseFuncMap = new HashMap<>();

    private final AppConfig appConfig;
    private final Vertx vertx;

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
                    .collect(Collectors.toList());
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
                                } catch(Exception e){
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
        if( cacheGetMasterMapConfigFilePath != null ){
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

    private String getFileExtension(String file){
        Matcher m = fileExtPattern.matcher(file);
        if( m.find() ){
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
        if( ext == null ){
            throw new RuntimeException("Cannot find file extension.");
        }
        String mime = mimeMap.get(ext);
        if( mime == null ){
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
        noDatabaseFuncMap.put("shohousen-drawer", this::shohousenDrawer);
        noDatabaseFuncMap.put("calc-rcpt-age", this::calcRcptAge);
        noDatabaseFuncMap.put("calc-futan-wari", this::calcFutanWari);
        noDatabaseFuncMap.put("print-drawer", this::printDrawer);
        noDatabaseFuncMap.put("save-drawer-as-pdf", this::saveDrawerAsPdf);
        noDatabaseFuncMap.put("get-shohousen-save-pdf-path", this::getShohousenSavePdfPath);
        noDatabaseFuncMap.put("convert-to-romaji", this::convertToRomaji);
    }

    private void convertToRomaji(RoutingContext ctx) {
        String text = ctx.request().getParam("text");
        if(text == null){
            throw new RuntimeException("Missing parameter (text).");
        }
        String romaji = Romaji.toRomaji(text);
        ctx.response().end("\"" + romaji + "\"");
    }

    private void getShohousenSavePdfPath(RoutingContext ctx) {
        String textId = ctx.request().getParam("text-id");
        String patientId = ctx.request().getParam("patient-id");
        String name = ctx.request().getParam("name");
        String date = ctx.request().getParam("date");
        if( !(textId != null && patientId != null && name != null && date != null) ){
            throw new RuntimeException("Missing parameter");
        }
        String mkdir = ctx.request().getParam("mkdir");
        String dir = System.getenv("MYCLINIC_SHOHOUSEN_DIR");
        if( dir == null ){
            throw new RuntimeException("Cannot find env var: MYCLINIC_SHOHOUSEN_DIR");
        }
        String month = date.substring(0, 7);
        Path shohousenDir = Path.of(dir, month);
        if( "true".equals(mkdir) && !Files.exists(shohousenDir) ){
            try {
                Files.createDirectories(shohousenDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String datePart = date.substring(0, 10).replace("-", "");
        String file = String.format("%s-%s-%s-%s-stamped.pdf", name, textId, patientId, datePart);
        ctx.response().end("\"" + shohousenDir.resolve(file).toFile().getAbsolutePath() + "\"");
    }

    private PaperSize resolvePaperSize(String arg){
        if( PaperSize.standard.containsKey(arg) ){
            return PaperSize.standard.get(arg);
        } else {
            String[] parts = arg.split(",");
            if( parts.length == 2 ){
                double width = Double.parseDouble(parts[0].trim());
                double height = Double.parseDouble(parts[1].trim());
                return new PaperSize(width, height);
            } else {
                throw new RuntimeException("Invalid paper size: " + arg);
            }
        }
    }

    public static class SaveDrawerAsPdfRequest {
        public List<List<Op>> pages;
        public String paperSize;
        public String savePath;
    }

    private void saveDrawerAsPdf(RoutingContext ctx) {
        vertx.<String>executeBlocking(promise -> {
            try {
                byte[] bytes = ctx.getBody().getBytes();
                SaveDrawerAsPdfRequest req = mapper.readValue(bytes, SaveDrawerAsPdfRequest.class);
                PaperSize paperSize = resolvePaperSize(req.paperSize);
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                PdfPrinter printer = new PdfPrinter(paperSize);
                printer.print(req.pages, outStream);
                byte[] pdfBytes = outStream.toByteArray();
                Files.write(Path.of(req.savePath), pdfBytes);
                promise.complete("true");
            } catch(Exception e){
                ctx.fail(e);
            }
        }, ar -> {
            if( ar.succeeded() ){
                ctx.response().end(ar.result());
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    private void printDrawer(RoutingContext ctx) {
        vertx.<String>executeBlocking(promise -> {
            try {
                byte[] bytes = ctx.getBody().getBytes();
                List<List<Op>> pages = mapper.readValue(bytes, new TypeReference<>(){});
                DrawerPrinter printer = new DrawerPrinter();
                printer.printPages(pages);
                promise.complete("true");
            } catch(Exception e){
                promise.fail(e);
            }
        }, ar -> {
            if( ar.succeeded() ){
                ctx.response().end(ar.result());
            } else {
                ctx.fail(ar.cause());
            }
        });
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
        } catch(Exception e){
            ctx.fail(e);
        }
    }

    private void calcFutanWari(RoutingContext ctx) {
        try {
            byte[] bytes = ctx.getBody().getBytes();
            CalcFutanWariRequestDTO req = mapper.readValue(bytes, CalcFutanWariRequestDTO.class);
            int futanWari = HokenUtil.calcFutanWari(req.hoken, req.rcptAge);
            ctx.response().end(String.format("%d", futanWari));
        } catch(Exception e){
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

    private void shohousenDrawer(RoutingContext ctx) {
        try {
            byte[] bytes = ctx.getBody().getBytes();
            ShohousenRequest req = mapper.readValue(bytes, ShohousenRequest.class);
            ShohousenData data = new ShohousenData();
            if( req.clinicInfo != null ){
                data.setClinicInfo(req.clinicInfo);
            }
            if( req.hoken != null ){
                data.setHoken(req.hoken);
            }
            if( req.futanWari != null ){
                data.setFutanWari(req.futanWari);
            }
            if( req.patient != null ){
                data.setPatient(req.patient);
            }
            if( req.drugs !=  null ){
                data.setDrugs(req.drugs);
            }
            if( req.issueDate != null ){
                LocalDate date = LocalDate.parse(req.issueDate);
                data.setKoufuDate(date);
            }
            if( req.validUpto != null ){
                LocalDate date = LocalDate.parse(req.validUpto);
                data.setValidUptoDate(date);
            }
            ShohousenDrawer drawer = new ShohousenDrawer();
            data.applyTo(drawer);
            List<Op> ops = drawer.getOps();
            ctx.response().end(mapper.writeValueAsString(ops));
        } catch(Exception e){
            ctx.fail(e);
        }
    }

    private void hokenRep(RoutingContext ctx) {
        try {
            HokenDTO hoken = mapper.readValue(ctx.getBodyAsString().getBytes(), HokenDTO.class);
            String rep = mapper.writeValueAsString(HokenUtil.hokenRep(hoken));
            ctx.response().end(rep);
        } catch(Exception e){
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
