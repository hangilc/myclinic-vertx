package dev.myclinic.vertx.drawerform.shujiiform;

import dev.myclinic.vertx.drawer.Op;
import dev.myclinic.vertx.drawerform.Box;
import dev.myclinic.vertx.drawerform.FormCompiler;
import dev.myclinic.vertx.drawerform.HAlign;
import dev.myclinic.vertx.drawerform.VAlign;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShujiiForm {

    private final FormCompiler c = new FormCompiler();
    private final Map<String, Box> marks = new HashMap<>();

    public ShujiiForm(){
        c.createFont("small", "MS Gothic", 3);
        c.createFont("regular", "MS Gothic", 4);
    }

    public FormCompiler getCompiler(){
        return c;
    }

    public List<Op> render(ShujiiData data){
        leftBox(new Box(40, 59-15, 40+97, 59));
        rightBox(new Box(210-56, 56.5-10, 210-13, 56));
        detail(new Box(12.5, 150, 210-11, 380));
        c.createPen("regular", 0, 0, 0, 0.2);
        c.setPen("regular");
        c.box(new Box(40, 59-15, 40+97, 59));
        c.box(marks.get("doctorName"));
        if( data != null ){
            writeSmall("doctorName", data.doctorName);
            writeSmall("clinicName", data.clinicName);
            writeSmall("clinicAddress", data.clinicAddress);
            writeSmall("phone", data.phone);
            writeSmall("fax", data.fax);
            writeRegularPara("detail", data.detail);
        }
        return c.getOps();
    }

    private void writeSmall(String mark, String value){
        if( value != null && !value.isEmpty() ){
            c.setFont("small");
            Box box = marks.get(mark);
            c.textAt(value, box.getLeft(), box.cy(), HAlign.Left, VAlign.Center);
        }
    }

    private void writeRegularPara(String mark, String value){
        if( value != null && !value.isEmpty() ){
            c.setFont("regular");
            Box box = marks.get(mark);
            c.paraIn(value, box);
        }
    }

    private void leftBox(Box box){
        List<Box> rows = box.splitToEvenRows(3);
        marks.put("doctorName", rows.get(0));
        marks.put("clinicName", rows.get(1));
        marks.put("clinicAddress", rows.get(2));
    }

    private void rightBox(Box box){
        List<Box> rows = box.splitToEvenRows(2);
        marks.put("phone", rows.get(0));
        marks.put("fax", rows.get(1));
    }

    private void detail(Box box){
        marks.put("detail", box);
    }

}
