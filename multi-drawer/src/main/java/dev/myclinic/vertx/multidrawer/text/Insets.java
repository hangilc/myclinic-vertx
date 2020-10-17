package dev.myclinic.vertx.multidrawer.text;

public class Insets {

    public double insetLeft = 0;
    public double insetTop = 0;
    public double insetRight = 0;
    public double insetBottom = 0;

    Insets() {

    }

    Insets(double inset){
        this.insetLeft = inset;
        this.insetTop = inset;
        this.insetRight = inset;
        this.insetBottom = inset;
    }

    Insets(double left, double top, double right, double bottom){
        this.insetLeft = left;
        this.insetTop = top;
        this.insetRight = right;
        this.insetBottom = bottom;
    }

}
