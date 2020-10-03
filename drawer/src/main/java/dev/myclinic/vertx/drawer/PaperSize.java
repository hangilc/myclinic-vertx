package dev.myclinic.vertx.drawer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hangil on 2017/05/14.
 */
public class PaperSize {
    private final double width;
    private final double height;

    public static PaperSize A4 = new PaperSize(210, 297);
    public static PaperSize A4_Landscape = A4.transpose();
    public static PaperSize A5 = new PaperSize(148, 210);
    public static PaperSize A5_Landscape = A5.transpose();
    public static PaperSize A6 = new PaperSize(105, 148);
    public static PaperSize A6_Landscape = A6.transpose();
    public static PaperSize B4 = new PaperSize(257, 364);
    public static PaperSize B4_Landscape = B4.transpose();
    public static PaperSize B5 = new PaperSize(182, 257);
    public static PaperSize B5_Landscape = B5.transpose();
    public static PaperSize B6 = new PaperSize(128, 182);
    public static PaperSize B6_Landscape = B6.transpose();

    public static final Map<String, PaperSize> standard = new HashMap<>();
    static {
        standard.put("A4", A4);
        standard.put("A4_Landscape", A4_Landscape);
        standard.put("A5", A5);
        standard.put("A5_Landscape", A5_Landscape);
        standard.put("A6", A6);
        standard.put("A6_Landscape", A6_Landscape);
        standard.put("B4", B4);
        standard.put("B4_Landscape", B4_Landscape);
        standard.put("B5", B5);
        standard.put("B5_Landscape", B5_Landscape);
        standard.put("B6", B6);
        standard.put("B6_Landscape", B6_Landscape);
    }

    public PaperSize(double width, double height){
        this.width = width;
        this.height = height;
    }

    public static PaperSize resolvePaperSize(String arg) {
        if (PaperSize.standard.containsKey(arg)) {
            return PaperSize.standard.get(arg);
        } else {
            String[] parts = arg.split(",");
            if (parts.length == 2) {
                double width = Double.parseDouble(parts[0].trim());
                double height = Double.parseDouble(parts[1].trim());
                return new PaperSize(width, height);
            } else {
                throw new RuntimeException("Invalid paper size: " + arg);
            }
        }
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public PaperSize transpose(){
        return new PaperSize(height, width);
    }
}
