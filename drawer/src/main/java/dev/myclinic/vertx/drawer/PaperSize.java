package dev.myclinic.vertx.drawer;

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

    public PaperSize(double width, double height){
        this.width = width;
        this.height = height;
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
