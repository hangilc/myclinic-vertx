package dev.myclinic.vertx.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlobalService {

    private final static GlobalService INSTANCE = new GlobalService();

    public static GlobalService getInstance() {
        return INSTANCE;
    }

    private GlobalService() {

    }

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

    public void ensureAppDirectory(String id) {
        Path dir = getAppDirectory(id);
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
            return dirId + ":" + path.getFileName().toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Path fileIdToPath(String fileId) {
        int index = fileId.lastIndexOf(":");
        Path dir = getAppDirectory(fileId.substring(0, index));
        String file = fileId.substring(index + 1);
        return dir.resolve(file);
    }

    public void moveFile(String srcId, String dstId) {
        Path srcPath = fileIdToPath(srcId);
        Path dstPath = fileIdToPath(dstId);
        try {
            Files.move(srcPath, dstPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
