package dev.myclinic.vertx.drawerprinterwin;

public class AuxSetting {

    private double dx = 0.0;
    private double dy = 0.0;
    private double scale = 1.0;
    private double rotate = 0.0;

    public double getDx() {
        return dx;
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public double getDy() {
        return dy;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double getRotate() {
        return this.rotate;
    }

    public void setRotate(double rotate) {
        this.rotate = rotate;
    }

    @Override
    public String toString() {
        return "AuxSetting{" +
                "dx=" + dx +
                ", dy=" + dy +
                ", scale=" + scale +
                ", rotate=" + rotate +
                '}';
    }
}
