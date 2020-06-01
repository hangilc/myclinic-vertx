package dev.myclinic.vertx.drawer;

/**
 * Created by hangil on 2017/05/14.
 */
public class OpCreateFont extends Op {

    private final String name;
    private final String fontName;
    private final double size;
    private final int weight;
    private final  boolean italic;

    public OpCreateFont(String name, String fontName, double size, int weight, boolean italic){
        super(OpCode.CreateFont);
        this.name = name;
        this.fontName = fontName;
        this.size = size;
        this.weight = weight;
        this.italic = italic;
    }

    public String getName() {
        return name;
    }

    public String getFontName() {
        return fontName;
    }

    public double getSize() {
        return size;
    }

    public int getWeight() {
        return weight;
    }

    public boolean isItalic() {
        return italic;
    }

}
