package dev.myclinic.vertx.drawer;

/**
 * Created by hangil on 2017/05/14.
 */
public class OpSetPen extends Op {

    private final String name;

    public OpSetPen(String name){
        super(OpCode.SetPen);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
