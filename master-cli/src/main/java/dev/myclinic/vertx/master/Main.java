package dev.myclinic.vertx.master;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) throws Exception {
        CmdArgs cmdArgs = CmdArgs.parse(args);
        switch(cmdArgs.command){
            case "download": {
                doDownload();
                break;
            }
            default: {
                System.err.printf("Unknown command: %s\n", cmdArgs.command);
                System.exit(1);
            }
        }
    }

    private static void doDownload() throws Exception {
        LocalDate today = LocalDate.now();
        String saveDir = String.format("./master-files/masters-%4d-%02d-%02d",
                today.getYear(), today.getMonthValue(), today.getDayOfMonth());
        if( Files.exists(Path.of(saveDir)) ){
            throw new RuntimeException("Save dir already exists. : " + saveDir);
        }
        Files.createDirectories(Path.of(saveDir));
        Downloader.downloadShinryou(Path.of(saveDir, "s.zip"));
        Downloader.downloadIyakuhin(Path.of(saveDir, "y.zip"));
        Downloader.downloadKizai((Path.of(saveDir, "t.zip")));
        Downloader.downloadShoubyoumei(Path.of(saveDir, "b.zip"));
        Downloader.downloadShuushokugo(Path.of(saveDir, "z.zip"));
        System.out.printf("Master files have been saved to %s\n", saveDir);
    }

}
