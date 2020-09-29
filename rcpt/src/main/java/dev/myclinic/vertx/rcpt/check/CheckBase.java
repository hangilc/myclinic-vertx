package dev.myclinic.vertx.rcpt.check;

import dev.myclinic.vertx.consts.DiseaseEndReason;
import dev.myclinic.vertx.consts.DrugCategory;
import dev.myclinic.vertx.consts.Madoku;
import dev.myclinic.vertx.consts.Zaikei;
import dev.myclinic.vertx.dto.*;
import dev.myclinic.vertx.rcpt.resolvedmap.ResolvedMap;
import dev.myclinic.vertx.rcpt.resolvedmap.ResolvedShinryouMap;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class CheckBase {

    //private static Logger logger = LoggerFactory.getLogger(CheckBase.class);
    private List<VisitFull2DTO> visits;
    private ResolvedMap resolvedMasterMap;
    private List<DiseaseFullDTO> diseases;
    private Fixer api;
    private Scope scope;

    CheckBase(Scope scope) {
        this.visits = scope.visits;
        this.resolvedMasterMap = scope.resolvedMasterMap;
        this.diseases = scope.diseases;
        this.api = scope.api;
        this.scope = scope;
    }

    void error(String msg) {
        error(msg, null, null);
    }

    void error(String msg, String fixMessage, Runnable fixer) {
        Error error = new Error(scope.patient, msg, fixMessage, fixer);
        scope.errorHandler.accept(error);
    }

    void forEachVisit(Consumer<VisitFull2DTO> cb) {
        visits.forEach(cb);
    }

    ResolvedShinryouMap getShinryouMaster() {
        return resolvedMasterMap.shinryouMap;
    }

    VisitFull2DTO findVisit(Predicate<VisitFull2DTO> pred) {
        return visits.stream().filter(pred).findFirst().orElseThrow(() -> {
            throw new RuntimeException("Cannot find visit");
        });
    }

    int countShinryou(VisitFull2DTO visit, Predicate<ShinryouFullDTO> pred) {
        return (int) visit.shinryouList.stream().filter(pred).count();
    }

    int countShinryouInVisits(Predicate<ShinryouFullDTO> pred) {
        return visits.stream().mapToInt(visit -> countShinryou(visit, pred)).sum();
    }

    int countShinryouMasterInVisits(int shinryoucode) {
        Predicate<ShinryouFullDTO> pred = s -> s.master.shinryoucode == shinryoucode;
        return countShinryouInVisits(pred);
    }

    int countShohousenGroupInVisits() {
        return countShinryouInVisits(s -> {
            int shinryoucode = s.master.shinryoucode;
            return shinryoucode == getShinryouMaster().処方せん料 ||
                    shinryoucode == getShinryouMaster().処方せん料７;
        });
    }

    void enterShinryou(VisitFull2DTO visit, int shinryoucode) {
        ShinryouDTO shinryou = new ShinryouDTO();
        shinryou.visitId = visit.visit.visitId;
        shinryou.shinryoucode = shinryoucode;
        api.enterShinryou(shinryou);
    }

    int countShinryouMaster(VisitFull2DTO visit, int shinryoucode) {
        return (int) visit.shinryouList.stream().filter(s -> s.master.shinryoucode == shinryoucode).count();
    }

    int countShoshinGroup(VisitFull2DTO visit) {
        return countShinryouMaster(visit, getShinryouMaster().初診);
    }

    int countSaishinGroup(VisitFull2DTO visit) {
        return countShinryouMaster(visit, getShinryouMaster().再診) +
                countShinryouMaster(visit, getShinryouMaster().同日再診);
    }

    List<ShinryouFullDTO> filterShinryou(VisitFull2DTO visit, Predicate<ShinryouFullDTO> pred) {
        return visit.shinryouList.stream().filter(pred).collect(Collectors.toList());
    }

    void removeExtraShinryouMasterInVisits(int shinryoucode, int toBeRemained) {
        List<Integer> shinryouIds = visits.stream().flatMap(visit -> visit.shinryouList.stream())
                .filter(s -> s.master.shinryoucode == shinryoucode)
                .skip(toBeRemained)
                .map(s -> s.shinryou.shinryouId)
                .collect(Collectors.toList());
        api.batchDeleteShinryou(shinryouIds);
    }

    void removeExtraShinryou(VisitFull2DTO visit, Predicate<ShinryouFullDTO> pred, int toBeRemained) {
        List<Integer> shinryouIds = visit.shinryouList.stream().filter(pred)
                .map(s -> s.shinryou.shinryouId)
                .skip(toBeRemained)
                .collect(Collectors.toList());
        api.batchDeleteShinryou(shinryouIds);
    }

    void removeExtraShinryouMaster(VisitFull2DTO visit, int shinryoucode, int toBeRemained) {
        removeExtraShinryou(visit, s -> s.master.shinryoucode == shinryoucode, toBeRemained);
    }

    void enterShohouryou(VisitFull2DTO visit) {
        enterShinryou(visit, getShinryouMaster().処方料);
    }

    void enterShohouryou7(VisitFull2DTO visit) {
        enterShinryou(visit, getShinryouMaster().処方料７);
    }

    void removeExtraShohouryou(VisitFull2DTO visit, int remain) {
        removeExtraShinryouMaster(visit, getShinryouMaster().処方料, remain);
    }

    void removeExtraShohouryou7(VisitFull2DTO visit, int remain) {
        removeExtraShinryouMaster(visit, getShinryouMaster().処方料７, remain);
    }

    void forEachShinryouInVisits(Consumer<ShinryouFullDTO> cb) {
        visits.stream().flatMap(visit -> visit.shinryouList.stream()).forEach(cb);
    }

    void forEachShinryou(VisitFull2DTO visit, Consumer<ShinryouFullDTO> cb) {
        visit.shinryouList.forEach(cb);
    }

    int countDrugInVisits(Predicate<DrugFullDTO> pred) {
        return visits.stream().mapToInt(visit -> countDrug(visit, pred)).sum();
    }

    List<DiseaseFullDTO> listDisease(VisitFull2DTO visit) {
        String at = visit.visit.visitedAt.substring(0, 10);
        return diseases.stream().filter(d -> isValidAt(d, at)).collect(Collectors.toList());
    }

    int countDisease(Predicate<DiseaseFullDTO> pred) {
        return (int) diseases.stream().filter(pred).count();
    }

    private boolean isValidAt(DiseaseFullDTO disease, String at) {
        String startDate = disease.disease.startDate;
        String endDate = disease.disease.endDate;
        return inTheInterval(startDate, endDate, at);
    }

    private boolean inTheInterval(String startDate, String endDate, String at) {
        return startDate.compareTo(at) <= 0 &&
                ("0000-00-00".equals(endDate) || at.compareTo(endDate) <= 0);
    }

    boolean diseaseStartsAt(DiseaseFullDTO disease, VisitFull2DTO visit) {
        String at = visit.visit.visitedAt.substring(0, 10);
        return disease.disease.startDate.equals(at);
    }

//    DiseaseNewDTO createNewDisease(VisitFull2DTO visit, ResolvedShinryouByoumei rsb) {
//        DiseaseNewDTO result = new DiseaseNewDTO();
//        DiseaseDTO disease = new DiseaseDTO();
//        disease.endDate = "0000-00-00";
//        disease.endReason = DiseaseEndReason.NotEnded.getCode();
//        disease.patientId = visit.visit.patientId;
//        disease.shoubyoumeicode = rsb.byoumei.code;
//        disease.startDate = visit.visit.visitedAt.substring(0, 10);
//        result.disease = disease;
//        result.adjList = rsb.shuushokugoList.stream()
//                .map(adj -> {
//                    DiseaseAdjDTO adjDTO = new DiseaseAdjDTO();
//                    adjDTO.shuushokugocode = adj.code;
//                    return adjDTO;
//                })
//                .collect(Collectors.toList());
//        return result;
//    }

    void enterDisease(DiseaseNewDTO disease) {
        api.enterDisease(disease);
    }

    List<DrugFullDTO> filterDrug(VisitFull2DTO visit, Predicate<DrugFullDTO> pred) {
        return visit.drugs.stream().filter(pred).collect(Collectors.toList());
    }

    boolean isMadoku(DrugFullDTO drug) {
        return Madoku.fromCode(drug.master.madoku) != Madoku.NoMadoku;
    }

    DrugCategory drugCategoryOf(DrugFullDTO drug) {
        return DrugCategory.fromCode(drug.drug.category);
    }

    boolean isNaifuku(DrugFullDTO drug) {
        return drugCategoryOf(drug) == DrugCategory.Naifuku;
    }

    boolean isTonpuku(DrugFullDTO drug) {
        return drugCategoryOf(drug) == DrugCategory.Tonpuku;
    }

    boolean isGaiyou(DrugFullDTO drug) {
        return drug.master.zaikei == Zaikei.Gaiyou.getCode();
        //return drugCategoryOf(drug) == DrugCategory.Gaiyou;
    }

    int countDrug(VisitFull2DTO visit, Predicate<DrugFullDTO> pred) {
        return (int) visit.drugs.stream().filter(pred).count();
    }

    int countShohouryou(VisitFull2DTO visit) {
        return countShinryouMaster(visit, getShinryouMaster().処方料);
    }

    int countShohouryou7(VisitFull2DTO visit) {
        return countShinryouMaster(visit, getShinryouMaster().処方料７);
    }

    boolean isChoukiNaifukuDrug(DrugFullDTO drug) {
        return isNaifuku(drug) && drug.drug.days > 14;
    }

    int countChoukiNaifukuDrug(VisitFull2DTO visit) {
        return countDrug(visit, this::isChoukiNaifukuDrug);
    }

    String messageForRemoveExtra(String name, int total, int remain) {
        if (remain > 0) {
            return String.format("%s(%d件中%d件)を削除します。", name, total, total - remain);
        } else {
            return String.format("%s(%d件)を削除します。", name, total);
        }
    }

}
