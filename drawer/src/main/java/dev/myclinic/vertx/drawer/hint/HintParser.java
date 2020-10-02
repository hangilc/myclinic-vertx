package dev.myclinic.vertx.drawer.hint;

public class HintParser {

    public static Hint parse(String hintSrc){
        String[] parts = hintSrc.split(":");
        for(String part: parts){
            if( part.equals("circle") ){
                return new CircleHint(parts);
            }
        }
        return new TextHint(parts);

    }

}
