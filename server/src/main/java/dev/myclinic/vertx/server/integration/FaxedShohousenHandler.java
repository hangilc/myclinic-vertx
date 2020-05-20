package dev.myclinic.vertx.server.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
    }

    private void handleGetGroup(RoutingContext ctx) {
        String from = ctx.queryParam("from").get(0).replace("-", "");
        String upto = ctx.queryParam("upto").get(0).replace("-", "");
        Path dir = groupDir(from, upto);
        if( !Files.isDirectory(dir) ){
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
            if( paths.size() == 0 ){
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

    private Map<String, Object> getGroupStatus(Path groupDir, String from, String upto){
        String name = groupDirName(from, upto);
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("from", toSqlDateFormat(from));
        map.put("upto", toSqlDateFormat(upto));
        boolean done = reportFileExists(map, groupDir,
                shohousenFileName(from, upto), "shohousen_text_done", true);
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
                pharmaLetterFileName(from, upto), "pharma_letter_text_done", done);
        map.put("completed", done);
        return map;
    }

    private static final Pattern patDateSpec = Pattern.compile("(\\d{4})(\\d{2})(\\d{2})");
    private static final Pattern patSqlDate = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
    private static final Pattern patGroupDir = Pattern.compile("\\d{8}-\\d{8}");

    private String toSqlDateFormat(String fromOrUpto){
        Matcher matcher = patDateSpec.matcher(fromOrUpto);
        if( matcher.matches() ){
            return String.format("%s-%s-%s", matcher.group(1), matcher.group(2), matcher.group(3));
        }
        matcher = patSqlDate.matcher(fromOrUpto);
        if( matcher.matches() ){
            return fromOrUpto;
        } else {
            throw new RuntimeException("Invalid Date: " + fromOrUpto);
        }
    }

    private boolean reportFileExists(Map<String, Object> map, Path dir, String file, String key, boolean done){
        boolean exists = fileExists(dir, file);
        map.put(key, exists);
        return exists && done;
    }

    private boolean fileExists(Path dir, String file){
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
                if( isSuccess ) {
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
            if( matcher.matches() ){
                result.add(path);
            }
        });
        return result;
    }

    private Path getMyclinicApiProjectDir(){
        String apiDir = System.getenv("MYCLINIC_API_PROJECT_DIR");
        if (apiDir == null) {
            throw new RuntimeException("env var not defined: " + "MYCLINIC_API_PROJECT_DIR");
        }
        return Path.of(apiDir);
    }

    private Path getManagementRootDir() {
        String dirStr = System.getenv("MYCLINIC_FAXED_SHOHOUSEN_DATA_DIR");
        if (dirStr == null) {
            throw new RuntimeException("env var is not defined: " + "MYCLINIC_FAXED_SHOHOUSEN_DATA_DIR");
        }
        Path dir = Path.of(dirStr);
        if( !Files.isDirectory(dir) ){
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

    private String shohousenFileName(String from, String upto) {
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

    private String pharmaLetterFileName(String from, String upto) {
        return String.format("shohousen-pharma-letter-%s-%s.txt", from, upto);
    }

}
