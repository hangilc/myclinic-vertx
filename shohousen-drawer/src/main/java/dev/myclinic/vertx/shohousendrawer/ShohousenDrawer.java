package dev.myclinic.vertx.shohousendrawer;

import dev.myclinic.vertx.drawer.Box;
import dev.myclinic.vertx.drawer.Box.HorizAnchor;
import dev.myclinic.vertx.drawer.Box.VertAnchor;
import dev.myclinic.vertx.drawer.DrawerCompiler;
import dev.myclinic.vertx.drawer.DrawerCompiler.HAlign;
import dev.myclinic.vertx.drawer.DrawerCompiler.VAlign;
import dev.myclinic.vertx.drawer.Op;
import dev.myclinic.vertx.drawer.PaperSize;

import java.util.List;

public class ShohousenDrawer {

    private DrawerCompiler compiler = new DrawerCompiler();
    private Box wrap;
    private Box clinicInfoBox;
    private Box clinicPhoneBox;
    private Box clinicDoctorBox;
    private Box clinicHankoBox;
    private Box hokenshaBangouBox;
    private Box hihokenshaBox;
    private Box futanshaBangouBox;
    private Box jukyuushaBangouBox;
    private Box futanshaBangou2Box;
    private Box jukyuushaBangou2Box;
    private Box patientNameBox;
    private Box birthdayYearBox;
    private Box birthdayMonthBox;
    private Box birthdayDayBox;
    private Box sexMaleBox;
    private Box sexFemaleBox;
    private Box patientHihokenshaBox;
    private Box patientHifuyoushaBox;
    private Box patientFutanBox;
    private Box issueYearBox;
    private Box issueMonthBox;
    private Box issueDayBox;
    private Box validYearBox;
    private Box validMonthBox;
    private Box validDayBox;
    private Box drugsPaneBox;
    private Box memoPaneBox;
    private String NAME_FONT;
    private String NAME_SMALLER_FONT;
    private String NAME_MULTILINE_FONT;

    public static class ShohousenDrawerSettings {
        public int red = 0;
        public int green = 255;
        public int blue = 0;

        public void setColor(int red, int green, int blue){
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
    }

    private ShohousenDrawerSettings settings;

    public ShohousenDrawer(){
        this(new ShohousenDrawerSettings());
    }

    public ShohousenDrawer(ShohousenDrawerSettings settings){
        this.settings = settings;
        setupFonts();
        compiler.setTextColor(settings.red, settings.green, settings.blue);
        compiler.createPen("default-pen", settings.red, settings.green, settings.blue, 0.16);
        compiler.setPen("default-pen");
        wrap = Box.of(PaperSize.A5);
        drawTitle();
        Box r = wrap.shiftDown(13).setHeight(10.5, VertAnchor.Top);
        Box[] rr1 = r.splitToColumns(62);
        drawKouhi(rr1[0].shrinkWidth(2, HorizAnchor.Left));
        drawHoken(rr1[1]);
        Box box2 = wrap.setTop(r.getBottom()+2).setHeight(154.5, VertAnchor.Top);
        Box[] rr2 = box2.splitToRows(18, 24.5, 109, 143, 149.5);
        Box pat = rr2[0];
        Box issue = rr2[1];
        Box drugs = rr2[2];
        Box memo = rr2[3];
        Box chouzai1 = rr2[4];
        Box chouzai2 = rr2[5];
        Box[] rr3 = pat.splitToColumns(55);
        Box patient = rr3[0];
        Box clinic = rr3[1].shrinkWidth(1, HorizAnchor.Right);
        drawPatient(patient);
        drawClinic(clinic);
        drawIssue(issue);
        drawDrugs(drugs);
        drawMemo(memo);
        drawChouzai1(chouzai1);
        drawChouzai2(chouzai2);
        Box pharma = wrap.setTop(box2.getBottom() + 1).setHeight(24.5, VertAnchor.Top);
        drawPharmacy(pharma);
    }

    public List<Op> getOps(){
        return compiler.getOps();
    }

