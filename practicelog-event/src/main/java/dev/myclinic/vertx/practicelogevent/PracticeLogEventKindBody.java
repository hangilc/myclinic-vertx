package dev.myclinic.vertx.practicelogevent;

import dev.myclinic.vertx.dto.*;
import dev.myclinic.vertx.practicelogevent.body.*;

public class PracticeLogEventKindBody {

    public String kind;
    public PracticeLogEventBody body;

    public PracticeLogEventKindBody() {
    }

    public PracticeLogEventKindBody(String kind, PracticeLogEventBody body) {
        this.kind = kind;
        this.body = body;
    }

    private static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody logValue(String kind, PracticeLogEventBody body){
        return new dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody(kind, body);
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody visitCreated(VisitDTO visit) {
        return logValue("visit-created", new VisitCreated(visit));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody visitDeleted(VisitDTO deleted) {
        return logValue("visit-deleted", new VisitDeleted(deleted));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody visitUpdated(VisitDTO prev, VisitDTO updated) {
        return logValue("visit-updated", new VisitUpdated(prev, updated));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody wqueueCreated(WqueueDTO wqueue) {
        return logValue("wqueue-created", new WqueueCreated(wqueue));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody wqueueDeleted(WqueueDTO deleted) {
        return logValue("wqueue-deleted", new WqueueDeleted(deleted));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody wqueueUpdated(WqueueDTO prev, WqueueDTO updated) {
        return logValue("wqueue-updated", new WqueueUpdated(prev, updated));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody textUpdated(TextDTO prev, TextDTO updated) {
        return logValue("text-updated", new TextUpdated(prev, updated));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody textCreated(TextDTO text) {
        return logValue("text-created", new TextCreated(text));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody textDeleted(TextDTO deleted) {
        return logValue("text-deleted", new TextDeleted(deleted));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody pharmaQueueCreated(PharmaQueueDTO created) {
        return logValue("pharma-queue-created", new PharmaQueueCreated(created));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody pharmaQueueUpdated(PharmaQueueDTO prev, PharmaQueueDTO updated) {
        return logValue("pharma-queue-updated", new PharmaQueueUpdated(prev, updated));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody pharmaQueueDeleted(PharmaQueueDTO deleted) {
        return logValue("pharma-queue-deleted", new PharmaQueueDeleted(deleted));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody patientCreated(PatientDTO created) {
        return logValue("patient-created", new PatientCreated(created));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody patientUpdated(PatientDTO prev, PatientDTO updated) {
        return logValue("patient-updated", new PatientUpdated(prev, updated));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody patientDeleted(PatientDTO deleted) {
        return logValue("patient-deleted", new PatientDeleted(deleted));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody shahokokuhoCreated(ShahokokuhoDTO created) {
        return logValue("shahokokuho-created", new ShahokokuhoCreated(created));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody shahokokuhoUpdated(ShahokokuhoDTO prev, ShahokokuhoDTO updated) {
        return logValue("shahokokuho-updated", new ShahokokuhoUpdated(prev, updated));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody shahokokuhoDeleted(ShahokokuhoDTO deleted) {
        return logValue("shahokokuho-deleted", new ShahokokuhoDeleted(deleted));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody koukikoureiCreated(KoukikoureiDTO created) {
        return logValue("koukikourei-created", new KoukikoureiCreated(created));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody koukikoureiUpdated(KoukikoureiDTO prev, KoukikoureiDTO updated) {
        return logValue("koukikourei-updated", new KoukikoureiUpdated(prev, updated));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody koukikoureiDeleted(KoukikoureiDTO deleted) {
        return logValue("koukikourei-deleted", new KoukikoureiDeleted(deleted));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody roujinCreated(RoujinDTO created) {
        return logValue("roujin-created", new RoujinCreated(created));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody roujinUpdated(RoujinDTO prev, RoujinDTO updated) {
        return logValue("roujin-updated", new RoujinUpdated(prev, updated));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody roujinDeleted(RoujinDTO deleted) {
        return logValue("roujin-deleted", new RoujinDeleted(deleted));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody kouhiCreated(KouhiDTO created) {
        return logValue("kouhi-created", new KouhiCreated(created));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody kouhiUpdated(KouhiDTO prev, KouhiDTO updated) {
        return logValue("kouhi-updated", new KouhiUpdated(prev, updated));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody kouhiDeleted(KouhiDTO deleted) {
        return logValue("kouhi-deleted", new KouhiDeleted(deleted));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody chargeCreated(ChargeDTO created) {
        return logValue("charge-created", new ChargeCreated(created));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody chargeUpdated(ChargeDTO prev, ChargeDTO updated) {
        return logValue("charge-updated", new ChargeUpdated(prev, updated));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody chargeDeleted(ChargeDTO deleted) {
        return logValue("charge-deleted", new ChargeDeleted(deleted));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody paymentCreated(PaymentDTO created) {
        return logValue("payment-created", new PaymentCreated(created));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody paymentUpdated(PaymentDTO prev, PaymentDTO updated) {
        return logValue("payment-updated", new PaymentUpdated(prev, updated));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody paymentDeleted(PaymentDTO deleted) {
        return logValue("payment-deleted", new PaymentDeleted(deleted));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody shinryouCreated(ShinryouDTO created) {
        return logValue("shinryou-created", new ShinryouCreated(created));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody shinryouUpdated(ShinryouDTO prev, ShinryouDTO updated) {
        return logValue("shinryou-updated", new ShinryouUpdated(prev, updated));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody shinryouDeleted(ShinryouDTO deleted) {
        return logValue("shinryou-deleted", new ShinryouDeleted(deleted));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody shinryouAttrCreated(ShinryouAttrDTO created) {
        return logValue("shinryou-attr-created", new ShinryouAttrCreated(created));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody shinryouAttrUpdated(ShinryouAttrDTO prev, ShinryouAttrDTO updated) {
        return logValue("shinryou-attr-updated", new ShinryouAttrUpdated(prev, updated));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody shinryouAttrDeleted(ShinryouAttrDTO deleted) {
        return logValue("shinryou-attr-deleted", new ShinryouAttrDeleted(deleted));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody drugCreated(DrugDTO created) {
        return logValue("drug-created", new DrugCreated(created));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody drugUpdated(DrugDTO prev, DrugDTO updated) {
        return logValue("drug-updated", new DrugUpdated(prev, updated));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody drugDeleted(DrugDTO deleted) {
        return logValue("drug-deleted", new DrugDeleted(deleted));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody drugAttrCreated(DrugAttrDTO created) {
        return logValue("drug-attr-created", new DrugAttrCreated(created));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody drugAttrUpdated(DrugAttrDTO prev, DrugAttrDTO updated) {
        return logValue("drug-attr-updated", new DrugAttrUpdated(prev, updated));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody drugAttrDeleted(DrugAttrDTO deleted) {
        return logValue("drug-attr-deleted", new DrugAttrDeleted(deleted));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody shoukiCreated(ShoukiDTO created) {
        return logValue("shouki-created", new ShoukiCreated(created));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody shoukiUpdated(ShoukiDTO prev, ShoukiDTO updated) {
        return logValue("shouki-updated", new ShoukiUpdated(prev, updated));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody shoukiDeleted(ShoukiDTO deleted) {
        return logValue("shouki-deleted", new ShoukiDeleted(deleted));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody gazouLabelCreated(GazouLabelDTO created) {
        return logValue("gazou-label-created", new GazouLabelCreated(created));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody gazouLabelUpdated(GazouLabelDTO prev, GazouLabelDTO updated) {
        return logValue("gazou-label-updated", new GazouLabelUpdated(prev, updated));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody gazouLabelDeleted(GazouLabelDTO deleted) {
        return logValue("gazou-label-deleted", new GazouLabelDeleted(deleted));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody conductCreated(ConductDTO created) {
        return logValue("conduct-created", new ConductCreated(created));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody conductUpdated(ConductDTO prev, ConductDTO updated) {
        return logValue("conduct-updated", new ConductUpdated(prev, updated));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody conductDeleted(ConductDTO deleted) {
        return logValue("conduct-deleted", new ConductDeleted(deleted));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody conductShinryouCreated(ConductShinryouDTO created) {
        return logValue("conduct-shinryou-created", new ConductShinryouCreated(created));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody conductShinryouUpdated(ConductShinryouDTO prev, ConductShinryouDTO updated) {
        return logValue("conduct-shinryou-updated", new ConductShinryouUpdated(prev, updated));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody conductShinryouDeleted(ConductShinryouDTO deleted) {
        return logValue("conduct-shinryou-deleted", new ConductShinryouDeleted(deleted));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody conductDrugCreated(ConductDrugDTO created) {
        return logValue("conduct-drug-created", new ConductDrugCreated(created));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody conductDrugUpdated(ConductDrugDTO prev, ConductDrugDTO updated) {
        return logValue("conduct-drug-updated", new ConductDrugUpdated(prev, updated));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody conductDrugDeleted(ConductDrugDTO deleted) {
        return logValue("conduct-drug-deleted", new ConductDrugDeleted(deleted));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody conductKizaiCreated(ConductKizaiDTO created) {
        return logValue("conduct-kizai-created", new ConductKizaiCreated(created));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody conductKizaiUpdated(ConductKizaiDTO prev, ConductKizaiDTO updated) {
        return logValue("conduct-kizai-updated", new ConductKizaiUpdated(prev, updated));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody conductKizaiDeleted(ConductKizaiDTO deleted) {
        return logValue("conduct-kizai-deleted", new ConductKizaiDeleted(deleted));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody pharmaDrugCreated(PharmaDrugDTO created) {
        return logValue("pharma-drug-created", new PharmaDrugCreated(created));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody pharmaDrugUpdated(PharmaDrugDTO prev, PharmaDrugDTO updated) {
        return logValue("pharma-drug-updated", new PharmaDrugUpdated(prev, updated));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody pharmaDrugDeleted(PharmaDrugDTO deleted) {
        return logValue("pharma-drug-deleted", new PharmaDrugDeleted(deleted));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody diseaseCreated(DiseaseDTO created) {
        return logValue("disease-created", new DiseaseCreated(created));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody diseaseUpdated(DiseaseDTO prev, DiseaseDTO updated) {
        return logValue("disease-updated", new DiseaseUpdated(prev, updated));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody diseaseDeleted(DiseaseDTO deleted) {
        return logValue("disease-deleted", new DiseaseDeleted(deleted));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody diseaseAdjCreated(DiseaseAdjDTO created) {
        return logValue("disease-adj-created", new DiseaseAdjCreated(created));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody diseaseAdjUpdated(DiseaseAdjDTO prev, DiseaseAdjDTO updated) {
        return logValue("disease-adj-updated", new DiseaseAdjUpdated(prev, updated));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody diseaseAdjDeleted(DiseaseAdjDTO deleted) {
        return logValue("disease-adj-deleted", new DiseaseAdjDeleted(deleted));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody prescExampleCreated(PrescExampleDTO created) {
        return logValue("presc-example-created", new PrescExampleCreated(created));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody prescExampleUpdated(PrescExampleDTO prev, PrescExampleDTO updated) {
        return logValue("presc-example-updated", new PrescExampleUpdated(prev, updated));
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEventKindBody prescExampleDeleted(PrescExampleDTO deleted) {
        return logValue("presc-example-deleted", new PrescExampleDeleted(deleted));
    }

}
