package dev.myclinic.drawerform2;

import dev.myclinic.vertx.drawer.Box;
import static dev.myclinic.vertx.drawer.Box.*;
import dev.myclinic.vertx.drawer.DrawerCompiler;
import dev.myclinic.vertx.drawer.Render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FormCompiler extends DrawerCompiler {

    private final Map<String, Box> marks = new HashMap<>();
    private final Map<String, String> hints = new HashMap<>();
    private final List<String> descriptions = new ArrayList<>();

    public void addMark(String key, String description, Box box, List<Hint> hints){
        if( key.indexOf(':') >= 0 ){
            throw new RuntimeException("mark key cannot contain ':'");
        }
        marks.put(key, box);
        if( hints != null && hints.size() > 0 ){
            String h = hints.stream().map(Hint::serialize).collect(Collectors.joining(":"));
            this.hints.put(key, h);
        }
        if( description != null ){
            descriptions.add(String.format("%s:%s", key, description));
        }
    }

    public void modifyMark(String key, Function<Box, Box> modifier){
        Box b = marks.get(key);
        b = modifier.apply(b);
        marks.put(key, b);
    }

    public Map<String, Render.Rect> getMarks(){
        Map<String, Render.Rect> rs = new HashMap<>();
        for(String key: marks.keySet()){
            Box b = marks.get(key);
            rs.put(key, Render.Rect.fromBox(b));
        }
        return rs;
    }

    public Map<String, String> getHints(){
        return hints;
    }

    public List<String> getDescriptions(){ return descriptions; }

    public Box multi(Box box, VAlign valign, List<Multi> args){
        if( args.size() == 0 ){
            return box.setWidth(0, HorizAnchor.Left);
        }
        Box b = box;
        for(Multi m : args){
            b = m.render(this, b, valign);
            b = box.setLeft(b.getRight());
        }
        return box.setRight(b.getLeft());
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
        Box b = box;
        for(Multi m : args){
            b = m.render(this, b, valign);
            b = box.setLeft(b.getRight());
        }
        return box.setRight(b.getLeft());
    }

    public MultiLabel mLabel(String s){
        return new MultiLabel(s);
    }

    public MultiSpace mSpace(double width){
        return new MultiSpace(width);
    }

    public MultiBracket mBracket(String left, String mark, String description, List<Hint> hints, String right){
        return new MultiBracket(left, mark, description, hints, right);
    }

    public MultiBracket mBracket(String left, String right){
        return new MultiBracket(left, right);
    }

    public MultiJustified mJustified(String text, double width){
        return new MultiJustified(text, width);
    }

    public MultiJustified mJustified(String text){
        return new MultiJustified(text, null);
    }
}
