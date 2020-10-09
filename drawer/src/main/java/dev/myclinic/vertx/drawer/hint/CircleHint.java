package dev.myclinic.vertx.drawer.hint;

import dev.myclinic.vertx.drawer.Box;
import dev.myclinic.vertx.drawer.DrawerCompiler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CircleHint implements Hint {

    private static Pattern patRadius = Pattern.compile("radius\\(([0-9.]+)\\)");
    private final static Pattern patDefaultVAlign =
            Pattern.compile("default-v-align\\((top|center|bottom)\\)");

    private double radius = 2;

    public CircleHint(String[] specs) {
        for (String spec : specs) {
            if (spec == null || "circle".equals(spec)) {
                continue;
            }
            Matcher m;
            m = patDefaultVAlign.matcher(spec);
            if( m.matches() ){ // ignore default-v-align
                continue;
            }
            m = patRadius.matcher(spec);
            if (m.matches()) {
                this.radius = Double.parseDouble(m.group(1));
                continue;
            }
            throw new RuntimeException("Unknonwn hint: " + spec);
        }
    }

    @Override
    public Box render(DrawerCompiler compiler, Box box, String s) {
        if (!"false".equals(s)) {
            double cx = box.getCx();
            double cy = box.getCy();
            compiler.circle(cx, cy, radius);
        }
        return box;
    }
}
