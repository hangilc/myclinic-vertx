package dev.myclinic.vertx.rcpt.create.bill;

import dev.myclinic.vertx.consts.ConductKind;
import dev.myclinic.vertx.consts.HoukatsuKensaKind;
import dev.myclinic.vertx.rcpt.create.input.*;
import dev.myclinic.vertx.rcpt.create.output.Output;
import dev.myclinic.vertx.rcpt.resolvedmap.ResolvedShinryouMap;
import dev.myclinic.vertx.util.HokenUtil;
import dev.myclinic.vertx.util.kanjidate.KanjiDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static dev.myclinic.vertx.consts.MyclinicConsts.*;

class PatientBill {

    private static Logger logger = LoggerFactory.getLogger(PatientBill.class);
    private Seikyuu seikyuu;
    private Output out;
    private ResolvedShinryouMap resolvedShinryouMap;
    private Map<Integer, String> shinryouAliasMap;
    private HoukatsuKensaRevision.Revision houkatsuKensaRevision;
    private Map<SubShuukei, List<Item>> itemMap = new HashMap<>();
    private Shuukei shoshinShuukei = new Shuukei("shoshin", false, true);
    private List<String> shoshinKasan = new ArrayList<>();
    private Shuukei saishinSaishinShuukei = new Shuukei("saishin.saishin", true, true);
    private Shuukei saishinGairaiKanriShuukei = new Shuukei("saishin.gairaikanri", true, true);
    private Shuukei saishinJikangaiShuukei = new Shuukei("saishin.jikangai", true, true);
    private Shuukei saishinKyuujitsuShuukei = new Shuukei("saishin.kyuujitsu", true, true);
    private Shuukei saishinShinyaShuukei = new Shuukei("saishin.shinya", true, true);
    private Shuukei shidouShuukei = new Shuukei("shidou", false, false);
    private Shuukei zaitakuOushinShuukei = new Shuukei("zaitaku.oushin", false, true);
    private Shuukei zaitakuSonotaShuukei = new Shuukei("zaitaku.sonota", false, false);
    // TODO: (Zaitaku) add yakan, shinya, houmon, yakuzai
    private Shuukei touyakuNaifukuChouzaiShuukei = new Shuukei("touyaku.naifuku.chouzai", true, true);
    private Shuukei touyakuGaiyouChouzaiShuukei = new Shuukei("touyaku.gaiyou.chouzai", true, true);
    private Shuukei touyakuShohouShuukei = new Shuukei("touyaku.shohou", true, true);
    private Shuukei touyakuMadokuShuukei = new Shuukei("touyaku.madoku", false, true);
    private Shuukei touyakuChoukiShuukei = new Shuukei("touyaku.chouki", false, false);
    private Shuukei touyakuNaifukuYakuzai = new Shuukei("touyaku.naifuku.yakuzai", false, true);
    private Shuukei touyakuTonpukuYakuzai = new Shuukei("touyaku.tonpuku.yakuzai", false, true);
    private Shuukei touyakuGaiyouYakuzai = new Shuukei("touyaku.gaiyou.yakuzai", false, true);
    private Shuukei chuushaHikaShuukei = new Shuukei("chuusha.hika", false, true);
    private Shuukei chuushaJoumyakuShuukei = new Shuukei("chuusha.joumyaku", false, true);
    private Shuukei chuushaSonotaShuukei = new Shuukei("chuusha.sonota", false, true);
    private Shuukei shochiShuukei = new Shuukei("shochi", false, true);
    private Shuukei shujutsuShuukei = new Shuukei("shujutsu", false, true);
    private Shuukei kensaShuukei = new Shuukei("kensa", false, true);
    private Shuukei gazouShuukei = new Shuukei("gazou", false, true);
    private Shuukei sonotaShohousenShuukei = new Shuukei("sonota.shohousen", false, true);
    private Shuukei sonotaSonotaShuukei = new Shuukei("sonota.sonota", false, true);
    private List<KensaCollector> kensaCollectors = new ArrayList<>();

    PatientBill(Seikyuu seikyuu, Output output, ResolvedShinryouMap resolvedShinryouMap,
                Map<Integer, String> shinryouAliasMap, HoukatsuKensaRevision.Revision houkatsuKensaRevision) {
        this.seikyuu = seikyuu;
        this.out = output;
        this.resolvedShinryouMap = resolvedShinryouMap;
        this.shinryouAliasMap = shinryouAliasMap;
        this.houkatsuKensaRevision = houkatsuKensaRevision;
    }

