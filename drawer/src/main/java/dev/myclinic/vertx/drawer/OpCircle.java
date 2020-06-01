package dev.myclinic.vertx.drawer;

public class OpCircle extends Op {

    private final double cx;
    private final double cy;
    private final double r;

    public OpCircle(double cx, double cy, double r) {
        super(OpCode.Circle);
        this.cx = cx;
        this.cy = cy;
        this.r = r;
    }

    public double getCx() {
        return cx;
    }

    public double getCy() {
        return cy;
    }

    public double getR() {
        return r;
    }
}
