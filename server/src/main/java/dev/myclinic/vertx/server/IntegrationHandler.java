package dev.myclinic.vertx.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class IntegrationHandler {

    public static Router createRouter(Vertx vertx, ObjectMapper mapper) {
        Router router = Router.router(vertx);
        router.route("/faxed-shohousen-data/list-groups").handler(ctx -> {
            String dir = System.getenv("MYCLINIC_FAXED_SHOHOUSEN_DATA_DIR");
            if( dir == null ){
                throw new RuntimeException("env var is not defined: " + "MYCLINIC_FAXED_SHOHOUSEN_DATA_DIR");
            }
            if( !Files.isDirectory(Path.of(dir)) ){
                throw new RuntimeException("is not directory: " + dir);
            }
            try {
                List<String> result = new ArrayList<>();
                Files.newDirectoryStream(Path.of(dir)).forEach(path -> {
                    String name = path.toFile().getName();
                    result.add(name);
                });
                ctx.response().putHeader("content-type", "application/json; charset=UTF-8")
                        .end(mapper.writeValueAsString(result));
            } catch(Exception e){
                throw new RuntimeException(e);
            }
        });
        router.route("/faxed-shohousen-data/create-data").handler(ctx -> {
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

        });
        return router;
    }

    private static String readInputStream(InputStream is){
        InputStreamReader streamReader = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(streamReader);
        return reader.lines().collect(Collectors.joining());
    }


}
