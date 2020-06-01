package dev.myclinic.vertx.drawer;

/**
 * Created by hangil on 2017/05/14.
 */
public class OpSetTextColor extends Op {

    private final int r;
    private final int g;
    private final int b;

    public OpSetTextColor(int r, int g, int b){
        super(OpCode.SetTextColor);
        this.r = r;
        this.g = g;
        this.b = b;
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
}
