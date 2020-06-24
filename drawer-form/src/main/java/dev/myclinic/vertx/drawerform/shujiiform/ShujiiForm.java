package dev.myclinic.vertx.drawerform.shujiiform;

import dev.myclinic.vertx.drawer.Op;
import dev.myclinic.vertx.drawerform.Box;
import dev.myclinic.vertx.drawerform.Compiler;

import java.util.List;

public class ShujiiForm {

    private final Compiler c;
    private final ShujiiData data;

    public ShujiiForm(ShujiiData data){
        this.c = new Compiler();
        this.data = data;
    }

    public List<Op> render(){
        leftBox(new Box(40, 59-15, 40+95, 59));
        return c.getPages().get(0);
    }

    private void leftBox(Box box){
        List<Box> rows = box.splitToEvenRows(3);

    }

}
