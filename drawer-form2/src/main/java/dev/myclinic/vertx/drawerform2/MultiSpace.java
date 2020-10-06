package dev.myclinic.vertx.drawerform2;

import dev.myclinic.vertx.drawer.Box;
import static dev.myclinic.vertx.drawer.DrawerCompiler.VAlign;

public class MultiSpace implements Multi {

    private final double width;

    public MultiSpace(double width) {
        this.width = width;
    }

    @Override
    public Box render(FormCompiler c, Box box, VAlign valign) {
        return box.setWidth(width, Box.HorizAnchor.Left);
    }
}
