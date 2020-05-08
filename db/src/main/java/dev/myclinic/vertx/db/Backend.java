package dev.myclinic.vertx.db;

import dev.myclinic.vertx.consts.ConductKind;
import dev.myclinic.vertx.consts.DrugCategory;
import dev.myclinic.vertx.consts.PharmaQueueState;
import dev.myclinic.vertx.consts.WqueueWaitState;
import dev.myclinic.vertx.db.annotation.ExcludeFromFrontend;
import dev.myclinic.vertx.db.exception.CannotDeleteVisitSafelyException;
import dev.myclinic.vertx.dto.*;
import dev.myclinic.vertx.hotlinelogevent.HotlineLogger;
import dev.myclinic.vertx.practicelogevent.PracticeLogger;
import dev.myclinic.vertx.util.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.myclinic.vertx.db.Query.NullableProjector;
import static dev.myclinic.vertx.db.Query.Projector;
import static dev.myclinic.vertx.db.SqlTranslator.TableInfo;
import static java.util.stream.Collectors.*;

// TODO: check listTodaysPracticeLogBefore

public class Backend {
    private final static Logger logger = LoggerFactory.getLogger(dev.myclinic.vertx.db.Backend.class);
    private final TableSet ts;
    private final Query query;
    private final List<PracticeLogDTO> practiceLogs = new ArrayList<>();
    private final PracticeLogger practiceLogger;
    private final List<HotlineLogDTO> hotlineLogs = new ArrayList<>();
    private final HotlineLogger hotlineLogger;
    private final SqlTranslator sqlTranslator = new SqlTranslator();

    public Backend(TableSet ts, Query query) {
        this.ts = ts;
        this.query = query;
        this.practiceLogger = new PracticeLogger(dto -> {
            enterPracticeLog(dto);
            practiceLogs.add(dto);
        });
        this.hotlineLogger = new HotlineLogger(hotlineLogs::add);
    }
    private static <S, T, U> Projector<U> biProjector(Projector<S> p1, Projector<T> p2, BiFunction<S, T, U> f) {
        return (rs, ctx) -> {
            S s = p1.project(rs, ctx);
            T t = p2.project(rs, ctx);
            return f.apply(s, t);
        };
    }

    private final Projector<Integer> intProjector =
            (rs, ctx) -> rs.getInt(ctx.nextIndex());

    private final Projector<String> stringProjector =
            (rs, ctx) -> rs.getString(ctx.nextIndex());

    private int enterPracticeLog(PracticeLogDTO practiceLog) {
        ts.practiceLogTable.insert(query, practiceLog);
        return practiceLog.serialId;
    }

    @ExcludeFromFrontend
    public Query getQuery() {
        return query;
    }

    @ExcludeFromFrontend
    public String xlate(String sqlOrig, TableInfo tableInfo) {
        return sqlTranslator.translate(sqlOrig, tableInfo);
    }

    @ExcludeFromFrontend
    private String xlate(String sqlOrig, TableInfo tableInfo1, String alias1,
                         TableInfo tableInfo2, String alias2) {
        return sqlTranslator.translate(sqlOrig, tableInfo1, alias1, tableInfo2, alias2);
    }

    @ExcludeFromFrontend
    private String xlate(String sqlOrig, TableInfo tableInfo1, String alias1,
                         TableInfo tableInfo2, String alias2, TableInfo tableInfo3, String alias3) {
        return sqlTranslator.translate(sqlOrig, tableInfo1, alias1, tableInfo2, alias2,
                tableInfo3, alias3);
    }

    @ExcludeFromFrontend
    private String xlate(String sqlOrig, TableInfo tableInfo1, String alias1,
                         TableInfo tableInfo2, String alias2, TableInfo tableInfo3, String alias3,
                         TableInfo tableInfo4, String alias4) {
        return sqlTranslator.translate(sqlOrig, tableInfo1, alias1, tableInfo2, alias2,
                tableInfo3, alias3, tableInfo4, alias4);
    }

    private int numberOfPages(int totalItems, int itemsPerPage) {
        if (totalItems == 0) {
            return 0;
        }
        return (totalItems + itemsPerPage - 1) / itemsPerPage;
    }
    // Patient //////////////////////////////////////////////////////////////////////////

    public int enterPatient(PatientDTO patient) {
        ts.patientTable.insert(query, patient);
        practiceLogger.logPatientCreated(patient);
        return patient.patientId;
    }

    public PatientDTO getPatient(int patientId) {
        return ts.patientTable.getById(query, patientId);
    }

    public void updatePatient(PatientDTO patient) {
        PatientDTO prev = getPatient(patient.patientId);
        if (prev == null) {
            throw new RuntimeException("Cannot find previous patient to update: " + patient);
        }
        ts.patientTable.update(query, patient);
        practiceLogger.logPatientUpdated(prev, patient);
    }

    public List<PatientDTO> searchPatientByKeyword2(String lastNameKeyword, String firstNameKeyword) {
        String sql = xlate("select * from Patient where " +
                        " (lastName like ? or lastNameYomi like ?) and " +
                        " (firstName like ? or firstNameYomi like ?) ",
                ts.patientTable);
        return getQuery().query(sql, ts.patientTable,
                lastNameKeyword, lastNameKeyword, firstNameKeyword, firstNameKeyword);
    }

    public List<PatientDTO> searchPatientByKeyword(String keyword) {
        String sql = xlate("select * from Patient where " +
                        " (lastName like ? or lastNameYomi like ?) or " +
                        " (firstName like ? or firstNameYomi like ?) ",
                ts.patientTable);
        return getQuery().query(sql, ts.patientTable,
                keyword, keyword, keyword, keyword);
    }

    public List<PatientDTO> searchPatient(String text) {
        text = text.trim();
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        String[] parts = text.split("\\s+", 2);
        if (parts.length == 1) {
            String s = "%" + text + "%";
            return searchPatientByKeyword(s);
        } else {
            String last = "%" + parts[0] + "%";
            String first = "%" + parts[1] + "%";
            return searchPatientByKeyword2(last, first);
        }
    }

    public List<PatientDTO> listRecentlyRegisteredPatient(int n) {
        String sql = "select * from Patient order by patientId desc limit ?";
        return getQuery().query(xlate(sql, ts.patientTable), ts.patientTable, n);
    }

    // Visit ////////////////////////////////////////////////////////////////////////

    private int enterVisit(VisitDTO visit) {
        ts.visitTable.insert(query, visit);
        practiceLogger.logVisitCreated(visit);
        return visit.visitId;
    }

    public VisitDTO getVisit(int visitId) {
        return ts.visitTable.getById(query, visitId);
    }

    private void updateVisit(VisitDTO visit) {
        VisitDTO prev = getVisit(visit.visitId);
        if (prev == null) {
            throw new RuntimeException("Cannot find previous visit to update: " + visit);
        }
        ts.visitTable.update(query, visit);
        practiceLogger.logVisitUpdated(prev, visit);
    }

    void deleteVisit(int visitId) {
        VisitDTO visit = getVisit(visitId);
        ts.visitTable.delete(query, visitId);
        practiceLogger.logVisitDeleted(visit);
    }

    public List<VisitPatientDTO> listRecentVisitWithPatient(int page, int itemsPerPage) {
        String sql = "select v.*, p.* from Visit v, Patient p where v.patientId = p.patientId " +
                " order by v.visitId desc limit ? offset ? ";
        sql = xlate(sql, ts.visitTable, "v", ts.patientTable, "p");
        return getQuery().query(sql,
                (rs, ctx) -> {
                    VisitPatientDTO vp = new VisitPatientDTO();
                    vp.visit = ts.visitTable.project(rs, ctx);
                    vp.patient = ts.patientTable.project(rs, ctx);
                    return vp;
                },
                itemsPerPage,
                page * itemsPerPage);
    }

    public List<VisitPatientDTO> listTodaysVisit() {
        String sql = xlate("select v.*, p.* from Visit v, Patient p where date(v.visitedAt) = ? " +
                        " and v.patientId = p.patientId order by v.visitId",
                ts.visitTable, "v", ts.patientTable, "p");
        return getQuery().query(sql,
                biProjector(ts.visitTable, ts.patientTable, VisitPatientDTO::create),
                LocalDate.now().toString());
    }

    private int countVisitByPatient(int patientId) {
        String sql = xlate("select count(*) from Visit where patientId = ?",
                ts.visitTable);
        return getQuery().get(sql, intProjector, patientId);
    }

    private int countVisitByShahokokuhoId(int shahokokuhoId) {
        String sql = "select count(*) from Visit where shahokokuhoId = ?";
        return getQuery().get(xlate(sql, ts.visitTable), intProjector, shahokokuhoId);
    }

    private int countVisitByKoukikoureiId(int koukikoureiId) {
        String sql = "select count(*) from Visit where koukikoureiId = ?";
        return getQuery().get(xlate(sql, ts.visitTable), intProjector, koukikoureiId);
    }

    private int countVisitByRoujinId(int roujinId) {
        String sql = "select count(*) from Visit where roujinId = ?";
        return getQuery().get(xlate(sql, ts.visitTable), intProjector, roujinId);
    }

    private int countVisitByKouhiId(int kouhiId) {
        String sql = "select count(*) from Visit where kouhi1Id = ? or kouhi2Id = ? or kouhi3Id = ?";
        return getQuery().get(xlate(sql, ts.visitTable), intProjector, kouhiId, kouhiId, kouhiId);
    }

    public VisitFull2PageDTO listVisitFull2(int patientId, int page) {
        int itemsPerPage = 10;
        int nVisit = countVisitByPatient(patientId);
        List<VisitDTO> visits = Collections.emptyList();
        if (nVisit > 0) {
            String sql = xlate("select * from Visit where patientId = ? " +
                            " order by visitId desc limit ? offset ?",
                    ts.visitTable);
            visits = getQuery().query(sql, ts.visitTable, patientId,
                    itemsPerPage, itemsPerPage * page);
        }
        VisitFull2PageDTO visitFull2PageDTO = new VisitFull2PageDTO();
        visitFull2PageDTO.totalPages = numberOfPages(nVisit, itemsPerPage);
        visitFull2PageDTO.page = page;
        visitFull2PageDTO.visits = visits.stream().map(this::getVisitFull2).collect(toList());
        return visitFull2PageDTO;
    }

    public VisitFullDTO getVisitFull(int visitId) {
        VisitDTO visit = getVisit(visitId);
        return getVisitFull(visit);
    }

    public VisitFull2DTO getVisitFull2(int visitId) {
        VisitDTO visit = getVisit(visitId);
        return getVisitFull2(visit);
    }

    private VisitFullDTO getVisitFull(VisitDTO visit) {
        int visitId = visit.visitId;
        VisitFullDTO visitFullDTO = new VisitFullDTO();
        visitFullDTO.visit = visit;
        visitFullDTO.texts = listText(visitId);
        visitFullDTO.shinryouList = listShinryouFull(visitId);
        visitFullDTO.drugs = listDrugFull(visitId);
        visitFullDTO.conducts = listConductFull(visitId);
        return visitFullDTO;
    }

    private VisitFull2DTO getVisitFull2(VisitDTO visit) {
        int visitId = visit.visitId;
        VisitFull2DTO visitFull2DTO = new VisitFull2DTO();
        visitFull2DTO.visit = visit;
        visitFull2DTO.texts = listText(visitId);
        visitFull2DTO.shinryouList = listShinryouFull(visitId);
        visitFull2DTO.drugs = listDrugFull(visitId);
        visitFull2DTO.conducts = listConductFull(visitId);
        visitFull2DTO.hoken = getHoken(visit);
        visitFull2DTO.charge = getCharge(visitId);
        return visitFull2DTO;
    }

    private List<VisitDTO> pageVisitByPatient(int patientId, int page, int itemsPerPage) {
        String sql = "select * from Visit where patientId = ? order by visitId desc " +
                " limit ? offset ?";
        return getQuery().query(xlate(sql, ts.visitTable), ts.visitTable,
                patientId, itemsPerPage, itemsPerPage * page);
    }

    public VisitTextDrugPageDTO listVisitTextDrugForPatient(int patientId, int page) {
        int nVisits = countVisitByPatient(patientId);
        int itemsPerPage = 10;
        VisitTextDrugPageDTO result = new VisitTextDrugPageDTO();
        result.totalPages = numberOfPages(nVisits, itemsPerPage);
        result.page = page;
        result.visitTextDrugs = pageVisitByPatient(patientId, page, itemsPerPage).stream()
                .map(visit -> {
                    int visitId = visit.visitId;
                    VisitTextDrugDTO dto = new VisitTextDrugDTO();
                    dto.visit = visit;
                    dto.texts = listText(visitId);
                    dto.drugs = listDrugFull(visitId);
                    return dto;
                })
                .collect(toList());
        return result;
    }

    private int countVisitByPatientAndIyakuhincode(int patientId, int iyakuhincode) {
        String sql = "select count(*) from Visit v, Drug d where v.patientId = ? " +
                " and v.visitId = d.visitId and d.iyakuhincode = ? ";
        return getQuery().get(xlate(sql, ts.visitTable, "v", ts.drugTable, "d"),
                intProjector, patientId, iyakuhincode);
    }

    private List<VisitDTO> pageVisitByPatientAndIyakuhincode(int patientId, int iyakuhincode, int page,
                                                             int itemsPerPage) {
        String sql = "select v.* from Visit v, Drug d where v.patientId = ? " +
                " and v.visitId = d.visitId and d.iyakuhincode = ? order by v.visitId desc " +
                " limit ? offset ? ";
        return getQuery().query(xlate(sql, ts.visitTable, "v", ts.drugTable, "d"),
                ts.visitTable, patientId, iyakuhincode, itemsPerPage, itemsPerPage * page);
    }

    public VisitTextDrugPageDTO listVisitTextDrugForPatientAndIyakuhincode(int patientId, int iyakuhincode, int page) {
        int nVisits = countVisitByPatientAndIyakuhincode(patientId, iyakuhincode);
        int itemsPerPage = 10;
        VisitTextDrugPageDTO result = new VisitTextDrugPageDTO();
        result.totalPages = numberOfPages(nVisits, itemsPerPage);
        result.page = page;
        result.visitTextDrugs = pageVisitByPatientAndIyakuhincode(patientId, iyakuhincode, page, itemsPerPage)
                .stream()
                .map(visit -> {
                    int visitId = visit.visitId;
                    VisitTextDrugDTO dto = new VisitTextDrugDTO();
                    dto.visit = visit;
                    dto.texts = listText(visitId);
                    dto.drugs = listDrugFull(visitId);
                    return dto;
                })
                .collect(toList());
        return result;
    }

    private int countVisitHavingDrug(int patientId) {
        String sql = "select count(*) from Visit visit, Drug drug where visit.visitId = drug.visitId " +
                " and visit.patientId = ? group by visit.visitId having count(drug.drugId) > 0 ";
        return getQuery().get(xlate(sql, ts.visitTable, "visit", ts.drugTable, "drug"),
                intProjector, patientId);
    }

    private List<VisitDTO> pageVisitHavingDrug(int patientId, int page, int itemsPerPage) {
        String sql = "select visit.* from Visit visit, Drug drug where visit.visitId = drug.visitId " +
                " and visit.patientId = ? group by visit.visitId having count(drug.drugId) > 0 " +
                " order by visit.visitId desc limit ? offset ?";
        return getQuery().query(xlate(sql, ts.visitTable, "visit", ts.drugTable, "drug"),
                ts.visitTable, patientId, itemsPerPage, itemsPerPage * page);
    }

    public VisitDrugPageDTO pageVisitHavingDrug(int patientId, int page) {
        VisitDrugPageDTO result = new VisitDrugPageDTO();
        result.page = page;
        result.totalPages = countVisitHavingDrug(patientId);
        result.visitDrugs = pageVisitHavingDrug(patientId, page, 10).stream()
                .map(visit -> {
                    VisitDrugDTO visitDrug = new VisitDrugDTO();
                    visitDrug.visit = visit;
                    visitDrug.drugs = listDrugFull(visit.visitId);
                    return visitDrug;
                })
                .collect(toList());
        return result;
    }

    public int startVisit(int patientId){
        return startVisit(patientId, LocalDateTime.now()).visitId;
    }

