package dev.myclinic.vertx.rcpt.data;

import dev.myclinic.vertx.client.Service;
import dev.myclinic.vertx.consts.ConductKind;
import dev.myclinic.vertx.consts.DiseaseEndReason;
import dev.myclinic.vertx.consts.DrugCategory;
import dev.myclinic.vertx.dto.*;
import dev.myclinic.vertx.rcpt.create.Gendogaku;
import dev.myclinic.vertx.util.DiseaseUtil;
import dev.myclinic.vertx.util.NumberUtil;
import dev.myclinic.vertx.util.RcptUtil;
import dev.myclinic.vertx.util.kanjidate.GengouNenPair;
import dev.myclinic.vertx.util.kanjidate.KanjiDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

// TODO: 翌月の病名を含めない
class Data {

    private static Logger logger = LoggerFactory.getLogger(Data.class);
    private int year;
    private int month;
    private List<Integer> patientIds;
    private Xml xml;

    Data(int year, int month, List<Integer> patientIds) {
        this.year = year;
        this.month = month;
        this.patientIds = patientIds;
        this.xml = new Xml();
    }

    void run() throws Exception {
        if (patientIds == null) {
            patientIds = Service.api.listVisitingPatientIdHavingHokenCall(year, month).execute().body();
        }
        System.err.printf("Patients %d\n", patientIds.size());
        xml.prelude();
        xml.element("レセプト", () -> {
            outProlog();
            for (int patientId : patientIds) {
                PatientDTO patient = getPatient(patientId);
                List<DiseaseFullDTO> diseases = getDiseases(patientId, year, month);
                diseases = diseases.stream()
                        .map(d -> {
                            String endDate = d.disease.endDate;
                            if (!endDate.equals("0000-00-00") && isDateLater(endDate, year, month)) {
                                d.disease.endDate = "0000-00-00";
                            }
                            return d;
                        }).collect(toList());
                List<VisitFull2DTO> visits = getVisits(patientId, year, month);
                // TODO: change to group by shahokokuho/koukikourei
                Map<HokenIds, List<VisitFull2DTO>> bundles = visits.stream()
                        .collect(Collectors.groupingBy(visit -> new HokenIds(visit.visit)));
                if (bundles.keySet().size() > 1) {
                    System.err.printf("Multiple hoken for (%d) %s%s\n", patient.patientId,
                            patient.lastName, patient.firstName);
                }
                for (HokenIds hokenIds : bundles.keySet()) {
                    List<VisitFull2DTO> bundle = bundles.get(hokenIds);
                    outPatient(patient, bundle, diseases);
                }
            }
        });
    }

    private boolean isDateLater(String date, int year, int month) {
        LocalDate d = LocalDate.parse(date);
        LocalDate nextDay = LocalDate.of(year, month, 1).plus(1, ChronoUnit.MONTHS);
        return d.isEqual(nextDay) || d.isAfter(nextDay);
    }

    private void outProlog() {
        LocalDate d = LocalDate.of(year, month, 1);
        GengouNenPair gn = KanjiDate.yearToGengou(d);
        ClinicInfoDTO info = getClinicInfo();
        xml.element("元号", gn.gengou.getKanjiRep());
        xml.element("年", gn.nen);
        xml.element("月", month);
        xml.element("都道府県番号", info.todoufukencode);
        xml.element("医療機関コード",
                String.format("%s.%s.%s",
                        info.kikancode.substring(0, 2),
                        info.kikancode.substring(2, 6),
                        info.kikancode.substring(6, 7)));
        xml.element("医療機関住所", info.address);
        String phone = info.tel;
        if (phone.contains("-")) {
            String[] phoneParts = info.tel.split("-");
            phone = String.format("%s (%s) %s", phoneParts[0], phoneParts[1], phoneParts[2]);
        }
        xml.element("医療機関電話", phone);
        xml.element("医療機関名称", info.name);
    }

