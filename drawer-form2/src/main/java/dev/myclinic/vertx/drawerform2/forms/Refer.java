package dev.myclinic.vertx.drawerform2.forms;

import dev.myclinic.vertx.drawer.Box;
import dev.myclinic.vertx.drawer.DrawerCompiler;
import dev.myclinic.vertx.drawer.PaperSize;
import dev.myclinic.vertx.drawer.Point;
import dev.myclinic.vertx.drawer.form.Form;
import dev.myclinic.vertx.drawer.form.Page;
import dev.myclinic.vertx.drawer.printer.PrinterConsts;
import dev.myclinic.vertx.drawerform2.FormCompiler;
import dev.myclinic.vertx.drawerform2.Hints;

import java.util.List;

public class Refer {

    private final FormCompiler c = new FormCompiler();
    private final PaperSize paper = PaperSize.A4;
    private final Box paperBox = new Box(0, 0, paper.getWidth(), paper.getHeight());
    private final Box contentBox = new Box(30, 103, 170, 210);
    private final Point titlePoint = new Point(paperBox.getCx(), 41);
    private final Point referHospitalPoint = new Point(30, 58);
    private final Point referDoctorPoint = new Point(30, 58+6);
    private final Point patientNamePoint = new Point(30, 80);
    private final Point patientInfoPoint = new Point(50, 86);
    private final Point diagnosisPoint = new Point(30, 96);
    private final Point issueDatePoint = new Point(30, 220);
    private final Point addressPoint = new Point(118, 220);

    public Form createForm(){
        Form form = new Form();
        form.paper = "A4";
        setupFonts();
        form.setup = c.getOps();
        c.clearOps();
        renderTitle();
        Page page = new Page();
        page.name = "単ページ紹介状";
        page.ops = c.getOps();
        page.marks = c.getMarks();
        page.hints = c.getHints();
        form.pages = List.of(page);
        return form;
    }

    private void setupFonts(){
        c.createFont("serif-6", "MS Mincho", 6);
        c.createFont("serif-5", "MS Mincho", 5);
        c.createFont("serif-5-bold", "MS Mincho", 5, PrinterConsts.FW_BOLD, false);
        c.createFont("serif-4", "MS Mincho", 4);
    }

    private void renderTitle(){
        Point p = titlePoint;
        String font = "serif-5-bold";
        double fontSize = c.getFontSizeFor(font);
        Box box = new Box(p.getX(), p.getY(), p.getX(), p.getY());
        c.addMark("title", "タイトル", box,
                List.of(Hints.center(), Hints.vCenter(), Hints.font(font), Hints.spacing(5)));
    }

}