    public VisitDTO startVisit(int patientId, LocalDateTime at) {
        if (at == null) {
            at = LocalDateTime.now();
        }
        LocalDate atDate = at.toLocalDate();
        VisitDTO visitDTO = new VisitDTO();
        visitDTO.patientId = patientId;
        visitDTO.visitedAt = DateTimeUtil.toSqlDateTime(at);
        {
            List<ShahokokuhoDTO> list = findAvailableShahokokuho(patientId, atDate);
            if (list.size() == 0) {
                visitDTO.shahokokuhoId = 0;
            } else {
                visitDTO.shahokokuhoId = list.get(0).shahokokuhoId;
            }
        }
        {
            List<KoukikoureiDTO> list = findAvailableKoukikourei(patientId, atDate);
            if (list.size() == 0) {
                visitDTO.koukikoureiId = 0;
            } else {
                visitDTO.koukikoureiId = list.get(0).koukikoureiId;
            }
        }
        {
            List<RoujinDTO> list = findAvailableRoujin(patientId, atDate);
            if (list.size() == 0) {
                visitDTO.roujinId = 0;
            } else {
                visitDTO.roujinId = list.get(0).roujinId;
            }
        }
        {
            visitDTO.kouhi1Id = 0;
            visitDTO.kouhi2Id = 0;
            visitDTO.kouhi3Id = 0;
            List<KouhiDTO> list = findAvailableKouhi(patientId, atDate);
            int n = list.size();
            if (n > 0) {
                visitDTO.kouhi1Id = list.get(0).kouhiId;
                if (n > 1) {
                    visitDTO.kouhi2Id = list.get(1).kouhiId;
                    if (n > 2) {
                        visitDTO.kouhi3Id = list.get(2).kouhiId;
                    }
                }
            }
        }
        enterVisit(visitDTO);
        WqueueDTO wqueueDTO = new WqueueDTO();
        wqueueDTO.visitId = visitDTO.visitId;
        wqueueDTO.waitState = WqueueWaitState.WaitExam.getCode();
        enterWqueue(wqueueDTO);
        return visitDTO;
    }

    public void startExam(int visitId) {
        WqueueDTO wqueue = getWqueue(visitId);
        if (wqueue == null) {
            throw new RuntimeException("Cannot start exam because wqueue is null");
        }
        WqueueDTO updated = WqueueDTO.copy(wqueue);
        updated.waitState = WqueueWaitState.InExam.getCode();
        updateWqueue(updated);
    }

    public void suspendExam(int visitId) {
        setWqueueState(visitId, WqueueWaitState.WaitReExam);
    }

    public void endExam(int visitId, int charge) {
        VisitDTO visit = getVisit(visitId);
        if (visit == null) {
            throw new RuntimeException("No such visit: " + visitId);
        }
        ChargeDTO newCharge = new ChargeDTO();
        newCharge.visitId = visitId;
        newCharge.charge = charge;
        enterCharge(newCharge);
        WqueueDTO wqueue = getWqueue(visitId);
        if (wqueue == null) {
            throw new RuntimeException("Cannot find wqueue in endExam: " + visit);
        }
        WqueueDTO newWqueue = WqueueDTO.copy(wqueue);
        newWqueue.waitState = WqueueWaitState.WaitCashier.getCode();
        updateWqueue(newWqueue);
        int unprescribed = countUnprescribedDrug(visitId);
        if (unprescribed > 0) {
            PharmaQueueDTO newPharmaQueue = new PharmaQueueDTO();
            newPharmaQueue.visitId = visitId;
            newPharmaQueue.pharmaState = PharmaQueueState.WaitPack.getCode();
            enterPharmaQueue(newPharmaQueue);
        }
    }

    public void deleteVisitSafely(int visitId) {
        if (countTextForVisit(visitId) > 0) {
            throw new CannotDeleteVisitSafelyException("文章があるので、診察を削除できません。");
        }
        if (countDrugForVisit(visitId) > 0) {
            throw new CannotDeleteVisitSafelyException("投薬があるので、診察を削除できません。");
        }
        if (countShinryouForVisit(visitId) > 0) {
            throw new CannotDeleteVisitSafelyException("診療行為があるので、診察を削除できません。");
        }
        if (countConductForVisit(visitId) > 0) {
            throw new CannotDeleteVisitSafelyException("処置があるので、診察を削除できません。");
        }
        ChargeDTO charge = getCharge(visitId);
        if (charge != null) {
            throw new CannotDeleteVisitSafelyException("請求があるので、診察を削除できません。");
        }
        List<PaymentDTO> payments = listPayment(visitId);
        if (payments.size() > 0) {
            throw new CannotDeleteVisitSafelyException("支払い記録があるので、診察を削除できません。");
        }
        WqueueDTO wqueue = getWqueue(visitId);
        if (wqueue != null) {
            deleteWqueue(visitId);
        }
        PharmaQueueDTO pharmaQueue = getPharmaQueue(visitId);
        if (pharmaQueue != null) {
            deletePharmaQueue(visitId);
        }
        deleteVisit(visitId);
    }

    public void deleteVisitFromReception(int visitId) {
        WqueueDTO wq = getWqueue(visitId);
        if (wq != null) {
            if (wq.waitState != WqueueWaitState.WaitExam.getCode()) {
                throw new RuntimeException("診察の状態が診察待ちでないため、削除できません。");
            }
            deleteVisit(visitId);
        }
    }

    // Charge /////////////////////////////////////////////////////////////////////////////

    void enterCharge(ChargeDTO charge) {
        ts.chargeTable.insert(query, charge);
        practiceLogger.logChargeCreated(charge);
    }

    void updateCharge(ChargeDTO charge) {
        ChargeDTO prev = getCharge(charge.visitId);
        if (prev == null) {
            throw new RuntimeException("No previous charge to update: " + charge);
        }
        ts.chargeTable.update(query, charge);
        practiceLogger.logChargeUpdated(prev, charge);
    }

    public void modifyCharge(int visitId, int charge){
        ChargeDTO cur = getCharge(visitId);
        if( cur == null ){
            ChargeDTO dto = new ChargeDTO();
            dto.visitId = visitId;
            dto.charge = charge;
            enterCharge(dto);
        } else {
            cur.charge = charge;
            updateCharge(cur);
        }
    }

    public ChargeDTO getCharge(int visitId) {
        return ts.chargeTable.getById(query, visitId);
    }

    public void enterCharge(int visitId, int charge) {
        ChargeDTO newCharge = new ChargeDTO();
        newCharge.visitId = visitId;
        newCharge.charge = charge;
        enterCharge(newCharge);
        chargeModifiedHook(visitId);
    }

    public void updateCharge(int visitId, int charge) {
        ChargeDTO newCharge = new ChargeDTO();
        newCharge.visitId = visitId;
        newCharge.charge = charge;
        updateCharge(newCharge);
        chargeModifiedHook(visitId);
    }

    private void chargeModifiedHook(int visitId) {
        WqueueDTO wq = getWqueue(visitId);
        if (wq == null) {
            WqueueDTO newWqueue = new WqueueDTO();
            newWqueue.visitId = visitId;
            newWqueue.waitState = WqueueWaitState.WaitCashier.getCode();
            enterWqueue(newWqueue);
        } else {
            WqueueDTO newWqueue = WqueueDTO.copy(wq);
            newWqueue.waitState = WqueueWaitState.WaitCashier.getCode();
            updateWqueue(newWqueue);
        }
    }

    // Payment ////////////////////////////////////////////////////////////////////////////

    public List<PaymentDTO> listPayment(int visitId) {
        String sql = xlate("select * from Payment where visit_id = ? order by paytime",
                ts.paymentTable);
        return getQuery().query(sql, ts.paymentTable, visitId);
    }

    void enterPayment(PaymentDTO payment) {
        ts.paymentTable.insert(query, payment);
        practiceLogger.logPaymentCreated(payment);
    }

    public List<PaymentVisitPatientDTO> listPaymentByPatient(int patientId, int count) {
        String sql = "select payment.*, visit.*, patient.* from Payment payment, Visit visit, Patient patient " +
                " where payment.paytime = (select max(p2.paytime) from Payment p2 where payment.visitId = p2.visitId group by p2.visitId) " +
                " and visit.visitId = payment.visitId and patient.patientId = visit.patientId and visit.patientId = ?" +
                " order by payment.visitId desc limit ?";
        return getQuery().query(xlate(sql, ts.paymentTable, "payment",
                ts.visitTable, "visit", ts.patientTable, "patient",
                ts.paymentTable, "p2"),
                (rs, ctx) -> {
                    PaymentVisitPatientDTO dto = new PaymentVisitPatientDTO();
                    dto.payment = ts.paymentTable.project(rs, ctx);
                    dto.visit = ts.visitTable.project(rs, ctx);
                    dto.patient = ts.patientTable.project(rs, ctx);
                    return dto;
                },
                patientId, count);
    }

    private List<PaymentDTO> _listRecentPayment(int count) {
        String sql = "select payment.* from Payment payment where payment.paytime = " +
                " (select max(p2.paytime) from Payment p2 where p2.visitId = payment.visitId group by p2.visitId) " +
                " order by payment.visitId desc limit ?";
        return getQuery().query(xlate(sql, ts.paymentTable, "payment", ts.paymentTable, "p2"),
                ts.paymentTable, count);
    }

    public List<PaymentVisitPatientDTO> listRecentPayment(int count) {
        String sql = "select payment.*, visit.*, patient.* from Payment payment, Visit visit, Patient patient " +
                " where payment.paytime = (select max(p2.paytime) from Payment p2 where payment.visitId = p2.visitId group by p2.visitId) " +
                " and visit.visitId = payment.visitId and patient.patientId = visit.patientId " +
                " order by payment.visitId desc limit ?";
        return getQuery().query(xlate(sql, ts.paymentTable, "payment",
                ts.visitTable, "visit", ts.patientTable, "patient",
                ts.paymentTable, "p2"),
                (rs, ctx) -> {
                    PaymentVisitPatientDTO dto = new PaymentVisitPatientDTO();
                    dto.payment = ts.paymentTable.project(rs, ctx);
                    dto.visit = ts.visitTable.project(rs, ctx);
                    dto.patient = ts.patientTable.project(rs, ctx);
                    return dto;
                },
                count);
    }

    // Wqueue /////////////////////////////////////////////////////////////////////////////

    void enterWqueue(WqueueDTO wqueue) {
        ts.wqueueTable.insert(query, wqueue);
        practiceLogger.logWqueueCreated(wqueue);
    }

    void updateWqueue(WqueueDTO wqueue) {
        WqueueDTO prev = getWqueue(wqueue.visitId);
        if (prev == null) {
            throw new RuntimeException("cannot find previous wqueue: " + wqueue);
        }
        ts.wqueueTable.update(query, wqueue);
        practiceLogger.logWqueueUpdated(prev, wqueue);
    }

    public WqueueDTO getWqueue(int visitId) {
        return ts.wqueueTable.getById(query, visitId);
    }

    public void deleteWqueue(int visitId) {
        WqueueDTO wqueue = getWqueue(visitId);
        if (wqueue != null) {
            ts.wqueueTable.delete(query, wqueue.visitId);
            practiceLogger.logWqueueDeleted(wqueue);
        }
    }

    public List<WqueueDTO> listWqueue() {
        String sql = xlate("select * from Wqueue order by visitId", ts.wqueueTable);
        return getQuery().query(sql, ts.wqueueTable);
    }

    private WqueueFullDTO composeWqueueFullDTO(WqueueDTO wqueue) {
        WqueueFullDTO wqueueFullDTO = new WqueueFullDTO();
        wqueueFullDTO.wqueue = wqueue;
        wqueueFullDTO.visit = getVisit(wqueue.visitId);
        wqueueFullDTO.patient = getPatient(wqueueFullDTO.visit.patientId);
        return wqueueFullDTO;
    }

    public WqueueFullDTO getWqueueFull(int visitId) {
        WqueueDTO wqueueDTO = getWqueue(visitId);
        if (wqueueDTO == null) {
            return null;
        } else {
            return composeWqueueFullDTO(wqueueDTO);
        }
    }

    public List<WqueueFullDTO> listWqueueFull() {
        return listWqueue().stream().map(this::composeWqueueFullDTO).collect(toList());
    }

    public List<WqueueFullDTO> listWqueueFullForExam() {
        List<Integer> examStates = List.of(
                WqueueWaitState.WaitExam.getCode(),
                WqueueWaitState.InExam.getCode(),
                WqueueWaitState.WaitReExam.getCode()
        );
        return listWqueue().stream().filter(wq -> examStates.contains(wq.waitState))
                .map(this::composeWqueueFullDTO).collect(toList());
    }

    private void setWqueueState(int visitId, WqueueWaitState state) {
        WqueueDTO wqueue = getWqueue(visitId);
        if (wqueue == null) {
            WqueueDTO newWqueue = new WqueueDTO();
            newWqueue.visitId = visitId;
            newWqueue.waitState = state.getCode();
            enterWqueue(newWqueue);
        } else {
            WqueueDTO updatedWqueue = WqueueDTO.copy(wqueue);
            updatedWqueue.waitState = state.getCode();
            updateWqueue(updatedWqueue);
        }
    }

    // Cashier //////////////////////////////////////////////////////////////////////////////////

    public void finishCashier(PaymentDTO payment) {
        int visitId = payment.visitId;
        enterPayment(payment);
        PharmaQueueDTO pharmaQueue = getPharmaQueue(visitId);
        WqueueDTO wqueue = getWqueue(visitId);
        if (pharmaQueue != null) {
            if (wqueue != null) {
                WqueueDTO modified = WqueueDTO.copy(wqueue);
                modified.waitState = WqueueWaitState.WaitDrug.getCode();
                updateWqueue(modified);
            }
        } else {
            if (wqueue != null) {
                deleteWqueue(visitId);
            }
        }
    }

    // Hoken //////////////////////////////////////////////////////////////////////////////

    public HokenDTO getHoken(int visitId) {
        VisitDTO visit = getVisit(visitId);
        return getHoken(visit);
    }

    private HokenDTO getHoken(VisitDTO visit) {
        HokenDTO hokenDTO = new HokenDTO();
        if (visit.shahokokuhoId > 0) {
            hokenDTO.shahokokuho = getShahokokuho(visit.shahokokuhoId);
        }
        if (visit.koukikoureiId > 0) {
            hokenDTO.koukikourei = getKoukikourei(visit.koukikoureiId);
        }
        if (visit.roujinId > 0) {
            hokenDTO.roujin = getRoujin(visit.roujinId);
        }
        if (visit.kouhi1Id > 0) {
            hokenDTO.kouhi1 = getKouhi(visit.kouhi1Id);
        }
        if (visit.kouhi2Id > 0) {
            hokenDTO.kouhi2 = getKouhi(visit.kouhi2Id);
        }
        if (visit.kouhi3Id > 0) {
            hokenDTO.kouhi3 = getKouhi(visit.kouhi3Id);
        }
        return hokenDTO;
    }

    public HokenDTO listAvailableHoken(int patientId, LocalDate visitedAt) {
        HokenDTO hokenDTO = new HokenDTO();
        hokenDTO.shahokokuho = findAvailableShahokokuho(patientId, visitedAt).stream().findFirst().orElse(null);
        hokenDTO.koukikourei = findAvailableKoukikourei(patientId, visitedAt).stream().findFirst().orElse(null);
        hokenDTO.roujin = findAvailableRoujin(patientId, visitedAt).stream().findFirst().orElse(null);
        List<KouhiDTO> kouhiList = findAvailableKouhi(patientId, visitedAt);
        if (kouhiList.size() > 0) {
            hokenDTO.kouhi1 = kouhiList.get(0);
            if (kouhiList.size() > 1) {
                hokenDTO.kouhi2 = kouhiList.get(1);
                if (kouhiList.size() > 2) {
                    hokenDTO.kouhi3 = kouhiList.get(2);
                }
            }
        }
        return hokenDTO;
    }

    public HokenListDTO listHoken(int patientId) {
        HokenListDTO result = new HokenListDTO();
        result.shahokokuhoListDTO = listShahokokuho(patientId);
        result.koukikoureiListDTO = listKoukikourei(patientId);
        result.roujinListDTO = listRoujin(patientId);
        result.kouhiListDTO = listKouhi(patientId);
        return result;
    }

