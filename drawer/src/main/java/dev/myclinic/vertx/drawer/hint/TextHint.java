package dev.myclinic.vertx.drawer.hint;

import dev.myclinic.vertx.drawer.Box;
import dev.myclinic.vertx.drawer.DrawerCompiler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextHint extends HintBase implements Hint {

    private double spacing = 0;

    private final static Pattern spacingPattern =
            Pattern.compile("spacing\\(([0-9.]+)\\)");

    public TextHint(String[] specs) {
        for (String spec : specs) {
            if (spec == null) {
                continue;
            }
            if (super.parse(spec)) {
                continue;
            }
            Matcher m;
            m = spacingPattern.matcher(spec);
            if (m.matches()) {
                this.spacing = Double.parseDouble(m.group(1));
                continue;
            }
            throw new RuntimeException("Unknonw hint: " + spec);
        }
    }

    @Override
    public Box render(DrawerCompiler compiler, Box box, String s) {
        box = adjustBox(box);
        String font = getFont();
        if( font != null ){
            compiler.pushFont();
            compiler.setFont(font);
        }
        Box r = compiler.textIn(s, box, super.getHAlign(), getVAlign(),
                new DrawerCompiler.TextAtOpt(spacing));
        if( font != null ){
            compiler.popFont();
        }
        return r;
    }

}
