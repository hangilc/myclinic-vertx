package dev.myclinic.vertx.drawer.hint;

import dev.myclinic.vertx.drawer.Box;
import dev.myclinic.vertx.drawer.DrawerCompiler;

public interface Hint {
    void render(DrawerCompiler compiler, Box box, String s);
}