    public HokenListDTO listAvailableAllHoken(int patientId, LocalDate at) {
        HokenListDTO hokenListDTO = new HokenListDTO();
        hokenListDTO.shahokokuhoListDTO = findAvailableShahokokuho(patientId, at);
        hokenListDTO.koukikoureiListDTO = findAvailableKoukikourei(patientId, at);
        hokenListDTO.roujinListDTO = findAvailableRoujin(patientId, at);
        hokenListDTO.kouhiListDTO = findAvailableKouhi(patientId, at);
        return hokenListDTO;
    }

    public void updateHoken(VisitDTO visit) {
        VisitDTO origVisit = getVisit(visit.visitId);
        VisitDTO updated = VisitDTO.copy(origVisit);
        updated.shahokokuhoId = visit.shahokokuhoId;
        updated.koukikoureiId = visit.koukikoureiId;
        updated.roujinId = visit.roujinId;
        updated.kouhi1Id = visit.kouhi1Id;
        updated.kouhi2Id = visit.kouhi2Id;
        updated.kouhi3Id = visit.kouhi3Id;
        updateVisit(updated);
    }

    // Drug ///////////////////////////////////////////////////////////////////////////

    public DrugDTO getDrug(int drugId) {
        return ts.drugTable.getById(query, drugId);
    }

    public DrugWithAttrDTO getDrugWithAttr(int drugId) {
        DrugWithAttrDTO result = new DrugWithAttrDTO();
        result.drug = getDrug(drugId);
        result.attr = findDrugAttr(drugId);
        return result;
    }

    int countDrugForVisit(int visitId) {
        String sql = "select count(*) from Drug where visitId = ?";
        return getQuery().get(xlate(sql, ts.drugTable), intProjector, visitId);
    }

    public int enterDrug(DrugDTO drug) {
        ts.drugTable.insert(query, drug);
        practiceLogger.logDrugCreated(drug);
        return drug.drugId;
    }

    public void updateDrug(DrugDTO drug) {
        DrugDTO prev = getDrug(drug.drugId);
        if (prev == null) {
            throw new RuntimeException("Cannot find prev drug while updating: " + drug);
        }
        ts.drugTable.update(query, drug);
        practiceLogger.logDrugUpdated(prev, drug);
    }

    public int batchUpdateDrugDays(List<Integer> drugIds, int days) {
        int count = 0;
        for (Integer drugId : drugIds) {
            DrugDTO prev = getDrug(drugId);
            if (prev.category == DrugCategory.Naifuku.getCode() && prev.days != days) {
                DrugDTO drug = DrugDTO.copy(prev);
                drug.days = days;
                updateDrug(drug);
                count += 1;
            }
        }
        return count;
    }

    public int markDrugsAsPrescribed(int visitId) {
        String sql = "select * from Drug where visitId = ? ";
        List<DrugDTO> drugs = getQuery().query(xlate(sql, ts.drugTable), ts.drugTable, visitId);
        int count = 0;
        for (DrugDTO drug : drugs) {
            if (drug.prescribed == 0) {
                DrugDTO updated = DrugDTO.copy(drug);
                updated.prescribed = 1;
                updateDrug(updated);
                count += 1;
            }
        }
        return count;
    }

    void deleteDrug(int drugId) {
        DrugDTO drug = getDrug(drugId);
        if (drug != null) {
            ts.drugTable.delete(query, drug.drugId);
            practiceLogger.logDrugDeleted(drug);
        }
    }

    public DrugFullDTO getDrugFull(int drugId) {
        String sql = xlate("select d.*, m.* from Drug d, IyakuhinMaster m, Visit v " +
                        " where d.drugId = ? and d.visitId = v.visitId and d.iyakuhincode = m.iyakuhincode " +
                        " and " + ts.dialect.isValidAt("m.validFrom", "m.validUpto", "v.visitedAt"),
                ts.drugTable, "d", ts.iyakuhinMasterTable, "m", ts.visitTable, "v");
        return getQuery().get(sql,
                biProjector(ts.drugTable, ts.iyakuhinMasterTable, DrugFullDTO::create),
                drugId);
    }

    public DrugFullWithAttrDTO getDrugFullWithAttr(int drugId) {
        DrugFullWithAttrDTO result = new DrugFullWithAttrDTO();
        result.drug = getDrugFull(drugId);
        result.attr = findDrugAttr(drugId);
        return result;
    }

    private Projector<DrugAttrDTO> createNullableDrugAttrProjector() {
        return new NullableProjector<>(
                ts.drugAttrTable,
                attr -> attr.drugId == 0
        );
    }

    public List<DrugWithAttrDTO> listDrugWithAttr(int visitId) {
        String sql = xlate("select d.*, a.* from Drug d left join DrugAttr a " +
                        " on d.drugId = a.drugId where d.visitId = ? order by d.drugId",
                ts.drugTable, "d", ts.drugAttrTable, "a");
        Projector<DrugAttrDTO> nullableDrugAttrProjector = createNullableDrugAttrProjector();
        return getQuery().query(sql,
                biProjector(ts.drugTable, nullableDrugAttrProjector, DrugWithAttrDTO::create),
                visitId);
    }

    public List<DrugDTO> listDrug(int visitId) {
        String sql = "select * from Drug where visitId = ? order by drugId";
        return getQuery().query(xlate(sql, ts.drugTable), ts.drugTable, visitId);
    }

    public List<DrugFullDTO> listDrugFull(int visitId) {
        String sql = xlate(
                "select d.*, m.* from Drug d, IyakuhinMaster m, Visit v " +
                        " where d.visitId = ? and d.visitId = v.visitId and d.iyakuhincode = m.iyakuhincode " +
                        " and " + ts.dialect.isValidAt("m.validFrom", "m.validUpto", "v.visitedAt") +
                        " order by d.drugId",
                ts.drugTable, "d", ts.iyakuhinMasterTable, "m", ts.visitTable, "v");
        return getQuery().query(sql, biProjector(ts.drugTable, ts.iyakuhinMasterTable, DrugFullDTO::create),
                visitId);
    }

    private List<Integer> listRepresentativeNaifukuTonpukuDrugId(int patientId) {
        String sql = xlate("select MAX(d.drugId) from Drug d, Visit v where d.visitId = v.visitId " +
                        " and v.patientId = ? " +
                        " and d.category in (0, 1) " +
                        " group by d.iyakuhincode, d.amount, d.usage, d.days ",
                ts.drugTable, "d", ts.visitTable, "v");
        return getQuery().query(sql,
                (rs, ctx) -> rs.getInt(ctx.nextIndex()),
                patientId);
    }

    private List<Integer> listRepresentativeNaifukuTonpukuDrugId(String text, int patientId) {
        String searchText = "%" + text + "%";
        String sql = xlate("select MAX(d.drugId) from Drug d, Visit v, IyakuhinMaster m " +
                        " where d.visitId = v.visitId " +
                        " and v.patientId = ? " +
                        " and d.category in (0, 1) " +
                        " and d.iyakuhincode = m.iyakuhincode " +
                        " and " + ts.dialect.isValidAt("m.validFrom", "m.validUpto", "v.visitedAt") +
                        " and m.name like ? " +
                        " group by d.iyakuhincode, d.amount, d.usage, d.days ",
                ts.drugTable, "d", ts.visitTable, "v", ts.iyakuhinMasterTable, "m");
        return getQuery().query(sql,
                (rs, ctx) -> rs.getInt(ctx.nextIndex()),
                patientId, searchText);
    }

    private List<Integer> listRepresentativeGaiyouDrugId(int patientId) {
        String sql = xlate("select MAX(d.drugId) from Drug d, Visit v where d.visitId = v.visitId " +
                        " and v.patientId = ? " +
                        " and d.category = 2 " +
                        " group by d.iyakuhincode, d.amount, d.usage ",
                ts.drugTable, "d", ts.visitTable, "v");
        return getQuery().query(sql,
                (rs, ctx) -> rs.getInt(ctx.nextIndex()),
                patientId);
    }

    public List<Integer> listRepresentativeGaiyouDrugId(String text, int patientId) {
        String searchText = "%" + text + "%";
        String sql = xlate("select MAX(d.drugId) from Drug d, Visit v, IyakuhinMaster m " +
                        " where d.visitId = v.visitId " +
                        " and v.patientId = ? " +
                        " and d.category = 2 " +
                        " and d.iyakuhincode = m.iyakuhincode " +
                        " and " + ts.dialect.isValidAt("m.validFrom", "m.validUpto", "v.visitedAt") +
                        " and m.name like ? " +
                        " group by d.iyakuhincode, d.amount, d.usage ",
                ts.drugTable, "d", ts.visitTable, "v", ts.iyakuhinMasterTable, "m");
        return getQuery().query(sql,
                (rs, ctx) -> rs.getInt(ctx.nextIndex()),
                patientId, searchText);
    }

    public List<DrugFullDTO> listPrevDrugByPatient(int patientId) {
        return Stream.concat(
                listRepresentativeNaifukuTonpukuDrugId(patientId).stream(),
                listRepresentativeGaiyouDrugId(patientId).stream()
        ).sorted(Comparator.<Integer>naturalOrder().reversed())
                .map(this::getDrugFull)
                .collect(toList());
    }

    public List<DrugFullDTO> searchPrevDrug(String text, int patientId) {
        return Stream.concat(
                listRepresentativeNaifukuTonpukuDrugId(text, patientId).stream(),
                listRepresentativeGaiyouDrugId(text, patientId).stream()
        ).sorted(Comparator.<Integer>naturalOrder().reversed())
                .map(this::getDrugFull)
                .collect(toList());
    }

    public int countUnprescribedDrug(int visitId) {
        String sql = xlate("select count(*) from Drug where visitId = ? and prescribed = 0",
                ts.drugTable);
        return getQuery().get(sql, (rs, ctx) -> rs.getInt(ctx.nextIndex()), visitId);
    }

    private List<Integer> listIyakuhincodeByPatient(int patientId) {
        String sql = "select distinct d.iyakuhincode from Drug d, Visit v where d.visitId = v.visitId " +
                " and v.patientId = ?";
        return getQuery().query(xlate(sql, ts.drugTable, "d", ts.visitTable, "v"),
                intProjector, patientId);
    }

    private String getNameForIyakuhincode(int iyakuhincode) {
        String sql = "select name from IyakuhinMaster where iyakuhincode = ? order by validFrom desc limit 1";
        return getQuery().get(xlate(sql, ts.iyakuhinMasterTable), stringProjector, iyakuhincode);
    }

    public List<IyakuhincodeNameDTO> listIyakuhinForPatient(int patientId) {
        return listIyakuhincodeByPatient(patientId).stream()
                .map(iyakuhincode -> {
                    IyakuhincodeNameDTO iyakuhincodeNameDTO = new IyakuhincodeNameDTO();
                    iyakuhincodeNameDTO.iyakuhincode = iyakuhincode;
                    iyakuhincodeNameDTO.name = getNameForIyakuhincode(iyakuhincode);
                    return iyakuhincodeNameDTO;
                })
                .sorted(Comparator.comparing(dto -> dto.name))
                .collect(toList());
    }

    public int enterDrugWithAttr(DrugWithAttrDTO drugWithAttr) {
        DrugDTO drug = drugWithAttr.drug;
        enterDrug(drug);
        if (drugWithAttr.attr != null) {
            drugWithAttr.attr.drugId = drug.drugId;
            enterDrugAttr(drugWithAttr.attr);
        }
        if (drug.prescribed == 0) {
            WqueueDTO wq = getWqueue(drug.visitId);
            if (wq != null) {
                if (wq.waitState == WqueueWaitState.WaitCashier.getCode() ||
                        wq.waitState == WqueueWaitState.WaitDrug.getCode()) {
                    PharmaQueueDTO pharmaQueue = getPharmaQueue(drug.visitId);
                    if (pharmaQueue == null) {
                        PharmaQueueDTO newPharmaQueue = new PharmaQueueDTO();
                        newPharmaQueue.visitId = drug.visitId;
                        newPharmaQueue.pharmaState = PharmaQueueState.WaitPack.getCode();
                        enterPharmaQueue(newPharmaQueue);
                    }
                }
            }
        }
        return drugWithAttr.drug.drugId;
    }

    public void updateDrugWithAttr(DrugWithAttrDTO drugWithAttr) {
        DrugDTO drug = drugWithAttr.drug;
        DrugAttrDTO attr = drugWithAttr.attr;
        DrugAttrDTO prevAttr = findDrugAttr(drug.drugId);
        updateDrug(drug);
        if (attr == null || DrugAttrDTO.isEmpty(attr)) {
            if (prevAttr != null) {
                deleteDrugAttr(drug.drugId);
            }
        } else {
            if (prevAttr == null) {
                enterDrugAttr(attr);
            } else {
                updateDrugAttr(attr);
            }
        }
        WqueueDTO wq = getWqueue(drug.visitId);
        if (wq != null) {
            if (wq.waitState == WqueueWaitState.WaitCashier.getCode() ||
                    wq.waitState == WqueueWaitState.WaitDrug.getCode()) {
                int unprescribed = countUnprescribedDrug(drug.visitId);
                if (unprescribed > 0) {
                    PharmaQueueDTO pharmaQueue = getPharmaQueue(drug.visitId);
                    if (pharmaQueue == null) {
                        PharmaQueueDTO newPharmaQueue = new PharmaQueueDTO();
                        newPharmaQueue.visitId = drug.visitId;
                        newPharmaQueue.pharmaState = PharmaQueueState.WaitPack.getCode();
                        enterPharmaQueue(newPharmaQueue);
                    } else {
                        if (pharmaQueue.pharmaState == PharmaQueueState.PackDone.getCode()) {
                            PharmaQueueDTO updated = PharmaQueueDTO.copy(pharmaQueue);
                            updated.pharmaState = PharmaQueueState.WaitPack.getCode();
                            updatePharmaQueue(updated);
                        }
                    }
                } else {
                    // Do nothing!
                    // PharmaQueue is deleted explicitly by calling deletePharmaQueue (from pharma program)
                }
            }
        }
    }

    public void deleteDrugWithAttr(int drugId) {
        DrugAttrDTO attr = findDrugAttr(drugId);
        deleteDrug(drugId);
        if (attr != null) {
            deleteDrugAttr(drugId);
        }
    }

    public void batchDeleteDrugs(List<Integer> drugIds) {
        for (Integer drugId : drugIds) {
            deleteDrugWithAttr(drugId);
        }
    }

    public DrugAttrDTO setDrugTekiyou(int drugId, String tekiyou) {
        DrugAttrDTO curr = findDrugAttr(drugId);
        if (curr != null) {
            DrugAttrDTO attr = DrugAttrDTO.copy(curr);
            attr.tekiyou = tekiyou;
            if (DrugAttrDTO.isEmpty(attr)) {
                deleteDrugAttr(drugId);
                return null;
            } else {
                updateDrugAttr(attr);
                return attr;
            }
        } else {
            if (tekiyou != null && !tekiyou.isEmpty()) {
                DrugAttrDTO attr = new DrugAttrDTO();
                attr.drugId = drugId;
                attr.tekiyou = tekiyou;
                enterDrugAttr(attr);
                return attr;
            } else {
                return null;
            }
        }
    }

    public DrugAttrDTO deleteDrugTekiyou(int drugId) {
        DrugAttrDTO attr = findDrugAttr(drugId);
        if (attr != null) {
            DrugAttrDTO updated = DrugAttrDTO.copy(attr);
            updated.tekiyou = null;
            if (DrugAttrDTO.isEmpty(updated)) {
                deleteDrugAttr(drugId);
                return null;
            } else {
                updateDrugAttr(updated);
                return updated;
            }
        } else {
            return null;
        }
    }

    // DrugAttr /////////////////////////////////////////////////////////////////////////

    public DrugAttrDTO findDrugAttr(int drugId) {
        return ts.drugAttrTable.getById(query, drugId);
    }

    public void enterDrugAttr(DrugAttrDTO drugAttr) {
        ts.drugAttrTable.insert(query, drugAttr);
        practiceLogger.logDrugAttrCreated(drugAttr);
    }

