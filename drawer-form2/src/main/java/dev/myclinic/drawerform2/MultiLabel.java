package dev.myclinic.drawerform2;

import dev.myclinic.vertx.drawer.Box;
import dev.myclinic.vertx.drawer.DrawerCompiler;

public class MultiLabel implements Multi {

    private final String text;

    public MultiLabel(String text) {
        this.text = text;
    }

    @Override
    public Box render(FormCompiler c, Box box) {
        return c.textIn(text, box, DrawerCompiler.HAlign.Left, DrawerCompiler.VAlign.Top);
    }
}