    public void setHakkouKikan(String address, String name, String phone, String kikancode){
        DrawerCompiler c = this.compiler;
        Box clinic_info = clinicInfoBox;
        Box clinic_phone = clinicPhoneBox;
        Box r = clinic_info.shift(2, 1);
        c.setTextColor(settings.red, settings.green, settings.blue);
        c.setFont("mincho-3");
        c.textIn(address, r, HAlign.Left, VAlign.Top);
        r = r.shift(4, 4);
        c.setFont("mincho-4");
        c.textIn(name, r, HAlign.Left, VAlign.Top);
        Box rr = r;
        rr = rr.shrinkWidth(34, HorizAnchor.Right);
        rr = rr.shrinkHeight(0.5, VertAnchor.Bottom);
        c.setFont("mincho-3");
        c.textIn("(機関コード " + kikancode + ")", rr, HAlign.Left, VAlign.Top);
        r = clinic_phone.shift(6, 0);
        c.setFont("mincho-3");
        c.textIn(phone, r, HAlign.Left, VAlign.Top);
    }

    public void setDoctorName(String name){
        DrawerCompiler c = this.compiler;
        Box r = clinicDoctorBox.shift(35, 0);
        c.setTextColor(settings.red, settings.green, settings.blue);
        c.setFont("mincho-3.5");
        c.textIn(name, r, HAlign.Left, VAlign.Top);
    }

    public void setHokenshaBangou(String bangou){
        DrawerCompiler c = this.compiler;
        Box box = hokenshaBangouBox;
        c.setTextColor(0, 0, 0);
        c.setFont("gothic-4");
        c.textInEvenColumns(bangou, box, 8, DrawerCompiler.TextInEvenColumnsJustification.Right);
    }

    public void setHihokensha(String str){
        DrawerCompiler c = this.compiler;
        Box box = hihokenshaBox;
        box = box.shrinkWidth(5, HorizAnchor.Right);
        c.setTextColor(0, 0, 0);
        c.setFont("gothic-4");
        c.textIn(str, box, HAlign.Left, VAlign.Center);
    }

    public void setKouhi1Futansha(String str){
        DrawerCompiler c = this.compiler;
        Box box = futanshaBangouBox;
        c.setTextColor(0, 0, 0);
        c.setFont("gothic-4");
        c.textInEvenColumns(str, box, 8, DrawerCompiler.TextInEvenColumnsJustification.Right);
    }

    public void setKouhi1Jukyuusha(String str){
        DrawerCompiler c = this.compiler;
        Box box = jukyuushaBangouBox;
        c.setTextColor(0, 0, 0);
        c.setFont("gothic-4");
        c.textInEvenColumns(str, box, 7, DrawerCompiler.TextInEvenColumnsJustification.Right);
    }

    public void setKouhi2Futansha(String str){
        DrawerCompiler c = this.compiler;
        Box box = futanshaBangou2Box;
        c.setTextColor(0, 0, 0);
        c.setFont("gothic-4");
        c.textInEvenColumns(str, box, 8, DrawerCompiler.TextInEvenColumnsJustification.Right);
    }

    public void setKouhi2Jukyuusha(String str){
        DrawerCompiler c = this.compiler;
        Box box = jukyuushaBangou2Box;
        c.setTextColor(0, 0, 0);
        c.setFont("gothic-4");
        c.textInEvenColumns(str, box, 7, DrawerCompiler.TextInEvenColumnsJustification.Right);
    }

    public void setShimei(String name){
        DrawerCompiler c = this.compiler;
        Box box = patientNameBox;
        box = box.shrinkWidth(2, HorizAnchor.Right);
        String font = "mincho-4.5";
        c.setTextColor(0, 0, 0);
        c.setFont(font);
        //c.textIn(name, box, HAlign.Left, VAlign.Center);
        DrawerCompiler.TextInBoundedOptions opts = new DrawerCompiler.TextInBoundedOptions();
        opts.smallerFonts = List.of(NAME_SMALLER_FONT);
        opts.multilineFont = NAME_MULTILINE_FONT;
        c.textInBounded(name, box, HAlign.Left, VAlign.Center, opts);
    }

