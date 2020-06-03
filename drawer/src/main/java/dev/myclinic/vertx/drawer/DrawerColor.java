package dev.myclinic.vertx.drawer;

import com.itextpdf.kernel.colors.Color;

import java.util.HashMap;
import java.util.Map;

public class DrawerColor {
    public int r;
    public int g;
    public int b;

    public DrawerColor(){

    }

    public DrawerColor(int r, int g, int b){
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public static DrawerColor red = new DrawerColor(255, 0, 0);
    public static DrawerColor green = new DrawerColor(0, 255, 0);
    public static DrawerColor blue = new DrawerColor(0, 0, 255);

    public static Map<String, DrawerColor> colorMap = new HashMap<>();
    static {
        colorMap.put("red", red);
        colorMap.put("green", green);
        colorMap.put("blue", blue);
    }
}