    public void updateDrugAttr(DrugAttrDTO drugAttr) {
        DrugAttrDTO prev = findDrugAttr(drugAttr.drugId);
        if (prev == null) {
            throw new RuntimeException("Previous drug attr does not exist: " + drugAttr.drugId);
        }
        ts.drugAttrTable.update(query, drugAttr);
        practiceLogger.logDrugAttrUpdated(prev, drugAttr);
    }

    public void deleteDrugAttr(int drugId) {
        DrugAttrDTO deleted = findDrugAttr(drugId);
        if (deleted == null) {
            throw new RuntimeException("No such drug attr to delete: " + drugId);
        }
        ts.drugAttrTable.delete(query, drugId);
        practiceLogger.logDrugAttrDeleted(deleted);
    }

    public List<DrugAttrDTO> batchGetDrugAttr(List<Integer> drugIds) {
        if (drugIds.size() == 0) {
            return Collections.emptyList();
        } else {
            String sql = "select * from DrugAttr where drugId in (" +
                    drugIds.stream().map(Object::toString).collect(joining(",")) + ")";
            return getQuery().query(xlate(sql, ts.drugAttrTable), ts.drugAttrTable);
        }
    }

    // Shouki //////////////////////////////////////////////////////////////////////////

    public List<ShoukiDTO> batchGetShouki(List<Integer> visitIds) {
        return visitIds.stream()
                .map(id -> ts.shoukiTable.getById(query, id))
                .filter(Objects::nonNull).collect(toList());
    }

    public ShoukiDTO findShouki(int visitId) {
        return ts.shoukiTable.getById(query, visitId);
    }

    public void enterShouki(ShoukiDTO shouki) {
        ts.shoukiTable.insert(query, shouki);
        practiceLogger.logShoukiCreated(shouki);
    }

    public void updateShouki(ShoukiDTO shouki) {
        ShoukiDTO prev = findShouki(shouki.visitId);
        if (prev == null) {
            throw new RuntimeException("Cannot find shouki to update: " + shouki.visitId);
        }
        ts.shoukiTable.update(query, shouki);
        practiceLogger.logShoukiUpdated(prev, shouki);
    }

    public void deleteShouki(int visitId) {
        ShoukiDTO deleted = findShouki(visitId);
        if (deleted == null) {
            throw new RuntimeException("Cannot find shouki to delete: " + visitId);
        }
        ts.shoukiTable.delete(query, visitId);
        practiceLogger.logShoukiDeleted(deleted);
    }

    // Text ////////////////////////////////////////////////////////////////////////////

    public int enterText(TextDTO text) {
        ts.textTable.insert(query, text);
        practiceLogger.logTextCreated(text);
        return text.textId;
    }

    public TextDTO getText(int textId) {
        return ts.textTable.getById(query, textId);
    }

    public void updateText(TextDTO text) {
        TextDTO prev = getText(text.textId);
        if (prev == null) {
            throw new RuntimeException("Cannot find previous text to update: " + text);
        }
        ts.textTable.update(query, text);
        practiceLogger.logTextUpdated(prev, text);
    }

    public void deleteText(int textId) {
        TextDTO text = getText(textId);
        if (text != null) {
            ts.textTable.delete(query, textId);
            practiceLogger.logTextDeleted(text);
        }
    }

    int countTextForVisit(int visitId) {
        String sql = "select count(*) from Text where visitId = ?";
        return getQuery().get(xlate(sql, ts.textTable), intProjector, visitId);
    }

    public List<TextDTO> listText(int visitId) {
        String sql = xlate("select * from Text where visitId = ? order by textId",
                ts.textTable);
        return getQuery().query(sql, ts.textTable, visitId);
    }

    public TextVisitPageDTO searchText(int patientId, String text, int page) {
        int itemsPerPage = 20;
        String searchText = "%" + text + "%";
        String countSql = xlate("select count(*) from Text t, Visit v " +
                        " where t.visitId = v.visitId and v.patientId = ? " +
                        " and t.content like ? ",
                ts.textTable, "t", ts.visitTable, "v");
        int totalItems = getQuery().getInt(countSql, patientId, searchText);
        String sql = xlate("select t.*, v.* from Text t, Visit v " +
                        " where t.visitId = v.visitId and v.patientId = ? " +
                        " and t.content like ? order by t.textId limit ? offset ?",
                ts.textTable, "t", ts.visitTable, "v");
        List<TextVisitDTO> textVisits = getQuery().query(sql,
                biProjector(ts.textTable, ts.visitTable, TextVisitDTO::create),
                patientId, searchText, itemsPerPage, itemsPerPage * page);
        TextVisitPageDTO result = new TextVisitPageDTO();
        result.page = page;
        result.totalPages = numberOfPages(totalItems, itemsPerPage);
        result.textVisits = textVisits;
        return result;
    }

    public TextVisitPatientPageDTO searchTextGlobally(String text, int page) {
        int itemsPerPage = 20;
        String searchText = "%" + text + "%";
        String countSql = xlate("select count(*) from Text where content like ?", ts.textTable);
        int totalItems = getQuery().getInt(countSql, searchText);
        String sql = xlate("select t.*, v.*, p.* from Text t, Visit v, Patient p " +
                        " where t.visitId = v.visitId and v.patientId = p.patientId " +
                        " and t.content like ? order by t.textId limit ? offset ?",
                ts.textTable, "t", ts.visitTable, "v", ts.patientTable, "p");
        List<TextVisitPatientDTO> textVisits = getQuery().query(sql,
                (rs, ctx) -> {
                    TextVisitPatientDTO row = new TextVisitPatientDTO();
                    row.text = ts.textTable.project(rs, ctx);
                    row.visit = ts.visitTable.project(rs, ctx);
                    row.patient = ts.patientTable.project(rs, ctx);
                    return row;
                },
                searchText, itemsPerPage, itemsPerPage * page);
        TextVisitPatientPageDTO result = new TextVisitPatientPageDTO();
        result.page = page;
        result.totalPages = numberOfPages(totalItems, itemsPerPage);
        result.textVisitPatients = textVisits;
        return result;
    }

    // Shinryou ////////////////////////////////////////////////////////////////////////////

    public ShinryouDTO getShinryou(int shinryouId) {
        return ts.shinryouTable.getById(query, shinryouId);
    }

    public ShinryouWithAttrDTO getShinryouWithAttr(int shinryouId) {
        ShinryouWithAttrDTO result = new ShinryouWithAttrDTO();
        result.shinryou = getShinryou(shinryouId);
        result.attr = findShinryouAttr(shinryouId);
        return result;
    }

    int countShinryouForVisit(int visitId) {
        String sql = "select count(*) from Shinryou where visitId = ?";
        return getQuery().get(xlate(sql, ts.shinryouTable), intProjector, visitId);
    }

    public int enterShinryou(ShinryouDTO shinryou) {
        ts.shinryouTable.insert(query, shinryou);
        practiceLogger.logShinryouCreated(shinryou);
        return shinryou.shinryouId;
    }

    public void updateShinryou(ShinryouDTO shinryou){
        ShinryouDTO prev = ts.shinryouTable.getById(query, shinryou.shinryouId);
        ts.shinryouTable.update(query, shinryou);
        practiceLogger.logShinryouUpdated(prev, shinryou);
    }

    void deleteShinryou(int shinryouId) {
        ShinryouDTO shinryou = getShinryou(shinryouId);
        if (shinryou != null) {
            ts.shinryouTable.delete(query, shinryouId);
            practiceLogger.logShinryouDeleted(shinryou);
        }
    }

    public ShinryouFullDTO getShinryouFull(int shinryouId) {
        String sql = xlate("select s.*, m.* from Shinryou s, ShinryouMaster m, Visit v " +
                        " where s.shinryouId = ? and s.visitId = v.visitId and s.shinryoucode = m.shinryoucode " +
                        " and " + ts.dialect.isValidAt("m.validFrom", "m.validUpto", "v.visitedAt"),
                ts.shinryouTable, "s", ts.shinryouMasterTable, "m", ts.visitTable, "v");
        return getQuery().get(sql,
                biProjector(ts.shinryouTable, ts.shinryouMasterTable, ShinryouFullDTO::create),
                shinryouId);
    }

    public ShinryouFullWithAttrDTO getShinryouFullWithAttr(int shinryouId) {
        ShinryouFullWithAttrDTO result = new ShinryouFullWithAttrDTO();
        result.shinryou = getShinryouFull(shinryouId);
        result.attr = findShinryouAttr(shinryouId);
        return result;
    }

    public List<ShinryouFullDTO> listShinryouFullByIds(List<Integer> shinryouIds) {
        return shinryouIds.stream().map(this::getShinryouFull).collect(toList());
    }

    public List<ShinryouFullWithAttrDTO> listShinryouFullWithAttrByIds(List<Integer> shinryouIds) {
        return shinryouIds.stream().map(this::getShinryouFullWithAttr).collect(toList());
    }

    public List<ShinryouDTO> listShinryou(int visitId) {
        String sql = xlate("select * from Shinryou where visitId = ? order by shinryouId",
                ts.shinryouTable);
        return getQuery().query(sql, ts.shinryouTable, visitId);
    }

    private Projector<ShinryouAttrDTO> createNullableShinryouAttrProjector() {
        return new NullableProjector<>(
                ts.shinryouAttrTable,
                attr -> attr.shinryouId == 0
        );
    }

    public List<ShinryouWithAttrDTO> listShinryouWithAttr(int visitId) {
        String sql = xlate("select s.*, a.* from Shinryou s left join ShinryouAttr a " +
                        " on s.shinryouId = a.shinryouId" +
                        " where s.visitId = ? order by s.shinryoucode",
                ts.shinryouTable, "s", ts.shinryouAttrTable, "a");
        Projector<ShinryouAttrDTO> nullableShinryouAttrProjector = createNullableShinryouAttrProjector();
        return getQuery().query(sql,
                biProjector(ts.shinryouTable, nullableShinryouAttrProjector, ShinryouWithAttrDTO::create),
                visitId);
    }

    public List<ShinryouFullDTO> listShinryouFull(int visitId) {
        String sql = xlate("select s.*, m.* from Shinryou s, ShinryouMaster m, Visit v " +
                        " where s.visitId = ? and s.visitId = v.visitId and s.shinryoucode = m.shinryoucode " +
                        " and " + ts.dialect.isValidAt("m.validFrom", "m.validUpto", "v.visitedAt") +
                        " order by s.shinryoucode",
                ts.shinryouTable, "s", ts.shinryouMasterTable, "m", ts.visitTable, "v");
        return getQuery().query(sql,
                biProjector(ts.shinryouTable, ts.shinryouMasterTable, ShinryouFullDTO::create),
                visitId);
    }

    public List<ShinryouFullWithAttrDTO> listShinryouFullWithAttr(int visitId) {
        String sql = xlate("select s.*, m.*, a.* from Shinryou s, ShinryouMaster m, Visit v" +
                        " left join ShinryouAttr a on s.shinryouId = a.shinryouId " +
                        " where s.visitId = ? and s.visitId = v.visitId and s.shinryoucode = m.shinryoucode " +
                        " and " + ts.dialect.isValidAt("m.validFrom", "m.validUpto", "v.visitedAt") +
                        " order by s.shinryoucode",
                ts.shinryouTable, "s", ts.shinryouMasterTable, "m", ts.visitTable, "v",
                ts.shinryouAttrTable, "a");
        Projector<ShinryouAttrDTO> nullableShinryouAttrProjector = createNullableShinryouAttrProjector();
        return getQuery().query(sql,
                biProjector(biProjector(ts.shinryouTable, ts.shinryouMasterTable, ShinryouFullDTO::create),
                        nullableShinryouAttrProjector,
                        ShinryouFullWithAttrDTO::create),
                visitId);
    }

    public int enterShinryouWithAttr(ShinryouWithAttrDTO shinryouWithAttr) {
        ShinryouDTO shinryou = shinryouWithAttr.shinryou;
        ShinryouAttrDTO attr = shinryouWithAttr.attr == null || ShinryouAttrDTO.isEmpty(shinryouWithAttr.attr) ?
                null : shinryouWithAttr.attr;
        enterShinryou(shinryou);
        if (attr != null) {
            attr.shinryouId = shinryou.shinryouId;
            enterShinryouAttr(attr);
        }
        return shinryou.shinryouId;
    }

    public List<Integer> batchEnterShinryou(List<ShinryouDTO> shinryouList) {
        List<Integer> shinryouIds = new ArrayList<>();
        for (ShinryouDTO shinryou : shinryouList) {
            enterShinryou(shinryou);
            shinryouIds.add(shinryou.shinryouId);
        }
        return shinryouIds;
    }

    public void deleteShinryouWithAttr(int shinryouId) {
        ShinryouAttrDTO attr = findShinryouAttr(shinryouId);
        deleteShinryou(shinryouId);
        if (attr != null) {
            deleteShinryouAttr(shinryouId);
        }
    }

    public void batchDeleteShinryouWithAttr(List<Integer> shinryouIds) {
        for (Integer shinryouId : shinryouIds) {
            deleteShinryouWithAttr(shinryouId);
        }
    }

    public List<Integer> deleteDuplicateShinryou(int visitId) {
        return listShinryou(visitId).stream()
                .collect(groupingBy(s -> s.shinryoucode))
                .values().stream()
                .flatMap(g -> g.subList(1, g.size()).stream())
                .map(s -> {
                    deleteShinryou(s.shinryouId);
                    return s.shinryouId;
                })
                .collect(toList());
    }

    // ShinryouAttr /////////////////////////////////////////////////////////////////////////////

    public List<ShinryouAttrDTO> batchGetShinryouAttr(List<Integer> shinryouIds) {
        return shinryouIds.stream().map(id -> ts.shinryouAttrTable.getById(query, id))
                .filter(Objects::nonNull).collect(toList());
    }

    public ShinryouAttrDTO findShinryouAttr(int shinryouId) {
        return ts.shinryouAttrTable.getById(query, shinryouId);
    }

    public void enterShinryouAttr(ShinryouAttrDTO shinryouAttr) {
        ts.shinryouAttrTable.insert(query, shinryouAttr);
        practiceLogger.logShinryouAttrCreated(shinryouAttr);
    }

    public void updateShinryouAttr(ShinryouAttrDTO shinryouAttr) {
        ShinryouAttrDTO prev = findShinryouAttr(shinryouAttr.shinryouId);
        if (prev == null) {
            throw new RuntimeException("Cannot find shinryou attr to update: " + shinryouAttr.shinryouId);
        }
        ts.shinryouAttrTable.update(query, shinryouAttr);
        practiceLogger.logShinryouAttrUpdated(prev, shinryouAttr);
    }

    public void deleteShinryouAttr(int shinryouId) {
        ShinryouAttrDTO deleted = findShinryouAttr(shinryouId);
        if (deleted == null) {
            throw new RuntimeException("Cannot find shinryou attr to delete: " + shinryouId);
        }
        ts.shinryouAttrTable.delete(query, shinryouId);
        practiceLogger.logShinryouAttrDeleted(deleted);
    }

    public void setShinryouAttr(int shinryouId, ShinryouAttrDTO attr) {
        if (attr == null || ShinryouAttrDTO.isEmpty(attr)) {
            deleteShinryouAttr(shinryouId);
        } else {
            ShinryouAttrDTO curr = findShinryouAttr(shinryouId);
            if (curr == null) {
                enterShinryouAttr(attr);
            } else {
                updateShinryouAttr(attr);
            }
        }
    }

    public ShinryouAttrDTO deleteShinryouTekiyou(int shinryouId) {
        ShinryouAttrDTO attr = findShinryouAttr(shinryouId);
        if (attr != null) {
            ShinryouAttrDTO updated = ShinryouAttrDTO.copy(attr);
            updated.tekiyou = null;
            if (ShinryouAttrDTO.isEmpty(updated)) {
                deleteShinryouAttr(shinryouId);
                return null;
            } else {
                updateShinryouAttr(updated);
                return updated;
            }
        } else {
            return null;
        }
    }

