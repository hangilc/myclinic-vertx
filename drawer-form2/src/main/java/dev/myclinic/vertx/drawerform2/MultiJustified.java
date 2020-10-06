package dev.myclinic.vertx.drawerform2;

import dev.myclinic.vertx.drawer.Box;
import static dev.myclinic.vertx.drawer.Box.HorizAnchor;
import dev.myclinic.vertx.drawer.DrawerCompiler;

public class MultiJustified implements Multi {

    private String text;
    private Double width = null;

    public MultiJustified(String text, Double width) {
        this.text = text;
        this.width = width;
    }

    @Override
    public Box render(FormCompiler c, Box box, DrawerCompiler.VAlign valign) {
        if( width != null ){
            box = box.setWidth(width, HorizAnchor.Left);
        }
        return c.textInJustified(text, box, valign);
    }
}
