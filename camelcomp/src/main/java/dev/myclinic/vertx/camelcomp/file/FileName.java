package dev.myclinic.vertx.camelcomp.file;

import java.time.LocalDateTime;

public class FileName {

    public static String createTimestampedFileName(String prefix, String suffix){
        LocalDateTime dt = LocalDateTime.now();
        String stamp = String.format("%04d%02d%02d%02d%02d%02d%03d",
                dt.getYear(), dt.getMonthValue(), dt.getDayOfMonth(),
                dt.getHour(), dt.getMinute(), dt.getSecond(), dt.getNano()/1000);
        return prefix + stamp + suffix;
    }

}
