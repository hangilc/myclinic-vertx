package dev.myclinic.vertx.drawer;

import java.util.List;

/**
 * Created by hangil on 2017/05/14.
 */
public class OpDrawChars extends Op {

    private final String chars;
    private final List<Double> xs;
    private final List<Double> ys;

    public OpDrawChars(String chars, List<Double> xs, List<Double> ys){
        super(OpCode.DrawChars);
        this.chars = chars;
        this.xs = xs;
        this.ys = ys;
    }

    public String getChars() {
        return chars;
    }

    public List<Double> getXs() {
        return xs;
    }

    public List<Double> getYs() {
        return ys;
    }
}
