package dev.myclinic.drawerform2;

public class Hints {

    public static class Right implements Hint {
        @Override
        public String serialize() {
            return "right";
        }
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

}
