package dev.myclinic.vertx.master;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Main {

    public static void main(String[] args) throws Exception {
        if( args.length > 0 ){
            switch(args[0]){
                case "pick-shinryou": {
                    pickShinryou(args);
                    return;
                }
            }
        }
        CmdArgs cmdArgs = CmdArgs.parse(args);
        switch(cmdArgs.command){
            case "download": {
                doDownload();
                break;
            }
            case "update-shinryou": {
                doUpdateShinryou(cmdArgs.updateShinryouArgs);
                break;
            }
            default: {
                System.err.printf("Unknown command: %s\n", cmdArgs.command);
                System.exit(1);
            }
        }
    }

    private static void pickShinryou(String[] args) throws Exception {
        var params = new Object(){
            public int shinryoucode;
        };
        if( args.length == 2 ){
            params.shinryoucode = Integer.parseInt(args[1]);
        } else {
            System.err.println("Invalid arg to pick-shinryou");
            System.err.println("usage: pick-shinryou SHINRYOUCODE");
            System.exit(1);
        }
        String savedDir = Misc.mostRecentlyDownloaded();
        Path zipFile = Path.of(savedDir, "s.zip");
        ShinryouUpdater updater = new ShinryouUpdater(zipFile);
        updater.iter(csv -> {
            if( csv.shinryoucode == params.shinryoucode ){
                System.out.println(csv);
                System.out.printf("insert into shinryoukoui_master_arch set shinryoucode=%d, name='%s'," +
                                " tensuu='%s', tensuu_shikibetsu='%s', shuukeisaki='%s', houkatsukensa='%s'," +
                                " oushinkubun='%s', kensagroup='%s', valid_from='%s', valid_upto='%s';\n",
                        csv.shinryoucode,
                        csv.name,
                        csv.tensuu,
                        csv.tensuuShikibetsu,
                        csv.shuukeisaki,
                        csv.houkatsukensa,
                        csv.oushinKubun,
                        csv.kensaGroup,
                        LocalDate.now().toString(),
                        "0000-00-00"
                );
            }
        });
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

    private static void doUpdateShinryou(CmdArgs.UpdateShinryouArgs args) throws Exception {
        String savedDir = Misc.mostRecentlyDownloaded();
        Path zipFile = Path.of(savedDir, "s.zip");
        System.err.printf("reading master file: %s\n", zipFile.toString());
        ShinryouUpdater updater = new ShinryouUpdater(zipFile);
        if( args.exec ){
            updater.update(args.validFrom);
        } else if( args.henkoukubun ){
            updater.henkouKubun();
        } else {
            updater.dryRun();
        }
    }



}
