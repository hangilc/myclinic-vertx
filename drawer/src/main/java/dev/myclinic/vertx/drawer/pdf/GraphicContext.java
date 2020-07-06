package dev.myclinic.vertx.drawer.pdf;

import com.itextpdf.text.BaseColor;

class GraphicContext {

    private boolean initialized = false;
    private BaseColor strokeColor = new BaseColor(0, 0, 0);
    private float strokeWidth = 1;
    private float[] strokeStyle = null;

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public BaseColor getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(BaseColor strokeColor) {
        this.strokeColor = strokeColor;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public float[] getStrokeStyle() {
        return strokeStyle;
    }

    public void setStrokeStyle(float[] strokeStyle) {
        this.strokeStyle = strokeStyle;
    }
}
