package dev.myclinic.vertx.drawer;

/**
 * Created by hangil on 2017/05/17.
 */
public class Point {
    private final double x;
    private final double y;

    public Point(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Point shiftX(double dx){
        return new Point(x + dx, y);
    }

    public Point shiftY(double dy){
        return new Point(x, y + dy);
    }

    public Point shiftToRight(double amount){
        return shiftX(amount);
    }

    public Point shiftToLeft(double amount){
        return shiftX(-amount);
    }

    public Point shiftToUpward(double amount){
        return shiftY(-amount);
    }

    public Point shiftToDownward(double amount){
        return shiftY(amount);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
