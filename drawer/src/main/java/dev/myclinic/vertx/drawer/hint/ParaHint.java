package dev.myclinic.vertx.drawer.hint;

import dev.myclinic.vertx.drawer.Box;
import dev.myclinic.vertx.drawer.DrawerCompiler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.myclinic.vertx.drawer.DrawerCompiler.*;

class ParaHint extends HintBase implements Hint {

    private double leading = 0;

    private final static Pattern patLeading = Pattern.compile("leading\\(([0-9.]+)\\)");

    public ParaHint(String[] args){
        for (String a : args) {
            if (a == null || "para".equals(a) || parse(a)) {
                continue;
            }
            Matcher m = patLeading.matcher(a);
            if (m.matches()) {
                this.leading = Double.parseDouble(m.group(1));
                continue;
            }
            throw new RuntimeException("Unknown hint: " + a);
        }
    }

    @Override
    public void render(DrawerCompiler compiler, Box box, String s) {
        box = adjustBox(box);
        compiler.paragraph(s, box, getHAlign(), getVAlign(), leading);
    }

}
