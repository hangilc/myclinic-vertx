package dev.myclinic.vertx.server.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.myclinic.vertx.server.integration.IntegrationUtil.ExecResult;
import static dev.myclinic.vertx.server.integration.IntegrationUtil.ExecRequest;

public class HoumonKangoHandler {

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
        router.route(HttpMethod.GET, "/create-shijisho").handler(this::handleCreateShijisho);
        router.route(HttpMethod.GET, "/list-params").handler(this::handleListParams);
    }

    private void handleCreateShijisho(RoutingContext ctx) {
        ctx.response().end("done");
        Path springProjectDir = IntegrationUtil.getMyclinicSpringProjectDir();
        String url = "https://deno.myclinic.dev/houmon-kango/create-houmon-kango-form.ts";
        vertx.<Buffer>executeBlocking(promise -> {
            ExecRequest req1 = new ExecRequest();
            req1.command = List.of("deno", "run", "--allow-net", url);
            Map<String, String> env = new HashMap<>();
            env.put("NO_COLOR", "yes");
            ExecResult er1 = IntegrationUtil.exec(req1);
            if (er1.isError()) {
                promise.fail(er1.getErrorMessage());
                return;
            }
            byte[] drawer = er1.stdOut;
            Path jar = Path.of(springProjectDir.toFile().getAbsolutePath(), "drawer-printer",
                    "target", "drawer-printer-1.0.0-SNAPSHOT.jar");
            ExecRequest req2 = new ExecRequest();
            req2.command = List.of("java", "-jar", jar.toString(), "-e", "utf-8", "--pdf", "-");
            req2.stdIn = drawer;
            ExecResult er2 = IntegrationUtil.exec(req2);
            if (er2.isError()) {
                promise.fail(er2.getErrorMessage());
                return;
            }
            ctx.response().putHeader("content-type", "application/pdf");
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

}
