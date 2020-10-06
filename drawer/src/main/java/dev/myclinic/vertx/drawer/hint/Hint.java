package dev.myclinic.vertx.drawer.hint;

import dev.myclinic.vertx.drawer.Box;
import dev.myclinic.vertx.drawer.DrawerCompiler;

public interface Hint {
    void render(DrawerCompiler compiler, Box box, String s);
    static void render(DrawerCompiler compiler, Box box, String s, String hintSrc){
        if( hintSrc == null ){
            compiler.textIn(s, box, DrawerCompiler.HAlign.Left, DrawerCompiler.VAlign.Top);
        } else {
            Hint hint = HintParser.parse(hintSrc);
            hint.render(compiler, box, s);
        }
    }
}
