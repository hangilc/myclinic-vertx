package dev.myclinic.vertx.drawerform;

import dev.myclinic.vertx.drawer.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Compiler {

    private List<List<Op>> pages = new ArrayList<>();
    private List<Op> ops = new ArrayList<>();
    private double scale = 1.0;
    private double offsetX = 0;
    private double offsetY = 0;

    public Compiler() {

    }

    public void setScale(double scale){
        this.scale = scale;
    }

    public void setOffsetX(double offsetX){
        this.offsetX = offsetX;
    }

    public void setOffsetY(double offsetY){
        this.offsetY = offsetY;
    }

    public void newPage(){
        pages.add(ops);
        this.ops = new ArrayList<>();
    }

    public List<List<Op>> getPages(){
        if( ops.size() > 0 ){
            newPage();
        }
        return pages;
    }

    private double transX(double x){
        return x * scale + offsetX;
    }

    private double transY(double y){
        return y * scale + offsetY;
    }

    private void opMoveTo(double x, double y){
        x = transX(x);
        y = transY(y);
        ops.add(new OpMoveTo(x, y));
    }

    private void opLineTo(double x, double y){
        x = transX(x);
        y = transY(y);
        ops.add(new OpLineTo(x, y));
    }

    private void opCreateFont(String name, String fontName, double size, int weight, boolean italic){
        ops.add(new OpCreateFont(name, fontName, size * scale, weight, italic));
    }

    private void opSetFont(String name){
        ops.add(new OpSetFont(name));
    }

    private void opDrawChars(String text, List<Double> xs, List<Double> ys){
        xs = xs.stream().map(this::transX).collect(toList());
        ys = ys.stream().map(this::transY).collect(toList());
        ops.add(new OpDrawChars(text, xs, ys));
    }

    private void opSetTextColor(int r, int g, int b){
        ops.add(new OpSetTextColor(r, g, b));

    }

    // TODO: adjust penStyle
    private void opCreatePen(String name, int red, int green, int blue, double width, int penStyle){
        ops.add(new OpCreatePen(name, red, green, blue, width * scale, penStyle));
    }

    private void opSetPen(String name){
        ops.add(new OpSetPen(name));
    }

    private void opCircle(double cx, double cy, double r){
        cx = transX(cx);
        cy = transY(cy);
        r *= scale;
        ops.add(new OpCircle(cx, cy, r));
    }

    public void moveTo(double x, double y) {
        opMoveTo(x, y);
    }

    public void lineTo(double x, double y) {
        opLineTo(x, y);
    }

    public void createFont(String name, String fontName, double size, int weight, boolean italic) {
        opCreateFont(name, fontName, size, weight, italic);
        fontMap.put(name, size);
    }

    public void createFont(String name, String fontName, double size) {
        createFont(name, fontName, size, 0, false);
    }



}
