package dev.myclinic.vertx.drawer.hint;

import dev.myclinic.vertx.drawer.Box;
import static dev.myclinic.vertx.drawer.DrawerCompiler.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class HintBase {

    private double leftPadding = 0;
    private double topPadding = 0;
    private double rightPadding = 0;
    private double bottomPadding = 0;
    private HAlign halign = HAlign.Left;
    private VAlign valign = VAlign.Top;

    private final static Pattern patLeftPadding =
            Pattern.compile("left-padding\\(([0-9.]+)\\)");
    private final static Pattern patTopPadding =
            Pattern.compile("top-padding\\(([0-9.]+)\\)");
    private final static Pattern patRightPadding =
            Pattern.compile("right-padding\\(([0-9.]+)\\)");
    private final static Pattern patBottomPadding =
            Pattern.compile("bottom-padding\\(([0-9.]+)\\)");
    private final static Pattern patPadding =
            Pattern.compile("padding\\(([0-9.]+)\\)");
    private final static Pattern patXPadding =
            Pattern.compile("x-padding\\(([0-9.]+)\\)");
    private final static Pattern patYPadding =
            Pattern.compile("y-padding\\(([0-9.]+)\\)");

    public boolean parse(String s){
        Matcher m;
        m = patLeftPadding.matcher(s);
        if( m.matches() ){
            this.leftPadding = Double.parseDouble(m.group(1));
            return true;
        }
        m = patTopPadding.matcher(s);
        if( m.matches() ){
            this.topPadding = Double.parseDouble(m.group(1));
            return true;
        }
        m = patRightPadding.matcher(s);
        if( m.matches() ){
            this.rightPadding = Double.parseDouble(m.group(1));
            return true;
        }
        m = patBottomPadding.matcher(s);
        if( m.matches() ){
            this.bottomPadding = Double.parseDouble(m.group(1));
            return true;
        }
        m = patPadding.matcher(s);
        if( m.matches() ){
            this.leftPadding = this.topPadding =
                    this.rightPadding = this.bottomPadding = Double.parseDouble(m.group(1));
            return true;
        }
        m = patXPadding.matcher(s);
        if( m.matches() ){
            this.leftPadding = this.rightPadding = Double.parseDouble(m.group(1));
            return true;
        }
        m = patYPadding.matcher(s);
        if( m.matches() ){
            this.topPadding = this.bottomPadding = Double.parseDouble(m.group(1));
            return true;
        }
        switch(s){
            case "left": {
                this.halign = HAlign.Left;
                return true;
            }
            case "right": {
                this.halign = HAlign.Right;
                return true;
            }
            case "center": {
                this.halign = HAlign.Center;
                return true;
            }
            case "v-top": {
                this.valign = VAlign.Top;
                return true;
            }
            case "v-bottom": {
                this.valign = VAlign.Bottom;
                return true;
            }
            case "v-center": {
                this.valign = VAlign.Center;
                return true;
            }
        }
        return false;
    }

    public Box adjustBox(Box box){
        return box.inset(leftPadding, topPadding, rightPadding, bottomPadding);
    }

    public HAlign getHAlign(){
        return halign;
    }

    public VAlign getVAlign(){
        return valign;
    }

}
