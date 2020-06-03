package dev.myclinic.vertx.drawer;

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

    public static final DrawerColor RED = new DrawerColor(255, 0, 0);
    public static final DrawerColor GREEN = new DrawerColor(0, 255, 0);
    public static final DrawerColor BLUE = new DrawerColor(0, 0, 255);
    public static final DrawerColor BLACK = new DrawerColor(0, 0, 0);
    public static final DrawerColor WHITE = new DrawerColor(255, 255, 255);

    public static Map<String, DrawerColor> colorMap = new HashMap<>();
    static {
        colorMap.put("red", RED);
        colorMap.put("green", GREEN);
        colorMap.put("blue", BLUE);
        colorMap.put("black", BLACK);
        colorMap.put("white", WHITE);
    }

    public static DrawerColor resolve(String color){
        if( colorMap.containsKey(color) ){
            return colorMap.get(color);
        }
        String[] parts = color.split(":");
        if( parts.length == 3 ){
            int r = Integer.parseInt(parts[0]);
            int g = Integer.parseInt(parts[1]);
            int b = Integer.parseInt(parts[2]);
            return new DrawerColor(r, g, b);
        } else if( parts.length == 1 ){
            int c = Integer.parseInt(parts[0]);
            return new DrawerColor(c, c, c);
        } else {
            throw new RuntimeException("Invalid color: " + color);
        }
    }
}
