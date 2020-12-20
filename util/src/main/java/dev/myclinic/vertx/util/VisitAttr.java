package dev.myclinic.vertx.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import dev.myclinic.vertx.dto.VisitDTO;

import java.util.Map;
import java.util.Optional;

public class VisitAttr {

    public static Optional<Integer> getFutanWari(VisitDTO visit){
        String attr = visit.attributes;
        if( attr == null || "".equals(attr) ){
            return Optional.empty();
        } else {
            Map<String, Object> map = null;
            try {
                map = Misc.mapper.readValue(attr, new TypeReference<>(){});
                Object value = map.get("futanWari");
                if( value == null ) {
                    return Optional.empty();
                } else if( value instanceof Integer ){
                    return Optional.of((Integer)value);
                } else {
                    throw new RuntimeException("Invalid futanWari value");
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
