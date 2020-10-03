package dev.myclinic.drawerform2.houmonkango;

import dev.myclinic.drawerform2.FormCompiler;
import dev.myclinic.drawerform2.Hints;
import dev.myclinic.vertx.drawer.Box;
import dev.myclinic.vertx.drawer.DrawerCompiler;
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
            c.addMarkAndHints("address", cols[1], List.of(Hints.leftPadding(2)));
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
            c.addMarkAndHints("disease", cols[1], List.of(Hints.leftPadding(2)));
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
        for(int i=0;i<rows.length-1;i++){
            c.frameBottom(rows[i]);
        };
        {
            Box box = rows[0];
            Box[] cc = box.splitToColumns(20.5);
            c.frameRight(cc[0]);
            Box[] subRows = cc[0].inset(2).splitToEvenRows(2);
            c.textIn("病状・治療", subRows[0], )
            c.textsInRows(
                    cc[0].shrinkAll(2).splitToEvenRows(2),
                    ["病状・治療", "状態"],
      [
            VAlign.CentersetSpacing(0, -0.5, -0.5, 0),
                    VAlign.CentersetSpacing(6),
      ],
    );
            c.addMarkAndHints(
                    "disease-condition",
                    cc[1],
                    "para:padding(2):v-top:leading(0.7)",
                    );
        }
