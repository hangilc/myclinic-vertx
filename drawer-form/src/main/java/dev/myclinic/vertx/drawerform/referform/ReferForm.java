package dev.myclinic.vertx.drawerform.referform;

import dev.myclinic.vertx.drawer.Op;
import dev.myclinic.vertx.drawer.printer.PrinterConsts;
import dev.myclinic.vertx.drawerform.FormCompiler;

import java.util.List;

public class ReferForm {

    private final FormCompiler c = new FormCompiler();

    public ReferForm() {
        c.createFont("serif-6", "MS Mincho", 6);
        c.createFont("serif-5", "MS Mincho", 5);
        c.createFont("serif-5-bold", "MS Mincho", 5, PrinterConsts.FW_BOLD, false);
        c.createFont("serif-4", "MS Mincho", 4);
    }

    public FormCompiler getCompiler(){
        return c;
    }

    public List<Op> render(ReferData data){
        return c.getOps();
    }

}
