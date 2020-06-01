package dev.myclinic.vertx.drawer.render;

import dev.myclinic.vertx.drawer.DrawerCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static dev.myclinic.vertx.drawer.DrawerCompiler.HAlign;
import static dev.myclinic.vertx.drawer.DrawerCompiler.VAlign;

public class PlusMinusRenderer implements Renderable {

    private static Logger logger = LoggerFactory.getLogger(PlusMinusRenderer.class);
    private String plus = "＋";
    private String minus = "－";

    @Override
    public void render(DrawerCompiler compiler, double x, double y, VAlign valign) {
        compiler.textAt(plus, x, y, HAlign.Left, valign);
        compiler.textAt(minus, x, y + compiler.getCurrentFontSize()* 0.46, HAlign.Left, valign);
    }

    @Override
    public double calcWidth(DrawerCompiler compiler) {
        return List.of(plus, minus).stream().mapToDouble(compiler::calcTextWidth).max().orElse(0);
    }
}
