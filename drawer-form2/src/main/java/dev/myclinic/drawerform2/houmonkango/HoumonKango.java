package dev.myclinic.drawerform2.houmonkango;

import dev.myclinic.drawerform2.FormCompiler;
import dev.myclinic.vertx.drawer.Box;
import dev.myclinic.vertx.drawer.DrawerConsts;
import dev.myclinic.vertx.drawer.PaperSize;
import static dev.myclinic.vertx.drawer.DrawerCompiler.*;
import java.util.HashMap;

import static dev.myclinic.vertx.drawer.Render.*;

public class HoumonKango {

    private FormCompiler c = new FormCompiler();

    public Form createForm(){
        Form form = new Form();
        form.page = "A4";
        form.marks = new HashMap<>();
        form.hints = new HashMap<>();
        Box paperBox = new Box(PaperSize.A4);
        setupFonts();
        drawTitle(paperBox);
        c.setFont("regular");
        form.form = c.getOps();
        return form;
    }

    private void setupFonts(){
        c.createFont("title", "MS Mincho", 5, DrawerConsts.FontWeightBold, false);
        c.createFont("regular", "MS Mincho", 3.5);
        c.createFont("small", "MS Mincho", 3);
        c.createFont("input-regular", "MS Gothic", 3.5);
    }

    private void drawTitle(Box paperBox){
        c.setFont("title");
        c.textAt("介護予防訪問看護・訪問看護指示書", paperBox.getCx(), 13, HAlign.Center, VAlign.Top);
        c.textAt("在宅患者訪問点滴注射指示書", paperBox.getCx(), 18, HAlign.Center, VAlign.Top);
    }

}
