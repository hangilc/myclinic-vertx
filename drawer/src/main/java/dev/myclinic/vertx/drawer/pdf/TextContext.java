package dev.myclinic.vertx.drawer.pdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.BaseFont;

class TextContext {

    private boolean initialized = false;
    private BaseFont textFont;
    private float textSize;
    private float ascendor;
    private BaseColor textColor = new BaseColor(0, 0, 0);

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public BaseFont getTextFont() {
        return textFont;
    }

    public void setTextFont(BaseFont textFont) {
        this.textFont = textFont;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float getAscendor() {
        return ascendor;
    }

    public void setAscendor(float ascendor) {
        this.ascendor = ascendor;
    }

    public BaseColor getTextColor() {
        return textColor;
    }

    public void setTextColor(BaseColor textColor) {
        this.textColor = textColor;
    }
}
