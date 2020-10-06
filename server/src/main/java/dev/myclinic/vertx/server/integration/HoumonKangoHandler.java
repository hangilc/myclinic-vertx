package dev.myclinic.vertx.server.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.drawer.DrawerCompiler;
import dev.myclinic.vertx.drawer.Op;
import dev.myclinic.vertx.drawer.PaperSize;
import dev.myclinic.vertx.drawer.Render;
import dev.myclinic.vertx.drawer.form.Form;
import dev.myclinic.vertx.drawer.pdf.PdfPrinter;
import dev.myclinic.vertx.server.GlobalService;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

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
            if (ar.succeeded()) {
                ctx.response().end(ar.result());
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    private void handleGetRecord(RoutingContext ctx) {
        int patientId = IntegrationUtil.getIntParam(ctx, "patient-id");
        Path path = getRecordPath(patientId);
        if (!Files.exists(path)) {
            ctx.response().end(String.format("{\"patientId\": %d, \"history\": []}", patientId));
        } else {
            ctx.response().sendFile(path.toFile().getAbsolutePath());
        }
    }

    private String jsonEncode(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
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

    public void handleCreateShijisho(RoutingContext ctx) {
        vertx.<String>executeBlocking(promise -> {
            try {
                String rsrc = "houmon-kango-form.json";
                URL url = getClass().getClassLoader().getResource(rsrc);
                Form form = mapper.readValue(url, Form.class);
                DrawerCompiler c = new DrawerCompiler();
                Map<String, Object> params = mapper.readValue(
                        ctx.getBody().getBytes(),
                        new TypeReference<>() {
                        }
                );
                for (String key : params.keySet()) {
                    String value = params.get(key).toString();
                    render.add(key, value);
                }
                List<Op> ops = render.getOps();
                GlobalService gs = GlobalService.getInstance();
                String outFileIdToken = gs.createTempAppFilePath(
                        GlobalService.getInstance().portalTmpDirToken,
                        "houmon-kango", ".pdf"
                );
                Path outFile = gs.resolveAppPath(outFileIdToken);
                PaperSize resolvedPaperSize = resolvePaperSize(form.page);
                PdfPrinter pdfPrinter = new PdfPrinter(resolvedPaperSize);
                pdfPrinter.print(List.of(ops), outFile.toString());
                promise.complete(jsonEncode(outFileIdToken));
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

    private void handleListParams(RoutingContext ctx) {
        vertx.<String>executeBlocking(promise -> {
            try {
                String rsrc = "houmon-kango-form.json";
                URL url = getClass().getClassLoader().getResource(rsrc);
                Render.Form form = mapper.readValue(url, Render.Form.class);
                StringBuilder sb = new StringBuilder();
                for (String desc : form.descriptions) {
                    sb.append(desc);
                    sb.append("\n");
                }
                byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
                Buffer buffer = Buffer.buffer(bytes);
                ctx.response().putHeader("content-type", "text/plain; charset=UTF-8");
                ctx.response().putHeader("content-length", String.format("%d", bytes.length));
                ctx.response().write(buffer);
            } catch (Exception e) {
                promise.fail(e);
            }
        }, ar ->
        {
            if (ar.succeeded()) {
                ctx.response().end();
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    private Path getHoumonKangoConfigDir() {
        return Path.of(IntegrationUtil.getConfigDir(), "houmon-kango");
    }

    private Path getRecordPath(int patientId) {
        String file = String.format("record-%d.json", patientId);
        return getHoumonKangoConfigDir().resolve(file);
    }

}
