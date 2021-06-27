package dev.myclinic.vertx.drawerform2.forms;

import dev.myclinic.vertx.drawer.Box;
import dev.myclinic.vertx.drawer.DrawerCompiler;
import static dev.myclinic.vertx.drawer.DrawerCompiler.*;
import dev.myclinic.vertx.drawer.PaperSize;
import dev.myclinic.vertx.drawer.Point;
import dev.myclinic.vertx.drawer.form.Form;
import dev.myclinic.vertx.drawer.form.Page;
import dev.myclinic.vertx.drawerform2.FormCompiler;
import dev.myclinic.vertx.drawerform2.Hints;

import java.util.Collections;
import java.util.List;

public class CovidVac2ndShot {

    public static Form createForm() {
        return new CovidVac2ndShot().create();
    }

    private FormCompiler c = new FormCompiler();
    private PaperSize paperSize;

    private CovidVac2ndShot() {

    }

    private Form create() {
        Form form = new Form();
        form.paper = "A5";
        this.paperSize = PaperSize.resolvePaperSize(form.paper);
        setupFonts();
        form.setup = c.getOps();
        c.clearOps();
        form.pages = Collections.singletonList(page());
        return form;
    }

    private void setupFonts() {
        c.createFont("regular", "serif", 4);
        c.createFont("title", "serif", 6);
    }

    private Page page() {
        Page page = new Page();
        Point p = new Point(paperSize.getWidth() / 2.0, 41);
        Box box = new Box(p.getX(), p.getY(), p.getX(), p.getY());
        c.setFont("title");
        c.textAt("コロナワクチン２回目接種の日時", p.getX(), p.getY(), HAlign.Center, VAlign.Center);
        c.setFont("regular");
        c.addMark("patient", "患者氏名", new Box(30, 60, 30, 60), List.of(Hints.vCenter(), Hints.font("regular")));
        c.multi(new Box(30, 85, 30, 85), VAlign.Center, List.of(
                c.mLabel("接種日時："),
                c.mSpace(70).mark("at", "接種日時")
        ));
        double x = 30;
        double y = 111;
        c.addMark("postal-code", "郵便番号", new Box(x, y, x, y), List.of(Hints.vCenter(), Hints.font("regular")));
        y += 13;
        c.addMark("address", "住所", new Box(x, y, x, y), List.of(Hints.vCenter(), Hints.font("regular")));
        y += 13;
        c.addMark("phone", "電話番号", new Box(x, y, x, y), List.of(Hints.vCenter(), Hints.font("regular")));
        y += 13;
        c.addMark("clinic-name", "医院名", new Box(x, y, x, y), List.of(Hints.vCenter(), Hints.font("regular")));
        y += 13;
        c.addMark("doctor", "医師名", new Box(x, y, x, y), List.of(Hints.vCenter(), Hints.font("regular")));
        page.ops = c.getOps();
        page.marks = c.getMarks();
        page.hints = c.getHints();
        page.descriptions = c.getDescriptions();
        return page;
    }

}