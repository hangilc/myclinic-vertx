package dev.myclinic.vertx.drawer.render;

import dev.myclinic.vertx.drawer.DrawerCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.myclinic.vertx.drawer.DrawerCompiler.HAlign;
import static dev.myclinic.vertx.drawer.DrawerCompiler.VAlign;

public class SuperScriptRenderer implements Renderable {

    private static Logger logger = LoggerFactory.getLogger(SuperScriptRenderer.class);
    private String text;
    private String fontName;

    public SuperScriptRenderer(String text, String fontName) {
        this.text = text;
        this.fontName = fontName;
    }

    @Override
    public void render(DrawerCompiler compiler, double x, double y, VAlign valign) {
        double top = y;
        if( valign == VAlign.Center ){
            top -= compiler.getCurrentFontSize() * 0.5;
        } else if( valign == VAlign.Bottom ){
            top -= compiler.getCurrentFontSize();
        }
        double offset = compiler.getFontSizeFor(fontName) * 0.5 - compiler.getCurrentFontSize() * 0.25;
        compiler.setFontPushing(fontName);
        compiler.textAt(text, x, top - offset, HAlign.Left, VAlign.Center);
        compiler.popFont();
    }

    @Override
    public double calcWidth(DrawerCompiler compiler) {
        double fontSize = compiler.getFontSizeFor(fontName);
        return compiler.calcTextWidth(text, fontSize);
    }

}
