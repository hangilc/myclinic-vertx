package dev.myclinic.vertx.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.consts.ConductKind;
import dev.myclinic.vertx.consts.MeisaiSection;
import dev.myclinic.vertx.db.Backend;
import dev.myclinic.vertx.db.Query;
import dev.myclinic.vertx.db.TableSet;
import dev.myclinic.vertx.dto.*;
import dev.myclinic.vertx.houkatsukensa.HoukatsuKensa;
import dev.myclinic.vertx.mastermap.MasterKind;
import dev.myclinic.vertx.mastermap.MasterMap;
import dev.myclinic.vertx.meisai.Meisai;
import dev.myclinic.vertx.meisai.RcptVisit;
import dev.myclinic.vertx.meisai.SectionItem;
import dev.myclinic.vertx.util.DateTimeUtil;
import dev.myclinic.vertx.util.HokenUtil;
import dev.myclinic.vertx.util.RcptUtil;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

class RestHandler extends RestHandlerBase implements Handler<RoutingContext> {

    private static final Logger logger = LoggerFactory.getLogger(RestHandler.class);

    private final DataSource ds;
    private final TableSet ts;
    private final HoukatsuKensa houkatsuKensa;

    interface RestFunction {
        void call(RoutingContext ctx, Connection conn) throws Exception;
    }

    public RestHandler(DataSource ds, TableSet ts, ObjectMapper mapper, MasterMap masterMap,
                       HoukatsuKensa houkatsuKensa) {
        super(mapper, masterMap);
        this.ds = ds;
        this.ts = ts;
        this.houkatsuKensa = houkatsuKensa;
    }

    private final Map<String, RestFunction> funcMap = new HashMap<>();

