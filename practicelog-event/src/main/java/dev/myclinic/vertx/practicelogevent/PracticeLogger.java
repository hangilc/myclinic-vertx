package dev.myclinic.vertx.practicelogevent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.dto.*;
import dev.myclinic.vertx.util.DateTimeUtil;

import java.time.LocalDateTime;
import java.util.function.Consumer;

public class PracticeLogger {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final Consumer<PracticeLogDTO> onLogHandler;

    public PracticeLogger(Consumer<PracticeLogDTO> onLogHandler){
        this.onLogHandler = onLogHandler;
    }

    private void logValue(PracticeLogEventKindBody kindBody) {
        try {
            PracticeLogDTO dto = new PracticeLogDTO();
            dto.kind = kindBody.kind;
            dto.createdAt = DateTimeUtil.toSqlDateTime(LocalDateTime.now());
            dto.body = mapper.writeValueAsString(kindBody.body);
            onLogHandler.accept(dto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void logVisitCreated(VisitDTO visit) {
        logValue(PracticeLogEventKindBody.visitCreated(visit));
    }

    public void logVisitDeleted(VisitDTO deleted) {
        logValue(PracticeLogEventKindBody.visitDeleted(deleted));
    }

    public void logVisitUpdated(VisitDTO prev, VisitDTO updated) {
        logValue(PracticeLogEventKindBody.visitUpdated(prev, updated));
    }

    public void logWqueueCreated(WqueueDTO wqueue) {
        logValue(PracticeLogEventKindBody.wqueueCreated(wqueue));
    }

    public void logWqueueDeleted(WqueueDTO deleted) {
        logValue(PracticeLogEventKindBody.wqueueDeleted(deleted));
    }

    public void logWqueueUpdated(WqueueDTO prev, WqueueDTO updated) {
        logValue(PracticeLogEventKindBody.wqueueUpdated(prev, updated));
    }

    public void logTextUpdated(TextDTO prev, TextDTO updated) {
        logValue(PracticeLogEventKindBody.textUpdated(prev, updated));
    }

    public void logTextCreated(TextDTO text) {
        logValue(PracticeLogEventKindBody.textCreated(text));
    }

    public void logTextDeleted(TextDTO deleted) {
        logValue(PracticeLogEventKindBody.textDeleted(deleted));
    }

    public void logPharmaQueueCreated(PharmaQueueDTO created) {
        logValue(PracticeLogEventKindBody.pharmaQueueCreated(created));
    }

    public void logPharmaQueueUpdated(PharmaQueueDTO prev, PharmaQueueDTO updated) {
        logValue(PracticeLogEventKindBody.pharmaQueueUpdated(prev, updated));
    }

    public void logPharmaQueueDeleted(PharmaQueueDTO deleted) {
        logValue(PracticeLogEventKindBody.pharmaQueueDeleted(deleted));
    }

    public void logPatientCreated(PatientDTO created) {
        logValue(PracticeLogEventKindBody.patientCreated(created));
    }

    public void logPatientUpdated(PatientDTO prev, PatientDTO updated) {
        logValue(PracticeLogEventKindBody.patientUpdated(prev, updated));
    }

    public void logPatientDeleted(PatientDTO deleted) {
        logValue(PracticeLogEventKindBody.patientDeleted(deleted));
    }

    public void logShahokokuhoCreated(ShahokokuhoDTO created) {
        logValue(PracticeLogEventKindBody.shahokokuhoCreated(created));
    }

    public void logShahokokuhoUpdated(ShahokokuhoDTO prev, ShahokokuhoDTO updated) {
        logValue(PracticeLogEventKindBody.shahokokuhoUpdated(prev, updated));
    }

    public void logShahokokuhoDeleted(ShahokokuhoDTO deleted) {
        logValue(PracticeLogEventKindBody.shahokokuhoDeleted(deleted));
    }

    public void logKoukikoureiCreated(KoukikoureiDTO created) {
        logValue(PracticeLogEventKindBody.koukikoureiCreated(created));
    }

    public void logKoukikoureiUpdated(KoukikoureiDTO prev, KoukikoureiDTO updated) {
        logValue(PracticeLogEventKindBody.koukikoureiUpdated(prev, updated));
    }

    public void logKoukikoureiDeleted(KoukikoureiDTO deleted) {
        logValue(PracticeLogEventKindBody.koukikoureiDeleted(deleted));
    }

    public void logRoujinCreated(RoujinDTO created) {
        logValue(PracticeLogEventKindBody.roujinCreated(created));
    }

    public void logRoujinUpdated(RoujinDTO prev, RoujinDTO updated) {
        logValue(PracticeLogEventKindBody.roujinUpdated(prev, updated));
    }

    public void logRoujinDeleted(RoujinDTO deleted) {
        logValue(PracticeLogEventKindBody.roujinDeleted(deleted));
    }

    public void logKouhiCreated(KouhiDTO created) {
        logValue(PracticeLogEventKindBody.kouhiCreated(created));
    }

    public void logKouhiUpdated(KouhiDTO prev, KouhiDTO updated) {
        logValue(PracticeLogEventKindBody.kouhiUpdated(prev, updated));
    }

    public void logKouhiDeleted(KouhiDTO deleted) {
        logValue(PracticeLogEventKindBody.kouhiDeleted(deleted));
    }

    public void logChargeCreated(ChargeDTO created) {
        logValue(PracticeLogEventKindBody.chargeCreated(created));
    }

    public void logChargeUpdated(ChargeDTO prev, ChargeDTO updated) {
        logValue(PracticeLogEventKindBody.chargeUpdated(prev, updated));
    }

    public void logChargeDeleted(ChargeDTO deleted) {
        logValue(PracticeLogEventKindBody.chargeDeleted(deleted));
    }

    public void logPaymentCreated(PaymentDTO created) {
        logValue(PracticeLogEventKindBody.paymentCreated(created));
    }

    public void logPaymentUpdated(PaymentDTO prev, PaymentDTO updated) {
        logValue(PracticeLogEventKindBody.paymentUpdated(prev, updated));
    }

    public void logPaymentDeleted(PaymentDTO deleted) {
        logValue(PracticeLogEventKindBody.paymentDeleted(deleted));
    }

    public void logShinryouCreated(ShinryouDTO created) {
        logValue(PracticeLogEventKindBody.shinryouCreated(created));
    }

    public void logShinryouUpdated(ShinryouDTO prev, ShinryouDTO updated) {
        logValue(PracticeLogEventKindBody.shinryouUpdated(prev, updated));
    }

    public void logShinryouDeleted(ShinryouDTO deleted) {
        logValue(PracticeLogEventKindBody.shinryouDeleted(deleted));
    }

    public void logShinryouAttrCreated(ShinryouAttrDTO created) {
        logValue(PracticeLogEventKindBody.shinryouAttrCreated(created));
    }

    public void logShinryouAttrUpdated(ShinryouAttrDTO prev, ShinryouAttrDTO updated) {
        logValue(PracticeLogEventKindBody.shinryouAttrUpdated(prev, updated));
    }

    public void logShinryouAttrDeleted(ShinryouAttrDTO deleted) {
        logValue(PracticeLogEventKindBody.shinryouAttrDeleted(deleted));
    }

    public void logDrugCreated(DrugDTO created) {
        logValue(PracticeLogEventKindBody.drugCreated(created));
    }

    public void logDrugUpdated(DrugDTO prev, DrugDTO updated) {
        logValue(PracticeLogEventKindBody.drugUpdated(prev, updated));
    }

    public void logDrugDeleted(DrugDTO deleted) {
        logValue(PracticeLogEventKindBody.drugDeleted(deleted));
    }

    public void logDrugAttrCreated(DrugAttrDTO created) {
        logValue(PracticeLogEventKindBody.drugAttrCreated(created));
    }

    public void logDrugAttrUpdated(DrugAttrDTO prev, DrugAttrDTO updated) {
        logValue(PracticeLogEventKindBody.drugAttrUpdated(prev, updated));
    }

    public void logDrugAttrDeleted(DrugAttrDTO deleted) {
        logValue(PracticeLogEventKindBody.drugAttrDeleted(deleted));
    }

    public void logShoukiCreated(ShoukiDTO created) {
        logValue(PracticeLogEventKindBody.shoukiCreated(created));
    }

    public void logShoukiUpdated(ShoukiDTO prev, ShoukiDTO updated) {
        logValue(PracticeLogEventKindBody.shoukiUpdated(prev, updated));
    }

    public void logShoukiDeleted(ShoukiDTO deleted) {
        logValue(PracticeLogEventKindBody.shoukiDeleted(deleted));
    }

    public void logGazouLabelCreated(GazouLabelDTO created) {
        logValue(PracticeLogEventKindBody.gazouLabelCreated(created));
    }

    public void logGazouLabelUpdated(GazouLabelDTO prev, GazouLabelDTO updated) {
        logValue(PracticeLogEventKindBody.gazouLabelUpdated(prev, updated));
    }

    public void logGazouLabelDeleted(GazouLabelDTO deleted) {
        logValue(PracticeLogEventKindBody.gazouLabelDeleted(deleted));
    }

    public void logConductCreated(ConductDTO created) {
        logValue(PracticeLogEventKindBody.conductCreated(created));
    }

    public void logConductUpdated(ConductDTO prev, ConductDTO updated) {
        logValue(PracticeLogEventKindBody.conductUpdated(prev, updated));
    }

    public void logConductDeleted(ConductDTO deleted) {
        logValue(PracticeLogEventKindBody.conductDeleted(deleted));
    }

    public void logConductShinryouCreated(ConductShinryouDTO created) {
        logValue(PracticeLogEventKindBody.conductShinryouCreated(created));
    }

    public void logConductShinryouUpdated(ConductShinryouDTO prev, ConductShinryouDTO updated) {
        logValue(PracticeLogEventKindBody.conductShinryouUpdated(prev, updated));
    }

    public void logConductShinryouDeleted(ConductShinryouDTO deleted) {
        logValue(PracticeLogEventKindBody.conductShinryouDeleted(deleted));
    }

    public void logConductDrugCreated(ConductDrugDTO created) {
        logValue(PracticeLogEventKindBody.conductDrugCreated(created));
    }

    public void logConductDrugUpdated(ConductDrugDTO prev, ConductDrugDTO updated) {
        logValue(PracticeLogEventKindBody.conductDrugUpdated(prev, updated));
    }

    public void logConductDrugDeleted(ConductDrugDTO deleted) {
        logValue(PracticeLogEventKindBody.conductDrugDeleted(deleted));
    }

    public void logConductKizaiCreated(ConductKizaiDTO created) {
        logValue(PracticeLogEventKindBody.conductKizaiCreated(created));
    }

    public void logConductKizaiUpdated(ConductKizaiDTO prev, ConductKizaiDTO updated) {
        logValue(PracticeLogEventKindBody.conductKizaiUpdated(prev, updated));
    }

    public void logConductKizaiDeleted(ConductKizaiDTO deleted) {
        logValue(PracticeLogEventKindBody.conductKizaiDeleted(deleted));
    }

    public void logPharmaDrugCreated(PharmaDrugDTO created) {
        logValue(PracticeLogEventKindBody.pharmaDrugCreated(created));
    }

    public void logPharmaDrugUpdated(PharmaDrugDTO prev, PharmaDrugDTO updated) {
        logValue(PracticeLogEventKindBody.pharmaDrugUpdated(prev, updated));
    }

    public void logPharmaDrugDeleted(PharmaDrugDTO deleted) {
        logValue(PracticeLogEventKindBody.pharmaDrugDeleted(deleted));
    }

    public void logDiseaseCreated(DiseaseDTO created) {
        logValue(PracticeLogEventKindBody.diseaseCreated(created));
    }

    public void logDiseaseUpdated(DiseaseDTO prev, DiseaseDTO updated) {
        logValue(PracticeLogEventKindBody.diseaseUpdated(prev, updated));
    }

    public void logDiseaseDeleted(DiseaseDTO deleted) {
        logValue(PracticeLogEventKindBody.diseaseDeleted(deleted));
    }

    public void logDiseaseAdjCreated(DiseaseAdjDTO created) {
        logValue(PracticeLogEventKindBody.diseaseAdjCreated(created));
    }

    public void logDiseaseAdjUpdated(DiseaseAdjDTO prev, DiseaseAdjDTO updated) {
        logValue(PracticeLogEventKindBody.diseaseAdjUpdated(prev, updated));
    }

    public void logDiseaseAdjDeleted(DiseaseAdjDTO deleted) {
        logValue(PracticeLogEventKindBody.diseaseAdjDeleted(deleted));
    }

    public void logPrescExampleCreated(PrescExampleDTO created) {
        logValue(PracticeLogEventKindBody.prescExampleCreated(created));
    }

    public void logPrescExampleUpdated(PrescExampleDTO prev, PrescExampleDTO updated) {
        logValue(PracticeLogEventKindBody.prescExampleUpdated(prev, updated));
    }

    public void logPrescExampleDeleted(PrescExampleDTO deleted) {
        logValue(PracticeLogEventKindBody.prescExampleDeleted(deleted));
    }

}
