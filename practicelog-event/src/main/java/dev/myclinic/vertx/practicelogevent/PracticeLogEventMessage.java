package dev.myclinic.vertx.practicelogevent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.practicelogevent.body.*;
import dev.myclinic.vertx.util.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class PracticeLogEventMessage {

    private static Logger logger = LoggerFactory.getLogger(dev.myclinic.vertx.practicelogevent.PracticeLogEventMessage.class);
    private static ObjectMapper mapper = new ObjectMapper();
    static {
        //mapper.registerModule(new JavaTimeModule());
    }

    private PracticeLogEventMessage() {

    }

    public static byte[] serialize(int serialId, String kind, LocalDateTime createdAtTime,
                                   dev.myclinic.vertx.practicelogevent.PracticeLogEventBody body) {
        try {
            String createdAt = DateTimeUtil.toSqlDateTime(createdAtTime);
            dev.myclinic.vertx.practicelogevent.PracticeLogEvent event = new dev.myclinic.vertx.practicelogevent.PracticeLogEvent(serialId, kind, createdAt, body);
            return mapper.writeValueAsBytes(event);
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static dev.myclinic.vertx.practicelogevent.PracticeLogEvent deserialize(byte[] bytes){
        try {
            JsonNode root = mapper.readTree(bytes);
            JsonNode serialIdNode = root.get("serialId");
            if (serialIdNode == null || !serialIdNode.isInt()) {
                throw new RuntimeException("Cannot read serialId field: " +
                        new String(bytes, StandardCharsets.UTF_8));
            }
            int serialId = serialIdNode.asInt();
            JsonNode createdAtNode = root.get("createdAt");
            if (createdAtNode == null || !createdAtNode.isTextual()) {
                throw new RuntimeException("Cannot read createdAt field: " +
                        new String(bytes, StandardCharsets.UTF_8));
            }
            String createdAt = createdAtNode.asText();
            JsonNode kindNode = root.get("kind");
            if (kindNode == null || !kindNode.isTextual()) {
                throw new RuntimeException("Cannot read kindNode field: " +
                        new String(bytes, StandardCharsets.UTF_8));
            }
            String kind = kindNode.asText();
            JsonNode bodyNode = root.get("body");
            if (bodyNode == null || !bodyNode.isObject()) {
                throw new RuntimeException("Cannot read body field: " +
                        new String(bytes, StandardCharsets.UTF_8));
            }
            return new dev.myclinic.vertx.practicelogevent.PracticeLogEvent(serialId, kind, createdAt, dispatch(kind, bodyNode));
        } catch(IOException e){
            throw new UncheckedIOException(e);
        }
    }

    private static dev.myclinic.vertx.practicelogevent.PracticeLogEventBody dispatch(String kind, JsonNode bodyNode) throws IOException {
        switch (kind) {
            case "visit-created": {
                return mapper.readValue(bodyNode.traverse(), VisitCreated.class);
            }
            case "visit-deleted": {
                return mapper.readValue(bodyNode.traverse(), VisitDeleted.class);
            }
            case "text-created": {
                return mapper.readValue(bodyNode.traverse(), TextCreated.class);
            }
            case "text-updated": {
                return mapper.readValue(bodyNode.traverse(), TextUpdated.class);
            }
            case "text-deleted": {
                return mapper.readValue(bodyNode.traverse(), TextDeleted.class);
            }
            case "drug-created": {
                return mapper.readValue(bodyNode.traverse(), DrugCreated.class);
            }
            case "drug-updated": {
                return mapper.readValue(bodyNode.traverse(), DrugUpdated.class);
            }
            case "drug-deleted": {
                return mapper.readValue(bodyNode.traverse(), DrugDeleted.class);
            }
            case "drug-attr-created": {
                return mapper.readValue(bodyNode.traverse(), DrugAttrCreated.class);
            }
            case "drug-attr-updated": {
                return mapper.readValue(bodyNode.traverse(), DrugAttrUpdated.class);
            }
            case "drug-attr-deleted": {
                return mapper.readValue(bodyNode.traverse(), DrugAttrDeleted.class);
            }
            case "shouki-created": {
                return mapper.readValue(bodyNode.traverse(), ShoukiCreated.class);
            }
            case "shouki-updated": {
                return mapper.readValue(bodyNode.traverse(), ShoukiUpdated.class);
            }
            case "shouki-deleted": {
                return mapper.readValue(bodyNode.traverse(), ShoukiDeleted.class);
            }
            case "shinryou-created": {
                return mapper.readValue(bodyNode.traverse(), ShinryouCreated.class);
            }
            case "shinryou-deleted": {
                return mapper.readValue(bodyNode.traverse(), ShinryouDeleted.class);
            }
            case "shinryou-attr-created": {
                return mapper.readValue(bodyNode.traverse(), ShinryouAttrCreated.class);
            }
            case "shinryou-attr-updated": {
                return mapper.readValue(bodyNode.traverse(), ShinryouAttrUpdated.class);
            }
            case "shinryou-attr-deleted": {
                return mapper.readValue(bodyNode.traverse(), ShinryouAttrDeleted.class);
            }
            case "conduct-created": {
                return mapper.readValue(bodyNode.traverse(), ConductCreated.class);
            }
            case "conduct-updated": {
                return mapper.readValue(bodyNode.traverse(), ConductUpdated.class);
            }
            case "conduct-deleted": {
                return mapper.readValue(bodyNode.traverse(), ConductDeleted.class);
            }
            case "gazou-label-created": {
                return mapper.readValue(bodyNode.traverse(), GazouLabelCreated.class);
            }
            case "gazou-label-updated": {
                return mapper.readValue(bodyNode.traverse(), GazouLabelUpdated.class);
            }
            case "gazou-label-deleted": {
                return mapper.readValue(bodyNode.traverse(), GazouLabelDeleted.class);
            }
            case "conduct-shinryou-created": {
                return mapper.readValue(bodyNode.traverse(), ConductShinryouCreated.class);
            }
            case "conduct-shinryou-deleted": {
                return mapper.readValue(bodyNode.traverse(), ConductShinryouDeleted.class);
            }
            case "conduct-drug-created": {
                return mapper.readValue(bodyNode.traverse(), ConductDrugCreated.class);
            }
            case "conduct-drug-deleted": {
                return mapper.readValue(bodyNode.traverse(), ConductDrugDeleted.class);
            }
            case "conduct-kizai-created": {
                return mapper.readValue(bodyNode.traverse(), ConductKizaiCreated.class);
            }
            case "conduct-kizai-deleted": {
                return mapper.readValue(bodyNode.traverse(), ConductKizaiDeleted.class);
            }
            case "charge-created": {
                return mapper.readValue(bodyNode.traverse(), ChargeCreated.class);
            }
            case "charge-updated": {
                return mapper.readValue(bodyNode.traverse(), ChargeUpdated.class);
            }
            case "payment-created": {
                return mapper.readValue(bodyNode.traverse(), PaymentCreated.class);
            }
            case "wqueue-created": {
                return mapper.readValue(bodyNode.traverse(), WqueueCreated.class);
            }
            case "wqueue-updated": {
                return mapper.readValue(bodyNode.traverse(), WqueueUpdated.class);
            }
            case "wqueue-deleted": {
                return mapper.readValue(bodyNode.traverse(), WqueueDeleted.class);
            }
            case "hoken-updated": {
                return mapper.readValue(bodyNode.traverse(), VisitUpdated.class);
            }
            case "shahokokuho-created": {
                return mapper.readValue(bodyNode.traverse(), ShahokokuhoCreated.class);
            }
            case "shahokokuho-updated": {
                return mapper.readValue(bodyNode.traverse(), ShahokokuhoUpdated.class);
            }
            case "shahokokuho-deleted": {
                return mapper.readValue(bodyNode.traverse(), ShahokokuhoDeleted.class);
            }
            case "koukikourei-created": {
                return mapper.readValue(bodyNode.traverse(), KoukikoureiCreated.class);
            }
            case "koukikourei-updated": {
                return mapper.readValue(bodyNode.traverse(), KoukikoureiUpdated.class);
            }
            case "koukikourei-deleted": {
                return mapper.readValue(bodyNode.traverse(), KoukikoureiDeleted.class);
            }
            case "kouhi-created": {
                return mapper.readValue(bodyNode.traverse(), KouhiCreated.class);
            }
            case "kouhi-updated": {
                return mapper.readValue(bodyNode.traverse(), KouhiUpdated.class);
            }
            case "kouhi-deleted": {
                return mapper.readValue(bodyNode.traverse(), KouhiDeleted.class);
            }
            case "pharma-queue-created": {
                return mapper.readValue(bodyNode.traverse(), PharmaQueueCreated.class);
            }
            case "pharma-queue-updated": {
                return mapper.readValue(bodyNode.traverse(), PharmaQueueUpdated.class);
            }
            case "pharma-queue-deleted": {
                return mapper.readValue(bodyNode.traverse(), PharmaQueueDeleted.class);
            }
            case "patient-created": {
                return mapper.readValue(bodyNode.traverse(), PatientCreated.class);
            }
            case "patient-updated": {
                return mapper.readValue(bodyNode.traverse(), PatientUpdated.class);
            }
            case "patient-deleted": {
                return mapper.readValue(bodyNode.traverse(), PatientDeleted.class);
            }
            case "disease-created": {
                return mapper.readValue(bodyNode.traverse(), DiseaseCreated.class);
            }
            case "disease-updated": {
                return mapper.readValue(bodyNode.traverse(), DiseaseUpdated.class);
            }
            case "disease-deleted": {
                return mapper.readValue(bodyNode.traverse(), DiseaseDeleted.class);
            }
            case "disease-adj-created": {
                return mapper.readValue(bodyNode.traverse(), DiseaseAdjCreated.class);
            }
            case "disease-adj-updated": {
                return mapper.readValue(bodyNode.traverse(), DiseaseAdjUpdated.class);
            }
            case "disease-adj-deleted": {
                return mapper.readValue(bodyNode.traverse(), DiseaseAdjDeleted.class);
            }
            case "presc-example-created": {
                return mapper.readValue(bodyNode.traverse(), PrescExampleCreated.class);
            }
            case "presc-example-updated": {
                return mapper.readValue(bodyNode.traverse(), PrescExampleUpdated.class);
            }
            case "presc-example-deleted": {
                return mapper.readValue(bodyNode.traverse(), PrescExampleDeleted.class);
            }
            default: {
                throw new RuntimeException("Unknown practice log kind: " + kind);
            }
        }
    }

}
