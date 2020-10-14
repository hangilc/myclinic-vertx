package dev.myclinic.vertx.master;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Misc {

    public static String mostRecentlyDownloaded() throws IOException {
        DirectoryStream<Path> ds = Files.newDirectoryStream(Path.of("./master-files"), "masters-*");
        List<String> paths = new ArrayList<>();
        for(Path path: ds){
            paths.add(path.toString());
        }
        if( paths.size() == 0 ){
            return null;
        } else {
            paths.sort(Comparator.reverseOrder());
            return paths.get(0);
        }
    }

}
