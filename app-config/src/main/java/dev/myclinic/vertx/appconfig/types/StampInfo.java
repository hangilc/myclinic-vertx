package dev.myclinic.vertx.appconfig.types;

public class StampInfo {

    public String imageFile;
    public double scale = 1.0;
    public double xPos = 0.0;  // in mm unit
    public double yPos = 0.0;  // in mm unit
    public boolean isImageCenterRelative = true;  // (xPos, yPos) is the position of center of stamp

}
