package dev.myclinic.vertx.drawer.pdf;

import com.itextpdf.text.BaseColor;

class StrokeData {
    public BaseColor color;
    public float width;
    public float[] style;

    public StrokeData(BaseColor color, float width, float[] style) {
        this.color = color;
        this.width = width;
        this.style = style;
    }
}
