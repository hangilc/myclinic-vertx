package dev.myclinic.vertx.drawerform2;

import dev.myclinic.vertx.drawer.Box;
import dev.myclinic.vertx.drawer.DrawerCompiler;
import static dev.myclinic.vertx.drawer.DrawerCompiler.*;

import java.util.ArrayList;
import java.util.List;

public class MultiBracket implements Multi {

    private final String left;
    private final String mark;
    private final List<Hint> hints;
    private final String description;
    private final String right;

    public MultiBracket(String left, String mark, String description, List<Hint> hints, String right) {
        this.left = left;
        this.mark = mark;
        this.hints = hints;
        this.description = description;
        this.right = right;
    }

    public MultiBracket(String left, String right) {
        this(left, null, null, null, right);
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
            c.addMark(mark, description, mb, mhints);
        }
        return box;
    }
}
