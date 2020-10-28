package dev.myclinic.vertx.drawerform.receipt;

import dev.myclinic.vertx.drawer.Box;
import dev.myclinic.vertx.drawer.Box.HorizAnchor;
import dev.myclinic.vertx.drawer.Box.VertAnchor;
import dev.myclinic.vertx.drawer.DrawerCompiler;
import dev.myclinic.vertx.drawer.DrawerCompiler.HAlign;
import dev.myclinic.vertx.drawer.DrawerCompiler.VAlign;
import dev.myclinic.vertx.drawer.Op;
import dev.myclinic.vertx.drawer.PaperSize;

import java.util.List;

public class ReceiptDrawer {

    private DrawerCompiler compiler = new DrawerCompiler();
    private String NAME_SMALLER_FONT = "name-smaller-font";
    private String NAME_MULTILINE_FONT = "name-multiline-font";

    public ReceiptDrawer(ReceiptDrawerData data){
        setupFonts();
        compiler.createPen("regular", 0, 0, 0, 0.1);
        compiler.setPen("regular");
        //Box frameBox = new Box(0, 0, 148, 105);
        Box frameBox = new Box(PaperSize.A6_Landscape);
        Box titleBox = frameBox.shiftDown(4).setWidth(28, HorizAnchor.Center)
                .setHeight(6, VertAnchor.Top);
        Box row1 = frameBox.innerBox(13, 14, 73, 23);
        Box row2 = frameBox.shiftDown(row1.getBottom()+3)
                .setHeight(4, VertAnchor.Top).setLeft(13).setWidth(60, HorizAnchor.Left);
        Box row3 = frameBox.setTop(row2.getBottom()+3)
                .setHeight(10, VertAnchor.Top).setLeft(13).setWidth(120, HorizAnchor.Left);
        Box row4 = frameBox.setTop(row3.getBottom()+3)
                .setHeight(10, VertAnchor.Top).setLeft(13).setWidth(120, HorizAnchor.Left);
        Box row5 = frameBox.setTop(row4.getBottom()+1)
                .setHeight(10, VertAnchor.Top).setLeft(13).setWidth(120, HorizAnchor.Left);
        Box hokengaiBox = frameBox.setTop(row5.getBottom()+3)
                .setHeight(25, VertAnchor.Top).setLeft(13).setWidth(48, HorizAnchor.Left);
        Box instituteBox = hokengaiBox.flipRight().shiftToRight(11)
                .setHeight(25, VertAnchor.Top).setWidth(30, HorizAnchor.Left);
        Box ryoushuuBox = instituteBox.flipRight().shiftToRight(7)
                .setHeight(29, VertAnchor.Top).setWidth(24, HorizAnchor.Left);
        mainTitle(titleBox);
        renderRow1(row1, data.getPatientName(), data.getCharge());
        renderRow2(row2, data.getVisitDate(), data.getIssueDate());
        renderRow3(row3, data.getPatientId(), data.getHoken(), data.getFutanWari());
        renderRow4(row4, data.getShoshin(), data.getKanri(), data.getZaitaku(), data.getKensa(), data.getGazou());
        renderRow5(row5, data.getTouyaku(), data.getChuusha(), data.getShochi(), data.getSonota(), data.getSouten());
        renderHokengai(hokengaiBox, data.getHokengai());
        renderInstitute(instituteBox, data.getClinicName(), data.getAddressLines());
        renderRyoushuu(ryoushuuBox);
    }

    public List<Op> getOps(){
        return compiler.getOps();
    }

    private void setupFonts(){
        compiler.createFont("mincho-6", "MS Mincho", 6);
        compiler.createFont("mincho-4", "MS Mincho", 4);
        compiler.createFont("gothic-5", "MS Gothic", 5);
        compiler.createFont("gothic-4", "MS Gothic", 4);
        compiler.createFont("gothic-2.6", "MS Gothic", 2.6);
        compiler.createFont(NAME_SMALLER_FONT, "MS Mincho", 4.2);
        compiler.createFont(NAME_MULTILINE_FONT, "MS Mincho", 3);
    }

    private void mainTitle(Box box){
        compiler.setFont("mincho-6");
        compiler.textInJustified("領収証", box, VAlign.Top);
    }

    private void renderRow1(Box box, String name, String charge){
        compiler.setFont("mincho-6");
        compiler.frameBottom(box);
        compiler.textIn("様", box, HAlign.Right, VAlign.Bottom);
        Box nameBox = box.shrinkWidth(8, HorizAnchor.Left);
        {
            //compiler.textIn(name, nameBox, HAlign.Center, VAlign.Bottom);
            DrawerCompiler.TextInBoundedOptions opts = new DrawerCompiler.TextInBoundedOptions();
            opts.smallerFonts = List.of(NAME_SMALLER_FONT);
            opts.multilineFont = NAME_MULTILINE_FONT;
            opts.multilineHAlign = HAlign.Left;
            compiler.textInBounded(name, nameBox, HAlign.Center, VAlign.Bottom, opts);
        }
        Box chargeBox = box.flipRight().shiftToRight(8).setWidth(52, HorizAnchor.Left);
        compiler.textIn("領収金額", chargeBox, HAlign.Left, VAlign.Bottom);
        compiler.textIn("円", chargeBox, HAlign.Right, VAlign.Bottom);
        compiler.frameBottom(chargeBox);
        Box kingakuBox = chargeBox.displaceLeftEdge(24).displaceRightEdge(-6.9);
        compiler.setFont("gothic-5");
        compiler.textIn(charge, kingakuBox, HAlign.Right, VAlign.Bottom);
    }