    void run() {
        out.printInt("patient_id", seikyuu.patientId);
        out.printStr("hokenshubetsu", hokenShubetsuSlug(seikyuu.hokenShubetsu));
        out.printStr("hokentandoku", hokenTandokuSlug(seikyuu.hokenTandoku));
        out.printStr("hokenfutan", hokenFutanSlug(seikyuu.hokenFutan));
        if( seikyuu.tokkiJikou != null && !seikyuu.tokkiJikou.isEmpty() ){
            out.printStr("tokkijikou", seikyuu.tokkiJikou);
        }
        ifPositive(seikyuu.kouhiFutanshaBangou1, n -> {
            String value = String.format("%08d", n);
            out.printStr("kouhifutanshabangou1", value);
        });
        ifPositive(seikyuu.kouhiJukyuushaBangou1, n -> {
            String value = String.format("%07d", n);
            out.printStr("kouhifutaniryoujukyuushabangou1", value);
        });
        ifPositive(seikyuu.kouhiFutanshaBangou2, n -> {
            String value = String.format("%08d", n);
            out.printStr("kouhifutanshabangou2", value);
        });
        ifPositive(seikyuu.kouhiJukyuushaBangou2, n -> {
            String value = String.format("%07d", n);
            out.printStr("kouhifutaniryoujukyuushabangou2", value);
        });
        ifPositiveOrElse(seikyuu.hokenshaBangou,
                n -> out.printStr("hokenshabangou", formatHokenshaBangou(n)),
                () -> System.err.printf("%d: 保険者番号なし\n", seikyuu.patientId)
        );
        ifNotEmpty(seikyuu.hihokenshaKigou, seikyuu.hihokenshaBangou,
                (a, b) -> {
                    String value = String.format("%s     %s", a, b);
                    out.printStr("hihokenshashou", value);
                },
                () -> System.err.printf("%d: 被保険者記号番号なし\n", seikyuu.patientId)
        );
        out.printInt("kyuufuwariai", seikyuu.kyuufuWariai);
        out.printStr("shimei", seikyuu.shimei);
        out.printStr("seibetsu", seibetsuSlug(seikyuu.seibetsu));
        LocalDate birthday = LocalDate.parse(seikyuu.birthday);
        out.printStr("seinengappi.gengou", getGengouSlug(birthday));
        out.printInt("seinengappi.nen", getNen(birthday));
        out.printInt("seinengappi.tsuki", birthday.getMonthValue());
        out.printInt("seinengappi.hi", birthday.getDayOfMonth());
        runByoumei(seikyuu);
        out.printInt("shinryounissuu.hoken", calcShinryouNissuuHoken(seikyuu.visits));
        ifPositive(calcShinryouNissuuKouhi1(seikyuu.visits), kouhi1Count ->
                out.printInt("shinryounissuu.kouhi.1", kouhi1Count));
        ifPositive(calcShinryouNissuuKouhi2(seikyuu.visits), kouhi2Count ->
                out.printInt("shinryounissuu.kouhi.2", kouhi2Count));
        if( seikyuu.shouki != null && !seikyuu.shouki.isEmpty() ){
            out.printStr("shoujoushouki", seikyuu.shouki);
        }
        for (Visit visit : seikyuu.visits) {
            KensaCollector kensaCollector = new KensaCollector(resolvedShinryouMap);
            for (Shinryou shinryou : visit.shinryouList) {
                dispatchShinryou(shinryou, LocalDate.parse(visit.visitedAt.substring(0, 10)), kensaCollector);
            }
            for(Conduct conduct: visit.conducts){
                dispatchConduct(conduct);
            }
            kensaCollectors.add(kensaCollector);
        }
        handleNaifukuYakuzai();
        handleTonpukuYakuzai();
        handleGaiyouYakuzai();
        shoshinShuukei.print(out);
        shoshinKasan.forEach(kasan -> out.printStr("shoshinkasan", kasan));
        outputTekiyou(SubShuukei.SUB_SHOSHIN);
        saishinSaishinShuukei.print(out);
        saishinGairaiKanriShuukei.print(out);
        saishinJikangaiShuukei.print(out);
        saishinKyuujitsuShuukei.print(out);
        saishinShinyaShuukei.print(out);
        outputTekiyou(SubShuukei.SUB_SAISHIN);
        shidouShuukei.print(out);
        outputTekiyou(SubShuukei.SUB_SHIDOU);
        zaitakuOushinShuukei.print(out);
        zaitakuSonotaShuukei.print(out);
        outputTekiyou(SubShuukei.SUB_ZAITAKU);
        touyakuNaifukuChouzaiShuukei.print(out);
        touyakuGaiyouChouzaiShuukei.print(out);
        touyakuShohouShuukei.print(out);
        touyakuMadokuShuukei.print(out);
        touyakuChoukiShuukei.print(out);
        touyakuNaifukuYakuzai.print(out);
        touyakuTonpukuYakuzai.print(out);
        touyakuGaiyouYakuzai.print(out);
        outputTekiyou(SubShuukei.SUB_TOUYAKU_SHOHOU);
        outputTekiyou(SubShuukei.SUB_TOUYAKU_NAIFUKU);
        outputTekiyou(SubShuukei.SUB_TOUYAKU_TONPUKU);
        outputTekiyou(SubShuukei.SUB_TOUYAKU_GAIYOU);
        chuushaHikaShuukei.print(out);
        chuushaJoumyakuShuukei.print(out);
        chuushaSonotaShuukei.print(out);
        outputTekiyou(SubShuukei.SUB_CHUUSHA_HIKA);
        outputTekiyou(SubShuukei.SUB_CHUUSHA_JOUMYAKU);
        outputTekiyou(SubShuukei.SUB_CHUUSHA_SONOTA);
        shochiShuukei.print(out);
        outputTekiyou(SubShuukei.SUB_SHOCHI);
        shujutsuShuukei.print(out);
        outputTekiyou(SubShuukei.SUB_SHUJUTSU);
        handleKensa();
        kensaShuukei.print(out);
        outputTekiyou(SubShuukei.SUB_KENSA);
        gazouShuukei.print(out);
        outputTekiyou(SubShuukei.SUB_GAZOU);
        sonotaShohousenShuukei.print(out);
        sonotaSonotaShuukei.print(out);
        outputTekiyou(SubShuukei.SUB_SONOTA);
        int totalTen = calcTotalTen();
        out.printInt("kyuufu.hoken.seikyuuten", totalTen);
        outputKouhiJikofutan(totalTen);
    }

