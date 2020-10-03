package dev.myclinic.drawerform2;

import dev.myclinic.vertx.drawer.Box;

public class MultiSpace implements Multi {

    private final double width;

    public MultiSpace(double width) {
        this.width = width;
    }

    @Override
    public Box render(FormCompiler c, Box box) {
        return box.setWidth(width, Box.HorizAnchor.Left);
    }
}