    public void setBirthday(int year, int month, int day){
        DrawerCompiler c = this.compiler;
        c.setTextColor(0, 0, 0);
        c.setFont("gothic-2.5");
        c.textIn("" + year, birthdayYearBox, HAlign.Right, VAlign.Center);
        c.textIn("" + month, birthdayMonthBox, HAlign.Right, VAlign.Center);
        c.textIn("" + day, birthdayDayBox, HAlign.Right, VAlign.Center);
    }

    public void setSexMale(){
        DrawerCompiler c = this.compiler;
        c.setTextColor(0, 0, 0);
        c.setFont("gothic-3");
        Box box = sexMaleBox;
        c.textIn("○", box.shiftUp(0.3), HAlign.Center, VAlign.Center);
    }

    public void setSexFemale(){
        DrawerCompiler c = this.compiler;
        c.setTextColor(0, 0, 0);
        c.setFont("gothic-3");
        Box box = sexFemaleBox;
        c.textIn("○", box.shiftUp(0.3), HAlign.Center, VAlign.Center);
    }

    public void setKubunHihokensha(){
        DrawerCompiler c = this.compiler;
        c.setTextColor(0, 0, 0);
        c.setFont("gothic-3");
        Box box = patientHihokenshaBox;
        c.textIn("○", box.shiftUp(0.3), HAlign.Center, VAlign.Center);
    }

    public void setKubunHifuyousha(){
        DrawerCompiler c = this.compiler;
        c.setTextColor(0, 0, 0);
        c.setFont("gothic-3");
        Box box = patientHifuyoushaBox;
        c.textIn("○", box.shiftUp(0.3), HAlign.Center, VAlign.Center);
    }

    public void setFutanWari(int futanWari){
        DrawerCompiler c = this.compiler;
        c.setTextColor(0, 0, 0);
        c.setFont("gothic-3");
        c.textIn("" + futanWari, patientFutanBox, HAlign.Right, VAlign.Center);
    }

    public void setKoufuDate(int year, int month, int day){
        DrawerCompiler c = this.compiler;
        c.setTextColor(0, 0, 0);
        c.setFont("gothic-2.5");
        c.textIn("" + year, issueYearBox, HAlign.Right, VAlign.Center);
        c.textIn("" + month, issueMonthBox, HAlign.Right, VAlign.Center);
        c.textIn("" + day, issueDayBox, HAlign.Right, VAlign.Center);
    }

    public void setValidUptoDate(int year, int month, int day){
        DrawerCompiler c = this.compiler;
        c.setTextColor(0, 0, 0);
        c.setFont("gothic-2.5");
        c.textIn("" + year, validYearBox, HAlign.Right, VAlign.Center);
        c.textIn("" + month, validMonthBox, HAlign.Right, VAlign.Center);
        c.textIn("" + day, validDayBox, HAlign.Right, VAlign.Center);
    }

    public void setPharmacyName(String pharmacyName){
        DrawerCompiler c = this.compiler;
        c.setTextColor(0, 0, 0);
        c.setFont("gothic-2.5");
        c.textIn(pharmacyName, memoPaneBox.shift(-1, -1), HAlign.Right, VAlign.Bottom);
    }

    public void setDrugLinesAndMemo(List<String> drugLines, String memo){
        DrawerCompiler c = this.compiler;
        c.setTextColor(0, 0, 0);
        c.setFont("gothic-4.5");
        double y = c.multilineText(drugLines, drugsPaneBox, HAlign.Left, VAlign.Top, 0);
        Box memoBox = memoPaneBox;
        if( y > memoPaneBox.getTop() ){
             memoBox = memoPaneBox.setTop(y + 4.5);
        }
        c.setFont("gothic-4.5");
        c.setTextColor(0, 0, 0);
        c.paragraph(memo, memoBox.inset(1), HAlign.Left, VAlign.Top, 1);
    }

    public void setDrugLines(List<String> drugLines){
        DrawerCompiler c = this.compiler;
        c.setTextColor(0, 0, 0);
        c.setFont("gothic-4.5");
        c.multilineText(drugLines, drugsPaneBox, HAlign.Left, VAlign.Top, 0);
    }

    public void setMemo(String memo){
        DrawerCompiler c = this.compiler;
        c.setFont("gothic-4.5");
        c.setTextColor(0, 0, 0);
        c.paragraph(memo, memoPaneBox.shift(1, 1), HAlign.Left, VAlign.Top, 1);
    }