    private int getNen(LocalDate date){
        return KanjiDate.yearToGengou(date).nen;
    }

    private String hokenShubetsuSlug(String hokenShubetsu) {
        switch (hokenShubetsu) {
            case "社国":
                return "shakoku";
            case "公費":
                return "kouhi";
            case "老人":
                return "roujin";
            case "退職":
                return "taishoku";
            case "後期高齢":
                return "koukikourei";
            default:
                throw new RuntimeException("Unknown hoken shubetsu: " + hokenShubetsu);
        }
    }

    private String hokenTandokuSlug(String hokenTandoku) {
        switch (hokenTandoku) {
            case "単独":
                return "tandoku";
            case "２併":
                return "hei2";
            case "３併":
                return "hei3";
            default:
                throw new RuntimeException("Unknown hoken tandoku: " + hokenTandoku);
        }
    }

    private String hokenFutanSlug(String hokenFutan) {
        switch (hokenFutan) {
            case "本人":
                return "honnin";
            case "三才未満":
                return "sansai";
            case "六才未満":
                return "rokusai";
            case "家族":
                return "kazoku";
            case "高齢９":
                return "kourei9";
            case "高齢８":
                return "kourei8";
            case "高齢７":
                return "kourei7";
            default:
                throw new RuntimeException("Unknown hoken futan: " + hokenFutan);
        }
    }

    private void ifPositive(int n, Consumer<Integer> cb) {
        if (n > 0) {
            cb.accept(n);
        }
    }

    private void ifPositiveOrElse(int n, Consumer<Integer> presentCb, Runnable errorCb) {
        if (n > 0) {
            presentCb.accept(n);
        } else {
            errorCb.run();
        }
    }

    private void ifNotEmpty(String a, String b, BiConsumer<String, String> cb, Runnable errorCb) {
        if (a == null) {
            a = "";
        }
        if (b == null) {
            b = "";
        }
        if (a.isEmpty() && b.isEmpty()) {
            errorCb.run();
        } else {
            cb.accept(a, b);
        }
    }

    private void ifNotNull(String s, Consumer<String> cb) {
        if (s != null) {
            cb.accept(s);
        }
    }

