package dev.myclinic.vertx.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.MapEntry;
import dev.myclinic.vertx.appconfig.AppConfig;
import dev.myclinic.vertx.dto.PracticeConfigDTO;
import dev.myclinic.vertx.dto.ReferItemDTO;
import dev.myclinic.vertx.dto.StringResultDTO;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class NoDatabaseRestHandler extends RestHandlerBase implements Handler<RoutingContext> {

    private static final Logger logger = LoggerFactory.getLogger(NoDatabaseRestHandler.class);

    interface NoDatabaseRestFunction {
        void call(RoutingContext ctx, NoDatabaseImpl impl) throws Exception;
    }

    private final Map<String, NoDatabaseRestFunction> noDatabaseFuncMap = new HashMap<>();

    private final AppConfig appConfig;
    private final Vertx vertx;

    NoDatabaseRestHandler(AppConfig appConfig, ObjectMapper mapper, Vertx vertx) {
        super(mapper);
        this.appConfig = appConfig;
        this.vertx = vertx;
    }

    private String cacheListDiseaseExample;

    private void listDiseaseExample(RoutingContext ctx, NoDatabaseImpl impl) throws Exception {
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

    private void listHokensho(RoutingContext ctx, NoDatabaseImpl impl) throws Exception {
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

    private void getClinicInfo(RoutingContext ctx, NoDatabaseImpl impl) throws Exception {
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

    private void getMasterMapConfigFilePath(RoutingContext ctx, NoDatabaseImpl impl) throws Exception {
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

    private void getShinryouByoumeiMapConfigFilePath(RoutingContext ctx, NoDatabaseImpl impl) throws Exception {
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

    private void getHokensho(RoutingContext ctx, NoDatabaseImpl impl) throws Exception {
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

    private void getReferList(RoutingContext ctx, NoDatabaseImpl impl) throws Exception {
        appConfig.getReferList()
                .onSuccess(result -> {
                    ctx.response().end(jsonEncode(result));
                })
                .onFailure(e -> ctx.fail(500, e));
    }

    private void getNameMapConfigFilePath(RoutingContext ctx, NoDatabaseImpl impl) throws Exception {
        HttpServerRequest req = ctx.request();
        StringResultDTO _value = impl.getNameMapConfigFilePath();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getPowderDrugConfigFilePath(RoutingContext ctx, NoDatabaseImpl impl) throws Exception {
        HttpServerRequest req = ctx.request();
        StringResultDTO _value = impl.getPowderDrugConfigFilePath();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getPracticeConfig(RoutingContext ctx, NoDatabaseImpl impl) throws Exception {
        HttpServerRequest req = ctx.request();
        PracticeConfigDTO _value = impl.getPracticeConfig();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
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
                NoDatabaseImpl impl = new NoDatabaseImpl();
                f.call(routingContext, impl);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