    private void renderRow2(Box box, String visitDate, String issueDate){
        compiler.setFont("mincho-4");
        compiler.textIn("診察日", box, HAlign.Left, VAlign.Center);
        Box dateBox = box.shrinkWidth(16, HorizAnchor.Right);
        compiler.textIn(visitDate, dateBox, HAlign.Left, VAlign.Center);
        Box issueBox = box.flipRight().shiftToRight(6);
        compiler.textIn("発効日", issueBox, HAlign.Left, VAlign.Center);
        compiler.textIn(issueDate, issueBox.displaceLeftEdge(16), HAlign.Left, VAlign.Center);
    }

    private void renderRow3(Box box, String patientId, String hoken, String futanWari){
        Box[][] cells = box.splitToEvenCells(2, 3);
        compiler.frameCells(cells);
        compiler.setFont("mincho-4");
        compiler.textIn("患者番号", cells[0][0], HAlign.Center, VAlign.Center);
        compiler.textIn("保険種別", cells[0][1], HAlign.Center, VAlign.Center);
        compiler.textIn("負担割合", cells[0][2], HAlign.Center, VAlign.Center);
        compiler.textIn( patientId, cells[1][0], HAlign.Center, VAlign.Center);
        compiler.textIn( hoken, cells[1][1], HAlign.Center, VAlign.Center);
        compiler.textIn(futanWari, cells[1][2], HAlign.Center, VAlign.Center);
    }

    private void renderRow4(Box box, String shoshin, String kanri, String zaitaku, String kensa, String gazou){
        Box[][] cells = box.splitToEvenCells(2, 5);
        compiler.frameCells(cells);
        compiler.setFont("mincho-4");
        compiler.textIn("初・再診料", cells[0][0], HAlign.Center, VAlign.Center);
        compiler.textIn(shoshin , cells[1][0], HAlign.Center, VAlign.Center);
        compiler.textIn("医学管理等", cells[0][1], HAlign.Center, VAlign.Center);
        compiler.textIn(kanri , cells[1][1], HAlign.Center, VAlign.Center);
        compiler.textIn("在宅医療", cells[0][2], HAlign.Center, VAlign.Center);
        compiler.textIn(zaitaku , cells[1][2], HAlign.Center, VAlign.Center);
        compiler.textIn("検査", cells[0][3], HAlign.Center, VAlign.Center);
        compiler.textIn(kensa , cells[1][3], HAlign.Center, VAlign.Center);
        compiler.textIn("画像診断", cells[0][4], HAlign.Center, VAlign.Center);
        compiler.textIn(gazou , cells[1][4], HAlign.Center, VAlign.Center);
    }

    private void renderRow5(Box box, String touyaku, String chuusha, String shochi, String sonota, String souten){
        Box[][] cells = box.splitToEvenCells(2, 5);
        compiler.frameCells(cells);
        compiler.frameRightOfNthColumn(cells, 3,  -1);
        compiler.setFont("mincho-4");
        compiler.textIn("投薬", cells[0][0], HAlign.Center, VAlign.Center);
        compiler.textIn(touyaku , cells[1][0], HAlign.Center, VAlign.Center);
        compiler.textIn("注射", cells[0][1], HAlign.Center, VAlign.Center);
        compiler.textIn(chuusha , cells[1][1], HAlign.Center, VAlign.Center);
        compiler.textIn("処置", cells[0][2], HAlign.Center, VAlign.Center);
        compiler.textIn(shochi , cells[1][2], HAlign.Center, VAlign.Center);
        compiler.textIn("その他", cells[0][3], HAlign.Center, VAlign.Center);
        compiler.textIn(sonota , cells[1][3], HAlign.Center, VAlign.Center);
        compiler.textIn("診療総点数", cells[0][4], HAlign.Center, VAlign.Center);
        compiler.textIn(souten , cells[1][4], HAlign.Center, VAlign.Center);
    }

    private void renderHokengai(Box box, String[] texts){
        Box[][] cells = box.splitToEvenCells(5, 1);
        compiler.setFont("mincho-4");
        compiler.frameCells(cells);
        compiler.textIn("保険外", cells[0][0], HAlign.Center, VAlign.Center);
        for(int i=0;i<texts.length;i++){
            compiler.textIn(texts[i], cells[1][0].shrinkWidth(1, HorizAnchor.Right), HAlign.Left, VAlign.Center);
        }
    }

    private void renderInstitute(Box box, String name, String[] addressLines){
        box = box.shiftToRight(-4);
        Box[] bb = box.splitToRows(5);
        compiler.setFont("gothic-4");
        compiler.textIn(name, bb[0], HAlign.Left, VAlign.Top);
        compiler.setFont("gothic-2.6");
        compiler.multilineText(addressLines, bb[1], HAlign.Left, VAlign.Top, 1);
    }

    private void renderRyoushuu(Box box){
        compiler.box(box);
        Box[] bb = box.splitToRows(5);
        compiler.frameBottom(bb[0]);
        compiler.setFont("mincho-4");
        compiler.textIn("領収印", bb[0], HAlign.Center, VAlign.Center);
    }

}