    private void frameDate(Box[] cols){
        double offset = 1;
        compiler.textIn("年", cols[0].flipRight().shiftToRight(offset), HAlign.Left, VAlign.Center);
        compiler.textIn("月", cols[1].flipRight().shiftToRight(offset), HAlign.Left, VAlign.Center);
        compiler.textIn("日", cols[2].flipRight().shiftToRight(offset), HAlign.Left, VAlign.Center);
    }

    private void setupFonts(){
        compiler.createFont("mincho-5", "MS Mincho", 5);
        compiler.createFont("mincho-4.5", "MS Mincho", 4.5);
        compiler.createFont("mincho-4", "MS Mincho", 4);
        compiler.createFont("mincho-3.5", "MS Mincho", 3.5);
        compiler.createFont("mincho-3", "MS Mincho", 3);
        compiler.createFont("mincho-2.5", "MS Mincho", 2.5);
        compiler.createFont("mincho-2", "MS Mincho", 2);
        compiler.createFont("mincho-1.8", "MS Mincho", 1.8);
        compiler.createFont("mincho-1.5", "MS Mincho", 1.5);
        compiler.createFont("mincho-1.4", "MS Mincho", 1.4);
        compiler.createFont("gothic-4.5", "MS Gothic", 4.5);
        compiler.createFont("gothic-4", "MS Gothic", 4);
        compiler.createFont("gothic-3", "MS Gothic", 3);
        compiler.createFont("gothic-2.5", "MS Gothic", 2.5);
        this.NAME_FONT = "mincho-4.5";
        this.NAME_SMALLER_FONT = "mincho-3";
        this.NAME_MULTILINE_FONT = "mincho-2";
    }

    private void drawTitle(){
        Box box1 = wrap.shiftDown(1).setLeft(51).setRight(93);
        compiler.setFont("mincho-5");
        compiler.textAtJustified("処方せん", box1.getLeft(), box1.getRight(), box1.getTop(), VAlign.Top);
        Box box2 = box1.shiftDown(6);
        compiler.setFont("mincho-2.5");
        compiler.textIn("(この処方せんは、どの保険薬局でも有効です。)", box2, HAlign.Center, VAlign.Top);
    }

    private void drawKouhi(Box b){
        Box[] rr = b.splitToEvenRows(2);
        Box row1 = rr[0];
        compiler.box(row1);
        Box[] cc1 = row1.splitToColumns(14.3);
        compiler.frameRight(cc1[0]);
        compiler.setFont("mincho-2");
        compiler.textAtJustified("公費負担者番号", cc1[0].getLeft()+0.5, cc1[0].getRight()-0.5, cc1[0].getCy(), VAlign.Center);
        this.futanshaBangouBox = cc1[1];
        //compiler.setBox("futanshaBangou", cc[1]);
        compiler.frameInnerColumnBorders(cc1[1], 8);
        Box row2 = rr[1].shrinkWidth(cc1[1].getWidth()/8, HorizAnchor.Left);
        compiler.box(row2);
        Box[] cc2 = row2.splitToColumns(14.3);
        compiler.frameRight(cc2[0]);
        compiler.textAtJustified("公費負担医療", cc2[0].getLeft()+0.5, cc2[0].getRight()-0.5, cc2[0].getTop()+cc2[0].getHeight()/4, VAlign.Center);
        compiler.textAtJustified("の受給者番号", cc2[0].getLeft()+0.5, cc2[0].getRight()-0.5, cc2[0].getTop()+cc2[0].getHeight()/4*3, VAlign.Center);
        //compiler.setBox("jukyuushaBangou", cc2[1]);
        this.jukyuushaBangouBox = cc2[1];
        compiler.frameInnerColumnBorders(cc2[1], 7);
    }