    private String formatHokenshaBangou(int hokenshaBangou) {
        if (hokenshaBangou < 10000)
            return String.format("%04d", hokenshaBangou);
        if (hokenshaBangou <= 99999)
            return String.format("%06d", hokenshaBangou);
        if (hokenshaBangou >= 1000000 && hokenshaBangou <= 9999999)
            return String.format("%08d", hokenshaBangou);
        return "" + hokenshaBangou;
    }

    private String seibetsuSlug(String seibetsu) {
        switch (seibetsu) {
            case "男":
                return "otoko";
            case "女":
                return "onna";
            default:
                throw new RuntimeException("Unknown seibtsu: " + seibetsu);
        }
    }

    private String getGengouSlug(LocalDate date) {
        return KanjiDate.yearToGengou(date).gengou.getAlphaRep().toLowerCase();
        //return Gengou.fromEra(DateTimeUtil.getEra(date)).getRomaji();
    }

    private String getGengou(LocalDate date) {
        return KanjiDate.yearToGengou(date).gengou.getKanjiRep();
        //return Gengou.fromEra(DateTimeUtil.getEra(date)).getKanji();
    }

    private void runByoumei(Seikyuu seikyuu) {
        int index = 1;
        List<String> chiyu = new ArrayList<>();
        List<String> shibou = new ArrayList<>();
        List<String> chuushi = new ArrayList<>();
        for (Byoumei byoumei : seikyuu.byoumeiList) {
            LocalDate startDate = LocalDate.parse(byoumei.startDate);
            if (index <= 4) {
                out.printStr(String.format("shoubyoumei.%d", index), byoumei.name);
                out.printInt(String.format("shinryoukaishi.nen.%d", index), getNen(startDate));
                out.printInt(String.format("shinryoukaishi.tsuki.%d", index), startDate.getMonthValue());
                out.printInt(String.format("shinryoukaishi.hi.%d", index), startDate.getDayOfMonth());
            } else {
                out.printStr("shoubyoumei_extra",
                        String.format("%d:%s:%d:%d:%d",
                                index,
                                byoumei.name,
                                getNen(startDate),
                                startDate.getMonthValue(),
                                startDate.getDayOfMonth()));
            }
            final int currentIndex = index;
            ifNotNull(byoumei.endDate, s -> {
                LocalDate d = LocalDate.parse(s);
                String tenkiDate = String.format("%c%d.%02d.%02d", getGengou(d).charAt(0), getNen(d),
                        d.getMonthValue(), d.getDayOfMonth());
                String tenkiStr = String.format("%d(%s)", currentIndex, tenkiDate);
                switch (byoumei.tenki) {
                    case "治ゆ":
                        chiyu.add(tenkiStr);
                        break;
                    case "死亡":
                        shibou.add(tenkiStr);
                        break;
                    case "中止":
                        chuushi.add(tenkiStr);
                        break;
                }
            });
            index += 1;
        }
        if (chiyu.size() > 0) {
            out.printStr("tenki.chiyu", String.join(",", chiyu));
        }
        if (shibou.size() > 0) {
            out.printStr("tenki.shibou", String.join(",", shibou));
        }
        if (chuushi.size() > 0) {
            out.printStr("tenki.chuushi", String.join(",", chuushi));
        }
    }

    private int calcShinryouNissuuHoken(List<Visit> visits) {
        return (int) visits.stream().map(v -> v.visitedAt).distinct().count();
    }

    private int calcShinryouNissuuKouhi1(List<Visit> visits) {
        // TODO: implement kouhi1 nissuu
        return 0;
    }

    private int calcShinryouNissuuKouhi2(List<Visit> visits) {
        // TODO: implement kouhi2 nissuu
        return 0;
    }

    private void addItem(SubShuukei subShuukei, Item item) {
        if (itemMap.containsKey(subShuukei)) {
            List<Item> items = itemMap.get(subShuukei);
            Item.add(items, item);
        } else {
            List<Item> items = new ArrayList<>();
            items.add(item);
            itemMap.put(subShuukei, items);
        }
    }

