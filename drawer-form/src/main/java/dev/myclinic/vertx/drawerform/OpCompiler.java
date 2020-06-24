package dev.myclinic.vertx.drawerform;

import dev.myclinic.vertx.drawer.*;

import java.util.ArrayList;
import java.util.List;

class OpCompiler {

    private List<Op> ops = new ArrayList<>();

    public List<Op> getOps(){
        List<Op> result = ops;
        this.ops = new ArrayList<>();
        return result;
    }

    public void moveTo(double x, double y){
        ops.add(new OpMoveTo(x, y));
    }

    public void lineTo(double x, double y){
        ops.add(new OpLineTo(x, y));
    }

    public void createFont(String name, String fontName, double size, int weight, boolean italic){
        ops.add(new OpCreateFont(name, fontName, size, weight, italic));
    }

    public void setFont(String name){
        ops.add(new OpSetFont(name));
    }

    public void drawChars(String text, List<Double> xs, List<Double> ys){
        ops.add(new OpDrawChars(text, xs, ys));
    }

    public void setTextColor(int r, int g, int b){
        ops.add(new OpSetTextColor(r, g, b));

    }

    public void createPen(String name, int red, int green, int blue, double width, int penStyle){
        ops.add(new OpCreatePen(name, red, green, blue, width, penStyle));
    }

    public void setPen(String name){
        ops.add(new OpSetPen(name));
    }

    public void circle(double cx, double cy, double r){
        ops.add(new OpCircle(cx, cy, r));
    }

}
