package dev.myclinic.vertx.drawerform.textform;

import dev.myclinic.vertx.drawer.Op;
import dev.myclinic.vertx.drawerform.Box;
import dev.myclinic.vertx.drawerform.FormCompiler;
import dev.myclinic.vertx.drawerform.Paper;

import java.util.Collections;
import java.util.List;

public class TextForm {

    FormCompiler c = new FormCompiler();

    public List<List<Op>> render(TextData data){
        Paper paper = Paper.A4;
        Box pageBox = new Box(0, 0, paper.getWidth(), paper.getHeight());
        c.createFont("regular", "MS Mincho", 4.0);
        c.setFont("regular");
        c.paraIn(data.text, pageBox);
        return Collections.singletonList(c.getOps());
    }

}
