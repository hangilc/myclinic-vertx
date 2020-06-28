package dev.myclinic.vertx.drawerform.referform;

import dev.myclinic.vertx.drawer.Op;
import dev.myclinic.vertx.drawer.printer.PrinterConsts;
import dev.myclinic.vertx.drawerform.*;

import java.util.List;

public class ReferForm {

    private final FormCompiler c = new FormCompiler();
    private final Paper paper = Paper.A4;
    private final Box paperBox = new Box(0, 0, paper.getWidth(), paper.getHeight());
    private final Box contentBox = new Box(30, 103, 170, 210);
    private final Point titlePoint = new Point(paperBox.cx(), 41);
    private final Point referHospitalPoint = new Point(30, 58);
    private final Point referDoctorPoint = new Point(30, 58+6);
    private final Point patientNamePoint = new Point(30, 80);
    private final Point patientInfoPoint = new Point(50, 86);
    private final Point diagnosisPoint = new Point(30, 96);
    private final Point issueDatePoint = new Point(30, 220);
    private final Point addressPoint = new Point(118, 220);

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
        if( data != null ){
            if( data.title != null ){
                renderTitle(data.title);
            }
            if( data.referHospital != null ){
                renderReferHospital(data.referHospital);
            }
            if( data.referDoctor != null ){
                renderReferDoctor(data.referDoctor);
            }
            if( data.patientName != null ){
                renderPatientName(data.patientName);
            }
            if( data.patientInfo != null ){
                renderPatientInfo(data.patientInfo);
            }
            if( data.diagnosis != null ){
                renderDiagnosis(data.diagnosis);
            }
            if( data.issueDate != null ){
                renderIssueDate(data.issueDate);
            }
            renderContent(data.content);
            renderAddress(data.clinicPostalCode, data.clinicAddress, data.clinicPhone, data.clinicFax,
                    data.clinicName, data.doctorName);
        }
        return c.getOps();
    }

    private void renderTitle(String title){
        Point p = titlePoint;
        c.setFont("serif-5-bold");
        c.textAt(title, p.getX(), p.getY(), HAlign.Center, VAlign.Center,
                mes -> FormCompiler.addExtraSpaces(mes, 5));
    }

    private void renderReferHospital(String name){
        Point p = referHospitalPoint;
        c.setFont("serif-4");
        c.textAt(name, p.getX(), p.getY(), HAlign.Left, VAlign.Bottom);
    }

    public void renderReferDoctor(String text){
        Point p = referDoctorPoint;
        c.setFont("serif-4");
        c.textAt(text, p.getX(), p.getY(), HAlign.Left, VAlign.Bottom);
    }

    public void renderPatientName(String name){
        Point p = patientNamePoint;
        c.setFont("serif-5");
        c.textAt(name, p.getX(), p.getY(), HAlign.Left, VAlign.Bottom);
    }

    public void renderPatientInfo(String text){
        Point p = patientInfoPoint;
        c.setFont("serif-4");
        c.textAt(text, p.getX(), p.getY(), HAlign.Left, VAlign.Bottom);
    }

    public void renderDiagnosis(String text){
        Point p = diagnosisPoint;
        c.setFont("serif-5");
        c.textAt(text, p.getX(), p.getY(), HAlign.Left, VAlign.Bottom);
    }

    public void renderIssueDate(String text){
        Point p = issueDatePoint;
        c.setFont("serif-4");
        c.textAt(text, p.getX(), p.getY(), HAlign.Left, VAlign.Bottom);
    }

    public void renderAddress(String addr1, String addr2, String addr3, String addr4,
                              String clinicName, String doctorName){
        Point p = addressPoint;
        c.setFont("serif-4");
        double x = p.getX();
        double y = p.getY() + 4;
        double lineHeight = 4 + 2;
        c.textAt(addr1, x, y, HAlign.Left, VAlign.Bottom);
        y += lineHeight;
        c.textAt(addr2, x, y, HAlign.Left, VAlign.Bottom);
        y += lineHeight;
        c.textAt(addr3, x, y, HAlign.Left, VAlign.Bottom);
        y += lineHeight;
        c.textAt(addr4, x, y, HAlign.Left, VAlign.Bottom);
        y += lineHeight;
        y += 4;
        c.textAt(clinicName, x, y, HAlign.Left, VAlign.Bottom);
        y += lineHeight;
        String txt = "院長";
        Box mes = c.textAt(txt, x, y, HAlign.Left, VAlign.Center);
        x = mes.getRight() + 4;
        c.setFont("serif-6");
        mes = c.textAt(doctorName, x, y, HAlign.Left, VAlign.Center);
        x = mes.getRight() + 8;
        c.setFont("serif-4");
        c.textAt("㊞", x, y, HAlign.Left, VAlign.Center);
    }

    public void renderContent(String content){
        Box box = contentBox;
        c.setFont("serif-4");
        c.paraIn(content, box);
    }

}
