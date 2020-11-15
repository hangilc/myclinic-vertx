package dev.myclinic.vertx.scanner;

import java.nio.file.Path;

public class Globals {

    //private static Logger logger = LoggerFactory.getLogger(Globals.class);

    private Globals() {

    }

    public static String defaultDevice;
    public static Path savingDir;
    public static int dpi;
    public static Path regularDocSavingDirHint;

    static {
        ScannerSetting setting = ScannerSetting.INSTANCE;
        defaultDevice = setting.getDefaultDevice();
        savingDir = setting.getSavingDir();
        dpi = setting.getDpi();
        regularDocSavingDirHint = setting.getRegularDocSavingDirHint();
    }

}