//        {
//            let box = rows[1];
//            let cc: Box[] = box.splitToColumns(20.5);
//            c.frameRight(cc[0]);
//            c.textsInRows(
//                    cc[0].shrinkAll(2).splitToEvenRows(3),
//                    ["投与中の", "薬剤の用", "量・用法"],
//      [
//            VAlign.CentersetSpacing(1),
//                    VAlign.CentersetSpacing(1),
//                    VAlign.Center
//      ],
//    );
//            c.addMarkAndHints("drugs", cc[1], "para:padding(2):v-top:leading(0.7)");
//        }
//        {
//            let box = rows[2];
//            let cc: Box[] = box.splitToColumns(20.5);
//            c.frameRight(cc[0]);
//            c.textsInRows(
//                    cc[0].shrink(2, 2, 2, 3).splitToEvenRows(2),
//                    ["日常生活", "自立度"],
//      [
//            VAlign.CentersetSpacing(1),
//                    VAlign.Center
//      ],
//    );
//            {
//                let [r1, r2] = cc[1].splitToEvenRows(2);
//                c.frameBottom(r1);
//                {
//                    let [c1, c2] = r1.splitToColumns(27.5);
//                    c.frameRight(c1);
//                    c.textIn(c1.displace(1.5, 0), "寝たきり度", VAlign.Center;
//                    c.textIn(
//                            c2.shrinkWidth(5, HorizAnchor.Right),
//                            "J1",
//                            VAlign.Center
//                            );
//                    c.addMarkAndHints("netakiri.J1", c.b, "circle:radius(1.7)");
//                    c.textIn(c.b.flipRight().displace(6, 0), "J2", VAlign.Center;
//                    c.addMarkAndHints("netakiri.J2", c.b, "circle:radius(1.7)");
//                    c.textIn(c.b.flipRight().displace(6, 0), "A1", VAlign.Center;
//                    c.addMarkAndHints("netakiri.A1", c.b, "circle:radius(1.7)");
//                    c.textIn(c.b.flipRight().displace(6, 0), "A2", VAlign.Center;
//                    c.addMarkAndHints("netakiri.A2", c.b, "circle:radius(1.7)");
//                    c.textIn(c.b.flipRight().displace(6, 0), "B1", VAlign.Center;
//                    c.addMarkAndHints("netakiri.B1", c.b, "circle:radius(1.7)");
//                    c.textIn(c.b.flipRight().displace(6, 0), "B2", VAlign.Center;
//                    c.addMarkAndHints("netakiri.B2", c.b, "circle:radius(1.7)");
//                    c.textIn(c.b.flipRight().displace(6, 0), "C1", VAlign.Center;
//                    c.addMarkAndHints("netakiri.C1", c.b, "circle:radius(1.7)");
//                    c.textIn(c.b.flipRight().displace(6, 0), "C2", VAlign.Center;
//                    c.addMarkAndHints("netakiri.C2", c.b, "circle:radius(1.7)");
//                }
//                {
//                    let [c1, c2] = r2.splitToColumns(27.5);
//                    c.frameRight(c1);
//                    c.textIn(c1.displace(1.5, 0), "認知症の状況", VAlign.Center;
//                    c.textIn(
//                            c2.shrinkWidth(5, HorizAnchor.Right),
//                            "Ｉ",
//                            VAlign.Center
//                            );
//                    c.addMarkAndHints("ninchi.1", c.b, "circle:radius(1.7)");
//                    c.textIn(
//                            c.b.flipRight().displace(6, 0),
//                            "IIa",
//                            VAlign.Center
//                            );
//                    c.addMarkAndHints("ninchi.2a", c.b, "circle:radius(1.7)");
//                    c.textIn(
//                            c.b.flipRight().displace(6, 0),
//                            "IIb",
//                            VAlign.Center
//                            );
//                    c.addMarkAndHints("ninchi.2b", c.b, "circle:radius(1.7)");
//                    c.textIn(
//                            c.b.flipRight().displace(6, 0),
//                            "IIIa",
//                            VAlign.Center
//                            );
//                    c.addMarkAndHints("ninchi.3a", c.b, "circle:radius(1.7)");
//                    c.textIn(
//                            c.b.flipRight().displace(6, 0),
//                            "IIIb",
//                            VAlign.Center
//                            );
//                    c.addMarkAndHints("ninchi.3b", c.b, "circle:radius(1.7)");
//                    c.textIn(c.b.flipRight().displace(6, 0), "IV", VAlign.Center;
//                    c.addMarkAndHints("ninchi.4", c.b, "circle:radius(1.7)");
//                    c.textIn(c.b.flipRight().displace(6, 0), "Ｍ", VAlign.Center;
//                    c.addMarkAndHints("ninchi.M", c.b, "circle:radius(1.7)");
//                }
//            }
//        }
//        {
//            let box = rows[3];
//            let [c0, c1] = box.splitToColumns(48);
//            c.frameRight(c0);
//            c.textIn(c0.displace(2), "要介護認定の状況", vCenter.setSpacing(1.5));
//            c.textIn(c1.displace(3.5), "自立", vCenter);
//            c.addMarkAndHints("youkaigo.jiritsu", c.b, "circle:radius(1.7)");
//            c.textIn(c.b.flipRight().displace(6), "要支援（", vCenter);
//            c.textIn(c.b.flipRight(), "１", vCenter);
//            c.addMarkAndHints("youkaigo.youshien1", c.b, "circle:radius(1.7)");
//            c.textIn(c.b.flipRight().displace(3.5), "２", vCenter);
//            c.addMarkAndHints("youkaigo.youshien2", c.b, "circle:radius(1.7)");
//            c.textIn(c.b.flipRight(), "）", vCenter);
//            c.textIn(c.b.flipRight().displace(7.5), "要介護（", vCenter);
//            c.textIn(c.b.flipRight(), "１", vCenter);
//            c.addMarkAndHints("youkaigo.youkaigo1", c.b, "circle:radius(1.7)");
//            c.textIn(c.b.flipRight().displace(3), "２", vCenter);
//            c.addMarkAndHints("youkaigo.youkaigo2", c.b, "circle:radius(1.7)");
//            c.textIn(c.b.flipRight().displace(3), "３", vCenter);
//            c.addMarkAndHints("youkaigo.youkaigo3", c.b, "circle:radius(1.7)");
//            c.textIn(c.b.flipRight().displace(3), "４", vCenter);
//            c.addMarkAndHints("youkaigo.youkaigo4", c.b, "circle:radius(1.7)");
//            c.textIn(c.b.flipRight().displace(3), "５", vCenter);
//            c.addMarkAndHints("youkaigo.youkaigo5", c.b, "circle:radius(1.7)");
//            c.textIn(c.b.flipRight(), "）", vCenter);
//        }
//        {
//            let box = rows[4];
//            let [c0, c1] = box.splitToColumns(48);
//            c.frameRight(c0);
//            c.textIn(c0.displace(2), "要介護認定の状況", vCenter.setSpacing(2.5));
//            c.textIn(c1.displace(3.5), "NPUAP分類", vCenter);
//            c.textIn(c.b.flipRight().displace(6), "III度", vCenter);
//            c.textIn(c.b.flipRight().displace(2), "IV度", vCenter);
//            c.textIn(c.b.flipRight().displace(6), "DESIGN分類", vCenter);
//            c.textIn(c.b.flipRight().displace(2), "D3", vCenter);
//            c.textIn(c.b.flipRight().displace(3), "D4", vCenter);
//            c.textIn(c.b.flipRight().displace(3), "D5", vCenter);
//        }
//        {
//            let box = rows[5];
//            let [c0, c1] = box.splitToColumns(23.5);
//            let r3_r5_c1 = c1;
//            c.frameRight(c0);
//            let lines = c0.shrinkWidth(2, HorizAnchor.Right).allocTextLines(
//                    c.getCurrentFontSize(),
//                    2.0,
//                    2,
//                    );
//            let dy = box.cy - Box.vertCenterOfTextLines(lines);
//            lines = Box.displaceTextOfLines(lines, 0, dy);
//            c.textIn(lines[0], "装着・使用", textOpts);
//            c.textIn(lines[1], "医療機器等", textOpts);
//            {
//                let rr = c1.shrinkAll(2).splitToEvenRows(7);
//                {
//                    c.textIn(rr[0], "１", vCenter);
//                    c.textIn(c.b.flipRight(), "．自動腹膜灌流装置", vCenter);
//                    c.textIn(c.b.displaceLeftTo(rr[0].left + 46), "２", vCenter);
//                    c.textIn(c.b.flipRight(), "．透析液供給装置", vCenter);
//                    c.textIn(c.b.displaceLeftTo(rr[0].left + 90), "３", vCenter);
//                    c.textsWithIntervalsIn(
//                            c.b.flipRight(),
//                            ["．酸素療法（", "/min）"],
//          [12],
//                    vCenter,
//        );
//                }
//                {
//                    c.textIn(rr[1], "４", vCenter);
//                    c.textIn(c.b.flipRight(), "．吸引器", vCenter);
//                    c.textIn(c.b.displaceLeftTo(rr[0].left + 46), "５", vCenter);
//                    c.textIn(c.b.flipRight(), "．中心静脈栄養", vCenter);
//                    c.textIn(c.b.displaceLeftTo(rr[0].left + 90), "６", vCenter);
//                    c.textIn(c.b.flipRight(), "．輸液ポンプ", vCenter);
//                }
//                let commaPos: number;
//                let lastParenPos: number;
//                {
//                    let drawn, gaps;
//                    c.textIn(rr[2], "７", vCenter);
//                    c.textIn(c.b.flipRight(), "．経管栄養（", vCenter);
//                    c.textIn(c.b.flipRight(), "経鼻", vCenter);
//                    c.textIn(c.b.flipRight(), "・", vCenter);
//                    c.textIn(c.b.flipRight(), "胃ろう", vCenter);
//        [drawn, gaps] = c.textsWithIntervalsIn(
//                        c.b.flipRight(),
//                        ["：チューブサイズ", "、", "日に１回交換）"],
//          [21, 15],
//                    vCenter,
//        );
//                    commaPos = drawn[1].left;
//                    lastParenPos = drawn[2].right - c.getCurrentFontSize();
//                }
//                {
//                    c.textIn(rr[3], "８", vCenter);
//                    c.textIn(c.b.flipRight(), "．留置カテーテル（サイズ", vCenter);
//                    let markTubeSize = c.b.flipRight().setRight(commaPos);
//                    c.textsWithIntervalsIn(
//                            markTubeSize.flipRight(),
//                            ["、", "日に１回交換）"],
//          [15],
//                    vCenter,
//        );
//                }
//                {
//                    c.multi(
//                            rr[4],
//                            ["９", "．人工呼吸器（", "陽圧式", "・", "陰圧式", "：設定"],
//                    VAlign.Center,
//        );
//                    let p = c.b.right;
//                    let q = lastParenPos;
//                    let mark = new Box(p, c.b.top, q, c.b.bottom);
//                    c.textIn(mark.flipRight(), "）", VAlign.Center;
//                }
//                {
//                    c.multi(
//                            rr[5],
//                            ["１０", "．気管カニューレ（サイズ", 11, "）"],
//                    VAlign.Center,
//        );
//                }
//                {
//                    c.multi(
//                            rr[6],
//                            [
//                            "１１",
//                            "．人工肛門",
//                            18,
//                            multiText("１２").mark("mark11"),
//                            "．人工膀胱",
//                            10,
//                            "１３",
//                            "．その他（",
//                            multiSpace(35).setRight(lastParenPos),
//                            "）",
//          ],
//                    VAlign.Center,
//        );
//                }
//            }
//        }
    }

    private void renderRow4(Box row) {
//        let [r1, r2] = row.splitToEvenRows(2);
//        let m1 = c.multi(r1.displace(2), [multiText("留意事項及び指示事項").mark(":t1")], VAlign.Center);
//        // c.textIn(r1.displace(2), multiText("留意事項及び指示事項").mark(":t1"), VAlign.Center;
//        let m2 = c.multi(
//                r2.displace(2),
//                [
//                multiText("I").setWidth(c.getCurrentFontSize()).setOpts(
//                        textOpts.halignCenter(),
//                        ),
//        multiText("療養生活指導上の留意事項").mark(":t2"),
//    ],
//        VAlign.Center,
//  );
//        let t1 = m1.t1;
//        let t2 = m2.t2;
//        let left = Math.max(t1.right, t2.right);
//        let b = row.setLeft(left).shrink(2, 1, 1, 1);
//        c.addMarkAndHints("ryuui-jikou", b, "para:v-top");
    }

    private void renderRow5(Box row) {
//        let [c1, c2] = row.splitToColumns(8.5);
//        c.multi(
//                c1.displace(2).splitToEvenRows(4)[0],
//                [
//                multiText("II").setWidth(c.getCurrentFontSize()).setOpts(
//                        textOpts.halignCenter(),
//                        ),
//    ],
//        VAlign.Center,
//  );
//        let rr = c2.splitToEvenRows(4);
//        c.multi(
//                rr[0],
//                [
//                multiText("１").mark("rehabilitation.mark").hint("circle:radius(1.5)"),
//                "．リハビリテーション",
//                multiSpace(0).setRight(row.right).mark("rehabilitation")
//                        .hint("x-padding(4)"),
//    ],
//        VAlign.Center,
//  );
//        c.multi(rr[1], ["２", "．褥瘡の処置など"], VAlign.Center);
//        c.multi(rr[2], ["３", "．装置・使用機器等の操作援助・管理"], VAlign.Center);
//        c.multi(rr[3], ["４", "．その他"], VAlign.Center);
    }

    private void renderRow6(Box row) {
//        let rr = row.splitToEvenRows(2);
//        c.textIn(
//                rr[0].displace(2),
//                "在宅患者訪問点滴注射に関する指示（投与薬剤・投与量・投与方法等）",
//                VAlign.Center
//                );

    }

    private void renderRow7(Box row) {
//        let rr = row.splitToEvenRows(2);
//        let d1 = c.textIn(
//                rr[0].displace(2),
//                "緊急時の連絡先",
//                VAlign.Center
//                );
//        let mark1 = d1.flipRight().setRight(row.right);
//        let d2 = c.textIn(
//                rr[1].displace(2),
//                "不在時の対応法",
//                VAlign.Center
//                );
//        let mark2 = d2.flipRight().setRight(row.right);

    }

    private void renderRow8(Box row) {
//        let rr = row.splitToEvenRows(3);
//        c.textIn(rr[0].displace(2), "特記すべき留意事項", VAlign.Center;
//        c.saveFont();
//        c.setFont("small");
//        c.textIn(
//                c.b.flipRight(),
//                "（注：薬の相互作用・副作用についての留意点、薬物アレルギーの既往、定期巡回・随時対応型訪問",
//                VAlign.Center
//                );
//        c.textIn(
//                rr[1].displace(2),
//                "介護看護及び複合型サービス利用時の留意事項等があれば記載して下さい。）",
//                VAlign.Center
//                );
//        c.restoreFont();
//        let mark = rr[2];
    }

    private void renderRow9(Box row) {
//        let rr = row.splitToEvenRows(4);
//        let fontSize = c.getCurrentFontSize();
//        let rightLimit = row.right - 15;
//        c.textIn(rr[0].displace(2), "他の訪問看護ステーションへの指示", vCenter);
//        c.multi(
//                rr[1].displace(10),
//                [
//                "（",
//                "無",
//                fontSize,
//                "有",
//                fontSize,
//                "：指定訪問看護ステーション名",
//                multiSpace(10).setRight(rightLimit),
//                "）",
//    ],
//        VAlign.Center,
//  );
//        c.textIn(rr[2].displace(2), "たんの吸引等実施のための訪問介護事業所への指示", vCenter);
//        c.multi(
//                rr[3].displace(10),
//                [
//                "（",
//                "無",
//                fontSize,
//                "有",
//                fontSize,
//                "：指定訪問介護事業所名",
//                multiSpace(10).setRight(rightLimit),
//                "）",
//    ],
//        VAlign.Center,
//  );
    }

    private void renderAddr(Box box) {
//        let rr = box.splitToEvenRows(6);
//        let fontSize = c.getCurrentFontSize();
//        c.multi(
//                rr[0].displace(c.getCurrentFontSize() * 4),
//                [
//                "令和",
//                multiSpace(9).mark("issue-date.nen").hint(
//                        "right:right-padding(1)",
//                        ),
//                "年",
//                multiSpace(6).mark("issue-date.month").hint(
//                        "right:right-padding(1)",
//                        ),
//                "月",
//                multiSpace(6).mark("issue-date.day").hint(
//                        "right:right-padding(1)",
//                        ),
//                "日",
//    ],
//        VAlign.Center,
//  );
//        c.multi(
//                rr[1],
//                [
//                "医療機関名",
//                fontSize,
//                multiSpace(0).setRight(box.right).mark("clinic.name").hint(
//                        "x-padding(2)",
//                        ),
//    ],
//        VAlign.Center,
//  );
//        c.multi(
//                rr[2],
//                [
//                multiText("住所").setOpts(textOpts.halignJustified()).setWidth(
//                        fontSize * 5,
//                        ),
//                fontSize,
//                multiSpace(10).setRight(box.right).mark("clinic.address")
//                        .hint("x-padding(2)"),
//    ],
//        VAlign.Center,
//  );
//        c.multi(
//                rr[3],
//                [
//                multiText("電話").setOpts(textOpts.halignJustified()).setWidth(
//                        fontSize * 5,
//                        ),
//                fontSize,
//                multiSpace(10).setRight(box.right).mark("clinic.phone").hint(
//                        "x-padding(2)",
//                        ),
//    ],
//        VAlign.Center,
//  );
//        c.multi(
//                rr[4],
//                [
//                "（ＦＡＸ）",
//                fontSize,
//                multiSpace(10).setRight(box.right).mark("clinic.fax").hint(
//                        "x-padding(2)",
//                        ),
//    ],
//        VAlign.Center,
//  );
//        c.multi(
//                rr[5],
//                [
//                multiText("医師氏名").setOpts(textOpts.halignJustified()).setWidth(
//                        fontSize * 5,
//                        ),
//                fontSize,
//                multiSpace(70).mark("doctor-name").hint("right-padding(30)"),
//                "印",
//    ],
//        VAlign.Center,
//  );

    }

}
