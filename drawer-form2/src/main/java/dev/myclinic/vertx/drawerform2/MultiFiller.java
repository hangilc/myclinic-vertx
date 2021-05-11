package dev.myclinic.vertx.drawerform2;

import dev.myclinic.vertx.drawer.Box;
import dev.myclinic.vertx.drawer.DrawerCompiler;

public class MultiFiller implements Multi {
    @Override
    public Box render(FormCompiler c, Box box, DrawerCompiler.VAlign valign) {
        return box;
    }
}