    private void drawHoken(Box r){
        DrawerCompiler c = compiler;
        Box[] rr = r.splitToEvenRows(2);
        Box upper = rr[0];
        Box lower = rr[1];
        upper = upper.setWidth(58, HorizAnchor.Left);
        c.box(upper);
        rr = upper.splitToColumns(13);
        Box left = rr[0];
        Box right = rr[1];
        c.frameRight(left);
        c.setFont("mincho-2");
        c.textAtJustified("保険者番号", left.getLeft()+0.5, left.getRight()-0.5, left.getCy(), VAlign.Center);
        this.hokenshaBangouBox = right;
        c.frameInnerColumnBorders(right, 8);
        c.box(lower);
        rr = lower.splitToColumns(13);
        left = rr[0];
        right = rr[1];
        this.hihokenshaBox = right;
        c.frameRight(left);
        c.setFont("mincho-1.4");
        c.textAtJustified("被保険者証・被保険", left.getLeft()+0.5, left.getRight()-0.5, left.getTop()+left.getHeight()/4, VAlign.Center);
        c.textAtJustified("者手帳の記号・番号", left.getLeft()+0.5, left.getRight()-0.5, left.getTop()+left.getHeight()/4*3, VAlign.Center);
    }

    private void drawPatient(Box r) {
        DrawerCompiler c = this.compiler;
        c.box(r);
        Box p = r.setWidth(4, HorizAnchor.Left);
        c.frameRight(p);
        c.setFont("mincho-2.5");
        c.textAtVertJustified("患者", p.getCx(), p.getTop()+4, p.getBottom()-4, HAlign.Center);
        Box[] rr = p.setLeft(p.getRight()).setRight(r.getRight()).splitToRows(9.5, 13.8);
        c.frameBottom(rr[0]);
        c.frameBottom(rr[1]);
        Box upper = rr[0];
        Box middle = rr[1];
        Box lower = rr[2];
        rr = upper.splitToColumns(10.5);
        p = rr[0];
        this.patientNameBox = rr[1];
        c.frameRight(p);
        c.setFont("mincho-2.5");
        c.textAtJustified("氏名", p.getLeft()+2, p.getRight()-2, p.getCy(), VAlign.Center);
        rr = middle.splitToColumns(10.5, 39);
        p = rr[0];
        c.frameRight(p);
        c.setFont("mincho-2");
        c.textAtJustified("生年月日", p.getLeft()+0.5, p.getRight()-0.5, p.getCy(), VAlign.Center);
        p = rr[1];
        c.frameRight(p);
        Box[] dd = p.splitToColumns(9, 17, 25);
        birthdayYearBox = dd[0];
        birthdayMonthBox = dd[1];
        birthdayDayBox = dd[2];
        frameDate(dd);
        p = rr[2];
        dd = p.splitToEvenColumns(3);
        sexMaleBox = dd[0];
        sexFemaleBox = dd[2];
        c.textIn("男", dd[0], HAlign.Center, VAlign.Center);
        c.textIn("・", dd[1], HAlign.Center, VAlign.Center);
        c.textIn("女", dd[2], HAlign.Center, VAlign.Center);
        rr = lower.splitToColumns(10.5, 24, 37.3);
        patientHihokenshaBox = rr[1];
        patientHifuyoushaBox = rr[2];
        Box futanBox = rr[3].shrinkWidth(4, HorizAnchor.Left);
        patientFutanBox = futanBox;
        c.frameInnerColumnBorders(rr);
        c.setFont("mincho-2.5");
        c.textAtJustified("区分", rr[0].getLeft()+2, rr[0].getRight()-2, rr[0].getCy(), VAlign.Center);
        c.textInJustified("被保険者", rr[1].inset(1.5, 0), VAlign.Center);
        c.textInJustified("被扶養者", rr[2].inset(1.5, 0), VAlign.Center);
        c.textIn("割", futanBox.shiftToRight(3), HAlign.Right, VAlign.Center);
    }

