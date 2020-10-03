package dev.myclinic.drawerform2;

import dev.myclinic.vertx.drawer.Box;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MarkedMulti implements Multi {

    private final Multi orig;
    private final String mark;
    private final List<Hint> hints = new ArrayList<>();

    public MarkedMulti(Multi orig, String mark) {
        this.orig = orig;
        this.mark = mark;
    }

    @Override
    public Box render(FormCompiler c, Box box) {
        Box b = orig.render(c, box);
        c.addMark(mark, b);
        if( hints.size() > 0 ){
            String h = hints.stream().map(Hint::serialize).collect(Collectors.joining(":"));
            c.addHint(mark, h);
        }
        return b;
    }

    public MarkedMulti right(){
        hints.add(new Hints.Right());
        return this;
    }

    public MarkedMulti rightPadding(double padding){
        hints.add(new Hints.RightPadding(padding));
        return this;
    }

}