    public ShinryouAttrDTO setShinryouTekiyou(int shinryouId, String tekiyou) {
        ShinryouAttrDTO curr = findShinryouAttr(shinryouId);
        if (curr != null) {
            ShinryouAttrDTO attr = ShinryouAttrDTO.copy(curr);
            attr.tekiyou = tekiyou;
            if (ShinryouAttrDTO.isEmpty(attr)) {
                deleteShinryouAttr(shinryouId);
                return null;
            } else {
                updateShinryouAttr(attr);
                return attr;
            }
        } else {
            if (tekiyou != null && !tekiyou.isEmpty()) {
                ShinryouAttrDTO attr = new ShinryouAttrDTO();
                attr.shinryouId = shinryouId;
                attr.tekiyou = tekiyou;
                enterShinryouAttr(attr);
                return attr;
            } else {
                return null;
            }
        }
    }

    // Conduct ///////////////////////////////////////////////////////////////////////////////

    public int enterConduct(ConductDTO conduct) {
        ts.conductTable.insert(query, conduct);
        practiceLogger.logConductCreated(conduct);
        return conduct.conductId;
    }

    int countConductForVisit(int visitId) {
        String sql = "select count(*) from Conduct where visitId = ?";
        return getQuery().get(xlate(sql, ts.conductTable), intProjector, visitId);
    }

    public ConductDTO getConduct(int conductId) {
        return ts.conductTable.getById(query, conductId);
    }

    void deleteConduct(int conductId) {
        ConductDTO conduct = getConduct(conductId);
        if (conduct != null) {
            ts.conductTable.delete(query, conduct.conductId);
            practiceLogger.logConductDeleted(conduct);
        }
    }

    private void updateConduct(ConductDTO prev, ConductDTO conduct) {
        ts.conductTable.update(query, conduct);
        practiceLogger.logConductUpdated(prev, conduct);
    }

    public void modifyConductKind(int conductId, int conductKind) {
        ConductDTO prev = getConduct(conductId);
        ConductDTO updated = ConductDTO.copy(prev);
        updated.kind = conductKind;
        updateConduct(prev, updated);
    }

    public List<ConductDTO> listConduct(int visitId) {
        String sql = xlate("select * from Conduct where visitId = ?", ts.conductTable);
        return getQuery().query(sql, ts.conductTable, visitId);
    }

    public List<ConductFullDTO> listConductFullByIds(List<Integer> conductIds) {
        return conductIds.stream().map(this::getConductFull).collect(toList());
    }

    private ConductFullDTO extendConduct(ConductDTO conduct) {
        int conductId = conduct.conductId;
        ConductFullDTO conductFullDTO = new ConductFullDTO();
        conductFullDTO.conduct = conduct;
        conductFullDTO.gazouLabel = ts.gazouLabelTable.getById(query, conductId);
        conductFullDTO.conductShinryouList = listConductShinryouFull(conductId);
        conductFullDTO.conductDrugs = listConductDrugFull(conductId);
        conductFullDTO.conductKizaiList = listConductKizaiFull(conductId);
        return conductFullDTO;
    }

    public ConductFullDTO getConductFull(int conductId) {
        ConductDTO conduct = ts.conductTable.getById(query, conductId);
        if (conduct == null) {
            return null;
        }
        return extendConduct(conduct);
    }

    public List<ConductFullDTO> listConductFull(int visitId) {
        return listConduct(visitId).stream().map(this::extendConduct).collect(toList());
    }

    public ConductFullDTO enterConductFull(ConductEnterRequestDTO req) {
        ConductFullDTO result = new ConductFullDTO();
        ConductDTO conduct = new ConductDTO();
        conduct.visitId = req.visitId;
        conduct.kind = req.kind;
        enterConduct(conduct);
        result.conduct = conduct;
        int conductId = conduct.conductId;
        if (req.gazouLabel != null) {
            GazouLabelDTO gazouLabel = new GazouLabelDTO();
            gazouLabel.conductId = conductId;
            gazouLabel.label = req.gazouLabel;
            enterGazouLabel(gazouLabel);
            result.gazouLabel = gazouLabel;
        }
        if (req.shinryouList != null) {
            result.conductShinryouList = new ArrayList<>();
            req.shinryouList.forEach(shinryou -> {
                shinryou.conductId = conductId;
                enterConductShinryou(shinryou);
                result.conductShinryouList.add(getConductShinryouFull(shinryou.conductShinryouId));
            });
        }
        if (req.drugs != null) {
            result.conductDrugs = new ArrayList<>();
            req.drugs.forEach(drug -> {
                drug.conductId = conductId;
                enterConductDrug(drug);
                result.conductDrugs.add(getConductDrugFull(drug.conductDrugId));
            });
        }
        if (req.kizaiList != null) {
            result.conductKizaiList = new ArrayList<>();
            req.kizaiList.forEach(kizai -> {
                kizai.conductId = conductId;
                enterConductKizai(kizai);
                result.conductKizaiList.add(getConductKizaiFull(kizai.conductKizaiId));
            });
        }
        return result;
    }

    public void deleteConductFull(int conductId) {
        GazouLabelDTO gazouLabel = findGazouLabel(conductId);
        if (gazouLabel != null) {
            deleteGazouLabel(conductId);
        }
        listConductShinryou(conductId)
                .forEach(s -> deleteConductShinryou(s.conductShinryouId));
        listConductDrug(conductId)
                .forEach(d -> deleteConductDrug(d.conductDrugId));
        listConductKizai(conductId)
                .forEach(k -> deleteConductKizai(k.conductKizaiId));
        deleteConduct(conductId);
    }

    // GazouLabel ///////////////////////////////////////////////////////////////////////////

    public void enterGazouLabel(GazouLabelDTO gazouLabel) {
        ts.gazouLabelTable.insert(query, gazouLabel);
        practiceLogger.logGazouLabelCreated(gazouLabel);
    }

    public GazouLabelDTO findGazouLabel(int conductId) {
        return ts.gazouLabelTable.getById(query, conductId);
    }

    public void deleteGazouLabel(int conductId) {
        GazouLabelDTO deleted = findGazouLabel(conductId);
        if (deleted != null) {
            ts.gazouLabelTable.delete(query, deleted.conductId);
            practiceLogger.logGazouLabelDeleted(deleted);
        }
    }

    public void updateGazouLabel(GazouLabelDTO gazouLabel) {
        GazouLabelDTO prev = findGazouLabel(gazouLabel.conductId);
        if (prev == null) {
            throw new RuntimeException("Cannot find previous gazou label to update: " + gazouLabel);
        }
        ts.gazouLabelTable.update(query, gazouLabel);
        practiceLogger.logGazouLabelUpdated(prev, gazouLabel);
    }

    public void setGazouLabel(int conductId, String label) {
        if (label == null || label.isEmpty()) {
            deleteGazouLabel(conductId);
        } else {
            GazouLabelDTO gazouLabel = findGazouLabel(conductId);
            if (gazouLabel == null) {
                GazouLabelDTO newGazouLabel = new GazouLabelDTO();
                newGazouLabel.conductId = conductId;
                newGazouLabel.label = label;
                enterGazouLabel(newGazouLabel);
            } else {
                GazouLabelDTO modified = GazouLabelDTO.copy(gazouLabel);
                modified.label = label;
                updateGazouLabel(modified);
            }
        }
    }

    // ConductShinryou //////////////////////////////////////////////////////////////////////

    public int enterConductShinryou(ConductShinryouDTO shinryou) {
        ts.conductShinryouTable.insert(query, shinryou);
        practiceLogger.logConductShinryouCreated(shinryou);
        return shinryou.conductShinryouId;
    }

    public ConductShinryouDTO getConductShinryou(int conductShinryouId) {
        return ts.conductShinryouTable.getById(query, conductShinryouId);
    }

    public void deleteConductShinryou(int conductShinryouId) {
        ConductShinryouDTO deleted = getConductShinryou(conductShinryouId);
        if (deleted != null) {
            ts.conductShinryouTable.delete(query, deleted.conductShinryouId);
            practiceLogger.logConductShinryouDeleted(deleted);
        }
    }

    public List<ConductShinryouDTO> listConductShinryou(int conductId) {
        String sql = xlate("select * from ConductShinryou where conductId = ? order by conductShinryouId",
                ts.conductShinryouTable);
        return getQuery().query(sql, ts.conductShinryouTable, conductId);
    }

    public ConductShinryouFullDTO getConductShinryouFull(int conductShinryouId) {
        String sql = xlate("select s.*, m.* from ConductShinryou s, Conduct c, ShinryouMaster m, Visit v " +
                        " where s.conductShinryouId = ? and s.conductId = c.conductId and c.visitId = v.visitId " +
                        " and s.shinryoucode = m.shinryoucode and " +
                        ts.dialect.isValidAt("m.validFrom", "m.validUpto", "v.visitedAt"),
                ts.conductShinryouTable, "s", ts.conductTable, "c", ts.shinryouMasterTable, "m",
                ts.visitTable, "v");
        return getQuery().get(sql,
                biProjector(ts.conductShinryouTable, ts.shinryouMasterTable, ConductShinryouFullDTO::create),
                conductShinryouId);
    }

    public List<ConductShinryouFullDTO> listConductShinryouFull(int conductId) {
        String sql = xlate("select s.*, m.* from ConductShinryou s, Conduct c, ShinryouMaster m, Visit v " +
                        " where s.conductId = ? and s.conductId = c.conductId and c.visitId = v.visitId " +
                        " and s.shinryoucode = m.shinryoucode and " +
                        ts.dialect.isValidAt("m.validFrom", "m.validUpto", "v.visitedAt") +
                        " order by s.conductShinryouId",
                ts.conductShinryouTable, "s", ts.conductTable, "c", ts.shinryouMasterTable, "m",
                ts.visitTable, "v");
        return getQuery().query(sql,
                biProjector(ts.conductShinryouTable, ts.shinryouMasterTable, ConductShinryouFullDTO::create),
                conductId);
    }

    // ConductDrug ///////////////////////////////////////////////////////////////////////////

    public int enterConductDrug(ConductDrugDTO drug) {
        ts.conductDrugTable.insert(query, drug);
        practiceLogger.logConductDrugCreated(drug);
        return drug.conductDrugId;
    }

    public ConductDrugDTO getConductDrug(int conductDrugId) {
        return ts.conductDrugTable.getById(query, conductDrugId);
    }

    public void deleteConductDrug(int conductDrugId) {
        ConductDrugDTO deleted = getConductDrug(conductDrugId);
        if (deleted != null) {
            ts.conductDrugTable.delete(query, deleted.conductDrugId);
            practiceLogger.logConductDrugDeleted(deleted);
        }
    }

    public List<ConductDrugDTO> listConductDrug(int conductId) {
        String sql = xlate("select * from ConductDrug where conductId = ? order by conductDrugId",
                ts.conductDrugTable);
        return getQuery().query(sql, ts.conductDrugTable, conductId);
    }

    public ConductDrugFullDTO getConductDrugFull(int conductDrugId) {
        String sql = xlate("select d.*, m.* from ConductDrug d, Conduct c, IyakuhinMaster m, Visit v " +
                        " where d.conductDrugId = ? and d.conductId = c.conductId and c.visitId = v.visitId " +
                        " and d.iyakuhincode = m.iyakuhincode and " +
                        ts.dialect.isValidAt("m.validFrom", "m.validUpto", "v.visitedAt"),
                ts.conductDrugTable, "d", ts.conductTable, "c", ts.iyakuhinMasterTable, "m",
                ts.visitTable, "v");
        return getQuery().get(sql,
                biProjector(ts.conductDrugTable, ts.iyakuhinMasterTable, ConductDrugFullDTO::create),
                conductDrugId);
    }

    public List<ConductDrugFullDTO> listConductDrugFull(int conductId) {
        String sql = xlate("select d.*, m.* from ConductDrug d, Conduct c, IyakuhinMaster m, Visit v " +
                        " where d.conductId = ? and d.conductId = c.conductId and c.visitId = v.visitId " +
                        " and d.iyakuhincode = m.iyakuhincode and " +
                        ts.dialect.isValidAt("m.validFrom", "m.validUpto", "v.visitedAt") +
                        " order by d.conductDrugId",
                ts.conductDrugTable, "d", ts.conductTable, "c", ts.iyakuhinMasterTable, "m",
                ts.visitTable, "v");
        return getQuery().query(sql,
                biProjector(ts.conductDrugTable, ts.iyakuhinMasterTable, ConductDrugFullDTO::create),
                conductId);
    }

    // ConductKizai //////////////////////////////////////////////////////////////////////////

    public int enterConductKizai(ConductKizaiDTO kizai) {
        ts.conductKizaiTable.insert(query, kizai);
        practiceLogger.logConductKizaiCreated(kizai);
        return kizai.conductKizaiId;
    }

    public ConductKizaiDTO getConductKizai(int conductKizaiId) {
        return ts.conductKizaiTable.getById(query, conductKizaiId);
    }

    public void deleteConductKizai(int conductKizaiId) {
        ConductKizaiDTO deleted = getConductKizai(conductKizaiId);
        if (deleted != null) {
            ts.conductKizaiTable.delete(query, deleted.conductKizaiId);
            practiceLogger.logConductKizaiDeleted(deleted);
        }
    }

    public List<ConductKizaiDTO> listConductKizai(int conductId) {
        String sql = xlate("select * from ConductKizai where conductId = ? order by conductKizaiId",
                ts.conductKizaiTable);
        return getQuery().query(sql, ts.conductKizaiTable, conductId);
    }

    public ConductKizaiFullDTO getConductKizaiFull(int conductKizaiId) {
        String sql = xlate("select k.*, m.* from ConductKizai k, Conduct c, KizaiMaster m, Visit v " +
                        " where k.conductKizaiId = ? and k.conductId = c.conductId and c.visitId = v.visitId " +
                        " and k.kizaicode = m.kizaicode and " +
                        ts.dialect.isValidAt("m.validFrom", "m.validUpto", "v.visitedAt"),
                ts.conductKizaiTable, "k", ts.conductTable, "c", ts.kizaiMasterTable, "m",
                ts.visitTable, "v");
        return getQuery().get(sql,
                biProjector(ts.conductKizaiTable, ts.kizaiMasterTable, ConductKizaiFullDTO::create),
                conductKizaiId);
    }

    public List<ConductKizaiFullDTO> listConductKizaiFull(int conductId) {
        String sql = xlate("select k.*, m.* from ConductKizai k, Conduct c, KizaiMaster m, Visit v " +
                        " where k.conductId = ? and k.conductId = c.conductId and c.visitId = v.visitId " +
                        " and k.kizaicode = m.kizaicode and " +
                        ts.dialect.isValidAt("m.validFrom", "m.validUpto", "v.visitedAt") +
                        " order by k.conductKizaiId",
                ts.conductKizaiTable, "k", ts.conductTable, "c", ts.kizaiMasterTable, "m",
                ts.visitTable, "v");
        return getQuery().query(sql,
                biProjector(ts.conductKizaiTable, ts.kizaiMasterTable, ConductKizaiFullDTO::create),
                conductId);
    }

    // BatchEnter ////////////////////////////////////////////////////////////////////////

    public BatchEnterResultDTO batchEnter(BatchEnterRequestDTO req) {
        BatchEnterResultDTO result = new BatchEnterResultDTO();
        result.drugIds = new ArrayList<>();
        result.shinryouIds = new ArrayList<>();
        result.conductIds = new ArrayList<>();
        if (req.drugs != null) {
            req.drugs.forEach(drugWithAttr -> {
                enterDrugWithAttr(drugWithAttr);
                result.drugIds.add(drugWithAttr.drug.drugId);
            });
        }
        if (req.shinryouList != null) {
            req.shinryouList.forEach(shinryouWithAttr -> {
                enterShinryouWithAttr(shinryouWithAttr);
                result.shinryouIds.add(shinryouWithAttr.shinryou.shinryouId);
            });
        }
        if (req.conducts != null) {
            req.conducts.forEach(conductReq -> {
                ConductFullDTO c = enterConductFull(conductReq);
                result.conductIds.add(c.conduct.conductId);
            });
        }
        return result;
    }

    // Shahokokuho //////////////////////////////////////////////////////////////////////////////

    public ShahokokuhoDTO getShahokokuho(int shahokokuhoId) {
        return ts.shahokokuhoTable.getById(query, shahokokuhoId);
    }

    public int enterShahokokuho(ShahokokuhoDTO shahokokuho) {
        ts.shahokokuhoTable.insert(query, shahokokuho);
        practiceLogger.logShahokokuhoCreated(shahokokuho);
        return shahokokuho.shahokokuhoId;
    }

