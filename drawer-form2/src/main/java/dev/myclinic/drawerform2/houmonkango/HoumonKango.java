package dev.myclinic.drawerform2.houmonkango;

import dev.myclinic.drawerform2.FormCompiler;
import dev.myclinic.vertx.drawer.Box;
import dev.myclinic.vertx.drawer.DrawerConsts;
import dev.myclinic.vertx.drawer.PaperSize;

import java.util.HashMap;
import java.util.List;

import static dev.myclinic.vertx.drawer.DrawerCompiler.HAlign;
import static dev.myclinic.vertx.drawer.DrawerCompiler.VAlign;
import static dev.myclinic.vertx.drawer.Render.Form;

public class HoumonKango {

    private FormCompiler c = new FormCompiler();

    public Form createForm() {
        Form form = new Form();
        Box paperBox = new Box(PaperSize.A4);
        setupFonts();
        drawTitle(paperBox);
        c.setFont("regular");
        subtitle1(24);
        form.page = "A4";
        form.marks = c.getMarks();
        form.hints = c.getHints();
        form.form = c.getOps();
        return form;
    }

    private void setupFonts() {
        c.createFont("title", "MS Mincho", 5, DrawerConsts.FontWeightBold, false);
        c.createFont("regular", "MS Mincho", 3.5);
        c.createFont("small", "MS Mincho", 3);
        c.createFont("input-regular", "MS Gothic", 3.5);
    }

    private void drawTitle(Box paperBox) {
        c.setFont("title");
        c.textAt("介護予防訪問看護・訪問看護指示書", paperBox.getCx(), 13, HAlign.Center, VAlign.Top);
        c.textAt("在宅患者訪問点滴注射指示書", paperBox.getCx(), 18, HAlign.Center, VAlign.Top);
    }

    private void subtitle1(double y) {
        double x = 113;
        c.textAt("訪問看護指示期間", x, y, HAlign.Right, VAlign.Top);
        c.multiAt(x, y, VAlign.Top, List.of(
                c.mLabel("（令和"),
                c.mSpace(7).mark("subtitle1.from.nen").right().rightPadding(0.5),
                c.mLabel("年"),
                c.mSpace(4.5).mark("subtitle1.from.month").right().rightPadding(0.5),
                c.mLabel("月"),
                c.mSpace(4.5).mark("subtitle1.from.day").right().rightPadding(0.5),
                c.mLabel("日～令和"),
                c.mSpace(7).mark("subtitle1.upto.nen").right().rightPadding(0.5),
                c.mLabel("年"),
                c.mSpace(4.5).mark("subtitle1.upto.month").right().rightPadding(0.5),
                c.mLabel("月"),
                c.mSpace(4.5).mark("subtitle1.upto.day").right().rightPadding(0.5),
                c.mLabel("日）")
        ));
    }

}
