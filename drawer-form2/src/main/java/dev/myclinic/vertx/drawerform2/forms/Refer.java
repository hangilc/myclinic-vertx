package dev.myclinic.vertx.drawerform2.forms;

import dev.myclinic.vertx.drawer.Box;
import dev.myclinic.vertx.drawer.PaperSize;
import dev.myclinic.vertx.drawer.Point;
import dev.myclinic.vertx.drawer.form.Form;
import dev.myclinic.vertx.drawer.form.Page;
import dev.myclinic.vertx.drawer.printer.PrinterConsts;
import dev.myclinic.vertx.drawerform2.FormCompiler;
import dev.myclinic.vertx.drawerform2.Hints;
import static dev.myclinic.vertx.drawer.DrawerCompiler.*;

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
        markTitle();
        markReferHospital();
        markReferDoctor();
        markPatientName();
        markPatientInfo();
        markDiagnosis();
        markIssueDate();
        renderAddress();
        markContent();
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

    private void markTitle(){
        Point p = titlePoint;
        String font = "serif-5-bold";
        Box box = new Box(p.getX(), p.getY(), p.getX(), p.getY());
        c.addMark("title", "タイトル", box,
                List.of(Hints.center(), Hints.vCenter(), Hints.font(font), Hints.spacing(5)));
    }

    private void markAt(Point p, String font, String mark, String description){
        markAt(p.getX(), p.getY(), font, mark, description);
    }

    private void markAt(double x, double y, String font, String mark, String description){
        Box box = new Box(x, y, x, y);
        c.addMark(mark, description, box, List.of(Hints.vBottom(), Hints.font(font)));
    }

    private void markReferHospital(){
        markAt(referHospitalPoint, "serif-4", "refer-hospital", "紹介先医療機関");
    }

    private void markReferDoctor(){
        markAt(referDoctorPoint, "serif-4", "refer-doctor", "紹介先医師名");
    }

    private void markPatientName(){
        markAt(patientNamePoint, "serif-5", "patient-name", "患者氏名");
    }

    private void markPatientInfo(){
        markAt(patientInfoPoint, "serif-4", "patient-info", "患者情報");
    }

    private void markDiagnosis(){
        markAt(diagnosisPoint, "serif-5", "diagnosis", "診断名");
    }

    private void markIssueDate(){
        markAt(issueDatePoint, "serif-4", "issue-date", "発行日");
    }

    private void renderAddress(){
        Point p = addressPoint;
        String font = "serif-4";
        double fontSize = c.getFontSizeFor(font);
        double x = p.getX();
        double y = p.getY() + 4;
        double lineHeight = 4 + 2;
        markAt(x, y, font, "address-1", "住所（１行目）");
        y += lineHeight;
        markAt(x, y, font, "address-2", "住所（２行目）");
        y += lineHeight;
        markAt(x, y, font, "address-3", "住所（３行目）");
        y += lineHeight;
        markAt(x, y, font, "address-4", "住所（４行目）");
        y += lineHeight;
        y += 4;
        markAt(x, y, font, "clinic-name", "発行医療機関名");
        y += lineHeight;
        c.setFont("serif-4");
        Box box = new Box(x, y, x, y);
        c.multi(box, VAlign.Center, List.of(
                c.mLabel("院長"),
                c.mSpace(30).mark("doctor-name", "発行医師氏名").addHints(
                        Hints.font("serif-6"), Hints.leftPadding(3)),
                c.mSpace(12),
                c.mLabel("㊞").mark("stamp", "印鑑")
        ));
    }

    private void markContent(){
        c.addMark("content", "内容", contentBox, List.of(Hints.para(), Hints.font("serif-4")));
    }

}
