package dev.myclinic.vertx.consts;

public class Units {

    private static final double MilliMeterPerInch = 25.4;
    private static final double PointPerInch = 72;

    public static double mmToInch(double mm){
        return mm / MilliMeterPerInch;
    }

    public static double inchToMm(double inch){
        return inch * MilliMeterPerInch;
    }

    public static double mmToPoint(double mm){
        return mmToInch(mm) * PointPerInch;
    }

}
