package dev.myclinic.vertx.server.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.myclinic.vertx.server.integration.IntegrationUtil.ExecResult;
import static dev.myclinic.vertx.server.integration.IntegrationUtil.ExecRequest;

public class HoumonKangoHandler {

    private static final Logger logger = LoggerFactory.getLogger(HoumonKangoHandler.class);

    private final Vertx vertx;
    private final ObjectMapper mapper;

    private HoumonKangoHandler(Vertx vertx, ObjectMapper mapper) {
        this.vertx = vertx;
        this.mapper = mapper;
    }

    public static Router createRouter(Vertx vertx, ObjectMapper mapper) {
        Router router = Router.router(vertx);
        HoumonKangoHandler handler = new HoumonKangoHandler(vertx, mapper);
        router.route("/*").handler(ctx -> {
            ctx.response().putHeader("content-type", "application/json; charset=UTF-8");
            ctx.next();
        });
        handler.addRoutes(router);
        return router;
    }

    private void addRoutes(Router router) {
        router.route(HttpMethod.POST, "/create-shijisho").handler(this::handleCreateShijisho);
        router.route(HttpMethod.GET, "/list-params").handler(this::handleListParams);
        //router.route(HttpMethod.GET, "/get-clinic-param").handler(this::handleGetClinicParam);
        router.route(HttpMethod.GET, "/get-record").handler(this::handleGetRecord);
        router.route(HttpMethod.POST, "/save-record").handler(this::handleSaveRecord);
    }

    private void handleSaveRecord(RoutingContext ctx) {
        int patientId = IntegrationUtil.getIntParam(ctx, "patient-id");
        Path path = getRecordPath(patientId);
        vertx.<String>executeBlocking(promise -> {
            try {
                Files.write(path, ctx.getBody().getBytes());
                promise.complete("true");
            } catch (IOException e) {
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

    private void handleGetRecord(RoutingContext ctx) {
        int patientId = IntegrationUtil.getIntParam(ctx, "patient-id");
        Path path = getRecordPath(patientId);
        if( !Files.exists(path) ){
            ctx.response().end(String.format("{\"patientId\": %d, \"history\": []}", patientId));
        } else {
            ctx.response().sendFile(path.toFile().getAbsolutePath());
        }
    }

    private Path createHoumonKangoTmpFile(){
        Path tmpDir = Path.of("server", "webroot", "portal", "tmp");
        try {
            if( !Files.exists(tmpDir) ){
                Files.createDirectory(tmpDir);
            }
            return Files.createTempFile(tmpDir, "houmon-kango", ".pdf");
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    private String jsonEncode(Object obj){
        try {
            return mapper.writeValueAsString(obj);
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    private void handleCreateShijisho(RoutingContext ctx) {
        Path springProjectDir = IntegrationUtil.getMyclinicSpringProjectDir();
        String url = "https://deno.myclinic.dev/houmon-kango/create-houmon-kango-form.ts";
        vertx.<String>executeBlocking(promise -> {
            ExecRequest req1 = new ExecRequest();
            req1.command = List.of("deno", "run", "--allow-net", url, "-");
            Map<String, String> env = new HashMap<>();
            env.put("NO_COLOR", "yes");
            req1.env = env;
            req1.stdIn = ctx.getBody().getBytes();
//            String dataAttr = ctx.request().getFormAttribute("data");
//            if( dataAttr != null ){
//                req1.stdIn = dataAttr.getBytes(StandardCharsets.UTF_8);
//            } else {
//                req1.stdIn = ctx.getBody().getBytes();
//            }
            if( req1.stdIn.length == 0 ){
                req1.stdIn = "{}".getBytes();
            }
            ExecResult er1 = IntegrationUtil.exec(req1);
            if (er1.isError()) {
                promise.fail(er1.getErrorMessage());
                return;
            }
            byte[] drawer = er1.stdOut;
            Path jar = Path.of(springProjectDir.toFile().getAbsolutePath(), "drawer-printer",
                    "target", "drawer-printer-1.0.0-SNAPSHOT.jar");
            Path outFile = createHoumonKangoTmpFile();
            ExecRequest req2 = new ExecRequest();
            req2.command = List.of("java", "-jar", jar.toString(), "-e", "utf-8", "--pdf",
                    outFile.toString());
            req2.stdIn = drawer;
            ExecResult er2 = IntegrationUtil.exec(req2);
            if (er2.isError()) {
                promise.fail(er2.getErrorMessage());
                return;
            }
            ctx.response().putHeader("content-type", "application/pdf");
            promise.complete(jsonEncode(outFile.getFileName().toString()));
        }, ar -> {
            if (ar.succeeded()) {
                ctx.response().end(ar.result());
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    private void handleListParams(RoutingContext ctx) {
        String url = "https://deno.myclinic.dev/houmon-kango/create-houmon-kango-form.ts";
        vertx.<Buffer>executeBlocking(promise -> {
            ExecRequest req = new ExecRequest();
            Map<String, String> env = new HashMap<>();
            env.put("NO_COLOR", "yes");
            req.command = List.of("deno", "run", "--allow-net", url, "-p");
            req.env = env;
            ExecResult er = IntegrationUtil.exec(req);
            if (er.stdErr.length == 0) {
                promise.complete(Buffer.buffer(er.stdOut));
            } else {
                promise.fail(new String(er.stdErr, StandardCharsets.UTF_8));
            }
        }, ar -> {
            if (ar.succeeded()) {
                ctx.response().end(ar.result());
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    private Path getHoumonKangoConfigDir(){
        return Path.of(IntegrationUtil.getConfigDir(), "houmon-kango");
    }

    private Path getRecordPath(int patientId){
        String file = String.format("record-%d.json", patientId);
        return getHoumonKangoConfigDir().resolve(file);
    }

}
