package dev.myclinic.drawerform2;

import static dev.myclinic.vertx.drawer.DrawerCompiler.*;

public class Hints {

    public static class Right implements Hint {
        @Override
        public String serialize() {
            return "right";
        }
    }

    public static Right right(){
        return new Right();
    }

    public static class Center implements Hint {
        @Override
        public String serialize() {
            return "center";
        }
    }

    public static Center center(){
        return new Center();
    }

    public static class Circle implements Hint {
        @Override
        public String serialize() {
            return "circle";
        }
    }

    public static Circle circle(){
        return new Circle();
    }

    public static class Para implements Hint {
        @Override
        public String serialize() {
            return "para";
        }
    }

    public static Para para(){
        return new Para();
    }

    private static class SingleValueHint implements Hint {
        private final String name;
        private final double value;

        public SingleValueHint(String name, double value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String serialize() {
            return String.format("%s(%g)", name, value);
        }
    }

    public static class RightPadding extends SingleValueHint {
        public RightPadding(double value) {
            super("right-padding", value);
        }
    }

    public static RightPadding rightPadding(double padding){
        return new RightPadding(padding);
    }

    public static class LeftPadding extends SingleValueHint {
        public LeftPadding(double value) {
            super("left-padding", value);
        }
    }

    public static LeftPadding leftPadding(double padding){
        return new LeftPadding(padding);
    }

    public static class Radius extends SingleValueHint {
        public Radius(double radius){
            super("radius", radius);
        }
    }

    public static class Padding extends SingleValueHint {

        public Padding(double value) {
            super("padding", value);
        }
    }

    public static Padding padding(double value){
        return new Padding(value);
    }

    public static class XPadding extends SingleValueHint {
        public XPadding(double value) {
            super("x-padding", value);
        }
    }

    public static XPadding xPadding(double padding){
        return new XPadding(padding);
    }

    public static Radius radius(double radius){
        return new Radius(radius);
    }

    public static class VTop implements Hint {
        @Override
        public String serialize() {
            return "v-top";
        }
    }

    public static VTop vTop(){
        return new VTop();
    }

    public static class VCenter implements Hint {
        @Override
        public String serialize() {
            return "v-center";
        }
    }

    public static VCenter vCenter(){
        return new VCenter();
    }

    public static class Leading extends SingleValueHint {
        public Leading(double leading){
            super("leading", leading);
        }
    }

    public static Leading leading(double leading){
        return new Leading(leading);
    }

    public static class Spacing extends SingleValueHint {
        public Spacing(double spacing){
            super("spacing", spacing);
        }
    }

    public static Spacing spacing(double spacing){
        return new Spacing(spacing);
    }

    public static class RightAt extends SingleValueHint {
        public RightAt(double right){
            super("right-at", right);
        }
    }

    public static RightAt rightAt(double right){
        return new RightAt(right);
    }

    public static class DefaultVAlign implements Hint {

        private final VAlign valign;

        public DefaultVAlign(VAlign valign){
            this.valign = valign;
        }

        @Override
        public String serialize() {
            return String.format("default-v-align(%s)", getRep());
        }

        private String getRep(){
            switch(valign){
                case Top: return "top";
                case Center: return "center";
                case Bottom: return "bottom";
                default: throw new RuntimeException("Invalid valign: " + valign);
            }
        }
    }
}
