package dev.myclinic.vertx.server;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class NoDatabaseRestHandler extends RestHandlerBase implements Handler<RoutingContext> {

    private static final Logger logger = LoggerFactory.getLogger(NoDatabaseRestHandler.class);

    interface NoDatabaseRestFunction {
        void call(RoutingContext ctx, NoDatabaseImpl impl) throws Exception;
    }

    private final Map<String, NoDatabaseRestFunction> noDatabaseFuncMap = new HashMap<>();

    private final AppConfig appConfig;
    private final Vertx vertx;

    NoDatabaseRestHandler(AppConfig appConfig, ObjectMapper mapper, Vertx vertx){
        super(mapper);
        this.appConfig = appConfig;
        this.vertx = vertx;
    }

    private String cacheListDiseaseExample;

    private void listDiseaseExample(RoutingContext ctx, NoDatabaseImpl impl) throws Exception {
        if( cacheListDiseaseExample != null ){
            ctx.response().end(cacheListDiseaseExample);
        } else {
            HttpServerRequest req = ctx.request();
            appConfig.listDiseaseExample()
                    .onComplete(ar -> {
                        if (ar.failed()) {
                            req.response().setStatusCode(500).end("Cannot get disease examples.");
                        } else {
                            cacheClinicInfo = jsonEncode(ar.result());
                            req.response().end(cacheClinicInfo);
                        }
                    });
        }
    }

    private List<String> implListHokensho(String storageDir, int patientId) throws IOException {
        String pat = String.format("glob:%d-hokensho-*.{jpg,jpeg,bmp}", patientId);
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher(pat);
        Path patientDir = Paths.get(storageDir, "" + patientId);
        if( Files.exists(patientDir) && Files.isDirectory(patientDir) ){
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
                                    logger.error("Failed to list hokensho.", e);
                                    promise.fail(e);
                                }
                            },
                            ar2 -> {
                                if( ar2.failed() ){
                                    logger.error("Failed to list hokensho.", ar2.cause());
                                    ctx.response().setStatusCode(500).end("Failed to list hokensho.");
                                } else {
                                    ctx.response().end(jsonEncode(ar2.result()));
                                }
                            }
                    );

                })
                .onComplete(ar -> {
                    if( ar.failed() ){
                        ctx.response().setStatusCode(500).end("Failed to get scan dir.");
                    } else {
                        String scanDir = ar.result();
                        vertx.<List<String>>executeBlocking(
                                promise -> {
                                    try {
                                        List<String> hokenList = implListHokensho(scanDir, patientId);
                                        promise.complete(hokenList);
                                    } catch(Exception e){
                                        logger.error("Failed to list hokensho.", e);
                                        promise.fail(e);
                                    }
                                },
                                ar2 -> {
                                    if( ar2.failed() ){
                                        logger.error("Failed to list hokensho.", ar2.cause());
                                        ctx.response().setStatusCode(500).end("Failed to list hokensho.");
                                    } else {
                                        ctx.response().end(jsonEncode(ar2.result()));
                                    }
                                }
                        );
                    }
                });
    }

    private String cacheClinicInfo;

    private void getClinicInfo(RoutingContext ctx, NoDatabaseImpl impl) throws Exception {
        if( cacheClinicInfo != null ){
            ctx.response().end(cacheClinicInfo);
        } else {
            HttpServerRequest req = ctx.request();
            appConfig.getClinicInfo()
                    .onSuccess(dto -> ctx.response().end(jsonEncode(dto)))
                    .onFailure(e -> ctx.response()
                            .setStatusCode(500).end("Failed to get clinic info.")
                    );
        }
    }

    private void getMasterMapConfigFilePath(RoutingContext ctx, NoDatabaseImpl impl) throws Exception {
        appConfig.getMasterMapConfigFilePath()
                .onSuccess(path -> {
                    StringResultDTO dto = new StringResultDTO();
                    dto.value = path;
                    throw new RuntimeException("Failure");
                    //ctx.response().end(jsonEncode(dto));
                })
                .onFailure(e -> {
                    System.out.println("onFailure reached");
                    ctx.response()
                            .setStatusCode(404).end("Failed to get location of master map file.");
                        }
                );
    }

    private void getShinryouByoumeiMapConfigFilePath(RoutingContext ctx, NoDatabaseImpl impl) throws Exception {
        HttpServerRequest req = ctx.request();
        StringResultDTO _value = impl.getShinryouByoumeiMapConfigFilePath();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getHokensho(RoutingContext ctx, NoDatabaseImpl impl) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int patientId = Integer.parseInt(params.get("patient-id"));
        String file = params.get("file");
        byte[] _value = impl.getHokensho(patientId, file);
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getReferList(RoutingContext ctx, NoDatabaseImpl impl) throws Exception {
        HttpServerRequest req = ctx.request();
        List<ReferItemDTO> _value = impl.getReferList();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
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
        if( f == null ){
            routingContext.next();
        } else {
            try {
                routingContext.response().putHeader("content-type", "application/json; charset=UTF-8");
                NoDatabaseImpl impl = new NoDatabaseImpl(appConfig);
                f.call(routingContext,impl);
            } catch(Exception e){
                throw new RuntimeException(e);
            }
        }
    }
}
