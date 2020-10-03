package dev.myclinic.drawerform2;

import dev.myclinic.vertx.drawer.Box;
import dev.myclinic.vertx.drawer.DrawerCompiler;
import dev.myclinic.vertx.drawer.Render;

import static dev.myclinic.vertx.drawer.DrawerCompiler.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormCompiler extends DrawerCompiler {

    private final Map<String, Render.Rect> marks = new HashMap<>();
    private final Map<String, String> hints = new HashMap<>();

    public void addMark(String key, Box box){
        Render.Rect r = new Render.Rect(box.getLeft(), box.getTop(), box.getRight(), box.getBottom());
        marks.put(key, r);
    }

    public void addHint(String key, String hint){
        hints.put(key, hint);
    }

    public Map<String, Render.Rect> getMarks(){
        return marks;
    }

    public Map<String, String> getHints(){
        return hints;
    }

    public Box multiAt(double x, double y, VAlign valign, List<Multi> args){
        double height = getCurrentFontSize();
        double top;
        switch(valign){
            case Center: top = y - height * 0.5; break;
            case Bottom: top = y - height; break;
            default: top = y; break;
        }
        if( args.size() == 0 ){
            return new Box(x, top, x, top + height);
        }
        Box box = new Box(x, top, 300, top + height);
        for(Multi m : args){
            Box b = m.render(this, box);
            box = box.setLeft(b.getRight());
        }
        return box.setLeft(x);
    }

    public MultiLabel mLabel(String s){
        return new MultiLabel(s);
    }

    public MultiSpace mSpace(double width){
        return new MultiSpace(width);
    }

}
