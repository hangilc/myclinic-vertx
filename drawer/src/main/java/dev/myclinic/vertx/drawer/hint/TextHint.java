package dev.myclinic.vertx.drawer.hint;

import dev.myclinic.vertx.drawer.Box;
import dev.myclinic.vertx.drawer.DrawerCompiler;

public class TextHint extends HintBase implements Hint {

    public TextHint(String[] specs) {
        for (String spec : specs) {
            if (spec == null) {
                continue;
            }
            if (super.parse(spec)) {
                continue;
            }
            throw new RuntimeException("Unknonw hint: " + spec);
        }
    }

    @Override
    public void render(DrawerCompiler compiler, Box box, String s) {
        box = adjustBox(box);
        compiler.textIn(s, box, super.getHAlign(), getVAlign());
    }

}
