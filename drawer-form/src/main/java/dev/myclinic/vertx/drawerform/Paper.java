package dev.myclinic.vertx.drawerform;

import java.util.HashMap;
import java.util.Map;

public class Paper {

    private final double width;
    private final double height;

    public static final Paper A4 = new Paper(210, 297);
    public static final Paper A4_Landscape = A4.transpose();
    public static final Paper A5 = new Paper(148, 210);
    public static final Paper A5_Landscape = A5.transpose();
    public static final Paper A6 = new Paper(105, 148);
    public static final Paper A6_Landscape = A6.transpose();
    public static final Paper B4 = new Paper(257, 364);
    public static final Paper B4_Landscape = B4.transpose();
    public static final Paper B5 = new Paper(182, 257);
    public static final Paper B5_Landscape = B5.transpose();
    public static final Paper B6 = new Paper(128, 182);
    public static final Paper B6_Landscape = B6.transpose();

    private static final Map<String, Paper> paperNameMap = new HashMap<>();
    static {
        paperNameMap.put("A4", A4);
        paperNameMap.put("A4_Landscape", A4_Landscape);
        paperNameMap.put("A5", A5);
        paperNameMap.put("A5_Landscape", A5_Landscape);
        paperNameMap.put("A6", A6);
        paperNameMap.put("A6_Landscape", A6_Landscape);
        paperNameMap.put("B4", B4);
        paperNameMap.put("B4_Landscape", B4_Landscape);
        paperNameMap.put("B5", B5);
        paperNameMap.put("B5_Landscape", B5_Landscape);
        paperNameMap.put("B6", B6);
        paperNameMap.put("B6_Landscape", B6_Landscape);
    }

    public static Paper getPaperByName(String name){
        return paperNameMap.getOrDefault(name, null);
    }

    Paper(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public Paper transpose(){
        //noinspection SuspiciousNameCombination
        return new Paper(height, width);
    }
}