    public List<ShahokokuhoDTO> findAvailableShahokokuho(int patientId, LocalDate at) {
        String sql = xlate("select * from Shahokokuho where patientId = ? and " +
                        ts.dialect.isValidAt("validFrom", "validUpto", "?"),
                ts.shahokokuhoTable);
        return getQuery().query(sql, ts.shahokokuhoTable, patientId, at, at);
    }

    public List<ShahokokuhoDTO> listShahokokuho(int patientId) {
        String sql = xlate("select * from Shahokokuho where patientId = ? order by shahokokuhoId desc",
                ts.shahokokuhoTable);
        return getQuery().query(sql, ts.shahokokuhoTable, patientId);
    }

    public void updateShahokokuho(ShahokokuhoDTO shahokokuho) {
        ShahokokuhoDTO prev = getShahokokuho(shahokokuho.shahokokuhoId);
        if (prev == null) {
            throw new RuntimeException("Cannot find shahokokuho with shahokokuhoId: " + shahokokuho.shahokokuhoId);
        }
        ts.shahokokuhoTable.update(query, shahokokuho);
        practiceLogger.logShahokokuhoUpdated(prev, shahokokuho);
    }

    void deleteShahokokuho(int shahokokuhoId) {
        ShahokokuhoDTO dto = getShahokokuho(shahokokuhoId);
        if (dto != null) {
            ts.shahokokuhoTable.delete(query, shahokokuhoId);
            practiceLogger.logShahokokuhoDeleted(dto);
        }
    }

    public void deleteShahokokuhoSafely(ShahokokuhoDTO shahokokuho) {
        deleteShahokokuhoSafely(shahokokuho.shahokokuhoId);
    }

    public void deleteShahokokuhoSafely(int shahokokuhoId) {
        int count = countVisitByShahokokuhoId(shahokokuhoId);
        if (count != 0) {
            throw new RuntimeException("この社保・国保はすでに使用されているので、削除できません。");
        }
        deleteShahokokuho(shahokokuhoId);
    }

    // Koukikourei //////////////////////////////////////////////////////////////////////////////

    public KoukikoureiDTO getKoukikourei(int koukikoureiId) {
        return ts.koukikoureiTable.getById(query, koukikoureiId);
    }

    public List<KoukikoureiDTO> findAvailableKoukikourei(int patientId, LocalDate at) {
        String sql = xlate("select * from Koukikourei where patientId = ? and " +
                        ts.dialect.isValidAt("validFrom", "validUpto", "?"),
                ts.koukikoureiTable);
        return getQuery().query(sql, ts.koukikoureiTable, patientId, at, at);
    }

    public int enterKoukikourei(KoukikoureiDTO koukikourei) {
        ts.koukikoureiTable.insert(query, koukikourei);
        practiceLogger.logKoukikoureiCreated(koukikourei);
        return koukikourei.koukikoureiId;
    }

    public void updateKoukikourei(KoukikoureiDTO koukikourei) {
        KoukikoureiDTO prev = getKoukikourei(koukikourei.koukikoureiId);
        if (prev == null) {
            throw new RuntimeException("Cannot find koukikourei with koukikoureiId: " + koukikourei.koukikoureiId);
        }
        ts.koukikoureiTable.update(query, koukikourei);
        practiceLogger.logKoukikoureiUpdated(prev, koukikourei);
    }

    public List<KoukikoureiDTO> listKoukikourei(int patientId) {
        String sql = xlate("select * from Koukikourei where patientId = ? order by koukikoureiId desc",
                ts.koukikoureiTable);
        return getQuery().query(sql, ts.koukikoureiTable, patientId);
    }

    public void deleteKoukikourei(KoukikoureiDTO dto) {
        deleteKoukikourei(dto.koukikoureiId);
    }

    void deleteKoukikourei(int koukikoureiId) {
        KoukikoureiDTO dto = getKoukikourei(koukikoureiId);
        if (dto != null) {
            ts.koukikoureiTable.delete(query, koukikoureiId);
            practiceLogger.logKoukikoureiDeleted(dto);
        }
    }

    public void deleteKoukikoureiSafely(int koukikoureiId) {
        int count = countVisitByKoukikoureiId(koukikoureiId);
        if (count != 0) {
            throw new RuntimeException("この後期高齢保険はすでに使用されているので、削除できません。");
        }
        deleteKoukikourei(koukikoureiId);
    }

    // Roujin ////////////////////////////////////////////////////////////////////////////////////

    public RoujinDTO getRoujin(int roujinId) {
        return ts.roujinTable.getById(query, roujinId);
    }

    public int enterRoujin(RoujinDTO roujin) {
        ts.roujinTable.insert(query, roujin);
        practiceLogger.logRoujinCreated(roujin);
        return roujin.roujinId;
    }

    public void updateRoujin(RoujinDTO roujin) {
        RoujinDTO prev = getRoujin(roujin.roujinId);
        if (prev == null) {
            throw new RuntimeException("Cannot find roujin with roujinId: " + roujin.roujinId);
        }
        ts.roujinTable.update(query, roujin);
        practiceLogger.logRoujinUpdated(prev, roujin);
    }

    public List<RoujinDTO> findAvailableRoujin(int patientId, LocalDate at) {
        String sql = xlate("select * from Roujin where patientId = ? and " +
                        ts.dialect.isValidAt("validFrom", "validUpto", "?"),
                ts.roujinTable);
        return getQuery().query(sql, ts.roujinTable, patientId, at, at);
    }

    public List<RoujinDTO> listRoujin(int patientId) {
        String sql = xlate("select * from Roujin where patientId = ? order by roujinId desc",
                ts.roujinTable);
        return getQuery().query(sql, ts.roujinTable, patientId);
    }

    void deleteRoujin(int roujinId) {
        RoujinDTO dto = getRoujin(roujinId);
        if (dto != null) {
            ts.roujinTable.delete(query, roujinId);
            practiceLogger.logRoujinDeleted(dto);
        }
    }

    public void deleteRoujinSafely(RoujinDTO roujin){
        deleteRoujinSafely(roujin.roujinId);
    }

    public void deleteRoujinSafely(int roujinId) {
        int count = countVisitByRoujinId(roujinId);
        if (count != 0) {
            throw new RuntimeException("この老人保険はすでに使用されているので、削除できません。");
        }
        deleteRoujin(roujinId);
    }

    // Kouhi //////////////////////////////////////////////////////////////////////////////////////

    public KouhiDTO getKouhi(int kouhiId) {
        return ts.kouhiTable.getById(query, kouhiId);
    }

    public List<KouhiDTO> findAvailableKouhi(int patientId, LocalDate at) {
        String sql = xlate("select * from Kouhi where patientId = ? and " +
                        ts.dialect.isValidAt("validFrom", "validUpto", "?"),
                ts.kouhiTable);
        return getQuery().query(sql, ts.kouhiTable, patientId, at, at);
    }

    public int enterKouhi(KouhiDTO kouhi) {
        ts.kouhiTable.insert(query, kouhi);
        practiceLogger.logKouhiCreated(kouhi);
        return kouhi.kouhiId;
    }

    public void updateKouhi(KouhiDTO kouhi) {
        KouhiDTO prev = getKouhi(kouhi.kouhiId);
        if (prev == null) {
            throw new RuntimeException("Cannot find kouhi with kouhiId: " + kouhi.kouhiId);
        }
        ts.kouhiTable.update(query, kouhi);
        practiceLogger.logKouhiUpdated(prev, kouhi);
    }

    public List<KouhiDTO> listKouhi(int patientId) {
        String sql = xlate("select * from Kouhi where patientId = ? order by kouhiId desc",
                ts.kouhiTable);
        return getQuery().query(sql, ts.kouhiTable, patientId);
    }

    void deleteKouhi(int kouhiId) {
        KouhiDTO dto = getKouhi(kouhiId);
        if (dto != null) {
            ts.kouhiTable.delete(query, kouhiId);
            practiceLogger.logKouhiDeleted(dto);
        }
    }

    public void deleteKouhiSafely(KouhiDTO kouhi){
        deleteKouhiSafely(kouhi.kouhiId);
    }

    private void deleteKouhiSafely(int kouhiId) {
        int count = countVisitByKouhiId(kouhiId);
        if (count != 0) {
            throw new RuntimeException("この公費負担はすでに使用されているので、削除できません。");
        }
        deleteKouhi(kouhiId);
    }

    // Disease ////////////////////////////////////////////////////////////////////////////////////

    public int enterDisease(DiseaseDTO disease) {
        ts.diseaseTable.insert(query, disease);
        practiceLogger.logDiseaseCreated(disease);
        return disease.diseaseId;
    }

    public int enterNewDisease(DiseaseNewDTO disease) {
        enterDisease(disease.disease);
        int diseaseId = disease.disease.diseaseId;
        if (disease.adjList != null) {
            disease.adjList.forEach(adj -> {
                adj.diseaseId = diseaseId;
                enterDiseaseAdj(adj);
            });
        }
        return diseaseId;
    }

    public DiseaseDTO getDisease(int diseaseId) {
        return ts.diseaseTable.getById(query, diseaseId);
    }

    public void updateDisease(DiseaseDTO disease) {
        DiseaseDTO prev = getDisease(disease.diseaseId);
        if (prev == null) {
            throw new RuntimeException("Cannot find previous disease to update: " + disease);
        }
        ts.diseaseTable.update(query, disease);
        practiceLogger.logDiseaseUpdated(prev, disease);
    }

    void deleteDisease(int diseaseId) {
        DiseaseDTO deleted = getDisease(diseaseId);
        if (deleted != null) {
            ts.diseaseTable.delete(query, diseaseId);
            practiceLogger.logDiseaseDeleted(deleted);
        }
    }

    public void deleteDiseaseWithAdj(int diseaseId) {
        List<DiseaseAdjDTO> adjList = listDiseaseAdj(diseaseId);
        for (DiseaseAdjDTO adj : adjList) {
            deleteDiseaseAdj(adj.diseaseAdjId);
        }
        deleteDisease(diseaseId);
    }

    public DiseaseFullDTO getDiseaseFull(int diseaseId) {
        String diseaseSql = xlate("select d.*, m.* from Disease d, ByoumeiMaster m " +
                        " where d.diseaseId = ? and d.shoubyoumeicode = m.shoubyoumeicode " +
                        " and " + ts.dialect.isValidAt("m.valid_from", "m.validUpto", "d.startDate"),
                ts.diseaseTable, "d", ts.byoumeiMasterTable, "m");
        DiseaseFullDTO result = getQuery().get(diseaseSql,
                biProjector(ts.diseaseTable, ts.byoumeiMasterTable, DiseaseFullDTO::create),
                diseaseId);
        String adjSql = xlate("select a.*, m.* from DiseaseAdj a, ShuushokugoMaster m " +
                        " where a.diseaseId = ? and a.shuushokugocode = m.shuushokugocode " +
                        " order by a.diseaseAdjId",
                ts.diseaseAdjTable, "a", ts.shuushokugoMasterTable, "m");
        result.adjList = getQuery().query(adjSql,
                biProjector(ts.diseaseAdjTable, ts.shuushokugoMasterTable, DiseaseAdjFullDTO::create),
                diseaseId);
        return result;
    }

    public List<DiseaseFullDTO> listCurrentDiseaseFull(int patientId) {
        String sql = xlate("select diseaseId from Disease where patientId = ? " +
                        " and " + ts.dialect.isValidUptoUnbound("endDate") +
                        " order by diseaseId ",
                ts.diseaseTable);
        List<Integer> diseaseIds = getQuery().query(sql, (rs, ctx) -> rs.getInt(ctx.nextIndex()), patientId);
        return diseaseIds.stream().map(this::getDiseaseFull).collect(toList());
    }

    public List<DiseaseFullDTO> listDiseaseFull(int patientId) {
        String sql = xlate("select diseaseId from Disease where patientId = ? order by diseaseId ",
                ts.diseaseTable);
        List<Integer> diseaseIds = getQuery().query(sql, (rs, ctx) -> rs.getInt(ctx.nextIndex()), patientId);
        return diseaseIds.stream().map(this::getDiseaseFull).collect(toList());
    }

    public void batchUpdateDiseaseEndReason(List<DiseaseModifyEndReasonDTO> modifications) {
        for (DiseaseModifyEndReasonDTO modify : modifications) {
            DiseaseDTO prev = getDisease(modify.diseaseId);
            DiseaseDTO d = DiseaseDTO.copy(prev);
            d.endDate = modify.endDate;
            d.endReason = modify.endReason;
            updateDisease(d);
        }
    }

    public void modifyDisease(DiseaseModifyDTO diseaseModify) {
        if (diseaseModify.shuushokugocodes == null) {
            diseaseModify.shuushokugocodes = Collections.emptyList();
        }
        DiseaseDTO disease = diseaseModify.disease;
        DiseaseDTO prevDisease = getDisease(disease.diseaseId);
        if (!disease.equals(prevDisease)) {
            updateDisease(disease);
        }
        List<DiseaseAdjDTO> prevAdjList = listDiseaseAdj(disease.diseaseId);
        List<Integer> prevAdjCodes = prevAdjList.stream().map(adj -> adj.shuushokugocode).collect(toList());
        if (!prevAdjCodes.equals(diseaseModify.shuushokugocodes)) {
            List<Integer> prevAdjIds = prevAdjList.stream().map(adj -> adj.diseaseAdjId).collect(toList());
            for (Integer diseaseAdjId : prevAdjIds) {
                deleteDiseaseAdj(diseaseAdjId);
            }
            for (Integer shuushokugocode : diseaseModify.shuushokugocodes) {
                DiseaseAdjDTO adj = new DiseaseAdjDTO();
                adj.diseaseId = disease.diseaseId;
                adj.shuushokugocode = shuushokugocode;
                enterDiseaseAdj(adj);
            }
        }
    }

    // DiseaseAdj ////////////////////////////////////////////////////////////////////////

    public int enterDiseaseAdj(DiseaseAdjDTO adj) {
        ts.diseaseAdjTable.insert(query, adj);
        practiceLogger.logDiseaseAdjCreated(adj);
        return adj.diseaseAdjId;
    }

    private DiseaseAdjDTO getDiseaseAdj(int diseaseAdjId) {
        return ts.diseaseAdjTable.getById(query, diseaseAdjId);
    }

    public void deleteDiseaseAdj(int diseaseAdjId) {
        DiseaseAdjDTO adj = getDiseaseAdj(diseaseAdjId);
        if (adj != null) {
            ts.diseaseAdjTable.delete(query, adj.diseaseAdjId);
            practiceLogger.logDiseaseAdjDeleted(adj);
        }
    }

    public List<DiseaseAdjDTO> listDiseaseAdj(int diseaseId) {
        String sql = "select * from DiseaseAdj where diseaseId = ? order by diseaseAdjId";
        return getQuery().query(xlate(sql, ts.diseaseAdjTable), ts.diseaseAdjTable, diseaseId);
    }

    public int deleteDiseaseAdjForDisease(DiseaseDTO disease) {
        int count = 0;
        String sql = "select * from DiseaseAdj where diseaseId = ? ";
        List<DiseaseAdjDTO> adjList = getQuery().query(xlate(sql, ts.diseaseAdjTable), ts.diseaseAdjTable,
                disease.diseaseId);
        for (DiseaseAdjDTO adj : adjList) {
            deleteDiseaseAdj(adj.diseaseAdjId);
            count += 1;
        }
        return count;
    }

    // PharmaQueue ///////////////////////////////////////////////////////////////////////

    public PharmaQueueDTO getPharmaQueue(int visitId) {
        return ts.pharmaQueueTable.getById(query, visitId);
    }

    public void enterPharmaQueue(PharmaQueueDTO pharmaQueue) {
        ts.pharmaQueueTable.insert(query, pharmaQueue);
        practiceLogger.logPharmaQueueCreated(pharmaQueue);
    }

    public void updatePharmaQueue(PharmaQueueDTO pharmaQueue) {
        PharmaQueueDTO prev = getPharmaQueue(pharmaQueue.visitId);
        if (prev == null) {
            throw new RuntimeException("Cannot find prev to update PharmaQueue: " + pharmaQueue);
        }
        ts.pharmaQueueTable.update(query, pharmaQueue);
        practiceLogger.logPharmaQueueUpdated(prev, pharmaQueue);
    }

