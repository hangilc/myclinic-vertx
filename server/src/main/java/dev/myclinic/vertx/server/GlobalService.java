package dev.myclinic.vertx.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class GlobalService {

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

    /*
    private Path getPortalTmpDir() {
        return Path.of("work", "portal-tmp");
    }

    private Path getPaperScanRootDir() {
        String dir = System.getenv("MYCLINIC_PAPER_SCAN_DIR");
        if (dir == null) {
            throw new RuntimeException("Missing env var: MYCLINIC_PAPER_SCAN_DIR");
        }
        return Path.of(dir);
    }

    private static final Pattern paperScanDirPattern =
            Pattern.compile("/paper-scan/(\\d+)");

    public Path getAppDirectory(String id) {
        if (id.equals("/portal-tmp")) {
            return getPortalTmpDir();
        }
        if (id.equals("/paper-scan")) {
            return getPaperScanRootDir();
        }
        Matcher m = paperScanDirPattern.matcher(id);
        if (m.matches()) {
            return getPaperScanRootDir().resolve(m.group(1));
        }
        throw new RuntimeException("Invalid app directory id: " + id);
    }

    public void ensureAppDirectory(String id, String... subs) {
        Path dir = getAppDirectory(id);
        if( subs.length > 0 ){
            dir = Path.of(dir.toString(), subs);
        }
        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String createTempAppFilePath(String dirId, String prefix, String suffix) {
        Path dir = getAppDirectory(dirId);
        try {
            Path path = Files.createTempFile(dir, prefix, suffix);
            return dirId + "/" + path.getFileName().toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Path fileIdToPath(String fileId) {
        if( !fileId.startsWith("/") ){
            throw new RuntimeException("fileId should start with '/'");
        }
        int index = fileId.indexOf("/", 1);
        if( index <= 0 ){
            throw new RuntimeException("Cannot find file part in fileId: " + fileId);
        }
        Path dir = getAppDirectory(fileId.substring(0, index));
        String file = fileId.substring(index + 1);
        return dir.resolve(file);
    }

    public void moveAppFile(String srcId, String dstId) {
        Path srcPath = fileIdToPath(srcId);
        Path dstPath = fileIdToPath(dstId);
        try {
            System.out.println("moveAppFile src: " + srcPath.toString());
            System.out.println("moveAppFile dst: " + dstPath.toString());
            Path parent = dstPath.getParent();
            if( !Files.exists(parent) ){
                Files.createDirectories(parent);
            }
            Files.move(srcPath, dstPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAppFile(String file){
        Path path = fileIdToPath(file);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

     */

}
