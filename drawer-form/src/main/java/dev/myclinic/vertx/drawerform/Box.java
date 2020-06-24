package dev.myclinic.vertx.drawerform;

import java.util.ArrayList;
import java.util.List;

public class Box {

    private final double left;
    private final double top;
    private final double right;
    private final double bottom;

    public Box(double left, double top, double right, double bottom){
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    @Override
    public String toString() {
        return "Box{" +
                "left=" + left +
                ", top=" + top +
                ", right=" + right +
                ", bottom=" + bottom +
                '}';
    }

    public double getLeft() {
        return left;
    }

    public double getTop() {
        return top;
    }

    public double getRight() {
        return right;
    }

    public double getBottom() {
        return bottom;
    }

    public double getWidth(){
        return this.right - this.left;
    }

    public double getHeight(){
        return this.bottom - this.top;
    }

    public Box innerBox(double left, double top, double right, double bottom){
        return new Box(this.left + left, this.top + top, this.left + right, this.top + bottom);
    }

    public Box inset(double dx1, double dy1, double dx2, double dy2){
        return new Box(this.left + dx1, this.top + dy1, this.right - dx2, this.bottom - dy2);
    }

    public Box inset(double dx, double dy){
        return inset(dx, dy, dx, dy);
    }

    public Box inset(double inset){
        return inset(inset, inset);
    }

    public Box setLeft(double left){
        return new Box(left, this.top, this.right, this.bottom);
    }

    public Box setTop(double top){
        return new Box(this.left, top, this.right, this.bottom);
    }

    public Box setRight(double right){
        return new Box(this.left, this.top, right, this.bottom);
    }

    public Box setBottom(double bottom){
        return new Box(this.left, this.top, this.right, bottom);
    }

    public List<Box> splitToRows(double... borders){
        List<Box> rows = new ArrayList<>();
        double rowTop = top;
        for (double border : borders) {
            double rowBottom = top + border;
            rows.add(new Box(left, rowTop, right, rowBottom));
            rowTop = rowBottom;
        }
        rows.add(new Box(left, rowTop, right, bottom));
        return rows;
    }

    public List<Box> splitToRowsByHeights(double... hs){
        double[] borders = new double[hs.length];
        double y = 0;
        for(int i=0;i<hs.length;i++){
            y += hs[i];
            borders[i] = y;
        }
        return splitToRows(borders);
    }

    public List<Box> splitToEvenRows(int n){
        double[] borders = new double[n-1];
        double height = getHeight();
        for(int i=1;i<n;i++){
            borders[i-1] = height / n * i;
        }
        return splitToRows(borders);
    }

    public List<Box> splitToColumns(double... borders){
        List<Box> cols = new ArrayList<>();
        double colLeft = left;
        for (double border : borders) {
            double colRight = left + border;
            cols.add(new Box(colLeft, top, colRight, bottom));
            colLeft = colRight;
        }
        cols.add(new Box(colLeft, top, right, bottom));
        return cols;
    }

    public List<Box> splitToColumnsByWidths(double... ws){
        List<Box> cols = new ArrayList<>();
        double[] borders = new double[ws.length];
        double x = 0;
        for(int i=0;i<ws.length;i++){
            x += ws[i];
            borders[i] = x;
        }
        return splitToColumns(borders);
    }

    public List<Box> splitToEvenColumns(int n){
        double[] borders = new double[n-1];
        double width = getWidth();
        for(int i=1;i<n;i++){
            borders[i-1] = width / n * i;
        }
        return splitToColumns(borders);
    }

    public Box shift(double dx, double dy){
        return new Box(this.left + dx, this.top + dy, this.right + dx, this.bottom + dy);
    }

    public Box flipRight(){
        return shift(getWidth(), 0);
    }

    public Box flipLeft(){
        return shift(-getWidth(), 0);
    }

    public Box flipUp(){
        return shift(0, -getHeight());
    }

    public Box flipDown(){
        return shift(0, getHeight());
    }

}
