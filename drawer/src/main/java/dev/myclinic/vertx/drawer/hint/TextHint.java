package dev.myclinic.vertx.drawer.hint;

import dev.myclinic.vertx.drawer.Box;
import dev.myclinic.vertx.drawer.DrawerCompiler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.myclinic.vertx.drawer.DrawerCompiler.HAlign;
import static dev.myclinic.vertx.drawer.DrawerCompiler.VAlign;

public class TextHint implements Hint {

    private enum Mode {TextIn, Para}

    ;

    private Mode mode = Mode.TextIn;
    private HAlign halign = HAlign.Left;
    private VAlign valign = VAlign.Center;
    private double rightPadding = 0;
    private double leftPadding = 0;
    private double topPadding = 0;
    private double bottomPadding = 0;
    private double leading = 0;

    private static Pattern patRightPadding =
            Pattern.compile("right-padding\\(([0-9.]+)\\)");
    private static Pattern patLeftPadding =
            Pattern.compile("left-padding\\(([0-9.]+)\\)");
    private static Pattern patPara =
            Pattern.compile("padding\\(([0-9.]+)\\)");
    private static Pattern patLeading =
            Pattern.compile("leading\\(([0-9.]+)\\)");
    private static Pattern patXPadding =
            Pattern.compile("x-padding\\(([0-9.]+)\\)");

    public TextHint(String[] specs) {
        for (String spec : specs) {
            if (spec == null) {
                continue;
            }
            switch (spec) {
                case "right":
                    this.halign = HAlign.Right;
                    break;
                case "center":
                    this.halign = HAlign.Center;
                    break;
                case "para":
                    this.mode = Mode.Para;
                    break;
                case "v-top":
                    this.valign = VAlign.Top;
                    break;
                default: {
                    Matcher m = patRightPadding.matcher(spec);
                    if (m.matches()) {
                        this.rightPadding = Double.parseDouble(m.group(1));
                        break;
                    }
                    m = patLeftPadding.matcher(spec);
                    if (m.matches()) {
                        this.leftPadding = Double.parseDouble(m.group(1));
                        break;
                    }
                    m = patPara.matcher(spec);
                    if (m.matches()) {
                        double padding = Double.parseDouble(m.group(1));
                        this.leftPadding = this.topPadding = this.rightPadding = this.bottomPadding = padding;
                        break;
                    }
                    m = patLeading.matcher(spec);
                    if (m.matches()) {
                        this.leading = Double.parseDouble(m.group(1));
                        break;
                    }
                    m = patXPadding.matcher(spec);
                    if (m.matches()) {
                        double padding = Double.parseDouble(m.group(1));
                        this.leftPadding = this.rightPadding = padding;
                        break;
                    }
                    throw new RuntimeException("Unknonw hint: " + spec);
                }
            }
        }
    }

    @Override
    public void render(DrawerCompiler compiler, Box box, String s) {
        if (mode == Mode.Para) {
            renderPara(compiler, box, s);
        } else if (mode == Mode.TextIn) {
            renderTextIn(compiler, box, s);
        }
    }

    public void renderPara(DrawerCompiler compiler, Box box, String s) {
        Box b = box.inset(leftPadding, topPadding, rightPadding, bottomPadding);
        compiler.paragraph(s, b, halign, valign, leading);
    }

    public void renderTextIn(DrawerCompiler compiler, Box box, String s) {
        Box b = box.inset(leftPadding, topPadding, rightPadding, bottomPadding);
        compiler.textIn(s, b, halign, valign);
    }

}
