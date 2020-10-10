package dev.myclinic.vertx.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.Image;
import dev.myclinic.vertx.appconfig.AppConfig;
import dev.myclinic.vertx.appconfig.types.ShohousenGrayStampInfo;
import dev.myclinic.vertx.appconfig.types.StampInfo;
import dev.myclinic.vertx.drawer.DrawerColor;
import dev.myclinic.vertx.drawer.DrawerCompiler;
import dev.myclinic.vertx.drawer.Op;
import dev.myclinic.vertx.drawer.PaperSize;
import dev.myclinic.vertx.drawer.form.Form;
import dev.myclinic.vertx.drawer.form.Page;
import dev.myclinic.vertx.drawer.hint.Hint;
import dev.myclinic.vertx.drawer.hint.HintBase;
import dev.myclinic.vertx.drawer.hint.HintParser;
import dev.myclinic.vertx.drawer.hint.ParaHint;
import dev.myclinic.vertx.drawer.pdf.PdfPrinter;
import dev.myclinic.vertx.drawer.printer.AuxSetting;
import dev.myclinic.vertx.drawer.printer.DrawerPrinter;
import dev.myclinic.vertx.drawer.Box;
//import dev.myclinic.vertx.drawerform.Box;
import dev.myclinic.vertx.drawerform.FormCompiler;
import dev.myclinic.vertx.drawerform.Paper;
import dev.myclinic.vertx.drawerform.medcert.MedCertData;
import dev.myclinic.vertx.drawerform.medcert.MedCertForm;
//import dev.myclinic.vertx.drawerform.referform.ReferData;
//import dev.myclinic.vertx.drawerform.referform.ReferForm;
import dev.myclinic.vertx.drawerform.shujiiform.ShujiiData;
import dev.myclinic.vertx.drawerform.shujiiform.ShujiiForm;
import dev.myclinic.vertx.dto.*;
import dev.myclinic.vertx.mastermap.MasterMap;
import dev.myclinic.vertx.pdf.Stamper;
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
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final Map<String, String> mimeMap = new HashMap<>();

    {
        mimeMap.put("jpg", "image/jpeg");
        mimeMap.put("jpeg", "image/jpeg");
        mimeMap.put("png", "image/png");
        mimeMap.put("gif", "image/gif");
        mimeMap.put("pdf", "application/pdf");
    }

    private static final Pattern fileExtPattern = Pattern.compile("\\.([^.]+)$");

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
        noDatabaseFuncMap.put("view-drawer-as-pdf", this::viewDrawerAsPdf);
        //noDatabaseFuncMap.put("save-shohousen-pdf", this::saveShohousenPdf);
        noDatabaseFuncMap.put("get-shohousen-save-pdf-path", this::getShohousenSavePdfPath);
        noDatabaseFuncMap.put("convert-to-romaji", this::convertToRomaji);
        //noDatabaseFuncMap.put("shohousen-gray-stamp-info", this::shohousenGrayStampInfo);
        //noDatabaseFuncMap.put("refer-stamp-info", this::referStampInfo);
        noDatabaseFuncMap.put("send-fax", this::sendFax);
        noDatabaseFuncMap.put("poll-fax", this::pollFax);
        noDatabaseFuncMap.put("probe-shohousen-fax-image", this::probeShohousenFaxImage);
        noDatabaseFuncMap.put("show-pdf", this::showPdf);
        noDatabaseFuncMap.put("list-shujii-patient", this::listShujiiPatient);
        noDatabaseFuncMap.put("get-shujii-master-text", this::getShujiiMasterText);
        noDatabaseFuncMap.put("save-shujii-master-text", this::saveShujiiMasterText);
        noDatabaseFuncMap.put("compile-shujii-drawer", this::compileShujiiDrawer);
        noDatabaseFuncMap.put("shujii-criteria", this::shujiiCriteria);
        noDatabaseFuncMap.put("save-shujii-image", this::saveShujiiImage);
        noDatabaseFuncMap.put("create-printer-setting", this::createPrinterSetting);
        noDatabaseFuncMap.put("modify-printer-setting", this::modifyPrinterSetting);
        noDatabaseFuncMap.put("list-printer-setting", this::listPrinterSetting);
        noDatabaseFuncMap.put("print-guide-frame", this::printGuideFrame);
        noDatabaseFuncMap.put("get-printer-json-setting", this::getPrinterJsonSetting);
        noDatabaseFuncMap.put("save-printer-json-setting", this::savePrinterJsonSetting);
        noDatabaseFuncMap.put("print-refer", this::printRefer);
        //noDatabaseFuncMap.put("refer-drawer", this::referDrawer);
        noDatabaseFuncMap.put("save-refer", this::saveRefer);
        noDatabaseFuncMap.put("list-refer", this::listRefer);
        noDatabaseFuncMap.put("get-refer", this::getRefer);
        noDatabaseFuncMap.put("delete-refer", this::deleteRefer);
        noDatabaseFuncMap.put("create-refer-image-save-path", this::createReferImageSavePath);
        noDatabaseFuncMap.put("move-app-file", this::moveAppFile);
        noDatabaseFuncMap.put("delete-app-file", this::deleteAppFile);
        noDatabaseFuncMap.put("save-patient-image", this::savePatientImage);
        noDatabaseFuncMap.put("view-drawer", this::viewDrawer);
        noDatabaseFuncMap.put("create-temp-file-name", this::createTempFileName);
        noDatabaseFuncMap.put("delete-file", this::deleteFile);
        noDatabaseFuncMap.put("copy-file", this::copyFile);
        noDatabaseFuncMap.put("put-stamp-on-pdf", this::putStampOnPdf);
        noDatabaseFuncMap.put("render-medcert", this::renderMedCert);
    }

    private void renderMedCert(RoutingContext ctx) {
        vertx.<String>executeBlocking(promise -> {
            try {
                MedCertData data = mapper.readValue(ctx.getBody().getBytes(), MedCertData.class);
                MedCertForm form = new MedCertForm();
                List<Op> ops = form.render(data);
                String savePath = doCreateTempFileName("medcert", ".pdf");
                PdfPrinter pdfPrinter = new PdfPrinter(PaperSize.A4);
                pdfPrinter.print(Collections.singletonList(ops), savePath);
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

    private void deleteFile(RoutingContext ctx) {
        String file = ctx.request().getParam("file");
        if( file == null ){
            throw new RuntimeException("Missing parameter: file");
        }
        Path path = GlobalService.getInstance().resolveAppPath(file);
        try {
            Files.delete(path);
            ctx.response().end("true");
        } catch(Exception e){
            ctx.fail(e);
        }
    }

    public void copyFile(RoutingContext ctx){
        String src = ctx.request().getParam("src");
        if( src == null ){
            throw new RuntimeException("Missing parameter: src");
        }
        Path srcPath = GlobalService.getInstance().resolveAppPath(src);
        String dst = ctx.request().getParam("dst");
        if( dst == null ){
            throw new RuntimeException("Missing parameter: dst");
        }
        Path dstPath = GlobalService.getInstance().resolveAppPath(dst);
        String mkdir = ctx.request().getParam("mkdir");
        vertx.<Void>executeBlocking(promise -> {
            try {
                if( mkdir != null ){
                    Path parent = dstPath.getParent();
                    if( parent != null && !Files.exists(parent) ){
                        Files.createDirectories(parent);
                    }
                }
                Files.copy(srcPath, dstPath);
                promise.complete();
            } catch (Exception e) {
                promise.fail(e);
            }
        }, ar -> {
            if (ar.succeeded()) {
                ctx.response().end("true");
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    private String doCreateTempFileName(String prefix, String suffix){
        String fileId = GlobalService.getInstance().createTempAppFilePath(
                GlobalService.getInstance().portalTmpDirToken,
                prefix,
                suffix
        );
        return fileId;
    }

    private void createTempFileName(RoutingContext ctx) {
        String prefix = ctx.request().getParam("prefix");
        if( prefix == null ){
            prefix = "";
        }
        String suffix = ctx.request().getParam("suffix");
        if( suffix == null ){
            suffix = "";
        }
        ctx.response().end(jsonEncode(doCreateTempFileName(prefix, suffix)));
    }

    private void putStampOnPdf(RoutingContext ctx) {
        String srcFile = ctx.request().getParam("src-file");
        if( srcFile == null ){
            throw new RuntimeException("Missing parameter: src-file");
        }
        Path srcPath = GlobalService.getInstance().resolveAppPath(srcFile);
        String stamp = ctx.request().getParam("stamp");
        if( stamp == null ){
            throw new RuntimeException("Missing parameter: stamp");
        }
        String dstFile = ctx.request().getParam("dst-file");
        if( dstFile == null ){
            throw new RuntimeException("Missing parameter: dst-file");
        }
        Path dstPath = GlobalService.getInstance().resolveAppPath(dstFile);
        vertx.<Void>executeBlocking(promise -> {
            try {
                StampInfo stampInfo = appConfig.getStampInfo(stamp);
                Stamper.StamperOption opt = new Stamper.StamperOption();
                opt.xPos = stampInfo.xPos;
                opt.yPos = stampInfo.yPos;
                opt.scale = stampInfo.scale;
                opt.stampCenterRelative = stampInfo.isImageCenterRelative;
                Stamper stamper = new Stamper();
                stamper.putStamp(srcPath.toString(), stampInfo.imageFile,
                        dstPath.toString(), opt);
                promise.complete();
            } catch (Exception e) {
                promise.fail(e);
            }
        }, ar -> {
            if (ar.succeeded()) {
                ctx.response().end("true");
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    private void viewDrawer(RoutingContext ctx) {
        ctx.response().putHeader("content-type", "text/html; charset=utf-8");
        ctx.response().end("VIEW DRAWER");
    }

    private void deleteAppFile(RoutingContext ctx) {
        String file = ctx.request().getParam("file");
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Missing parameter: file");
        }
        vertx.executeBlocking(promise -> {
            try {
                Path path = GlobalService.getInstance().resolveAppPath(file);
                Files.delete(path);
                promise.complete();
            } catch (Exception e) {
                promise.fail(e);
            }
        }, ar -> {
            if (ar.succeeded()) {
                ctx.response().end("true");
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    private void moveAppFile(RoutingContext ctx) {
        String src = ctx.request().getParam("src");
        String dst = ctx.request().getParam("dst");
        if (src == null || src.isEmpty()) {
            throw new RuntimeException("Missing parameter: src");
        }
        if (dst == null || dst.isEmpty()) {
            throw new RuntimeException("Missing parameter: dst");
        }
        vertx.executeBlocking(promise -> {
            try {
                Path srcPath = GlobalService.getInstance().resolveAppPath(src);
                Path dstPath = GlobalService.getInstance().resolveAppPath(dst);
                Files.move(srcPath, dstPath);
                promise.complete();
            } catch (Exception e) {
                promise.fail(e);
            }
        }, ar -> {
            if (ar.succeeded()) {
                ctx.response().end("true");
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    private void deleteRefer(RoutingContext ctx) {
        String patientIdParam = ctx.request().getParam("patient-id");
        String file = ctx.request().getParam("file");
        if (patientIdParam == null) {
            throw new RuntimeException("Missing parameter: patient-id");
        }
        int patientId = Integer.parseInt(patientIdParam);
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Missing parameter: file");
        }
        Path dir = getReferDir(patientId);
        Path path = dir.resolve(file);
        if (!Files.exists(path)) {
            throw new RuntimeException("No such file: " + path.toString());
        }
        vertx.<String>executeBlocking(promise -> {
            try {
                Files.delete(path);
                promise.complete("true");
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

    private void createReferImageSavePath(RoutingContext ctx){
        String patientId = ctx.request().getParam("patient-id");
        if( patientId == null ){
            throw new RuntimeException("Missing parameter: patient-id");
        }
        try {
            Integer.parseInt(patientId);
        } catch(NumberFormatException e){
            throw new RuntimeException("Invalid patient-id: " + patientId);
        }
        String suffix = ctx.request().getParam("suffix");
        if( suffix == null ){
            throw new RuntimeException("Missing param: suffix");
        }
        String timestamp = DateTimeUtil.toPackedSqlDateTime(LocalDateTime.now());
        String fileName = String.format("%s-refer-%s.%s", patientId, timestamp, suffix);
        Path local = Path.of(patientId, fileName);
        String result = GlobalService.getInstance().createAppPathToken(
                GlobalService.getInstance().paperScanDirToken,
                local.toString()
        );
        ctx.response().end(jsonEncode(result));
    }

    private void getRefer(RoutingContext ctx) {
        String patientIdParam = ctx.request().getParam("patient-id");
        String file = ctx.request().getParam("file");
        if (patientIdParam == null) {
            throw new RuntimeException("Missing parameter: patient-id");
        }
        int patientId = Integer.parseInt(patientIdParam);
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Missing parameter: file");
        }
        Path dir = getReferDir(patientId);
        Path path = dir.resolve(file);
        vertx.<Buffer>executeBlocking(promise -> {
            try {
                byte[] bytes = Files.readAllBytes(path);
                promise.complete(Buffer.buffer(bytes));
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

    private Path getReferDir(int patientId) {
        String dir = System.getenv("MYCLINIC_REFER_DIR");
        if (dir == null) {
            throw new RuntimeException("Missing env var: MYCLINIC_REFER_DIR");
        }
        return Path.of(dir, String.format("%d", patientId));
    }

    private final static Pattern referFilePattern =
            Pattern.compile("\\d+-refer-\\d{8}.*\\.json");

    private void saveRefer(RoutingContext ctx) {
        vertx.<String>executeBlocking(resolve -> {
            try {
                String patientIdParam = ctx.request().getParam("patient-id");
                if (patientIdParam == null) {
                    throw new RuntimeException("Missing param: patient-id");
                }
                int patientId = Integer.parseInt(patientIdParam);
                byte[] bytes = ctx.getBody().getBytes();
                String stamp = DateTimeUtil.toPackedSqlDateTime(LocalDateTime.now());
                String filename = String.format("%d-refer-%s.json", patientId, stamp);
                Path referDir = getReferDir(patientId);
                if (!Files.isDirectory(referDir)) {
                    Files.createDirectories(referDir);
                }
                Path file = referDir.resolve(filename);
                Files.write(file, bytes);
                resolve.complete(jsonEncode(filename));
            } catch (Exception e) {
                resolve.fail(e);
            }
        }, ar -> {
            if (ar.succeeded()) {
                ctx.response().end(ar.result());
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    private void listRefer(RoutingContext ctx) {
        vertx.<String>executeBlocking(promise -> {
            try {
                String patientIdParam = ctx.request().getParam("patient-id");
                if (patientIdParam == null) {
                    throw new RuntimeException("Missing param: patient-id");
                }
                int patientId = Integer.parseInt(patientIdParam);
                Path referDir = getReferDir(patientId);
                if (Files.exists(referDir)) {
                    List<String> list = Files.list(referDir)
                            .map(path -> {
                                String filename = path.getFileName().toString();
                                Matcher m = referFilePattern.matcher(filename);
                                return m.matches() ? filename : null;
                            })
                            .filter(Objects::nonNull)
                            .collect(toList());
                    promise.complete(jsonEncode(list));
                } else {
                    promise.complete("[]");
                }
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

    private Form readFormRsrc(String rsrc) throws IOException {
        URL url = getClass().getClassLoader().getResource(rsrc);
        return mapper.readValue(url, Form.class);
    }

    private String printPdf(Form form, List<PdfPrinter.FormPageData> pageDataList, String tempFilePrefix)
            throws Exception {
        String outToken = GlobalService.getInstance().createTempAppFilePath(
                GlobalService.getInstance().portalTmpDirToken,
                tempFilePrefix,
                ".pdf"
        );
        Path outPath = GlobalService.getInstance().resolveAppPath(outToken);
        try(OutputStream os = new FileOutputStream(outPath.toString())){
            PdfPrinter pdfPrinter = new PdfPrinter(form.paper);
            pdfPrinter.print(form, pageDataList, os);
        }
        return outToken;
    }

    private List<PdfPrinter.FormPageData> trySinglePageRefer(Form form, Map<String, String> marks){
        Page page = form.pages.get(0);
        DrawerCompiler c = new DrawerCompiler();
        c.importOps(form.setup);
        Box box = page.marks.get("content").toBox();
        String s = marks.get("content");
        Box rendered = Hint.render(c, box, s, page.hints.get("content"));
        if( rendered.getBottom() > box.getBottom() ){
            return null;
        } else {
            PdfPrinter.FormPageData pageData = new PdfPrinter.FormPageData();
            pageData.pageId = 0;
            pageData.markTexts = marks;
            pageData.customRenderers = new HashMap<>();
            return List.of(pageData);
        }
    }

    private List<PdfPrinter.FormPageData> multiPageRefer(Form form, Map<String, String> marks){
        List<PdfPrinter.FormPageData> result = new ArrayList<>();
        DrawerCompiler c = new DrawerCompiler();
        String content = marks.get("content");
        c.importOps(form.setup);
        {
            Page page1 = form.pages.get(1);
            PdfPrinter.FormPageData pageData = new PdfPrinter.FormPageData();
            pageData.pageId = 1;
            pageData.markTexts = new HashMap<>();
            for(String key: marks.keySet()){
                if( page1.marks.containsKey(key) ){
                    if( !key.equals("content") ) {
                        pageData.markTexts.put(key, marks.get(key));
                    }
                }
            }
            ParaHint paraHint = (ParaHint)HintParser.parse(page1.hints.get("content"));
            String font = paraHint.getFont();
            if( font == null ){
                throw new RuntimeException("Cannot find font of paragraph (refer).");
            }
            c.setFont(font);
            DrawerCompiler.ParagraphResult pr = c.paragraph2(content, page1.marks.get("content").toBox(),
                    paraHint.getHAlign(), paraHint.getLeading());
            c.clearOps();
            pageData.markTexts.put("content", content.substring(0, pr.renderedEndIndex));
            System.err.printf("CONTENT(%d): %s --- %s\n", pageData.pageId,
                    content.substring(0, pr.renderedEndIndex).substring(0, 10),
                    content.substring(0, pr.renderedEndIndex).substring(pr.renderedEndIndex - 10));
            content = content.substring(pr.renderedEndIndex);
            pageData.customRenderers = new HashMap<>();
            result.add(pageData);
        }
        int pageCount = 0;
        while( true ){
            if( pageCount++ > 100 ){
                throw new RuntimeException("Too many pages");
            }
            DrawerCompiler.ParagraphResult pr;
            {
                Page page3 = form.pages.get(3); // last
                ParaHint paraHint = (ParaHint) HintParser.parse(page3.hints.get("content"));
                String font = paraHint.getFont();
                if (font == null) {
                    throw new RuntimeException("Cannot find font of paragraph (refer).");
                }
                c.setFont(font);
                pr = c.paragraph2(content, page3.marks.get("content").toBox(),
                        paraHint.getHAlign(), paraHint.getLeading());
                c.clearOps();
            }
            if( pr.renderedEndIndex >= content.length() ){
                Page page3 = form.pages.get(3); // last
                PdfPrinter.FormPageData pageData = new PdfPrinter.FormPageData();
                pageData.pageId = 3;
                pageData.markTexts = new HashMap<>();
                for(String key: marks.keySet()){
                    if( page3.marks.containsKey(key) ){
                        if( !key.equals("content") ) {
                            pageData.markTexts.put(key, marks.get(key));
                        }
                    }
                }
                pageData.markTexts.put("content", content.substring(0, pr.renderedEndIndex));
                pageData.customRenderers = new HashMap<>();
                result.add(pageData);
                return result;
            } else {
                Page page2 = form.pages.get(2); // middle
                ParaHint paraHint = (ParaHint)HintParser.parse(page2.hints.get("content"));
                String font = paraHint.getFont();
                if( font == null ){
                    throw new RuntimeException("Cannot find font of paragraph (refer).");
                }
                c.setFont(font);
                pr = c.paragraph2(content, page2.marks.get("content").toBox(),
                        paraHint.getHAlign(), paraHint.getLeading());
                c.clearOps();
                PdfPrinter.FormPageData pageData = new PdfPrinter.FormPageData();
                pageData.pageId = 2;
                pageData.markTexts = new HashMap<>();
                for(String key: marks.keySet()){
                    if( page2.marks.containsKey(key) ){
                        if( !key.equals("content") ) {
                            pageData.markTexts.put(key, marks.get(key));
                        }
                    }
                }
                pageData.markTexts.put("content", content.substring(0, pr.renderedEndIndex));
                System.err.printf("PAGE-DATA LENGTH: %d\n", content.length());
                pageData.customRenderers = new HashMap<>();
                result.add(pageData);
                content = content.substring(pr.renderedEndIndex);
            }
        }
    }

    private void printRefer(RoutingContext ctx){
        vertx.<String>executeBlocking(promise -> {
            try {
                Form form = readFormRsrc("refer-form.json");
                Map<String, String> marks = mapper.readValue(ctx.getBody().getBytes(),
                        new TypeReference<>(){});
                List<PdfPrinter.FormPageData> pageDataList = trySinglePageRefer(form, marks);
                if( pageDataList == null ){
                    pageDataList = multiPageRefer(form, marks);
                }
                String token = printPdf(form, pageDataList, "refer");
                promise.complete(jsonEncode(token));
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

//    private void referDrawer(RoutingContext ctx) {
//        try {
//            ReferData data = mapper.readValue(ctx.getBody().getBytes(), ReferData.class);
//            ReferForm form = new ReferForm();
//            List<Op> ops = form.render(data);
//            ctx.response().end(jsonEncode(ops));
//        } catch (Exception e) {
//            ctx.fail(e);
//        }
//    }
//
    private static double objectToDouble(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        } else if (obj instanceof String) {
            return Double.parseDouble((String) obj);
        } else {
            throw new RuntimeException("Cannot convert to double: " + obj);
        }
    }

    private static class PrinterSettingPaths {
        private final String dir;
        private final String settingName;

        PrinterSettingPaths(String settingName) {
            this.dir = System.getenv("MYCLINIC_PRINTER_SETTINGS_DIR");
            this.settingName = settingName;
        }

        private Path getPath(String suffix) {
            if (dir == null || dir.isEmpty()) {
                throw new RuntimeException("Missing env var: MYCLINIC_PRINTER_SETTINGS_DIR");
            }
            return Path.of(dir, this.settingName + suffix);
        }

        Path getDevmodePath() {
            return getPath(".devmode");
        }

        Path getDevnamesPath() {
            return getPath(".devnames");
        }

        Path getJsonSettingPath() {
            return getPath(".json");
        }
    }

    public static class PrinterJsonSetting {
        public double scaleX = 1.0;
        public double scaleY = 1.0;
        public double offsetX = 0.0;
        public double offsetY = 0.0;

        public static PrinterJsonSetting get(String settingName, ObjectMapper mapper) {
            PrinterJsonSetting result = new PrinterJsonSetting();
            if (settingName != null) {
                PrinterSettingPaths paths = new PrinterSettingPaths(settingName);
                Map<String, Object> dict;
                try {
                    dict = mapper.readValue(paths.getJsonSettingPath().toFile(),
                            new TypeReference<>() {
                            });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (dict.containsKey("scale")) {
                    result.scaleX = result.scaleY = objectToDouble(dict.get("scale"));
                }
                if (dict.containsKey("scaleX")) {
                    result.scaleX = objectToDouble(dict.get("scaleX"));
                }
                if (dict.containsKey("scaleY")) {
                    result.scaleY = objectToDouble(dict.get("scaleY"));
                }
                if (dict.containsKey("offsetX")) {
                    result.offsetX = objectToDouble(dict.get("offsetX"));
                }
                if (dict.containsKey("offsetY")) {
                    result.offsetY = objectToDouble(dict.get("offsetY"));
                }
            }
            return result;
        }
    }

    private static class PrinterSetting {
        public byte[] devmode;
        public byte[] devnames;

        public static PrinterSetting read(String name) {
            try {
                PrinterSetting result = new PrinterSetting();
                if (name != null) {
                    PrinterSettingPaths paths = new PrinterSettingPaths(name);
                    result.devmode = Files.readAllBytes(paths.getDevmodePath());
                    result.devnames = Files.readAllBytes(paths.getDevnamesPath());
                    return result;
                }
                return result;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void savePrinterJsonSetting(RoutingContext ctx) {
        try {
            String setting = ctx.request().getParam("setting");
            if (setting == null || setting.isEmpty()) {
                throw new RuntimeException("Missing parameter: setting");
            }
            byte[] bytes = ctx.getBody().getBytes();
            PrinterSettingPaths paths = new PrinterSettingPaths(setting);
            Files.write(paths.getJsonSettingPath(), bytes);
            ctx.response().end("true");
        } catch (Exception e) {
            ctx.fail(e);
        }
    }

    private void getPrinterJsonSetting(RoutingContext ctx) {
        try {
            String settingName = ctx.request().getParam("setting");
            if (settingName == null) {
                throw new RuntimeException("Missing parameter: setting");
            }
            PrinterSettingPaths paths = new PrinterSettingPaths(settingName);
            PrinterJsonSetting jsonSetting = PrinterJsonSetting.get(settingName, mapper);
            ctx.response().end(jsonEncode(jsonSetting));
        } catch (Exception e) {
            ctx.fail(e);
        }
    }

    private void printGuideFrame(RoutingContext ctx) {
        try {
            String settingName = ctx.request().getParam("setting");
            String paperName = ctx.request().getParam("paper");
            String insetArg = ctx.request().getParam("inset");
            Paper paperTmp = Paper.A4;
            if (paperName != null) {
                paperTmp = Paper.getPaperByName(paperName);
                if (paperTmp == null) {
                    throw new RuntimeException("Unknown paper: " + paperName);
                }
            }
            final Paper paper = paperTmp;
            executorService.execute(() -> {
                PrinterSetting setting = PrinterSetting.read(settingName);
                PrinterJsonSetting jsonSetting = PrinterJsonSetting.get(settingName, mapper);
                double inset = 10.0;
                if (insetArg != null) {
                    inset = Double.parseDouble(insetArg);
                }
                FormCompiler c = new FormCompiler();
                c.setScale(jsonSetting.scaleX, jsonSetting.scaleY);
                c.setOffsetX(jsonSetting.offsetX);
                c.setOffsetY(jsonSetting.offsetY);
                c.createPen("regular", 0, 0, 0, 0.2);
                c.box(new dev.myclinic.vertx.drawerform.Box(0, 0, paper.getWidth(), paper.getHeight()).inset(inset));
                List<Op> ops = c.getOps();
                DrawerPrinter printer = new DrawerPrinter();
                printer.print(ops, setting.devmode, setting.devnames);
            });
            ctx.response().end("true");
        } catch (Exception e) {
            ctx.fail(e);
        }
    }

    private void listPrinterSetting(RoutingContext ctx) {
        try {
            String dir = System.getenv("MYCLINIC_PRINTER_SETTINGS_DIR");
            if (dir == null || dir.isEmpty()) {
                throw new RuntimeException("Missing env var: MYCLINIC_PRINTER_SETTINGS_DIR");
            }
            List<String> names = Files.list(Path.of(dir)).filter(path ->
                    path.getFileName().toString().endsWith(".devmode"))
                    .map(path -> path.getFileName().toString().replaceAll("\\.devmode$", ""))
                    .collect(toList());
            ctx.response().end(jsonEncode(names));
        } catch (Exception e) {
            ctx.fail(e);
        }
    }

    private void modifyPrinterSetting(RoutingContext ctx) {
        try {
            String settingName = ctx.request().getParam("setting");
            if (settingName == null || settingName.isEmpty()) {
                throw new RuntimeException("Missing parameter: setting");
            }
            executorService.execute(() -> {
                PrinterSetting setting = PrinterSetting.read(settingName);
                DrawerPrinter printer = new DrawerPrinter();
                DrawerPrinter.DialogResult result = printer.printDialog(
                        setting.devmode, setting.devnames
                );
                if (result.ok) {
                    try {
                        PrinterSettingPaths paths = new PrinterSettingPaths(settingName);
                        Files.write(paths.getDevmodePath(), result.devmodeData);
                        Files.write(paths.getDevnamesPath(), result.devnamesData);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (Exception e) {
            ctx.fail(e);
        }
    }

    private void createPrinterSetting(RoutingContext ctx) {
        try {
            String dir = System.getenv("MYCLINIC_PRINTER_SETTINGS_DIR");
            if (dir == null || dir.isEmpty()) {
                throw new RuntimeException("Missing env var: MYCLINIC_PRINTER_SETTINGS_DIR");
            }
            String name = ctx.request().getParam("setting");
            if (name == null || name.isEmpty()) {
                throw new RuntimeException("Missing parameter: name");
            }
            executorService.execute(() -> {
                DrawerPrinter printer = new DrawerPrinter();
                DrawerPrinter.DialogResult result = printer.printDialog(null, null);
                if (result.ok) {
                    try {
                        PrinterSettingPaths paths = new PrinterSettingPaths(name);
                        Files.write(paths.getDevmodePath(), result.devmodeData);
                        Files.write(paths.getDevnamesPath(), result.devnamesData);
                        Files.write(paths.getJsonSettingPath(), "{}".getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            ctx.response().end("true");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void savePatientImage(RoutingContext ctx) {
        try {
            String patientIdParam = ctx.request().getParam("patient-id");
            if (patientIdParam == null || patientIdParam.isEmpty()) {
                throw new RuntimeException("Missing parameter: patient-id");
            }
            int patientId = Integer.parseInt(patientIdParam);
            Set<FileUpload> fileUploads = ctx.fileUploads();
            Path patientDir = GlobalService.getInstance().resolveAppPath(
                    GlobalService.getInstance().paperScanDirToken + "/" +
                            String.format("%d", patientId)
            );
            if (!Files.exists(patientDir)) {
                Files.createDirectories(patientDir);
            }
            vertx.executeBlocking(promise -> {
                try {
                    for (FileUpload f : fileUploads) {
                        String filename = f.fileName();
                        String uploaded = f.uploadedFileName();
                        Path dst = patientDir.resolve(filename);
                        Files.move(Path.of(uploaded), dst);
                    }
                    promise.complete();

                } catch (Exception e) {
                    promise.fail(e);
                }
            }, ar -> {
                if (ar.succeeded()) {
                    ctx.response().end("true");
                } else {
                    ctx.fail(ar.cause());
                }
            });
        } catch (Exception e) {
            ctx.fail(e);
        }
    }

    private void saveShujiiImage(RoutingContext ctx) {
        try {
            String name = ctx.request().getParam("name");
            if (name == null || name.isEmpty()) {
                throw new RuntimeException("Missing parameter: name");
            }
            String patientId = ctx.request().getParam("patient-id");
            if (patientId == null || patientId.isEmpty()) {
                throw new RuntimeException("Missing parameter: patient-id");
            }
            String shujiiDir = System.getenv("MYCLINIC_SHUJII_DIR");
            if (shujiiDir == null) {
                throw new RuntimeException("Cannot find env var MYCLINIC_SHUJII_DIR.");
            }
            Path patientDir = Path.of(shujiiDir, String.format("%s-%s", name, patientId));
            if (!Files.isDirectory(patientDir)) {
                throw new RuntimeException("No such directory: " + patientDir.toString());
            }
            Set<FileUpload> uploads = ctx.fileUploads();
            vertx.executeBlocking(promise -> {
                try {
                    for (FileUpload f : uploads) {
                        String filename = f.fileName();
                        String uploaded = f.uploadedFileName();
                        Path dst = patientDir.resolve(filename);
                        Files.move(Path.of(uploaded), dst);
                    }
                    promise.complete();
                } catch (Exception e) {
                    promise.fail(e);
                }
            }, ar -> {
                if (ar.succeeded()) {
                    ctx.response().end("true");
                } else {
                    ctx.fail(ar.cause());
                }
            });
        } catch (Exception e) {
            ctx.fail(e);
        }
    }

    private void shujiiCriteria(RoutingContext ctx) {
        String shujiiDir = System.getenv("MYCLINIC_SHUJII_DIR");
        if (shujiiDir == null) {
            throw new RuntimeException("Cannot find env var MYCLINIC_SHUJII_DIR.");
        }
        Path file = Path.of(shujiiDir, "criteria.pdf");
        ctx.response().putHeader("content-type", "application/pdf");
        ctx.response().sendFile(file.toString().toString());
    }

    private void compileShujiiDrawer(RoutingContext ctx) {
        try {
            ShujiiData data = mapper.readValue(ctx.getBody().getBytes(), ShujiiData.class);
            String settingName = ctx.request().getParam("setting");
            PrinterJsonSetting jsonSetting = PrinterJsonSetting.get(settingName, mapper);
            ShujiiForm form = new ShujiiForm();
            FormCompiler c = form.getCompiler();
            c.setScale(jsonSetting.scaleX, jsonSetting.scaleY);
            c.setOffsetX(jsonSetting.offsetX);
            c.setOffsetY(jsonSetting.offsetY);
            List<Op> ops = form.render(data);
            ctx.response().end(jsonEncode(ops));
        } catch (Exception e) {
            ctx.fail(e);
        }
    }

    private void saveShujiiMasterText(RoutingContext ctx) {
        vertx.<String>executeBlocking(promise -> {
            try {
                String name = ctx.request().getParam("name");
                if (name == null || name.isEmpty()) {
                    throw new RuntimeException("Missing parameter: name");
                }
                String patientId = ctx.request().getParam("patient-id");
                if (patientId == null || patientId.isEmpty()) {
                    throw new RuntimeException("Missing parameter: patient-id");
                }
                String text = this.mapper.readValue(ctx.getBody().getBytes(), String.class);
                String shujiiDir = System.getenv("MYCLINIC_SHUJII_DIR");
                if (shujiiDir == null) {
                    throw new RuntimeException("Cannot find env var MYCLINIC_SHUJII_DIR.");
                }
                Path patientDir = Path.of(shujiiDir, String.format("%s-%s", name, patientId));
                if (!Files.isDirectory(patientDir)) {
                    //noinspection ResultOfMethodCallIgnored
                    patientDir.toFile().mkdirs();
                }
                Path patientFile = patientDir.resolve(name + ".txt");
                Files.write(patientFile, text.getBytes(StandardCharsets.UTF_8));
                promise.complete("true");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, ar -> {
            if (ar.succeeded()) {
                ctx.response().end(ar.result());
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    private void getShujiiMasterText(RoutingContext ctx) {
        try {
            String shujiiDir = System.getenv("MYCLINIC_SHUJII_DIR");
            if (shujiiDir == null) {
                throw new RuntimeException("Cannot find env var MYCLINIC_SHUJII_DIR.");
            }
            PatientDTO patient = this.mapper.readValue(ctx.getBody().getBytes(), PatientDTO.class);
            String patientName = patient.lastName + patient.firstName;
            Path patientDir = Path.of(shujiiDir, String.format("%s-%d", patientName, patient.patientId));
            Path masterPath = patientDir.resolve(patientName + ".txt");
            if (!(Files.exists(masterPath))) {
                ctx.response().end(jsonEncode(null));
            } else {
                ctx.response().end(jsonEncode(readFileContent(masterPath)));
            }
        } catch (Exception e) {
            ctx.fail(e);
        }
    }

    private String readFileContent(Path path) throws Exception {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (MalformedInputException ex) {
            return Files.readString(path, Charset.defaultCharset());
        }
    }

    private void listShujiiPatient(RoutingContext ctx) {
        vertx.<String>executeBlocking(promise -> {
            try {
                String shujiiDir = System.getenv("MYCLINIC_SHUJII_DIR");
                if (shujiiDir == null) {
                    throw new RuntimeException("Cannot find env var MYCLINIC_SHUJII_DIR.");
                }
                List<Integer> patientIds = new ArrayList<>();
                for (Path path : Files.list(Path.of(shujiiDir)).collect(toList())) {
                    String name = path.getFileName().toString();
                    if ("arch".equals(name)) {
                        continue;
                    }
                    if (!Files.isDirectory(path)) {
                        continue;
                    }
                    String[] parts = name.split("-");
                    if (parts.length != 2) {
                        throw new RuntimeException("Invalid directory: " + name);
                    }
                    patientIds.add(Integer.parseInt(parts[1]));
                }
                promise.complete(jsonEncode(patientIds));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }, ar -> {
            if (ar.succeeded()) {
                ctx.response().end(ar.result());
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    private void showPdf(RoutingContext ctx) {
        String pdfFileToken = ctx.request().getParam("file");
        if (pdfFileToken == null) {
            throw new RuntimeException("Missing parameter: file");
        }
        Path pdfFile = GlobalService.getInstance().resolveAppPath(pdfFileToken);
        if (!pdfFile.toFile().exists()) {
            throw new RuntimeException("No such file: " + pdfFile);
        }
        ctx.response().putHeader("content-type", "application/pdf");
        ctx.response().sendFile(pdfFile.toString());
    }

    private void probeShohousenFaxImage(RoutingContext ctx) {
        String textIdPara = ctx.request().getParam("text-id");
        String date = ctx.request().getParam("date");
        String dirToken = GlobalService.getInstance().shohousenFaxDirToken;
        String month = date.substring(0, 7);
        String shohousenDirToken = dirToken + File.separator + month;
        Path shohousenDir = GlobalService.getInstance().resolveAppPath(shohousenDirToken);
        if (!Files.exists(shohousenDir)) {
            ctx.response().end("null");
            return;
        }
        Pattern pat = Pattern.compile(String.format("^[a-zA-Z]+-%s-.+\\.pdf$", textIdPara));
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(shohousenDir)) {
            for (Path path : stream) {
                String name = path.getFileName().toString();
                if (pat.matcher(name).matches()) {
                    ctx.response().end(jsonEncode(shohousenDirToken + File.separator + name));
                    return;
                }
            }
            ctx.response().end("null");
        } catch (IOException e) {
            ctx.fail(e);
        }
    }

    private void sendFax(RoutingContext ctx) {
        String faxNumber = ctx.request().getParam("fax-number"); // "+8133335..."
        String pdfFileToken = ctx.request().getParam("pdf-file");
        if (faxNumber == null) {
            throw new RuntimeException("fax-number parameter is missing");
        }
        if (pdfFileToken == null) {
            throw new RuntimeException("pdf-file parameter is missing");
        }
        String pdfFile = GlobalService.getInstance().resolveAppPath(pdfFileToken).toString();
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

//    private void shohousenGrayStampInfo(RoutingContext ctx) {
//        try {
//            var info = appConfig.getShohousenGrayStampInfo();
//            String rep = mapper.writeValueAsString(info);
//            ctx.response().end(rep);
//        } catch (Exception e) {
//            ctx.fail(e);
//        }
//    }
//
//    private void referStampInfo(RoutingContext ctx){
//        try {
//            var info = appConfig.getReferStampInfo();
//            String rep = mapper.writeValueAsString(info);
//            ctx.response().end(rep);
//        } catch (Exception e) {
//            ctx.fail(e);
//        }
//    }

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

    private String composeShohousenSavePdfTokenPath(String name, int textId, int patientId,
                                               LocalDate date) {
        String nameRomaji = name;
        if (isNotRomaji(nameRomaji)) {
            nameRomaji = Romaji.toRomaji(nameRomaji);
        }
        String month = date.toString().substring(0, 7);
        String file = String.format("%s-%d-%d-%s-stamped.pdf", nameRomaji,
                textId, patientId, date.toString().replace("-", ""));
        String parentToken = GlobalService.getInstance().createAppPathToken(
                GlobalService.getInstance().shohousenFaxDirToken,
                month
        );
        Path parentPath = GlobalService.getInstance().resolveAppPath(parentToken);
        if( !Files.exists(parentPath) ){
            try {
                Files.createDirectories(parentPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return parentToken + File.separator + file;
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
        String pdfTokenPath = composeShohousenSavePdfTokenPath(name, Integer.parseInt(textId),
                Integer.parseInt(patientId), LocalDate.parse(date));
        ctx.response().end(jsonEncode(pdfTokenPath));
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
            Path savePath = GlobalService.getInstance().resolveAppPath(req.savePath);
            Files.write(savePath, pdfBytes);
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

    private void viewDrawerAsPdf(RoutingContext ctx) {
        String paperSizeParam = ctx.request().getParam("paper");
        if (paperSizeParam == null) {
            paperSizeParam = "A4";
        }
        PaperSize paperSize = resolvePaperSize(paperSizeParam);
        vertx.<Buffer>executeBlocking(promise -> {
            try {
                List<List<Op>> pages = mapper.readValue(ctx.request().getParam("pages"),
                        new TypeReference<>() {
                        });
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                PdfPrinter printer = new PdfPrinter(paperSize);
                printer.print(pages, outStream);
                byte[] pdfBytes = outStream.toByteArray();
                ctx.response().putHeader("content-type", "application/pdf");
                promise.complete(Buffer.buffer(pdfBytes));
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

    private void printDrawer(RoutingContext ctx) {
        try {
            String settingName = ctx.request().getParam("setting");
            final PrinterSetting setting = PrinterSetting.read(settingName);
            AuxSetting auxSetting = new AuxSetting();
            executorService.execute(() -> {
                try {
                    byte[] bytes = ctx.getBody().getBytes();
                    List<List<Op>> pages = mapper.readValue(bytes, new TypeReference<>() {
                    });
                    DrawerPrinter printer = new DrawerPrinter();
                    // jsonSetting is not used. It is used when pages are created.
                    printer.printPages(pages, setting.devmode, setting.devnames, auxSetting);
                } catch (Exception e) {
                    logger.error("print-drawer failed", e);
                }
            });
            ctx.response().end("true");
        } catch (Exception e) {
            ctx.fail(e);
        }
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

//    private void saveShohousenPdf(RoutingContext ctx) {
//        vertx.<String>executeBlocking(promise -> {
//            try {
//                String textIdArg = ctx.request().getParam("text-id");
//                if (textIdArg == null) {
//                    throw new RuntimeException("text-id is missing");
//                }
//                int textId = Integer.parseInt(textIdArg);
//                byte[] bytes = ctx.getBody().getBytes();
//                ShohousenRequest req = mapper.readValue(bytes, ShohousenRequest.class);
//                if (req.patient == null) {
//                    throw new RuntimeException("patient info is missing");
//                }
//                if (req.issueDate == null) {
//                    throw new RuntimeException("issue date is missing");
//                }
//                ShohousenData data = convertToShohousenData(req);
//                ShohousenDrawer drawer = new ShohousenDrawer();
//                if (req.color != null) {
//                    DrawerColor defaultColor = DrawerColor.resolve(req.color);
//                    drawer.setDefaultColor(defaultColor);
//                }
//                drawer.init();
//                data.applyTo(drawer);
//                List<Op> ops = drawer.getOps();
//                String saveTokenPath = composeShohousenSavePdfTokenPath(
//                        req.patient.lastNameYomi + req.patient.firstNameYomi,
//                        textId,
//                        req.patient.patientId,
//                        LocalDate.parse(req.issueDate)
//                );
//                SaveDrawerAsPdfRequest saveReq = new SaveDrawerAsPdfRequest();
//                saveReq.pages = List.of(ops);
//                saveReq.paperSize = "A5";
//                saveReq.savePath = saveTokenPath;
//                //ShohousenGrayStampInfo stampInfo = appConfig.getShohousenGrayStampInfo();
////                StampRequest stampReq = new StampRequest();
////                stampReq.path = stampInfo.path;
////                stampReq.scale = stampInfo.scale;
////                stampReq.offsetX = stampInfo.offsetX;
////                stampReq.offsetY = stampInfo.offsetY;
////                saveReq.stamp = stampReq;
//                doSaveDrawerAsPdf(saveReq);
//                promise.complete(jsonEncode(saveTokenPath));
//            } catch (Exception e) {
//                promise.fail(e);
//            }
//        }, ar -> {
//            if (ar.succeeded()) {
//                ctx.response().end(ar.result());
//            } else {
//                ctx.fail(ar.cause());
//            }
//        });
//    }

    private void hokenRep(RoutingContext ctx) {
        try {
            HokenDTO hoken = mapper.readValue(ctx.getBody().getBytes(), HokenDTO.class);
            HokenUtil.fillHokenRep(hoken);
            String rep = mapper.writeValueAsString(hoken.rep);
            ctx.response().end(rep);
        } catch (Exception e) {
            ctx.fail(e);
        }
    }

    private void shahokokuhoRep(RoutingContext ctx) {
        try {
            ShahokokuhoDTO hoken = mapper.readValue(ctx.getBody().getBytes(), ShahokokuhoDTO.class);
            String rep = mapper.writeValueAsString(ShahokokuhoUtil.rep(hoken));
            ctx.response().end(rep);
        } catch (Exception e) {
            ctx.fail(e);
        }
    }

    private void koukikoureiRep(RoutingContext ctx) {
        try {
            KoukikoureiDTO hoken = mapper.readValue(ctx.getBody().getBytes(), KoukikoureiDTO.class);
            String rep = mapper.writeValueAsString(KoukikoureiUtil.rep(hoken));
            ctx.response().end(rep);
        } catch (Exception e) {
            ctx.fail(e);
        }
    }

    private void roujinRep(RoutingContext ctx) {
        try {
            RoujinDTO hoken = mapper.readValue(ctx.getBody().getBytes(), RoujinDTO.class);
            String rep = mapper.writeValueAsString(RoujinUtil.rep(hoken));
            ctx.response().end(rep);
        } catch (Exception e) {
            ctx.fail(e);
        }
    }

    private void kouhiRep(RoutingContext ctx) {
        try {
            KouhiDTO hoken = mapper.readValue(ctx.getBody().getBytes(), KouhiDTO.class);
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
