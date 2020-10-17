package dev.myclinic.vertx.server.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.drawer.Op;
import dev.myclinic.vertx.drawer.pdf.PdfPrinter;
import dev.myclinic.vertx.dto.ClinicInfoDTO;
import dev.myclinic.vertx.multidrawer.seal8x3.Seal8x3Data;
import dev.myclinic.vertx.multidrawer.seal8x3.Seal8x3Drawer;
import dev.myclinic.vertx.multidrawer.text.TextDrawer;
import dev.myclinic.vertx.prescfax.Data;
import dev.myclinic.vertx.prescfax.ShohousenGroup;
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
        int row = getOptionalIntParam(ctx, "row").orElse(1);
        int col = getOptionalIntParam(ctx, "col").orElse(1);
        int n = getIntParam(ctx, "n");
        GlobalService.getInstance().executorService.execute(() -> {
            try {
                ClinicInfoDTO clinicInfo = GlobalService.getInstance().client.getClinicInfo();
                List<String> label = List.of(
                        clinicInfo.postalCode,
                        clinicInfo.address,
                        clinicInfo.name
                );
                Seal8x3Data data = new Seal8x3Data();
                data.labels = Collections.nCopies(n, label);
                data.startRow = row;
                data.startColumn = col;
                Seal8x3Drawer drawer = new Seal8x3Drawer();
                List<List<Op>> pages = drawer.draw(data);
                Path groupDir = groupDir(from, upto).resolve();
                String pdfFileName = clinicLabelPdfFileName(from, upto);
                Path pdfFile = groupDir.resolve(pdfFileName);
                PdfPrinter pdfPrinter = new PdfPrinter("A4");
                pdfPrinter.print(pages, pdfFile.toString());
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                reportFileStatus(result, pdfFile, "clinicLabelPdfFile");
                ctx.response().end(mapper.writeValueAsString(result));
            } catch (Exception e) {
                ctx.fail(e);
            }
        });
    }

    private String getConfigDir() {
        return GlobalService.getInstance().resolveAppPath(
                GlobalService.getInstance().configDirToken
        ).toString();
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

    public static class PharmaAddrNotFoundException extends Exception {
        public List<String> missing;

        public PharmaAddrNotFoundException(List<String> missing) {
            this.missing = missing;
        }
    }

    private List<List<String>> createPharmaLabelText(GlobalService.AppFileToken dataFile)
            throws PharmaAddrNotFoundException, IOException {
        Map<String, String> pharmaAddr = mapper.readValue(pharmaAddrFile().resolve().toFile(),
                new TypeReference<>() {
                });
        dev.myclinic.vertx.prescfax.Data data = mapper.readValue(dataFile.resolve().toFile(),
                dev.myclinic.vertx.prescfax.Data.class);
        List<String> missing = new ArrayList<>();
        List<List<String>> result = new ArrayList<>();
        for (ShohousenGroup group : data.groups) {
            String addrValue = pharmaAddr.get(group.pharmacy.fax);
            if (addrValue == null) {
                missing.add(String.format("%s:%s", group.pharmacy.name, group.pharmacy.fax));
            } else {
                List<String> lines = Arrays.asList(addrValue.split("\n"));
                result.add(lines);
            }
        }
        if (missing.size() > 0) {
            throw new PharmaAddrNotFoundException(missing);
        }
        return result;
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
        GlobalService.getInstance().executorService.execute(() -> {
            try {
                List<List<String>> labels = createPharmaLabelText(dataFile(from, upto));
                Seal8x3Data sealData = new Seal8x3Data();
                sealData.labels = labels;
                sealData.startRow = row;
                sealData.startColumn = col;
                Seal8x3Drawer drawer = new Seal8x3Drawer();
                List<List<Op>> pages = drawer.draw(sealData);
                String pharmaLabelPdfFileName = pharmaLabelPdfFileName(from, upto);
                Path pharmaLabelPdfFile = groupDir(from, upto).toFileToken(pharmaLabelPdfFileName)
                        .resolve();
                PdfPrinter pdfPrinter = new PdfPrinter("A4");
                pdfPrinter.print(pages, pharmaLabelPdfFile.toString());
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                reportFileStatus(result, pharmaLabelPdfFile, "pharmaLabelPdfFile");
                ctx.response().end(mapper.writeValueAsString(result));
            } catch (PharmaAddrNotFoundException e) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("error", "missing-pharma-addr");
                result.put("missing", e.missing);
                try {
                    ctx.response().end(mapper.writeValueAsString(result));
                } catch (JsonProcessingException jsonProcessingException) {
                    ctx.fail(e);
                }
            } catch (Exception e) {
                ctx.fail(e);
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
            }
            ;
        });
    }

    private void handleCreatePharmaLetterText(RoutingContext ctx) {
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

    private void handleCreateShohousenPdf(RoutingContext ctx) {
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
    }

    private void handleCreateShohousenText(RoutingContext ctx) {
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

    private void handleGetGroup(RoutingContext ctx) {
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

    private static final DateTimeFormatter sqlDateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");

    private String getFileTimeRep(FileTime ft) {
        return ft.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(sqlDateTimeFormatter);
    }

    private boolean reportFileStatus(Map<String, Object> map, Path path, String prefix) {
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

    private boolean reportFileStatus(Map<String, Object> map, Path path, String prefix, boolean done) {
        boolean exists = reportFileStatus(map, path, prefix);
        return exists && done;
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

    private List<Path> listGroupDirs() throws IOException {
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

    private GlobalService.AppDirToken getManagementRootDir() {
        return new GlobalService.AppDirToken(
                GlobalService.getInstance().shohousenFaxManagementDirToken,
                Collections.emptyList()
        );
    }

    private String groupDirName(String from, String upto) {
        return String.format("%s-%s", from, upto);
    }

    private GlobalService.AppDirToken groupDir(String from, String upto) {
        return getManagementRootDir().addSubDir(groupDirName(from, upto));
    }

    private String shohousenTextFileName(String from, String upto) {
        return String.format("shohousen-%s-%s.json", from, upto);
    }

    private String shohousenPdfFileName(String from, String upto) {
        return String.format("shohousen-%s-%s.pdf", from, upto);
    }

    private GlobalService.AppFileToken shohousenPdfFile(String from, String upto) {
        return groupDir(from, upto).toFileToken(shohousenPdfFileName(from, upto));
    }

    private String clinicLabelPdfFileName(String from, String upto) {
        return String.format("shohousen-clinic-label-%s-%s.pdf", from, upto);
    }

    private GlobalService.AppFileToken clinicLabelPdf(String from, String upto) {
        return groupDir(from, upto).toFileToken(clinicLabelPdfFileName(from, upto));
    }

    private String dataFileName(String from, String upto) {
        return String.format("shohousen-data-%s-%s.json", from, upto);
    }

    private GlobalService.AppFileToken dataFile(String from, String upto) {
        GlobalService.AppDirToken dir = groupDir(from, upto);
        return dir.toFileToken(dataFileName(from, upto));
    }

    private String pharmaLabelPdfFileName(String from, String upto) {
        return String.format("shohousen-pharma-label-%s-%s.pdf", from, upto);
    }

    private GlobalService.AppFileToken pharmaLabelPdf(String from, String upto) {
        return groupDir(from, upto).toFileToken(pharmaLabelPdfFileName(from, upto));
    }

    private String pharmaLetterPdfFileName(String from, String upto) {
        return String.format("shohousen-pharma-letter-%s-%s.pdf", from, upto);
    }

    private GlobalService.AppFileToken pharmaLetterPdf(String from, String upto) {
        return groupDir(from, upto).toFileToken(pharmaLetterPdfFileName(from, upto));
    }

    private String pharmaLetterTextFileName(String from, String upto) {
        return String.format("shohousen-pharma-letter-%s-%s.txt", from, upto);
    }

    private GlobalService.AppFileToken pharmaAddrFile() {
        return new GlobalService.AppDirToken(GlobalService.getInstance().configDirToken, List.of())
                .toFileToken("pharma-addr.json");
    }

    private ShohousenData shohousenTextToData(dev.myclinic.vertx.prescfax.Text text) {
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
        data.setDrugs(text.content);
        data.pharmacyName = text.pharmacyName;
        return data;
    }

}
