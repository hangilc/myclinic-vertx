package dev.myclinic.vertx.hotlinelogevent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.hotlinelogevent.body.HotlineBeep;
import dev.myclinic.vertx.hotlinelogevent.body.HotlineCreated;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HotlineEventMessage {

    private final static ObjectMapper mapper = new ObjectMapper();

    private HotlineEventMessage() {

    }

    public static byte[] serialize(HotlineEvent event) throws JsonProcessingException {
        return mapper.writeValueAsBytes(event);
    }

    public static HotlineEvent deserialize(byte[] src) throws IOException {
        JsonNode root = mapper.readTree(src);
        JsonNode kindNode = root.get("kind");
        if (kindNode == null || !kindNode.isTextual()) {
            throw new RuntimeException("Cannot find field 'kind': " +
                    new String(src, StandardCharsets.UTF_8));
        }
        HotlineEvent event = new HotlineEvent();
        event.kind = kindNode.asText();
        JsonNode bodyNode = root.get("body");
        if (bodyNode == null) {
            throw new RuntimeException("Cannot find field 'body': " +
                    new String(src, StandardCharsets.UTF_8));
        }
        switch (event.kind) {
            case "created": {
                event.body = mapper.readValue(bodyNode.traverse(), HotlineCreated.class);
                break;
            }
            case "beep": {
                event.body = mapper.readValue(bodyNode.traverse(), HotlineBeep.class);
                break;
            }
            default: {
                throw new RuntimeException("Unknown kind: " + event.kind);
            }
        }
        System.out.println("hotline event deserialized: " + event);
        return event;
    }

}
