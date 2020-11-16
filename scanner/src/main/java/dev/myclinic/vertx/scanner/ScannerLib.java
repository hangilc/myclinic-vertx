package dev.myclinic.vertx.scanner;

import dev.myclinic.vertx.scanner.wia.Wia;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public class ScannerLib {

    //private static Logger logger = LoggerFactory.getLogger(ScannerLib.class);

    private ScannerLib() {

    }

    static String getScannerDevice(Consumer<String> alertFunc){
        String deviceId = getScannerDeviceSetting();
        if( deviceId == null ){
            deviceId = chooseScannerDevice(alertFunc);
        }
        return deviceId;
    }

    static String getScannerDeviceSetting(){
        String deviceId = Globals.defaultDevice;
        if( !"".equals(deviceId) ){
            return deviceId;
        } else {
            return null;
        }
    }

    static public String chooseScannerDevice(Consumer<String> alertFunc){
        List<Wia.Device> devices = Wia.listDevices();
        if (devices.size() == 0) {
            alertFunc.accept("接続された。スキャナーがみつかりません。");
            return null;
        } else if (devices.size() == 1) {
            return devices.get(0).deviceId;
        } else {
            return Wia.pickScannerDevice();
        }
    }

    static boolean convertImage(Path source, String format, Path output) throws IOException {
        BufferedImage src = ImageIO.read(source.toFile());
        return ImageIO.write(src, format, output.toFile());
    }

}
