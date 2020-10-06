package dev.myclinic.drawerform2.houmonkango;

import dev.myclinic.drawerform2.FormCompiler;
import dev.myclinic.drawerform2.Hints;
import dev.myclinic.vertx.drawer.Box;
import dev.myclinic.vertx.drawer.DrawerCompiler;
import dev.myclinic.vertx.drawer.DrawerConsts;
import dev.myclinic.vertx.drawer.PaperSize;

import java.util.List;

import static dev.myclinic.vertx.drawer.Box.HorizAnchor;
import static dev.myclinic.vertx.drawer.Box.VertAnchor;
import static dev.myclinic.vertx.drawer.DrawerCompiler.*;
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
        c.textAt(
                "上記のとおり、指定訪問看護の実施を指示する。",
                mainBox.getLeft(), mainBox.getBottom() + 3,
                HAlign.Left, VAlign.Top
        );
        Box addrBox = new Box(
                mainBox.getLeft() + 75,
                mainBox.getBottom() + 10,
                mainBox.getRight(),
                mainBox.getBottom() + 35
        );
        renderAddr(addrBox);
        Box recipientBox = new Box(
                mainBox.getLeft(),
                addrBox.getBottom() + 2,
                mainBox.getRight(),
                addrBox.getBottom() + 6
        ).setHeight(5, VertAnchor.Top);
        c.multi(
                recipientBox,
                VAlign.Center,
                List.of(
                        c.mSpace(60).mark("recipient", "提出先（訪問看護ステーション）")
                                .addHints(Hints.right(), Hints.rightPadding(3)),
                        c.mLabel("殿")
                )
        );
        c.setFont("input-regular");
        form.page = "A4";
        form.marks = c.getMarks();
        form.hints = c.getHints();
        form.descriptions = c.getDescriptions();
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
                c.mSpace(7).mark("subtitle1.from.nen", "訪問看護指示期間開始（年）")
                        .addHints(Hints.right(), Hints.rightPadding(0.5)),
                c.mLabel("年"),
                c.mSpace(4.5).mark("subtitle1.from.month", "訪問看護指示期間開始（月）")
                        .addHints(Hints.right(), Hints.rightPadding(0.5)),
                c.mLabel("月"),
                c.mSpace(4.5).mark("subtitle1.from.day", "訪問看護指示期間開始（日）")
                        .addHints(Hints.right(), Hints.rightPadding(0.5)),
                c.mLabel("日～令和"),
                c.mSpace(7).mark("subtitle1.upto.nen", "訪問看護指示期間期限（年）")
                        .addHints(Hints.right(), Hints.rightPadding(0.5)),
                c.mLabel("年"),
                c.mSpace(4.5).mark("subtitle1.upto.month", "訪問看護指示期間期限（月）")
                        .addHints(Hints.right(), Hints.rightPadding(0.5)),
                c.mLabel("月"),
                c.mSpace(4.5).mark("subtitle1.upto.day", "訪問看護指示期間期限（日）")
                        .addHints(Hints.right(), Hints.rightPadding(0.5)),
                c.mLabel("日）")
        ));
    }

    private void subtitle2(double y) {
        double x = 113;
        c.textAt("点滴注射指示期間", x, y, HAlign.Right, VAlign.Top);
        c.multiAt(x, y, VAlign.Top, List.of(
                c.mLabel("（令和"),
                c.mSpace(7).mark("subtitle2.from.nen", "点滴注射指示期間開始（年）")
                        .addHints(Hints.right(), Hints.rightPadding(0.5)),
                c.mLabel("年"),
                c.mSpace(4.5).mark("subtitle2.from.month", "点滴注射指示期間開始（月）")
                        .addHints(Hints.right(), Hints.rightPadding(0.5)),
                c.mLabel("月"),
                c.mSpace(4.5).mark("subtitle2.from.day", "点滴注射指示期間開始（日）")
                        .addHints(Hints.right(), Hints.rightPadding(0.5)),
                c.mLabel("日～令和"),
                c.mSpace(7).mark("subtitle2.upto.nen", "点滴注射指示期間期限（年）")
                        .addHints(Hints.right(), Hints.rightPadding(0.5)),
                c.mLabel("年"),
                c.mSpace(4.5).mark("subtitle2.upto.month", "点滴注射指示期間期限（月）")
                        .addHints(Hints.right(), Hints.rightPadding(0.5)),
                c.mLabel("月"),
                c.mSpace(4.5).mark("subtitle2.upto.day", "点滴注射指示期間期限（日）")
                        .addHints(Hints.right(), Hints.rightPadding(0.5)),
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
            c.addMark("shimei", "患者氏名", cc[0], List.of(Hints.center(), Hints.vCenter()));
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
            c.addMark("birthday.gengou.meiji", "生年月日（元号：明治）", b, List.of(Hints.circle(), Hints.radius(1.5)));
            b = c.textIn("・", b.flipRight(), HAlign.Left, VAlign.Center);
            b = c.textIn("大", b.flipRight(), HAlign.Left, VAlign.Center);
            c.addMark("birthday.gengou.taishou", "生年月日（元号：大正）", b, List.of(Hints.circle(), Hints.radius(1.5)));
            b = c.textIn("・", b.flipRight(), HAlign.Left, VAlign.Center);
            b = c.textIn("昭", b.flipRight(), HAlign.Left, VAlign.Center);
            c.addMark("birthday.gengou.shouwa", "生年月日（元号：昭和）", b, List.of(Hints.circle(), Hints.radius(1.5)));
            b = c.textIn("・", b.flipRight(), HAlign.Left, VAlign.Center);
            b = c.textIn("平", b.flipRight(), HAlign.Left, VAlign.Center);
            c.addMark("birthday.gengou.heisei", "生年月日（元号：平成）", b, List.of(Hints.circle(), Hints.radius(1.5)));
            b = c.multi(
                    b.flipRight(),
                    VAlign.Center,
                    List.of(
                            c.mSpace(8).mark("birthday.nen", "生年月日（年）")
                                    .addHints(Hints.right(), Hints.rightPadding(0.5)),
                            c.mLabel("年"),
                            c.mSpace(8).mark("birthday.month", "生年月日（月）")
                                    .addHints(Hints.right(), Hints.rightPadding(0.5)),
                            c.mLabel("月"),
                            c.mSpace(8).mark("birthday.day", "生年月日（日）")
                                    .addHints(Hints.right(), Hints.rightPadding(0.5)),
                            c.mLabel("日")
                    ));

            c.multi(
                    rr[1].shrinkWidth(63, HorizAnchor.Right),
                    VAlign.Center,
                    List.of(
                            c.mLabel("（"),
                            c.mSpace(6).mark("age", "年齢").addHints(Hints.right(), Hints.rightPadding(0.5)),
                            c.mLabel("歳）")
                    ));
        }
    }

    private void renderRow1(Box row) {
        Box[] cols = row.splitToColumns(20);
        for (int i = 0; i < cols.length - 1; i++) {
            c.frameRight(cols[i]);
        }
        {
            c.textIn(
                    "患者住所",
                    cols[0].shrinkWidth(1.5, HorizAnchor.Right),
                    HAlign.Left,
                    VAlign.Center
            );
            c.addMark("address", "患者住所", cols[1], List.of(Hints.leftPadding(2), Hints.vCenter()));
        }
    }

    private void renderRow2(Box row) {
        Box[] cols = row.splitToColumns(29);
        for (int i = 0; i < cols.length - 1; i++) {
            c.frameRight(cols[i]);
        }
        {
            c.textIn(
                    "主たる傷病名",
                    cols[0].shrinkWidth(1.5, HorizAnchor.Right),
                    HAlign.Left,
                    VAlign.Center
            );
            c.addMark("disease", "主たる傷病名", cols[1], List.of(Hints.leftPadding(2), Hints.vCenter()));
        }
    }

    private void renderRow3(Box row) {
        Box[] cols = row.splitToColumns(8.5);
        for (int i = 0; i < cols.length - 1; i++) {
            c.frameRight(cols[i]);
        }
        {
            c.textAtVert(
                    "現在の状況・該当項目に〇等",
                    cols[0].getCx(),
                    cols[0].getCy(),
                    HAlign.Center,
                    VAlign.Center,
                    new DrawerCompiler.TextAtOpt(1.5)
            );
        }
        Box[] rows = cols[1].splitToRows(12, 28, 42.5, 49.5, 56.5);
        for (int i = 0; i < rows.length - 1; i++) {
            c.frameBottom(rows[i]);
        }
        {
            Box box = rows[0];
            Box[] cc = box.splitToColumns(20.5);
            c.frameRight(cc[0]);
            Box[] subRows = cc[0].inset(2).splitToEvenRows(2);
            c.textIn("病状・治療", subRows[0], HAlign.Left, VAlign.Center,
                    new TextAtOpt().extraSpaces(0, -0.5, -0.5, 0));
            c.textIn("状態", subRows[1], HAlign.Left, VAlign.Center, new TextAtOpt(6));
            c.addMark(
                    "disease-condition", "病状",
                    cc[1],
                    List.of(Hints.para(), Hints.padding(2), Hints.vTop(), Hints.leading(0.7))
            );
        }
        {
            Box box = rows[1];
            Box[] cc = box.splitToColumns(20.5);
            c.frameRight(cc[0]);
            Box[] subRows = cc[0].inset(2).splitToEvenRows(3);
            c.textIn("投与中の", subRows[0], HAlign.Left, VAlign.Center, new TextAtOpt(1));
            c.textIn("薬剤の用", subRows[1], HAlign.Left, VAlign.Center, new TextAtOpt(1));
            c.textIn("量・用法", subRows[2], HAlign.Left, VAlign.Center);
            c.addMark("drugs", "薬剤", cc[1],
                    List.of(Hints.para(), Hints.padding(2), Hints.vTop(), Hints.leading(0.7)));
        }
        {
            Box box = rows[2];
            Box[] cc = box.splitToColumns(20.5);
            c.frameRight(cc[0]);
            Box[] subRows = cc[0].inset(2, 2, 2, 3).splitToEvenRows(2);
            c.textIn("日常生活", subRows[0], HAlign.Left, VAlign.Center, new TextAtOpt(1));
            c.textIn("自立度", subRows[1], HAlign.Left, VAlign.Center);

            {
                Box[] rr = cc[1].splitToEvenRows(2);
                Box r1 = rr[0];
                Box r2 = rr[1];
                c.frameBottom(r1);
                {
                    Box[] cN = r1.splitToColumns(27.5);
                    Box c1 = cN[0];
                    Box c2 = cN[1];
                    c.frameRight(c1);
                    c.textIn("寝たきり度", c1.shift(1.5, 0), HAlign.Left, VAlign.Center);
                    Box b = c.textIn(
                            "J1",
                            c2.shrinkWidth(5, HorizAnchor.Right),
                            HAlign.Left,
                            VAlign.Center
                    );
                    c.addMark("netakiri.J1", "寝たきり度(J1)", b, List.of(Hints.circle(), Hints.radius(1.7)));
                    b = c.textIn("J2", b.flipRight().shift(6, 0), HAlign.Left, VAlign.Center);
                    c.addMark("netakiri.J2", "寝たきり度(J2)", b, List.of(Hints.circle(), Hints.radius(1.7)));
                    b = c.textIn("A1", b.flipRight().shift(6, 0), HAlign.Left, VAlign.Center);
                    c.addMark("netakiri.A1", "寝たきり度(A1)", b, List.of(Hints.circle(), Hints.radius(1.7)));
                    b = c.textIn("A2", b.flipRight().shift(6, 0), HAlign.Left, VAlign.Center);
                    c.addMark("netakiri.A2", "寝たきり度(A2)", b, List.of(Hints.circle(), Hints.radius(1.7)));
                    b = c.textIn("B1", b.flipRight().shift(6, 0), HAlign.Left, VAlign.Center);
                    c.addMark("netakiri.B1", "寝たきり度(B1)", b, List.of(Hints.circle(), Hints.radius(1.7)));
                    b = c.textIn("B2", b.flipRight().shift(6, 0), HAlign.Left, VAlign.Center);
                    c.addMark("netakiri.B2", "寝たきり度(B2)", b, List.of(Hints.circle(), Hints.radius(1.7)));
                    b = c.textIn("C1", b.flipRight().shift(6, 0), HAlign.Left, VAlign.Center);
                    c.addMark("netakiri.C1", "寝たきり度(C1)", b, List.of(Hints.circle(), Hints.radius(1.7)));
                    b = c.textIn("C2", b.flipRight().shift(6, 0), HAlign.Left, VAlign.Center);
                    c.addMark("netakiri.C2", "寝たきり度(C2)", b, List.of(Hints.circle(), Hints.radius(1.7)));
                }
                {
                    Box[] cN = r2.splitToColumns(27.5);
                    Box c1 = cN[0];
                    Box c2 = cN[1];
                    c.frameRight(c1);
                    c.textIn("認知症の状況", c1.shift(1.5, 0), HAlign.Left, VAlign.Center);
                    Box b = c.textIn(
                            "Ｉ",
                            c2.shrinkWidth(5, HorizAnchor.Right),
                            HAlign.Left,
                            VAlign.Center
                    );
                    c.addMark("ninchi.1", "認知症の状況(1)", b, List.of(Hints.circle(), Hints.radius(1.7)));
                    b = c.textIn(
                            "IIa",
                            b.flipRight().shift(6, 0),
                            HAlign.Left,
                            VAlign.Center
                    );
                    c.addMark("ninchi.2a", "認知症の状況(2a)", b, List.of(Hints.circle(), Hints.radius(1.7)));
                    b = c.textIn(
                            "IIb",
                            b.flipRight().shift(6, 0),
                            HAlign.Left,
                            VAlign.Center
                    );
                    c.addMark("ninchi.2b", "認知症の状況(2b)", b, List.of(Hints.circle(), Hints.radius(1.7)));
                    b = c.textIn(
                            "IIIa",
                            b.flipRight().shift(6, 0),
                            HAlign.Left,
                            VAlign.Center
                    );
                    c.addMark("ninchi.3a", "認知症の状況(3a)", b, List.of(Hints.circle(), Hints.radius(1.7)));
                    b = c.textIn(
                            "IIIb",
                            b.flipRight().shift(6, 0),
                            HAlign.Left,
                            VAlign.Center
                    );
                    c.addMark("ninchi.3b", "認知症の状況(3b)", b, List.of(Hints.circle(), Hints.radius(1.7)));
                    b = c.textIn("IV", b.flipRight().shift(6, 0), HAlign.Left, VAlign.Center);
                    c.addMark("ninchi.4", "認知症の状況(4)", b, List.of(Hints.circle(), Hints.radius(1.7)));
                    b = c.textIn("Ｍ", b.flipRight().shift(6, 0), HAlign.Left, VAlign.Center);
                    c.addMark("ninchi.M", "認知症の状況(M)", b, List.of(Hints.circle(), Hints.radius(1.7)));
                }
            }
        }
        {
            Box box = rows[3];
            Box[] cN = box.splitToColumns(48);
            Box c0 = cN[0];
            Box c1 = cN[1];
            c.frameRight(c0);
            c.textIn("要介護認定の状況", c0.shift(2, 0), HAlign.Left, VAlign.Center, new TextAtOpt(1.5));
            Box b = c.textIn("自立", c1.shift(3.5, 0), HAlign.Left, VAlign.Center);
            c.addMark("youkaigo.jiritsu", "要介護認定の状況（自立）", b, List.of(Hints.circle(), Hints.radius(1.7)));
            b = c.textIn("要支援（", b.flipRight().shift(6, 0), HAlign.Left, VAlign.Center);
            b = c.textIn("１", b.flipRight(), HAlign.Left, VAlign.Center);
            c.addMark("youkaigo.youshien1", "要介護認定の状況（要支援１）", b, List.of(Hints.circle(), Hints.radius(1.7)));
            b = c.textIn("２", b.flipRight().shift(3.5, 0), HAlign.Left, VAlign.Center);
            c.addMark("youkaigo.youshien2", "要介護認定の状況（要支援２）", b, List.of(Hints.circle(), Hints.radius(1.7)));
            b = c.textIn("）", b.flipRight(), HAlign.Left, VAlign.Center);
            b = c.textIn("要介護（", b.flipRight().shift(7.5, 0), HAlign.Left, VAlign.Center);
            b = c.textIn("１", b.flipRight(), HAlign.Left, VAlign.Center);
            c.addMark("youkaigo.youkaigo1", "要介護認定の状況（要介護１）", b, List.of(Hints.circle(), Hints.radius(1.7)));
            b = c.textIn("２", b.flipRight().shift(3, 0), HAlign.Left, VAlign.Center);
            c.addMark("youkaigo.youkaigo2", "要介護認定の状況（要介護２）", b, List.of(Hints.circle(), Hints.radius(1.7)));
            b = c.textIn("３", b.flipRight().shift(3, 0), HAlign.Left, VAlign.Center);
            c.addMark("youkaigo.youkaigo3", "要介護認定の状況（要介護３）", b, List.of(Hints.circle(), Hints.radius(1.7)));
            b = c.textIn("４", b.flipRight().shift(3, 0), HAlign.Left, VAlign.Center);
            c.addMark("youkaigo.youkaigo4", "要介護認定の状況（要介護４）", b, List.of(Hints.circle(), Hints.radius(1.7)));
            b = c.textIn("５", b.flipRight().shift(3, 0), HAlign.Left, VAlign.Center);
            c.addMark("youkaigo.youkaigo5", "要介護認定の状況（要介護５）", b, List.of(Hints.circle(), Hints.radius(1.7)));
            c.textIn("）", b.flipRight(), HAlign.Left, VAlign.Center);
        }
        {
            Box box = rows[4];
            Box[] cN = box.splitToColumns(48);
            Box c0 = cN[0];
            Box c1 = cN[1];
            c.frameRight(c0);
            c.textIn("褥瘡の深さ", c0.shift(2, 0), HAlign.Left, VAlign.Center,
                    new TextAtOpt(1.5));
            Box b = c.textIn("NPUAP分類", c1.shift(3.5, 0), HAlign.Left, VAlign.Center);
            b = c.textIn("III度", b.flipRight().shift(6, 0), HAlign.Left, VAlign.Center);
            b = c.textIn("IV度", b.flipRight().shift(2, 0), HAlign.Left, VAlign.Center);
            b = c.textIn("DESIGN分類", b.flipRight().shift(6, 0), HAlign.Left, VAlign.Center);
            b = c.textIn("D3", b.flipRight().shift(2, 0), HAlign.Left, VAlign.Center);
            b = c.textIn("D4", b.flipRight().shift(3, 0), HAlign.Left, VAlign.Center);
            c.textIn("D5", b.flipRight().shift(3, 0), HAlign.Left, VAlign.Center);
        }
        {
            Box box = rows[5];
            Box[] cN = box.splitToColumns(23.5);
            Box c0 = cN[0];
            Box c1 = cN[1];
            c.frameRight(c0);
            c.paragraph("装着・使用\n医療機器等", c0.inset(2), HAlign.Left, VAlign.Center, 2.0);
            {
                Box[] rr = c1.inset(2).splitToEvenRows(7);
                {
                    Box b;
                    b = c.textIn("１", rr[0], HAlign.Left, VAlign.Center);
                    b = c.textIn("．自動腹膜灌流装置", b.flipRight(), HAlign.Left, VAlign.Center);
                    b = c.textIn("２", b.setLeft(rr[0].getLeft() + 46), HAlign.Left, VAlign.Center);
                    b = c.textIn("．透析液供給装置", b.flipRight(), HAlign.Left, VAlign.Center);
                    b = c.textIn("３", b.setLeft(rr[0].getLeft() + 90), HAlign.Left, VAlign.Center);
                    c.multi(b.flipRight(), VAlign.Center, List.of(
                            c.mLabel("．酸素療法（"),
                            c.mSpace(12),
                            c.mLabel("/min）")
                    ));
                }
                {
                    Box b;
                    b = c.textIn("４", rr[1], HAlign.Left, VAlign.Center);
                    b = c.textIn("．吸引器", b.flipRight(), HAlign.Left, VAlign.Center);
                    b = c.textIn("５", b.setLeft(rr[0].getLeft() + 46), HAlign.Left, VAlign.Center);
                    b = c.textIn("．中心静脈栄養", b.flipRight(), HAlign.Left, VAlign.Center);
                    b = c.textIn("６", b.setLeft(rr[0].getLeft() + 90), HAlign.Left, VAlign.Center);
                    c.textIn("．輸液ポンプ", b.flipRight(), HAlign.Left, VAlign.Center);
                }
                double commaPos;
                double lastParenPos;
                double lastParenRightPos;
                {
                    Box b;
                    b = c.textIn("７", rr[2], HAlign.Left, VAlign.Center);
                    b = c.textIn("．経管栄養（", b.flipRight(), HAlign.Left, VAlign.Center);
                    b = c.textIn("経鼻", b.flipRight(), HAlign.Left, VAlign.Center);
                    b = c.textIn("・", b.flipRight(), HAlign.Left, VAlign.Center);
                    b = c.textIn("胃ろう", b.flipRight(), HAlign.Left, VAlign.Center);
                    b = c.textIn("：チューブサイズ", b.flipRight(), HAlign.Left, VAlign.Center);
                    b = b.flipRight().shift(21, 0);
                    commaPos = b.getLeft();
                    b = c.textIn("、", b, HAlign.Left, VAlign.Center);
                    b = b.flipRight().shift(15, 0);
                    lastParenPos = b.getLeft();
                    b = c.textIn("日に１回交換）", b, HAlign.Left, VAlign.Center);
                    lastParenRightPos = b.getRight();
                }
                {
                    Box b;
                    b = c.textIn("８", rr[3], HAlign.Left, VAlign.Center);
                    b = c.textIn("．留置カテーテル（サイズ", b.flipRight(), HAlign.Left, VAlign.Center);
                    b = b.shift(commaPos - b.getLeft(), 0);
                    b = c.textIn("、", b, HAlign.Left, VAlign.Center);
                    b = b.shift(lastParenPos - b.getLeft(), 0);
                    b = c.textIn("日に１回交換）", b, HAlign.Left, VAlign.Center);
                }
                {
                    Box b;
                    b = c.multi(
                            rr[4],
                            VAlign.Center,
                            List.of(
                                    c.mLabel("９．人工呼吸器（"),
                                    c.mLabel("陽圧式"),
                                    c.mLabel("・"),
                                    c.mLabel("陰圧式"),
                                    c.mLabel("：設定")));
                    b = b.flipRight();
                    b = b.setRight(lastParenRightPos);
                    c.textIn("）", b, HAlign.Right, VAlign.Center);
                }
                {
                    c.multi(
                            rr[5],
                            VAlign.Center,
                            List.of(
                                    c.mLabel("１０"),
                                    c.mLabel("．気管カニューレ（サイズ"),
                                    c.mSpace(11),
                                    c.mLabel("）"))
                    );
                }
                {
                    c.multi(
                            rr[6],
                            VAlign.Center,
                            List.of(
                                    c.mLabel("１１"),
                                    c.mLabel("．人工肛門"),
                                    c.mSpace(18),
                                    c.mLabel("１２"),
                                    c.mLabel("．人工膀胱"),
                                    c.mSpace(10),
                                    c.mLabel("１３").mark("souchi.sonota.mark",
                                            "装置・その他：１３（マーク）").addHints(Hints.circle()),
                                    c.mLabel("．その他（"),
                                    c.mSpace(35).mark("souchi.sonota",
                                            "装置・その他：１３").addHints(Hints.center()),
                                    (comp, b, valign) -> {
                                        b = b.setRight(lastParenRightPos);
                                        Box bb = c.textIn("）", b, HAlign.Right, VAlign.Center);
                                        comp.modifyMark("souchi.sonota",
                                                mb -> mb.setRight(bb.getLeft()));
                                        return bb;
                                    }
                            ));

                }
            }
        }
    }

    private void renderRow4(Box row) {
        Box[] cN = row.splitToEvenRows(2);
        Box r1 = cN[0];
        Box r2 = cN[1];
        Box b1 = c.textIn("留意事項及び指示事項", r1.shift(2, 0), HAlign.Left, VAlign.Center);
        Box b2 = c.multi(r2.shift(2, 0), VAlign.Center,
                List.of(
                        (comp, b, valign) -> {
                            b = b.setWidth(comp.getCurrentFontSize(), HorizAnchor.Left);
                            comp.textIn("I", b, HAlign.Center, valign);
                            return b;
                        },
                        c.mLabel("療養生活指導上の留意事項")
                ));
        Box b = row.setLeft(Math.max(b1.getRight(), b2.getRight())).inset(2, 1, 1, 1);
        c.addMark("ryuui-jikou", "留意事項：療養生活指導上の留意事項", b, List.of(Hints.para(), Hints.vTop()));
    }

    private void renderRow5(Box row) {
        Box[] cN = row.splitToColumns(8.5);
        Box c1 = cN[0];
        Box c2 = cN[1];
        c.textIn("II",
                c1.shift(2, 0).splitToEvenRows(4)[0].setWidth(c.getCurrentFontSize(), HorizAnchor.Left),
                HAlign.Center, VAlign.Center);
        Box[] rr = c2.splitToEvenRows(4);
        Box b;
        b = c.multi(rr[0], VAlign.Center, List.of(
                c.mLabel("１").mark("rehabilitation.mark", "留意事項：リハビリテーション（マーク）")
                        .addHints(Hints.circle(), Hints.radius(1.5)),
                c.mLabel("．リハビリテーション")));
        b = rr[0].setLeft(b.getRight());
        c.addMark("rehabilitation", "留意事項：リハビリテーション", b, List.of(Hints.xPadding(4), Hints.vCenter()));
        c.multi(rr[1], VAlign.Center, List.of(c.mLabel("２"), c.mLabel("．褥瘡の処置など")));
        c.multi(rr[2], VAlign.Center, List.of(c.mLabel("３"), c.mLabel("．装置・使用機器等の操作援助・管理")));
        c.multi(rr[3], VAlign.Center, List.of(c.mLabel("４"), c.mLabel("．その他")));
    }

    private void renderRow6(Box row) {
        Box[] rr = row.splitToEvenRows(2);
        c.textIn(
                "在宅患者訪問点滴注射に関する指示（投与薬剤・投与量・投与方法等）",
                rr[0].shift(2, 0),
                HAlign.Left,
                VAlign.Center
        );
    }

    private void renderRow7(Box row) {
        Box[] rr = row.splitToEvenRows(2);
        Box d1 = c.textIn("緊急時の連絡先", rr[0].shift(2, 0), HAlign.Left, VAlign.Center);
        c.addMark("emergency.contact", "緊急時の連絡先", rr[0].setLeft(d1.getRight()).shrinkWidth(2, HorizAnchor.Right),
                List.of(Hints.vCenter()));
        Box d2 = c.textIn("不在時の対応法", rr[1].shift(2, 0), HAlign.Left, VAlign.Center);
        c.addMark("absence.reaction", "不在時の対応法", rr[1].setLeft(d2.getRight()).shrinkWidth(2, HorizAnchor.Right),
                List.of(Hints.vCenter()));
    }

    private void renderRow8(Box row) {
        Box[] rr = row.splitToEvenRows(3);
        Box b = c.textIn("特記すべき留意事項", rr[0].shift(2, 0), HAlign.Left, VAlign.Center);
        c.pushFont();
        c.setFont("small");
        c.textIn(
                "（注：薬の相互作用・副作用についての留意点、薬物アレルギーの既往、定期巡回・随時対応型訪問",
                b.flipRight(),
                HAlign.Left,
                VAlign.Center
        );
        c.textIn(
                "介護看護及び複合型サービス利用時の留意事項等があれば記載して下さい。）",
                rr[1].shift(2, 0),
                HAlign.Left,
                VAlign.Center
        );
        c.popFont();
        c.addMark("special-notice", "特記すべき留意事項", rr[2].shrinkWidth(2, HorizAnchor.Right), List.of(Hints.vCenter()));
    }

    private void renderRow9(Box row) {
        Box[] rr = row.splitToEvenRows(4);
        double fontSize = c.getCurrentFontSize();
        double rightLimit = row.getRight() - 15;
        c.textIn("他の訪問看護ステーションへの指示", rr[0].shift(2, 0), HAlign.Left, VAlign.Center);
        c.multi(
                rr[1].shift(10, 0).setRight(rightLimit),
                VAlign.Center,
                List.of(
                        c.mLabel("（"),
                        c.mLabel("無"),
                        c.mSpace(fontSize),
                        c.mLabel("有"),
                        c.mSpace(fontSize),
                        c.mBracket("：指定訪問看護ステーション名", "）")
                )
        );
        c.textIn("たんの吸引等実施のための訪問介護事業所への指示", rr[2].shift(2, 0),
                HAlign.Left, VAlign.Center);
        c.multi(
                rr[3].shift(10, 0).setRight(rightLimit),
                VAlign.Center,
                List.of(
                        c.mLabel("（"),
                        c.mLabel("無"),
                        c.mSpace(fontSize),
                        c.mLabel("有"),
                        c.mSpace(fontSize),
                        c.mBracket("：指定訪問介護事業所名", "）")
                )
        );
    }

    private void renderAddr(Box box) {
        Box[] rr = box.splitToEvenRows(6);
        double fontSize = c.getCurrentFontSize();
        c.multi(
                rr[0].shift(c.getCurrentFontSize() * 4, 0),
                VAlign.Center,
                List.of(
                        c.mLabel("令和"),
                        c.mSpace(9).mark("issue-date.nen", "発行日（年）")
                                .addHints(Hints.right(), Hints.rightPadding(1)),
                        c.mLabel("年"),
                        c.mSpace(6).mark("issue-date.month", "発行日（月）")
                                .addHints(Hints.right(), Hints.rightPadding(1)),
                        c.mLabel("月"),
                        c.mSpace(6).mark("issue-date.day", "発行日（日）")
                                .addHints(Hints.right(), Hints.rightPadding(1)),
                        c.mLabel("日")
                )
        );
        c.multi(
                rr[1],
                VAlign.Center,
                List.of(
                        c.mLabel("医療機関名"),
                        c.mSpace(fontSize),
                        c.mBracket("", "clinic.name", "医療機関名", List.of(Hints.xPadding(2), Hints.font("regular")), "")
                )
        );
        c.multi(
                rr[2],
                VAlign.Center,
                List.of(
                        c.mJustified("住所", fontSize * 5),
                        c.mSpace(fontSize),
                        c.mBracket("", "clinic.address", "医療機関（住所）", List.of(Hints.xPadding(2), Hints.font("regular")), "")
                )
        );
        c.multi(
                rr[3],
                VAlign.Center,
                List.of(
                        c.mJustified("電話", fontSize * 5),
                        c.mSpace(fontSize),
                        c.mBracket("", "clinic.phone", "医療機関（電話）", List.of(Hints.xPadding(2), Hints.font("regular")), "")
                )
        );
        c.multi(
                rr[4],
                VAlign.Center,
                List.of(
                        c.mLabel("（ＦＡＸ）"),
                        c.mSpace(fontSize),
                        c.mBracket("", "clinic.fax", "医療機関（ＦＡＸ）", List.of(Hints.xPadding(2), Hints.font("regular")), "")
                )
        );
        c.multi(
                rr[5],
                VAlign.Center,
                List.of(
                        c.mJustified("医師氏名", fontSize * 5),
                        c.mSpace(fontSize),
                        c.mSpace(70).mark("doctor-name", "医師氏名").addHints(
                                Hints.rightPadding(30), Hints.font("regular")
                        ),
                        c.mLabel("印")
                )
        );

    }

}
