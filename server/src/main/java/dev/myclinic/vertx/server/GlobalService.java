package dev.myclinic.vertx.server;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class GlobalService {

    private final static GlobalService INSTANCE = new GlobalService();

    public static GlobalService getInstance() {
        return INSTANCE;
    }

    public final String portalTmpDirToken = "[portal-tmp]";
    public final String paperScanDirToken = "[paper-scan]";
    public final String shohousenFaxDirToken = "[shohousen-fax]";
    public final String shohousenFaxManagementDirToken = "[shohousen-fax-management]";
    public final String myclinicApiProjectDirToken = "[myclinic-api-proj]";
    public final String myclinicSpringProjectDirToken = "[myclinic-spring-proj]";
    public final String configDirToken = "[config]";

    private final Map<String, String> appDirTokenMap = new HashMap<>();

    private GlobalService() {
        addDirTokenFromEnv(portalTmpDirToken, "MYCLINIC_PORTAL_TMP_DIR");
        addDirTokenFromEnv(paperScanDirToken, "MYCLINIC_PAPER_SCAN_DIR");
        addDirTokenFromEnv(shohousenFaxDirToken, "MYCLINIC_SHOHOUSEN_DIR");
        addDirTokenFromEnv(shohousenFaxManagementDirToken, "MYCLINIC_FAXED_SHOHOUSEN_DATA_DIR");
        addDirTokenFromEnv(myclinicApiProjectDirToken, "MYCLINIC_API_PROJECT_DIR");
        addDirTokenFromEnv(myclinicSpringProjectDirToken, "MYCLINIC_SPRING_PROJECT_DIR");
        addDirTokenFromEnv(configDirToken, "MYCLINIC_CONFIG_DIR");
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

    public String createAppPathToken(String dirToken, String... subpaths) {
        if (isValidDirToken(dirToken)) {
            return dirToken + "/" + String.join("/", subpaths);
        } else {
            throw new RuntimeException("Invalid appDirToken");
        }
    }

    private static class AppPath {
        String dirToken;
        String subpath;

        public AppPath(String dirToken, String subpath) {
            this.dirToken = dirToken;
            this.subpath = subpath;
        }

        static AppPath parse(String tokenPath) {
            if (tokenPath == null || !tokenPath.startsWith("[")) {
                System.err.printf("Invalid appPath: %s\n", tokenPath);
                throw new RuntimeException("Invalid token path");
            }
            int i = tokenPath.indexOf(']');
            if (i < 0) {
                throw new RuntimeException("Invalid token path");
            }
            String dirToken = tokenPath.substring(0, i + 1);
            String subpath = tokenPath.substring(i + 1);
            subpath = subpath.replaceAll("^/+", "");
            return new AppPath(dirToken, subpath);
        }
    }


    public Path resolveAppPath(String tokenPath) {
        AppPath appPath = AppPath.parse(tokenPath);
        Path result = Path.of(resolveDirToken(appPath.dirToken), appPath.subpath);
        String prefix = appDirTokenMap.get(appPath.dirToken);
        try {
            if (result.toString().equals(prefix) ||
                    result.toFile().getCanonicalPath().startsWith(prefix + File.separator)) {
                return result;
            } else {
                throw new RuntimeException("Invalid token path");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String createTempAppFilePath(String dirToken, String prefix, String suffix) {
        Path dir = Path.of(resolveDirToken(dirToken));
        try {
            Path path = Files.createTempFile(dir, prefix, suffix);
            return dirToken + "/" + path.getFileName().toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPaperScanPatientFileToken(int patientId, String fileName){
        if( fileName.contains("/") || fileName.contains("\\") ){
            throw new RuntimeException("fileName cannot contain directory separator.");
        }
        return paperScanDirToken + "/" + String.format("%d", patientId) + "/" + fileName;
    }

    public static class AppFileToken {
        private final String dirToken;
        private final List<String> subDirs;
        private final String fileName;

        public static AppFileToken parse(String path){
            String[] parts = path.split("[/\\\\]");
            if( parts.length >= 2 ){
                List<String> subDirs = new ArrayList<>(Arrays.asList(parts).subList(1, parts.length - 1));
                return new AppFileToken(parts[0], subDirs, parts[parts.length - 1]);
            } else {
                throw new RuntimeException("too few parts for AppFileTokey.parse");
            }
        }

        public AppFileToken(String dirToken, String fileName){
            this(dirToken, Collections.emptyList(), fileName);
        }

        public AppFileToken(String dirToken, List<String> subDirs, String fileName){
            if( !GlobalService.getInstance().isValidDirToken(dirToken) ){
                throw new RuntimeException("Invalid dir token: " + dirToken);
            }
            this.dirToken = dirToken;
            for(String subDir: subDirs){
                if( subDir.contains("/") || subDir.contains("\\") ){
                    throw new RuntimeException("subdir cannot contain directory separator");
                }
                if( subDir.equals("..") ){
                    throw new RuntimeException("subdir cannot be '..'");
                }
            }
            this.subDirs = new ArrayList<>(subDirs);
            if( fileName.contains("/") || fileName.contains("\\") ){
                throw new RuntimeException("fileName cannot contain directory separator");
            }
            this.fileName = fileName;
        }

        @Override
        public String toString(){
            List<String> parts = new ArrayList<>();
            parts.add(dirToken);
            parts.addAll(subDirs);
            parts.add(fileName);
            return String.join("/", parts);
        }

        private Path ensureSubDirs() throws IOException {
            String top = GlobalService.getInstance().resolveDirToken(dirToken);
            Path path = Path.of(top, subDirs.toArray(new String[0]));
            Files.createDirectories(path);
            return path;
        }

        private Path resolve(){
            try {
                return ensureSubDirs().resolve(fileName);
            } catch(Exception e){
                throw new RuntimeException(e);
            }
        }

        public OutputStream openOutputStream(){
            try {
                return new FileOutputStream(resolve().toFile());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        public InputStream openInputStream(){
            try {
                return new FileInputStream(resolve().toFile());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        public static void move(AppFileToken src, AppFileToken dst){
            Path srcPath = src.resolve();
            Path dstPath = dst.resolve();
            try {
                Files.move(srcPath, dstPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public static void copy(AppFileToken src, AppFileToken dst){
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