    public void deletePharmaQueue(int visitId) {
        PharmaQueueDTO pharmaQueue = getPharmaQueue(visitId);
        if (pharmaQueue != null) {
            ts.pharmaQueueTable.delete(query, pharmaQueue.visitId);
            practiceLogger.logPharmaQueueDeleted(pharmaQueue);
        }
    }

    public PharmaQueueFullDTO getPharmaQueueFull(int visitId) {
        VisitDTO visitDTO = getVisit(visitId);
        PharmaQueueFullDTO result = new PharmaQueueFullDTO();
        result.visitId = visitId;
        result.patient = getPatient(visitDTO.patientId);
        result.pharmaQueue = getPharmaQueue(visitId);
        result.wqueue = getWqueue(visitId);
        return result;
    }

    public List<PharmaQueueDTO> listPharmaQueue() {
        String sql = "select * from PharmaQueue order by visitId";
        return getQuery().query(xlate(sql, ts.pharmaQueueTable), ts.pharmaQueueTable);
    }

    public List<PharmaQueueFullDTO> listPharmaQueueFullForToday() {
        List<VisitPatientDTO> visitPatients = listTodaysVisit();
        if (visitPatients.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Integer, WqueueDTO> wqueueMap = new HashMap<>();
        Map<Integer, PharmaQueueDTO> pharmaQueueMap = new HashMap<>();
        listWqueue().forEach(wq -> wqueueMap.put(wq.visitId, wq));
        listPharmaQueue().forEach(pq -> pharmaQueueMap.put(pq.visitId, pq));
        return visitPatients.stream()
                .map(item -> {
                    int visitId = item.visit.visitId;
                    PharmaQueueFullDTO pharmaQueueFullDTO = new PharmaQueueFullDTO();
                    pharmaQueueFullDTO.visitId = visitId;
                    pharmaQueueFullDTO.patient = item.patient;
                    pharmaQueueFullDTO.pharmaQueue = pharmaQueueMap.get(visitId);
                    pharmaQueueFullDTO.wqueue = wqueueMap.get(visitId);
                    return pharmaQueueFullDTO;
                })
                .collect(Collectors.toList());
    }

    public List<PharmaQueueFullDTO> listPharmaQueueFullForPrescription() {
        return listPharmaQueue().stream()
                .map(pq -> pq.visitId)
                .map(this::getPharmaQueueFull)
                .collect(toList());
    }

    // PharmaDrug ////////////////////////////////////////////////////////////////////////

    public PharmaDrugDTO getPharmaDrug(int iyakuhincode) {
        return ts.pharmaDrugTable.getById(query, iyakuhincode);
    }

    public void enterPharmaDrug(PharmaDrugDTO pharmaDrug) {
        ts.pharmaDrugTable.insert(query, pharmaDrug);
        practiceLogger.logPharmaDrugCreated(pharmaDrug);
    }

    public void updatePharmaDrug(PharmaDrugDTO pharmaDrug) {
        PharmaDrugDTO prev = getPharmaDrug(pharmaDrug.iyakuhincode);
        ts.pharmaDrugTable.update(query, pharmaDrug);
        practiceLogger.logPharmaDrugUpdated(prev, pharmaDrug);
    }

    public void deletePharmaDrug(int iyakuhincode) {
        PharmaDrugDTO prev = getPharmaDrug(iyakuhincode);
        ts.pharmaDrugTable.delete(query, iyakuhincode);
        practiceLogger.logPharmaDrugDeleted(prev);
    }

    public List<PharmaDrugNameDTO> listAllPharmaDrugNames() {
        String sql = "select m.iyakuhincode, m.name, m.yomi from PharmaDrug p, IyakuhinMaster m where " +
                " p.iyakuhincode = m.iyakuhincode group by m.iyakuhincode, m.name ";
        return getQuery().query(xlate(sql, ts.pharmaDrugTable, "p", ts.iyakuhinMasterTable, "m"),
                (rs, ctx) -> {
                    PharmaDrugNameDTO result = new PharmaDrugNameDTO();
                    result.iyakuhincode = rs.getInt(ctx.nextIndex());
                    result.name = rs.getString(ctx.nextIndex());
                    result.yomi = rs.getString(ctx.nextIndex());
                    return result;
                });
    }

    // ShinryouMaster ////////////////////////////////////////////////////////////////////

    public ShinryouMasterDTO findShinryouMasterByName(String name, LocalDate at) {
        String sql = xlate("select * from ShinryouMaster where name = ? " +
                        " and " + ts.dialect.isValidAt("validFrom", "validUpto", "?") +
                        " limit 1",
                ts.shinryouMasterTable);
        String atString = at.toString();
        List<ShinryouMasterDTO> matches = getQuery().query(sql,
                ts.shinryouMasterTable, name, atString, atString);
        return matches.size() == 0 ? null : matches.get(0);
    }

    public ShinryouMasterDTO resolveShinryouMasterByName(List<String> nameCandidates, LocalDate at) {
        for (String name : nameCandidates) {
            ShinryouMasterDTO m = findShinryouMasterByName(name, at);
            if (m != null) {
                return m;
            }
        }
        return null;
    }

    public Map<String, Integer> batchResolveShinryouNames(LocalDate at, List<List<String>> args) {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (List<String> arg : args) {
            if (arg.size() < 1) {
                continue;
            }
            String key = arg.get(0);
            if (arg.size() == 1) {
                ShinryouMasterDTO m = findShinryouMasterByName(key, at);
                if (m != null) {
                    result.put(key, m.shinryoucode);
                }
            } else {
                ShinryouMasterDTO m = resolveShinryouMasterByName(arg.subList(1, arg.size()), at);
                if (m != null) {
                    result.put(key, m.shinryoucode);
                }
            }
        }
        return result;
    }

    public List<ShinryouMasterDTO> searchShinryouMaster(String text, LocalDate at) {
        String sql = xlate("select * from ShinryouMaster where name like ? " +
                        " and " + ts.dialect.isValidAt("validFrom", "validUpto", "?"),
                ts.shinryouMasterTable);
        String searchText = "%" + text + "%";
        String atString = at.toString();
        return getQuery().query(sql, ts.shinryouMasterTable, searchText, atString, atString);
    }

    public ShinryouMasterDTO getShinryouMaster(int shinryoucode, LocalDate at) {
        String sql = xlate("select * from ShinryouMaster " +
                        " where shinryoucode = ? " +
                        " and " + ts.dialect.isValidAt("validFrom", "validUpto", "?"),
                ts.shinryouMasterTable);
        String atString = at.toString();
        return getQuery().get(sql, ts.shinryouMasterTable, shinryoucode, atString, atString);
    }

    // IyakuhinMaster /////////////////////////////////////////////////////////////////////

    public IyakuhinMasterDTO getIyakuhinMaster(int iyakuhincode, LocalDate at) {
        String sql = xlate("select * from IyakuhinMaster where iyakuhincode = ? " +
                        " and " + ts.dialect.isValidAt("validFrom", "validUpto", "?"),
                ts.iyakuhinMasterTable);
        String atString = at.toString();
        return getQuery().get(sql, ts.iyakuhinMasterTable, iyakuhincode, atString, atString);
    }

    public List<IyakuhinMasterDTO> searchIyakuhinMaster(String text, LocalDate at) {
        String sql = xlate("select * from IyakuhinMaster where name like ? " +
                        " and " + ts.dialect.isValidAt("validFrom", "validUpto", "?"),
                ts.iyakuhinMasterTable);
        String searchText = "%" + text + "%";
        String atString = at.toString();
        return getQuery().query(sql, ts.iyakuhinMasterTable, searchText, atString, atString);
    }

    // KizaiMaster ///////////////////////////////////////////////////////////////////////

    public KizaiMasterDTO getKizaiMaster(int kizaicode, LocalDate at) {
        String sql = xlate("select * from KizaiMaster where kizaicode = ? " +
                        " and " + ts.dialect.isValidAt("validFrom", "validUpto", "?"),
                ts.kizaiMasterTable);
        String atString = at.toString();
        return getQuery().get(sql, ts.kizaiMasterTable, kizaicode, atString, atString);
    }

    public KizaiMasterDTO findKizaiMasterByName(String name, LocalDate at) {
        String sql = xlate("select * from KizaiMaster where name = ? " +
                        " and " + ts.dialect.isValidAt("validFrom", "validUpto", "?") +
                        " limit 1",
                ts.kizaiMasterTable);
        String atString = at.toString();
        List<KizaiMasterDTO> matches = getQuery().query(sql,
                ts.kizaiMasterTable, name, atString, atString);
        return matches.size() == 0 ? null : matches.get(0);
    }

    public KizaiMasterDTO resolveKizaiMasterByCandidates(List<String> nameCandidates, LocalDate at) {
        for (String name : nameCandidates) {
            KizaiMasterDTO m = findKizaiMasterByName(name, at);
            if (m != null) {
                return m;
            }
        }
        return null;
    }

    public Map<String, Integer> batchResolveKizaiNames(LocalDate at, List<List<String>> args) {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (List<String> arg : args) {
            if (arg.size() < 1) {
                continue;
            }
            String key = arg.get(0);
            if (arg.size() == 1) {
                KizaiMasterDTO m = findKizaiMasterByName(key, at);
                if (m != null) {
                    result.put(key, m.kizaicode);
                }
            } else {
                KizaiMasterDTO m = resolveKizaiMasterByCandidates(arg.subList(1, arg.size()), at);
                if (m != null) {
                    result.put(key, m.kizaicode);
                }
            }
        }
        return result;
    }

    public List<KizaiMasterDTO> searchKizaiMaster(String text, LocalDate at) {
        String sql = xlate("select * from KizaiMaster where name like ? " +
                        " and " + ts.dialect.isValidAt("validFrom", "validUpto", "?"),
                ts.kizaiMasterTable);
        String searchText = "%" + text + "%";
        String atString = at.toString();
        return getQuery().query(sql, ts.kizaiMasterTable, searchText, atString, atString);
    }

    // ByoumeiMaster /////////////////////////////////////////////////////////////////////

    public List<ByoumeiMasterDTO> searchByoumeiMaster(String text, LocalDate at) {
        String sql = xlate("select * from ByoumeiMaster where name like ? " +
                        " and " + ts.dialect.isValidAt("validFrom", "validUpto", "?"),
                ts.byoumeiMasterTable);
        String searchText = "%" + text + "%";
        String atString = at.toString();
        return getQuery().query(sql, ts.byoumeiMasterTable, searchText, atString, atString);
    }

    public ByoumeiMasterDTO findByoumeiMasterByName(String name, LocalDate at) {
        String sql = xlate("select * from ByoumeiMaster where name = ? " +
                        " and " + ts.dialect.isValidAt("validFrom", "validUpto", "?"),
                ts.byoumeiMasterTable);
        String atString = at.toString();
        return getQuery().get(sql, ts.byoumeiMasterTable, name, atString, atString);
    }

    // ShuushokugoMaster /////////////////////////////////////////////////////////////////

    public List<ShuushokugoMasterDTO> searchShuushokugoMaster(String text, LocalDate at) {
        String sql = xlate("select * from ShuushokugoMaster where name like ?",
                ts.shuushokugoMasterTable);
        String searchText = "%" + text + "%";
        return getQuery().query(sql, ts.shuushokugoMasterTable, searchText);
    }

    public ShuushokugoMasterDTO getShuushokugoMasterByName(String name) {
        String sql = xlate("select * from ShuushokugoMaster where name = ?",
                ts.shuushokugoMasterTable);
        return getQuery().get(sql, ts.shuushokugoMasterTable, name);
    }

    // PrescExample //////////////////////////////////////////////////////////////////////

    public PrescExampleDTO getPrescExample(int prescExampleId) {
        return ts.prescExampleTable.getById(query, prescExampleId);
    }

    public int enterPrescExample(PrescExampleDTO prescExample) {
        ts.prescExampleTable.insert(query, prescExample);
        practiceLogger.logPrescExampleCreated(prescExample);
        return prescExample.prescExampleId;
    }

    public void updatePrescExample(PrescExampleDTO prescExample) {
        PrescExampleDTO prev = getPrescExample(prescExample.prescExampleId);
        if (prev == null) {
            throw new RuntimeException("Cannot find presc example to update: " +
                    prescExample.prescExampleId);
        }
        ts.prescExampleTable.update(query, prescExample);
        practiceLogger.logPrescExampleUpdated(prev, prescExample);
    }

    public void deletePrescExample(int prescExampleId) {
        PrescExampleDTO deleted = getPrescExample(prescExampleId);
        if (deleted == null) {
            throw new RuntimeException("Cannot find presc example to delete: " +
                    prescExampleId);
        }
        ts.prescExampleTable.delete(query, prescExampleId);
        practiceLogger.logPrescExampleDeleted(deleted);
    }

    public List<PrescExampleFullDTO> searchPrescExample(String text) {
        String sql = xlate("select p.*, m.* from PrescExample p, IyakuhinMaster m where " +
                        " m.iyakuhincode = p.iyakuhincode and m.validFrom = p.masterValidFrom " +
                        " and m.name like ? ",
                ts.prescExampleTable, "p", ts.iyakuhinMasterTable, "m");
        String searchText = "%" + text + "%";
        return getQuery().query(sql,
                biProjector(ts.prescExampleTable, ts.iyakuhinMasterTable, PrescExampleFullDTO::create),
                searchText);
    }

    public List<PrescExampleFullDTO> listAllPrescExample() {
        String sql = xlate("select p.*, m.* from PrescExample p, IyakuhinMaster m where " +
                        " m.iyakuhincode = p.iyakuhincode and m.validFrom = p.masterValidFrom ",
                ts.prescExampleTable, "p", ts.iyakuhinMasterTable, "m");
        return getQuery().query(sql,
                biProjector(ts.prescExampleTable, ts.iyakuhinMasterTable, PrescExampleFullDTO::create));
    }

    // Pharma ////////////////////////////////////////////////////////////////////////////

    public void prescDone(int visitId) {
        markDrugsAsPrescribed(visitId);
        PharmaQueueDTO pharmaQueue = getPharmaQueue(visitId);
        if (pharmaQueue != null) {
            deletePharmaQueue(visitId);
        }
        WqueueDTO wqueue = getWqueue(visitId);
        if (wqueue != null) {
            deleteWqueue(visitId);
        }
    }

    // PracticeLog ///////////////////////////////////////////////////////////////////////

    public PracticeLogDTO getLastPracticeLog() {
        String sql = xlate("select * from PracticeLog order by serialId desc limit 1",
                ts.practiceLogTable);
        return getQuery().get(sql, ts.practiceLogTable);
    }

    public int getLastPracticeLogId() {
        String sql = "select serialId from PracticeLog order by serialId desc limit 1";
        return getQuery().get(xlate(sql, ts.practiceLogTable), intProjector);
    }

    public List<PracticeLogDTO> listPracticeLogSince(int afterThisId) {
        String sql = xlate("select * from PracticeLog where serialId > ? order by serialId",
                ts.practiceLogTable);
        return getQuery().query(sql, ts.practiceLogTable, afterThisId);
    }

//    public List<PracticeLogDTO> listPracticeLogInRange(int afterThisId, int beforeThisId) {
//        String sql = xlate("select * from PracticeLog where serialId > ? and serialId < ? " +
//                        " order by serialID",
//                ts.practiceLogTable);
//        return getQuery().query(sql, ts.practiceLogTable, afterThisId, beforeThisId);
//    }

    public List<PracticeLogDTO> listAllPracticeLog(LocalDate date) {
        String sql = xlate("select * from PracticeLog where date(createdAt) = ? order by serialId",
                ts.practiceLogTable);
        return getQuery().query(sql, ts.practiceLogTable, date.toString());
    }

    public List<PracticeLogDTO> listTodaysPracticeLog() {
        String sql = xlate("select * from PracticeLog where date(createdAt) = current_date" +
                        " order by serialId",
                ts.practiceLogTable);
        return getQuery().query(sql, ts.practiceLogTable);
    }

    public List<PracticeLogDTO> listTodaysPracticeLogBefore(int beforeThisId) {
        logger.debug("enter listTodaysPracticeLogBefore: {}", beforeThisId);
        String sql = xlate("select * from PracticeLog where date(createdAt) = current_date" +
                        " and serialId < ? order by serialId",
                ts.practiceLogTable);
        return getQuery().query(sql, ts.practiceLogTable, beforeThisId);
    }

    // Hotline ///////////////////////////////////////////////////////////////////////////////

    public Integer getLastHotlineId() {
        String sql = "select * from Hotline order by hotlineId desc limit 1";
        HotlineDTO hotline = getQuery().get(xlate(sql, ts.hotlineTable), ts.hotlineTable);
        return hotline == null ? 0 : hotline.hotlineId;
    }

    public List<HotlineDTO> listHotlineInRange(int lowerHotlineId, int upperHotlineId) {
        String sql = "select * from Hotline where hotlineId >= ? and hotlineId <= ? order by hotlineId";
        return getQuery().query(xlate(sql, ts.hotlineTable), ts.hotlineTable, lowerHotlineId, upperHotlineId);
    }

    public List<HotlineDTO> listTodaysHotlineInRange(int afterId, int beforeId) {
        String sql = "select * from Hotline where date(postedAt) = CURRENT_DATE " +
                " and hotlineId > ? and hotlineId < ? order by hotlineId";
        return getQuery().query(xlate(sql, ts.hotlineTable), ts.hotlineTable, afterId, beforeId);
    }

    public List<HotlineDTO> listRecentHotline(int thresholdHotlineId) {
        String sql = "select * from Hotline where hotlineId > ? order by hotlineId";
        return getQuery().query(xlate(sql, ts.hotlineTable), ts.hotlineTable, thresholdHotlineId);
    }

    public Integer enterHotline(HotlineDTO hotline) {
        ts.hotlineTable.insert(query, hotline);
        hotlineLogger.logCreated(hotline);
        return hotline.hotlineId;
    }

    public void hotlineBeep(String target) {
        hotlineLogger.logBeep(target);
    }

    public List<HotlineDTO> listTodaysHotline() {
        String sql = "select * from Hotline where date(postedAt) = CURRENT_DATE order by hotlineId";
        return getQuery().query(xlate(sql, ts.hotlineTable), ts.hotlineTable);
    }

    public List<HotlineDTO> listTodaysHotlineAfter(int afterId) {
        String sql = "select * from Hotline where date(postedAt) = CURRENT_DATE " +
                " and hotlineId > ? order by hotlineId";
        return getQuery().query(xlate(sql, ts.hotlineTable), ts.hotlineTable, afterId);
    }

    public HotlineDTO getTodaysLastHotline() {
        String sql = "select * from Hotline where date(postedAt) = CURRENT_DATE order by hotlineId desc limit 1";
        return getQuery().get(xlate(sql, ts.hotlineTable), ts.hotlineTable);
    }

    // IntraclinicPost /////////////////////////////////////////////////////////////////////

    public IntraclinicPostDTO getIntraclinicPost(int id) {
        return ts.intraclinicPostTable.getById(query, id);
    }

    public int enterIntraclinicPost(IntraclinicPostDTO post) {
        ts.intraclinicPostTable.insert(query, post);
        return post.id;
    }

    public void updateIntraclinicPost(IntraclinicPostDTO post) {
        ts.intraclinicPostTable.update(query, post);
    }

    public List<IntraclinicPostDTO> listRecentIntraclinicPost(int n) {
        String sql = "select * from IntraclinicPost order by id desc limit ?";
        return getQuery().query(xlate(sql, ts.intraclinicPostTable), ts.intraclinicPostTable, n);
    }

    public int countIntraclinicPost() {
        String sql = "select count(*) from IntraclinicPost";
        return getQuery().getInt(xlate(sql, ts.intraclinicPostTable));
    }

    public List<IntraclinicPostDTO> listIntraclinicPostPage(int page, int itemsPerPage) {
        String sql = "select * from IntraclinicPost order by id desc limit ? offset ?";
        return getQuery().query(xlate(sql, ts.intraclinicPostTable), ts.intraclinicPostTable,
                itemsPerPage, page * itemsPerPage);
    }

    // IntraclinicComment /////////////////////////////////////////////////////////////////////

    public IntraclinicCommentDTO getIntraclinicComment(int id) {
        return ts.intraclinicCommentTable.getById(query, id);
    }

    public int enterIntraclinicComment(IntraclinicCommentDTO comment) {
        ts.intraclinicCommentTable.insert(query, comment);
        return comment.id;
    }

    public void updateIntraclinicComment(IntraclinicCommentDTO comment) {
        ts.intraclinicCommentTable.update(query, comment);
    }

    public void deleteIntraclinicComment(int id) {
        ts.intraclinicCommentTable.delete(query, id);
    }

    public List<IntraclinicCommentDTO> listIntraclinicCommentByPostId(int postId) {
        String sql = "select * from IntraclinicComment where postId = ? order by id";
        return getQuery().query(xlate(sql, ts.intraclinicCommentTable), ts.intraclinicCommentTable, postId);
    }

    // Intraclinic ///////////////////////////////////////////////////////////////////////////////

    public IntraclinicPostFullPageDTO pageIntraclinicPostFull(int page, int itemsPerPage) {
        IntraclinicPostFullPageDTO result = new IntraclinicPostFullPageDTO();
        result.totalPages = numberOfPages(countIntraclinicPost(), itemsPerPage);
        if (page >= result.totalPages) {
            page = result.totalPages - 1;
        }
        if (page < 0) {
            page = 0;
        }
        result.page = page;
        result.posts = listIntraclinicPostPage(page, itemsPerPage).stream()
                .map(post -> {
                    IntraclinicPostFullDTO full = new IntraclinicPostFullDTO();
                    full.post = post;
                    full.comments = listIntraclinicCommentByPostId(post.id);
                    return full;
                })
                .collect(toList());
        return result;
    }

    public List<VisitFull2DTO> listVisitByPatientHavingHoken(int patientId, int year, int month) throws Exception {
        String sql = "select visit_id from visit where patientId = ? " +
                " and year(visitedAt) = ? and month(visitedAt) = ? " +
                " and not (shahokokuhoId = 0 and koukikoureiId = 0 and roujinId = 0 " +
                "   and kouhi1Id = 0 and kouhi2Id = 0 and kouhi3Id = 0) ";
        List<Integer> visitIds = getQuery().query(xlate(sql, ts.visitTable), intProjector,
                patientId, year, month);
        return visitIds.stream()
                .map(this::getVisitFull2)
                .collect(toList());
    }

    public HokenDTO convertToHoken(VisitDTO visitDTO) throws Exception {
        HokenDTO hoken = new HokenDTO();
        if( visitDTO.shahokokuhoId != 0 ){
            hoken.shahokokuho = getShahokokuho(visitDTO.shahokokuhoId);
        }
        if( visitDTO.koukikoureiId != 0 ){
            hoken.koukikourei = getKoukikourei(visitDTO.koukikoureiId);
        }
        if( visitDTO.roujinId != 0 ){
            hoken.roujin = getRoujin(visitDTO.roujinId);
        }
        if( visitDTO.kouhi1Id != 0 ){
            hoken.kouhi1 = getKouhi(visitDTO.kouhi1Id);
        }
        if( visitDTO.kouhi2Id != 0 ){
            hoken.kouhi2 = getKouhi(visitDTO.kouhi2Id);
        }
        if( visitDTO.kouhi3Id != 0 ){
            hoken.kouhi3 = getKouhi(visitDTO.kouhi3Id);
        }
        return hoken;
    }

    public List<DrugFullDTO> listDrugFullByDrugIds(List<Integer> drugIds) throws Exception {
        return drugIds.stream().map(this::getDrugFull).collect(toList());
    }

    public VisitTextDrugPageDTO listVisitTextDrugByPatientAndIyakuhincode(int patientId, int iyakuhincode, int page) throws Exception {
        String countSql = "select count(*) from Drug drug, Visit visit where drug.iyakuhincode = ? " +
                " and visit.patientId = ? and visit.visitId = drug.visitId ";
        int count = getQuery().get(xlate(countSql, ts.drugTable, "drug", ts.visitTable, "visit"),
                intProjector, iyakuhincode, patientId);
        String sql = "select visit.* from Drug drug, Visit visit where drug.iyakuhincode = ? " +
                " and visit.patientId = ? and visit.visitId = drug.visitId order by visit.visitId desc " +
                " limit ? offset ? ";
        int itemsPerPage = 10;
        List<VisitDTO> visits = getQuery().query(xlate(sql, ts.drugTable, "drug", ts.visitTable, "visit"),
                ts.visitTable, iyakuhincode, patientId, itemsPerPage, itemsPerPage * page);
        VisitTextDrugPageDTO result = new VisitTextDrugPageDTO();
        result.page = page;
        result.totalPages = numberOfPages(count, itemsPerPage);
        result.visitTextDrugs = visits.stream().map(visit -> {
            VisitTextDrugDTO dto = new VisitTextDrugDTO();
            dto.visit = visit;
            dto.texts = listText(visit.visitId);
            dto.drugs = listDrugFull(visit.visitId);
            return dto;
        }).collect(toList());
        return result;
    }


    public BatchEnterResultDTO batchEnterShinryouByName(List<String> names, int visitId) throws Exception {
        throw new RuntimeException("not implemented");
//        if( names == null ){
//            names = Collections.emptyList();
//        }
//        VisitDTO visit = getVisit(visitId);
//        LocalDate at = LocalDate.parse(visit.visitedAt.substring(0, 10));
//        BatchEnterAccum accum = new BatchEnterAccum();
//        if( names.size() > 0 ) {
//            for (String name : names) {
//                if (name.equals("骨塩定量")) {
//                    addKotsuenTeiryou(accum, visitId, at);
//                } else {
//                    Optional<ShinryouMasterDTO> optMaster = resolveShinryouMaster(name, at);
//                    if (optMaster.isPresent()) {
//                        ShinryouDTO shinryouDTO = new ShinryouDTO();
//                        shinryouDTO.visitId = visitId;
//                        shinryouDTO.shinryoucode = optMaster.get().shinryoucode;
//                        shinryouDTO.shinryouId = enterShinryou(shinryouDTO);
//                        accum.shinryouIds.add(shinryouDTO.shinryouId);
//                    } else {
//                        accum.errorMessages.add(String.format("%sはその期日に使用できません。", name));
//                    }
//                }
//            }
//            if (accum.errorMessages.size() > 0) {
//                accum.errorMessages.forEach(System.out::println);
//                throw new RuntimeException(String.join("", accum.errorMessages));
//            }
//        }
//        return accum.toBatchEnterResult();
    }

    public List<Integer> batchEnterDrugs(List<DrugDTO> drugs) throws Exception {
        throw new RuntimeException("Not implemented: batchEnterDrugs");
    }

    public Map<Integer, IyakuhinMasterDTO> batchResolveIyakuhinMaster(List<Integer> iyakuhincode, LocalDate at) throws Exception {
        throw new RuntimeException("Not implemented: batchResolveIyakuhinMaster");
    }

    public List<VisitIdVisitedAtDTO> listVisitIdVisitedAtByPatientAndIyakuhincode(int patientId, int iyakuhincode) throws Exception {
        throw new RuntimeException("Not implemented: listVisitIdVisitedAtByPatientAndIyakuhincode");
    }

    public List<Integer> batchCopyShinryou(int visitId, List<ShinryouDTO> srcList) throws Exception {
        throw new RuntimeException("Not implemented: batchCopyShinryou");
    }

    public List<PharmaQueueFullDTO> listPharmaQueueForToday() throws Exception {
        throw new RuntimeException("Not implemented: listPharmaQueueForToday");
    }

    public List<VisitPatientDTO> listRecentVisits(int page, int itemsPerPage) throws Exception {
        throw new RuntimeException("Not implemented: listRecentVisits");
    }

    public List<PharmaQueueFullDTO> listPharmaQueueForPrescription() throws Exception {
        throw new RuntimeException("Not implemented: listPharmaQueueForPrescription");
    }

    public List<DiseaseFullDTO> pageDiseaseFull(int patientId, int page, int itemsPerPage) throws Exception {
        throw new RuntimeException("Not implemented: pageDiseaseFull");
    }

    public int enterInject(int visitId, int kind, int iyakuhincode, double amount) throws Exception {
        throw new RuntimeException("Not implemented: enterInject");
    }

    public int resolveShinryoucode(int shinryoucode, LocalDate at) throws Exception {
        throw new RuntimeException("Not implemented: resolveShinryoucode");
    }

    public IyakuhinMasterDTO resolveIyakuhinMaster(int iyakuhincode, LocalDate at) throws Exception {
        throw new RuntimeException("Not implemented: resolveIyakuhinMaster");
    }

    public Map<String, Integer> batchResolveByoumeiNames(LocalDate at, List<List<String>> args) throws Exception {
        throw new RuntimeException("Not implemented: batchResolveByoumeiNames");
    }

    public List<DiseaseFullDTO> listDiseaseByPatientAt(int patientId, int year, int month) throws Exception {
        throw new RuntimeException("Not implemented: listDiseaseByPatientAt");
    }

    public List<VisitChargePatientDTO> listVisitChargePatientAt(LocalDate at) throws Exception {
        throw new RuntimeException("Not implemented: listVisitChargePatientAt");
    }

    public ShinryouMasterDTO resolveShinryouMaster(int shinryoucode, LocalDate at) throws Exception {
        throw new RuntimeException("Not implemented: resolveShinryouMaster");
    }

    public TextVisitPageDTO searchTextByPage(int patientId, String text, int page) throws Exception {
        throw new RuntimeException("Not implemented: searchTextByPage");
    }

    public VisitDrugPageDTO pageVisitDrug(int patientId, int page) throws Exception {
        throw new RuntimeException("Not implemented: pageVisitDrug");
    }

    public MeisaiDTO getVisitMeisai(int visitId) throws Exception {
        throw new RuntimeException("Not implemented: getVisitMeisai");
    }

    public int countPageOfDiseaseByPatient(int patientId, int itemsPerPage) throws Exception {
        throw new RuntimeException("Not implemented: countPageOfDiseaseByPatient");
    }

    public Map<String, Integer> batchResolveShuushokugoNames(LocalDate at, List<List<String>> args) throws Exception {
        throw new RuntimeException("Not implemented: batchResolveShuushokugoNames");
    }

    public List<PracticeLogDTO> listPracticeLogInRange(LocalDate date, int afterId, int beforeId) throws Exception {
        throw new RuntimeException("Not implemented: listPracticeLogInRange");
    }

    public List<Integer> listVisitingPatientIdHavingHoken(int year, int month) throws Exception {
        throw new RuntimeException("Not implemented: listVisitingPatientIdHavingHoken");
    }

    public List<Integer> listVisitIdByPatient(int patientId) throws Exception {
        throw new RuntimeException("Not implemented: listVisitIdByPatient");
    }

    public KizaiMasterDTO resolveKizaiMasterByName(String name, LocalDate at) throws Exception {
        throw new RuntimeException("Not implemented: resolveKizaiMasterByName");
    }

    public List<Integer> copyAllConducts(int targetVisitId, int sourceVisitId) throws Exception {
        throw new RuntimeException("Not implemented: copyAllConducts");
    }

    public String getNameOfIyakuhin(int iyakuhincode) throws Exception {
        throw new RuntimeException("Not implemented: getNameOfIyakuhin");
    }

    public KizaiMasterDTO resolveKizaiMaster(int kizaicode, LocalDate at) throws Exception {
        throw new RuntimeException("Not implemented: resolveKizaiMaster");
    }

    public List<PracticeLogDTO> listAllPracticeLog(LocalDate date, int lastId) throws Exception {
        throw new RuntimeException("Not implemented: listAllPracticeLog");
    }

    public ShinryouMasterDTO resolveShinryouMasterByName(String name, String at) throws Exception {
        throw new RuntimeException("Not implemented: resolveShinryouMasterByName");
    }

    public int enterXp(int visitId, String label, String film) throws Exception {
        throw new RuntimeException("Not implemented: enterXp");
    }

    public VisitFull2PatientPageDTO pageVisitFullWithPatientAt(LocalDate at, int page) throws Exception {
        throw new RuntimeException("Not implemented: pageVisitFullWithPatientAt");
    }

}
