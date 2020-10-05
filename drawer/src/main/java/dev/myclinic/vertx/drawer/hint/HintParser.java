package dev.myclinic.vertx.drawer.hint;

public class HintParser {

    public static Hint parse(String hintSrc) {
        String[] parts = hintSrc.split(":");
        for (String part : parts) {
            if ("circle".equals(part)) {
                return new CircleHint(parts);
            } else if ("para".equals(part)) {
                return new ParaHint(parts);
            }
        }
        return new TextHint(parts);
    }

}
