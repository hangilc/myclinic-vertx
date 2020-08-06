package dev.myclinic.vertx.drawerform.medcert;

import dev.myclinic.vertx.drawer.Op;
import dev.myclinic.vertx.drawerform.*;

import java.util.List;

public class MedCertForm {

    private FormCompiler c = new FormCompiler();

    public MedCertForm() {
    }

    public List<Op> render(MedCertData data){
        Paper paper = Paper.A4;
        Box pageBox = new Box(0, 0, paper.getWidth(), paper.getHeight());
        c.createFont("title", "MS Mincho", 5.2, FormCompiler.Bold, false);
        c.createFont("regular", "MS Mincho", 4);
        c.createFont("regular-bold", "MS Mincho", 4, FormCompiler.Bold, false);
        c.createFont("large", "MS Mincho", 4.8);
        Point titleCenter = new Point(105, 42);
        c.setFont("title");
        c.textAt("診断書", titleCenter.getX(), titleCenter.getY(),
                HAlign.Center, VAlign.Center, FormCompiler.spaceAdder(10));
        double y = 58;
        c.setFont("regular");
        c.textAt("氏名", 62, y, HAlign.Left, VAlign.Center);
        c.setFont("large");
        c.textAt(data.patientName, 76, y, HAlign.Left, VAlign.Center);
        y = 63.5;
        c.setFont("regular");
        c.textAt(data.birthDate, 120, y, HAlign.Left, VAlign.Center);
        double x = 30;
        y = 76.0;
        c.setFont("regular-bold");
        c.textAt("診断名", x, y, HAlign.Left, VAlign.Center, FormCompiler.spaceAdder(4));
        c.setFont("large");
        c.textAt(data.diagnosis, 60, y, HAlign.Left, VAlign.Center);
        c.setFont("regular");
        Box textBox = new Box(x, 92, 176, 120);
        c.paraIn(data.text, textBox, 1);
        c.textAt("上記の通り診断する。", x, 133, HAlign.Left, VAlign.Center);
        c.textAt(data.issueDate, 112, 146, HAlign.Left, VAlign.Center);
        double xx = 120;
        y = 159;
        c.textAt(data.postalCode, xx, y, HAlign.Left, VAlign.Center);
        y +=  6;
        c.textAt(data.address, xx, y, HAlign.Left, VAlign.Center);
        y +=  6;
        c.textAt(data.phone, xx, y, HAlign.Left, VAlign.Center);
        y +=  6;
        c.textAt(data.fax, xx, y, HAlign.Left, VAlign.Center);
        y +=  6;
        c.textAt(data.clinicName, xx, y, HAlign.Left, VAlign.Center);
        y +=  9;
        c.textAt("医師", xx, y, HAlign.Left, VAlign.Center);
        return c.getOps();
    }

}
