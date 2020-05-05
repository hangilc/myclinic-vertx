package dev.myclinic.vertx.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

class RestHandlerBase {

    protected static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
    }

    protected <T> T _convertParam(String src, TypeReference<T> typeRef) throws JsonProcessingException {
        return mapper.readValue(src, typeRef);
    }

}
