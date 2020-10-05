package dev.myclinic.drawerform2;

import dev.myclinic.vertx.drawer.Box;
import static dev.myclinic.vertx.drawer.DrawerCompiler.VAlign;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MarkedMulti implements Multi {

    private final Multi orig;
    private final String mark;
    private final List<Hint> hints = new ArrayList<>();

    public MarkedMulti(Multi orig, String mark) {
        this.orig = orig;
        this.mark = mark;
    }

    @Override
    public Box render(FormCompiler c, Box box, VAlign valign) {
        Box b = orig.render(c, box, valign);
        c.addMark(mark, b);
        c.setHints(mark, hints);
        return b;
    }

    public MarkedMulti addHints(Hint ...hints){
        Collections.addAll(this.hints, hints);
        return this;
    }

}
