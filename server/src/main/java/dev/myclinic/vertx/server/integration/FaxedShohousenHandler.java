package dev.myclinic.vertx.server.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        router.route("/get-last-group").handler(this::handleGetLastGroup);
        router.route("/get-group").handler(this::handleGetGroup);
        router.route("/create-data").handler(this::handleCreateData);
        router.route("/create-shohousen-text").handler(this::handleCreateShohousenText);
        router.route("/create-shohousen-pdf").handler(this::handleCreateShohousenPdf);
        router.route("/create-pharma-letter-text").handler(this::handleCreatePharmaLetterText);
        router.route("/create-pharma-letter-pdf").handler(this::handleCreatePharmaLetterPdf);
        router.route("/create-pharma-label-pdf").handler(this::handleCreatePharmaLabelPdf);
        router.route("/create-clinic-label-pdf").handler(this::handleCreateClinicLabelPdf);
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
                result = execinMyclinicSpring(commands);
                if (result.isError()) {
                    promise.complete(result.errorAsJson(mapper));
                    return;
                }
                Path groupDir = groupDir(from, upto);
                String pdfFileName = clinicLabelPdfFileName(from, upto);
                Path pdfFile = groupDir.resolve(pdfFileName);
                commands.clear();
                commands.addAll(List.of(
                        "java", "-jar", jarFile("drawer-printer"),
                        "-i", tempFile2.toFile().getAbsolutePath(),
                        "--pdf", pdfFile.toFile().getAbsolutePath()
                ));
                result = execinMyclinicSpring(commands);
                if (result.isError()) {
                    promise.complete(result.errorAsJson(mapper));
                } else {
                    Map<String, Object> map = Map.of(
                            "success", true,
                            "clinicPdfFile", pdfFileName,
                            "clinicPdfFileSize", pdfFile.toFile().length()
                    );
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
        return System.getenv("MYCLINIC_CONFIG_DIR");
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

    private ExecResult execinMyclinicSpring(List<String> commands) throws IOException {
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
                Path apiDir = getMyclinicApiProjectDir();
                Path groupDir = groupDir(from, upto);
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
                }
                result.put("pharmaLabelPdfFie", pharmaLabelPdfFileName);
                result.put("pharmaLabelPdfFileSize", pharmaLabelPdfFile.toFile().length());
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
        vertx.<String>executeBlocking(promise -> {
            Path tempFile = null;
            try {
                tempFile = Files.createTempFile("myclinic-", "-pharma-letter-drawer.txt");
                Path sprintDir = getMyclinicSpringProjectDir();
                Path groupDir = groupDir(from, upto);
                String textFileName = pharmaLetterTextFileName(from, upto);
                Path textFile = groupDir.resolve(textFileName);
                ProcessBuilder pb1 = new ProcessBuilder(
                        "java", "-jar",
                        "multi-drawer-cli\\target\\multi-drawer-cli-1.0.0-SNAPSHOT.jar",
                        "text",
                        "-i", textFile.toFile().getAbsolutePath(),
                        "-o", tempFile.toFile().getAbsolutePath())
                        .directory(sprintDir.toFile());
                Map<String, String> env1 = pb1.environment();
                env1.put("MYCLINIC_CONFIG", System.getenv("MYCLINIC_CONFIG_DIR"));
                Map<String, Object> result1 = execOld(pb1);
                if (!result1.get("success").equals(true)) {
                    promise.complete(mapper.writeValueAsString(result1));
                    return;
                }
                String pdfFileName = pharmaLetterPdfFileName(from, upto);
                Path pdfFile = groupDir.resolve(pdfFileName);
                ProcessBuilder pb2 = new ProcessBuilder(
                        "java", "-jar",
                        "drawer-printer\\target\\drawer-printer-1.0.0-SNAPSHOT.jar",
                        "-i", tempFile.toFile().getAbsolutePath(),
                        "--pdf", pdfFile.toFile().getAbsolutePath())
                        .directory(sprintDir.toFile());
                Map<String, String> env2 = pb1.environment();
                env2.put("MYCLINIC_CONFIG", System.getenv("MYCLINIC_CONFIG_DIR"));
                Map<String, Object> result2 = execOld(pb2);
                if (result2.get("success").equals(true)) {
                    result2.put("pdfFile", pdfFileName);
                    result2.put("pdfFileSize", pdfFile.toFile().length());
                }
                promise.complete(mapper.writeValueAsString(result2));
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (tempFile != null) {
                    //noinspection ResultOfMethodCallIgnored
                    tempFile.toFile().delete();
                }
            }
        }, ar -> {
            if (ar.succeeded()) {
                ctx.response().putHeader("content-type", "application/json; charset=UTF-8")
                        .end(ar.result());
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    private void handleCreatePharmaLetterText(RoutingContext ctx) {
        String fromDate = ctx.queryParam("from").get(0);
        String uptoDate = ctx.queryParam("upto").get(0);
        String from = fromDate.replace("-", "");
        String upto = uptoDate.replace("-", "");
        vertx.<String>executeBlocking(promise -> {
            try {
                Path apiDir = getMyclinicApiProjectDir();
                Path groupDir = groupDir(from, upto);
                String dataFileName = dataFileName(from, upto);
                Path dataFile = groupDir.resolve(dataFileName);
                String textFileName = pharmaLetterTextFileName(from, upto);
                Path textFile = groupDir.resolve(textFileName);
                ProcessBuilder pb = new ProcessBuilder("python",
                        "presc.py", "pharma-letter",
                        "-i", dataFile.toFile().getAbsolutePath(),
                        "-o", textFile.toFile().getAbsolutePath())
                        .directory(apiDir.toFile());
                Map<String, String> env = pb.environment();
                env.put("MYCLINIC_CONFIG", System.getenv("MYCLINIC_CONFIG_DIR"));
                Map<String, Object> result = execOld(pb);
                boolean isSuccess = result.get("success").equals(true);
                if (isSuccess) {
                    result.put("pharmaLetterTextFileName", textFileName);
                    result.put("pharmaLetterTextFileSize", textFile.toFile().length());
                }
                promise.complete(mapper.writeValueAsString(result));
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

    private Map<String, Object> execOld(ProcessBuilder pb) throws IOException {
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

    private void handleCreateShohousenPdf(RoutingContext ctx) {
        String fromDate = ctx.queryParam("from").get(0);
        String uptoDate = ctx.queryParam("upto").get(0);
        String from = fromDate.replace("-", "");
        String upto = uptoDate.replace("-", "");
        vertx.<String>executeBlocking(promise -> {
            Path tempFile = null;
            try {
                tempFile = Files.createTempFile("myclinic-", "-shohousen.txt");
                Path sprintDir = getMyclinicSpringProjectDir();
                Path groupDir = groupDir(from, upto);
                String shohousenTextFileName = shohousenTextFileName(from, upto);
                Path textFile = groupDir.resolve(shohousenTextFileName);
                ProcessBuilder pb1 = new ProcessBuilder(
                        "java", "-jar",
                        "shohousen-drawer\\target\\shohousen-drawer-1.0.0-SNAPSHOT.jar",
                        "-i", textFile.toFile().getAbsolutePath(),
                        "-o", tempFile.toFile().getAbsolutePath())
                        .directory(sprintDir.toFile());
                Map<String, String> env1 = pb1.environment();
                env1.put("MYCLINIC_CONFIG", System.getenv("MYCLINIC_CONFIG_DIR"));
                Map<String, Object> result1 = execOld(pb1);
                if (!result1.get("success").equals(true)) {
                    promise.complete(mapper.writeValueAsString(result1));
                    return;
                }
                String pdfFileName = shohousenPdfFileName(from, upto);
                Path pdfFile = groupDir.resolve(pdfFileName);
                ProcessBuilder pb2 = new ProcessBuilder(
                        "java", "-jar",
                        "drawer-printer\\target\\drawer-printer-1.0.0-SNAPSHOT.jar",
                        "--pdf-page-size", "A5",
                        "--pdf-shrink", "2",
                        "-i", tempFile.toFile().getAbsolutePath(),
                        "--pdf", pdfFile.toFile().getAbsolutePath())
                        .directory(sprintDir.toFile());
                Map<String, String> env2 = pb1.environment();
                env2.put("MYCLINIC_CONFIG", System.getenv("MYCLINIC_CONFIG_DIR"));
                Map<String, Object> result2 = execOld(pb2);
                if (result2.get("success").equals(true)) {
                    result2.put("pdfFile", pdfFileName);
                    result2.put("pdfFileSize", pdfFile.toFile().length());
                }
                promise.complete(mapper.writeValueAsString(result2));
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (tempFile != null) {
                    //noinspection ResultOfMethodCallIgnored
                    tempFile.toFile().delete();
                }
            }
        }, ar -> {
            if (ar.succeeded()) {
                ctx.response().putHeader("content-type", "application/json; charset=UTF-8")
                        .end(ar.result());
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    private void handleCreateShohousenText(RoutingContext ctx) {
        String fromDate = ctx.queryParam("from").get(0);
        String uptoDate = ctx.queryParam("upto").get(0);
        String from = fromDate.replace("-", "");
        String upto = uptoDate.replace("-", "");
        vertx.<String>executeBlocking(promise -> {
            try {
                Path apiDir = getMyclinicApiProjectDir();
                Path groupDir = groupDir(from, upto);
                //noinspection ResultOfMethodCallIgnored
                groupDir.toFile().mkdir();
                String dataFileName = dataFileName(from, upto);
                Path dataFile = groupDir.resolve(dataFileName);
                String textFileName = shohousenTextFileName(from, upto);
                Path textFile = groupDir.resolve(textFileName);
                ProcessBuilder pb = new ProcessBuilder("python",
                        "presc.py", "print",
                        "-i", dataFile.toFile().getAbsolutePath(),
                        "-o", textFile.toFile().getAbsolutePath())
                        .directory(apiDir.toFile());
                Map<String, String> env = pb.environment();
                env.put("MYCLINIC_CONFIG", System.getenv("MYCLINIC_CONFIG_DIR"));
                Process process = pb.start();
                InputStream is = process.getInputStream();
                InputStream es = process.getErrorStream();
                String stdErr = readInputStream(es);
                boolean isSuccess = process.exitValue() == 0;
                Map<String, Object> map = new HashMap<>();
                map.put("success", isSuccess);
                if (isSuccess) {
                    map.put("shohousenTextFileName", textFileName);
                    map.put("shohousenTextFileSize", textFile.toFile().length());
                } else {
                    map.put("errorMessage", stdErr);
                }
                promise.complete(mapper.writeValueAsString(map));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, ar -> {
            if (ar.succeeded()) {
                ctx.response().putHeader("content-type", "application/json; charset=UTF-8")
                        .end(ar.result());
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    private void handleGetGroup(RoutingContext ctx) {
        String from = ctx.queryParam("from").get(0).replace("-", "");
        String upto = ctx.queryParam("upto").get(0).replace("-", "");
        Path dir = groupDir(from, upto);
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

    private void handleGetLastGroup(RoutingContext ctx) {
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

    private Map<String, Object> getGroupStatus(Path groupDir, String from, String upto) {
        String name = groupDirName(from, upto);
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("from", toSqlDateFormat(from));
        map.put("upto", toSqlDateFormat(upto));
        boolean done = reportFileExists(map, groupDir,
                shohousenTextFileName(from, upto), "shohousen_text_done", true);
        done = reportFileExists(map, groupDir,
                shohousenPdfFileName(from, upto), "shohousen_pdf_done", done);
        done = reportFileExists(map, groupDir,
                clinicLabelPdfFileName(from, upto), "clinic_label_pdf_done", done);
        done = reportFileExists(map, groupDir,
                dataFileName(from, upto), "data_done", done);
        done = reportFileExists(map, groupDir,
                pharmaLabelPdfFileName(from, upto), "pharma_label_pdf_done", done);
        done = reportFileExists(map, groupDir,
                pharmaLetterPdfFileName(from, upto), "pharma_letter_pdf_done", done);
        done = reportFileExists(map, groupDir,
                pharmaLetterTextFileName(from, upto), "pharma_letter_text_done", done);
        map.put("completed", done);
        return map;
    }

    private static final Pattern patDateSpec = Pattern.compile("(\\d{4})(\\d{2})(\\d{2})");
    private static final Pattern patSqlDate = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
    private static final Pattern patGroupDir = Pattern.compile("\\d{8}-\\d{8}");

    private String toSqlDateFormat(String fromOrUpto) {
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

    private boolean reportFileExists(Map<String, Object> map, Path dir, String file, String key, boolean done) {
        boolean exists = fileExists(dir, file);
        map.put(key, exists);
        return exists && done;
    }

    private boolean fileExists(Path dir, String file) {
        return Files.exists(dir.resolve(file));
    }

    private void handleListGroups(RoutingContext ctx) {
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

    private void handleCreateData(RoutingContext ctx) {
        String fromDate = ctx.queryParam("from").get(0);
        String uptoDate = ctx.queryParam("upto").get(0);
        String from = fromDate.replace("-", "");
        String upto = uptoDate.replace("-", "");
        vertx.<String>executeBlocking(promise -> {
            try {
                Path apiDir = getMyclinicApiProjectDir();
                Path groupDir = groupDir(from, upto);
                //noinspection ResultOfMethodCallIgnored
                groupDir.toFile().mkdir();
                String dataFileName = dataFileName(from, upto);
                Path dataFile = groupDir.resolve(dataFileName);
                ProcessBuilder pb = new ProcessBuilder("python",
                        "presc.py", "data", fromDate, uptoDate, "-o", dataFile.toFile().getAbsolutePath())
                        .directory(apiDir.toFile());
                Map<String, String> env = pb.environment();
                env.put("MYCLINIC_CONFIG", System.getenv("MYCLINIC_CONFIG_DIR"));
                Process process = pb.start();
                InputStream is = process.getInputStream();
                InputStream es = process.getErrorStream();
                String stdErr = readInputStream(es);
                boolean isSuccess = process.exitValue() == 0;
                Map<String, Object> map = new HashMap<>();
                map.put("success", isSuccess);
                if (isSuccess) {
                    map.put("dataFileName", dataFileName);
                    map.put("dataFileSize", dataFile.toFile().length());
                } else {
                    map.put("errorMessage", stdErr);
                }
                promise.complete(mapper.writeValueAsString(map));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, ar -> {
            if (ar.succeeded()) {
                ctx.response().putHeader("content-type", "application/json; charset=UTF-8")
                        .end(ar.result());
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    private List<Path> listGroupDirs() throws IOException {
        Path dir = getManagementRootDir();
        List<Path> result = new ArrayList<>();
        Files.newDirectoryStream(dir).forEach(path -> {
            Matcher matcher = patGroupDir.matcher(path.toFile().getName());
            if (matcher.matches()) {
                result.add(path);
            }
        });
        return result;
    }

    private Path getMyclinicApiProjectDir() {
        String dir = System.getenv("MYCLINIC_API_PROJECT_DIR");
        if (dir == null) {
            throw new RuntimeException("env var not defined: " + "MYCLINIC_API_PROJECT_DIR");
        }
        return Path.of(dir);
    }

    private Path getMyclinicSpringProjectDir() {
        String dir = System.getenv("MYCLINIC_SPRING_PROJECT_DIR");
        if (dir == null) {
            throw new RuntimeException("env var not defined: " + "MYCLINIC_SPRING_PROJECT_DIR");
        }
        return Path.of(dir);
    }

    private Path getManagementRootDir() {
        String dirStr = System.getenv("MYCLINIC_FAXED_SHOHOUSEN_DATA_DIR");
        if (dirStr == null) {
            throw new RuntimeException("env var is not defined: " + "MYCLINIC_FAXED_SHOHOUSEN_DATA_DIR");
        }
        Path dir = Path.of(dirStr);
        if (!Files.isDirectory(dir)) {
            throw new RuntimeException("is not directory: " + dir);
        }
        return dir;
    }

    private String readInputStream(InputStream is) {
        InputStreamReader streamReader = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(streamReader);
        return reader.lines().collect(Collectors.joining("\n"));
    }

    private String groupDirName(String from, String upto) {
        return String.format("%s-%s", from, upto);
    }

    private Path groupDir(String from, String upto) {
        return getManagementRootDir().resolve(groupDirName(from, upto));
    }

    private String shohousenTextFileName(String from, String upto) {
        return String.format("shohousen-%s-%s.json", from, upto);
    }

    private String shohousenPdfFileName(String from, String upto) {
        return String.format("shohousen-%s-%s.pdf", from, upto);
    }

    private String clinicLabelPdfFileName(String from, String upto) {
        return String.format("shohousen-clinic-label-%s-%s.pdf", from, upto);
    }

    private String dataFileName(String from, String upto) {
        return String.format("shohousen-data-%s-%s.json", from, upto);
    }

    private String pharmaLabelPdfFileName(String from, String upto) {
        return String.format("shohousen-pharma-label-%s-%s.pdf", from, upto);
    }

    private String pharmaLetterPdfFileName(String from, String upto) {
        return String.format("shohousen-pharma-letter-%s-%s.pdf", from, upto);
    }

    private String pharmaLetterTextFileName(String from, String upto) {
        return String.format("shohousen-pharma-letter-%s-%s.txt", from, upto);
    }

}
