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

}