    private void runShoshinKasan(int shinryoucode) {
//        if (shinryoucode == resolvedShinryouMap.初診時間外加算 ||
//                shinryoucode == resolvedShinryouMap.初診乳幼児時間外加算) {
//            shoshinKasan.add("jikangai");
//        }
//        if (shinryoucode == resolvedShinryouMap.初診休日加算 ||
//                shinryoucode == resolvedShinryouMap.初診乳幼児休日加算) {
//            shoshinKasan.add("kyuujitsu");
//        }
//        if (shinryoucode == resolvedShinryouMap.初診深夜加算 ||
//                shinryoucode == resolvedShinryouMap.初診乳幼児深夜加算) {
//            shoshinKasan.add("shinya");
//        }

    }

    private void dispatchShinryou(Shinryou shinryou, LocalDate visitedAt, KensaCollector kensaCollector) {
        switch (shinryou.shuukeisaki) {
            case SHUUKEI_SHOSHIN: {
                if (shinryou.shinryoucode == resolvedShinryouMap.初診) {
                    shoshinShuukei.add(shinryou.tensuu);
                    Item item = Item.fromShinryou(shinryou, TekiyouProc.noOutput);
                    addItem(SubShuukei.SUB_SHOSHIN, item);
                } else {
                    runShoshinKasan(shinryou.shinryoucode);
                    addItem(SubShuukei.SUB_SHOSHIN, Item.fromShinryou(shinryou, shinryouAliasMap));
                }
                break;
            }
            case SHUUKEI_SAISHIN_SAISHIN: {
                if (shinryou.shinryoucode == resolvedShinryouMap.再診) {
                    saishinSaishinShuukei.add(shinryou.tensuu);
                    Item item = Item.fromShinryou(shinryou, TekiyouProc.noOutput);
                    addItem(SubShuukei.SUB_SAISHIN, item);
                } else if (shinryou.shinryoucode == resolvedShinryouMap.同日再診) {
                    saishinSaishinShuukei.add(shinryou.tensuu);
                    addItem(SubShuukei.SUB_SAISHIN, Item.fromShinryou(shinryou, shinryouAliasMap));
                }
                break;
            }
            case SHUUKEI_SAISHIN_GAIRAIKANRI: {
                saishinGairaiKanriShuukei.add(shinryou.tensuu);
                addItem(SubShuukei.SUB_SAISHIN, Item.fromShinryou(shinryou, TekiyouProc.noOutput));
                break;
            }
            case SHUUKEI_SAISHIN_JIKANGAI: {
                saishinJikangaiShuukei.add(shinryou.tensuu);
                addItem(SubShuukei.SUB_SAISHIN, Item.fromShinryou(shinryou, TekiyouProc.noOutput));
                break;
            }
            case SHUUKEI_SAISHIN_KYUUJITSU: {
                saishinKyuujitsuShuukei.add(shinryou.tensuu);
                addItem(SubShuukei.SUB_SAISHIN, Item.fromShinryou(shinryou, TekiyouProc.noOutput));
                break;
            }
            case SHUUKEI_SAISHIN_SHINYA: {
                saishinShinyaShuukei.add(shinryou.tensuu);
                addItem(SubShuukei.SUB_SAISHIN, Item.fromShinryou(shinryou, TekiyouProc.noOutput));
                break;
            }
            case SHUUKEI_SHIDOU: {
                shidouShuukei.add(shinryou.tensuu);
                if (shinryou.shinryoucode == resolvedShinryouMap.診療情報提供料１ ||
                        shinryou.shinryoucode == resolvedShinryouMap.療養費同意書交付料) {
                    String dateLabel = KanjiDate.toKanji(visitedAt);
                    addItem(SubShuukei.SUB_SHIDOU, Item.fromShinryou(shinryou, (output, shuukei, tanka, count) -> {
                        output.printTekiyou(shuukei, shinryou.name, tanka, count);
                        output.printTekiyouAux(dateLabel);
                    }));
                } else {
                    addItem(SubShuukei.SUB_SHIDOU, Item.fromShinryou(shinryou, shinryouAliasMap));
                }
                break;
            }
            case SHUUKEI_ZAITAKU:
                if( shinryou.shinryoucode == resolvedShinryouMap.往診 ){
                    zaitakuOushinShuukei.add(shinryou.tensuu);
                    addItem(SubShuukei.SUB_ZAITAKU, Item.fromShinryou(shinryou, shinryouAliasMap));
                } else {
                    zaitakuSonotaShuukei.add(shinryou.tensuu);
                    if( shinryou.shinryoucode == resolvedShinryouMap.訪問看護指示料 ){
                        String dateLabel = KanjiDate.toKanji(visitedAt);
                        addItem(SubShuukei.SUB_ZAITAKU, Item.fromShinryou(shinryou, (output, shuukei, tanka, count) -> {
                            output.printTekiyou(shuukei, shinryou.name, tanka, count);
                            output.printTekiyouAux(dateLabel);
                        }));
                    } else {
                        addItem(SubShuukei.SUB_ZAITAKU, Item.fromShinryou(shinryou, shinryouAliasMap));
                    }
                }
                break;
            case SHUUKEI_TOUYAKU_NAIFUKUTONPUKUCHOUZAI:{
                touyakuNaifukuChouzaiShuukei.add(shinryou.tensuu);
                addItem(SubShuukei.SUB_TOUYAKU_NAIFUKU, Item.fromShinryou(shinryou, TekiyouProc.noOutput));
                break;
            }
            case SHUUKEI_TOUYAKU_GAIYOUCHOUZAI:{
                touyakuGaiyouChouzaiShuukei.add(shinryou.tensuu);
                addItem(SubShuukei.SUB_TOUYAKU_GAIYOU, Item.fromShinryou(shinryou, TekiyouProc.noOutput));
                break;
            }
            case SHUUKEI_TOUYAKU_SHOHOU:{
                if( shinryou.shinryoucode == resolvedShinryouMap.処方料 ||
                        shinryou.shinryoucode == resolvedShinryouMap.処方料７ ){
                    touyakuShohouShuukei.add(shinryou.tensuu);
                    addItem(SubShuukei.SUB_TOUYAKU_SHOHOU, Item.fromShinryou(shinryou, TekiyouProc.noOutput));
                } else {
                    touyakuShohouShuukei.addWithoutCount(shinryou.tensuu);
                    addItem(SubShuukei.SUB_TOUYAKU_SHOHOU, Item.fromShinryou(shinryou, shinryouAliasMap));
                }
                break;
            }
            case SHUUKEI_TOUYAKU_MADOKU:{
                touyakuMadokuShuukei.add(shinryou.tensuu);
                addItem(SubShuukei.SUB_TOUYAKU_MADOKU, Item.fromShinryou(shinryou, shinryouAliasMap));
                break;
            }
            case SHUUKEI_TOUYAKU_CHOUKI:{
                touyakuChoukiShuukei.add(shinryou.tensuu);
                addItem(SubShuukei.SUB_TOUYAKU_CHOUKI, Item.fromShinryou(shinryou, shinryouAliasMap));
                break;
            }
            case SHUUKEI_CHUUSHA_HIKA:{
                logger.warn("SHUUKEI_CHUUSHA_HIKA encountered (ignored)");
                chuushaHikaShuukei.add(shinryou.tensuu);
                addItem(SubShuukei.SUB_CHUUSHA_HIKA, Item.fromShinryou(shinryou, shinryouAliasMap));
                break;
            }
            case SHUUKEI_CHUUSHA_JOUMYAKU:{
                logger.warn("SHUUKEI_CHUUSHA_JOUMYAKU encountered (ignored)");
                chuushaJoumyakuShuukei.add(shinryou.tensuu);
                addItem(SubShuukei.SUB_CHUUSHA_JOUMYAKU, Item.fromShinryou(shinryou, shinryouAliasMap));
                break;
            }
            case SHUUKEI_CHUUSHA_OTHERS:{
                logger.warn("SHUUKEI_CHUUSHA_OTHERS encountered (ignored)");
                chuushaSonotaShuukei.add(shinryou.tensuu);
                addItem(SubShuukei.SUB_CHUUSHA_SONOTA, Item.fromShinryou(shinryou, shinryouAliasMap));
                break;
            }
            case SHUUKEI_CHUUSHA_SEIBUTSUETC:{
                logger.warn("SHUUKEI_CHUUSHA_SEIBUTSUETC encountered (ignored)");
                chuushaSonotaShuukei.add(shinryou.tensuu);
                addItem(SubShuukei.SUB_CHUUSHA_SONOTA, Item.fromShinryou(shinryou, shinryouAliasMap));
                break;
            }
            case SHUUKEI_SHOCHI:{
                shochiShuukei.add(shinryou.tensuu);
                addItem(SubShuukei.SUB_SHOCHI, Item.fromShinryou(shinryou, shinryouAliasMap));
                break;
            }
            case SHUUKEI_SHUJUTSU_SHUJUTSU: // fall through
            case SHUUKEI_SHUJUTSU_YUKETSU:  // fall through
            case SHUUKEI_MASUI:{
                shujutsuShuukei.add(shinryou.tensuu);
                addItem(SubShuukei.SUB_SHUJUTSU, Item.fromShinryou(shinryou, shinryouAliasMap));
                break;
            }
            case SHUUKEI_KENSA:{
                kensaCollector.add(shinryou);
                break;
            }
            case SHUUKEI_GAZOUSHINDAN: {
                logger.warn("Gazoushindann encountered in dispatch shinryou.");
                gazouShuukei.add(shinryou.tensuu);
                addItem(SubShuukei.SUB_GAZOU, Item.fromShinryou(shinryou, shinryouAliasMap));
                break;
            }
            case SHUUKEI_OTHERS: {
                if( shinryou.shinryoucode == resolvedShinryouMap.処方せん料 ||
                        shinryou.shinryoucode == resolvedShinryouMap.処方せん料７ ){
                    sonotaShohousenShuukei.add(shinryou.tensuu);
                    addItem(SubShuukei.SUB_SONOTA, Item.fromShinryou(shinryou, TekiyouProc.noOutput));
                } else {
                    //sonotaSonotaShuukei.add(shinryou.tensuu);
                    sonotaShohousenShuukei.addWithoutCount(shinryou.tensuu); // for backward compatibility
                    addItem(SubShuukei.SUB_SONOTA, Item.fromShinryou(shinryou, shinryouAliasMap));
                }
                break;
            }
            default:
                logger.warn("Unknown shuukei: " + shinryou.shuukeisaki);
                sonotaSonotaShuukei.add(shinryou.tensuu);
                addItem(SubShuukei.SUB_SONOTA, Item.fromShinryou(shinryou, shinryouAliasMap));
                break;
        }
    }

