package dev.myclinic.vertx.drawer.pdf;

import com.itextpdf.text.pdf.BaseFont;

class DrawerFont {
    public BaseFont font;
    public float size;
    public float ascendor;

    public DrawerFont(BaseFont font, float size, float ascendor) {
        this.font = font;
        this.size = size;
        this.ascendor = ascendor;
    }

}
