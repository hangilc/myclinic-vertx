package dev.myclinic.vertx.server.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FaxedShohousenHandler {

    private final Vertx vertx;
    private final ObjectMapper mapper;

    public static Router createRouter(Vertx vertx, ObjectMapper mapper) {
        Router router = Router.router(vertx);
        FaxedShohousenHandler handler = new FaxedShohousenHandler(vertx, mapper);
        router.route("/*").handler(ctx -> {
            ctx.response().putHeader("content-type", "application/json; charset=UTF-8");
            ctx.next();
        });
        handler.addRoutes(router);
        return router;
    }

    private FaxedShohousenHandler(Vertx vertx, ObjectMapper mapper) {
        this.vertx = vertx;
        this.mapper = mapper;
    }

    private void addRoutes(Router router) {
        router.route("/list-groups").handler(this::handleListGroups);
        router.route("/create-data").handler(this::handleCreateData);
        router.route("/save-data").handler(this::handleSaveData);
    }

    private void handleSaveData(RoutingContext ctx) {
        String from = ctx.queryParam("from").get(0);
        String upto = ctx.queryParam("upto").get(0);
        String dir = ensureFaxedShohousenDataDir(from, upto);
        vertx.<String>executeBlocking(promise -> {
            try {
                String body = ctx.getBodyAsString();
                Path file = Path.of(dir, makeFaxedShohousenDataFileName(from, upto));
                Files.write(file, body.getBytes(StandardCharsets.UTF_8));
                promise.complete(mapper.writeValueAsString(file.toString()));
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

    private void handleListGroups(RoutingContext ctx){
        try {
            String dir = getManagementRootDir();
            List<String> result = new ArrayList<>();
            Files.newDirectoryStream(Path.of(dir)).forEach(path -> {
                String name = path.toFile().getName();
                result.add(name);
            });
            ctx.response().end(mapper.writeValueAsString(result));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleCreateData(RoutingContext ctx){
        String from = ctx.queryParam("from").get(0);
        String upto = ctx.queryParam("upto").get(0);
        vertx.<String>executeBlocking(promise -> {
            try {
                String apiDir = System.getenv("MYCLINIC_API_PROJECT_DIR");
                if( apiDir == null ){
                    throw new RuntimeException("env var not defined: " + "MYCLINIC_API_PROJECT_DIR");
                }
                ProcessBuilder pb = new ProcessBuilder("python",
                        "presc.py", "data", from, upto)
                        .directory(new File(apiDir));
                Map<String, String> env = pb.environment();
                env.put("MYCLINIC_CONFIG", System.getenv("MYCLINIC_CONFIG_DIR"));
                Process process = pb.start();
                InputStream is = process.getInputStream();
                InputStream es = process.getErrorStream();
                String stdOut = readInputStream(is);
                String stdErr = readInputStream(es);
                Map<String, String> map = new HashMap<>();
                map.put("stdout", stdOut);
                map.put("stderr", stdErr);
                promise.complete(mapper.writeValueAsString(map));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, ar -> {
            if( ar.succeeded() ){
                ctx.response().putHeader("content-type", "application/json; charset=UTF-8")
                        .end(ar.result());
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    private String getManagementRootDir() {
        String dir = System.getenv("MYCLINIC_FAXED_SHOHOUSEN_DATA_DIR");
        if (dir == null) {
            throw new RuntimeException("env var is not defined: " + "MYCLINIC_FAXED_SHOHOUSEN_DATA_DIR");
        }
        if (!Files.isDirectory(Path.of(dir))) {
            throw new RuntimeException("is not directory: " + dir);
        }
        return dir;
    }

    private String readInputStream(InputStream is){
        InputStreamReader streamReader = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(streamReader);
        return reader.lines().collect(Collectors.joining("\n"));
    }

    private String getFaxedShohousenDataDirName(String from, String upto){
        return String.format("%s-%s", from, upto);
    }

    private String getFaxedShohousenDataDirFullName(String from, String upto){
        return Path.of(getManagementRootDir(),
                getFaxedShohousenDataDirName(from, upto)).toString();
    }

    private String ensureFaxedShohousenDataDir(String from, String upto){
        Path path = Path.of(getFaxedShohousenDataDirFullName(from, upto));
        if( !Files.isDirectory(path) ){
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return path.toString();
    }

    private String makeFaxedShohousenDataFileName(String from, String upto){
        return String.format("shohousen-data-%s-%s.json", from, upto);
    }

}