    private void drawClinic(Box box){
        DrawerCompiler c = this.compiler;
        Box[] rr = box.splitToRows(9.5, 13.8);
        Box upper = rr[0];
        Box middle = rr[1];
        Box lower = rr[2];
        rr = upper.splitToColumns(11);
        Box p = rr[0];
        this.clinicInfoBox = rr[1];
        p.shrinkHeight(1.5, VertAnchor.Bottom);
        p.shrinkHeight(0.5, VertAnchor.Bottom);
        Box[] pp = p.splitToEvenRows(3);
        c.setFont("mincho-1.5");
        c.textInJustified("保険医療機関", pp[0], VAlign.Top);
        c.setFont("mincho-1.8");
        c.textInJustified("の所在地", pp[1], VAlign.Center);
        c.textInJustified("及び名称", pp[2], VAlign.Bottom);
        rr = middle.splitToColumns(11);
        this.clinicPhoneBox = rr[1];
        c.textInJustified("電話番号", rr[0], VAlign.Center);
        rr = lower.splitToColumns(11);
        this.clinicDoctorBox = rr[1];
        c.textInJustified("保険医氏名", rr[0], VAlign.Center);
        this.clinicHankoBox = new Box(
                box.getLeft() + 53.5+7, box.getBottom() - 5.5, box.getLeft() + 56.5+7, box.getBottom() - 2.5
        );
        c.textIn("印", clinicHankoBox, HAlign.Center, VAlign.Center);
    }

    private void drawIssue(Box box){
        DrawerCompiler c = this.compiler;
        c.box(box);
        Box[] rr = box.splitToColumns(14.5, 55, 71.5);
        c.setFont("mincho-2.5");
        c.frameRight(rr[0]);
        c.frameRight(rr[1]);
        c.frameRight(rr[2]);
        c.textInJustified("交付年月日", rr[0].inset(0.5, 0), VAlign.Center);
        Box[] pp = rr[1].splitToColumns(16, 24, 32);
        issueYearBox = pp[0];
        issueMonthBox = pp[1];
        issueDayBox = pp[2];
        c.setFont("mincho-2");
        frameDate(pp);
        pp = rr[2].splitToEvenRows(2);
        c.textInJustified("処方せんの", pp[0].inset(0.5, 0), VAlign.Center);
        c.textInJustified("使用期間", pp[1].inset(0.5, 0), VAlign.Center);
        Box b = rr[3];
        rr = b.splitToColumns(16, 25, 35);
        validYearBox = rr[0];
        validMonthBox = rr[1];
        validDayBox = rr[2];
        frameDate(rr);
        b = b.shrinkWidth(40, HorizAnchor.Right);
        b = b.inset(1.5, 0);
        rr = b.splitToEvenRows(3);
        c.setFont("mincho-1.8");
        c.textIn("特に記載のある場合を除き、", rr[0], HAlign.Center, VAlign.Center);
        c.textIn("交付の日を含めて４日以内に保", rr[1], HAlign.Center, VAlign.Center);
        c.textIn("険薬局に提出すること。", rr[2], HAlign.Center, VAlign.Center);
    }

    private void drawDrugs(Box box){
        DrawerCompiler c = this.compiler;
        c.box(box);
        Box[] rr = box.splitToColumns(4);
        c.frameRight(rr[0]);
        c.setFont("mincho-2.5");
        c.textInVertJustified("処方", rr[0].inset(0, 24), HAlign.Center);
        drugsPaneBox = rr[1];
    }

    private void drawMemo(Box r){
        DrawerCompiler c = this.compiler;
        c.box(r);
        Box[] rr = r.splitToColumns(4);
        c.frameRight(rr[0]);
        c.setFont("mincho-2.5");
        c.textInVertJustified("備考", rr[0].inset(0, 7), HAlign.Center);
        memoPaneBox = rr[1];
    }

    private void drawChouzai1(Box r){
        DrawerCompiler c = this.compiler;
        c.box(r);
        Box[] rr = r.splitToColumns(14.5, 82, 95.5);
        c.frameInnerColumnBorders(rr);
        c.setFont("mincho-2");
        c.textInJustified("調剤年月日", rr[0].inset(1, 0), VAlign.Center);
        Box[] dd = rr[1].splitToColumns(28, 41, 53);
        frameDate(dd);
        c.setFont("mincho-1.5");
        c.textInJustified("公費負担者番号", rr[2].inset(0.5, 0), VAlign.Center);
        this.futanshaBangou2Box = rr[3];
        c.frameInnerColumnBorders(rr[3], 8);
    }

