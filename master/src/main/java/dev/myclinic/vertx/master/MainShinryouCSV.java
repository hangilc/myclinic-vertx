package dev.myclinic.vertx.master;

import dev.myclinic.vertx.master.csv.ZipFileParser;

public class MainShinryouCSV {

    public static void usage() {
        System.err.println("MainShinryouCSV shinryou-zip-file");
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            usage();
            System.exit(1);
        }
        String zipFile = args[0];
        ZipFileParser.iterShinryouZipFile(zipFile, csv -> {
            System.out.println(csv);
        });
    }
}
