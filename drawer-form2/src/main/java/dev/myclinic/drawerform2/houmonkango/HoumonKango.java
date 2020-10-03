package dev.myclinic.drawerform2.houmonkango;

import dev.myclinic.drawerform2.FormCompiler;
import dev.myclinic.drawerform2.Hints;
import dev.myclinic.vertx.drawer.Box;
import dev.myclinic.vertx.drawer.DrawerConsts;
import dev.myclinic.vertx.drawer.PaperSize;

import java.util.List;

import static dev.myclinic.vertx.drawer.Box.HorizAnchor;
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
        subtitle2(29);
        Box mainBox = paperBox.innerBox(
                16,
                33,
                paperBox.getWidth() - 19,
                paperBox.getHeight() - 56
        );
        c.box(mainBox);
        Box[] rows = mainBox.splitToRows(
                8.5,
                17,
                27.5,
                117.5,
                mainBox.getHeight() - 75,
                mainBox.getHeight() - 52,
                mainBox.getHeight() - 42,
                mainBox.getHeight() - 32.5,
                mainBox.getHeight() - 18.5
        );
        for (int i = 0; i < rows.length - 1; i++) {
            c.frameBottom(rows[i]);
        }
        renderRow0(rows[0]);
        renderRow1(rows[1]);
        renderRow2(rows[2]);
        renderRow3(rows[3]);
        renderRow4(rows[4]);
        renderRow5(rows[5]);
        renderRow6(rows[6]);
        renderRow7(rows[7]);
        renderRow8(rows[8]);
        renderRow9(rows[9]);
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

    private void subtitle2(double y) {
        double x = 113;
        c.textAt("点滴注射指示期間", x, y, HAlign.Right, VAlign.Top);
        c.multiAt(x, y, VAlign.Top, List.of(
                c.mLabel("（令和"),
                c.mSpace(7).mark("subtitle2.from.nen").right().rightPadding(0.5),
                c.mLabel("年"),
                c.mSpace(4.5).mark("subtitle2.from.month").right().rightPadding(0.5),
                c.mLabel("月"),
                c.mSpace(4.5).mark("subtitle2.from.day").right().rightPadding(0.5),
                c.mLabel("日～令和"),
                c.mSpace(7).mark("subtitle2.upto.nen").right().rightPadding(0.5),
                c.mLabel("年"),
                c.mSpace(4.5).mark("subtitle2.upto.month").right().rightPadding(0.5),
                c.mLabel("月"),
                c.mSpace(4.5).mark("subtitle2.upto.day").right().rightPadding(0.5),
                c.mLabel("日）")
        ));
    }

    private void renderRow0(Box row) {
        Box[] cols = row.splitToColumns(20, 83.5);
        for (int i = 0; i < cols.length - 1; i++) {
            c.frameRight(cols[i]);
        }
        {
            c.textIn(
                    "患者氏名",
                    cols[0].shrinkWidth(1.5, HorizAnchor.Right),
                    HAlign.Left,
                    VAlign.Center
            );
        }
        {
            Box[] cc = cols[1].splitToColumns(55.5);
            c.textIn("様", cc[1], HAlign.Left, VAlign.Center);
            c.addMarkAndHints("shimei", cc[0], List.of(new Hints.Center()));
        }
        {
            Box[] rr = cols[2].splitToEvenRows(2);
            Box b = c.textIn(
                    "生年月日",
                    rr[0].shrinkWidth(1.5, HorizAnchor.Right),
                    HAlign.Left,
                    VAlign.Center
            );
            b = c.textIn("明", b.flipRight().shift(3, 0), HAlign.Left, VAlign.Center);
            c.addMarkAndHints("birthday.gengou.meiji", b, List.of(Hints.circle(), Hints.radius(1.5)));
            b = c.textIn("・", b.flipRight(), HAlign.Left, VAlign.Center);
            b = c.textIn("大", b.flipRight(), HAlign.Left, VAlign.Center);
            c.addMarkAndHints("birthday.gengou.taishou", b, List.of(Hints.circle(), Hints.radius(1.5)));
            b = c.textIn("・", b.flipRight(), HAlign.Left, VAlign.Center);
            b = c.textIn("昭", b.flipRight(), HAlign.Left, VAlign.Center);
            c.addMarkAndHints("birthday.gengou.shouwa", b, List.of(Hints.circle(), Hints.radius(1.5)));
            b = c.textIn("・", b.flipRight(), HAlign.Left, VAlign.Center);
            b = c.textIn("平", b.flipRight(), HAlign.Left, VAlign.Center);
            c.addMarkAndHints("birthday.gengou.heisei", b, List.of(Hints.circle(), Hints.radius(1.5)));
            b = c.multi(
                    b.flipRight(),
                    VAlign.Center,
                    List.of(
                            c.mSpace(8).mark("birthday.nen").right().rightPadding(0.5),
                            c.mLabel("年"),
                            c.mSpace(8).mark("birthday.month").right().rightPadding(0.5),
                            c.mLabel("月"),
                            c.mSpace(8).mark("birthday.day").right().rightPadding(0.5),
                            c.mLabel("日")
                    ));

            c.multi(
                    rr[1].shrinkWidth(63, HorizAnchor.Right),
                    VAlign.Center,
                    List.of(
                            c.mLabel("（"),
                            c.mSpace(6).mark("age").right().rightPadding(0.5),
                            c.mLabel("歳）")
                    ));
        }
    }

    private void renderRow1(Box row) {

    }

    private void renderRow2(Box row) {

    }

    private void renderRow3(Box row) {

    }

    private void renderRow4(Box row) {

    }

    private void renderRow5(Box row) {

    }

    private void renderRow6(Box row) {

    }

    private void renderRow7(Box row) {

    }

    private void renderRow8(Box row) {

    }

    private void renderRow9(Box row) {

    }

}
