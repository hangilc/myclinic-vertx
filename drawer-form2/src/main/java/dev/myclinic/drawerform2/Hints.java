package dev.myclinic.drawerform2;

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

    public static Radius radius(double radius){
        return new Radius(radius);
    }

}