    private int calcTotalTen() {
        int ten = 0;
        for (List<Item> items : itemMap.values()) {
            for (Item item : items) {
                ten += item.tanka * item.count;
            }
        }
        return ten;
    }

    private void outputTekiyou(SubShuukei subShuukei) {
        outputTekiyou(itemMap.get(subShuukei), subShuukei);
    }

    private void outputTekiyou(List<Item> items, SubShuukei subShuukei){
        if( items == null ){
            return;
        }
        int n = items.size();
        boolean isFirst = true;
        for (Item item: items) {
            if( item.tekiyouProc == TekiyouProc.noOutput ){
                continue;
            }
            String shuukei;
            if (isFirst) {
                shuukei = "" + subShuukei.getCode();
                isFirst = false;
            } else {
                shuukei = "";
            }
            item.tekiyouProc.outputTekiyou(out, shuukei, item.tanka, item.count);
        }
    }

    private void handleNaifukuYakuzai(){
        List<Item> items = new ArrayList<>();
        for(Visit visit: seikyuu.visits){
            List<NaifukuCollector> collectors = NaifukuCollector.fromNaifukuList(visit.drug.naifukuList);
            for(NaifukuCollector collector: collectors){
                Item item = Item.fromNaifukuCollector(collector);
                Item.add(items, item);
            }
        }
        int count = items.stream().mapToInt(item -> item.count).sum();
        int ten = items.stream().mapToInt(item -> item.tanka * item.count).sum();
        touyakuNaifukuYakuzai.set(null, count, ten);
        items.forEach(item -> addItem(SubShuukei.SUB_TOUYAKU_NAIFUKU, item));
    }

