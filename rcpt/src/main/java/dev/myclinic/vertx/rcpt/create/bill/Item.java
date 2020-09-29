package dev.myclinic.vertx.rcpt.create.bill;

import dev.myclinic.vertx.consts.ConductKind;
import dev.myclinic.vertx.consts.HoukatsuKensaKind;
import dev.myclinic.vertx.rcpt.create.input.*;
import dev.myclinic.vertx.rcpt.resolvedmap.ResolvedShinryouMap;
import dev.myclinic.vertx.util.NumberUtil;
import dev.myclinic.vertx.util.RcptUtil;
import dev.myclinic.vertx.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Item {
    private static Logger logger = LoggerFactory.getLogger(Item.class);

    public Object rep;
    public int tanka;
    public TekiyouProc tekiyouProc;
    public int count;

    public Item(Object rep, int tanka, TekiyouProc tekiyouProc, int count) {
        this.rep = rep;
        this.tanka = tanka;
        this.tekiyouProc = tekiyouProc;
        this.count = count;
    }

    public Item copy() {
        return new Item(rep, tanka, tekiyouProc, count);
    }

    public boolean canMerge(Item arg) {
        if (arg == null) {
            return false;
        }
        return Objects.equals(rep, arg.rep);
    }

    public static void add(List<Item> items, Item item) {
        for (Item i : items) {
            if (i.canMerge(item)) {
                i.count += item.count;
                return;
            }
        }
        items.add(item.copy());
    }

    public static Item fromShinryou(Shinryou shinryou, TekiyouProc tekiyouProc) {
        return new Item(
                new ShinryouRep(shinryou),
                shinryou.tensuu,
                tekiyouProc,
                1
        );
    }

    public static Item fromShinryou(Shinryou shinryou, Map<Integer, String> aliasMap) {
        String a = aliasMap.get(shinryou.shinryoucode);
        String name = a == null ? shinryou.name : a;
        return fromShinryou(shinryou,
                (output, shuukei, tanka, count) -> {
                    output.printTekiyou(shuukei, name, tanka, count);
                    if( shinryou.tekiyou != null && !shinryou.tekiyou.isEmpty()){
                        output.printTekiyouAux(shinryou.tekiyou);
                    }
                });
    }

    public static Item fromNaifukuCollector(NaifukuCollector collector) {
        return new Item(
                collector.getNaifukuRep(),
                collector.getTanka(),
                (output, shuukei, tanka, count) -> {
                    output.beginDrug(shuukei, tanka, count);
                    for (Naifuku drug : collector.getNaifukuList()) {
                        output.addDrug(drug.name, drug.amount, drug.unit);
                    }
                    output.endDrug();
                    boolean multiple = collector.getNaifukuList().size() > 1;
                    for(Naifuku drug: collector.getNaifukuList()){
                        if( drug.tekiyou != null && !drug.tekiyou.isEmpty() ){
                            if( multiple ){
                                String text = String.format("%s（%s）", drug.tekiyou, drug.name);
                                output.printTekiyouAux(text);
                            } else {
                                output.printTekiyouAux(drug.tekiyou);
                            }
                        }
                    }
                },
                collector.getDays()
        );
    }

    public static Item fromTonpuku(Tonpuku drug) {
        return new Item(
                new TonpukuRep(drug),
                RcptUtil.touyakuKingakuToTen(drug.amount * drug.yakka),
                (output, shuukei, tanka, count) -> {
                    output.beginDrug(shuukei, tanka, count);
                    output.addDrug(drug.name, drug.amount, drug.unit);
                    output.endDrug();
                    if( drug.tekiyou != null && !drug.tekiyou.isEmpty() ){
                        output.printTekiyouAux(drug.tekiyou);
                    }
                },
                drug.days
        );
    }

    private static Pattern gaiyouSheetsPerDayPattern = Pattern.compile(
            "[1１一]日\\s*([0-9０-９]+)枚"
    );

    public static Item fromGaiyou(Gaiyou drug, int patientId) {
        if (drug.unit.equals("枚")) {
            final String tekiyouText;
            if( drug.tekiyou == null ) {
                int timesPerDay = parseGaiyouTimesPerDay(drug.usage, 1);
                int sheetsPerTimes = parseGaiyouSheetsPerTimes(drug.usage, 1);
                String digits = String.format("%d", timesPerDay * sheetsPerTimes);
                String kanjiDigits = StringUtil.transliterate(digits, StringUtil::digitToKanji);
                tekiyouText = String.format("１日%s枚", kanjiDigits);
            } else {
                tekiyouText = drug.tekiyou;
            }
            Matcher matcher = gaiyouSheetsPerDayPattern.matcher(tekiyouText);
            if( !matcher.find() ){
                System.err.printf("１日何枚の記載がありません。patient_id: %d\n", patientId);
            }
            return new Item(
                    new GaiyouRep(drug),
                    RcptUtil.touyakuKingakuToTen(drug.amount * drug.yakka),
                    (output, shuukei, tanka, count) -> {
                        output.beginDrug(shuukei, tanka, count);
                        output.addDrug(drug.name, drug.amount, drug.unit);
                        output.endDrug();
                        if( tekiyouText != null && !tekiyouText.isEmpty() ) {
                            output.printTekiyouAux(tekiyouText);
                        }
                    },
                    1
            );
        } else {
            return new Item(
                    new GaiyouRep(drug),
                    RcptUtil.touyakuKingakuToTen(drug.amount * drug.yakka),
                    (output, shuukei, tanka, count) -> {
                        output.beginDrug(shuukei, tanka, count);
                        output.addDrug(drug.name, drug.amount, drug.unit);
                        output.endDrug();
                        if( drug.tekiyou != null && !drug.tekiyou.isEmpty() ){
                            output.printTekiyouAux(drug.tekiyou);
                        }
                    },
                    1
            );
        }
    }

    private static Pattern gaiyouTaimesPerDayPattern = Pattern.compile(
            "[1１一]日\\s*([0-9０-９]+)回"
    );

    private static Integer parseGaiyouTimesPerDay(String usage, Integer defaultValue) {
        Matcher matcher = gaiyouTaimesPerDayPattern.matcher(usage);
        if (matcher.find()) {
            String src = StringUtil.transliterate(matcher.group(1), StringUtil::kanjiToDigit);
            try {
                return Integer.parseInt(src);
            } catch (NumberFormatException ex) {
                logger.error("Invalid gaiyou times per day: {}", usage);
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    private static Pattern gaiyouSheetsPerTimesPattern = Pattern.compile(
            "[1１一]回\\s*([0-9０-９]+)枚"
    );

    private static Integer parseGaiyouSheetsPerTimes(String usage, Integer defaultValue) {
        Matcher matcher = gaiyouSheetsPerTimesPattern.matcher(usage);
        if (matcher.find()) {
            String src = StringUtil.transliterate(matcher.group(1), StringUtil::kanjiToDigit);
            try {
                return Integer.parseInt(src);
            } catch (NumberFormatException ex) {
                logger.error("Invalid gaiyou sheets per times: {}", usage);
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public static Item fromConductShinryou(ConductShinryou shinryou) {
        return new Item(
                new ConductShinryouRep(shinryou),
                shinryou.tensuu,
                (output, shuukei, tanka, count) -> output.printTekiyou(shuukei, shinryou.name, tanka, count),
                1
        );
    }

    public static Item fromConductDrug(SubShuukei subShuukei, ConductDrug drug) {
        int ten;
        if (subShuukei == SubShuukei.SUB_GAZOU) {
            ten = RcptUtil.shochiKingakuToTen(drug.yakka * drug.amount);
        } else {
            ten = RcptUtil.touyakuKingakuToTen(drug.yakka * drug.amount);
        }
        String label = conductDrugLabel(drug);
        return new Item(
                new ConductDrugRep(drug),
                ten,
                (output, shuukei, tanka, count) ->
                        output.printTekiyou(shuukei, label, tanka, count),
                1
        );
    }

    public static Item fromConductKizai(ConductKizai kizai) {
        String label = conductKizaiLabel(kizai);
        return new Item(
                new ConductKizaiRep(kizai),
                RcptUtil.kizaiKingakuToTen(kizai.kingaku * kizai.amount),
                (output, shuukei, tanka, count) ->
                        output.printTekiyou(shuukei, label, tanka, count),
                1
        );
    }

    private static String conductDrugLabel(ConductDrug d) {
        return String.format("%s %s%s", d.name, NumberUtil.formatNumber(d.amount), d.unit);
    }

    private static String conductKizaiLabel(ConductKizai kizai) {
        return String.format("%s %s%s", kizai.name, NumberUtil.formatNumber(kizai.amount), kizai.unit);
    }

    // For compatibility with previous version
    public static Item fromConduct(Conduct conduct) {
        ConductKind conductKind = ConductKind.fromKanjiRep(conduct.kind);
        int ten = 0;
        ten += conduct.shinryouList.stream().mapToInt(s -> s.tensuu).sum();
        Function<Double, Integer> drugKingakuConverter;
        if (conductKind == ConductKind.Gazou) {
            drugKingakuConverter = RcptUtil::shochiKingakuToTen;
        } else {
            drugKingakuConverter = RcptUtil::touyakuKingakuToTen;
        }
        ten += conduct.drugs.stream().mapToInt(d -> drugKingakuConverter.apply(d.yakka * d.amount)).sum();
        ten += conduct.kizaiList.stream().mapToInt(k -> RcptUtil.kizaiKingakuToTen(k.kingaku * k.amount)).sum();
        String label;
        if (conductKind == ConductKind.Gazou) {
            if ("胸部単純Ｘ線".equals(conduct.label)) {
                label = String.format("胸部単純Ｘ線（%s）",
                        conduct.kizaiList.stream().map(Item::conductKizaiLabel).collect(Collectors.joining("、")));
            } else {
                label = String.format("%s（%s）", conduct.label, collectConductLabels(conduct));
            }
        } else {
            label = collectConductLabels(conduct);
        }
        return new Item(
                new ConductRep(conduct),
                ten,
                (output, shuukei, tanka, count) -> output.printTekiyou(shuukei, label, tanka, count),
                1
        );
    }

    private static String collectConductLabels(Conduct conduct) {
        List<String> labelItems = new ArrayList<>();
        labelItems.addAll(conduct.shinryouList.stream().map(s -> s.name).collect(Collectors.toList()));
        labelItems.addAll(conduct.drugs.stream().map(Item::conductDrugLabel).collect(Collectors.toList()));
        labelItems.addAll(conduct.kizaiList.stream().map(Item::conductKizaiLabel).collect(Collectors.toList()));
        return String.join("、", labelItems);
    }

    public static Item fromHoukatsuKensa(HoukatsuKensaKind kind, List<Shinryou> list,
                                         HoukatsuKensaRevision.Revision revision) {
        return new Item(
                new HoukatsuKensaRep(kind, list),
                calcHoukatsuTen(revision, kind, list),
                (output, shuukei, tanka, count) ->
                        output.printTekiyou(shuukei, createHoukatsuKensaLabel(list), tanka, count),
                1
        );
    }

    private static int calcHoukatsuTen(HoukatsuKensaRevision.Revision revision,
                                       HoukatsuKensaKind kind, List<Shinryou> list) {
        return revision.calcTen(kind, list.size()).orElseGet(() ->
                list.stream().mapToInt(shinryou -> shinryou.tensuu).sum());
    }

    private static String createHoukatsuKensaLabel(List<Shinryou> list) {
        return list.stream().map(shinryou -> shinryou.name).collect(Collectors.joining("、"));
    }

    public static Item fromHandanryouList(List<Shinryou> handanryouList, ResolvedShinryouMap resolvedShinryouMap) {
        String label = "（判）" + handanryouList.stream().map(s -> Item.rewriteHandanryouName(s, resolvedShinryouMap))
                .collect(Collectors.joining("、"));
        return new Item(
                new HandanryouListRep(handanryouList),
                handanryouList.stream().mapToInt(s -> s.tensuu).sum(),
                (output, shuukei, tanka, count) -> output.printTekiyou(shuukei, label, tanka, count),
                1
        );
    }

    private static String rewriteHandanryouName(Shinryou shinryou, ResolvedShinryouMap map) {
        int shinryoucode = shinryou.shinryoucode;
        if (shinryoucode == map.尿便検査判断料) {
            return "尿";
        } else if (shinryoucode == map.血液検査判断料) {
            return "血";
        } else if (shinryoucode == map.生化Ⅰ判断料) {
            return "生Ⅰ";
        } else if (shinryoucode == map.生化Ⅱ判断料) {
            return "生Ⅱ";
        } else if (shinryoucode == map.免疫検査判断料) {
            return "免";
        } else if (shinryoucode == map.微生物検査判断料) {
            return "微";
        } else if (shinryoucode == map.病理判断料) {
            return "病学";
        } else {
            logger.warn("Unknown handanryou: " + shinryou.name);
            return shinryou.name;
        }
    }

}
