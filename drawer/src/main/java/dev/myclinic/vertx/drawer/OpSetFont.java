package dev.myclinic.vertx.drawer;

/**
 * Created by hangil on 2017/05/14.
 */
public class OpSetFont extends Op {

    private final String name;

    public OpSetFont(String name){
        super(OpCode.SetFont);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
