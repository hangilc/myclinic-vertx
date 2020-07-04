package dev.myclinic.vertx.drawerform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

class AffineCompiler extends OpCompiler {

    private double scaleX = 1.0;
    private double scaleY = 1.0;
    private double offsetX = 0;
    private double offsetY = 0;

    public void setScale(double scaleX, double scaleY){
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public void setOffsetX(double offsetX){
        this.offsetX = offsetX;
    }

    public void setOffsetY(double offsetY){
        this.offsetY = offsetY;
    }

    private double transX(double x){
        return x * scaleX + offsetX;
    }

    private double transY(double y){
        return y * scaleY + offsetY;
    }

    @Override
    public void moveTo(double x, double y){
        x = transX(x);
        y = transY(y);
        super.moveTo(x, y);
    }

    @Override
    public void lineTo(double x, double y){
        x = transX(x);
        y = transY(y);
        super.lineTo(x, y);
    }

    @Override
    public void createFont(String name, String fontName, double size, int weight, boolean italic){
        super.createFont(name, fontName, size * scaleY, weight, italic);
    }

    @Override
    public void drawChars(String text, List<Double> xs, List<Double> ys){
        xs = xs.stream().map(this::transX).collect(toList());
        ys = ys.stream().map(this::transY).collect(toList());
        super.drawChars(text, xs, ys);
    }

    @Override
    public void createPen(String name, int red, int green, int blue, double width, List<Double> penStyle){
        super.createPen(name, red, green, blue, width * scaleY, penStyle);
    }

    @Override
    public void circle(double cx, double cy, double r){
        cx = transX(cx);
        cy = transY(cy);
        r *= scaleY;
        super.circle(cx, cy, r);
    }

}
