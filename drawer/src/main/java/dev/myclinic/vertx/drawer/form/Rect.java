package dev.myclinic.vertx.drawer.form;

import dev.myclinic.vertx.drawer.Box;

public class Rect {

    public double left;
    public double top;
    public double right;
    public double bottom;

    public Rect(){ }

    public Rect(double left, double top, double right, double bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public Box toBox(){
        return new Box(left, top, right, bottom);
    }

    public static Rect fromBox(Box box){
        return new Rect(box.getLeft(), box.getTop(), box.getRight(), box.getBottom());
    }

}
