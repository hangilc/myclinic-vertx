package dev.myclinic.vertx.server.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.drawer.Op;
import dev.myclinic.vertx.drawer.pdf.PdfPrinter;
import dev.myclinic.vertx.multidrawer.text.TextDrawer;
import dev.myclinic.vertx.prescfax.Data;
import dev.myclinic.vertx.server.GlobalService;
import dev.myclinic.vertx.shohousendrawer.ShohousenData;
import dev.myclinic.vertx.shohousendrawer.ShohousenDrawer;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FaxedShohousenHandler {
    private static final Logger logger = LoggerFactory.getLogger(FaxedShohousenHandler.class);

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
        router.route(HttpMethod.GET, "/list-groups").handler(this::handleListGroups);
        router.route(HttpMethod.GET, "/get-last-group").handler(this::handleGetLastGroup);
        router.route(HttpMethod.GET, "/get-group").handler(this::handleGetGroup);
        router.route(HttpMethod.GET, "/count-shohousen").handler(this::handleCountShohousen);
        router.route(HttpMethod.GET, "/shohousen-pdf").handler(this::handleShohousenPdf);
        router.route(HttpMethod.GET, "/pharma-letter-pdf").handler(this::handlePharmaLetterPdf);
        router.route(HttpMethod.GET, "/pharma-label-pdf").handler(this::handlePharmaLabelPdf);
        router.route(HttpMethod.GET, "/clinic-label-pdf").handler(this::handleClinicLabelPdf);

        router.route(HttpMethod.POST, "/create-data").handler(this::handleCreateData);
        router.route(HttpMethod.POST, "/create-shohousen-text").handler(this::handleCreateShohousenText);
        router.route(HttpMethod.POST, "/create-shohousen-pdf").handler(this::handleCreateShohousenPdf);
        router.route(HttpMethod.POST, "/create-pharma-letter-text").handler(this::handleCreatePharmaLetterText);
        router.route(HttpMethod.POST, "/create-pharma-letter-pdf").handler(this::handleCreatePharmaLetterPdf);
        router.route(HttpMethod.POST, "/create-pharma-label-pdf").handler(this::handleCreatePharmaLabelPdf);
        router.route(HttpMethod.POST, "/create-clinic-label-pdf").handler(this::handleCreateClinicLabelPdf);
    }

    private void handleClinicLabelPdf(RoutingContext ctx) {
        String from = ctx.queryParam("from").get(0).replace("-", "");
        String upto = ctx.queryParam("upto").get(0).replace("-", "");
        ctx.response().putHeader("content-type", "application/pdf")
                .sendFile(clinicLabelPdf(from, upto).resolve().toFile().getAbsolutePath());
    }

    private void handlePharmaLabelPdf(RoutingContext ctx) {
        String from = ctx.queryParam("from").get(0).replace("-", "");
        String upto = ctx.queryParam("upto").get(0).replace("-", "");
        ctx.response().putHeader("content-type", "application/pdf")
                .sendFile(pharmaLabelPdf(from, upto).resolve().toFile().getAbsolutePath());
    }

    private void handlePharmaLetterPdf(RoutingContext ctx) {
        String from = ctx.queryParam("from").get(0).replace("-", "");
        String upto = ctx.queryParam("upto").get(0).replace("-", "");
        ctx.response().putHeader("content-type", "application/pdf")
                .sendFile(pharmaLetterPdf(from, upto).resolve().toFile().getAbsolutePath());
    }

    private void handleShohousenPdf(RoutingContext ctx) {
        String from = ctx.queryParam("from").get(0).replace("-", "");
        String upto = ctx.queryParam("upto").get(0).replace("-", "");
        ctx.response().putHeader("content-type", "application/pdf")
                .sendFile(shohousenPdfFile(from, upto).resolve().toFile().getAbsolutePath());
    }

    private void handleCountShohousen(RoutingContext ctx) {
        String from = ctx.queryParam("from").get(0).replace("-", "");
        String upto = ctx.queryParam("upto").get(0).replace("-", "");
        Path dir = groupDir(from, upto).resolve();
        Path dataFile = dataFile(from, upto).resolve();
        vertx.<String>executeBlocking(promise -> {
            try {
                JsonNode root = mapper.readValue(dataFile.toFile(), JsonNode.class);
                JsonNode groups = root.get("groups");
                promise.complete(String.format("%d", groups.size()));
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

    private void handleCreateClinicLabelPdf(RoutingContext ctx) {
        String fromDate = ctx.queryParam("from").get(0);
        String uptoDate = ctx.queryParam("upto").get(0);
        String from = fromDate.replace("-", "");
        String upto = uptoDate.replace("-", "");
        Optional<Integer> optRow = getOptionalIntParam(ctx, "row");
        Optional<Integer> optCol = getOptionalIntParam(ctx, "col");
        int n = getIntParam(ctx, "n");
        vertx.<String>executeBlocking(promise -> {
            Path tempFile1 = null;
            Path tempFile2 = null;
            try {
                tempFile1 = Files.createTempFile("myclinic-", "-clinic-label.txt");
                ArrayList<String> commands = new ArrayList<>(List.of(
                        "python", "presc.py", "clinic-label",
                        "-n", String.format("%d", n),
                        "-o", tempFile1.toFile().getAbsolutePath()
                ));
                ExecResult result = execInMyclinicApi(commands);
                if (result.isError()) {
                    promise.complete(result.errorAsJson(mapper));
                    return;
                }
                tempFile2 = Files.createTempFile("myclinic-", "-clinic-label-drawer.txt");
                commands.clear();
                commands.addAll(new ArrayList<>(List.of(
                        "java", "-jar", jarFile("multi-drawer-cli"), "seal8x3",
                        "-i", tempFile1.toFile().getAbsolutePath(),
                        "-o", tempFile2.toFile().getAbsolutePath()
                )));
                optRow.ifPresent(row -> commands.addAll(List.of("-r", String.format("%d", row))));
                optCol.ifPresent(col -> commands.addAll(List.of("-c", String.format("%d", col))));
                result = execInMyclinicSpring(commands);
                if (result.isError()) {
                    promise.complete(result.errorAsJson(mapper));
                    return;
                }
                Path groupDir = groupDir(from, upto).resolve();
                String pdfFileName = clinicLabelPdfFileName(from, upto);
                Path pdfFile = groupDir.resolve(pdfFileName);
                commands.clear();
                commands.addAll(List.of(
                        "java", "-jar", jarFile("drawer-printer"),
                        "-i", tempFile2.toFile().getAbsolutePath(),
                        "--pdf", pdfFile.toFile().getAbsolutePath()
                ));
                result = execInMyclinicSpring(commands);
                if (result.isError()) {
                    promise.complete(result.errorAsJson(mapper));
                } else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("success", true);
                    reportFileStatus(map, pdfFile, "clinicLabelPdfFile");
                    promise.complete(mapper.writeValueAsString(map));
                }
            } catch (Exception e) {
                ctx.fail(e);
            } finally {
                if (tempFile1 != null) {
                    //noinspection ResultOfMethodCallIgnored
                    tempFile1.toFile().delete();
                }
                if (tempFile2 != null) {
                    //noinspection ResultOfMethodCallIgnored
                    tempFile2.toFile().delete();
                }
            }
        }, ar -> {
            if (ar.succeeded()) {
                ctx.response().end(ar.result());
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    private String jarFile(String module) {
        return String.format("%s\\target\\%s-1.0.0-SNAPSHOT.jar", module, module);
    }

    private String getConfigDir() {
        return GlobalService.getInstance().resolveAppPath(
                GlobalService.getInstance().configDirToken
        ).toString();
//        return System.getenv("MYCLINIC_CONFIG_DIR");
    }

    private Optional<Integer> getOptionalIntParam(RoutingContext ctx, String name) {
        List<String> list = ctx.queryParam(name);
        if (list.size() == 0) {
            return Optional.empty();
        } else if (list.size() == 1) {
            String para = list.get(0);
            try {
                int ival = Integer.parseInt(para);
                return Optional.of(ival);
            } catch (NumberFormatException e) {
                throw new RuntimeException(String.format("INvalid number (%s): %s", name, para));
            }
        } else {
            throw new RuntimeException("Multiple values supplied: " + name);
        }
    }

    private int getIntParam(RoutingContext ctx, String name) {
        List<String> list = ctx.queryParam(name);
        if (list.size() == 0) {
            throw new RuntimeException("Missing param: " + name);
        } else if (list.size() == 1) {
            String para = list.get(0);
            try {
                return Integer.parseInt(para);
            } catch (NumberFormatException e) {
                throw new RuntimeException(String.format("Invalid number (%s): %s", name, para));
            }
        } else {
            throw new RuntimeException("Multiple values supplied: " + name);
        }
    }

    private static class ExecResult {
        int retCode;
        String stdOut;
        String stdErr;

        ExecResult(int retCode, String stdOut, String stdErr) {
            this.retCode = retCode;
            this.stdOut = stdOut;
            this.stdErr = stdErr;
        }

        boolean isError() {
            return retCode != 0;
        }

        String errorAsJson(ObjectMapper mapper) throws JsonProcessingException {
            Map<String, Object> m = Map.of("success", !isError(), "errorMessage", stdErr);
            return mapper.writeValueAsString(m);
        }
    }

    private ExecResult exec(List<String> commands, File directory, Map<String, String> env)
            throws IOException {
        ProcessBuilder pb = new ProcessBuilder(commands);
        if (directory != null) {
            pb.directory(directory);
        }
        if (env != null) {
            Map<String, String> penv = pb.environment();
            for (String key : env.keySet()) {
                penv.put(key, env.get(key));
            }
        }
        Process process = pb.start();
        InputStream os = process.getInputStream();
        InputStream es = process.getErrorStream();
        String stdOut = readInputStream(os);
        String stdErr = readInputStream(es);
        return new ExecResult(process.exitValue(), stdOut, stdErr);
    }

    private ExecResult execInMyclinicApi(List<String> commands) throws IOException {
        Path apiDir = getMyclinicApiProjectDir();
        Map<String, String> env = Map.of("MYCLINIC_CONFIG", getConfigDir());
        return exec(commands, apiDir.toFile(), env);
    }

    private ExecResult execInMyclinicSpring(List<String> commands) throws IOException {
        Path springDir = getMyclinicSpringProjectDir();
        Map<String, String> env = Map.of("MYCLINIC_CONFIG", getConfigDir());
        return exec(commands, springDir.toFile(), env);
    }

    private Map<String, Object> createPharmaLabelTextFile(File dataFile, File outFile) throws IOException {
        Path apiDir = getMyclinicApiProjectDir();
        ProcessBuilder pb1 = new ProcessBuilder("python",
                "presc.py", "pharma-label",
                "-i", dataFile.getAbsolutePath(),
                "-o", outFile.getAbsolutePath())
                .directory(apiDir.toFile());
        Map<String, String> env1 = pb1.environment();
        env1.put("MYCLINIC_CONFIG", getConfigDir());
        return execOld(pb1);
    }

    private Map<String, Object> createPharmaLabelDrawerFile(File textFile, File outFile,
                                                            int row, int col) throws IOException {
        Path sprintDir = getMyclinicSpringProjectDir();
        ProcessBuilder pb1 = new ProcessBuilder(
                "java", "-jar",
                "multi-drawer-cli\\target\\multi-drawer-cli-1.0.0-SNAPSHOT.jar",
                "seal8x3",
                "-r", String.format("%d", row),
                "-c", String.format("%d", col),
                "-i", textFile.getAbsolutePath(),
                "-o", outFile.getAbsolutePath())
                .directory(sprintDir.toFile());
        Map<String, String> env1 = pb1.environment();
        env1.put("MYCLINIC_CONFIG", System.getenv("MYCLINIC_CONFIG_DIR"));
        return execOld(pb1);
    }

    private Map<String, Object> createPdfFile(File drawerFile, File outFile, List<String> opts) throws IOException {
        Path sprintDir = getMyclinicSpringProjectDir();
        List<String> commands;
        commands = new ArrayList<>(List.of(
                "java",
                "-jar", "drawer-printer\\target\\drawer-printer-1.0.0-SNAPSHOT.jar",
                "-i", drawerFile.getAbsolutePath(),
                "--pdf", outFile.getAbsolutePath()));
        if (opts != null) {
            commands.addAll(opts);
        }
        ProcessBuilder pb = new ProcessBuilder(commands).directory(sprintDir.toFile());
        Map<String, String> env = pb.environment();
        env.put("MYCLINIC_CONFIG", getConfigDir());
        return execOld(pb);
    }

    private void handleCreatePharmaLabelPdf(RoutingContext ctx) {
        String fromDate = ctx.queryParam("from").get(0);
        String uptoDate = ctx.queryParam("upto").get(0);
        String from = fromDate.replace("-", "");
        String upto = uptoDate.replace("-", "");
        Optional<Integer> optRow = getOptionalIntParam(ctx, "row");
        Optional<Integer> optCol = getOptionalIntParam(ctx, "col");
        int row = optRow.orElse(1);
        int col = optCol.orElse(1);
        vertx.<String>executeBlocking(promise -> {
            Path tempFile1 = null;
            Path tempFile2 = null;
            try {
                tempFile1 = Files.createTempFile("myclinic-", "-pharma-label.txt");
                Path groupDir = groupDir(from, upto).resolve();
                String dataFileName = dataFileName(from, upto);
                Path dataFile = groupDir.resolve(dataFileName);
                Map<String, Object> result = createPharmaLabelTextFile(dataFile.toFile(), tempFile1.toFile());
                if (!result.get("success").equals(true)) {
                    promise.complete(mapper.writeValueAsString(result));
                    return;
                }
                tempFile2 = Files.createTempFile("myclinic-", "-pharma-label-drawer.txt");
                result = createPharmaLabelDrawerFile(tempFile1.toFile(), tempFile2.toFile(),
                        row, col);
                if (!result.get("success").equals(true)) {
                    promise.complete(mapper.writeValueAsString(result));
                    return;
                }
                String pharmaLabelPdfFileName = pharmaLabelPdfFileName(from, upto);
                Path pharmaLabelPdfFile = groupDir.resolve(pharmaLabelPdfFileName);
                result = createPdfFile(tempFile2.toFile(), pharmaLabelPdfFile.toFile(), null);
                if (!result.get("success").equals(true)) {
                    promise.complete(mapper.writeValueAsString(result));
                    return;
                } else {
                    reportFileStatus(result, pharmaLabelPdfFile, "pharmaLabelPdfFile");
                }
                promise.complete(mapper.writeValueAsString(result));
            } catch (Exception e) {
                ctx.fail(e);
            } finally {
                if (tempFile1 != null) {
                    //noinspection ResultOfMethodCallIgnored
                    tempFile1.toFile().delete();
                }
                if (tempFile2 != null) {
                    //noinspection ResultOfMethodCallIgnored
                    tempFile2.toFile().delete();
                }
            }
        }, ar -> {
            if (ar.succeeded()) {
                ctx.response().end(ar.result());
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    private void handleCreatePharmaLetterPdf(RoutingContext ctx) {
        String fromDate = ctx.queryParam("from").get(0);
        String uptoDate = ctx.queryParam("upto").get(0);
        String from = fromDate.replace("-", "");
        String upto = uptoDate.replace("-", "");
        GlobalService.getInstance().executorService.execute(() -> {
            try {
                GlobalService.AppDirToken groupDir = groupDir(from, upto);
                String textFileName = pharmaLetterTextFileName(from, upto);
                var textFile = groupDir.toFileToken(textFileName);
                String pharmaLetterText = textFile.read();
                TextDrawer textDrawer = new TextDrawer();
                List<List<Op>> pages = textDrawer.draw(pharmaLetterText);
                String pdfFileName = pharmaLetterPdfFileName(from, upto);
                Path pdfFile = groupDir.toFileToken(pdfFileName).resolve();
                PdfPrinter pdfPrinter = new PdfPrinter("A4");
                pdfPrinter.print(pages, pdfFile.toString());
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                reportFileStatus(result, pdfFile, "pharmaLetterPdfFile");
                ctx.response().end(mapper.writeValueAsString(result));
            } catch (Exception e) {
                throw new RuntimeException(e);
            };
        });
    }

//    private void handleCreatePharmaLetterPdf(RoutingContext ctx) {
//        String fromDate = ctx.queryParam("from").get(0);
//        String uptoDate = ctx.queryParam("upto").get(0);
//        String from = fromDate.replace("-", "");
//        String upto = uptoDate.replace("-", "");
//        vertx.<String>executeBlocking(promise -> {
//            Path tempFile = null;
//            try {
//                tempFile = Files.createTempFile("myclinic-", "-pharma-letter-drawer.txt");
//                Path sprintDir = getMyclinicSpringProjectDir();
//                Path groupDir = groupDir(from, upto).resolve();
//                String textFileName = pharmaLetterTextFileName(from, upto);
//                Path textFile = groupDir.resolve(textFileName);
//                ProcessBuilder pb1 = new ProcessBuilder(
//                        "java", "-jar",
//                        "multi-drawer-cli\\target\\multi-drawer-cli-1.0.0-SNAPSHOT.jar",
//                        "text",
//                        "-i", textFile.toFile().getAbsolutePath(),
//                        "-o", tempFile.toFile().getAbsolutePath())
//                        .directory(sprintDir.toFile());
//                Map<String, String> env1 = pb1.environment();
//                env1.put("MYCLINIC_CONFIG", System.getenv("MYCLINIC_CONFIG_DIR"));
//                Map<String, Object> result1 = execOld(pb1);
//                if (!result1.get("success").equals(true)) {
//                    promise.complete(mapper.writeValueAsString(result1));
//                    return;
//                }
//                String pdfFileName = pharmaLetterPdfFileName(from, upto);
//                Path pdfFile = groupDir.resolve(pdfFileName);
//                ProcessBuilder pb2 = new ProcessBuilder(
//                        "java", "-jar",
//                        "drawer-printer\\target\\drawer-printer-1.0.0-SNAPSHOT.jar",
//                        "-i", tempFile.toFile().getAbsolutePath(),
//                        "--pdf", pdfFile.toFile().getAbsolutePath())
//                        .directory(sprintDir.toFile());
//                Map<String, String> env2 = pb1.environment();
//                env2.put("MYCLINIC_CONFIG", System.getenv("MYCLINIC_CONFIG_DIR"));
//                Map<String, Object> result2 = execOld(pb2);
//                if (result2.get("success").equals(true)) {
//                    reportFileStatus(result2, pdfFile, "pharmaLetterPdfFile");
//                }
//                promise.complete(mapper.writeValueAsString(result2));
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            } finally {
//                if (tempFile != null) {
//                    //noinspection ResultOfMethodCallIgnored
//                    tempFile.toFile().delete();
//                }
//            }
//        }, ar -> {
//            if (ar.succeeded()) {
//                ctx.response().putHeader("content-type", "application/json; charset=UTF-8")
//                        .end(ar.result());
//            } else {
//                ctx.fail(ar.cause());
//            }
//        });
//    }

        private void handleCreatePharmaLetterText (RoutingContext ctx){
            String fromDate = ctx.queryParam("from").get(0);
            String uptoDate = ctx.queryParam("upto").get(0);
            String from = fromDate.replace("-", "");
            String upto = uptoDate.replace("-", "");
            GlobalService.getInstance().executorService.execute(() -> {
                try {
                    GlobalService.AppDirToken groupDir = groupDir(from, upto);
                    String dataFileName = dataFileName(from, upto);
                    Path dataFile = groupDir.toFileToken(dataFileName).resolve();
                    dev.myclinic.vertx.prescfax.Data data =
                            mapper.readValue(dataFile.toFile(),
                                    dev.myclinic.vertx.prescfax.Data.class);
                    String textFileName = pharmaLetterTextFileName(from, upto);
                    GlobalService.AppFileToken textFile = groupDir.toFileToken(textFileName);
                    String letterText = dev.myclinic.vertx.prescfax.PharmaLetterText.createFromData(data);
                    textFile.write(letterText);
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", true);
                    reportFileStatus(result, textFile.resolve(), "pharmaLetterTextFile");
                    ctx.response().end(mapper.writeValueAsString(result));
                } catch (Exception e) {
                    ctx.fail(e);
                }
            });
        }

        private Map<String, Object> execOld (ProcessBuilder pb) throws IOException {
            Process process = pb.start();
            InputStream os = process.getInputStream();
            InputStream es = process.getErrorStream();
            String stdOut = readInputStream(os);
            String stdErr = readInputStream(es);
            boolean isSuccess = process.exitValue() == 0;
            Map<String, Object> result = new HashMap<>();
            result.put("success", isSuccess);
            result.put("stdOut", stdOut);
            result.put("stdErr", stdErr);
            return result;
        }

        private void handleCreateShohousenPdf (RoutingContext ctx){
            String fromDate = ctx.queryParam("from").get(0);
            String uptoDate = ctx.queryParam("upto").get(0);
            String from = fromDate.replace("-", "");
            String upto = uptoDate.replace("-", "");
            GlobalService.getInstance().executorService.execute(() -> {
                try {
                    Path groupDir = groupDir(from, upto).resolve();
                    String shohousenTextFileName = shohousenTextFileName(from, upto);
                    Path textFile = groupDir.resolve(shohousenTextFileName);
                    List<dev.myclinic.vertx.prescfax.Text> texts =
                            mapper.readValue(textFile.toFile(),
                                    new TypeReference<>() {
                                    });
                    List<ShohousenData> dataList = texts.stream().map(this::shohousenTextToData)
                            .collect(Collectors.toList());
                    List<List<Op>> pages = new ArrayList<>();
                    for (ShohousenData data : dataList) {
                        ShohousenDrawer drawer = new ShohousenDrawer();
                        drawer.init();
                        data.applyTo(drawer);
                        List<Op> ops = drawer.getOps();
                        pages.add(ops);
                    }
                    String pdfFileName = shohousenPdfFileName(from, upto);
                    Path pdfFile = groupDir.resolve(pdfFileName);
                    PdfPrinter pdfPrinter = new PdfPrinter("A5");
                    pdfPrinter.print(pages, pdfFile.toString());
                    Map<String, Object> result2 = new HashMap<>();
                    result2.put("success", true);
                    reportFileStatus(result2, pdfFile, "shohousenPdfFile");
                    ctx.response().end(mapper.writeValueAsString(result2));
                } catch (Exception e) {
                    ctx.fail(e);
                }
            });
//        vertx.<String>executeBlocking(promise -> {
//            Path tempFile = null;
//            try {
//                String tmpFileToken = GlobalService.getInstance().createTempAppFilePath(
//                        GlobalService.getInstance().portalTmpDirToken,
//                        "myclinic-", "-shohousen.txt");
//                tempFile = GlobalService.getInstance().resolveAppPath(tmpFileToken);
//                //tempFile = Files.createTempFile("myclinic-", "-shohousen.txt");
//                Path springDir = getMyclinicSpringProjectDir();
//                Path groupDir = groupDir(from, upto).resolve();
//                String shohousenTextFileName = shohousenTextFileName(from, upto);
//                Path textFile = groupDir.resolve(shohousenTextFileName);
//                ProcessBuilder pb1 = new ProcessBuilder(
//                        "java", "-jar",
//                        "shohousen-drawer\\target\\shohousen-drawer-1.0.0-SNAPSHOT.jar",
//                        "-i", textFile.toFile().getAbsolutePath(),
//                        "-o", tempFile.toFile().getAbsolutePath())
//                        .directory(springDir.toFile());
//                Map<String, String> env1 = pb1.environment();
//                env1.put("MYCLINIC_CONFIG", System.getenv("MYCLINIC_CONFIG_DIR"));
//                Map<String, Object> result1 = execOld(pb1);
//                if (!result1.get("success").equals(true)) {
//                    System.err.println(result1.get("stdErr"));
//                    promise.complete(mapper.writeValueAsString(result1.get("stdErr")));
//                    return;
//                }
//                String pdfFileName = shohousenPdfFileName(from, upto);
//                Path pdfFile = groupDir.resolve(pdfFileName);
//                ProcessBuilder pb2 = new ProcessBuilder(
//                        "java", "-jar",
//                        "drawer-printer\\target\\drawer-printer-1.0.0-SNAPSHOT.jar",
//                        "--pdf-page-size", "A5",
//                        "--pdf-shrink", "2",
//                        "-i", tempFile.toFile().getAbsolutePath(),
//                        "--pdf", pdfFile.toFile().getAbsolutePath())
//                        .directory(springDir.toFile());
//                Map<String, String> env2 = pb1.environment();
//                env2.put("MYCLINIC_CONFIG", System.getenv("MYCLINIC_CONFIG_DIR"));
//                Map<String, Object> result2 = execOld(pb2);
//                if (result2.get("success").equals(true)) {
//                    reportFileStatus(result2, pdfFile, "shohousenPdfFile");
//                } else {
//                    System.err.println(result2.get("stdErr"));
//                    promise.complete(mapper.writeValueAsString(result2.get("stdErr")));
//                }
//                promise.complete(mapper.writeValueAsString(result2));
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new RuntimeException(e);
//            } finally {
//                if (tempFile != null) {
//                    //noinspection ResultOfMethodCallIgnored
//                    tempFile.toFile().delete();
//                }
//            }
//        }, ar -> {
//            if (ar.succeeded()) {
//                ctx.response().putHeader("content-type", "application/json; charset=UTF-8")
//                        .end(ar.result());
//            } else {
//                ar.cause().printStackTrace();
//                ctx.fail(ar.cause());
//            }
//        });
        }

//    private void handleCreateShohousenPdf(RoutingContext ctx) {
//        String fromDate = ctx.queryParam("from").get(0);
//        String uptoDate = ctx.queryParam("upto").get(0);
//        String from = fromDate.replace("-", "");
//        String upto = uptoDate.replace("-", "");
//        vertx.<String>executeBlocking(promise -> {
//            Path tempFile = null;
//            try {
//                String tmpFileToken = GlobalService.getInstance().createTempAppFilePath(
//                        GlobalService.getInstance().portalTmpDirToken,
//                        "myclinic-", "-shohousen.txt");
//                tempFile = GlobalService.getInstance().resolveAppPath(tmpFileToken);
//                //tempFile = Files.createTempFile("myclinic-", "-shohousen.txt");
//                Path springDir = getMyclinicSpringProjectDir();
//                Path groupDir = groupDir(from, upto).resolve();
//                String shohousenTextFileName = shohousenTextFileName(from, upto);
//                Path textFile = groupDir.resolve(shohousenTextFileName);
//                ProcessBuilder pb1 = new ProcessBuilder(
//                        "java", "-jar",
//                        "shohousen-drawer\\target\\shohousen-drawer-1.0.0-SNAPSHOT.jar",
//                        "-i", textFile.toFile().getAbsolutePath(),
//                        "-o", tempFile.toFile().getAbsolutePath())
//                        .directory(springDir.toFile());
//                Map<String, String> env1 = pb1.environment();
//                env1.put("MYCLINIC_CONFIG", System.getenv("MYCLINIC_CONFIG_DIR"));
//                Map<String, Object> result1 = execOld(pb1);
//                if (!result1.get("success").equals(true)) {
//                    System.err.println(result1.get("stdErr"));
//                    promise.complete(mapper.writeValueAsString(result1.get("stdErr")));
//                    return;
//                }
//                String pdfFileName = shohousenPdfFileName(from, upto);
//                Path pdfFile = groupDir.resolve(pdfFileName);
//                ProcessBuilder pb2 = new ProcessBuilder(
//                        "java", "-jar",
//                        "drawer-printer\\target\\drawer-printer-1.0.0-SNAPSHOT.jar",
//                        "--pdf-page-size", "A5",
//                        "--pdf-shrink", "2",
//                        "-i", tempFile.toFile().getAbsolutePath(),
//                        "--pdf", pdfFile.toFile().getAbsolutePath())
//                        .directory(springDir.toFile());
//                Map<String, String> env2 = pb1.environment();
//                env2.put("MYCLINIC_CONFIG", System.getenv("MYCLINIC_CONFIG_DIR"));
//                Map<String, Object> result2 = execOld(pb2);
//                if (result2.get("success").equals(true)) {
//                    reportFileStatus(result2, pdfFile, "shohousenPdfFile");
//                } else {
//                    System.err.println(result2.get("stdErr"));
//                    promise.complete(mapper.writeValueAsString(result2.get("stdErr")));
//                }
//                promise.complete(mapper.writeValueAsString(result2));
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new RuntimeException(e);
//            } finally {
//                if (tempFile != null) {
//                    //noinspection ResultOfMethodCallIgnored
//                    tempFile.toFile().delete();
//                }
//            }
//        }, ar -> {
//            if (ar.succeeded()) {
//                ctx.response().putHeader("content-type", "application/json; charset=UTF-8")
//                        .end(ar.result());
//            } else {
//                ar.cause().printStackTrace();
//                ctx.fail(ar.cause());
//            }
//        });
//    }

        private void handleCreateShohousenText (RoutingContext ctx){
            String fromDate = ctx.queryParam("from").get(0);
            String uptoDate = ctx.queryParam("upto").get(0);
            String from = fromDate.replace("-", "");
            String upto = uptoDate.replace("-", "");
            GlobalService.getInstance().executorService.execute(() -> {
                try {
                    Path groupDir = groupDir(from, upto).resolve();
                    String dataFileName = dataFileName(from, upto);
                    Path dataFile = groupDir.resolve(dataFileName);
                    dev.myclinic.vertx.prescfax.Data data = mapper.readValue(dataFile.toFile(),
                            dev.myclinic.vertx.prescfax.Data.class);
                    List<dev.myclinic.vertx.prescfax.Text> texts =
                            dev.myclinic.vertx.prescfax.Text.createFromData(data);
                    String textFileName = shohousenTextFileName(from, upto);
                    Path textFile = groupDir.resolve(textFileName);
                    mapper.writeValue(textFile.toFile(), texts);
                    Map<String, Object> map = new HashMap<>();
                    map.put("success", true);
                    reportFileStatus(map, textFile, "shohousenTextFile");
                    ctx.response().end(mapper.writeValueAsString(map));
                } catch (Exception e) {
                    ctx.fail(e);
                }
            });
        }

//    private void handleCreateShohousenText(RoutingContext ctx) {
//        String fromDate = ctx.queryParam("from").get(0);
//        String uptoDate = ctx.queryParam("upto").get(0);
//        String from = fromDate.replace("-", "");
//        String upto = uptoDate.replace("-", "");
//        vertx.<String>executeBlocking(promise -> {
//            try {
//                Path apiDir = getMyclinicApiProjectDir();
//                Path groupDir = groupDir(from, upto).resolve();
//                //noinspection ResultOfMethodCallIgnored
//                groupDir.toFile().mkdir();
//                String dataFileName = dataFileName(from, upto);
//                Path dataFile = groupDir.resolve(dataFileName);
//                String textFileName = shohousenTextFileName(from, upto);
//                Path textFile = groupDir.resolve(textFileName);
//                ProcessBuilder pb = new ProcessBuilder("python",
//                        "presc.py", "print",
//                        "-i", dataFile.toFile().getAbsolutePath(),
//                        "-o", textFile.toFile().getAbsolutePath())
//                        .directory(apiDir.toFile());
//                Map<String, String> env = pb.environment();
//                env.put("MYCLINIC_CONFIG", System.getenv("MYCLINIC_CONFIG_DIR"));
//                Process process = pb.start();
//                InputStream is = process.getInputStream();
//                InputStream es = process.getErrorStream();
//                String stdErr = readInputStream(es);
//                boolean isSuccess = process.exitValue() == 0;
//                Map<String, Object> map = new HashMap<>();
//                map.put("success", isSuccess);
//                if (isSuccess) {
//                    reportFileStatus(map, textFile, "shohousenTextFile");
//                } else {
//                    map.put("errorMessage", stdErr);
//                }
//                promise.complete(mapper.writeValueAsString(map));
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }, ar -> {
//            if (ar.succeeded()) {
//                ctx.response().putHeader("content-type", "application/json; charset=UTF-8")
//                        .end(ar.result());
//            } else {
//                ctx.fail(ar.cause());
//            }
//        });
//    }

        private void handleGetGroup (RoutingContext ctx){
            String from = ctx.queryParam("from").get(0).replace("-", "");
            String upto = ctx.queryParam("upto").get(0).replace("-", "");
            Path dir = groupDir(from, upto).resolve();
            if (!Files.isDirectory(dir)) {
                ctx.response().end("null");
            } else {
                Map<String, Object> map = getGroupStatus(dir, from, upto);
                try {
                    ctx.response().end(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map));
                } catch (JsonProcessingException e) {
                    ctx.fail(e);
                }
            }
        }

        private void handleGetLastGroup (RoutingContext ctx){
            try {
                List<Path> paths = listGroupDirs();
                if (paths.size() == 0) {
                    ctx.response().end("null");
                } else {
                    paths.sort(Comparator.naturalOrder());
                    Path lastDir = paths.get(paths.size() - 1);
                    String name = lastDir.toFile().getName();
                    String[] dates = name.split("-");
                    String from = dates[0];
                    String upto = dates[1];
                    Map<String, Object> map = getGroupStatus(lastDir, from, upto);
                    ctx.response().end(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map));
                }
            } catch (IOException e) {
                ctx.fail(e);
            }
        }

        private Map<String, Object> getGroupStatus (Path groupDir, String from, String upto){
            String name = groupDirName(from, upto);
            Map<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("from", toSqlDateFormat(from));
            map.put("upto", toSqlDateFormat(upto));
            boolean done = reportFileStatus(map, groupDir.resolve(shohousenTextFileName(from, upto)),
                    "shohousenTextFile", true);
            done = reportFileStatus(map, groupDir.resolve(shohousenPdfFileName(from, upto)),
                    "shohousenPdfFile", done);
            done = reportFileStatus(map, groupDir.resolve(clinicLabelPdfFileName(from, upto)),
                    "clinicLabelPdfFile", done);
            done = reportFileStatus(map, groupDir.resolve(dataFileName(from, upto)),
                    "dataFile", done);
            done = reportFileStatus(map, groupDir.resolve(pharmaLabelPdfFileName(from, upto)),
                    "pharmaLabelPdfFile", done);
            done = reportFileStatus(map, groupDir.resolve(pharmaLetterPdfFileName(from, upto)),
                    "pharmaLetterPdfFile", done);
            done = reportFileStatus(map, groupDir.resolve(pharmaLetterTextFileName(from, upto)),
                    "pharmaLetterTextFile", done);
            map.put("completed", done);
            return map;
        }

        private static final Pattern patDateSpec = Pattern.compile("(\\d{4})(\\d{2})(\\d{2})");
        private static final Pattern patSqlDate = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
        private static final Pattern patGroupDir = Pattern.compile("\\d{8}-\\d{8}");

        private String toSqlDateFormat (String fromOrUpto){
            Matcher matcher = patDateSpec.matcher(fromOrUpto);
            if (matcher.matches()) {
                return String.format("%s-%s-%s", matcher.group(1), matcher.group(2), matcher.group(3));
            }
            matcher = patSqlDate.matcher(fromOrUpto);
            if (matcher.matches()) {
                return fromOrUpto;
            } else {
                throw new RuntimeException("Invalid Date: " + fromOrUpto);
            }
        }

        private static final DateTimeFormatter sqlDateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");

        private String getFileTimeRep (FileTime ft){
            return ft.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(sqlDateTimeFormatter);
        }

        private boolean reportFileStatus (Map < String, Object > map, Path path, String prefix){
            boolean exists = path.toFile().exists();
            if (exists) {
                map.put(prefix, prefix);
                map.put(prefix + "Size", path.toFile().length());
                try {
                    String modifiedAt = getFileTimeRep(Files.getLastModifiedTime(path));
                    map.put(prefix + "LastModifiedAt", modifiedAt);
                } catch (Exception e) {
                    logger.error("Cannot get file modification date.", e);
                }
            }
            return exists;
        }

        private boolean reportFileStatus (Map < String, Object > map, Path path, String prefix,boolean done){
            boolean exists = reportFileStatus(map, path, prefix);
            return exists && done;
        }

        private void handleListGroups (RoutingContext ctx){
            try {
                List<String> result = new ArrayList<>();
                List<Path> paths = listGroupDirs();
                paths.forEach(path -> {
                    String name = path.toFile().getName();
                    result.add(name);
                });
                ctx.response().end(mapper.writeValueAsString(result));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private void handleCreateData (RoutingContext ctx){
            String fromDate = ctx.queryParam("from").get(0);
            String uptoDate = ctx.queryParam("upto").get(0);
            String from = fromDate.replace("-", "");
            String upto = uptoDate.replace("-", "");
            GlobalService.getInstance().executorService.execute(() -> {
                try {
                    dev.myclinic.vertx.prescfax.Data data = Data.create(
                            GlobalService.getInstance().client,
                            LocalDate.parse(fromDate),
                            LocalDate.parse(uptoDate)
                    );
                    GlobalService.AppDirToken groupDir = groupDir(from, upto);
                    String dataFileName = dataFileName(from, upto);
                    GlobalService.AppFileToken dataFile = groupDir.toFileToken(dataFileName);
                    try (OutputStream os = dataFile.openOutputStream()) {
                        mapper.writeValue(os, data);
                    }
                    Map<String, Object> map = new HashMap<>();
                    map.put("success", true);
                    reportFileStatus(map, dataFile.resolve(), "dataFile");
                    ctx.response().end(mapper.writeValueAsString(map));
                } catch (Exception e) {
                    ctx.fail(e);
                }
            });
        }

//    private void handleCreateData(RoutingContext ctx) {
//        String fromDate = ctx.queryParam("from").get(0);
//        String uptoDate = ctx.queryParam("upto").get(0);
//        String from = fromDate.replace("-", "");
//        String upto = uptoDate.replace("-", "");
//        vertx.<String>executeBlocking(promise -> {
//            try {
//                Path apiDir = getMyclinicApiProjectDir();
//                Path groupDir = groupDir(from, upto).resolve();
//                //noinspection ResultOfMethodCallIgnored
//                groupDir.toFile().mkdir();
//                String dataFileName = dataFileName(from, upto);
//                Path dataFile = groupDir.resolve(dataFileName);
//                ProcessBuilder pb = new ProcessBuilder("python",
//                        "presc.py", "data", fromDate, uptoDate, "-o", dataFile.toFile().getAbsolutePath())
//                        .directory(apiDir.toFile());
//                Map<String, String> env = pb.environment();
//                env.put("MYCLINIC_CONFIG", System.getenv("MYCLINIC_CONFIG_DIR"));
//                Process process = pb.start();
//                InputStream is = process.getInputStream();
//                InputStream es = process.getErrorStream();
//                String stdErr = readInputStream(es);
//                boolean isSuccess = process.exitValue() == 0;
//                Map<String, Object> map = new HashMap<>();
//                map.put("success", isSuccess);
//                if (isSuccess) {
//                    reportFileStatus(map, dataFile, "dataFile");
//                } else {
//                    map.put("errorMessage", stdErr);
//                }
//                promise.complete(mapper.writeValueAsString(map));
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }, ar -> {
//            if (ar.succeeded()) {
//                ctx.response().putHeader("content-type", "application/json; charset=UTF-8")
//                        .end(ar.result());
//            } else {
//                ctx.fail(ar.cause());
//            }
//        });
//    }

        private List<Path> listGroupDirs () throws IOException {
            Path dir = getManagementRootDir().resolve();
            List<Path> result = new ArrayList<>();
            Files.newDirectoryStream(dir).forEach(path -> {
                Matcher matcher = patGroupDir.matcher(path.toFile().getName());
                if (matcher.matches()) {
                    result.add(path);
                }
            });
            return result;
        }

        private Path getMyclinicApiProjectDir () {
            return GlobalService.getInstance().resolveAppPath(
                    GlobalService.getInstance().myclinicApiProjectDirToken
            );
        }

        private Path getMyclinicSpringProjectDir () {
            return GlobalService.getInstance().resolveAppPath(
                    GlobalService.getInstance().myclinicSpringProjectDirToken
            );
        }

        private GlobalService.AppDirToken getManagementRootDir () {
            return new GlobalService.AppDirToken(
                    GlobalService.getInstance().shohousenFaxManagementDirToken,
                    Collections.emptyList()
            );
        }

        private String readInputStream (InputStream is){
            InputStreamReader streamReader = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(streamReader);
            return reader.lines().collect(Collectors.joining("\n"));
        }

        private String groupDirName (String from, String upto){
            return String.format("%s-%s", from, upto);
        }

        private GlobalService.AppDirToken groupDir (String from, String upto){
            return getManagementRootDir().addSubDir(groupDirName(from, upto));
        }

        private String shohousenTextFileName (String from, String upto){
            return String.format("shohousen-%s-%s.json", from, upto);
        }

        private String shohousenPdfFileName (String from, String upto){
            return String.format("shohousen-%s-%s.pdf", from, upto);
        }

        private GlobalService.AppFileToken shohousenPdfFile (String from, String upto){
            return groupDir(from, upto).toFileToken(shohousenPdfFileName(from, upto));
        }

        private String clinicLabelPdfFileName (String from, String upto){
            return String.format("shohousen-clinic-label-%s-%s.pdf", from, upto);
        }

        private GlobalService.AppFileToken clinicLabelPdf (String from, String upto){
            return groupDir(from, upto).toFileToken(clinicLabelPdfFileName(from, upto));
        }

        private String dataFileName (String from, String upto){
            return String.format("shohousen-data-%s-%s.json", from, upto);
        }

        private GlobalService.AppFileToken dataFile (String from, String upto){
            GlobalService.AppDirToken dir = groupDir(from, upto);
            return dir.toFileToken(dataFileName(from, upto));
        }

        private String pharmaLabelPdfFileName (String from, String upto){
            return String.format("shohousen-pharma-label-%s-%s.pdf", from, upto);
        }

        private GlobalService.AppFileToken pharmaLabelPdf (String from, String upto){
            return groupDir(from, upto).toFileToken(pharmaLabelPdfFileName(from, upto));
        }

        private String pharmaLetterPdfFileName (String from, String upto){
            return String.format("shohousen-pharma-letter-%s-%s.pdf", from, upto);
        }

        private GlobalService.AppFileToken pharmaLetterPdf (String from, String upto){
            return groupDir(from, upto).toFileToken(pharmaLetterPdfFileName(from, upto));
        }

        private String pharmaLetterTextFileName (String from, String upto){
            return String.format("shohousen-pharma-letter-%s-%s.txt", from, upto);
        }

        private ShohousenData shohousenTextToData (dev.myclinic.vertx.prescfax.Text text){
            ShohousenData data = new ShohousenData();
            data.clinicAddress = text.clinicAddress;
            data.clinicName = text.clinicName;
            data.clinicPhone = text.clinicPhone;
            data.kikancode = text.kikancode;
            data.doctorName = text.doctorName;
            data.hokenshaBangou = text.hokenshaBangou;
            data.hihokensha = text.hihokensha;
            data.futansha = text.futansha;
            data.jukyuusha = text.jukyuusha;
            data.futansha2 = text.futansha2;
            data.jukyuusha2 = text.jukyuusha2;
            data.shimei = text.shimei;
            data.birthday = text.birthday != null ? LocalDate.parse(text.birthday) : null;
            data.sex = text.sex;
            data.honnin = text.honnin;
            data.futanWari = text.futanWari;
            data.koufuDate = text.koufuDate != null ? LocalDate.parse(text.koufuDate) : null;
//        if(  text.validUptoDate != null && !text.validUptoDate.equals("") ){
//            data.validUptoDate = LocalDate.parse(text.validUptoDate);
//        }
            data.setDrugs(text.content);
            data.pharmacyName = text.pharmacyName;
            return data;
        }

    }
