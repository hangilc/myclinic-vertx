package dev.myclinic.drawerform2;

import dev.myclinic.vertx.drawer.Box;
import dev.myclinic.vertx.drawer.DrawerCompiler;
import static dev.myclinic.vertx.drawer.DrawerCompiler.*;

import java.util.ArrayList;
import java.util.List;

public class MultiBracket implements Multi {

    private final String left;
    private final String mark;
    private final List<Hint> hints;
    private final String right;

    public MultiBracket(String left, String mark, List<Hint> hints, String right) {
        this.left = left;
        this.mark = mark;
        this.hints = hints;
        this.right = right;
    }

    public MultiBracket(String left, String right) {
        this(left, null, null, right);
    }

    @Override
    public Box render(FormCompiler c, Box box, DrawerCompiler.VAlign valign) {
        Box b1 = c.textIn(left, box, HAlign.Left, valign);
        Box b2 = c.textIn(right, box, HAlign.Right, valign);
        if( mark != null ){
            Box mb = box.setLeft(b1.getRight()).setRight(b2.getLeft());
            List<Hint> mhints = new ArrayList<>();
            mhints.add(new Hints.DefaultVAlign(valign));
            mhints.addAll(hints);
            c.addMarkAndHints(mark, mb, mhints);
        }
        return box;
    }
}