    private void drawChouzai2(Box r){
        DrawerCompiler c = this.compiler;
        Box[] rr = r.splitToColumns(14.5, 82, 95.5);
        c.frameInnerColumnBorders(rr);
        c.setFont("mincho-2");
        Box[] cc = rr[0].splitToEvenRows(3);
        c.setFont("mincho-1.5");
        c.textInJustified("保険薬局の所在", cc[0].inset(0.5, 0), VAlign.Center);
        c.textInJustified("地及び名称", cc[1].inset(0.5, 0), VAlign.Center);
        c.textInJustified("保険薬剤師氏名", cc[2].inset(0.5, 0), VAlign.Center);
        c.setFont("mincho-2");
        c.textIn("印", rr[1].shiftToRight(59), HAlign.Left, VAlign.Center);
        c.setFont("mincho-1.5");
        cc = rr[2].inset(0.5, 0).splitToEvenRows(2);
        c.textInJustified("公費負担医療", cc[0], VAlign.Center);
        c.textInJustified("の受給者番号", cc[1], VAlign.Center);
        Box[] bb = rr[3].splitToEvenColumns(8);
        Box jukyuushaBangou2 = rr[3].setRight(bb[7].getLeft());
        this.jukyuushaBangou2Box = jukyuushaBangou2;
        //c.setBox("jukyuushaBangou2", jukyuushaBangou2);
        c.frameInnerColumnBorders(jukyuushaBangou2, 7);
        r = r.setRight(bb[7].getLeft());
        c.box(r);
    }

    private void drawPharmacy(Box r){
        DrawerCompiler c = this.compiler;
        Box[] rr = r.splitToColumns(85);
        Box left = rr[0];
        Box right = rr[1];
        c.box(left);
        c.box(right);
        c.setFont("mincho-2");

        Box[] pp = left.splitToRows(3, 10, 17);
        c.frameBottom(pp[0]);
        c.frameBottom(pp[1]);
        c.frameBottom(pp[2]);
        Box[] qq = pp[0].splitToColumns(11.5, 27.8, 47, 57.3, 76.5);
        for (int i = 0; i < 5; i++) {
            c.frameRight(qq[i]);
        }
        c.textInJustified("調剤料", qq[0].inset(1, 0), VAlign.Center);
        c.textInJustified("薬剤料", qq[1].inset(3, 0), VAlign.Center);
        c.textIn("計", qq[2], HAlign.Center, VAlign.Center);
        c.textInJustified("調剤数量", qq[3].inset(0.5, 0), VAlign.Center);
        c.textInJustified("合計", qq[4].inset(4, 0), VAlign.Center);
        c.textInJustified("加算", qq[5].inset(1.5, 0), VAlign.Center);
        for (int j = 1; j <= 3; j++) {
            qq = pp[j].splitToColumns(11.5, 27.8, 47, 57.3, 76.5);
            for (int i = 0; i < 5; i++) {
                c.frameRight(qq[i]);
            }
        }

        pp = right.splitToRows(3, 10, 13);
        for (int i = 0; i < 3; i++) {
            c.frameBottom(pp[i]);
        }
        qq = pp[0].splitToColumns(19.5, 39);
        for (int i = 0; i < 2; i++) {
            c.frameRight(qq[i]);
        }
        c.textInJustified("調剤基本料", qq[0].inset(2, 0), VAlign.Center);
        c.textInJustified("管理指導料", qq[1].inset(2, 0), VAlign.Center);
        c.textInJustified("総合計", qq[2].inset(2, 0), VAlign.Center);
        qq = pp[1].splitToColumns(19.5, 39);
        for (int i = 0; i < 2; i++) {
            c.frameRight(qq[i]);
        }
        qq = pp[2].splitToColumns(19.5, 39);
        for (int i = 0; i < 2; i++) {
            c.frameRight(qq[i]);
        }
        c.textInJustified("患者負担金", qq[0].inset(2, 0), VAlign.Center);
        c.textInJustified("請求金額", qq[1].inset(2, 0), VAlign.Center);
        c.textInJustified("調剤済印", qq[2].inset(2, 0), VAlign.Center);
        qq = pp[3].splitToColumns(19.5, 39);
        for (int i = 0; i < 2; i++) {
            c.frameRight(qq[i]);
        }
    }

}
