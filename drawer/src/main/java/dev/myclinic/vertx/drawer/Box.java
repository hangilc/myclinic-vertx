package dev.myclinic.vertx.drawer;

import java.util.Arrays;

public class Box {

    public enum HorizAnchor {
        Left, Center, Right
    }

    public enum VertAnchor {
        Top, Center, Bottom
    }

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

    public Box(PaperSize paperSize){
        this(0, 0, paperSize.getWidth(), paperSize.getHeight());
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

    public Box copy(){
        return new Box(left, top, right, bottom);
    }

    public Box innerBox(double left, double top, double right, double bottom){
        return new Box(this.left + left, this.top + top, this.left + right, this.top + bottom);
    }

    public Point innerPoint(double left, double top){
        return new Point(this.left + left, this.top + top);
    }

    public double getWidth(){
        return right - left;
    }

    public double getHeight(){
        return bottom - top;
    }

    public double getCx(){
        return (left + right)/2.0;
    }

    public double getCy(){
        return (top + bottom)/2.0;
    }

    public Box setLeft(double left){
        return new Box(left, top, right, bottom);
    }

    public Box displaceLeftEdge(double dx){
        return new Box(left + dx, top, right, bottom);
    }

    public Box setTop(double top){
        return new Box(left, top, right, bottom);
    }

    public Box setRight(double right){
        return new Box(left, top, right, bottom);
    }

    public Box displaceRightEdge(double dx){
        return new Box(left, top, right + dx, bottom);
    }

    public Box setBottom(double bottom){
        return new Box(left, top, right, bottom);
    }

    public Box inset(double amount){
        return inset(amount, amount);
    }

    public Box inset(double dx, double dy){
        return new Box(left + dx, top + dy, right - dx, bottom - dy);
    }

    public Box inset(double dx1, double dy1, double dx2, double dy2){
        return new Box(left + dx1, top + dy1, right - dx2, bottom - dy2);
    }

    public Box shift(double dx, double dy){
        return new Box(left + dx, top + dy, right + dx, bottom + dy);
    }

    public Box shiftUp(double dy){
        return shift(0, -dy);
    }

    public Box shiftDown(double dy){
        return shift(0, dy);
    }

    public Box shiftToRight(double dx){
        return shift(dx, 0);
    }

    public Box shiftToLeft(double dx){
        return shift(-dx, 0);
    }

    public Box shrinkWidth(double dx, HorizAnchor anchor){
        switch(anchor){
            case Left: return new Box(left, top, right - dx, bottom);
            case Center: {
                double half = dx/2.0;
                return new Box(left + half, top, right - half, bottom);
            }
            case Right: {
                return new Box(left + dx, top, right, bottom);
            }
            default: throw new RuntimeException("unknown anchor: " + anchor);
        }
    }

    public Box shrinkHeight(double dy, VertAnchor anchor){
        switch(anchor){
            case Top: return new Box(left, top, right, bottom - dy);
            case Center: {
                double half = dy/2.0;
                return new Box(left, top + half, right, bottom - half);
            }
            case Bottom: return new Box(left, top + dy, right, bottom);
            default: throw new RuntimeException("unknown anchor: " + anchor);
        }
    }

    public Box expandHeight(double dy, VertAnchor anchor){
        return shrinkHeight(-dy, anchor);
    }

    public Box setWidth(double width, HorizAnchor anchor){
        switch(anchor){
            case Left: return new Box(left, top, left + width, bottom);
            case Center: {
                double left = getCx() - width/2.0;
                return new Box(left, top, left + width, bottom);
            }
            case Right: return new Box(right - width, top, right, bottom);
            default: throw new RuntimeException("unknown anchor: " + anchor);
        }
    }

    public Box setHeight(double height, VertAnchor anchor){
        switch(anchor){
            case Top: return new Box(left, top, right, top + height);
            case Center: {
                double top = getCy() - height/2.0;
                return new Box(left, top, right, top + height);
            }
            case Bottom: return new Box(left, bottom - height, right, bottom);
            default: throw new RuntimeException("unknown anchor: " + anchor);
        }
    }

    public Box flipRight(){
        return shiftToRight(getWidth());
    }

    public Box flipLeft(){
        return shiftToLeft(getWidth());
    }

    public Box flipUp(){
        return shiftUp(getHeight());
    }

    public Box flipDown(){
        return shiftDown(getHeight());
    }

    public Box[] splitToColumns(double... borders){
        Box[] cols = new Box[borders.length + 1];
        double colLeft = left;
        for(int i=0;i<borders.length;i++){
            double colRight = left + borders[i];
            cols[i] = new Box(colLeft, top, colRight, bottom);
            colLeft = colRight;
        }
        cols[borders.length] = new Box(colLeft, top, right, bottom);
        return cols;
    }

    public Box[] splitToColumnsByWidths(double... ws){
        Box[] cols = new Box[ws.length+1];
        double colLeft = left;
        for(int i=0;i<ws.length;i++){
            double colRight = colLeft + ws[i];
            cols[i] = new Box(colLeft, top, colRight, bottom);
            colLeft = colRight;
        }
        cols[ws.length] = new Box(colLeft, top, right, bottom);
        return cols;
    }

    public Box[] splitToEvenColumns(int n){
        Box[] cols = new Box[n];
        double colLeft = left;
        double w = getWidth() / n;
        for(int i=0;i<n;i++){
            double colRight = (i == (n-1)) ? right: (left + w * (i+1));
            cols[i] = new Box(colLeft, top, colRight, bottom);
            colLeft = colRight;
        }
        return cols;
    }

    public Box[] splitToRows(double... borders){
        Box[] rows = new Box[borders.length + 1];
        double rowTop = top;
        for(int i=0;i<borders.length;i++){
            double rowBottom = top + borders[i];
            rows[i] = new Box(left, rowTop, right, rowBottom);
            rowTop = rowBottom;
        }
        rows[borders.length] = new Box(left, rowTop, right, bottom);
        return rows;
    }

    public Box[] splitToRowsByHeights(double... hs){
        Box[] rows = new Box[hs.length + 1];
        double rowTop = top;
        for(int i=0;i<hs.length;i++){
            double rowBottom = rowTop + hs[i];
            rows[i] = new Box(left, rowTop, right, rowBottom);
            rowTop = rowBottom;
        }
        rows[hs.length] = new Box(left, rowTop, right, bottom);
        return rows;
    }

    public Box[] splitToEvenRows(int n){
        Box[] rows = new Box[n];
        double rowTop = top;
        double h = getHeight() / n;
        for(int i=0;i<n;i++){
            double rowBottom = (i == (n-1)) ? bottom: (top + h * (i+1));
            rows[i] = new Box(left, rowTop, right, rowBottom);
            rowTop = rowBottom;
        }
        return rows;
    }

    public Box[] splitToVerticallyJustifiedRows(double rowHeight, int nrows){
        if( nrows <= 1 ){
            return new Box[] { setHeight(rowHeight, VertAnchor.Top) };
        }
        double gap = (getHeight() - rowHeight * nrows) / (nrows - 1);
        double left = getLeft();
        double right = getRight();
        double top = getTop();
        Box[] rows = new Box[nrows];
        for(int i=0;i<nrows;i++){
            if( i != (nrows - 1) ){
                rows[i] = new Box(left, top, right, top + rowHeight);
                top += rowHeight + gap;
            } else {
                rows[i] = new Box(left, getBottom() - rowHeight, right, getBottom());
            }
        }
        return rows;
    }

    public Box[] splitToHorizontallyJustifiedColumns(double colWidth, int ncols){
        if( ncols <= 1 ){
            return new Box[] { setWidth(colWidth, HorizAnchor.Left) };
        }
        double gap = (getWidth() - colWidth * ncols) / (ncols - 1);
        double top = getTop();
        double bottom = getBottom();
        double left = getLeft();
        Box[] cols = new Box[ncols];
        for(int i=0;i<ncols;i++){
            if( i != (ncols - 1) ){
                cols[i] = new Box(left, top, left + colWidth, bottom);
                left += colWidth + gap;
            } else {
                cols[i] = new Box(getRight() - colWidth, top, getRight(), bottom);
            }
        }
        return cols;
    }

    public Box[][] splitToEvenCells(int nrows, int ncols){
        Box[][] cells = new Box[nrows][];
        Box[] rows = splitToEvenRows(nrows);
        for(int i=0;i<nrows;i++){
            cells[i] = rows[i].splitToEvenColumns(ncols);
        }
        return cells;
    }

    public Point getCenterPoint(){
        return new Point(getCx(), getCy());
    }

    public static Box boundingBox2(Box a, Box b){
        return new Box(Math.min(a.left, b.left), Math.min(a.top, b.top),
                Math.max(a.right, b.right), Math.max(a.bottom, b.bottom));
    }

    public static Box boundingBox(Box... boxes){
        return Arrays.stream(boxes).reduce(Box::boundingBox2).get();
    }

    public static Box of(PaperSize paperSize){
        return new Box(0, 0, paperSize.getWidth(), paperSize.getHeight());
    }


}
