package dev.myclinic.vertx.drawer.pdf;

import com.itextpdf.text.pdf.BaseFont;

class BaseFontData {
    public BaseFont baseFont;
    public float ascendorRate;

    public BaseFontData(BaseFont baseFont, float ascendorRate) {
        this.baseFont = baseFont;
        this.ascendorRate = ascendorRate;
    }
}
