package dev.myclinic.vertx.drawer;

/**
 * Created by hangil on 2017/05/14.
 */
public class OpLineTo extends Op {

    private final double x;
    private final double y;

    public OpLineTo(double x, double y){
        super(OpCode.LineTo);
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

}
