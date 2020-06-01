package dev.myclinic.vertx.drawer.render;

import dev.myclinic.vertx.drawer.DrawerCompiler;
import dev.myclinic.vertx.drawer.DrawerCompiler.VAlign;

public interface Renderable {
    void render(DrawerCompiler compiler, double x, double y, VAlign valign); // returns horizontal advancement
    double calcWidth(DrawerCompiler compiler);
}
