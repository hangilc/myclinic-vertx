package dev.myclinic.vertx.drawerform2;

import dev.myclinic.vertx.drawer.Box;
import dev.myclinic.vertx.drawer.DrawerCompiler;
import static dev.myclinic.vertx.drawer.DrawerCompiler.VAlign;

public class MultiLabel implements Multi {

    private final String text;
    private String font = null;

    public MultiLabel(String text) {
        this.text = text;
    }

    @Override
    public Box render(FormCompiler c, Box box, VAlign valign) {
        if( font != null ){
            c.pushFont();
            c.setFont(font);
        }
        Box b = c.textIn(text, box, DrawerCompiler.HAlign.Left, valign);
        if( font != null ){
            c.popFont();
        }
        return b;
    }

    public MultiLabel font(String font){
        this.font = font;
        return this;
    }
}
