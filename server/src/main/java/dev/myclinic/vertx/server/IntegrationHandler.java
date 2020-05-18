package dev.myclinic.vertx.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
        return router;
    }

}
