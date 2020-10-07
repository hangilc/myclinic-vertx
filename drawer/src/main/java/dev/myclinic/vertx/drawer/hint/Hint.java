package dev.myclinic.vertx.drawer.hint;

import dev.myclinic.vertx.drawer.Box;
import dev.myclinic.vertx.drawer.DrawerCompiler;

import java.util.List;

public interface Hint {
    void render(DrawerCompiler compiler, Box box, String s);

    static void render(DrawerCompiler compiler, Box box, String s, Hint hint){
        if( hint == null ){
            compiler.textIn(s, box, DrawerCompiler.HAlign.Left, DrawerCompiler.VAlign.Top);
        } else {
            hint.render(compiler, box, s);
        }
    }

    static void render(DrawerCompiler compiler, Box box, String s, String hintSrc){
        render(compiler, box, s, HintParser.parse(hintSrc));
    }
}