    private void searchByoumeiMaster(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        String text = params.get("text");
        LocalDate at = LocalDate.parse(params.get("at"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<ByoumeiMasterDTO> _value = backend.searchByoumeiMaster(text, at);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listVisitByPatientHavingHoken(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int patientId = Integer.parseInt(params.get("patient-id"));
        int year = Integer.parseInt(params.get("year"));
        int month = Integer.parseInt(params.get("month"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<VisitFull2DTO> _value = backend.listVisitByPatientHavingHoken(patientId, year, month);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listRecentlyRegisteredPatients(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        Integer n = params.contains("n") ? Integer.parseInt(params.get("n")) : 20;
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<PatientDTO> _value = backend.listRecentlyRegisteredPatient(n);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listTodaysHotlineInRange(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int after = Integer.parseInt(params.get("after"));
        int before = Integer.parseInt(params.get("before"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<HotlineDTO> _value = backend.listTodaysHotlineInRange(after, before);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listTodaysVisits(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<VisitPatientDTO> _value = backend.listTodaysVisit();
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void batchGetDrugAttr(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        List<Integer> drugIds = params.getAll("drug-ids[]").stream().map(Integer::valueOf).collect(toList());
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<DrugAttrDTO> _value = backend.batchGetDrugAttr(drugIds);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getDrugFull(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int drugId = Integer.parseInt(params.get("drug-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        DrugFullDTO _value = backend.getDrugFull(drugId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getShinryouFull(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int shinryouId = Integer.parseInt(params.get("shinryou-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        ShinryouFullDTO _value = backend.getShinryouFull(shinryouId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void convertToHoken(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        VisitDTO visit = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        HokenDTO _value = backend.convertToHoken(visit);
        HokenUtil.fillHokenRep(_value);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void batchDeleteDrugs(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        List<Integer> drugId = params.getAll("drug-id[]").stream().map(Integer::valueOf).collect(toList());
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.batchDeleteDrugs(drugId);
        conn.commit();
        req.response().end("true");
    }

    private void getConductKizaiFull(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int conductKizaiId = Integer.parseInt(params.get("conduct-kizai-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        ConductKizaiFullDTO _value = backend.getConductKizaiFull(conductKizaiId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void findByoumeiMasterByName(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        String name = params.get("name");
        LocalDate at = LocalDate.parse(params.get("at"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        ByoumeiMasterDTO _value = backend.findByoumeiMasterByName(name, at);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listDrugFullByDrugIds(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        List<Integer> drugId = params.getAll("drug-id[]").stream().map(Integer::valueOf).collect(toList());
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<DrugFullDTO> _value = backend.listDrugFullByDrugIds(drugId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listWqueueFull(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<WqueueFullDTO> _value = backend.listWqueueFull();
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getShahokokuho(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int shahokokuhoId = Integer.parseInt(params.get("shahokokuho-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        ShahokokuhoDTO _value = backend.getShahokokuho(shahokokuhoId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void batchResolveShinryouNames(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        LocalDate at = LocalDate.parse(params.get("at"));
        List<List<String>> args = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        Map<String, Integer> _value = backend.batchResolveShinryouNames(at, args);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void deleteConductKizai(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int conductKizaiId = Integer.parseInt(params.get("conduct-kizai-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.deleteConductKizai(conductKizaiId);
        conn.commit();
        req.response().end("true");
    }

    private void getPharmaQueueFull(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int visitId = Integer.parseInt(params.get("visit-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        PharmaQueueFullDTO _value = backend.getPharmaQueueFull(visitId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void enterShinryouAttr(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        ShinryouAttrDTO attr = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.enterShinryouAttr(attr);
        conn.commit();
        req.response().end("true");
    }

    private void deleteGazouLabel(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int conductId = Integer.parseInt(params.get("conduct-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.deleteGazouLabel(conductId);
        conn.commit();
        req.response().end("true");
    }

    private void findShinryouAttr(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int shinryouId = Integer.parseInt(params.get("shinryou-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        ShinryouAttrDTO _value = backend.findShinryouAttr(shinryouId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listVisitFull2(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int patientId = Integer.parseInt(params.get("patient-id"));
        int page = Integer.parseInt(params.get("page"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        VisitFull2PageDTO _value = backend.listVisitFull2(patientId, page);
        _value.visits.forEach(v -> HokenUtil.fillHokenRep(v.hoken));
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getText(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int textId = Integer.parseInt(params.get("text-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        TextDTO _value = backend.getText(textId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void enterConductFull(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        ConductEnterRequestDTO arg = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        ConductFullDTO _value = backend.enterConductFull(arg);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void findShouki(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int visitId = Integer.parseInt(params.get("visit-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        ShoukiDTO _value = backend.findShouki(visitId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listConductFullByIds(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        List<Integer> conductId = params.getAll("conduct-id[]").stream().map(Integer::valueOf).collect(toList());
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<ConductFullDTO> _value = backend.listConductFullByIds(conductId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getKouhi(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int kouhiId = Integer.parseInt(params.get("kouhi-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        KouhiDTO _value = backend.getKouhi(kouhiId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void enterShouki(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        ShoukiDTO shouki = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.enterShouki(shouki);
        conn.commit();
        req.response().end("true");
    }

    private void deleteKoukikourei(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        KoukikoureiDTO koukikourei = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.deleteKoukikoureiSafely(koukikourei.koukikoureiId);
        conn.commit();
        req.response().end("true");
    }

    private void listDiseaseFull(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int patientId = Integer.parseInt(params.get("patient-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<DiseaseFullDTO> _value = backend.listDiseaseFull(patientId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listHoken(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int patientId = Integer.parseInt(params.get("patient-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        HokenListDTO _value = backend.listHoken(patientId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void deleteShouki(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int visitId = Integer.parseInt(params.get("visit-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.deleteShouki(visitId);
        conn.commit();
        req.response().end("true");
    }

    private void findDrugAttr(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int drugId = Integer.parseInt(params.get("drug-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        DrugAttrDTO _value = backend.findDrugAttr(drugId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void updateHoken(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        VisitDTO hoken = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.updateHoken(hoken);
        conn.commit();
        req.response().end("true");
    }

    private void listVisitTextDrugByPatientAndIyakuhincode(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int patientId = Integer.parseInt(params.get("patient-id"));
        int iyakuhincode = Integer.parseInt(params.get("iyakuhincode"));
        Integer page = params.contains("page") ? Integer.parseInt(params.get("page")) : 0;
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        VisitTextDrugPageDTO _value = backend.listVisitTextDrugByPatientAndIyakuhincode(patientId, iyakuhincode, page);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getWqueueFull(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int visitId = Integer.parseInt(params.get("visit-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        WqueueFullDTO _value = backend.getWqueueFull(visitId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listShinryou(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int visitId = Integer.parseInt(params.get("visit-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<ShinryouDTO> _value = backend.listShinryou(visitId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listShinryouFull(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int visitId = Integer.parseInt(params.get("visit-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<ShinryouFullDTO> _value = backend.listShinryouFull(visitId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void searchShinryouMaster(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        String text = params.get("text");
        LocalDate at = LocalDate.parse(params.get("at"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<ShinryouMasterDTO> _value = backend.searchShinryouMaster(text, at);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void enterConductKizai(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        ConductKizaiDTO conductKizai = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        int _value = backend.enterConductKizai(conductKizai);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void batchEnterDrugs(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        List<DrugDTO> drugs = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<Integer> _value = backend.batchEnterDrugs(drugs);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void deleteDisease(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int diseaseId = Integer.parseInt(params.get("disease-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.deleteDiseaseWithAdj(diseaseId);
        conn.commit();
        req.response().end("true");
    }

    private void listAllPharmaDrugNames(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<PharmaDrugNameDTO> _value = backend.listAllPharmaDrugNames();
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listPayment(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int visitId = Integer.parseInt(params.get("visit-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<PaymentDTO> _value = backend.listPayment(visitId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listVisitIdVisitedAtByPatientAndIyakuhincode(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int patientId = Integer.parseInt(params.get("patient-id"));
        int iyakuhincode = Integer.parseInt(params.get("iyakuhincode"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<VisitIdVisitedAtDTO> _value = backend.listVisitIdVisitedAtByPatientAndIyakuhincode(patientId, iyakuhincode);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void batchDeleteShinryou(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        List<Integer> shinryouId = params.getAll("shinryou-id[]").stream().map(Integer::valueOf).collect(toList());
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.batchDeleteShinryouWithAttr(shinryouId);
        conn.commit();
        req.response().end("true");
    }

    private void listCurrentDiseaseFull(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int patientId = Integer.parseInt(params.get("patient-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<DiseaseFullDTO> _value = backend.listCurrentDiseaseFull(patientId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void enterDisease(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        DiseaseNewDTO diseaseNew = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        int _value = backend.enterNewDisease(diseaseNew);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getCharge(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int visitId = Integer.parseInt(params.get("visit-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        ChargeDTO _value = backend.getCharge(visitId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void enterConductShinryou(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        ConductShinryouDTO conductShinryou = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        int _value = backend.enterConductShinryou(conductShinryou);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void enterKouhi(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        KouhiDTO kouhi = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        int _value = backend.enterKouhi(kouhi);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listAllPrescExample(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<PrescExampleFullDTO> _value = backend.listAllPrescExample();
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void finishCashier(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        PaymentDTO payment = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.finishCashier(payment);
        conn.commit();
        req.response().end("true");
    }

    private void enterDrugAttr(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        DrugAttrDTO attr = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.enterDrugAttr(attr);
        conn.commit();
        req.response().end("true");
    }

    private void enterDrug(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        DrugDTO drug = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        int _value = backend.enterDrug(drug);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void deletePharmaDrug(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int iyakuhincode = Integer.parseInt(params.get("iyakuhincode"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.deletePharmaDrug(iyakuhincode);
        conn.commit();
        req.response().end("true");
    }

    private void updatePatient(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        PatientDTO patient = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.updatePatient(patient);
        conn.commit();
        req.response().end("true");
    }

    private void deleteText(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int textId = Integer.parseInt(params.get("text-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.deleteText(textId);
        conn.commit();
        req.response().end("true");
    }

    private void modifyCharge(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int visitId = Integer.parseInt(params.get("visit-id"));
        int charge = Integer.parseInt(params.get("charge"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.modifyCharge(visitId, charge);
        conn.commit();
        req.response().end("true");
    }

    private void listAvailableHoken(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int patientId = Integer.parseInt(params.get("patient-id"));
        LocalDate at = LocalDate.parse(params.get("at"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        HokenDTO _value = backend.listAvailableHoken(patientId, at);
        HokenUtil.fillHokenRep(_value);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listAvailableAllHoken(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int patientId = Integer.parseInt(params.get("patient-id"));
        LocalDate at = LocalDate.parse(params.get("at"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        HokenListDTO _value = backend.listAvailableAllHoken(patientId, at);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void updateDrug(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        DrugDTO drug = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.updateDrug(drug);
        conn.commit();
        req.response().end("true");
    }

    private void findGazouLabel(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int conductId = Integer.parseInt(params.get("conduct-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        GazouLabelDTO _value = backend.findGazouLabel(conductId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void deleteConductShinryou(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int conductShinryouId = Integer.parseInt(params.get("conduct-shinryou-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.deleteConductShinryou(conductShinryouId);
        conn.commit();
        req.response().end("true");
    }

    private void listPharmaQueueForToday(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<PharmaQueueFullDTO> _value = backend.listPharmaQueueForToday();
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void deleteKouhi(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        KouhiDTO kouhi = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.deleteKouhiSafely(kouhi);
        conn.commit();
        req.response().end("true");
    }

    private void enterHotline(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        HotlineDTO hotline = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        int _value = backend.enterHotline(hotline);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listRecentVisits(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        Integer page = params.contains("page") ? Integer.parseInt(params.get("page")) : 0;
        Integer itemsPerPage = params.contains("items-per-page") ? Integer.parseInt(params.get("items-per-page")) : 30;
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<VisitPatientDTO> _value = backend.listRecentVisits(page, itemsPerPage);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void deleteConduct(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int conductId = Integer.parseInt(params.get("conduct-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.deleteConductFull(conductId);
        conn.commit();
        req.response().end("true");
    }

    private void listPharmaQueueForPrescription(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<PharmaQueueFullDTO> _value = backend.listPharmaQueueForPrescription();
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getDrug(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int drugId = Integer.parseInt(params.get("drug-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        DrugDTO _value = backend.getDrug(drugId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void enterKoukikourei(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        KoukikoureiDTO koukikourei = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        int _value = backend.enterKoukikourei(koukikourei);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void findShinryouMasterByName(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        String name = params.get("name");
        LocalDate at = LocalDate.parse(params.get("at"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        ShinryouMasterDTO _value = backend.findShinryouMasterByName(name, at);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void updateShinryou(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        ShinryouDTO shinryou = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.updateShinryou(shinryou);
        conn.commit();
        req.response().end("true");
    }

    private void getPatient(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int patientId = Integer.parseInt(params.get("patient-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        PatientDTO _value = backend.getPatient(patientId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void pageDiseaseFull(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int patientId = Integer.parseInt(params.get("patient-id"));
        Integer page = params.contains("page") ? Integer.parseInt(params.get("page")) : 0;
        Integer itemsPerPage = params.contains("items-per-page") ? Integer.parseInt(params.get("items-per-page")) : 10;
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<DiseaseFullDTO> _value = backend.pageDiseaseFull(patientId, page, itemsPerPage);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void batchGetShinryouAttr(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        List<Integer> shinryouIds = params.getAll("shinryou-ids[]").stream().map(Integer::valueOf).collect(toList());
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<ShinryouAttrDTO> _value = backend.batchGetShinryouAttr(shinryouIds);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void batchGetShouki(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        List<Integer> visitIds = params.getAll("visit-ids[]").stream().map(Integer::valueOf).collect(toList());
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<ShoukiDTO> _value = backend.batchGetShouki(visitIds);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getConduct(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int conductId = Integer.parseInt(params.get("conduct-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        ConductDTO _value = backend.getConduct(conductId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void deleteConductDrug(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int conductDrugId = Integer.parseInt(params.get("conduct-drug-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.deleteConductDrug(conductDrugId);
        conn.commit();
        req.response().end("true");
    }

    private void searchPrescExample(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        String text = params.get("text");
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<PrescExampleFullDTO> _value = backend.searchPrescExample(text);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void searchPatient(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        String text = params.get("text");
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<PatientDTO> _value = backend.searchPatient(text);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void updateKoukikourei(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        KoukikoureiDTO koukikourei = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.updateKoukikourei(koukikourei);
        conn.commit();
        req.response().end("true");
    }

    private void deleteRoujin(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        RoujinDTO roujin = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.deleteRoujinSafely(roujin);
        conn.commit();
        req.response().end("true");
    }

    private void deleteDrugTekiyou(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int drugId = Integer.parseInt(params.get("drug-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        DrugAttrDTO _value = backend.deleteDrugTekiyou(drugId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void findPharmaDrug(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int iyakuhincode = Integer.parseInt(params.get("iyakuhincode"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        PharmaDrugDTO _value = backend.getPharmaDrug(iyakuhincode);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listDrugFull(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int visitId = Integer.parseInt(params.get("visit-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<DrugFullDTO> _value = backend.listDrugFull(visitId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listWqueueFullForExam(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<WqueueFullDTO> _value = backend.listWqueueFullForExam();
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void updateShouki(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        ShoukiDTO shouki = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.updateShouki(shouki);
        conn.commit();
        req.response().end("true");
    }

    private void prescDone(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int visitId = Integer.parseInt(params.get("visit-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.prescDone(visitId);
        conn.commit();
        req.response().end("true");
    }

    private void searchTextGlobally(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        String text = params.get("text");
        int page = Integer.parseInt(params.get("page"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        TextVisitPatientPageDTO _value = backend.searchTextGlobally(text, page);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void enterShinryou(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        ShinryouDTO shinryou = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        int _value = backend.enterShinryou(shinryou);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void setDrugTekiyou(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int drugId = Integer.parseInt(params.get("drug-id"));
        String tekiyou = params.get("tekiyou");
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        DrugAttrDTO _value = backend.setDrugTekiyou(drugId, tekiyou);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void enterPharmaDrug(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        PharmaDrugDTO pharmaDrug = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.enterPharmaDrug(pharmaDrug);
        conn.commit();
        req.response().end("true");
    }

    private void listDiseaseByPatientAt(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int patientId = Integer.parseInt(params.get("patient-id"));
        int year = Integer.parseInt(params.get("year"));
        int month = Integer.parseInt(params.get("month"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<DiseaseFullDTO> _value = backend.listDiseaseByPatientAt(patientId, year, month);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getConductFull(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int conductId = Integer.parseInt(params.get("conduct-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        ConductFullDTO _value = backend.getConductFull(conductId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listIyakuhinForPatient(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int patientId = Integer.parseInt(params.get("patient-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<IyakuhincodeNameDTO> _value = backend.listIyakuhinForPatient(patientId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void searchShuushokugoMaster(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        String text = params.get("text");
        LocalDate at = params.contains("at") ? LocalDate.parse(params.get("at")) : null;
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<ShuushokugoMasterDTO> _value = backend.searchShuushokugoMaster(text, at);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void deleteShahokokuho(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        ShahokokuhoDTO shahokokuho = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.deleteShahokokuhoSafely(shahokokuho);
        conn.commit();
        req.response().end("true");
    }

    private void listVisitChargePatientAt(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        LocalDate at = LocalDate.parse(params.get("at"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<VisitChargePatientDTO> _value = backend.listVisitChargePatientAt(at);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void deleteDuplicateShinryou(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int visitId = Integer.parseInt(params.get("visit-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<Integer> _value = backend.deleteDuplicateShinryou(visitId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getKoukikourei(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int koukikoureiId = Integer.parseInt(params.get("koukikourei-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        KoukikoureiDTO _value = backend.getKoukikourei(koukikoureiId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void searchIyakuhinMaster(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        String text = params.get("text");
        LocalDate at = LocalDate.parse(params.get("at"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<IyakuhinMasterDTO> _value = backend.searchIyakuhinMaster(text, at);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void deleteShinryouTekiyou(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int shinryouId = Integer.parseInt(params.get("shinryou-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        ShinryouAttrDTO _value = backend.deleteShinryouTekiyou(shinryouId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void searchTextByPage(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int patientId = Integer.parseInt(params.get("patient-id"));
        String text = params.get("text");
        int page = Integer.parseInt(params.get("page"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        TextVisitPageDTO _value = backend.searchTextByPage(patientId, text, page);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void pageVisitDrug(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int patientId = Integer.parseInt(params.get("patient-id"));
        int page = Integer.parseInt(params.get("page"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        VisitDrugPageDTO _value = backend.pageVisitDrug(patientId, page);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getConductDrugFull(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int conductDrugId = Integer.parseInt(params.get("conduct-drug-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        ConductDrugFullDTO _value = backend.getConductDrugFull(conductDrugId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void countPageOfDiseaseByPatient(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int patientId = Integer.parseInt(params.get("patient-id"));
        int itemsPerPage = Integer.parseInt(params.get("items-per-page"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        int _value = backend.countPageOfDiseaseByPatient(patientId, itemsPerPage);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void startExam(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int visitId = Integer.parseInt(params.get("visit-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.startExam(visitId);
        conn.commit();
        req.response().end("true");
    }

    private void getShinryouMaster(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int shinryoucode = Integer.parseInt(params.get("shinryoucode"));
        LocalDate at = LocalDate.parse(params.get("at"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        ShinryouMasterDTO _value = backend.getShinryouMaster(shinryoucode, at);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void searchPrevDrug(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        String text = params.contains("text") ? params.get("text") : "";
        int patientId = Integer.parseInt(params.get("patient-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<DrugFullDTO> _value = backend.searchPrevDrug(text, patientId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listShinryouFullByIds(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        List<Integer> shinryouId = params.getAll("shinryou-id[]").stream().map(Integer::valueOf).collect(toList());
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<ShinryouFullDTO> _value = backend.listShinryouFullByIds(shinryouId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void startVisit(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int patientId = Integer.parseInt(params.get("patient-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        int _value = backend.startVisit(patientId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void modifyDisease(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        DiseaseModifyDTO diseaseModify = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.modifyDisease(diseaseModify);
        conn.commit();
        req.response().end("true");
    }

    private void deleteShinryou(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int shinryouId = Integer.parseInt(params.get("shinryou-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.deleteShinryouWithAttr(shinryouId);
        conn.commit();
        req.response().end("true");
    }

    private void listVisitTextDrugForPatient(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int patientId = Integer.parseInt(params.get("patient-id"));
        int page = Integer.parseInt(params.get("page"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        VisitTextDrugPageDTO _value = backend.listVisitTextDrugForPatient(patientId, page);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void endExam(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int visitId = Integer.parseInt(params.get("visit-id"));
        int charge = Integer.parseInt(params.get("charge"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.endExam(visitId, charge);
        conn.commit();
        req.response().end("true");
    }

    private void searchKizaiMaster(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        String text = params.get("text");
        LocalDate at = LocalDate.parse(params.get("at"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<KizaiMasterDTO> _value = backend.searchKizaiMaster(text, at);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void batchUpdateDiseaseEndReason(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        List<DiseaseModifyEndReasonDTO> args = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.batchUpdateDiseaseEndReason(args);
        conn.commit();
        req.response().end("true");
    }

    private void listPracticeLogInRange(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        LocalDate date = LocalDate.parse(params.get("date"));
        int afterId = Integer.parseInt(params.get("after-id"));
        int beforeId = Integer.parseInt(params.get("before-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<PracticeLogDTO> _value = backend.listPracticeLogInRange(date, afterId, beforeId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getHoken(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int visitId = Integer.parseInt(params.get("visit-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        HokenDTO _value = backend.getHoken(visitId);
        HokenUtil.fillHokenRep(_value);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void updateKouhi(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        KouhiDTO kouhi = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.updateKouhi(kouhi);
        conn.commit();
        req.response().end("true");
    }

    private void suspendExam(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int visitId = Integer.parseInt(params.get("visit-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.suspendExam(visitId);
        conn.commit();
        req.response().end("true");
    }

    private void listPaymentByPatient(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int patientId = Integer.parseInt(params.get("patient-id"));
        Integer n = params.contains("n") ? Integer.parseInt(params.get("n")) : 30;
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<PaymentVisitPatientDTO> _value = backend.listPaymentByPatient(patientId, n);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listVisitingPatientIdHavingHoken(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int year = Integer.parseInt(params.get("year"));
        int month = Integer.parseInt(params.get("month"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<Integer> _value = backend.listVisitingPatientIdHavingHoken(year, month);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void updateText(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        TextDTO text = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.updateText(text);
        conn.commit();
        req.response().end("true");
    }

    private void listVisitIdByPatient(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int patientId = Integer.parseInt(params.get("patient-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<Integer> _value = backend.listVisitIdByPatient(patientId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getPharmaDrug(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int iyakuhincode = Integer.parseInt(params.get("iyakuhincode"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        PharmaDrugDTO _value = backend.getPharmaDrug(iyakuhincode);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void batchUpdateDrugDays(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        List<Integer> drugId = params.getAll("drug-id[]").stream().map(Integer::valueOf).collect(toList());
        int days = Integer.parseInt(params.get("days"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.batchUpdateDrugDays(drugId, days);
        conn.commit();
        req.response().end("true");
    }

    private void deleteDrug(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int drugId = Integer.parseInt(params.get("drug-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.deleteDrugWithAttr(drugId);
        conn.commit();
        req.response().end("true");
    }

    private void getConductShinryouFull(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int conductShinryouId = Integer.parseInt(params.get("conduct-shinryou-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        ConductShinryouFullDTO _value = backend.getConductShinryouFull(conductShinryouId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void enterConductDrug(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        ConductDrugDTO conductDrug = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        int _value = backend.enterConductDrug(conductDrug);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void deleteVisitFromReception(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int visitId = Integer.parseInt(params.get("visit-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.deleteVisitFromReception(visitId);
        conn.commit();
        req.response().end("true");
    }

    private void enterPrescExample(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        PrescExampleDTO prescExample = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        int _value = backend.enterPrescExample(prescExample);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void deletePrescExample(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int prescExampleId = Integer.parseInt(params.get("presc-example-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.deletePrescExample(prescExampleId);
        conn.commit();
        req.response().end("true");
    }

    private void getShuushokugoMasterByName(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        String name = params.get("name");
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        ShuushokugoMasterDTO _value = backend.getShuushokugoMasterByName(name);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getNameOfIyakuhin(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int iyakuhincode = Integer.parseInt(params.get("iyakuhincode"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        String _value = backend.getNameOfIyakuhin(iyakuhincode);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void setShinryouTekiyou(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int shinryouId = Integer.parseInt(params.get("shinryou-id"));
        String tekiyou = params.get("tekiyou");
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        ShinryouAttrDTO _value = backend.setShinryouTekiyou(shinryouId, tekiyou);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getVisit(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int visitId = Integer.parseInt(params.get("visit-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        VisitDTO _value = backend.getVisit(visitId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void modifyGazouLabel(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int conductId = Integer.parseInt(params.get("conduct-id"));
        String label = params.get("label");
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.setGazouLabel(conductId, label);
        conn.commit();
        req.response().end("true");
    }

    private void updatePharmaDrug(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        PharmaDrugDTO pharmaDrug = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.updatePharmaDrug(pharmaDrug);
        conn.commit();
        req.response().end("true");
    }

    private void listRecentHotline(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int thresholdHotlineId = Integer.parseInt(params.get("threshold-hotline-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<HotlineDTO> _value = backend.listRecentHotline(thresholdHotlineId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listText(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int visitId = Integer.parseInt(params.get("visit-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<TextDTO> _value = backend.listText(visitId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getRoujin(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int roujinId = Integer.parseInt(params.get("roujin-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        RoujinDTO _value = backend.getRoujin(roujinId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listPracticeLogAt(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        LocalDate date = LocalDate.parse(params.get("date"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<PracticeLogDTO> _value = backend.listPracticeLogAt(date);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listAllPracticeLogAfter(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        LocalDate date = LocalDate.parse(params.get("date"));
        int lastId = Integer.parseInt(params.get("last-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<PracticeLogDTO> _value = backend.listAllPracticeLogAfter(date, lastId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listRecentPayment(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        Integer n = params.contains("n") ? Integer.parseInt(params.get("n")) : 30;
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<PaymentVisitPatientDTO> _value = backend.listRecentPayment(n);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void batchResolveKizaiNames(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        LocalDate at = LocalDate.parse(params.get("at"));
        List<List<String>> args = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        Map<String, Integer> _value = backend.batchResolveKizaiNames(at, args);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listTodaysHotline(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<HotlineDTO> _value = backend.listTodaysHotline();
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void enterShahokokuho(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        ShahokokuhoDTO shahokokuho = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        int _value = backend.enterShahokokuho(shahokokuho);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void updatePrescExample(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        PrescExampleDTO prescExample = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.updatePrescExample(prescExample);
        conn.commit();
        req.response().end("true");
    }

    private void modifyConductKind(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int conductId = Integer.parseInt(params.get("conduct-id"));
        int kind = Integer.parseInt(params.get("kind"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.modifyConductKind(conductId, kind);
        conn.commit();
        req.response().end("true");
    }

    private void enterPatient(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        PatientDTO patient = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        int _value = backend.enterPatient(patient);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void pageVisitFullWithPatientAt(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        LocalDate at = LocalDate.parse(params.get("at"));
        int page = Integer.parseInt(params.get("page"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        VisitFull2PatientPageDTO _value = backend.pageVisitFullWithPatientAt(at, page);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void updateShahokokuho(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        ShahokokuhoDTO shahokokuho = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.updateShahokokuho(shahokokuho);
        conn.commit();
        req.response().end("true");
    }

    private void enterText(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        TextDTO text = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        int _value = backend.enterText(text);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void deleteVisit(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int visitId = Integer.parseInt(params.get("visit-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        backend.deleteVisitSafely(visitId);
        conn.commit();
        req.response().end("true");
    }

    private void getDiseaseFull(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int diseaseId = Integer.parseInt(params.get("disease-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        DiseaseFullDTO _value = backend.getDiseaseFull(diseaseId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listRecentVisitWithPatient(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int page = 0;
        int itemsPerPage = 30;
        String paraPage = params.get("page");
        if (paraPage != null) {
            page = Integer.parseInt(paraPage);
        }
        String paraItemsPerPage = params.get("items-per-page");
        if (paraItemsPerPage != null) {
            itemsPerPage = Integer.parseInt(paraItemsPerPage);
        }
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<VisitPatientDTO> _value = backend.listRecentVisitWithPatient(page, itemsPerPage);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listVisitPatientAt(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        LocalDate at = LocalDate.parse(params.get("at"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<VisitPatientDTO> _value = backend.listVisitPatientAt(at);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }


    {
        funcMap.put("search-byoumei-master", this::searchByoumeiMaster);
        funcMap.put("list-visit-by-patient-having-hoken", this::listVisitByPatientHavingHoken);
        funcMap.put("list-recently-registered-patients", this::listRecentlyRegisteredPatients);
        funcMap.put("list-todays-hotline-in-range", this::listTodaysHotlineInRange);
        funcMap.put("list-todays-visits", this::listTodaysVisits);
        funcMap.put("batch-get-drug-attr", this::batchGetDrugAttr);
        funcMap.put("get-drug-full", this::getDrugFull);
        funcMap.put("get-shinryou-full", this::getShinryouFull);
        funcMap.put("convert-to-hoken", this::convertToHoken);
        funcMap.put("batch-delete-drugs", this::batchDeleteDrugs);
        funcMap.put("get-conduct-kizai-full", this::getConductKizaiFull);
        funcMap.put("find-byoumei-master-by-name", this::findByoumeiMasterByName);
        funcMap.put("get-byoumei-master", this::getByoumeiMaster);
        funcMap.put("get-shuushokugo-master", this::getShuushokugoMaster);
        funcMap.put("list-drug-full-by-drug-ids", this::listDrugFullByDrugIds);
        funcMap.put("list-wqueue-full", this::listWqueueFull);
        funcMap.put("get-shahokokuho", this::getShahokokuho);
        funcMap.put("batch-resolve-shinryou-names", this::batchResolveShinryouNames);
        funcMap.put("delete-conduct-kizai", this::deleteConductKizai);
        funcMap.put("get-pharma-queue-full", this::getPharmaQueueFull);
        funcMap.put("enter-shinryou-attr", this::enterShinryouAttr);
        funcMap.put("delete-gazou-label", this::deleteGazouLabel);
        funcMap.put("find-shinryou-attr", this::findShinryouAttr);
        funcMap.put("list-visit-full2", this::listVisitFull2);
        funcMap.put("get-text", this::getText);
        funcMap.put("enter-conduct-full", this::enterConductFull);
        funcMap.put("find-shouki", this::findShouki);
        funcMap.put("list-conduct-full-by-ids", this::listConductFullByIds);
        funcMap.put("get-kouhi", this::getKouhi);
        funcMap.put("enter-shouki", this::enterShouki);
        funcMap.put("delete-koukikourei", this::deleteKoukikourei);
        funcMap.put("list-disease-full", this::listDiseaseFull);
        funcMap.put("list-hoken", this::listHoken);
        funcMap.put("delete-shouki", this::deleteShouki);
        funcMap.put("find-drug-attr", this::findDrugAttr);
        funcMap.put("update-hoken", this::updateHoken);
        funcMap.put("list-visit-text-drug-by-patient-and-iyakuhincode", this::listVisitTextDrugByPatientAndIyakuhincode);
        funcMap.put("get-wqueue-full", this::getWqueueFull);
        funcMap.put("list-shinryou-full", this::listShinryouFull);
        funcMap.put("search-shinryou-master", this::searchShinryouMaster);
        funcMap.put("enter-conduct-kizai", this::enterConductKizai);
        funcMap.put("batch-enter-drugs", this::batchEnterDrugs);
        funcMap.put("delete-disease", this::deleteDisease);
        funcMap.put("list-all-pharma-drug-names", this::listAllPharmaDrugNames);
        funcMap.put("list-payment", this::listPayment);
        funcMap.put("list-visit-id-visited-at-by-patient-and-iyakuhincode", this::listVisitIdVisitedAtByPatientAndIyakuhincode);
        funcMap.put("batch-delete-shinryou", this::batchDeleteShinryou);
        funcMap.put("list-current-disease-full", this::listCurrentDiseaseFull);
        funcMap.put("enter-disease", this::enterDisease);
        funcMap.put("get-charge", this::getCharge);
        funcMap.put("enter-conduct-shinryou", this::enterConductShinryou);
        funcMap.put("enter-kouhi", this::enterKouhi);
        funcMap.put("list-all-presc-example", this::listAllPrescExample);
        funcMap.put("finish-cashier", this::finishCashier);
        funcMap.put("enter-drug-attr", this::enterDrugAttr);
        funcMap.put("enter-drug", this::enterDrug);
        funcMap.put("delete-pharma-drug", this::deletePharmaDrug);
        funcMap.put("update-patient", this::updatePatient);
        funcMap.put("delete-text", this::deleteText);
        funcMap.put("modify-charge", this::modifyCharge);
        funcMap.put("list-available-hoken", this::listAvailableHoken);
        funcMap.put("list-available-all-hoken", this::listAvailableAllHoken);
        funcMap.put("update-drug", this::updateDrug);
        funcMap.put("get-find-label", this::findGazouLabel);
        funcMap.put("delete-conduct-shinryou", this::deleteConductShinryou);
        funcMap.put("list-pharma-queue-full-for-today", this::listPharmaQueueForToday);
        funcMap.put("delete-kouhi", this::deleteKouhi);
        funcMap.put("enter-hotline", this::enterHotline);
        funcMap.put("list-visit-with-patient", this::listRecentVisits);
        funcMap.put("delete-conduct", this::deleteConduct);
        funcMap.put("list-pharma-queue-full-for-prescription", this::listPharmaQueueForPrescription);
        funcMap.put("get-drug", this::getDrug);
        funcMap.put("enter-koukikourei", this::enterKoukikourei);
        funcMap.put("find-shinryou-master-by-name", this::findShinryouMasterByName);
        funcMap.put("update-shinryou", this::updateShinryou);
        funcMap.put("get-patient", this::getPatient);
        funcMap.put("page-disease-full", this::pageDiseaseFull);
        funcMap.put("batch-get-shinryou-attr", this::batchGetShinryouAttr);
        funcMap.put("batchy-get-shouki", this::batchGetShouki);
        funcMap.put("get-conduct", this::getConduct);
        funcMap.put("delete-conduct-drug", this::deleteConductDrug);
        funcMap.put("search-presc-example-full-by-name", this::searchPrescExample);
        funcMap.put("search-patient", this::searchPatient);
        funcMap.put("update-koukikourei", this::updateKoukikourei);
        funcMap.put("delete-roujin", this::deleteRoujin);
        funcMap.put("delete-drug-tekiyou", this::deleteDrugTekiyou);
        funcMap.put("find-pharma-drug", this::findPharmaDrug);
        funcMap.put("list-drug-full", this::listDrugFull);
        funcMap.put("list-wqueue-full-for-exam", this::listWqueueFullForExam);
        funcMap.put("update-shouki", this::updateShouki);
        funcMap.put("presc-done", this::prescDone);
        funcMap.put("search-text-globally", this::searchTextGlobally);
        funcMap.put("enter-shinryou", this::enterShinryou);
        funcMap.put("set-drug-tekiyou", this::setDrugTekiyou);
        funcMap.put("enter-pharma-drug", this::enterPharmaDrug);
        funcMap.put("list-disease-by-patient-at", this::listDiseaseByPatientAt);
        funcMap.put("get-conduct-full", this::getConductFull);
        funcMap.put("list-iyakuhin-for-patient", this::listIyakuhinForPatient);
        funcMap.put("search-shuushokugo-master", this::searchShuushokugoMaster);
        funcMap.put("delete-shahokokuho", this::deleteShahokokuho);
        funcMap.put("list-visit-charge-patient-at", this::listVisitChargePatientAt);
        funcMap.put("delete-duplicate-shinryou", this::deleteDuplicateShinryou);
        funcMap.put("get-koukikourei", this::getKoukikourei);
        funcMap.put("search-iyakuhin-master-by-name", this::searchIyakuhinMaster);
        funcMap.put("delete-shinryou-tekiyou", this::deleteShinryouTekiyou);
        funcMap.put("search-text-by-page", this::searchTextByPage);
        funcMap.put("page-visit-drug", this::pageVisitDrug);
        funcMap.put("get-conduct-drug-full", this::getConductDrugFull);
        funcMap.put("count-page-of-disease-by-patient", this::countPageOfDiseaseByPatient);
        funcMap.put("start-exam", this::startExam);
        funcMap.put("get-shinryou-master", this::getShinryouMaster);
        funcMap.put("search-prev-drug", this::searchPrevDrug);
        funcMap.put("list-shinryou-full-by-ids", this::listShinryouFullByIds);
        funcMap.put("start-visit", this::startVisit);
        funcMap.put("modify-disease", this::modifyDisease);
        funcMap.put("delete-shinryou", this::deleteShinryou);
        funcMap.put("list-visit-text-drug-for-patient", this::listVisitTextDrugForPatient);
        funcMap.put("end-exam", this::endExam);
        funcMap.put("search-kizai-master-by-name", this::searchKizaiMaster);
        funcMap.put("batch-update-disease-end-reason", this::batchUpdateDiseaseEndReason);
        funcMap.put("list-practice-log-in-range", this::listPracticeLogInRange);
        funcMap.put("get-hoken", this::getHoken);
        funcMap.put("update-kouhi", this::updateKouhi);
        funcMap.put("suspend-exam", this::suspendExam);
        funcMap.put("list-payment-by-patient", this::listPaymentByPatient);
        funcMap.put("list-visiting-patient-id-having-hoken", this::listVisitingPatientIdHavingHoken);
        funcMap.put("update-text", this::updateText);
        funcMap.put("list-visit-id-for-patient", this::listVisitIdByPatient);
        funcMap.put("get-pharma-drug", this::getPharmaDrug);
        funcMap.put("batch-update-drug-days", this::batchUpdateDrugDays);
        funcMap.put("delete-drug", this::deleteDrug);
        funcMap.put("get-conduct-shinryou-full", this::getConductShinryouFull);
        funcMap.put("enter-conduct-drug", this::enterConductDrug);
        funcMap.put("delete-visit-from-reception", this::deleteVisitFromReception);
        funcMap.put("enter-presc-example", this::enterPrescExample);
        funcMap.put("delete-presc-example", this::deletePrescExample);
        funcMap.put("find-shuushokugo-master-by-name", this::getShuushokugoMasterByName);
        funcMap.put("get-name-of-iyakuhin", this::getNameOfIyakuhin);
        funcMap.put("set-shinryou-tekiyou", this::setShinryouTekiyou);
        funcMap.put("get-visit", this::getVisit);
        funcMap.put("modify-gazou-label", this::modifyGazouLabel);
        funcMap.put("update-pharma-drug", this::updatePharmaDrug);
        funcMap.put("list-recent-hotline", this::listRecentHotline);
        funcMap.put("list-text", this::listText);
        funcMap.put("get-roujin", this::getRoujin);
        funcMap.put("list-practice-log-at", this::listPracticeLogAt);
        funcMap.put("list-practice-log-after", this::listAllPracticeLogAfter);
        funcMap.put("list-recent-payment", this::listRecentPayment);
        funcMap.put("batch-resolve-kizai-names", this::batchResolveKizaiNames);
        funcMap.put("list-todays-hotline", this::listTodaysHotline);
        funcMap.put("enter-shahokokuho", this::enterShahokokuho);
        funcMap.put("update-presc-example", this::updatePrescExample);
        funcMap.put("modify-conduct-kind", this::modifyConductKind);
        funcMap.put("enter-patient", this::enterPatient);
        funcMap.put("page-visit-full2-with-patient-at", this::pageVisitFullWithPatientAt);
        funcMap.put("update-shahokokuho", this::updateShahokokuho);
        funcMap.put("enter-text", this::enterText);
        funcMap.put("delete-visit", this::deleteVisit);
        funcMap.put("get-disease-full", this::getDiseaseFull);
        funcMap.put("list-recent-visit-with-patient", this::listRecentVisitWithPatient);
        funcMap.put("list-visit-patient-at", this::listVisitPatientAt);
        funcMap.put("list-shinryou", this::listShinryou);
        funcMap.put("batch-get-patient", this::batchGetPatient);
        funcMap.put("get-most-recent-visit-of-patient", this::getMostRecentVisitOfPatient);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////

    {
        funcMap.put("batch-enter-shinryou-by-name", this::batchEnterShinryouByName);
        funcMap.put("batch-resolve-iyakuhin-master", this::batchResolveIyakuhinMaster);
        funcMap.put("batch-copy-shinryou", this::batchCopyShinryou);
        funcMap.put("enter-inject", this::enterInject);
        funcMap.put("resolve-shinryoucode", this::resolveShinryoucode);
        funcMap.put("resolve-iyakuhin-master", this::resolveIyakuhinMaster);
        funcMap.put("batch-resolve-byoumei-names", this::batchResolveByoumeiNames);
        funcMap.put("resolve-shinryou-master", this::resolveShinryouMaster);
        funcMap.put("get-visit-meisai", this::getVisitMeisai);
        funcMap.put("batch-resolve-shuushokugo-names", this::batchResolveShuushokugoNames);
        funcMap.put("resolve-kizai-master-by-name", this::resolveKizaiMasterByName);
        funcMap.put("copy-all-conducts", this::copyAllConducts);
        funcMap.put("resolve-kizai-master", this::resolveKizaiMaster);
        funcMap.put("resolve-shinryou-master-by-name", this::resolveShinryouMasterByName);
        funcMap.put("enter-xp", this::enterXp);
    }

    private void getByoumeiMaster(RoutingContext ctx, Connection conn) throws Exception {
        String shoubyoumeicodeParam = ctx.request().getParam("shoubyoumeicode");
        String atParam = ctx.request().getParam("at");
        if( shoubyoumeicodeParam == null ){
            throw new RuntimeException("Missing param: shoubyoumeicode.");
        }
        if( atParam == null ){
            throw new RuntimeException("Missing param: at.");
        }
        int shoubyoumeicode = Integer.parseInt(shoubyoumeicodeParam);
        LocalDate at = LocalDate.parse(atParam);
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        ByoumeiMasterDTO result = backend.getByoumeiMaster(shoubyoumeicode, at);
        conn.commit();
        ctx.response().end(jsonEncode(result));
    }

    private void getShuushokugoMaster(RoutingContext ctx, Connection conn) throws Exception {
        String shuushokugocodeParam = ctx.request().getParam("shuushokugocode");
        String atParam = ctx.request().getParam("at");
        if( shuushokugocodeParam == null ){
            throw new RuntimeException("Missing param: shuushokugocode.");
        }
        if( atParam == null ){
            throw new RuntimeException("Missing param: at.");
        }
        int shuushokugocode = Integer.parseInt(shuushokugocodeParam);
        LocalDate at = LocalDate.parse(atParam);
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        ShuushokugoMasterDTO result = backend.getShuushokugoMaster(shuushokugocode, at);
        conn.commit();
        ctx.response().end(jsonEncode(result));
    }

    private void getMostRecentVisitOfPatient(RoutingContext ctx, Connection conn) throws Exception {
        String patientIdParam = ctx.request().getParam("patient-id");
        if( patientIdParam == null ){
            throw new RuntimeException("Missing parameter: patient-id.");
        }
        int patientId = Integer.parseInt(patientIdParam);
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        VisitDTO result = backend.getMostRecentVisitOfPatient(patientId);
        conn.commit();
        ctx.response().end(jsonEncode(result));
    }

    private void batchGetPatient(RoutingContext ctx, Connection conn) throws Exception {
        List<Integer> patientIds = this.mapper.readValue(ctx.getBody().getBytes(),
                new TypeReference<>(){});
        List<PatientDTO> result = new ArrayList<>();
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        for(int patientId: patientIds){
            PatientDTO patient = backend.getPatient(patientId);
            result.add(patient);
        }
        conn.commit();
        ctx.response().end(jsonEncode(result));
    }

    private ConductShinryouDTO createConductShinryouReq(String name, LocalDate at) {
        try {
            ConductShinryouDTO dto = new ConductShinryouDTO();
            dto.shinryoucode = masterMap.resolve(MasterKind.Shinryou, name, at);
            return dto;
        } catch (Exception e) {
            logger.info("Failed to enter " + name, e);
            throw new RuntimeException(String.format("%sを入力できませんでした。", name), e);
        }
    }

    private ConductDrugDTO createConductDrugReq(int iyakuhincode, double amount, LocalDate at) {
        try {
            ConductDrugDTO dto = new ConductDrugDTO();
            dto.iyakuhincode = masterMap.resolve(MasterKind.Yakuzai, iyakuhincode, at);
            dto.amount = amount;
            return dto;
        } catch (Exception e) {
            logger.info("Failed to enter {}", iyakuhincode, e);
            throw new RuntimeException(String.format("%dを入力できませんでした。", iyakuhincode), e);
        }
    }

    private ConductKizaiDTO createConductKizaiReq(String name, double amount, LocalDate at) {
        try {
            ConductKizaiDTO dto = new ConductKizaiDTO();
            dto.kizaicode = masterMap.resolve(MasterKind.Kizai, name, at);
            dto.amount = amount;
            return dto;
        } catch (Exception e) {
            logger.info("Failed to enter " + name, e);
            throw new RuntimeException(String.format("%sを入力できませんでした。", name), e);
        }
    }

    private ShinryouWithAttrDTO createShinryouReq(int visitId, int shinryoucode, LocalDate at) {
        ShinryouWithAttrDTO result = new ShinryouWithAttrDTO();
        ShinryouDTO shinryou = new ShinryouDTO();
        shinryou.shinryoucode = shinryoucode;
        shinryou.visitId = visitId;
        result.shinryou = shinryou;
        return result;
    }

    private ConductEnterRequestDTO createKotsuenTeiryouReq(int visitId, LocalDate at) {
        ConductEnterRequestDTO creq = new ConductEnterRequestDTO();
        creq.visitId = visitId;
        creq.kind = ConductKind.Gazou.getCode();
        creq.gazouLabel = "骨塩定量に使用";
        creq.shinryouList = List.of(createConductShinryouReq("骨塩定量ＭＤ法", at));
        creq.kizaiList = List.of(createConductKizaiReq("四ツ切", 1, at));
        return creq;
    }

    private void batchEnterShinryouByName(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        List<String> names = params.getAll("name[]");
        int visitId = Integer.parseInt(params.get("visit-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        BatchEnterResultDTO _value = batchEnterShinryouByName(backend, names, visitId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    public BatchEnterResultDTO batchEnterShinryouByName(Backend backend, List<String> names, int visitId)
            throws Exception {
        if (names == null) {
            names = Collections.emptyList();
        }
        VisitDTO visit = backend.getVisit(visitId);
        LocalDate at = LocalDate.parse(visit.visitedAt.substring(0, 10));
        BatchEnterRequestDTO req = new BatchEnterRequestDTO();
        req.shinryouList = new ArrayList<>();
        req.drugs = new ArrayList<>();
        req.conducts = new ArrayList<>();
        for (String name : names) {
            if (name.equals("骨塩定量")) {
                req.conducts.add(createKotsuenTeiryouReq(visitId, at));
            } else {
                ShinryouMasterDTO master = resolveShinryouMasterByName(backend, name, at);
                if( master == null ){
                    throw new RuntimeException("Cannot find master for: " + name);
                }
                req.shinryouList.add(createShinryouReq(visitId, master.shinryoucode, at));
            }
        }
        return backend.batchEnter(req);
    }

    private void batchResolveIyakuhinMaster(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        List<Integer> iyakuhincodes = params.getAll("iyakuhincode[]").stream().map(Integer::valueOf).collect(toList());
        LocalDate at = LocalDate.parse(params.get("at"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        Map<Integer, IyakuhinMasterDTO> _value = new HashMap<>();
        for (int iyakuhincode : iyakuhincodes) {
            int resolvedCode = masterMap.resolve(MasterKind.Yakuzai, iyakuhincode, at);
            _value.put(iyakuhincode, backend.getIyakuhinMaster(resolvedCode, at));
        }
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private List<Integer> batchCopyShinryou(Backend backend, int visitId, List<ShinryouDTO> srcList)
            throws Exception {
        VisitDTO visit = backend.getVisit(visitId);
        LocalDate atDate = LocalDate.parse(visit.visitedAt.substring(0, 10));
        List<Integer> shinryouIds = new ArrayList<>();
        for (ShinryouDTO src : srcList) {
            int shinryoucode = masterMap.resolve(MasterKind.Shinryou, src.shinryoucode, atDate);
            ShinryouMasterDTO master = backend.getShinryouMaster(shinryoucode, atDate);
            ShinryouDTO newShinryou = new ShinryouDTO();
            newShinryou.visitId = visitId;
            newShinryou.shinryoucode = master.shinryoucode;
            newShinryou.shinryouId = backend.enterShinryou(newShinryou);
            shinryouIds.add(newShinryou.shinryouId);
        }
        return shinryouIds;
    }

    private void batchCopyShinryou(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int visitId = Integer.parseInt(params.get("visit-id"));
        List<ShinryouDTO> srcList = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<Integer> _value = batchCopyShinryou(backend, visitId, srcList);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    public int enterInject(Backend backend, int visitId, int kind, int iyakuhincode, double amount)
            throws Exception {
        BatchEnterRequestDTO req = BatchEnterRequestDTO.create();
        ConductEnterRequestDTO creq = ConductEnterRequestDTO.create(visitId, kind, null);
        VisitDTO visit = backend.getVisit(visitId);
        LocalDate at = LocalDate.parse(visit.visitedAt.substring(0, 10));
        ConductKind conductKind = ConductKind.fromCode(kind);
        if (conductKind == ConductKind.HikaChuusha) {
            creq.shinryouList.add(createConductShinryouReq("皮下筋注", at));
        } else if (conductKind == ConductKind.JoumyakuChuusha) {
            creq.shinryouList.add(createConductShinryouReq("静注", at));
        } else {
            throw new RuntimeException(String.format("Invalid conduct kind: %s", conductKind));
        }
        creq.drugs.add(createConductDrugReq(iyakuhincode, amount, at));
        req.conducts.add(creq);
        BatchEnterResultDTO result = backend.batchEnter(req);
        if (result.conductIds.size() == 1 && result.shinryouIds.size() == 0 && result.drugIds.size() == 0) {
            return result.conductIds.get(0);
        } else {
            throw new RuntimeException("注射を入力できませんでした。");
        }
    }

    private void enterInject(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int visitId = Integer.parseInt(params.get("visit-id"));
        int kind = Integer.parseInt(params.get("kind"));
        int iyakuhincode = Integer.parseInt(params.get("iyakuhincode"));
        double amount = Double.parseDouble(params.get("amount"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        int _value = enterInject(backend, visitId, kind, iyakuhincode, amount);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private int resolveShinryoucode(int shinryoucode, LocalDate at) throws Exception {
        return masterMap.resolve(MasterKind.Shinryou, shinryoucode, at);
    }

    private void resolveShinryoucode(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int shinryoucode = Integer.parseInt(params.get("shinryoucode"));
        LocalDate at = LocalDate.parse(params.get("at"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        int _value = resolveShinryoucode(shinryoucode, at);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private IyakuhinMasterDTO resolveIyakuhinMaster(Backend backend, int iyakuhincode, LocalDate at)
            throws Exception {
        iyakuhincode = masterMap.resolve(MasterKind.Yakuzai, iyakuhincode, at);
        return backend.getIyakuhinMaster(iyakuhincode, at);
    }

    private void resolveIyakuhinMaster(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int iyakuhincode = Integer.parseInt(params.get("iyakuhincode"));
        LocalDate at = LocalDate.parse(params.get("at"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        IyakuhinMasterDTO _value = resolveIyakuhinMaster(backend, iyakuhincode, at);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private Map<String, Integer> batchResolveByoumeiNames(LocalDate at, List<List<String>> args)
            throws Exception {
        Map<String, Integer> map = new HashMap<>();
        for (List<String> names : args) {
            if (names.size() < 1) {
                continue;
            }
            String key = names.get(0);
            for (String name : names) {
                Optional<Integer> optCode = masterMap.tryResolve(MasterKind.Byoumei, name);
                if (optCode.isPresent()) {
                    map.put(key, optCode.get());
                    break;
                }
            }
        }
        return map;
    }

    private void batchResolveByoumeiNames(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        LocalDate at = LocalDate.parse(params.get("at"));
        List<List<String>> args = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        Map<String, Integer> _value = batchResolveByoumeiNames(at, args);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    public ShinryouMasterDTO resolveShinryouMaster(Backend backend, int shinryoucode, LocalDate at)
            throws Exception {
        shinryoucode = masterMap.resolve(MasterKind.Shinryou, shinryoucode, at);
        return backend.getShinryouMaster(shinryoucode, at);
    }

    private void resolveShinryouMaster(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int shinryoucode = Integer.parseInt(params.get("shinryoucode"));
        LocalDate at = LocalDate.parse(params.get("at"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        ShinryouMasterDTO _value = resolveShinryouMaster(backend, shinryoucode, at);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private MeisaiDTO getVisitMeisai(Backend backend, int visitId) throws Exception {
        RcptVisit rcptVisit = new RcptVisit();
        VisitDTO visit = backend.getVisit(visitId);
        List<ShinryouFullDTO> shinryouList = backend.listShinryouFull(visitId);
        List<DrugFullDTO> drugs = backend.listDrugFull(visitId);
        List<ConductFullDTO> conducts = backend.listConductFull(visitId);
        LocalDate at = DateTimeUtil.parseSqlDateTime(visit.visitedAt).toLocalDate();
        HoukatsuKensa.Revision revision = houkatsuKensa.findRevision(at);
        rcptVisit.addShinryouList(shinryouList, revision);
        rcptVisit.addDrugs(drugs);
        rcptVisit.addConducts(conducts);
        Meisai meisai = rcptVisit.getMeisai();
        MeisaiDTO meisaiDTO = new MeisaiDTO();
        meisaiDTO.sections = new ArrayList<>();
        for (MeisaiSection section : MeisaiSection.values()) {
            List<SectionItem> items = meisai.getItems(section);
            if (items != null) {
                MeisaiSectionDTO meisaiSectionDTO = new MeisaiSectionDTO();
                meisaiSectionDTO.name = section.toString();
                meisaiSectionDTO.label = section.getLabel();
                meisaiSectionDTO.items = items.stream()
                        .map(SectionItem::toSectionItemDTO)

                        .collect(Collectors.toList());
                meisaiSectionDTO.sectionTotalTen = SectionItem.sum(items);
                meisaiDTO.sections.add(meisaiSectionDTO);
            }
        }
        meisaiDTO.totalTen = meisai.totalTen();
        PatientDTO patientDTO = backend.getPatient(visit.patientId);
        if (patientDTO.birthday != null) {
            HokenDTO hokenDTO = backend.getHokenForVisit(visit);
            HokenUtil.fillHokenRep(hokenDTO);
            meisaiDTO.hoken = hokenDTO;
            LocalDate birthdayDate = DateTimeUtil.parseSqlDate(patientDTO.birthday);
            int rcptAge = HokenUtil.calcRcptAge(birthdayDate.getYear(), birthdayDate.getMonth().getValue(),
                    birthdayDate.getDayOfMonth(), at.getYear(), at.getMonth().getValue());
            meisaiDTO.futanWari = HokenUtil.calcFutanWari(hokenDTO, rcptAge);
        } else {
            meisaiDTO.futanWari = 10;
        }
        meisaiDTO.charge = RcptUtil.calcCharge(meisaiDTO.totalTen, meisaiDTO.futanWari);
        return meisaiDTO;
    }

    private void getVisitMeisai(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int visitId = Integer.parseInt(params.get("visit-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        MeisaiDTO _value = getVisitMeisai(backend, visitId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void batchResolveShuushokugoNames(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        LocalDate at = LocalDate.parse(params.get("at"));
        List<List<String>> args = _convertParam(ctx.getBody().getBytes(), new TypeReference<>() {
        });
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        Map<String, Integer> _value = batchResolveShuushokugoNames(at, args);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    public Map<String, Integer> batchResolveShuushokugoNames(LocalDate at, List<List<String>> args) throws Exception {
        Map<String, Integer> map = new HashMap<>();
        for (List<String> names : args) {
            if (names.size() >= 1) {
                String key = names.get(0);
                for (String name : names) {
                    Optional<Integer> optCode = masterMap.tryResolve(MasterKind.Shuushokugo, name, at);
                    if (optCode.isPresent()) {
                        map.put(key, optCode.get());
                        break;
                    }
                }
            }
        }
        return map;
    }

    private KizaiMasterDTO resolveKizaiMasterByName(Backend backend, String name, LocalDate at)
            throws Exception {
        int code = masterMap.resolve(MasterKind.Kizai, name, at);
        return backend.getKizaiMaster(code, at);
    }

    private void resolveKizaiMasterByName(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        String name = params.get("name");
        LocalDate at = LocalDate.parse(params.get("at"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        KizaiMasterDTO _value = resolveKizaiMasterByName(backend, name, at);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private List<Integer> copyAllConducts(Backend backend, int targetVisitId, int sourceVisitId)
            throws Exception {
        BatchEnterRequestDTO req = BatchEnterRequestDTO.create();
        for (var src : backend.listConductFull(sourceVisitId)) {
            String gazouLabel = src.gazouLabel != null ? src.gazouLabel.label : null;
            ConductEnterRequestDTO creq =
                    ConductEnterRequestDTO.create(targetVisitId, src.conduct.kind, gazouLabel);
            src.conductShinryouList.forEach(shinryou -> {
                ConductShinryouDTO copy = new ConductShinryouDTO();
                copy.shinryoucode = shinryou.conductShinryou.shinryoucode;
                creq.shinryouList.add(copy);
            });
            src.conductDrugs.forEach(drug -> {
                ConductDrugDTO copy = new ConductDrugDTO();
                copy.iyakuhincode = drug.conductDrug.iyakuhincode;
                copy.amount = drug.conductDrug.amount;
                creq.drugs.add(copy);
            });
            src.conductKizaiList.forEach(kizai -> {
                ConductKizaiDTO copy = new ConductKizaiDTO();
                copy.kizaicode = kizai.conductKizai.kizaicode;
                creq.kizaiList.add(copy);
            });
            req.conducts.add(creq);
        }
        BatchEnterResultDTO result = backend.batchEnter(req);
        return result.conductIds;
    }

    private void copyAllConducts(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int targetVisitId = Integer.parseInt(params.get("target-visit-id"));
        int sourceVisitId = Integer.parseInt(params.get("source-visit-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        List<Integer> _value = copyAllConducts(backend, targetVisitId, sourceVisitId);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private KizaiMasterDTO resolveKizaiMaster(Backend backend, int kizaicode, LocalDate at) throws Exception {
        int code = masterMap.resolve(MasterKind.Kizai, kizaicode, at);
        return backend.getKizaiMaster(code, at);
    }

    private void resolveKizaiMaster(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int kizaicode = Integer.parseInt(params.get("kizaicode"));
        LocalDate at = LocalDate.parse(params.get("at"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        KizaiMasterDTO _value = resolveKizaiMaster(backend, kizaicode, at);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private ShinryouMasterDTO resolveShinryouMasterByName(Backend backend, String name, LocalDate at)
            throws Exception {
        Integer code = masterMap.tryResolve(MasterKind.Shinryou, name, at).orElse(null);
        if( code != null ){
            return backend.getShinryouMaster(code, at);
        } else {
            return backend.findShinryouMasterByName(name, at);
        }
    }

    private void resolveShinryouMasterByName(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        String name = params.get("name");
        LocalDate at = LocalDate.parse(params.get("at"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        ShinryouMasterDTO _value = resolveShinryouMasterByName(backend, name, at);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private int enterXp(Backend backend, int visitId, String label, String film) throws Exception {
        BatchEnterRequestDTO req = BatchEnterRequestDTO.create();
        ConductEnterRequestDTO creq = ConductEnterRequestDTO.create(visitId,
                ConductKind.Gazou.getCode(), label);
        VisitDTO visit = backend.getVisit(visitId);
        LocalDate at = LocalDate.parse(visit.visitedAt.substring(0, 10));
        creq.shinryouList.add(createConductShinryouReq("単純撮影", at));
        creq.shinryouList.add(createConductShinryouReq("単純撮影診断", at));
        creq.kizaiList.add(createConductKizaiReq(film, 1, at));
        req.conducts.add(creq);
        BatchEnterResultDTO result = backend.batchEnter(req);
        if (result.conductIds.size() == 1 && result.shinryouIds.size() == 0 && result.drugIds.size() == 0) {
            return result.conductIds.get(0);
        } else {
            throw new RuntimeException("処置（X線写真）を入力できませんでした。");
        }
    }

    private void enterXp(RoutingContext ctx, Connection conn) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int visitId = Integer.parseInt(params.get("visit-id"));
        String label = params.get("label");
        String film = params.get("film");
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        int _value = enterXp(backend, visitId, label, film);
        conn.commit();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    @Override
    public void handle(RoutingContext routingContext) {
        HttpServerRequest req = routingContext.request();
        RestFunction f = funcMap.get(req.getParam("action"));
        if (f == null) {
            routingContext.next();
        } else {
            Connection conn = null;
            try {
                HttpServerResponse resp = req.response();
                resp.putHeader("content-type", "application/json; charset=UTF-8");
                conn = ds.getConnection();
                conn.setAutoCommit(false);
                f.call(routingContext, conn);
            } catch (Exception e) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (Exception ex) {
                        logger.error("Rollback failed", ex);
                    }
                }
                throw new RuntimeException(e);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
