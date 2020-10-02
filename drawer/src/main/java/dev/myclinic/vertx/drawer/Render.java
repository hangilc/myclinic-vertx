package dev.myclinic.vertx.drawer;

import dev.myclinic.vertx.drawer.hint.Hint;
import dev.myclinic.vertx.drawer.hint.HintParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Render {

    public static class Rect {
        public double left;
        public double top;
        public double right;
        public double bottom;
    }

    public static class Form {
        public String page;
        public Map<String, Rect> marks;
        public Map<String, String> hints;
        public List<Op> form;
    }

    private final Form form;
    private final DrawerCompiler c;

    public Render(Form form){
        this.form = form;
        this.c = new DrawerCompiler();
        this.c.importOps(form.form);
    }

    public void add(String mark, String value){
        Rect rect = form.marks.get(mark);
        if( rect == null ){
            throw new RuntimeException("Cannot find mark: " + mark);
        }
        Box box = new Box(rect.left, rect.top, rect.right, rect.bottom);
        String hintSrc = form.hints.get(mark);
        if( hintSrc == null ){
            c.textIn(value, box, DrawerCompiler.HAlign.Left, DrawerCompiler.VAlign.Top);
        } else {
            Hint hint = HintParser.parse(hintSrc);
            hint.render(c, box, value);
        }
    }

    public List<Op> getOps(){
        return c.getOps();
    }

}
