package dev.myclinic.vertx.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.client2.Client;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class GlobalService {

    private final static GlobalService INSTANCE = new GlobalService();

    public static GlobalService getInstance() {
        return INSTANCE;
    }

    public final String portalTmpDirToken = "[portal-tmp]";
    public final String paperScanDirToken = "[paper-scan]";
    public final String shohousenFaxDirToken = "[shohousen-fax]";
    public final String shohousenFaxManagementDirToken = "[shohousen-fax-management]";
    public final String configDirToken = "[config]";
    private boolean simulateSlowDownload = false;
    private boolean simulateSlowUpload = false;
    private boolean simulateUploadFail = false;

    public final Client client;
    public final ExecutorService executorService;

    private final Map<String, String> appDirTokenMap = new HashMap<>();

    private GlobalService() {
        addDirTokenFromEnv(portalTmpDirToken, "MYCLINIC_PORTAL_TMP_DIR");
        addDirTokenFromEnv(paperScanDirToken, "MYCLINIC_PAPER_SCAN_DIR");
        addDirTokenFromEnv(shohousenFaxDirToken, "MYCLINIC_SHOHOUSEN_DIR");
        addDirTokenFromEnv(shohousenFaxManagementDirToken, "MYCLINIC_FAXED_SHOHOUSEN_DATA_DIR");
        addDirTokenFromEnv(configDirToken, "MYCLINIC_CONFIG_DIR");
        this.client = new Client(System.getenv("MYCLINIC_SERVICE"));
        this.executorService = Executors.newFixedThreadPool(6);
    }

    public void setSimulateSlowDownload(boolean value){
        this.simulateSlowDownload = value;
    }

    public boolean getSimulateSlowDownload(){
        return this.simulateSlowDownload;
    }

    public void setSimulateSlowUpload(boolean value){
        this.simulateSlowUpload = value;
    }

    public boolean getSimulateSlowUpload(){
        return this.simulateSlowUpload;
    }

    public void setSimulateUploadFail(boolean value){
        this.simulateUploadFail = value;
    }

    public boolean getSimulateUploadFail(){
        return this.simulateUploadFail;
    }

    private void addDirTokenFromEnv(String token, String envVar) {
        String path = System.getenv(envVar);
        if (path == null) {
            throw new RuntimeException("Cannot find env var " + envVar);
        } else {
            if (!path.endsWith("/")) {
                path = path + "/";
            }
            try {
                path = new File(path).getCanonicalPath();
                appDirTokenMap.put(token, path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String resolveDirToken(String token) {
        String path = appDirTokenMap.get(token);
        if (path == null) {
            throw new RuntimeException("Invalid dir token: " + token);
        }
        return path;
    }

    private boolean isValidDirToken(String token) {
        return appDirTokenMap.containsKey(token);
    }

    public Path resolveAppPath(String tokenPath) {
        return AppFileToken.parse(tokenPath).resolve();
    }

    public String createTempAppFilePath(String dirToken, String prefix, String suffix) {
        AppDirToken appDir = new AppDirToken(dirToken, Collections.emptyList());
        return appDir.createTemp(prefix, suffix).toString();
    }

    public final static class AppDirToken {
        private final String dirToken;
        private final List<String> subDirs = new ArrayList<>();

        public AppDirToken(String dirToken, List<String> subDirs){
            checkDirToken(dirToken);
            this.dirToken = dirToken;
            for(String subDir: subDirs){
                addSubDir(subDir);
            }
        }

        public AppDirToken addSubDir(String dir){
            checkSubDir(dir);
            this.subDirs.add(dir);
            return this;
        }

        @Override
        public String toString(){
            List<String> parts = new ArrayList<>();
            parts.add(dirToken);
            parts.addAll(subDirs);
            parts.add("");
            return String.join("/", parts);
        }

        private Path ensureSubDirs() throws IOException {
            String top = GlobalService.getInstance().resolveDirToken(dirToken);
            Path path = Path.of(top, subDirs.toArray(new String[0]));
            if( !Files.isDirectory(path) ){
                Files.createDirectories(path);
            }
            return path;
        }

        public Path resolve() {
            try {
                return ensureSubDirs();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private static void checkDirToken(String dirToken) {
            if (!GlobalService.getInstance().isValidDirToken(dirToken)) {
                throw new RuntimeException("Invalid dir token: " + dirToken);
            }
        }

        private static void checkSubDir(String subDir) {
            if (subDir == null) {
                throw new RuntimeException("subDir cannot be null");
            }
            if ("..".equals(subDir)) {
                throw new RuntimeException("Invalid subDir");
            }
            if (subDir.contains("/") || subDir.contains("\\")) {
                throw new RuntimeException("subDir cannot contain directory separator");
            }
        }

        public AppFileToken toFileToken(String fileName){
            return new AppFileToken(this, fileName);
        }

        public AppFileToken createTemp(String prefix, String suffix) {
            Path dir = resolve();
            try {
                Path path = Files.createTempFile(dir, prefix, suffix);
                return toFileToken(path.getFileName().toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public final static class AppFileToken {
        private final AppDirToken appDirToken;
        private final String fileName;

        public static AppFileToken parse(String path) {
            String[] subPaths = path.split("[/\\\\]");
            if( subPaths.length >= 2 ){
                String dirToken = subPaths[0];
                List<String> subDirs = Arrays.asList(subPaths).subList(1, subPaths.length - 1);
                AppDirToken appDir = new AppDirToken(dirToken, subDirs);
                String fileName = subPaths[subPaths.length - 1];
                return appDir.toFileToken(fileName);
            } else {
                throw new RuntimeException("Too few arguments to parse");
            }
        }

        @Override
        public String toString() {
            return appDirToken.toString() + "/" + fileName;
        }

        public AppFileToken(AppDirToken appDirToken, String fileName) {
            this.appDirToken = appDirToken;
            checkFileName(fileName);
            this.fileName = fileName;
        }

        private static void checkFileName(String fileName) {
            if (fileName == null) {
                throw new RuntimeException("fileName cannot be null");
            }
            if ("..".equals(fileName) || ".".equals(fileName) || "".equals(fileName)) {
                throw new RuntimeException("Invalid fileName");
            }
            if (fileName.contains("/") || fileName.contains("\\")) {
                throw new RuntimeException("fileName cannot contain directory separator");
            }
        }

        public Path resolve(){
            return appDirToken.resolve().resolve(fileName);
        }

        public OutputStream openOutputStream() {
            try {
                return new FileOutputStream(resolve().toFile());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        public void write(String fileContent){
            try(OutputStream os = openOutputStream()){
                os.write(fileContent.getBytes(StandardCharsets.UTF_8));
            } catch(Exception e){
                throw new RuntimeException(e);
            }
        }

        public InputStream openInputStream() {
            try {
                return new FileInputStream(resolve().toFile());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        public String read(){
            try(InputStream is = openInputStream()){
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            } catch(Exception e){
                throw new RuntimeException(e);
            }
        }

        public static void move(AppFileToken src, AppFileToken dst) {
            Path srcPath = src.resolve();
            Path dstPath = dst.resolve();
            try {
                Files.move(srcPath, dstPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public static void copy(AppFileToken src, AppFileToken dst) {
            Path srcPath = src.resolve();
            Path dstPath = dst.resolve();
            try {
                Files.copy(srcPath, dstPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
