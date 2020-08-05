package dev.myclinic.vertx.pdf;

public class Unit {

    public static double mmToPoint(double mmValue){
        return mmValue / 25.4 * 72.0;
    }

    public static double pointToMm(double pointValue){
        return pointValue / 72.0 * 25.4;
    }

}
