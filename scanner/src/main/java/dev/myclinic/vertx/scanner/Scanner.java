package dev.myclinic.vertx.scanner;

import dev.myclinic.vertx.scanner.wia.Wia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Scanner {

    private static final Logger logger = LoggerFactory.getLogger(Scanner.class);

//    public static void init() {
//        Wia.CoInitialize();
//    }
//

    public static void coInitialize(){
        Wia.CoInitialize();
    }

    public static void coUninitialize(){
        Wia.CoUninitialize();
    }

    public static void onClosing() {
        try {
            if( Globals.regularDocSavingDirHint != null ) {
                ScannerSetting.INSTANCE.setRegularDocSavingDirHint(Globals.regularDocSavingDirHint);
            }
        } catch(Exception ex){
            logger.error("Error on onClosing", ex);
        }
    }

}
