package dev.myclinic.vertx.drawer;

/**
 * Created by hangil on 2017/05/14.
 */
public class OpCreatePen extends Op {

    private final String name;
    private final int r;
    private final int g;
    private final int b;
    private final double width;
    private final int penStyle;

    public static final int PS_SOLID       = 0;
    public static final int PS_DASH        = 1;
    public static final int PS_DOT         = 2;
    public static final int PS_DASHDOT     = 3;
    public static final int PS_DASHDOTDOT  = 4;
    public static final int PS_NULL        = 5;
    public static final int PS_INSIDEFRAME = 6;

    public OpCreatePen(String name, int r, int g, int b, double width, int penStyle){
        super(OpCode.CreatePen);
        this.name = name;
        this.r = r;
        this.g = g;
        this.b = b;
        this.width = width;
        this.penStyle = penStyle;
   }

    public OpCreatePen(String name, int r, int g, int b, double width){
        this(name, r, g, b, width, PS_SOLID);
    }

    public String getName() {
        return name;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    public double getWidth() {
        return width;
    }

    public int getPenStyle() {
        return penStyle;
    }

}