    private void handleTonpukuYakuzai(){
        List<Item> items = new ArrayList<>();
        for(Visit visit: seikyuu.visits){
            for(Tonpuku tonpuku: visit.drug.tonpukuList){
                Item item = Item.fromTonpuku(tonpuku);
                Item.add(items, item);
            }
        }
        int count = items.stream().mapToInt(item -> item.count).sum();
        int ten = items.stream().mapToInt(item -> item.tanka * item.count).sum();
        touyakuTonpukuYakuzai.set(null, count, ten);
        items.forEach(item -> addItem(SubShuukei.SUB_TOUYAKU_TONPUKU, item));
    }

    private void handleGaiyouYakuzai(){
        List<Item> items = new ArrayList<>();
        for(Visit visit: seikyuu.visits){
            for(Gaiyou gaiyou : visit.drug.gaiyouList){
                Item item = Item.fromGaiyou(gaiyou, seikyuu.patientId);
                Item.add(items, item);
            }
        }
        int count = items.stream().mapToInt(item -> item.count).sum();
        int ten = items.stream().mapToInt(item -> item.tanka * item.count).sum();
        touyakuGaiyouYakuzai.set(null, count, ten);
        items.forEach(item -> addItem(SubShuukei.SUB_TOUYAKU_GAIYOU, item));
    }