    private ClinicInfoDTO getClinicInfo() {
        try {
            return Service.api.getClinicInfoCall().execute().body();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private PatientDTO getPatient(int patientId) {
        try {
            return Service.api.getPatientCall(patientId).execute().body();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private List<DiseaseFullDTO> getDiseases(int patientId, int year, int month) {
        try {
            return Service.api.listDiseaseByPatientAtCall(patientId,
                    year, month).execute().body();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private List<VisitFull2DTO> getVisits(int patientId, int year, int month) {
        try {
            return Service.api.listVisitByPatientHavingHokenCall(patientId,
                    year, month).execute().body();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void outPatient(PatientDTO patient, List<VisitFull2DTO> visits,
            List<DiseaseFullDTO> diseases) {
        if (visits.size() == 0) {
            System.err.println("No visits.");
            System.err.println(patient);
        }
        HokenDTO hoken = visits.get(0).hoken;
        String futan = getFutan(patient, hoken);
        String shouki = getShouki(visits.stream().map(v -> v.visit.visitId).collect(toList()));
        if (!shouki.isEmpty()) {
            System.err.printf("症状詳記（%d）%s%s：%s\n", patient.patientId,
                    patient.lastName, patient.firstName, shouki);
        }
        xml.unindent();
        xml.element("請求", () -> {
            String shubetsu = getShubetsu(hoken);
            xml.element("患者番号", patient.patientId);
            xml.element("保険種別", shubetsu);
            xml.element("保険単独", getTandoku(hoken));
            xml.element("保険負担", futan);
            xml.element("給付割合", getKyuufuWari(futan));
            {
                String tokki = tokkiJikou(patient, futan, shubetsu);
                if (!tokki.isEmpty()) {
                    xml.element("特記事項", tokki);
                }
            }
            outHokenDetail(hoken);
            xml.element("氏名", patient.lastName + "　" + patient.firstName);
            xml.element("性別",
                    patient.sex.equals("F") ? "女" : "男");
            xml.element("生年月日", patient.birthday);
            xml.element("症状詳記", shouki);
            diseases.forEach(this::outDisease);
            visits.forEach(this::outVisit);
        });
    }

    private String tokkiJikou(PatientDTO patient, String futan, String shubetsu) {
        LocalDate birthday = LocalDate.parse(patient.birthday);
        int age = RcptUtil.calcRcptAge(birthday.getYear(), birthday.getMonthValue(),
                birthday.getDayOfMonth(), year, month);
        if (age >= 70) {
            String tokkijikou = Gendogaku.getTokijikou(patient.patientId);
            if (tokkijikou != null) {
                return tokkijikou;
            } else /* if (shubetsu.equals("後期高齢")) */{
                if (futan.equals("高齢７")) {
                    return "26区ア";
                } else if (futan.equals("高齢８")) {
                    return "41区カ";
                } else {
                    return "42区キ";
                }
            }/* else { // 高齢受給（限度額適用認定証なし）
                // return "29区エ";
                if( futan.equals("高齢７") ){
                    return ""
                }
            } */
        }
        return "";
    }

    private String getShouki(List<Integer> visitIds) {
        try {
            List<ShoukiDTO> shoukiList = Service.api.batchGetShoukiCall(visitIds).execute().body();
            return shoukiList.stream().map(s -> s.shouki).collect(Collectors.joining("\n"));
        } catch (IOException e) {
            logger.error("Failed to get shouki. {}", e);
            throw new UncheckedIOException(e);
        }
    }

    private String getShubetsu(HokenDTO hoken) {
        if (hoken.roujin != null) {
            return "老人";
        } else if (hoken.koukikourei != null) {
            return "後期高齢";
        } else if (hoken.shahokokuho != null) {
            int n = hoken.shahokokuho.hokenshaBangou / 1000000;
            if (n == 67 || (n >= 72 && n <= 75)) {
                return "退職";
            } else {
                return "社国";
            }
        } else if (hoken.kouhi1 != null || hoken.kouhi2 != null || hoken.kouhi3 != null) {
            return "公費";
        } else {
            return "????";
        }
    }

    private String getTandoku(HokenDTO hoken) {
        if (hoken.shahokokuho != null || hoken.roujin != null || hoken.koukikourei != null) {
            if (hoken.kouhi2 != null) {
                return "３併";
            } else if (hoken.kouhi1 != null) {
                return "２併";
            } else {
                return "単独";
            }
        } else {
            if (hoken.kouhi2 != null) {
                return "２併";
            } else {
                return "単独";
            }
        }
    }

    private String getFutan(PatientDTO patient, HokenDTO hoken) {
        if (patient.birthday != null && !patient.birthday.equals("0000-00-00")) {
            LocalDate birthday = LocalDate.parse(patient.birthday);
            int age = RcptUtil.calcRcptAge(birthday.getYear(), birthday.getMonthValue(),
                    birthday.getDayOfMonth(), year, month);
            if (age < 6) {
                return "六才未満";
            }
        }

        if (hoken.roujin != null) {
            switch (hoken.roujin.futanWari) {
                case 1:
                    return "高齢９";
                case 2:
                    return "高齢８";
                case 3:
                    return "高齢７";
                default:
                    return "??????";
            }
        } else if (hoken.koukikourei != null) {
            switch (hoken.koukikourei.futanWari) {
                case 1:
                    return "高齢９";
                case 2:
                    return "高齢８";
                case 3:
                    return "高齢７";
                default:
                    return "??????";
            }
        } else if (hoken.shahokokuho != null) {
            switch (hoken.shahokokuho.kourei) {
                case 1:
                    return "高齢９";
                case 2:
                    return "高齢８";
                case 3:
                    return "高齢７";
                default: {
                    return hoken.shahokokuho.honnin != 0 ? "本人" : "家族";
                }
            }
        } else {
            return "本人";
        }
    }

    private int getKyuufuWari(String futan) {
        switch (futan) {
            case "六才未満":
                return 8;
            case "高齢９":
                return 9;
            case "高齢８":
                return 8;
            default:
                return 7;
        }
    }

    private boolean needKouhiSwap(KouhiDTO k1, KouhiDTO k2) {
        int f1 = k1.futansha / 1000000;
        int f2 = k2.futansha / 1000000;
        return f1 == 88 && f2 == 82;
    }

    private void outHokenDetail(HokenDTO hoken) {
        if (hoken.shahokokuho != null) {
            ShahokokuhoDTO shaho = hoken.shahokokuho;
            xml.element("保険者番号", shaho.hokenshaBangou);
            xml.element("被保険者記号", shaho.hihokenshaKigou);
            xml.element("被保険者番号", shaho.hihokenshaBangou);
            xml.element("被保険者枝番", shaho.edaban);
        } else if (hoken.koukikourei != null) {
            KoukikoureiDTO kouki = hoken.koukikourei;
            xml.element("保険者番号", kouki.hokenshaBangou);
            xml.element("被保険者記号", "");
            xml.element("被保険者番号", kouki.hihokenshaBangou);
        }
        KouhiDTO kouhi1 = hoken.kouhi1;
        KouhiDTO kouhi2 = hoken.kouhi2;
        if (kouhi1 != null && kouhi2 != null && needKouhiSwap(kouhi1, kouhi2)) {
            KouhiDTO tmp = kouhi1;
            kouhi1 = kouhi2;
            kouhi2 = tmp;
        }
        if (kouhi1 != null) {
            xml.element("公費1負担者番号", kouhi1.futansha);
            xml.element("公費1受給者番号", kouhi1.jukyuusha);
        }
        if (kouhi2 != null) {
            xml.element("公費2負担者番号", kouhi2.futansha);
            xml.element("公費2受給者番号", kouhi2.jukyuusha);
        }
    }

    private void outDisease(DiseaseFullDTO disease) {
        xml.element("傷病名", () -> {
            xml.element("名称", DiseaseUtil.getFullName(disease));
            xml.element("診療開始日", disease.disease.startDate);
            if (disease.disease.endReason != DiseaseEndReason.NotEnded.getCode()) {
                if (!disease.disease.endDate.equals("0000-00-00")) { // for backward compatibility
                    xml.element("転帰", getTenki(disease.disease.endReason));
                    xml.element("診療終了日", disease.disease.endDate);
                }
            }
        });
    }

    private String getTenki(char endReason) {
        switch (endReason) {
            case 'S':
                return "中止";
            case 'C':
                return "治ゆ";
            case 'N':
                return "継続";
            case 'D':
                return "死亡";
            default:
                return "????";
        }
    }

    private void outVisit(VisitFull2DTO visit) {
        xml.element("受診", () -> {
            xml.element("受診日", visit.visit.visitedAt);
            List<ShinryouFullDTO> shinryouList = new ArrayList<>(visit.shinryouList);
            List<Integer> shinryouIds = shinryouList.stream().map(s -> s.shinryou.shinryouId).collect(toList());
            Map<Integer, ShinryouAttrDTO> shinryouAttrMap = collectShinryouAttr(shinryouIds);
            shinryouList.sort(Comparator.comparingInt(a -> a.shinryou.shinryouId)); // for backwork compatibility (not
                                                                                    // necessary for funtion)
            shinryouList.forEach(s -> outShinryou(s, shinryouAttrMap.get(s.shinryou.shinryouId)));
            List<Integer> drugIds = visit.drugs.stream().map(d -> d.drug.drugId).collect(toList());
            Map<Integer, DrugAttrDTO> drugAttrMap = collectDrugAttr(drugIds);
            outDrugs(visit.drugs, drugAttrMap);
            visit.conducts.forEach(this::outConduct);
        });
    }

    private Map<Integer, ShinryouAttrDTO> collectShinryouAttr(List<Integer> shinryouIds) {
        Map<Integer, ShinryouAttrDTO> map = new HashMap<>();
        try {
            List<ShinryouAttrDTO> list = Service.api.batchGetShinryouAttrCall(shinryouIds).execute().body();
            for (ShinryouAttrDTO attr : list) {
                map.put(attr.shinryouId, attr);
            }
        } catch (IOException e) {
            logger.error("Failed to get shinryou attr: {}", shinryouIds);
        }
        return map;
    }

    private Map<Integer, DrugAttrDTO> collectDrugAttr(List<Integer> drugIds) {
        Map<Integer, DrugAttrDTO> map = new HashMap<>();
        try {
            List<DrugAttrDTO> list = Service.api.batchGetDrugAttrCall(drugIds).execute().body();
            for (DrugAttrDTO attr : list) {
                map.put(attr.drugId, attr);
            }
        } catch (IOException e) {
            logger.error("Failed to get shinryou attr: {}", drugIds);
        }
        return map;
    }

    private void outShinryou(ShinryouFullDTO shinryou, ShinryouAttrDTO attr) {
        xml.element("診療", () -> {
            xml.element("診療コード", shinryou.master.shinryoucode);
            xml.element("名称", shinryou.master.name);
            xml.element("点数", shinryou.master.tensuu);
            xml.element("集計先", shinryou.master.shuukeisaki);
            if (!shinryou.master.houkatsukensa.equals("00")) {
                xml.element("包括検査", shinryou.master.houkatsukensa);
            }
            if (!shinryou.master.kensaGroup.equals("00")) {
                xml.element("検査グループ", shinryou.master.kensaGroup);
            }
            if (attr != null) {
                if (attr.tekiyou != null) {
                    xml.element("摘要", attr.tekiyou);
                }
            }
        });
    }

    private void outDrugs(List<DrugFullDTO> drugs, Map<Integer, DrugAttrDTO> attrMap) {
        if (drugs.size() > 0) {
            xml.element("投薬", () -> drugs.forEach(d -> outOneDrug(d, attrMap.get(d.drug.drugId))));
        }
    }

    private void outOneDrug(DrugFullDTO drug, DrugAttrDTO attr) {
        DrugCategory drugCategory = DrugCategory.fromCode(drug.drug.category);
        String category = "????";
        if (drugCategory == null) {
            System.err.println("Unknown drug category: " + drug.drug.category);
        } else {
            category = drugCategory.getKanji();
        }
        String tekiyouTmp = null;
        if (attr != null && attr.tekiyou != null && !attr.tekiyou.isEmpty()) {
            tekiyouTmp = attr.tekiyou;
        }
        final String tekiyou = tekiyouTmp;
        xml.element(category, () -> {
            xml.element("医薬品コード", drug.master.iyakuhincode);
            xml.element("名称", drug.master.name);
            xml.element("用量", NumberUtil.formatNumber(drug.drug.amount));
            xml.element("単位", drug.master.unit);
            xml.element("用法", drug.drug.usage);
            xml.element("薬価", "%.2f", drug.master.yakka);
            xml.element("麻毒", drug.master.madoku);
            if (drugCategory == DrugCategory.Naifuku) {
                xml.element("日数", drug.drug.days);
            } else if (drugCategory == DrugCategory.Tonpuku) {
                xml.element("回数", drug.drug.days);
            }
            if (tekiyou != null) {
                xml.element("摘要", tekiyou);
            }
        });
    }

    private void outConduct(ConductFullDTO conduct) {
        xml.element("行為", () -> {
            if (conduct.gazouLabel != null) {
                String label = conduct.gazouLabel.label;
                if (!label.isEmpty()) {
                    xml.element("ラベル", label);
                }
            }
            xml.element("種類", getConductKindLabel(conduct.conduct.kind));
            conduct.conductShinryouList.forEach(this::outConductShinryou);
            conduct.conductDrugs.forEach(this::outConductDrug);
            conduct.conductKizaiList.forEach(this::outConductKizai);
        });
    }

    private String getConductKindLabel(int code) {
        ConductKind kind = ConductKind.fromCode(code);
        if (kind == null) {
            throw new RuntimeException("Unknown conduct kind: " + code);
        } else {
            return kind.getKanjiRep();
        }
    }

    private void outConductShinryou(ConductShinryouFullDTO shinryou) {
        xml.element("診療", () -> {
            xml.element("診療コード", shinryou.master.shinryoucode);
            xml.element("名称", shinryou.master.name);
            xml.element("点数", shinryou.master.tensuu);
        });
    }

    private void outConductDrug(ConductDrugFullDTO drug) {
        xml.element("薬剤", () -> {
            xml.element("医薬品コード", drug.master.iyakuhincode);
            xml.element("名称", drug.master.name);
            xml.element("用量", drug.conductDrug.amount);
            xml.element("単位", drug.master.unit);
            xml.element("薬価", "%.2f", drug.master.yakka);
        });
    }

    private void outConductKizai(ConductKizaiFullDTO kizai) {
        xml.element("器材", () -> {
            xml.element("器材コード", kizai.master.kizaicode);
            xml.element("名称", kizai.master.name);
            xml.element("量", kizai.conductKizai.amount);
            xml.element("単位", kizai.master.unit);
            xml.element("金額", "%.2f", kizai.master.kingaku);
        });
    }

}
