package dev.myclinic.vertx.drawer;

import java.util.Collections;
import java.util.List;

public class OpCreatePen extends Op {

    private final String name;
    private final int r;
    private final int g;
    private final int b;
    private final double width;
    private final List<Double> penStyle;

    public OpCreatePen(String name, int r, int g, int b, double width, List<Double> penStyle){
        super(OpCode.CreatePen);
        this.name = name;
        this.r = r;
        this.g = g;
        this.b = b;
        this.width = width;
        this.penStyle = penStyle;
   }

    public OpCreatePen(String name, int r, int g, int b, double width){
        this(name, r, g, b, width, Collections.emptyList());
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

    public List<Double> getPenStyle() {
        return penStyle;
    }

}