    private void dispatchConduct(Conduct conduct){
        ConductKind kind = ConductKind.fromKanjiRep(conduct.kind);
        if (kind == null) {
            logger.error("Unknown conduct kind: " + conduct.kind);
            return;
        }
        switch (kind) {
            case HikaChuusha:{
                addConduct(chuushaHikaShuukei, SubShuukei.SUB_CHUUSHA_HIKA, conduct);
                break;
            }
            case JoumyakuChuusha:{
                addConduct(chuushaJoumyakuShuukei, SubShuukei.SUB_CHUUSHA_JOUMYAKU, conduct);
                break;
            }
            case OtherChuusha:{
                addConduct(chuushaSonotaShuukei, SubShuukei.SUB_CHUUSHA_SONOTA, conduct);
                break;
            }
            case Gazou: {
                addConduct(gazouShuukei, SubShuukei.SUB_GAZOU, conduct);
                break;
            }
            default:
                logger.error("Unknown conduct kind: " + kind);
                break;
        }
    }

    // TODO: seprate yakuzai for chuusha and gazou
    private void addConductImproved(Shuukei shuukei, SubShuukei subShuukei, Conduct conduct){
        List<Item> items = new ArrayList<>();
        for(ConductShinryou shinryou: conduct.shinryouList){
            Item item = Item.fromConductShinryou(shinryou);
            Item.add(items, item);
        }
        for(ConductDrug drug: conduct.drugs){
            Item item = Item.fromConductDrug(subShuukei, drug);
            Item.add(items, item);
        }
        for(ConductKizai kizai: conduct.kizaiList){
            Item item = Item.fromConductKizai(kizai);
            Item.add(items, item);
        }
        int ten = items.stream().mapToInt(item -> item.tanka * item.count).sum();
        shuukei.add(ten);
        items.forEach(item -> addItem(subShuukei, item));
    }

    // For compatibility with previsou version
    private void addConduct(Shuukei shuukei, SubShuukei subShuukei, Conduct conduct){
        Item item = Item.fromConduct(conduct);
        int ten = item.tanka * item.count;
        shuukei.add(ten);
        addItem(subShuukei, item);
    }

    private void handleKensa(){
        for(KensaCollector kensaCollector: kensaCollectors) {
            Map<HoukatsuKensaKind, List<Shinryou>> houkatsuMap = kensaCollector.getHoukatsuMap();
            for (HoukatsuKensaKind kind : houkatsuMap.keySet()) {
                List<Shinryou> list = houkatsuMap.get(kind);
                Item item = Item.fromHoukatsuKensa(kind, list, houkatsuKensaRevision);
                kensaShuukei.add(item.tanka);
                addItem(SubShuukei.SUB_KENSA, item);
            }
            List<Shinryou> handanryouList = kensaCollector.getHandanryouList();
            if (handanryouList.size() > 0) {
                Item item = Item.fromHandanryouList(handanryouList, resolvedShinryouMap);
                kensaShuukei.add(item.tanka);
                addItem(SubShuukei.SUB_KENSA, item);
            }
            for (Shinryou shinryou : kensaCollector.getShinryouList()) {
                Item item = Item.fromShinryou(shinryou, shinryouAliasMap);
                kensaShuukei.add(item.tanka);
                addItem(SubShuukei.SUB_KENSA, item);
            }
        }
    }

    private void outputKouhiJikofutan(int totalTen){
        if (seikyuu.kouhiFutanshaBangou1 > 0) {
            int futanWari = HokenUtil.kouhiFutanWari(seikyuu.kouhiFutanshaBangou1);
            if( seikyuu.kouhiFutanshaBangou2 > 0){
                int futanWari2 = HokenUtil.kouhiFutanWari(seikyuu.kouhiFutanshaBangou2);
                futanWari =  Math.min(futanWari, futanWari2);
            }
            int jikofutan = totalTen * futanWari;
            out.printInt("ichibu-futankin-kouhi-1", jikofutan);
        }
    }

}
